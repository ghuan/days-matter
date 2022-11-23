package com.msf.util.cnd.standard;

import com.msf.util.cnd.Cnd;
import com.msf.util.cnd.CndAnalysis;

import java.util.List;

public class AND extends Cnd {
	public AND() {
		this.symbol = "and";
		this.needBrackets = true;
	}

	public Object run(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			int i = 1;

			for(int size = ls.size(); i < size; ++i) {
				boolean r = (Boolean)processor.run((List)ls.get(i));
				if (!r) {
					return false;
				}
			}

			return true;
		} catch (Exception var6) {
			throw new RuntimeException(var6.getMessage());
		}
	}
}
