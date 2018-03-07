package com.lantaiyuan.common.constEnum;

/**
 * kafka 消费者名称
 * Created by zhouyongbo on 2017-11-21.
 */
public enum KafkaConsum {

    /**
     * 测试
     */
    SPARKTEST("sparkDemo"),

    /**
     * 线路分时客流
     */
    LINEPASSENGERFLOWNAME("Topic_FlowStat"),

    /**
     * 站点分时客流
     */
    STATIONPASSENGERFLOWNAME("Topic_FlowStat"),

    /**
     * 站点分时客流(多条线路汇总)
     * */
    STATIONALLLINEPASSENGERFLOWNAME("Topic_FlowStat"),

    /**
    *车辆实时客流
    * */
    VHHICLEPASSENGERFLOWNAME("Topic_FlowStat"),

    /**
    *车辆gps位置信息
     * */
    CARGPSNAME("gpsUpTopic"),

    /**
    邯郸app车辆位置信息
     **/
    GW2APP("gw2app-handan"),

    /**
     *正常行驶期间gps信息
     * */
    ITS_Topic_GIS("ITS_Topic_GIS"),

    /**
     * 车辆进出站信息
     */
    ITS_Topic_StationIO("ITS_Topic_StationIO"),

    /**
     * 车辆进出场信息
     */
    ITS_Topic_ParkIO("ITS_Topic_ParkIO"),

    /**
     *站内高密度发送gps信息
     * */
    ITS_Topic_GIS_HD("ITS_Topic_GIS_HD"),

   /**
    *线路高低峰异常
    * */
   LINEHLABNORMALNAME("Topic_FlowStat"),

    /**
     *站点高低峰异常
     * */
    STATIONHLABNORMALNAME("Topic_FlowStat");

    KafkaConsum(String key) {
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
