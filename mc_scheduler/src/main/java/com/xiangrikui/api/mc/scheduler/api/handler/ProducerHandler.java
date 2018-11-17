package com.xiangrikui.api.mc.scheduler.api.handler;

import com.xiangrikui.api.mc.common.annotation.HttpMethod;
import com.xiangrikui.api.mc.common.annotation.HttpMethod.METHOD;
import com.xiangrikui.api.mc.common.annotation.HttpMethod.STATUS_CODE;
import com.xiangrikui.api.mc.common.annotation.HttpRouterInfo;
import com.xiangrikui.api.mc.common.exception.BusinessException;
import com.xiangrikui.api.mc.scheduler.api.response.SimpleResponse;
import com.xiangrikui.api.mc.scheduler.component.IProducerComponent;
import com.xiangrikui.api.mc.scheduler.component.implement.ProducerComponent;
import com.xrk.hws.http.context.HttpContext;

/**
 * 
 * 生产者操作相关handler
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月11日 下午4:32:50
 * <br> JDK版本：1.8
 * <br>==========================
 */
@HttpRouterInfo(router = "api/v1/producer")
public class ProducerHandler extends AbstractHttpWorkerHandler
{
	private IProducerComponent producerComponent = new ProducerComponent();
	
	@HttpMethod(uri="/message", method=METHOD.POST, code=STATUS_CODE.OK)
	public SimpleResponse produceMessage(String message, String producer_name, String topic, String traceLogContext,HttpContext context) throws BusinessException
	{
		boolean result = producerComponent.produceMessage(message, producer_name, topic,traceLogContext);
		return buildSimpleResponse(result);
	}
	
	private SimpleResponse buildSimpleResponse(boolean result)
	{
		SimpleResponse simpleResponse = new SimpleResponse();
		simpleResponse.setResult(result);
		return simpleResponse;
	}
}