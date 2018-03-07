package com.lantaiyuan.staticCache;

import java.sql.SQLException;

/**
 * 服务缓存数据 初始化接口
 * Created by zhouyongbo on 2017-11-20.
 */
public abstract class IStaticCache {


    /**
     * 初始化操作
     */
    public abstract void init() throws SQLException;


    /**
     * 执行操作
     */
    public abstract void execute() throws SQLException;


}
