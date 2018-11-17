package com.xiangrikui.api.mc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP处理类路由信息，将指定URI的请求路由到符合匹配条件的处理类中
 * HttpRouterInfo: HttpRouterInfo.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRouterInfo
{
	/**
	 * 
	 * 路由名称，可以用作JS代理插件KEY使用  
	 *    
	 * @return
	 */
	String name() default "";
	
	/**
	 * 
	 * 是否生成JS代理类，默认为false，设置为true则会生成各个Http接口的代理类，可通过JS接口获取
	 *    
	 * @return
	 */
	boolean proxy() default false;
	/**
	 * 
	 * 路由信息，可以使用正则式，默认前向匹配（注：最前面不用加'/'符号)  
	 *    
	 * @return
	 */
	String router() default "";
	/**
	 * 
	 * 允许的HTTP请求方法，默认为允许所有请求方法，如果需要过滤的话，可以使用正则式，如:PUT|GET，表示允许PUT或GET方法  
	 *    
	 * @return
	 */
	String method() default ".*";
}
