package com.lty.common.commonEnum;

import com.lty.netty.server.filter.decoder.UserDecoder;

/**
 * 译码处理类
 * @author zhouyongbo 2017/12/3
 */
public enum  TypeDecoder {
    USER(UserDecoder.class);

    private Class key;

    TypeDecoder(Class key) {
        this.key = key;
    }

    public Class getKey() {
        return key;
    }

    public void setKey(Class key) {
        this.key = key;
    }
}
