package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageExtpoolNodeTemp;

public class StorageExtpoolTempDao extends BaseDao implements DaoInterface {

	public StorageExtpoolTempDao() {
		super("nms_storage_extpool");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_extpool where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageExtpoolNodeTemp vo = new StorageExtpoolNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setName(rs.getString("name"));
			vo.setExtpool_id(rs.getString("extpool_id"));
			vo.setStgtype(rs.getString("stgtype"));
			vo.setRankgrp(rs.getString("rankgrp"));
			vo.setStatus(rs.getString("status"));
			vo.setAvailstor(rs.getString("availstor"));
			vo.setAllocated(rs.getString("allocated"));
			vo.setAvailable(rs.getString("available"));
			vo.setReserved(rs.getString("reserved"));
			vo.setNumvols(rs.getString("numvols"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageExtpoolNodeTemp vo = (StorageExtpoolNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_extpool(nodeid,ip,name,extpool_id,stgtype,rankgrp,status,availstor,allocated,available,reserved,numvols,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getExtpool_id());
		sql.append("','");
		sql.append(vo.getStgtype());
		sql.append("','");
		sql.append(vo.getRankgrp());
		sql.append("','");
		sql.append(vo.getStatus());
		sql.append("','");
		sql.append(vo.getAvailstor());
		sql.append("','");
		sql.append(vo.getAllocated());
		sql.append("','");
		sql.append(vo.getAvailable());
		sql.append("','");
		sql.append(vo.getReserved());
		sql.append("','");
		sql.append(vo.getNumvols());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageExtpoolNodeTemp> getStorageExtpoolNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

}
