package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.sql.Time;

public class StringToSQLTime implements Converter<String, Time> {
	public StringToSQLTime() {
	}

	public Time convert(String source) {
		return Time.valueOf(source);
	}
}
