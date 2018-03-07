package com.ltybdservice.dest.destbean;

import com.alibaba.fastjson.JSON;
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
public class BDSCityGps implements IEvent, Serializable {
    private int type;
    private BDSCityGpsData data = new BDSCityGpsData();

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
                data = JSON.parseObject(value,BDSCityGpsData.class);
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

    public static class BDSCityGpsData implements  Serializable{
        private String cityCode;
        private String max_speed_line_name;
        private double max_speed;
        private String min_speed_line_name;
        private double min_speed;
        private double svg_speed;

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

        public double getMax_speed() {
            return max_speed;
        }

        public void setMax_speed(double max_speed) {
            this.max_speed = max_speed;
        }

        public String getMin_speed_line_name() {
            return min_speed_line_name;
        }

        public void setMin_speed_line_name(String min_speed_line_name) {
            this.min_speed_line_name = min_speed_line_name;
        }

        public double getMin_speed() {
            return min_speed;
        }

        public void setMin_speed(double min_speed) {
            this.min_speed = min_speed;
        }

        public double getSvg_speed() {
            return svg_speed;
        }

        public void setSvg_speed(double svg_speed) {
            this.svg_speed = svg_speed;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BDSCityGpsData getData() {
        return data;
    }

    public void setData(BDSCityGpsData data) {
        this.data = data;
    }
}
