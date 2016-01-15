package com.supersoft.stone.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.supersoft.stone.service.EhcacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by guanjunpu on 2016/1/15.
 */
@Service
public class EhcacheServiceImpl implements EhcacheService{

    //将查询到的数据缓存到myCache中,并使用方法名称加上参数中的userNo作为缓存的key
    //通常更新操作只需刷新缓存中的某个值,所以为了准确的清除特定的缓存,故定义了这个唯一的key,从而不会影响其它缓存值
    @Cacheable(value="myCache", key="'get'+#mcode")
    public JSONObject getCache(String mcode){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据库查到此mcode对应storeId：123456");
        JSONObject storeId = new JSONObject();
        storeId.put("storeId","654321");
        return storeId;
    }
}
