package com.msf.util.cnd.converter.support;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.mvel2.MVEL;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.*;

public class ElementToObject implements GenericConverter {
	public ElementToObject() {
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (Element.class.isInstance(source)) {
			try {
				Element el = (Element)source;
				Object dest = targetType.getType().newInstance();
				List<Attribute> attrs = el.attributes();
				Iterator i$ = attrs.iterator();
				while(i$.hasNext()) {
					Attribute attr = (Attribute)i$.next();
					try {
						MVEL.setProperty(dest, attr.getName(), attr.getValue());
					} catch (Exception var12) {
						try {
							Map<String, Object> vars = new HashMap();
							vars.put("key", attr.getName());
							vars.put("value", attr.getValue());
							MVEL.eval("setProperty(key,value)", dest, vars);
						} catch (Exception var11) {
						}
					}
				}

				return dest;
			} catch (Exception var13) {
				throw new IllegalStateException("failed to convert element to entity", var13);
			}
		} else {
			throw new IllegalStateException("source object must be a Element");
		}
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet();
		set.add(new ConvertiblePair(Element.class, Object.class));
		return set;
	}
}
