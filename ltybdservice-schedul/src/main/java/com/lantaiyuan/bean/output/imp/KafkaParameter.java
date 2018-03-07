package com.lantaiyuan.bean.output.imp;

import com.lantaiyuan.bean.output.OutParameter;
import com.lantaiyuan.common.constEnum.KafkaProduce;

import java.util.List;

/**
 * kafka 输出类型参数
 * Created by zhouyongbo on 2017-11-22.
 */
public class KafkaParameter extends OutParameter {
    private List<KafkaProduce> topics;//

    public KafkaParameter(Object data, List<KafkaProduce> topics) {
        super(data);
        this.topics = topics;
    }

    public List<KafkaProduce> getTopics() {
        return topics;
    }

    public void setTopics(List<KafkaProduce> topics) {
        this.topics = topics;
    }
}
