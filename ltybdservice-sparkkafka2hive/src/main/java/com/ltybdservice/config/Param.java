package com.ltybdservice.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/12/14$ 10:24$
 * @description: 计算时用于过滤的参数
 */
public class Param {
    private String cityCode;
    public static Param param = new Param().getParam();

    private synchronized Param getParam() {
        if (param != null) {
            return param;
        }
        InputStream in = this.getClass().getResourceAsStream("/param.yml");
        Param Param = new Yaml().loadAs(in, Param.class);
        return Param;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public static void main(String[] args) {

    }

}
