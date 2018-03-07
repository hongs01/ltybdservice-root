package com.ltybdservice.bolt;

import com.alibaba.fastjson.JSONObject;
import com.ltybdservice.jsonutil.JsonUtil;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class inoutStationBolt extends BaseBasicBolt{
	
	private Logger LOG = LoggerFactory.getLogger(inoutStationBolt.class);

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("dev_id", "line_id","vehicle_id","time","lon","lat","vec1","vec2"
        		,"distance","angle","altitude","vehicle_status","bus_station_code","bus_station_no"
        		,"driver_id","station_flag","station_report","dis_next","time_next","next_station_no"
        		,"in_out_flag","direction","field_no","field_property","citycode","workmonth","workdate"));
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub	
		String jsonStr = input.getString(0);
		try{
			JSONObject json = JSONObject.parseObject(jsonStr);
			JSONObject jsonBody=(JSONObject) json.get("body");
			String workmonth = new java.text.SimpleDateFormat("yyyyMM").format(Long.parseLong(jsonBody.get("time").toString())*1000);
			String workdate = new java.text.SimpleDateFormat("yyyyMMdd").format(Long.parseLong(jsonBody.get("time").toString())*1000);
			String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(jsonBody.get("time").toString())*1000);
			String city_code = jsonBody.get("city_code").toString();
			if(city_code.length() > 6){
				city_code = city_code.substring(0,6);
			}
			collector.emit(new Values(jsonBody.get("dev_id").toString(),jsonBody.get("line_id").toString(),jsonBody.get("vehicle_id").toString(),time,jsonBody.get("lon").toString(),
					jsonBody.get("lat").toString(),jsonBody.get("vec1").toString(),jsonBody.get("vec2").toString(),jsonBody.get("distance").toString(),jsonBody.get("angle").toString(),
					jsonBody.get("altitude").toString(),jsonBody.get("vehicle_status").toString(),jsonBody.get("bus_station_code").toString(),jsonBody.get("bus_station_no").toString(),
					jsonBody.get("driver_id").toString(),jsonBody.get("station_flag").toString(),jsonBody.get("station_report").toString(),jsonBody.get("dis_next").toString(),jsonBody.get("time_next").toString(),
					jsonBody.get("next_station_no").toString(),jsonBody.get("in_out_flag").toString(),jsonBody.get("direction").toString(),jsonBody.get("field_no").toString(),
					jsonBody.get("field_property").toString(),city_code,workmonth,workdate));
		}
		catch (Exception e){
			LOG.error("convert to json fail");
		}
	}
}