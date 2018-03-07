package com.ltybdservice;

import com.ltybdservice.bolt.*;
import com.ltybdservice.common.GetRunConfig;
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
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.zookeeper.client.ConnectStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @auther zhouzefei
 */
public class BuspoolApplication {
	 
	public static int NUM_WORKERS;  
    public static int NUM_ACKERS;  
    public static int MSG_TIMEOUT;  
    public static int SPOUT_PARALLELISM_HINT;  
    public static int FIRST_BOLT_PARALLELISM_HINT;  
    public static int SECOND_BOLT_PARALLELISM_HINT;  
    public static int THIRD_BOLT_PARALLELISM_HINT;
    public static String zkServer;
    public static String spouttopic;
    public static String zkroot;
    public static String zkid;
    public static String redispool;
    public static Map<String,String> runConfigMap = new HashMap<String, String>();
    
    private static final Logger LOG = LoggerFactory.getLogger(BuspoolApplication.class);
    
    public StormTopology buildTopology() {  
        TopologyBuilder builder = new TopologyBuilder();  
        //spout发射从kafka中读取的信息
        builder.setSpout("kafkaSpout", new KafkaSpout(createSpoutConfig()), SPOUT_PARALLELISM_HINT);  
        //第一个bolt将获取的信息按citycode发射到下一个bolt
        builder.setBolt("firsteBolt", new FirstBolt(), FIRST_BOLT_PARALLELISM_HINT).shuffleGrouping("kafkaSpout");  
        //第二个bolt将信息按用户所乘坐的巴士线路发射到下一个bolt
        builder.setBolt("secondBolt", new SecondBolt(), SECOND_BOLT_PARALLELISM_HINT).fieldsGrouping("firsteBolt",new Fields("citycode")); 
        //第三个bolt寻找满足拼车的用户，车辆资源形成订单
        builder.setBolt("thirdBolt", new ThirdBolt(getClusterConfig()), THIRD_BOLT_PARALLELISM_HINT).fieldsGrouping("secondBolt",new Fields("busId"));
        //第四个bolt往kafka推送消息
        builder.setBolt("fourthBolt", new FourthBolt(getClusterConfig()), THIRD_BOLT_PARALLELISM_HINT).shuffleGrouping("thirdBolt");
        return builder.createTopology();  
    }  
    
    public JedisClusterConfig getClusterConfig(){
    	Set<InetSocketAddress> nodes = new HashSet<InetSocketAddress>();
    	String[] redisraw = redispool.split(",");
    	try {
//			nodes.add(new InetSocketAddress(InetAddress.getByName("192.168.2.21"),7000));
//			nodes.add(new InetSocketAddress(InetAddress.getByName("192.168.2.21"),7001));
//	    	nodes.add(new InetSocketAddress(InetAddress.getByName("192.168.2.21"),7002));
//	    	nodes.add(new InetSocketAddress(InetAddress.getByName("192.168.2.21"),7003));
//	    	nodes.add(new InetSocketAddress(InetAddress.getByName("192.168.2.21"),7004));
//	    	nodes.add(new InetSocketAddress(InetAddress.getByName("192.168.2.21"),7005));
    		for(int i=0;i<redisraw.length;i++){
    			String host = redisraw[i].split(":")[0];
    			int port = Integer.parseInt(redisraw[i].split(":")[1]);
    			nodes.add(new InetSocketAddress(InetAddress.getByName(host),port));
    		}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	JedisClusterConfig clusterConfig = new JedisClusterConfig.Builder().setNodes(nodes).build();
    	return clusterConfig;
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
	private static SpoutConfig createSpoutConfig() {
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
		SpoutConfig spoutConfig = new SpoutConfig(zkHosts, spouttopic, zkroot, zkid);
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConfig.zkServers = serverAddresses;
		spoutConfig.zkPort = zkPort;
		spoutConfig.zkRoot = zkroot;
//		spoutConfig.ignoreZkOffsets = true;
		spoutConfig.startOffsetTime = 0L;
		return spoutConfig;
	}

    
	/**
	 * @param args
	 * @throws AuthorizationException 
	 * @throws InvalidTopologyException 
	 * @throws AlreadyAliveException 
	 */
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException, InterruptedException {
		
		LOG.error("begin");
		//读取配置文件，加载配置信息
		GetRunConfig runConfig = new GetRunConfig();
		runConfigMap = runConfig.getStormConf();
		NUM_WORKERS =  Integer.parseInt(runConfigMap.get("NUM_WORKERS"));
		NUM_ACKERS =  Integer.parseInt(runConfigMap.get("NUM_ACKERS"));
		MSG_TIMEOUT = Integer.parseInt(runConfigMap.get("MSG_TIMEOUT"));
		SPOUT_PARALLELISM_HINT = Integer.parseInt(runConfigMap.get("SPOUT_PARALLELISM_HINT"));
		FIRST_BOLT_PARALLELISM_HINT = Integer.parseInt(runConfigMap.get("FIRST_BOLT_PARALLELISM_HINT"));
		SECOND_BOLT_PARALLELISM_HINT = Integer.parseInt(runConfigMap.get("SECOND_BOLT_PARALLELISM_HINT"));
		THIRD_BOLT_PARALLELISM_HINT = Integer.parseInt(runConfigMap.get("THIRD_BOLT_PARALLELISM_HINT"));
		zkServer = runConfigMap.get("zkServer");
		spouttopic = runConfigMap.get("spouttopic");
		zkroot = runConfigMap.get("zkroot");
		zkid = runConfigMap.get("zkid");
		redispool = runConfigMap.get("redispool");
		
		BuspoolApplication kafkaTopology = new BuspoolApplication();
        StormTopology stormTopology = kafkaTopology.buildTopology();  
        
		Config config=new Config();
		config.setNumWorkers(NUM_WORKERS);  
        config.setNumAckers(NUM_ACKERS);  
        config.setMessageTimeoutSecs(MSG_TIMEOUT);  
        config.setMaxSpoutPending(5000);
		if (args.length == 0) {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("BuspoolApplication", config, stormTopology);
			Thread.sleep(60 * 1000 * 10 * 10);
			cluster.killTopology("BuspoolApplication");
			cluster.shutdown();
			System.exit(0);
		} else if (args.length == 1) {
			StormSubmitter.submitTopology(args[0], config, stormTopology);
		} else {
			System.out.println("Usage: BuspoolApplication [topology name]");
			System.exit(1);
		}

	}

}
