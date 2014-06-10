package com.afunms.query;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class QueryService {

	private static String driver = "";
	private static String url = "";
	private static String username = "";
	private static String pwd = "";

	private Vector<Vector<String>> rows;
	private Vector<String> head;
	private String message = "";
	private int count = 0;

	public void getAllDataFromDB(String sql) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean hasResultSet = false;
		conn = this.getConnection();

		try {
			stmt = conn.createStatement();
			sql = new String(sql.getBytes("ISO-8859-1"), "gb2312");
			hasResultSet = stmt.execute(sql);
			if (hasResultSet) {
				head = new Vector<String>();
				rows = new Vector<Vector<String>>();
				rs = stmt.getResultSet();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				String columnName = "";
				for (int i = 0; i < columnCount; i++) {
					columnName = rsmd.getColumnName(i + 1);
					head.add(columnName);
				}
				while (rs.next()) {
					Vector<String> row = new Vector<String>();
					for (int i = 0; i < columnCount; i++) {
						row.addElement(rs.getString(i + 1));
					}
					rows.add(row);
				}
			} else {
				count = stmt.getUpdateCount();
				message = "该SQL语句影响的记录有" + stmt.getUpdateCount() + "条";
			}
		} catch (SQLException e) {
			message = e.toString();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			if (driver.equals("oracle.jdbc.driver.OracleDriver")) {
				if (username.equals("sys") || username.equals("system")) {
					java.util.Properties info = new java.util.Properties();
					info.put("user", username);
					info.put("password", pwd);
					info.put("internal_logon", "sysdba");
					try {
						Class.forName(driver).newInstance();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					conn = DriverManager.getConnection(url, info);
				} else {
					try {
						Class.forName(driver).newInstance();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					conn = DriverManager.getConnection(url, username, pwd);
				}
			} else {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, username, pwd);
			}
		} catch (SQLException e) {
			message = "连接失败" + e.toString();
		} catch (ClassNotFoundException e) {
			message = "加载数据库驱动失败！" + e.toString();
		}
		return conn;
	}

	@SuppressWarnings("static-access")
	public boolean testConnection(String driver, String url, String username, String pwd) {
		Connection conn = null;
		try {
			if (driver.equals("oracle.jdbc.driver.OracleDriver")) {
				if (username.equals("sys") || username.equals("system")) {
					java.util.Properties info = new java.util.Properties();
					info.put("user", username);
					info.put("password", pwd);
					info.put("internal_logon", "sysdba");
					try {
						Class.forName(driver).newInstance();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					conn = DriverManager.getConnection(url, info);
				} else {
					try {
						Class.forName(driver).newInstance();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					conn = DriverManager.getConnection(url, username, pwd);
				}
			} else {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, username, pwd);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.pwd = pwd;
		return true;
	}

	public Vector<Vector<String>> getRows() {
		return rows;
	}

	public Vector<String> getHead() {
		return head;
	}

	public String getMessage() {
		return message;
	}

	public int getCount() {
		return count;
	}
}
