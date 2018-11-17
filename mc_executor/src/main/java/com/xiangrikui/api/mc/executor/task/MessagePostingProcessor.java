package com.xiangrikui.api.mc.executor.task;

import java.util.Objects;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.retry.RetryClient;
import com.xiangrikui.api.mc.executor.http.PushEntity;
import com.xiangrikui.api.mc.executor.http.RequestManager;
import com.xiangrikui.api.mc.executor.vo.TaskVO;
import com.xrk.retry.RetryAble;

/**
 * 
 * 消费者适配器post消息的处理器
 * 实现了Runnable接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月18日 下午3:56:47
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class MessagePostingProcessor implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MessagePostingProcessor.class);
	//private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");
	private static final Logger SUCCESSFUL_LOGGER = LoggerFactory.getLogger("executorSuccessfulRecordLogger");
	
	//断定不为空
	private Queue<TaskVO> messagePostingQueue;
	
	@SuppressWarnings("unused")
	private MessagePostingProcessor() {}
	
	public MessagePostingProcessor(Queue<TaskVO> messagePostingQueue)
	{
		this.messagePostingQueue = Objects.requireNonNull(messagePostingQueue);
	}
	
	@Override
	public void run()
	{
		//LOGGER.debug("poll from messagePostingQueue");
		TaskVO taskVO = messagePostingQueue.poll();
		
		//队列为空，返回
		if (taskVO == null)
		{
			return;
		}
		
		LOGGER.debug("get a taskVO from queue, vo: {}", taskVO.toString());
		
		tryPostingMessage(taskVO);
	}
	
	public boolean tryPostingMessage(TaskVO taskVO)
	{
		boolean ret = false;
		
		try
		{
			ret = postMessage(taskVO);
		}
		catch (Exception e)
		{
			LOGGER.error(String.format("fail to postMessage, vo: %s", taskVO.toString()), e);
		}
		
		//假如投递消息失败，提交到重试任务队列中执行。
		if (!ret)
		{
			//设置重试的业务运行类
			taskVO.setRetryRunnerClass(new RetryAble() {
				@Override
				public boolean retryAble() throws Exception
				{
					
					boolean flag = postMessage(taskVO);
					if(flag){
					//重试投递成功，记录成功日志
					SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, consumer: {}, message: {}", 
							taskVO.getProducerVO().getName(), 
							taskVO.getTopic(),
							taskVO.getConsumerVO().getName(),
							taskVO.getMessage());
					}
					return flag;
				}
			});
			RetryClient.getInstance().submitTask(taskVO);
		}else{
			//投递成功，记录成功日志
			SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, consumer: {}, message: {}", 
					taskVO.getProducerVO().getName(), 
					taskVO.getTopic(),
					taskVO.getConsumerVO().getName(),
					taskVO.getMessage());
		}
		return true;
	}
	
	
	
	private boolean postMessage(TaskVO taskVO)
	{
		boolean ret = false;
		try
		{
			PushEntity pushEntity = new PushEntity();
			pushEntity.setMessage(taskVO.getMessage());
			pushEntity.setProduce_time(System.currentTimeMillis());
			pushEntity.setProducer_name(taskVO.getProducerVO().getName());
			pushEntity.setTopic(taskVO.getTopic());
			ret = RequestManager.postMessage(taskVO.getCallbackUrl(), pushEntity,taskVO.getTraceLogContent());
		}
		catch (Exception e)
		{
			LOGGER.error("fail to post message, interal server error, task: {}", taskVO.toString());
		}
		return ret;
	}
}