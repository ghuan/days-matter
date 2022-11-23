package com.msf.util.cnd;

import com.msf.util.cnd.converter.ConversionUtils;

import java.util.Date;
import java.util.List;

public class CndUtils {
	public CndUtils() {
	}

	public static Number toNumber(Object lso, CndAnalysis processor) throws RuntimeException {
		try {
			Number v = null;
			if (lso instanceof List) {
				v = (Number) ConversionUtils.convert(processor.run((List)lso), Number.class);
			} else {
				v = (Number)ConversionUtils.convert(lso, Number.class);
			}

			return v;
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public static String toString(Object lso, CndAnalysis processor) throws RuntimeException {
		try {
			String s = null;
			if (lso instanceof List) {
				s = processor.toString((List)lso);
			} else if (lso instanceof String) {
				s = "'" + lso + "'";
			} else {
				s = (String)ConversionUtils.convert(lso, String.class);
				if (lso instanceof Date) {
					return "'" + s + "'";
				}
			}

			return s;
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}
}
