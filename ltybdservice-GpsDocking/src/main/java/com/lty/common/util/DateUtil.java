package com.lty.common.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转换
 * @author zhouyongbo
 */
public class DateUtil {

    public final static String FORMAT_13 = "yyyy-MM-dd HH:mm:ss"; //精确到秒


    public static Calendar formatCalendar(Long date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_13);
        Date parse = new Date(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        return calendar;
    }

    public static Calendar formatCalendar(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_13);
        try {
            Date parse = simpleDateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
