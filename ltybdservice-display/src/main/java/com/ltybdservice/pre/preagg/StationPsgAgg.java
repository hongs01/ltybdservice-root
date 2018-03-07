package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.BusPsg;
import com.ltybdservice.pre.prebean.StationPsg;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 聚合station级客流信息
 * 聚合值：
 */
public class StationPsgAgg implements ReducerAggregator<StationPsg> {
    private static final Logger LOG = LoggerFactory.getLogger(StationPsgAgg.class);

    @Override
    public StationPsg init() {
        StationPsg stationPsg = new StationPsg();
        return stationPsg;
    }

    @Override
    public StationPsg reduce(StationPsg curr, TridentTuple tuple) {
        BusPsg busPsg = (BusPsg) tuple.getValueByField("busPsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        int stationId = (Integer) tuple.getValueByField("stationId");
        String workDate = (String) tuple.getValueByField("workDate");
        //坐标字段
        curr.setCityCode(cityCode);
        curr.setStationId(stationId);
        curr.setWorkDate(workDate);
        curr.setHalfHour(busPsg.getHalfHour());
        curr.setStationName(busPsg.getStationName());
        if (busPsg.getStationName().equals("天宇生态园")) {
            curr.setLatitude(36.679168);
            curr.setLongitude(114.468194);
        } else if (busPsg.getStationName().equals("新世纪站")) {
            curr.setLatitude(36.655778);
            curr.setLongitude(114.628883);
        } else if (busPsg.getStationName().equals("高庄")) {
            curr.setLatitude(36.584173);
            curr.setLongitude(114.539196);
        } else if (busPsg.getStationName().equals("公交总公司")) {
            curr.setLatitude(36.626124);
            curr.setLongitude(114.546958);
        } else {
            curr.setLatitude(busPsg.getLatitude());
            curr.setLongitude(busPsg.getLongitude());
        }


        //聚合字段
        curr.setPsg(curr.getPsg() + busPsg.getUpFlow());
        return curr;
    }
}
