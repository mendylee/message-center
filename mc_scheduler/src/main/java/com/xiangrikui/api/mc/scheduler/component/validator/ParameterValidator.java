package com.xiangrikui.api.mc.scheduler.component.validator;

import com.xiangrikui.api.mc.common.util.Strings;

/**
 * 
 * ParameterValidator: ParameterValidator.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月2日 下午6:09:30
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ParameterValidator
{
	public static boolean validateProducerName(String producerName)
	{
		return !Strings.isNullOrEmpty(producerName);
	}
	
	public static boolean validateTopic(String topic)
	{
		return !Strings.isNullOrEmpty(topic);
	}
	
	public static boolean validateMessage(String message)
	{
		return !Strings.isNullOrEmpty(message);
	}
	
	public static boolean validateConsumerName(String consumerName)
	{
		return !Strings.isNullOrEmpty(consumerName);
	}
	
	public static boolean validateCallbackUrl(String callbackUrl)
	{
		return !Strings.isNullOrEmpty(callbackUrl);
	}
}
