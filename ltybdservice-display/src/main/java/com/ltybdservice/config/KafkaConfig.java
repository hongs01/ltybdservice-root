package com.ltybdservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 10.1.254.76:3306->外网访问 202.104.136.228:5506
 */
public class KafkaConfig implements Serializable {
    //kafka consumer conf
    private String gpsTopicZkUrl;
    private String psgTopicZkUrl;
    //kafka producer consumer topic
    private String gpsTopicName;
    private String psgTopicName;
    //kafka producer conf
    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    //kafka producer dest topic
    private String destTopicName;

    /**
     * @return typeAdministratorhor: Admini2017/11/22 16:47date: 2017/11/2KafkaConfig  * @method:[]afkaConfig
     * @param: []
     * @desciption:
     */
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

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:41
     * @method: getConfig
     * @param: [pro]
     * @desciption:
     */
    private void getConfig(Properties pro) {
        gpsTopicZkUrl = pro.getProperty("gpsTopicZkUrl");
        psgTopicZkUrl = pro.getProperty("psgTopicZkUrl");
        //kafka producer consumer topic
        gpsTopicName = pro.getProperty("gpsTopicName");
        psgTopicName = pro.getProperty("psgTopicName");
        //kafka producer conf
        bootstrapServers = pro.getProperty("bootstrapServers");
        keySerializer = pro.getProperty("keySerializer");
        valueSerializer = pro.getProperty("valueSerializer");
        //kafka producer dest topic
        destTopicName = pro.getProperty("destTopicName");
    }

    public String getGpsTopicZkUrl() {
        return gpsTopicZkUrl;
    }

    public String getPsgTopicZkUrl() {
        return psgTopicZkUrl;
    }

    public String getGpsTopicName() {
        return gpsTopicName;
    }

    public String getPsgTopicName() {
        return psgTopicName;
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
