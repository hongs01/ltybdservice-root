package com.ltybdservice.hiveutil;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DcDataBaseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DcDataBaseUtil.class);
    private static ComboPooledDataSource dcCpds = null;
    private static Connection dcConnection = null;
    private static Map<String, ArrayList<Map>> ods_op_line_map = new HashMap<String, ArrayList<Map>>();              //key为citycode
    private static Map<String, ArrayList<Map>> ods_tjs_car_map = new HashMap<String, ArrayList<Map>>();              //key为citycode
    private static Map<String, ArrayList<Map>> ods_op_stationblock_map = new HashMap<String, ArrayList<Map>>();      //key为citycode
    private static DcDataBaseUtil dcConnectionInstance = new DcDataBaseUtil();

    private DcDataBaseUtil() {
    }

    /**
     * @return type: com.mchange.v2.c3p0.ComboPooledDataSource
     * @author: Administrator
     * @date: 2017/11/22 16:29
     * @method: getCpds
     * @param: [conf]
     * @desciption:
     */
    private static ComboPooledDataSource getCpds(DataSourceConf conf) {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(conf.getDriverName()); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(conf.getJdbcConnUrl() + conf.getDataBaseName());
        cpds.setUser(conf.getHiveUser());
        cpds.setPassword(conf.getHivePassword());
        cpds.setMaxStatements(180);
        return cpds;
    }

    public static DcDataBaseUtil getDcConnectionInstance(DataSourceConf conf) {
        if (dcConnection == null) {
            try {
                dcCpds = getCpds(conf);
                dcConnection = dcCpds.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dcConnectionInstance;
    }

    public void closeDcConnection() {
        try {
            dcConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dcCpds.close();
    }

    //use dc databases
    public Map<String, ArrayList<Map>> getOds_op_lineTable(String cityCode) throws PropertyVetoException, SQLException {
        if (ods_op_line_map.get(cityCode) != null) {
            return ods_op_line_map;
        }
        String sql = "select linename,id from ods_op_line where citycode= " + cityCode;
//        String sql = "select linename,id from  ods_op_line";
        ArrayList<Map> arrayList = new ArrayList<Map>();
        Map lineId2lineNameMap = new HashMap<Integer, String>();
        PreparedStatement ps = dcConnection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lineId2lineNameMap.put(rs.getInt("id"), rs.getString("linename"));
        }
        rs.close();
        arrayList.add(lineId2lineNameMap);

        ods_op_line_map.put(cityCode, arrayList);
        return ods_op_line_map;
    }

    //use dc databases
    public Map<String, ArrayList<Map>> getOds_tjs_carTable(String cityCode) throws PropertyVetoException, SQLException {
        if (ods_tjs_car_map.get(cityCode) != null) {
            return ods_tjs_car_map;
        }
        String sql = "select onBoardId,lineId,zws from ods_tjs_car where  citycode= " + cityCode;
//        String sql = "select onBoardId,lineId,zws from ods_tjs_car";
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Map> arrayList = new ArrayList<Map>();
        Map devId2lineId = new HashMap<Integer, Integer>();
        Map devId2SeatNumber = new HashMap<Integer, Integer>();
        ps = dcConnection.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            devId2lineId.put(rs.getInt("onBoardId"), rs.getInt("lineId"));
            devId2SeatNumber.put(rs.getInt("onBoardId"), rs.getInt("zws"));
        }
        rs.close();
        arrayList.add(devId2lineId);
        arrayList.add(devId2SeatNumber);
        ods_tjs_car_map.put(cityCode, arrayList);
        return ods_tjs_car_map;
    }

    //use dc databases
    public Map<String, ArrayList<Map>> getOds_op_stationblockTable(String cityCode) throws PropertyVetoException, SQLException {
        if (ods_op_stationblock_map.get(cityCode) != null) {
            return ods_op_stationblock_map;
        }
        String sql = "select citycode,bus_station_code,stationname from ods_op_stationblock where  citycode= " + cityCode;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Map> arrayList = new ArrayList<Map>();
        Map stationId2StationName = new HashMap<String, String>();
        ps = dcConnection.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            try {
                stationId2StationName.put(rs.getString("bus_station_code"), rs.getString("stationname"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        rs.close();
        arrayList.add(stationId2StationName);
        ods_op_stationblock_map.put(cityCode, arrayList);
        return ods_op_stationblock_map;
    }


    public int devId2lineId(String cityCode, int devId) throws SQLException, PropertyVetoException {
        Map<Integer, Integer> devId2lineIdMap = getOds_tjs_carTable(cityCode).get(cityCode).get(0);
        int lineid;
        if (devId2lineIdMap.get(devId) == null) {
            LOG.error("no, the devId is not exsit :" + devId);
            lineid = 0;
        } else {
//            LOG.error("yes, the devId is exsit :" + devId);
            lineid = devId2lineIdMap.get(devId);
        }

        return lineid;
    }

    public int devId2SeatNumber(String cityCode, int devId) throws SQLException, PropertyVetoException {
        Map<Integer, Integer> devId2SeatNumber = getOds_tjs_carTable(cityCode).get(cityCode).get(1);
        int zws;
        if (devId2SeatNumber.get(devId) == null) {
            LOG.error("no, the devId is not exsit :" + devId);
            zws = 0;
        } else {
//            LOG.error("yes, the devId is exsit :" + devId);
            zws = devId2SeatNumber.get(devId);
        }
        return zws;
    }


    public String lineId2lineName(String cityCode, int lineId) throws PropertyVetoException, SQLException {
        Map<Integer, String> lineId2lineNameMap = getOds_op_lineTable(cityCode).get(cityCode).get(0);
        String lineName;
        if (lineId2lineNameMap.get(lineId) == null) {
            LOG.error("no, the lineId is not exsit :" + lineId);
            lineName = "lineName";
        } else {
//            LOG.error("yes, the lineId is  exsit :" + lineId);
            lineName = lineId2lineNameMap.get(lineId);
        }
        return lineName;
    }

    public String stationId2StationName(String cityCode, int stationId) throws PropertyVetoException, SQLException {
        Map<String, String> stationId2StationNameMap = getOds_op_stationblockTable(cityCode).get(cityCode).get(0);
        String stationName;
        if (stationId2StationNameMap.get(String.valueOf(stationId)) == null) {
            LOG.error("no, the stationId is not exsit :" + stationId);
            stationName = "stationName";
        } else {
//            LOG.error("yes, the stationId is  exsit :" + stationId);
            stationName = stationId2StationNameMap.get(String.valueOf(stationId));
        }

        return stationName;
    }
}


