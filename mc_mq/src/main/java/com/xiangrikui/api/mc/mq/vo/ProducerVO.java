package com.xiangrikui.api.mc.mq.vo;

import java.io.Serializable;

/**
 * 
 * 生产者VO
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午5:56:57
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ProducerVO implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "ProducerVO [name=" + name + ", getName()=" + getName() + ", getClass()="
		        + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
		        + "]";
	}
}
