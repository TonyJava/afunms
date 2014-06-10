package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.alltype;

@SuppressWarnings("unchecked")
public class alltypeDao extends BaseDao implements DaoInterface {

	public alltypeDao() {
		super("ip_alltype");
	}

	public int count(String table, String where) {
		int i = 0;
		try {
			rs = conn.executeQuery("select count(*) from " + table + " " + where);
			if (rs == null) {
				return 0;
			}
			while (rs.next()) {
				i = rs.getInt(1);
			}

		} catch (Exception e) {
			i = 0;
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
		return i;
	}

	@Override
	protected synchronized int getNextID() {
		return super.getNextID();
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
		alltype vo = new alltype();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBackbone_name(rs.getString("backbone_name"));
			vo.setLoopback_begin(rs.getString("loopback_begin"));
			vo.setLoopback_end(rs.getString("loopback_end"));
			vo.setPe_begin(rs.getString("pe_begin"));
			vo.setPe_end(rs.getString("pe_end"));
			vo.setPe_ce_begin(rs.getString("pe_ce_begin"));
			vo.setPe_ce_end(rs.getString("pe_ce_end"));
			vo.setBus_begin(rs.getString("bus_begin"));
			vo.setBus_end(rs.getString("bus_end"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public List queryID() {
		List list = new ArrayList();
		String sql = "select max(id) from ip_alltype ";
		try {
			rs = conn.executeQuery(sql);
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			list = null;
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
		return list;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean saveCZ(BaseVo baseVo) {
		alltype vo = (alltype) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into ip_alltype (backbone_name,loopback_begin,loopback_end,pe_begin,pe_end,pe_ce_begin,pe_ce_end,bus_begin,bus_end) values(");
		sql.append("'");
		sql.append(vo.getBackbone_name());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getLoopback_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getLoopback_end());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_end());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_ce_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_ce_end());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getBus_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getBus_end());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		boolean result = false;
		alltype vo = (alltype) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_alltype set name='");
		sql.append("");
		sql.append("',descr='");
		sql.append("");
		sql.append("',bak='");
		sql.append("");
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
