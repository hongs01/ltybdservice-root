package com.ltybdservice.bolt;

import com.ltybdservice.bean.BuspoolingDemandInfo;
import com.ltybdservice.common.GetBusLines;
import com.ltybdservice.common.GetRunConfig;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondBolt extends BaseBasicBolt{
	/**
	 *  @auther hongshuai
	 */
	private static final long serialVersionUID = -2751912457576333554L;
	OutputCollector collector =null;
	
	private  String gaodeKey;
	private  String buspathplanurl;
	private  Logger LOG = LoggerFactory.getLogger(SecondBolt.class);
	
	public SecondBolt(){
		Map<String,String> runConfigMap = new HashMap<String, String>();
		GetRunConfig runConfig = new GetRunConfig();
		runConfigMap = runConfig.getStormConf();
		this.gaodeKey = runConfigMap.get("gaodekey");
		this.buspathplanurl = runConfigMap.get("buspathplanurl");
	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		BuspoolingDemandInfo bdiInfo = (BuspoolingDemandInfo)tuple.getValue(1);
		LOG.error(bdiInfo.getStartPlaceName());
		List  resultList=getBusData(bdiInfo);
		List <List> transitsList =(List <List>) resultList.get(0);
        if(transitsList.toString()!="[]"){
        	List <Map<String,String>> segmentList = transitsList.get(0);

        	if(segmentList.size()<=2 && segmentList.size()>0){
        		Map<String,String> transferMap = segmentList.get(0);
            	String busname=transferMap.get("busname");
            	String busId=transferMap.get("busid");
            	List busLines = (List) resultList.get(1);
            	String realStartName =  busLines.get(0).toString();
            	String beginLocation =  busLines.get(1).toString();
            	String realEndName =  busLines.get(busLines.size()-2).toString();
//            	System.err.println(realEndName);
            	String endLocation =  busLines.get(busLines.size()-1).toString();
            	float beginLat = Float.parseFloat(beginLocation.split(",")[0]); 
            	float beginLon = Float.parseFloat(beginLocation.split(",")[1]);
            	float endLat = Float.parseFloat(endLocation.split(",")[0]); 
            	float endLon = Float.parseFloat(endLocation.split(",")[1]);
            	String distance = (String) resultList.get(3);
            	String duration =  (String) resultList.get(2);
            	bdiInfo.setBusLine(busname);
            	bdiInfo.setRealStartName(realStartName);
            	bdiInfo.setRealStartlon(beginLon);
            	bdiInfo.setRealStartlat(beginLat);
            	bdiInfo.setRealEndName(realEndName);
            	bdiInfo.setRealEndlon(endLon);
            	bdiInfo.setRealEndlat(endLat);
            	bdiInfo.setDistance(distance);
            	bdiInfo.setDuration(duration);
            	bdiInfo.setBusId(busId);
            	LOG.error(busId);
             	collector.emit(new Values(busId,bdiInfo));
        	}
        }
	}
	//请求高德数据
	public List getBusData(BuspoolingDemandInfo bdiInfo){
		//起点和终点经纬度
		float startPlaceLon=bdiInfo.getStartPlaceLon();
		float startPlaceLat=bdiInfo.getStartPlaceLat();
		float endPlaceLon=bdiInfo.getEndPlaceLon();
		float endPlaceLat=bdiInfo.getEndPlaceLat();
		//城市编码
		String cityCode=bdiInfo.getCityCode();
		GetRunConfig runConfig = new GetRunConfig();
		String url= this.buspathplanurl;
//		String url = runConfig.buspathplanurl;
//		String gaodeKey = runConfig.gaodeKey;
		StringBuffer req_url=new StringBuffer();
		req_url.append(url).append("origin=").append(startPlaceLon).append(",").append(startPlaceLat).append("&destination=")
		.append(endPlaceLon).append(",").append(endPlaceLat).append("&city=").append(cityCode).append("&strategy=5&key=").append(gaodeKey);
//		System.out.println(req_url.toString());
		GetBusLines getbusLine = new GetBusLines();
		List lineData=getbusLine.getTransferPlan(req_url.toString());
		return lineData;
	}
	

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector=collector;
		
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("busId","bdiInfo"));
	}

}
