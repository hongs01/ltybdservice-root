package com.ltybdservice.dest.destbean;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.hbaseutil.IEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class BDSTimeCityPsg implements IEvent, Serializable {
    private int type;
    private List<BDSTimeCityPsgData> data = new ArrayList<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BDSTimeCityPsgData> getData() {
        return data;
    }

    public void setData(List<BDSTimeCityPsgData> data) {
        this.data = data;
    }

    @Override
    public String getValueByField(String field) {
        String string = null;
        switch (field) {
            case "type":
                string = String.valueOf(type);
                break;
            case "data":
                string = JSON.toJSONString(data);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "type":
                type = Integer.parseInt(value);
                break;
            case "data":
                data = JSON.parseArray(value,BDSTimeCityPsgData.class);
                break;
        }
        return this;
    }

    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("type");
        Fields.add("data");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }

    public static class BDSTimeCityPsgData implements  Serializable{
        private String citycode;//	城市id
        private String time;//	时间段(20:00~20:30)
        private int psg;// 	城市时间段客流量
        private int forecast_psg;//预测时间段客流量
        private double congestion_satisfaction;//	时间段拥挤满意度(满载率) 0~1之间
        private double waite_satisfaction;//	时间段候车满满意度 0~1之间

        public String getCitycode() {
            return citycode;
        }

        public void setCitycode(String citycode) {
            this.citycode = citycode;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getPsg() {
            return psg;
        }

        public void setPsg(int psg) {
            this.psg = psg;
        }

        public int getForecast_psg() {
            return forecast_psg;
        }

        public void setForecast_psg(int forecast_psg) {
            this.forecast_psg = forecast_psg;
        }

        public double getCongestion_satisfaction() {
            return congestion_satisfaction;
        }

        public void setCongestion_satisfaction(double congestion_satisfaction) {
            this.congestion_satisfaction = congestion_satisfaction;
        }

        public double getWaite_satisfaction() {
            return waite_satisfaction;
        }

        public void setWaite_satisfaction(double waite_satisfaction) {
            this.waite_satisfaction = waite_satisfaction;
        }
    }
}
