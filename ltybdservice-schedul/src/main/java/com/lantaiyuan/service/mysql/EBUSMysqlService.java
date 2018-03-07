package com.lantaiyuan.service.mysql;

import com.lantaiyuan.common.config.domain.MysqlEbusConfig;
import com.lantaiyuan.service.IAbstractService;
import com.lantaiyuan.utils.DateUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EBUSMysqlService extends IAbstractService {

    /*在相同库中的话可以继承BaseService*/
    protected Connection conn=null;

    @Override
    public void init() throws SQLException {
        conn=getConnection();
    }
    //获取mysql连接
    public Connection getConnection() throws SQLException {
        //加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Map<String,String> map= MysqlEbusConfig.getConfig();
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
        /*PreparedStatement prepar=conn.prepareStatement("select * from rel_route_station a,base_station b where a.cityCode='130400' and a.stationNo=1\n" +
                "and a.stationId =b.stationId and b.cityCode='130400';");*/
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
        /*PreparedStatement prepar=conn.prepareStatement("select * from \n" +
                "(\n" +
                "select distinct routeId,direction,stationNo,b.latitude,b.longitude from rel_route_station a,base_station b where a.cityCode='130400' \n" +
                "and a.stationId =b.stationId and b.cityCode='130400' \n" +
                ") a\n" +
                "inner JOIN\n" +
                "(\n" +
                "select  routeId,direction,max(stationNo) stationNo from rel_route_station a,base_station b where a.cityCode='130400' \n" +
                "and a.stationId =b.stationId and b.cityCode='130400' group by routeid,direction\n" +
                ") b\n" +
                "on a.routeId = b.routeId and a.direction = b.direction and a.stationNo = b.stationNo;");*/
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
        /*PreparedStatement prepar=conn.prepareStatement("select * from \n" +
                "(\n" +
                "select distinct routeId,direction,stationNo,b.latitude,b.longitude from rel_route_station a,base_station b where a.cityCode='130400' \n" +
                "and a.stationId =b.stationId and b.cityCode='130400' \n" +
                ") a\n" +
                "inner JOIN\n" +
                "(\n" +
                "select  routeId,direction,max(stationNo) stationNo from rel_route_station a,base_station b where a.cityCode='130400' \n" +
                "and a.stationId =b.stationId and b.cityCode='130400' group by routeid,direction\n" +
                ") b\n" +
                "on a.routeId = b.routeId and a.direction = b.direction and a.stationNo = b.stationNo;");*/
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
}
