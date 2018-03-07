package com.lantaiyuan.rtscheduling.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaConfig implements Serializable{
	Properties property = new Properties();
	 
	public KafkaConfig() {
	        InputStream in = this.getClass().getResourceAsStream("/config.properties");
	        try {
	        	property.load(in);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	   
	 public KafkaProducer<String, String> getKafkaProducer() {
	    	String kafkaServerUrl = property.getProperty("kafkaServerUrl");
	    	property.put("bootstrap.servers",kafkaServerUrl);
	    	property.put("acks", "all");
	    	property.put("retries", 0);
	    	property.put("batch.size", 16384);
	    	property.put("linger.ms", 1);
	    	property.put("buffer.memory", 33554432);
	    	property.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    	property.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    	KafkaProducer<String, String> producer=new KafkaProducer<String, String>(property);
	    	return producer;
	    }

}
