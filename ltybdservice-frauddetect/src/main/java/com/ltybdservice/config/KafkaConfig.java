package com.ltybdservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class KafkaConfig implements Serializable {
    //kafka consumer conf
    private  String userPoseTopicZkUrl;
    private  String transactionRecordTopicZkUrl;
    //kafka producer consumer topic
    private  String userPoseTopicName;
    private  String transactionRecordTopicName;
    //kafka producer conf
    private  String bootstrapServers;
    private  String keySerializer;
    private  String valueSerializer;
    //kafka producer dest topic
    private  String destTopicName;
    public KafkaConfig() {
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/config.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig(pro);
    }
    private void getConfig(Properties pro) {
        userPoseTopicZkUrl=pro.getProperty("userPoseTopicZkUrl");
        transactionRecordTopicZkUrl=pro.getProperty("transactionRecordTopicZkUrl");
        //kafka producer consumer topic
        userPoseTopicName=pro.getProperty("userPoseTopicName");
        transactionRecordTopicName=pro.getProperty("transactionRecordTopicName");
        //kafka producer conf
        bootstrapServers=pro.getProperty("bootstrapServers");
        keySerializer=pro.getProperty("keySerializer");
        valueSerializer=pro.getProperty("valueSerializer");
        //kafka producer dest topic
        destTopicName=pro.getProperty("destTopicName");
    }

    public String getUserPoseTopicZkUrl() {
        return userPoseTopicZkUrl;
    }

    public void setUserPoseTopicZkUrl(String userPoseTopicZkUrl) {
        this.userPoseTopicZkUrl = userPoseTopicZkUrl;
    }

    public String getTransactionRecordTopicZkUrl() {
        return transactionRecordTopicZkUrl;
    }

    public void setTransactionRecordTopicZkUrl(String transactionRecordTopicZkUrl) {
        this.transactionRecordTopicZkUrl = transactionRecordTopicZkUrl;
    }

    public String getUserPoseTopicName() {
        return userPoseTopicName;
    }

    public void setUserPoseTopicName(String userPoseTopicName) {
        this.userPoseTopicName = userPoseTopicName;
    }

    public String getTransactionRecordTopicName() {
        return transactionRecordTopicName;
    }

    public void setTransactionRecordTopicName(String transactionRecordTopicName) {
        this.transactionRecordTopicName = transactionRecordTopicName;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public void setDestTopicName(String destTopicName) {
        this.destTopicName = destTopicName;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public String getDestTopicName() {
        return destTopicName;
    }
}
