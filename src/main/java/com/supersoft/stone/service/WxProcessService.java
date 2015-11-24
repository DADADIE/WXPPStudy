package com.supersoft.stone.service;

import com.supersoft.stone.pojo.ReceiveXmlBean;

/**
 * User:guanjunpu
 * Date:2015/10/26
 * Time:21:46
 * Description:This class is created to ...
 */
public interface WxProcessService {
    /**
     * 处理所有来自微信的消息后回复消息
     * @param xml
     * @return
     */
    public String processWechatMsg(ReceiveXmlBean xml);
}
