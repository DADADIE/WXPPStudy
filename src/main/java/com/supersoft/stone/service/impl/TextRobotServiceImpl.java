package com.supersoft.stone.service.impl;

import com.supersoft.stone.pojo.ReceiveXmlBean;
import com.supersoft.stone.service.RobotService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * User:hacker
 * Date:2015/10/28
 * Time:0:36
 * Description:This class is created to ...
 */
@Service
public class TextRobotServiceImpl implements RobotService {
    /**
     * 获取文本机器人处理结果
     * @param xmlBean
     * @return
     */
    @Override
    public String getXmlResponse(ReceiveXmlBean xmlBean) {
        /**获取处理结果串**/
        String result = "一块有梦想的石头到底能走多远---坚持之道。";
        return formatXmlAnswer(xmlBean.getFromUserName(), xmlBean.getToUserName(),result);
    }

    /**
     * 封装文字类的xml返回消息
     * @param to
     * @param from
     * @param content
     * @return
     */
    public String formatXmlAnswer(String to, String from, String content) {
        StringBuffer sb = new StringBuffer();
        Date date = new Date();
        sb.append("<xml><ToUserName><![CDATA[");
        sb.append(to);
        sb.append("]]></ToUserName><FromUserName><![CDATA[");
        sb.append(from);
        sb.append("]]></FromUserName><CreateTime>");
        sb.append(date.getTime());
        sb.append("</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[");
        sb.append(content);
        sb.append("]]></Content><FuncFlag>0</FuncFlag></xml>");
        return sb.toString();
    }
}
