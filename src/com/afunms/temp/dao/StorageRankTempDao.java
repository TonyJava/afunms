package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.StorageRankNodeTemp;

public class StorageRankTempDao extends BaseDao implements DaoInterface {

	public StorageRankTempDao() {
		super("nms_storage_rank");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_storage_rank where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		StorageRankNodeTemp vo = new StorageRankNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setRank_id(rs.getString("rank_id"));
			vo.setGroup(rs.getString("rank_group"));
			vo.setState(rs.getString("state"));
			vo.setDatastate(rs.getString("datastate"));
			vo.setArray(rs.getString("array"));
			vo.setRaidtype(rs.getString("raidtype"));
			vo.setExtpoolid(rs.getString("extpoolid"));
			vo.setStgtype(rs.getString("stgtype"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StorageRankNodeTemp vo = (StorageRankNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_storage_rank(nodeid,ip,rank_id,rank_group,state,datastate,array,raidtype,extpoolid,stgtype,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getRank_id());
		sql.append("','");
		sql.append(vo.getGroup());
		sql.append("','");
		sql.append(vo.getState());
		sql.append("','");
		sql.append(vo.getDatastate());
		sql.append("','");
		sql.append(vo.getArray());
		sql.append("','");
		sql.append(vo.getRaidtype());
		sql.append("','");
		sql.append(vo.getExtpoolid());
		sql.append("','");
		sql.append(vo.getStgtype());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<StorageRankNodeTemp> getStorageRankNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "'");
		return findByCondition(sql.toString());
	}

}
