package com.afunms.temp.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.IpNodeTemp;

public class IpTempDao extends BaseDao implements DaoInterface {

	public IpTempDao() {
		super("topo_ipalias");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from topo_ipalias where ipaddress='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		IpNodeTemp vo = new IpNodeTemp();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setAliasip(rs.getString("aliasip"));
			vo.setDescr(rs.getString("descr"));
			vo.setIndexs(rs.getString("indexs"));
			vo.setSpeeds(rs.getString("speeds"));
			vo.setTypes(rs.getString("types"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		return false;

	}

	public boolean update(BaseVo vo) {
		return false;
	}

}
