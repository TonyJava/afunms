package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.field;

public class fieldDao extends BaseDao implements DaoInterface {

	public fieldDao() {
		super("ip_field");
	}

	public void delete(String sql) {

		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from ip_field where backbone_id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	public synchronized int getNextID(String otherTable) {
		int id = 0;
		try {
			rs = conn.executeQuery("select max(id) from " + otherTable);
			if (rs.next()) {
				id = rs.getInt(1) + 1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			id = 0;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return id;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		field vo = new field();
		try {
			vo.setId(rs.getInt("id"));
			vo.setRunning(rs.getString("running"));
			vo.setName(rs.getString("name"));
			vo.setBackbone_id(rs.getInt("backbone_id"));
			vo.setFlag(rs.getInt("flag"));
		} catch (Exception e) {
			// SysLogger.error("PortconfigDao.loadFromRS()",e);
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public BaseVo loadOne(String tablename, String backbone_name) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from " + tablename + " where backbone_name = '" + backbone_name + "'");
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				vo = loadFromRS(rs);
			}
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
			conn.close();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		boolean result = false;
		field vo = (field) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ip_field (name,running,backbone_id,flag)  values('");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getRunning());
		sql.append("',");
		sql.append(vo.getBackbone_id());
		sql.append(",");
		sql.append(vo.getFlag());
		sql.append(")");
		try {
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

	public boolean update(BaseVo vo) {
		return false;
	}

	public void update(String field_id, String name, String running) {
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_field set name='");
		sql.append(name);
		sql.append("',running='");
		sql.append(running);
		sql.append("'");
		sql.append(" where id=");
		sql.append(field_id);
		try {
			conn.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
