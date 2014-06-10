package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;


@SuppressWarnings("unchecked")
public class SystemConfigDao extends BaseDao implements DaoInterface {
	public SystemConfigDao() {
		super("system_config");
	}

	
	public List listByPage(int curpage, int perpage) {
		// 不显示超级管理员
		return listByPage(curpage, "", perpage);
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 根据已知variable_name 的名，更新其值(此处variable_name 在system_config 表中得为唯一值)
	 * 
	 * @param variable_name
	 * @param value
	 * @return
	 */
	public Boolean updateSystemConfigByVariablenameAndValue(String variable_name, String value) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update system_config set value = '");
		sqlBuffer.append(value);
		sqlBuffer.append("' where variable_name = '");
		sqlBuffer.append(variable_name);
		sqlBuffer.append("'");
		DBManager dbmanager = new DBManager();
		try {
			dbmanager.executeUpdate(sqlBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
		}
		return true;
	}

	/**
	 * 根据已知variable_name 的名，更新其值(此处variable_name 在system_config 表中得为唯一值)
	 * 
	 * @param variable_name
	 * @param value
	 * @return
	 */
	public String getSystemCollectByVariablename(String variable_name) {
		String retValue = "";
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select value from system_config ");
		sqlBuffer.append(" where variable_name = '");
		sqlBuffer.append(variable_name);
		sqlBuffer.append("'");
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			rs = dbmanager.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				retValue = rs.getString("value");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dbmanager.close();
		}
		return retValue;
	}
}
