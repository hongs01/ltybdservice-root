package com.ltybdservice.pre.prebean;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.hbaseutil.IEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class CityGps implements IEvent, Serializable {
    private String workDate;//日,
    private String halfHour;//半小时
    private String cityCode;//城市id，
    private String max_speed_line_name;
    private int max_speed;
    private String min_speed_line_name;
    private int min_speed;
    private int cityAvgSpeed;
    private int timeAvgSpeed;
    private int toNextStopTime;//城市最新平均线路距下一站时间
    private Map<Integer, Integer> mapLineId2LineAvgSpeed = new HashMap<>();            //最新线路速度表
    private Map<Integer, Integer> mapLineId2TimeAvgSpeed = new HashMap<>();            //最新线路速度表
    private Map<Integer, Integer> mapLineId2NextStopTime = new HashMap<>();   //最新线路距离下一站时间表

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getHalfHour() {
        return halfHour;
    }

    public void setHalfHour(String halfHour) {
        this.halfHour = halfHour;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getMax_speed_line_name() {
        return max_speed_line_name;
    }

    public void setMax_speed_line_name(String max_speed_line_name) {
        this.max_speed_line_name = max_speed_line_name;
    }

    public int getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(int max_speed) {
        this.max_speed = max_speed;
    }

    public String getMin_speed_line_name() {
        return min_speed_line_name;
    }

    public void setMin_speed_line_name(String min_speed_line_name) {
        this.min_speed_line_name = min_speed_line_name;
    }

    public int getMin_speed() {
        return min_speed;
    }

    public void setMin_speed(int min_speed) {
        this.min_speed = min_speed;
    }

    public int getCityAvgSpeed() {
        return cityAvgSpeed;
    }

    public void setCityAvgSpeed(int cityAvgSpeed) {
        this.cityAvgSpeed = cityAvgSpeed;
    }

    public int getToNextStopTime() {
        return toNextStopTime;
    }

    public void setToNextStopTime(int toNextStopTime) {
        this.toNextStopTime = toNextStopTime;
    }

    public Map<Integer, Integer> getMapLineId2LineAvgSpeed() {
        return mapLineId2LineAvgSpeed;
    }

    public void setMapLineId2LineAvgSpeed(Map<Integer, Integer> mapLineId2LineAvgSpeed) {
        this.mapLineId2LineAvgSpeed = mapLineId2LineAvgSpeed;
    }

    public Map<Integer, Integer> getMapLineId2NextStopTime() {
        return mapLineId2NextStopTime;
    }

    public void setMapLineId2NextStopTime(Map<Integer, Integer> mapLineId2NextStopTime) {
        this.mapLineId2NextStopTime = mapLineId2NextStopTime;
    }

    public int getTimeAvgSpeed() {
        return timeAvgSpeed;
    }

    public void setTimeAvgSpeed(int timeAvgSpeed) {
        this.timeAvgSpeed = timeAvgSpeed;
    }

    public Map<Integer, Integer> getMapLineId2TimeAvgSpeed() {
        return mapLineId2TimeAvgSpeed;
    }

    public void setMapLineId2TimeAvgSpeed(Map<Integer, Integer> mapLineId2TimeAvgSpeed) {
        this.mapLineId2TimeAvgSpeed = mapLineId2TimeAvgSpeed;
    }

    @Override
    public String getValueByField(String field) {

        String string = null;
        switch (field) {
            case "workDate":
                string = workDate;
                break;
            case "halfHour":
                string = halfHour;
                break;
            case "cityCode":
                string = cityCode;
                break;
            case "max_speed_line_name":
                string = max_speed_line_name;
                break;
            case "max_speed":
                string = String.valueOf(max_speed);
                break;
            case "min_speed_line_name":
                string = min_speed_line_name;
                break;
            case "min_speed":
                string = String.valueOf(min_speed);
                break;
            case "cityAvgSpeed":
                string = String.valueOf(cityAvgSpeed);
                break;

            case "timeAvgSpeed":
                string = String.valueOf(timeAvgSpeed);
                break;
            case "toNextStopTime":
                string = String.valueOf(toNextStopTime);
                break;
            case "mapLineId2LineAvgSpeed":
                string = JSON.toJSONString(mapLineId2LineAvgSpeed);
                break;
            case "mapLineId2TimeAvgSpeed":
                string = JSON.toJSONString(mapLineId2TimeAvgSpeed);
                break;
            case "mapLineId2NextStopTime":
                string = JSON.toJSONString(mapLineId2NextStopTime);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "workDate":
                workDate = value;
                break;
            case "halfHour":
                halfHour = value;
                break;
            case "cityCode":
                cityCode = value;
                break;
            case "max_speed_line_name":
                max_speed_line_name = value;
                break;
            case "max_speed":
                max_speed = Integer.parseInt(value);
                break;
            case "min_speed_line_name":
                min_speed_line_name = value;
                break;
            case "min_speed":
                min_speed = Integer.parseInt(value);
                break;
            case "cityAvgSpeed":
                cityAvgSpeed = Integer.parseInt(value);
                break;
            case "timeAvgSpeed":
                timeAvgSpeed = Integer.parseInt(value);
                break;
            case "toNextStopTime":
                toNextStopTime = Integer.parseInt(value);
                break;
            case "mapLineId2LineAvgSpeed":
                mapLineId2LineAvgSpeed = JSON.parseObject(value, HashMap.class);
                break;
            case "mapLineId2TimeAvgSpeed":
                mapLineId2TimeAvgSpeed = JSON.parseObject(value, HashMap.class);
                break;
            case "mapLineId2NextStopTime":
                mapLineId2NextStopTime = JSON.parseObject(value, HashMap.class);
                break;
        }
        return this;
    }

    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("workDate");
        Fields.add("halfHour");
        Fields.add("cityCode");
        Fields.add("max_speed_line_name");
        Fields.add("max_speed");
        Fields.add("min_speed_line_name");
        Fields.add("min_speed");
        Fields.add("cityAvgSpeed");
        Fields.add("timeAvgSpeed");
        Fields.add("toNextStopTime");
        Fields.add("mapLineId2LineAvgSpeed");
        Fields.add("mapLineId2TimeAvgSpeed");
        Fields.add("mapLineId2NextStopTime");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
