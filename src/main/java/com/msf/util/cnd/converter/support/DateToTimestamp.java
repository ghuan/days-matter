package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class DateToTimestamp implements Converter<Date, Timestamp> {
	public DateToTimestamp() {
	}

	public Timestamp convert(Date source) {
		return new Timestamp(source.getTime());
	}
}
