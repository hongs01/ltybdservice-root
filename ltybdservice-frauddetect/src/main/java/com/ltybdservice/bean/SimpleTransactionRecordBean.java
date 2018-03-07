package com.ltybdservice.bean;

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
public class SimpleTransactionRecordBean implements IEvent, Serializable {
    private int dev_id;
    private int tdc_flag;
    private String v_card_code;
    private long up_time;
    private String verify_code;
    private int bus_station_code;
    private int bus_station_no;
    private int line_id;
    private String driver_id;
    private long vehicle_id;
    private int bus_req;
    private int onbus_flag;
    private boolean ondirection_flag;
    private float lon;
    private float lat;

    public static SimpleTransactionRecordBean transactionRecordBean2SimpleTransactionRecordBean(TransactionRecordBean src) {
        SimpleTransactionRecordBean dest = new SimpleTransactionRecordBean();
        TransactionRecordBean.Body body = src.getBody();
        dest.dev_id = body.getDev_id();
        dest.tdc_flag= body.getTdc_flag();
        dest.v_card_code= body.getV_card_code();
        dest.up_time= body.getUp_time();
        dest.verify_code= body.getVerify_code();
        dest.bus_station_code= body.getBus_station_code();
        dest.bus_station_no= body.getBus_station_no();
        dest.line_id= body.getLine_id();
        dest.driver_id= body.getDriver_id();
        dest.vehicle_id= body.getVehicle_id();
        dest.bus_req= body.getBus_req();
        dest.onbus_flag= body.getOnbus_flag();
        dest.ondirection_flag= body.isOndirection_flag();
        dest.lon=body.getGps_data().getLon();
        dest.lat=body.getGps_data().getLat();
        return dest;
    }

    public int getDev_id() {
        return dev_id;
    }

    public void setDev_id(int dev_id) {
        this.dev_id = dev_id;
    }

    public int getTdc_flag() {
        return tdc_flag;
    }

    public void setTdc_flag(int tdc_flag) {
        this.tdc_flag = tdc_flag;
    }

    public String getV_card_code() {
        return v_card_code;
    }

    public void setV_card_code(String v_card_code) {
        this.v_card_code = v_card_code;
    }

    public long getUp_time() {
        return up_time;
    }

    public void setUp_time(long up_time) {
        this.up_time = up_time;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public int getBus_station_code() {
        return bus_station_code;
    }

    public void setBus_station_code(int bus_station_code) {
        this.bus_station_code = bus_station_code;
    }

    public int getBus_station_no() {
        return bus_station_no;
    }

    public void setBus_station_no(int bus_station_no) {
        this.bus_station_no = bus_station_no;
    }

    public int getLine_id() {
        return line_id;
    }

    public void setLine_id(int line_id) {
        this.line_id = line_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getBus_req() {
        return bus_req;
    }

    public void setBus_req(int bus_req) {
        this.bus_req = bus_req;
    }

    public int getOnbus_flag() {
        return onbus_flag;
    }

    public void setOnbus_flag(int onbus_flag) {
        this.onbus_flag = onbus_flag;
    }

    public boolean isOndirection_flag() {
        return ondirection_flag;
    }

    public void setOndirection_flag(boolean ondirection_flag) {
        this.ondirection_flag = ondirection_flag;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    @Override
    public String getValueByField(String field) {
        String string = null;
        switch (field) {
            case "dev_id":
                string = String.valueOf(dev_id);
                break;
            case "tdc_flag":
                string = String.valueOf(tdc_flag);
                break;
            case "v_card_code":
                string = v_card_code;
                break;
            case "up_time":
                string = String.valueOf(up_time);
                break;
            case "verify_code":
                string = verify_code;
                break;
            case "bus_station_code":
                string = String.valueOf(bus_station_code);
                break;
            case "bus_station_no":
                string = String.valueOf(bus_station_no);
                break;
            case "line_id":
                string = String.valueOf(line_id);
                break;
            case "driver_id":
                string = driver_id;
                break;
            case "vehicle_id":
                string = String.valueOf(vehicle_id);
                break;
            case "bus_req":
                string = String.valueOf(bus_req);
                break;
            case "onbus_flag":
                string = String.valueOf(onbus_flag);
                break;
            case "ondirection_flag":
                string = String.valueOf(ondirection_flag);
                break;
            case "lon":
                string = String.valueOf(lon);
                break;
            case "lat":
                string = String.valueOf(lat);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "dev_id":
                dev_id = Integer.parseInt(value);
                break;
            case "tdc_flag":
                tdc_flag = Integer.parseInt(value);
                break;
            case "v_card_code":
                v_card_code = value;
                break;
            case "up_time":
                up_time = Long.parseLong(value);
                break;
            case "verify_code":
                verify_code = value;
                break;
            case "bus_station_code":
                bus_station_code = Integer.parseInt(value);
                break;
            case "bus_station_no":
                bus_station_no = Integer.parseInt(value);
                break;
            case "line_id":
                line_id = Integer.parseInt(value);
                break;
            case "driver_id":
                driver_id = value;
                break;
            case "vehicle_id":
                vehicle_id = Long.parseLong(value);
                break;
            case "bus_req":
                bus_req = Integer.parseInt(value);
                break;
            case "onbus_flag":
                onbus_flag = Integer.parseInt(value);
                break;
            case "ondirection_flag":
                ondirection_flag = Boolean.parseBoolean(value);
                break;
            case "lon":
                lon = Float.parseFloat(value);
                break;
            case "lat":
                lat = Float.parseFloat(value);
                break;
        }
        return this;
    }


    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("dev_id");
        Fields.add("tdc_flag");
        Fields.add("v_card_code");
        Fields.add("up_time");
        Fields.add("verify_code");
        Fields.add("bus_station_code");
        Fields.add("bus_station_no");
        Fields.add("line_id");
        Fields.add("driver_id");
        Fields.add("vehicle_id");
        Fields.add("bus_req");
        Fields.add("onbus_flag");
        Fields.add("ondirection_flag");
        Fields.add("lon");
        Fields.add("lat");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
