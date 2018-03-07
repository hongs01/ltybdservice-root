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
    private String zkservers;
    private String topics;
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

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getCheckpointLocation() {
        return checkpointLocation;
    }

    public void setCheckpointLocation(String checkpointLocation) {
        this.checkpointLocation = checkpointLocation;
    }

    public String getZkservers() {
        return zkservers;
    }

    public void setZkservers(String zkservers) {
        this.zkservers = zkservers;
    }
}
