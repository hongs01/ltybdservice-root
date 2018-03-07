package com.lantaiyuan.bean.output;

/**
 * 输出参数设置
 * Created by zhouyongbo on 2017-11-22.
 */
public abstract class OutParameter {
    private Object data; //需要输出的数据

    public OutParameter() {
    }

    public OutParameter(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
