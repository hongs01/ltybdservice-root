package com.ltybdservice;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.config.HbaseTableConfig;
import com.ltybdservice.config.KafkaConfig;
import com.ltybdservice.config.SqlConnConfig;
import com.ltybdservice.dest.destagg.*;
import com.ltybdservice.dest.destbean.*;
import com.ltybdservice.pre.preagg.*;
import com.ltybdservice.pre.prebean.*;
import com.ltybdservice.srcbean.SrcBusGps;
import com.ltybdservice.srcbean.SrcBusPsg;
import com.ltybdservice.hbaseutil.StormEventHBaseMapState;
import com.ltybdservice.hiveutil.DataSourceConf;
import com.ltybdservice.jsonutil.JsonUtil;
import com.ltybdservice.kafkautil.ProducerConf;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.OpaqueTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.*;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 注意group by的参数顺序不能变
 */
public class DisplayApplication {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayApplication.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final HbaseTableConfig hbaseTableConfig = new HbaseTableConfig();
    private static final SqlConnConfig sqlConnConfig = new SqlConnConfig();
    private static final KafkaConfig kafkaConfig = new KafkaConfig();

    static OpaqueTridentKafkaSpout createKafkaSpout(String zkUrl, String topic) {
        ZkHosts hosts = new ZkHosts(zkUrl);
        TridentKafkaConfig config = new TridentKafkaConfig(hosts, topic);
        config.scheme = new SchemeAsMultiScheme(new StringScheme());
//        config.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        config.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        return new OpaqueTridentKafkaSpout(config);
    }

