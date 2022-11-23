package com.msf.util.cnd.converter.support;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.core.convert.converter.Converter;

public class StringToDocument implements Converter<String, Document> {
	public StringToDocument() {
	}

	public Document convert(String source) {
		try {
			return DocumentHelper.parseText(source);
		} catch (DocumentException var3) {
			throw new IllegalArgumentException("Failed to parse xml " + source + ", cause: " + var3.getMessage(), var3);
		}
	}
}
