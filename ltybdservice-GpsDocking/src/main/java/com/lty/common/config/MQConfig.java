package com.lty.common.config;

import com.lty.MQ.IAbsMqProduction;
import com.lty.MQ.production.KafkaProduction;
import com.lty.common.MQ.MQType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ使用配置文件
 *
 */
@Configuration
public class MQConfig {


    //默认为kafka类型
    @Bean
    public IAbsMqProduction getMqProduction(){
            Object type = BeUseingType.USEMQ.getType();
        if ( type == MQType.KAFKA){
            return new KafkaProduction();
        }
        return new KafkaProduction();
    }

}
