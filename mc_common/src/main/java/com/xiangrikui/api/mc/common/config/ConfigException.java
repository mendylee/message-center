package com.xiangrikui.api.mc.common.config;

/**
 * 
 * ConfigException: ConfigException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2016年6月23日 下午3:38:58
 * <br> JDK版本：1.8
 * <br>==========================
 */
public class ConfigException extends RuntimeException 
{
	private static final long serialVersionUID = -8727533258158539992L;

	public ConfigException() {}

	public ConfigException(String message) 
	{
		super(message);
	}

	public ConfigException(Throwable cause)
	{
		super(cause);
	}

	public ConfigException(String message, Throwable cause) 
	{
		super(message, cause);	
	}
}
