package com.lty.kafka;

import com.alibaba.fastjson.JSONObject;
import com.lty.hbase.HbaseFactoryUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class kafkaconsumer {

    private static String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    private static String ROW_KEY_COMMON_FORMAT = "%04d%06d%s";

    Connection conn = HbaseFactoryUtil.getInstance().con();
    Table table = conn.getTable(TableName.valueOf("bus_gps_info"));

    public kafkaconsumer() throws IOException {
    }

    @KafkaListener(topics = "ITS_Topic_GIS")
    public void processMessage(ConsumerRecord<String, String> record) {
        String value = record.value();
        try{
            JSONObject json = JSONObject.parseObject(value);
            JSONObject body = json.getJSONObject("body");
            int lineId = body.getInteger("line_id");
            int devId = body.getInteger("dev_id");
            String time = body.getString("gps_time");

            Long timestamp = Long.parseLong(time) * 1000;
            String date = new java.text.SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS).format(new java.util.Date(timestamp));

            String rowkey = String.format(ROW_KEY_COMMON_FORMAT, new Object[]{lineId,devId,date});

            Put put = new Put(Bytes.toBytes(rowkey));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("devSn"),Bytes.toBytes(body.getString("dev_sn")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("devId"),Bytes.toBytes(body.getString("dev_id")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cityCode"),Bytes.toBytes(body.getString("city_code")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("companyCode"),Bytes.toBytes(body.getString("company_code")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("lineId"),Bytes.toBytes(body.getString("line_id")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("gpsTime"),Bytes.toBytes(body.getString("gps_time")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("lon"),Bytes.toBytes(body.getString("lon")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("lat"),Bytes.toBytes(body.getString("lat")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("angle"),Bytes.toBytes(body.getString("angle")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("altitude"),Bytes.toBytes(body.getString("altitude")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("speed"),Bytes.toBytes(body.getString("speed")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("distance"),Bytes.toBytes(body.getString("distance")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("disNext"),Bytes.toBytes(body.getString("dis_next")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("timeNext"),Bytes.toBytes(body.getString("time_next")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("nextStationNo"),Bytes.toBytes(body.getString("next_station_no")));
            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("vehicleStatus"),Bytes.toBytes(body.getString("vehicle_status")));
            table.put(put);


            System.out.println("写入成功，rowkey:" + rowkey);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
