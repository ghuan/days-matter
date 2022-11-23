package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class CONCAT extends Cnd {
	public CONCAT() {
		this.symbol = "concat";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			StringBuffer sb = new StringBuffer();
			int i = 1;

			for(int size = ls.size(); i < size; ++i) {
				Object o = ls.get(i);
				if (o instanceof List) {
					o = processor.run((List)o);
				}

				sb.append((String) ConversionUtils.convert(o, String.class));
			}

			return sb.toString();
		} catch (Exception var7) {
			throw new RuntimeException(var7.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		return "(" + super.toString(ls, processor) + ")";
	}
}
