package com.xiangrikui.api.mc.scheduler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Set;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.annotation.HttpRouterInfo;
import com.xiangrikui.api.mc.common.util.ClassHelper;
import com.xiangrikui.api.mc.mq.MCMQClient;
import com.xiangrikui.api.mc.scheduler.api.handler.AbstractHttpWorkerHandler;
import com.xiangrikui.api.mc.scheduler.lock.LockClient;
import com.xiangrikui.api.mc.scheduler.queue.ProducingQueueService;
import com.xiangrikui.api.mc.storage.cache.CacheService;
import com.xrk.hws.http.HttpServer;
import com.xrk.hws.http.monitor.MonitorClient;

/**
 * 
 * scheduler主启动类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月10日 下午4:11:38
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SchedulerApplication 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerApplication.class);
	
    public static void main( String[] args )
    {
    	String appBasePath = "";
		try 
		{
			CodeSource codeSource = SchedulerApplication.class.getProtectionDomain().getCodeSource();
	        appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
    		File jarFile = new File(appBasePath);
        	appBasePath = jarFile.getParentFile().getPath();
        }
        catch (URISyntaxException | UnsupportedEncodingException e)
		{
        	//LOG服务还未初始化
            e.printStackTrace();
        }
		String configPath = String.format("%s/config/", appBasePath);
		System.out.println("configPath=" + configPath);
		String log4jPath = String.format("%slog4j.xml", configPath);
		DOMConfigurator.configureAndWatch(log4jPath, 60000);
		
		//加载系统配置项
		SchedulerConfig.init(configPath);
		
		//应用程序初始化操作，如：配置加载、环境初始化等 
		CacheService.init(configPath);
		
		//初始化MQ服务
		MCMQClient.init(configPath);
		
		//初始化队列服务
		//传入应用部署的根目录
		ProducingQueueService.init(appBasePath);
		
		//初始化锁服务
		LockClient.init(null);
		
		//加载质量日志监控组件
		try
		{
			MonitorClient.init(configPath);
		}
		catch (Exception e)
		{
			LOGGER.error("fail to init MonitorClient", e);
		}

		//readTimeout，writeTimeout
		HttpServer server = new HttpServer(SchedulerConfig.getChannelReadTimeout(), SchedulerConfig.getChannelWriteTimeout());
		// 线程数默认为处理器数目
		int processNum = Runtime.getRuntime().availableProcessors();
		server.init(processNum, processNum * 2, processNum * 2, null);
		
		server.addListen(new InetSocketAddress(SchedulerConfig.getDefaultPort()));

		// 自动加载指定包下的所有处理器
		Set<Class<?>> set = ClassHelper.getClasses(SchedulerConfig.getHandlerPackage());
		for (Class<?> classes : set) 
		{
			if (classes.isAnnotationPresent(HttpRouterInfo.class)) 
			{
				try
				{
					AbstractHttpWorkerHandler handler = (AbstractHttpWorkerHandler) classes
					        .newInstance();
					handler.register(server);
				}
				catch (InstantiationException e) 
				{
					LOGGER.error(e.getMessage(), e);
				}
				catch (IllegalAccessException e)
				{
					LOGGER.error(e.getMessage(), e);
				}
			} 
		}
		
		server.run();
    }
}
