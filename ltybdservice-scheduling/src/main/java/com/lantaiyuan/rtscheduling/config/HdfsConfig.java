package com.lantaiyuan.rtscheduling.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;

public class HdfsConfig implements Serializable{
	  Properties property = new Properties();
	  
	   public HdfsConfig() {
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
	    	Configuration conf = new Configuration();
	    	//String hdfsPath = property.getProperty("hdfsPath");
			String hdfsPath = "hdfs://192.168.2.249:8020";

			conf.set("fs.defaultFS",hdfsPath);
			conf.set("dfs.client.block.write.replace-datanode-on-failure.policy","NEVER");
			conf.set("dfs.client.block.write.replace-datanode-on-failure.enable","true");
			return conf;
	    }

}
