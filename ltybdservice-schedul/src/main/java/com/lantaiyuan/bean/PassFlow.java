package com.lantaiyuan.bean;

import java.io.Serializable;

public class PassFlow implements Serializable {
  //设备ID
  private	int dev_id;
  //城市编号
  private String city_code;
//客流时间
  private long stat_time;
  //经度
  private double lon;
  //纬度
  private double lat;
  //城市公交站点编码
  private int  bus_station_code;
  //站点序号
  private int bus_station_no;
  //线路ID
  private int line_id;
  //车辆编号
  private int vehicle_id;
  //0:上行 1:下行
  private int direction;
  //0:未营运 1:营运
  private int working_flag;
  //0:前门关 1:前门开
  private int fdoor_flag;
  //0:后门关 1:后门开
  private int bdoor_flag;
  //0:正常行驶 1:离线行驶
  private int online_flag;
  //0:未超速 1:超速中
  private int speed_flag;
  //0:站场外 1:站场内
  private int position_flag;
  //5秒之内上的客流
  private int up_flow;
  //5秒之内下的客流
  private int down_flow;
  //车上总人数
  private int total_flow;
  
    public int getDev_id() {
		return dev_id;
	}
	public void setDev_id(int dev_id) {
		this.dev_id = dev_id;
	}
	public String getCity_code() {
	  return city_code;
	}
	public void setCity_code(String city_code) {
	  this.city_code = city_code;
	}
	public long getStat_time() {
		return stat_time;
	}
	public void setStat_time(long stat_time) {
		this.stat_time = stat_time;
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
	public int getBus_station_code() {
		return bus_station_code;
	}
	public void setBus_station_code(int bus_station_code) {
		this.bus_station_code = bus_station_code;
	}
	public int getBus_station_no() {
		return bus_station_no;
	}
	public void setBus_station_no(int bus_station_no) {
		this.bus_station_no = bus_station_no;
	}
	public int getLine_id() {
		return line_id;
	}
	public void setLine_id(int line_id) {
		this.line_id = line_id;
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
}
