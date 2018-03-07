package com.ltybdservice.hbaseutil;


import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description: 实现该接口用于映射对象实例与hbase中的一行
 */
public interface IEvent extends Serializable {
    /**
     * @return type: java.lang.String
     * @author: Administrator
     * @date: 2017/11/22 16:38
     * @method: getValueByField
     * @param: [field]
     * @desciption:
     */
    String getValueByField(String field);

    /**
     * @return type: com.ltybdservice.hbaseutil.IEvent
     * @author: Administrator
     * @date: 2017/11/22 16:38
     * @method: setValueByField
     * @param: [field, value]
     * @desciption:
     */
    IEvent setValueByField(String field, String value) throws ParseException;

    /**
     * @return type: java.util.ArrayList<java.lang.String>
     * @author: Administrator
     * @date: 2017/11/22 16:39
     * @method: eventGetFields
     * @param: []
     * @desciption:
     */
    ArrayList<String> eventGetFields();

    /**
     * @return type: java.lang.String
     * @author: Administrator
     * @date: 2017/11/22 16:39
     * @method: eventGetColumnFamily
     * @param: []
     * @desciption:
     */
    String eventGetColumnFamily();
}
