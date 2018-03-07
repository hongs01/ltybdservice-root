package com.ltybdservice.mysqlutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;


/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 10.1.254.76:3306->外网访问 202.104.136.228:5506
 */
public class MysqlUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(MysqlUtilTest.class);

    public static void main(String[] args) {
        MysqlConf conf = new MysqlConf("com.mysql.jdbc.Driver", "jdbc:mysql://202.104.136.228:5506", "ebus?useUnicode=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false", "dev", "123456");
        DataBaseUtil connectionInstance = DataBaseUtil.getConnectionInstance(conf);
        String userId = null;
        try {
            userId = connectionInstance.cardCode2UserId("130400", "130400201000053");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connectionInstance.closeConnection();
        LOG.info(userId);
    }
}
