package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDSTimeCityPsg;
import com.ltybdservice.pre.prebean.CityGps;
import com.ltybdservice.pre.prebean.CityPsg;
import com.ltybdservice.hbaseutil.HbaseUtil;
import com.ltybdservice.kafkautil.ProducerConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class BDSTimeCityPsgAgg implements ReducerAggregator<BDSTimeCityPsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BDSTimeCityGpsAgg.class);
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private String destTopicName;
    private String cityGpsTable;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDSTimeCityPsgAgg(ProducerConf producerConf, String destTopicName, String cityGpsTable) {
        this.destTopicName = destTopicName;
        this.cityGpsTable = cityGpsTable;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDSTimeCityPsgAgg" + UUID.randomUUID().toString());
    }

    @Override
    public BDSTimeCityPsg init() {
        if (producer == null) producer = new KafkaProducer<>(props);
        BDSTimeCityPsg timeCityPsg = new BDSTimeCityPsg();
        timeCityPsg.setType(105);
        return timeCityPsg;
    }

    @Override
    public BDSTimeCityPsg reduce(BDSTimeCityPsg curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(105);
        }
        if (producer == null) producer = new KafkaProducer<>(props);
        CityPsg cityPsg = (CityPsg) tuple.getValueByField("cityPsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        BDSTimeCityPsg.BDSTimeCityPsgData timeCityPsgData = new BDSTimeCityPsg.BDSTimeCityPsgData();
        timeCityPsgData.setCitycode(cityCode);
        timeCityPsgData.setTime(cityPsg.getHalfHour());

        //时间段内上车人数
        timeCityPsgData.setPsg(cityPsg.getTotalUpFlow());
        //时间段内平均拥挤度
        timeCityPsgData.setCongestion_satisfaction(Double.parseDouble(df.format(cityPsg.getCrowd())));//拥挤满意度
        //预测客流
        timeCityPsgData.setForecast_psg(cityPsg.getForecastPsg());

        //时间段内平均候车时间
        CityGps cityGps = (CityGps) HbaseUtil.getConnectionInstance().getEvent(cityGpsTable, new CityGps(), cityPsg.getCityCode() + cityPsg.getWorkDate() + cityPsg.getHalfHour()); // 从hbase中查找城市平均候车时间,注意查表的键值
        if (cityGps.getToNextStopTime() > 10000) {
            timeCityPsgData.setWaite_satisfaction(0.1);
        } else if (cityGps.getToNextStopTime() > 5000) {
            timeCityPsgData.setWaite_satisfaction(0.2);
        } else if (cityGps.getToNextStopTime() > 1000) {
            timeCityPsgData.setWaite_satisfaction(0.5);
        } else if (cityGps.getToNextStopTime() > 100) {
            timeCityPsgData.setWaite_satisfaction(0.8);
        } else if (cityGps.getToNextStopTime() > 10) {
            timeCityPsgData.setWaite_satisfaction(0.99);
        } else {
            timeCityPsgData.setWaite_satisfaction(1);
        }

        Iterator<BDSTimeCityPsg.BDSTimeCityPsgData> it = curr.getData().iterator();
        while (it.hasNext()) {
            BDSTimeCityPsg.BDSTimeCityPsgData dataBean = it.next();
            if (dataBean.getTime().equals(cityPsg.getHalfHour())) {
                it.remove();
            }
        }
        curr.getData().add(0, timeCityPsgData);
        if (curr.getData().size() == 25) {
            curr.getData().remove(24);
        }
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}
