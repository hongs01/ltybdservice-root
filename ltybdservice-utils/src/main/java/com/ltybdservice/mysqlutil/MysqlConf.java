package com.ltybdservice.mysqlutil;

import java.io.Serializable;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 10.1.254.76:3306->外网访问 202.104.136.228:5506
 */
public class MysqlConf implements Serializable {
    private String driverName;
    private String jdbcConnUrl;
    private String dataBaseName;
    private String user;
    private String password;

    /**
     * @Administrator     * @aut2017/11/22i16:38or
     * @dateMysqlConf/22 16:37
  [driverName, jdbcConnUrl, dataBaseName, user, password]cConnUrl, dataBaseName, user, password]
     * @desciption:
     */
    public MysqlConf(String driverName, String jdbcConnUrl, String dataBaseName, String user, String password) {
        this.driverName = driverName;
        this.jdbcConnUrl = jdbcConnUrl;
        this.dataBaseName = dataBaseName;
        this.user = user;
        this.password = password;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getJdbcConnUrl() {
        return jdbcConnUrl;
    }

    public void setJdbcConnUrl(String jdbcConnUrl) {
        this.jdbcConnUrl = jdbcConnUrl;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
