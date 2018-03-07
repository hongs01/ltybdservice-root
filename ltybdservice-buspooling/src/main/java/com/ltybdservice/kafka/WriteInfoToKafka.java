package com.ltybdservice.kafka;

import com.alibaba.fastjson.JSONObject;
import com.ltybdservice.bean.BuspoolingDemandInfo;
import com.ltybdservice.common.GetRunConfig;
import com.ltybdservice.common.JSONUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

import java.util.Map;
import java.util.Properties;


public class WriteInfoToKafka {
	private String bootserv;
	private static final Logger LOG = LoggerFactory.getLogger(WriteInfoToKafka.class);
	
	public WriteInfoToKafka(){
		GetRunConfig getRunConf = new GetRunConfig();
		Map<String,String> runconfMap = getRunConf.getStormConf();
		bootserv = runconfMap.get("bootstrap.servers");
	}

	public void realTimePush(JedisCommands jedisCommands,String matchId){
		//拼车基础信息
		JSONObject individualcarpooling=new JSONObject();
		//拼车动态
		JSONObject carpoolorder=new JSONObject();
		
		for(int i=0;i<jedisCommands.llen(matchId);i++){
			String bdiJson = jedisCommands.lindex(matchId, i);
			BuspoolingDemandInfo bdiInfo = JSONUtil.Json2Bean(bdiJson, BuspoolingDemandInfo.class);
			
			individualcarpooling.put("orderNo", bdiInfo.getOrderNo());
			individualcarpooling.put("matchId", matchId);
	        individualcarpooling.put("realStartPlaceName",bdiInfo.getRealStartName());
			individualcarpooling.put("realStartPlaceLon",bdiInfo.getRealStartlon());
	        individualcarpooling.put("realStartPlaceLat",bdiInfo.getRealStartlat());
	        individualcarpooling.put("realEndPlaceName",bdiInfo.getRealEndName());
	        individualcarpooling.put("realEndPlaceLon",bdiInfo.getRealEndlon());
	        individualcarpooling.put("realEndPlaceLat",bdiInfo.getRealEndlat());
	        individualcarpooling.put("matchStatus", 1);
	        individualcarpooling.put("matchTimes", jedisCommands.llen(matchId)-i);
	        individualcarpooling.put("realPrice", null);
			individualcarpooling.put("expectAboardTime", bdiInfo.getArrivalTime());
			individualcarpooling.put("expectOffBusTime", bdiInfo.getArrivalTime());
			pushToKafka("individualcarpoolingTopic",i,individualcarpooling.toString());
			
			carpoolorder.put("matchId", matchId);
		    carpoolorder.put("matchStatus", 1);
		    carpoolorder.put("matchExpectTime", -1);
		    carpoolorder.put("matchPersons", jedisCommands.llen(matchId));
		    carpoolorder.put("requiredPersons",7);
		    carpoolorder.put("departTime", null);
		    carpoolorder.put("cityCode",bdiInfo.getCityCode());
		    carpoolorder.put("carpoolRouteId",null);
		    carpoolorder.put("driverPhoneNo", -1);
		    carpoolorder.put("departBusType", -1);
		    carpoolorder.put("departBusSeats", -1);
		    carpoolorder.put("vehicleId", -1);
		    carpoolorder.put("busPlateNumber", -1);
		    carpoolorder.put("driverId", -1);
		    carpoolorder.put("driverName", -1);
		    carpoolorder.put("driverJudgement", -1);
		    pushToKafka("carpoolorderTopic",i,carpoolorder.toString());
		}
	}
	
	public void pushToKafka(String topic,int i,String data){
	    Properties props = new Properties();
	    props.put("bootstrap.servers", bootserv);
	    props.put("acks", "all");
	    props.put("retries", 0);
	    props.put("batch.size", 16384);
	    props.put("linger.ms", 1);
	    props.put("buffer.memory", 33554432);
	    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    Producer<String, String> producer = new KafkaProducer<String, String>(props);
       
	    producer.send(new ProducerRecord<String, String>(topic,String.valueOf(i),data)); 
	    
	    producer.close();
	}
}
