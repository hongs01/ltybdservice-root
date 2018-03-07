package com.lty.netty.packet;


/**
 * netty 转发
 * @author zhouyongbo 2017/12/08
 */
public class BusNettyPacket {


    private String line_id;
    private String dev_id;
    private String lonlat;
    private String bus_station_no;
    private String next_station_no;
    private String city_code;
    private String Accuracy;
    private float altitude;
    private long in_time;

    public BusNettyPacket(String line_id, String dev_id, String lonlat, String bus_station_no, String next_station_no, String city_code, String accuracy, float altitude, long in_time) {
        this.line_id = line_id;
        this.dev_id = dev_id;
        this.lonlat = lonlat;
        this.bus_station_no = bus_station_no;
        this.next_station_no = next_station_no;
        this.city_code = city_code;
        Accuracy = accuracy;
        this.altitude = altitude;
        this.in_time = in_time;
    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public String getLonlat() {
        return lonlat;
    }

    public void setLonlat(String lonlat) {
        this.lonlat = lonlat;
    }

    public String getBus_station_no() {
        return bus_station_no;
    }

    public void setBus_station_no(String bus_station_no) {
        this.bus_station_no = bus_station_no;
    }

    public String getNext_station_no() {
        return next_station_no;
    }

    public void setNext_station_no(String next_station_no) {
        this.next_station_no = next_station_no;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
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

    public long getIn_time() {
        return in_time;
    }

    public void setIn_time(long in_time) {
        this.in_time = in_time;
    }

    @Override
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("bus={\"line_id\":").append("\"").append(line_id).append("\"").append(",\"bus_no\":").append("\"").append(dev_id).append("\"").append(",\"lonlat\":").append(lonlat).append(",\"bus_station\":").append(bus_station_no).append(",\"next_station\":").append(next_station_no).append(",\"city_code\":").append(city_code).append(",\"lonlat\":").append(lonlat).append(",\"Accuracy\":").append(Accuracy).append(",\"altitude\":").append(altitude).append(",\"in_time\":").append("\"").append(in_time).append("\"").append("}");
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
