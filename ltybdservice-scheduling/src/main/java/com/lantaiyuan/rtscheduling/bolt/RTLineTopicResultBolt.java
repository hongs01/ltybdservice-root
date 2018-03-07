package com.lantaiyuan.rtscheduling.bolt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.hadoop.fs.FileSystem;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.hadoop.conf.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.bean.PassFlow;
import com.lantaiyuan.rtscheduling.common.KafkaUtil;
import com.lantaiyuan.rtscheduling.common.TimeUtil;
import com.lantaiyuan.rtscheduling.config.HdfsConfig;
import com.lantaiyuan.rtscheduling.config.KafkaConfig;
import com.lantaiyuan.rtscheduling.service.HbaseService;
import com.lantaiyuan.rtscheduling.service.HdfsService;
import com.lantaiyuan.rtscheduling.service.RtLineTopicService;


public class RTLineTopicResultBolt extends WindowedBolt{
	
	private static final Logger LOG = LoggerFactory.getLogger(RTLineTopicResultBolt.class);
	private static final long serialVersionUID = 1L;
	
	private FileSystem fs;
	private KafkaProducer<String, String> producer;
	private Map<String,Integer>   lineNumMap = new HashMap<String,Integer>();
	private Map<String,String> 	lineNameMap =new HashMap<String,String>();
	
	KafkaConfig kafkaconfig;
	HbaseService hbaseservice;
	TimeUtil timeutil=null;
	RtLineTopicService rtlinetopicservice=null;
	KafkaUtil kafkautil=null;
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		kafkautil=new KafkaUtil();
		rtlinetopicservice=new RtLineTopicService();
		timeutil=new TimeUtil();
		HdfsConfig hdfsconfig=new HdfsConfig();
		Configuration conf=hdfsconfig.getConfig();
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		HdfsService hdfsservice=new HdfsService();
		//KafkaConfig kafkaconfig=new KafkaConfig();
		 hbaseservice=new HbaseService();
		//producer=kafkaconfig.getKafkaProducer();
		
		Properties props = new Properties();
	      props.put("bootstrap.servers", "202.104.136.228:9092");
	      props.put("acks", "all");
	      props.put("retries", 0);
	      props.put("batch.size", 16384);
	      props.put("linger.ms", 1);
	      props.put("buffer.memory", 33554432);
	      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	     producer = new KafkaProducer<String, String>(props);
		
		//hbase建表
		hbaseservice.createTable("realTable","realInfo@");
		String month=timeutil.getWorkMonth();
		String workdate=timeutil.getWorkDate();
	
		//查询预测客流基础数据
		lineNumMap=hdfsservice.getPredictPassengerFlowBaseInfo(workdate,fs);
		//获取线路基础数据
		lineNameMap=hdfsservice.getLineBaseData(month,workdate,fs);
	}

	@Override
	public void emitCurrentWindowCounts() {
		List<Tuple> windowedTuples = cache.getAndAdvanceWindow();
		if (windowedTuples!= null && windowedTuples.size()!= 0){
			Map<String,Integer> lineId=  new HashMap<String,Integer>();
			for (Tuple t : windowedTuples) {
				int sum=0;
				String citycodelineid=t.getString(0);
				PassFlow passflow=(PassFlow)t.getValue(1);
				int tempsum=0;
				tempsum+=lineId.get(citycodelineid)==null?0:lineId.get(citycodelineid);
				sum=tempsum+passflow.getUp_flow();
				System.out.println("citycodelineid:"+citycodelineid+"=============>sum"+lineId);
				lineId.put(citycodelineid, sum);
			}
			for(String cdlineid:lineId.keySet()){
				  //唯一标示号
				String uuid=UUID.randomUUID().toString().replaceAll("-", "");
				//线路ID
				String routeId = cdlineid.split("@")[1];
		        //实时客流
				int  real_timePassengerFlow=0;
				if(lineId.size()>0){
					real_timePassengerFlow=lineId.get(cdlineid);
				}
				 //城市id
				String cityCode=cdlineid.split("@")[0];
		        //时间
		        String nowHour=timeutil.format(timeutil.getCurrentHour());
		        //预测客流
		        JSONArray predictPassengerFlow=rtlinetopicservice.getPredictPassengerFlow(lineNumMap,routeId);
			
		        try {
					hbaseservice.savaToHbase("realTable", routeId, nowHour, real_timePassengerFlow);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //取hbase数据并封装
		     Map<String,Integer> RTNummap= null;
		      try {
		    	  RTNummap=rtlinetopicservice.getHbaseData("realTable",routeId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		      
		      JSONArray ja=new JSONArray();
		      List<String> listNowTime=new ArrayList<String>();
		      if(RTNummap!=null){
		    	     for(String nowtime:RTNummap.keySet()){
		    	    	 listNowTime.add(nowtime);
		    	    	 Collections.sort(listNowTime);
				      } 
		    	     for(int i=0;i<listNowTime.size();i++){
		    	    	 JSONObject job=new JSONObject();
		    	    	 job.put("nowtime",listNowTime.get(i));
				    	  job.put("real_timePassengerFlow", RTNummap.get(listNowTime.get(i)));
				    	  ja.add(job);
		    	     }
		      }else{
		    	  JSONObject job=new JSONObject();
		    	  job.put("nowtime", "");
		    	  job.put("real_timePassengerFlow", "");
		    	  ja.add(job);
		      }
		 
		      LOG.info("====> put data to JSON");
		        JSONObject  jobj=new JSONObject();
		        jobj.put("uuid", uuid);
		        jobj.put("lineId", routeId);
		        jobj.put("cityCode", cityCode);
		        jobj.put("lineName", rtlinetopicservice.getLineNameByLineId(lineNameMap,routeId));
		       // jobj.put("lineDirection", lineDirection);
		        //jobj.put("nowtime", nowtime);
		        jobj.put("predictPassengerFlow", predictPassengerFlow);
		        jobj.put("real_timePassengerFlow", ja);
		        
		        kafkautil.pushToKafka("real_timeLineTopic",producer, jobj);
			}
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
