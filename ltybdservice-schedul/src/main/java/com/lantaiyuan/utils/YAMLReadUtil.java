package com.lantaiyuan.utils;

import org.ho.yaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyongbo on 2017-11-16.
 */
public class YAMLReadUtil {

    /**
     * 读取YML
     * @param classLoader
     * @return
     * @throws IOException
     */
    public static Map<String,Object> readYml(ClassLoader classLoader) throws IOException {
        InputStream resourceAsStream = classLoader.getResourceAsStream("system-set.yml");
       HashMap<String,Object>  stringObjectHashMap = Yaml.loadType(resourceAsStream, HashMap.class);
        if (resourceAsStream != null ){
                resourceAsStream.close();
            }
        return stringObjectHashMap;
    }


    /**
     * 根据前拿取配置文件数据
     * @param dataMap
     * @param key
     * @param index
     * @return
     */
    public static Map<String,Object> getMap(Map<String,Object> dataMap,String [] key,int index){
        if (key == null || key.length ==0 )return dataMap;

        if ((key.length -1) == index){
            return (Map<String, Object>) dataMap.get(key[index]);
        }
        Object o = dataMap.get(key[index]);
        if (o instanceof Map){
            return getMap((Map<String, Object>) o ,key,index+1);
        }
        return dataMap;
    }


    /**
     * 设置配置文件
     * @param confiClass  配置文件Class
     * @param dataMap 属性
     */
    public static void setConfig(Class confiClass,Map<String,Object> dataMap)  {
        try {
            Object obj = confiClass.newInstance();
            if (dataMap == null ) return;
            for (String key:dataMap.keySet()){
                Field declaredField = confiClass.getDeclaredField(key);
                if (declaredField == null) continue;
                Class type = declaredField.getType();
                declaredField.setAccessible(true);
                if (type == List.class){
                    String value = (String) dataMap.get(key);
                    List<String> strings = Arrays.asList(value.split(","));
                    declaredField.set(obj,strings);
                }else if (Map.class.isAssignableFrom(type)){
                    declaredField.set(obj,dataMap.get(key));
                }else{
                    declaredField.set(obj,dataMap.get(key));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

    }
}
