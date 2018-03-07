package com.ltybdservice.config;

import com.ltybdservice.hbaseutil.HbaseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class HbaseTableConfig implements Serializable {
    //hbase table name
    private String tablePrefix;//压力测试使用
    private String dayPrefix;
    private String halfHourPrefix;
    private String columnFamily;
    private String BusPsgTable;
    private String StationPsgTable;
    private String LinePsgTable;
    private String CityPsgTable;
    private String BDS10LinePsgTable;
    private String BDS10StationPsgTable;
    private String BDSAllLinePsgTable;
    private String BDSAllStationPsgTable;
    private String BDSTimeCityPsgTable;
    private String BusGpsTable;
    private String LineGpsTable;
    private String CityGpsTable;
    private String BDSBusGpsTable;
    private String BDSCityGpsTable;
    private String BDSTimeCityGpsTable;
    //day table
    private String DayBusPsgTable;
    private String DayStationPsgTable;
    private String DayLinePsgTable;
    private String DayCityPsgTable;
    private String DayBDS10LinePsgTable;
    private String DayBDS10StationPsgTable;
    private String DayBDSAllLinePsgTable;
    private String DayBDSAllStationPsgTable;
    private String DayBDSTimeCityPsgTable;
    private String DayBusGpsTable;
    private String DayLineGpsTable;
    private String DayCityGpsTable;
    private String DayBDSBusGpsTable;
    private String DayBDSCityGpsTable;
    private String DayBDSTimeCityGpsTable;
    //halfHour table
    private String HalfHourBusPsgTable;
    private String HalfHourLinePsgTable;
    private String HalfHourStationPsgTable;
    private String HalfHourCityPsgTable;
    private String HalfHourBDS10LinePsgTable;
    private String HalfHourBDS10StationPsgTable;
    private String HalfHourBDSAllLinePsgTable;
    private String HalfHourBDSAllStationPsgTable;
    private String HalfHourBDSTimeCityPsgTable;
    private String HalfHourBusGpsTable;
    private String HalfHourLineGpsTable;
    private String HalfHourCityGpsTable;
    private String HalfHourBDSBusGpsTable;
    private String HalfHourBDSCityGpsTable;
    private String HalfHourBDSTimeCityGpsTable;

    public HbaseTableConfig() {
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/config.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig(pro);
    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:40
     * @method: getConfig
     * @param: [pro]
     * @desciption:
     */
    private void getConfig(Properties pro) {
        tablePrefix = pro.getProperty("tablePrefix");//压力测试使用
        dayPrefix = pro.getProperty("dayPrefix");
        halfHourPrefix = pro.getProperty("halfHourPrefix");
        columnFamily = pro.getProperty("columnFamily");
        //day table
        BusPsgTable = pro.getProperty("BusPsgTable");
        StationPsgTable = pro.getProperty("StationPsgTable");
        LinePsgTable = pro.getProperty("LinePsgTable");
        CityPsgTable = pro.getProperty("CityPsgTable");
        BDS10LinePsgTable = pro.getProperty("BDS10LinePsgTable");
        BDS10StationPsgTable = pro.getProperty("BDS10StationPsgTable");
        BDSAllLinePsgTable = pro.getProperty("BDSAllLinePsgTable");
        BDSAllStationPsgTable = pro.getProperty("BDSAllStationPsgTable");
        BDSTimeCityPsgTable = pro.getProperty("BDSTimeCityPsgTable");
        BusGpsTable = pro.getProperty("BusGpsTable");
        LineGpsTable = pro.getProperty("LineGpsTable");
        CityGpsTable = pro.getProperty("CityGpsTable");
        BDSBusGpsTable = pro.getProperty("BDSBusGpsTable");
        BDSCityGpsTable = pro.getProperty("BDSCityGpsTable");
        BDSTimeCityGpsTable = pro.getProperty("BDSTimeCityGpsTable");
        //day table
        DayBusPsgTable = tablePrefix + dayPrefix + BusPsgTable;
        DayStationPsgTable = tablePrefix + dayPrefix + StationPsgTable;
        DayLinePsgTable = tablePrefix + dayPrefix + LinePsgTable;
        DayCityPsgTable = tablePrefix + dayPrefix + CityPsgTable;
        DayBDS10LinePsgTable = tablePrefix + dayPrefix + BDS10LinePsgTable;
        DayBDS10StationPsgTable = tablePrefix + dayPrefix + BDS10StationPsgTable;
        DayBDSAllLinePsgTable = tablePrefix + dayPrefix + BDSAllLinePsgTable;
        DayBDSAllStationPsgTable = tablePrefix + dayPrefix + BDSAllStationPsgTable;
        DayBDSTimeCityPsgTable = tablePrefix + dayPrefix + BDSTimeCityPsgTable;
        DayBusGpsTable = tablePrefix + dayPrefix + BusGpsTable;
        DayLineGpsTable = tablePrefix + dayPrefix + LineGpsTable;
        DayCityGpsTable = tablePrefix + dayPrefix + CityGpsTable;
        DayBDSBusGpsTable = tablePrefix + dayPrefix + BDSBusGpsTable;
        DayBDSCityGpsTable = tablePrefix + dayPrefix + BDSCityGpsTable;
        DayBDSTimeCityGpsTable = tablePrefix + dayPrefix + BDSTimeCityGpsTable;
        //halfHour table
        HalfHourBusPsgTable = tablePrefix + halfHourPrefix + BusPsgTable;
        HalfHourLinePsgTable = tablePrefix + halfHourPrefix + LinePsgTable;
        HalfHourStationPsgTable = tablePrefix + halfHourPrefix + StationPsgTable;
        HalfHourCityPsgTable = tablePrefix + halfHourPrefix + CityPsgTable;
        HalfHourBDS10LinePsgTable = tablePrefix + halfHourPrefix + BDS10LinePsgTable;
        HalfHourBDS10StationPsgTable = tablePrefix + halfHourPrefix + BDS10StationPsgTable;
        HalfHourBDSAllLinePsgTable = tablePrefix + halfHourPrefix + BDSAllLinePsgTable;
        HalfHourBDSAllStationPsgTable = tablePrefix + halfHourPrefix + BDSAllStationPsgTable;
        HalfHourBDSTimeCityPsgTable = tablePrefix + halfHourPrefix + BDSTimeCityPsgTable;
        HalfHourBusGpsTable = tablePrefix + halfHourPrefix + BusGpsTable;
        HalfHourLineGpsTable = tablePrefix + halfHourPrefix + LineGpsTable;
        HalfHourCityGpsTable = tablePrefix + halfHourPrefix + CityGpsTable;
        HalfHourBDSBusGpsTable = tablePrefix + halfHourPrefix + BDSBusGpsTable;
        HalfHourBDSCityGpsTable = tablePrefix + halfHourPrefix + BDSCityGpsTable;
        HalfHourBDSTimeCityGpsTable = tablePrefix + halfHourPrefix + BDSTimeCityGpsTable;
    }

    /**
     * @return type: void
     * @author: Administrator
     * @date: 2017/11/22 16:40
     * @method: createHbaseTable
     * @param: []
     * @desciption:
     */
    public void createHbaseTable() {
        HbaseUtil connectionInstance = HbaseUtil.getConnectionInstance();
        try {
            //halfHour table
            connectionInstance.createSchemaTables(DayBusPsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayStationPsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayLinePsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayCityPsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDS10LinePsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDS10StationPsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSAllLinePsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSAllStationPsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSTimeCityPsgTable, columnFamily);
            connectionInstance.createSchemaTables(DayBusGpsTable, columnFamily);
            connectionInstance.createSchemaTables(DayLineGpsTable, columnFamily);
            connectionInstance.createSchemaTables(DayCityGpsTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSBusGpsTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSCityGpsTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSCityGpsTable, columnFamily);
            connectionInstance.createSchemaTables(DayBDSTimeCityGpsTable, columnFamily);
            //halfHour table
            connectionInstance.createSchemaTables(HalfHourBusPsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourStationPsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourLinePsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourCityPsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDS10LinePsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDS10StationPsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSAllLinePsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSAllStationPsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSTimeCityPsgTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBusGpsTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourLineGpsTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourCityGpsTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSBusGpsTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSCityGpsTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSCityGpsTable, columnFamily);
            connectionInstance.createSchemaTables(HalfHourBDSTimeCityGpsTable, columnFamily);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionInstance.closeConnection();
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getDayPrefix() {
        return dayPrefix;
    }

    public String getHalfHourPrefix() {
        return halfHourPrefix;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public String getDayBusPsgTable() {
        return DayBusPsgTable;
    }

    public String getDayStationPsgTable() {
        return DayStationPsgTable;
    }

    public String getDayLinePsgTable() {
        return DayLinePsgTable;
    }

    public String getDayCityPsgTable() {
        return DayCityPsgTable;
    }

    public String getDayBDS10LinePsgTable() {
        return DayBDS10LinePsgTable;
    }

    public String getDayBDS10StationPsgTable() {
        return DayBDS10StationPsgTable;
    }

    public String getDayBDSAllLinePsgTable() {
        return DayBDSAllLinePsgTable;
    }

    public String getDayBDSAllStationPsgTable() {
        return DayBDSAllStationPsgTable;
    }

    public String getDayBDSTimeCityPsgTable() {
        return DayBDSTimeCityPsgTable;
    }

    public String getDayBusGpsTable() {
        return DayBusGpsTable;
    }

    public String getDayLineGpsTable() {
        return DayLineGpsTable;
    }

    public String getDayCityGpsTable() {
        return DayCityGpsTable;
    }

    public String getDayBDSBusGpsTable() {
        return DayBDSBusGpsTable;
    }

    public String getDayBDSCityGpsTable() {
        return DayBDSCityGpsTable;
    }

    public String getDayBDSTimeCityGpsTable() {
        return DayBDSTimeCityGpsTable;
    }

    public String getHalfHourBusPsgTable() {
        return HalfHourBusPsgTable;
    }

    public String getHalfHourLinePsgTable() {
        return HalfHourLinePsgTable;
    }

    public String getHalfHourStationPsgTable() {
        return HalfHourStationPsgTable;
    }

    public String getHalfHourCityPsgTable() {
        return HalfHourCityPsgTable;
    }

    public String getHalfHourBDS10LinePsgTable() {
        return HalfHourBDS10LinePsgTable;
    }

    public String getHalfHourBDS10StationPsgTable() {
        return HalfHourBDS10StationPsgTable;
    }

    public String getHalfHourBDSAllLinePsgTable() {
        return HalfHourBDSAllLinePsgTable;
    }

    public String getHalfHourBDSAllStationPsgTable() {
        return HalfHourBDSAllStationPsgTable;
    }

    public String getHalfHourBDSTimeCityPsgTable() {
        return HalfHourBDSTimeCityPsgTable;
    }

    public String getHalfHourBusGpsTable() {
        return HalfHourBusGpsTable;
    }

    public String getHalfHourLineGpsTable() {
        return HalfHourLineGpsTable;
    }

    public String getHalfHourCityGpsTable() {
        return HalfHourCityGpsTable;
    }

    public String getHalfHourBDSBusGpsTable() {
        return HalfHourBDSBusGpsTable;
    }

    public String getHalfHourBDSCityGpsTable() {
        return HalfHourBDSCityGpsTable;
    }

    public String getHalfHourBDSTimeCityGpsTable() {
        return HalfHourBDSTimeCityGpsTable;
    }
}
