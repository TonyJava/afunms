package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageArraySiteNodeTemp;

public class StorageArraySiteTempDao extends BaseDao implements DaoInterface {

	public StorageArraySiteTempDao() {
		super("nms_storage_arraysite");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_arraysite where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageArraySiteNodeTemp vo = new StorageArraySiteNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setArsite(rs.getString("arsite"));
			vo.setDa_pair(rs.getString("da_pair"));
			vo.setDkcap(rs.getString("dkcap"));
			vo.setState(rs.getString("state"));
			vo.setArray(rs.getString("array"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageArraySiteNodeTemp vo = (StorageArraySiteNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_arraysite(nodeid,ip,arsite,da_pair,dkcap,state,array,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getArsite());
		sql.append("','");
		sql.append(vo.getDa_pair());
		sql.append("','");
		sql.append(vo.getDkcap());
		sql.append("','");
		sql.append(vo.getState());
		sql.append("','");
		sql.append(vo.getArray());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageArraySiteNodeTemp> getStorageArraySiteNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

}
