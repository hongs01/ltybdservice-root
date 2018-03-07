package com.ltybdservice.hbaseutil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class TestUser implements IEvent, Serializable {
    String name;
    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String getValueByField(String field) {
        String string = null;
        switch (field) {
            case "name":
                string = name;
                break;
            case "age":
                string = String.valueOf(age);
                break;
        }
        return string;
    }

    @Override
    public IEvent setValueByField(String field, String value) {
        switch (field) {
            case "name":
                name = value;
                break;
            case "age":
                age = Integer.parseInt(value);
                break;
        }
        return this;
    }

    @Override
    public ArrayList<String> eventGetFields() {
        ArrayList<String> Fields = new ArrayList<String>();
        Fields.add("name");
        Fields.add("age");
        return Fields;
    }

    @Override
    public String eventGetColumnFamily() {
        return "cf";
    }
}
