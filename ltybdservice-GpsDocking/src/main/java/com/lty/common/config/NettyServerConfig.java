package com.lty.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * netty 服务配置
 * @author 2017/12/03 zhouyongbo
 */
@ConfigurationProperties(prefix = "spring.netty.server")
@Component
public class NettyServerConfig {
    private Integer port;
    private Integer so_backlog;
    private Integer so_sndbuf;
    private Integer so_rcvbuf;
    private boolean so_keepalive;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSo_backlog() {
        return so_backlog;
    }

    public void setSo_backlog(Integer so_backlog) {
        this.so_backlog = so_backlog;
    }

    public Integer getSo_sndbuf() {
        return so_sndbuf;
    }

    public void setSo_sndbuf(Integer so_sndbuf) {
        this.so_sndbuf = so_sndbuf;
    }

    public Integer getSo_rcvbuf() {
        return so_rcvbuf;
    }

    public void setSo_rcvbuf(Integer so_rcvbuf) {
        this.so_rcvbuf = so_rcvbuf;
    }

    public boolean getSo_keepalive() {
        return so_keepalive;
    }

    public void setSo_keepalive(boolean so_keepalive) {
        this.so_keepalive = so_keepalive;
    }
}
