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
 * 总客流：totalFlow
 */
public class BusPsg implements IEvent, Serializable {
    //坐标字段
    private long time;//事件时间，13位
    private String halfHour;//半小时
    private String workDate;//日20170809
    private String cityCode;//城市id 6位
    private int busId;// 公交车ID（设备id）
    private int lineId;   //线路id
    private String lineName;   //线路名
    private int stationId;// UINT,城市公交站点编码
    private String stationName;    //	站点名称
    //信息字段
    private int upFlow;//UINT,本次上的客流
    private int inBusPsg;//UINT,车上总人数
    private double crowd;     //拥挤度
    private int vehicleId;// 车辆编号
    private double longitude;// double,经度
    private double latitude;// double,纬度
    //聚合字段
    private int totalFlow;//UINT,总客流，新增字段，需要聚合的值

    public void getBaseBusPsg(RawBusPsg rawBusPsg) {
        this.cityCode = rawBusPsg.getCityCode();
        this.busId = rawBusPsg.getBusId();
        this.lineId = rawBusPsg.getLineId();
        this.lineName = rawBusPsg.getLineName();
        this.stationId = rawBusPsg.getStationId();
        this.stationName = rawBusPsg.getStationName();
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

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(int upFlow) {
        this.upFlow = upFlow;
    }

    public int getInBusPsg() {
        return inBusPsg;
    }

    public void setInBusPsg(int inBusPsg) {
        this.inBusPsg = inBusPsg;
    }

    public double getCrowd() {
        return crowd;
    }

    public void setCrowd(double crowd) {
        this.crowd = crowd;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(int totalFlow) {
        this.totalFlow = totalFlow;
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
            case "stationId":
                string = String.valueOf(stationId);
                break;
            case "stationName":
                string = stationName;
                break;
            case "upFlow":
                string = String.valueOf(upFlow);
                break;
            case "inBusPsg":
                string = String.valueOf(inBusPsg);
                break;
            case "crowd":
                string = String.valueOf(crowd);
                break;
            case "vehicleId":
                string = String.valueOf(vehicleId);
                break;
            case "longitude":
                string = String.valueOf(longitude);
                break;
            case "latitude":
                string = String.valueOf(latitude);
                break;
            case "totalFlow":
                string = String.valueOf(totalFlow);
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
            case "stationId":
                stationId = Integer.parseInt(value);
                break;
            case "stationName":
                stationName = value;
                break;
            case "upFlow":
                upFlow = Integer.parseInt(value);
                break;
            case "inBusPsg":
                inBusPsg = Integer.parseInt(value);
                break;
            case "crowd":
                crowd = Double.parseDouble(value);
                break;
            case "vehicleId":
                vehicleId = Integer.parseInt(value);
                break;
            case "longitude":
                longitude = Double.parseDouble(value);
                break;
            case "latitude":
                latitude = Double.parseDouble(value);
                break;
            case "totalFlow":
                totalFlow = Integer.parseInt(value);
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
        Fields.add("stationId");
        Fields.add("stationName");
        Fields.add("upFlow");
        Fields.add("inBusPsg");
        Fields.add("crowd");
        Fields.add("vehicleId");
        Fields.add("longitude");
        Fields.add("latitude");
        Fields.add("totalFlow");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}