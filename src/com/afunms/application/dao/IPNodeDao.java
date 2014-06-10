package com.afunms.application.dao;

import java.sql.ResultSet;

import com.afunms.application.model.IPNode;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.NetworkUtil;

public class IPNodeDao extends BaseDao implements DaoInterface {
	public IPNodeDao() {
		super("app_ip_node");
	}

	public boolean save(BaseVo baseVo) {
		IPNode vo = (IPNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into app_ip_node(id,ip_address,ip_long,alias)values(");
		sql.append(vo.getId());
		sql.append(",'");
		sql.append(vo.getIpAddress());
		sql.append("',");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(",'");
		sql.append(vo.getAlias());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		IPNode vo = (IPNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update app_ip_node set alias='");
		sql.append(vo.getAlias());
		sql.append("',ip_address='");
		sql.append(vo.getIpAddress());
		sql.append("',ip_long=");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(" where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from topo_node_single_data where node_id=" + id);
			conn.addBatch("delete from app_ip_node where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		IPNode vo = new IPNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setAlias(rs.getString("alias"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
}