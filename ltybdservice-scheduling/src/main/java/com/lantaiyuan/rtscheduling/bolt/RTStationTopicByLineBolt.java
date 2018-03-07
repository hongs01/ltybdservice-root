package com.lantaiyuan.rtscheduling.bolt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
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

import com.lantaiyuan.rtscheduling.bean.PassFlow;

public class RTStationTopicByLineBolt extends BaseBasicBolt{
	private FileSystem fs;
	//private List<String> lineIds=null;
	private static Map<String,String> stationMap = new HashMap<String,String>();
	//private Map latMap = new HashMap<String,String>();
	public String CityCode;
	
    private static final Logger LOG = LoggerFactory.getLogger(RTStationTopicByLineBolt.class);
	private static final long serialVersionUID = 2695650418724090897L;

	private OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	
	    }
	
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String citycodelineIddirection=tuple.getString(0);
		PassFlow passflow=(PassFlow)tuple.getValue(1);
		//System.err.println(citycodelineIddirection);
		String citycodelineiddirectionsid=citycodelineIddirection+"@"+passflow.getBus_station_code();
		//按站序发射
		 collector.emit(new Values(citycodelineiddirectionsid,passflow));
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("citycodelineiddirectionsid","passflow"));
	}
	

}
