package com.lantaiyuan.common.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*Myslq连接驱动*/
public class MysqlConnection {
    private static Connection conn = null;

    private static MysqlConnection mysqlConnection = null;

    private MysqlConnection() {
    }

    public static synchronized MysqlConnection getInstance(){
        if (mysqlConnection ==null){
            mysqlConnection = new MysqlConnection();
        }
        return mysqlConnection;
    }

    public Connection getConnection() throws SQLException {
        //加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //加载驱动类
            conn = DriverManager.getConnection("jdbc:mysql://192.168.2.141/bdapplication",
                    "lty", "DB*&%Khds983LVF");
            //获取与目标数据库的连接，参数（"jdbc:mysql://localhost/数据库名"，"root"，"数据库密码"；
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }
}
