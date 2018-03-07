package com.lty.common.bean.gps;

/**
 *
 * @author zhouyongbo 2017/12/3
 */
public class User extends GatewayPack {
    private String account;
    private String password;

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
