package com.xiangrikui.api.mc.mq.factory;

import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.xiangrikui.api.mc.mq.IMCConsumer;
import com.xiangrikui.api.mc.mq.activemq.JMSConsumer;

/**
 * 
 * 多消费者
 * 维护多个JMSConsumer实例
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年7月8日 下午5:57:01
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class PoolConsumer implements IMCConsumer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PoolConsumer.class);
	
	private JMSConsumer[] consumers;
	
	private String[] queueNames;
	
	private int queueSize;
	
	@SuppressWarnings("unused")
	private PoolConsumer() {}
	
	public PoolConsumer(String username, String password, String[] queueNames, String brokerUrl)
	{
		if (queueNames == null || queueNames.length == 0)
		{
			//参数异常
			LOGGER.error("fail to construct PoolConsumer, queueName array is null");
			throw new IllegalArgumentException("queueName array is null");
		}
		
		this.queueNames = queueNames;
		this.queueSize = queueNames.length;
		
		//新建消费者组
		consumers = new JMSConsumer[queueSize];
		for (int i=0; i<queueSize; i++)
		{
			if (Strings.isNullOrEmpty(queueNames[i]))
			{
				//参数异常
				LOGGER.error("fail to construct PoolConsumer, queueName is null");
				throw new IllegalArgumentException("queueName is null");
			}
			consumers[i] = new JMSConsumer(username, password, this.queueNames[i], brokerUrl);
		}
	}
	
	@Override
	public void setMessageListener(MessageListener messageListener)
	{
		if (messageListener == null)
		{
			throw new NullPointerException("messageListener is null");
		}
		
		if (consumers != null && consumers.length > 0)
		{
			for (JMSConsumer consumer : consumers)
			{
				consumer.setMessageListener(messageListener);
			}
		}
	}

	@Override
	public void start()
	{
		if (consumers != null && consumers.length > 0)
		{
			try
			{
				for (JMSConsumer consumer : consumers)
				{
					consumer.start();
				}
			}
			catch (Exception e)
			{
				LOGGER.error("fail to start", e);
			}
		}
	}

	@Override
	public void shutdown()
	{
		if (consumers != null && consumers.length > 0)
		{
			for (JMSConsumer consumer : consumers)
			{
				consumer.shutdown();
			}
		}
	}
}
