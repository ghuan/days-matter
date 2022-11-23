package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class DateToLong implements Converter<Date, Long> {
	public DateToLong() {
	}

	public Long convert(Date source) {
		return source.getTime();
	}
}
