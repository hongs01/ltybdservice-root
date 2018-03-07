package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.BusGps;
import com.ltybdservice.pre.prebean.RawBusGps;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 聚合bus级移动信息
 * 聚合值：
 * 平均速度：avgSpeed
 */
public class BusGpsAgg implements ReducerAggregator<BusGps> {
    private static final Logger LOG = LoggerFactory.getLogger(BusGpsAgg.class);

    @Override
    public BusGps init() {
        BusGps busGps = new BusGps();
        busGps.setTimeAvgSpeed(0);
        busGps.setCount(0);
        return busGps;
    }

    @Override
    public BusGps reduce(BusGps curr, TridentTuple tuple) {
        RawBusGps rawBusGps = (RawBusGps) tuple.getValueByField("str");
        String workDate = (String) tuple.getValueByField("workDate");
        String halfHour = (String) tuple.getValueByField("halfHour");
        //坐标字段
        curr.getBaseBusGps(rawBusGps);
        //信息字段
        curr.setTime(rawBusGps.getTime());
        curr.setWorkDate(workDate);
        curr.setHalfHour(halfHour);
        curr.setBusSpeed(rawBusGps.getVec1());
        curr.setToNextStopTime(rawBusGps.getTime_next());
        curr.setLongitude(rawBusGps.getLon());
        curr.setLatitude(rawBusGps.getLat());
        curr.setDirection(rawBusGps.getDirection());
        curr.setAngle(rawBusGps.getAngle());
        //聚合字段
        curr.setCount(curr.getCount() + 1);
        curr.setTimeAvgSpeed((curr.getTimeAvgSpeed() * curr.getCount() + rawBusGps.getVec1()) / curr.getCount());
        return curr;
    }
}
