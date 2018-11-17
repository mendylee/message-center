package com.xiangrikui.api.mc.scheduler.api.response;

/**
 * 
 * 简单响应类型
 * {"result": boolean}
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月12日 上午11:29:29
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class SimpleResponse
{
	private boolean result;

	public boolean getResult()
	{
		return result;
	}

	public void setResult(boolean result)
	{
		this.result = result;
	}
	
	@Override
	public String toString()
	{
		return String.format("{\"result\":%s}", result);
	}
}
