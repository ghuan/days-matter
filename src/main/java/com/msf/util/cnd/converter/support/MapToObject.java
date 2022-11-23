package com.msf.util.cnd.converter.support;

import com.msf.common.core.util.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapToObject implements GenericConverter {
	public MapToObject() {
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType.isMap()) {
			try {
				Map<String, Object> map = (Map)source;
				return BeanUtils.mapToBean(map,targetType.getType());
			} catch (Exception var6) {
				throw new IllegalStateException("failed to convert map to entity", var6);
			}
		} else {
			throw new IllegalStateException("source object must be a map");
		}
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet();
		set.add(new ConvertiblePair(Map.class, Object.class));
		return set;
	}
}
