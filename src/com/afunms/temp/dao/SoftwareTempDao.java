package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.SoftwareNodeTemp;

public class SoftwareTempDao extends BaseDao implements DaoInterface {

	public SoftwareTempDao() {
		super("nms_software_data_temp");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_software_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		SoftwareNodeTemp vo = new SoftwareNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setType(rs.getString("type"));
			vo.setInsdate(rs.getString("insdate"));
			vo.setName(rs.getString("name"));
			vo.setStype(rs.getString("stype"));
			vo.setSwid(rs.getString("swid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		SoftwareNodeTemp vo = (SoftwareNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_software_data_temp(nodeid,ip,type,subtype,insdate,name,stype,swid,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getInsdate());
		sql.append("','");
		sql.append(vo.getName().replace("'", ""));
		sql.append("','");
		sql.append(vo.getStype());
		sql.append("','");
		sql.append(vo.getSwid());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<SoftwareNodeTemp> getSoftwareNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

}
