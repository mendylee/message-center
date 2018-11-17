package com.xiangrikui.api.mc.storage.cache;

import com.xiangrikui.api.mc.common.util.SortEntity;

public class ExpireObject<T> extends SortEntity<T>
{
	private long expireTime;
	private T key;
	public ExpireObject(java.util.Date date, T key)
	{
		super(date.getTime(), key);		
		setExpireTime(date.getTime());
		setKey(key);
	}
	
	public long getExpireTime()
    {
	    return expireTime;
    }
	public void setExpireTime(long expireTime)
    {
	    this.expireTime = expireTime;
    }

	public T getKey()
    {
	    return key;
    }

	public void setKey(T key)
    {
	    this.key = key;
    }
}
