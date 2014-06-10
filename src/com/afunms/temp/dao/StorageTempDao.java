package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageNodeTemp;

public class StorageTempDao extends BaseDao implements DaoInterface {

	public StorageTempDao() {
		super("nms_storage_data_temp");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageNodeTemp vo = new StorageNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setType(rs.getString("type"));
			vo.setName(rs.getString("name"));
			vo.setStype(rs.getString("stype"));
			vo.setCap(rs.getString("cap"));
			vo.setStorageindex(rs.getString("storageindex"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageNodeTemp vo = (StorageNodeTemp) baseVo;
		if (vo.getName() == null)
			vo.setName("");
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_data_temp(nodeid,ip,type,subtype,name,stype,cap,storageindex,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getName().replace("\\", "/"));
		sql.append("','");
		sql.append(vo.getStype());
		sql.append("','");
		sql.append(vo.getCap());
		sql.append("','");
		sql.append(vo.getStorageindex());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageNodeTemp> getStorageNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

}
