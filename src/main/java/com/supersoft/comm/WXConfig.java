package com.supersoft.comm;

import com.alibaba.fastjson.JSONObject;

/**
 * @author guanjunpu
 * @date   2015/10/27
 */
public class WXConfig {

    /**
     * wx公众平台配置的对接token
     */
    String token = "";

    private static WXConfig instance = null;

    public static WXConfig getInstance(){
        if(instance == null){
            instance = new WXConfig();
            instance.setToken(ConfigHelper.getProperty("WX_TOKEN"));
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
