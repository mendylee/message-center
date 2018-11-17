package com.xiangrikui.api.mc.scheduler.api.response;

/**
 * 
 * UnsubscribeResponse: UnsubscribeResponse.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 下午4:00:56
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class UnsubscribeResponse
{
	private String topic;
	private String consumer_name;
	private String callback_url;
	
	public String getTopic()
	{
		return topic;
	}
	public void setTopic(String topic)
	{
		this.topic = topic;
	}
	public String getConsumer_name()
	{
		return consumer_name;
	}
	public void setConsumer_name(String consumer_name)
	{
		this.consumer_name = consumer_name;
	}
	public String getCallback_url()
	{
		return callback_url;
	}
	public void setCallback_url(String callback_url)
	{
		this.callback_url = callback_url;
	}
}
