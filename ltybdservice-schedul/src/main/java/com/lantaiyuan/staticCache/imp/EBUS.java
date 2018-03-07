package com.lantaiyuan.staticCache.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.bean.GW_APP;
import com.lantaiyuan.bean.INSTATION;
import com.lantaiyuan.bean.OUTSTATION;
import com.lantaiyuan.common.config.domain.Car;
import com.lantaiyuan.service.IAbstractService;
import com.lantaiyuan.service.mysql.EBUSMysqlService;
import com.lantaiyuan.start.imp.KafkaCounsumerAllCity;
import com.lantaiyuan.staticCache.IStaticCache;
import com.lantaiyuan.utils.DateUtil;
import kafka.utils.Json;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EBUS extends IStaticCache {

    //缓存mysql发车站经纬度数据
    public static Map<String,String> lonlatMap=new HashMap<String,String>();
    //缓存mysql到站时间数据
    public static Map<String,String> lonlatMap_end=new HashMap<String,String>();
    //缓存mysql线路方向最大站序
    public static Map<String,String> MaxOrderMap=new HashMap<String,String>();

    private EBUSMysqlService ebusmysqlservice;
    @Override
    public void init() throws SQLException {
        //从mysql取
        ebusmysqlservice= IAbstractService.getInstance(EBUSMysqlService.class);
    }

    @Override
    public void execute() throws SQLException {
        lonlatMap=ebusmysqlservice.getAlllonlatMap();
        lonlatMap_end= ebusmysqlservice.getAlltimeMap_end();
        MaxOrderMap=ebusmysqlservice.getMaxOrderMap();
    }

    public Map<String,String> getlonlatMap(){
        return lonlatMap;
    }

    public Map<String,String> getLonlatMap_end(){
        return lonlatMap_end;
    }

    /*获取线路上的发车时间*/
    public String getStartTimeByLidDir(String cityCode,int lineId,int direction,GW_APP gw_app){
        String key=cityCode+"_"+lineId+"_"+direction;
        String value=null;
        if (lonlatMap.keySet().contains(key)){
            value=lonlatMap.get(key);
            String lon=value.split("_")[0];
            String lat=value.split("_")[1];
            double distance=distanceByLongNLat(Double.parseDouble(lon),Double.parseDouble(lat),gw_app);
            Map<String,Integer> carMap=getCarConfig();
            if(distance<carMap.get("car.distance")){
                return gw_app.getEventTime();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }

    /*获取线路上的发车时间*/
    public String getStartTimeByLidDirG(String cityCode,int lineId,int direction,JSONObject json){
        String key=cityCode+"_"+lineId+"_"+direction;
        String value=null;
        if (lonlatMap.keySet().contains(key)){
            value=lonlatMap.get(key);
            String lon=value.split("_")[0];
            String lat=value.split("_")[1];
            double distance=distanceByLongNLat(Double.parseDouble(lon),Double.parseDouble(lat),json);
            Map<String,Integer> carMap=getCarConfig();
            if(distance<carMap.get("car.distance")){
                return json.get("gps_time").toString();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }

    //获取终点站到达时间
    public String getEndTimeByLidDir(String citycode,int lineId,int direction,GW_APP gw_app){
        String key=citycode+"_"+lineId+"_"+direction;
        String value=null;
        if (lonlatMap_end.keySet().contains(key)){
            value=lonlatMap_end.get(key);
            String lon=value.split("_")[0];
            String lat=value.split("_")[1];
            double distance=distanceByLongNLat(Double.parseDouble(lon),Double.parseDouble(lat),gw_app);
            Map<String,Integer> carMap=getCarConfig();
            if(distance<carMap.get("car.distance")){
                return gw_app.getEventTime();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }
    //获取终点站到达时间
    public String getEndTimeByLidDirG(String citycode,int lineId,int direction,JSONObject json){
        String key=citycode+"_"+lineId+"_"+direction;
        String value=null;
        if (lonlatMap_end.keySet().contains(key)){
            value=lonlatMap_end.get(key);
            String lon=value.split("_")[0];
            String lat=value.split("_")[1];
            double distance=distanceByLongNLat(Double.parseDouble(lon),Double.parseDouble(lat),json);
            Map<String,Integer> carMap=getCarConfig();
            if(distance<carMap.get("car.distance")){
                return json.get("gps_time").toString();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }

    //获取终点站到达时间(进站包)
    public String getEndTimeByLidDir1(String cityCode,int lineId,int direction,INSTATION instation){
        String key=cityCode+"_"+lineId+"_"+direction;
        String maxSno=null;
        if (MaxOrderMap.keySet().contains(key)){
            maxSno=MaxOrderMap.get(key);
            if(Integer.parseInt(maxSno)==instation.getCurrStationNo()){
                return instation.getEventTime();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }

    //获取终点站到达时间(出站包)
    public String getEndTimeByLidDir2(String cityCode,int lineId,int direction,OUTSTATION outstation){
        String key=cityCode+"_"+lineId+"_"+direction;
        String maxSno=null;
        if (MaxOrderMap.keySet().contains(key)){
            maxSno=MaxOrderMap.get(key);
            if(Integer.parseInt(maxSno)==outstation.getCurrStationNo()){
                return outstation.getInStationTime();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }


    //获取终点站到达时间(出站包)
    public String getEndTimeByLidDirNew(String cityCode,int lineId,int direction,JSONObject json){
        String key=cityCode+"_"+lineId+"_"+direction;
        String maxSno=null;
        if (MaxOrderMap.keySet().contains(key)){
            maxSno=MaxOrderMap.get(key);
            if(Integer.parseInt(maxSno)==Integer.parseInt(json.get("next_station_no").toString())){
                return json.get("gps_time").toString();
            }else {
                return "";
            }
        }else{
            return "";
        }
    }


    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @return 返回距离 单位：米
     */
    public double distanceByLongNLat(double long1, double lat1,GW_APP gw_app) {
        double a, b, R;
        double lon2=Double.parseDouble(gw_app.getLongitude());
        double lat2=Double.parseDouble(gw_app.getLatitude());
        R = 6378137;//地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - lon2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    public double distanceByLongNLat(double long1, double lat1,JSONObject json) {
        double a, b, R;
        double lon2=Double.parseDouble(json.get("lon").toString());
        double lat2=Double.parseDouble(json.get("lat").toString());
        R = 6378137;//地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - lon2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    public static Map<String, Integer> getCarConfig() {
        return Car.getCarConfig();
    }


    public List<KafkaCounsumerAllCity.Coordinate> getSubList(List<KafkaCounsumerAllCity.Coordinate> list){
        if(list.size()<=19){
            return list;
        }else{
            return list.subList(0,19);
        }
    }

}
