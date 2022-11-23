package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class DateToSQLDate implements Converter<Date, java.sql.Date> {
	public DateToSQLDate() {
	}

	public java.sql.Date convert(Date source) {
		return new java.sql.Date(source.getTime());
	}
}
