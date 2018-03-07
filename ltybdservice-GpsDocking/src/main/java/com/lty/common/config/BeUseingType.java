package com.lty.common.config;


import com.lty.common.MQ.MQType;

/**
 *@author zhouyongbo 2017/10/28
 * 正在使用中的中间件
 */
public enum BeUseingType {
    USEMQ(MQType.KAFKA);
    private Object type;

    BeUseingType(Object type) {
        this.type = type;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }
}
