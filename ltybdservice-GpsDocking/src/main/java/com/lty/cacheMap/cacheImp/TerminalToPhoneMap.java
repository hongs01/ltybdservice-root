package com.lty.cacheMap.cacheImp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lty.cacheMap.ICacheMap;

/**
 * 终端手机号缓存
 * 终端编号 ----> 手机号码
 * @author zhouyongbo 2017/12/23
 */
@Component
public class TerminalToPhoneMap implements ICacheMap {
    private static Logger logger = LoggerFactory.getLogger(TerminalToPhoneMap.class);
    private static Map<String,String> PHONEMAP = new HashMap<String,String>();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 15 02 ? * *")
    @Override
    public void init() {
        PHONEMAP = execute();
        logger.info("终端编号 ----> 手机号码 静态缓存加载完成");
    }

    private Map<String, String> execute() {
        Map<String,String> map = new HashMap<String, String>();
        try{
            List<Map<String, String>> objects = getObjects();
            for (int i = 0; i < objects.size(); i++) {
                map.putAll(objects.get(i));
            }
        }catch (Exception e){
            logger.info("缓存出错",e);
        }
        return map;
    }


    private List<Map<String,String>> getObjects(){
        // String querySql = "select OnBoardId,interCard AS MobileNo FROM tjs_car t
        // where t.delState = 0 ";
        String querySql = "SELECT inner_code AS OnBoardId,  inter_card_number AS MobileNo FROM  fleet_vehicle";
        List<Map<String, String>> query = jdbcTemplate.query(querySql, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String,String> mapRow(ResultSet resultSet, int index) throws SQLException {
                Map<String,String> stringMap = new HashMap<String,String>();
                stringMap.put(resultSet.getString("OnBoardId"),resultSet.getString("MobileNo"));
                return stringMap;
            }
        });
        return query;
    }

    public static <T, V> T getValue(V v) {
        String s = PHONEMAP.get(v);
        if (s == null)return null;
        return (T)s;
    }



}
