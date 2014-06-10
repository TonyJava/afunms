package com.afunms.util;

import java.sql.Connection;

import com.afunms.util.connectionPool.DBConnectPoolManager;

public class DataGate {

	/**
	 * @roseuid 3AD1A51103A6
	 */
	public static Connection getCon() throws Exception {
		return DBConnectPoolManager.getInstance().getConnection();
	}

	/**
	 * @roseuid 3AD1A5180270
	 */
	public static void freeCon(Connection con) throws Exception {
		DBConnectPoolManager.getInstance().freeConnection(con);
	}
}
