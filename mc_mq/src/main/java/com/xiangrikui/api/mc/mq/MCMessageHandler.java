package com.xiangrikui.api.mc.mq;

/**
 * 
 * 消息中心专用消息接收回调接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午3:56:07
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface MCMessageHandler
{
	public void handle(MCMessage message);
}
