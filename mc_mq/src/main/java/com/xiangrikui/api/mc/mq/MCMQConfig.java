package com.xiangrikui.api.mc.mq;

import com.xiangrikui.api.mc.common.config.Config;

/**
 * 
 * MQ配置管理文件
 * 因mc_mq为独立模块，因此配置文件也是独立一份
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月1日 下午12:49:15
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MCMQConfig
{
	private static Config configInstance = null;
	
	public static final String BROKER_URL = "broker_url";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String QUEUE_NAME = "queue_name";
	public static final String QUEUE_COUNT = "queue_count";
	
	public static void init(String basePath)
	{
		String path = basePath + "mq.properties";
		configInstance = new Config(path);
	}
	
	public static String getBrokerUrl()
	{
		return configInstance.getSetting(BROKER_URL);
	}
	
	public static String getUsername()
	{
		return configInstance.getSetting(USERNAME);
	}
	
	public static String getPassword()
	{
		return configInstance.getSetting(PASSWORD);
	}
	
	public static String getQueueName()
	{
		return configInstance.getSetting(QUEUE_NAME);
	}
	
	public static int getQueueCount()
	{
		return configInstance.getIntSetting(QUEUE_COUNT);
	}
}
