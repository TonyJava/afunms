package com.database;

import java.sql.SQLException;

public interface DatabaseAccessInterface {
	public abstract void executeSQL(String sqlStatement) throws SQLException;

	public abstract void executeSQL(String[] sqlStatement) throws SQLException;

}
