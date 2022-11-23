package com.msf.util.cnd.converter.support;

import com.msf.common.core.util.JSONUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class StringToList implements Converter<String, List> {
	public StringToList() {
	}

	public List convert(String source) {
		return (List) JSONUtils.parse(source, List.class);
	}
}
