package com.lantaiyuan.bdserver.kafka.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.bdserver.util.ChcpConstants;
import com.lantaiyuan.bdserver.util.JsonHelper;
import com.lantaiyuan.bdserver.websocket.service.WebsocketService;
import com.lantaiyuan.bdserver.websocket.vo.Message;
import com.lantaiyuan.bdserver.websocket.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerListener.class);

    @Autowired
    private WebsocketService websocketService;

    @KafkaListener(topics = ChcpConstants.Constants.KAFKA_LISTENER_TOPIC)
    public void consumer(String message) {

        logger.info("consumer topic string get : {}", message);

        JSONObject json=JSON.parseObject(message);
        String header=json.getString("header");
        String body=json.getString("body");
        JSONObject jsonBody=JSON.parseObject(body);
        double lon=jsonBody.getDouble("lon");
        double lat=jsonBody.getDouble("lat");

        String lonlat="("+lon+","+lat+")";
        String Accuracy="";
        float altitude=jsonBody.getFloat("altitude");
        int angle=jsonBody.getInteger("angle");
        long gpsTime=jsonBody.getLong("gps_time");
        float speed=jsonBody.getFloat("speed");
        int gpsCount=0;
        int gpsSum=0;
        int lineId=jsonBody.getInteger("line_id");
        String busNo=jsonBody.get("dev_id").toString();
        String cityCode=jsonBody.getString("city_code");

        StringBuffer sb=new StringBuffer();
        sb.append("{\"lonlat\":").append("\"").append(lonlat).append("\"").append(",\"Accuracy\":").append("\"").append(Accuracy).append("\"").append(",\"altitude\":").append(altitude).append(",\"angle\":").append(angle).append(",\"gps_time\":").append(gpsTime).append(",\"speed\":").append(speed).append(",\"gps_count\":").append(gpsCount).append(",\"gps_sum\":").append(gpsSum).append(",\"line_id\":").append(lineId).append(",\"bus_no\":").append("\"").append(busNo).append("\"").append(",\"city_code\":").append("\"").append(cityCode).append("\"").append("}");

        message= sb.toString();
        sb.delete(0,sb.length());
        
        Message messageReq = new Message();
        messageReq.setMessage(message);

        logger.info("send websocket request : {}", JsonHelper.toJson(messageReq).toString());

        Response response = websocketService.send(messageReq);

        logger.info("send websocket response : {}", JsonHelper.toJson(response).toString());

    }

}
