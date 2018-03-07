package com.lty.controller;

import com.lty.cacheMap.cacheImp.TerminalToPhoneMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GpsInitController {

    @Autowired
    TerminalToPhoneMap terminalToPhoneMap;

    @RequestMapping("/init")
    public Object init(){
        terminalToPhoneMap.init();
        return "缓存初始化成功";
    }
}
