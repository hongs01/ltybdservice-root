package com.ltybdservice.hiveutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HiveUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(HiveUtilTest.class);

    public static void main(String[] args) throws SQLException, InterruptedException, PropertyVetoException {
        String driverName = "org.apache.hive.jdbc.HiveDriver";
        String jdbcConnUrl = "jdbc:hive2://master:10000/";
        String dcDataBase = "dc";
        String dpDataBase = "dp";
        String hiveUser = "hdfs";
        String hivePassword = "hdfs";
        DataSourceConf dcConf = new DataSourceConf(driverName, jdbcConnUrl, dcDataBase, hiveUser, hivePassword);
        DataSourceConf dpConf = new DataSourceConf(driverName, jdbcConnUrl, dpDataBase, hiveUser, hivePassword);
        DcDataBaseUtil dcInstance = DcDataBaseUtil.getDcConnectionInstance(dcConf);
        DpDataBaseUtil dpInstance = DpDataBaseUtil.getDpConnectionInstance(dpConf);
        LOG.info("zws: " + String.valueOf(dcInstance.devId2SeatNumber("130400", 10014)));
        LOG.info("lineId: " + dcInstance.devId2lineId("130400", 3));
        LOG.info("lineName: " + dcInstance.lineId2lineName("130400", 10));
        LOG.info("stationId2StationName: " + dcInstance.stationId2StationName("130400", 191));
        LOG.info("halfHourPsg: " + dpInstance.lineId2HalfHourPsg("130400", 34, "20170920", 14));
        LOG.info("substring: " + hiveUser.substring(0, 2));
        dcInstance.closeDcConnection();
        dpInstance.closeDpConnection();

    }
}
