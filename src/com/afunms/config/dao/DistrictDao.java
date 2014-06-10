package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.DistrictConfig;


@SuppressWarnings("unchecked")
public class DistrictDao extends BaseDao implements DaoInterface {

	public DistrictDao() {
		super("nms_district");
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
			conn.addBatch("delete from nms_district where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	/**
	 * 列出所有方法
	 */
	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_district order by id");
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
		DistrictConfig vo = new DistrictConfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setDesc(rs.getString("dis_desc"));
			vo.setDescolor(rs.getString("descolor"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	/**
	 * 添加方法
	 */
	public boolean save(BaseVo basevo) {
		DistrictConfig vo = (DistrictConfig) basevo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_district(name,dis_desc,descolor)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDesc());
		sql.append("','");
		sql.append(vo.getDescolor());
		sql.append("'");
		sql.append(")");

		return saveOrUpdate(sql.toString());
	}

	/**
	 * 修改方法
	 */
	public boolean update(BaseVo baseVo) {
		DistrictConfig vo = (DistrictConfig) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_district set name ='");
		sql.append(vo.getName());
		sql.append("',dis_desc='");
		sql.append(vo.getDesc());
		sql.append("',descolor='");
		sql.append(vo.getDescolor());
		sql.append("' where id=" + vo.getId());
		return saveOrUpdate(sql.toString());
	}

}
