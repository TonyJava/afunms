package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.pe_cestorage;
import com.afunms.ip.stationtype.model.stationtype;

@SuppressWarnings("unchecked")
public class pe_cestorageDao extends BaseDao implements DaoInterface {

	public pe_cestorageDao() {
		super("ip_pe_ce");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from ip_pe_ce where backbone_id=" + id[i]);
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
		BaseDao base = new pe_cestorageDao();
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
		pe_cestorage vo = new pe_cestorage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setS1(rs.getString("s1"));
			vo.setS2(rs.getString("s2"));
			vo.setField_id(rs.getInt("field_id"));
			vo.setBackbone_id(rs.getInt("backbone_id"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public List QueryTwo(int n, int pe_ce_id) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select t.* from(select * from ip_pe_ce where type=0 and pe_ce_id = " + pe_ce_id + ") t  limit 0, " + n);
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
				}
			}
		}
		return list;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public void saveIP(String ip, int begin, String backbone, int end, String ip_end) {
		String[] ip1 = ip.split("\\.");
		String[] ip2 = ip_end.split("\\.");
		String address = ip.substring(0, ip.lastIndexOf("."));
		int num_end = ip1[0].length() + ip1[1].length();

		String address2 = ip.substring(0, num_end + 1);
		String addressend = ip.substring(ip.lastIndexOf(".") + 1);
		int end_num = Integer.parseInt(addressend);
		try {
			if (!ip1[2].equals(ip2[2])) {
				for (int i = begin; i <= end; i++) {
					int x = begin++;
					StringBuffer sql = new StringBuffer(100);
					sql.append("insert into ip_pe_ce (pe_ce,pe_ce_id) values(");
					sql.append("'");
					sql.append(address2 + "." + x + "." + addressend);
					sql.append("',");
					sql.append("'");
					sql.append(backbone);
					sql.append("')");
					for (int j = end_num; j <= 254; j++) {
						StringBuffer sql1 = new StringBuffer();
						sql1.append("insert into ip_pe_ce (pe_ce,pe_ce_id) values(");
						sql1.append("'");
						if (!ip1[2].equals(ip2[2])) {
							sql1.append(address2 + "." + x + "." + j);
						} else {
							sql1.append(address + "." + x);
						}
						sql1.append("',");
						sql1.append("'");
						sql1.append(backbone);
						sql1.append("')");
						try {
							conn.addBatch(sql1.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						conn.addBatch(sql.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (!ip1[1].equals(ip2[1])) {

			} else {
				for (int i = begin; i <= end; i++) {
					int x = begin++;
					StringBuffer sql = new StringBuffer(100);
					sql.append("insert into ip_pe_ce (pe_ce,pe_ce_id) values(");
					sql.append("'");
					sql.append(address + "." + x);
					sql.append("',");
					sql.append("'");
					sql.append(backbone);
					sql.append("')");
					try {
						conn.addBatch(sql.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

	}

	public boolean update(BaseVo baseVo) {
		boolean result = false;
		stationtype vo = (stationtype) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_stationtype set name='");
		sql.append(vo.getName());
		sql.append("',descr='");
		sql.append(vo.getDescr());
		sql.append("',bak='");
		sql.append(vo.getBak());
		sql.append("' where id=");
		sql.append(vo.getId());
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

	public boolean update(int id, int field_id) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_pe_ce set type=1,field_id = '");
		sql.append(field_id);
		sql.append("' where id=");
		sql.append(id);
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

	public void update(String sql) {

		try {
			conn.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public boolean updateFirst(String pe_ce) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_pe_ce set type=0,field_id = '");
		sql.append(0);
		sql.append("' where pe_ce='");
		sql.append(pe_ce);
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

	public boolean updateSecond(String pe_ce, int field_id) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update ip_pe_ce set type=1,field_id = '");
		sql.append(field_id);
		sql.append("' where pe_ce='");
		sql.append(pe_ce);
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
