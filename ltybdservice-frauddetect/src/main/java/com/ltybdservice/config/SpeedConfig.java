package com.ltybdservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * @author: Administrator$
 * @project: ltybdservice-root$
 * @date: 2017/11/22$ 16:23$
 * @description:
 */
public class SpeedConfig {
    private float speedCriterion;
    public SpeedConfig() {
        Properties pro = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/config.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig(pro);
    }
    private void getConfig(Properties pro) {
        speedCriterion=Float.parseFloat(pro.getProperty("speedCriterion"));
    }

    public float getSpeedCriterion() {
        return speedCriterion;
    }

    public void setSpeedCriterion(float speedCriterion) {
        this.speedCriterion = speedCriterion;
    }
}
