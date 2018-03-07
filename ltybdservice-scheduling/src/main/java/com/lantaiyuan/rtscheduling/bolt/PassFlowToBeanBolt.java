package com.lantaiyuan.rtscheduling.bolt;

import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.bean.PassFlow;
import com.lantaiyuan.rtscheduling.common.JSONUtil;

/**
 * @author hongshuai
 *将客流数据从json格式转化为bean
 *并按城市编码和线路Id进行分组下发
 */
public class PassFlowToBeanBolt  extends BaseBasicBolt{
	
	private static final long serialVersionUID = 2695650418724090897L;
	
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String jsonStr = tuple.getString(0);
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject jsonHeader=(JSONObject) json.get("header");
		JSONObject jsonBody=(JSONObject) json.get("body");
	
		if("4097".equals(jsonHeader.getString("msg_id"))){
			//System.err.println(jsonBody.toString());
			//json转化为bean
			PassFlow passflow = JSONUtil.Json2Bean(jsonBody.toString(), PassFlow.class);
			String citycode_lineid=passflow.getCity_code()+"@"+passflow.getLine_id();
			collector.emit(new Values(citycode_lineid,passflow));
	   }
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("citycode_lineid","passflow"));
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	
	}


}
