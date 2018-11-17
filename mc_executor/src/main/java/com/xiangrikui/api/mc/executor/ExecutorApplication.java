package com.xiangrikui.api.mc.executor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.retry.RetryClient;
import com.xiangrikui.api.mc.executor.task.MessagePostingQueueService;
import com.xiangrikui.api.mc.executor.task.TaskCenter;
import com.xiangrikui.api.mc.mq.MCMQClient;

/**
 * 
 * 消费者适配器主启动器
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月16日 下午6:28:35
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ExecutorApplication 
{
    public static void main( String[] args )
    {
    	String appBasePath = "";
		try 
		{
			CodeSource codeSource = ExecutorApplication.class.getProtectionDomain().getCodeSource();
	        appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
    		File jarFile = new File(appBasePath);
        	appBasePath = jarFile.getParentFile().getPath();
        }
        catch (URISyntaxException | UnsupportedEncodingException e)
		{
        	//LOG服务还未初始化
            e.printStackTrace();
        }
		String configPath = String.format("%s/config/", appBasePath);
		System.out.println("configPath=" + configPath);
		String log4jPath = String.format("%slog4j.xml", configPath);
		DOMConfigurator.configureAndWatch(log4jPath, 60000);
		
		Logger logger = LoggerFactory.getLogger(ExecutorApplication.class);
		
		//初始化全局配置文件
		ExecutorConfig.init(configPath);
		
		//初始化消息投递队列
		//传入应用部署的根目录
		MessagePostingQueueService.init(appBasePath);
		//初始化重试客户端
		RetryClient.init(appBasePath);
    	//初始化MQ
        MCMQClient.init(configPath);
        //设置消费者监听器
        MCMQClient.getInstance().setHandler(TaskCenter.getInstance());
        
        logger.info("ExecutorApplication start ...");
    }
}
