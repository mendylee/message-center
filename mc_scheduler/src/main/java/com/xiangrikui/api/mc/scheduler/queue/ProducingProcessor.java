package com.xiangrikui.api.mc.scheduler.queue;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.mq.MCMessage;
import com.xiangrikui.api.mc.mq.MCMQClient;
import com.xiangrikui.api.mc.mq.vo.ConsumerVO;
import com.xiangrikui.api.mc.mq.vo.MessageSendingVO;
import com.xiangrikui.api.mc.mq.vo.ProducerVO;
import com.xiangrikui.api.mc.scheduler.cache.SubscriberTableCache;
import com.xiangrikui.api.mc.scheduler.vo.MessageProducingVO;
import com.xiangrikui.api.mc.scheduler.vo.SubscriberTableVO;
import com.xiangrikui.api.mc.storage.cache.CacheService;

/**
 * 
 * 生产者生产消息的处理器
 * 实现了Runnable接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月18日 下午3:56:47
 * <br> JDK版本：1.8
 * <br>=================
 * =========
 */
public class ProducingProcessor implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProducingProcessor.class);
	private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("schedulerFailedRecordLogger");
	private static final Logger SUCCESSFUL_LOGGER = LoggerFactory.getLogger("schedulerSuccessfulRecordLogger");
	
	//断定不为空
	private Queue<MessageProducingVO> messageProducingQueue;
	
	private SubscriberTableCache subscriberTableCache = (SubscriberTableCache) CacheService.GetService(SubscriberTableCache.class);
	
	//最大重试次数
	//默认5次
	public static final int DEFAULT_MAX_RETRYING_COUNT = 5;
	private int maxRetryingCount = DEFAULT_MAX_RETRYING_COUNT;
	
	@SuppressWarnings("unused")
	private ProducingProcessor() {}
	
	public ProducingProcessor(Queue<MessageProducingVO> messageProducingQueue)
	{
		this.messageProducingQueue = Objects.requireNonNull(messageProducingQueue);
	}
	
	@Override
	public void run()
	{
		MessageProducingVO messageProducingVO = messageProducingQueue.poll();
		
		//队列为空，返回
		if (messageProducingVO == null)
		{
			return;
		}
		
		LOGGER.debug("get a messageProducingVO from queue, vo: {}", messageProducingVO.toString());
		
		trySendingMessage(messageProducingVO);
	}
	
	/**
	 * 
	 * 尝试将消息体发送至MQ
	 * 首先尝试同步发送，失败后重新入队
	 *    
	 * @param messageProducingVO
	 * @return
	 */
	public boolean trySendingMessage(MessageProducingVO messageProducingVO)
	{
		boolean ret = false;
		
		try
		{
			ret = sendMessage(messageProducingVO);
		}
		catch (Exception e)
		{
			LOGGER.error(String.format("fail to sendMessage, vo: %s", messageProducingVO.toString()), e);
		}
		
		//假如发送消息失败，重新入队
		if (!ret)
		{
			if (messageProducingVO.getRetryingCount() >= maxRetryingCount)
			{
				//记录错误日志
				//FAILED_LOGGER.info("retryingCount is more than {}, vo: {}", maxRetryingCount, messageProducingVO.toString());
				//1.0.2 修改错误日志记录格式
				FAILED_LOGGER.info("retryingCount is more than {}, producer: {}, topic: {}, message: {}", 
						maxRetryingCount, 
						messageProducingVO.getProducerName(), 
						messageProducingVO.getTopic(), 
						messageProducingVO.getMessage());
				LOGGER.error("fail to send message, retryingCount is more than {}", maxRetryingCount);
				return false;
			}
			else
			{
				messageProducingVO.incRetryingCount();
				if (!messageProducingQueue.offer(messageProducingVO))
				{
					//记录错误日志
					//FAILED_LOGGER.info("fail to put vo in queue, vo: {}", messageProducingVO.toString());
					//1.0.2 修改错误日志记录格式
					FAILED_LOGGER.info("fail to put vo in queue, producer: {}, topic: {}, message: {}", 
							messageProducingVO.getProducerName(), 
							messageProducingVO.getTopic(), 
							messageProducingVO.getMessage());
					LOGGER.error("fail to put vo in queue, vo: {}", messageProducingVO.toString());
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean sendMessage(MessageProducingVO messageProducingVO)
	{
		boolean ret = false;
		
		String topic = messageProducingVO.getTopic();
		String producerName = messageProducingVO.getProducerName();
		String message = messageProducingVO.getMessage();
		String traceLogContent = messageProducingVO.getTraceLogContent();
		
		SubscriberTableVO subscriberTableVO = subscriberTableCache.get(topic);
		
		if (subscriberTableVO == null)
		{
			LOGGER.warn("this topic is null, vo: {}", messageProducingVO.toString());
			ret = true;
		}
		else
		{
			Map<String, ConsumerVO> consumers = subscriberTableVO.getConsumers();
			ProducerVO producer = subscriberTableVO.getProducer();
			
			if (consumers == null || consumers.size() == 0)
			{
				LOGGER.warn("this topic has no consumer, topic: {}", topic);
				ret = true;
			}
			else
			{
				if (producer == null)
				{
					LOGGER.error("producer is null, vo: {}", messageProducingVO.toString());
					ret = false;
				}
				else
				{
					if (!producerName.equals(producer.getName()))
					{
						//暂时不对此种现象做限制
						//即不对生产者的身份做限制
						LOGGER.warn("this topic's producer_name is not equal with the parameter, topic' producer_name: {}, parameter: {}", producer.getName(), producerName);
						
						//新建生产者
						producer = new ProducerVO();
						producer.setName(producerName);
					}
					
					MessageSendingVO messageSendingVO = new MessageSendingVO();
					messageSendingVO.setConsumers(consumers);
					messageSendingVO.setProducer(producer);
					messageSendingVO.setTopic(topic);
					messageSendingVO.setMessage(message);
					//V1.1.0新增traceLogContent
					messageSendingVO.setTraceLogContent(traceLogContent);
					//初始化重试次数为0，处理失败时增加
					messageSendingVO.setRetryingCount(0);
					
					MCMessage mcMessage = new MCMessage();
					mcMessage.setTitle(topic);
					mcMessage.setContent(messageSendingVO);
					
					try
					{
						ret = MCMQClient.getInstance().send(mcMessage);
						//发送成功，记录成功日志
						//用专门的logger记录成专门的日志文件
						SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, message: {}", 
								producer.getName(), 
								topic, 
								message);
					}
					catch (Exception e)
					{
						LOGGER.error(String.format("send message fail, vo: %s", messageProducingVO.toString()), e);
					}
				}
			}
		}

		return ret;
	}
	
	public void setMaxRetryingCount(int maxRetryingCount)
	{
		this.maxRetryingCount = maxRetryingCount;
	}
}