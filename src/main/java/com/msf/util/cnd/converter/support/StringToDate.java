package com.msf.util.cnd.converter.support;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class StringToDate implements Converter<String, Date> {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATETIME1_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String DATETIME2_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DATETIME3_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final String DATETIME4_FORMAT = "yyyy-MM-dd HH:mm";

	public StringToDate() {
	}

	public static Date toDate(String s) {
		return DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC().parseLocalDate(s).toDate();
	}

	public static Date toDatetime(String s) {
		return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC().parseDateTime(s).toDate();
	}

	public Date convert(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		} else {
			int len = source.length();
			if (StringUtils.contains(source, "T")) {
				if (StringUtils.contains(source, "Z")) {
					return len == 20 ? DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC().parseDateTime(source).toDate() : ISODateTimeFormat.dateTime().parseDateTime(source).toDate();
				} else {
					return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC().parseDateTime(source).toDate();
				}
			} else if (StringUtils.contains(source, ":")) {
				return len == 16 ? DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(source).toDate() : DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(source).toDate();
			} else if (StringUtils.contains(source, "-")) {
				return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(source).toDate();
			} else if (StringUtils.equals(source.toLowerCase(), "now")) {
				return new Date();
			} else if (StringUtils.equals(source.toLowerCase(), "today")) {
				return (new DateTime()).withTimeAtStartOfDay().toDate();
			} else if (StringUtils.equals(source.toLowerCase(), "yesterday")) {
				return (new LocalDate()).minusDays(1).toDate();
			} else if (StringUtils.equals(source.toLowerCase(), "tomorrow")) {
				return (new LocalDate()).plusDays(1).toDate();
			} else {
				throw new IllegalArgumentException("Invalid date string value '" + source + "'");
			}
		}
	}
}
