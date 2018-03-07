package com.lty.MQ.consume;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lty.common.util.DateUtil;
import com.lty.netty.client.NettyStartClient;
import com.lty.netty.packet.CoordinateNettyPacket;

/**  
 * @Description:    实时GPS位置信息
 * @author: yuwenfeng
 * @date:   2018年1月25日 下午5:20:20   
*/
@Component
public class ItsRealTimeGPS {
    private static final Logger logger = LoggerFactory.getLogger(ItsRealTimeGPS.class);

    @KafkaListener(topics = { "ITS_Topic_GIS", "ITS_Topic_GIS_HD" })
    public void listen(ConsumerRecord<String, String> record) {
        try {
            JSONObject jsonObject = JSON.parseObject(record.value());
            JSONObject body = jsonObject.getJSONObject("body");
            double lon=body.getDouble("lon");
            double lat=body.getDouble("lat");

            String lonlat="("+lon+","+lat+")";
            String Accuracy="";
            float altitude=body.getFloat("altitude");
            int angle=body.getInteger("angle");
            long gpsTime=body.getLong("gps_time");
            float speed=body.getFloat("speed");
            int gpsCount=0;
            int gpsSum=0;
            int lineId=body.getInteger("line_id");
            String busNo=body.get("dev_id").toString();
            String cityCode=body.getString("city_code");
            CoordinateNettyPacket coordinateNettyPacket = new CoordinateNettyPacket(lonlat,Accuracy,altitude,angle,gpsTime,speed,gpsCount,gpsSum,lineId,busNo,cityCode);
            if (null != coordinateNettyPacket) {
                NettyStartClient.doProcess(coordinateNettyPacket);
            } else {
            }
        } catch (Exception e) {
            logger.error("数据源错误:", e);
        }
    }
}
