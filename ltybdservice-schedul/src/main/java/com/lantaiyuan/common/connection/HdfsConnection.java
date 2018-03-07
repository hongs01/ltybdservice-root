package com.lantaiyuan.common.connection;


import com.lantaiyuan.common.config.domain.HdfsConfig;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;

/**
 * Created by zhouyongbo on 2017-11-20.
 */
public class HdfsConnection {

    private static FileSystem fs;
    private static HdfsConnection hdfsConnection;

    private HdfsConnection() {
    }

    public static synchronized HdfsConnection getInstance(){
        if (hdfsConnection == null){
            hdfsConnection = new HdfsConnection();
        }
        return hdfsConnection;
    }

    public synchronized FileSystem getFs(){
        if (fs == null){
            try {
                fs = FileSystem.get(HdfsConfig.getConfig());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fs;
    }
}
