package com.xiangrikui.api.mc.scheduler.lock;

/**
 * 
 * 同步锁操作接口
 * 锁住指定key
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月27日 下午4:01:58
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface ILock
{
	/**
	 * 最大阻塞时间，单位是毫秒
	 */
	public static final int DEFAULT_MAX_BLOCKING_TIME = 10000;
	
	/**
	 * 阻塞间隔，单位是毫秒
	 */
	public static final int DEFAULT_BLOCKING_INTERVAL = 1000;
	
	/**
	 * 
	 * 阻塞锁，最大阻塞时间为默认值
	 *    
	 * @param key
	 */
	boolean lock(String key);
	
	/**
	 * 
	 * 阻塞锁，最大阻塞时间为waitingTime
	 *    
	 * @param key
	 * @param waitingTime
	 */
	boolean lock(String key, int waitingTime);
	
	/**
	 * 
	 * 非阻塞锁，key被占用时直接返回false
	 *    
	 * @param key
	 */
	boolean lockWithoutBlocking(String key);
	
	/**
	 * 
	 * 解锁
	 *    
	 * @param key
	 */
	void unlock(String key);
}
