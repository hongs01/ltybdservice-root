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
public class LineGps implements IEvent, Serializable {
    //坐标字段
    private String workDate;//日,
    private String halfHour;//半小时
    private String cityCode;//城市id，
    private int lineId;
    private String lineName;
    //聚合字段
    private int lineAvgSpeed;
    private int timeAvgSpeed;
    private int toNextStopTime;//线路最新平均车辆距下一站时间
    //聚合缓存表
    private Map<Integer,Integer> mapBus2BusSpeed =new HashMap<>();          //最新车辆速度表
    private Map<Integer,Integer> mapBus2TimeAvgSpeed =new HashMap<>();
    private Map<Integer,Integer> mapBus2NextStopTime=new HashMap<>(); //最新车辆到下一站时间表

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

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public int getLineAvgSpeed() {
        return lineAvgSpeed;
    }

    public void setLineAvgSpeed(int lineAvgSpeed) {
        this.lineAvgSpeed = lineAvgSpeed;
    }

    public int getToNextStopTime() {
        return toNextStopTime;
    }

    public void setToNextStopTime(int toNextStopTime) {
        this.toNextStopTime = toNextStopTime;
    }

    public Map<Integer, Integer> getMapBus2BusSpeed() {
        return mapBus2BusSpeed;
    }

    public void setMapBus2BusSpeed(Map<Integer, Integer> mapBus2BusSpeed) {
        this.mapBus2BusSpeed = mapBus2BusSpeed;
    }

    public Map<Integer, Integer> getMapBus2NextStopTime() {
        return mapBus2NextStopTime;
    }

    public void setMapBus2NextStopTime(Map<Integer, Integer> mapBus2NextStopTime) {
        this.mapBus2NextStopTime = mapBus2NextStopTime;
    }

    public int getTimeAvgSpeed() {
        return timeAvgSpeed;
    }

    public void setTimeAvgSpeed(int timeAvgSpeed) {
        this.timeAvgSpeed = timeAvgSpeed;
    }

    public Map<Integer, Integer> getMapBus2TimeAvgSpeed() {
        return mapBus2TimeAvgSpeed;
    }

    public void setMapBus2TimeAvgSpeed(Map<Integer, Integer> mapBus2TimeAvgSpeed) {
        this.mapBus2TimeAvgSpeed = mapBus2TimeAvgSpeed;
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
            case "lineId":
                string = String.valueOf(lineId);
                break;
            case "lineName":
                string = lineName;
                break;
            case "lineAvgSpeed":
                string = String.valueOf(lineAvgSpeed);
                break;
            case "timeAvgSpeed":
                string = String.valueOf(timeAvgSpeed);
                break;
            case "toNextStopTime":
                string = String.valueOf(toNextStopTime);
                break;
            case "mapBus2BusSpeed":
                string = JSON.toJSONString(mapBus2BusSpeed);
                break;
            case "mapBus2TimeAvgSpeed":
                string = JSON.toJSONString(mapBus2TimeAvgSpeed);
                break;
            case "mapBus2NextStopTime":
                string = JSON.toJSONString(mapBus2NextStopTime);
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
            case "lineId":
                lineId = Integer.parseInt(value);
                break;
            case "lineName":
                lineName = value;
                break;
            case "lineAvgSpeed":
                lineAvgSpeed = Integer.parseInt(value);
                break;
            case "timeAvgSpeed":
                timeAvgSpeed = Integer.parseInt(value);
                break;
            case "toNextStopTime":
                toNextStopTime = Integer.parseInt(value);
                break;
            case "mapBus2BusSpeed":
                mapBus2BusSpeed = JSON.parseObject(value,HashMap.class);
                break;
            case "mapBus2TimeAvgSpeed":
                mapBus2TimeAvgSpeed = JSON.parseObject(value,HashMap.class);
                break;
            case "mapBus2NextStopTime":
                mapBus2NextStopTime = JSON.parseObject(value,HashMap.class);
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
        Fields.add("lineId");
        Fields.add("lineName");
        Fields.add("lineAvgSpeed");
        Fields.add("timeAvgSpeed");
        Fields.add("toNextStopTime");
        Fields.add("mapBus2BusSpeed");
        Fields.add("mapBus2TimeAvgSpeed");
        Fields.add("mapBus2NextStopTime");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
