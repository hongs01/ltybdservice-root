package com.ltybdservice.hiveudf;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by zhouzefei on 2017-11-23.
 */
public class add_date extends UDF{
    public String evaluate(String date,int n){
        GregorianCalendar gc=new GregorianCalendar();
        try {
            gc.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
            gc.add(5, n);
            return new SimpleDateFormat("yyyyMMdd").format(gc.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}