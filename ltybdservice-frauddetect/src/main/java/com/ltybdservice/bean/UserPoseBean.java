package com.ltybdservice.bean;


import com.ltybdservice.hbaseutil.IEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:实例
 * {"citycode":"440305","currenttime":1505108929363,"latitude":"22.561434","longitude":"113.93873",
 * "phonemodel":"867707027321274","position":"航天微电机大厦","userid":"3976"}
 */
public class UserPoseBean implements IEvent, Serializable {
    private String citycode;//城市编码
    private long currenttime	;//	发生时间
    private float latitude;//	纬度
    private float longitude;//	经度
    private String phonemodel;//手机唯一标识码
    private String position;//	用户位置文字描述
    private String userid;//		用户编号

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

    @Override
    public String getValueByField(String field) {
        String string = null;
        switch (field) {
            case "citycode":
                string = citycode;
                break;
            case "currenttime":
                string = String.valueOf(currenttime);
                break;
            case "latitude":
                string = String.valueOf(latitude);
                break;
            case "longitude":
                string = String.valueOf(longitude);
                break;
            case "phonemodel":
                string = phonemodel;
                break;
            case "position":
                string = position;
                break;
            case "userid":
                string = userid;
                break;
        }
        return string;
    }



    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "citycode":
                citycode = value;
                break;
            case "currenttime":
                currenttime = Long.parseLong(value);
                break;
            case "latitude":
                latitude = Float.parseFloat(value);
                break;
            case "longitude":
                longitude = Float.parseFloat(value);
                break;
            case "phonemodel":
                phonemodel = value;
                break;
            case "position":
                position = value;
                break;
            case "userid":
                userid = value;
                break;
        }
        return this;
    }
    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("citycode");
        Fields.add("currenttime");
        Fields.add("latitude");
        Fields.add("longitude");
        Fields.add("phonemodel");
        Fields.add("position");
        Fields.add("userid");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
