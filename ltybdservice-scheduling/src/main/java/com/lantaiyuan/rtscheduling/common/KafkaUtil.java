package com.lantaiyuan.rtscheduling.common;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.alibaba.fastjson.JSONObject;

public class KafkaUtil {
	//推送kafka
	public void pushToKafka(String topic, KafkaProducer<String, String> producer,JSONObject jsonobject){
		System.err.println("fsdfdsf"+jsonobject.toString());
	   	 try {
             Thread.sleep(8000);
         } catch (InterruptedException e) {
             e.printStackTrace(); 
         }
		  producer.send(new ProducerRecord<String, String>(topic,String.valueOf(0),jsonobject.toString())); 
		    
		//  producer.close();
		}
}
