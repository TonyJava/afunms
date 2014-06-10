package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.stationstorage;
import com.afunms.ip.stationtype.model.stationtype;



@SuppressWarnings("unchecked")
public class stationstorageDao extends BaseDao implements DaoInterface {

	public stationstorageDao() {
		super("ip_station");
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
		// TODO Auto-generated method stub
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		stationstorage vo = new stationstorage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setStation(rs.getString("station"));
			vo.setStation_id(rs.getInt("pe_ce_station"));
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

	public boolean saveCZ(BaseVo baseVo) {
		stationtype vo = (stationtype) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into ip_stationtype (name,descr,bak) values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getDescr());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
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

}
