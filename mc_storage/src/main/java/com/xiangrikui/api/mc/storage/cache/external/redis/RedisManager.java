package com.xiangrikui.api.mc.storage.cache.external.redis;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.storage.cache.CacheConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * Redis管理类，单例，初始化时载入所有Redis配置项，并管理redis连接池
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年11月16日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class RedisManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);
	
	private static volatile RedisManager instance = null;
	
	//redis连接池单例，断定不为空
	private IRedisPool redisPool;
	
	public static RedisManager getInstance()
	{
		if (instance == null)
		{
			synchronized (RedisManager.class)
			{
				if (instance == null)
				{
					instance = new RedisManager();
				}
			}
		}
		return instance;
	}
	
	private RedisManager()
	{
		LOGGER.info("RedisManager, init redis start ...");
		Set<String> sentinels = new HashSet<String>();
		
		//String reAddr = SysConfigCache.getInstance().getRedisAddr();
		String reAddr = CacheConfig.getRedisHost();
		//boolean isCluster = SysConfigCache.getInstance().isRedisCluster();
		boolean isCluster = CacheConfig.redisIsCluster();
		
		String[] aryAddr = reAddr.split(",");
		String host = "";
		int port = 0;
		for (String addr : aryAddr)
		{
			if (addr.isEmpty()) 
			{
				continue;
			}

			String[] ary = addr.split(":");
			if (ary.length != 2) 
			{
				LOGGER.warn("redis config wrong addr: {}", addr);
				continue;
			}

			if (!ary[1].matches("\\d+"))
			{
				LOGGER.warn("redis config wrong port! addr = {}", addr);
				continue;
			}

			port = Integer.parseInt(ary[1]);
			host = ary[0];
			sentinels.add(new HostAndPort(host, port).toString());			 
		}
		
		if(isCluster)
		{
			JedisPoolConfig config = new JedisPoolConfig();
			//获取连接时的最大等待时间
			config.setMaxWaitMillis(60000);
			//最大连接数
			config.setMaxTotal(100);
			//最大空闲连接数
			config.setMaxIdle(20);
			//获取连接时是否测试连接可用性
			config.setTestOnBorrow(false);
			//返回连接时是否测试连接可用性
			config.setTestOnReturn(false);
			//是否验证空闲连接
			config.setTestWhileIdle(false);
			//每隔60秒异步检查空闲连接可用性
			config.setTimeBetweenEvictionRunsMillis(60000);
			//空闲连接保持在连接池中不被回收的最大时间
			config.setMinEvictableIdleTimeMillis(120000);
			//每次扫描最多扫描空闲连接数，-1代表扫描所有
			config.setNumTestsPerEvictionRun(-1);

			LOGGER.info("RedisManager, create a redisSentinelPool");
			//redisPool = new RedisSentinelPool(SysConfigCache.getInstance().getRedisMasterName(), sentinels, config);
			redisPool = new RedisSentinelPool(CacheConfig.getRedisSentinelMasterName(), sentinels, config);
		}
		else
		{
			//这里redisClient用RedisPool
			//RedisPool实现了业务所需的JedisCommands内的部分接口
			LOGGER.info("RedisManager, create a redisPool");
			redisPool = new RedisPool(host, port);
		}
		
		LOGGER.info("RedisManager, init redis end ...");
	}
	
	public IRedisPool getPool()
	{
		return redisPool;
	}
}
