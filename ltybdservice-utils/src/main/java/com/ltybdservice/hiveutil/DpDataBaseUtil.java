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
public class DpDataBaseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DpDataBaseUtil.class);
    private static ComboPooledDataSource dpCpds = null;
    private static Connection dpConnection = null;
    private static Map<String, ArrayList<Map>> hour_passflow_line_map0 = new HashMap<>();       //key为citycode+hour direction=0
    private static Map<String, ArrayList<Map>> hour_passflow_line_map1 = new HashMap<>();       //key为citycode+hour direction=1

    private static DpDataBaseUtil dpConnectionInstance = new DpDataBaseUtil();

    private DpDataBaseUtil() {
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

    /**
     * @return type: com.ltybdservice.hiveutil.DpDataBaseUtil
     * @author: Administrator
     * @date: 2017/11/22 16:29
     * @method: getDpConnectionInstance
     * @param: [conf]
     * @desciption:
     */
    public static DpDataBaseUtil getDpConnectionInstance(DataSourceConf conf) {
        if (dpConnection == null) {
            try {
                dpCpds = getCpds(conf);
                dpConnection = dpCpds.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dpConnectionInstance;
    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:30
     * @method: closeDpConnection
     * @param: []
     * @desciption:
     */
    public void closeDpConnection() {
        try {
            dpConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dpCpds.close();
    }

    /**
     * @return type: java.util.Map<java.lang.String,java.util.ArrayList<java.util.Map>>
     * @author: Administrator
     * @date: 2017/11/22 16:30
     * @method: getHour_passflow_lineTable0
     * @param: [workDate, hour]
     * @desciption: use dp databases
     */
    public Map<String, ArrayList<Map>> getHour_passflow_lineTable0(String workDate, String hour) throws PropertyVetoException, SQLException {
        if (hour_passflow_line_map0.get(hour) != null) {
            return hour_passflow_line_map0;
        }
        hour_passflow_line_map0.clear();//清除过时数据，避免数据一直累积
        String sql = "select citycode,lineid,workdate,offtime,predictionpassengernum from  dm_passflow_line_dm where type = 1 and cycle = 30 and direction = 0 and workdate = " + workDate + " and offtime =" + hour;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Map> arrayList = new ArrayList<Map>();
        Map lineid2PrePsg = new HashMap<String, Integer>();
        ps = dpConnection.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            lineid2PrePsg.put(String.valueOf(rs.getInt("lineid")) + "@" + rs.getString("citycode"), rs.getInt("predictionpassengernum"));
        }

        rs.close();
        arrayList.add(lineid2PrePsg);
        hour_passflow_line_map0.put(hour, arrayList);
        return hour_passflow_line_map0;
    }

    /**
     * @return type: java.util.Map<java.lang.String,java.util.ArrayList<java.util.Map>>
     * @author: Administrator
     * @date: 2017/11/22 16:30
     * @method: getHour_passflow_lineTable1
     * @param: [workDate, hour]
     * @desciption:
     */
    public Map<String, ArrayList<Map>> getHour_passflow_lineTable1(String workDate, String hour) throws PropertyVetoException, SQLException {
        if (hour_passflow_line_map1.get(hour) != null) {
            return hour_passflow_line_map1;
        }
        hour_passflow_line_map1.clear();//清除过时数据，避免数据一直累积
        String sql = "select citycode,lineid,workdate,offtime,predictionpassengernum from  dm_passflow_line_dm where type = 1 and cycle = 30 and direction = 1 and workdate = " + workDate + " and offtime =" + hour;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Map> arrayList = new ArrayList<Map>();
        Map lineid2PrePsg = new HashMap<String, Integer>();
        ps = dpConnection.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            lineid2PrePsg.put(String.valueOf(rs.getInt("lineid")) + "@" + rs.getString("citycode"), rs.getInt("predictionpassengernum"));
        }

        rs.close();
        arrayList.add(lineid2PrePsg);
        hour_passflow_line_map1.put(hour, arrayList);
        return hour_passflow_line_map1;
    }

    /**
     * @return type: int
     * @author: Administrator
     * @date: 2017/11/22 16:32
     * @method: lineId2HalfHourPsg
     * @param: [cityCode, lineId, workDate, hour]
     * @desciption:
     */
    public int lineId2HalfHourPsg(String cityCode, int lineId, String workDate, int hour) throws PropertyVetoException, SQLException {
        Map<String, Integer> lineid2PrePsgMap0 = getHour_passflow_lineTable0(workDate, String.valueOf(hour)).get(String.valueOf(hour)).get(0);
        Map<String, Integer> lineid2PrePsgMap1 = getHour_passflow_lineTable1(workDate, String.valueOf(hour)).get(String.valueOf(hour)).get(0);
        String key = String.valueOf(lineId) + "@" + cityCode;
        int hourPrePsg0;
        if (lineid2PrePsgMap0.get(key) == null) {
            LOG.error("no, workDate " + workDate + ",the lineId@cityCode is not exsit :" + key);
            hourPrePsg0 = 0;
        } else {
//            LOG.error("yes, workDate " + workDate + ",the lineId@cityCode is exsit :" + key);
            hourPrePsg0 = lineid2PrePsgMap0.get(key);
        }
        int hourPrePsg1;
        if (lineid2PrePsgMap1.get(key) == null) {
            LOG.error("no, workDate " + workDate + ",the lineId@cityCode is not exsit :" + key);
            hourPrePsg1 = 0;
        } else {
//            LOG.error("yes, workDate " + workDate + ",the lineId@cityCode is exsit :" + key);
            hourPrePsg1 = lineid2PrePsgMap1.get(key);
        }
        return hourPrePsg0 + hourPrePsg1;
    }
}
