package com.lantaiyuan.output.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.bean.output.imp.KafkaParameter;
import com.lantaiyuan.common.connection.KafKaProduceConnection;
import com.lantaiyuan.common.constEnum.KafkaProduce;
import com.lantaiyuan.common.exception.OutPutException;
import com.lantaiyuan.output.IAbstractOutService;
import com.lantaiyuan.utils.ListUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;

/**
 *  kafka 进行输出
 * Created by zhouyongbo on 2017-11-22.
 */
public class KafkaProduceOut extends IAbstractOutService<KafkaParameter> {

    private static  KafkaProducer kafkaProducer;

    @Override
    public void init() {
        kafkaProducer = KafKaProduceConnection.getInstance().getKafkaProducer();
    }

    @Override
    public void sendMsg(KafkaParameter outParameter) {
        if (ListUtil.isListEmpty(outParameter.getTopics())){
                throw new OutPutException("topics 不能为空");
        }
        Object data = outParameter.getData();
        if (data ==null){
            throw new OutPutException("data 不能为空");
        }

        for (KafkaProduce kafkaProduce:outParameter.getTopics()){
            try{
                if (!(data instanceof String)){
                    data = JSON.toJSONString(data);
                }
                kafkaProducer.send(new ProducerRecord<String, String>( kafkaProduce.getKey(),String.valueOf(0),(String)data));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
