package com.msf.util.cnd.converter.support;

import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.sql.Date;

public class StringToSQLDate implements Converter<String, Date> {
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	public StringToSQLDate() {
	}

	public Date convert(String source) {
		if (source.length() > 10) {
			source = source.substring(0, 10);
		}

		return new Date(DateTimeFormat.forPattern("yyyy-MM-dd").parseMillis(source));
	}
}
