package com.lty.netty.client.filter;

import com.lty.netty.client.DecodeParameter;
import com.lty.netty.client.distance.ServerConfig;
import com.lty.netty.client.filter.GPS.GpsDecodeFilter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 客户端解码
 * @author zhouyongbo 2017/12/4
 */
public class ClientFilter extends ByteToMessageDecoder {

    private ServerConfig serverConfig;

    public ClientFilter(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        AbstracDecodetFilter abstracDecodetFilter = null;
        if ("$@$@".equals(serverConfig.getDataHead())) {
            abstracDecodetFilter = new GpsDecodeFilter();
            abstracDecodetFilter.decode( new DecodeParameter(channelHandlerContext,byteBuf,list));
        }
    }
}
