package com.ltybdservice.config;

import com.ltybdservice.hbaseutil.HbaseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HbaseTableConfig implements Serializable {
    //hbase table name
    private  String userPoseTable;
    private  String simpleTransactionTable;
    private  String columnFamily;
    public HbaseTableConfig() {
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/config.properties");
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
        getConfig(pro);
    }
    private void getConfig(Properties pro) {
        userPoseTable=pro.getProperty("userPose");//压力测试使用
        simpleTransactionTable=pro.getProperty("simpleTransaction");
        columnFamily =pro.getProperty("columnFamily");
    }
    public void createHbaseTable(){
        HbaseUtil connectionInstance= HbaseUtil.getConnectionInstance();
        try {
            //halfHour table
            connectionInstance.createSchemaTables(userPoseTable, columnFamily);
            connectionInstance.createSchemaTables(simpleTransactionTable, columnFamily);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionInstance.closeConnection();
    }

    public String getUserPoseTable() {
        return userPoseTable;
    }

    public void setUserPoseTable(String userPoseTable) {
        this.userPoseTable = userPoseTable;
    }

    public String getSimpleTransactionTable() {
        return simpleTransactionTable;
    }

    public void setSimpleTransactionTable(String simpleTransactionTable) {
        this.simpleTransactionTable = simpleTransactionTable;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }
}
