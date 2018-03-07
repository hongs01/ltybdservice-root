package com.ltybdservice.commonutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DateUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtilTest.class);
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static Map<Integer, String> mapLineId2LineAvgSpeed = new HashMap<>();

    private static class TestUser {
        String name;
        int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static void main(String[] args) {

        LOG.error("dateString = " + DateUtil.stamp10toDateString(1506656690));
//        LOG.error("stamp10 = " + dateStringtoStamp10("2017-09-20 23:58:17") / 1000);
//        mapLineId2LineAvgSpeed.put(1,"nihao");
//        String str = JSON.toJSONString(mapLineId2LineAvgSpeed);
//        Map<Integer,Integer> map = JSON.parseObject(str,HashMap.class);
//        LOG.error("map value = " + map.get(1));
//        List<TestUser> data = new ArrayList<>();
//        TestUser user = new TestUser();
//        user.setAge(11);
//        user.setName("234");
//        data.add(user);
//        String str = JSON.toJSONString(data);
//        LOG.error("toJSONString = " + str);
//        List<TestUser> userList = JSON.parseArray(str,TestUser.class);
//        TestUser ii= userList.get(0);
//        LOG.error("value = " + ii.getName());

    }
}
