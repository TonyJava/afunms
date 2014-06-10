package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.event.model.CheckEvent;

@SuppressWarnings("unchecked")
public class CheckEventDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CheckEventDao() {
		super("nms_checkevent");
	}

	@Override
	public boolean delete(String[] id) {
		return true;
	}

	public boolean deleteByNodeType(String nodeid, String type) {
		boolean flag = true;
		String sql = "delete from nms_checkevent where nodeid='" + nodeid + "' and type ='" + type + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean deleteCheckEvent() {
		return saveOrUpdate("delete from nms_checkevent where 1=1");
	}

	public boolean deleteCheckEvent(String nodeId, String type, String subtype) {
		return saveOrUpdate("delete from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "'");
	}

	public boolean deleteCheckEvent(String nodeId, String type, String subtype, String name) {
		return saveOrUpdate("delete from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='" + name + "'");
	}

	public boolean deleteCheckEvent(String nodeId, String type, String subtype, String name, String sindex) {
		return saveOrUpdate("delete from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='" + name
				+ "' and sindex='" + sindex + "'");
	}

	public boolean empty() {
		boolean flag = true;
		String sql = "delete from nms_checkevent";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public List<CheckEvent> findCheckEvent(String nodeId) {
		List<CheckEvent> list = new ArrayList<CheckEvent>();
		try {
			rs = conn.executeQuery("select * from nms_checkevent where nodeid='" + nodeId + "'");
			while (rs.next()) {
				list.add((CheckEvent) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CheckEvent> findCheckEvent(String nodeId, String type, String subtype, String name) {
		List<CheckEvent> list = new ArrayList<CheckEvent>();
		try {
			rs = conn.executeQuery("select * from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='" + name
					+ "'");
			while (rs.next()) {
				list.add((CheckEvent) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CheckEvent> findCheckEvent(String nodeId, String type, String subtype, String name, String sindex) {
		List<CheckEvent> list = new ArrayList<CheckEvent>();
		try {
			if (sindex != null && !"".equals(sindex) && !"null".equals(sindex)) {
				rs = conn.executeQuery("select * from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
						+ name + "' and sindex='" + sindex + "'");
			} else {
				rs = conn.executeQuery("select * from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
						+ name + "'");
			}
			while (rs.next()) {
				list.add((CheckEvent) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public BaseVo findCheckEventByName(String nodeId, String type, String subtype, String name, String sindex) {
		CheckEvent vo = null;
		try {
			if (sindex != null && !"".equals(sindex) && !"null".equals(sindex)) {
				rs = conn.executeQuery("select * from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
						+ name + "' and sindex='" + sindex + "'");
			} else {
				rs = conn.executeQuery("select * from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
						+ name + "'");
			}
			if (rs.next()) {
				vo = (CheckEvent) loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public CheckEvent findLikeName(String name) {
		CheckEvent vo = null;
		try {
			rs = conn.executeQuery("select * from nms_checkevent where name like '" + name + "'");
			if (rs.next()) {
				vo = (CheckEvent) loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_checkevent");
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
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_checkevent " + where);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CheckEvent vo = new CheckEvent();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setContent(rs.getString("content"));
			vo.setIndicatorsName(rs.getString("indicators_name"));
			vo.setSindex(rs.getString("sindex"));
			vo.setAlarmlevel(rs.getInt("alarmlevel"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setBid(rs.getString("bid"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		CheckEvent vo = (CheckEvent) baseVo;
		// 先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(nodeid,indicators_name,sindex,type,subtype,alarmlevel,thevalue,collecttime,bid)values(");
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
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean savecheckevent(BaseVo baseVo) {
		CheckEvent vo = (CheckEvent) baseVo;
		// 先删除,如果有该指标告警
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(nodeid,indicators_name,sindex,type,subtype,alarmlevel,content,thevalue,collecttime)values(");
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
		sql.append(vo.getCollecttime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		return true;
	}
}
