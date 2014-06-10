package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageIOPortNodeTemp;

public class StorageIOPortTempDao extends BaseDao implements DaoInterface {

	public StorageIOPortTempDao() {
		super("nms_storage_ioport");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_ioport where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageIOPortNodeTemp vo = new StorageIOPortNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setIoport_id(rs.getString("ioport_id"));
			vo.setWwpn(rs.getString("wwpn"));
			vo.setState(rs.getString("state"));
			vo.setType(rs.getString("type"));
			vo.setTopo(rs.getString("topo"));
			vo.setPortgrp(rs.getString("portgrp"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageIOPortNodeTemp vo = (StorageIOPortNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_ioport(nodeid,ip,ioport_id,wwpn,state,type,topo,portgrp,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getIoport_id());
		sql.append("','");
		sql.append(vo.getWwpn());
		sql.append("','");
		sql.append(vo.getState());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getTopo());
		sql.append("','");
		sql.append(vo.getPortgrp());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageIOPortNodeTemp> getStorageIOPortNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

}
