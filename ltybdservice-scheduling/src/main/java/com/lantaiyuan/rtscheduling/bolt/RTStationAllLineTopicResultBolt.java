package com.lantaiyuan.rtscheduling.bolt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
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
import com.lantaiyuan.rtscheduling.service.RTStationAllLineTopicService;

public class RTStationAllLineTopicResultBolt extends  WindowedBolt{
	
	private static final Logger LOG = LoggerFactory.getLogger(RTStationAllLineTopicResultBolt.class);
	private static final long serialVersionUID = 2695650418724090897L;
	private KafkaProducer<String, String> producer;

	private OutputCollector collector;
	private FileSystem fs;
	HbaseService hbaseservice;
	private static Map<String,String> LineStationMap = new HashMap<String,String>();
	//预测客流基础数据
	private static Map<String,String> preBaseMap = new HashMap<String,String>();
	TimeUtil timeutil=null;
	RTStationAllLineTopicService rtstationalllinetopicservice=null;
	KafkaUtil kafkautil=null;
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;	
		kafkautil=new KafkaUtil();
		rtstationalllinetopicservice=new RTStationAllLineTopicService();
		timeutil=new TimeUtil();
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
		
		 hbaseservice=new HbaseService();
		hbaseservice.createTable("realStationAllLineTable","realStationAllLineInfo@");
		
		String workmonth=timeutil.getWorkMonth();
		String workdate=timeutil.getWorkDate();
		
		HdfsService hdfsservice=new HdfsService();
		LineStationMap=hdfsservice.getLineStationBaseInfo(workmonth,workdate,fs);
		preBaseMap=hdfsservice.getPrdBaseInfo(workmonth,workdate,fs);
		
	}

	@Override
	public void emitCurrentWindowCounts() {
		//从缓存获取实时客流信息
	    List<Tuple> windowedTuples = cache.getAndAdvanceWindow();
	    if (windowedTuples!= null && windowedTuples.size()!= 0){
	    	Map<String,Integer> stationIdupNum=  new HashMap<String,Integer>();
			Map<String,Integer> stationIddownNum=  new HashMap<String,Integer>();
			for (Tuple t : windowedTuples) {
				String stationId=t.getString(0).split("@")[1];
				PassFlow passflow=(PassFlow)t.getValue(1);
                int upNumber=0;
                int downNumber=0;
                int tempupNumber=0;
                int tempdownNumber=0;
                tempupNumber+=stationIdupNum.get(stationId)==null?0:stationIdupNum.get(stationId);
                tempdownNumber+=stationIddownNum.get(stationId)==null?0:stationIddownNum.get(stationId);
                upNumber=tempupNumber+passflow.getUp_flow();
                downNumber=tempdownNumber+passflow.getDown_flow();
                stationIdupNum.put(stationId, upNumber);
                stationIddownNum.put(stationId, downNumber);
			}
			for(String citycodestationId:stationIdupNum.keySet()){
				//站点ID
				String stationId=citycodestationId;
				 //线路ID
				//String lineId=getLineIdBystationId(stationId);
				//时间
		        String nowtime=timeutil.getCurrentTime();
		        
		        List<Map<String,String>> timelistMap=new ArrayList<Map<String,String>>();
		        try {
					timelistMap=hbaseservice.getDataFromHbase("realStationAllLineTable",stationId);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		      //滞站客流(取前面一辆车的上车人数)
		        int stagnantTraffic =0;
		        if(timelistMap.size()>0){
		        	stagnantTraffic=Integer.parseInt(timelistMap.get(timelistMap.size()-1).get("upNumber"));
		        }
		        //上车人数
		        int upNumber=0;
		        if(stationIdupNum.size()>0){
		        	upNumber=stationIdupNum.get(citycodestationId);
		        }
		        //下车人数
		        int downNumber=0;
		        if(stationIddownNum.size()>0){
		        	downNumber=stationIddownNum.get(citycodestationId);	
		        }
		      //根据站点ID查询线路汇总ID
		        String allLineId=rtstationalllinetopicservice.getAllLineIdByStationId(LineStationMap,stationId);
		        
		        //推送kafka之前先存入hbase
		        try {
		        	hbaseservice.savaStationAllLineToHbase("realStationAllLineTable",stationId, timeutil.formatTime(nowtime),stagnantTraffic, upNumber,downNumber);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		      //从hbase查询结果中获取整点的人数
		        String currentHour= timeutil.formatToHour(nowtime);
			      JSONObject rtjson1=new JSONObject();
			      if(timelistMap.size()>0){
			    	  for(int i=0;i<timelistMap.size();i++){
				        	if(currentHour.equals(timelistMap.get(i).get("time"))){
				        		rtjson1.put("time",currentHour);
				        		rtjson1.put("stagnantTraffic", timelistMap.get(i).get("stagnantTraffic"));
				        		rtjson1.put("upNumber", timelistMap.get(i).get("upNumber"));
				        		rtjson1.put("downNumber", timelistMap.get(i).get("downNumber"));
				        	}else{
				        		rtjson1.put("time",currentHour);
				        		rtjson1.put("stagnantTraffic", 0);
				        		rtjson1.put("upNumber", 0);
				        		rtjson1.put("downNumber", 0);
				        	}
				        }  
			      }
			      
			      JSONObject rtjson2=new JSONObject();
			    	rtjson2.put("time", nowtime);
			        rtjson2.put("stagnantTraffic",stagnantTraffic);
			        rtjson2.put("upNumber",upNumber);
			        rtjson2.put("downNumber",downNumber);
			        
			      //预测客流
			        JSONObject prejson1 = null;
					try {
						prejson1 = rtstationalllinetopicservice.getPredictPassengerFlowForOneHour(preBaseMap,stationId,nowtime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
			        JSONObject prejson2 = null;
					try {
						prejson2 = rtstationalllinetopicservice.getPredictPassengerFlowForTwoHour(preBaseMap,stationId,nowtime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
					
					 JSONArray ja=new JSONArray();
				        ja.add(rtjson1);
				        ja.add(rtjson2);
				        ja.add(prejson1);
				        ja.add(prejson2);
				       
				    	JSONObject jobj=new JSONObject();
						 jobj.put("stationId", stationId);
						 jobj.put("allLineId", allLineId);
						 jobj.put("data", ja);
						//发送到kafka
						 kafkautil.pushToKafka("real_timeStationTopic",producer,jobj);
			}
	    }
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
