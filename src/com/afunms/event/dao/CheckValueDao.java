package com.afunms.event.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.event.model.CheckValue;

@SuppressWarnings("unchecked")
public class CheckValueDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CheckValueDao() {
		super("nms_checkvalue");
	}

	public boolean deleteByName(String indicatorsName) {
		boolean flag = true;
		String sql = "delete from nms_checkvalue where indicators_name='" + indicatorsName + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean deleteByNodeType(String nodeid, String type) {
		boolean flag = true;
		String sql = "delete from nms_checkvalue where nodeid='" + nodeid + "' and type ='" + type + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public boolean deleteCheckValue(String nodeId, String type, String subtype, String name) {
		return saveOrUpdate("delete from nms_checkvalue where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='" + name + "'");
	}

	public boolean deleteCheckValue(String nodeId, String type, String subtype, String name, String sindex) {
		return saveOrUpdate("delete from nms_checkvalue where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='" + name
				+ "' and sindex='" + sindex + "'");
	}

	public boolean empty() {
		String sql = "delete from nms_checkvalue";
		return saveOrUpdate(sql);
	}

	public List<CheckValue> findCheckValue(String nodeId) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			rs = conn.executeQuery("select * from nms_checkvalue where nodeid='" + nodeId + "'");
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CheckValue> findCheckValue(String nodeId, String type) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			rs = conn.executeQuery("select * from nms_checkvalue where nodeid='" + nodeId + "' and type='" + type + "'");
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CheckValue> findCheckValue(String nodeId, String type, String subtype, String name) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			rs = conn.executeQuery("select * from nms_checkvalue where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='" + name
					+ "'");
			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CheckValue> findCheckValue(String nodeId, String type, String subtype, String name, String sindex) {
		List<CheckValue> list = new ArrayList<CheckValue>();
		try {
			if (sindex != null && !"".equals(sindex) && !"null".equals(sindex)) {
				rs = conn.executeQuery("select * from nms_checkvalue where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
						+ name + "' and sindex='" + sindex + "'");
			} else {
				rs = conn.executeQuery("select * from nms_checkvalue where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
						+ name + "'");
			}

			while (rs.next()) {
				list.add((CheckValue) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_checkvalue order by name");
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

	public List loadByWhere(String where) {
		return findByCondition(where);
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CheckValue vo = new CheckValue();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setContent(rs.getString("content"));
			vo.setIndicatorsName(rs.getString("indicators_name"));
			vo.setSindex(rs.getString("sindex"));
			vo.setAlarmlevel(rs.getInt("alarmlevel"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setTopoShow(rs.getString("topo_show"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		CheckValue vo = (CheckValue) baseVo;
		// 先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkvalue(nodeid,indicators_name,sindex,type,subtype,alarmlevel,content,thevalue,topo_show)values(");
		sql.append("'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIndicatorsName());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',");
		sql.append(vo.getAlarmlevel());
		sql.append(",'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getTopoShow());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean savecheckvalue(BaseVo baseVo) {
		CheckValue vo = (CheckValue) baseVo;
		// 先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkvalue(nodeid,indicators_name,sindex,type,subtype,alarmlevel,content,thevalue,topo_show)values(");
		sql.append("'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIndicatorsName());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("',");
		sql.append(vo.getAlarmlevel());
		sql.append(",'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getTopoShow());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		return true;
	}
}
