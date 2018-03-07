package com.ltybdservice.bean;
/*APP提交的拼车需求信息*/
/**
 * @auther zhouzefei
 */
public class BuspoolingDemandInfo {
	//kafka里能获取到的信息
	private String orderNo;  //支付的订单号
    private int userId;  //用户id 
    private Double paidPrice ;  //用户支付金额    【app预估金额】
    private String startPlaceName;  //起点名称
    private float startPlaceLon;  //起点经度
    private float startPlaceLat; //起点纬度
    private String endPlaceName;   //终点名称
    private float endPlaceLon;  //终点经度
    private float endPlaceLat;  //终点纬度
    private int seats;  //座位数        【用户人数】
    private int maxWalkDistance;  //可接受的最长步行距离(单位: 米)
    private String earliestStartTime;  //最早出发时间 【date+time】
    private String latestStartTime;  //最晚出发时间 【date+time】
    private int isRegular;  //是否规律性【专线+拼车】
    private String regularDetail;  //如果是规律性（工作日，非工作日） 
    private String cityCode;  //城市编码
    
    //查询公交线路后补充的信息
    private String busLine;  //查询高德返回的公交信息
    private String busId;  //查询高德返回的公交ID
    private String realStartName; //查询高德后返回的上车地点名称
    private String realEndName;  //查询高德后返回的下车地点名称
    private float realStartlon; //查询高德后返回的上车起点经度
    private float realStartlat; //查询高德后返回的上车起点纬度
    private float realEndlon; //查询高德后返回的下车车起点经度
    private float realEndlat; //查询高德后返回的下车起点纬度
    private String distance; //查询高德后返回的里程数
    private String duration; //查询高德后返回的耗时
    
    
    private long arrivalTime = 0L; //预计上车时间
    private long downTime = 0L;  //预计下车时间
    private long  startStationNO; //起点站序
    private long  endStationNO; //终点站序
    
    
    public BuspoolingDemandInfo() {
        
    }
     
    
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public Double getPaidPrice() {
		return paidPrice;
	}
	public void setPaidPrice(Double paidPrice) {
		this.paidPrice = paidPrice;
	}
	
	public String getStartPlaceName() {
		return startPlaceName;
	}
	public void setStartPlaceName(String startPlaceName) {
		this.startPlaceName = startPlaceName;
	}
	
	public float getStartPlaceLon() {
		return startPlaceLon;
	}
	public void setStartPlaceLon(float startPlaceLon) {
		this.startPlaceLon = startPlaceLon;
	}
	
	public float getStartPlaceLat() {
		return startPlaceLat;
	}
	public void setStartPlaceLat(float startPlaceLat) {
		this.startPlaceLat = startPlaceLat;
	}
	
	public String getEndPlaceName() {
		return endPlaceName;
	}
	public void setEndPlaceName(String endPlaceName) {
		this.endPlaceName = endPlaceName;
	}
	
	public float getEndPlaceLon() {
		return endPlaceLon;
	}
	public void setEndPlaceLon(float endPlaceLon) {
		this.endPlaceLon = endPlaceLon;
	}
	
	public int getSeats() {
		return seats;
	}
	public void setSeats(int seats) {
		this.seats = seats;
	}
	
	public float getEndPlaceLat() {
		return endPlaceLat;
	}
	public void setEndPlaceLat(float endPlaceLat) {
		this.endPlaceLat = endPlaceLat;
	}
	
	public String getEarliestStartTime() {
		return earliestStartTime;
	}
	public void setEarliestStartTime(String earliestStartTime) {
		this.earliestStartTime = earliestStartTime;
	}
	
	public int getMaxWalkDistance() {
		return maxWalkDistance;
	}
	public void setMaxWalkDistance(int maxWalkDistance) {
		this.maxWalkDistance = maxWalkDistance;
	}
	
	public int getIsRegular() {
		return isRegular;
	}
	public void setIsRegular(int isRegular) {
		this.isRegular = isRegular;
	}
	
	public String getLatestStartTime() {
		return latestStartTime;
	}
	public void setLatestStartTime(String latestStartTime) {
		this.latestStartTime = latestStartTime;
	}
	
	public String getRegularDetail() {
		return regularDetail;
	}
	public void setRegularDetail(String regularDetail) {
		this.regularDetail = regularDetail;
	}
	
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}


	public String getBusLine() {
		return busLine;
	}


	public void setBusLine(String busLine) {
		this.busLine = busLine;
	}


	public String getRealStartName() {
		return realStartName;
	}


	public void setRealStartName(String realStartName) {
		this.realStartName = realStartName;
	}


	public String getRealEndName() {
		return realEndName;
	}


	public void setRealEndName(String realEndName) {
		this.realEndName = realEndName;
	}


	public float getRealStartlon() {
		return realStartlon;
	}


	public void setRealStartlon(float realStartlon) {
		this.realStartlon = realStartlon;
	}
	
	public float getRealStartlat() {
		return realStartlat;
	}


	public void setRealStartlat(float realStartlat) {
		this.realStartlat = realStartlat;
	}
	
	public float getRealEndlon() {
		return realEndlon;
	}


	public void setRealEndlon(float realEndlon) {
		this.realEndlon = realEndlon;
	}
	
	public float getRealEndlat() {
		return realEndlat;
	}


	public void setRealEndlat(float realEndlat) {
		this.realEndlat = realEndlat;
	}


	public long getArrivalTime() {
		return arrivalTime;
	}


	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}


	public long getStartStationNO() {
		return startStationNO;
	}


	public void setStartStationNO(long startStationNO) {
		this.startStationNO = startStationNO;
	}


	public long getEndStationNO() {
		return endStationNO;
	}


	public void setEndStationNO(long endStationNO) {
		this.endStationNO = endStationNO;
	}


	public String getDistance() {
		return distance;
	}


	public void setDistance(String distance) {
		this.distance = distance;
	}


	public long getDownTime() {
		return downTime;
	}


	public void setDownTime(long downTime) {
		this.downTime = downTime;
	}


	public String getDuration() {
		return duration;
	}


	public void setDuration(String duration) {
		this.duration = duration;
	}


	public String getBusId() {
		return busId;
	}


	public void setBusId(String busId) {
		this.busId = busId;
	}
    
}
