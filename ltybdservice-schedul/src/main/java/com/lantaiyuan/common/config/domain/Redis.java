package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.ListUtil;
import com.lantaiyuan.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyongbo on 2017-11-16.
 */
public class Redis extends ISetProperties {

    @Override
    public String getPrefix() {
        return "redis";
    }

    private static String host;

    private static int port;

    private static int timeout;

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static Map<String,String> getRedisConfig(){
        Map<String,String> map = new HashMap<String, String>();
        map.put("redis.host",host);
        map.put("redis.port",port+"");
        map.put("redis.timeout",timeout+"");
        return map;
    }
}
