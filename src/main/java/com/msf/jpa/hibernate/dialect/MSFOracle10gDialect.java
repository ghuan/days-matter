package com.msf.jpa.hibernate.dialect;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class MSFOracle10gDialect extends Oracle10gDialect {

	public MSFOracle10gDialect() {
		super();
		registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.CLOB, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.NCLOB, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.NCHAR, StandardBasicTypes.STRING.getName());
		registerHibernateType(Types.OTHER, StandardBasicTypes.STRING.getName());//polardb

		registerFunction( "date", new SQLFunctionTemplate(StandardBasicTypes.DATE, "to_date(?1,'yyyy-MM-dd')") );
		registerFunction( "sum_day", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1") );
		registerFunction( "sum_day2", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1+?2"));
		registerFunction( "sum_month", new SQLFunctionTemplate(StandardBasicTypes.DATE, "add_months(?1,?2)") );
	}
}
