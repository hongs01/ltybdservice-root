package com.lantaiyuan.rtscheduling.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;

public class StormSchedulingConfig implements Serializable{
	
	Properties property = new Properties();
	
	public StormSchedulingConfig() {
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
	
    public Properties getProperty() {
		return property;
    }
}
