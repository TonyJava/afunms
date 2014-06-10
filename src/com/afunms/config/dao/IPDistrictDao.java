package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.IPDistrictConfig;

@SuppressWarnings("unchecked")
public class IPDistrictDao extends BaseDao implements DaoInterface {

	public IPDistrictDao() {
		super("nms_ipdistrict");
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
			conn.addBatch("delete from nms_ipdistrict where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteByDistrictId(String districtId) {
		String sql = "delete from nms_ipdistrict where district_id = '" + districtId + "'";
		return saveOrUpdate(sql);
	}

	public List getDistrictById(int id) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_district where id = " + id);
		return findByCriteria(sql.toString());
	}

	/**
	 * 列出所有方法
	 */
	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_ipdistrict order by id");
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

	public List loadByDistrictId(String districtId) {
		String sql = "select * from nms_ipdistrict where district_id = '" + districtId + "'";
		return findByCriteria(sql);
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		IPDistrictConfig vo = new IPDistrictConfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setDistrictid(rs.getInt("district_id"));
			vo.setStartip(rs.getString("startip"));
			vo.setEndip(rs.getString("endip"));
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
		IPDistrictConfig vo = (IPDistrictConfig) basevo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_ipdistrict(district_id,startip,endip)values(");
		sql.append(vo.getDistrictid());
		sql.append(",'");
		sql.append(vo.getStartip());
		sql.append("','");
		sql.append(vo.getEndip());
		sql.append("'");
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 修改方法
	 */
	public boolean update(BaseVo baseVo) {
		IPDistrictConfig vo = (IPDistrictConfig) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_ipdistrict set district_id=");
		sql.append(vo.getDistrictid());
		sql.append(",startip='");
		sql.append(vo.getStartip());
		sql.append("',endip='");
		sql.append(vo.getEndip());
		sql.append("' where id=" + vo.getId());
		return saveOrUpdate(sql.toString());
	}

}
