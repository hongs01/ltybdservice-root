package com.lantaiyuan.rtscheduling.topology;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.zookeeper.client.ConnectStringParser;

import com.lantaiyuan.rtscheduling.bolt.GpsUpBolt;
import com.lantaiyuan.rtscheduling.bolt.OldGpsUpBolt;
import com.lantaiyuan.rtscheduling.bolt.PassFlowToBeanBolt;
import com.lantaiyuan.rtscheduling.bolt.PassengerFlowByStationBolt;
import com.lantaiyuan.rtscheduling.bolt.PassengerFlowRealTimeUpBolt;
import com.lantaiyuan.rtscheduling.bolt.RTLineTopicResultBolt;
import com.lantaiyuan.rtscheduling.bolt.RTStationAllLineTopicResultBolt;
import com.lantaiyuan.rtscheduling.bolt.RTStationTopicByLineBolt;
import com.lantaiyuan.rtscheduling.bolt.RTStationTopicResultBolt;
import com.lantaiyuan.rtscheduling.bolt.RTVehicleTopicResultBolt;
import com.lantaiyuan.rtscheduling.bolt.UnexpectedLineTimeBolt;
import com.lantaiyuan.rtscheduling.bolt.UnexpectedLineTopoResultBolt;
import com.lantaiyuan.rtscheduling.bolt.UnexpectedStationByLineBolt;
import com.lantaiyuan.rtscheduling.bolt.UnexpectedStationByLineTimeBolt;
import com.lantaiyuan.rtscheduling.bolt.UnexpectedStationResultBolt;
import com.lantaiyuan.rtscheduling.config.StormSchedulingConfig;

public class SchedulingTopology {
	
