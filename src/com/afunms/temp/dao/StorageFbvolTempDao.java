package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageFbvolNodeTemp;

public class StorageFbvolTempDao extends BaseDao implements DaoInterface {

	public StorageFbvolTempDao() {
		super("nms_storage_fbvol");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_fbvol where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageFbvolNodeTemp vo = new StorageFbvolNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setName(rs.getString("name"));
			vo.setFbvol_id(rs.getString("fbvol_id"));
			vo.setAccstate(rs.getString("accstate"));
			vo.setDatastate(rs.getString("datastate"));
			vo.setConfigstate(rs.getString("configstate"));
			vo.setDevicemtm(rs.getString("devicemtm"));
			vo.setDatatype(rs.getString("datatype"));
			vo.setExtpool(rs.getString("extpool"));
			vo.setCap_2_30B(rs.getString("cap_2_30B"));
			vo.setCap_10_9B(rs.getString("cap_10_9B"));
			vo.setCap_blocks(rs.getString("cap_blocks"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageFbvolNodeTemp vo = (StorageFbvolNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql
				.append("insert into nms_storage_fbvol(nodeid,ip,name,fbvol_id,accstate,datastate,configstate,devicemtm,datatype,extpool,cap_2_30B,cap_10_9B,cap_blocks,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getFbvol_id());
		sql.append("','");
		sql.append(vo.getAccstate());
		sql.append("','");
		sql.append(vo.getDatastate());
		sql.append("','");
		sql.append(vo.getConfigstate());
		sql.append("','");
		sql.append(vo.getDevicemtm());
		sql.append("','");
		sql.append(vo.getDatatype());
		sql.append("','");
		sql.append(vo.getExtpool());
		sql.append("','");
		sql.append(vo.getCap_2_30B());
		sql.append("','");
		sql.append(vo.getCap_10_9B());
		sql.append("','");
		sql.append(vo.getCap_blocks());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageFbvolNodeTemp> getStorageFbvolNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

	@SuppressWarnings("unchecked")
	public List<StorageFbvolNodeTemp> getStorageFbvolNodeTemp(String nodeid, String type, String subtype, List<String> idList) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		if (idList != null && idList.size() > 0) {
			sql.append(" and (");
			for (int i = 0; i < idList.size(); i++) {
				if (i != 0) {
					sql.append(" or");
				}
				sql.append(" fbvol_id='" + idList.get(i) + "'");
			}
			sql.append(" )");
		}
		return findByCondition(sql.toString());
	}

}
