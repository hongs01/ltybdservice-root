package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.util.List;

/**
 * Hbase 配置
 * Created by zhouyongbo on 2017-11-22.
 */
public class HBaseConfig extends ISetProperties{

    @Override
    public String getPrefix() {
        return "hbase";
    }

    private static List<String> zkquorums;
    private static String rootdir;
    private static String port;


    public static List<String> getZkquorums() {
        return zkquorums;
    }

    public static String getRootdir() {
        return rootdir;
    }

    public static String getPort() {
        return port;
    }

    public static Configuration getHbaseConfig(){
        Configuration entries = HBaseConfiguration.create();
        entries.set("hbase.zookeeper.property.clientPort", port);
        entries.set("hbase.zookeeper.quorum","slave,master");
        entries.set("hbase.rootdir",rootdir);
        return entries;
    }
}
