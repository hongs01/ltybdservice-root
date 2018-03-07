package com.ltybdservice.pre.prebean;

import com.ltybdservice.srcbean.SrcBusPsg;
import com.ltybdservice.hiveutil.DataSourceConf;
import com.ltybdservice.hiveutil.DcDataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 新增字段
 */
public class RawBusPsg implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RawBusPsg.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private static final DecimalFormat df = new DecimalFormat("#.0000");
    //坐标字段
    private long time;//事件时间，13位
    private String workDate;//日20170809
    private String cityCode;//城市id 6位
    private int busId;// 公交车ID（设备id）
    private int lineId;   //线路id
    private String lineName;   //线路名
    private int stationId;// UINT,城市公交站点编码
    private String stationName;    //	站点名称
    //信息字段
    private double lon;//	double	经度
    private double lat;//	double	纬度
    private int bus_station_no;//UINT	站点序号
    private int vehicle_id;//UINT	车辆编号
    private int direction;//UINT	0:上行 1:下行
    private int working_flag;//UINT	0:未营运 1:营运
    private int fdoor_flag;//UINT	0:前门关 1:前门开
    private int bdoor_flag;//UINT	0:后门关 1:后门开
    private int online_flag;//UINT	0:正常行驶 1:离线行驶
    private int speed_flag;//UINT	0:未超速 1:超速中
    private int position_flag;//UINT	0:站场外 1:站场内
    private int up_flow;//UINT	5秒之内上的客流
    private int down_flow;//UINT	5秒之内下的客流
    private int total_flow;//UINT	车上总人数
    //新增字段
    private int seatNumber;  //车辆座位数，新增字段
    private double crowd;     //拥挤度,新增字段

    public static RawBusPsg transFromBusPsg(DataSourceConf conf, SrcBusPsg srcBusPsg) {
        RawBusPsg rawBusPsg = new RawBusPsg();
        SrcBusPsg.Body body = srcBusPsg.getBody();
        rawBusPsg.time = body.getStat_time() * 1000;
        rawBusPsg.workDate = formatter.format(new Date(rawBusPsg.time));
        rawBusPsg.cityCode = body.getCity_code();
        rawBusPsg.busId = body.getDev_id();
        rawBusPsg.lineId = body.getLine_id();
        try {
            rawBusPsg.lineName = DcDataBaseUtil.getDcConnectionInstance(conf).lineId2lineName(rawBusPsg.cityCode, rawBusPsg.lineId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        rawBusPsg.stationId = body.getBus_station_code();
        try {
            rawBusPsg.stationName = DcDataBaseUtil.getDcConnectionInstance(conf).stationId2StationName(rawBusPsg.cityCode, rawBusPsg.stationId);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rawBusPsg.lon = body.getLon();
        rawBusPsg.lat = body.getLat();
        rawBusPsg.bus_station_no = body.getBus_station_no();
        rawBusPsg.vehicle_id = body.getVehicle_id();
        rawBusPsg.direction = body.getDirection();
        rawBusPsg.working_flag = body.getWorking_flag();
        rawBusPsg.fdoor_flag = body.getFdoor_flag();
        rawBusPsg.bdoor_flag = body.getBdoor_flag();
        rawBusPsg.online_flag = body.getOnline_flag();
        rawBusPsg.speed_flag = body.getSpeed_flag();
        rawBusPsg.position_flag = body.getPosition_flag();
        rawBusPsg.up_flow = body.getUp_flow();
        rawBusPsg.down_flow = body.getDown_flow();
        rawBusPsg.total_flow = body.getTotal_flow();

        try {
            rawBusPsg.seatNumber = DcDataBaseUtil.getDcConnectionInstance(conf).devId2SeatNumber(rawBusPsg.cityCode, rawBusPsg.busId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        if (rawBusPsg.seatNumber != 0)
            rawBusPsg.crowd = Double.parseDouble(df.format((double) rawBusPsg.total_flow / rawBusPsg.seatNumber));
        return rawBusPsg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getBus_station_no() {
        return bus_station_no;
    }

    public void setBus_station_no(int bus_station_no) {
        this.bus_station_no = bus_station_no;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getWorking_flag() {
        return working_flag;
    }

    public void setWorking_flag(int working_flag) {
        this.working_flag = working_flag;
    }

    public int getFdoor_flag() {
        return fdoor_flag;
    }

    public void setFdoor_flag(int fdoor_flag) {
        this.fdoor_flag = fdoor_flag;
    }

    public int getBdoor_flag() {
        return bdoor_flag;
    }

    public void setBdoor_flag(int bdoor_flag) {
        this.bdoor_flag = bdoor_flag;
    }

    public int getOnline_flag() {
        return online_flag;
    }

    public void setOnline_flag(int online_flag) {
        this.online_flag = online_flag;
    }

    public int getSpeed_flag() {
        return speed_flag;
    }

    public void setSpeed_flag(int speed_flag) {
        this.speed_flag = speed_flag;
    }

    public int getPosition_flag() {
        return position_flag;
    }

    public void setPosition_flag(int position_flag) {
        this.position_flag = position_flag;
    }

    public int getUp_flow() {
        return up_flow;
    }

    public void setUp_flow(int up_flow) {
        this.up_flow = up_flow;
    }

    public int getDown_flow() {
        return down_flow;
    }

    public void setDown_flow(int down_flow) {
        this.down_flow = down_flow;
    }

    public int getTotal_flow() {
        return total_flow;
    }

    public void setTotal_flow(int total_flow) {
        this.total_flow = total_flow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getCrowd() {
        return crowd;
    }

    public void setCrowd(double crowd) {
        this.crowd = crowd;
    }
}
