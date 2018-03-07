package com.ltybdservice.hiveoptions;

import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.tuple.Fields;

public class GetHiveOptitions {
	public static HiveOptions GetOptitions(String tbName){
		String metaStoreURI = "thrift://master:9083";
        String dbName = "dc";
        // Fields for possible partition
        String[] partNames = {"citycode","workmonth","workdate"};
        String tblName = "";
        String[] colNames = {};
		HiveOptions hiveoptitions = null;
        if(tbName.equals("ods_gpscoord")){
        	tblName = "ods_gpscoord";
            // Fields for possible column data
            String[] cols = {"gprsid", "vendor","terminalno","driverid","time","direction","longitude","latitude"
    				,"kph","azimuth","height","busstatus","stationno","enginetemp","withintemp","gprssignal","runmileag"
    				,"tonextstopdistance","tonextstoptime"};
            colNames = cols;
        }
        else if(tbName.equals("ods_t_s_field")){
        	tblName = "ods_t_s_field";
            // Fields for possible column data
            String[] cols = {"angle", "busstatus","driverid","fieldno","flag","gprsid","kph","latitude"
    				,"longitude","oils","powerconsume","property","terminalno","time"};
            colNames = cols;
        }
        else if(tbName.equals("ods_enterstation")){
        	tblName = "ods_enterstation";
            // Fields for possible column data
            String[] cols = {"busstatus", "direction", "distance","driverid","entertime","gprsid","notifyways","reserved","stationno"
    				,"stationproperty","terminalno","time"};
            colNames = cols;
        }
        else if(tbName.equals("ods_leavestation")){
        	tblName = "ods_leavestation";
            // Fields for possible column data
            String[] cols = {"busstatus", "direction","driverid","entertime","gotincount","gotoutcount","gprsid","leavetime"
            		,"notifyways","stationno","stationproperty","terminalno"};
            colNames = cols;
        }
        else if(tbName.equals("ods_passengerflow")){
        	tblName = "ods_passengerflow";
            // Fields for possible column data
            String[] cols = {"dev_id", "line_id","stat_time","lon","lat","bus_station_code","bus_station_no","vehicle_id"
            		,"direction","working_flag","fdoor_flag","bdoor_flag","online_flag","speed_flag","position_flag",
            		"up_flow","down_flow","total_flow"};
            colNames = cols;
        }
        else if(tbName.equals("ods_in_out_station")){
        	tblName = "ods_in_out_station";
            // Fields for possible column data
            String[] cols = {"dev_id", "line_id","vehicle_id","time","lon","lat","vec1","vec2"
            		,"distance","angle","altitude","vehicle_status","bus_station_code","bus_station_no","driver_id",
            		"station_flag","station_report","dis_next","time_next","next_station_no","in_out_flag","direction",
            		"field_no","field_property"};
            colNames = cols;
        }
        else if(tbName.equals("ods_gps_coordinate")){
        	tblName = "ods_gps_coordinate";
            // Fields for possible column data
            String[] cols = {"dev_id","line_id","direction","vehicle_id","gps_time","lon","lat","vec1"
    				,"vec2","distance","angle","altitude","dis_next","time_next","next_station_no","signal","temperature"};
            colNames = cols;
        }
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper()
        	.withColumnFields(new Fields(colNames))
        	.withPartitionFields(new Fields(partNames));

        hiveoptitions = new HiveOptions(metaStoreURI, dbName, tblName, mapper)
        	.withTxnsPerBatch(6)
        	.withBatchSize(1000)
        	.withIdleTimeout(10);
        return hiveoptitions;
	}	
}
