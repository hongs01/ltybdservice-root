package com.lantaiyuan.bean.Hbase;

/**
 * 预测客流信息
 * Created by hongshuai on 2017-11-27.
 */
public class RealStationTable {
    public static final String TABELNAME = "realStationTable";
    public static final String COLUMN_FAMILY_NAME="realStationInfo@";

    private  String lineId;
    private  String direction;
    private  String   stationId;
    private  String  nowtime;
    private  int stagnantTraffic;
    private  int upNumber;
    private  int downNumber;

    public RealStationTable(String lineId,String direction,String stationId, String nowtime, int stagnantTraffic, int upNumber, int downNumber){
            this.lineId=lineId;
            this.direction=direction;
            this.stationId=stationId;
            this.nowtime=nowtime;
            this.stagnantTraffic=stagnantTraffic;
            this.upNumber=upNumber;
            this.downNumber=downNumber;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getNowtime() {
        return nowtime;
    }

    public void setNowtime(String nowtime) {
        this.nowtime = nowtime;
    }

    public int getStagnantTraffic() {
        return stagnantTraffic;
    }

    public void setStagnantTraffic(int stagnantTraffic) {
        this.stagnantTraffic = stagnantTraffic;
    }

    public int getUpNumber() {
        return upNumber;
    }

    public void setUpNumber(int upNumber) {
        this.upNumber = upNumber;
    }

    public int getDownNumber() {
        return downNumber;
    }

    public void setDownNumber(int downNumber) {
        this.downNumber = downNumber;
    }

}
