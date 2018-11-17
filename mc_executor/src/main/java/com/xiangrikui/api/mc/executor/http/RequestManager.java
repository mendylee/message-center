package com.xiangrikui.api.mc.executor.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.xiangrikui.api.mc.common.http.HTTP;
import com.xiangrikui.api.mc.common.http.Request;
import com.xiangrikui.api.mc.common.http.Response;
import com.xrk.hws.http.monitor.support.TraceInfoVo;

/**
 * 
 * RequestManager: RequestManager.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月19日 下午3:12:58
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class RequestManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestManager.class);
	
	private static final String DEFAULT_XRK_TRACE_ID = "xrk_trace_id";
	private static final String DEFAULT_XRK_TRACE_PATH = "xrk_trace_path";
	
	//请求超时时间
	//默认为2秒
	public static final int DEFAULT_REQUEST_TIMEOUT_SECOND = 2;
	private static int requestTimeoutSecond = DEFAULT_REQUEST_TIMEOUT_SECOND;
	
	private static Gson gson = new Gson();
	
	/**
	 * 
	 * 投递消息
	 * 将生产者的消息投递给消费者指定的callback_url
	 * 使用http post方法
	 * 将消息以json格式附在http请求的body处
	 *    
	 * @param url			callback_url
	 * @param pushEntity	默认的通用推送实体
	 * @return
	 */
	public static boolean postMessage(String url, PushEntity pushEntity,String traceLogContent)
	{
		LOGGER.debug("prepare to post message, url: {}, message: {},traceLogContent: {}", url, pushEntity.toString(),traceLogContent);
		boolean ret = false;
		
		//url 有效性验证
		//暂缺
		
		Request request = new Request(url);
		
		String bodyStr;
		
		try
		{
			bodyStr = gson.toJson(pushEntity);
		}
		catch (Exception e)
		{
			LOGGER.error(String.format("fail to get json, url: %s, pushEntity: %s", url, pushEntity.toString()), e);
			return false;
		}
		
		byte[] body = bodyStr.getBytes();
		request.setBody(body);
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		TraceInfoVo traceInfo = gson.fromJson(traceLogContent, TraceInfoVo.class);
		headers.put(DEFAULT_XRK_TRACE_ID, traceInfo.getTraceId());
		headers.put(DEFAULT_XRK_TRACE_PATH, traceInfo.getTracePath());
		request.setHeaders(headers);
		
		try
		{
			Response response = HTTP.POST(request, requestTimeoutSecond);
			
			if (response == null)
			{
				LOGGER.error("fail to request, response is null, url: {}, message: {}", url, bodyStr);
			}
			else
			{
				String content = response.getContent();
				int statusCode = response.getStatusCode();
				LOGGER.debug("get response successfully! url: {}, message: {}, content: {}, statusCode: {}", url, bodyStr, content, statusCode);
				
				if (statusCode > 300)
				{
					LOGGER.debug("httpCode is wrong, url: {}, message: {}, statusCode: {}", url, bodyStr, statusCode);
				}
				else
				{
					LOGGER.debug("post message successfully! url: {}, content: {}", url, content);
					ret = true;
				}
			}
		}
		catch (IOException e) 
		{
			//IO异常
			LOGGER.error(String.format("fail to request, url: %s, message: %s", url, bodyStr), e);
		}
		catch (Exception e)
		{
			//内部服务异常
			LOGGER.error(String.format("fail to request, url: %s, message: %s", url, bodyStr), e);
		}
		
		return ret;
	}
}
