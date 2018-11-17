package com.xiangrikui.api.mc.common.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Function;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Response;
import com.ning.http.client.SignatureCalculator;
import com.ning.http.client.cookie.Cookie;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;


/**
 * 
* 异步Http接口工具类
 * <p>一般情况下，直接使用SimpleHttpClient.me()这个默认的客户端作调用</p>
 * <p>需要自订参数时，直接构建<code>SimpleHttpClient.HttpClientBuilder</code>
 * 这个Builder来创建SimpleHttpClient实例</p>
 * 
 * 增加delete和put方法
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月24日 下午4:09:00
 * <br> JDK版本：1.8
 * <br>==========================
 */
public final class CustomAsyncHttpClient
{

	private static int http_timeout;
	private static int http_connect_timeout;
	private static boolean http_tcpNoDelay;
	private static boolean http_keepAlive;
	
	static 
	{
		http_timeout = Integer.parseInt("60000");
		http_connect_timeout = Integer.parseInt("5000");
		http_tcpNoDelay = "true".equals("true");
		http_keepAlive = "true".equals("true");
	}
	
    private static CustomAsyncHttpClient instance = new Builder().setCompress(true).setConnectTimeout(http_connect_timeout).setReadTimeout(http_timeout).build();

    private AsyncHttpClient client;

