package com.lantaiyuan.utils;

import com.lantaiyuan.common.lastInit.LastInit;
import com.lantaiyuan.output.IAbstractOutService;
import com.lantaiyuan.service.IAbstractService;
import com.lantaiyuan.start.AbstractStart;
import com.lantaiyuan.common.scanner.ClassPathPackageScanner;
import com.lantaiyuan.staticCache.IStaticCache;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫描解析
 * Created by zhouyongbo on 2017-11-15.
 */
public class ScannerClassUtil {

    /**
     * 启动类
     * @param abstractStarts
     */
    public static void startMain(List<Class> abstractStarts){
        try {
            for (Class abstractStart:abstractStarts){
                if (Modifier.isAbstract(abstractStart.getModifiers()))continue;
//                new Thread(() -> {
                    try {
                        Constructor declaredConstructor = abstractStart.getDeclaredConstructor();
                        declaredConstructor.setAccessible(true);
                        AbstractStart start = (AbstractStart) declaredConstructor.newInstance();
                        start.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassCastException(e.getMessage());
        }
    }


    public static void initCache(List<Class> staticClass) throws SQLException {
            for (Class abstractStart:staticClass ){
                Constructor declaredConstructor = null;
                try {
                    declaredConstructor = abstractStart.getDeclaredConstructor();
                    declaredConstructor.setAccessible(true);
                    IStaticCache start = (IStaticCache) declaredConstructor.newInstance();
                    start.init();
                    start.execute();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
    }

    /**
     *  初始化配置文件
     * @param configClass
     * @throws Exception
     */
    public static void initProperties(List<Class> configClass) throws Exception {
        Map<String, Object> dataMap = YAMLReadUtil.readYml(Thread.currentThread().getContextClassLoader());
        for (Class config:configClass){
            Object conObj = config.newInstance();
            //得到前缀
                String prefix = (String) config.getMethod("getPrefix").invoke(conObj,null);
                if (prefix==null || "".equals(prefix.trim())){
                    prefix = "";
                }
                String[] split = prefix.split("\\.");
                Map<String, Object> map = YAMLReadUtil.getMap(dataMap, split, 0);
                //得到的数据 将直接赋值给属性
                YAMLReadUtil.setConfig(config,map);
        }
    }


    /**
     * 寻找class 文件
     * @param aClass
     * @throws Exception
     */
    public static List<String> findClass(Class aClass)throws Exception{
        ClassPathPackageScanner classPathPackageScanner =
                new ClassPathPackageScanner(aClass.getPackage().getName(), Thread.currentThread().getContextClassLoader());
        List<String> fullyQualifiedClassNameList = classPathPackageScanner.getFullyQualifiedClassNameList();
        if (null == fullyQualifiedClassNameList || fullyQualifiedClassNameList.size() == 0){
            throw new ClassCastException("包不存在或者找不到启动类");
        }
        return fullyQualifiedClassNameList;
    }



    //解析
    public static  List<Class> analysisClass(List<String> classNames,Class type) throws Exception {
        //拿到类名
        List<Class> abstractStarts = new ArrayList<Class>();
        for (String className:classNames){
            Class classIn = Class.forName(className,false, Thread.currentThread().getContextClassLoader());
            if (classIn == type ) {
                continue;
            }
            if (type.isAssignableFrom(classIn)){
                abstractStarts.add(classIn);
            }
        }
        return abstractStarts;
    }


    /**
     * 注入service
     * @param serviceClass
     */
    public static void initService(List<Class> serviceClass) throws SQLException {
        for (Class clazz:serviceClass){
            try {
                Constructor declaredConstructor = clazz.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                Object o = declaredConstructor.newInstance();
                IAbstractService.putClassMap(clazz.getName(),o);
            } catch (Exception e){
                e.printStackTrace();
                throw new ClassCastException("service 注入失败;"+clazz.getName());
            }
        }
        IAbstractService.doInit();
    }


    public static void initOutputService(List<Class> outClass) {
        for (Class clazz:outClass){
            try {
                Constructor declaredConstructor = clazz.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                Object o = declaredConstructor.newInstance();
                IAbstractOutService.putClassMap(clazz.getName(),o);
            } catch (Exception e){
                e.printStackTrace();
                throw new ClassCastException("service 注入失败;"+clazz.getName());
            }
        }
        IAbstractOutService.doInit();
    }

    public static void  startLastInit(List<Class> lastInit) {
        try {
            for (Class aClass:lastInit){
                if (Modifier.isAbstract(aClass.getModifiers()))continue;
                new Thread(() -> {
                    try {
                        Constructor declaredConstructor = aClass.getDeclaredConstructor();
                        declaredConstructor.setAccessible(true);
                        LastInit start = (LastInit) declaredConstructor.newInstance();
                        start.lastExecuteInit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassCastException(e.getMessage());
        }
    }
}
