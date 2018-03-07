package com.lantaiyuan;

import com.alibaba.fastjson.JSONObject;
import com.lantaiyuan.bean.GW_APP;
import com.lantaiyuan.bean.INSTATION;
import com.lantaiyuan.bean.OUTSTATION;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlData {

    protected Connection conn=getConnection();

    //缓存mysql发车站经纬度数据
    public Map<String,String> lonlatMap=getAlllonlatMap();
    //缓存mysql到站时间数据
    public Map<String,String> lonlatMap_end=getAlltimeMap_end();
    //缓存mysql线路方向最大站序
    public  Map<String,String> MaxOrderMap=getMaxOrderMap();

    public MysqlData() throws SQLException {
    }

    //获取mysql连接
    public Connection getConnection() throws SQLException {
        //加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Map<String,String> map=new HashMap<>();
            map.put("url","jdbc:mysql://10.1.254.21:3306/ebus-test");
            map.put("user","liuwei");
            map.put("password","123456");
            String url=map.get("url");
            String user=map.get("user");
            String password=map.get("password");
            //加载驱动类
            conn = DriverManager.getConnection(url,
                    user, password);
            //获取与目标数据库的连接，参数（"jdbc:mysql://localhost/数据库名"，"root"，"数据库密码"；
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    //获取发车经纬度缓存
    public Map<String,String> getAlllonlatMap() throws SQLException {
        ResultSet set=null;
        PreparedStatement prepar=conn.prepareStatement("select * from rel_route_station a,base_station b where a.stationNo=1\n" +
                "and a.stationId =b.stationId;");

        set=prepar.executeQuery();
        //将得到的数据库响应的查询结果存放在ResultSet对象中
        Map<String,String>  returnMap=new HashMap<String, String>();
        while(set.next())
        {
            String key=set.getString("cityCode")+"_"+set.getString("routeId")+"_"+ set.getString("direction");
            String value=set.getString("longitude")+"_"+set.getString("latitude");
            returnMap.put(key,value);
        }
        return returnMap;
    }

    //获取到达站点经纬度缓存
    public Map<String,String> getAlltimeMap_end() throws SQLException {
        ResultSet set=null;

        PreparedStatement prepar=conn.prepareStatement("select * from \n" +
                "(\n" +
                "select distinct a.cityCode, routeId,direction,stationNo,b.latitude,b.longitude from rel_route_station a,base_station b where \n" +
                " a.stationId =b.stationId and a.cityCode=b.cityCode\n" +
                ") a\n" +
                "inner JOIN\n" +
                "(\n" +
                "select  a.cityCode,routeId,direction,max(stationNo) stationNo from rel_route_station a,base_station b where \n" +
                " a.stationId =b.stationId and a.cityCode=b.cityCode group by cityCode,routeid,direction\n" +
                ") b\n" +
                "on a.cityCode=b.cityCode  and a.routeId = b.routeId and a.direction = b.direction and a.stationNo = b.stationNo");
        set=prepar.executeQuery();
        //将得到的数据库响应的查询结果存放在ResultSet对象中
        Map<String,String>  returnMap=new HashMap<String, String>();
        while(set.next())
        {
            String key=set.getString("cityCode")+"_"+set.getString("routeId")+"_"+ set.getString("direction");
            String value=set.getString("longitude")+"_"+set.getString("latitude");
            returnMap.put(key,value);
        }
        return returnMap;
    }

    //获取线路最大站序Map
    public Map<String,String> getMaxOrderMap() throws SQLException {
        ResultSet set=null;

        PreparedStatement prepar=conn.prepareStatement("select * from \n" +
                "(\n" +
                "select distinct a.cityCode, routeId,direction,stationNo,b.latitude,b.longitude from rel_route_station a,base_station b where \n" +
                " a.stationId =b.stationId and a.cityCode=b.cityCode\n" +
                ") a\n" +
                "inner JOIN\n" +
                "(\n" +
                "select  a.cityCode,routeId,direction,max(stationNo) stationNo from rel_route_station a,base_station b where \n" +
                " a.stationId =b.stationId and a.cityCode=b.cityCode group by cityCode,routeid,direction\n" +
                ") b\n" +
                "on a.cityCode=b.cityCode  and a.routeId = b.routeId and a.direction = b.direction and a.stationNo = b.stationNo");
        set=prepar.executeQuery();
        //将得到的数据库响应的查询结果存放在ResultSet对象中
        Map<String,String>  returnMap=new HashMap<String, String>();
        while(set.next())
        {
            String key=set.getString("cityCode")+"_"+set.getString("routeId")+"_"+ set.getString("direction");
            String value=set.getString("stationNo");
            returnMap.put(key,value);
        }
        return returnMap;
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

    public List<ScalaMain.Coordinate> getSubList(List<ScalaMain.Coordinate> list){
        if(list.size()<=19){
            return list;
        }else{
            return list.subList(0,19);
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

    public static Map<String,Integer> getCarConfig(){
        Map<String,Integer> map = new HashMap<String, Integer>();
        map.put("car.distance",200);
        return map;
    }
}
