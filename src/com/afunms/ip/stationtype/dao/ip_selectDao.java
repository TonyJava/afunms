package com.afunms.ip.stationtype.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.ip.stationtype.model.ip_select;

public class ip_selectDao extends BaseDao implements DaoInterface {

	public ip_selectDao() {
		super("ip_portal_apply");
	}

	public void delete(String sql) {

		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		ip_select vo = new ip_select();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setIds(rs.getString("ids"));
			vo.setRouteName(rs.getString("routeName"));
			vo.setRouteType(rs.getString("routeType"));
			vo.setS1Name(rs.getString("s1Name"));
			vo.setS1Type(rs.getString("s1Type"));
			vo.setS2Name(rs.getString("s2Name"));
			vo.setS2Type(rs.getString("s2Type"));
			vo.setRFactory(rs.getString("rFactory"));
			vo.setS1Factory(rs.getString("s1Factory"));
			vo.setS2Factory(rs.getString("s2Factory"));
			vo.setDeviceType(rs.getString("deviceType"));
			vo.setDeviceCount(rs.getString("deviceCount"));
			vo.setHasCabinet(rs.getString("hasCabinet"));
			vo.setLocation(rs.getString("location"));
			vo.setDebugUnit(rs.getString("debugUnit"));
			vo.setFlag(rs.getString("flag"));

		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateFlag(String id) {
		String sql = "update ip_portal_apply set flag = 1 where id = " + id;
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public void updateName(String id, String name) {
		String sql = "update ip_portal_apply set name = '" + name + "' where id = " + id;
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}
}
