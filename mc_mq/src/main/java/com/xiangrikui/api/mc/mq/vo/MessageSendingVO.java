package com.xiangrikui.api.mc.mq.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * MessageSendingVO: MessageSendingVO.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午5:53:22
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MessageSendingVO implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String topic;
	private ProducerVO producer;
	private Map<String, ConsumerVO> consumers;
	private int retryingCount;
	private String message;
	//V1.1.0新增质量日志追踪内容
	private String traceLogContent;
	
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
	public int getRetryingCount()
	{
		return retryingCount;
	}
	public void setRetryingCount(int retryingCount)
	{
		this.retryingCount = retryingCount;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getTraceLogContent()
	{
		return traceLogContent;
	}
	public void setTraceLogContent(String traceLogContent)
	{
		this.traceLogContent = traceLogContent;
	}
	@Override
	public String toString()
	{
		return "MessageSendingVO [topic=" + topic + ", producer=" + producer + ", consumers="
		        + consumers + ", retryingCount=" + retryingCount + ", message=" + message + "]";
	}
}