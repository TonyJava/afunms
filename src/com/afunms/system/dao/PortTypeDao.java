package com.afunms.system.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.vo.PortTypeVo;

@SuppressWarnings("unchecked")
public class PortTypeDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public PortTypeDao() {
		super("nms_porttype");
	}

	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_porttype order by id");
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

	public List loadByIp(String ip) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_porttype where ipaddress='" + ip + "' order by id");
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

	public List loadByIpAndName(String ip, String name) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_keyfile where ipaddress='" + ip + "' and filename = '" + name + "' order by id");
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

	public boolean save(BaseVo basevo) {
		PortTypeVo vo = (PortTypeVo) basevo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_porttype(typeid,chname,bak)values(");
		sql.append(vo.getTypeid());
		sql.append(",'");
		sql.append(vo.getChname());
		sql.append("','");
		sql.append(vo.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo basevo) {
		PortTypeVo vo = (PortTypeVo) basevo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_porttype set typeid=");
		sql.append(vo.getTypeid());
		sql.append(",chname='");
		sql.append(vo.getChname());
		sql.append("',bak='");
		sql.append(vo.getBak());
		sql.append("' where id=");
		sql.append(vo.getId());

		try {
			SysLogger.info(sql.toString());
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

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_porttype where id=" + id[i]);
			}
			conn.executeBatch();
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
			rs = conn.executeQuery("select * from nms_porttype where id=" + id);
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		PortTypeVo vo = new PortTypeVo();
		try {
			vo.setId(rs.getInt("id"));
			vo.setTypeid(rs.getInt("typeid"));
			vo.setChname(rs.getString("chname"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}
}
