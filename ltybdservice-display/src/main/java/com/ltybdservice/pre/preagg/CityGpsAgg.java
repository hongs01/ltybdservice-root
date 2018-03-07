package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.CityGps;
import com.ltybdservice.pre.prebean.LineGps;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 聚合city级移动信息
 * 聚合值：
 */
public class CityGpsAgg implements ReducerAggregator<CityGps> {
    private static final Logger LOG = LoggerFactory.getLogger(CityGpsAgg.class);

    @Override
    public CityGps init() {
        CityGps cityGps = new CityGps();
        cityGps.setMax_speed(Integer.MIN_VALUE);
        cityGps.setMin_speed(Integer.MAX_VALUE);
        return cityGps;
    }

    @Override
    public CityGps reduce(CityGps curr, TridentTuple tuple) {
        if (curr.getMin_speed() < 1) curr.setMin_speed(Integer.MAX_VALUE);
        LineGps lineGps = (LineGps) tuple.getValueByField("lineGps");
        String cityCode = (String) tuple.getValueByField("cityCode");
        String workDate = (String) tuple.getValueByField("workDate");
        //坐标字段
        curr.setCityCode(cityCode);
        curr.setWorkDate(workDate);
        curr.setHalfHour(lineGps.getHalfHour());
        //聚合字段
        //最大速度线路
        if (lineGps.getLineAvgSpeed() < curr.getMin_speed()) {
            curr.setMin_speed(lineGps.getLineAvgSpeed());
            curr.setMin_speed_line_name(lineGps.getLineName());
        }
        //最小速度线路
        if (lineGps.getLineAvgSpeed() > curr.getMax_speed()) {
            curr.setMax_speed(lineGps.getLineAvgSpeed());
            curr.setMax_speed_line_name(lineGps.getLineName());
        }

        //平均线路速度作为城市速度1
        curr.getMapLineId2LineAvgSpeed().put(lineGps.getLineId(), lineGps.getLineAvgSpeed());
        int totalSpeed1 = 0;
        for (int lineSpeed :
                curr.getMapLineId2LineAvgSpeed().values()) {
            totalSpeed1 = totalSpeed1 + lineSpeed;
        }
        curr.setCityAvgSpeed((int) totalSpeed1 / curr.getMapLineId2LineAvgSpeed().size());
        //平均线路速度作为城市速度2
        curr.getMapLineId2TimeAvgSpeed().put(lineGps.getLineId(), lineGps.getTimeAvgSpeed());
        int totalSpeed2 = 0;
        for (int lineSpeed :
                curr.getMapLineId2TimeAvgSpeed().values()) {
            totalSpeed2 = totalSpeed2 + lineSpeed;
        }
        curr.setTimeAvgSpeed((int) totalSpeed2 / curr.getMapLineId2TimeAvgSpeed().size());
        //平均线路距离下站时间，作为城市距离下站时间
        curr.getMapLineId2NextStopTime().put(lineGps.getLineId(), lineGps.getToNextStopTime());
        long totalToNextStopTime = 0l;
        for (int lineToNextStopTime :
                curr.getMapLineId2NextStopTime().values()) {
            totalToNextStopTime = totalToNextStopTime + lineToNextStopTime;
        }
        curr.setToNextStopTime((int) totalToNextStopTime / curr.getMapLineId2NextStopTime().size());
        return curr;
    }
}
