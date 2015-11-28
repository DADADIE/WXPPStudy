package com.supersoft.stone.controller;

import com.supersoft.stone.pojo.ReceiveXmlBean;
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
@RequestMapping("/api/wxEnter")
public class WxEnterController {

	@Resource
	private WxProcessService wxProcessServiceImpl;

	@RequestMapping("/index")
	public String index(){
		return "index";
	}

	@RequestMapping("/entry")
	@ResponseBody
	public String entry(HttpServletRequest request){
		try {
			/**判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回**/
			String echostr1 = checkFirst(request);
			if (echostr1 != null) return echostr1;
			/**读取来自微信的xml消息**/
			String xml = receiveXmlMsg(request);
			/**绑定xml到xmlBean处理对象**/
			ReceiveXmlBean xmlBean = bindXmltoBean(xml);
			/**微信消息处理流程**/
			return wxProcessServiceImpl.processWechatMsg(xmlBean);
		} catch (IOException e) {
			System.out.println("WxEnterController异常: "+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回
	 * @param request
	 * @return
	 */
	private String checkFirst(HttpServletRequest request) {
		String echostr = request.getParameter("echostr");
/*		*//** 微信加密签名 **//*
		String signature = request.getParameter("signature");
		*//** 随机字符串  **//*
		String echostr = request.getParameter("echostr");
		*//** 时间戳  **//*
		String timestamp = request.getParameter("timestamp");
		*//** 随机数  **//*
		String nonce = request.getParameter("nonce");

		String[] str = {WXConfig.getInstance().getToken(), timestamp, nonce };
		Arrays.sort(str); *//** 字典序排序 **//*
		String bigStr = str[0] + str[1] + str[2];
		*//** SHA1加密 **//*
		*//** 确认请求来至微信 **/
		if(echostr!=null && echostr.length()>1)
            return echostr;
		return null;
	}

	/**
	 * 读取来自微信的xml消息
	 * @param request
	 * @throws IOException
	 */
	private String receiveXmlMsg(HttpServletRequest request) throws IOException {
		StringBuffer xmlSB = new StringBuffer();
		InputStream is = request.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String s = "";
		while((s=br.readLine())!=null){
            xmlSB.append(s);
        }
		return xmlSB.toString();
	}
	private ReceiveXmlBean bindXmltoBean(String xml) {
		ReceiveXmlBean xmlMsg = null;
		try {
			if (xml.length() <= 0 || xml == null)
				return null;
			/** 将字符串转化为XML文档对象 **/
			Document document = DocumentHelper.parseText(xml);
			/** 获得文档的根节点 **/
			Element root = document.getRootElement();
			/** 遍历根节点下所有子节点 **/
			Iterator<?> iterator = root.elementIterator();

			/** 遍历所有结点 **/
			xmlMsg = new ReceiveXmlBean();
			/**利用反射机制，调用set方法 **/
			/**获取该实体的元类型 **/
			Class<?> c = Class.forName("com.supersoft.stone.pojo.ReceiveXmlBean");
			xmlMsg = (ReceiveXmlBean)c.newInstance();/**创建这个实体的对象**/

			while(iterator.hasNext()){
				Element element = (Element)iterator.next();
				/**获取set方法中的参数字段（实体类的属性）**/
				Field field = c.getDeclaredField(element.getName());
				/**获取set方法，field.getType())获取它的参数数据类型**/
				Method method = c.getDeclaredMethod("set"+element.getName(), field.getType());
				/**调用set方法**/
				method.invoke(xmlMsg, element.getText());
			}
		} catch (Exception e) {
			System.out.println("xml 格式异常: "+ xmlMsg);
			e.printStackTrace();
		}
		return xmlMsg;
	}
}
