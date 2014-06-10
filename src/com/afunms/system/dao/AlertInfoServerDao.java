package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.system.model.AlertInfoServer;

@SuppressWarnings("unchecked")
public class AlertInfoServerDao extends BaseDao implements DaoInterface {
	public AlertInfoServerDao() {
		super("system_alertinfoserver");
	}

	public List getByFlage(int flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from system_alertinfoserver where flag = " + flag);
		return findByCriteria(sql.toString());
	}

	public boolean save(BaseVo baseVo) {
		return false;
	}

	public int save(AlertInfoServer vo) {
		int result = -1;
		try {

			StringBuffer sqlBf = new StringBuffer(100);
			sqlBf.append("insert into system_alertinfoserver(ipaddress,port,infodesc,flag)");
			sqlBf.append("values('");
			sqlBf.append(vo.getIpaddress());
			sqlBf.append("','");
			sqlBf.append(vo.getPort());
			sqlBf.append("','");
			sqlBf.append(vo.getDesc());
			sqlBf.append("',");
			sqlBf.append(vo.getFlag());
			sqlBf.append(")");
			conn.executeUpdate(sqlBf.toString());
			result = 1;
		} catch (Exception e) {
			result = -1;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo baseVo) {
		AlertInfoServer vo = (AlertInfoServer) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update system_alertinfoserver set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',port='");
		sql.append(vo.getPort());
		sql.append("',infodesc='");
		sql.append(vo.getDesc());
		sql.append("',flag=");
		sql.append(vo.getFlag());
		sql.append(" where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	public boolean delete(String id[]) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++)
				conn.addBatch("delete from system_alertinfoserver where id=" + id[i]);
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		AlertInfoServer vo = new AlertInfoServer();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setPort(rs.getString("port"));
			vo.setDesc(rs.getString("infodesc"));
			vo.setFlag(rs.getInt("flag"));
		} catch (Exception ex) {
			vo = null;
			ex.printStackTrace();
		}
		return vo;
	}
}
