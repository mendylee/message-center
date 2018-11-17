package com.xiangrikui.api.mc.mq.factory;

import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.mq.IMCConsumer;
import com.xiangrikui.api.mc.mq.activemq.JMSConsumer;

/**
 * 
 * 单独的生产者
 * 维护一个JMSConsumer实例
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年7月8日 下午5:09:02
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SingleConsumer implements IMCConsumer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SingleConsumer.class);
	
	private JMSConsumer consumer;
	
	@SuppressWarnings("unused")
	private SingleConsumer() {}
	
	public SingleConsumer(String username, String password, String queue, String brokerUrl)
	{
		consumer = new JMSConsumer(username, password, queue, brokerUrl);
	}
	
	@Override
	public void setMessageListener(MessageListener messageListener)
	{
		consumer.setMessageListener(messageListener);
	}

	@Override
	public void start()
	{
		try
		{
			consumer.start();
		}
		catch (Exception e)
		{
			LOGGER.error("fail to start", e);
		}
	}

	@Override
	public void shutdown()
	{
		consumer.shutdown();
	}
}
