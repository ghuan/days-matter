package com.msf.util.cnd.converter.support;

import com.msf.common.core.util.JSONUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public class StringToMap implements Converter<String, Map> {
	public StringToMap() {
	}

	public Map convert(String source) {
		return (Map) JSONUtils.parse(source, Map.class);
	}
}
