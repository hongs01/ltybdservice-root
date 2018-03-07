package com.lantaiyuan.rtscheduling.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.common.TimeUtil;

public class RTStationTopicService {
	TimeUtil timeutil=new TimeUtil();
	
	public JSONObject getPredictPassengerFlowForOneHour(Map<String,String> preBaseMap,String lineId,String lineDirection,String stationId,String nowtime) throws ParseException{
		JSONObject json=new JSONObject();
		String afterOneHour=timeutil.formatToNextHour(nowtime,1);
		Map<String,String> timeNumMap=new HashMap<String,String>();
		if(preBaseMap.size()>0){
			for(String lineiddirectionsidtime:preBaseMap.keySet()){
				if(lineiddirectionsidtime.split("@")[0].equals(lineId)&&lineiddirectionsidtime.split("@")[1].equals(lineDirection)
						&&lineiddirectionsidtime.split("@")[2].equals(stationId)){
					timeNumMap.put(lineiddirectionsidtime.split("@")[3], preBaseMap.get(lineiddirectionsidtime));
				}
			}	
		}
		
		if(!timeNumMap.isEmpty()){
			json.put("time",afterOneHour);
			if(timeNumMap.containsKey(afterOneHour)){
				json.put("stagnantTraffic",timeNumMap.get(afterOneHour).split("@")[0]);
				json.put("upNumber", timeNumMap.get(afterOneHour).split("@")[1]);
				json.put("downNumber", timeNumMap.get(afterOneHour).split("@")[2]);
			}else{
				json.put("stagnantTraffic",0);	
				json.put("upNumber",0);	
				json.put("downNumber",0);	
			}
			
		}else{
			json.put("time",afterOneHour);
			json.put("stagnantTraffic",0);
			json.put("upNumber",0);
			json.put("downNumber",0);
		}
		
		return json;
	}
	
	
	public JSONObject getPredictPassengerFlowForTwoHour(Map<String,String> preBaseMap,String lineId,String lineDirection,String stationId,String nowtime) throws ParseException{
		JSONObject json=new JSONObject();
		String afterOneHour=timeutil.formatToNextHour(nowtime,2);
		System.err.println("afterOneHour:"+afterOneHour);
		Map<String,String> timeNumMap=new HashMap<String,String>();
		if(preBaseMap.size()>0){
			for(String lineiddirectionsidtime:preBaseMap.keySet()){
				if(lineiddirectionsidtime.split("@")[0].equals(lineId)&&lineiddirectionsidtime.split("@")[1].equals(lineDirection)
						&&lineiddirectionsidtime.split("@")[2].equals(stationId)){
					timeNumMap.put(lineiddirectionsidtime.split("@")[3], preBaseMap.get(lineiddirectionsidtime));
				}
			}	
		}
		
		if(!timeNumMap.isEmpty()){
			json.put("time",afterOneHour);
			if(timeNumMap.containsKey(afterOneHour)){
				json.put("stagnantTraffic",timeNumMap.get(afterOneHour).split("@")[0]);
				json.put("upNumber", timeNumMap.get(afterOneHour).split("@")[1]);
				json.put("downNumber", timeNumMap.get(afterOneHour).split("@")[2]);
			}else{
				json.put("stagnantTraffic",0);	
				json.put("upNumber", 0);
				json.put("downNumber", 0);
			}
			
		}else{
			json.put("time",afterOneHour);
			json.put("stagnantTraffic",0);
			json.put("upNumber",0);
			json.put("downNumber",0);
		}
		return json;
	}
	
}
