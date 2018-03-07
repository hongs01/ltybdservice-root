package com.lty.netty.client.filter.GPS;


import com.lty.netty.client.DecodeParameter;

/**
 * 处理接口
 * @author zhouyongbo 2017/12/5
 */
public interface IGpsDecodeFilter {


    public void doDecode(DecodeParameter decodeParameter) throws Exception;
}
