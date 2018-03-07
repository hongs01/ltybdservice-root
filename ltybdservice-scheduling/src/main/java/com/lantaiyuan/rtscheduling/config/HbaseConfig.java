package com.lantaiyuan.rtscheduling.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseConfig implements Serializable{
	private static final Logger LOG = LoggerFactory.getLogger(HbaseConfig.class);

	Properties property = new Properties();
	Configuration config = HBaseConfiguration.create(); 
	   public HbaseConfig() {
	        InputStream in = this.getClass().getResourceAsStream("/config.properties");
	        try {
	            property.load(in);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	   
	    public Configuration getConfig() {
	    	String hbasezkquorum = property.getProperty("hbasezkquorum");
	    	String hbaserootdir = property.getProperty("hbaserootdir");
	    	
	    	config.set("hbase.zookeeper.property.clientPort", "2181"); 
			config.set("hbase.zookeeper.quorum", hbasezkquorum);
			config.set("hbase.rootdir",hbaserootdir);
			return config;
	    }
	    
	    public Connection getConn(){
	    	Connection conn = null;
	    	try {
				 conn = ConnectionFactory.createConnection(config);
			} catch (IOException e) {
				LOG.error("数据库连接不成功！");
				e.printStackTrace();
			}
	    	return conn;
	    }
}
