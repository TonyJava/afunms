package com.afunms.application.wasmonitor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysUtil;

@SuppressWarnings("unchecked")
public class GetWasInfo extends BaseDao implements DaoInterface {
	public GetWasInfo() {
		super("");
	}

	public HashMap executeQueryHashMap(String ip, String tablesub) throws SQLException {
		HashMap hm = new HashMap();
		try {
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				String tablename = "";

				tablename = tablesub + allipstr;
				rs = conn.executeQuery("select * from " + tablename);
				ResultSetMetaData rsmd = rs.getMetaData();
				int numCols = rsmd.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= numCols; i++) {
						String key = rsmd.getColumnName(i);
						String value = rs.getString(i);
						if (value == null) {
							value = "";
						}
						hm.put(key, value);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return hm;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		return null;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

}
