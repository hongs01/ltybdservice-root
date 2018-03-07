package com.lantaiyuan.service;


import com.lantaiyuan.common.exception.ServiceClassNotFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyongbo on 2017-11-20.
 */
public abstract class IAbstractService {

    /**
     * 缓存对象 --- 单例
     */
    private static Map<String,Object> classMap = new HashMap<String,Object>();

    public abstract void init() throws SQLException;

    public static <T> T getInstance(Class t){
        Object o = classMap.get(t.getName());
        if (o == null){
            throw new ServiceClassNotFoundException("错 误 ! "+t.getName()+" 类 找 不 到");
        }
        return (T)o;
    }

    public static void doInit() throws SQLException {
        for (String name:classMap.keySet()){
            IAbstractService o = (IAbstractService) classMap.get(name);
            o.init();
        }
    }

    public static void putClassMap(String name,Object o ){
        classMap.put(name,o);
    }
}
