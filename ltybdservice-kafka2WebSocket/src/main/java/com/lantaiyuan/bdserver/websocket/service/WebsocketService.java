package com.lantaiyuan.bdserver.websocket.service;

import com.lantaiyuan.bdserver.util.ChcpConstants;
import com.lantaiyuan.bdserver.util.JsonHelper;
import com.lantaiyuan.bdserver.websocket.vo.Message;
import com.lantaiyuan.bdserver.websocket.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WebsocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Response send(@RequestBody Message reqVO) {

        logger.info("get req msg: {}", reqVO.getMessage());

        //String message = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())+ " 收到一条消息: " + reqVO.getMessage();
        String message= reqVO.getMessage();

        Response response = new Response(message);
        messagingTemplate.convertAndSend(ChcpConstants.WEBSOCKET_DESTINATION.getTopic(), response);

        logger.info("send ws msg: {}", JsonHelper.toJson(response).toString());

        return response;
    }

}
