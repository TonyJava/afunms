package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageHostConnectNodeTemp;

public class StorageHostConnectTempDao extends BaseDao implements DaoInterface {

	public StorageHostConnectTempDao() {
		super("nms_storage_hostconnect");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_hostconnect where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageHostConnectNodeTemp vo = new StorageHostConnectNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setName(rs.getString("name"));
			vo.setHostconnect_id(rs.getString("hostconnect_id"));
			vo.setWwpn(rs.getString("wwpn"));
			vo.setHostType(rs.getString("hosttype"));
			vo.setProfile(rs.getString("profile"));
			vo.setPortgrp(rs.getString("portgrp"));
			vo.setVolgrpID(rs.getString("volgrpid"));
			vo.setEssIOport(rs.getString("essIOport"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageHostConnectNodeTemp vo = (StorageHostConnectNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_hostconnect(nodeid,ip,name,hostconnect_id,wwpn,hosttype,profile,portgrp,essioport,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getHostconnect_id());
		sql.append("','");
		sql.append(vo.getWwpn());
		sql.append("','");
		sql.append(vo.getHostType());
		sql.append("','");
		sql.append(vo.getProfile());
		sql.append("','");
		sql.append(vo.getPortgrp());
		sql.append("','");
		sql.append(vo.getEssIOport());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageHostConnectNodeTemp> getStorageHostConnectNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

}
