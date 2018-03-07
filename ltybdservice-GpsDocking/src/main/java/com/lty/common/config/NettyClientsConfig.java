package com.lty.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * netty 模拟终端配置
 * @author zhouyongbo 2017/12/4
 */
@Component
@ConfigurationProperties(prefix = "spring.netty.client")
public class NettyClientsConfig {

    private List<DistanceGatewayServer> distanceGatewayServers ;


    public List<DistanceGatewayServer> getDistanceGatewayServers() {
        return distanceGatewayServers;
    }

    public void setDistanceGatewayServers(List<DistanceGatewayServer> distanceGatewayServers) {
        this.distanceGatewayServers = distanceGatewayServers;
    }
}
