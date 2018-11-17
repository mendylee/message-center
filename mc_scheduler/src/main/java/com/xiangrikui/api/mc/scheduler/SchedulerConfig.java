package com.xiangrikui.api.mc.scheduler;

import com.xiangrikui.api.mc.common.config.Config;

/**
 * 
 * app层面的全局配置文件
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月1日 下午3:33:45
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SchedulerConfig
{
	private static Config configInstance = null;
	
	public static final String HANDLER_PACKAGE = "handler_package";
	public static final String CHANNEL_READ_TIMEOUT = "channel_read_timeout";
	public static final String CHANNEL_WRITE_TIMEOUT = "channel_write_timeout";
	public static final String DEFAULT_PORT = "default_port";
	public static final String PRODUCING_QUEUE_THREAD_COUNT = "producing_queue_thread_count";
	public static final String PRODUCING_QUEUE_SCHEDULED_PERIOD = "producing_queue_scheduled_period";
	
	public static void init(String basePath)
	{
		String path = basePath + "app.properties";
		configInstance = new Config(path);
	}
	
	public static String getHandlerPackage()
	{
		return configInstance.getSetting(HANDLER_PACKAGE);
	}
	
	public static int getChannelReadTimeout()
	{
		return configInstance.getIntSetting(CHANNEL_READ_TIMEOUT);
	}
	
	public static int getChannelWriteTimeout()
	{
		return configInstance.getIntSetting(CHANNEL_WRITE_TIMEOUT);
	}
	
	public static int getDefaultPort()
	{
		return configInstance.getIntSetting(DEFAULT_PORT);
	}
	
	public static int getProducingQueueThreadCount()
	{
		return configInstance.getIntSetting(PRODUCING_QUEUE_THREAD_COUNT);
	}
	
	public static int getProducingQueueScheduledPeriod()
	{
		return configInstance.getIntSetting(PRODUCING_QUEUE_SCHEDULED_PERIOD);
	}
}