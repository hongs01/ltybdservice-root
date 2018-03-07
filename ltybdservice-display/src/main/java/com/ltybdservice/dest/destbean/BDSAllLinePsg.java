package com.ltybdservice.dest.destbean;

import com.alibaba.fastjson.JSON;
import com.ltybdservice.hbaseutil.IEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class BDSAllLinePsg implements IEvent, Serializable {
    private int type;//	类型，类似header区分业务
    private BDSAllLinePsgData data = new BDSAllLinePsgData();

    @Override
    public String getValueByField(String field) {
        String string = null;
        switch (field) {
            case "type":
                string = String.valueOf(type);
                break;
            case "data":
                string = JSON.toJSONString(data);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) throws ParseException {
        switch (field) {
            case "type":
                type = Integer.parseInt(value);
                break;
            case "data":
                data = JSON.parseObject(value,BDSAllLinePsgData.class);
                break;
        }
        return this;
    }

    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("type");
        Fields.add("data");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }

    public static class BDSAllLinePsgData implements  Serializable{
        private String citycode;//	城市id
        private int total_psg;// 	客流量（从0点到现在的客流量）

        public String getCitycode() {
            return citycode;
        }

        public void setCitycode(String citycode) {
            this.citycode = citycode;
        }

        public int getTotal_psg() {
            return total_psg;
        }

        public void setTotal_psg(int total_psg) {
            this.total_psg = total_psg;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BDSAllLinePsgData getData() {
        return data;
    }

    public void setData(BDSAllLinePsgData data) {
        this.data = data;
    }
}
