package com.lty.common.bean.mq;

import java.io.Serializable;

/**
 * MQ 消息格式
 */
public class MQProtocol implements Serializable {
    private MQHeard header;
    private MQBody body;

    public MQProtocol(MQHeard header, MQBody body) {
        this.header = header;
        this.body = body;
    }

    public MQHeard getHeader() {
        return header;
    }

    public void setHeader(MQHeard header) {
        this.header = header;
    }

    public MQBody getBody() {
        return body;
    }

    public void setBody(MQBody body) {
        this.body = body;
    }
}
