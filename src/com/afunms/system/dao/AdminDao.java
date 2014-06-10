package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Admin;

@SuppressWarnings("unchecked")
public class AdminDao extends BaseDao {

	public AdminDao() {
		super("nms_func");
	}

	public boolean insertAdmin(BaseVo baseVo) {
		String sql = null;
		boolean result = false;
		try {
			Admin admin = (Admin) baseVo;
			int Admin_id = getNextID();
			sql = "insert into nms_func(id,func_desc,ch_desc) values('" + Admin_id + "','+admin.getFunc_desc()" + "','" + admin.getCh_desc() + "')";
			result = saveOrUpdate(sql);
		} catch (Exception e) {
			result = false;
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public Admin findAdminById(String id) {
		Admin admin = (Admin) findByID(id);
		return admin;
	}

	public Admin findAdminByFuncId(String funcId) {
		Admin admin = new Admin();
		try {

			String sql = "select * from nms_func where func_desc = '" + funcId + "'";
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				admin.setId(rs.getInt("id"));
				admin.setFunc_desc(rs.getString("func_desc"));
				admin.setCh_desc(rs.getString("ch_desc"));
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return admin;
	}

	public List<Admin> findAllAdmin() {
		List<Admin> adminlist = loadAll();
		return adminlist;
	}

	public boolean deleteAdmin() {
		return false;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Admin admin = new Admin();
		try {
			admin.setId(rs.getInt("id"));
			admin.setFunc_desc(rs.getString("func_desc"));
			admin.setCh_desc(rs.getString("ch_desc"));
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return admin;

	}
}
