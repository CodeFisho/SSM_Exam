package com.qingluo.programmer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	//返回指定格式的日期字符串
	public static String getDate(String format,Date date) {
		SimpleDateFormat sdfDateFormat=new SimpleDateFormat(format);
		return sdfDateFormat.format(date);
	}

}
