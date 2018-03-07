package com.ltybdservice.pre.prebean;

import com.ltybdservice.hbaseutil.IEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 需要聚合值：
 * 平均速度：timeAvgSpeed
 */
public class BusGps implements IEvent, Serializable {
    //坐标字段
    private long time;//事件时间,13位
    private String workDate;//日,
    private String halfHour;//半小时
    private String cityCode;//城市id，
    private int busId;//公交车ID（设备id，原始数据中的terminalNo）
    private int lineId;   //线路id
    private String lineName;   //线路名
    //信息字段
    private int busSpeed;//时速
    private int toNextStopTime;//距下一站时间
    private double latitude;//纬度,
    private double longitude;//经度,
    private int direction;//方向
    private int angle;//方位角,
    //聚合字段
    private int timeAvgSpeed;   //平均速度，新增字段，需要聚合的数据
    private int count;//用于计算平均速度的计数器

    public void getBaseBusGps(RawBusGps rawBusGps) {
        this.cityCode = rawBusGps.getCityCode();
        this.busId = rawBusGps.getBusId();
        this.lineId = rawBusGps.getLineId();
        this.lineName = rawBusGps.getLineName();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHalfHour() {
        return halfHour;
    }

    public void setHalfHour(String halfHour) {
        this.halfHour = halfHour;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
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

    public int getBusSpeed() {
        return busSpeed;
    }

    public void setBusSpeed(int busSpeed) {
        this.busSpeed = busSpeed;
    }

    public int getToNextStopTime() {
        return toNextStopTime;
    }

    public void setToNextStopTime(int toNextStopTime) {
        this.toNextStopTime = toNextStopTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getTimeAvgSpeed() {
        return timeAvgSpeed;
    }

    public void setTimeAvgSpeed(int timeAvgSpeed) {
        this.timeAvgSpeed = timeAvgSpeed;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String getValueByField(String field) {
        String string = null;
        switch (field) {
            case "time":
                string = String.valueOf(time);
                break;
            case "halfHour":
                string = halfHour;
                break;
            case "workDate":
                string = workDate;
                break;
            case "cityCode":
                string = cityCode;
                break;
            case "busId":
                string = String.valueOf(busId);
                break;
            case "lineId":
                string = String.valueOf(lineId);
                break;
            case "lineName":
                string = lineName;
                break;
            case "busSpeed":
                string = String.valueOf(busSpeed);
                break;
            case "toNextStopTime":
                string = String.valueOf(toNextStopTime);
                break;
            case "latitude":
                string = String.valueOf(latitude);
                break;
            case "longitude":
                string = String.valueOf(longitude);
                break;
            case "direction":
                string = String.valueOf(direction);
                break;
            case "angle":
                string = String.valueOf(angle);
                break;
            case "timeAvgSpeed":
                string = String.valueOf(timeAvgSpeed);
                break;
            case "count":
                string = String.valueOf(count);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "time":
                time = Long.parseLong(value);
                break;
            case "halfHour":
                halfHour = value;
                break;
            case "workDate":
                workDate = value;
                break;
            case "cityCode":
                cityCode = value;
                break;
            case "busId":
                busId = Integer.parseInt(value);
                break;
            case "lineId":
                lineId = Integer.parseInt(value);
                break;
            case "lineName":
                lineName = value;
                break;
            case "busSpeed":
                busSpeed = Integer.parseInt(value);
                break;
            case "toNextStopTime":
                toNextStopTime = Integer.parseInt(value);
                break;
            case "latitude":
                latitude = Double.parseDouble(value);
                break;
            case "longitude":
                longitude = Double.parseDouble(value);
                break;
            case "direction":
                direction = Integer.parseInt(value);
                break;
            case "angle":
                angle = Integer.parseInt(value);
                break;
            case "timeAvgSpeed":
                timeAvgSpeed = Integer.parseInt(value);
                break;
            case "count":
                count = Integer.parseInt(value);
                break;
        }
        return this;
    }

    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("time");
        Fields.add("halfHour");
        Fields.add("workDate");
        Fields.add("cityCode");
        Fields.add("busId");
        Fields.add("lineId");
        Fields.add("lineName");
        Fields.add("busSpeed");
        Fields.add("toNextStopTime");
        Fields.add("latitude");
        Fields.add("longitude");
        Fields.add("direction");
        Fields.add("angle");
        Fields.add("timeAvgSpeed");
        Fields.add("count");

        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
