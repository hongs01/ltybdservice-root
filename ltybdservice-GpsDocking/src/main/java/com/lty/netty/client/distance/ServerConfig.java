package com.lty.netty.client.distance;

/**
 * 连接服务端配置bean
 */
public class ServerConfig {
    private String ipAddress;
    private Integer port;
    private String dataHead;
    private Integer clientPort;

    public ServerConfig(String ipAddress, Integer port, String dataHead,Integer clientPort, int state) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.dataHead = dataHead;
        this.clientPort = clientPort;
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
