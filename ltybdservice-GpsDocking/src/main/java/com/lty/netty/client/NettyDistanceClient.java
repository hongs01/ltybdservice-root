package com.lty.netty.client;


import com.lty.netty.client.distance.ServerConfig;
import com.lty.netty.client.filter.ClientFilter;
import com.lty.netty.client.handler.ClientHandler;
import com.lty.netty.packet.CoordinateNettyPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 *
 * 远程客户端口
 * @author zhouyongbo 2017/12/4
 */
public class NettyDistanceClient implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(NettyDistanceClient.class);
    private ServerConfig serverConfig;

    private LinkedBlockingDeque<Object> arrayBlockingQueue = new LinkedBlockingDeque<Object>();

    public NettyDistanceClient(ServerConfig serverConfig) {
            this.serverConfig = serverConfig;
              init();
    }




    private  EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();
    private  ChannelFuture channelFuture;

    public void init(){
        b.group(group);
        b.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addFirst(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        super.channelInactive(ctx);
                        ctx.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect();
                            }
                        }, 1, TimeUnit.SECONDS);
                    }
                });
                if (serverConfig.getDataHead() == null){
                    pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
//                    pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                    pipeline.addLast("encoder", new ByteArrayEncoder());
                    pipeline.addLast("handler", new ClientHandler(serverConfig));
                }else {
                    ByteBuf delimiter1 = Unpooled.copiedBuffer(serverConfig.getDataHead().getBytes());
                    pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter1));
                    pipeline.addLast("decoder", new ClientFilter(serverConfig));
                    pipeline.addLast("encoder", new ByteArrayEncoder());
                    pipeline.addLast("handler", new ClientHandler(serverConfig));
                }

            }
        });

        //数据发送线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (channelFuture == null) continue;
                    try {
                        Object take = arrayBlockingQueue.take();
                        //数据转换
                        //byte[] jtData = ((CoordinateNettyPacket) take).getJtData();
                        if (take != null){
                            sendMsg(take.toString().getBytes());
                            //logger.info("sendMsg--->"+take.toString());
                        }else {
                            arrayBlockingQueue.addLast(take);
                        }
                    }catch (Exception e){
                        //logger.error("server:"+serverConfig.getIpAddress()+":"+serverConfig.getPort()+"-->Exception:"+e.getMessage());
                    }
                }
            }
        }).start();


    }

    public void sendMsg(byte[] obj){
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(obj));
    }

    public void doAdd(Object data){
        //是否符合需要的数据
        //不符合需要的数据则丢弃掉
//        if (data instanceof CoordinatePacket){
            this.arrayBlockingQueue.addLast(data);
//        }

    }



    @Override
    public void run() {
        doConnect();
    }

    public void doConnect(){
        try {
           getChannelFuture();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
//            group.shutdownGracefully();
        }
    }

    public ChannelFuture getChannelFuture(){
        if (serverConfig.getClientPort() == null || serverConfig.getClientPort() == 0){
//            connect = b.bind(serverConfig.getIpAddress(), serverConfig.getPort());
            channelFuture= b.connect(serverConfig.getIpAddress(), serverConfig.getPort());
        }else {
//            b.remoteAddress(new InetSocketAddress(serverConfig.getIpAddress(), serverConfig.getPort()));
//            connect =  b.bind();
            channelFuture = b.connect(new InetSocketAddress(serverConfig.getIpAddress(), serverConfig.getPort()), new InetSocketAddress(serverConfig.getClientPort()));
//            connect = b.bind(new InetSocketAddress(serverConfig.getIpAddress(), serverConfig.getPort()), serverConfig.getClientPort());
//            b.bind()
        }
        try {
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                       logger.info("连接服务端成功--->"+serverConfig.getIpAddress()+":"+serverConfig.getPort() );
                        f.channel().writeAndFlush("01");
                    } else {
                        logger.info("连接服务端失败--->"+serverConfig.getIpAddress()+":"+serverConfig.getPort() );
                        f.channel().eventLoop().schedule( new Runnable() {
                            @Override
                            public void run() {
                                logger.info("正在尝试重新连接--->"+serverConfig.getIpAddress()+":"+serverConfig.getPort() );
                                doConnect();
                            }
                        }, 1, TimeUnit.SECONDS);
                    }
                }
            }).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channelFuture;
    }
}
