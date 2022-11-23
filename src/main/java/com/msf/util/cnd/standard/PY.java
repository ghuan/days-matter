package com.msf.util.cnd.standard;

import com.msf.common.core.util.PyConverter;
import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class PY extends Cnd {
	public PY() {
		this.symbol = "pingyin";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object lso = ls.get(1);
			String str = null;
			if (lso instanceof List) {
				str = (String)processor.run((List)lso);
			} else {
				str = (String) ConversionUtils.convert(lso, String.class);
			}

			return PyConverter.getFirstLetter(str);
		} catch (Exception var5) {
			throw new RuntimeException(var5.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		return this.symbol + "(" + processor.toString((List)ls.get(1)) + ")";
	}
}
