package com.lantaiyuan.rtscheduling.bolt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.lantaiyuan.rtscheduling.bean.OldGPS;
import com.lantaiyuan.rtscheduling.common.JSONUtil;
import com.lantaiyuan.rtscheduling.common.TimeUtil;
import com.lantaiyuan.rtscheduling.config.HdfsConfig;
import com.lantaiyuan.rtscheduling.service.HdfsService;

public class OldGpsUpBolt extends BaseBasicBolt{
	//查基础数据map
	private Map<String,String> gprsIdMap = new HashMap<String,String>();
	private FileSystem fs;	
	
	@Override
    public void prepare(Map stormConf, TopologyContext context) {
		HdfsConfig hdfsconfig=new HdfsConfig();
		Configuration conf=hdfsconfig.getConfig();
		HdfsService hdfsservice=new HdfsService();
		TimeUtil timeutil=new TimeUtil();
		String workmonth=timeutil.getWorkMonth();
		String workdate=timeutil.getWorkDate();
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//查询线路基础信息
		gprsIdMap=hdfsservice.getLineBaseInfo(workmonth,workdate,fs);
	}
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String jsonStr = tuple.getString(0);
		if(JSONUtil.getJSONType(jsonStr)){
			OldGPS oldgps = JSONUtil.Json2Bean(jsonStr, OldGPS.class);	
			String lineId=getLineIdByGprsId(oldgps.getGprsId());
			String citycode_lineId=oldgps.getCityCode()+"@"+lineId;
			collector.emit(new Values(citycode_lineId,oldgps));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}
	
	//根据GPRSID查找线路ＩＤ
	public String getLineIdByGprsId(int gprsId){
		String lineId=null;
		if(gprsIdMap.keySet().contains(gprsId)){
			lineId= gprsIdMap.get(gprsId);
		}
		return lineId;	
	}

}
