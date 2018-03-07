package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDSAllLinePsg;
import com.ltybdservice.pre.prebean.CityPsg;
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
 * @description: 按城市、日期统计客流量
 */
public class BDSAllLinePsgAgg implements ReducerAggregator<BDSAllLinePsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BDSAllLinePsgAgg.class);
    private String destTopicName;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDSAllLinePsgAgg(ProducerConf producerConf, String destTopicName) {
        this.destTopicName = destTopicName;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDSAllLinePsgAgg" + UUID.randomUUID().toString());
    }

    @Override
    public BDSAllLinePsg init() {
        if (producer == null) producer = new KafkaProducer<>(props);
        BDSAllLinePsg cityTotalPsg = new BDSAllLinePsg();
        cityTotalPsg.setType(104);
        return cityTotalPsg;
    }

    @Override
    public BDSAllLinePsg reduce(BDSAllLinePsg curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(104);
        }
        if (producer == null) producer = new KafkaProducer<>(props);
        CityPsg cityPsg = (CityPsg) tuple.getValueByField("cityPsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        curr.getData().setCitycode(cityCode);
        curr.getData().setTotal_psg(cityPsg.getTotalUpFlow());
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}
