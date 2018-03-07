package com.ltybdservice;

import com.ltybdservice.bolt.gpsBolt;
import com.ltybdservice.bolt.inoutStationBolt;
import com.ltybdservice.bolt.passFlowBolt;
import com.ltybdservice.hiveoptions.GetHiveOptitions;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.hive.bolt.HiveBolt;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.zookeeper.client.ConnectStringParser;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class Kafka2HiveApplication {
	
	 public static String zkServer = "39.108.123.102:2181";
	 public static String zkroot = "/storm";
	 public static int NUM_WORKERS = 1;
	 public static int NUM_ACKERS =1;
	 public static int MSG_TIMEOUT = 180;
	 public static int SPOUT_PARALLELISM_HINT = 1;
	 public static int BOLT_PARALLELISM_HINT = 1;
	 public static int HIVEBOLT_PARALLELISM_HINT = 1;

	 public StormTopology buildTopology() {  
	        TopologyBuilder builder = new TopologyBuilder(); 
	        //spout发射从kafka中读取的信息
//	        builder.setSpout("gpsSpout", new KafkaSpout(createSpoutConfig(zkServer,"Topic_GIS","gpsoffset")), SPOUT_PARALLELISM_HINT);
//		    builder.setBolt("gpsDataBolt", new gpsBolt(), BOLT_PARALLELISM_HINT).shuffleGrouping("gpsSpout");
//		    builder.setBolt("gpshiveBolt", new HiveBolt(GetHiveOptitions.GetOptitions("ods_gps_coordinate")),HIVEBOLT_PARALLELISM_HINT).shuffleGrouping("gpsDataBolt");
	        	
		    builder.setSpout("stationSpout", new KafkaSpout(createSpoutConfig(zkServer,"Topic_StationIO","stationoffset")), SPOUT_PARALLELISM_HINT);
		    builder.setBolt("stationDataBolt", new inoutStationBolt(), BOLT_PARALLELISM_HINT).shuffleGrouping("stationSpout");
		    builder.setBolt("stationhiveBolt", new HiveBolt(GetHiveOptitions.GetOptitions("ods_in_out_station")),HIVEBOLT_PARALLELISM_HINT).shuffleGrouping("stationDataBolt");
		        
//	        builder.setSpout("enterSpout", new KafkaSpout(createSpoutConfig("192.168.2.250:2181","enterStationUpTopic","enterhiveBolt")), SPOUT_PARALLELISM_HINT); 
//		    builder.setBolt("enterDataBolt", new enterStationBolt(), BOLT_PARALLELISM_HINT).shuffleGrouping("enterSpout");  
//		    builder.setBolt("enterhiveBolt", new HiveBolt(GetHiveOptitions.GetOptitions("ods_enterstation")),HIVEBOLT_PARALLELISM_HINT).shuffleGrouping("enterDataBolt");
//		        
//	        builder.setSpout("leaveSpout", new KafkaSpout(createSpoutConfig("192.168.2.250:2181","enterStationUpTopic","leavehiveBolt")), SPOUT_PARALLELISM_HINT);
//		    builder.setBolt("leaveDataBolt", new leaveStationBolt(), BOLT_PARALLELISM_HINT).shuffleGrouping("leaveSpout");
//		    builder.setBolt("leavehiveBolt", new HiveBolt(GetHiveOptitions.GetOptitions("ods_leavestation")),HIVEBOLT_PARALLELISM_HINT).shuffleGrouping("leaveDataBolt");
//
		    builder.setSpout("passSpout", new KafkaSpout(createSpoutConfig(zkServer,"Topic_FlowStat","passoffset")), SPOUT_PARALLELISM_HINT);
		    builder.setBolt("passDataBolt", new passFlowBolt(), BOLT_PARALLELISM_HINT).shuffleGrouping("passSpout");  
		    builder.setBolt("passhiveBolt", new HiveBolt(GetHiveOptitions.GetOptitions("ods_passengerflow")),HIVEBOLT_PARALLELISM_HINT).shuffleGrouping("passDataBolt");
		    
	        return builder.createTopology();  
	    }  
		/**
		 * 获取spoutConfig
		 *
		 * @param topic
		 *            topic名称
		 * @param id
		 *            id
		 * @return
		 */
		private static SpoutConfig createSpoutConfig(String zkServer,String topic,String id) {
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
//			spoutConfig.ignoreZkOffsets = true;
			spoutConfig.startOffsetTime = 0L;
			return spoutConfig;
		}
		
		public static void main(String[] args) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
			
			Kafka2HiveApplication kafkaTopology = new Kafka2HiveApplication();
	        StormTopology stormTopology = kafkaTopology.buildTopology();  
	        
			Config config=new Config();
			config.setNumWorkers(NUM_WORKERS);  
	        config.setNumAckers(NUM_ACKERS);  
	        config.setMessageTimeoutSecs(MSG_TIMEOUT);  
	        config.setMaxSpoutPending(5000);
			config.put("HADOOP_USER_NAME","hdfs");
	        config.put("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
	        config.put("hive.metastore.execute.setugi", "true");
	        config.put("hive.support.concurrency", "true");
	        config.put("hive.execution.engine", "tez");
	        config.put("hive.txn.manager", "org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
	        config.put("max.request.size", "15728640");

			if (args.length == 0) {
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("Kafka2HiveApplication", config, stormTopology);
				Thread.sleep(60 * 1000 * 10 * 10);
				cluster.killTopology("Kafka2HiveApplication");
				cluster.shutdown();
				System.exit(0);
			} else if (args.length == 1) {
				StormSubmitter.submitTopology(args[0], config, stormTopology);
			} else {
				System.out.println("Usage: Kafka2HiveApplication [topology name]");
				System.exit(1);
			}
		}
		
}
