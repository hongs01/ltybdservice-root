package com.lty.MQ.consume;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lty.netty.client.NettyStartClient;
import com.lty.netty.packet.BusNettyPacket;
import com.lty.netty.packet.CoordinateNettyPacket;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**  
 * @Description:    实时公交位置信息
 * @author: shuxiaokui
 * @date:   2018年2月28日 下午2:20:20
*/
@Component
public class ItsRealTimeBus {
    private static final Logger logger = LoggerFactory.getLogger(ItsRealTimeBus.class);

    @KafkaListener(topics = "ITS_Topic_StationIO")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            JSONObject jsonObject = JSON.parseObject(record.value());
            JSONObject body = jsonObject.getJSONObject("body");
            String line_id=body.get("line_id").toString();
            String dev_id=body.get("dev_id").toString();
            //double lon=body.getDouble("lon");
            //double lat=body.getDouble("lat");
            String lonlat="";
            String bus_station_no=body.get("bus_station_no").toString();
            //String next_station_no=body.get("next_station_no").toString();
            String next_station_no="";
            String city_code=body.getString("city_code");
            String Accuracy="";
            //float altitude=body.getFloat("altitude");
            float altitude=0;
            long in_time=body.getLong("in_time");

            BusNettyPacket busNettyPacket = new BusNettyPacket(line_id,dev_id,lonlat,bus_station_no,next_station_no,city_code,Accuracy,altitude,in_time);
            if (null != busNettyPacket) {
                NettyStartClient.doProcess(busNettyPacket);
            } else {
            }
        } catch (Exception e) {
            logger.error("数据源错误:", e);
        }
    }
}
