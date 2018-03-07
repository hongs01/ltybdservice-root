package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.BusGps;
import com.ltybdservice.pre.prebean.LineGps;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 聚合line级移动信息
 * 聚合值：
 */
public class LineGpsAgg implements ReducerAggregator<LineGps> {
    private static final Logger LOG = LoggerFactory.getLogger(LineGpsAgg.class);

    @Override
    public LineGps init() {
        LineGps lineGps = new LineGps();
        return lineGps;
    }

    @Override
    public LineGps reduce(LineGps curr, TridentTuple tuple) {
        BusGps busGps = (BusGps) tuple.getValueByField("busGps");
        String cityCode = (String) tuple.getValueByField("cityCode");
        int lineId = (Integer) tuple.getValueByField("lineId");
        String workDate = (String) tuple.getValueByField("workDate");
        //坐标字段
        curr.setWorkDate(workDate);
        curr.setHalfHour(busGps.getHalfHour());
        curr.setLineId(lineId);
        curr.setCityCode(cityCode);
        curr.setLineName(busGps.getLineName());

        //聚合字段
        //更新速度表1
        curr.getMapBus2BusSpeed().put(busGps.getBusId(), busGps.getBusSpeed());
        int speed1 = 0;
        for (int value :
                curr.getMapBus2BusSpeed().values()) {
            speed1 = speed1 + value;
        }
        curr.setLineAvgSpeed(speed1 / curr.getMapBus2BusSpeed().size());
        //更新速度表2
        curr.getMapBus2TimeAvgSpeed().put(busGps.getBusId(), busGps.getTimeAvgSpeed());
        int speed2 = 0;
        for (int value :
                curr.getMapBus2TimeAvgSpeed().values()) {
            speed2 = speed2 + value;
        }
        curr.setTimeAvgSpeed(speed2 / curr.getMapBus2TimeAvgSpeed().size());
        //更新车辆距离下站的时间表
        curr.getMapBus2NextStopTime().put(busGps.getBusId(), busGps.getToNextStopTime());
        long toNextStopTime = 0;
        for (int value :
                curr.getMapBus2NextStopTime().values()) {
            toNextStopTime = toNextStopTime + value;
        }
        curr.setToNextStopTime((int) toNextStopTime / curr.getMapBus2NextStopTime().size());
        return curr;
    }
}
