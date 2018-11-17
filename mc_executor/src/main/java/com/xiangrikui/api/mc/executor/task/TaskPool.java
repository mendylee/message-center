package com.xiangrikui.api.mc.executor.task;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.util.FixedAndBlockedThreadPoolExecutor;
import com.xiangrikui.api.mc.executor.vo.TaskVO;

/**
 * 
 * 任务池
 * 由任务中心管理唯一一个任务池实例
 * 任务池维护一个支持阻塞的固定大小的线程池，默认20个线程，可调整，但不可动态修改
 * 这里不做单例限制
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 下午5:24:04
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class TaskPool
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskPool.class);
	private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");
	
	//任务处理池最大线程数
	//默认50个
	public static final int DEFAULT_TASK_THREAD_COUNT = 50;
	private int maxTaskThreadCount = DEFAULT_TASK_THREAD_COUNT;
	
	private ExecutorService taskThreadPool;
	
	public TaskPool()
	{
		this(DEFAULT_TASK_THREAD_COUNT);
	}
	
	public TaskPool(int threadCount)
	{
		maxTaskThreadCount = threadCount;
		//初始化任务处理线程池
		taskThreadPool = new FixedAndBlockedThreadPoolExecutor(maxTaskThreadCount);
	}
	
	public boolean addTask(TaskVO taskVO)
	{
		try
		{
			taskThreadPool.execute(new TaskProcessor(taskVO));
			return true;
		}
		catch (Exception e)
		{
			//TODO
			LOGGER.error("fail to execute task", e);
			FAILED_LOGGER.error("fail to execute task, vo: {}", taskVO == null ? "null" : taskVO.toString());
			return false;
		}
	}
}