package com.ltybdservice.hbaseutil;

import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.storm.hbase.security.HBaseSecurityUtil;
import org.apache.storm.task.IMetricsContext;
import org.apache.storm.topology.FailedException;
import org.apache.storm.trident.state.*;
import org.apache.storm.trident.state.map.*;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.security.PrivilegedExceptionAction;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class StormEventHBaseMapState<T> implements IBackingMap<T> {
    private static Logger LOG = LoggerFactory.getLogger(StormEventHBaseMapState.class);
    private int partitionNum;
    @SuppressWarnings("rawtypes")
    private static final Map<StateType, Serializer> DEFAULT_SERIALZERS = Maps.newHashMap();

    static {
        DEFAULT_SERIALZERS.put(StateType.NON_TRANSACTIONAL, new JSONNonTransactionalSerializer());
        DEFAULT_SERIALZERS.put(StateType.TRANSACTIONAL, new JSONTransactionalSerializer());
        DEFAULT_SERIALZERS.put(StateType.OPAQUE, new JSONOpaqueSerializer());
    }

    private StormEventHBaseMapState.Options<T> options;
    private Serializer<T> serializer;
    private HTable table;

    public StormEventHBaseMapState(final StormEventHBaseMapState.Options<T> options, Map map, int partitionNum) {
        this.options = options;
        this.serializer = options.serializer;
        this.partitionNum = partitionNum;

        final Configuration hbConfig = HBaseConfiguration.create();
        Map<String, Object> conf = (Map<String, Object>) map.get(options.configKey);
        if (conf == null) {
            LOG.info("hbase configuration not found using key '" + options.configKey + "'");
            LOG.info("Using hbase config from first hbase-site.xml found on classpath.");
        } else {
            if (conf.get("hbase.rootdir") == null) {
                LOG.warn("No 'hbase.rootdir' value found in configuration! Using hbase defaults.");
            }
            for (String key : conf.keySet()) {
                hbConfig.set(key, String.valueOf(conf.get(key)));
            }
        }

        try {
            UserProvider provider = HBaseSecurityUtil.login(map, hbConfig);
            this.table = provider.getCurrent().getUGI().doAs(new PrivilegedExceptionAction<HTable>() {
                @Override
                public HTable run() throws IOException {
                    return new HTable(hbConfig, options.tableName);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("hbase bolt preparation failed: " + e.getMessage(), e);
        }

    }

    @SuppressWarnings("rawtypes")
    public static StateFactory opaque() {
        StormEventHBaseMapState.Options<OpaqueValue> options = new StormEventHBaseMapState.Options<OpaqueValue>();
        return opaque(options);
    }

    @SuppressWarnings("rawtypes")
    public static StateFactory opaque(StormEventHBaseMapState.Options<OpaqueValue> opts) {

        return new StormEventHBaseMapState.Factory(StateType.OPAQUE, opts);
    }

    @SuppressWarnings("rawtypes")
    public static StateFactory transactional() {
        StormEventHBaseMapState.Options<TransactionalValue> options = new StormEventHBaseMapState.Options<TransactionalValue>();
        return transactional(options);
    }

    @SuppressWarnings("rawtypes")
    public static StateFactory transactional(StormEventHBaseMapState.Options<TransactionalValue> opts) {
        return new StormEventHBaseMapState.Factory(StateType.TRANSACTIONAL, opts);
    }

    public static StateFactory nonTransactional() {
        StormEventHBaseMapState.Options<Object> options = new StormEventHBaseMapState.Options<Object>();
        return nonTransactional(options);
    }

    public static StateFactory nonTransactional(StormEventHBaseMapState.Options<Object> opts) {
        return new StormEventHBaseMapState.Factory(StateType.NON_TRANSACTIONAL, opts);
    }

    public static class Options<T> implements Serializable {

        private Serializer<T> serializer = null;
        private int cacheSize = 5000;
        private String globalKey = "$HBASE_STATE_GLOBAL$";
        private String configKey = "hbase.config";
        private String tableName;
        private String columnFamily;
        private IEvent event;

        public String getColumnFamily() {
            return columnFamily;
        }

        public IEvent getEvent() {
            return event;
        }

        public Options<T> setSerializer(Serializer<T> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Options<T> setCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Options<T> setGlobalKey(String globalKey) {
            this.globalKey = globalKey;
            return this;
        }

        public Options<T> setConfigKey(String configKey) {
            this.configKey = configKey;
            return this;
        }

        public Options<T> setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Options<T> setColumnFamily(String columnFamily) {
            this.columnFamily = columnFamily;
            return this;
        }

        public Options<T> setEvent(IEvent event) {
            this.event = event;
            return this;
        }
    }

    protected static class Factory implements StateFactory {
        private StateType stateType;
        private StormEventHBaseMapState.Options options;

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Factory(StateType stateType, StormEventHBaseMapState.Options options) {
            this.stateType = stateType;
            this.options = options;

            if (this.options.serializer == null) {
                this.options.serializer = DEFAULT_SERIALZERS.get(stateType);
            }

            if (this.options.serializer == null) {
                throw new RuntimeException("Serializer should be specified for type: " + stateType);
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
            LOG.info("Preparing hbase State for partition {} of {}.", partitionIndex + 1, numPartitions);
            IBackingMap state = new StormEventHBaseMapState(options, conf, partitionIndex);
            if (options.cacheSize > 0) {
                state = new CachedMap(state, options.cacheSize);
            }
            MapState mapState;
            switch (stateType) {
                case NON_TRANSACTIONAL:
                    mapState = NonTransactionalMap.build(state);
                    break;
                case OPAQUE:
                    mapState = OpaqueMap.build(state);
                    break;
                case TRANSACTIONAL:
                    mapState = TransactionalMap.build(state);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown state type: " + stateType);
            }
            return new SnapshottableMap(mapState, new Values(options.globalKey));
        }

    }

    @Override
    public List<T> multiGet(List<List<Object>> keys) {
        List<Get> gets = new ArrayList<Get>();
        for (int i = 0; i < keys.size(); i++) {
            List<Object> nkey = keys.get(i);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                for (Object key : nkey) {
                    bos.write(String.valueOf(key).getBytes());
                }
                bos.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException creating hbase row key.", e);
            }
            byte[] hbaseKey = bos.toByteArray();
            Get get = new Get(hbaseKey);
            for (String filed : options.getEvent().eventGetFields()) {
                get.addColumn(options.getColumnFamily().getBytes(), filed.getBytes());
            }
            gets.add(get);
        }

        List<T> retval = new ArrayList<T>();
        Result[] results;
        try {
            results = table.get(gets);
        } catch (IOException e) {
            throw new FailedException("IOException while reading from hbase.", e);
        }
        for (int i = 0; i < keys.size(); i++) {
            Class<? extends IEvent> cls = options.getEvent().getClass();
            IEvent nEvent = null;
            try {
                nEvent = cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Result result = results[i];
            ArrayList<String> fields = nEvent.eventGetFields();
            for (int i1 = 0; i1 < fields.size(); i1++) {
                String str = fields.get(i1);
                byte[] value = result.getValue(options.getColumnFamily().getBytes(), str.getBytes());
                if (value != null) {
                    try {
                        nEvent.setValueByField(str, new String(value));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (nEvent != null) {
//                LOG.info("multiGet :" + nEvent.toString());
                retval.add((T) nEvent);
            } else {
                retval.add(null);
            }
        }
        return retval;
    }

    @Override
    public void multiPut(List<List<Object>> keys, List<T> values) {
        List<Put> puts = new ArrayList<Put>();
        for (int i = 0; i < keys.size(); i++) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (int j = 0; j < keys.get(i).size(); j++) {
                try {
                    bos.write(String.valueOf(keys.get(i).get(j)).getBytes());
                    bos.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException creating hbase row key.", e);
                }
            }
            byte[] hbaseKey = bos.toByteArray();
            Put put = null;
            T val = values.get(i);
            if (val != null) {
                IEvent event = (IEvent) val;
                put = new Put(hbaseKey);
//                    LOG.info("multiPut :" + event.toString());
                for (String field : options.getEvent().eventGetFields()) {
                    String value = event.getValueByField(field);
                    if (value != null)
                        put.addColumn(options.getColumnFamily().getBytes(), field.getBytes(), value.getBytes());
                    else
                        put.addColumn(options.getColumnFamily().getBytes(), field.getBytes(), null);
                }
                puts.add(put);

            }
        }
        try {
            this.table.put(puts);
        } catch (InterruptedIOException e) {
            throw new FailedException("Interrupted while writing to hbase", e);
        } catch (RetriesExhaustedWithDetailsException e) {
            throw new FailedException("Retries exhaused while writing to hbase", e);
        } catch (IOException e) {
            throw new FailedException("IOException while writing to hbase", e);
        }
    }
}
