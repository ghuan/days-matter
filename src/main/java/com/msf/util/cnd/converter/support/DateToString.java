package com.msf.util.cnd.converter.support;

import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class DateToString implements Converter<Date, String> {
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public DateToString() {
	}

	public String convert(Date source) {
		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(source.getTime());
	}
}
