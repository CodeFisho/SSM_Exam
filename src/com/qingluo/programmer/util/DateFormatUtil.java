package com.qingluo.programmer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	//����ָ����ʽ�������ַ���
	public static String getDate(String format,Date date) {
		SimpleDateFormat sdfDateFormat=new SimpleDateFormat(format);
		return sdfDateFormat.format(date);
	}

}
