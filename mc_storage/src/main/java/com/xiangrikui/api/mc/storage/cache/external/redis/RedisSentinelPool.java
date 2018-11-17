package com.xiangrikui.api.mc.storage.cache.external.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * 
 * 基于sentinel集群的自定义连接池
 * 实现了JedisCommands接口的部分方法
 * 实现的方法有hget, hset, hexists, hdel, del, hlen
 *
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年10月27日
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class RedisSentinelPool implements IRedisPool
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelPool.class);
	
	private JedisSentinelPool sentinelPool;
	
	private int maxTotal = 50;
	private int maxWaitingMillis = 60000;
	private int maxIdle = 20;
	private boolean testConnection = true;
	private boolean testWhileIdel = true;
	
	//获取连接时的最大重试次数
	private int retryingCount = 10;
	//获取连接失败后的休眠时间
	private int retryingWaitingMillis = 2000;
	
	public RedisSentinelPool(String masterName, Set<String> sentinels)
	{
		init(masterName, sentinels, null);
	}
	
	public RedisSentinelPool(String masterName, Set<String> sentinels, GenericObjectPoolConfig config)
	{
		init(masterName, sentinels, config);
	}
	
	private void init(String masterName, Set<String> sentinels, GenericObjectPoolConfig config)
	{
		if (config == null)
		{
			config = new GenericObjectPoolConfig();
			//获取连接时的最大等待时间
			config.setMaxWaitMillis(maxWaitingMillis);
			//最大连接数
			config.setMaxTotal(maxTotal);
			//最大空闲连接数
			config.setMaxIdle(maxIdle);
			//获取连接时是否测试连接可用性
			config.setTestOnBorrow(testConnection);
			//
			config.setTestWhileIdle(testWhileIdel);
		}
		
		LOGGER.debug("init jedisSentinelPool start, masterName: {}", masterName);
		try
		{
			sentinelPool = new JedisSentinelPool(masterName, sentinels, config);
			LOGGER.debug("init jedisSentinelPool end, masterName: {}", masterName);
		}
		catch (Exception e)
		{
			LOGGER.debug("fail to init jedisSentinelPool, masterName: {}", masterName);
		}
	}
	
	public JedisSentinelPool getPool()
	{
		return sentinelPool;
	}
	
	/**
	 * 
	 * 从池中获取连接，获取失败后重试一定次数
	 * 
	 * 重试次数达到最大次数时，抛出运行时异常
	 *    
	 * @return
	 */
	@Override
	public Jedis getResource()
	{
		Jedis jedis = null;
		jedis = getResourceFromPool();
		int time = 1;
		while (jedis == null && time <= retryingCount)
		{
			LOGGER.debug("RedisSentinelPool, retry to get resource, time: {}", time);
			wait(retryingWaitingMillis);
			jedis = getResourceFromPool();
			time ++;
		}
		if (jedis == null)
		{
			LOGGER.error("RedisSentinelPool, fail to get resource from pool, jedis is null");
			throw new RuntimeException("RedisSentinelPool, fail to get resource from pool, jedis is null");
		}
		
		return jedis;
	}
	
	/**
	 * 
	 * 从池中安全地获取单个连接
	 * 假如出现异常报错，返回空
	 *    
	 * @return
	 */
	private synchronized Jedis getResourceFromPool()
	{
		Jedis jedis = null;
		try
		{
			jedis = sentinelPool.getResource();
		}
		catch (Exception e)
		{
			LOGGER.error("RedisSentinelPool, fail to get Resource", e);
		}
		return jedis;
	}
	
	/**
	 * 使程序等待给定的毫秒数
	 *
	 * @param 给定的毫秒数
	 */
	private void wait(int millis)
	{
		try 
		{
			Thread.sleep(millis);
		} 
		catch (InterruptedException e)
		{
			LOGGER.error(String.format("RedisSentinelPool, fail to sleep, msg: %s", e.getMessage()), e);
		}
	}
	
	/**
	 * 
	 * 根据异常的类型、异常信息判断当前的jedis连接是否出故障，并返回boolean值
	 *    
	 * @param exception
	 * @return
	 */
	@Override
	public boolean handleJedisException(Exception exception)
	{
		if (exception == null)
		{
			return false;
		}
		
		if (exception instanceof JedisException)
		{
			if (exception instanceof JedisConnectionException)
	    	{
				LOGGER.error("RedisSentinelPool, Redis connection lost.", exception);
	    	}
	    	else if (exception instanceof JedisDataException)
	    	{
	    		if ((exception.getMessage() != null) && (exception.getMessage().indexOf("READONLY") != -1)) 
	    		{
	    			LOGGER.error("RedisSentinelPool, Redis connection are read-only slave.", exception);
	            }
				else
				{
					// dataException, isBroken=false
					return false;
				}
			}
			else 
			{
				LOGGER.error("RedisSentinelPool, Jedis exception happen.", exception);
			}
			
			return true;
		}
    	
		LOGGER.error("RedisSentinelPool, normal exception happen, ", exception);
		return false;
	}
	
	/**
	 * 
	 * 根据需求返回连接，返回失败则强制关闭该连接 
	 *    
	 * @param jedis
	 * @param isBroken
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void returnResource(Jedis jedis, boolean isBroken)
	{
		if (jedis != null)
		{
			try
	    	{
		        if(isBroken)
		        {
		        	sentinelPool.returnBrokenResource(jedis);
		        }
		        else
		        {
		        	sentinelPool.returnResource(jedis);
		        }
	        }
	        catch (Exception e)
	    	{
	        	LOGGER.error("RedisSentinelPool, fail to return resource, forced close the jedis.", e);
	            destroyResource(jedis);
	        }
		}
    }
	
	@Override
	public void returnResource(Jedis jedis, Exception exception)
	{
		boolean isBroken = handleJedisException(exception);
		returnResource(jedis, isBroken);
	}
	
	/**
	 * 
	 * 强制销毁连接  
	 *    
	 * @param jedis
	 */
	@Override
	public void destroyResource(Jedis jedis) 
	{
		if ((jedis != null) && jedis.isConnected())
		{
			try 
			{
				try 
				{
					jedis.quit();
				} 
				catch (Exception e)
				{
					LOGGER.error("RedisSentinelPool, destroy jedis, fail to quit jedis", e);
				}
				jedis.disconnect();
			} 
			catch (Exception e)
			{
				LOGGER.error("RedisSentinelPool, destroy jedis, fail to disconnect jedis", e);
			}
		}
	}
}
