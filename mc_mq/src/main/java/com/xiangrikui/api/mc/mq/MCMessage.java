package com.xiangrikui.api.mc.mq;

import java.io.Serializable;

/**
 * 
 * 内部专用消息体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午4:02:13
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MCMessage implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String title;
	private Serializable content;
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public Serializable getContent()
	{
		return content;
	}
	public void setContent(Serializable content)
	{
		this.content = content;
	}
}
