package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class DateToNumber implements Converter<Date, Number> {
	public DateToNumber() {
	}

	public Number convert(Date source) {
		return source.getTime();
	}
}
