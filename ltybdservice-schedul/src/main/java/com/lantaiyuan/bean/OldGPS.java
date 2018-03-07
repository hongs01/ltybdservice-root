package com.lantaiyuan.bean;

/**
 * @author Administrator
 *gps坐标信息
 */
public class OldGPS {
   //城市ID
	private String cityCode;
	//线路gprsId
	private int gprsId;
	//厂商编号
	private String vendor;
	//设备编号
	private int terminalNo;
	//数据包内容中的保留内容
	private String nouse;
	//司机工号
	private String driverId;
	//发生时间
	private long time;
	//经度
	private double longitude;
	//纬度
	private double latitude;
	//时速
	private int kph;
	//方位角
	private float azimuth;
	//高度
	private int height;
	//车辆状态
	private int busStatus;
	//下一站序
	private int stationNo;
	//发动机温度
	private float engineTemp;
	//车内温度
	private float withinTemp;
	//信号强度
	private int GPRSSignal;
	//当天行驶里程
	private float runMileag;
	//距离下一站距离
	private int toNextStopDistance;
	//距离下一站时间(单位秒)
	private int toNextStopTime;
	//方向
	private int direction;
	//
	private int gPRSSignal;
	//
	private String workDate;
	public String getWorkDate() {
		return workDate;
	}
	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}
	public String getWorkMonth() {
		return workMonth;
	}
	public void setWorkMonth(String workMonth) {
		this.workMonth = workMonth;
	}
	//
	private String workMonth;
	
	public int getgPRSSignal() {
		return gPRSSignal;
	}
	public void setgPRSSignal(int gPRSSignal) {
		this.gPRSSignal = gPRSSignal;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCitycode(String citycode) {
		this.cityCode = citycode;
	}
	public int getGprsId() {
		return gprsId;
	}
	public void setGprsId(int gprsId) {
		this.gprsId = gprsId;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public int getTerminalNo() {
		return terminalNo;
	}
	public void setTerminalNo(int terminalNo) {
		this.terminalNo = terminalNo;
	}
	public String getNouse() {
		return nouse;
	}
	public void setNouse(String nouse) {
		this.nouse = nouse;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getKph() {
		return kph;
	}
	public void setKph(int kph) {
		this.kph = kph;
	}
	public float getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getBusStatus() {
		return busStatus;
	}
	public void setBusStatus(int busStatus) {
		this.busStatus = busStatus;
	}
	public int getStationNo() {
		return stationNo;
	}
	public void setStationNo(int stationNo) {
		this.stationNo = stationNo;
	}
	public float getEngineTemp() {
		return engineTemp;
	}
	public void setEngineTemp(float engineTemp) {
		this.engineTemp = engineTemp;
	}
	public float getWithinTemp() {
		return withinTemp;
	}
	public void setWithinTemp(float withinTemp) {
		this.withinTemp = withinTemp;
	}
	public int getGPRSSignal() {
		return GPRSSignal;
	}
	public void setGPRSSignal(int gPRSSignal) {
		GPRSSignal = gPRSSignal;
	}
	public float getRunMileag() {
		return runMileag;
	}
	public void setRunMileag(float runMileag) {
		this.runMileag = runMileag;
	}
	public int getToNextStopDistance() {
		return toNextStopDistance;
	}
	public void setToNextStopDistance(int toNextStopDistance) {
		this.toNextStopDistance = toNextStopDistance;
	}
	public int getToNextStopTime() {
		return toNextStopTime;
	}
	public void setToNextStopTime(int toNextStopTime) {
		this.toNextStopTime = toNextStopTime;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
}
