package com.xiangrikui.api.mc.mq;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * 消息中心通用生产者接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年7月8日 下午3:28:08
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface IMCProducer
{
	/**
	 * 
	 * 发送map格式的消息
	 *    
	 * @param queue
	 * @param messageMap
	 */
	void send(Map<String, Object> messageMap);
	
	/**
	 * 
	 * 发送obj格式的消息
	 *    
	 * @param queue
	 * @param messageObj
	 */
	void send(Serializable messageObj);
}
