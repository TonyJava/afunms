package com.afunms.system.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.system.model.Position;

public class PositionDao extends BaseDao implements DaoInterface {
	public PositionDao() {
		super("system_position");
	}

	public boolean save(BaseVo baseVo) {
		Position vo = (Position) baseVo;
		boolean result = false;
		try {
			conn.executeUpdate("insert into system_position(id,name)values(" + getNextID() + ",'" + vo.getName() + "')");
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo baseVo) {
		Position vo = (Position) baseVo;
		boolean result = false;
		try {
			conn.executeUpdate("update system_position set name='" + vo.getName() + "' where id=" + vo.getId());
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from system_position where id=" + id[i]);
				conn.addBatch("delete from nms_user where dept_id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			result = false;
			conn.rollback();
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Position vo = new Position();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
}
