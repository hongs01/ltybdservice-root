package com.lty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableScheduling
public class StartApplication{

    private static Logger logger = LoggerFactory.getLogger(StartApplication.class);

    public static void main( String[] args ) {
//        SpringApplication app = new SpringApplication(StartApplication.class);
//        app.setWebEnvironment(false);
//        app.run(args);
        SpringApplication.run(StartApplication.class,args);
        logger.info("spring application start succeed");
    }
//
//    @Override
//    public void run(String... strings) throws Exception {
//        logger.info("application start succeed");
//        //非web方式启动
//        Thread.currentThread().join();
//    }
}
