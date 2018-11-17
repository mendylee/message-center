package com.xiangrikui.api.mc.scheduler.api.handler;

import io.netty.handler.codec.http.HttpRequest;

import java.util.UUID;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.hws.http.monitor.MonitorProperties;
import com.xrk.hws.http.monitor.support.TraceInfoVo;

/**
 * 
 * TrackerInfoHelper: 质量日志追踪帮助类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年10月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class TrackerInfoHelper
{
	
	public static final String DEFAULT_XRK_TRACE_ID = "xrk_trace_id";
	public static final String DEFAULT_XRK_TRACE_PATH = "xrk_trace_path";
	private static final String TRACE_PATH_SPLIT_CHAR = "$";
	
	
	public static  TraceInfoVo buildTraceInfo(HttpContext ctx){
		HttpRequest httpRequest = ctx.request;
		String tracePath = httpRequest.headers().get(DEFAULT_XRK_TRACE_ID);
		String traceId = httpRequest.headers().get(DEFAULT_XRK_TRACE_PATH);
		
		if (traceId == null || traceId.isEmpty())
		{
			traceId = UUID.randomUUID().toString();
		}
		
		String timestamp = String.valueOf(System.currentTimeMillis());
		timestamp = timestamp.substring(timestamp.length() - 8, timestamp.length());
		String currentNodePath = String.format("%s:%s", timestamp, MonitorProperties.getCurrentNodePath());
		
		if (tracePath == null || tracePath.isEmpty())
		{
			tracePath = currentNodePath;
		}
		else
		{
			tracePath = tracePath + TRACE_PATH_SPLIT_CHAR + currentNodePath;
		}
		
		TraceInfoVo traceInfo = new TraceInfoVo(traceId, tracePath);
		return traceInfo;
	}
}
