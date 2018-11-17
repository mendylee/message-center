package com.xiangrikui.api.mc.scheduler.api.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.annotation.HttpMethod;
import com.xiangrikui.api.mc.common.annotation.HttpMethod.METHOD;
import com.xiangrikui.api.mc.common.annotation.HttpMethod.STATUS_CODE;
import com.xiangrikui.api.mc.common.annotation.HttpRouterInfo;
import com.xiangrikui.api.mc.common.exception.BusinessException;
import com.xiangrikui.api.mc.common.exception.InternalServerException;
import com.xiangrikui.api.mc.mq.vo.ConsumerVO;
import com.xiangrikui.api.mc.scheduler.api.response.SimpleResponse;
import com.xiangrikui.api.mc.scheduler.api.response.UnsubscribeResponse;
import com.xiangrikui.api.mc.scheduler.component.IConsumerComponent;
import com.xiangrikui.api.mc.scheduler.component.implement.ConsumerComponent;

/**
 * 
 * 消费者操作相关handler
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 上午11:20:42
 * <br> JDK版本：1.8
 * <br>==========================
 */
@HttpRouterInfo(router = "api/v1/consumer")
public class ConsumerHandler extends AbstractHttpWorkerHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHandler.class);
	
	private IConsumerComponent consumerComponent = new ConsumerComponent();
	
	@HttpMethod(uri="", method=METHOD.POST, code=STATUS_CODE.OK)
	public SimpleResponse subscribe(String topic, String consumer_name, String callback_url) throws BusinessException
	{
		boolean result = consumerComponent.subscribe(topic, consumer_name, callback_url);
		return buildSimpleResponse(result);
	}
	
	@HttpMethod(uri="", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public UnsubscribeResponse unsubsribe(String topic, String consumer_name) throws BusinessException
	{
		ConsumerVO consumerVO = consumerComponent.unsubscribe(topic, consumer_name);
		if (consumerVO == null)
		{
			LOGGER.error("consumerVO is null, topic: {}, consumer_name: {}", topic, consumer_name);
			throw new InternalServerException("10000000", "consumerVO is null");
		}
		return buildUnsubsribeResponse(consumerVO, topic);
	}
	
	private SimpleResponse buildSimpleResponse(boolean result)
	{
		SimpleResponse simpleResponse = new SimpleResponse();
		simpleResponse.setResult(result);
		return simpleResponse;
	}
	
	private UnsubscribeResponse buildUnsubsribeResponse(ConsumerVO consumerVO, String topic)
	{
		UnsubscribeResponse unsubscribeResponse = new UnsubscribeResponse();
		unsubscribeResponse.setCallback_url(consumerVO.getCallbackUrl());
		unsubscribeResponse.setConsumer_name(consumerVO.getName());
		unsubscribeResponse.setTopic(topic);
		return unsubscribeResponse;
	}
}