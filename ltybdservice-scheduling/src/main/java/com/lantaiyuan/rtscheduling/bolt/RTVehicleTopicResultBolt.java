package com.lantaiyuan.rtscheduling.bolt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
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
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
/*import org.json.simple.JSONObject;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.bean.GPS;
import com.lantaiyuan.rtscheduling.bean.PassFlow;
import com.lantaiyuan.rtscheduling.bean.StationObject;
import com.lantaiyuan.rtscheduling.common.CommonUtil;
import com.lantaiyuan.rtscheduling.common.KafkaUtil;
import com.lantaiyuan.rtscheduling.common.TimeUtil;
import com.lantaiyuan.rtscheduling.config.HdfsConfig;
import com.lantaiyuan.rtscheduling.config.KafkaConfig;
import com.lantaiyuan.rtscheduling.service.HdfsService;

public class RTVehicleTopicResultBolt extends BaseBasicBolt{
	private KafkaProducer<String, String> producer;
	private FileSystem fs;
	//查基础数据map
	private Map<String,String> gprsIdMap = new HashMap<String,String>();
	//保存车辆终端号和车辆编号map
	private Map<Integer,String> carMap = new HashMap<Integer,String>();
	//保存车辆编号和载客人数map
	private Map<String,Integer> ZkrsMap = new HashMap<String,Integer>();
	//站点基础数据    所有站序存list
	static Map<String,Double> StationBaseData=new HashMap<String,Double>();
	static Map<String,String> StationBaseData1=new HashMap<String,String>();
	//趟次编号trip
	private Map<String,List<Integer>> tripMap = new HashMap<String,List<Integer>>();
    //站点基础数据   距起点站距离存map
	//Map<Integer,Double> beginDis=new HashMap<Integer,Double>();
	//车辆到站预测准点(按站序排序)
	List<JSONObject> stationObjs=new ArrayList<JSONObject>();
	
	
	//线路ID和station的map
	Map<String,String> lineStationMap=new HashMap<String,String>();
	//车辆到站预测准点
	Map<String,List<StationObject>> StationObjsData=new HashMap<String,List<StationObject>>();
	Map<String,String> StationObjsMap=new HashMap<String,String>();
	
	public String CityCode;
	
	
    private static final Logger LOG = LoggerFactory.getLogger(RTVehicleTopicResultBolt.class);
	private static final long serialVersionUID = 2695650418724090897L;
	
	static private List<StationObject> stationObjects=new ArrayList<StationObject>();

	//缓存车辆最新gps信息
	static Map<Integer,GPS> gpsMap=new HashMap<Integer, GPS>();
	//站点ID和站点名称的map
	Map<String,String> StationMap=new HashMap<String,String>();
	
	KafkaUtil kafkautil=null;
	
	TimeUtil timeutil=null;
    //按线路ID,线路方向区分车辆Map
   static	Map<String,Map<Integer,GPS>>  citycode_lineId_direction_Map=new HashMap<String,Map<Integer,GPS>>();
		@Override
	    public void prepare(Map stormConf, TopologyContext context) {
			HdfsConfig hdfsconfig=new HdfsConfig();
			Configuration conf=hdfsconfig.getConfig();
			try {
				fs = FileSystem.get(conf);
			} catch (IOException e) {
				e.printStackTrace();
			}   
			
			kafkautil=new KafkaUtil();
			timeutil=new TimeUtil();
			//KafkaConfig kafkaconfig=new KafkaConfig();
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
			
		        String workmonth=timeutil.getWorkMonth();
				String workdate=timeutil.getWorkDate();
		       
		       
		       HdfsService hdfsservice=new HdfsService();
			//查询站点和站名基础信息(ods_op_stationblock);
			StationMap=	hdfsservice.getStationIDStationNameBaseInfo(workmonth,workdate,fs);
			//查询车辆准点到站
			StationObjsData=hdfsservice.getStationObjsData(workmonth,workdate,fs);
			//查询站点基础信息
			StationBaseData=hdfsservice.getStationBaseInfo(workmonth,workdate,fs);
			StationBaseData1=hdfsservice.getStationBaseInfo1(workmonth,workdate,fs);
		
			//查询线路基础信息
			gprsIdMap=hdfsservice.getLineBaseInfo(workmonth,workdate,fs);
			//查询车辆基础信息
			carMap=hdfsservice.getCarBaseInfo(workmonth,workdate,fs);
			//查询车辆载客人数基础信息
			ZkrsMap=hdfsservice.getZkrsBaseInfo(workmonth,workdate,fs);
	    }
		
		public void execute(Tuple tuple, BasicOutputCollector collector) {
			
			String bolttype=tuple.getSourceComponent();
			System.err.println(bolttype+"=============");
			String citycode_lineId_direction_p=null;
			String citycode_lineId_direction_g=null;
			
			
			if(bolttype.equals("Bolt_gpsUp")){
				citycode_lineId_direction_g=tuple.getString(0);
				GPS	gps=(GPS) tuple.getValue(1);
				if(citycode_lineId_direction_Map.get(citycode_lineId_direction_g)==null){
					Map<Integer,GPS> map=new HashMap<Integer,GPS>();
					map.put(gps.getDev_id(), gps);
					citycode_lineId_direction_Map.put(citycode_lineId_direction_g, map);
				}else if(citycode_lineId_direction_Map.get(citycode_lineId_direction_g)!=null){
					Map<Integer,GPS> map=citycode_lineId_direction_Map.get(citycode_lineId_direction_g);
					map.put(gps.getDev_id(), gps);
					citycode_lineId_direction_Map.put(citycode_lineId_direction_g,map);
				} 
				
			//	gpsMap =citycode_lineId_direction_Map.get(citycode_lineId_direction_g);

			//	gpsMap.put(gpsup.getTerminalNo(), gpsup);
				//对gpsMap进行排序，放入一个新的sortGpsMap中
			}else if(bolttype.equals("Bolt_passengerFlowRealTimeUp") ){
				citycode_lineId_direction_p=tuple.getString(0);
			    PassFlow	passflow=(PassFlow) tuple.getValue(1);
				gpsMap =citycode_lineId_direction_Map.get(citycode_lineId_direction_p);
				if(gpsMap==null){
					return;
				}
				//按下一站站序排序后的gpsList
				List<GPS> gpsList=new ArrayList<GPS>();
				sortGpsList(gpsList);
				
			    Integer lineId=passflow.getLine_id();
			    Integer lineDirection=passflow.getDirection();
			    //站点序号
			    int bus_station_no=passflow.getBus_station_no();
			  
			  //取站点基础数据表 list存放到起点距离
				List<Double> listDistance=new ArrayList<Double>();
				if(StationBaseData.size()>0){
					 for(String liniddirectionorder:StationBaseData.keySet()){
					    	if(liniddirectionorder.split("@")[0].equals(lineId.toString()) && liniddirectionorder.split("@")[1].equals(lineDirection.toString())){
					    		listDistance.add(StationBaseData.get(liniddirectionorder));
					    	}
					    }	
				}
			   
			    Collections.sort(listDistance);
			   
				//取站点基础数据表 list存放站序
				List<Integer> orderNolist=new ArrayList<Integer>();
				if(StationBaseData1.size()>0){
					 for(String liniddirectionorder:StationBaseData1.keySet()){
					    	if(liniddirectionorder.split("@")[0].equals(lineId.toString())&&liniddirectionorder.split("@")[1].equals(lineDirection.toString())){
					    		orderNolist.add(Integer.parseInt(StationBaseData1.get(liniddirectionorder)));
					    	}
					    }	
				}
			   
			    Collections.sort(orderNolist); 
			    
			    //如果车辆到达最后一站，把它从内存中剔除掉
			    if(bus_station_no==orderNolist.get(orderNolist.size()-1)){
			    	gpsMap.remove(passflow.getDev_id());
			    	citycode_lineId_direction_Map.put(citycode_lineId_direction_p, gpsMap);
			    }
			    
				//站点基础数据  所有的站间距离
				Map<Integer,Double>  stationDistance =new HashMap<Integer,Double>();
				//站间距离
				stationDistance=getStationDistance(listDistance);
				
				GPS gps=null;
				if(gpsMap.size()>0){
					if(gpsMap.containsKey(passflow.getDev_id())){
						 gps=gpsMap.get(passflow.getDev_id());
					}
				}
				
			    if(gps==null){
			    	return;
			    }
	
				if(gps!=null){
				//当前车辆的下一站站序
				int nextstationNo=gps.getNext_station_no();
				//当前车辆的终端号
				int devid=gps.getDev_id();
			
				int n=0;
				int k=0;
				for(int i=0;i<gpsList.size();i++){
					if(nextstationNo==gpsList.get(i).getNext_station_no()){
						n++;
					}
				}
				int predistance=0;
				int aftdistance=0;
				//所有的车都分属不同的站台
				if(n==1){
					for(int j=0;j<gpsList.size();j++){
						if(gpsList.get(j).getNext_station_no()==nextstationNo){
						    k=j; 	
						}
					}
					//前辆车的下一站序为      当前车辆的下一站序为nextstationNo
					int lsno=0;
					if(k==gpsList.size()-1){
						return;
					}else{
						 lsno=gpsList.get(k+1).getNext_station_no();
					}
					
					//后辆车的下一站序为   
					int nsno=0;
					if(k==0){
						return;
					}else{
						 nsno=gpsList.get(k-1).getNext_station_no();
					}
					
					int s1=0;
					int s2=0;
					for(int ii=nextstationNo;ii<=lsno;ii++){
						s1+=stationDistance.get(ii);
					}
					for(int jj=nsno;jj<=nextstationNo;jj++){
						s2+=stationDistance.get(jj);
					}
					 
					predistance=(int) (gpsList.get(k).getDis_next()+s1-gpsList.get(k+1).getDis_next());
					aftdistance=(int) (gpsList.get(k-1).getDis_next()+s2-gpsList.get(k).getDis_next());
				}else{
					/*if(n>=2){
						int n1=0;
						int l1=0;
						//前面一站序下标
						for(int j=0;j<gpsList.size();j++){
							if(gpsList.get(j).getNext_station_no()>nextstationNo){
								n1=j;
								break;
							}
						}
						//后面一站序下标
						for(int j=0;j<gpsList.size();j++){
							if(gpsList.get(j).getNext_station_no()<nextstationNo){
								l1=j;
							}
						}
						List<GPS> newlist=new ArrayList<GPS>();
						for(int j=0;j<gpsList.size();j++){
							if(gpsList.get(j).getNext_station_no()==nextstationNo){
								newlist.add(gpsList.get(j));	
							}
						}
						//按到下一站的距离排序  升序排序
						 CommonUtil.sort(newlist, "dis_next", true);
						 for(int i=0;i<newlist.size();i++){
							 System.err.println("newlistasdf:"+newlist.get(i).getDis_next());
						 }
						 
						 
						 int nn=0;
						 for(int j=0;j<newlist.size();j++){
							 if(newlist.get(j).getDev_id()==devid){
								 if(j==0){
									 int s1=0;
									 int nextNo=gpsList.get(n1).getNext_station_no();
									 for(int ii=nextstationNo;ii<=nextNo;ii++){
										 System.err.println("ii"+ii+"ss:"+stationDistance.get(ii-1)+stationDistance);
										s1+=stationDistance.get(ii);
										}
									 predistance=(int) (newlist.get(j).getDis_next()+s1-gpsList.get(n1).getDis_next());
									  aftdistance=(int) (newlist.get(j+1).getDis_next()-newlist.get(j).getDis_next());
								 }else if(j==newlist.size()-1){
									 int s2=0;
									 int lastNo=gpsList.get(l1).getNext_station_no();
									 for(int jj=lastNo;jj<=nextstationNo;jj++){
										 System.err.println("jj"+jj+"SS:"+stationDistance.get(jj-1)+"sdfg:"+stationDistance.get(0));
									//	 System.out.println("SS:"+stationDistance.get(jj));
									   s2+=stationDistance.get(jj); 
									 }
									 aftdistance= (int) (gpsList.get(l1).getDis_next()+s2-newlist.get(j).getDis_next());
									 predistance=(int)(newlist.get(j).getDis_next()-newlist.get(j-1).getDis_next());
								 } else{
									 predistance=(int) (newlist.get(j).getDis_next()-newlist.get(j-1).getDis_next());
									 aftdistance=(int) (newlist.get(j+1).getDis_next()-newlist.get(j).getDis_next());
								 }
							 }
						
						 }
					}*/
					
					predistance=0;
					predistance=0;
				} 
					
				
				int MaxOrderNo=getMaxOrderNo(orderNolist);
				
				String uuid=UUID.randomUUID().toString().replaceAll("-", "");
				String citycode=citycode_lineId_direction_p.split("@")[0];
				
				Long nowtime=passflow.getStat_time();
				double longitude =gps.getLon();
				double latitude =gps.getLat();
				
				//查询车辆基础信息
				String carId=getCarIdByOnBoardId(devid);
				int planId=5;
				int passengersNum=passflow.getTotal_flow();
				//从车辆基础信息中查询载客人数
				int Zkrs =getZkrsByCarId(carId);
				float full_loadratio=passengersNum/(Zkrs+1);  
				
				//将距离下一站的到站时间作为乘客等待时间   (s)
				int toNextStopTime=gps.getTime_next();
				int satisfaction=calSfByODoConf(toNextStopTime,full_loadratio);
				float frontDistance=(predistance<0?0:predistance);
				float behindDistance=(aftdistance<0?0:aftdistance);
				//预测回场时间
				String predictedBackCompanyTime=getPredictedBackCompanyTime(MaxOrderNo,gps,listDistance);
				//回场时间基础上加5分钟
				String nextDepartureTime=getNextDepartureTime(predictedBackCompanyTime,5);
		
				float busSpeed=gps.getVec1();
				
				JSONObject json=new JSONObject();
				
				json.put("uuid", uuid);
				json.put("cityCode", citycode);
				json.put("lineId", lineId);
				json.put("lineDirection", lineDirection);
				json.put("nowtime", nowtime);
				json.put("longitude",longitude);
				json.put("latitude",latitude);
				json.put("carId",carId);
				json.put("planId",planId);
				json.put("passengersNum", passengersNum);
				json.put("full_loadratio", full_loadratio);
				json.put("satisfaction",satisfaction);
	            
				json.put("frontDistance", frontDistance);
				json.put("behindDistance", behindDistance);
				json.put("predictedBackCompanyTime", predictedBackCompanyTime.split(" ")[1]);
				json.put("nextDepartureTime", nextDepartureTime.split(" ")[1]);

				json.put("busSpeed", busSpeed);
			
				
				//Integer time_next = gps.getTime_next()*1000;
				//long t1=System.currentTimeMillis();
				long t2 = gps.getGps_time()*1000 + gps.getTime_next()*1000;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String realTime = simpleDateFormat.format(t2);
				
				json.put("real_Time", realTime.split(" ")[1]);
				
				//存储趟次编号
				int tripId=1;
				String trip_key = lineId+lineDirection+"";
				//如果map不存在说明是第一趟
				if(tripMap.get(trip_key) == null){
					List<Integer> devList = new ArrayList<Integer>();
					devList.add(gps.getDev_id());
					tripMap.put(trip_key, devList);
				}else{ //如果map存在
					List<Integer> devList = tripMap.get(trip_key);
					//先判断List里是否有该车辆，如果有直接取序号
					if(devList.toString().contains(gps.getDev_id().toString())){
						for(int i=0;i<devList.size();i++){
							if(gps.getDev_id() == devList.get(i)){
								tripId = i + 1 ;
								break;
							}
						}
					}else{//如果没有，添加该车辆，那么list.size()+1 就是序号
						devList.add(gps.getDev_id());
						tripId = devList.size() + 1;
					}
				}
				
				String lineiddirectiontripid=lineId+"@"+lineDirection+"@"+tripId;
				try {
					initstationObjs(lineiddirectiontripid,gps,realTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				json.put("stationObjsData", stationObjs);
				System.err.println("++++++++++++++++++++++++++++:"+json.toString());
				kafkautil.pushToKafka("real_timeVehicleTopic",producer,json);
				}
			}
				//collector.emit(new Values(citycodegprsId_p,passengerflowrealtimeup,gpsup));
		}
		
		
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			
		}
		//将list排序  按stationNo升序排序
		public void sortGpsList(List<GPS> gpsList){
			for(int dev_id:gpsMap.keySet()){
				gpsList.add(gpsMap.get(dev_id));
				}
			System.err.println("gpsMap:"+gpsMap);
			CommonUtil.sort(gpsList, "next_station_no", false);
		}
		
		//根据list获取站间距离
		public Map<Integer,Double> getStationDistance(List<Double> listDistance){
			Map<Integer,Double>  staDistance=new HashMap<Integer,Double>();
			if(listDistance.size()>0){
				for(int i=0;i<listDistance.size();i++){
					if(i==0){
						staDistance.put(i+1,listDistance.get(i));
					}else{
					    staDistance.put(i+1, listDistance.get(i)-listDistance.get(i-1));
					}
				}	
			}
			return staDistance;
		}
		
		public int getMaxOrderNo(List<Integer> orderNolist){
            int  maxorderno=0;
            Collections.sort(orderNolist);
            if(orderNolist.size()>0){
            	 maxorderno=orderNolist.get(orderNolist.size()-1); 	
            }
			return maxorderno;
		}
		
		public String format(String time,long min){
			 SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date date = new Date();
			 String s=date.toLocaleString().split(" ")[0]+" "+time;
		        Date dd = null;
				try {
					dd = simpleDateFormat .parse(s);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        long timeStemp = 0;
				try {
					timeStemp = dd.getTime();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        long retmin=min+timeStemp ;
		       return simpleDateFormat.format(retmin);
		}
		public long timedif(long time,String mayarrtime){
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			//String d.toLocaleString().split(" ")[0]
		     
		        		String s=date.toLocaleString().split(" ")[0]+" "+mayarrtime;
		        		Date dd = null;
						try {
							dd = simpleDateFormat .parse(s);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		   long timeStemp =dd.getTime();
		        		   
		       long min=timeStemp-System.currentTimeMillis() ;
		       System.err.println("min:"+min);
			return min;
		}
		//转化预测到达下一站的时间戳
	/*	public long predictTimeToNextStop(int toNextStopTime){
			long predictTime=toNextStopTime*1000;
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//return  sdf.format(predictTime) ;
			return predictTime;
		}*/
		//计算预测回场时间
	public String getPredictedBackCompanyTime(int maxorderNo,GPS gps,List<Double> listDistance){
		//全程路程 (km)
		
		System.err.println("listDistance:"+listDistance+","+"maxorderNo:"+maxorderNo+","+gps.getNext_station_no());
		double totalDistance=listDistance.get(maxorderNo-1);
		
		//下一站序距离起点距离   (km)
		double maxorderNoToBeginDistance=listDistance.get(gps.getNext_station_no()-1);
		//下一站点距离终点站的距离  (km)
		double lastDistance=totalDistance-maxorderNoToBeginDistance;
		//下一站点距离终点站的时间  (s)
		int lasttime=(int) (lastDistance/(gps.getVec1()))*3600;
		//当前车辆距离下一站的时间  (s)
		int timeToNextStation=gps.getTime_next();
		//当前车辆距离终点站的时间 (s)
		int predictedbackcompanytime=timeToNextStation+lasttime;
		//在发生时间上加上预测时间
		Date afterDate = new Date(gps.getGps_time() +predictedbackcompanytime*1000);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return sdf.format(afterDate) ; 
	}
	
	//获取下次发车时间
	public String getNextDepartureTime(String predictedBackCompanyTime,int min){
		 SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = null;
			try {
				date = simpleDateFormat .parse(predictedBackCompanyTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        long timeStemp = date.getTime();
	        Date afterDate = new Date(timeStemp + min*60*1000);
	        String NextDepartureTime=simpleDateFormat.format(afterDate);
		return NextDepartureTime;
	}
		
		public static String getCurrentTime()
		{
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		return time;
		}
		
		//根据设备编号查找车辆编号
		public String getCarIdByOnBoardId(int onboardid){
			String carnum=null;
			if(carMap.size()>0){
				if(carMap.keySet().contains(onboardid)){
					carnum= carMap.get(onboardid);
				}	
			}
			System.err.println("carnum:"+carnum);
			return carnum;	
		}
		
		//根据车辆编号查号载客人数
		public int getZkrsByCarId(String carId){
			int Zkrs=0;
			if(ZkrsMap.size()>0){
				if(ZkrsMap.keySet().contains(carId)){
					Zkrs= ZkrsMap.get(carId);
				}	
			}
			System.err.println("Zkrs:"+Zkrs);
			return Zkrs;	
		}
		
		//读取ODOO配置文件计算乘客满意度加权值  
		public int calSfByODoConf(int waitTime,float full_loadratio){
			//乘客满意度加权值
			int satisfaction=0;
			//候车时间
			int wtime= (int) Math.ceil(waitTime/60);
			//满载率(%)
			int fullloadratio=(int) Math.ceil(full_loadratio*100);
			//候车满意度
			int waitingsfscore=0;
			//乘车舒适度
			int ridecomfortscore=0;
			//高峰   //平峰 //低峰
			if(false){
			    if(wtime>=1 && wtime<=5){
			    	waitingsfscore=80;
			    } else if(waitingsfscore>=6 && waitingsfscore<=15){
			    	waitingsfscore=60;
			    }else{
			    	waitingsfscore=40;
			    }
			    if(fullloadratio>=1 && fullloadratio<=50){
			    	ridecomfortscore=80;
			    }else if(fullloadratio>=51 && fullloadratio<=80){
			    	ridecomfortscore=60;
			    }else{
			    	ridecomfortscore=40;
			    }
			    //计算高峰时期下的乘客满意度加权值
			    satisfaction=(80*waitingsfscore+20*ridecomfortscore)/100;
			}else if(true){
				 if(wtime>=1 && wtime<=10){
					 waitingsfscore=80;
				    } else if(waitingsfscore>=11 && waitingsfscore<=30){
				    	waitingsfscore=60;
				    }else{
				    	waitingsfscore=40;
				    }
				 if(fullloadratio>=1 && fullloadratio<=10){
				    	ridecomfortscore=80;
				    }else if(fullloadratio>=11 && fullloadratio<=30){
				    	ridecomfortscore=60;
				    }else{
				    	ridecomfortscore=40;
				    }
				 //计算平峰时期下的乘客满意度加权值
				 satisfaction=(60*waitingsfscore+40*ridecomfortscore)/100;
			}else if(false){
				 if(wtime>=1 && wtime<=15){
					 waitingsfscore=80;
				    } else if(waitingsfscore>=16 && waitingsfscore<=30){
				    	waitingsfscore=60;
				    }else{
				    	waitingsfscore=40;
				    }
				 if(fullloadratio>=1 && fullloadratio<=15){
				    	ridecomfortscore=80;
				    }else if(fullloadratio>=16 && fullloadratio<=30){
				    	ridecomfortscore=60;
				    }else{
				    	ridecomfortscore=40;
				    }
				//计算低峰时期下的乘客满意度加权值
				 satisfaction=(60*waitingsfscore+40*ridecomfortscore)/100;
			}
			return satisfaction;
		}
		
		public void initstationObjs(String lineiddirectiontripid,GPS gps,String realtime) throws ParseException{
			
			List<StationObject> list=StationObjsData.get(lineiddirectiontripid);
			System.err.println("lineiddirectiontripid:"+lineiddirectiontripid);
			
			long time_dif = 0;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String nowdate = df.format(new Date());
			stationObjs.clear();
			boolean flag=false;
			String predictedTime="";
            if(list!=null){
            	for(int i=0;i<list.size();i++){
    				StationObject sObj = list.get(i);
    				if(gps.getNext_station_no() == Integer.parseInt(sObj.getStationNo())){
    					System.err.println("gps.getNext_station_no():"+gps.getNext_station_no());
    					flag=true;
    					/*Integer time_next = gps.getTime_next()*1000;
    					long t1=System.currentTimeMillis();
    					long t2 = gps.getGps_time()*1000 + gps.getTime_next()*1000;
    					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    					Date date = simpleDateFormat.parse(gps.getGps_time());
    					String realtime = simpleDateFormat.format(t2);*/
    					sObj.setRealTime(realtime.substring(11, 19));
    					predictedTime=realtime.substring(11, 19);
    					sObj.setPredictedTime(realtime.substring(11, 19));
//    			        long t2 = date.getTime();
//    			        long time_next_act = t2 - t1;
//    			        time_dif = time_next_act - time_next;
    					
    					/*String rtime=sObj.getRealTime();
    					List<StationObject> listtemp=new ArrayList<StationObject>();
    					for(int j=i;j<list.size();j++){
    						StationObject stationObject	=list.get(j);
    						stationObject.setPredictedTime(rtime);
    						listtemp.add(stationObject);
    					}
    					List<StationObject> newlist=new ArrayList<StationObject>();
    					for(int k=0;k<list.size();k++){
    						if(k<i){
    							newlist.add(list.get(k));
    						}else{
    							newlist.add(listtemp.get(k-i));
    						}
    					}
    					list.remove(i);
    					list.add(i, sObj);
    					StationObjsData.put(lineiddirectiontripid, newlist);*/
    				}else{
    					if(flag){
    						sObj.setPredictedTime(predictedTime);
    					}
    				}
    				JSONObject stationObj=new JSONObject();
    				stationObj.put("stationNo",sObj.getStationNo());
    				//stationObj.put("stationId",StationMap.get(sObj.getStationId()));
    				stationObj.put("stationId",getStationNameByStationId(sObj.getStationId())+"("+sObj.getPlanningTime().substring(0, 6)+")");
    				stationObj.put("planningTime",0);
    				stationObj.put("predictedTime",getMinToplan(sObj.getPredictedTime(),sObj.getPlanningTime()));
    				stationObj.put("realTime",getMinToplan(sObj.getRealTime(),sObj.getPlanningTime()));
    				stationObjs.add(stationObj);
    			}
            }
			
		}
		
		
		private String getMinToplan(String time,String planTime){
			String min=null;
			if(time.equals("")){
				min="";
			}else{
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
		     
		        		String s1=date.toLocaleString().split(" ")[0]+" "+time;
		        		String s2=date.toLocaleString().split(" ")[0]+" "+planTime;
		        		Date ds1 = null;
		        		Date ds2 = null;
						try {
							ds1 = simpleDateFormat .parse(s1);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							ds2 = simpleDateFormat .parse(s2);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		   long timeStemp1 =ds1.getTime();
		        		   long timeStemp2 =ds2.getTime();
		        		   
		        min=String.valueOf((timeStemp1-timeStemp2)/(1000*60)) ;
			}
			return  min;
		}
		
		public String getStationNameByStationId(String stationId){
			String stationame=null;
			System.err.println("StationMap:"+StationMap+"_____:"+stationId);
				if(StationMap.containsKey(stationId)){
				stationame=StationMap.get(stationId);
			}
		//	stationame=StationMap.get(stationId);
			return stationame;
		}
}
