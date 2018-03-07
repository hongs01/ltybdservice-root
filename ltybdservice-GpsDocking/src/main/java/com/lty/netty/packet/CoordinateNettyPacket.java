package com.lty.netty.packet;


import com.lty.cacheMap.cacheImp.TerminalToPhoneMap;
import com.lty.common.util.BCD8421Operater;
import com.lty.common.util.BitOperator;
import com.lty.common.util.NettyUtil;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * netty 转发
 * @author zhouyongbo 2017/12/08
 */
public class CoordinateNettyPacket {


    private String lonlat;
    private String Accuracy;
    private float altitude;
    private int angle;
    private long gpsTime;
    private float speed;
    private int gpsCount=0;
    private int gpsSum=0;
    private int lineId;
    private String busNo;
    private String cityCode;

    public CoordinateNettyPacket(String lonlat, String accuracy, float altitude, int angle, long gpsTime, float speed, int gpsCount, int gpsSum, int lineId, String busNo, String cityCode) {
        this.lonlat = lonlat;
        Accuracy = accuracy;
        this.altitude = altitude;
        this.angle = angle;
        this.gpsTime = gpsTime;
        this.speed = speed;
        this.gpsCount = gpsCount;
        this.gpsSum = gpsSum;
        this.lineId = lineId;
        this.busNo = busNo;
        this.cityCode = cityCode;
    }

    public String getLonlat() {
        return lonlat;
    }

    public void setLonlat(String lonlat) {
        this.lonlat = lonlat;
    }

    public String getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(String accuracy) {
        Accuracy = accuracy;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public long getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(long gpsTime) {
        this.gpsTime = gpsTime;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getGpsCount() {
        return gpsCount;
    }

    public void setGpsCount(int gpsCount) {
        this.gpsCount = gpsCount;
    }

    public int getGpsSum() {
        return gpsSum;
    }

    public void setGpsSum(int gpsSum) {
        this.gpsSum = gpsSum;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("GPS={\"lonlat\":").append("\"").append(lonlat).append("\"").append(",\"Accuracy\":").append("\"").append(Accuracy).append("\"").append(",\"altitude\":").append(altitude).append(",\"angle\":").append(angle).append(",\"gps_time\":").append(gpsTime).append(",\"speed\":").append(speed).append(",\"gps_count\":").append(gpsCount).append(",\"gps_sum\":").append(gpsSum).append(",\"line_id\":").append(lineId).append(",\"bus_no\":").append("\"").append(busNo).append("\"").append(",\"city_code\":").append("\"").append(cityCode).append("\"").append("}");
        return sb.toString();
    }



//    @Override
//    public String toString() {
//        return "CoordinateNettyPacket{" +
//                "informationId="+getInformationId()+
//                "informationAttribute="+getInformationAttribute()+
//                "cellPhone="+getCellPhone()+
//                "informationNumber="+getInformationNumber()+
//                "police=" + police +
//                ", busStatus=" + busStatus +
//                ", latitude=" + latitude +
//                ", longitude=" + longitude +
//                ", height=" + height +
//                ", kph=" + kph +
//                ", azimuth=" + azimuth +
//                ", time=" + time +
//                '}';
//    }
}
