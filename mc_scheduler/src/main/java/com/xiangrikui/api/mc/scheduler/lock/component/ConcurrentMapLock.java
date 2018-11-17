package com.xiangrikui.api.mc.scheduler.lock.component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.scheduler.lock.ILock;

/**
 * 
 * 基于concurrentHashMap实现的简单同步锁
 * 不适用于分布式结构
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月27日 下午4:17:40
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ConcurrentMapLock implements ILock
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentMapLock.class);
	
	private static final Integer DEFAULT_LOCK_VALUE = 1;
	
	private ConcurrentMap<String, Object> lockMap = null;
	
	public ConcurrentMapLock()
	{
		lockMap = new ConcurrentHashMap<String, Object>();
	}
	
	@Override
	public boolean lock(String key)
	{
		return lock(key, DEFAULT_MAX_BLOCKING_TIME);
	}

	@Override
	public boolean lock(String key, int waitingTime)
	{
		if (waitingTime < 0)
		{
			throw new IllegalArgumentException("waitingTime is negative!");
		}
		
		validateKey(key);
		
		//假如有重试，则重试次数至少1次
		int retryingCount = waitingTime / DEFAULT_BLOCKING_INTERVAL + 1;
		
		while (true)
		{
			if (lockMap.putIfAbsent(key, DEFAULT_LOCK_VALUE) == null)
			{
				//锁定成功
				//返回 true
				return true;
			}
			retryingCount--;
			
			if (retryingCount <= 0)
			{
				return false;
			}
			else
			{
				try
				{
					Thread.sleep(DEFAULT_BLOCKING_INTERVAL);
				}
				catch (InterruptedException e)
				{
					LOGGER.error(String.format("fail to sleep, key: %s", key), e);
					return false;
				}
			}
		}
	}

	@Override
	public boolean lockWithoutBlocking(String key)
	{
		validateKey(key);
		if (lockMap.putIfAbsent(key, DEFAULT_LOCK_VALUE) == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void unlock(String key)
	{
		validateKey(key);
		if (!lockMap.remove(key, DEFAULT_LOCK_VALUE))
		{
			//解锁报错，但不影响业务
			LOGGER.warn("fail to unlock, key: {}", key);
		}
	}
	
	private void validateKey(String key)
	{
		if (key == null || key.isEmpty())
		{
			throw new IllegalArgumentException("key is null string!");
		}
	}
}