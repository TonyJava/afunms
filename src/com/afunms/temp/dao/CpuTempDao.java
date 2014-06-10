package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class CpuTempDao extends BaseDao implements DaoInterface {

	public CpuTempDao() {
		super("nms_cpu_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_cpu_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_cpu_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_cpu_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<NodeTemp> getNodeTempList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

	@SuppressWarnings("unchecked")
	public List<NodeTemp> getCurrPerCpuList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and sindex!='avg'");
		return findByCondition(sql.toString());
	}

	/**
	 * 得到cpuperflist cpu性能信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subype
	 * @return
	 */
	public List getCpuPerListInfo(String nodeid, String type, String subtype) {
		List retList = new ArrayList();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_other_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = 'cpuperflist'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			Hashtable cpuperHash = new Hashtable();
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				cpuperHash.put(subentity, thevalue);
			}
			retList.add(cpuperHash);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbManager.close();
		}
		return retList;
	}

	public Vector getCpuInfo(String nodeid, String type, String subtype) {
		Vector retVector = new Vector();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_cpu_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = 'CPU'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			CpuCollectEntity cpUcollectdata = new CpuCollectEntity();
			while (rs.next()) {
				String thevalue = rs.getString("thevalue");
				cpUcollectdata.setThevalue(thevalue);
			}
			retVector.add(cpUcollectdata);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbManager.close();
		}
		return retVector;
	}

	public List<NodeTemp> getPerCpuList(String nodeids) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid in ('");
		sql.append(nodeids);
		sql.append("')");
		return findByCondition(sql.toString());
	}

	/**
	 * 得到每个节点对应的平均cpu利用率
	 * 
	 * @param monitorNodelist
	 * @return
	 */
	public List<NodeTemp> getCurrPerCpuList(List monitorNodelist) {
		List<NodeTemp> retList = new ArrayList<NodeTemp>();
		StringBuffer sql = new StringBuffer();
		sql.append("select nodeid,ip,type,subtype, avg(thevalue) avg_thevalue from nms_cpu_data_temp where nodeid in ('");
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
		sql.append("') group by nodeid");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				NodeTemp vo = new NodeTemp();
				vo.setNodeid(rs.getString("nodeid"));
				vo.setIp(rs.getString("ip"));
				vo.setType(rs.getString("type"));
				vo.setSubtype(rs.getString("subtype"));
				vo.setThevalue(rs.getString("avg_thevalue"));
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
