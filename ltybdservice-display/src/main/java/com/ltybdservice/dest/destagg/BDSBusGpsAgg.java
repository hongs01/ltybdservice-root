package com.ltybdservice.dest.destagg;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.dest.destbean.BDSBusGps;
import com.ltybdservice.pre.prebean.BusGps;
import com.ltybdservice.pre.prebean.BusPsg;
import com.ltybdservice.hbaseutil.HbaseUtil;
import com.ltybdservice.hiveutil.DataSourceConf;
import com.ltybdservice.kafkautil.ProducerConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class BDSBusGpsAgg implements ReducerAggregator<BDSBusGps> {
    private static final Logger LOG = LoggerFactory.getLogger(BDSBusGpsAgg.class);
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private String  busPsgTable;
    private DataSourceConf dataSourceConf;
    private String destTopicName;
    private  Producer<String, String> producer;
    private Properties props=new Properties();
    public BDSBusGpsAgg(DataSourceConf dataSourceConf, ProducerConf producerConf, String destTopicName, String  busPsgTable) {
        this.destTopicName = destTopicName;
        this.dataSourceConf = dataSourceConf;
        this.busPsgTable=busPsgTable;
        props.put("bootstrap.servers", producerConf.getBootstrapServers());
        props.put("key.serializer", producerConf.getKeySerializer());
        props.put("value.serializer", producerConf.getValueSerializer());
        props.put("client.id", "BDSBusGpsAgg" + UUID.randomUUID().toString());
    }

    @Override
    public BDSBusGps init() {
        if(producer==null)producer = new KafkaProducer<>(props);
        BDSBusGps busGpsUp = new BDSBusGps();
        busGpsUp.setType(302);
        return busGpsUp;
    }

    @Override
    public BDSBusGps reduce(BDSBusGps curr, TridentTuple tuple) {
        if(curr.getType()==0){
            curr.setType(302);
        }
        if(producer==null)producer = new KafkaProducer<>(props);
        BusGps busGps = (BusGps) tuple.getValueByField("busGps");
        BDSBusGps.BDSBusGpsData busGpsUpDataBean = null;
        BusPsg busPsg = (BusPsg) HbaseUtil.getConnectionInstance().getEvent(busPsgTable,new BusPsg(),busGps.getCityCode()+busGps.getBusId()+busGps.getWorkDate());//通过查询hbase得到最新客流数据,注意查表键值
        busGpsUpDataBean = BDSBusGps.BDSBusGpsData.GpsPsg2GpsUpBean(dataSourceConf,busGps, busPsg);
        //不断更新每条线路的gps数据和客流信息，丢掉原来的换成新到的数据
        Iterator<BDSBusGps.BDSBusGpsData> it = curr.getData().iterator();
        while (it.hasNext()) {
            BDSBusGps.BDSBusGpsData dataBean = it.next();
            if (dataBean.getBus_no() == busGpsUpDataBean.getBus_no()) {
                it.remove();
            }
        }
        curr.getData().add(busGpsUpDataBean);
        String jsonStr = JSON.toJSONString(curr);
        producer.send(new ProducerRecord<>(destTopicName, jsonStr));
        return curr;
    }
}
