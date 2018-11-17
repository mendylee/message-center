package com.xiangrikui.api.mc.mq;

import java.util.Objects;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.mq.activemq.MessageHandler;
import com.xiangrikui.api.mc.mq.activemq.MultiThreadMessageListener;
import com.xiangrikui.api.mc.mq.factory.PoolConsumer;
import com.xiangrikui.api.mc.mq.factory.PoolProducer;
import com.xiangrikui.api.mc.mq.factory.SingleConsumer;
import com.xiangrikui.api.mc.mq.factory.SingleProducer;

/**
 * 
 * MQClient，获取内置MQ操作方法的唯一入口
 * 单例，需传入配置文件地址主动初始化
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月13日 下午4:09:07
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MCMQClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MCMQClient.class);
	
	private static MCMQClient instance = null;
	
	private IMCProducer producer = null;
	private IMCConsumer consumer = null;
	
	private MCMessageHandler handler = null;
	
	private MCMQClient()
	{
		LOGGER.info("init MCMQClient");
		
		String queueName = Objects.requireNonNull(MCMQConfig.getQueueName(), "value of queuename is null");
		int queueCount = MCMQConfig.getQueueCount();
		
		if (queueCount > 1)
		{
			String[] queueNames = getQueueNameArray(queueName, queueCount);
			
			LOGGER.info("init MCMQClient with multi queue, queuename: {}, count: {}", queueName, queueCount);
			
			producer = new PoolProducer(MCMQConfig.getBrokerUrl(), 
					MCMQConfig.getUsername(), 
					MCMQConfig.getPassword(), 
					queueNames);
			
			consumer = new PoolConsumer(MCMQConfig.getUsername(), 
					MCMQConfig.getPassword(), 
					queueNames, 
					MCMQConfig.getBrokerUrl());
		}
		else
		{
			LOGGER.info("init MCMQClient with single queue, queuename: {}", queueName);
			
			producer = new SingleProducer(MCMQConfig.getBrokerUrl(), 
					MCMQConfig.getUsername(), 
					MCMQConfig.getPassword(),
					queueName);
			
			consumer = new SingleConsumer(MCMQConfig.getUsername(), 
					MCMQConfig.getPassword(), 
					queueName, 
					MCMQConfig.getBrokerUrl());
		}
	}
	
	public static MCMQClient getInstance()
	{
		Objects.requireNonNull(instance);
		
		return instance;
	}
	
	public static void init(String path)
	{
		MCMQConfig.init(path);
		instance = new MCMQClient();
	}
	
	/**
	 * 
	 * 方法作用描述说明.  
	 *    
	 * @param mcMessage
	 * @return
	 */
	public boolean send(MCMessage mcMessage)
	{
		Objects.requireNonNull(mcMessage);

		producer.send(mcMessage);
		return true;
	}
	
	/**
	 * 
	 * 方法作用描述说明.  
	 *    
	 * @param handler
	 */
	public void setHandler(MCMessageHandler handler)
	{
		Objects.requireNonNull(handler);
		this.handler = handler;
		
		consumer.setMessageListener(new MultiThreadMessageListener(100, new MessageHandler()
        {
            public void handle(Message message)
            {
                try
                {
                    invokeListener(message);
                }
                catch (Exception e)
                {
                    LOGGER.error("fail to invoke handler", e);
                }
            }
        }));
        
        try
        {
			consumer.start();
		}
		catch (Exception e)
        {
			LOGGER.error("fail to start consumer", e);
		}
	}
	
	private void invokeListener(Message message) throws JMSException
	{
		if (this.handler != null)
		{
			ObjectMessage objectMessage = (ObjectMessage) message;
			MCMessage mcMessage = (MCMessage) objectMessage.getObject();
			handler.handle(mcMessage);
		}
		else
		{
			LOGGER.info("handler is null");
		}
	}
	
	private String[] getQueueNameArray(String queueName, int queueCount)
	{
		String[] queueNames = new String[queueCount];
		for (int i=0; i<queueCount; i++)
		{
			queueNames[i] = queueName + "_" + i;
		}
		return queueNames;
	}
}