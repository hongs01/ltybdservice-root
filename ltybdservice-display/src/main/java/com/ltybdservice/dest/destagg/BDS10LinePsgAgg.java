package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDS10LinePsg;
import com.ltybdservice.pre.prebean.LinePsg;
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
 * @description: type:103
 * 按城市、日期获取top10客流量的线路
 */
public class BDS10LinePsgAgg implements ReducerAggregator<BDS10LinePsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BDS10LinePsgAgg.class);
    private String destTopicName;
    private Producer<String, String> producer;
    private Properties props = new Properties();

    public BDS10LinePsgAgg(ProducerConf producerConf, String destTopicName) {
        this.destTopicName = destTopicName;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDS10LinePsgAgg" + UUID.randomUUID().toString());
    }

    @Override
    public BDS10LinePsg init() {
        if (producer == null) {
            producer = new KafkaProducer<>(props);
        }
        BDS10LinePsg top10BDS10LinePsg = new BDS10LinePsg();
        top10BDS10LinePsg.setType(103);
        return top10BDS10LinePsg;
    }

    @Override
    public BDS10LinePsg reduce(BDS10LinePsg curr, TridentTuple tuple) {
        if (curr.getType() == 0) {
            curr.setType(103);
        }
        if (producer == null) {
            producer = new KafkaProducer<>(props);
        }
        LinePsg linePsg = (LinePsg) tuple.getValueByField("linePsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        BDS10LinePsg.BDS10LinePsgData linePsgDataBean = new BDS10LinePsg.BDS10LinePsgData();
        linePsgDataBean.setCityCode(cityCode);
        linePsgDataBean.setLine_id(String.valueOf(linePsg.getLineId()));
        linePsgDataBean.setLine_name(linePsg.getLineName());
        linePsgDataBean.setTotal_psg(linePsg.getTotalUpFlow());

        Iterator<BDS10LinePsg.BDS10LinePsgData> it = curr.getData().iterator();
        while (it.hasNext()) {
            BDS10LinePsg.BDS10LinePsgData dataBean = it.next();
            if (dataBean.getLine_id().equals(linePsgDataBean.getLine_id())) {
                it.remove();
            }
        }
        curr.getData().add(linePsgDataBean);
        Collections.sort(curr.getData(), new Comparator<BDS10LinePsg.BDS10LinePsgData>() {
            @Override
            public int compare(BDS10LinePsg.BDS10LinePsgData o1, BDS10LinePsg.BDS10LinePsgData o2) {
                //降序排列
                return o2.getTotal_psg() - o1.getTotal_psg();
            }
        });
        if (curr.getData().size() == 11) {
            //降序排列后将最后一个记录删除
            curr.getData().remove(10);
        }
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}
