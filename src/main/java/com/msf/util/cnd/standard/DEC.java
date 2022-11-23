package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class DEC extends Cnd {
	public DEC() {
		this.symbol = "-";
		this.needBrackets = true;
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Number result = null;
			Object lso = ls.get(1);
			if (lso instanceof List) {
				result = (Number)processor.run((List)lso);
			} else {
				result = (Number) ConversionUtils.convert(lso, Number.class);
			}

			int i = 2;

			for(int size = ls.size(); i < size; ++i) {
				Number v = null;
				lso = ls.get(i);
				if (lso instanceof List) {
					v = (Number)processor.run((List)lso);
				} else {
					v = (Number) ConversionUtils.convert(lso, Number.class);
				}

				result = ((Number)result).doubleValue() - v.doubleValue();
			}

			return result;
		} catch (Exception var8) {
			throw new RuntimeException(var8.getMessage());
		}
	}
}
