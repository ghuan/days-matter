package com.msf.util.cnd.standard;

import com.msf.common.core.util.DBFunctionUtils;
import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.Date;
import java.util.List;

public class TODATE extends Cnd {
	public TODATE() {
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Date result = null;
			Object lso = ls.get(1);
			if (lso instanceof List) {
				result = (Date)ConversionUtils.convert(processor.run((List)lso), Date.class);
			} else {
				result = (Date) ConversionUtils.convert(ls.get(1), Date.class);
			}

			return result;
		} catch (Exception var5) {
			throw new RuntimeException(var5.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		String result = null;
		Object lso = ls.get(1);
		if (lso instanceof List) {
			result = ConversionUtils.convert(processor.toString((List)lso), String.class);
		} else {
			result = (String) ConversionUtils.convert(ls.get(1), String.class);
		}
		return DBFunctionUtils.STR_TO_DATE(result,(String)ls.get(2));
	}
}
