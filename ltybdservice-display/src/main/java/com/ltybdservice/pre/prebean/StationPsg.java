package com.ltybdservice.pre.prebean;

import com.ltybdservice.hbaseutil.IEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class StationPsg implements IEvent, Serializable {
    //坐标字段
    private String workDate;//日,
    private String halfHour;//半小时
    private String cityCode;       //城市id
    private double latitude;	//	纬度
    private double longitude;	//	经度
    private int stationId;   //站点id
    private String stationName;	//	站点名称
    //聚合字段
    private int psg;  //	客流量

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

    public int getPsg() {
        return psg;
    }

    public void setPsg(int psg) {
        this.psg = psg;
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
            case "latitude":
                string = String.valueOf(latitude);
                break;
            case "longitude":
                string = String.valueOf(longitude);
                break;
            case "stationId":
                string = String.valueOf(stationId);
                break;
            case "stationName":
                string = stationName;
                break;
            case "psg":
                string = String.valueOf(psg);
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
            case "latitude":
                latitude = Double.parseDouble(value);
                break;
            case "longitude":
                longitude = Double.parseDouble(value);
                break;
            case "stationId":
                stationId = Integer.parseInt(value);
                break;
            case "stationName":
                stationName = value;
                break;
            case "psg":
                psg = Integer.parseInt(value);
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
        Fields.add("latitude");
        Fields.add("longitude");
        Fields.add("stationId");
        Fields.add("stationName");
        Fields.add("psg");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
