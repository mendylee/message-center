package com.xiangrikui.api.mc.executor.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.util.Strings;
import com.xiangrikui.api.mc.executor.vo.TaskVO;
import com.xiangrikui.api.mc.mq.MCMessage;
import com.xiangrikui.api.mc.mq.MCMessageHandler;
import com.xiangrikui.api.mc.mq.vo.ConsumerVO;
import com.xiangrikui.api.mc.mq.vo.MessageSendingVO;
import com.xiangrikui.api.mc.mq.vo.ProducerVO;

/**
 * 
 * 任务处理入口
 * 实现MCMessageHandler接口用以接受mq发送的消息，并将消息体解析成任务池可以处理的taskVO
 * 单例，后期优化初始化方式
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 下午5:15:52
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class TaskCenter implements MCMessageHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskCenter.class);
	private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");
	
	private volatile static TaskCenter instance = null;
	private static final Object syncObj = new Object();
	
	private TaskPool taskPool = null;
	
	private TaskCenter() 
	{
		taskPool = new TaskPool();
	}
	
	public static TaskCenter getInstance()
	{
		if (instance == null)
		{
			synchronized (syncObj) 
			{
				if (instance == null)
				{
					instance = new TaskCenter();
				}
			}
		}
		return instance;
	}
	
	@Override
	public void handle(MCMessage message)
	{
		if (message != null)
    	{
    		MessageSendingVO messageSendingVO = (MessageSendingVO) message.getContent();
    		addTasks(messageSendingVO);
    	}
    	else
    	{
    		LOGGER.error("fail to handle message, message is null");
    	}
	}
	
	/**
	 * 
	 * 将消息传送实体转化成任务VO列表
	 * 并送入任务池
	 *    
	 * @param messageSendingVO
	 */
	private void addTasks(MessageSendingVO messageSendingVO)
	{
		if (messageSendingVO == null)
		{
			LOGGER.error("messageSendingVO is null");
			return;
		}
		
		String topic = messageSendingVO.getTopic();
		if (Strings.isNullOrEmpty(topic))
		{
			LOGGER.error("messageSendingVO topic is null");
			return;
		}
		
		Map<String, ConsumerVO> consumers = messageSendingVO.getConsumers();
		if (consumers == null || consumers.size() == 0)
		{
			LOGGER.error("messageSendingVO consumers is null, topic: {}", messageSendingVO.getTopic());
			return;
		}
		
		ProducerVO producer = messageSendingVO.getProducer();
		if (producer == null)
		{
			LOGGER.error("messageSendingVO producer is null");
			return;
		}
		
		Set<Entry<String, ConsumerVO>> entries = consumers.entrySet();
		ConsumerVO consumerVO = null;
		TaskVO taskVO = null;
		for (Entry<String, ConsumerVO> entry : entries)
		{
			taskVO = new TaskVO();
			consumerVO = entry.getValue();
			taskVO.setTopic(topic);
			taskVO.setConsumerVO(consumerVO);
			taskVO.setCallbackUrl(consumerVO.getCallbackUrl());
			taskVO.setProducerVO(producer);
			taskVO.setMessage(messageSendingVO.getMessage());
			taskVO.setTraceLogContent(messageSendingVO.getTraceLogContent());
			//add by
			//将vo放入任务池
			if (!taskPool.addTask(taskVO))
			{
				//放入任务池失败
				//TODO
				//后续需要准备失败队列或者失败日志
				FAILED_LOGGER.error("fail to add task, task: {}", taskVO.toString());
				LOGGER.error("fail to add task, task: {}", taskVO.toString());
			}
		}
	}
}