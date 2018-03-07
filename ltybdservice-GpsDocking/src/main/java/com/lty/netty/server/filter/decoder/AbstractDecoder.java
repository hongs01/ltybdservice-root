package com.lty.netty.server.filter.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 译码处理
 * @author zhouyongbo 2017/12/3
 */
public abstract class AbstractDecoder {

    public abstract void doDecode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list);


    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
        doDecode(channelHandlerContext,byteBuf,list);
    };
}
