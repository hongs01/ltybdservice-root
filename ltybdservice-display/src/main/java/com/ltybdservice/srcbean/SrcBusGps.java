package com.ltybdservice.srcbean;

import java.io.Serializable;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 实例：
 * {
 * "header":{
 * "msg_flag":4,
 * "msg_sn":0,
 * "msg_id":4101
 * },
 * "body":{
 * "city_code":"13040000",
 * "dev_id":5120,
 * "line_id":21,
 * "direction":0,
 * "vehicle_id":12345,
 * "gps_time":1502094772,
 * "lon":123.123441,
 * "lat":23.123424,
 * "vec1":0,
 * "vec2":0,
 * "distance":0,
 * "angle":0,
 * "altitude":0,
 * "dis_next":747,
 * "time_next":1792,
 * "next_station_no":2,
 * "signal":32,
 * "temperature":24
 * }
 * }
 */


public class SrcBusGps implements Serializable {
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

    public static class Header implements Serializable {
        private int msg_flag;//UINT	消息标志，0x01:住建部；0x02:交通部，0x03:LTY；0x04:其它
        private int msg_sn;//UINT	报文序列号
        private int msg_id;//UINT	业务消息类型

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

    public static class Body implements Serializable {
        private int dev_id;//UINT	设备ID
        private String city_code;//	String	城市编号
        private int line_id;//UINT	线路ID
        private int direction;//	UINT	0,上行；1，下行；
        private int vehicle_id;//UINT	车辆编号
        private long gps_time;//ULONG	GPS时间 10位
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

        public int getDev_id() {
            return dev_id;
        }

        public void setDev_id(int dev_id) {
            this.dev_id = dev_id;
        }

        public String getCity_code() {
            return city_code;
        }

        public void setCity_code(String city_code) {
            this.city_code = city_code;
        }

        public int getLine_id() {
            return line_id;
        }

        public void setLine_id(int line_id) {
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
}
