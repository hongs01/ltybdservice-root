package com.ltybdservice.dest.destbean;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.hbaseutil.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class BDSAllStationPsg implements IEvent, Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(BDSAllStationPsg.class);
    private int type;
    private List<BDSAllStationPsgData> data = new ArrayList<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BDSAllStationPsgData> getData() {
        return data;
    }

    public void setData(List<BDSAllStationPsgData> data) {
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
                data = JSON.parseArray(value,BDSAllStationPsgData.class);
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


    public static class BDSAllStationPsgData implements  Serializable{
        private String citycode;    //城市id
        private double latitude;    //纬度
        private double longitude;        //经度
        private String stationName;        //站点名称
        private String time;    //时间段(20:00~20:30)
        private int psg;    //站点的客流量（当前时间半小时内的客流量）

        public String getCitycode() {
            return citycode;
        }

        public void setCitycode(String citycode) {
            this.citycode = citycode;
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

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
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
    }
}
