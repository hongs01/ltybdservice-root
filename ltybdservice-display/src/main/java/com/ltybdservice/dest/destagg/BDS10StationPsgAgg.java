package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDS10StationPsg;
import com.ltybdservice.pre.prebean.StationPsg;
import com.ltybdservice.kafkautil.ProducerConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class BDS10StationPsgAgg implements ReducerAggregator<BDS10StationPsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BDS10StationPsgAgg.class);
    private String destTopicName;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDS10StationPsgAgg(ProducerConf producerConf, String destTopicName) {
        this.destTopicName = destTopicName;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDS10StationPsgAgg" + UUID.randomUUID().toString());
    }

    @Override
    public BDS10StationPsg init() {
        if (producer == null) producer = new KafkaProducer<>(props);
        BDS10StationPsg listStationPsg = new BDS10StationPsg();
        listStationPsg.setType(202);
        return listStationPsg;
    }

    @Override
    public BDS10StationPsg reduce(BDS10StationPsg curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(202);
        }
        if (producer == null) producer = new KafkaProducer<>(props);
        StationPsg stationPsg = (StationPsg) tuple.getValueByField("stationPsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        BDS10StationPsg.BDS10StationPsgData stationDayPsgData = new BDS10StationPsg.BDS10StationPsgData();
        stationDayPsgData.setCitycode(cityCode);
        stationDayPsgData.setLatitude(stationPsg.getLatitude());
        stationDayPsgData.setLongitude(stationPsg.getLongitude());
        stationDayPsgData.setPsg(stationPsg.getPsg());
        stationDayPsgData.setStationName(stationPsg.getStationName());
        Iterator<BDS10StationPsg.BDS10StationPsgData> it = curr.getData().iterator();
        while (it.hasNext()) {
            BDS10StationPsg.BDS10StationPsgData dataBean = it.next();
            if (dataBean.getStationName().equals(stationDayPsgData.getStationName())) {
                it.remove();
            }
        }
        curr.getData().add(stationDayPsgData);
        Collections.sort(curr.getData(), new Comparator<BDS10StationPsg.BDS10StationPsgData>() {
            @Override
            public int compare(BDS10StationPsg.BDS10StationPsgData o1, BDS10StationPsg.BDS10StationPsgData o2) {
                return o2.getPsg() - o1.getPsg();
            }
        });
        if (curr.getData().size() == 11) {
            curr.getData().remove(10);                                               //降序排列后将最后一个记录删除
        }
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}
