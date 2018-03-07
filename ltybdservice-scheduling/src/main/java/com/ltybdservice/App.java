package com.ltybdservice;

import com.lantaiyuan.rtscheduling.common.TimeUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	TimeUtil timeUtil=new TimeUtil();
    	System.err.println(timeUtil.getWorkMonth());
        System.out.println( "Hello World!" );
    }
}
