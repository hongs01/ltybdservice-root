package com.ltybdservice.bolt;

import com.alibaba.fastjson.JSONObject;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class passFlowBolt  extends BaseBasicBolt{
	
	private Logger LOG = LoggerFactory.getLogger(passFlowBolt.class);

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("dev_id", "line_id","stat_time","lon","lat","bus_station_code","bus_station_no","vehicle_id"
        		,"direction","working_flag","fdoor_flag","bdoor_flag","online_flag","speed_flag","position_flag",
        		"up_flow","down_flow","total_flow","citycode","workmonth","workdate"));
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub	
		String jsonStr = input.getString(0);
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject jsonBody=(JSONObject) json.get("body");
		String workmonth = new java.text.SimpleDateFormat("yyyyMM").format(Long.parseLong(jsonBody.get("stat_time").toString())*1000);
		String workdate = new java.text.SimpleDateFormat("yyyyMMdd").format(Long.parseLong(jsonBody.get("stat_time").toString())*1000);
		String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(jsonBody.get("stat_time").toString())*1000);
		String city_code = jsonBody.get("city_code").toString();
		if(city_code.length() > 6){
			city_code = city_code.substring(0,6);
		}
		collector.emit(new Values(jsonBody.get("dev_id").toString(),jsonBody.get("line_id").toString(),time,jsonBody.get("lon").toString(),jsonBody.get("lat").toString(),
				jsonBody.get("bus_station_code").toString(),jsonBody.get("bus_station_no").toString(),jsonBody.get("vehicle_id").toString(),
				jsonBody.get("direction").toString(),jsonBody.get("working_flag").toString(),jsonBody.get("fdoor_flag").toString(),jsonBody.get("bdoor_flag").toString(),
				jsonBody.get("online_flag").toString(),jsonBody.get("speed_flag").toString(),jsonBody.get("position_flag").toString(),jsonBody.get("up_flow").toString(),
				jsonBody.get("down_flow").toString(),jsonBody.get("total_flow").toString(),city_code,workmonth,workdate));
	}

}