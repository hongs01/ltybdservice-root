package com.lty.MQ;

/**
 * @author zhouyongbo 2017/10/28
 * MQ 应用层
 */
public interface IApplyAbsMqProduction {

    void sendMsg(Object msg);
}
