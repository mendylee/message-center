package com.xiangrikui.api.mc.executor;

import com.xiangrikui.api.mc.common.config.Config;

/**
 * 
 * app层面的全局配置文件
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月22日 下午5:59:52
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ExecutorConfig
{
	private static Config configInstance = null;
	
	public static final String MESSAGE_POSTING_QUEUE_THREAD_COUNT = "message_posting_queue_thread_count";
	public static final String MESSAGE_POSTING_QUEUE_SCHEDULED_PERIOD = "message_posting_queue_scheduled_period";
	
	public static void init(String basePath)
	{
		String path = basePath + "app.properties";
		configInstance = new Config(path);
	}
	
	public static int getMessagePostingQueueThreadCount()
	{
		return configInstance.getIntSetting(MESSAGE_POSTING_QUEUE_THREAD_COUNT);
	}
	
	public static int getMessagePostingQueueScheduledPeriod()
	{
		return configInstance.getIntSetting(MESSAGE_POSTING_QUEUE_SCHEDULED_PERIOD);
	}
}