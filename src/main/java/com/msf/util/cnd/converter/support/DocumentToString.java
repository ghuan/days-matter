package com.msf.util.cnd.converter.support;

import org.dom4j.Document;
import org.springframework.core.convert.converter.Converter;

public class DocumentToString implements Converter<Document, String> {
	public DocumentToString() {
	}

	public String convert(Document source) {
		return source.asXML();
	}
}
