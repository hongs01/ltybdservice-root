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
public class CityPsg implements IEvent, Serializable {
    private String workDate;//日,
    private String halfHour;//半小时
    private String cityCode;//城市id
    private int totalUpFlow;//最新城市当天累计上车人数
    private double crowd;         //总座位数
    private int forecastPsg;      //预测客流
    private Map<Integer,Integer> mapLineId2TotalUpFlow=new HashMap<>();  //最新线路累计上车人数表
    private Map<Integer,Double> mapLineId2Crowd=new HashMap<>();  //最新线路座位数表
    private Map<Integer,Integer> mapLineId2ForecastPsg=new HashMap<>();

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

    public int getTotalUpFlow() {
        return totalUpFlow;
    }

    public void setTotalUpFlow(int totalUpFlow) {
        this.totalUpFlow = totalUpFlow;
    }

    public double getCrowd() {
        return crowd;
    }

    public void setCrowd(double crowd) {
        this.crowd = crowd;
    }

    public int getForecastPsg() {
        return forecastPsg;
    }

    public void setForecastPsg(int forecastPsg) {
        this.forecastPsg = forecastPsg;
    }

    public Map<Integer, Integer> getMapLineId2TotalUpFlow() {
        return mapLineId2TotalUpFlow;
    }

    public void setMapLineId2TotalUpFlow(Map<Integer, Integer> mapLineId2TotalUpFlow) {
        this.mapLineId2TotalUpFlow = mapLineId2TotalUpFlow;
    }

    public Map<Integer, Double> getMapLineId2Crowd() {
        return mapLineId2Crowd;
    }

    public void setMapLineId2Crowd(Map<Integer, Double> mapLineId2Crowd) {
        this.mapLineId2Crowd = mapLineId2Crowd;
    }

    public Map<Integer, Integer> getMapLineId2ForecastPsg() {
        return mapLineId2ForecastPsg;
    }

    public void setMapLineId2ForecastPsg(Map<Integer, Integer> mapLineId2ForecastPsg) {
        this.mapLineId2ForecastPsg = mapLineId2ForecastPsg;
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
            case "totalUpFlow":
                string = String.valueOf(totalUpFlow);
                break;
            case "crowd":
                string = String.valueOf(crowd);
                break;
            case "forecastPsg":
                string =  String.valueOf(forecastPsg);
                break;
            case "mapLineId2TotalUpFlow":
                string = JSON.toJSONString(mapLineId2TotalUpFlow);
                break;
            case "mapLineId2Crowd":
                string = JSON.toJSONString(mapLineId2Crowd);
                break;
            case "mapLineId2ForecastPsg":
                string = JSON.toJSONString(mapLineId2ForecastPsg);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "workDate":
                workDate =value;
                break;
            case "halfHour":
                halfHour = value;
                break;
            case "cityCode":
                cityCode = value;
                break;
            case "totalUpFlow":
                totalUpFlow = Integer.parseInt(value);
                break;
            case "crowd":
                crowd = Double.parseDouble(value);
                break;
            case "forecastPsg":
                forecastPsg = Integer.parseInt(value);
                break;
            case "mapLineId2TotalUpFlow":
                mapLineId2TotalUpFlow = JSON.parseObject(value,HashMap.class);
                break;
            case "mapLineId2Crowd":
                mapLineId2Crowd = JSON.parseObject(value,HashMap.class);
                break;
            case "mapLineId2ForecastPsg":
                mapLineId2ForecastPsg = JSON.parseObject(value,HashMap.class);
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
        Fields.add("totalUpFlow");
        Fields.add("crowd");
        Fields.add("forecastPsg");
        Fields.add("mapLineId2TotalUpFlow");
        Fields.add("mapLineId2Crowd");
        Fields.add("mapLineId2ForecastPsg");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
