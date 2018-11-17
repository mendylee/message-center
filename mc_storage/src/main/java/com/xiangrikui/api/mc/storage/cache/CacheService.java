package com.xiangrikui.api.mc.storage.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.util.ScanClass;

/**
 * 缓存服务类 
 * CacheService: CacheService.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class CacheService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
	
	private static Map<String, AbstractCache<?>> hsCache;
	private static boolean bInit = false;
	
	//此处不作多线程保护，调用端需要保证初始化只有一个线程
	public static void init(String basePath)
	{
		if(bInit)
		{
			return;
		}		

		CacheConfig.init(basePath);
		
		hsCache = new ConcurrentHashMap<String, AbstractCache<?>>();
		LOGGER.info("CacheServer init start");
		//加载已实现的缓存对象
		Set<Class<?>> set = ScanClass.getClasses(CacheConfig.getCacheComponentPackage());
		for (Class<?> classes : set) {
			//忽略基本类型
			if(classes.isInterface() 
					|| classes.isAnnotation() 
					|| classes.isAnonymousClass()
					|| classes.isEnum()
					|| classes.isPrimitive()){
				continue;
			}
			
			String superClassName = classes.getSuperclass().getName();
			if(superClassName.equals(AbstractCache.class.getName()))
			{
				try {
					AbstractCache<?> tmpObj =  (AbstractCache<?>)classes.newInstance();
	                hsCache.put(classes.getName(), tmpObj);
                }
				catch (InstantiationException e) {
					LOGGER.error(e.getMessage(), e);
				}
				catch (IllegalAccessException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		
		bInit = true;
		LOGGER.info("CacheServer init end");
	}
	
	/**
	 * 
	 * 获取指定缓存对象的实体  
	 *    
	 * @return
	 */
    public static AbstractCache<?> GetService(Class<?> t)
	{
		String key = t.getName();
		return hsCache.get(key);
	}
    
//    /**
//     * 
//     * debug模式下清空缓存 
//     *
//     */
//    public static void cleanAll()
//    {
//    	cleanAll(true);
//    }
//    
//    public static void cleanAll(boolean onlyIfDebug)
//    {
//    	if (onlyIfDebug && !SysConfigCache.getInstance().isDebugProject())
//    	{
//    		return;
//    	}
//    	
//    	for (AbstractCache<?> cache: hsCache.values())
//		{
//			cache.clear();
//		}
//    }
//    
//    public static void reset()
//    {
//    	if (SysConfigCache.getInstance().isDebugProject())
//    	{
//    		synchronized (syncObj)
//    		{
//    			//清空缓存
//        		for (AbstractCache<?> cache: hsCache.values())
//        		{
//        			cache.clear();
//        		}
//        		bInit = false;
//        		Init();
//			}
//    	}
//    }
}
