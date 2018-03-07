package com.lty.netty.server.filter.decoder;

import com.lty.common.commonEnum.TypeDecoder;
import com.lty.common.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 译码处理
 * @author zhouyongbo 2017/12/3
 */
@Component
public class DecoderHandling {

    public AbstractDecoder getInstance(TypeDecoder typeDecoder){
        Object obj = null;
        if (typeDecoder == TypeDecoder.USER){
            obj =  SpringUtil.getBean(typeDecoder.getKey().getName());
        }
        return (AbstractDecoder) obj;
    }
}
