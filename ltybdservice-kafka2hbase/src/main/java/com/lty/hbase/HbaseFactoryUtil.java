package com.lty.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HbaseFactoryUtil {
	  Connection connection;
	  private static HbaseFactoryUtil hbaseFactoryUtil;  
	    private HbaseFactoryUtil(){}
	    public static synchronized HbaseFactoryUtil getInstance() {  
	    if (hbaseFactoryUtil == null) {  
	    	hbaseFactoryUtil = new HbaseFactoryUtil();  
	    }  
	    return hbaseFactoryUtil;  
	    }  
	    
	    public Connection con()  {
	    	  
	        // 取得一个数据库连接的配置参数对象  
	        Configuration conf = HBaseConfiguration.create();
	    //    conf.set("hbase.zookeeper.quorum", "192.168.2.254,192.168.2.249");  
	        
	    //    conf.set("hbase.zookeeper.property.clientPort", "2181"); 
	  
	        // 取得一个数据库连接对象  
	        try {
				connection = ConnectionFactory.createConnection(conf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        return connection;
	    }    
	    
	    public Configuration fig()  {
	    	  
	        // 取得一个数据库连接的配置参数对象  
	        Configuration conf = HBaseConfiguration.create();
	        return conf;
	    }     
}
