/**
* <p>Title: MyUtils.java</p>
* <p>Copyright: Copyright (c) 2016</p>
* <p>Company: lty</p>
*/
package com.lty.common.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
* <p>Title: MyUtils</p>
* <p>Description: 字节转换工具类</p>
* <p>Company: lty</p>
* @author liuhao
* @date 2016年11月14日 下午3:53:32
* version 1.0
*/
public class MyUtils {
	//将指定byte数组以16进制的形式打印到控制台   
	public static String convertHexString( byte[] b) {  
	   StringBuilder res = new StringBuilder();
	   for (int i = 0; i < b.length; i++) {    
	     String hex = Integer.toHexString(b[i] & 0xFF);    
	     if (hex.length() == 1) {    
	       hex = '0' + hex;    
	     }    
	     
	     res.append(hex.toUpperCase());
	   }    
	  
	   return res.toString();
	} 
	
	/**
	 * 二进制byte数组转换为16进制字符串
	 * @param bytes
	 * @return
	 */
	 public static String bytes2hex03(byte[] bytes)  
	 {  
	     final String HEX = "0123456789abcdef";  
	     StringBuilder sb = new StringBuilder(bytes.length * 2);  
	     for (byte b : bytes)  
	     {  
	         // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数  
	         sb.append(HEX.charAt((b >> 4) & 0x0f));  
	         // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数  
	         sb.append(HEX.charAt(b & 0x0f));  
	     }  
	  
	     return sb.toString();  
	 }
	
	 /**
     * int转byte[]，高位在前低位在后
     * 
     * @param value
     * @return
     */
    public static byte[] varIntToByteArray(long value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        Long l = new Long(value);
        try {
            if (l == l.byteValue()) {
                oos.writeByte(l.byteValue());
            } else if (l == l.shortValue()) {
                oos.writeShort(l.shortValue());
            } else if (l == l.intValue()) {
                oos.writeInt(l.intValue());
            } else if (l == l.longValue()) {
                oos.writeLong(l.longValue());
            } else if (l == l.floatValue()) {
                oos.writeFloat(l.floatValue());
            } else if (l == l.doubleValue()) {
                oos.writeDouble(l.doubleValue());
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                oos.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }
    
    //整形转字节数组(低位在前，高位在后 )(4个字节)
    public static byte[] int2Byte(int res) {  
    	byte[] targets = new byte[4];  
    	  
    	targets[0] = (byte) (res & 0xff);// 最低位   
    	targets[1] = (byte) ((res >> 8) & 0xff);// 次低位   
    	targets[2] = (byte) ((res >> 16) & 0xff);// 次高位   
    	targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。   
    	return targets;   
    }  
    
    //字节数组转整形(低位在前，高位在后 )(4个字节)
    public static int byte2Int(byte[] res) {   
    	// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000   
    	  
    	int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或   
    	| ((res[2] << 24) >>> 8) | (res[3] << 24);   
    	return targets;   
    }   
    
    //高位在前，低位在后  
    public static byte[] int2bytes(int num,int type){  
    	byte[] result = null;
    	
        if(type == 1){
        	result = new byte[4];  
            result[0] = (byte)((num >>> 24) & 0xff);//说明一  
            result[1] = (byte)((num >>> 16)& 0xff );  
            result[2] = (byte)((num >>> 8) & 0xff );  
            result[3] = (byte)((num >>> 0) & 0xff );  
        }
        else{
        	result = new byte[2];
        	result[0] = (byte)((num >>> 8) & 0xff );  
            result[1] = (byte)((num >>> 0) & 0xff );  
        }
        return result;  
    }  
    
    //高位在前，低位在后  
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
    
    
    public static String dateHex(int str){  
    	String result="";
    	if(str<10 && str>=0){
    		result="0"+str;
    	}else{
    		result=""+str;
    	}
    	return result;
    }
    
}
