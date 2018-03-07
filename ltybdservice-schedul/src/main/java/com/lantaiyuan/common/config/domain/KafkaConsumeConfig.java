package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyongbo on 2017-11-17.
 */
public class KafkaConsumeConfig extends ISetProperties {

    @Override
    public String getPrefix() {
        return "kafka.consume";
    }
    private static List<String> brokerList;
    private static int acks;
    private static String type;
    private static String group;
    private static String keySerializer;
    private static String valueSerializer;
    private static boolean autoCommit;
    private static String offsetReset;
    private static String bootstrapSservers;


    private static String groupId;

    public static List<String> getBrokerList() {
        return brokerList;
    }

    public static int getAcks() {
        return acks;
    }

    public static String getType() {
        return type;
    }

    public static String getKeySerializer() {
        return keySerializer;
    }

    public static String getValueSerializer() {
        return valueSerializer;
    }

    public static boolean isAutoCommit() {
        return autoCommit;
    }

    public static String getOffsetReset() {
        return offsetReset;
    }

    public static String getGroup() {
        return group;
    }

    public static String getBootstrapSservers() {
        return bootstrapSservers;
    }
    public static String getGroupId() {
        return groupId;
    }

    public static Map<String,String> getConfig(){
        Map<String,String> map = new HashMap<String,String>();

        map.put("bootstrap.servers",String.join(",",brokerList));

        if (!StringUtil.isEmpty(keySerializer)){
            map.put("key.deserializer",keySerializer);
        }

        if (!StringUtil.isEmpty(valueSerializer)){
            map.put("value.deserializer",valueSerializer);
        }

        if (!StringUtil.isEmpty(group)){
            map.put("group.id",group);
        }

        map.put("enable.auto.commit",String.valueOf(isAutoCommit()));
        if (!StringUtil.isEmpty(offsetReset)){
            map.put("auto.offset.reset",offsetReset);
        }

        return map;
    }

    public static Map<String,String> getConfigNew(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("bootstrap.servers",bootstrapSservers);
        map.put("key.deserializer",keySerializer);
        map.put("value.deserializer",valueSerializer);
        if (!StringUtil.isEmpty(groupId)){
            map.put("group.id",groupId);
        }
        map.put("auto.offset.reset",offsetReset);
        return map;
    }
}
