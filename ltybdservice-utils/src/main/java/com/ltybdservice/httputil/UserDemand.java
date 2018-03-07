package com.ltybdservice.httputil;

import java.io.IOException;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class UserDemand {
    private static final String key = "d5e7ef42aa54830245f9188b3e26d50d";
    private static final String keywords = "公交站";
    private static final String radius = "1000";
    private static final String offset = "1";
    private String user;
    private String originStation;
    private String destStation;

    public UserDemand(String user, String originLocation, String destLocation) throws IOException {
        this.user = user;
        this.originStation = HttpUtil.getStation(key, originLocation, keywords, radius, offset).getName();
        this.destStation = HttpUtil.getStation(key, destLocation, keywords, radius, offset).getName();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOriginStation() {
        return originStation;
    }

    public void setOriginStation(String originStation) {
        this.originStation = originStation;
    }

    public String getDestStation() {
        return destStation;
    }

    public void setDestStation(String destStation) {
        this.destStation = destStation;
    }
}
