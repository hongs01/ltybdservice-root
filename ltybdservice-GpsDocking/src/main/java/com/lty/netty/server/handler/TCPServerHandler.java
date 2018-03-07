package com.lty.netty.server.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 *
 * @author zhouyongbo 2017/12/3
 */
public class TCPServerHandler extends ChannelInboundHandlerAdapter {


    @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
             throws Exception {
             // TODO Auto-generated method stub
                System.out.println("server receive message :"+ msg);
                ctx.channel().writeAndFlush("yes server already accept your message" + msg);
//                ctx.close();
    }

     public void channelActive(ChannelHandlerContext ctx) throws Exception {
             // TODO Auto-generated method stub
            System.out.println("channelActive>>>>>>>>");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete >>>>>>>>>>>>");
        ctx.flush();

//        ctx.fireChannelRead(ctx);
    }

    @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
              System.out.println("exception is general");
    }
}
