package com.iteye.weimingtom.wce.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static String getTimeString() {
    	return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
    			.getDefault()).format(new Date(System.currentTimeMillis()));
    }
    
    public static String getDateString() {
    	return new SimpleDateFormat("yyyy-MM-dd", Locale
    			.getDefault()).format(new Date(System.currentTimeMillis()));
    }
}
