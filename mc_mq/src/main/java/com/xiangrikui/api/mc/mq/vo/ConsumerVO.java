package com.xiangrikui.api.mc.mq.vo;

import java.io.Serializable;

/**
 * 
 * 消费者VO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午5:57:22
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ConsumerVO implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String callbackUrl;
	private String description;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getCallbackUrl()
	{
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl)
	{
		this.callbackUrl = callbackUrl;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString()
	{
		return "ConsumerVO [name=" + name + ", callbackUrl=" + callbackUrl + ", description="
		        + description + "]";
	}
}