package com.ltybdservice.httputil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HttpUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtilTest.class);
    private static final String key = "d5e7ef42aa54830245f9188b3e26d50d";
    private static final String keywords = "公交站";
    private static final String radius = "1000";
    private static final String offset = "1";

    public static void main(String[] args) throws Exception {
//        Station station=HttpUtil.getStation(key,"114.399675,30.457256",keywords,radius,offset);
//        LOG.info(station.getName());
        UserDemand userDemand1 = new UserDemand("郑熊猫1", "114.399675,30.457256", "114.333929,30.576977");
        UserDemand userDemand2 = new UserDemand("郑熊猫2", "114.399675,30.457256", "114.333929,30.576977");
        UserDemand userDemand3 = new UserDemand("郑熊猫2", "114.399675,30.457255", "114.333929,30.576977");
        LineDemand lineDemand = new LineDemand();
        lineDemand.addUserDemand(userDemand1);
        lineDemand.addUserDemand(userDemand2);
        lineDemand.addUserDemand(userDemand3);
        Map<String, ArrayList<String>> line = lineDemand.getLine();
        for (String lineName : line.keySet()
                ) {
            LOG.info("线路名：" + lineName);
            LOG.info("线路需求量（人次）：" + String.valueOf(line.get(lineName).size()));
        }
    }
}
