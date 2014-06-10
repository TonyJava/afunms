package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageVolgrpFbvolNodeTemp;

public class StorageVolgrpFbvolTempDao extends BaseDao implements DaoInterface {

	public StorageVolgrpFbvolTempDao() {
		super("nms_storage_volgrp_fbvol");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_volgrp_fbvol where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageVolgrpFbvolNodeTemp vo = new StorageVolgrpFbvolNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setName(rs.getString("name"));
			vo.setVolgrp_id(rs.getString("volgrp_id"));
			vo.setType(rs.getString("type"));
			vo.setVols(rs.getString("vols"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageVolgrpFbvolNodeTemp vo = (StorageVolgrpFbvolNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_volgrp_fbvol(nodeid,ip,name,volgrp_id,type,vols,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getVolgrp_id());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getVols());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageVolgrpFbvolNodeTemp> getStorageVolgrpFbvolNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

	@SuppressWarnings("unchecked")
	public List<StorageVolgrpFbvolNodeTemp> getStorageVolgrpFbvolNodeTemp(String nodeid, String type, String subtype, String volgrp_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and volgrp_id='" + volgrp_id + "'");
		return findByCondition(sql.toString());
	}

}
