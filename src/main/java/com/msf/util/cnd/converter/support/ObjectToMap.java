package com.msf.util.cnd.converter.support;

import com.msf.common.core.util.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectToMap implements GenericConverter {
	public ObjectToMap() {
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType.isMap()) {
			try {
				return BeanUtils.beanToMap(source);
			} catch (Exception var5) {
				throw new IllegalStateException("failed to convert map to entity", var5);
			}
		} else {
			throw new IllegalStateException("source object must be a map");
		}
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet();
		set.add(new ConvertiblePair(Object.class, Map.class));
		return set;
	}
}
