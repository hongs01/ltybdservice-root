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
public class BDSTimeCityGps implements IEvent, Serializable {
    private int type;//类型，类似header区分业务
    private List<BDSTimeCityGpsData> data = new ArrayList<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BDSTimeCityGpsData> getData() {
        return data;
    }

    public void setData(List<BDSTimeCityGpsData> data) {
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
                data = JSON.parseArray(value,BDSTimeCityGpsData.class);
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
    public static class BDSTimeCityGpsData implements  Serializable{
        private String citycode;//城市id
        private String time;//时间段（20:30）
        private double svg_speed;//时间段所有线路的平均运营速度

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

        public double getSvg_speed() {
            return svg_speed;
        }

        public void setSvg_speed(double svg_speed) {
            this.svg_speed = svg_speed;
        }
    }
}
