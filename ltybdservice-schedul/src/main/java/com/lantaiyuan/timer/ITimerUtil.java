package com.lantaiyuan.timer;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ITimerUtil {

    public static <T> void runTimer(T t, String methodName, int hours,Object... args){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object o = t;
                int init = 0;
                //默认时间
                Long nowHours = 0L;
                Long defountHours = 24*3600L;
                while (true){
                    if (init==0){
                        init++;
                        nowHours = getSecondsToHours(hours);
                    }else {
                        nowHours = defountHours;
                    }
                    try {
                        Thread.sleep(nowHours*1000);
                        Method declaredMethod = o.getClass().getDeclaredMethod(methodName);
                        declaredMethod.invoke(o,args);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

//到给定时间剩余的秒数
    public static Long getSecondsToHours(int hours){
        String str="";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        str = df.format(new Date());
        Long nowH=Long.valueOf(str.split(":")[0]);
        Long nowM=Long.valueOf(str.split(":")[1]);
        Long nowS=Long.valueOf(str.split(":")[2]);
        Long sub=0L;
        if(hours>nowH){
         sub=(hours-nowH)*3600-nowM*60-nowS;
        }else if (hours==nowH){
            sub=24*3600-(nowM*60+nowS);
        }else{
            sub=(24+nowH)*3600+nowM*60+nowS-hours*3600;
        }
        return sub;
    }
}
