package com.xiangrikui.api.mc.common.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;

/**
 * 
 */
public class Util
{
	
	public static boolean isNumeric(String num)
	{
		if(Strings.isNullOrEmpty(num)){
			return false;
		}
		
		return num.matches("^[\\d.]+$");
	}

	public static String getAppPath(Class<?> classes)
	{
		String appBasePath = "";
		try {
			CodeSource codeSource = classes.getProtectionDomain().getCodeSource();
			appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
			File jarFile = new File(appBasePath);
			appBasePath = jarFile.getParentFile().getPath();
		}
		catch (URISyntaxException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return appBasePath;
	}

	public static String getIdentityHashCode(Object object) {
        return "0x" + Integer.toHexString(System.identityHashCode(object));
    }
	
}
