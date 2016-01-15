package com.supersoft.stone.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.supersoft.stone.pojo.ReceiveXmlBean;
import com.supersoft.stone.service.EhcacheService;
import com.supersoft.stone.service.WxProcessService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @author 关军浦
 * @date 2015-10-26
 * @description 微信公众平台被动消息入口
 */
@Controller
@RequestMapping("api/ehcache")
public class EhcacheController {

	@Resource
	private EhcacheService ehcacheService;

	@RequestMapping("/getCache")
	@ResponseBody
	public String getCache(HttpServletRequest request){
		long startTime = System.currentTimeMillis();
		JSONObject cache = ehcacheService.getCache("100300");
		long endTime = System.currentTimeMillis();
		return "获取storeId:"+ cache.getString("storeId")+"耗时:"+(endTime-startTime)+"ms";
	}
}
