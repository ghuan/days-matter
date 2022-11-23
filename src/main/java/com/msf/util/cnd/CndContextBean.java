package com.msf.util.cnd;

import java.util.HashMap;
import java.util.Map;

public class CndContextBean {
	private boolean forPreparedStatement = false;
	private Map<String, Object> statementParameters;

	public CndContextBean() {
	}

	public boolean isForPreparedStatement() {
		return this.forPreparedStatement;
	}

	public void setForPreparedStatement(boolean forPreparedStatement) {
		this.forPreparedStatement = forPreparedStatement;
	}

	public void setParameter(String nm, Object val) {
		if (this.statementParameters == null) {
			this.statementParameters = new HashMap();
		}

		this.statementParameters.put(nm, val);
	}

	public Map<String, Object> getStatementParameters() {
		if (this.statementParameters == null) {
			this.statementParameters = new HashMap();
		}

		return this.statementParameters;
	}

	public void clearPatameters() {
		if (this.statementParameters != null) {
			this.statementParameters.clear();
		}

	}
}
