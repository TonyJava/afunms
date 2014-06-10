package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.HostServiceGroup;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

@SuppressWarnings("unchecked")
public class HostServiceGroupDao extends BaseDao implements DaoInterface {

	public HostServiceGroupDao() {
		super("nms_host_service_group");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		HostServiceGroup hostServiceGroup = new HostServiceGroup();
		try {
			hostServiceGroup.setId(rs.getInt("id"));
			hostServiceGroup.setIpaddress(rs.getString("ipaddress"));
			hostServiceGroup.setName(rs.getString("name"));
			hostServiceGroup.setNodeid(rs.getString("nodeid"));
			hostServiceGroup.setMon_flag(rs.getString("mon_flag"));
			hostServiceGroup.setAlarm_level(rs.getString("alarm_level"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hostServiceGroup;
	}

	public boolean save(BaseVo vo) {
		HostServiceGroup hostServiceGroup = (HostServiceGroup) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_host_service_group(id,ipaddress,name,nodeid,mon_flag,alarm_level)values('");
		sql.append(hostServiceGroup.getId());
		sql.append("','");
		sql.append(hostServiceGroup.getIpaddress());
		sql.append("','");
		sql.append(hostServiceGroup.getName());
		sql.append("','");
		sql.append(hostServiceGroup.getNodeid());
		sql.append("','");
		sql.append(hostServiceGroup.getMon_flag());
		sql.append("','");
		sql.append(hostServiceGroup.getAlarm_level());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public synchronized int getNextId() {
		return super.getNextID();
	}

	public List findByIp(String ipaddress) {
		String sql = " where ipaddress='" + ipaddress + "'";
		return findByCondition(sql);
	}

	public boolean update(BaseVo vo) {
		HostServiceGroup hostServiceGroup = (HostServiceGroup) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_host_service_group set ipaddress='");
		sql.append(hostServiceGroup.getIpaddress());
		sql.append("',name='");
		sql.append(hostServiceGroup.getName());
		sql.append("',nodeid='");
		sql.append(hostServiceGroup.getNodeid());
		sql.append("',mon_flag='");
		sql.append(hostServiceGroup.getMon_flag());
		sql.append("',alarm_level='");
		sql.append(hostServiceGroup.getAlarm_level());
		sql.append("' where id=");
		sql.append(hostServiceGroup.getId());
		return saveOrUpdate(sql.toString());
	}

}