package com.msf.util.cnd.converter;

import com.msf.util.cnd.converter.support.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Iterator;
import java.util.Set;

public class ConversionUtils {
	private static ConfigurableConversionService conversion = new DefaultConversionService();

	public ConversionUtils() {
	}

	public void setConverters(Set<Converter> converters) {
		Iterator i$ = converters.iterator();

		while(i$.hasNext()) {
			Converter c = (Converter)i$.next();
			conversion.addConverter(c);
		}

	}

	public static <T> T convert(Object source, Class<T> targetType) {
		return targetType.isInstance(source) ? (T) source : conversion.convert(source, targetType);
	}

	public static boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return conversion.canConvert(sourceType, targetType);
	}

	static {
		conversion.addConverter(new LongToDate());
		conversion.addConverter(new LongToTimestamp());
		conversion.addConverter(new DateToLong());
		conversion.addConverter(new DateToNumber());
		conversion.addConverter(new DateToString());
		conversion.addConverter(new DateToTimestamp());
		conversion.addConverter(new DateToSQLDate());
		conversion.addConverter(new IntegerToBoolean());
		conversion.addConverter(new StringToDate());
		conversion.addConverter(new StringToSQLDate());
		conversion.addConverter(new StringToSQLTime());
		conversion.addConverter(new StringToTimestamp());
		conversion.addConverter(new StringToMap());
		conversion.addConverter(new StringToList());
		conversion.addConverter(new StringToDocument());
		conversion.addConverter(new StringToElement());
		conversion.addConverter(new StringToInetSocketAddress());
		conversion.addConverter(new DocumentToString());
		conversion.addConverter(new ElementToString());
		conversion.addConverter(new ElementToObject());
		conversion.addConverter(new ObjectToElement());
		conversion.addConverter(new MapToObject());
		conversion.addConverter(new ObjectToMap());
	}
}
