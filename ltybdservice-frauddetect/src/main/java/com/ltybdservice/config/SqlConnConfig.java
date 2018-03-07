package com.ltybdservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class SqlConnConfig implements Serializable {
    //hiveconf
    private String mysqlDriverName;
    private String mysqlJdbcConnUrl;
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlDataBaseName;
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
    private void getConfig(Properties pro) {
        mysqlDriverName=pro.getProperty("mysqlDriverName");
        mysqlJdbcConnUrl=pro.getProperty("mysqlJdbcConnUrl");
        mysqlUser=pro.getProperty("mysqlUser");
        mysqlPassword=pro.getProperty("mysqlPassword");
        mysqlDataBaseName=pro.getProperty("mysqlDataBaseName");
    }

    public String getMysqlDriverName() {
        return mysqlDriverName;
    }

    public void setMysqlDriverName(String mysqlDriverName) {
        this.mysqlDriverName = mysqlDriverName;
    }

    public String getMysqlJdbcConnUrl() {
        return mysqlJdbcConnUrl;
    }

    public void setMysqlJdbcConnUrl(String mysqlJdbcConnUrl) {
        this.mysqlJdbcConnUrl = mysqlJdbcConnUrl;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

    public String getMysqlDataBaseName() {
        return mysqlDataBaseName;
    }

    public void setMysqlDataBaseName(String mysqlDataBaseName) {
        this.mysqlDataBaseName = mysqlDataBaseName;
    }
}
