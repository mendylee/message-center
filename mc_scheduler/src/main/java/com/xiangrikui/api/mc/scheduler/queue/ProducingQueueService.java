package com.xiangrikui.api.mc.scheduler.queue;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.util.FixedAndBlockedScheduledThreadPoolExecutor;
import com.xiangrikui.api.mc.scheduler.SchedulerConfig;
import com.xiangrikui.api.mc.scheduler.vo.MessageProducingVO;
import com.xiangrikui.api.mc.storage.queue.FQueue;

/**
 * 
 * 生产者重试队列，当生产者发送消息失败时，先入队，再定期从队列内拉取处理
 * 单例
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月18日 下午2:53:47
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ProducingQueueService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProducingQueueService.class);
	
	private volatile static ProducingQueueService instance = null;
	private static final Object syncObj = new Object();
	
	//定时任务处理服务
	private FixedAndBlockedScheduledThreadPoolExecutor scheduledExecutorService = null;
	
	//任务处理器
	private ProducingProcessor producingProcessor = null;
	
	//生产者队列，默认使用FQueue队列
	//维护唯一一个队列实例
	private Queue<MessageProducingVO> messageProducingQueue = null;
	
	//定时任务处理线程
	//默认5个线程
	public static final int DEFAULT_THREAD_COUNT = 5;
	private int threadCount =  DEFAULT_THREAD_COUNT;
	
	//定时任务处理周期，单位是ms
	//默认2000ms
	public static final int DEFAULT_SCHEDULED_PERIOD = 2000;
	private long scheduledPeriod = DEFAULT_SCHEDULED_PERIOD;
	
	private ProducingQueueService(String basePath)
	{
		LOGGER.info("init producing queue service start ...");
		
		//加载配置参数
		initConfig();
		
		scheduledExecutorService = new FixedAndBlockedScheduledThreadPoolExecutor(threadCount);
		
		//参数为测试参数
		//messageProducingQueue = new LinkedBlockingQueue<MessageProducingVO>();
		String queueFilePath = basePath + "/tmp/fqueue";
		try 
		{
			messageProducingQueue = new FQueue<MessageProducingVO>(queueFilePath, MessageProducingVO.class);
		}
		catch (Exception e)
		{
			LOGGER.error(String.format("fail to init fqueue, path: %s", queueFilePath), e);
			return;
		}
		
		//初始化生产任务处理器
		producingProcessor = new ProducingProcessor(messageProducingQueue);
		
		//启动定时器
		scheduledExecutorService.scheduleAtFixedRate(producingProcessor, 1, scheduledPeriod, TimeUnit.MILLISECONDS);
		
		LOGGER.info("init producing queue service end ...");
		LOGGER.info("producing queue start ...");
	}
	
	private void initConfig()
	{
		threadCount = SchedulerConfig.getProducingQueueThreadCount();
		scheduledPeriod = SchedulerConfig.getProducingQueueScheduledPeriod();
	}
	
	public static ProducingQueueService getInstance()
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
					instance = new ProducingQueueService(basePath);
				}
			}
		}
	}
	
	public boolean offer(MessageProducingVO messageProducingVO)
	{
		Objects.requireNonNull(messageProducingVO);
		
		//直接调用任务处理器的发送消息方法，尝试同步发送，失败后入队
		//return producingProcessor.trySendingMessage(messageProducingVO);
		
		//1.0.1 改
		//直接入队，发送消息直接使用异步方式
		return messageProducingQueue.offer(messageProducingVO);
	}
}