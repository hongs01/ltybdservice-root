package com.lantaiyuan.rtscheduling.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.config.HbaseConfig;

import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.KeyValue;


public class HbaseService {
	
	private static final Logger LOG = LoggerFactory.getLogger(HbaseService.class);
	static HbaseConfig hbaseconfig=new HbaseConfig();
	private static Connection conn=hbaseconfig.getConn();
	private static String RTSERIES="realInfo@";
	private static String RTStationSERIES="realStationInfo@";
	private static String RTLineSERIES="realStationAllLineInfo@";
	
	    //hbase创建一个表
		public void createTable(String tableName,String familyName){
			Admin admin=null;
			TableName table = TableName.valueOf(tableName);
			LOG.info("====>begin create");
			try {
	          admin = conn.getAdmin();
	          if (!admin.tableExists(table)) {
	               System.out.println(tableName + " table not Exists");
	               HTableDescriptor descriptor = new HTableDescriptor(table);
	               descriptor.addFamily(new HColumnDescriptor(familyName.getBytes()));
	               admin.createTable(descriptor);
	          }
	          LOG.info("====>success");
			}catch(Exception e){
				e.printStackTrace();
			}finally {
	            IOUtils.closeQuietly(admin);
			}
		}
		
		//线路数据存入hbase
		public void savaToHbase(String tableName,String lineId,String nowHour,int RTimePassengerFlow) throws IOException{
			 Table table = null;
			String currenttime=Long.toString(System.currentTimeMillis());
			LOG.info("====>begin insert data");
			//System.out.println("开始插入数据");
	        table = conn.getTable(TableName.valueOf(tableName));
	        Put put = new Put(Bytes.toBytes(currenttime));
	        put.add(RTSERIES.getBytes(),"lineId@".getBytes(),lineId.getBytes());
			put.add(RTSERIES.getBytes(),"time@".getBytes(),nowHour.getBytes());
			put.add(RTSERIES.getBytes(),"real_timePassengerFlow@".getBytes(),String.valueOf(RTimePassengerFlow).getBytes());
	        table.put(put);
	        LOG.info("====> insert data  success");
	        //  System.out.println("插入数据成功");
		}
		
		
		//从hbase查询前辆车的上车人数
		public List<Map<String,String>> getDataFromHbase(String tableName,String lineId,String lineDirection,String stationId) throws IOException{
		System.err.println("===="+tableName+lineId+lineDirection+stationId);
			List<Map<String,String>> listMap=new ArrayList<Map<String,String>>();
			List<Map<String,String>> timelistMap=new ArrayList<Map<String,String>>();
			listMap=getTodayHbaseData(tableName);
			int real_timePassengerFlow=0;
			JSONObject json=new JSONObject();
			for(int i=0;i<listMap.size();i++){
				if(listMap.get(i).get("lineId").equals(lineId)&&listMap.get(i).get("lineDirection").equals(lineDirection)&&listMap.get(i).get("stationId").equals(stationId)){
					timelistMap.add(listMap.get(i));
				}
			}
			System.err.println("******"+timelistMap.size());
			return timelistMap;
		}
		
		public  List<Map<String,String>> getTodayHbaseData(String tableName) throws IOException{
			String ToDate=null;
			
			List<Map<String,String>>  listmap=new ArrayList<Map<String,String>>();
			    Table table = null;
				System.out.println("开始查询");
	            table = conn.getTable(TableName.valueOf(tableName));
	            ResultScanner rs = table.getScanner(new Scan());
	            for (Result r : rs) {
	                    // System.out.println("获得到rowkey:" + new String(r.getRow()));
	            	if(isToday(new String(r.getRow()))){
	            		 Map<String,String> map=new HashMap<String, String>();
	                     for (KeyValue keyValue : r.raw()) {
	                  //  System.err.println("key:"+new String(keyValue.getKey()).trim());
	                  //  System.err.println("123:"+new String(keyValue.getKey()).trim().split("@")[1]);
	                    map.put((new String(keyValue.getKey()).trim().split("@")[1]), new String(keyValue.getValue()));
	                     }
	                     listmap.add(map);
	            	}
	            }
	            System.out.println("查询结束");
	            System.err.println(listmap.size());
	            return listmap;
		}
		
