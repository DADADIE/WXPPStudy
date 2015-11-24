package com.supersoft.stone.service;

import com.supersoft.stone.pojo.ReceiveXmlBean;

/**
 * User:hacker
 * Date:2015/10/26
 * Time:22:51
 * Description:This class is created to ...
 */
public interface RobotService {
    /**
     * 获取机器人处理结果
     * @param xmlBean
     * @return
     */
    public String getXmlResponse(ReceiveXmlBean xmlBean);
}
