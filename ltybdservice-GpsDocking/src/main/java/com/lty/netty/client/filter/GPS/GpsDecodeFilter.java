package com.lty.netty.client.filter.GPS;


import com.lty.common.util.MyUtils;
import com.lty.netty.client.DecodeParameter;
import com.lty.netty.client.filter.AbstracDecodetFilter;
import com.lty.netty.client.filter.GPS.decode.VehicleCoordinate;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * gps解码器
 * @author zhouyongbo 2017/12/5
 */
public class GpsDecodeFilter extends AbstracDecodetFilter {


    @Override
    public void decode(DecodeParameter decodeParameter) throws Exception {
        List<Object> list = decodeParameter.getList();
        ByteBuf byteBuf = decodeParameter.getByteBuf();
        ChannelHandlerContext channelHandlerContext = decodeParameter.getChannelHandlerContext();

        IGpsDecodeFilter iGpsDecodeFilter = null;
        if(byteBuf.readableBytes() > 2){
            // 标记开始位置
            byteBuf.markReaderIndex();
            String headFlag = "$@$@";
            byte[] bodyLengthBytes = new byte[2];
            byteBuf.readBytes(bodyLengthBytes);
            int bodyLength = MyUtils.bytes2int(bodyLengthBytes);//包长
            byte bodyFlagByte = byteBuf.readByte();
            String bodyFlag = MyUtils.convertHexString(new byte[]{bodyFlagByte});//包标记

            decodeParameter.setBodyFlag(bodyFlag);
            decodeParameter.setHeadFlag(headFlag);
            decodeParameter.setBodyLength(bodyLength);
            if (bodyFlag.equals("80")){
                iGpsDecodeFilter = new VehicleCoordinate();
            }

        }else {
            byteBuf.skipBytes(byteBuf.readableBytes());
            return;
        }
        if (iGpsDecodeFilter != null){
            iGpsDecodeFilter.doDecode(decodeParameter);
        }
    }



}
