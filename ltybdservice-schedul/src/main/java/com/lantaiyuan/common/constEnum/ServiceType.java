package com.lantaiyuan.common.constEnum;


import com.lantaiyuan.service.hdfs.HdfsOperation;
import com.lantaiyuan.service.mysql.BaseService;

/**
 * service 类型
 * 用于创建实例
 * Created by zhouyongbo on 2017-11-20.
 */
public enum  ServiceType {
    HDFS(HdfsOperation.class),
    MYSQL(BaseService.class);

    private Class key;


    ServiceType(Class key) {
        this.key = key;
    }

    public Class getKey() {
        return key;
    }

    public void setKey(Class key) {
        this.key = key;
    }
}
