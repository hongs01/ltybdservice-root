package com.ltybdservice.pre.preagg;

import com.ltybdservice.pre.prebean.CityPsg;
import com.ltybdservice.pre.prebean.LinePsg;
import com.ltybdservice.hiveutil.DataSourceConf;
import com.ltybdservice.hiveutil.DpDataBaseUtil;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 聚合city级客流信息
 * 聚合值：
 */
public class CityPsgAgg implements ReducerAggregator<CityPsg> {
    private static final Logger LOG = LoggerFactory.getLogger(CityPsgAgg.class);
    private static final DecimalFormat df = new DecimalFormat("#.0000");
    private DataSourceConf conf;

    public CityPsgAgg(DataSourceConf conf) {
        this.conf = conf;
    }

    @Override
    public CityPsg init() {
        CityPsg cityPsg = new CityPsg();
        return cityPsg;
    }

    @Override
    public CityPsg reduce(CityPsg curr, TridentTuple tuple) {
        LinePsg linePsg = (LinePsg) tuple.getValueByField("linePsg");
        String workDate = (String) tuple.getValueByField("workDate");
        String cityCode = (String) tuple.getValueByField("cityCode");
        //坐标字段
        curr.setWorkDate(workDate);
        curr.setCityCode(cityCode);
        curr.setHalfHour(linePsg.getHalfHour());
        //聚合字段
        //更新客流表
        curr.getMapLineId2TotalUpFlow().put(linePsg.getLineId(), linePsg.getTotalUpFlow());
        //计算总客流
        int totalUpFlow = 0;
        for (int value :
                curr.getMapLineId2TotalUpFlow().values()) {
            totalUpFlow = totalUpFlow + value;
        }
        curr.setTotalUpFlow(totalUpFlow);

        //更新拥挤度表
        curr.getMapLineId2Crowd().put(linePsg.getLineId(), linePsg.getCrowd());
        //计算平均拥挤度
        double crowd = 0;
        for (Object value :
                curr.getMapLineId2Crowd().values()) {
            crowd = crowd + Double.parseDouble(value.toString());
        }
        curr.setCrowd(Double.parseDouble(df.format(crowd / curr.getMapLineId2Crowd().size())));

        //更新预测客流表
        int halfHourForecastPsg = 0;
        try {
//            halfHourForecastPsg = DpDataBaseUtil.getDpConnectionInstance(conf).lineId2HalfHourPsg(cityCode, linePsg.getLineId(), workDate, Integer.parseInt(linePsg.getHalfHour().substring(0, 2)));
            halfHourForecastPsg = DpDataBaseUtil.getDpConnectionInstance(conf).lineId2HalfHourPsg(cityCode, linePsg.getLineId(), "20170920", Integer.parseInt(linePsg.getHalfHour().substring(0, 2)));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        curr.getMapLineId2ForecastPsg().put(linePsg.getLineId(), halfHourForecastPsg);
        //计算总客流
        int totalForecastPsg = 0;
        for (int value :
                curr.getMapLineId2ForecastPsg().values()) {
            totalForecastPsg = totalForecastPsg + value;
        }
        curr.setForecastPsg(totalForecastPsg);
        return curr;
    }
}
