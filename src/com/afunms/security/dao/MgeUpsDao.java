package com.afunms.security.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.security.model.MgeUps;

@SuppressWarnings("unchecked")
public class MgeUpsDao extends BaseDao implements DaoInterface {

	public MgeUpsDao() {
		super("app_ups_node");
	}

	public List findByIP(String ipAddress) {
		return findByCriteria("select * from app_ups_node where ip_address='" + ipAddress + "'");
	}

	public Vector<SystemCollectEntity> getSystemcollectdataByResultSet(ResultSet rs) {
		Vector<SystemCollectEntity> retVector = new Vector<SystemCollectEntity>();
		try {
			while (rs.next()) {
				SystemCollectEntity systemcollectdata = new SystemCollectEntity();
				systemcollectdata.setBak(rs.getString("bak"));
				systemcollectdata.setCategory(rs.getString("category"));
				systemcollectdata.setChname(rs.getString("chname"));
				if (rs.getString("count") != null) {
					systemcollectdata.setCount(Long.parseLong(rs.getString("count")));
				} else {
					systemcollectdata.setCount(Long.parseLong("0"));
				}
				systemcollectdata.setEntity(rs.getString("entity"));
				systemcollectdata.setIpaddress(rs.getString("ipaddress"));
				systemcollectdata.setRestype(rs.getString("restype"));
				systemcollectdata.setSubentity(rs.getString("subentity"));
				systemcollectdata.setThevalue(rs.getString("thevalue"));
				systemcollectdata.setUnit(rs.getString("unit"));
				retVector.add(systemcollectdata);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retVector;
	}

	public List getUpsByBID(Vector bids) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( bid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or bid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + ")";
		}
		sql.append("select * from app_ups_node " + wstr);
		return findByCriteria(sql.toString());
	}

	/**
	 * 根据IP得到UPS的临时数据信息
	 * 
	 * @param ipaddress
	 * @return
	 */
	public Hashtable getUpsIpData(String ipaddress) {
		Hashtable retHash = new Hashtable();
		DBManager dbmanager = new DBManager();
		Hashtable inputhash = new Hashtable();
		Hashtable batteryhash = new Hashtable();
		Hashtable bypasshash = new Hashtable();
		Hashtable outputhash = new Hashtable();
		Hashtable statuehash = new Hashtable();
		Hashtable systemhash = new Hashtable();
		ResultSet rs = null;
		try {
			rs = dbmanager.executeQuery("select * from nms_ups_battery_data_temp where ipaddress = '" + ipaddress + "'");
			Vector batteryVector = getSystemcollectdataByResultSet(rs);
			batteryhash.put("battery", batteryVector);
			retHash.put("battery", batteryVector);
			// bypass
			rs = dbmanager.executeQuery("select * from nms_ups_bypass_data_temp where ipaddress = '" + ipaddress + "'");
			Vector bypassVector = getSystemcollectdataByResultSet(rs);
			bypasshash.put("bypass", bypassVector);
			retHash.put("bypass", bypassVector);
			// input
			rs = dbmanager.executeQuery("select * from nms_ups_input_data_temp where ipaddress = '" + ipaddress + "'");
			Vector inputVector = getSystemcollectdataByResultSet(rs);
			inputhash.put("input", inputVector);
			retHash.put("input", inputVector);
			// output
			rs = dbmanager.executeQuery("select * from nms_ups_output_data_temp where ipaddress = '" + ipaddress + "'");
			Vector outputVector = getSystemcollectdataByResultSet(rs);
			outputhash.put("output", outputVector);
			retHash.put("output", outputVector);
			// statue
			rs = dbmanager.executeQuery("select * from nms_ups_statue_data_temp where ipaddress = '" + ipaddress + "'");
			Vector statueVector = getSystemcollectdataByResultSet(rs);
			statuehash.put("statue", statueVector);
			retHash.put("statue", statueVector);
			// system
			rs = dbmanager.executeQuery("select * from nms_ups_systemgroup_data_temp where ipaddress = '" + ipaddress + "'");
			Vector systemVector = getSystemcollectdataByResultSet(rs);
			systemhash.put("system", systemVector);
			retHash.put("system", systemVector);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dbmanager.close();
		}
		return retHash;
	}

	public List loadByType(String type) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from app_ups_node where type='" + type + "' order by id");
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadByTypeAndSubtype(String type, String subtype) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from app_ups_node where type='" + type + "' and subtype='" + subtype + "' order by id");
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		MgeUps vo = new MgeUps();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("alias"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setLocation(rs.getString("location"));
			vo.setCommunity(rs.getString("community"));
			vo.setSysOid(rs.getString("sys_oid"));
			vo.setSysName(rs.getString("sys_name"));
			vo.setSysDescr(rs.getString("sys_descr"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setIsmanaged(rs.getString("ismanaged"));
			vo.setCollecttype(rs.getString("collecttype"));
			vo.setBid(rs.getString("bid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public List loadMonitorAir() {
		return findByCriteria("select * from app_ups_node where ismanaged=1 and type ='air' order by id");
	}

	public List loadMonitorUps() {
		return findByCriteria("select * from app_ups_node where ismanaged=1 and type ='ups' order by id");
	}

	public boolean save(BaseVo baseVo) {
		MgeUps vo = (MgeUps) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into app_ups_node(id,alias,location,ip_address,type,community,sys_oid,sys_name,sys_descr,ismanaged,bid,collecttype,subtype)values(");
		sql.append(vo.getId());
		sql.append(",'");
		sql.append(vo.getAlias());
		sql.append("','");
		sql.append(vo.getLocation());
		sql.append("','");
		sql.append(vo.getIpAddress());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getCommunity());
		sql.append("','");
		sql.append(vo.getSysOid());
		sql.append("','");
		sql.append(vo.getSysName());
		sql.append("','");
		sql.append(vo.getSysDescr());
		sql.append("','");
		sql.append(vo.getIsmanaged());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("','");
		sql.append(vo.getCollecttype());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("')");
		String ip = vo.getIpAddress();
		String allipstr = "";
		allipstr = SysUtil.doip(ip);
		CreateTableManager ctable = new CreateTableManager();
		ctable.createTable("ping", allipstr, "ping");// Ping

		ctable.createTable("pinghour", allipstr, "pinghour");// Ping

		ctable.createTable("pingday", allipstr, "pingday");// Ping
		if ("ups".equalsIgnoreCase(vo.getType())) {
			ctable.createTable("input", allipstr, "input");// input
			ctable.createTable("inputhour", allipstr, "inputhour");// input
			ctable.createTable("inputday", allipstr, "inputday");// input
			ctable.createTable("output", allipstr, "output");// output
			ctable.createTable("outputhour", allipstr, "outputhour");// output
			ctable.createTable("outputday", allipstr, "outputday");// output
		}
		conn.executeBatch();
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		MgeUps vo = (MgeUps) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update app_ups_node set alias='");
		sql.append(vo.getAlias());
		sql.append("',location='");
		sql.append(vo.getLocation());
		sql.append("',ismanaged='");
		sql.append(vo.getIsmanaged());
		sql.append("',community='");
		sql.append(vo.getCommunity());
		sql.append("',bid='");
		sql.append(vo.getBid());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	public boolean update(String id, String ismanaged) {
		StringBuffer sql = new StringBuffer();
		sql.append("update app_ups_node set ismanaged='");
		sql.append(ismanaged);
		sql.append("' where id=");
		sql.append(id);
		return saveOrUpdate(sql.toString());
	}
}