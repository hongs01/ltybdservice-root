package com.ltybdservice.httputil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HttpUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);
    static final String AMAP_SEARCH_URI = "http://restapi.amap.com/v3/place/around";

    /**
     * @return type: com.ltybdservice.httputil.Station
     * @author: Administrator
     * @date: 2017/11/22 16:37
     * @method: getStation
     * @param: [key, location, keywords, radius, offset]
     * @desciption:
     */
    static Station getStation(String key, String location, String keywords, String radius, String offset) throws IOException {
        StringBuffer reqUri = new StringBuffer();
        Station station = new Station();
        reqUri.append(AMAP_SEARCH_URI)
                .append("?")
                .append("key=")
                .append(key)
                .append("&")
                .append("location=")
                .append(location)
                .append("&")
                .append("keywords=")
                .append(keywords)
                .append("&")
                .append("radius=")
                .append(radius)
                .append("&")
                .append("offset=")
                .append(offset);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(reqUri.toString());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                // The underlying HTTP connection is still held by the response object
                // to allow the response content to be streamed directly from the network socket.
                // In order to ensure correct deallocation of system resources
                // the user MUST call CloseableHttpResponse#close() from a finally clause.
                // Please note that if response content is not fully consumed the underlying
                // connection cannot be safely re-used and will be shut down and discarded
                // by the connection manager.
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity);
                JSONObject pois = JSON.parseObject(jsonStr).getJSONArray("pois").getJSONObject(0);
                station.setId(pois.getString("id"));
                station.setLocation(pois.getString("location"));
                station.setName(pois.getString("name"));
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity);
            }
        }
        return station;
    }
}
