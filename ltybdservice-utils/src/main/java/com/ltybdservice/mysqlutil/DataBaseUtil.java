package com.ltybdservice.mysqlutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 10.1.254.76:3306->外网访问 202.104.136.228:5506
 */
public class DataBaseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DataBaseUtil.class);
    private static Map<String, ArrayList<Map>> lty_virtual_card_account_map = new HashMap<String, ArrayList<Map>>();
    private static Connection conn = null;
    private static DataBaseUtil devConnectionInstance = new DataBaseUtil();

    private DataBaseUtil() {
    }

    /**
     * @return type: com.ltybdservice.mysqlutil.DataBaseUtil
     * @author: Administrator
     * @date: 2017/11/22 16:36
     * @method: getConnectionInstance
     * @param: [conf]
     * @desciption:
     */
    public static DataBaseUtil getConnectionInstance(MysqlConf conf) {
        if (conn == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = conf.getJdbcConnUrl() + "/" + conf.getDataBaseName();

            try {
                conn = DriverManager.getConnection(url, "dev", "123456");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return devConnectionInstance;
    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:37
     * @method: closeConnection
     * @param: []
     * @desciption:
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @return type: java.util.Map<java.lang.String,java.util.ArrayList<java.util.Map>>
     * @author: Administrator
     * @date: 2017/11/22 16:37
     * @method: getLty_virtual_card_accountTable
     * @param: [cityCode]
     * @desciption:
     */
    public Map<String, ArrayList<Map>> getLty_virtual_card_accountTable(String cityCode) throws PropertyVetoException, SQLException {
        if (lty_virtual_card_account_map.get(cityCode) != null) {
            return lty_virtual_card_account_map;
        }
//        String sql = "select user_id,card_no from " + "lty_virtual_card_account where citycode= "+cityCode;
        String sql = "select user_id,card_no from " + "lty_virtual_card_account";
        ResultSet rs = null;
        ArrayList<Map> arrayList = new ArrayList<Map>();
        Map cardNo2userIdMap = new HashMap<String, Integer>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            cardNo2userIdMap.put(rs.getString("card_no"), rs.getInt("user_id"));
        }
        rs.close();
        stmt.close();
        arrayList.add(cardNo2userIdMap);

        lty_virtual_card_account_map.put(cityCode, arrayList);
        return lty_virtual_card_account_map;
    }

    /**
     * @return type: java.lang.String
     * @author: Administrator
     * @date: 2017/11/22 16:37
     * @method: cardCode2UserId
     * @param: [cityCode, cardNo]
     * @desciption:
     */
    public String cardCode2UserId(String cityCode, String cardNo) throws PropertyVetoException, SQLException {
        Map<String, Integer> cardNo2userIdMap = getLty_virtual_card_accountTable(cityCode).get(cityCode).get(0);
        int userId;
        if (cardNo2userIdMap.get(cardNo) == null) {
            LOG.error("the cardNo is not exsit ");
            userId = 0;
        } else {
            userId = cardNo2userIdMap.get(cardNo);
        }
        return String.valueOf(userId);
    }
}

