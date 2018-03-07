package com.lty.MQ.production;

import com.lty.MQ.IAbsMqProduction;
import com.lty.MQ.IApplyAbsMqProduction;
import com.lty.common.MQ.DataMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouyongbo 2017/10/28
 * MQ实际应用层
 */
public abstract class ApplyAbsMqProduction implements IApplyAbsMqProduction {
    private static Logger logger = LoggerFactory.getLogger(ApplyAbsMqProduction.class);
    private String topicName;

    @Autowired
    private IAbsMqProduction iAbsMqProduction;


    public ApplyAbsMqProduction(String topicName) {
        this.topicName = topicName;
    }

    //----------------------------- 生产消息----------------------
    protected void execute(Object msg ) {
        iAbsMqProduction.sendMsg(new DataMQ(topicName,msg));

    }
    //-----------------------------

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
