package com.ltybdservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 10.1.254.76:3306->外网访问 202.104.136.228:5506
 */
public class SqlConnConfig implements Serializable {
    //hiveconf
    private String driverName;
    private String jdbcConnUrl;
    private String hiveUser;
    private String hivePassword;
    private String dcDataBase;
    private String dpDataBase;

    /**
     * @return type:
     * @author: Administrator
     * @date: 2017/11/22 16:41
     * @method: SqlConnConfig
     * @param: []
     * @desciption:
     */
    public SqlConnConfig() {
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/config.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig(pro);
    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:41
     * @method: getConfig
     * @param: [pro]
     * @desciption:
     */
    private void getConfig(Properties pro) {
        driverName = pro.getProperty("driverName");
        jdbcConnUrl = pro.getProperty("jdbcConnUrl");
        hiveUser = pro.getProperty("hiveUser");
        hivePassword = pro.getProperty("hivePassword");
        dcDataBase = pro.getProperty("dcDataBase");
        dpDataBase = pro.getProperty("dpDataBase");
    }

    public String getDriverName() {
        return driverName;
    }

    public String getJdbcConnUrl() {
        return jdbcConnUrl;
    }

    public String getHiveUser() {
        return hiveUser;
    }

    public String getHivePassword() {
        return hivePassword;
    }

    public String getDcDataBase() {
        return dcDataBase;
    }

    public String getDpDataBase() {
        return dpDataBase;
    }
}
