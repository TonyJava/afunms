package com.afunms.common.util;

import java.sql.ResultSet;

public class GeneratorKey {
	private static GeneratorKey keygen = new GeneratorKey();

	public static GeneratorKey getInstance() {
		return keygen;
	}

	private GeneratorKey() {
	}

	public synchronized int getKey() {
		DBManager db = new DBManager();
		int id = 0;
		ResultSet rs = null;
		try {
			rs = db.executeQuery("select id from nms_comp_rule order by id desc");
			if (rs.next()) {
				id = rs.getInt(1);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.close();
		}

		return id;
	}

	public synchronized int getNextKey() {
		DBManager db = new DBManager();
		int id = 0;
		ResultSet rs = null;
		try {
			rs = db.executeQuery("select id from nms_userReport order by id desc limit 1");
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.close();
		}

		return id;
	}

	public synchronized int getNextKey(String table) {
		DBManager db = new DBManager();
		int id = 0;
		ResultSet rs = null;
		try {
			rs = db.executeQuery("select id from " + table + " order by id desc limit 1");
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.close();
		}
		return id;
	}

	public synchronized int getTimingKey() {
		DBManager db = new DBManager();
		int id = 0;
		ResultSet rs = null;
		try {
			rs = db.executeQuery("select id from sys_timingbackup_telnetconfig order by id desc");
			if (rs.next()) {
				id = rs.getInt(1);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.close();
		}

		return id;
	}
}
