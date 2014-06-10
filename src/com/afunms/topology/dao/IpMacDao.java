package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.polling.om.IpMac;

@SuppressWarnings("unchecked")
public class IpMacDao extends BaseDao implements DaoInterface {
	public IpMacDao() {
		super("ipmac");
	}

	public List findByCondition(String key, String value) {
		return findByCriteria("select * from ipmac where " + key + "='" + value + "'");
	}

	public boolean deleteByHostIp(String hostip) {
		String sql = "delete from ipmac where relateipaddr='" + hostip + "'";
		return saveOrUpdate(sql);
	}

	public List loadIpMac() {
		return findByCriteria("select * from ipmac order by ip_address");
	}

	public IpMac loadIpMac(int id) {
		List ipmacList = findByCriteria("select * from ipmac where id=" + id);
		if (ipmacList != null && ipmacList.size() > 0) {
			IpMac ipmac = (IpMac) ipmacList.get(0);
			return ipmac;

		}
		return null;
	}

	public List loadIpMacByIP(String relateipaddr) {
		List ipmacList = findByCriteria("select * from ipmac where relateipaddr='" + relateipaddr + "'");
		return ipmacList;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		IpMac vo = new IpMac();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = new Date();
			cc.setTime(rs.getTimestamp("collecttime").getTime());
			tempCal.setTime(cc);
			vo.setId(rs.getLong("id"));
			vo.setRelateipaddr(rs.getString("relateipaddr"));
			vo.setBak(rs.getString("bak"));
			vo.setCollecttime(tempCal);
			vo.setIfband(rs.getString("ifband"));
			vo.setIfindex(rs.getString("ifindex"));
			vo.setIfsms(rs.getString("ifsms"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setMac(rs.getString("mac"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo baseVo) {
		IpMac vo = (IpMac) baseVo;
		String sql = "update ipmac set ifband='" + vo.getIfband() + "',ifsms='" + vo.getIfsms() + "' where id=" + vo.getId();
		return saveOrUpdate(sql);
	}

	public boolean deleteall() {
		String sql = "delete from ipmac";
		return saveOrUpdate(sql);
	}

	public List<String> getIfIps() {
		List<String> allIps = new ArrayList<String>();
		try {
			rs = conn.executeQuery("select a.ip_address from topo_interface a,topo_host_node b where a.node_id=b.id and b.category<4 and a.ip_address<>'' order by a.id");
			while (rs.next()) {
				String ips = rs.getString("ip_address");
				allIps.add(ips);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allIps;
	}

	public String loadOneColFromRS(ResultSet rs) {
		return "";
	}
}
