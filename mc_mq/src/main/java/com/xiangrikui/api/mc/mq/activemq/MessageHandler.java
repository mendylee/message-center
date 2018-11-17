package com.xiangrikui.api.mc.mq.activemq;

import javax.jms.Message;

public interface MessageHandler
{
	/** 
     * 消息回调提供的调用方法 
     * @param message 
     */  
    public void handle(Message message);
}
