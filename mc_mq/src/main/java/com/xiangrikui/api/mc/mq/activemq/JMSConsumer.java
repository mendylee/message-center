package com.xiangrikui.api.mc.mq.activemq;

import java.util.Objects;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSConsumer implements ExceptionListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JMSConsumer.class);
	
    //队列预取策略  
    private int queuePrefetch = DEFAULT_QUEUE_PREFETCH;
    public final static int DEFAULT_QUEUE_PREFETCH = 100;
    
    private String brokerUrl;
    
    private String username;
  
    private String password;
  
    private MessageListener messageListener;  
      
    private Connection connection;  
      
    private Session session;  
    //队列名  
    private String queue;
    
    @SuppressWarnings("unused")
	private JMSConsumer() {}
      
    public JMSConsumer(String username, String password, String queue, String brokerUrl)
    {
    	this(username, password, queue, brokerUrl, DEFAULT_QUEUE_PREFETCH);
    }
    
    public JMSConsumer(String username, String password, String queue, String brokerUrl, int queuePrefetch)
    {
    	this.username = Objects.requireNonNull(username);
    	this.password = Objects.requireNonNull(password);
    	this.queue = Objects.requireNonNull(queue);
    	this.brokerUrl = Objects.requireNonNull(brokerUrl);
    	this.queuePrefetch = (queuePrefetch <= 0) ? DEFAULT_QUEUE_PREFETCH : queuePrefetch;
    }
      
    /** 
     * 执行消息监听
     * 执行这一步之前，必须保证messageListener不为空
     * 
     * @throws Exception 
     */  
    public void start() throws Exception
    {
        //ActiveMQ的连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.username, this.password, this.brokerUrl);
        connection = connectionFactory.createConnection();
        
        //activeMQ预取策略
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(queuePrefetch);
        ((ActiveMQConnection) connection).setPrefetchPolicy(prefetchPolicy);
        connection.setExceptionListener(this);
        connection.start();
        
        //会话采用非事务级别，消息到达机制使用自动通知机制
        session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(this.queue);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(Objects.requireNonNull(this.messageListener));
    }
    
    /**
     * 关闭连接
     */
    public void shutdown()
    {
        try
        {
            if (session != null)
            {
                session.close();
                session = null;
            }
            if (connection != null)
            {
                connection.close();
                connection = null;
            }
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("fail to shutdown queue, queue", this.queue), e);
        }
    }
      
    @Override
    public void onException(JMSException e)
    {
        LOGGER.error(String.format("exception occur!!!, queue: %s", queue), e);
    }
    
    /**
     * 
     * 增加消息监听对象
     *    
     * @param messageListener
     */
    public void setMessageListener(MessageListener messageListener) 
    {
        this.messageListener = messageListener;  
    }
  
    public String getBrokerUrl()
    {  
        return brokerUrl;  
    }
  
    public String getUserName()
    {  
        return username;  
    }  

    public String getPassword() 
    {  
        return password;  
    }  

    public String getQueue()
    {  
        return queue;  
    }  

    public int getQueuePrefetch() 
    {  
        return queuePrefetch;  
    }
} 