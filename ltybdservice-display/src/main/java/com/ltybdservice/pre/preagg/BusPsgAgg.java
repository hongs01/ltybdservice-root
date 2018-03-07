package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.BusPsg;
import com.ltybdservice.pre.prebean.RawBusPsg;
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
 * 总客流：totalFlow
 */
public class BusPsgAgg implements ReducerAggregator<BusPsg> {
    private static final Logger LOG = LoggerFactory.getLogger(BusPsgAgg.class);

    @Override
    public BusPsg init() {
        BusPsg busPsg = new BusPsg();
        busPsg.setTotalFlow(0);
        return busPsg;
    }

    @Override
    public BusPsg reduce(BusPsg curr, TridentTuple tuple) {
        RawBusPsg rawBusPsg = (RawBusPsg) tuple.getValueByField("str");
        String workDate = (String) tuple.getValueByField("workDate");
        String halfHour = (String) tuple.getValueByField("halfHour");
        //坐标字段
        curr.getBaseBusPsg(rawBusPsg);
        //信息字段
        curr.setTime(rawBusPsg.getTime());
        curr.setWorkDate(workDate);
        curr.setHalfHour(halfHour);
        curr.setUpFlow(rawBusPsg.getUp_flow());
        curr.setInBusPsg(rawBusPsg.getTotal_flow());
        curr.setCrowd(rawBusPsg.getCrowd());
        curr.setVehicleId(rawBusPsg.getVehicle_id());
        curr.setLongitude(rawBusPsg.getLon());
        curr.setLatitude(rawBusPsg.getLat());
        //聚合字段
        curr.setTotalFlow(curr.getTotalFlow() + rawBusPsg.getUp_flow());
        return curr;
    }
}
