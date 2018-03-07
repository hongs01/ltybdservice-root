package com.lty.netty.client.filter;

import com.lty.netty.client.DecodeParameter;

/**
 * 抽象解码器
 * @author zhouyongbo 2017/12/5
 */
public abstract class AbstracDecodetFilter {

    public abstract void decode(DecodeParameter decodeParameter) throws Exception;

}
