package com.lantaiyuan.common.connection;

import com.lantaiyuan.common.config.domain.KafkaProducerConfig;
import kafka.serializer.StringEncoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;

/**
 *
 * kafka生产连接
 * Created by zhouyongbo on 2017-11-22.
 */
public class KafKaProduceConnection {
    private static KafKaProduceConnection kafKaProduceConnection;
    private static KafkaProducer kafkaProducer;
    private KafKaProduceConnection() {

    }

    public static synchronized KafKaProduceConnection getInstance(){
        if (kafKaProduceConnection == null){
            kafKaProduceConnection = new KafKaProduceConnection();
        }
        return kafKaProduceConnection;
    }

    public KafkaProducer getKafkaProducer(){
        if (kafkaProducer == null){
            kafkaProducer = new KafkaProducer(KafkaProducerConfig.getConfig());
        }
//        KEY_SERIALIZER_CLASS_CONFIG;
        return kafkaProducer;
    }
}
