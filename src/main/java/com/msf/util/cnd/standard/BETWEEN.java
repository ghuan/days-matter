package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;
import com.msf.util.cnd.CndUtils;

import java.util.List;

public class BETWEEN extends Cnd {
	public BETWEEN() {
		this.symbol = "between";
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			double v = CndUtils.toNumber(ls.get(1), processor).doubleValue();
			double low = CndUtils.toNumber(ls.get(2), processor).doubleValue();
			double high = CndUtils.toNumber(ls.get(3), processor).doubleValue();
			return low < v && v < high;
		} catch (Exception var9) {
			throw new RuntimeException(var9.getMessage());
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			StringBuffer sb = new StringBuffer(CndUtils.toString(ls.get(1), processor));
			sb.append(" between ").append(CndUtils.toString(ls.get(2), processor)).append(" and ").append(CndUtils.toString(ls.get(3), processor));
			return sb.toString();
		} catch (Exception var4) {
			throw new RuntimeException(var4);
		}
	}
}
