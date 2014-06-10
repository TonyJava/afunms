package com.bpm.system.dbpool;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

@SuppressWarnings("unused")
public class DbConn {
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private DbconnManager dbm = new DbconnManager();

	public DbConn() {
		init();
	}

	public Connection getConnection() {
		if (conn == null) {
			conn = dbm.getConnection();
		}
		return conn;
	}

	private void init() {
		try {
			conn = dbm.getConnection();
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql) {
		try {
			if (conn == null) {
				this.init();
			}
			rs = stmt.executeQuery(sql);
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return rs;
	}

	public int executeUpdate(String sql) {
		int iresult = -1;
		iresult = executeUpdate(sql, false);
		return iresult;
	}

	private int executeUpdate(String sql, boolean bCommit) {
		int iresult = -1;
		try {
			if (conn == null) {
				this.init();
			}
			iresult = stmt.executeUpdate(sql);
			if (bCommit) {
				conn.commit();
			}
			return iresult;
		} catch (SQLException se) {
			se.printStackTrace();
			try {
				conn.rollback();
				return iresult;
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return iresult;
			}

		}

	}

	private void commit() {
		try {
			conn.commit();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	
	private void rollback() {
		try {
			conn.rollback();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void addBatch(String sql) {
		try {
			if (null == conn) {
				this.init();
			}
			stmt.addBatch(sql);
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public int[] executeBatch() {
		int[] intlist = null;
		try {
			if (null == conn) {
				this.init();
				conn.setAutoCommit(false);
			}
			conn.setAutoCommit(false);
			intlist = stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			
		} catch (BatchUpdateException bse) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				stmt.clearBatch();
				conn.setAutoCommit(true);
			} catch (SQLException xe) {
				xe.printStackTrace();
			}
		}
		return intlist;
	}

}
