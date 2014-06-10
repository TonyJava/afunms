package com.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseAccessImpl implements DatabaseAccessInterface {

	public void executeSQL(String sqlStatement) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DBConnectionManager.getConnection();
			connection.setAutoCommit(true);
			statement = connection.prepareStatement(sqlStatement);
			statement.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				try {
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				throw ex;
			}
		}
		return;
	}

	public void executeSQL(String[] sqlStatement) throws SQLException {
	}

}
