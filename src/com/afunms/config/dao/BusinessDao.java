package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Business;


@SuppressWarnings("unchecked")
public class BusinessDao extends BaseDao implements DaoInterface {
	public BusinessDao() {
		super("system_business");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from system_business where id=" + id[i]);
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

	public boolean deleteVoAndChildVoById(String id) {
		boolean result = false;
		try {
			String sql = "delete from system_business where id='" + id + "' or pid='" + id + "'";
			conn.executeUpdate(sql);
			result = true;
		} catch (RuntimeException e) {
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
			rs = conn.executeQuery("select * from system_business where id=" + id);
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

	
	public List findByIDs(String IDs) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from system_business where id in(" + IDs + ")");
			if (rs != null) {
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public BaseVo findBySuperID(String id) {
		return super.findByID(id);
	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_business order by id");
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

	public Business loadBidbyID(String id) {
		Business vo = null;
		try {
			if (id != null || id != "") {
				rs = conn.executeQuery("select * from system_business where id =" + id);
			}
			while (rs.next()) {
				vo = (Business) loadFromRS(rs);
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
	public BaseVo loadFromRS(ResultSet rs) {
		Business vo = new Business();
		try {
			vo.setId(rs.getString("id"));
			vo.setName(rs.getString("name"));
			vo.setDescr(rs.getString("descr"));
			vo.setPid(rs.getString("pid"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	/**
	 * 
	 * @description 暂且支持四层节点
	 * @author wangxiangyong
	 * @date Feb 18, 2013 6:02:47 PM
	 * @return List
	 * @param bids
	 * @return
	 */
	public List loadRoleBusiness(String bids) {
		List list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			if (bids != null) {
				bids = bids.replaceAll(",,", ",");
				if (bids.length() > 1) {
					bids = bids.substring(1, bids.length() - 1);
					sql.append("select * from system_business ");
					sql.append("where pid in(0) or id in(");
					sql.append(bids);
					sql.append(") or id in(select pid from system_business where id in(");
					sql.append(bids).append("))");
					sql.append(" or id in(select a.pid from system_business a where a.id in(select b.pid from system_business b where b.id in(");
					sql.append(bids).append(")))");
					sql.append(" order by id");

					rs = conn.executeQuery(sql.toString());
					while (rs.next()) {
						list.add(loadFromRS(rs));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	/**
	 * 根据id递归查询所有节点，包括父节点
	 * 
	 * @author
	 * @param IDs
	 * @return
	 */
	public List queryRecursionIDs(String IDs) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select distinct * from system_business order by id ");
			if (rs != null) {
				while (rs.next()) {
					BaseVo vo = loadFromRS(rs);
					list.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean save(BaseVo baseVo) {
		Business vo = (Business) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_business(name,descr,pid)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDescr());
		sql.append("','");
		sql.append(vo.getPid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Business vo = (Business) baseVo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_business set name='");
		sql.append(vo.getName());
		sql.append("',descr='");
		sql.append(vo.getDescr());
		sql.append("',pid='");
		sql.append(vo.getPid());
		sql.append("' where id=");
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
