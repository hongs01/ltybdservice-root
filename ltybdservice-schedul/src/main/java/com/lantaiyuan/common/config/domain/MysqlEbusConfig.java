package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class MysqlEbusConfig extends ISetProperties {
    @Override
    public String getPrefix() {
        return "mysql.ebus";
    }
    private static String url;
    private static String user;
    private static String password;

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static Map<String,String> getConfig(){
        Map<String,String> map = new HashMap<String,String>();

        if (!StringUtil.isEmpty(url)){
            map.put("url",url);
        }

        if (!StringUtil.isEmpty(user)){
            map.put("user",user);
        }

        if (!StringUtil.isEmpty(password)){
            map.put("password",password);
        }
        return map;
    }
}
