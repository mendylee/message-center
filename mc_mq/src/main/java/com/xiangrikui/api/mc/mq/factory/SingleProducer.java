package com.xiangrikui.api.mc.mq.factory;

import java.io.Serializable;
import java.util.Map;

import com.xiangrikui.api.mc.mq.IMCProducer;
import com.xiangrikui.api.mc.mq.activemq.JMSProducer;

/**
 * 
 * 单独的生产者
 * 维护一个单独的producer实例
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年7月8日 下午4:59:42
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SingleProducer implements IMCProducer
{
	private JMSProducer producer;
	
	private String queue;
	
	@SuppressWarnings("unused")
	private SingleProducer() {}
	
	public SingleProducer(String brokerUrl, String userName, String password, String queue)
	{
		producer = new JMSProducer(brokerUrl, userName, password);
		this.queue = queue;
	}

	@Override
	public void send(Map<String, Object> messageMap)
	{
		producer.send(queue, messageMap);
	}

	@Override
	public void send(Serializable messageObj)
	{
		producer.send(queue, messageObj);
	}
}
