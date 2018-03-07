package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.BusPsg;
import com.ltybdservice.pre.prebean.LinePsg;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 聚合line级客流信息
 * 聚合值：
 */
public class LinePsgAgg implements ReducerAggregator<LinePsg> {
    private static final Logger LOG = LoggerFactory.getLogger(LinePsgAgg.class);
    private static final DecimalFormat df = new DecimalFormat("#.0000");

    @Override
    public LinePsg init() {
        LinePsg linePsg = new LinePsg();
        return linePsg;
    }

    @Override
    public LinePsg reduce(LinePsg curr, TridentTuple tuple) {
        BusPsg busPsg = (BusPsg) tuple.getValueByField("busPsg");
        String cityCode = (String) tuple.getValueByField("cityCode");
        int lineId = (Integer) tuple.getValueByField("lineId");
        String workDate = (String) tuple.getValueByField("workDate");
        //坐标字段
        curr.setWorkDate(workDate);
        curr.setHalfHour(busPsg.getHalfHour());
        curr.setCityCode(cityCode);
        curr.setLineId(lineId);
        curr.setLineName(busPsg.getLineName());


        //聚合字段
        //更新拥挤度表
        curr.getMapBus2Crowd().put(busPsg.getBusId(), busPsg.getCrowd());
        //计算平均拥挤度
        double crowd = 0;
        for (Object value :
                curr.getMapBus2Crowd().values()) {
            crowd = crowd + Double.parseDouble(value.toString());
        }
        curr.setCrowd(Double.parseDouble(df.format(crowd / curr.getMapBus2Crowd().size())));
        //更新客流量表
        curr.getMapBus2TotalUpFlow().put(busPsg.getBusId(), busPsg.getTotalFlow());
        //计算平均客流量
        int totalUpFlow = 0;
        for (int value :
                curr.getMapBus2TotalUpFlow().values()) {
            totalUpFlow = totalUpFlow + value;
        }
        curr.setTotalUpFlow(totalUpFlow);
        return curr;
    }
}
