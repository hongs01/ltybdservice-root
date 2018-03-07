package com.lantaiyuan.rtscheduling.bolt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.bean.PassFlow;
import com.lantaiyuan.rtscheduling.common.JSONUtil;

public class PassengerFlowByStationBolt extends BaseBasicBolt{
	private static final long serialVersionUID = 2695650418724090897L;
	OutputCollector collector =null;
	private FileSystem fs;
	//private List<String> lineIds=null;
	private static Map<String,String> stationMap = new HashMap<String,String>();
	//private Map latMap = new HashMap<String,String>();
	public String CityCode;
	
    private static final Logger LOG = LoggerFactory.getLogger(PassengerFlowByStationBolt.class);

	
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String jsonStr = tuple.getString(0);
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject jsonHeader=(JSONObject) json.get("header");
		JSONObject jsonBody=(JSONObject) json.get("body");
	
		if("4097".equals(jsonHeader.getString("msg_id"))){
			//json转化为bean
			PassFlow passflow = JSONUtil.Json2Bean(jsonBody.toString(), PassFlow.class);
			String citycode_StationId=passflow.getCity_code()+"@"+passflow.getBus_station_code();

			collector.emit(new Values(citycode_StationId,passflow));
		}
	}

	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("citycode_StationId","passflow"));
	}
	
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		
	    }
}
