package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.List;

public class ISNULL extends Cnd {
	public ISNULL() {
		this.name = "isNull";
	}

	public Object run(List ls, CndAnalysis processor) throws RuntimeException {
		Object lso = ls.get(1);
		if (lso instanceof List) {
			lso = processor.run((List)lso);
		}

		return lso == null;
	}

	public String toString(List ls, CndAnalysis processor) throws RuntimeException {
		Object lso = ls.get(1);
		if (lso instanceof List) {
			lso = processor.toString((List)lso);
		}

		StringBuffer sb = new StringBuffer((String) ConversionUtils.convert(lso, String.class));
		sb.append(" is null");
		return sb.toString();
	}
}
