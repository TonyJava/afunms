package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Macconfig;


@SuppressWarnings("unchecked")
public class MacconfigDao extends BaseDao implements DaoInterface {

	public MacconfigDao() {
		super("nms_macconfig");
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
			conn.addBatch("delete from nms_macconfig where id=" + id);
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
	 * 
	 * @param id
	 * @return
	 * @modify nielin
	 */
	public boolean deleteAll() {
		String sql = "delete from nms_macconfig";
		return saveOrUpdate(sql);
	}

	/**
	 * 根据id删除这条记录
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteByDistrictId(String districtId) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_macconfig where discrictid='" + districtId + "'");
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
	 * 按MAC找一条记录
	 */
	public List findByMac(String mac) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_macconfig where mac='" + mac + "'");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception ex) {
			 ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Macconfig vo = new Macconfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setMac(rs.getString("mac"));
			vo.setDeptid(rs.getInt("deptid"));
			vo.setEmployeeid(rs.getInt("employeeid"));
			vo.setMacdesc(rs.getString("macdesc"));
			vo.setDiscrictid(rs.getInt("discrictid"));
		} catch (Exception ex) {
			ex.printStackTrace();
			vo = null;
		}
		return vo;
	}


	/**
	 * 添加记录
	 */
	public boolean save(BaseVo baseVo) {
		Macconfig vo = (Macconfig) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_macconfig(mac,employeeid,discrictid,deptid,macdesc)values('");
		sql.append(vo.getMac());
		sql.append("','");
		sql.append(vo.getEmployeeid());
		sql.append("','");
		sql.append(vo.getDiscrictid());
		sql.append("','");
		sql.append(vo.getDeptid());
		sql.append("','");
		sql.append(vo.getMacdesc());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 
	 * @param mac
	 * @return
	 * @add nielin
	 */
	public boolean saveBatch(List list) {
		boolean result = false;
		if (list != null && list.size() > 0) {
			try {
				for (int i = 0; i < list.size(); i++) {
					Macconfig vo = (Macconfig) list.get(i);
					StringBuffer sql = new StringBuffer(100);
					sql.append("insert into nms_macconfig(mac,employeeid,discrictid,deptid,macdesc)values('");
					sql.append(vo.getMac());
					sql.append("','");
					sql.append(vo.getEmployeeid());
					sql.append("','");
					sql.append(vo.getDiscrictid());
					sql.append("','");
					sql.append(vo.getDeptid());
					sql.append("','");
					sql.append(vo.getMacdesc());
					sql.append("')");
					conn.addBatch(sql.toString());
				}
				conn.executeBatch();
				result = true;
			} catch (RuntimeException e) {
				e.printStackTrace();
				result = false;
			}
		}

		return result;
	}

	/**
	 * 修改mac记录
	 */
	public boolean update(BaseVo baseVo) {
		Macconfig vo = (Macconfig) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_macconfig set mac='");
		sql.append(vo.getMac());
		sql.append("',employeeid='");
		sql.append(vo.getEmployeeid());
		sql.append("',discrictid='");
		sql.append(vo.getDiscrictid());
		sql.append("',deptid='");
		sql.append(vo.getDeptid());
		sql.append("',macdesc='");
		sql.append(vo.getMacdesc());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

}
