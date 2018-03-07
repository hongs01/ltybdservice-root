package com.ltybdservice.kafkautil;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class KafkaUtil {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtil.class);
    private static KafkaProducer<String, String> producer = null;
    private static KafkaUtil producerInstance = new KafkaUtil();

    private KafkaUtil() {
    }

    /**
     * @return type: com.ltybdservice.kafkautil.KafkaUtil
     * @author: Administrator
     * @date: 2017/11/22 16:32
     * @method: getProducerInstance
     * @param: [conf]
     * @desciption:
     */
    public static KafkaUtil getProducerInstance(ProducerConf conf) {
        if (producer == null) {
            if (conf.getBootstrapServers() == null || conf.getClientId() == null) {

                try {
                    throw new Exception("BootstrapServers or ClientId is null");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Properties props = new Properties();
            props.put("bootstrap.servers", conf.getBootstrapServers());
            props.put("client.id", conf.getClientId());
            props.put("key.serializer", conf.getKeySerializer());
            props.put("value.serializer", conf.getValueSerializer());
            producer = new KafkaProducer<String, String>(props);
        }
        return producerInstance;
    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:32
     * @method: closeProducer
     * @param: []
     * @desciption:
     */
    public void closeProducer() {
        if (producer != null) {
            producer.close();
        }

    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:32
     * @method: sendString
     * @param: [topicName, str]
     * @desciption:
     */
    public void sendString(String topicName, String str) {
        producer.send(new ProducerRecord<>(topicName, str));
    }
}
