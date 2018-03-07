package com.ltybdservice.jsonutil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.shade.org.apache.http.util.TextUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class JsonUtil {
    private static final Log logger = LogFactory.getLog(JsonUtil.class);


    static public String Object2Json(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();


        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            objectMapper.writeValue(out, o);

            return new String(out.toByteArray(), "UTF-8");
        } catch (IOException e) {
            logger.error("Convert Map to JSON failure." + e);

        }
        return null;
    }

    static public Map Json2Map(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map maps = objectMapper.readValue(json, Map.class);
            return maps;
        } catch (Exception ex) {
            logger.error("Convert Json to Map failure." + ex);
        }
        return null;
    }

    static public List Json2List(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List list = objectMapper.readValue(json, List.class);
            return list;
        } catch (Exception ex) {
            logger.error("Convert Json to List failure." + ex);

        }
        return null;
    }

    /**
     * 返回指定类型的bean
     *
     * @param json
     * @param c
     * @return
     */
    static public <K> K Json2Bean(String json, Class<K> c) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            K b = objectMapper.readValue(json, c);
            return b;
        } catch (Exception ex) {
            logger.error("Convert Json to Bean failure." + ex);
        }
        return null;
    }

    /**
     * 返回指定类型的的List
     *
     * @param json
     * @param c
     * @return
     */
    static public <K> List<K> Json2List(String json, Class<K[]> c) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            K[] arr = objectMapper.readValue(json, c);
            return Arrays.asList(arr);
        } catch (Exception ex) {
            logger.error("Convert Json to Array failure." + ex);
        }
        return null;
    }

    static public <K> K[] Json2Array(String json, Class<K[]> c) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            K[] list = objectMapper.readValue(json, c);
            return list;
        } catch (Exception ex) {
            logger.error("Convert Json to Array failure." + ex);
        }
        return null;
    }

    //判断是否是json
    public static Boolean isJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];
        if (firstChar == '{') {
            return true;
        } else if (firstChar == '[') {
            return true;
        } else {
            return false;
        }
    }
}
