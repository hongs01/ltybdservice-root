package com.ltybdservice.commonutil;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DistanceUtilTest {
    public static void main(String[] args) {
        double distance = DistanceUtil.getDistance(121.491909, 31.233234, 121.411994, 31.206134);
        System.out.println("Distance is:" + distance);
    }
}
