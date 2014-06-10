package com.afunms.temp.dao;

import java.sql.ResultSet;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.temp.model.ArpNodeTemp;

public class ArpTempDao extends BaseDao implements DaoInterface {

	public ArpTempDao() {
		super("ipmac");
	}

	/**
	 * É¾³ýÒ»Ìõ¼ÇÂ¼
	 */
	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from ipmac where relateipaddr='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			SysLogger.error("Error in ArpTempDao.deleteByIp(String ip)", ex);
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		ArpNodeTemp vo = new ArpNodeTemp();
		try {
			vo.setId(rs.getInt("id"));
			vo.setRelateipaddr(rs.getString("relateipaddr"));
			vo.setIfindex(rs.getString("ifindex"));
			vo.setIfband(rs.getString("ifband"));
			vo.setIfsms(rs.getString("ifsms"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setMac(rs.getString("mac"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			SysLogger.error("ArpTempDao.loadFromRS()", e);
		}
		return vo;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

}
