package com.xiangrikui.api.mc.scheduler.component.implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.exception.BadRequestException;
import com.xiangrikui.api.mc.common.exception.BusinessException;
import com.xiangrikui.api.mc.common.exception.InternalServerException;
import com.xiangrikui.api.mc.scheduler.api.constants.BusinessCode;
import com.xiangrikui.api.mc.scheduler.component.IProducerComponent;
import com.xiangrikui.api.mc.scheduler.component.validator.ParameterValidator;
import com.xiangrikui.api.mc.scheduler.queue.ProducingQueueService;
import com.xiangrikui.api.mc.scheduler.vo.MessageProducingVO;

/**
 * 
 * Producer相关接口实现逻辑
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月12日 上午10:48:33
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ProducerComponent implements IProducerComponent
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerComponent.class);
	
	private ProducingQueueService producingQueueService = ProducingQueueService.getInstance();
	
	@Override
	public boolean produceMessage(String message, String producerName, String topic,String traceLogContent) throws BusinessException
	{
		LOGGER.info("ProducerComponent, produceMessage, message: {}, producerCode: {}, topic: {}", message, producerName, topic);
		
		//消息有效性验证
		if (!ParameterValidator.validateMessage(message))
		{
			LOGGER.debug("invalid message, message:{}, producerName:{}, topic:{}", message, producerName, topic);
			throw new BadRequestException(BusinessCode.PRODUCE_MESSAGE_INVALID_MESSAGE, BusinessCode.PRODUCE_MESSAGE_INVALID_MESSAGE_MSG);
		}
		
		//生产者有效性验证
		if (!ParameterValidator.validateProducerName(producerName))
		{
			LOGGER.debug("invalid producerName, message:{}, producerName:{}, topic:{}", message, producerName, topic);
			throw new BadRequestException(BusinessCode.PRODUCE_MESSAGE_INVALID_PRODUCER_NAME, BusinessCode.PRODUCE_MESSAGE_INVALID_PRODUCER_NAME_MSG);
		}
		
		//topic有效性验证
		if (!ParameterValidator.validateTopic(topic))
		{
			LOGGER.debug("invalid topic, message:{}, producerName:{}, topic:{}", message, producerName, topic);
			throw new BadRequestException(BusinessCode.PRODUCE_MESSAGE_INVALID_TOPIC, BusinessCode.PRODUCE_MESSAGE_INVALID_TOPIC_MSG);
		}
		
		MessageProducingVO messageProducingVO = new MessageProducingVO();
		messageProducingVO.setMessage(message);
		messageProducingVO.setProducerName(producerName);
		messageProducingVO.setTopic(topic);
		messageProducingVO.setTraceLogContent(traceLogContent);
		boolean ret = false;
		
		try
		{
			ret = producingQueueService.offer(messageProducingVO);
			if (!ret)
			{
				//入队失败
				LOGGER.error("fail to put vo in queue, vo: {}", messageProducingVO.toString());
			}
		}
		catch (Exception e)
		{
			//未知内部错误
			LOGGER.error(String.format("fail to put vo in queue, vo: %s, msg: %s", messageProducingVO.toString(), e.getMessage()), e);
		}
		
		if (!ret)
		{
			throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
		}
		
		LOGGER.info("ProducerComponent offer Successful");
		return ret;
	}
}
