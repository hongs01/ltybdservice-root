package com.lantaiyuan.utils;

import com.lantaiyuan.common.exception.ClassRunException;
import com.lantaiyuan.common.config.ISetProperties;
import com.lantaiyuan.common.lastInit.LastInit;
import com.lantaiyuan.output.IAbstractOutService;
import com.lantaiyuan.service.IAbstractService;
import com.lantaiyuan.start.AbstractStart;
import com.lantaiyuan.staticCache.IStaticCache;

import java.util.List;

/**
 * Created by zhouyongbo on 2017-11-16.
 */
public class StartUtil {

    //启动
    public static void startRunable(Class aClass) {
        try {
            List<String> aClass1 = ScannerClassUtil.findClass(aClass);
            //配置
            List<Class> setClass = ScannerClassUtil.analysisClass(aClass1, ISetProperties.class);
            ScannerClassUtil.initProperties(setClass);


            //service 注入
            List<Class> serviceClass = ScannerClassUtil.analysisClass(aClass1, IAbstractService.class);
            ScannerClassUtil.initService(serviceClass);


           //缓存
            List<Class> staticClass = ScannerClassUtil.analysisClass(aClass1, IStaticCache.class);
            ScannerClassUtil.initCache(staticClass);

            //输出源注入
            List<Class> outClass = ScannerClassUtil.analysisClass(aClass1, IAbstractOutService.class);
            ScannerClassUtil.initOutputService(outClass);

            //启动类
            List<Class> absStart = ScannerClassUtil.analysisClass(aClass1, AbstractStart.class);
            ScannerClassUtil.startMain(absStart);
            //最后启动接口类
            List<Class> lastInit = ScannerClassUtil.analysisClass(aClass1, LastInit.class);
            ScannerClassUtil.startLastInit(lastInit);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassRunException(e.getMessage());
        }
    }
}