	public static Map<String,String> runConfigMap = new HashMap<String, String>();
	public static String zkServer;
	public static String zkroot;
	public static String zkid;
    public static int BOLT_PARALLELISM_HINT_ONE;  
    public static int BOLT_PARALLELISM_HINT_TWO;
    public static int BOLT_PARALLELISM_HINT_THREE;
    public static int NUM_WORKERS;  
    public static int NUM_ACKERS;  
    public static int MSG_TIMEOUT;  

	
	 public StormTopology buildTopology() {   
	        TopologyBuilder builder = new TopologyBuilder();  
	        //线路分时客流
	          builder.setSpout("kafkaSpout_FlowStat", new KafkaSpout(createSpoutConfig("Topic_FlowStat","RTLineTopicTopo_key")), BOLT_PARALLELISM_HINT_ONE);  
	          builder.setBolt("Bolt_FlowStat", new PassFlowToBeanBolt(), BOLT_PARALLELISM_HINT_TWO).shuffleGrouping("kafkaSpout_FlowStat");  
	          builder.setBolt("RTLineTopicResultBolt", new RTLineTopicResultBolt(),BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("Bolt_FlowStat", new Fields("citycode_lineid"));
	        //站点分时客流(单条线路)
	          builder.setBolt("Bolt_passengerFlowRealTimeUp", new PassengerFlowRealTimeUpBolt(), BOLT_PARALLELISM_HINT_ONE).shuffleGrouping("kafkaSpout_FlowStat");  
	          builder.setBolt("RTStationTopicByLineBolt", new RTStationTopicByLineBolt(),BOLT_PARALLELISM_HINT_TWO).fieldsGrouping("Bolt_passengerFlowRealTimeUp", new Fields("citycode_lineid_direction"));
	          builder.setBolt("RTStationTopicResultBolt", new RTStationTopicResultBolt(), BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("RTStationTopicByLineBolt", new Fields("citycodelineiddirectionsid"));
            //站点分时客流(多条线路汇总)
	          builder.setBolt("Bolt_PassengerFlowRTimeUpBolt", new PassengerFlowByStationBolt(), BOLT_PARALLELISM_HINT_TWO).shuffleGrouping("kafkaSpout_FlowStat");  
	          builder.setBolt("RTStationAllLineTopicResultBolt", new RTStationAllLineTopicResultBolt(),BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("Bolt_PassengerFlowRTimeUpBolt", new Fields("citycode_StationId"));
	      
	       //车辆实时客流
	      //spout发射从kafka中topic gpsUpTopic中读取的信息
	        builder.setSpout("kafkaSpout_gpsUp", new KafkaSpout(createSpoutConfig("Topic_GIS","Topic_GIS_key")),BOLT_PARALLELISM_HINT_ONE);
	     //gps新网关
	        builder.setBolt("Bolt_gpsUp", new GpsUpBolt(), BOLT_PARALLELISM_HINT_TWO).shuffleGrouping("kafkaSpout_gpsUp");  
	     //gps老网关
	        builder.setBolt("Bolt_gpsUp", new OldGpsUpBolt(),BOLT_PARALLELISM_HINT_TWO).shuffleGrouping("kafkaSpout_gpsUp");
	      //  builder.setBolt("RTLineTopicBolt",new RTVehicleTopicResultBolt(), BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("Bolt_passengerFlowRealTimeUp", new Fields("citycode_lineid_direction"));
	      //  builder.setBolt("RTLineTopicBolt1", new RTVehicleTopicResultBolt(),BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("Bolt_gpsUp", new Fields("citycode_lineId_direction"));

	        //线路高低峰异常
			builder.setBolt("UnexpectedLineTimeBolt", new UnexpectedLineTimeBolt(), BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("Bolt_passengerFlowRealTimeUp", new Fields("citycode_lineid_direction"));
			builder.setBolt("UnexpectedLineTopoResultBolt", new UnexpectedLineTopoResultBolt(), BOLT_PARALLELISM_HINT_THREE).fieldsGrouping("UnexpectedLineTimeBolt",new Fields("citycodeLineIdDirectionMin"));

			//站点高低峰异常
			builder.setBolt("UnexpectedStationByLineBolt", new UnexpectedStationByLineBolt(), BOLT_PARALLELISM_HINT_TWO).fieldsGrouping("Bolt_passengerFlowRealTimeUp", new Fields("citycode_lineid_direction"));
			builder.setBolt("UnexpectedStationByLineTimeBolt", new UnexpectedStationByLineTimeBolt(), BOLT_PARALLELISM_HINT_TWO).fieldsGrouping("UnexpectedStationByLineBolt", new Fields("citycodelineiddirectionsno"));
			builder.setBolt("UnexpectedStationResultBolt", new UnexpectedStationResultBolt(), BOLT_PARALLELISM_HINT_TWO).fieldsGrouping("UnexpectedStationByLineTimeBolt",new Fields("citycodelineiddirectionsnoMin"));

	        
	        return builder.createTopology();  
	    }
	 
	 /**
		 * 获取spoutConfig
		 *
		 * @param topic
		 *            topic名称
		 * @param zkRoot
		 *            zk目录
		 * @param id
		 *            id
		 * @return
		 */
		private static SpoutConfig createSpoutConfig(String topic,String id) {
			ConnectStringParser connectStringParser = new ConnectStringParser(
					zkServer);
			List<InetSocketAddress> serverInetAddresses = connectStringParser
					.getServerAddresses();
			List<String> serverAddresses = new ArrayList<String>(
					serverInetAddresses.size());
			Integer zkPort = serverInetAddresses.get(0).getPort();
			for (InetSocketAddress serverInetAddress : serverInetAddresses) {
				serverAddresses.add(serverInetAddress.getHostName());
			}

			ZkHosts zkHosts = new ZkHosts(zkServer);
			SpoutConfig spoutConfig = new SpoutConfig(zkHosts, topic, zkroot, id);
			spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
			spoutConfig.zkServers = serverAddresses;
			spoutConfig.zkPort = zkPort;
			spoutConfig.zkRoot = zkroot;
			spoutConfig.ignoreZkOffsets = true;
			//spoutConfig.startOffsetTime = 0L;
			spoutConfig.startOffsetTime =kafka.api.OffsetRequest.LatestTime();
			return spoutConfig;
		}
	 
	 
	public static void main(String[] args) throws  AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		//读取配置文件，加载配置信息
		StormSchedulingConfig stormschedulingconfig = new StormSchedulingConfig();
		Properties  properties= stormschedulingconfig.getProperty();
		 zkServer = properties.getProperty("zkServer");
		 zkroot=properties.getProperty("zkroot");
		 zkid=properties.getProperty("zkid");
		 BOLT_PARALLELISM_HINT_ONE=Integer.parseInt(properties.getProperty("BOLT_PARALLELISM_HINT_ONE"));
		 BOLT_PARALLELISM_HINT_TWO=Integer.parseInt(properties.getProperty("BOLT_PARALLELISM_HINT_TWO"));
		 BOLT_PARALLELISM_HINT_THREE=Integer.parseInt(properties.getProperty("BOLT_PARALLELISM_HINT_THREE"));
		 NUM_WORKERS=Integer.parseInt(properties.getProperty("NUM_WORKERS"));
		 NUM_ACKERS=Integer.parseInt(properties.getProperty("NUM_ACKERS"));
		 MSG_TIMEOUT=Integer.parseInt(properties.getProperty("MSG_TIMEOUT"));
		 
		 SchedulingTopology kafkaTopology = new SchedulingTopology();  
        StormTopology stormTopology = kafkaTopology.buildTopology(); 
        
        Config config=new Config();
		config.setNumWorkers(NUM_WORKERS);  
        config.setNumAckers(NUM_ACKERS);  
        config.setMessageTimeoutSecs(MSG_TIMEOUT);  
        config.setMaxSpoutPending(5000);
        
        if(args.length > 0){
            // cluster submit.
            try {
                 StormSubmitter.submitTopology(args[0], config, stormTopology);
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            }
        }else{
            new LocalCluster().submitTopology(zkid, config, stormTopology);
        }
	}

}
