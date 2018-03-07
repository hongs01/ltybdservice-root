package com.lty.common.bean.mq;

/**
 * @author zhouyongbo 2017/10/31
 * 协议头信息
 */
public class MQHeard {
    private int msg_flag;
    private int msg_sn;
    private int msg_id;

    public MQHeard() {
    }

    public MQHeard(int msg_flag, int msg_sn, int msg_id) {
        this.msg_flag = msg_flag;
        this.msg_sn = msg_sn;
        this.msg_id = msg_id;
    }

    public MQHeard(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getMsg_flag() {
        return msg_flag;
    }

    public void setMsg_flag(int msg_flag) {
        this.msg_flag = msg_flag;
    }

    public int getMsg_sn() {
        return msg_sn;
    }

    public void setMsg_sn(int msg_sn) {
        this.msg_sn = msg_sn;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }
}
