package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.backbonestorage;
import com.afunms.ip.stationtype.model.stationtype;

@SuppressWarnings("unchecked")
public class backbonestorageDao extends BaseDao implements DaoInterface {

	public backbonestorageDao() {
		super("ip_backbone");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from ip_backbone where backbone_id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
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
		backbonestorage vo = new backbonestorage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBackbone(rs.getString("backbone"));
			vo.setBackbone_id(rs.getInt("backbone_id"));
			vo.setType(rs.getInt("type"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
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

		try {
			for (int i = begin; i <= end; i++) {
				int x = begin++;
				StringBuffer sql = new StringBuffer(100);
				sql.append("insert into ip_backbone (backbone,backbone_id) values(");
				sql.append("'");
				if (!ip1[2].equals(ip2[2])) {
					sql.append(address2 + "." + x + "." + addressend);
				} else {
					sql.append(address + "." + x);
				}
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
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

}
