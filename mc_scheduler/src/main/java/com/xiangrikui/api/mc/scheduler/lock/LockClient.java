package com.xiangrikui.api.mc.scheduler.lock;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.scheduler.lock.component.ConcurrentMapLock;

/**
 * 
 * 同步锁客户端
 * 因暂时无需支持分布式，临时使用concurrentMap简单实现
 * 单例，需主动初始化
 * 实际锁操作类是ILock接口的实现类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月27日 下午3:23:07
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class LockClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LockClient.class);
	
	private static LockClient instance = null;
	
	//断定不为空
	private ILock lockComponent = null;
	
	private LockClient()
	{
		//本版本默认为ConcurrentMap锁
		lockComponent = new ConcurrentMapLock();
	}
	
	public static void init(String path)
	{
		LOGGER.info("init lockClient, config path: {}", path);
		instance = new LockClient();
		LOGGER.info("init lockClient end");
	}
	
	public static LockClient getInstance()
	{
		return Objects.requireNonNull(instance);
	}
	
	public boolean lock(String key)
	{
		return lockComponent.lock(key);
	}
	
	public boolean lock(String key, int waitingTime)
	{
		return lockComponent.lock(key, waitingTime);
	}
	
	public boolean lockWithoutBlocking(String key)
	{
		return lockComponent.lockWithoutBlocking(key);
	}
	
	public void unlock(String key)
	{
		lockComponent.unlock(key);
	}
}
