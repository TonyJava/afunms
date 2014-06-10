package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.temp.model.NodeTemp;

public class SystemTempDao extends BaseDao implements DaoInterface {

	public SystemTempDao() {
		super("nms_system_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_system_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_system_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_system_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	@SuppressWarnings("unchecked")
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

	/**
	 * 得到系统信息
	 * 
	 * @param nodeid
	 * @return
	 */
	public Vector<SystemCollectEntity> getSystemInfo(String nodeid, String type, String subtype) {
		Vector<SystemCollectEntity> retVector = new Vector<SystemCollectEntity>();
		DBManager dbManager = new DBManager();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_system_data_temp where nodeid = '");
		sql.append(nodeid);
		sql.append("' and type = '");
		sql.append(type);
		sql.append("' and subtype = '");
		sql.append(subtype);
		sql.append("'");
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				SystemCollectEntity systemcollectdata = new SystemCollectEntity();
				systemcollectdata.setEntity(rs.getString("entity"));
				systemcollectdata.setSubentity(rs.getString("subentity"));
				systemcollectdata.setThevalue(rs.getString("thevalue"));
				retVector.add(systemcollectdata);
			}
		} catch (SQLException e) {
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

}
