package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageArrayNodeTemp;

public class StorageArrayTempDao extends BaseDao implements DaoInterface {

	public StorageArrayTempDao() {
		super("nms_storage_array");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_array where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageArrayNodeTemp vo = new StorageArrayNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setArray(rs.getString("array"));
			vo.setState(rs.getString("state"));
			vo.setData(rs.getString("data"));
			vo.setRaidtype(rs.getString("raidtype"));
			vo.setArsite(rs.getString("arsite"));
			vo.setRank(rs.getString("rank"));
			vo.setDa_pair(rs.getString("da_pair"));
			vo.setDdmcap(rs.getString("ddmcap"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageArrayNodeTemp vo = (StorageArrayNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_array(nodeid,ip,array,state,data,raidtype,arsite,rank,da_pair,ddmcap,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getArray());
		sql.append("','");
		sql.append(vo.getState());
		sql.append("','");
		sql.append(vo.getData());
		sql.append("','");
		sql.append(vo.getRaidtype());
		sql.append("','");
		sql.append(vo.getArsite());
		sql.append("','");
		sql.append(vo.getRank());
		sql.append("','");
		sql.append(vo.getDa_pair());
		sql.append("','");
		sql.append(vo.getDdmcap());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageArrayNodeTemp> getStorageArrayNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

}
