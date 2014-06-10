/**
 * @author sunqichang/孙启昌
 * Created on May 16, 2011 1:21:51 PM
 */
package com.afunms.capreport.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;

public class BaseDaoImp extends BaseDao {
	private static Logger log = Logger.getLogger(BaseDaoImp.class);

	public BaseDaoImp(String table) {
		super(table);
	}

	public BaseDaoImp() {
		super();
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		return null;
	}

	/**
	 * 执行传入的sql返回结果ArrayList
	 * 
	 * @param sql
	 * @return
	 */
	public ArrayList<Map<String, String>> executeQuery(String sql) {
		ArrayList<Map<String, String>> al = new ArrayList<Map<String, String>>();
		try {
			rs = conn.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, String> m = new HashMap<String, String>();
				for (int i = 1; i <= count; i++) {
					String cname = rsmd.getColumnName(i);
					m.put(cname, rs.getString(cname));
				}
				al.add(m);
			}
		} catch (SQLException e) {
			log.error("", e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return al;
	}
}
