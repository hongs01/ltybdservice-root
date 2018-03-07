package com.lantaiyuan.rtscheduling.bolt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.lantaiyuan.rtscheduling.bean.PassFlow;

public class UnexpectedLineTimeBolt extends BaseBasicBolt{

	
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String citycodeLineIdDirection=tuple.getString(0);
		PassFlow passflow=(PassFlow)tuple.getValue(1);
		String occurTimeMin=getTimeToMin(passflow.getStat_time());
		String citycodeLineIdDirectionMin=citycodeLineIdDirection+"@"+occurTimeMin;
		collector.emit(new Values(citycodeLineIdDirectionMin,passflow));
	}

	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("citycodeLineIdDirectionMin","passflow"));
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	
	}
	
	
	public String getTimeToMin(Long time){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String min = format.format(time).split(" ")[1];
		return min;
	}

}
