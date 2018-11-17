package com.xiangrikui.api.mc.storage.queue;

import com.xiangrikui.api.mc.common.config.Config;

/**
 * 
 * 队列配置
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月2日 下午3:09:25
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class QueueConfig
{	
	private static Config configInstance = null;
	
	public static final String QUEUE_CLASS = "queue_class";
	public static final String QUEUE_FQUEUE_DATA_DIR = "queue_fqueue_data_dir";
	public static final String QUEUE_FQUEUE_FILE_SIZE = "queue_fqueue_file_size";
	
	public static void init(String basePath)
	{
		String path = basePath + "queue.properties";
		configInstance = new Config(path);
	}
	
	public static String getQueueClass()
	{
		return configInstance.getSetting(QUEUE_CLASS);
	}
	
	public static String getFQueueDataDir()
	{
		return configInstance.getSetting(QUEUE_FQUEUE_DATA_DIR);
	}
	
	public static int getFQueueFileSize()
	{
		return configInstance.getIntSetting(QUEUE_FQUEUE_FILE_SIZE);
	}
}
