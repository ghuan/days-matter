package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;

public class LongToTimestamp implements Converter<Long, Timestamp> {
	public LongToTimestamp() {
	}

	public Timestamp convert(Long source) {
		return new Timestamp(source);
	}
}
