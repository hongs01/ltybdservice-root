package com.lty.netty.client;


import com.lty.common.config.DistanceGatewayServer;
import com.lty.common.config.NettyClientsConfig;
import com.lty.netty.client.distance.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NettyStartClient implements CommandLineRunner {

    private static final Map<String, NettyDistanceClient> CLIENT_MAP = new HashMap<String, NettyDistanceClient>();

    private static Logger logger = LoggerFactory.getLogger(NettyStartClient.class);
    @Autowired
    private NettyClientsConfig nettyClientsConfig;


    @Override
    public void run(String... strings) throws Exception {
        List<DistanceGatewayServer> distanceGatewayServers = nettyClientsConfig.getDistanceGatewayServers();
        if (distanceGatewayServers == null || distanceGatewayServers.size() == 0){
            logger.error("没有配置 远程服务端连接 ");
        }
        for (DistanceGatewayServer gatewayServer: distanceGatewayServers){
            NettyDistanceClient nettyDistanceClient =
                    new NettyDistanceClient(new ServerConfig(gatewayServer.getIpAddress(),
                            gatewayServer.getPort(), gatewayServer.getDataHead(),gatewayServer.getClientPort(),0));
            new Thread(nettyDistanceClient).start();
            CLIENT_MAP.put(gatewayServer.getIpAddress()+gatewayServer.getPort(),nettyDistanceClient);
        }
//        monitorServer();
    }


    public static void doProcess(Object data) {
        for (String key:CLIENT_MAP.keySet()){
            CLIENT_MAP.get(key).doAdd(data);
        }
    }


//    /**
//     * 监听连接  是否中断
//     */
//    public void monitorServer(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    for (String key : CLIENT_MAP.keySet()){
//                        NettyDistanceClient nettyDistanceClient = CLIENT_MAP.get(key);
//                        ServerConfig serverConfig = nettyDistanceClient.getServerConfig();
//                        logger.info("监听服务:"+serverConfig.getIpAddress()+":"+serverConfig.getPort()+"-->"+"    "+ serverConfig.getState());
//                        if ( serverConfig.getState() == 0 || serverConfig.getState() == 2) {
//                            new Thread(nettyDistanceClient).start();
//                        }
//                    }
//
//                }
//            }
//        }).start();
//    }


}
