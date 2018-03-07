package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.StringUtil;
import org.apache.hadoop.conf.Configuration;

/**
 * Created by zhouyongbo on 2017-11-20.
 */
public class HdfsConfig extends ISetProperties{

    @Override
    public String getPrefix() {
        return "hdfs";
    }
    private static String path;
    private static String failurePolicy;
    private static String failureEnable;
    public static String getPath() {
        return path;
    }

    public static String getFailurePolicy() {
        return failurePolicy;
    }

    public static String getFailureEnable() {
        return failureEnable;
    }

    public static Configuration getConfig(){
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS",path);
        if (!StringUtil.isEmpty(failurePolicy)){
            conf.set("dfs.client.block.write.replace-datanode-on-failure.policy",
                    failurePolicy);
        }
        if (!StringUtil.isEmpty(failureEnable)){
            conf.set("dfs.client.block.write.replace-datanode-on-failure.enable",
                    failureEnable);
        }
        return conf;
    }
}
