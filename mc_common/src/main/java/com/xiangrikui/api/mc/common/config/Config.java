package com.xiangrikui.api.mc.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 基本Config工具类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月1日 下午2:57:31
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class Config
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class); 
	
	protected Map<String, String> setting = new ConcurrentHashMap<String, String>();
	
	private String path;

	@SuppressWarnings("unused")
	private Config()
	{
		
	}
	
	public Config(String path)
	{
		iniSetting(path);
	}

	/**
	 * 初始化加载配置文件
	 * 
	 * @param path
	 *            加载路径
	 * @throws FileNotFoundException
	 */
	private synchronized void iniSetting(String path) 
	{
		this.path = path;
		
		File file;
		file = new File(path);
		FileInputStream in = null;
		try
		{
			in = new FileInputStream(file);
			Properties p = new Properties();
			p.load(in);
			// 遍历配置文件加入到Map中进行缓存
			Enumeration<?> item = p.propertyNames();
			while (item.hasMoreElements())
			{
				String key = (String) item.nextElement();
				setting.put(key, p.getProperty(key));
				LOGGER.info("load config, key: {}, value: {}", key, p.getProperty(key));
			}
			in.close();
		} 
		catch (FileNotFoundException e)
		{
			throw new ConfigException("FileNotFoundException", e);
		} 
		catch (IOException e) 
		{
			throw new ConfigException("IOException", e);
		} 
		catch (Exception e) 
		{
			throw new ConfigException("Exception", e);
		}
	}

	public void reload()
	{
		try
		{
			iniSetting(path);
		} 
		catch (ConfigException e) 
		{
			throw new ConfigException(e.getMessage(), e);
		}
	}

	/**
	 * 获取配置文件的某个键值的配置信息
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public String getSetting(String key)
	{
		return setting.get(key);
	}
	
	public int getIntSetting(String key)
	{
		String value = setting.get(key);
		try
		{
			return Integer.parseInt(value);
		}
		catch (Exception e)
		{
			throw new ConfigException("value is not int", e);
		}
	}
	
	public boolean getBooleanSetting(String key)
	{
		String value = setting.get(key);
		try 
		{
			return Boolean.parseBoolean(value);
		}
		catch (Exception e)
		{
			throw new ConfigException("value is not boolean", e);
		}
	}

	/**
	 * 设置配置文件的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void setSetting(String key, String value) 
	{
		setting.put(key, value);
	}
}
