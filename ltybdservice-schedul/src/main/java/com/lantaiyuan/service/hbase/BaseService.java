package com.lantaiyuan.service.hbase;

import com.lantaiyuan.common.connection.HbaseConnection;
import com.lantaiyuan.common.connection.HdfsConnection;
import com.lantaiyuan.service.IAbstractService;
import com.lantaiyuan.utils.DateUtil;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyongbo on 2017-11-22.
 * hbase 基础功能
 */
public class BaseService /*extends IAbstractService*/{
    private Connection conn ;

//    @Override
//    public void init() {
//        conn = HbaseConnection.getInstance().getConnection();
//    }



    /**
     * 创建表
     * @param tableName
     * @param familyName
     */
    public void createTable(String tableName, String familyName){
        Admin admin=null;
        TableName table = TableName.valueOf(tableName);
        try {
            admin = HbaseConnection.getInstance().getConnection().getAdmin();
            if (!admin.tableExists(table)) {
                HTableDescriptor descriptor = new HTableDescriptor(table);
                descriptor.addFamily(new HColumnDescriptor(familyName.getBytes()));
                admin.createTable(descriptor);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(admin);
        }
    }

    //从hbase获取当天数据
    public List<Map<String,String>> getTodayHbaseData(String tableName) throws IOException {
        String ToDate=null;
        List<Map<String,String>>  listmap=new ArrayList<Map<String,String>>();
        Table table = null;
        table = conn.getTable(TableName.valueOf(tableName));
        ResultScanner rs = table.getScanner(new Scan());
        for (Result r : rs) {
            if(DateUtil.isToday(new String(r.getRow()))){
                Map<String,String> map=new HashMap<String, String>();
                for (KeyValue keyValue : r.raw()) {
                    map.put((new String(keyValue.getKey()).trim().split("@")[1]), new String(keyValue.getValue()));
                }
                listmap.add(map);
            }
        }
        return listmap;
    }


}
