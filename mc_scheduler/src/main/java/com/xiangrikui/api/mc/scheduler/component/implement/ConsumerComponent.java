package com.xiangrikui.api.mc.scheduler.component.implement;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.exception.BadRequestException;
import com.xiangrikui.api.mc.common.exception.BusinessException;
import com.xiangrikui.api.mc.common.exception.InternalServerException;
import com.xiangrikui.api.mc.mq.vo.ConsumerVO;
import com.xiangrikui.api.mc.mq.vo.ProducerVO;
import com.xiangrikui.api.mc.scheduler.api.constants.BusinessCode;
import com.xiangrikui.api.mc.scheduler.cache.SubscriberTableCache;
import com.xiangrikui.api.mc.scheduler.component.IConsumerComponent;
import com.xiangrikui.api.mc.scheduler.component.validator.ParameterValidator;
import com.xiangrikui.api.mc.scheduler.lock.LockClient;
import com.xiangrikui.api.mc.scheduler.vo.SubscriberTableVO;
import com.xiangrikui.api.mc.storage.cache.CacheService;

/**
 * 
 * 消费者操作接口实现
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 上午10:41:01
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ConsumerComponent implements IConsumerComponent
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerComponent.class);
	
	private SubscriberTableCache subscriberTableCache = (SubscriberTableCache) CacheService.GetService(SubscriberTableCache.class);
	private LockClient lockClient = LockClient.getInstance();
	
	@Override
	public boolean subscribe(String topic, String consumerName, String callbackUrl) throws BusinessException
	{
		LOGGER.info("ConsumerComponent, subscribe, topic: {}, consumerName: {}, callbackUrl: {}", topic, consumerName, callbackUrl);
		
		//topic有效性验证
		if (!ParameterValidator.validateTopic(topic))
		{
			LOGGER.debug("invalid topic, callbackUrl:{}, consumerName:{}, topic:{}", callbackUrl, consumerName, topic);
			throw new BadRequestException(BusinessCode.SUBSCRIBE_TOPIC_INVALID_TOPIC, BusinessCode.SUBSCRIBE_TOPIC_INVALID_TOPIC_MSG);
		}
		
		//consumerName有效性验证
		if (!ParameterValidator.validateConsumerName(consumerName))
		{
			LOGGER.debug("invalid topic, callbackUrl:{}, consumerName:{}, topic:{}", callbackUrl, consumerName, topic);
			throw new BadRequestException(BusinessCode.SUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME, BusinessCode.SUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME_MSG);
		}
				
		//callbackUrl有效性验证
		if (!ParameterValidator.validateCallbackUrl(callbackUrl))
		{
			LOGGER.debug("invalid topic, callbackUrl:{}, consumerName:{}, topic:{}", callbackUrl, consumerName, topic);
			throw new BadRequestException(BusinessCode.SUBSCRIBE_TOPIC_INVALID_CALLBACK_URL, BusinessCode.SUBSCRIBE_TOPIC_INVALID_CALLBACK_URL_MSG);
		}
		
		String lockKey = topic + "_" + consumerName;
		
		try
		{
			if (lockClient.lock(lockKey))
			{
				return processSubscribing(topic, consumerName, callbackUrl);
			}
			else
			{
				LOGGER.error("fail to lock entity, topic: {}, consumerName: {}, callbackUrl: {}", topic, consumerName, callbackUrl);
				throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
			}
		}
		finally 
		{
			lockClient.unlock(lockKey);
		}
	}
	
	private boolean processSubscribing(String topic, String consumerName, String callbackUrl) throws BusinessException
	{
		SubscriberTableVO subscriberTableVO = subscriberTableCache.get(topic);
		
		if (subscriberTableVO == null)
		{
			LOGGER.debug("topic is null, topic: {}", topic);
			
			subscriberTableVO = new SubscriberTableVO();
			subscriberTableVO.setTopic(topic);
			
			//生成一个默认生产者，后续改掉这块逻辑
			//理论上应该先有主题和生产者，再有消费者
			ProducerVO producerVO = new ProducerVO();
			producerVO.setName("mc_default_producer");
			subscriberTableVO.setProducer(producerVO);
			
			//初始化消费者队列
			subscriberTableVO.setConsumers(new HashMap<String, ConsumerVO>());
		}
		
		//记录consumer
		Map<String, ConsumerVO> consumerMap = subscriberTableVO.getConsumers();
		if (!consumerMap.containsKey(consumerName))
		{
			//不存在，则新建
			LOGGER.info("add consumer, topic: {}, consumer: {}", topic, consumerName);
			ConsumerVO consumerVO = new ConsumerVO();
			consumerVO.setName(consumerName);
			consumerVO.setCallbackUrl(callbackUrl);
			consumerVO.setDescription(String.format("%s##%s##%s", topic, consumerName, callbackUrl));
			consumerMap.put(consumerName, consumerVO);
		}
		else
		{
			//存在，则更新
			//理论上不应该这么容易更新
			//后续应考虑加上权限控制
			ConsumerVO consumerVO = consumerMap.get(consumerName);
			consumerVO.setCallbackUrl(callbackUrl);
			consumerMap.put(consumerName, consumerVO);
		}
		
		//存入缓存
		if (subscriberTableCache.put(topic, subscriberTableVO))
		{
			LOGGER.info("add consumer succesfully!!");
		}
		else
		{
			LOGGER.error("fail to add consumer");
			throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
		}
		
		return true;
	}

	@Override
	public ConsumerVO unsubscribe(String topic, String consumerName) throws BusinessException
	{
		LOGGER.info("subscribe, topic: {}, consumerName: {}", topic, consumerName);
		
		//topic有效性验证
		if (!ParameterValidator.validateTopic(topic))
		{
			LOGGER.debug("invalid topic, consumerName:{}, topic:{}", consumerName, topic);
			throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_TOPIC, BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_TOPIC_MSG);
		}
		
		//consumerName有效性验证
		if (!ParameterValidator.validateConsumerName(consumerName))
		{
			LOGGER.debug("invalid topic, consumerName:{}, topic:{}", consumerName, topic);
			throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME, BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME_MSG);
		}	
		
		String lockKey = topic + "_" + consumerName;
		
		try
		{
			if (lockClient.lock(lockKey))
			{
				return processingUnsubscribing(topic, consumerName);
			}
			else
			{
				LOGGER.error("unsubscribe, fail to lock entity, topic: {}, consumerName: {}", topic, consumerName);
				throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
			}
		}
		finally
		{
			lockClient.unlock(lockKey);
		}
	}
	
	private ConsumerVO processingUnsubscribing(String topic, String consumerName) throws BusinessException
	{
		SubscriberTableVO subscriberTableVO = subscriberTableCache.get(topic);
		
		if (subscriberTableVO == null)
		{
			LOGGER.debug("topic is null, topic: {}", topic);
			throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC_MSG);
		}
		
//		if (!topic.equals(subscriberTableVO.getTopic()))
//		{
//			LOGGER.error("cache key is wrong!, key: {}, topic: {}", topic, subscriberTableVO.getTopic());
//			return null;
//		}
		
		Map<String, ConsumerVO> consumerMap = subscriberTableVO.getConsumers();
		if (consumerMap == null)
		{
			LOGGER.error("cache error! consumerMap is null, topic: {}", topic);
			throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER_MSG);
		}
		
		if (consumerMap.containsKey(consumerName))
		{
			ConsumerVO consumerVO = consumerMap.remove(consumerName);
			if (consumerVO != null)
			{
				//更新缓存
				if (subscriberTableCache.put(topic, subscriberTableVO))
				{
					LOGGER.info("unsubscribe successfully!!, consumer: %s", consumerVO.toString());
					
					//返回实体
					return consumerVO;
				}
				else
				{
					LOGGER.error("fail to update cache, topic: {}", topic);
					throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
				}
			}
			else
			{
				LOGGER.error("error occur! consumerVO is null, topic: {}", topic);
				throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
			}
		}
		else
		{
			LOGGER.debug("no such consumer, topic: {}, consumer: {}", topic, consumerName);
			throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER_MSG);
		}
	}
}
