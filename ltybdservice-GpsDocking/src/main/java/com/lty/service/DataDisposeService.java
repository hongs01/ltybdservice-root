package com.lty.service;

import com.lty.MQ.production.gps.EboardPackProduction;
import com.lty.common.bean.gps.EBoardPack;
import com.lty.common.bean.gps.GpsPacket;
import com.lty.common.bean.mq.MQHeard;
import com.lty.common.bean.mq.MQProtocol;
import com.lty.common.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据处理
 * @author zhouyongbo 2017/12/6
 */
public class DataDisposeService {
    private static Logger logger = LoggerFactory.getLogger(DataDisposeService.class);
    private EboardPackProduction eboardPackProduction = SpringUtil.getBeanByClass(EboardPackProduction.class);

    public void dispose(Object data){
        try{
            if(data instanceof EBoardPack){
                eboardPackProduction.sendMsg(new MQProtocol(new MQHeard(0x103),(EBoardPack)data));

            }else if (data instanceof GpsPacket){
                eboardPackProduction.sendMsg(data);
            }
        }catch (Exception e){
            logger.error("发送异常消息:",e);
        }
    }
}
