package com.ltybdservice.dao

import com.ltybdservice.caseclass.GpsInfo
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.ForeachWriter


class HbaseWriter extends ForeachWriter[GpsInfo]{
  var conn: Connection = null
  var tableName:TableName = null
  var table: Table = null
  var put:Put=null
  override def open(partitionId: Long, version: Long): Boolean = {
    val conf = HBaseConfiguration.create()
    conn = ConnectionFactory.createConnection(conf)
    tableName=TableName.valueOf("bus_gps_info")
    table=conn.getTable(tableName)
    true
  }

  override def process(value: GpsInfo): Unit = {
    put = new Put(Bytes.toBytes(value.rowKey))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("devSn"),Bytes.toBytes(value.devSn))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("devId"),Bytes.toBytes(value.devId))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cityCode"),Bytes.toBytes(value.cityCode))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("companyCode"),Bytes.toBytes(value.companyCode))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("lineId"),Bytes.toBytes(value.lineId))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("gpsTime"),Bytes.toBytes(value.gpsTime))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("lon"),Bytes.toBytes(value.lon))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("lat"),Bytes.toBytes(value.lat))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("angle"),Bytes.toBytes(value.angle))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("altitude"),Bytes.toBytes(value.altitude))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("speed"),Bytes.toBytes(value.speed))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("distance"),Bytes.toBytes(value.distance))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("disNext"),Bytes.toBytes(value.disNext))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("timeNext"),Bytes.toBytes(value.timeNext))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("nextStationNo"),Bytes.toBytes(value.nextStationNo))
    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("vehicleStatus"),Bytes.toBytes(value.vehicleStatus))
    table.put(put);
  }

  override def close(errorOrNull: Throwable): Unit = {
    if(errorOrNull!=null){
      errorOrNull.printStackTrace()
      conn.close()
    }
  }
}
