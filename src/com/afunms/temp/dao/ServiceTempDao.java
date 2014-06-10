package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.temp.model.ServiceNodeTemp;

public class ServiceTempDao extends BaseDao implements DaoInterface {

	public ServiceTempDao() {
		super("nms_sercice_data_temp");
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_sercice_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		ServiceNodeTemp vo = new ServiceNodeTemp();
		try {
			vo.setIp(rs.getString("ip"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setType(rs.getString("type"));
			vo.setName(rs.getString("name"));
			vo.setInstate(rs.getString("instate"));
			vo.setOpstate(rs.getString("opstate"));
			vo.setPaused(rs.getString("paused"));
			vo.setUninst(rs.getString("uninst"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setDescription(rs.getString("description"));
			vo.setStartMode(rs.getString("startMode"));
			vo.setPathName(rs.getString("pathName"));
			vo.setServiceType(rs.getString("serviceType"));
			vo.setPid(rs.getString("pid"));
			vo.setGroupstr(rs.getString("groupstr"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		ServiceNodeTemp vo = (ServiceNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_sercice_data_temp(nodeid,ip,type,subtype,name,instate,opstate,paused,uninst,collecttime)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',\"");
		sql.append(vo.getName());
		sql.append("\",'");
		sql.append(vo.getInstate());
		sql.append("','");
		sql.append(vo.getOpstate());
		sql.append("','");
		sql.append(vo.getPaused());
		sql.append("','");
		sql.append(vo.getUninst());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<ServiceNodeTemp> getNodeTempList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

	/**
	 * windows wmi
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<Hashtable<String, String>> getServicelistInfo(String nodeid, String type, String subtype) {
		List<Hashtable<String, String>> servicelist = new ArrayList<Hashtable<String, String>>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_sercice_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				String name = rs.getString("name");
				String instate = rs.getString("instate");
				String startMode = rs.getString("startMode");
				String pathName = rs.getString("pathName");
				String description = rs.getString("description");
				String serviceType = rs.getString("serviceType");
				String pid = rs.getString("pid");
				String groupstr = rs.getString("groupstr");
				hashtable.put("StartMode", startMode);
				hashtable.put("PathName", pathName);
				hashtable.put("Description", description);
				hashtable.put("ServiceType", serviceType);
				hashtable.put("State", instate);
				hashtable.put("DisplayName", name);
				hashtable.put("pid", pid);
				hashtable.put("groupstr", groupstr);
				servicelist.add(hashtable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbManager.close();
		}
		return servicelist;
	}

	/**
	 * windows snmp
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<Hashtable<String, String>> getServicelistInfoAll(String nodeid, String type, String subtype) {
		List<Hashtable<String, String>> servicelist = new ArrayList<Hashtable<String, String>>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_sercice_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				String name = rs.getString("name");
				String instate = rs.getString("instate");
				String opstate = rs.getString("opstate");
				String paused = rs.getString("paused");
				String uninst = rs.getString("uninst");
				String collecttime = rs.getString("collecttime");
				String startMode = rs.getString("startMode");
				String pathName = rs.getString("pathName");
				String description = rs.getString("description");
				String serviceType = rs.getString("serviceType");
				String pid = rs.getString("pid");
				String groupstr = rs.getString("groupstr");
				if (name == null) {
					name = "";
				}
				if (instate == null) {
					instate = "";
				}
				if (opstate == null) {
					opstate = "";
				}
				if (paused == null) {
					paused = "";
				}
				if (uninst == null) {
					uninst = "";
				}
				if (collecttime == null) {
					collecttime = "";
				}
				if (startMode == null) {
					startMode = "";
				}
				if (pathName == null) {
					pathName = "";
				}
				if (description == null) {
					description = "";
				}
				if (serviceType == null) {
					serviceType = "";
				}
				if (pid == null) {
					pid = "";
				}
				if (groupstr == null) {
					groupstr = "";
				}
				hashtable.put("name", name);
				hashtable.put("instate", instate);
				hashtable.put("opstate", opstate);
				hashtable.put("paused", paused);
				hashtable.put("uninst", uninst);
				hashtable.put("collecttime", collecttime);
				hashtable.put("StartMode", startMode);
				hashtable.put("PathName", pathName);
				hashtable.put("Description", description);
				hashtable.put("ServiceType", serviceType);
				hashtable.put("State", instate);
				hashtable.put("DisplayName", name);
				hashtable.put("pid", pid);
				hashtable.put("groupstr", groupstr);
				servicelist.add(hashtable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbManager.close();
		}
		return servicelist;
	}
}
