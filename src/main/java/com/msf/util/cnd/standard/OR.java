package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;

import java.util.List;

public class OR extends Cnd {
	public OR() {
		this.symbol = "or";
		this.needBrackets = true;
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			int i = 1;

			for(int size = ls.size(); i < size; ++i) {
				boolean r = (Boolean)processor.run((List)ls.get(i));
				if (r) {
					return true;
				}
			}

			return false;
		} catch (Exception var6) {
			throw new RuntimeException(var6.getMessage());
		}
	}
}
