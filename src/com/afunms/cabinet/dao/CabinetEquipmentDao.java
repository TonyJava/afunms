/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.cabinet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.cabinet.model.CabinetEquipment;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Business;

@SuppressWarnings("unchecked")
public class CabinetEquipmentDao extends BaseDao implements DaoInterface {
	public CabinetEquipmentDao() {
		super("nms_cabinet_equipments");
	}

	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_cabinet_equipments order by id");
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

	public Business loadBidbyID(String id) {
		Business vo = null;
		try {
			if (id != null || id != "") {
				rs = conn.executeQuery("select * from nms_cabinet_equipments where id =" + id);
			}
			while (rs.next())
				vo = (Business) loadFromRS(rs);
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		CabinetEquipment vo = (CabinetEquipment) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql
				.append("insert into nms_cabinet_equipments(id,cabinetid,nodeid,nodename,nodedescr,unumbers,businessName,businessid,operid,contactname,contactphone,contactemail,roomid)values(");
		sql.append(getNextID());
		sql.append(",");
		sql.append(vo.getCabinetid());
		sql.append(",");
		sql.append(vo.getNodeid());
		sql.append(",'");
		sql.append(vo.getNodename());
		sql.append("','");
		sql.append(vo.getNodedescr());
		sql.append("','");
		sql.append(vo.getUnmubers());
		sql.append("','");
		sql.append(vo.getBusinessName());
		sql.append("',");
		sql.append(vo.getBusinessid());
		sql.append(",");
		sql.append(vo.getOperid());
		sql.append(",'");
		sql.append(vo.getContactname());
		sql.append("','");
		sql.append(vo.getContactphone());
		sql.append("','");
		sql.append(vo.getContactemail());
		sql.append("',");
		sql.append(vo.getRoomid());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		CabinetEquipment vo = (CabinetEquipment) baseVo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_cabinet_equipments set cabinetid=");
		sql.append(vo.getCabinetid());
		sql.append(",nodeid=");
		sql.append(vo.getNodeid());
		sql.append(",nodename='");
		sql.append(vo.getNodename());
		sql.append("',nodedescr='");
		sql.append(vo.getNodedescr());
		sql.append("',unumbers='");
		sql.append(vo.getUnmubers());
		sql.append("',businessName='");
		sql.append(vo.getBusinessName());
		sql.append("',businessid=");
		sql.append(vo.getBusinessid());
		sql.append(",operid=");
		sql.append(vo.getOperid());
		sql.append(",contactname='");
		sql.append(vo.getContactname());
		sql.append("',contactphone='");
		sql.append(vo.getContactphone());
		sql.append("',contactemail='");
		sql.append(vo.getContactemail());
		sql.append("',roomid=");
		sql.append(vo.getRoomid());
		sql.append(" where id=");
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

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_cabinet_equipments where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteVoAndChildVoById(String id) {
		boolean result = false;
		try {
			String sql = "delete from nms_cabinet_equipments where id='" + id + "' or pid='" + id + "'";
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

	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_cabinet_equipments where id=" + id);
			if (rs.next())
				vo = loadFromRS(rs);
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
			rs = conn.executeQuery("select * from nms_cabinet_equipments where id in(" + IDs + ")");
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

	public List findByRoomidAndCabinetid(int roomid, int cabinetid) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_equipments where roomid =" + roomid + " and cabinetid = " + cabinetid);
			if (rs != null) {
				String[] us = null;
				while (rs.next()) {
					CabinetEquipment vo = (CabinetEquipment) loadFromRS(rs);
					String unumbers = vo.getUnmubers();
					if (unumbers != null && unumbers.length() > 0) {
						us = unumbers.split(",");
						if (us.length > 0) {
							for (int i = 0; i < us.length; i++) {
								String us_temp = us[i];
								list.add(us_temp);
							}

						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		CabinetEquipment vo = new CabinetEquipment();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCabinetid(rs.getInt("cabinetid"));

			vo.setNodeid(rs.getInt("nodeid"));
			vo.setNodename(rs.getString("nodename"));
			vo.setNodedescr(rs.getString("nodedescr"));

			vo.setUnmubers(rs.getString("unumbers"));

			vo.setOperid(rs.getInt("operid"));
			vo.setBusinessid(rs.getInt("businessid"));
			vo.setBusinessName(rs.getString("businessName"));
			vo.setContactname(rs.getString("contactname"));
			vo.setContactphone(rs.getString("contactphone"));
			vo.setContactemail(rs.getString("contactemail"));

			vo.setRoomid(rs.getInt("roomid"));

		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public BaseVo findBySuperID(String id) {
		return super.findByID(id);
	}

	public BaseVo findByU(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_cabinet_equipments where cabinetid=" + id);
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	public boolean delByCabinetID(int id) {
		String sql = new String();
		sql = "delete from nms_cabinet_equipments where cabinetid = " + id;
		return saveOrUpdate(sql);
	}

	public List loadByCabinetID(int id) {
		List list = new ArrayList();
		try {
			if (id != 0) {
				rs = conn.executeQuery("select * from nms_cabinet_equipments where cabinetid =" + id);
			}
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadByRoomID(int id) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_config where motorroom = '" + id + "'");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			try {
				if (rs != null)
					rs.close();

				if (conn != null)
					conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public List findByEquipment(int operid) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_equipments where operid =" + operid);
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

	public List findByEquipment(int roomid, int cabinetid) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_equipments where roomid =" + roomid + " and cabinetid = " + cabinetid);
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
}
