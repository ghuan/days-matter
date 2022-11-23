package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.converter.ConversionUtils;

import java.util.HashSet;
import java.util.List;

public class NOTIN extends Cnd {
	public NOTIN() {
		this.symbol = "not in ";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object o = processor.run((List)ls.get(1));
			List<?> rang = (List)ls.get(2);
			if (rang.get(0).equals("$")) {
				rang = (List)processor.run(rang);
			}

			HashSet<Object> set = new HashSet();
			set.addAll(rang);
			return set.contains(o);
		} catch (Exception var6) {
			throw new RuntimeException(var6.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			Object o = ls.get(1);
			StringBuffer sb = new StringBuffer();
			List rang;
			if (o instanceof List) {
				rang = (List)o;
				sb.append(processor.toString(rang));
			} else {
				sb.append((String)o);
			}

			sb.append(" ").append(this.symbol).append("(");
			rang = (List)ls.get(2);
			if (rang.get(0).equals("$")) {
				String s = processor.toString(rang);
				sb.append(s);
			} else {
				int i = 0;

				for(int size = rang.size(); i < size; ++i) {
					if (i > 0) {
						sb.append(",");
					}

					Object r = rang.get(i);
					String s = (String) ConversionUtils.convert(r, String.class);
					if (r instanceof Number) {
						sb.append(s);
					} else if (r instanceof List) {
						s = (String)ConversionUtils.convert(processor.run((List)r), String.class);
						sb.append(s);
					} else {
						sb.append("'").append(s).append("'");
					}
				}
			}

			return sb.append(")").toString();
		} catch (Exception var10) {
			var10.printStackTrace();
			throw new RuntimeException(var10.getMessage());
		}
	}
}
