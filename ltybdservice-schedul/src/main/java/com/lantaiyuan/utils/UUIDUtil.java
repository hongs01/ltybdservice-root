package com.lantaiyuan.utils;

import java.util.UUID;

/**
 * uuid 操作
 * Created by zhouyongbo on 2017-11-22.
 */
public class UUIDUtil {

    /**
     * 操作UUID
     * @return
     */
    public static String getUUIDString(String repalce){
        UUID uuid = UUID.randomUUID();
        if (repalce == null){
            return uuid.toString();
        }
        return uuid.toString().replaceAll("-",repalce);
    }
}
