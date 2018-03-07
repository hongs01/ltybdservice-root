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
public class LinePsg implements IEvent, Serializable {
    //坐标字段
    private String workDate;//日,
    private String halfHour;//半小时
    private String cityCode;//城市id
    private int lineId;
    private String lineName;
    //聚合字段
    private double crowd;  //线路拥挤度
    private int totalUpFlow;//线路最新当天累计上车人数
    //聚合缓存表
    private Map<Integer, Integer> mapBus2TotalUpFlow = new HashMap<>();  //最新车辆当天累计上车人数表
    private Map<Integer, Double> mapBus2Crowd = new HashMap<>();  //最新车辆座位数表

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

    public double getCrowd() {
        return crowd;
    }

    public void setCrowd(double crowd) {
        this.crowd = crowd;
    }

    public int getTotalUpFlow() {
        return totalUpFlow;
    }

    public void setTotalUpFlow(int totalUpFlow) {
        this.totalUpFlow = totalUpFlow;
    }

    public Map<Integer, Integer> getMapBus2TotalUpFlow() {
        return mapBus2TotalUpFlow;
    }

    public void setMapBus2TotalUpFlow(Map<Integer, Integer> mapBus2TotalUpFlow) {
        this.mapBus2TotalUpFlow = mapBus2TotalUpFlow;
    }

    public Map<Integer, Double> getMapBus2Crowd() {
        return mapBus2Crowd;
    }

    public void setMapBus2Crowd(Map<Integer, Double> mapBus2Crowd) {
        this.mapBus2Crowd = mapBus2Crowd;
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
            case "crowd":
                string = String.valueOf(crowd);
                break;
            case "totalUpFlow":
                string = String.valueOf(totalUpFlow);
                break;
            case "mapBus2TotalUpFlow":
                string = JSON.toJSONString(mapBus2TotalUpFlow);
                break;
            case "mapBus2Crowd":
                string = JSON.toJSONString(mapBus2Crowd);
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
            case "crowd":
                crowd = Double.parseDouble(value);
                break;
            case "totalUpFlow":
                totalUpFlow = Integer.parseInt(value);
                break;
            case "mapBus2TotalUpFlow":
                mapBus2TotalUpFlow = JSON.parseObject(value,HashMap.class);
                break;
            case "mapBus2Crowd":
                mapBus2Crowd = JSON.parseObject(value,HashMap.class);
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
        Fields.add("crowd");
        Fields.add("totalUpFlow");
        Fields.add("mapBus2TotalUpFlow");
        Fields.add("mapBus2Crowd");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
