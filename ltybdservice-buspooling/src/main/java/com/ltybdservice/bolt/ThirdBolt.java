package com.ltybdservice.bolt;

import com.ltybdservice.bean.BuspoolingDemandInfo;
import com.ltybdservice.common.GetBusLines;
import com.ltybdservice.common.GetRunConfig;
import com.ltybdservice.common.JSONUtil;
import com.ltybdservice.kafka.WriteInfoToKafka;
import org.apache.storm.redis.bolt.AbstractRedisBolt;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.JedisCommands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @auther zhouzefei
 */
public class ThirdBolt extends AbstractRedisBolt {
	
	private String getlatlonurl;
	private String buspathplanurl;
	private String gaodeKey;
	private Logger LOG = LoggerFactory.getLogger(ThirdBolt.class);
	private boolean poolFlag = false;
	private String poolMatchid = null;

	public ThirdBolt(JedisClusterConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
		Map<String,String> runConfigMap = new HashMap<String, String>();
		GetRunConfig runConfig = new GetRunConfig();
		runConfigMap = runConfig.getStormConf();
		this.gaodeKey = runConfigMap.get("gaodekey");
		this.getlatlonurl = runConfigMap.get("getlatlonurl");
		this.buspathplanurl = runConfigMap.get("buspathplanurl");
	}
	
	public boolean CreateNewOrder(BuspoolingDemandInfo bdiInfo,JedisCommands jedisCommands){
		//调用高德API的对象
		GetBusLines getBusLine = new GetBusLines();
		String matchId = UUID.randomUUID().toString().replaceAll("-", "");
		//把订单写入key池中
		jedisCommands.rpush("BusKey",bdiInfo.getBusId()+"matchId");
		//插入一条拼车订单
		jedisCommands.rpush(bdiInfo.getBusId()+"matchId", matchId);
		//生成这个用户所在线路的所有站点信息
		//调用高德API获取该线路起点和终点的经纬度
		String busBeginName = bdiInfo.getBusLine().split("--")[0].split("\\(")[1]; 
		String busEndName = bdiInfo.getBusLine().split("--")[1].split("\\)")[0]; 
		String cityCode = bdiInfo.getCityCode();
		String url = getlatlonurl + "city=" + cityCode + "&key=" + gaodeKey + "&address=";
		//获取起点经纬度
		String begin_url = url + busBeginName ;
		String beginLoc = getBusLine.addressToGPS(begin_url);
		//获取终点经纬度
		String end_url = url + busEndName ;
		String endLoc = getBusLine.addressToGPS(end_url);
		//调用高德API获取该线路的所有站点
		String busurl= this.buspathplanurl + "city=" + cityCode + "&key=" + this.gaodeKey +"&strategy=2&origin=";
		busurl = busurl + beginLoc.replace("\"","") + "&destination=" + endLoc.replace("\"","") ;
		List <List<String>> resultData = getBusLine.getTransferPlan(busurl);
		List <String> busLines = resultData.get(1);
		//将该线路信息放入Redis
		for(int i=0;i<busLines.size();i=i+2){
			jedisCommands.zadd(bdiInfo.getBusId()+"BusLine", i, busLines.get(i));
		}
		//将用户起点和终点的站序信息写入bean中
		Long startStationNO = jedisCommands.zrank(bdiInfo.getBusId()+"BusLine", bdiInfo.getRealStartName());
		Long endStationNO = jedisCommands.zrank(bdiInfo.getBusId()+"BusLine", bdiInfo.getRealEndName());
		if(endStationNO == null){
			endStationNO = (long) (busLines.size()/2+1);
		}
		bdiInfo.setStartStationNO(startStationNO);
		bdiInfo.setEndStationNO(endStationNO);
		//取用户容忍等待时间的中间时间作为用户出发时间
		long arrivalTime = (Long.parseLong(bdiInfo.getEarliestStartTime()) + Long.parseLong(bdiInfo.getLatestStartTime()))/2;
		bdiInfo.setArrivalTime(arrivalTime);
		long downTIme = arrivalTime + (Long.parseLong(bdiInfo.getDuration()));
		bdiInfo.setDownTime(downTIme);
		//将bean转为json
		String bdiInfoJson = JSONUtil.Object2Json(bdiInfo);
		//将该用户加入拼车
		jedisCommands.rpush(matchId,bdiInfoJson);
		//给该用户发送一个拼车动态消息
		WriteInfoToKafka writeInfo = new WriteInfoToKafka();
		writeInfo.realTimePush(jedisCommands,matchId);
		this.poolFlag = true;
		this.poolMatchid = matchId;
		return true;
	}
	
