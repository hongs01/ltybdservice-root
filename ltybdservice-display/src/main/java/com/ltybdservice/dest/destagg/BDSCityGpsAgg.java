package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDSCityGps;
import com.ltybdservice.pre.prebean.CityGps;
import com.ltybdservice.kafkautil.ProducerConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 按城市、日期统计行车速度
 */
public class BDSCityGpsAgg implements ReducerAggregator<BDSCityGps> {
    private static final Logger LOG = LoggerFactory.getLogger(BDSCityGpsAgg.class);
    private String destTopicName;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDSCityGpsAgg(ProducerConf producerConf, String destTopicName) {
        this.destTopicName = destTopicName;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDSCityGpsAgg" + UUID.randomUUID().toString());
    }


    @Override
    public BDSCityGps init() {
        if (producer == null) producer = new KafkaProducer<>(props);
        BDSCityGps cityMaxMinSpeedLineAvgSpeed = new BDSCityGps();
        cityMaxMinSpeedLineAvgSpeed.setType(303);
        return cityMaxMinSpeedLineAvgSpeed;
    }

    @Override
    public BDSCityGps reduce(BDSCityGps curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(303);
        }
        if (producer == null) producer = new KafkaProducer<>(props);
        CityGps cityGps = (CityGps) tuple.getValueByField("cityGps");
        curr.getData().setCityCode(cityGps.getCityCode());
        curr.getData().setMax_speed(cityGps.getMax_speed());
        curr.getData().setMax_speed_line_name(cityGps.getMax_speed_line_name());
        curr.getData().setMin_speed(cityGps.getMin_speed());
        curr.getData().setMin_speed_line_name(cityGps.getMin_speed_line_name());
        curr.getData().setSvg_speed(cityGps.getCityAvgSpeed());
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}