package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.topology.model.NetSyslogNodeAlarmKey;

@SuppressWarnings("unchecked")
public class NetSyslogNodeAlarmKeyDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public NetSyslogNodeAlarmKeyDao() {
		super("nms_netsyslogalarmkey_node");
	}

	public List loadAll() {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_netsyslogalarmkey_node order by id desc");
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
		NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_netsyslogalarmkey_node set keywords='");
		sql.append(vo.getKeywords());
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
			rs = conn.executeQuery("select * from nms_netsyslogalarmkey_node where nodeid=" + id);
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

	public BaseVo findByIpaddress(String ip) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select n.id,n.nodeid,n.keywords from nms_netsyslogalarmkey_node n,topo_host_node t where n.nodeid=t.id and  t.ip_address='" + ip + "'");
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
		NetSyslogNodeAlarmKey vo = new NetSyslogNodeAlarmKey();
		try {
			vo.setId(rs.getLong("id") + "");
			vo.setNodeid(rs.getString("nodeid"));
			vo.setKeywords(rs.getString("keywords"));
			vo.setLevel(rs.getString("levels"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean updateAlarmAll(String bid, String keywords) {
		String sql = "update nms_netsyslogalarmkey_node set keywords = '" + bid + "' where keywords ='" + keywords + "'";
		return saveOrUpdate(sql);
	}

	public boolean save(BaseVo baseVo) {
		boolean result = false;
		NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_netsyslogalarmkey_node(nodeid,keywords) values('" + vo.getNodeid() + "','");
		sql.append(vo.getKeywords());
		sql.append("')");

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	/**
	 * 删除已知节点的syslog告警关键字信息
	 * 
	 * @param nodeid
	 * @return
	 */
	public boolean delete(String nodeid) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("delete from nms_netsyslogalarmkey_node where nodeid = '" + nodeid + "'");
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

	/**
	 * 保存告警关键字信息列表
	 * 
	 * @param alarmkeyDetailList
	 */
	public void save(List alarmkeyDetailList) {
		try {
			for (int i = 0; i < alarmkeyDetailList.size(); i++) {
				NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey) alarmkeyDetailList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into nms_netsyslogalarmkey_node(nodeid,keywords,levels) values('" + vo.getNodeid() + "','");
				sql.append(vo.getKeywords());
				sql.append("','");
				sql.append(vo.getLevel());
				sql.append("')");
				try {
					conn.addBatch(sql.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}
}
