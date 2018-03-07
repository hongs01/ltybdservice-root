package com.ltybdservice.hbaseutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HbaseUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(HbaseUtilTest.class);

    public static void main(String[] args) {
        HbaseUtil connectionInstance = HbaseUtil.getConnectionInstance();
//        String TABLE_NAME = "user";
//        TestUser testUser = new TestUser();
//        testUser.setName("小明");
//        testUser.setAge(20);
//        connectionInstance.putEvent(TABLE_NAME, testUser, testUser.getName());
//        TestUser newTestUser = (TestUser) connectionInstance.getEvent(TABLE_NAME, testUser, testUser.getName());
//        LOG.info(newTestUser.getName() + newTestUser.getAge());
//        connectionInstance.deleteEvent(TABLE_NAME, testUser.getName());
//
//        TestUser testUser1 = new TestUser();
//        testUser1.setName("小明");
//        testUser1.setAge(20);
//
//        ArrayList<IEvent> eventTable  = connectionInstance.getTable(TABLE_NAME, new TestUser(),null);
//        for (IEvent event:
//                eventTable) {
//            TestUser testUser2 =(TestUser) event;
//            if(testUser2.getAge()>10){
//                connectionInstance.deleteEvent(TABLE_NAME, testUser2.getName());
//            }
//        }
//        eventTable.clear();
        try {
//            connectionInstance.deleteTable("test");
            connectionInstance.createSchemaTables("test", "cf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionInstance.closeConnection();
        LOG.info("test");
    }
}
