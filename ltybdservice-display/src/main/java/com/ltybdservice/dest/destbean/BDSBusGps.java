package com.ltybdservice.dest.destbean;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.pre.prebean.BusGps;
import com.ltybdservice.pre.prebean.BusPsg;
import com.ltybdservice.hbaseutil.IEvent;
import com.ltybdservice.hiveutil.DataSourceConf;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class BDSBusGps implements IEvent, Serializable {
    private int type;
    private List<BDSBusGpsData> data = new ArrayList<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BDSBusGpsData> getData() {
        return data;
    }

    public void setData(List<BDSBusGpsData> data) {
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
                data = JSON.parseArray(value,BDSBusGpsData.class);
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

    public static class BDSBusGpsData implements  Serializable{
        private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private String cityCode;//	城市id
        private int bus_no;// 车辆编号
        private String line_id;// 线路编号
        private String line_name;// 线路名称
        private int speed;// 当前时速
        private int direction;// 上下 下行
        private int total_person;// 车上人数
        private double latitude;// 23.043155,
        private double longitude;// 113.171841,
        private String formatTime;// "2017-08-09 07:35:19.000",
        private double runAngle;//偏向角

        public static BDSBusGpsData GpsPsg2GpsUpBean(DataSourceConf conf, BusGps busGps, BusPsg busPsg) {
            BDSBusGpsData busGpsUpDataBean = new BDSBusGpsData();
            busGpsUpDataBean.cityCode = busGps.getCityCode();
            busGpsUpDataBean.bus_no = busGps.getBusId();
            busGpsUpDataBean.line_id = String.valueOf(busGps.getLineId());
            busGpsUpDataBean.line_name = busGps.getLineName();
            busGpsUpDataBean.speed = busGps.getBusSpeed();
            busGpsUpDataBean.direction = busGps.getDirection();
            busGpsUpDataBean.total_person = busPsg.getInBusPsg();
            busGpsUpDataBean.latitude = busGps.getLatitude();
            busGpsUpDataBean.longitude = busGps.getLongitude();
            busGpsUpDataBean.formatTime = formatter.format(busGps.getTime());
            busGpsUpDataBean.runAngle = busGps.getAngle();
            return busGpsUpDataBean;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public int getBus_no() {
            return bus_no;
        }

        public void setBus_no(int bus_no) {
            this.bus_no = bus_no;
        }

        public String getLine_id() {
            return line_id;
        }

        public void setLine_id(String line_id) {
            this.line_id = line_id;
        }

        public String getLine_name() {
            return line_name;
        }

        public void setLine_name(String line_name) {
            this.line_name = line_name;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getTotal_person() {
            return total_person;
        }

        public void setTotal_person(int total_person) {
            this.total_person = total_person;
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

        public String getFormatTime() {
            return formatTime;
        }

        public void setFormatTime(String formatTime) {
            this.formatTime = formatTime;
        }

        public double getRunAngle() {
            return runAngle;
        }

        public void setRunAngle(double runAngle) {
            this.runAngle = runAngle;
        }

    }
}

