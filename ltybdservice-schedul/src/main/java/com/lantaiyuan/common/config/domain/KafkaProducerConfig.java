package com.lantaiyuan.common.config.domain;


import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.StringUtil;
import kafka.serializer.StringEncoder;

import java.util.List;
import java.util.Properties;

/**
 * Created by zhouyongbo on 2017-11-16.
 */
public class KafkaProducerConfig extends ISetProperties {
    @Override
    public String getPrefix() {
        return "kafka.producer";
    }
    private static List<String> brokerList;
    private static String acks;
    private static Integer retries;
    private static String batchSize;
    private static Integer lingerMs;
    private static Integer bufferMemory;
    private static String keySerializer;
    private static String valueSerializer;
    private static String serializerClass;

    public static List<String> getBrokerList() {
        return brokerList;
    }

    public static String getAcks() {
        return acks;
    }

    public static String getBatchSize() {
        return batchSize;
    }

    public static Integer getLingerMs() {
        return lingerMs;
    }

    public static Integer getBufferMemory() {
        return bufferMemory;
    }

    public static Integer getRetries() {
        return retries;
    }

    public static String getKeySerializer() {
        return keySerializer;
    }

    public static String getValueSerializer() {
        return valueSerializer;
    }

    public static Properties getConfig(){
        Properties props = new Properties();
        props.put("bootstrap.servers", String.join(",",brokerList));
        if (!StringUtil.isEmpty(acks)){
            props.put("acks", acks);
        }
        if (retries != null && retries>0){
            props.put("retries",retries);
        }
        if (!StringUtil.isEmpty(batchSize)){
            props.put("batch.size", batchSize);
        }
        if (lingerMs != null && lingerMs>0){
            props.put("linger.ms", lingerMs);
        }
        if (bufferMemory != null && bufferMemory>0){
            props.put("buffer.memory", bufferMemory);
        }

        if (!StringUtil.isEmpty(keySerializer)){
            props.put("key.serializer", keySerializer);
        }

        if (!StringUtil.isEmpty(valueSerializer)){
            props.put("value.serializer", valueSerializer);
        }
        if (!StringUtil.isEmpty(serializerClass)){
            props.put("value.serializer", serializerClass);
        }
        return props;
    }

}
