package com.lantaiyuan.rtscheduling.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	
	public int getCurrentHour()
	{
		Date d = new Date();
    	int hours = d.getHours();
    	return hours;
	}
	
	public String format(int n){
		String value=null;
		if(n>=0 &&n<10){
			value ="0"+String.valueOf(n);
		}else {
			value=String.valueOf(n);
		}
		return value;
	}
	
	//获取当前时间
		public static String getCurrentTime()
		{
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		return time;
		}
		
		//格式化时间，将整点过五分以内的都保存为整点
		public String formatTime(String nowtime){
			String returnTime=null;
			String minute=nowtime.substring(14,16);
			if(minute.equals("00")||minute.equals("01")||minute.equals("02")||minute.equals("03")||minute.equals("04")||minute.equals("05")){
				returnTime=nowtime.substring(11,13);
			}else{
				returnTime=nowtime;
			}
				
			return returnTime;
		}
		
		public String formatToHour(String nowtime){
			return nowtime.substring(11,13);
		}
		
		public String formatToNextHour(String time,int n) throws ParseException{
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date date=simpleDateFormat .parse(time);
		        long timeStemp = date.getTime();
		        Date afterDate = new Date(timeStemp + n*3600*1000);
		        String NextDepartureTime=simpleDateFormat.format(afterDate);
			return formatToHour(NextDepartureTime);
		}
		
		
		public String getTimeAfter1Hour(String stationStartTime){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=null;
			try {
				date = sdf.parse(stationStartTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar ca=Calendar.getInstance();
			ca.setTime(date);
			ca.add(Calendar.HOUR_OF_DAY, 1);
			return sdf.format(ca.getTime());
		}
		
		//获取年月日的日期(yyyyMMDD)
		public String getWorkDate(){
			Date d = new Date();  
	        System.out.println(d);  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
	        String dateNowStr = sdf.format(d);  
	        System.out.println("格式化后的日期：" + dateNowStr);
			return dateNowStr;
		}
		
		//获取年月(yyyyMM)
		public String getWorkMonth(){
			Date d = new Date();  
	        System.out.println(d);  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
	        String dateNowStr = sdf.format(d);  
	        System.out.println("格式化后的日期：" + dateNowStr);
			return dateNowStr;
		}
		
}
