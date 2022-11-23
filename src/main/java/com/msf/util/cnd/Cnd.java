package com.msf.util.cnd;

import java.util.List;

public abstract class Cnd {
	protected String symbol;
	protected String name;
	protected boolean needBrackets = false;

	public Cnd() {
	}

	public abstract Object run(List<?> var1, CndAnalysis var2) throws RuntimeException;

	public String getName() {
		if (this.name != null) {
			return this.name;
		} else {
			this.name = this.getClass().getSimpleName().toLowerCase();
			return this.name;
		}
	}

	public String toString(List<?> ls, CndAnalysis processor) throws RuntimeException {
		try {
			StringBuffer sb = new StringBuffer();
			if (this.needBrackets) {
				sb.append("(");
			}

			int i = 1;

			for(int size = ls.size(); i < size; ++i) {
				if (i > 1) {
					sb.append(" ").append(this.symbol).append(" ");
				}

				Object lso = ls.get(i);
				sb.append(CndUtils.toString(lso, processor));
			}

			if (this.needBrackets) {
				sb.append(")");
			}

			return sb.toString();
		} catch (Exception var7) {
			throw new RuntimeException(var7);
		}
	}
}
