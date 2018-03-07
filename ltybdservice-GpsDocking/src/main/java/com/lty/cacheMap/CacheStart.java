package com.lty.cacheMap;

import com.lty.cacheMap.cacheImp.TerminalToPhoneMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 缓存启动
 * @author zhouyongbo 2017/12/23
 */
@Component
public class CacheStart implements CommandLineRunner {

    @Autowired
    TerminalToPhoneMap  terminalToPhoneMap;

    @Override
    public void run(String... strings) throws Exception {
        terminalToPhoneMap.init();
    }
}
