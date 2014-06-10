package com.afunms.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SysbaseJdbcUtil {
	String strconn;
	String strDriver = "com.sybase.jdbc2.jdbc.SybDriver";
	String name;
	String pass;
	Connection conn = null;

	public Statement stmt = null;

	ResultSet rs = null;

	public SysbaseJdbcUtil(String url, String name, String pass) {
		this.strconn = url;
		this.name = name;
		this.pass = pass;
	}

	@SuppressWarnings("unused")
	public void closeConn() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public void closeStmt() {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql) {
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	public ResultSet executeUpdate(String sql) {
		try {
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	public java.sql.Connection jdbc() {
		try {
			Class.forName(strDriver).newInstance();
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public ResultSet query(String sql) {
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rs;
	}
}