package com.ltybdservice.hiveutil;

import java.io.Serializable;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DataSourceConf implements Serializable {
    private String driverName;
    private String jdbcConnUrl;
    private String dataBaseName;
    private String hiveUser;
    private String hivePassword;

    /**
     * @return type:
     * @author: Administrator
     * @date: 2017/11/22 16:31
     * @method: DataSourceConf
     * @param: [driverName, jdbcConnUrl, dataBaseName, hiveUser, hivePassword]
     * @desciption:
     */
    public DataSourceConf(String driverName, String jdbcConnUrl, String dataBaseName, String hiveUser, String hivePassword) {
        this.driverName = driverName;
        this.jdbcConnUrl = jdbcConnUrl;
        this.dataBaseName = dataBaseName;
        this.hiveUser = hiveUser;
        this.hivePassword = hivePassword;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getJdbcConnUrl() {
        return jdbcConnUrl;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getHiveUser() {
        return hiveUser;
    }

    public String getHivePassword() {
        return hivePassword;
    }
}
