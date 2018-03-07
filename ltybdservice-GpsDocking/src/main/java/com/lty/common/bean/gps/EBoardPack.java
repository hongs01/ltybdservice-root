/**
* <p>Title: EBoardPack.java</p>
* <p>Copyright: Copyright (c) 2016</p>
* <p>Company: lty</p>
*/
package com.lty.common.bean.gps;

import com.lty.common.bean.mq.MQBody;

/**
* <p>Title: EBoardPack</p>
* <p>Description: gps数据包实体类</p>
* <p>Company: lty</p>
* @author jiaweichen
* @date 2016年11月14日 上午10:19:55
* version 1.0
*/
public class EBoardPack extends GatewayPack implements MQBody{
	private int lineId;//线路
	private int carId;//车辆
	private int driverId;//司机
	private int stationId;//最近经过的站点
	private String dateTime;//时间
	private String longtitude;//经度
	private String latitude;//纬度
	private int velocity;//时速
	private int angle;//方位角
	private int height;//高度
	private int status;//车辆状态
	private int engineTp;//发动机温度
	private int carriageTp;//车厢内温度
	private int mileage;//行驶里程
	private int realTrip;//实际趟次
	private int planTrip;//计划趟次
	private String realDate;//实际发车时间
	private int runFlag;//运行标记

	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public int getVelocity() {
		return velocity;
	}
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	public int getAngle() {
		return angle;
	}
	public void setAngle(int angle) {
		this.angle = angle;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getEngineTp() {
		return engineTp;
	}
	public void setEngineTp(int engineTp) {
		this.engineTp = engineTp;
	}
	public int getCarriageTp() {
		return carriageTp;
	}
	public void setCarriageTp(int carriageTp) {
		this.carriageTp = carriageTp;
	}
	public int getMileage() {
		return mileage;
	}
	public void setMileage(int mileage) {
		this.mileage = mileage;
	}
	public int getRealTrip() {
		return realTrip;
	}
	public void setRealTrip(int realTrip) {
		this.realTrip = realTrip;
	}
	public int getPlanTrip() {
		return planTrip;
	}
	public void setPlanTrip(int planTrip) {
		this.planTrip = planTrip;
	}
	public String getRealDate() {
		return realDate;
	}
	public void setRealDate(String realDate) {
		this.realDate = realDate;
	}
	public int getRunFlag() {
		return runFlag;
	}
	public void setRunFlag(int runFlag) {
		this.runFlag = runFlag;
	}


	@Override
	public String toString() {
		return "EBoardPack{" +
				"lineId=" + lineId +
				", carId=" + carId +
				", driverId=" + driverId +
				", stationId=" + stationId +
				", dateTime='" + dateTime + '\'' +
				", longtitude=" + longtitude +
				", latitude=" + latitude +
				", velocity=" + velocity +
				", angle=" + angle +
				", height=" + height +
				", status=" + status +
				", engineTp=" + engineTp +
				", carriageTp=" + carriageTp +
				", mileage=" + mileage +
				", realTrip=" + realTrip +
				", planTrip=" + planTrip +
				", realDate='" + realDate + '\'' +
				", runFlag=" + runFlag +
				'}';
	}
}
