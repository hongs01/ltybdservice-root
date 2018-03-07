package com.lantaiyuan.utils;

import com.lantaiyuan.bean.GPS;

import java.util.*;

/**
 * Created by zhouyongbo on 2017-11-19.
 */
public class ListUtil {

    public static boolean isListEmpty(List list){
        if (list == null || list.size() == 0){
            return true;
        }
        return false;
    }


    //取站点基础数据表 list存放到起点距离
    public static List<Double> getToBeginDistanceList(Map<String,Double> StationBaseData,Integer lineId,Integer lineDirection){
        List<Double>  listDistance=new ArrayList<Double>();
        if(StationBaseData.size()>0){
            for(String liniddirectionorder:StationBaseData.keySet()){
                if(liniddirectionorder.split("@")[0].equals(lineId.toString()) && liniddirectionorder.split("@")[1].equals(lineDirection.toString())){
                    listDistance.add(StationBaseData.get(liniddirectionorder));
                }
            }
        }
        Collections.sort(listDistance);
        return  listDistance;
    }

    //取站点基础数据表 list存放站序
    public static List<Integer>  getOrderList(Map<String,String> StationBaseData1,Integer lineId,Integer lineDirection){
        List<Integer> orderNolist=new ArrayList<Integer>();
		if(StationBaseData1.size()>0){
        for(String liniddirectionorder:StationBaseData1.keySet()){
            if(liniddirectionorder.split("@")[0].equals(lineId.toString())&&liniddirectionorder.split("@")[1].equals(lineDirection.toString())){
                orderNolist.add(Integer.parseInt(StationBaseData1.get(liniddirectionorder)));
            }
        }
    }
	Collections.sort(orderNolist);
		return orderNolist;
    }

    //根据list获取站间距离
    public static  Map<Integer,Double> getStationDistance(List<Double> listDistance){
        Map<Integer,Double>  staDistance=new HashMap<Integer,Double>();
        if(listDistance.size()>0){
            for(int i=0;i<listDistance.size();i++){
                if(i==0){
                    staDistance.put(i+1,listDistance.get(i));
                }else{
                    staDistance.put(i+1, listDistance.get(i)-listDistance.get(i-1));
                }
            }
        }
        return staDistance;
    }

    //获取最大站序
    public static int getMaxOrderNo(List<Integer> orderNolist){
        int  maxorderno=0;
        Collections.sort(orderNolist);
        if(orderNolist.size()>0){
            maxorderno=orderNolist.get(orderNolist.size()-1);
        }
        return maxorderno;
    }

   public static  List<Map<String,Object>> addMapToList(List<Map<String,Object>> list, Map<String,Object> map){
       list.add(map);
       return list;
    }

    public static Long getStartTime(List<Long> list){

       return  null;
    }

    public static  Long getEndTime(List<Long> list){

        return null;
    }

}
