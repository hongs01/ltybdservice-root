package com.ltybdservice.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/12/19$ 16:49$
 * @description:
 */
public class KafkaConf {
    private String bootstrapServers;
    private String subscribeType;
    private String topic1;
    private String topic2;
    private String checkpointLocation;
    public static KafkaConf param = new KafkaConf().getParam();

    private synchronized KafkaConf getParam() {
        if (param != null) {
            return param;
        }
        InputStream in = this.getClass().getResourceAsStream("/kafka.yml");
        KafkaConf kafkaConf = new Yaml().loadAs(in, KafkaConf.class);
        return kafkaConf;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getSubscribeType() {
        return subscribeType;
    }

    public void setSubscribeType(String subscribeType) {
        this.subscribeType = subscribeType;
    }

    public String getTopic1() {
        return topic1;
    }

    public void setTopic1(String topic1) {
        this.topic1 = topic1;
    }

    public String getTopic2() {
        return topic2;
    }

    public void setTopic2(String topic2) {
        this.topic2 = topic2;
    }

    public String getCheckpointLocation() {
        return checkpointLocation;
    }

    public void setCheckpointLocation(String checkpointLocation) {
        this.checkpointLocation = checkpointLocation;
    }
}
