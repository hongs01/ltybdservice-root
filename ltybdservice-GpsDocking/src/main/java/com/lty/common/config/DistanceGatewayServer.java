package com.lty.common.config;


/**
 * 远程网关地址
 * @author zhouyongbo 2017/12/4
 */
public class DistanceGatewayServer {
    private String ipAddress;
    private Integer port;
    private String dataHead;//包头标记
    private Integer clientPort;//本机绑定端口号


    @Override
    public boolean equals(Object obj) {
        if (obj==null)return false;
        if (obj instanceof DistanceGatewayServer){
            DistanceGatewayServer gatewayServer = (DistanceGatewayServer) obj;
            if (ipAddress.equals(gatewayServer.getIpAddress())
                    && port.equals(gatewayServer.getPort())){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDataHead() {
        return dataHead;
    }

    public void setDataHead(String dataHead) {
        this.dataHead = dataHead;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }
}
