package com.xiangrikui.api.mc.executor.task;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.util.FixedAndBlockedScheduledThreadPoolExecutor;
import com.xiangrikui.api.mc.executor.ExecutorConfig;
import com.xiangrikui.api.mc.executor.vo.TaskVO;
import com.xiangrikui.api.mc.storage.queue.FQueue;

/**
 * 
 * 消息发送队列，先尝试直接发送，失败后加入重试队列
 * 任务处理器是MessageSendingProcessor
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月20日 下午5:53:38
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MessagePostingQueueService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MessagePostingQueueService.class);
	
	private volatile static MessagePostingQueueService instance = null;
	private static final Object syncObj = new Object();
	
	//定时任务处理服务
	private FixedAndBlockedScheduledThreadPoolExecutor scheduledExecutorService = null;
	
	//任务处理器
	private MessagePostingProcessor messagePostingProcessor = null;
	
	//消息发送队列，默认使用FQueue队列
	//维护唯一一个队列实例
	private Queue<TaskVO> messagePostingQueue = null;
	
	//定时任务处理线程
	//默认10个线程
	public static final int DEFAULT_THREAD_COUNT = 10;
	private int threadCount =  DEFAULT_THREAD_COUNT;
	
	//定时任务处理周期，单位是ms
	//默认100ms
	public static final int DEFAULT_SCHEDULED_PERIOD = 100;
	private long scheduledPeriod = DEFAULT_SCHEDULED_PERIOD;
	
	private MessagePostingQueueService(String basePath)
	{
		LOGGER.info("init post queue service start ...");
		
		//初始化配置参数
		initConfig();
		
		scheduledExecutorService = new FixedAndBlockedScheduledThreadPoolExecutor(threadCount);
		
		//参数为测试参数
		//messageSendingQueue = new LinkedBlockingQueue<TaskVO>();
		String queueFilePath = basePath + "/tmp/fqueue";
		try
		{
			messagePostingQueue = new FQueue<TaskVO>(queueFilePath, TaskVO.class);
		}
		catch (Exception e)
		{
			LOGGER.error(String.format("fail to init fqueue, path: %s", queueFilePath), e);
			return;
		}
		
		//初始化生产任务处理器
		messagePostingProcessor = new MessagePostingProcessor(messagePostingQueue);
		
		//启动定时器
		scheduledExecutorService.scheduleAtFixedRate(messagePostingProcessor, 1, scheduledPeriod, TimeUnit.MILLISECONDS);
		
		LOGGER.info("init post queue service end ...");
		LOGGER.info("post queue start ...");
	}
	
	private void initConfig()
	{
		threadCount = ExecutorConfig.getMessagePostingQueueThreadCount();
		scheduledPeriod = ExecutorConfig.getMessagePostingQueueScheduledPeriod();
	}
	
	public static MessagePostingQueueService getInstance()
	{
		return Objects.requireNonNull(instance);
	}
	
	//将地址传入
	public static void init(String basePath)
	{
		if (instance == null)
		{
			synchronized (syncObj) 
			{
				if (instance == null)
				{
					instance = new MessagePostingQueueService(basePath);
				}
			}
		}
	}
	
	public boolean offer(TaskVO taskVO)
	{
		Objects.requireNonNull(taskVO);
		return messagePostingProcessor.tryPostingMessage(taskVO);
	}
}