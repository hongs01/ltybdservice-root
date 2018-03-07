package com.ltybdservice.redisutil;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.InputStream;
import java.util.List;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/12/1$ 17:10$
 * @description:
 */
public class RedisClusterConf {
    private List<String> uri;

    public List<String> getUri() {
        return uri;
    }

    public void setUri(List<String> uri) {
        this.uri = uri;
    }
    public static JedisCluster jc= new RedisClusterConf().getJedisCluster();
    private synchronized  JedisCluster  getJedisCluster(){
        if(jc!=null){
            return jc;
        }
        InputStream in = this.getClass().getResourceAsStream("/redis-cluster-site.yml");
        RedisClusterConf redisClusterConf = new Yaml().loadAs(in,RedisClusterConf.class);
        java.util.HashSet<HostAndPort> jedisClusterNodes = new java.util.HashSet<HostAndPort>();
        for (String u:
                redisClusterConf.getUri()) {
            String[] addr=u.split(":");
            jedisClusterNodes.add(new HostAndPort(addr[0], Integer.parseInt(addr[1])));
        }
        return new JedisCluster(jedisClusterNodes, new GenericObjectPoolConfig());
    }
}
