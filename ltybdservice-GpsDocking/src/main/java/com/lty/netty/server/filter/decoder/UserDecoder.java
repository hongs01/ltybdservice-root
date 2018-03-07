package com.lty.netty.server.filter.decoder;

import com.lty.netty.server.filter.decoder.AbstractDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * user译码处理
 * @author zhouyongbo 2017/12/3
 */
@Component
public class UserDecoder extends AbstractDecoder {


    @Override
    public void doDecode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

    }
}
