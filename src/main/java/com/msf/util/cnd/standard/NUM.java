package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.math.BigDecimal;
import java.util.List;

public class NUM extends Cnd {
	public NUM() {
		this.name = "d";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Number result = 0;
			Object lso = ls.get(1);
			if (lso instanceof List) {
				result = (Number) ConversionUtils.convert(processor.run((List)lso), Number.class);
			} else {
				result = (Number)ConversionUtils.convert(ls.get(1), Number.class);
			}

			if (ls.size() == 3) {
				int scale = (Integer)ConversionUtils.convert(ls.get(2), Integer.TYPE);
				result = BigDecimal.valueOf((Double)ConversionUtils.convert(result, Double.class)).setScale(scale, 4).doubleValue();
			}

			return result;
		} catch (Exception var6) {
			throw new RuntimeException(var6.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		Number result = 0;
		Object lso = ls.get(1);
		if (lso instanceof List) {
			result = (Number)ConversionUtils.convert(processor.run((List)lso), Number.class);
		} else {
			result = (Number)ConversionUtils.convert(ls.get(1), Number.class);
		}

		return String.valueOf(result);
	}
}
