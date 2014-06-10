package com.afunms.cabinet.dao;

import java.sql.ResultSet;

import com.afunms.cabinet.model.EqpRoom;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class EqpRoomDao extends BaseDao implements DaoInterface {
	public EqpRoomDao() {
		super("nms_eqproom");
	}

	public boolean save(BaseVo baseVo) {
		EqpRoom vo = (EqpRoom) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_eqproom(id,name,descr,bak)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDescr());
		sql.append("','");
		sql.append(vo.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean save(String name) {
		StringBuffer sql = new StringBuffer();
		int id = getNextID("nms_eqproom");
		sql.append("insert into nms_eqproom(id, name) values(");
		sql.append(id);
		sql.append(",'");
		sql.append(name);
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		EqpRoom vo = (EqpRoom) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_eqproom set name='");
		sql.append(vo.getName());
		sql.append("',descr='");
		sql.append(vo.getDescr());
		sql.append("',bak='");
		sql.append(vo.getBak());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_eqproom where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		EqpRoom vo = new EqpRoom();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setDescr(rs.getString("descr"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
}
