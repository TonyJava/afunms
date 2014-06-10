package com.afunms.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("unused")
public class OracleJdbcUtil {
	String strconn;

	String strDriver = "oracle.jdbc.driver.OracleDriver";
	String name;
	String pass;
	Connection conn = null;

	public Statement stmt = null;

	ResultSet rs = null;

	public OracleJdbcUtil(String url, String name, String pass) {
		this.strconn = url;
		this.name = name;
		this.pass = pass;
	}

	public void closeConn() {
		file: // close connection
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeStmt() {
		file: // close statement

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql) {
		file: // select
		try {
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	public ResultSet executeUpdate(String sql) {
		file: // insert ,update
		try {
			conn = DriverManager.getConnection(strconn, name, pass);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	public java.sql.Connection jdbc() {
		file: // connection
		try {
			if (this.name.equals("sys") || this.name.equals("system")) {
				java.util.Properties info = new java.util.Properties();
				info.put("user", name);
				info.put("password", pass);
				info.put("internal_logon", "sysdba");
				Class.forName(strDriver).newInstance();
				conn = DriverManager.getConnection(strconn, info);
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} else {
				Class.forName(strDriver).newInstance();
				conn = DriverManager.getConnection(strconn, name, pass);
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}