package com.supersoft.stone.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by guanjunpu on 2016/1/15.
 */
public interface EhcacheService {
    JSONObject getCache(String mcode);
}
