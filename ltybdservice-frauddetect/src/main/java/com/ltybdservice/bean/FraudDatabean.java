package com.ltybdservice.bean;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class FraudDatabean {
    private String citycode;//城市编码
    private long currenttime	;//	发生时间
    private float latitude;//	纬度
    private float longitude;//	经度
    private String phonemodel;//手机唯一标识码
    private String position;//	用户逃票刷卡位置文字描述
    private String userid;//		用户编号
    private int fraud_flag;//		是否逃票

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public long getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(long currenttime) {
        this.currenttime = currenttime;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getPhonemodel() {
        return phonemodel;
    }

    public void setPhonemodel(String phonemodel) {
        this.phonemodel = phonemodel;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getFraud_flag() {
        return fraud_flag;
    }

    public void setFraud_flag(int fraud_flag) {
        this.fraud_flag = fraud_flag;
    }
}
