package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class GE extends Cnd {
	public GE() {
		this.symbol = ">=";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object lso = ls.get(1);
			Number v1 = null;
			if (lso instanceof List) {
				v1 = (Number) ConversionUtils.convert(processor.run((List)lso), Number.class);
			} else {
				v1 = (Number)ConversionUtils.convert(lso, Number.class);
			}

			int i = 2;

			for(int size = ls.size(); i < size; ++i) {
				Number v2 = null;
				lso = ls.get(i);
				if (lso instanceof List) {
					v2 = (Number)ConversionUtils.convert(processor.run((List)lso), Number.class);
				} else {
					v1 = (Number)ConversionUtils.convert(lso, Number.class);
				}

				if (v1.doubleValue() < v2.doubleValue()) {
					return false;
				}
			}

			return true;
		} catch (Exception var8) {
			throw new RuntimeException(var8.getMessage());
		}
	}
}