		//解析rowKey,获取年月日
		public boolean isToday(String rowkey){
			long row= Long.parseLong(rowkey);
			//当天日期时间戳
			 Date datetoday = new Date(System.currentTimeMillis());
			 //rowkey时间戳
			 Date daterow = new Date(row);
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH"); 
		        String Todate = simpleDateFormat.format(datetoday);  
		        String rowStr = simpleDateFormat.format(daterow);  
		       if(Todate.equals(rowStr)){
		    	   return true;
		       }else{
		    	   return false;
		       }
		}
		
		//站点数据存入hbase
		public void savaStationToHbase(String tableName,String lineId,String lineDirection,String stationId, String nowtime,int stagnantTraffic,int upNumber,int downNumber) throws IOException{
			 Table table = null;
				String currenttime=Long.toString(System.currentTimeMillis());
				LOG.info("====>begin insert data");
				//	System.out.println("开始插入数据");
		        table = conn.getTable(TableName.valueOf(tableName));
		        Put put = new Put(Bytes.toBytes(currenttime));
		        put.add(RTStationSERIES.getBytes(),"lineId@".getBytes(),lineId.getBytes());
				put.add(RTStationSERIES.getBytes(),"lineDirection@".getBytes(),lineDirection.getBytes());
				put.add(RTStationSERIES.getBytes(),"stationId@".getBytes(),stationId.getBytes());
				put.add(RTStationSERIES.getBytes(),"time@".getBytes(),nowtime.getBytes());
				put.add(RTStationSERIES.getBytes(),"stagnantTraffic@".getBytes(),String.valueOf(stagnantTraffic).getBytes());
				put.add(RTStationSERIES.getBytes(),"upNumber@".getBytes(),String.valueOf(upNumber).getBytes());
				put.add(RTStationSERIES.getBytes(),"downNumber@".getBytes(),String.valueOf(downNumber).getBytes());
		        table.put(put);
		        LOG.info("====> insert data success");
		}	
		
		//站点数据存入hbase
		public void savaStationAllLineToHbase(String tableName,String stationId, String nowtime,int stagnantTraffic,int upNumber,int downNumber) throws IOException{
			 Table table = null;
				String currenttime=Long.toString(System.currentTimeMillis());
				LOG.info("====>begin insert data");
				//System.out.println("开始插入数据");
		        table = conn.getTable(TableName.valueOf(tableName));
		        Put put = new Put(Bytes.toBytes(currenttime));
				put.add(RTLineSERIES.getBytes(),"stationId@".getBytes(),stationId.getBytes());
				put.add(RTLineSERIES.getBytes(),"time@".getBytes(),nowtime.getBytes());
				put.add(RTLineSERIES.getBytes(),"stagnantTraffic@".getBytes(),String.valueOf(stagnantTraffic).getBytes());
				put.add(RTLineSERIES.getBytes(),"upNumber@".getBytes(),String.valueOf(upNumber).getBytes());
				put.add(RTLineSERIES.getBytes(),"downNumber@".getBytes(),String.valueOf(downNumber).getBytes());
		        table.put(put);
		        LOG.info("====> insert data success");
		        //  System.out.println("插入数据成功");
		}
		
		//从hbase查询前辆车的上车人数
		public List<Map<String,String>> getDataFromHbase(String tableName,String stationId) throws IOException{
			List<Map<String,String>> listMap=new ArrayList<Map<String,String>>();
			List<Map<String,String>> timelistMap=new ArrayList<Map<String,String>>();

			listMap=getTodayHbaseData(tableName);
			JSONObject json=new JSONObject();
			if(listMap.size()>0){
				for(int i=0;i<listMap.size();i++){
					if(listMap.get(i).get("stationId").equals(stationId)){
						timelistMap.add(listMap.get(i));
					}
				}		
			}
			return timelistMap;
		}
}
