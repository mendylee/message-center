package com.xiangrikui.api.mc.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public final class Strings {

    private Strings() {
    }

    /**
     * 判断字符串是否为null或空字符串
     */
    public static boolean isNullOrEmpty(String text) {
        return text == null || text.length() == 0 || text.trim().isEmpty();
    }

    /**
     * 
     * 转换为安全的字符串格式，即null或空字符串默认返回空值  
     *    
     * @param text
     * @return
     */
    public static String safeNull(String text) {
        if (isNullOrEmpty(text)) {
            return "";
        } else {
            return text;
        }
    }

    /**
     * 如果指定的字符串为空值或空字符串，则返回默认值
     */
    public static String getOrElse(String text, String defaultValue) {
        return (text != null) ? text : defaultValue;
    }

    /**
     * 获取指定字符后面的字符串
     *
     * @param text 要处理的字符串
     * @param after 需要分隔的字符
     * @return 如果指定的字符不存在，则返回null
     */
    public static String after(String text, String after) {
        if (!text.contains(after)) {
            return null;
        }
        return text.substring(text.indexOf(after) + after.length());
    }

    /**
     * 将指定的字符串增加双引号
     */
    public static String doubleQuote(String text) {
        return quote(text, "\"");
    }

    /**
     * 将指定的字符串增加单引号
     */
    public static String singleQuote(String text) {
        return quote(text, "'");
    }

    /**
     * 
     *
     * @param text 在字符串两端增加指定的符号
     * @param quote  需要增加的符号
     *
     * @return 
     */
    public static String quote(String text, String quote) {
        return quote + text + quote;
    }

    /**
     * 
     * 获取字符串的编码格式  
     *    
     * @param str
     * @return
     */
    public static String getEncoding(String str)
	{
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		}
		catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		}
		catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		}
		catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		}
		catch (Exception exception3) {
		}
		return "";
	}

    //将字符串转换为gbk编码格式
	public static String toGBK(String source)
	{
		try {
			String encoding = getEncoding(source);
			String tmp = new String(source.getBytes(encoding), "gbk");
			return tmp;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return source;
		}
	}

    public static String getMD5(String msg){
        try {
            //1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buff = md.digest(msg.getBytes());
            //4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            StringBuffer md5str = new StringBuffer();
            //把数组每一字节换成16进制连成md5字符串
            int digital;
            for (int i = 0; i < buff.length; i++) {
                digital = buff[i];

                if(digital < 0) {
                    digital += 256;
                }
                if(digital < 16){
                    md5str.append("0");
                }
                md5str.append(Integer.toHexString(digital));
            }
            return md5str.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<String> getStrArray(String strs,String split){
        List<String> list = new ArrayList<>();
        if (!StringUtils.isEmpty(strs)) {
            String[] str = strs.split(split);
            for (String s : str) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                list.add(s);
            }
        }
        return list;
    }
}
