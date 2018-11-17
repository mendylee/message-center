package com.xiangrikui.api.mc.storage.cache.external.redis;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.util.Codec;
import com.xiangrikui.api.mc.storage.cache.ICache;
import com.xiangrikui.api.mc.storage.cache.external.KryoSerializer;

import redis.clients.jedis.JedisCommands;

/**
 * Redis缓存客户端实现,因只能存储字符类型数据，因此需要有一个编码转换过程，目前是将其转换为Base64格式
 * RedisCache: RedisCache.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class RedisCache<T> implements ICache<T>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);
	
	private Class<T> classType = null;
	private JedisCommands redisClient = null;
	private String cacheName = "";
	
	public RedisCache()
	{
		init();
	}
	
	private void init()
	{
		redisClient = new RedisPoolResource(RedisManager.getInstance().getPool());
	}

	@Override
    public void setClassType(Class<T> type, String cacheName)
    {
	    classType = type;
	    this.cacheName = cacheName;
    }

	private Class<T> getClassType()
	{
		return classType;
	}
	
	@Override
    public T get(String key)
    {
		String val = redisClient.hget(cacheName, key);
		if(val == null)
		{
			return null;
		}
		
		try 
		{
			byte[] bt = Codec.decodeBASE64(val);
			return KryoSerializer.Deserializer(getClassType(), bt);
		}
		catch (UnsupportedEncodingException e) 
		{
			LOGGER.error(String.format("RedisCache, fail to decode value, key: %s, value: %s", key, val), e);
			return null;
		}
    }

	@Override
    public boolean put(String key, T value)
    {
	    return put(key, value, 0);
    }
	
	@Override
    public boolean put(String key, T value, int expireTime)
    {
		byte[] bt = KryoSerializer.Serializer(value);
	    String val = Codec.encodeBASE64(bt);
	    redisClient.hset(cacheName, key, val);
	    if(expireTime > 0)
	    {
	    	//过期
	    	redisClient.expire(key, expireTime);
	    }
	    return true;
    }

	@Override
    public boolean remove(String key)
    {
	    Long count = redisClient.hdel(cacheName, key);
	    return count > 0;
    }

	@Override
    public boolean clear()
    {
		redisClient.del(cacheName);
	    return true;
    }

	@Override
    public boolean contain(String key)
    {
		return redisClient.hexists(cacheName, key);
    }

	@Override
    public long size()
    {
	    return redisClient.hlen(cacheName);
    }

	@Override
	public Map<String, T> getAll()
	{
		Map<String, T> retMap = new HashMap<String, T>();
		Map<String, String> srcMap = redisClient.hgetAll(cacheName);
		if (srcMap == null)
		{
			return null;
		}
		
		Set<Map.Entry<String, String>> entrys = srcMap.entrySet();
		for (Map.Entry<String, String> entry : entrys)
		{
			//逐个解码
			byte[] bt;
			try 
			{
				bt = Codec.decodeBASE64(entry.getValue());
			}
			catch (UnsupportedEncodingException e) 
			{
				LOGGER.error(String.format("RedisCache, getAll(), fail to decode base64, key: %s, value: %s", entry.getKey(), entry.getValue()), e);
				continue;
				
			}
			retMap.put(entry.getKey(), KryoSerializer.Deserializer(getClassType(), bt));
		}
		return retMap;
	}
}
