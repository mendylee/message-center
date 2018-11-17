package com.xiangrikui.api.mc.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 支持阻塞的、固定大小的定时处理线程池
 * 内部维护一个FixedAndBlockedThreadPoolExecutor
 * 与默认的ScheduledThreadPoolExecutor相比，定时间隔时间不受任务处理时间影响
 * 详细区别见main方法
 * 
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月23日 上午11:33:10
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class FixedAndBlockedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor
{
	private ExecutorService taskExecutor = null;
	
	public FixedAndBlockedScheduledThreadPoolExecutor(int corePoolSize)
	{
		super(corePoolSize);
		taskExecutor = Executors.newScheduledThreadPool(corePoolSize);
	}
	
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
	{
		return super.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run()
			{
				taskExecutor.execute(command);
			}
		}, initialDelay, period, unit);
	}
	
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
	{
		return super.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				taskExecutor.execute(command);
			}
		}, initialDelay, delay, unit);
	}
	
	
	/**
	 * 
	 * 测试方法，主要展示 FixedAndBlockedScheduledThreadPoolExecutor 与 ScheduledThreadPoolExecutor的区别
	 *    
	 * @param args
	 */
	public static void main(String[] args)
	{
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(5);
		FixedAndBlockedScheduledThreadPoolExecutor fixedScheduledExecutor = new FixedAndBlockedScheduledThreadPoolExecutor(5);
		
		final long startMs = System.currentTimeMillis();
		
		scheduledExecutor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run()
			{
				String name = Thread.currentThread().getName();
				System.out.println("scheduledExecutor " + name + " start at: " + (System.currentTimeMillis() - startMs));
				try 
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				System.out.println("scheduledExecutor " + name + " end at: " + (System.currentTimeMillis() - startMs));
			}
		}, 0, 2000, TimeUnit.MILLISECONDS);
		
		
		fixedScheduledExecutor.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run()
			{
				String name = Thread.currentThread().getName();
				System.out.println("fixedScheduledExecutor " + name + " start at: " + (System.currentTimeMillis() - startMs));
				try 
				{
					Thread.sleep(10000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				System.out.println("fixedScheduledExecutor " + name + " end at: " + (System.currentTimeMillis() - startMs));
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}
}
