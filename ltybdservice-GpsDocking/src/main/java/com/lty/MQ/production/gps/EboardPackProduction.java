package com.lty.MQ.production.gps;


import com.alibaba.fastjson.JSON;
import com.lty.MQ.production.ApplyAbsMqProduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * gps 消费
 *
 * @author zhouyongbo 2017/12/6
 */
@Component
public class EboardPackProduction  extends ApplyAbsMqProduction {
    private static Logger logger = LoggerFactory.getLogger(EboardPackProduction.class);

    public EboardPackProduction() {
        super("gw2app-huangshan");
    }
    @Override
    public void sendMsg(Object msg) {
        execute(JSON.toJSONString(msg));
        logger.info(JSON.toJSONString(msg));
    }
}
