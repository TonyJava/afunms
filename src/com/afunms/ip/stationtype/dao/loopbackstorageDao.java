package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.loopbackstorage;

@SuppressWarnings("unchecked")
public class loopbackstorageDao extends BaseDao implements DaoInterface {

	public loopbackstorageDao() {
		super("ip_loopback");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from ip_loopback where loopback_id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	public List findID(String sql) {
		BaseDao base = new loopbackstorageDao();
		return base.findByCriteria(sql);
	}

	public List loadCZ() {
		List list = new ArrayList();
		try {
			String sql = "select * from ip_dy ";
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		loopbackstorage vo = new loopbackstorage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setLoopback(rs.getString("loopback"));
			vo.setBackbone_id(rs.getInt("backbone_id"));
			vo.setType(rs.getInt("type"));
			vo.setField_id(rs.getInt("field_id"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public BaseVo QueryOne(String tablename, String name, int id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from " + tablename + " where id= (select min(id) from " + tablename + " where type = 0 and " + name + "_id = " + id + ")");
			if (rs.next()) {
				vo = loadFromRS(rs);
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
		return vo;
	}

	public List QueryTwo(int n, int loopback_id) {
		List list = new ArrayList();
		try {

			rs = conn.executeQuery("select t.* from(select * from ip_loopback where type=0 and loopback_id = '" + loopback_id + "') t  limit 0, " + n);
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

	public boolean save(BaseVo vo) {
		return false;
	}

	public void saveIP(String ip, String backbone, String ip_end) {
		String address = ip.substring(0, ip.lastIndexOf("."));
		String addressend = ip_end.substring(ip.lastIndexOf(".") + 1);
		int end_num = Integer.parseInt(addressend);
		try {
			for (int i = 3; i <= end_num; i++) {
				StringBuffer sql = new StringBuffer(100);
				sql.append("insert into ip_loopback (loopback,backbone_id) values(");
				sql.append("'");
				sql.append(address + "." + i);
				sql.append("',");
				sql.append(backbone);
				sql.append(")");
				try {
					conn.addBatch(sql.toString());
					conn.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public boolean update(int id, int field_id) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_loopback set type=1,field_id = '");
		sql.append(field_id);
		sql.append("' where id=");
		sql.append(id);
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

	public void update(String sql) {

		try {
			conn.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public boolean updateFirst(String loopback) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_loopback set type=0,field_id = '");
		sql.append(0);
		sql.append("' where loopback='");
		sql.append(loopback);
		sql.append("'");
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

	public boolean updateSecond(String loopback, int field_id) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_loopback set type=1,field_id = '");
		sql.append(field_id);
		sql.append("' where loopback='");
		sql.append(loopback);
		sql.append("'");
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

}
