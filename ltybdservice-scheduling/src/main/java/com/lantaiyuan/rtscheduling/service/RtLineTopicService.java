package com.lantaiyuan.rtscheduling.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.common.TimeUtil;

public class RtLineTopicService {
	TimeUtil timeutil=new TimeUtil();
	HbaseService hbaseservice=new HbaseService();
	//获取线路预测客流
	public JSONArray getPredictPassengerFlow(Map<String,Integer>  lineNumMap,String routeId){
		Map<String,Integer> timenumMap=new HashMap<String,Integer>();
		JSONArray ja=new JSONArray();
		if(lineNumMap.size()!=0){
			for(String lineIdHour:lineNumMap.keySet()){
				if(lineIdHour.split("@")[0].equals(routeId)&&!lineIdHour.split("@")[1].equals("\\N")){
					timenumMap.put(lineIdHour.split("@")[1],lineNumMap.get(lineIdHour));
				   }
			    }			
		
	for(String hour:timenumMap.keySet()){
		JSONObject timenum=new JSONObject();
		if(!hour.equals("\\N")){
			timenum.put("nowtime", hour);
			timenum.put("predictPassengerFlow", timenumMap.get(hour));
		}
		ja.add(timenum);
	}
   }
	return ja;
	} 
	
	//取hbase
		public Map<String,Integer> getHbaseData(String tableName,String routeId) throws IOException{
			List<Map<String,String>> listMap=new ArrayList<Map<String,String>>();
			List<Map<String,String>> timelistMap=new ArrayList<Map<String,String>>();
			listMap=hbaseservice.getTodayHbaseData(tableName);
			int real_timePassengerFlow=0;
			JSONObject json=new JSONObject();
			for(int i=0;i<listMap.size();i++){
				if(listMap.get(i).get("lineId").equals(routeId)){
					timelistMap.add(listMap.get(i));
				}
			}
			Map<String,Integer> timeMap=new HashMap<String,Integer>();
			for(int j=0;j<23;j++){
			for(int k=0;k<timelistMap.size();k++){
				if(timeutil.format(j).equals(timelistMap.get(k).get("time"))){
					int temp=timeMap.get(timeutil.format(j))==null?0:timeMap.get(timeutil.format(j));
					int sum=temp+Integer.parseInt(timelistMap.get(k).get("real_timePassengerFlow"));
					timeMap.put(timeutil.format(j), sum);
				}
			}
			}
			return timeMap;
		}
		
		//根据线路ID查询线路名称
		public String getLineNameByLineId(Map<String,String> 	lineNameMap,String lineId){
			String lineName="";
			if(lineNameMap!=null && lineNameMap.size()!=0 && lineNameMap.containsKey(lineId)){
					lineName=lineNameMap.get(lineId);
			}
		   return lineName;	
		}

}
