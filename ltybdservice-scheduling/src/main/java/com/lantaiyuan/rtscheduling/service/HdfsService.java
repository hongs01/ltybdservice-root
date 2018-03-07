package com.lantaiyuan.rtscheduling.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.rtscheduling.bean.StationObject;

public class HdfsService {
	
	/**实时线路接口   查询预测客流基础数据
	 * @param fs
	 * @return
	 */
	public Map<String,Integer> getPredictPassengerFlowBaseInfo(String workdate,FileSystem fs) {
		Map<String,Integer>   lineNumMap = new HashMap<String,Integer>();
		
		String hdfs = "/apps/hive/warehouse/dp.db/dm_passflow_line_bigdata";
		hdfs += "/citycode=" + "130400";
		hdfs += "/workdate="+workdate;
		FileStatus[] stats;
		try {		
			stats = fs.listStatus(new Path(hdfs));
		for (int i = 0; i < stats.length; ++i) {
			if (stats[i].isFile()) {
				// regular file
				FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
		          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
		          String lineStr;  
		          while ((lineStr = bis.readLine()) != null) {
		        	  lineNumMap.put(lineStr.split("\t")[0]+"@"+lineStr.split("\t")[1],Integer.parseInt(lineStr.split("\t")[2]));
		          }
			}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lineNumMap;
	}
	
	//查询线路基础数据
	public Map<String,String> getLineBaseData(String workmonth,String workdate,FileSystem fs){
		Map<String,String> 	lineNameMap =new HashMap<String,String>();
		String hdfs = "/apps/hive/warehouse/dc.db/ods_op_line";
		hdfs += "/citycode=" + "130400";
		hdfs += "/workmonth="+workmonth;
		hdfs += "/workdate="+workdate;
		//hdfs += "/lineid=1234";
		FileStatus[] stats;
		try {
			stats = fs.listStatus(new Path(hdfs));
			for (int i = 0; i < stats.length; ++i) {
				if (stats[i].isFile()) {
					// regular file
					FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
			          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
			          String lineStr;  
			          while ((lineStr = bis.readLine()) != null) {
			        //	  System.err.println(lineStr.split("\t")[0]+":"+lineStr.split("\t")[6]);
						lineNameMap.put(lineStr.split("\t")[0],lineStr.split("\t")[6]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lineNameMap;
}
	
	
	//实时站点分时客流(单条线路)
	public Map<String,String> getPrdBaseInfo(String workmonth,String workdate,FileSystem fs){
		Map<String,String> preBaseMap = new HashMap<String,String>();
		String hdfs = "/apps/hive/warehouse/dp.db/dm_passflow_station_dm";
		hdfs += "/cityid=" + "130400";
		hdfs += "/cycle=30";
		hdfs += "/type=1";
		hdfs += "/workmonth="+workmonth;
		hdfs += "/workdate="+workdate;
		//hdfs += "/lineid=1234";
		FileStatus[] stats;
		try {
			stats = fs.listStatus(new Path(hdfs));
			for (int i = 0; i < stats.length; ++i) {
				if (stats[i].isFile()) {
					// regular file
					FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
			          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
			          String lineStr;  
			          while ((lineStr = bis.readLine()) != null) {
			        	  //将lineid,direction,stationid,time(H)作为key,    将stagnantTraffic，upNum,downNum作为key    
						preBaseMap.put(lineStr.split("\t")[4]+"@"+lineStr.split("\t")[5]+"@"+lineStr.split("\t")[10]+"@"+lineStr.split("\t")[8],lineStr.split("\t")[11]+"@"+lineStr.split("\t")[11]+"@"+lineStr.split("\t")[11]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return preBaseMap;
	}
	
	
	//站点分时客流多条线路
	public Map<String,String> getLineStationBaseInfo(String workmonth,String workdate,FileSystem fs){
		Map<String,String> LineStationMap = new HashMap<String,String>();
		
		String hdfs = "/apps/hive/warehouse/dc.db/ods_op_station";
		hdfs += "/citycode=" + "130400";
		hdfs += "/workmonth="+workmonth;
		hdfs += "/workdate="+workdate;
		//hdfs += "/lineid=1234";
		FileStatus[] stats;
		try {
			stats = fs.listStatus(new Path(hdfs));
			for (int i = 0; i < stats.length; ++i) {
				if (stats[i].isFile()) {
					// regular file
					FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
			          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
			          String lineStr;  
			          while ((lineStr = bis.readLine()) != null) {
						LineStationMap.put(lineStr.split("\t")[21],lineStr.split("\t")[2]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return LineStationMap;
	}
	
	
	//查询站点和站名的基础信息
		public Map<String,String> getStationIDStationNameBaseInfo(String workmonth,String workdate,FileSystem fs){
			Map<String,String> StationMap=new HashMap<String,String>();
			
			String hdfs = "/apps/hive/warehouse/dc.db/ods_op_stationblock";
			hdfs += "/citycode=" + "130400";
			hdfs += "/workmonth="+workmonth;
			hdfs += "/workdate="+workdate;
			FileStatus[] stats;
			try {
				stats = fs.listStatus(new Path(hdfs));
				for (int i = 0; i < stats.length; ++i) {
					if (stats[i].isFile()) {
						// regular file
						FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
				          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
				          String lineStr;  
				          while ((lineStr = bis.readLine()) != null) {
							 StationMap.put(lineStr.split("\t")[0],lineStr.split("\t")[1]);
				          }
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return StationMap;
	}
		
		       //查车辆到站预测准点离线表
				public Map<String,List<StationObject>> getStationObjsData(String workmonth,String workdate,FileSystem fs){
					Map<String,List<StationObject>> StationObjsData=new HashMap<String,List<StationObject>>();

					if(StationObjsData.size() == 0 ){
						
					}
					JSONObject stationObj=new JSONObject();
					String hdfs = "/apps/hive/warehouse/dp.db/dm_arrival_forecast_dm";
					//hdfs += "/citycode=" + "130400";
					//hdfs += "/cycle=30";
					hdfs += "/cityid=" + "130400";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          String objKey = "";
						          while ((lineStr = bis.readLine()) != null) {
						        	  //线路ID,线路方向,趟次 ,站序         以下分别为 //站点ID,站点序号，预测到站时间
						        	  objKey = lineStr.split("\t")[2]+"@"+lineStr.split("\t")[3]+"@"+lineStr.split("\t")[4];
						        	  StationObject sobj = new StationObject();
						        	  sobj.setStationId(lineStr.split("\t")[5]);
						        	  sobj.setStationNo(lineStr.split("\t")[6]);
						        	  sobj.setPlanningTime(lineStr.split("\t")[7]);
						        	  sobj.setPredictedTime(lineStr.split("\t")[7]);
						        	  sobj.setRealTime("");
						        	  if(StationObjsData.get(objKey)==null){
						        		  List<StationObject> list=new ArrayList<StationObject>();
						        		  list.add(sobj);
						        		  StationObjsData.put(objKey, list);
						        	  }else{
						        		  List<StationObject> list= StationObjsData.get(objKey);
						        		  list.add(sobj);
						        		  //是否添加
						        		  StationObjsData.put(objKey, list);
						        	  }
						          }
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					};
					return StationObjsData;
				}
				
				//查询站点基础数据
				public  Map<String,Double> getStationBaseInfo(String workmonth,String workdate,FileSystem fs){
					Map<String,Double> StationBaseData=new HashMap<String,Double>();
					
					String hdfs = "/apps/hive/warehouse/dc.db/ods_op_station";
					hdfs += "/citycode=" + "130400";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
						        	StationBaseData.put(lineStr.split("\t")[1]+"@"+lineStr.split("\t")[20]+"@"+lineStr.split("\t")[5], Double.parseDouble(lineStr.split("\t")[7]));
						          }
							}
						}
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return StationBaseData;
				}
				
				//查询站点基础数据  
				public Map<String,String> getStationBaseInfo1(String workmonth,String workdate,FileSystem fs){
					Map<String,String> StationBaseData1=new HashMap<String,String>();
					String hdfs = "/apps/hive/warehouse/dc.db/ods_op_station";
					hdfs += "/citycode=" + "130400";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
									StationBaseData1.put(lineStr.split("\t")[1]+"@"+lineStr.split("\t")[20]+"@"+lineStr.split("\t")[5], lineStr.split("\t")[5]);
								}
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return StationBaseData1;
				}
				
				 //查询线路基础数据
				public Map<String,String> getLineBaseInfo(String workmonth,String workdate, FileSystem fs) {
					Map<String,String> gprsIdMap = new HashMap<String,String>();
					String hdfs = "/apps/hive/warehouse/dc.db/ods_op_line";
					hdfs += "/citycode=" + "130400";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
										gprsIdMap.put(lineStr.split("\t")[7],lineStr.split("\t")[0]);
						          }
							}
						}
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return gprsIdMap;
			}
				
				public Map<Integer,String> getCarBaseInfo(String workmonth,String workdate, FileSystem fs) {
					Map<Integer,String> carMap = new HashMap<Integer,String>();
					String hdfs = "/apps/hive/warehouse/dc.db/ods_tjs_car";
					hdfs += "/citycode=" + "130400";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
						        		carMap.put(Integer.parseInt(lineStr.split("\t")[21]),lineStr.split("\t")[6]);
										//ZkrsMap.put(lineStr.split("\t")[6], Integer.parseInt(lineStr.split("\t")[12]));
						          }
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return carMap;
			}
				
				public Map<String,Integer> getZkrsBaseInfo(String workmonth,String workdate,FileSystem fs) {
					Map<String,Integer> ZkrsMap = new HashMap<String,Integer>();
					String hdfs = "/apps/hive/warehouse/dc.db/ods_tjs_car";
					hdfs += "/citycode=" + "130400";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
										ZkrsMap.put(lineStr.split("\t")[6], Integer.parseInt(lineStr.split("\t")[12]));
						          }
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return ZkrsMap;
			}
				
				//查询线路分时基本数据
				public Map<String,Integer> getBaseInfo(String workmonth,String workdate,FileSystem fs){
					Map<String,Integer> lineIddirectiontimeMap=new HashMap<String,Integer>();
					String hdfs = "/apps/hive/warehouse/dp.db/dm_passflow_line_dm";
					hdfs += "/cityid=" + "130400";
					hdfs += "/cycle=" + "30";
					hdfs += "/type=" + "1";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {

						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
									lineIddirectiontimeMap.put(lineStr.split("\t")[4]+"@"+lineStr.split("\t")[5]+"@"+lineStr.split("\t")[8],Integer.parseInt(lineStr.split("\t")[9]));
								}
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return lineIddirectiontimeMap;
				}
				
				
				public Map<String,Integer> getPassFlowBaseInfo(String workmonth,String workdate, FileSystem fs) {
					
					Map<String,Integer> lineIdDirectionStationTimeMap=new HashMap<String,Integer>();
				
					String hdfs = "/apps/hive/warehouse/dp.db/dm_passflow_station_dm";
					hdfs += "/cityid=" + "130400";
					hdfs += "/cycle=30";
					hdfs += "/type=1";
					hdfs += "/workmonth="+workmonth;
					hdfs += "/workdate="+workdate;
					FileStatus[] stats;
					try {
						stats = fs.listStatus(new Path(hdfs));
						for (int i = 0; i < stats.length; ++i) {
							if (stats[i].isFile()) {
								// regular file
								FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
						          BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));  
						          String lineStr;  
						          while ((lineStr = bis.readLine()) != null) {
									/*System.err.println(lineStr.split("\t")[0]);
									System.out.println(lineStr.split("\t")[1]);*/
						        	  lineIdDirectionStationTimeMap.put(lineStr.split("\t")[4]+"@"+lineStr.split("\t")[5]+"@"+lineStr.split("\t")[10]+"@"+lineStr.split("\t")[8],Integer.parseInt(lineStr.split("\t")[11]));
								}
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return lineIdDirectionStationTimeMap;
				}
				
				
}
