package com.ltybdservice.bolt;

import com.ltybdservice.bean.BuspoolingDemandInfo;
import com.ltybdservice.common.JSONUtil;
import org.apache.storm.shade.org.apache.http.util.TextUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;


public class FirstBolt extends BaseRichBolt{
	/**
	 * @auther hongshuai,zhouzefei
	 */
	private static final long serialVersionUID = 2695650418724090897L;
//	OutputCollector collector =null;
	private OutputCollector collector;  
	//做一个简单的判断是否是json
    public static Boolean getJSONType(String str){  
        if(TextUtils.isEmpty(str)){  
            return false;  
        }  
  
        final char[] strChar = str.substring(0, 1).toCharArray();  
        final char firstChar = strChar[0];    
        if(firstChar == '{'){  
            return true;  
        }else if(firstChar == '['){  
            return true;  
        }else{  
            return false;  
        }  
    } 
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("citycode","cdinfo"));

	}
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		this. collector = collector;  
	}

	public void execute(Tuple tuple) {
		// TODO Auto-generated method stub
		String jsonStr = tuple.getString(0);
//		System.err.println(jsonStr);
		if (getJSONType(jsonStr)) {
			BuspoolingDemandInfo bdinfo = JSONUtil.Json2Bean(jsonStr,
					BuspoolingDemandInfo.class);
			// 根据Citycode发射到下一个bolt
			System.err.println(bdinfo.getOrderNo());
			if (bdinfo.getCityCode() != null) {
				collector.emit(new Values(bdinfo.getCityCode(), bdinfo));
				collector.ack(tuple);
			}
		}
	}

}
