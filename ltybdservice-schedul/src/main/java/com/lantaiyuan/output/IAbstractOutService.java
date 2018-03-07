package com.lantaiyuan.output;

import com.lantaiyuan.bean.output.OutParameter;
import com.lantaiyuan.common.exception.ServiceClassNotFoundException;
import com.lantaiyuan.service.IAbstractService;

import java.util.HashMap;
import java.util.Map;

/**
 *  输出定义
 * Created by zhouyongbo on 2017-11-22.
 */
public abstract class IAbstractOutService<T extends OutParameter> {

    /**
     * 缓存对象 --- 单例
     */
    private static Map<String,Object> classMap = new HashMap<String,Object>();

    public abstract void init();


    public static <T> T getInstance(Class t){
        Object o = classMap.get(t.getName());
        if (o == null){
            throw new ServiceClassNotFoundException("错 误 ! "+t.getName()+" 类 找 不 到");
        }
        return (T)o;
    }

    public static void doInit(){
        for (String name:classMap.keySet()){
            IAbstractOutService o = (IAbstractOutService) classMap.get(name);
            o.init();
        }
    }

    public static void putClassMap(String name,Object o ){
        classMap.put(name,o);
    }


    public abstract void sendMsg(T outParameter);


}
