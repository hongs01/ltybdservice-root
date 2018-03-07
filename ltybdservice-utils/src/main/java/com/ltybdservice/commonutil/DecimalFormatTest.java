package com.ltybdservice.commonutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class DecimalFormatTest {
    private static final Logger LOG = LoggerFactory.getLogger(DecimalFormatTest.class);
    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static void main(String[] args) {
        LOG.error("int/int = " + Double.parseDouble(df.format((double) 1000 / 3)));


        //待测试数据
        long i = 1;
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(2);
        //设置最小整数位数
        nf.setMinimumIntegerDigits(2);
        //输出测试语句
        System.out.println(nf.format(i));
    }
}
