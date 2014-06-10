package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.NodeTemp;

@SuppressWarnings("unchecked")
public class ENVTempDao extends BaseDao implements DaoInterface {

	public ENVTempDao() {
		super("nms_envir_data_temp");
	}

	/**
	 * É¾³ýÒ»Ìõ¼ÇÂ¼
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_envir_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_envir_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteById(String nodeid) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_envir_data_temp where nodeid='" + nodeid + "'");
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
		sql.append("insert into nms_envir_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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
	public List<NodeTemp> getCurrPerFlashList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and sindex ='avg'");
		return findByCondition(sql.toString());
	}

	public List<NodeTemp> getCurrENVList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' ");
		return findByCondition(sql.toString());
	}

	public List<NodeTemp> getCurrPerFlashList(String ip) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where ip='" + ip + "' and sindex <> 'avg'");
		return findByCondition(sql.toString());
	}

	public List<NodeTemp> getCurrFlashListInfo(String nodeid, String type, String subtype, String sindex) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and sindex ='" + sindex + "'");
		return findByCondition(sql.toString());
	}

	public List getCurrFlashSindex(String nodeid, String type, String subtype) throws SQLException {
		List sindexsList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(" select sindex from nms_envir_data_temp t where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' group by sindex");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				String sindex = rs.getString("sindex");
				sindexsList.add(sindex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
			rs.close();
		}
		return sindexsList;
	}
}
