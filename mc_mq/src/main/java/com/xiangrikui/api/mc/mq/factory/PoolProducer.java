package com.xiangrikui.api.mc.mq.factory;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.xiangrikui.api.mc.mq.IMCProducer;
import com.xiangrikui.api.mc.mq.activemq.JMSProducer;

/**
 * 
 * 多生产者
 * 维护多个JMSProducer实例
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年7月8日 下午5:28:50
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class PoolProducer implements IMCProducer
{
	private final static Logger LOGGER = LoggerFactory.getLogger(PoolProducer.class);
	
	private JMSProducer[] producers;
	
	private String[] queueNames;
	
	private int queueSize;
	private int currentIndex = 0;
	
	@SuppressWarnings("unused")
	private PoolProducer() {}
	
	public PoolProducer(String brokerUrl, String userName, String password, String[] queueNames)
	{
		if (queueNames == null || queueNames.length == 0)
		{
			//参数异常
			LOGGER.error("fail to construct PoolProducer, queueName array is null");
			throw new IllegalArgumentException("queueName array is null");
		}
		this.queueNames = queueNames;
		this.queueSize = queueNames.length;
		
		//新建生产者数组
		producers = new JMSProducer[queueSize];
		for (int i=0; i<queueSize; i++)
		{
			if (Strings.isNullOrEmpty(queueNames[i]))
			{
				//参数异常
				LOGGER.error("fail to construct PoolProducer, queueName is null");
				throw new IllegalArgumentException("queueName is null");
			}
			producers[i] = new JMSProducer(brokerUrl, userName, password);
		}
	}
	
	@Override
	public void send(Map<String, Object> messageMap)
	{
		int index = selectIndex();
		producers[index].send(queueNames[index], messageMap);
	}

	@Override
	public void send(Serializable messageObj)
	{
		int index = selectIndex();
		producers[index].send(queueNames[index], messageObj);
	}

	/**
	 * 
	 * 选择一个范围内的位置
	 * 不要求绝对的平均分布，不做去重处理，不做分布式同步处理
	 *    
	 * @return
	 */
	private int selectIndex()
	{
		//先取一个副本
		//直接操作this.currentIndex有范围溢出风险
		int currentIndex = this.currentIndex;
		
		if (currentIndex >= queueSize)
		{
			currentIndex = 0;
			this.currentIndex = 0;
		}
		this.currentIndex ++;
		return currentIndex;
	}
}
