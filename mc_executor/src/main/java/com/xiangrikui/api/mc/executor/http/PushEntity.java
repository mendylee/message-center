package com.xiangrikui.api.mc.executor.http;

/**
 * 
 * 推送实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月1日 下午5:00:54
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class PushEntity
{
	private String topic;
	private String producer_name;
	private String message;
	private long produce_time;
	
	public String getTopic()
	{
		return topic;
	}
	public void setTopic(String topic)
	{
		this.topic = topic;
	}
	public String getProducer_name()
	{
		return producer_name;
	}
	public void setProducer_name(String producer_name)
	{
		this.producer_name = producer_name;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public long getProduce_time()
	{
		return produce_time;
	}
	public void setProduce_time(long produce_time)
	{
		this.produce_time = produce_time;
	}
	
	@Override
	public String toString()
	{
		return "PushEntity [topic=" + topic + ", producer_name=" + producer_name + ", message="
		        + message + ", produce_time=" + produce_time + "]";
	}
}
