package com.xiangrikui.api.mc.scheduler.vo;

import java.util.Map;

import com.xiangrikui.api.mc.mq.vo.ConsumerVO;
import com.xiangrikui.api.mc.mq.vo.ProducerVO;

/**
 * 
 * SubscriberTableVO: SubscriberTableVO.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午6:11:19
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SubscriberTableVO
{
	private String topic;
	private ProducerVO producer;
	private Map<String, ConsumerVO> consumers;
	
	public String getTopic()
	{
		return topic;
	}
	public void setTopic(String topic)
	{
		this.topic = topic;
	}
	public ProducerVO getProducer()
	{
		return producer;
	}
	public void setProducer(ProducerVO producer)
	{
		this.producer = producer;
	}
	public Map<String, ConsumerVO> getConsumers()
	{
		return consumers;
	}
	public void setConsumers(Map<String, ConsumerVO> consumers)
	{
		this.consumers = consumers;
	}
	
	@Override
	public String toString()
	{
		return "SubscriberTableVO [topic=" + topic + ", producer=" + producer + ", consumers="
		        + consumers + "]";
	}
}
