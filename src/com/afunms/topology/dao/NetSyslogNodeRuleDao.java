package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.topology.model.NetSyslogNodeRule;

@SuppressWarnings("unchecked")
public class NetSyslogNodeRuleDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public NetSyslogNodeRuleDao() {
		super("nms_netsyslogrule_node");
	}

	public List loadAll() {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_netsyslogrule_node order by id desc");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public boolean update(BaseVo baseVo) {
		boolean result = false;
		NetSyslogNodeRule vo = (NetSyslogNodeRule) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_netsyslogrule_node set facility='");
		sql.append(vo.getFacility());
		sql.append("' where id=");
		sql.append(vo.getId());

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_netsyslogrule_node where nodeid='" + id + "'");
			if (rs.next())
				vo = loadFromRS(rs);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean deleteByNodeID(String id) {
		boolean flag = false;
		try {
			conn.executeUpdate("delete from nms_netsyslogrule_node where nodeid='" + id + "'");
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public BaseVo findByIpaddress(String ip) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select n.id,n.nodeid,n.facility from nms_netsyslogrule_node n,topo_host_node t where n.nodeid=t.id and  t.ip_address='" + ip + "'");
			if (rs.next())
				vo = loadFromRS(rs);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public String findIdByIpaddress(String ip) {
		String strId = "";
		try {
			rs = conn.executeQuery("select id from topo_host_node where ip_address='" + ip + "'");
			if (rs.next())
				strId = rs.getString("id");
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strId;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		NetSyslogNodeRule vo = new NetSyslogNodeRule();
		try {
			vo.setId(rs.getLong("id") + "");
			vo.setNodeid(rs.getString("nodeid"));
			vo.setFacility(rs.getString("facility"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean updateAlarmAll(String bid, String hostid) {
		String sql = "update nms_netsyslogrule_node set facility = '" + bid + "' where nodeid ='" + hostid + "'";
		return saveOrUpdate(sql);
	}

	public boolean save(BaseVo baseVo) {
		boolean result = false;
		NetSyslogNodeRule vo = (NetSyslogNodeRule) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_netsyslogrule_node(nodeid,facility) values('" + vo.getNodeid() + "','");
		sql.append(vo.getFacility());
		sql.append("')");

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}
}
