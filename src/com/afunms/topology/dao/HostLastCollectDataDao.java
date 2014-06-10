
package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.polling.snmp.Hostlastcollectdata;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class HostLastCollectDataDao extends BaseDao implements DaoInterface {
	public HostLastCollectDataDao() {
		super("hostlastcollectdata");
	}

	
	public List findByCondition(String key, String value) {
		return findByCriteria("select * from hostlastcollectdata where " + key + "='" + value + "'");
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Hostlastcollectdata vo = new Hostlastcollectdata();
		try {
			vo.setId(new Long(rs.getInt("id")));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setRestype(rs.getString("restype"));
			vo.setCategory(rs.getString("category"));
			vo.setEntity(rs.getString("entity"));
			vo.setSubentity(rs.getString("subentity"));
			vo.setThevalue(rs.getString("thevalue"));
			Date timestamp = rs.getTimestamp("collecttime");
			Calendar date = Calendar.getInstance();
			date.setTime(timestamp);
			vo.setCollecttime(date);
			vo.setUnit(rs.getString("unit"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo baseVo) {
		HostNode vo = (HostNode) baseVo;
		int managed = 0;
		if (vo.isManaged())
			managed = 1;
		String sql = "update topo_host_node set alias='" + vo.getAlias() + "',managed=" + managed + " where id=" + vo.getId();
		return saveOrUpdate(sql);
	}

	public String loadOneColFromRS(ResultSet rs) {
		return "";
	}
}
