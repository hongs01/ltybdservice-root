package com.ltybdservice.bean;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:实例
 * {"header":{"msg_flag":4,"msg_sn":9,"msg_id":458759},"body":{"dev_id":0,"tdc_flag":3,"v_card_code":"130400201000051","up_time":1505312208,"verify_code":"00000000000000000000",
 * "gps_data":{"gps_time":1505312208,"lon":0.000000,"lat":0.000000,"vec1":0,"vec2":0,"distance":0,"direction":0,"altitude":0},
 * "bus_station_code":9,"bus_station_no":10,"line_id":501,"driver_id":"0","vehicle_id":9999,"bus_req":0, "onbus_flag":1,"ondirection_flag":false}}
 */

public class TransactionRecordBean {
    private Header header;
    private Body body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Created by Administrator on 2017/9/5.
     */
    public static class GpsDataBean {
        private long gps_time;
        private float lon;
        private float lat;
        private int vec1;
        private int vec2;
        private float distance;
        private float direction;
        private float altitude;

        public long getGps_time() {
            return gps_time;
        }

        public void setGps_time(long gps_time) {
            this.gps_time = gps_time;
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

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public float getDirection() {
            return direction;
        }

        public void setDirection(float direction) {
            this.direction = direction;
        }

        public float getAltitude() {
            return altitude;
        }

        public void setAltitude(float altitude) {
            this.altitude = altitude;
        }
    }

    /**
     * Created by Administrator on 2017/9/12.
     */
    public static class Body {
        private int dev_id;
        private int tdc_flag;
        private String v_card_code;
        private long up_time;
        private String verify_code;
        private GpsDataBean gps_data;
        private int bus_station_code;
        private int bus_station_no;
        private int line_id;
        private String driver_id;
        private long vehicle_id;
        private int bus_req;
        private int onbus_flag;
        private boolean ondirection_flag;

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

        public GpsDataBean getGps_data() {
            return gps_data;
        }

        public void setGps_data(GpsDataBean gps_data) {
            this.gps_data = gps_data;
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
    }

    /**
     * Created by Administrator on 2017/9/12.
     */
    public static class Header {
        private int msg_flag;
        private int msg_sn;
        private int msg_id;

        public int getMsg_flag() {
            return msg_flag;
        }

        public void setMsg_flag(int msg_flag) {
            this.msg_flag = msg_flag;
        }

        public int getMsg_sn() {
            return msg_sn;
        }

        public void setMsg_sn(int msg_sn) {
            this.msg_sn = msg_sn;
        }

        public int getMsg_id() {
            return msg_id;
        }

        public void setMsg_id(int msg_id) {
            this.msg_id = msg_id;
        }
    }
}
