package com.lty.common.bean.gps;

import com.lty.common.bean.AbstractMountPack;

/**
 *
 * 包头信息 GPS服务器包头信息
 * @author zhouyongbo 2017/12/5
 */
public abstract class GatewayPack extends AbstractMountPack {
    private String headFlag;//标志头
    private int bodyLength;//包体长度.
    private String bodyFlag;//包标志
    public String getHeadFlag() {
        return headFlag;
    }
    public void setHeadFlag(String headFlag) {
        this.headFlag = headFlag;
    }
    public int getBodyLength() {
        return bodyLength;
    }
    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }
    public String getBodyFlag() {
        return bodyFlag;
    }

    public void setBodyFlag(String bodyFlag) {
        this.bodyFlag = bodyFlag;
    }
}
