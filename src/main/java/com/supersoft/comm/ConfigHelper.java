package com.supersoft.comm;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * 配置工具类，用于返回不同类型的配置项
 * 不依赖于spring, 因为有些配置项需要在spring初始化前调用.
 * 
 */
public class ConfigHelper {

    /**
     * 带有property-placeholder 对象的spring配置文件
     */
    private static final String SPRING_CONFIG_XML = "spring.xml";

	private static Properties instance;
	
	static {
		String prop = choosePropFromXml();
		prop = prop.replace("classpath:", "").trim();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(prop);
		if(is == null)	
			throw new RuntimeException("没有找到 classpath下关键配置文件:" + prop);
		
		instance = new Properties();
		try {
			instance.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally	{
			Utils.closeStream(is);
		}
	}

	/**
	 * 从spring.xml查找需要加载的properties
	 * @return
	 */
	private static String choosePropFromXml()	{

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(SPRING_CONFIG_XML);
		if(is == null)
			throw new RuntimeException("没有找到classpath下关键配置文件:" + SPRING_CONFIG_XML);
		try {
			SAXReader reader = new SAXReader();
			reader.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					return new InputSource(new ByteArrayInputStream("".getBytes()));
				}
			});
			Element root = reader.read(is).getRootElement();
            Element element = root.element("property-placeholder");
            String propFile = element.attributeValue("location");
			if(propFile == null)
				throw new RuntimeException("在spring配置文件[" + SPRING_CONFIG_XML + "]中没有查到properties文件:" + propFile);
			return propFile;
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		} finally	{
			Utils.closeStream(is);
		}
	}

	public static String getProperty(String key) {
		return instance.getProperty(key);
	}

	public static String getStringByKey(String key, String defaultStr) {
		if(null == key){
			return defaultStr;
		}
		return instance.getProperty(key, defaultStr);
	}

	public static int getIntByKey(String key, int defaultInt) {
		String resultValue = instance.getProperty(key);
		try {
			return Integer.parseInt(resultValue);
		} catch (Throwable e) {
			return defaultInt;
		}
	}

}
