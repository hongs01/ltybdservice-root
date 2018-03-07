package com.lantaiyuan.rtscheduling.bolt;

import java.util.Map;

import org.apache.storm.shade.org.apache.http.util.TextUtils;
import org.apache.storm.task.IOutputCollector;
import org.apache.storm.task.OutputCollector;
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


public class PassengerFlowRealTimeUpBolt extends BaseBasicBolt{private static final long serialVersionUID = 2695650418724090897L;

public void execute(Tuple tuple, BasicOutputCollector collector) {
	String jsonStr = tuple.getString(0);
	JSONObject json = JSONObject.parseObject(jsonStr);
	JSONObject jsonHeader=(JSONObject) json.get("header");
	JSONObject jsonBody=(JSONObject) json.get("body");

	if("4097".equals(jsonHeader.getString("msg_id"))){
		System.err.println("asdf:"+jsonBody.toString());
		//json转化为bean
		PassFlow passflow = JSONUtil.Json2Bean(jsonBody.toString(), PassFlow.class);
		String citycode_lineid_direction=passflow.getCity_code()+"@"+passflow.getLine_id()+"@"+passflow.getDirection();
		collector.emit(new Values(citycode_lineid_direction,passflow));
   }
}

public void declareOutputFields(OutputFieldsDeclarer declarer) {
	declarer.declare(new Fields("citycode_lineid_direction","passflow"));
}

@Override
public void prepare(Map stormConf, TopologyContext context) {

}

}
