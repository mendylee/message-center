package com.xiangrikui.api.mc.common.retry;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.retry.RetryConfig;
import com.xrk.retry.RetryScheduler;
import com.xrk.retry.RetrySchedulerFactory;
import com.xrk.retry.RetryTask;
import com.xrk.retry.failstore.mapdb.MapdbFailStoreFactory;
import com.xrk.retry.strategy.LadderRetryStrategy;

/**
 * 
 * RetryClient: 消息中心重试客户端
 * 				1.使用阶梯重试策略
 * 			    2.使用mapdb策略持久化		
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年9月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class RetryClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RetryClient.class);
	//重试任务调度器
	private RetryScheduler retryScheduler;
	
	private volatile static RetryClient instance = null;
	private static final Object syncLock = new Object();
	
	public static RetryClient getInstance(){
		return Objects.requireNonNull(instance);
	}
	
	//将地址传入
	public static void init(String basePath)
	{
		if (instance == null)
		{
			synchronized (syncLock) 
			{
				if (instance == null)
				{
					instance = new RetryClient();
				}
			}
		}
	}
	
	private RetryClient(){
		RetryConfig config = new RetryConfig();
		RetryScheduler retryService  = RetrySchedulerFactory.builder()
					 .withRetryConfig(config)
					 .withRetryStrategy(new LadderRetryStrategy())
					 .withFailStore(new MapdbFailStoreFactory().getFailStore(config, config.getFailStorePath()))
					 .name("messageCenter")
					 .build();
		retryService.start();
		retryScheduler = retryService;
		//异常时清理资源
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run()
			{
				LOGGER.info("Exception quit,RetryScheduler stop");
				retryScheduler.stop();
			}
		}));
	}
	
	public boolean submitTask(RetryTask retryTask){
		boolean success = false;
		if(retryScheduler.isStarted()){
			success = retryScheduler.submitTask(retryTask);
		}
		return success;
	}
	
	
	
}


