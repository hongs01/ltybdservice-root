package com.ltybdservice.kafkautil;

import java.io.Serializable;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class ProducerConf implements Serializable {
    private String bootstrapServers;
    private String clientId;
    private String keySerializer;
    private String valueSerializer;

    /**
     * @return type:
     * @author: Administrator
     * @date: 2017/11/22 16:35
     * @method: ProducerConf
     * @param: [bootstrapServers, clientId, keySerializer, valueSerializer]
     * @desciption:
     */
    public ProducerConf(String bootstrapServers, String clientId, String keySerializer, String valueSerializer) {
        this.bootstrapServers = bootstrapServers;
        this.clientId = clientId;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    /**
     * @return type:
     * @author: Administrator
     * @date: 2017/11/22 16:35
     * @method: ProducerConf
     * @param: [keySerializer, valueSerializer]
     * @desciption:
     */
    public ProducerConf(String keySerializer, String valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    /*Administratorurn type:
 2017/11/22h16:38dministrator
  ProducerConf2017/11/22 1[bootstrapServers, keySerializer, valueSerializer]bootstrapServers, keySerializer, valueSerializer]
     * @desciption:
     */
    public ProducerConf(String bootstrapServers, String keySerializer, String valueSerializer) {
        this.bootstrapServers = bootstrapServers;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getClientId() {
        return clientId;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
