package com.ltybdservice.bolt;

import com.ltybdservice.common.JSONUtil;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class leaveStationBolt  extends BaseBasicBolt{
	
	private Logger LOG = LoggerFactory.getLogger(leaveStationBolt.class);

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("busstatus", "direction","driverid","entertime","gotincount","gotoutcount","gprsid","leavetime"
        		,"notifyways","stationno","stationproperty","terminalno","citycode","workmonth","workdate"));
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		String jsonStr = input.getString(0);
		if(JSONUtil.getJSONType(jsonStr) && jsonStr.contains("130400")){
			Map gpsMap = JSONUtil.Json2Map(jsonStr);
			collector.emit(new Values(gpsMap.get("busStatus"),gpsMap.get("direction"),gpsMap.get("driverId"),
					gpsMap.get("enterTime"),gpsMap.get("gotInCount"),gpsMap.get("gotOutCount"),gpsMap.get("gprsId"),
					gpsMap.get("leaveTime"),gpsMap.get("notifyWays"),gpsMap.get("stationNo"),
					gpsMap.get("stationProperty"),gpsMap.get("terminalNo"),
					gpsMap.get("cityCode"),gpsMap.get("workMonth"),gpsMap.get("workDate")));
		}
	}

}