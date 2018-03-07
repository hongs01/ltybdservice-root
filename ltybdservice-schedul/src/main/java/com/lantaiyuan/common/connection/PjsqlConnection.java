package com.lantaiyuan.common.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*pjslq连接驱动*/
public class PjsqlConnection {
    private static Connection conn = null;

    private static PjsqlConnection pjsqlconnection = null;

    private PjsqlConnection() {
    }

    public static synchronized PjsqlConnection getInstance(){
        if (pjsqlconnection ==null){
            pjsqlconnection = new PjsqlConnection();
        }
        return pjsqlconnection;
    }

    public Connection getConnection() throws SQLException {
        //加载驱动
        try {
            Class.forName("org.postgresql.Driver");
            //加载驱动类
            conn = DriverManager.getConnection("jdbc:postgresql://192.168.2.180/wang_2",
                    "lantaiyuan", "123456");
            //获取与目标数据库的连接，参数（"jdbc:postgresql://localhost/数据库名"，"root"，"数据库密码"；
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }
}
