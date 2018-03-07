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

public class fieldBolt extends BaseBasicBolt{
	
	private Logger LOG = LoggerFactory.getLogger(fieldBolt.class);

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("angle", "busstatus","driverid","fieldno","flag","gprsid","kph","latitude"
				,"longitude","oils","powerconsume","property","terminalno","time","citycode","workmonth","workdate"));
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		String jsonStr = input.getString(0);
		if(JSONUtil.getJSONType(jsonStr) && jsonStr.contains("130400")){
			Map gpsMap = JSONUtil.Json2Map(jsonStr);
			collector.emit(new Values(gpsMap.get("angle"),gpsMap.get("busStatus"),gpsMap.get("driverId"),gpsMap.get("fieldNo"),
					gpsMap.get("flag"),gpsMap.get("gprsId"),gpsMap.get("kph"),gpsMap.get("latitude"),gpsMap.get("longitude"),
					gpsMap.get("oils"),gpsMap.get("powerConsume"),gpsMap.get("property"),gpsMap.get("terminalNo"),gpsMap.get("time"),
					gpsMap.get("cityCode"),gpsMap.get("workMonth"),gpsMap.get("workDate")));
		}
	}

}
