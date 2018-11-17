package com.xiangrikui.api.mc.mq;

import javax.jms.MessageListener;

/**
 * 
 * 消息中心通用消费者接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年7月8日 下午3:27:52
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface IMCConsumer
{
	/**
	 * 
	 * 设置消息监听 
	 *    
	 * @param messageListener
	 */
	void setMessageListener(MessageListener messageListener);
	
	/**
	 * 
	 * 启动监听
	 *
	 */
	void start();
	
	/**
	 * 
	 * 关闭监听
	 *
	 */
	void shutdown();
}
