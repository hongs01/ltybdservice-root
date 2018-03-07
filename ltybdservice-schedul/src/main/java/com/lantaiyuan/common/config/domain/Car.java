package com.lantaiyuan.common.config.domain;

import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.utils.ListUtil;
import com.lantaiyuan.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class Car extends ISetProperties {
    @Override
    public String getPrefix() {
        return "car";
    }

    private static int distance;

    public static int  getDistance() {
        return distance;
    }

    public static Map<String,Integer> getCarConfig(){
        Map<String,Integer> map = new HashMap<String, Integer>();
        map.put("car.distance",distance);
        return map;
    }
}
