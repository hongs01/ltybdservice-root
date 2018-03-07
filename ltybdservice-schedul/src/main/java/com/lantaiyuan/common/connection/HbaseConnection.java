package com.lantaiyuan.common.connection;

import com.lantaiyuan.common.config.domain.HBaseConfig;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 *
 * hbase 连接
 * Created by zhouyongbo on 2017-11-22.
 */
public class HbaseConnection {
    private static Connection con = null;

    private static HbaseConnection hbaseConnection = null;

    private HbaseConnection() {
    }


    public static synchronized HbaseConnection getInstance(){
        if (hbaseConnection ==null){
            hbaseConnection = new HbaseConnection();
        }
        return hbaseConnection;
    }

    public Connection getConnection(){
        if (con == null){
            try {
              //  con = ConnectionFactory.createConnection(HBaseConfig.getHbaseConfig());
                con=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return con;
    }
}