    static StormTopology buildTopology(DataSourceConf dcConf, DataSourceConf dpConf, ProducerConf producerConf) {
        TridentTopology tridentTopology = new TridentTopology();
        Stream tmpBusPsgStream = tridentTopology.newStream("psgTopic1", createKafkaSpout(kafkaConfig.getPsgTopicZkUrl(), kafkaConfig.getPsgTopicName()))  //bus客流信息
                .each(new Fields("str"), new BaseFilter() {
                    public boolean isKeep(TridentTuple tuple) {
                        String jsonStr = tuple.getString(0);
                        return JsonUtil.isJson(jsonStr);                         //过滤不符格式规范的字符串
                    }
                })
                .map((MapFunction) input -> {                                                                           //将字符串转换成数据对象
                    SrcBusPsg srcbusPsg = JSON.parseObject(input.getString(0), SrcBusPsg.class);
                    RawBusPsg rawBusPsg = RawBusPsg.transFromBusPsg(dcConf, srcbusPsg);                           //转换格式，增加非聚合字段
                    return new Values(rawBusPsg);
                })
                .each(new Fields("str"), new BaseFunction() {                                                          //field名不变还是kafka输出的field名
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {                              //根据新消息得到分组信息
                        RawBusPsg rawBusPsg = (RawBusPsg) tuple.getValueByField("str");
                        List<Object> values = new ArrayList<>();
                        values.add(rawBusPsg.getCityCode());
                        values.add(rawBusPsg.getBusId());
                        values.add(rawBusPsg.getWorkDate());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(rawBusPsg.getTime()));
                        String halfHour = null;
                        if (calendar.get(Calendar.MINUTE) < 30)
                            halfHour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":00";
                        else
                            halfHour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":30";
                        values.add(halfHour);
                        collector.emit(values);
                    }
                }, new Fields("cityCode", "busId", "workDate", "halfHour"));                        //输出
        //按天聚合数据
        Stream dayBusPsgStream = tmpBusPsgStream.groupBy(new Fields("cityCode", "busId", "workDate"))                    //按车辆聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBusPsgTable())
                                        .setEvent(new BusPsg())
                        ),
                        new Fields("str", "workDate", "halfHour"), new BusPsgAgg(), new Fields("busPsg")
                ).newValuesStream();
        Stream dayStationPsgStream = dayBusPsgStream
                .each(new Fields("busPsg"), new BaseFunction() {
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {
                        BusPsg busPsg = (BusPsg) tuple.getValueByField("busPsg");
                        List<Object> values = new ArrayList<>();
                        values.add(busPsg.getStationId());
                        collector.emit(values);
                    }
                }, new Fields("stationId"))
                .groupBy(new Fields("cityCode", "stationId", "workDate"))            //按站台聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayStationPsgTable())
                                        .setEvent(new StationPsg())
                        ),
                        new Fields("cityCode", "workDate", "stationId", "busPsg"), new StationPsgAgg(), new Fields("stationPsg")
                ).newValuesStream();
        Stream dayLinePsgStream = dayBusPsgStream
                .each(new Fields("busPsg"), new BaseFunction() {
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {
                        BusPsg busPsg = (BusPsg) tuple.getValueByField("busPsg");
                        List<Object> values = new ArrayList<>();
                        values.add(busPsg.getLineId());
                        collector.emit(values);
                    }
                }, new Fields("lineId"))
                .groupBy(new Fields("cityCode", "lineId", "workDate"))                //按线路聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayLinePsgTable())
                                        .setEvent(new LinePsg())
                        ),
                        new Fields("cityCode", "workDate", "lineId", "busPsg"), new LinePsgAgg(), new Fields("linePsg")
                ).newValuesStream();
        Stream dayCityPsgStream = dayLinePsgStream
                .groupBy(new Fields("cityCode", "workDate"))                          //按城市聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayCityPsgTable())
                                        .setEvent(new CityPsg())
                        ),
                        new Fields("cityCode", "linePsg", "workDate"), new CityPsgAgg(dpConf), new Fields("cityPsg")
                ).newValuesStream();

        //按半小时聚合数据
        Stream halfHourBusPsgStream = tmpBusPsgStream
                .groupBy(new Fields("cityCode", "busId", "workDate", "halfHour"))
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourBusPsgTable())
                                        .setEvent(new BusPsg())
                        ),
                        new Fields("str", "workDate", "halfHour"), new BusPsgAgg(), new Fields("busPsg")
                ).newValuesStream();
        Stream halfHourStationPsgStream = halfHourBusPsgStream
                .each(new Fields("busPsg"), new BaseFunction() {                                                          //field名不变还是kafka输出的field名
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {                              //根据新消息得到分组信息
                        BusPsg busPsg = (BusPsg) tuple.getValueByField("busPsg");
                        List<Object> values = new ArrayList<>();
                        values.add(busPsg.getStationId());
                        collector.emit(values);
                    }
                }, new Fields("stationId"))
                .groupBy(new Fields("cityCode", "stationId", "workDate", "halfHour"))                                     //城市半小时客流量
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourStationPsgTable())
                                        .setEvent(new StationPsg())
                        ),
                        new Fields("cityCode", "workDate", "stationId", "busPsg"), new StationPsgAgg(), new Fields("stationPsg")
                ).newValuesStream();
        Stream halfHourLinePsgStream = halfHourBusPsgStream
                .each(new Fields("busPsg"), new BaseFunction() {
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {
                        BusPsg busPsg = (BusPsg) tuple.getValueByField("busPsg");
                        List<Object> values = new ArrayList<>();
                        values.add(busPsg.getLineId());
                        collector.emit(values);
                    }
                }, new Fields("lineId"))
                .groupBy(new Fields("cityCode", "lineId", "workDate", "halfHour"))                //按线路聚合得到线路状况
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourLinePsgTable())
                                        .setEvent(new LinePsg())
                        ),
                        new Fields("cityCode", "workDate", "lineId", "busPsg"), new LinePsgAgg(), new Fields("linePsg")
                ).newValuesStream();
        Stream halfHourCityPsgStream = halfHourLinePsgStream
                .groupBy(new Fields("cityCode", "workDate", "halfHour"))                              //按城市聚合得到城市状况
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourCityPsgTable())
                                        .setEvent(new CityPsg())
                        ),
                        new Fields("cityCode", "workDate", "linePsg"), new CityPsgAgg(dpConf), new Fields("cityPsg")
                ).newValuesStream();


        //对外接口
        dayStationPsgStream.groupBy(new Fields("cityCode", "workDate"))                                                      //站点客流量top10,t202
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBDS10StationPsgTable())
                                        .setEvent(new BDS10StationPsg())
                        ),
                        new Fields("cityCode", "stationPsg"), new BDS10StationPsgAgg(producerConf, kafkaConfig.getDestTopicName()), new Fields()
                );
        halfHourStationPsgStream.groupBy(new Fields("cityCode", "workDate", "halfHour"))                                                      //所有站点客流量,t201
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourBDSAllStationPsgTable())
                                        .setEvent(new BDSAllStationPsg())
                        ),
                        new Fields("cityCode", "halfHour", "stationPsg"), new BDSAllStationPsgAgg(producerConf, kafkaConfig.getDestTopicName()), new Fields()
                );
        dayLinePsgStream.groupBy(new Fields("workDate", "cityCode"))                                                      //线路客流总量 top10,t103
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBDS10LinePsgTable())
                                        .setEvent(new BDS10LinePsg())
                        ),
                        new Fields("cityCode", "linePsg"), new BDS10LinePsgAgg(producerConf, kafkaConfig.getDestTopicName()), new Fields()
                );
        dayCityPsgStream.groupBy(new Fields("cityCode", "workDate"))                                                      //所有线路客流量(即城市客流量),t104
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBDSAllLinePsgTable())
                                        .setEvent(new BDSAllLinePsg())
                        ),
                        new Fields("cityCode", "cityPsg"), new BDSAllLinePsgAgg(producerConf, kafkaConfig.getDestTopicName()), new Fields()
                );
        halfHourCityPsgStream.groupBy(new Fields("cityCode", "workDate"))                                                 //半小时客流量,t105
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourBDSTimeCityPsgTable())
                                        .setEvent(new BDSTimeCityPsg())
                        ),
                        new Fields("cityCode", "cityPsg"), new BDSTimeCityPsgAgg(producerConf, kafkaConfig.getDestTopicName(), hbaseTableConfig.getHalfHourCityGpsTable()), new Fields()
                );

        Stream tmpBusGpsStream = tridentTopology.newStream("gpsTopic1", createKafkaSpout(kafkaConfig.getGpsTopicZkUrl(), kafkaConfig.getGpsTopicName()))  //bus速度信息
                .each(new Fields("str"), new BaseFilter() {
                    @Override
                    public boolean isKeep(TridentTuple tuple) {                                                   //过滤不符格式规范的字符串
                        String jsonStr = tuple.getString(0);
                        return JsonUtil.isJson(jsonStr);
                    }
                })
                .map((MapFunction) input -> {                                                                           //将字符串转换成数据对象
                    SrcBusGps srcBusGps = JSON.parseObject(input.getString(0), SrcBusGps.class);
                    RawBusGps rawbusGps = RawBusGps.transFromBusGps(dcConf, srcBusGps);                                  //转换格式，增加非聚合字段
                    return new Values(rawbusGps);
                })
                .each(new Fields("str"), new BaseFunction() {                                                          //field名不变还是kafka输出的field名
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {                              //根据新消息得到分组信息
                        RawBusGps rawBusGps = (RawBusGps) tuple.getValueByField("str");
                        List<Object> values = new ArrayList<>();
                        values.add(rawBusGps.getCityCode());
                        values.add(rawBusGps.getBusId());
                        values.add(rawBusGps.getWorkDate());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(rawBusGps.getTime()));
                        String halfHour = null;
                        if (calendar.get(Calendar.MINUTE) < 30)
                            halfHour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":00";
                        else
                            halfHour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":30";
                        values.add(halfHour);
                        collector.emit(values);
                    }
                }, new Fields("cityCode", "busId", "workDate", "halfHour"));
        //按天聚合数据
        Stream dayBusGpsStream = tmpBusGpsStream.groupBy(new Fields("cityCode", "busId", "workDate"))                    //按车辆聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBusGpsTable())
                                        .setEvent(new BusGps())
                        ),
                        new Fields("str", "workDate", "halfHour"), new BusGpsAgg(), new Fields("busGps")
                ).newValuesStream();
        Stream lineDayGpsStream = dayBusGpsStream
                .each(new Fields("busGps"), new BaseFunction() {                                                          //field名不变还是kafka输出的field名
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {                              //根据新消息得到分组信息
                        BusGps busGps = (BusGps) tuple.getValueByField("busGps");
                        List<Object> values = new ArrayList<>();
                        values.add(busGps.getLineId());
                        collector.emit(values);
                    }
                }, new Fields("lineId"))
                .groupBy(new Fields("cityCode", "lineId", "workDate"))                //按线路聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayLineGpsTable())
                                        .setEvent(new LineGps())
                        ),
                        new Fields("cityCode", "lineId", "workDate", "busGps"), new LineGpsAgg(), new Fields("lineGps")
                ).newValuesStream();
        Stream dayCityGpsStream = lineDayGpsStream.groupBy(new Fields("cityCode", "workDate"))                              //按城市聚合
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayCityGpsTable())
                                        .setEvent(new CityGps())
                        ),
                        new Fields("cityCode", "workDate", "lineGps"), new CityGpsAgg(), new Fields("cityGps")
                ).newValuesStream();


        //按半小时聚合数据
        Stream halfHourBusGpsStream = tmpBusGpsStream.groupBy(new Fields("cityCode", "busId", "workDate", "halfHour"))
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourBusGpsTable())
                                        .setEvent(new BusGps())
                        ),
                        new Fields("str", "workDate", "halfHour"), new BusGpsAgg(), new Fields("busGps")
                ).newValuesStream();
        Stream halfHourLineGpsStream = halfHourBusGpsStream
                .each(new Fields("busGps"), new BaseFunction() {                                                          //field名不变还是kafka输出的field名
                    @Override
                    public void execute(TridentTuple tuple, TridentCollector collector) {                              //根据新消息得到分组信息
                        BusGps busGps = (BusGps) tuple.getValueByField("busGps");
                        List<Object> values = new ArrayList<>();
                        values.add(busGps.getLineId());
                        collector.emit(values);
                    }
                }, new Fields("lineId"))
                .groupBy(new Fields("cityCode", "lineId", "workDate", "halfHour"))                //按线路聚合得到线路状况
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourLineGpsTable())
                                        .setEvent(new LineGps())
                        ),
                        new Fields("cityCode", "lineId", "workDate", "busGps"), new LineGpsAgg(), new Fields("lineGps")
                ).newValuesStream();
        Stream halfHourCityGpsStream = halfHourLineGpsStream.groupBy(new Fields("cityCode", "workDate", "halfHour"))                              //按城市聚合得到城市状况
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourCityGpsTable())
                                        .setEvent(new CityGps())
                        ),
                        new Fields("cityCode", "workDate", "lineGps"), new CityGpsAgg(), new Fields("cityGps")
                ).newValuesStream();

        //对外接口
        dayBusGpsStream.groupBy(new Fields("cityCode", "workDate"))                                                   //车辆实时gps数据,t302
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBDSBusGpsTable())
                                        .setEvent(new BDSBusGps())
                        ),
                        new Fields("busGps"), new BDSBusGpsAgg(dcConf, producerConf, kafkaConfig.getDestTopicName(), hbaseTableConfig.getDayBusPsgTable()), new Fields()
                );
        dayCityGpsStream.groupBy(new Fields("cityCode", "workDate"))                                                  //获取运营速度统计数据,t303
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getDayBDSCityGpsTable())
                                        .setEvent(new BDSCityGps())
                        ),
                        new Fields("cityGps"), new BDSCityGpsAgg(producerConf, kafkaConfig.getDestTopicName()), new Fields()
                );
        halfHourCityGpsStream.groupBy(new Fields("cityCode", "workDate"))                                //获取运营速度变化数据,t304
                .persistentAggregate(
                        StormEventHBaseMapState.nonTransactional(
                                new StormEventHBaseMapState.Options<>()
                                        .setColumnFamily(hbaseTableConfig.getColumnFamily())
                                        .setTableName(hbaseTableConfig.getHalfHourBDSTimeCityGpsTable())
                                        .setEvent(new BDSTimeCityGps())
                        ),
                        new Fields("cityGps"), new BDSTimeCityGpsAgg(producerConf, kafkaConfig.getDestTopicName()), new Fields()
                );
        return tridentTopology.build();
    }


    public static void main(String[] args) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
