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

public class gpsBolt extends BaseBasicBolt{
	
	private Logger LOG = LoggerFactory.getLogger(gpsBolt.class);
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("dev_id","line_id","direction","vehicle_id","gps_time","lon","lat","vec1"
				,"vec2","distance","angle","altitude","dis_next","time_next","next_station_no","signal","temperature"
				,"citycode","workmonth","workdate"));
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		String jsonStr = input.getString(0);
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject jsonBody=(JSONObject) json.get("body");
		String workmonth = new java.text.SimpleDateFormat("yyyyMM").format(Long.parseLong(jsonBody.get("gps_time").toString())*1000);
		String workdate = new java.text.SimpleDateFormat("yyyyMMdd").format(Long.parseLong(jsonBody.get("gps_time").toString())*1000);
		String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(jsonBody.get("gps_time").toString())*1000);
		collector.emit(new Values(jsonBody.get("dev_id").toString(),jsonBody.get("line_id").toString(),jsonBody.get("direction").toString(),jsonBody.get("vehicle_id").toString(),
				time,jsonBody.get("lon").toString(),jsonBody.get("lat").toString(),
				jsonBody.get("vec1").toString(),jsonBody.get("vec2").toString(),jsonBody.get("distance").toString(),jsonBody.get("angle").toString(),
				jsonBody.get("altitude").toString(),jsonBody.get("dis_next").toString(),jsonBody.get("time_next").toString(),jsonBody.get("next_station_no").toString(),
				jsonBody.get("signal").toString(),jsonBody.get("temperature").toString(),jsonBody.get("city_code").toString(),workmonth,workdate));
	}

}
