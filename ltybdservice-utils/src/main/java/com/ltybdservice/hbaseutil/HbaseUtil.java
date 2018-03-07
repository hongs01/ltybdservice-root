package com.ltybdservice.hbaseutil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.storm.topology.FailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HbaseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HbaseUtil.class);
    private static Connection sConn = null;
    private static HbaseUtil instance = new HbaseUtil();

    private HbaseUtil() {
    }

    public static HbaseUtil getConnectionInstance() {
        if (sConn == null) {
            Configuration configuration = HBaseConfiguration.create();
            try {
                sConn = ConnectionFactory.createConnection(configuration);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public void closeConnection() {
        if (sConn != null) {
            try {
                sConn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.createTable(table);
    }

    public void deleteTable(String tableName) throws IOException {
        Admin admin = sConn.getAdmin();
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.close();
    }

    public void createSchemaTables(String tableName, String cf) throws IOException {
        Admin admin = sConn.getAdmin();
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
        table.addFamily(new HColumnDescriptor(cf));
        createOrOverwrite(admin, table);
        admin.close();
    }


    public IEvent getEvent(String tableName, IEvent event, String key) {
        HTable table = null;
        try {
            table = (HTable) sConn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(String.valueOf(key).getBytes());
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException creating hbase row key.", e);
        }
        byte[] hbaseKey = bos.toByteArray();
        Get get = new Get(hbaseKey);
        for (String filed : event.eventGetFields()) {
            get.addColumn(event.eventGetColumnFamily().getBytes(), filed.getBytes());
        }
        Result result;
        try {
            result = table.get(get);
        } catch (IOException e) {
            throw new FailedException("IOException while reading from hbase.", e);
        }
        Class<? extends IEvent> cls = event.getClass();
        IEvent nEvent = null;
        try {
            nEvent = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ArrayList<String> fields = nEvent.eventGetFields();
        for (int i1 = 0; i1 < fields.size(); i1++) {
            String str = fields.get(i1);
            byte[] value = result.getValue(event.eventGetColumnFamily().getBytes(), str.getBytes());
            if (value != null) {
                try {
                    nEvent.setValueByField(str, new String(value));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return nEvent;
    }

    public void putEvent(String tableName, IEvent event, String key) {
        HTable table = null;
        try {
            table = (HTable) sConn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(String.valueOf(key).getBytes());
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException creating hbase row key.", e);
        }
        byte[] hbaseKey = bos.toByteArray();
        Put put = new Put(hbaseKey);
        for (String field : event.eventGetFields()) {
            String value = event.getValueByField(field);
            if (value != null)
                put.addColumn(event.eventGetColumnFamily().getBytes(), field.getBytes(), value.getBytes());
            else
                put.addColumn(event.eventGetColumnFamily().getBytes(), field.getBytes(), null);
        }
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(String tableName, String key) {
        HTable table = null;
        try {
            table = (HTable) sConn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(String.valueOf(key).getBytes());
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException creating hbase row key.", e);
        }
        byte[] hbaseKey = bos.toByteArray();
        Delete delete = new Delete(hbaseKey);
        try {
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<IEvent> getTable(String tableName, IEvent event, Filter filter) {
        HTable table = null;
        try {
            table = (HTable) sConn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<IEvent> eventTable = new ArrayList();
        Scan scan = new Scan();
        if (filter != null) {
            scan.setFilter(filter);
        }
        ResultScanner resultScanner = null;
        try {
            resultScanner = table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            for (Result result : resultScanner) {
                Class<? extends IEvent> cls = event.getClass();
                IEvent nEvent = null;
                try {
                    nEvent = cls.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                ArrayList<String> fields = nEvent.eventGetFields();
                for (int i1 = 0; i1 < fields.size(); i1++) {
                    String str = fields.get(i1);
                    byte[] value = result.getValue(event.eventGetColumnFamily().getBytes(), str.getBytes());
                    if (value != null) {
                        try {
//                            LOG.info(new String(value));
                            nEvent.setValueByField(str, new String(value));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                eventTable.add(nEvent);
            }
        } finally {
            if (resultScanner != null) resultScanner.close();
        }
        return eventTable;
    }
}
