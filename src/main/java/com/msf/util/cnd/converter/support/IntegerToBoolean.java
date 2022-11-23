package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

public class IntegerToBoolean implements Converter<Integer, Boolean> {
	public IntegerToBoolean() {
	}

	public Boolean convert(Integer source) {
		return source == 0 ? false : true;
	}
}
