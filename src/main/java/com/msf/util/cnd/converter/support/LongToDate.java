package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class LongToDate implements Converter<Long, Date> {
	public LongToDate() {
	}

	public Date convert(Long source) {
		return new Date(source);
	}
}
