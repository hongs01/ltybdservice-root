package com.lantaiyuan.service.pjSql;

import com.lantaiyuan.common.connection.PjsqlConnection;
import com.lantaiyuan.service.IAbstractService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class BaseService extends IAbstractService {

    protected Connection conn=null;

    @Override
    public void init() throws SQLException {
   //     conn = PjsqlConnection.getInstance().getConnection();
    }
}
