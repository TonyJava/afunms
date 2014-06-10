package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class PingTempDao extends BaseDao implements DaoInterface {

	public PingTempDao() {
		super("nms_ping_data_temp");
	}

	/**
	 * É¾³ýÒ»Ìõ¼ÇÂ¼
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_ping_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_ping_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		NodeTemp vo = new NodeTemp();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setIp(rs.getString("ip"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setEntity(rs.getString("entity"));
			vo.setSubentity(rs.getString("subentity"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setChname(rs.getString("chname"));
			vo.setRestype(rs.getString("restype"));
			vo.setSindex(rs.getString("sindex"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setUnit(rs.getString("unit"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		NodeTemp vo = (NodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_ping_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getEntity());
		sql.append("','");
		sql.append(vo.getSubentity());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getChname());
		sql.append("','");
		sql.append(vo.getRestype());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("','");
		sql.append(vo.getUnit());
		sql.append("','");
		sql.append(vo.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public List<NodeTemp> getNodeTempList(String nodeid, String type, String subtype, String[] subentities) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		if (subentities != null && subentities.length > 0) {
			for (int i = 0; i < subentities.length; i++) {
				if (i == 0) {
					sql.append(" and (");
				} else {
					sql.append(" or");
				}
				sql.append(" subentity='" + subentities[i] + "'");
			}
			sql.append(" )");
		}
		return findByCondition(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public Vector getPingInfo(String nodeid, String type, String subtype) {
		Vector retVector = new Vector();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_ping_data_temp where nodeid = '");
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
				PingCollectEntity pingcollectdata = new PingCollectEntity();
				pingcollectdata.setCategory(rs.getString("entity"));
				pingcollectdata.setEntity(rs.getString("subentity"));
				pingcollectdata.setSubentity(rs.getString("sindex"));
				pingcollectdata.setThevalue(rs.getString("thevalue"));
				String collecttime = rs.getString("collecttime");
				Date date = sdf.parse(collecttime);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				pingcollectdata.setCollecttime(calendar);
				retVector.add(pingcollectdata);
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
		return retVector;
	}

	public PingCollectEntity getPingInfo(String category, String subtype) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_dominoping_realtime where ");
		sqlBuffer.append(" category = '");
		sqlBuffer.append(category);
		sqlBuffer.append("' and subentity = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("'");
		DBManager dbManager = new DBManager();
		PingCollectEntity pingcollectdata = new PingCollectEntity();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				String value = rs.getString("thevalue");
				if (("null").equals(value) || value.equals("")) {
					value = "0";
				}
				pingcollectdata.setThevalue(value);
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
		return pingcollectdata;
	}

	/**
	 * 
	 * @param monitorNodelist
	 * @return
	 */
	public List<NodeTemp> getNodeTempList(List monitorNodelist) {
		List<NodeTemp> retList = new ArrayList<NodeTemp>();
		StringBuffer sql = new StringBuffer();
		sql.append("select nodeid,ip,type,subtype,thevalue  from nms_ping_data_temp where nodeid in ('");
		if (monitorNodelist != null && monitorNodelist.size() > 0) {
			for (int i = 0; i < monitorNodelist.size(); i++) {
				Object obj = monitorNodelist.get(i);
				if (obj instanceof HostNode) {
					HostNode hostNode = (HostNode) obj;
					sql.append(hostNode.getId());
					if (i != monitorNodelist.size() - 1) {
						sql.append("','");
					}
				}
			}
		}
		sql.append("') and subentity='Utilization' group by nodeid");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				NodeTemp vo = new NodeTemp();
				vo.setNodeid(rs.getString("nodeid"));
				vo.setIp(rs.getString("ip"));
				vo.setType(rs.getString("type"));
				vo.setSubtype(rs.getString("subtype"));
				vo.setThevalue(rs.getString("thevalue"));
				retList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return retList;
	}

	public List<NodeTemp> getNodeTempResList(List monitorNodelist) {
		List<NodeTemp> retList = new ArrayList<NodeTemp>();
		StringBuffer sql = new StringBuffer();
		sql.append("select nodeid,ip,type,subtype,thevalue  from nms_ping_data_temp where nodeid in ('");
		if (monitorNodelist != null && monitorNodelist.size() > 0) {
			for (int i = 0; i < monitorNodelist.size(); i++) {
				Object obj = monitorNodelist.get(i);
				if (obj instanceof HostNode) {
					HostNode hostNode = (HostNode) obj;
					sql.append(hostNode.getId());
					if (i != monitorNodelist.size() - 1) {
						sql.append("','");
					}
				}
			}
		}
		sql.append("') and subentity='ResponseTime' group by nodeid");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				NodeTemp vo = new NodeTemp();
				vo.setNodeid(rs.getString("nodeid"));
				vo.setIp(rs.getString("ip"));
				vo.setType(rs.getString("type"));
				vo.setSubtype(rs.getString("subtype"));
				vo.setThevalue(rs.getString("thevalue"));
				retList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return retList;
	}
}
