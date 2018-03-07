package com.ltybdservice.kafkautil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class KafkaUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtilTest.class);
    private static final String topicName = "test";

    public static void main(String[] args) throws SQLException, InterruptedException, PropertyVetoException {
        ProducerConf conf = new ProducerConf("hadoop01.ltyicloud.com:6667", "test", "org.apache.kafka.common.serialization.IntegerSerializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaUtil producerInstance = KafkaUtil.getProducerInstance(conf);
        String str = "testwashtopic";
        int i = 1000;
        while (i > 0) {
            producerInstance.sendString(topicName, str);
            LOG.info(str);
            TimeUnit.SECONDS.sleep(1);
            i--;
        }
        producerInstance.closeProducer();
    }
}
