package com.lty.common.MQ;

/**
 *  zhouyongbo 2017/10/27
 *  MQ参数类型
 */
public class DataMQ<T> {
    private String topicName;
    private T data;

    public DataMQ(String topicName, T data) {
        this.topicName = topicName;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
