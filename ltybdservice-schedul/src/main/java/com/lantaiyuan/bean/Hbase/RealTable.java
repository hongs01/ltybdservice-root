package com.lantaiyuan.bean.Hbase;


/**
 * 预测客流信息
 * Created by zhouyongbo on 2017-11-22.
 */
public class RealTable {
    public static final String TABELNAME = "realTable";
    public static final String LINE_ZU_NAME="realInfo@";

    private String lineId;
    private String nowHour;
    private Long dictPassengerFlowSum;

    /*实时线路构造方法*/
    public RealTable(String lineId, String nowHour, Long  dictPassengerFlowSum) {
        this.lineId = lineId;
        this.nowHour = nowHour;
        this.dictPassengerFlowSum = dictPassengerFlowSum;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getNowHour() {
        return nowHour;
    }

    public void setNowHour(String nowHour) {
        this.nowHour = nowHour;
    }

    public Long getDictPassengerFlowSum() {
        return dictPassengerFlowSum;
    }

    public void setDictPassengerFlowSum(Long dictPassengerFlowSum) {
        this.dictPassengerFlowSum = dictPassengerFlowSum;
    }
}
