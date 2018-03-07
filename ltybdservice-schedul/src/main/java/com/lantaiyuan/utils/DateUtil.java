package com.lantaiyuan.utils;

import com.lantaiyuan.common.constEnum.DateFormatType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhouyongbo on 2017-11-20.
 */
public class DateUtil {


    /**
     * 格式化日期
     * @param date
     * @param format
     * @return
     */
    public static String formartDate(Date date,String format){
        return  new SimpleDateFormat(format).format(date);
    }

    /**
     * 获取时间小时单位 并且根据类型 转换格式
     * @param date 时间
     * @param num  位数
     * @return
     */
    public static String gethour(Date date,int num){
        return String.format("%0"+num+"d",date.getHours());
    }


    //解析rowKey,获取年月日
    public static boolean isToday(String rowkey){
        String key=rowkey;
        //当天日期时间戳
        Date datetoday = new Date(System.currentTimeMillis());
        //rowkey时间戳
        Date daterow = new Date(Long.parseLong(rowkey));
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH");
//        String Todate = simpleDateFormat.format(datetoday);
//        String rowStr = simpleDateFormat.format(daterow);
        if(formartDate(datetoday, DateFormatType.YYYY_MM_DD.getKey()).
                equals(formartDate(daterow,DateFormatType.YYYY_MM_DD.getKey()))){
            return true;
        }else{
            return false;
        }
    }

    //格式化日期到分
    public static String getTimeToMin(Long time){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String min = format.format(time);
        return min.split(" ")[1];
    }

    //格式化到后n个小时
    public static  String formatToNextHour(String time,int n) throws ParseException {
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=simpleDateFormat .parse(time);
        long timeStemp = date.getTime();
        Date afterDate = new Date(timeStemp + n*3600*1000);
        String NextDepartureTime=simpleDateFormat.format(afterDate);
        return formatToHour(NextDepartureTime);
    }

    //格式化到小时
    public static String formatToHour(String nowtime){
        return nowtime.substring(11,13);
    }

    //数字格式化()
    public static String numberFormat(int n){
        String value=null;
        if(n>=0 &&n<10){
            value ="0"+String.valueOf(n);
        }else {
            value=String.valueOf(n);
        }
        return value;
    }

    //格式化实际时间
    public static  String formatRealTime(long t2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String realTime = simpleDateFormat.format(t2);
        return realTime;
    }

    //获取当前时间
    public static String getCurrentTime()
    {
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(date);
        return time;
    }

    //格式化时间字符串为时间戳
    public static Long formatTimeStr2TimeStamp(String timeStr) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf .parse(timeStr);
        return date.getTime();
    }
}
