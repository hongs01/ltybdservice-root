package com.ltybdservice.pre.prebean;

import com.ltybdservice.srcbean.SrcBusGps;
import com.ltybdservice.hiveutil.DataSourceConf;
import com.ltybdservice.hiveutil.DcDataBaseUtil;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 新增字段
 */
public class RawBusGps implements Serializable {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    //坐标字段
    private long time;//事件时间,13位
    private String workDate;//日,
    private String cityCode;//城市id，
    private int busId;//公交车ID（设备id，原始数据中的terminalNo）
    private int lineId;   //线路id，新增字段
    private String lineName;   //线路名，新增字段
    //信息字段
    private int direction;//	UINT	0,上行；1，下行；
    private int vehicle_id;//UINT	车辆编号
    private double lon;//	double	经度
    private double lat;//	double	纬度
    private int vec1;//UINT	速度，指卫星定位车载终端设备上传的行车速度信息，为必填项。单位为千米每小时(km/h)。
    private int vec2;//UINT	行驶记录速度，指车辆行驶记录设备上传的行车速度信息。单位为千米每小时(km/h)。
    private int distance;//UINT	车辆当日总里程数，指车辆上传的行车里程数。单位为米(m)。终端设备每天开机时候清零
    private int angle;//UINT	方向，0-359，单位为度(°)，正北为0，顺时针。
    private int altitude;//UINT	海拔高度，单位为米(m)。
    private int dis_next;//UINT	距离下站距离，单位米（m）
    private int time_next;//UINT	距离下站的时间，单位秒（s）
    private int next_station_no;//UINT	下一个站点序号
    private int signal;//UINT	GPS强度范围0～32，12以下为信号弱
    private int temperature;//UINT	车内温度，单位为摄氏度


    public static RawBusGps transFromBusGps(DataSourceConf conf, SrcBusGps srcBusGps) {
        RawBusGps rawBusGps = new RawBusGps();
        SrcBusGps.Body body = srcBusGps.getBody();
        rawBusGps.time = body.getGps_time() * 1000;
        rawBusGps.workDate = formatter.format(new Date(rawBusGps.time));
        rawBusGps.cityCode = body.getCity_code();
        rawBusGps.busId = body.getDev_id();
        try {
            rawBusGps.lineId = DcDataBaseUtil.getDcConnectionInstance(conf).devId2lineId(rawBusGps.cityCode, rawBusGps.busId);
            rawBusGps.lineName = DcDataBaseUtil.getDcConnectionInstance(conf).lineId2lineName(rawBusGps.cityCode, rawBusGps.lineId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        rawBusGps.direction = body.getDirection();
        rawBusGps.vehicle_id = body.getVehicle_id();
        rawBusGps.lon = body.getLon();
        rawBusGps.lat = body.getLat();
        rawBusGps.vec1 = body.getVec1();
        rawBusGps.vec2 = body.getVec2();
        rawBusGps.direction = body.getDirection();
        rawBusGps.distance = body.getDistance();
        rawBusGps.angle = body.getAngle();
        rawBusGps.altitude = body.getAltitude();
        rawBusGps.dis_next = body.getDis_next();
        rawBusGps.time_next = body.getTime_next();
        rawBusGps.next_station_no = body.getNext_station_no();
        rawBusGps.signal = body.getSignal();
        rawBusGps.temperature = body.getTemperature();

        return rawBusGps;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
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

    public int getTime_next() {
        return time_next;
    }

    public void setTime_next(int time_next) {
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
