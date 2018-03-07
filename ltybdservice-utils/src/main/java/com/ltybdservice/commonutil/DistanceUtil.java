package com.ltybdservice.commonutil;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DistanceUtil {
    private static final double EARTH_RADIUS = 6378137;

    /**
     * @return type: double
     * @author: Administrator
     * @date: 2017/11/22 16:26
     * @method: rad
     * @param: [d]
     * @desciption: 把经纬度转为度（°）
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * @return type: double
     * @author: Administrator
     * @date: 2017/11/22 16:27
     * @method: getDistance
     * @param: [lng1, lat1, lng2, lat2]
     * @desciption: 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2)
                                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
                )
        );
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
