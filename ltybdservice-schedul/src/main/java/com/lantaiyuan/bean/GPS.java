package com.lantaiyuan.bean;

import java.io.Serializable;

public class GPS implements Serializable{
    //设备ID
    Integer dev_id;
    //城市编号
    String city_code;
    //线路ID
    Integer line_id;
    //0,上行；1，下行；
    int direction;
    //车辆编号
    int vehicle_id;
    //GPS时间
    long gps_time;
    //经度
    double lon;
    //纬度
    double lat;
    //速度，指卫星定位车载终端设备上传的行车速度信息，为必填项。单位为千米每小时(km/h)。
    int vec1;
    //行驶记录速度，指车辆行驶记录设备上传的行车速度信息。单位为千米每小时(km/h)。
    int vec2;
    //车辆当日总里程数，指车辆上传的行车里程数。单位为米(m)。终端设备每天开机时候清零
    int distance;
    //方向，0-359，单位为度(°)，正北为0，顺时针。
    int angle;
    //海拔高度，单位为米(m)。
    int altitude;
    //距离下站距离，单位米（m）
    int dis_next;
    //距离下站的时间，单位秒（s）
    Integer time_next;
    //下一个站点序号
    int next_station_no;
    //GPS强度范围0～32，12以下为信号弱
    int signal;
    //车内温度，单位为摄氏度
    int temperature;

    public Integer getDev_id() {
        return dev_id;
    }

    public void setDev_id(Integer dev_id) {
        this.dev_id = dev_id;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public Integer getLine_id() {
        return line_id;
    }

    public void setLine_id(Integer line_id) {
        this.line_id = line_id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public long getGps_time() {
        return gps_time;
    }

    public void setGps_time(long gps_time) {
        this.gps_time = gps_time;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getVec1() {
        return vec1;
    }

    public void setVec1(int vec1) {
        this.vec1 = vec1;
    }

    public int getVec2() {
        return vec2;
    }

    public void setVec2(int vec2) {
        this.vec2 = vec2;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getDis_next() {
        return dis_next;
    }

    public void setDis_next(int dis_next) {
        this.dis_next = dis_next;
    }

    public Integer getTime_next() {
        return time_next;
    }

    public void setTime_next(Integer time_next) {
        this.time_next = time_next;
    }

    public int getNext_station_no() {
        return next_station_no;
    }

    public void setNext_station_no(int next_station_no) {
        this.next_station_no = next_station_no;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
