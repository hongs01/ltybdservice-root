package com.lty.common.util;

import io.netty.channel.Channel;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * netty 工具类
 * @author zhouyongbo 2017/12/5
 */
public class NettyUtil {

    public static int getActiveState(Channel channel){
        boolean active = channel.isActive();
        if (!active){
            return 0;
        }else {
            return 1;
        }
    }

    public static int getCompleteState(Channel channel) {
        boolean active = channel.isActive();
        if (!active){
            return 1;
        }else {
            return 0;
        }
    }


    //坐标转换
    public static byte[] coordinateToBytes(BigDecimal coordinate){
        int v = (int)(coordinate.doubleValue() * 1000000);
        return  integerTo4Bytes(v);
    }


    /**
     * 时间转换
     */
    public static byte[] calendarToBytes(Calendar calendar){
        byte[] bytes = new byte[6];
//        String s = String.valueOf(calendar.get(Calendar.YEAR));
//        int year = Integer.valueOf(s.substring(s.length() - 2, s.length()));
        bytes[0] = (byte)(calendar.get(Calendar.YEAR) %100);
        bytes[1] = (byte)(calendar.get(Calendar.MONTH) + 1);
        bytes[2] = (byte)calendar.get(Calendar.DAY_OF_MONTH);
        bytes[3] = (byte)calendar.get(Calendar.HOUR_OF_DAY);
        bytes[4] = (byte)calendar.get(Calendar.MINUTE);
        bytes[5] = (byte)calendar.get(Calendar.SECOND);
        return bytes;
    }

    public static void main(String[] args) {

//        Coordinate coordinate1 = new Coordinate(111.65);//1879.26
//        Coordinate coordinate = new Coordinate(29.03);//487.77
//        byte[] bs = coordinateToBytes(coordinate1);
//        int degree = ;

//        int _3 = (bs[3] & 0xFF) << 24;
//        int _2 = (bs[2] & 0xFF) << 16;
//        int _1 = (bs[1] & 0xFF) << 8;
//        int _0 = (bs[0] & 0xFF);
//        int i = _3 + _2 + _1 + _0;
//        float v = Float.intBitsToFloat(i);
        //先将经度与纬度 以度为单位转换成 int类型 然后再转换为字节数组

        String s = "02 3A C2 4A";
        String a = "06 86 CD C6 ";

//        int aaa = (int) Math.rint(coordinate1.getDoubleValue()*1000000);
//        byte[] bytes = integerTo4Bytes(aaa);

        byte[] byteBuffer = StringHex.getByteBuffer(a);

//        double v = (_3 + _2 + _1) / 1000000d;
//        v = v + _0;
//        float v = byte2Float(byteBuffer);
//        double v = fourBytesToInteger(bs)/1000000d;
//        double i = Float.intBitsToFloat( + ((bs[2] & 0xFF) << 16) + ((bs[1] & 0xFF) << 8) );
//        System.out.println(v);
    }


    public static byte[] integerTo4Bytes(int value){
        byte[] result = new byte[4];
        result[0] = (byte) ((value >>> 24) & 0xFF);
        result[1] = (byte) ((value >>> 16) & 0xFF);
        result[2] = (byte) ((value >>> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }

    public static int fourBytesToInteger(byte[] value) {
        // if (value.length < 4) {
        // throw new Exception("Byte array too short!");
        // }
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        return ((temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
    }

    public static float byteBE2Float(byte[] bytes) {
        int l;
        l = bytes[0];
        l &= 0xff;
        l |= ((long) bytes[1] << 8);
        l &= 0xffff;
        l |= ((long) bytes[2] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[3] << 24);
        return Float.intBitsToFloat(l);
    }


    public static float byte2Float(byte[] bs) {
        return Float.intBitsToFloat(
                (((bs[3] & 0xFF) << 24) + ((bs[2] & 0xFF) << 16) + ((bs[1] & 0xFF) << 8) + (bs[0] & 0xFF)));
    }

}
