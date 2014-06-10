package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.ip_bussinesstype;

@SuppressWarnings("unchecked")
public class bussinessDao extends BaseDao implements DaoInterface {

	public bussinessDao() {
		super("ip_bussiness");
	}

	public boolean delect(List id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.size(); i++) {
				conn.addBatch("delete from ip_bussiness where backbone_id=" + id.get(i));
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
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from ip_bussiness where id=" + id[i]);
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
		ip_bussinesstype vo = new ip_bussinesstype();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBusname(rs.getString("busname"));
			vo.setBuskind(rs.getString("buskind"));// 实时业务，非实时业务
			vo.setSegment(rs.getString("segment"));
			vo.setGateway(rs.getString("gateway"));
			vo.setEncryption(rs.getString("encryption"));
			vo.setVlan(rs.getString("vlan"));
			vo.setField_id(rs.getInt("field_id"));
			vo.setBackbone_id(rs.getInt("backbone_id"));
			vo.setVlan_ip(rs.getString("vlan_ip"));
			vo.setFlag(rs.getString("flag"));
			vo.setIp_use(rs.getString("ip_use"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public List queryBussiness(String backbone_name, String bussiness) {
		List list = new ArrayList();
		String sql = "select t.* from  (select * from ip_bussiness where backbone_name='" + backbone_name + "' and bussiness='" + bussiness + "' and field_id = 0) t  limit 0,8";
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
				}
			}
			conn.close();
		}
		return list;
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

	public List QueryList(int id) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from ip_bussiness where field_id = " + id);
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
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

	public List select(String sql) {
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

	public boolean update(BaseVo baseVo) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
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

	public void updateField_id(int id, int field_id) {
		String sql = "update ip_bussiness set field_id=" + field_id + " where id = " + id;
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
