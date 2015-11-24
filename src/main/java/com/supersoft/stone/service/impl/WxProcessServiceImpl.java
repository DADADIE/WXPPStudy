package com.supersoft.stone.service.impl;

import com.supersoft.stone.pojo.ReceiveXmlBean;
import com.supersoft.stone.service.RobotService;
import com.supersoft.stone.service.WxProcessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User:hacker
 * Date:2015/10/26
 * Time:21:49
 * Description:This class is created to ...
 */
@Service
public class WxProcessServiceImpl implements WxProcessService{

    @Resource
    private RobotService textRobotServiceImpl;

    /**
     * 处理所有来自微信的消息后回复消息
     * @param xml
     * @return
     */
    public String processWechatMsg(ReceiveXmlBean xml) {
        /** 以文本消息为例，调用图灵机器人api接口，获取回复内容 */
        String result = "";
        try{
            if("text".endsWith(xml.getMsgType())){
                result = textRobotServiceImpl.getXmlResponse(xml);
            }
        }catch(Exception e){
            System.out.println("WxProcessServiceImpl异常: "+ e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
