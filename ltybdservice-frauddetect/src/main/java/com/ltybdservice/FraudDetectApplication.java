package com.ltybdservice;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.commonutil.DistanceUtil;
import com.ltybdservice.hbaseutil.HbaseUtil;
import com.ltybdservice.hbaseutil.IEvent;
import com.ltybdservice.jsonutil.JsonUtil;
import com.ltybdservice.kafkautil.KafkaUtil;
import com.ltybdservice.kafkautil.ProducerConf;
import com.ltybdservice.mysqlutil.MysqlConf;
import com.ltybdservice.mysqlutil.DataBaseUtil;
import com.ltybdservice.bean.FraudDatabean;
import com.ltybdservice.bean.SimpleTransactionRecordBean;
import com.ltybdservice.bean.TransactionRecordBean;
import com.ltybdservice.bean.UserPoseBean;
import com.ltybdservice.config.HbaseTableConfig;
import com.ltybdservice.config.KafkaConfig;
import com.ltybdservice.config.SpeedConfig;
import com.ltybdservice.config.SqlConnConfig;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
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
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.operation.MapFunction;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class FraudDetectApplication {
    private static final Logger LOG = LoggerFactory.getLogger(FraudDetectApplication.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final HbaseTableConfig hbaseTableConfig = new HbaseTableConfig();
    private static final SqlConnConfig sqlConnConfig = new SqlConnConfig();
    private static final KafkaConfig kafkaConfig = new KafkaConfig();
    private static final SpeedConfig speedConfig = new SpeedConfig();              //速度单位：米/分钟

    static OpaqueTridentKafkaSpout createKafkaSpout(String zkUrl, String topic) {
        ZkHosts hosts = new ZkHosts(zkUrl);
        TridentKafkaConfig config = new TridentKafkaConfig(hosts, topic);
        config.scheme = new SchemeAsMultiScheme(new StringScheme());
        config.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
//        config.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        return new OpaqueTridentKafkaSpout(config);
    }

    /**
     * @return type: com.ltybdservice.bean.FraudDatabean
     * @author: Administrator
     * @date: 2017/11/22 16:57
     * @method: getFraud
     * @param: [simpleRecord, tableName, speedCriterion]
     * @desciption:
     */
    static FraudDatabean getFraud(SimpleTransactionRecordBean simpleRecord, String tableName, float speedCriterion) {
        MysqlConf mysqlConf = new MysqlConf(sqlConnConfig.getMysqlDriverName(), sqlConnConfig.getMysqlJdbcConnUrl(), sqlConnConfig.getMysqlDataBaseName(), sqlConnConfig.getMysqlUser(), sqlConnConfig.getMysqlPassword());
        FraudDatabean fraudDatabean = new FraudDatabean();
        long simpleRecordTime = simpleRecord.getUp_time();//10位长度
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(simpleRecordTime * 1000));
        calendar.add(Calendar.MINUTE, 5);//把日期往后增加5分钟.整数往后推,负数往前移动
        String timeString = formatter.format(calendar.getTime());//要查询的用户位置信息是刷卡后5分钟的信息
        //V_card_code前六位是城市编码
        Filter filter = null;
        try {
            filter = new PrefixFilter(Bytes.toBytes(simpleRecord.getV_card_code().substring(0, 4) + DataBaseUtil.getConnectionInstance(mysqlConf).cardCode2UserId(simpleRecord.getV_card_code().substring(0, 4), simpleRecord.getV_card_code()) + timeString.substring(0, 17)));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<IEvent> userPoseArray = HbaseUtil.getConnectionInstance().getTable(tableName, new UserPoseBean(), filter);
        //获取五分钟后的第一条数据记录
        if (userPoseArray.size() > 0) {
            UserPoseBean firstuserPose = (UserPoseBean) userPoseArray.get(0);
            fraudDatabean.setCitycode(firstuserPose.getCitycode());
            fraudDatabean.setLongitude(simpleRecord.getLon());
            fraudDatabean.setLatitude(simpleRecord.getLat());
            fraudDatabean.setPhonemodel(firstuserPose.getPhonemodel());
            fraudDatabean.setPosition(String.valueOf(simpleRecord.getBus_station_code()));
            fraudDatabean.setUserid(firstuserPose.getUserid());
            double distance = DistanceUtil.getDistance(firstuserPose.getLongitude(), firstuserPose.getLatitude(), simpleRecord.getLon(), simpleRecord.getLat());
            long timeInterval = (firstuserPose.getCurrenttime() / 1000 - simpleRecordTime) / 60;                    //得到分钟数
            LOG.error("firstuserPose.getCurrenttime()=" + firstuserPose.getCurrenttime() + "simpleRecordTime=" + simpleRecordTime + "speed = " + distance / timeInterval);
            if (distance / timeInterval > speedCriterion) {                                                     //速度单位：米/分钟
                fraudDatabean.setCurrenttime(simpleRecordTime * 1000);
                fraudDatabean.setFraud_flag(1);
            } else fraudDatabean.setFraud_flag(0);
            userPoseArray.clear();
        }
        return fraudDatabean;
    }

    /**
     * @return type: org.apache.storm.generated.StormTopology
     * @author: Administrator
     * @date: 2017/11/22 16:57
     * @method: buildTopology
     * @param: [producerConf]
     * @desciption:
     */
    static StormTopology buildTopology(ProducerConf producerConf) {
        TridentTopology tridentTopology = new TridentTopology();
        tridentTopology.newStream("userPoseTopic", createKafkaSpout(kafkaConfig.getUserPoseTopicZkUrl(), kafkaConfig.getUserPoseTopicName()))
                .each(new Fields("str"), new BaseFilter() {
                    public boolean isKeep(TridentTuple tuple) {                                //过滤不符格式规范的字符串
                        String jsonStr = tuple.getString(0);
                        return JsonUtil.isJson(jsonStr) && (!jsonStr.contains("\"userid\":\"\"")) && (!jsonStr.contains("\"citycode\":\"\""));
                    }
                })
                .map((MapFunction) input -> {
                    UserPoseBean userPoseBean = JSON.parseObject(input.getString(0), UserPoseBean.class);
                    //乘客信息表的行键是：城市代码前4位（共6位）+用户id（4位）+格式化时间
                    HbaseUtil.getConnectionInstance().putEvent(hbaseTableConfig.getUserPoseTable(), userPoseBean, userPoseBean.getCitycode().substring(0, 4) + userPoseBean.getUserid() + formatter.format(new Date(userPoseBean.getCurrenttime())));  //存储进hbase
                    return new Values(userPoseBean);
                });


//        tridentTopology.newStream("test1", createKafkaSpout(zkUrl, "transactionRecordTopic"))
        tridentTopology.newStream("transactionRecordTopic", createKafkaSpout(kafkaConfig.getTransactionRecordTopicZkUrl(), kafkaConfig.getTransactionRecordTopicName()))
                .each(new Fields("str"), new BaseFilter() {
                    @Override
                    public boolean isKeep(TridentTuple tuple) {                                //过滤不符格式规范的字符串
                        String jsonStr = tuple.getString(0);
                        return JsonUtil.isJson(jsonStr) && jsonStr.contains("v_card_code") && (!jsonStr.contains("\"verify_code\":0")) && (jsonStr.contains("\"ondirection_flag\":false") || jsonStr.contains("\"ondirection_flag\":true"));
                    }
                })
                .map((MapFunction) input -> {
                    TransactionRecordBean transactionRecordBean = JSON.parseObject(input.getString(0), TransactionRecordBean.class);
                    SimpleTransactionRecordBean simpleTransactionRecordBean = SimpleTransactionRecordBean.transactionRecordBean2SimpleTransactionRecordBean(transactionRecordBean);
//                    LOG.error(simpleTransactionRecordBean.getV_card_code());
                    HbaseUtil.getConnectionInstance().putEvent(hbaseTableConfig.getSimpleTransactionTable(), simpleTransactionRecordBean, simpleTransactionRecordBean.getV_card_code());  //存储新的记录进hbase，虚拟卡ID=6字节城市代码+9字节注册唯一码
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(calendar.MINUTE, -5);//把日期往后减少5分钟.整数往后推,负数往前移动
                    Date date = calendar.getTime();
                    ArrayList<IEvent> simpleTransactionArray = HbaseUtil.getConnectionInstance().getTable(hbaseTableConfig.getSimpleTransactionTable(), new SimpleTransactionRecordBean(), null);
                    for (IEvent event :
                            simpleTransactionArray) {
                        SimpleTransactionRecordBean simpleRecord = (SimpleTransactionRecordBean) event;
                        if (new Date(simpleRecord.getUp_time()).before(date)) {
//                            LOG.error("this record need to be checked"+simpleRecord.getV_card_code());
                            FraudDatabean fraudDatabean = getFraud(simpleRecord, hbaseTableConfig.getUserPoseTable(), speedConfig.getSpeedCriterion());
                            if (fraudDatabean.getFraud_flag() == 1) {
                                String jsonStr = JSON.toJSONString(fraudDatabean);
                                KafkaUtil.getProducerInstance(producerConf).sendString(kafkaConfig.getDestTopicName(), jsonStr);
                            }
                            //判断是否逃票后,删除记录，以免重复判断
                            HbaseUtil.getConnectionInstance().deleteEvent(hbaseTableConfig.getSimpleTransactionTable(), simpleRecord.getV_card_code());
                        }
                    }
                    //清除list中的数据，防止内存泄漏
                    simpleTransactionArray.clear();
                    return new Values(simpleTransactionRecordBean);
                });
        return tridentTopology.build();
    }

    public static void main(String[] args) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
//        hbaseTableConfig.createHbaseTable();
        Config conf = new Config();
        conf.setMaxSpoutPending(20);
        conf.setNumWorkers(3);
        ProducerConf producerConf = new ProducerConf(kafkaConfig.getBootstrapServers(), kafkaConfig.getKeySerializer(), kafkaConfig.getValueSerializer());
        producerConf.setClientId("testFraud");
        if (args.length == 0) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("FraudDetectApplication", conf, buildTopology(producerConf));
            Thread.sleep(60 * 1000 * 10 * 10);
            cluster.killTopology("FraudDetectApplication");
            cluster.shutdown();
            System.exit(0);
        } else if (args.length == 1) {
            StormSubmitter.submitTopology(args[0], conf, buildTopology(producerConf));
        } else {
            System.out.println("Usage: FraudDetectApplication [topology name]");
            System.exit(1);
        }
    }
}
