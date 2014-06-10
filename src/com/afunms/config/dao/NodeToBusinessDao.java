package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.NodeToBusiness;

@SuppressWarnings("unchecked")
public class NodeToBusinessDao extends BaseDao implements DaoInterface {
	public NodeToBusinessDao() {
		super("system_nodetobusiness");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from system_nodetobusiness where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteall() {
		boolean result = false;
		try {
			conn.addBatch("delete from system_nodetobusiness");
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteallbyNE(int nodeid, String eletype) {
		boolean result = false;
		try {
			conn.addBatch("delete from system_nodetobusiness where nodeid=" + nodeid + " and elementtype='" + eletype + "'");
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_nodetobusiness where id=" + id);
			if (rs.next()) {
				vo = loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_nodetobusiness order by id");
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

	public List loadByNodeAndEtype(int nodeid, String eletype) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_nodetobusiness where nodeid=" + nodeid + " and elementtype='" + eletype + "' order by id");
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

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		NodeToBusiness vo = new NodeToBusiness();
		try {
			vo.setId(rs.getInt("id"));
			vo.setElementtype(rs.getString("elementtype"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setBusinessid(rs.getInt("businessid"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		NodeToBusiness vo = (NodeToBusiness) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_nodetobusiness(elementtype,nodeid,businessid)values(");
		sql.append("'");
		sql.append(vo.getElementtype());
		sql.append("',");
		sql.append(vo.getNodeid());
		sql.append(",");
		sql.append(vo.getBusinessid());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		NodeToBusiness vo = (NodeToBusiness) baseVo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_nodetobusiness set elementtype=");
		sql.append(vo.getElementtype());
		sql.append(",nodeid=");
		sql.append(vo.getNodeid());
		sql.append(",businessid=");
		sql.append(vo.getBusinessid());
		sql.append(" where id=");
		sql.append(vo.getId());

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
}
