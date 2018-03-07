package com.ltybdservice.commonutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DateUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String stamp10toDateString(long stamp) {
        return formatter.format(new Date(stamp * 1000));
    }

    /**
     * @return type: long
     * @author: Administrator
     * @date: 2017/11/22 16:30
     * @method: dateStringtoStamp10
     * @param: [dateString]
     * @desciption:
     */
    public static long dateStringtoStamp10(String dateString) {
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
}
