/**
 * <p>Description: active_server_alarm</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project 衡水信用社
 * @date 2007-3-23
 */

package com.afunms.cabinet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

@SuppressWarnings("unchecked")
public class MachineCabinetDao extends BaseDao implements DaoInterface {
	public MachineCabinetDao() {
		super("nms_cabinet_config");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		MachineCabinet machineCabinet = new MachineCabinet();
		try {
			machineCabinet.setId(rs.getInt("id"));
			machineCabinet.setName(rs.getString("name"));
			machineCabinet.setMachinex(rs.getString("machinex"));
			machineCabinet.setMachiney(rs.getString("machiney"));
			machineCabinet.setMachinez(rs.getString("machinez"));
			machineCabinet.setUselect(rs.getString("uselect"));
			machineCabinet.setMotorroom(rs.getString("motorroom"));
			machineCabinet.setStandards(rs.getString("standards"));
			machineCabinet.setPowers(rs.getString("powers"));
			machineCabinet.setHeights(rs.getString("heights"));
			machineCabinet.setWidths(rs.getString("widths"));
			machineCabinet.setDepths(rs.getString("depths"));
			machineCabinet.setNos(rs.getString("nos"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return machineCabinet;
	}

	public boolean save(BaseVo vo) {
		MachineCabinet machineCabinet = (MachineCabinet) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_cabinet_config(id, name, machinex, machiney, machinez, uselect,motorroom," + "standards,powers,heights,widths,depths,nos) values('");
		sql.append(machineCabinet.getId());
		sql.append("','");
		sql.append(machineCabinet.getName());
		sql.append("','");
		sql.append(machineCabinet.getMachinex());
		sql.append("','");
		sql.append(machineCabinet.getMachiney());
		sql.append("','");
		sql.append(machineCabinet.getMachinez());
		sql.append("','");
		sql.append(machineCabinet.getUselect());
		sql.append("','");
		sql.append(machineCabinet.getMotorroom());
		sql.append("','");
		sql.append(machineCabinet.getStandards());
		sql.append("','");
		sql.append(machineCabinet.getPowers());
		sql.append("','");
		sql.append(machineCabinet.getHeights());
		sql.append("','");
		sql.append(machineCabinet.getWidths());
		sql.append("','");
		sql.append(machineCabinet.getDepths());
		sql.append("','");
		sql.append(machineCabinet.getNos());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean save(String name, String motorroom) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_cabinet_config(id, name, motorroom) values(");
		sql.append(getNextID("nms_cabinet_config"));
		sql.append(",'");
		sql.append(name);
		sql.append("','");
		sql.append(motorroom);
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		MachineCabinet machineCabinet = (MachineCabinet) vo;

		StringBuffer sql = new StringBuffer();
		sql.append("update nms_cabinet_config set name ='");
		sql.append(machineCabinet.getName());
		sql.append("',machinex='");
		sql.append(machineCabinet.getMachinex());
		sql.append("',machiney='");
		sql.append(machineCabinet.getMachiney());
		sql.append("',machinez='");
		sql.append(machineCabinet.getMachinez());
		sql.append("',uselect='");
		sql.append(machineCabinet.getUselect());
		sql.append("',motorroom='");
		sql.append(machineCabinet.getMotorroom());
		sql.append("',standards='");
		sql.append(machineCabinet.getStandards());
		sql.append("',powers='");
		sql.append(machineCabinet.getPowers());
		sql.append("',heights='");
		sql.append(machineCabinet.getHeights());
		sql.append("',widths='");
		sql.append(machineCabinet.getWidths());
		sql.append("',depths='");
		sql.append(machineCabinet.getDepths());
		sql.append("',nos='");
		sql.append(machineCabinet.getNos());
		sql.append("' where id=" + machineCabinet.getId());
		return saveOrUpdate(sql.toString());
	}

	// 查询所有数据

	public List loadAll() {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_config");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			SysLogger.error("PortconfigDao:loadAll()", e);
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

	// 查询所有数据
	public List loadId(String id) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_config where motorroom =" + id);
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

	public List selectById(String roomid) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_config where motorroom=" + roomid);
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception ex) {
			ex.printStackTrace();
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

	public Hashtable<String, Integer> getCountByU(int id) {
		Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
		try {
			rs = conn.executeQuery("select count(*) con,uselect from nms_cabinet_config where motorroom = '" + id + "' group by uselect ");
			while (rs.next())
				ht.put(rs.getString("uselect"), rs.getInt("con"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ht;
	}

	public List loadByRoomIDAndU(int id, String uselect) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_cabinet_config where motorroom = '" + id + "' and uselect = '" + uselect + "'");
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
}
