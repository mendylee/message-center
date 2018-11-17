package com.xiangrikui.api.mc.scheduler.api.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiangrikui.api.mc.common.annotation.HttpMethod;
import com.xiangrikui.api.mc.common.annotation.HttpMethod.METHOD;
import com.xiangrikui.api.mc.common.annotation.HttpMethod.STATUS_CODE;
import com.xiangrikui.api.mc.common.annotation.HttpRouterInfo;
import com.xiangrikui.api.mc.common.exception.BusinessException;
import com.xiangrikui.api.mc.common.util.Strings;
import com.xiangrikui.api.mc.scheduler.api.response.SimpleResponse;
import com.xiangrikui.api.mc.scheduler.cache.SubscriberTableCache;
import com.xiangrikui.api.mc.scheduler.vo.SubscriberTableVO;
import com.xiangrikui.api.mc.storage.cache.CacheService;
import com.xrk.hws.http.context.HttpContext;

/**
 * 
 * 调试接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年5月17日 下午3:22:02
 * <br> JDK版本：1.8
 * <br>==========================
 */
@HttpRouterInfo(router = "api/v1/debug")
public class DebugHandler extends AbstractHttpWorkerHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DebugHandler.class);
	
	private SubscriberTableCache subscriberTableCache = (SubscriberTableCache) CacheService.GetService(SubscriberTableCache.class);
	
	@HttpMethod(uri="", method=METHOD.DELETE, code=STATUS_CODE.OK)
	public SimpleResponse deleteSubscriberTable(String topic, String producer_name, String consumer_name, HttpContext context) throws BusinessException
	{
		LOGGER.debug("delete subscriber table, topic: {}, producer_name: {}, consumer_name: {}", topic, producer_name, consumer_name);
		
		//当三个参数都为空时，清空所有
		if (Strings.isNullOrEmpty(topic))
		{
			if (Strings.isNullOrEmpty(producer_name))
			{
				if (Strings.isNullOrEmpty(consumer_name))
				{
					subscriberTableCache.clear();
					LOGGER.debug("delete subscriber table, clean all !!!!");
				}
			}
		}
		
		boolean result = true;
		
		return buildSimpleResponse(result);
	}
	
	@HttpMethod(uri="", method=METHOD.GET, code=STATUS_CODE.OK)
	public List<SubscriberTableVO> getSubscriberTable(String topic) throws BusinessException
	{
		List<SubscriberTableVO> list = new ArrayList<SubscriberTableVO>();
		
		Map<String, SubscriberTableVO> map = subscriberTableCache.getAll();
		if (map != null && map.size() > 0)
		{
			Set<Entry<String, SubscriberTableVO>> entries = map.entrySet();
			for (Entry<String, SubscriberTableVO> entry : entries)
			{
				list.add(entry.getValue());
			}
		}
		
		return list;
	}
	
	private SimpleResponse buildSimpleResponse(boolean result)
	{
		SimpleResponse simpleResponse = new SimpleResponse();
		simpleResponse.setResult(result);
		return simpleResponse;
	}
}