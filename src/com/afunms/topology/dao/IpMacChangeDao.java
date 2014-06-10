package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.polling.om.IpMacChange;


@SuppressWarnings("unchecked")
public class IpMacChangeDao extends BaseDao implements DaoInterface {
	public IpMacChangeDao() {
		super("nms_ipmacchange");
	}

	public List findByCondition(String key, String value) {
		return findByCriteria("select * from nms_ipmacchange where " + key + "='" + value + "'");
	}

	public List loadIpMac() {
		return findByCriteria("select * from nms_ipmacchange order by ip_address");
	}

	public Hashtable loadMacIpHash() {
		Hashtable returnValue = new Hashtable();
		List ipmacList = null;
		try {
			ipmacList = findByCriteria("select * from nms_ipmacchange order by id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ipmacList != null && ipmacList.size() > 0) {
			for (int i = 0; i < ipmacList.size(); i++) {
				IpMacChange ipmacchange = (IpMacChange) ipmacList.get(i);
				returnValue.put(ipmacchange.getRelateipaddr() + ":" + ipmacchange.getIpaddress() + ":" + ipmacchange.getMac(), ipmacchange);
			}
		}
		return returnValue;

	}

	public IpMacChange loadIpMacChange(int id) {
		List ipmacList = findByCriteria("select * from nms_ipmacchange where id=" + id);
		if (ipmacList != null && ipmacList.size() > 0) {
			IpMacChange ipmacchange = (IpMacChange) ipmacList.get(0);
			return ipmacchange;

		}
		return null;
	}

	public boolean deleteByHostIp(String hostip) {
		String sql = "delete from nms_ipmacchange where relateipaddr='" + hostip + "'";
		return saveOrUpdate(sql);
	}

	public List loadIpMacByIP(String relateipaddr) {
		List ipmacList = findByCriteria("select * from nms_ipmacchange where relateipaddr='" + relateipaddr + "'");
		return ipmacList;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		IpMacChange vo = new IpMacChange();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = new Date();
			cc.setTime(rs.getTimestamp("collecttime").getTime());
			tempCal.setTime(cc);
			vo.setId(rs.getLong("id"));
			vo.setRelateipaddr(rs.getString("relateipaddr"));
			vo.setBak(rs.getString("bak"));
			vo.setCollecttime(tempCal);
			vo.setIfindex(rs.getString("ifindex"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setMac(rs.getString("mac"));
			vo.setChangetype(rs.getString("changetype"));
			vo.setDetail(rs.getString("detail"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo baseVo) {
		String sql = ""; 
		return saveOrUpdate(sql);
	}

	public boolean deleteall() {
		String sql = "delete from nms_ipmacchange";
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
