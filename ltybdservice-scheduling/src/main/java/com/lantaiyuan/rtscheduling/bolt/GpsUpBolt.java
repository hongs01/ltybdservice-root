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
import com.lantaiyuan.rtscheduling.bean.GPS;
import com.lantaiyuan.rtscheduling.common.JSONUtil;

public class GpsUpBolt extends BaseBasicBolt{
	private FileSystem fs;
	//private List<String> lineIds=null;
	private static Map<Integer,String> gprsidMap = new HashMap<Integer,String>();
	//private Map latMap = new HashMap<String,String>();
	public String CityCode;
	
    private static final Logger LOG = LoggerFactory.getLogger(GpsUpBolt.class);
	
	private static final long serialVersionUID = 2695650418724090897L;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
			
	    }
	
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String jsonStr = tuple.getString(0);
		//System.err.println(jsonStr);
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject jsonHeader=(JSONObject) json.get("header");
		JSONObject jsonBody=(JSONObject) json.get("body");
		
		if("4101".equals(jsonHeader.getString("msg_id"))){
			//json转化为bean
			GPS gps = JSONUtil.Json2Bean(jsonBody.toString(), GPS.class);
             System.err.println("jsonBody.toString():"+jsonBody.toString());
            String citycode_lineId_direction=gps.getCity_code()+"@"+gps.getLine_id()+"@"+gps.getDirection();
            collector.emit(new Values(citycode_lineId_direction,gps));
		}
		
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("citycode_lineId_direction","gps"));
	}
	
}
