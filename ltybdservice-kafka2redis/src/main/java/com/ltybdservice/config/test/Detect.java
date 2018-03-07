package com.ltybdservice.config.test;

import com.ltybdservice.commonutil.DistanceUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/12/25$ 9:12$
 * @description:
 */
public class Detect {
    /**
    人车最小分离距离
     */
    final static  double minDistance=50;
    /**
    用户，车辆最小可用数据个数
     */
    final static int minSize=3;
    final static int maxSize=10;
    /**
     * 人车计算最大间隔时间
     */
    final static int maxSeconds=3;
    static boolean detect(ArrayList<BusInfo> busList,ArrayList<UserInfo> userList){
        if(userList.size()<minSize||busList.size()<maxSize){
            return false;
        }
        //按时间降序排列
        busList.sort((o1, o2) -> (int)(o2.getEventTime() - o1.getEventTime()));
        userList.sort((o1, o2) -> (int)(o2.getEventTime() - o1.getEventTime()));
        UserInfo user0=userList.get(0);
        UserInfo user1=userList.get(1);
        UserInfo user2=userList.get(2);
        boolean flag=false;
        for(int j=0;j<maxSize-minSize;j++){
            BusInfo bus0=busList.get(j);
            BusInfo bus1=busList.get(j+1);
            BusInfo bus2=busList.get(j+2);
            double d0 = DistanceUtil.getDistance(user0.getLongitude(),user0.getLatitude(),bus0.getLongitude(),bus0.getLatitude());
            double d1 = DistanceUtil.getDistance(user1.getLongitude(),user1.getLatitude(),bus1.getLongitude(),bus1.getLatitude());
            double d2 = DistanceUtil.getDistance(user2.getLongitude(),user2.getLatitude(),bus1.getLongitude(),bus2.getLatitude());
            if(d0>minDistance&&d1>minDistance &&d2>minDistance&&Math.abs(bus0.getEventTime()-user0.getEventTime())<maxSeconds&&Math.abs(bus1.getEventTime()-user1.getEventTime())<maxSeconds&&Math.abs(bus2.getEventTime()-user2.getEventTime())<maxSeconds){
                flag=true;
                break;
            }
        }
        if(flag==true){
            return true;
        }else {
            return false;
        }
    }


    public static void  main(String[] argv){
        /*ArrayList<BusInfo> busList=new ArrayList<BusInfo>();
        ArrayList<UserInfo> userList= new ArrayList<UserInfo>();
        for(int i=0;i<Detect.maxSize;i++){
            BusInfo bus = new BusInfo("400800",11,234,123,32);
            busList.add(bus);
        }
        for(int i=0;i<Detect.minSize;i++){
            UserInfo user = new UserInfo("400800",12,234,123,34);
            userList.add(user);
        }
        boolean detect=Detect.detect(busList,userList);
        System.out.println("the detect result is :"+detect);*/

        Jedis jc = new Jedis("10.1.254.22", 6379);
        System.out.println(jc.llen("settle_busgps_130400_15615"));
    }
    static class BusInfo{
        private String cityCode;
        private int vehicleId;
        private long eventTime;
        private double longitude;
        private double latitude;

        public BusInfo(String cityCode, int vehicleId, long eventTime, double longitude, double latitude) {
            this.cityCode = cityCode;
            this.vehicleId = vehicleId;
            this.eventTime = eventTime;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public int getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(int vehicleId) {
            this.vehicleId = vehicleId;
        }

        public long getEventTime() {
            return eventTime;
        }

        public void setEventTime(long eventTime) {
            this.eventTime = eventTime;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
    static class UserInfo{
        private String cityCode;
        private int UserId;
        private long eventTime;
        private double longitude;
        private double latitude;

        public UserInfo(String cityCode, int userId, long eventTime, double longitude, double latitude) {
            this.cityCode = cityCode;
            UserId = userId;
            this.eventTime = eventTime;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public int getUserId() {
            return UserId;
        }

        public void setUserId(int userId) {
            UserId = userId;
        }

        public long getEventTime() {
            return eventTime;
        }

        public void setEventTime(long eventTime) {
            this.eventTime = eventTime;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