    /**
     * 供方便时使用的Executor，不要在里面
     */
    public static ExecutorService DEFAULT_EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).build());

    private CustomAsyncHttpClient()
    {
    	
    }

    /**
     * 获取默认的Http客户端
     * @return
     */
    public static CustomAsyncHttpClient getInstance() 
    {
        return instance;
    }

    /**
     * 一般在容器生命周期结束时调用
     */
    public void shutdown()
    {
        client.close();
    }

    public CheckedFuture<Response, HttpIoException> doGet(String url, Map<String, String> params, Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator) 
    {
        BoundRequestBuilder builder = client.prepareGet(url);
		if (calculator != null) 
		{
		    builder.setSignatureCalculator(calculator);
		}
		if (headers != null) 
		{
		    FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
		    for (Map.Entry<String, String> entry : headers.entrySet())
		    {
		    	headerMap.add(entry.getKey(), entry.getValue());
		    }
		    builder = builder.setHeaders(headerMap);
		}
		if (cookies != null)
		{
			for (Cookie cookie : cookies) 
			{
				builder = builder.addCookie(cookie);
			}
		}
		if (params != null)
		{
			for (Map.Entry<String, String> param : params.entrySet())
			{
				builder = builder.addQueryParam(param.getKey(), param.getValue());
			}
		}
		ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
		return Futures.makeChecked(future, new Function<Exception, HttpIoException>() 
		{
		    @Override
		    public HttpIoException apply(Exception input)
		    {
		        return new HttpIoException(input);
		    }
		});
    }
    
    /**
     * 
     * 执行一个异步的delete方法
     *    
     * @param url
     * @param headers
     * @param cookies
     * @param calculator
     * @return
     */
    public CheckedFuture<Response, HttpIoException> doDelete(String url, Map<String, String> params, Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator)
    {
    	BoundRequestBuilder builder = client.prepareDelete(url);
    	if (calculator != null)
    	{
    		builder.setSignatureCalculator(calculator);
    	}
    	if (headers != null)
    	{
    		FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
    		for (Map.Entry<String, String> entry : headers.entrySet())
    		{
    			headerMap.add(entry.getKey(), entry.getValue());
    		}
    		builder = builder.setHeaders(headerMap);
    	}
    	if (cookies != null)
    	{
    		for (Cookie cookie : cookies)
    		{
    			builder.addCookie(cookie);
    		}
    	}
    	if (params != null)
		{
		    for (Map.Entry<String, String> entry : params.entrySet())
		    {
		        builder = builder.addQueryParam(entry.getKey(), entry.getValue());
		    }
		}
    	ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
    	return Futures.makeChecked(future, new Function<Exception, HttpIoException>() 
    	{
    		@Override
    		public HttpIoException apply(Exception input)
    		{
    			return new HttpIoException(input);
    		}
    	});
    }
    
    /**
     * 
     * 执行一个异步的put方法
     *    
     * @param url
     * @param headers
     * @param cookies
     * @param calculator
     * @return
     */
    public CheckedFuture<Response, HttpIoException> doPut(String url, Map<String, String> params, Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator)
    {
    	BoundRequestBuilder builder = client.preparePut(url);
    	if (calculator != null)
    	{
    		builder.setSignatureCalculator(calculator);
    	}
    	if (headers != null)
    	{
    		FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
    		for (Map.Entry<String, String> entry : headers.entrySet())
    		{
    			headerMap.add(entry.getKey(), entry.getValue());
    		}
    		builder = builder.setHeaders(headerMap);
    	}
    	if (cookies != null)
    	{
    		for (Cookie cookie : cookies)
    		{
    			builder.addCookie(cookie);
    		}
    	}
    	if (params != null)
		{
		    for (Map.Entry<String, String> entry : params.entrySet())
		    {
		        builder = builder.addQueryParam(entry.getKey(), entry.getValue());
		    }
		}
    	ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
    	return Futures.makeChecked(future, new Function<Exception, HttpIoException>() 
    	{
    		@Override
    		public HttpIoException apply(Exception input)
    		{
    			return new HttpIoException(input);
    		}
    	}); 
    }
    
    /**
     * 
     * 执行一个异步post方法 
     *    
     * @param url
     * @param params
     * @param headers
     * @param cookies
     * @param calculator
     * @return
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> params, byte[] body, Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator)
    {
        BoundRequestBuilder builder = client.preparePost(url);
		if (calculator != null)
		{
		    builder.setSignatureCalculator(calculator);
		}
		
		if (headers != null) 
		{
		    FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
		    for (Map.Entry<String, String> entry : headers.entrySet())
		    {
		    	headerMap.add(entry.getKey(), entry.getValue());
		    }
		    builder = builder.setHeaders(headerMap);
		}
		
		if (body != null)
		{
			//TODO
			//这里没问题？
			builder.setBody(body);
		}
		else if (params != null)
		{
		    for (Map.Entry<String, String> entry : params.entrySet())
		    {
		        //builder = builder.addQueryParam(entry.getKey(), entry.getValue());
		        builder = builder.addFormParam(entry.getKey(), entry.getValue());
		    }
		}
		
		if (cookies != null)
		{
			for(Cookie cookie : cookies) 
			{
				builder = builder.addCookie(cookie);
			}
		}

		ListenableFuture<Response> future = new ListenableFutureAdapter<Response>(builder.execute());
		return Futures.makeChecked(future, new Function<Exception, HttpIoException>() 
		{
		    @Override
		    public HttpIoException apply(Exception input) 
		    {
		        return new HttpIoException(input);
		    }
		});
    }

    public static class Builder
    {
        int connectTimeout = 5000;
        int readTimeout = 10000;
        int maxConnections = -1;
        int maxConnectionsPerHost = -1;
        boolean compress;
        boolean redirectEnabled;

        public Builder setConnectTimeout(int timeoutMs) 
        {
            this.connectTimeout = timeoutMs;
            return this;
        }

        public Builder setReadTimeout(int readTimeout)
        {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setMaxConnectionsTotal(int total) 
        {
            this.maxConnections = total;
            return this;
        }

        public Builder setMaxConnectionsPerHost(int max)
        {
            this.maxConnectionsPerHost = max;
            return this;
        }

        public Builder setCompress(boolean doCompress)
        {
            this.compress = doCompress;
            return this;
        }

        public Builder setRedirect(boolean enable) 
        {
            redirectEnabled = enable;
            return this;
        }

        /**
         * 通过手工构造AsyncHttpClientConfig，来创建http客户端
         * @param config async http client里面的配置
         * @return http客户端
         */
        public CustomAsyncHttpClient build(AsyncHttpClientConfig config)
        {
            CustomAsyncHttpClient httpClient = new CustomAsyncHttpClient();
            httpClient.client = new AsyncHttpClient(config);
            return httpClient;
        }

        /**
         * 通过简单的配置来构造http客户端
         * @return http客户端
         */
        public CustomAsyncHttpClient build() 
        {
            CustomAsyncHttpClient httpClient = new CustomAsyncHttpClient();
            com.ning.http.client.AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            if (this.connectTimeout > 0) 
            {
                builder = builder.setConnectTimeout(connectTimeout);
            }
            if (this.readTimeout > 0)
            {
                builder = builder.setRequestTimeout(readTimeout);
            }
            if (maxConnectionsPerHost > 0) 
            {
                builder = builder.setMaxConnectionsPerHost(maxConnectionsPerHost);
            }
            if (maxConnections > 0)
            {
                builder = builder.setMaxConnections(maxConnections);
            }
            builder = builder.setFollowRedirect(redirectEnabled).setCompressionEnforced(compress);
            if (http_tcpNoDelay)
            {
            	// 开启tcpNoDelay
                NettyAsyncHttpProviderConfig providerConfig = new NettyAsyncHttpProviderConfig();
                providerConfig.addProperty("tcpNoDelay", true);
                builder.setAsyncHttpClientProviderConfig(providerConfig);
            }
            if (http_keepAlive) 
            {
            	builder.setAllowPoolingConnections(true);
            }
            else
            {
            	builder.setAllowPoolingConnections(false);
            }
            
            httpClient.client = new AsyncHttpClient(builder.build());
            return httpClient;
        }
    }
}
