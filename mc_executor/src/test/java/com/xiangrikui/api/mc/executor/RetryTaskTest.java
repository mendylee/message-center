package com.xiangrikui.api.mc.executor;

import com.xiangrikui.api.mc.executor.vo.TaskVO;
import com.xrk.retry.RetryConfig;
import com.xrk.retry.RetryScheduler;
import com.xrk.retry.RetrySchedulerFactory;
import com.xrk.retry.failstore.mapdb.MapdbFailStoreFactory;
import com.xrk.retry.strategy.LadderRetryStrategy;

public class RetryTaskTest
{
	public static void main(String[] args)
    {
		TaskVO taskVO = new TaskVO();
		taskVO.setMessage("123");
		RetryConfig config = new RetryConfig();
		RetryScheduler retryService  = RetrySchedulerFactory.builder()
					 .withRetryConfig(config)
					 .withRetryStrategy(new LadderRetryStrategy("5s 6s"))
					 .withFailStore(new MapdbFailStoreFactory().getFailStore(config, config.getFailStorePath()))
					 .name("messageCenter")
					 .build();
		retryService.start();
		retryService.submitTask(taskVO);
    }
}
