package com.ltybdservice.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @auther hongshuai,zhouzefei
 */
public class GetBusLines {
	
//	public static void main(String[] args){
//		String url = "http://restapi.amap.com/v3/direction/transit/integrated?origin=113.939064,22.560663&destination=113.95441,22.574783&city=%E6%B7%B1%E5%9C%B3&strategy=5&key=237b4289b6270e3020214466fca583cd";
//		List result = getTransferPlan(url);
//	}
    public  List getTransferPlan(String url){
    	//存储换乘方案的list
    	List  transitsList = new LinkedList();
    	List viaLists = new LinkedList();
    	List resultList = new LinkedList();
    	String duration = "";
    	String distance = "";
    	AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
        builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
        builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
     
        AsyncHttpClient client = new AsyncHttpClient(builder.build());
        try {
            ListenableFuture<Response> future = client.prepareGet(url).execute();
            String busJson = future.get().getResponseBody();
            JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(future.get().getResponseBody());
            if(jsonNode.findValue("status").textValue().equals("1")) {
         	   JSONUtil jsonUtil = new JSONUtil();
        		Map routeMap = jsonUtil.Json2Map(busJson);
        		Map transitsMap = jsonUtil.Json2Map(jsonUtil.Object2Json(routeMap.get("route")));
        		List transitsJsonList = jsonUtil.Json2List(jsonUtil.Object2Json(transitsMap.get("transits")));

             //循环乘车方案
//        		for (int i = 0; i < transitsJsonList.size(); i++) {
        		for (int i = 0; i < 1; i++) {
        			String transits = jsonUtil.Object2Json(transitsJsonList.get(i));
        			Map segmentsMap = jsonUtil.Json2Map(transits);
        			List segmentsList = jsonUtil.Json2List(jsonUtil.Object2Json(segmentsMap.get("segments")));
        			duration = segmentsMap.get("duration").toString();
        			distance = segmentsMap.get("distance").toString();
        			//储存换乘信息的LIST
    				List <Map<String,String>> segmentList = new LinkedList<Map<String,String>>();
        			//循环换乘线路
        			for (int j = 0; j < segmentsList.size(); j++) {
        				//储存换乘信息
        				Map<String,String> transferMap = new HashMap();
        				//获取换乘方案
        				Map segmentMap = jsonUtil.Json2Map(jsonUtil.Object2Json(segmentsList.get(j)));
        				
        				//walking不为空的情况下，取出步行的信息
        				if(segmentMap.get("walking").toString() != "[]"){
        					Map walkMap = jsonUtil.Json2Map(jsonUtil.Object2Json(segmentMap.get("walking")));
        					transferMap.put("walkdist", walkMap.get("distance").toString());
        					transferMap.put("walkdura", walkMap.get("duration").toString());
        				}
        				else{
        					transferMap.put("walkdist", "0");
        					transferMap.put("walkdura", "0");
        				}
			
        				Map buslinesMap = jsonUtil.Json2Map(jsonUtil.Object2Json(segmentMap.get("bus")));
        				List buslinesList = jsonUtil.Json2List(jsonUtil.Object2Json(buslinesMap.get("buslines")));
//        				System.out.println(buslinesList.size());
        				//循环公交
        				for (int k = 0; k < buslinesList.size(); k++) {
        					Map busMap = jsonUtil.Json2Map(jsonUtil.Object2Json(buslinesList.get(k)));
        					Map departureMap = jsonUtil.Json2Map(jsonUtil.Object2Json(busMap.get("departure_stop")));
        					Map arrivalMap = jsonUtil.Json2Map(jsonUtil.Object2Json(busMap.get("arrival_stop")));
        					//获取上车站点名称，id,经纬度
        					transferMap.put("busdeparname", departureMap.get("name").toString());
        					transferMap.put("busdeparid", departureMap.get("id").toString());
        					transferMap.put("busdeparloc", departureMap.get("location").toString());
        					//获取下车站点名称，id,经纬度
        					transferMap.put("busarrivname", arrivalMap.get("name").toString());
//        					System.err.println("busarrivname" + transferMap.get("busarrivname"));
        					transferMap.put("busarrivid", arrivalMap.get("id").toString());
        					transferMap.put("busarrivloc", arrivalMap.get("location").toString());
        					//获取上车线路
        					transferMap.put("busname", busMap.get("name").toString());
        					transferMap.put("busid", busMap.get("id").toString());
        					//获取经停信息
        					List viaList =  jsonUtil.Json2List(jsonUtil.Object2Json(busMap.get("via_stops")));
        					
        					viaLists.add(transferMap.get("busdeparname"));
        					viaLists.add(transferMap.get("busdeparloc"));
        					for(int m=0;m<viaList.size();m++){
        						Map viaMap = jsonUtil.Json2Map(jsonUtil.Object2Json(viaList.get(m)));
        						viaLists.add(viaMap.get("name"));
        						viaLists.add(viaMap.get("location"));
        					}
        					viaLists.add(transferMap.get("busarrivname"));
        					viaLists.add(transferMap.get("busarrivloc"));
        				}
        				//将完整信息放入方案List里
        				segmentList.add(transferMap);
        			}
        			//将方案信息放入方案list
        			transitsList.add(segmentList);
        		}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(client != null){
                client.close();
            }
        }
        resultList.add(transitsList);
        resultList.add(viaLists);
        resultList.add(duration);
        resultList.add(distance);
    	return resultList;
    }
    //根据城市名称获取经纬度
    public  String addressToGPS(String url) {  
    	String LaLongitude = "";
    	AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
        builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
        builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
     
        AsyncHttpClient client = new AsyncHttpClient(builder.build());
        try {
            ListenableFuture<Response> future = client.prepareGet(url).execute();
            String busJson = future.get().getResponseBody();
            JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(future.get().getResponseBody());
            if(jsonNode.findValue("status").textValue().equals("1")) {
            	LaLongitude = jsonNode.findValue("location").toString();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(client != null){
                client.close();
            }
        }
		return LaLongitude;
        
    }
}