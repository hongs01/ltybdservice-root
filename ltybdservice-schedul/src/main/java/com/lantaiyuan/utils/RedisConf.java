package com.lantaiyuan.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/12/1$ 17:10$
 * @description:
 */
public class RedisConf implements Serializable  {
    private List<String> uri;

    public List<String> getUri() {
        return uri;
    }

    public void setUri(List<String> uri) {
        this.uri = uri;
    }
    public static JedisCluster jc= new RedisConf().getJedisCluster();
    private synchronized JedisCluster getJedisCluster(){
        if(jc!=null){
            return jc;
        }
        InputStream in = this.getClass().getResourceAsStream("/redis-site.yml");
        RedisConf redisConf = new Yaml().loadAs(in,RedisConf.class);
        java.util.HashSet<HostAndPort> jedisClusterNodes = new java.util.HashSet<HostAndPort>();
        for (String u:
                redisConf.getUri()) {
            String[] addr=u.split(":");
            jedisClusterNodes.add(new HostAndPort(addr[0], Integer.parseInt(addr[1])));
        }
        return new JedisCluster(jedisClusterNodes,new GenericObjectPoolConfig());
    }
}
