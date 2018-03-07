package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDSTimeCityGps;
import com.ltybdservice.pre.prebean.CityGps;
import com.ltybdservice.kafkautil.ProducerConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 按城市，半小时统计行车平均速度
 */
public class BDSTimeCityGpsAgg implements ReducerAggregator<BDSTimeCityGps> {
    private static final Logger LOG = LoggerFactory.getLogger(BDSTimeCityGpsAgg.class);
    private String destTopicName;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDSTimeCityGpsAgg(ProducerConf producerConf, String destTopicName) {
        this.destTopicName = destTopicName;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDSTimeCityGpsAgg" + UUID.randomUUID().toString());
    }


    @Override
    public BDSTimeCityGps init() {
        if (producer == null) producer = new KafkaProducer<>(props);
        BDSTimeCityGps timeCityGps = new BDSTimeCityGps();
        timeCityGps.setType(304);
        return timeCityGps;
    }

    @Override
    public BDSTimeCityGps reduce(BDSTimeCityGps curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(304);
        }
        if (producer == null) producer = new KafkaProducer<>(props);
        CityGps cityGps = (CityGps) tuple.getValueByField("cityGps");
        BDSTimeCityGps.BDSTimeCityGpsData timeCityGpsData = new BDSTimeCityGps.BDSTimeCityGpsData();
        timeCityGpsData.setCitycode(cityGps.getCityCode());
        timeCityGpsData.setTime(cityGps.getHalfHour());
        timeCityGpsData.setSvg_speed(cityGps.getTimeAvgSpeed());

        //得到该城市的数据
        Iterator<BDSTimeCityGps.BDSTimeCityGpsData> it = curr.getData().iterator();
        while (it.hasNext()) {
            BDSTimeCityGps.BDSTimeCityGpsData dataBean = it.next();
            if (dataBean.getTime().equals(cityGps.getHalfHour())) {
                it.remove();
            }
        }
        curr.getData().add(timeCityGpsData);
        if (curr.getData().size() == 25) {
            curr.getData().remove(0);
        }
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}