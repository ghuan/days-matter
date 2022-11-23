package com.msf.util.cnd.converter.support;

import com.msf.util.cnd.converter.ConversionUtils;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class StringToTimestamp implements Converter<String, Timestamp> {
	public StringToTimestamp() {
	}

	public Timestamp convert(String source) {
		Date dt = (Date) ConversionUtils.convert(source, Date.class);
		return new Timestamp(dt.getTime());
	}
}
