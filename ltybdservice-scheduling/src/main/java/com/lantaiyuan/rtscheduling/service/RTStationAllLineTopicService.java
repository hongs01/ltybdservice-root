package com.lantaiyuan.rtscheduling.service;

import java.text.ParseException;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.common.TimeUtil;

public class RTStationAllLineTopicService {
	TimeUtil timeutil=new TimeUtil();
	
	public String getAllLineIdByStationId(Map<String,String> LineStationMap,String stationId){
		StringBuilder sb=new StringBuilder();
		if(LineStationMap.size()>0){
			for(String lineid:LineStationMap.keySet()){
				if(stationId.equals(LineStationMap.get(lineid))){
					sb.append(lineid).append(",");
				}
			}
			if(!sb.toString().equals("")){
				sb.deleteCharAt(sb.length()-1);
			}else{
				sb.append(34);
			}			
		}
		return sb.toString();
	}
	
	public JSONObject getPredictPassengerFlowForOneHour(Map<String,String> preBaseMap,String stationId,String nowtime) throws ParseException{
		JSONObject json=new JSONObject();
		String afterOneHour=timeutil.formatToNextHour(nowtime,1);
		int stagnantTraffic=0;
		int upNumber=0;
		int downNumber=0;
		if(preBaseMap.size()>0){
			for(String lineiddirectionsidtime:preBaseMap.keySet()){
				if(lineiddirectionsidtime.split("@")[2].equals(stationId)&&lineiddirectionsidtime.split("@")[3].equals(afterOneHour)){
					stagnantTraffic+=Integer.parseInt(preBaseMap.get(lineiddirectionsidtime).split("@")[0]);
					upNumber+=Integer.parseInt(preBaseMap.get(lineiddirectionsidtime).split("@")[1]);
					downNumber+=Integer.parseInt(preBaseMap.get(lineiddirectionsidtime).split("@")[2]);
				}
			}	
		}
		json.put("time",afterOneHour);
		json.put("stagnantTraffic",stagnantTraffic);
		json.put("upNumber", upNumber);
		json.put("downNumber", downNumber);
		return json;
	}
	
	public JSONObject getPredictPassengerFlowForTwoHour(Map<String,String> preBaseMap,String stationId,String nowtime) throws ParseException{
		JSONObject json=new JSONObject();
		String afterOneHour=timeutil.formatToNextHour(nowtime,2);
		int stagnantTraffic=0;
		int upNumber=0;
		int downNumber=0;
		if(preBaseMap.size()>0){
			for(String lineiddirectionsidtime:preBaseMap.keySet()){
				if(lineiddirectionsidtime.split("@")[2].equals(stationId)&&lineiddirectionsidtime.split("@")[3].equals(afterOneHour)){
					stagnantTraffic+=Integer.parseInt(preBaseMap.get(lineiddirectionsidtime).split("@")[0]);
					upNumber+=Integer.parseInt(preBaseMap.get(lineiddirectionsidtime).split("@")[1]);
					downNumber+=Integer.parseInt(preBaseMap.get(lineiddirectionsidtime).split("@")[2]);
				}
			}	
		}
		json.put("time",afterOneHour);
		json.put("stagnantTraffic",stagnantTraffic);
		json.put("upNumber", upNumber);
		json.put("downNumber", downNumber);
		return json;
	}
}