//        hbaseTableConfig.createHbaseTable();
        DataSourceConf dcConf = new DataSourceConf(sqlConnConfig.getDriverName(), sqlConnConfig.getJdbcConnUrl(), sqlConnConfig.getDcDataBase(), sqlConnConfig.getHiveUser(), sqlConnConfig.getHivePassword());
        DataSourceConf dpConf = new DataSourceConf(sqlConnConfig.getDriverName(), sqlConnConfig.getJdbcConnUrl(), sqlConnConfig.getDpDataBase(), sqlConnConfig.getHiveUser(), sqlConnConfig.getHivePassword());
        ProducerConf producerConf = new ProducerConf(kafkaConfig.getBootstrapServers(), kafkaConfig.getKeySerializer(), kafkaConfig.getValueSerializer());

        Config stormConf = new Config();
        stormConf.setMaxSpoutPending(20);
        stormConf.setNumWorkers(3);
        if (args.length == 0) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("DisplayApplication", stormConf, buildTopology(dcConf, dpConf, producerConf));
            Thread.sleep(60 * 1000 * 10 * 10);
            cluster.killTopology("DisplayApplication");
            cluster.shutdown();
            System.exit(0);
        } else if (args.length == 1) {
            StormSubmitter.submitTopology(args[0], stormConf, buildTopology(dcConf, dpConf, producerConf));
        } else {
            System.out.println("Usage: DisplayApplication [topology name]");
            System.exit(1);
        }
    }
}
