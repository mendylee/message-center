package com.xiangrikui.api.mc.scheduler.vo;

/**
 * 
 * 消息生产VO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月18日 下午2:59:43
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MessageProducingVO
{
	private String producerName;
	private String topic;
	private String message;
	private int retryingCount;
	//1.2.2 消息体新增traceInfo信息
	private String traceLogContent;
	
	public String getProducerName()
	{
		return producerName;
	}
	public void setProducerName(String producerName)
	{
		this.producerName = producerName;
	}
	public String getTopic()
	{
		return topic;
	}
	public void setTopic(String topic)
	{
		this.topic = topic;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public int getRetryingCount()
	{
		return retryingCount;
	}
	public void setRetryingCount(int retryingCount)
	{
		this.retryingCount = retryingCount;
	}
	
	public void incRetryingCount()
	{
		this.retryingCount++;
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
		return "MessageProducingVO [producerName=" + producerName + ", topic=" + topic
		        + ", message=" + message + ", retryingCount=" + retryingCount + "]";
	}
}
