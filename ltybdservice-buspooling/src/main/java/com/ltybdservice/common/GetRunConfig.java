package com.ltybdservice.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class GetRunConfig {
    public Map<String, String> getStormConf() {
        Map<String, String> config = new HashMap<String, String>();
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/buspool.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> it = pro.stringPropertyNames().iterator();
        while (it.hasNext()) {
            String key = it.next();
            config.put(key, pro.getProperty(key));
        }
        return config;
    }

}
