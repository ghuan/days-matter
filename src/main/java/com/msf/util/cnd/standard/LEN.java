package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class LEN extends Cnd {
	public LEN() {
		this.symbol = "len";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		Object o = ls.get(1);
		if (o instanceof List) {
			o = processor.run((List)o);
		}

		String str = (String) ConversionUtils.convert(o, String.class);
		return str.length();
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		StringBuffer sb = (new StringBuffer(this.symbol)).append("(");
		Object lso = ls.get(1);
		if (lso instanceof List) {
			sb.append(processor.toString((List)lso));
		} else {
			sb.append("'").append((String)ConversionUtils.convert(lso, String.class)).append("'");
		}

		sb.append(")");
		return sb.toString();
	}
}
