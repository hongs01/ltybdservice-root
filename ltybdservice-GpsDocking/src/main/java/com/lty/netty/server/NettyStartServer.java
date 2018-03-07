package com.lty.netty.server;

import com.lty.common.config.NettyServerConfig;
import com.lty.netty.server.filter.FilterDecoder;
import com.lty.netty.server.handler.TCPServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TCP 服务启动
 * @author zhouyongbo 2017/12/3
 */
@Component
public class NettyStartServer implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(NettyStartServer.class);

    @Autowired
    private NettyServerConfig nettyServerConfig;


    // 1 创建线两个事件循环组
    // 一个是用于处理服务器端接收客户端连接的
    // 一个是进行网络通信的（网络读写的）
   private EventLoopGroup pGroup = new NioEventLoopGroup();
   private EventLoopGroup cGroup = new NioEventLoopGroup();
    // 2 创建辅助工具类ServerBootstrap，用于服务器通道的一系列配置
   private ServerBootstrap serverBootstrap = new ServerBootstrap();


    private void init (){
        this.serverBootstrap.group(pGroup, cGroup)
                .channel(NioServerSocketChannel.class) // 指定NIO的模式.NioServerSocketChannel对应TCP, NioDatagramChannel对应UDP
                .option(ChannelOption.SO_BACKLOG, nettyServerConfig.getSo_backlog()) // 设置TCP缓冲区
                .option(ChannelOption.SO_SNDBUF, nettyServerConfig.getSo_sndbuf()) // 设置发送缓冲大小
                .option(ChannelOption.SO_RCVBUF, nettyServerConfig.getSo_rcvbuf()) // 这是接收缓冲大小
                .option(ChannelOption.SO_KEEPALIVE, nettyServerConfig.getSo_keepalive()) // 保持连接
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {  //SocketChannel建立连接后的管道
                        // 3 在这里配置 通信数据的处理逻辑, 可以addLast多个...
                        ChannelPipeline pipeline = sc.pipeline();
//                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//                        pipeline.addLast(new LengthFieldPrepender(4));
//                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
//                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        ByteBuf delimiter1 = Unpooled.copiedBuffer("$@$@".getBytes());
                        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter1));
                        pipeline.addLast("decoder",new FilterDecoder());
                        pipeline.addLast(new TCPServerHandler());

                    }
                });
        logger.info("netty server init() ");
    }
    @Override
    public void run(String... strings) throws Exception {
        init ();
//        logger.info("netty server run(): port---->"+nettyServerConfig.getPort());
//        ChannelFuture sync = serverBootstrap.bind(nettyServerConfig.getPort()).sync();
//        sync.channel().closeFuture().sync();

    }


    public void close(){
//        pGroup.shutdownGracefully();
//        cGroup.shutdownGracefully();
    }
}
