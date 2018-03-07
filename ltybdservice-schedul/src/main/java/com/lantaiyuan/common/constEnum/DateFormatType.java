package com.lantaiyuan.common.constEnum;

/**
 * 时间格式类型
 * Created by zhouyongbo on 2017-11-20.
 */
public enum  DateFormatType {
    YYYYMMDD("yyyyMMdd"),
    YYYY_MM_DD("yyyy-MM-dd"),
    YYYY_MM_DD_HH("yyyy-MM-dd HH");
    DateFormatType(String key) {
        this.key = key;
    }
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
