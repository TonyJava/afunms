package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.IpaddressPanel;

@SuppressWarnings("unchecked")
public class IpaddressPanelDao extends BaseDao implements DaoInterface {
	public IpaddressPanelDao() {
		super("system_ipaddresspanel");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from system_ipaddresspanel where id=" + id[i]);
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

	public boolean deleteByHostIp(String hostip) {
		String sql = "delete from nms_portscan_config where ipaddress='" + hostip + "'";
		return saveOrUpdate(sql);
	}

	public void empty() {
		try {
			conn.executeUpdate("delete from system_ipaddresspanel ");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_ipaddresspanel where id=" + id);
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

	public BaseVo findByIpaddress(String ipaddress) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_ipaddresspanel where ipaddress='" + ipaddress + "'");
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

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_ipaddresspanel order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		IpaddressPanel vo = new IpaddressPanel();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setStatus(rs.getString("status"));
			vo.setImageType(rs.getString("imageType"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public IpaddressPanel loadIpaddressPanel(int id) {
		List modelList = findByCriteria("select * from system_ipaddresspanel where id=" + id);
		if (modelList != null && modelList.size() > 0) {
			IpaddressPanel model = (IpaddressPanel) modelList.get(0);
			return model;

		}
		return null;
	}

	public IpaddressPanel loadIpaddressPanel(String ipaddress) {
		List modelList = findByCriteria("select * from system_ipaddresspanel where ipaddress='" + ipaddress + "'");
		if (modelList != null && modelList.size() > 0) {
			IpaddressPanel model = (IpaddressPanel) modelList.get(0);
			return model;

		}
		return null;
	}

	/**
	 * @author nielin modify at 2010-01-14
	 */
	public boolean save(BaseVo baseVo) {
		IpaddressPanel vo = (IpaddressPanel) baseVo;
		String sqldelete = "";
		sqldelete = "delete from system_ipaddresspanel where ipaddress='" + vo.getIpaddress() + "'";
		try {
			conn.executeUpdate(sqldelete);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_ipaddresspanel(ipaddress,status,imageType)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getStatus());
		sql.append("','");
		sql.append(vo.getImageType());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	/**
	 * Save a list of IpaddressPanel into database
	 * 
	 * @author nielin add at 2010-01-14
	 * @param list
	 * @return
	 */
	public boolean save(List list) {
		try {
			for (int i = 0; i < list.size(); i++) {
				IpaddressPanel vo = (IpaddressPanel) list.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into system_ipaddresspanel(ipaddress,status,imagetype) values (");
				sql.append("'");
				sql.append(vo.getIpaddress());
				sql.append("','");
				sql.append(vo.getStatus());
				sql.append("','");
				sql.append(vo.getImageType());
				sql.append("')");
				conn.executeUpdate(sql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
		}
		return true;
	}

	public boolean update(BaseVo baseVo) {
		IpaddressPanel vo = (IpaddressPanel) baseVo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_ipaddresspanel set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',status='");
		sql.append(vo.getStatus());
		sql.append("',imagetype='");
		sql.append(vo.getImageType());
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
