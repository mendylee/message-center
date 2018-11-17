package com.xiangrikui.api.mc.executor.task;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.executor.vo.TaskVO;

/**
 * 
 * 任务处理器
 * 实现了Runnable接口
 * 处理任务调用的真是方法是MessagePostingQueueService里的入队方法
 * 队列内部根据重试次数判断是否需要重试，决定是异步还是同步执行
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 下午5:50:15
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class TaskProcessor implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskProcessor.class);
	
	//断定不为空
	private TaskVO taskVO;
	
	//最大重试次数
	//默认为10次
	public static final int DEFAULT_MAX_RETRYING_COUNT = 10;
	
	@SuppressWarnings("unused")
	private TaskProcessor() 
	{
		
	}
	
	public TaskProcessor(TaskVO task)
	{
		this.taskVO = Objects.requireNonNull(task);
	}
	
	@Override
	public void run()
	{
		//执行任务
		//调用MessagePostingQueueService的入队方法
		
		LOGGER.debug("process task, task: {}", taskVO.toString());
		
		boolean ret = false;
		try
		{
			ret = MessagePostingQueueService.getInstance().offer(taskVO);
			if (!ret)
			{
				//业务异常，入队失败
				//内部已记录错误日志，这里无需重复记录
				LOGGER.error("fail to put taskVO in queue, vo: {}", taskVO.toString());
			}
		}
		catch (Exception e)
		{
			//内部服务异常，处理失败
			//内部已记录错误日志，这里无需重复记录
			LOGGER.error(String.format("fail to put taskVO in queue, internal server error, vo: %s", taskVO.toString()), e);
		}

	}
}
