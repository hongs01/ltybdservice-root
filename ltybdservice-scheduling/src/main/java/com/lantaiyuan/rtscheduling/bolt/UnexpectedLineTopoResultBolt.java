package com.lantaiyuan.rtscheduling.bolt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.bean.PassFlow;
import com.lantaiyuan.rtscheduling.common.KafkaUtil;
import com.lantaiyuan.rtscheduling.common.TimeUtil;
import com.lantaiyuan.rtscheduling.config.HdfsConfig;
import com.lantaiyuan.rtscheduling.config.KafkaConfig;
import com.lantaiyuan.rtscheduling.service.HdfsService;

public class UnexpectedLineTopoResultBolt extends WindowedBolt{
	private static final Logger LOG = LoggerFactory.getLogger(UnexpectedLineTopoResultBolt.class);
	private KafkaProducer<String, String> producer;
	private String bootserv;
	Map<String,Integer> lineIddirectiontimeMap=new HashMap<String,Integer>();
	private FileSystem fs;
	private OutputCollector collector;
	
	KafkaUtil kafkautil=null;
	TimeUtil timeutil=null;
/*	public UnexpectedLineTopoResultBolt(){
		Map<String, String> runConfigMap = new HashMap<String, String>();
		GetRunConfig getRunConf = new GetRunConfig();
		Map<String, String> runconfMap = getRunConf.getStormConf();
		bootserv = runconfMap.get("bootstrap.servers");
	}*/

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
	   HdfsConfig hdfsconfig=new HdfsConfig();
		Configuration conf=hdfsconfig.getConfig();
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		/*KafkaConfig kafkaconfig=new KafkaConfig();
		producer=kafkaconfig.getKafkaProducer();*/
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
			
		kafkautil=new KafkaUtil();
		timeutil=new TimeUtil();
		 String workmonth=timeutil.getWorkMonth();
			String workdate=timeutil.getWorkDate();
		HdfsService hdfsservice=new HdfsService();	
		lineIddirectiontimeMap=hdfsservice.getBaseInfo(workmonth,workdate,fs);
	}

	@Override
	public void emitCurrentWindowCounts() {
		
		List<Tuple> windowedTuples = cache.getAndAdvanceWindow();
		if (windowedTuples!= null && windowedTuples.size()!= 0){
			Map<String,Integer> CLDM=  new HashMap<String,Integer>();
			Map<String,List<Long>> lineDirectionTime=new HashMap<String,List<Long>>();
			String cityCode=null;
			String lineDirection=null;
			String lineId=null;
			for (Tuple t : windowedTuples) {
				int sum=0;
				String citycodeLineIdDirectionMin=t.getString(0);
				
				cityCode=citycodeLineIdDirectionMin.split("@")[0];
				lineId=citycodeLineIdDirectionMin.split("@")[1];
				lineDirection=citycodeLineIdDirectionMin.split("@")[2];
				String LineIdDirectionMin=lineId+"@"+lineDirection+"@"+citycodeLineIdDirectionMin.split("@")[3];
				System.err.println("LineIdDirectionMin:"+LineIdDirectionMin);
				
				String LineIdDirection=lineId+"@"+lineDirection;
				PassFlow passflow=(PassFlow)t.getValue(1);
				int tempsum=0;
				tempsum+=CLDM.get(LineIdDirectionMin)==null?0:CLDM.get(LineIdDirectionMin);
				sum=tempsum+passflow.getUp_flow();
				
				if(lineDirectionTime.get(LineIdDirection)==null){
					List<Long> list=new ArrayList<Long>();
					list.add(passflow.getStat_time());
					lineDirectionTime.put(LineIdDirection, list);
				}else{
					List<Long> list=lineDirectionTime.get(LineIdDirection);
					list.add(passflow.getStat_time());
					lineDirectionTime.put(LineIdDirection, list);
				}
			    CLDM.put(LineIdDirectionMin, sum);
			}
			
			
			for(String lineidDirection:lineDirectionTime.keySet()){
				int n1=5;
				int n2=0;
				int	lineType=0;
				int lineStatus=0;
				for(String cldm:CLDM.keySet()){
					if(lineIddirectiontimeMap.containsKey(cldm) && cldm.split("@")[0].equals(lineidDirection.split("@")[0])
							&& cldm.split("@")[1].equals(lineidDirection.split("@")[1])){
						if(CLDM.get(cldm)>=lineIddirectiontimeMap.get(cldm)*(1+0.2)){
							n1++;
						}else if(CLDM.get(cldm)<=lineIddirectiontimeMap.get(cldm)*(1-0.2)){
							n2++;
						}else{
							break;
						}
					}
					
				}
				if(n1==5){
				lineType=0;
				lineStatus=1;
				}else{
					lineStatus=0;
				}
				if(n2==5){
					lineType=1;
					lineStatus=1;
				}else{
					lineStatus=0;
				}
				
				if(n1==5||n2==5){
					List<Long> timelist=lineDirectionTime.get(lineId+"@"+lineDirection);
					String lineStartTime=getStartTime(timelist);
					String lineEndTime = null;
					try {
						lineEndTime = getEndTime(lineStartTime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JSONObject jobj = new JSONObject();
					jobj.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
					jobj.put("lineType", lineType);
					jobj.put("lineStatus", lineStatus);
					jobj.put("cityCode", cityCode);
					jobj.put("lineDirection", lineDirection);
					jobj.put("lineId", lineId);
					jobj.put("lineStartTime", lineStartTime);
					jobj.put("lineEndTime", lineEndTime);
					 kafkautil.pushToKafka("unexpectedLineTopic",producer, jobj);
				}else{
					JSONObject jobj = new JSONObject();
					jobj.put("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
					jobj.put("lineStatus", lineStatus);
					jobj.put("cityCode", cityCode);
					jobj.put("lineDirection", lineDirection);
					jobj.put("lineId", lineId);
					 kafkautil.pushToKafka("unexpectedLineTopic",producer, jobj);
				}
			}
		}
	}


	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}
	
	
	public String getStartTime(List<Long> timelist){
		String lineStartTime=null;
		Collections.sort(timelist);
		Long StartTime=timelist.get(0);
		
		  SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		  lineStartTime = sdf.format(StartTime*1000);  
		return lineStartTime;
	}
	
	//结束时间在开始时间上加5分钟
	public String getEndTime(String lineStartTime) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=sdf.parse(lineStartTime); 
		 Date afterDate = new Date(date.getTime() + 300000);
		 String lineEndTime=sdf.format(afterDate );
		return lineEndTime;
	}

}
