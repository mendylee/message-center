package com.xiangrikui.api.mc.scheduler.component;

import com.xiangrikui.api.mc.common.exception.BusinessException;
import com.xiangrikui.api.mc.mq.vo.ConsumerVO;

/**
 * 
 * 消费者操作接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 上午10:38:12
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface IConsumerComponent
{
	boolean subscribe(String topic, String consumerName, String callbackUrl) throws BusinessException;
	
	ConsumerVO unsubscribe(String topic, String consumerName) throws BusinessException;
}
