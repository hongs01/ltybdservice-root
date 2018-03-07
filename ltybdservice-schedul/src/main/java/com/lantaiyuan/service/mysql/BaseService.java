package com.lantaiyuan.service.mysql;

import com.lantaiyuan.common.connection.MysqlConnection;
import com.lantaiyuan.service.IAbstractService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseService /*extends IAbstractService */{
    protected Connection conn=null;

//    @Override
//    public void init() throws SQLException {
//        conn = MysqlConnection.getInstance().getConnection();
//    }



}
