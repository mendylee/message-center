package com.xiangrikui.api.mc.storage.cache;

import com.xiangrikui.api.mc.common.config.Config;

/**
 * 
 * CacheConfig: CacheConfig.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月1日 下午3:49:06
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class CacheConfig
{
	private static Config configInstance = null;
	
	public static final String CACHE_CLASS = "cache_class";
	public static final String CACHE_COMPONENT_PACKAGE = "cache_component_package";
	public static final String REDIS_HOST = "redis_host";
	public static final String REDIS_IS_CLUSTER = "redis_is_cluster";
	public static final String REDIS_SENTINEL_MASTER_NAME = "redis_sentinel_master_name";
	
	public static void init(String basePath)
	{
		String path = basePath + "cache.properties";
		configInstance = new Config(path);
	}
	
	public static String getCacheClass()
	{
		return configInstance.getSetting(CACHE_CLASS);
	}
	
	public static String getCacheComponentPackage()
	{
		return configInstance.getSetting(CACHE_COMPONENT_PACKAGE);
	}
	
	public static String getRedisHost()
	{
		return configInstance.getSetting(REDIS_HOST);
	}
	
	public static boolean redisIsCluster()
	{
		return configInstance.getBooleanSetting(REDIS_IS_CLUSTER);
	}
	
	public static String getRedisSentinelMasterName()
	{
		return configInstance.getSetting(REDIS_SENTINEL_MASTER_NAME);
	}
}
