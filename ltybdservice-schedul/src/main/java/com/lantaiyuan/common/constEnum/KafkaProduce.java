package com.lantaiyuan.common.constEnum;

/**
 * kafka 生产者名称
 * Created by zhouyongbo on 2017-11-21.
 */
public enum  KafkaProduce {

    /**
     *  sprk测试
     */
    SPARKTEST("sparkDemo1"),
    /**
     * 线路分时客流
     */
    LINEPASSENGERFLOWNAME("real_timeLineTopic"),

    /**
     * 站点分时客流
     */
    STATIONPASSENGERFLOWNAME("real_timeStationTopic"),

    /**
     * 线路高低峰异常
     * */
    LINEHLABNORMAL("unexpectedLineTopic"),

    /**
     * 站点高低峰异常*
     * */
    STATIONABNORMAL("unexpectedStationTopic");

    KafkaProduce(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
