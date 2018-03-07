package com.lantaiyuan.service.hdfs;

import com.lantaiyuan.common.connection.HdfsConnection;
import com.lantaiyuan.service.IAbstractService;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * hdfs 操作
 * Created by zhouyongbo on 2017-11-20.
 */
public class HdfsOperation  extends IAbstractService{

    private HdfsConnection hdfsConnection = null;

    @Override
    public void init() {
        hdfsConnection = HdfsConnection.getInstance();
    }

    /**实时线路接口   查询预测客流基础数据
     * @param workdate
     * @return
     */
    public  Map<String,Integer> getPredictPassengerFlowBaseInfo(String workdate){
        FileSystem fs = hdfsConnection.getFs();
        Map<String,Integer> lineNumMap = new HashMap<String,Integer>();
        String hdfs = "/apps/hive/warehouse/dp.db/dm_passflow_line_bigdata";
        hdfs += "/citycode=" + "130400";
        hdfs += "/workdate="+workdate;
        FileStatus[] stats;
        try {
            stats = fs.listStatus(new Path(hdfs));
            for (int i = 0; i < stats.length; ++i) {
                if (stats[i].isFile()) {
                    // regular file
                    FSDataInputStream hdfsInStream = fs.open(stats[i].getPath());
                    BufferedReader bis = new BufferedReader(new InputStreamReader(hdfsInStream,"UTF-8"));
                    String lineStr;
                    while ((lineStr = bis.readLine()) != null) {
                        lineNumMap.put(lineStr.split("\t")[0]+"@"+lineStr.split("\t")[1],Integer.parseInt(lineStr.split("\t")[2]));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lineNumMap;
    }



}
