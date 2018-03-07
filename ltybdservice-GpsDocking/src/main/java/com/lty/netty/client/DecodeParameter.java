package com.lty.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 解码参数
 * @author zhouyongbo 2017/12/5
 */
public class DecodeParameter {
    private ChannelHandlerContext channelHandlerContext;
    private ByteBuf byteBuf;
    private List<Object> list;

    public DecodeParameter(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        this.channelHandlerContext = channelHandlerContext;
        this.byteBuf = byteBuf;
        this.list = list;
    }

    private String headFlag;
    private int bodyLength;
    private String bodyFlag;


    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

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
