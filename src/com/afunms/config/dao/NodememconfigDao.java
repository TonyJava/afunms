package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Nodememconfig;

@SuppressWarnings("unchecked")
public class NodememconfigDao extends BaseDao implements DaoInterface {

	public NodememconfigDao() {
		super("nms_nodememconfig");
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_nodememconfig where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Nodememconfig nodememconfig = new Nodememconfig();
		try {
			if (rs != null) {
				nodememconfig.setId(rs.getInt("id"));
				nodememconfig.setNodeid(rs.getInt("nodeid"));
				nodememconfig.setTotalVisibleMemorySize(rs.getString("totalVisibleMemorySize"));
				nodememconfig.setTotalVirtualMemorySize(rs.getString("totalVirtualMemorySize"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodememconfig;
	}

	public boolean save(BaseVo vo) {
		Nodememconfig nodememconfig = (Nodememconfig) vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_nodememconfig(nodeid,totalVisibleMemorySize,totalVirtualMemorySize)values(");
		sql.append("'");
		sql.append(nodememconfig.getNodeid());
		sql.append("','");
		sql.append(nodememconfig.getTotalVisibleMemorySize());
		sql.append("','");
		sql.append(nodememconfig.getTotalVisibleMemorySize());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		Nodememconfig nodememconfig = (Nodememconfig) vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("update nms_nodememconfig set nodeid='");
		sql.append(nodememconfig.getNodeid());
		sql.append("',totalVisibleMemorySize='");
		sql.append(nodememconfig.getTotalVisibleMemorySize());
		sql.append("',totalVirtualMemorySize='");
		sql.append(nodememconfig.getTotalVirtualMemorySize());
		sql.append("'where id=");
		sql.append(nodememconfig.getId());
		return saveOrUpdate(sql.toString());
	}
}
