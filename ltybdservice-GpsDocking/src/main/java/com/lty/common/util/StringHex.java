package com.lty.common.util;

/**
 * 进制转换
 * @author zhouyongbo 2017/12/3
 */
public class StringHex {

    public static  String bytes2hex(byte[] bArray) {
        //字节数据转16进制字符串
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return getSpaceHex(sb.toString());
    }


    public static byte[] getByteBuffer(String str){
        //根据16进制字符串得到buffer
        String[] command=str.split(" ");
        byte[] abc=new byte[command.length];
        for(int i=0;i<command.length;i++){
            abc[i]=Integer.valueOf(command[i],16).byteValue();
        }
        return abc;
    }


    //高位在前，低位在后

    /**
     *
     * byte转十进制
     * @param bytes
     * @return
     */
    public static int bytes2int(byte[] bytes){
        int result = 0;
        if(bytes.length == 4){
            int a = (bytes[0] & 0xff) << 24;//说明二
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        }
        else if(bytes.length == 2){
            int a = (bytes[0] & 0xff) << 8;
            int b = (bytes[1] & 0xff);
            result = a | b ;
        }
        else if(bytes.length == 1){
            int a = (bytes[0] & 0xff);
            result = a ;
        }
        return result;
    }


    public static  String getSpaceHex(String str){
        //将不带空格的16进制字符串加上空格
        String re="";
        String regex = "(.{2})";
        re = str.replaceAll (regex, "$1 ");
        return re;
    }
}
