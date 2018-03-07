package com.ltybdservice.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/12/14$ 10:24$
 * @description: 计算时用于过滤的参数
 */
public class FilterParam {
    /**
     * 用户信息量必须达到一定数量才是有效的用户
     */
    private int minUserInfoSize;
    /**
     * od信息量必须达到一定数量才是有效的od
     */
    private int odInfoSize;
    /**
     * 用户行程时间必须超过一定时间才算一趟行程，以秒为单位
     */
    private int minInterval;
    /**
     * 用户行程时间必须小于一定时间才算一趟行程，以秒为单位
     */
    private int maxInterval;
    public static FilterParam param = new FilterParam().getParam();

    private synchronized FilterParam getParam() {
        if (param != null) {
            return param;
        }
        InputStream in = this.getClass().getResourceAsStream("/param.yml");
        FilterParam filterParam = new Yaml().loadAs(in, FilterParam.class);
        return filterParam;
    }

    public int getMinUserInfoSize() {
        return minUserInfoSize;
    }

    public void setMinUserInfoSize(int minUserInfoSize) {
        this.minUserInfoSize = minUserInfoSize;
    }

    public int getOdInfoSize() {
        return odInfoSize;
    }

    public void setOdInfoSize(int odInfoSize) {
        this.odInfoSize = odInfoSize;
    }

    public int getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(int minInterval) {
        this.minInterval = minInterval;
    }

    public int getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(int maxInterval) {
        this.maxInterval = maxInterval;
    }

    public static void main(String[] args) {
        System.out.println(FilterParam.param.getMinInterval());
    }

}
