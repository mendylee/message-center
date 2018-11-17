package com.xiangrikui.api.mc.executor.vo;

import com.xiangrikui.api.mc.mq.vo.ConsumerVO;
import com.xiangrikui.api.mc.mq.vo.ProducerVO;
import com.xrk.retry.RetryTask;

/**
 * 
 * 任务处理的最小子单元
 * 包括一个topic，一个生产者，一个消费者
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 下午5:24:41
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class TaskVO extends RetryTask
{
	
	private String topic;
	private ProducerVO producerVO;
	private ConsumerVO consumerVO;
	private String callbackUrl;
	private String message;
	//V1.1.0 新增trace信息
	private String traceLogContent;
	
	public String getTopic()
	{
		return topic;
	}
	public void setTopic(String topic)
	{
		this.topic = topic;
	}
	public ProducerVO getProducerVO()
	{
		return producerVO;
	}
	public void setProducerVO(ProducerVO producerVO)
	{
		this.producerVO = producerVO;
	}
	public ConsumerVO getConsumerVO()
	{
		return consumerVO;
	}
	public void setConsumerVO(ConsumerVO consumerVO)
	{
		this.consumerVO = consumerVO;
	}
	public String getCallbackUrl()
	{
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl)
	{
		this.callbackUrl = callbackUrl;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	
	
	public String getTraceLogContent()
	{
		return traceLogContent;
	}
	public void setTraceLogContent(String traceLogContent)
	{
		this.traceLogContent = traceLogContent;
	}
	@Override
	public String toString()
	{
		return "TaskVO [topic=" + topic + ", producerVO=" + producerVO + ", consumerVO="
		        + consumerVO + ", callbackUrl=" + callbackUrl + ", message=" + message
		        + ",RetryTask="+super.toString()+" ]";
	}
}
