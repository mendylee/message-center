package com.xiangrikui.api.mc.scheduler.component;

import com.xiangrikui.api.mc.common.exception.BusinessException;

/**
 * 
 * Producer接口操作接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月12日 上午10:49:13
 * <br> JDK版本：1.8
 * <br>==========================
 */
public interface IProducerComponent
{
	boolean produceMessage(String message, String producer, String topic,String traceLogContent) throws BusinessException;
}
