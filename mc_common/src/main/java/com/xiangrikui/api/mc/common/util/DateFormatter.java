package com.xiangrikui.api.mc.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    public static String formatDateToStr(Date date,String format){
        if(null==date || StringUtils.isEmpty(format)){
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(date);
    }
}
