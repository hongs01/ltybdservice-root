package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDSAllStationPsg;
import com.ltybdservice.pre.prebean.StationPsg;
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
 * @description:
 */
public class BDSAllStationPsgAgg implements ReducerAggregator<BDSAllStationPsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BDSAllStationPsgAgg.class);
    private String destTopicName;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDSAllStationPsgAgg(ProducerConf producerConf, String destTopicName) {
        this.destTopicName = destTopicName;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDSAllStationPsgAgg" + UUID.randomUUID().toString());
    }

    @Override
    public BDSAllStationPsg init() {
        if (producer == null) producer = new KafkaProducer<>(props);
        BDSAllStationPsg allStationPsg = new BDSAllStationPsg();
        allStationPsg.setType(201);
        return allStationPsg;
    }

    @Override
    public BDSAllStationPsg reduce(BDSAllStationPsg curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(201);
        }
        if (producer == null) producer = new KafkaProducer<>(props);
        StationPsg stationPsg = (StationPsg) tuple.getValueByField("stationPsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        String halfHour = (String) tuple.getValueByField("halfHour");
        BDSAllStationPsg.BDSAllStationPsgData allStationPsgData = new BDSAllStationPsg.BDSAllStationPsgData();
        allStationPsgData.setCitycode(cityCode);
        allStationPsgData.setLatitude(stationPsg.getLatitude());
        allStationPsgData.setLongitude(stationPsg.getLongitude());
        allStationPsgData.setStationName(stationPsg.getStationName());
        allStationPsgData.setPsg(stationPsg.getPsg());
        allStationPsgData.setTime(halfHour);


//      得到该城市的数据
        Iterator<BDSAllStationPsg.BDSAllStationPsgData> it = curr.getData().iterator();
        while (it.hasNext()) {
            BDSAllStationPsg.BDSAllStationPsgData dataBean = it.next();
            if (dataBean.getStationName().equals(allStationPsgData.getStationName())) {
                it.remove();
            }
        }
        curr.getData().add(allStationPsgData);
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}
