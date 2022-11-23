//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.p6spy.engine.common;

import com.msf.common.core.util.DBFunctionUtils;
import com.p6spy.engine.logging.P6LogLoadableOptions;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.logging.format.BinaryFormat;
import com.p6spy.engine.spy.P6SpyOptions;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Value {
	private Object value;

	public Value(Object valueToSet) {
		this();
		this.value = valueToSet;
	}

	public Value() {
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return this.convertToString(this.value);
	}

	public String convertToString(Object value) {
		String result;
		if (value == null) {
			result = "NULL";
		} else {
			if (value instanceof byte[]) {
				P6LogLoadableOptions logOptions = P6LogOptions.getActiveInstance();
				if (logOptions == null || !logOptions.getExcludebinary()) {
					BinaryFormat binaryFormat = P6SpyOptions.getActiveInstance().getDatabaseDialectBinaryFormatInstance();
					return binaryFormat.toString((byte[])((byte[])value));
				}

				result = "[binary]";
			} else if (value instanceof Timestamp || value instanceof Date) {
				result = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(value);
				return DBFunctionUtils.STR_TO_DATE("'"+result+"'","yyyy-MM-dd HH:mm:ss");
			} else if (value instanceof Boolean) {
				if ("numeric".equals(P6SpyOptions.getActiveInstance().getDatabaseDialectBooleanFormat())) {
					result = Boolean.FALSE.equals(value) ? "0" : "1";
				} else {
					result = value.toString();
				}
			} else {
				result = value.toString();
			}

			result = this.quoteIfNeeded(result, value);
		}

		return result;
	}

	private String quoteIfNeeded(String stringValue, Object obj) {
		if (stringValue == null) {
			return null;
		} else {
			return !Number.class.isAssignableFrom(obj.getClass()) && !Boolean.class.isAssignableFrom(obj.getClass()) ? "'" + this.escape(stringValue) + "'" : stringValue;
		}
	}

	private String escape(String stringValue) {
		return stringValue.replaceAll("'", "''");
	}
}
