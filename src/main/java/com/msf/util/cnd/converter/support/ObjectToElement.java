package com.msf.util.cnd.converter.support;

import com.msf.common.core.util.BeanUtils;
import com.msf.util.cnd.converter.ConversionUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ObjectToElement implements GenericConverter {
	public ObjectToElement() {
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
//			Map<String, Object> map = (Map)BeanUtils.map(source, HashMap.class);
			Map<String, Object> map = BeanUtils.beanToMap(source);
			Element beanEl = DocumentHelper.createElement(source.getClass().getSimpleName());
			Set<String> fields = map.keySet();

			Element fieldEl;
			for(Iterator i$ = fields.iterator(); i$.hasNext(); beanEl.add(fieldEl)) {
				String field = (String)i$.next();
				fieldEl = DocumentHelper.createElement(field);
				Object val = map.get(field);
				if (val != null) {
					fieldEl.setText((String) ConversionUtils.convert(val, String.class));
				}
			}

			return beanEl;
		} catch (Exception var11) {
			throw new IllegalStateException("failed to convert entity to element", var11);
		}
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet();
		set.add(new ConvertiblePair(Object.class, Element.class));
		return set;
	}
}
