package com.lty.netty.client.handler;

import com.lty.netty.client.distance.ServerConfig;
import com.lty.service.DataDisposeService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private ServerConfig serverConfig;
//    private DataDisposeService dataDisposeService = new DataDisposeService();
    public ClientHandler(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //需要发送初始化信息
//        ctx.writeAndFlush(Unpooled.copiedBuffer("01", CharsetUtil.UTF_8));
//        ctx.writeAndFlush("01");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // TODO Auto-generated method stub
        logger.info("server receive message :"+msg);
//        dataDisposeService.dispose(msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexpected exception from downstream.", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
