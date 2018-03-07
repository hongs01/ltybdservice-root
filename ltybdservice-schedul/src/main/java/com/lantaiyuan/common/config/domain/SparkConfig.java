package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.ListUtil;
import com.lantaiyuan.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyongbo on 2017-11-16.
 */
public class SparkConfig extends ISetProperties{
    @Override
    public String getPrefix() {
        return "spark";
    }

    private static boolean exploit = false;//是否为开发模式
    private static String appName;
    private static String master;
    private static List<String> jars;
    private static String executorMemory;
    private static String checkpoint;
    private static int second = 1;//window偏移数

    public static int getSecond() {
        return second;
    }

    public static String getAppName() {
        return appName;
    }

    public static boolean isExploit() {
        return exploit;
    }

    public static String getMaster() {
        return master;
    }

    public static List<String> getJars() {
        return jars;
    }

    public static String getExecutorMemory() {
        return executorMemory;
    }

    public static String getCheckpoint() {
        return checkpoint;
    }

    public static Map<String,String> getSparkConfig(){
        Map<String,String> map = new HashMap<String, String>();
        map.put("spark.master",master);
        map.put("spark.app.name",appName);
        if (exploit && !ListUtil.isListEmpty(jars) ){
            map.put("spark.jars",String.join(",",jars));
        }
        if (!StringUtil.isEmpty(executorMemory)){
            map.put("spark.executor.memory",executorMemory);
        }
        return map;
    }
}
