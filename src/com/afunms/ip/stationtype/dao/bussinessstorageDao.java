package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.bussinessstorage;
import com.afunms.ip.stationtype.model.stationtype;


@SuppressWarnings("unchecked")
public class bussinessstorageDao extends BaseDao implements DaoInterface {

	public bussinessstorageDao() {
		super("ip_bussiness");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from ip_bussiness where bussiness_id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		bussinessstorage vo = new bussinessstorage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBussiness(rs.getString("bussiness"));
			vo.setBussiness_id(rs.getInt("bussiness_id"));
			vo.setType(rs.getInt("type"));
			vo.setField_id(rs.getString("field_id"));
			vo.setBus_id(rs.getString("bus_id"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public List queryID(String sql) {
		List list = new ArrayList();
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

	public void saveIP(String ip, int begin, String backbone, int end, String ip_end) {
		String[] ip1 = ip.split("\\.");
		String[] ip2 = ip_end.split("\\.");
		String address = ip.substring(0, ip.lastIndexOf("."));
		int num_end = ip1[0].length() + ip1[1].length();

		String address2 = ip.substring(0, num_end + 1);
		String addressend = ip.substring(ip.lastIndexOf(".") + 1);
		int end_num = Integer.parseInt(addressend);

		// IPµÚ¶þ¶Î±ä»»
		int two_num = Integer.parseInt(ip1[2]);

		try {
			if (!ip1[2].equals(ip2[2])) {
				for (int i = begin; i <= end; i++) {
					int x = begin++;
					StringBuffer sql = new StringBuffer(100);
					sql.append("insert into ip_bussiness (bussiness,bussiness_id) values(");
					sql.append("'");
					sql.append(address2 + "." + x + "." + addressend);
					sql.append("',");
					sql.append("'");
					sql.append(backbone);
					sql.append("')");
					for (int j = end_num; j <= 254; j++) {
						StringBuffer sql1 = new StringBuffer();
						sql1.append("insert into ip_bussiness (bussiness,bussiness_id) values(");
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
				for (int i = begin; i <= end; i++) {
					int x = begin++;
					StringBuffer sql = new StringBuffer(100);
					sql.append("insert into ip_bussiness (bussiness,bussiness_id) values(");
					sql.append("'");
					sql.append(ip1[0] + "." + x + "." + ip1[2] + "." + ip1[3]);
					sql.append("',");
					sql.append("'");
					sql.append(backbone);
					sql.append("')");
					for (int j = two_num; j <= 254; j++) {
						StringBuffer sql1 = new StringBuffer();
						sql1.append("insert into ip_bussiness (bussiness,bussiness_id) values(");
						sql1.append("'");
						sql1.append(ip1[0] + "." + x + "." + j + "." + ip1[3]);
						sql1.append("',");
						sql1.append("'");
						sql1.append(backbone);
						sql1.append("')");
						for (int y = 0; y <= 254; y++) {
							StringBuffer sql2 = new StringBuffer();
							sql2.append("insert into ip_bussiness (bussiness,bussiness_id) values(");
							sql2.append("'");
							sql2.append(ip1[0] + "." + x + "." + j + "." + y);
							sql2.append("',");
							sql2.append("'");
							sql2.append(backbone);
							sql2.append("')");
							try {
								conn.addBatch(sql2.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
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
			} else {
				for (int i = begin; i <= end; i++) {
					int x = begin++;
					StringBuffer sql = new StringBuffer(100);
					sql.append("insert into ip_bussiness (bussiness,bussiness_id) values(");
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

	public void updateIP(String sql) {
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
