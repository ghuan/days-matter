package com.msf.util.cnd.converter.support;

import org.dom4j.Element;
import org.springframework.core.convert.converter.Converter;

public class ElementToString implements Converter<Element, String> {
	public ElementToString() {
	}

	public String convert(Element source) {
		return source.asXML();
	}
}