	public boolean JoinBusPool(BuspoolingDemandInfo bdiInfo /*新加入用户的信息*/
			,BuspoolingDemandInfo oldUserinfo /*在拼订单里某一个用户的信息*/
			,String matchId /*拼车的matchID*/
			,Long matchSize /*该线路所有matchId的个数*/
			,int i /*该matchId在该线路所有matchId的排序*/
			,JedisCommands jedisCommands /*操作redis的对象*/){
		//调用高德API的对象
		GetBusLines getBusLine = new GetBusLines();
		//第一重判断，出行时间是否冲突
		long newEarTime = Long.parseLong(bdiInfo.getEarliestStartTime());
		long newLatTime = Long.parseLong(bdiInfo.getLatestStartTime());
		long oldArrivalTime = Long.parseLong(oldUserinfo.getLatestStartTime());
		if(newEarTime > oldArrivalTime){
			//出行时间存在冲突
			if(i== matchSize-1){
				//已经是匹配的最后一个订单，都没有找到可拼的，就新开辟一个订单
				CreateNewOrder(bdiInfo,jedisCommands);
				return true;
			}
			else{ //寻找下一个订单
				return false;
			}
		}
		else{
			//出行时间不冲突，判断到达时间是否合理
			//调用高德API，查询从新加入用户的O点到订单里已存在最近的O点的距离所消耗的时间
			String cityCode = bdiInfo.getCityCode();
			String busurl= this.buspathplanurl + "city=" + cityCode + "&key=" + this.gaodeKey +"&strategy=2&origin=";
			busurl = busurl + bdiInfo.getRealStartlat() + "," + bdiInfo.getRealStartlon() + "&destination=" 
			+ oldUserinfo.getRealStartlat() + "," + oldUserinfo.getRealStartlon() ;
			List resultList = getBusLine.getTransferPlan(busurl);
			String duration = (String) resultList.get(2);
			//测算出新用户的出发时间
			long arrivalTime = oldArrivalTime - Long.parseLong(duration)*1000;
			//判断新用户的出发时间是否在可接受范围内
			if(newEarTime <= arrivalTime && arrivalTime <= newLatTime){
				//满足条件，加入该拼车
				bdiInfo.setArrivalTime(arrivalTime);
				long downTIme = arrivalTime + (Long.parseLong(bdiInfo.getDuration()));
				bdiInfo.setDownTime(downTIme);
				//将bean转为json
				String bdiInfoJson = JSONUtil.Object2Json(bdiInfo);
				//将该用户加入拼车,并且放在对比用户之前
				String oldbdiJson = JSONUtil.Object2Json(oldUserinfo);
				jedisCommands.linsert(matchId,LIST_POSITION.BEFORE, oldbdiJson, bdiInfoJson);
				//给该用户发送一个拼车动态消息
//				WriteInfoToKafka writeInfo = new WriteInfoToKafka();
//				writeInfo.realTimePush(jedisCommands,matchId);
				this.poolFlag = true;
				this.poolMatchid = matchId;
				return true;
			}
			else{
				//不满足条件
				if(i== matchSize-1){
					//已经是匹配的最后一个订单，都没有找到可拼的，就新开辟一个订单
					CreateNewOrder(bdiInfo,jedisCommands);
					return true;
				}
				else{ //寻找下一个订单
					return false;
				}
			}
		}
	}
	
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getInstance();
			BuspoolingDemandInfo bdiInfo = (BuspoolingDemandInfo)input.getValue(1);
			LOG.error(bdiInfo.getBusLine());
			LOG.error(bdiInfo.getBusId() + "matchId");
			Long matchSize = jedisCommands.llen(bdiInfo.getBusId()+"matchId");
			//拼车第一步，先看这条巴士线路有没有在拼的订单
			if(matchSize>0){
				//循环订单号。查看是否可以满足用户的拼车需求
				outterLoop: for(int i=0;i<matchSize;i++){
					//获取拼车的matchId
					String matchId = jedisCommands.lindex(bdiInfo.getBusId()+"matchId", i);
					//获取该订单里第一个用户
					String bdiJson = jedisCommands.lindex(matchId, 0);
					BuspoolingDemandInfo oldUserInfo = JSONUtil.Json2Bean(bdiJson, BuspoolingDemandInfo.class);
					//判断新加入用户起点在已拼用户的起点之前还是之后
					long OldIndex = oldUserInfo.getStartStationNO();
					Long NewIndex = jedisCommands.zrank(bdiInfo.getBusId()+"BusLine", bdiInfo.getRealStartName());
					Long endStationNO = jedisCommands.zrank(bdiInfo.getBusId()+"BusLine", bdiInfo.getRealEndName());
					if(endStationNO == null){
						endStationNO = (long) (jedisCommands.zcard(bdiInfo.getBusId()+"BusLine")+1);
					}
					//补全新用户的bean信息
					bdiInfo.setEndStationNO(endStationNO);
					bdiInfo.setStartStationNO(NewIndex);
					//如果新用户起点在已存在订单里的用户之前
					if(NewIndex < OldIndex){
						if(JoinBusPool(bdiInfo,oldUserInfo,matchId,matchSize,i,jedisCommands)){
							break;
						}
						else{
							continue;
						}
					}
					else if(NewIndex == OldIndex){
						//如果用户起点和已存在订单的用户起点一致
						long oldArrivalTime = oldUserInfo.getArrivalTime();
						//判断到达时间是否在该用户容忍范围内
						long newEarTime = Long.parseLong(bdiInfo.getEarliestStartTime());
						long newLatTime = Long.parseLong(bdiInfo.getLatestStartTime());
						if(newEarTime<=oldArrivalTime && oldArrivalTime<=newLatTime){
							//把该用户加入订单
							bdiInfo.setArrivalTime(oldArrivalTime);
							long downTIme = oldArrivalTime + (Long.parseLong(bdiInfo.getDuration()));
							bdiInfo.setDownTime(downTIme);
							//将bean转为json
							String bdiInfoJson = JSONUtil.Object2Json(bdiInfo);
							//将该用户加入拼车,并且放在对比用户之前
							String oldbdiJson = JSONUtil.Object2Json(oldUserInfo);
							jedisCommands.linsert(matchId,LIST_POSITION.BEFORE, oldbdiJson, bdiInfoJson);
							//给该用户发送一个拼车动态消息
//							WriteInfoToKafka writeInfo = new WriteInfoToKafka();
//							writeInfo.realTimePush(jedisCommands,matchId);
							this.poolFlag = true;
							this.poolMatchid = matchId;
							break;
						}
						else{
							continue;//继续寻找
						}
					}
					else if(NewIndex > OldIndex){
						//如果新用户起点在已存在订单里的用户之后					
						for(int j=0;j<jedisCommands.llen(matchId);j++){
							//获取该订单里的用户信息
							String oldbdiJson = jedisCommands.lindex(matchId, j);
							BuspoolingDemandInfo orderUserInfo = JSONUtil.Json2Bean(oldbdiJson, BuspoolingDemandInfo.class);
							if(NewIndex < orderUserInfo.getStartStationNO()){
								long oldArrivalTime = orderUserInfo.getArrivalTime();
								if(JoinBusPool(bdiInfo,orderUserInfo,matchId,matchSize,i,jedisCommands)){
									break outterLoop;
								}
								else{
									continue outterLoop;
								}
							}
							else if(NewIndex == orderUserInfo.getStartStationNO()){
								long oldArrivalTime = orderUserInfo.getArrivalTime();
								//判断到达时间是否在该用户容忍范围内
								long newEarTime = Long.parseLong(bdiInfo.getEarliestStartTime());
								long newLatTime = Long.parseLong(bdiInfo.getLatestStartTime());
								if(newEarTime <= oldArrivalTime && oldArrivalTime <= newLatTime){
									//满足条件，加入拼车
									bdiInfo.setArrivalTime(oldArrivalTime);
									long downTIme = oldArrivalTime + (Long.parseLong(bdiInfo.getDuration()));
									bdiInfo.setDownTime(downTIme);
									//将bean转为json
									String bdiInfoJson = JSONUtil.Object2Json(bdiInfo);
									//将该用户加入拼车,并且放在对比用户之前
									String oderUserJson = JSONUtil.Object2Json(orderUserInfo);
									jedisCommands.linsert(matchId,LIST_POSITION.BEFORE, oderUserJson, bdiInfoJson);
									//给该用户发送一个拼车动态消息
//									WriteInfoToKafka writeInfo = new WriteInfoToKafka();
//									writeInfo.realTimePush(jedisCommands,matchId);
									this.poolFlag = true;
									this.poolMatchid = matchId;
									break outterLoop;
								}
								else{ //寻找下一个订单
									continue outterLoop;
								}
							}
							if(j==jedisCommands.llen(matchId)-1 && NewIndex > orderUserInfo.getStartStationNO()){
								//说明该用户按站序排是最后一个用户
								//判断到达该用户站点的时间
								String cityCode = bdiInfo.getCityCode();
								String busurl= this.buspathplanurl + "city=" + cityCode + "&key=" + this.gaodeKey +"&strategy=2&origin=";
								busurl = busurl + orderUserInfo.getRealStartlat() + "," + orderUserInfo.getRealStartlon() + "&destination=" 
										+ bdiInfo.getRealStartlat() + "," + bdiInfo.getRealStartlon() ;
								//调用高德API的对象
								GetBusLines getBusLine = new GetBusLines();
								List resultList = getBusLine.getTransferPlan(busurl);
								String duration = (String) resultList.get(2);
								//测算出新用户的出发时间
								long arrivalTime = orderUserInfo.getArrivalTime() + Long.parseLong(duration)*1000;
								//判断到达时间是否在该用户容忍范围内
								long newEarTime = Long.parseLong(bdiInfo.getEarliestStartTime());
								long newLatTime = Long.parseLong(bdiInfo.getLatestStartTime());
								if(newEarTime <= arrivalTime && arrivalTime <= newLatTime){
									//满足条件，加入拼车
									bdiInfo.setArrivalTime(arrivalTime);
									long downTIme = arrivalTime + (Long.parseLong(bdiInfo.getDuration()));
									bdiInfo.setDownTime(downTIme);
									//将bean转为json
									String bdiInfoJson = JSONUtil.Object2Json(bdiInfo);
									//将该用户加入拼车,并且放在对比用户之后
									String oderUserJson = JSONUtil.Object2Json(orderUserInfo);
									jedisCommands.linsert(matchId,LIST_POSITION.AFTER, oderUserJson, bdiInfoJson);
									//给该用户发送一个拼车动态消息
//									WriteInfoToKafka writeInfo = new WriteInfoToKafka();
//									writeInfo.realTimePush(jedisCommands,matchId);
									this.poolFlag = true;
									this.poolMatchid = matchId;
									break outterLoop;
								}
								else{
									//不满足条件，判断是否是最后一个订单
									if(i== matchSize-1){
										//已经是匹配的最后一个订单，都没有找到可拼的，就新开辟一个订单
										CreateNewOrder(bdiInfo,jedisCommands);
										break outterLoop;
									}
									else{ //寻找下一个订单
										continue outterLoop;
									}
								}
							}
						}
					}
				}
			}
			else{
				//在没有在拼订单的情况下，生成一个订单号，把该用户作为第一个拼车用户
				CreateNewOrder(bdiInfo,jedisCommands);
			}
			if(this.poolFlag){
				this.collector.emit(new Values(this.poolMatchid));
			}
		} finally {
			if (jedisCommands != null) {
				returnInstance(jedisCommands);
				this.collector.ack(input);
			}
		}

	}
	

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("matchid"));
	}

}
