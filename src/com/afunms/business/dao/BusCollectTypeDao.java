package com.afunms.business.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.business.model.BusCollectType;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

@SuppressWarnings("unchecked")
public class BusCollectTypeDao extends BaseDao implements DaoInterface {

	public BusCollectTypeDao() {
		super("nms_buscolltype");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {

		BusCollectType vo = new BusCollectType();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCollecttype(rs.getString("collecttype"));
			vo.setBct_desc(rs.getString("bct_desc"));
		}

		catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	/**
	 * 列出所有方法
	 */
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_buscolltype order by id");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	/**
	 * 修改方法
	 */
	public boolean update(BaseVo baseVo) {
		BusCollectType vo = (BusCollectType) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_buscolltype set collecttype ='");
		sql.append(vo.getCollecttype());
		sql.append("',bct_desc='");
		sql.append(vo.getBct_desc());
		sql.append("' where id=" + vo.getId());
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 添加方法
	 */
	public boolean save(BaseVo basevo) {
		BusCollectType vo = (BusCollectType) basevo;
		StringBuffer sql = new StringBuffer();
		int id = getNextID();
		sql.append("insert into nms_buscolltype(id,collecttype,bct_desc)values(");
		sql.append(id);
		sql.append(",'");
		sql.append(vo.getCollecttype());
		sql.append("','");
		sql.append(vo.getBct_desc());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 根据id删除这条记录
	 * 
	 * @param id
	 * @return
	 */
	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_buscolltype where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo getCollectTypeById(int id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_buscolltype where id=" + id);
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

}
