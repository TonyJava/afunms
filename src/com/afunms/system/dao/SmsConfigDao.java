package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.system.model.SmsConfig;
import com.afunms.system.model.User;

@SuppressWarnings("unchecked")
public class SmsConfigDao extends BaseDao implements DaoInterface {
	public SmsConfigDao() {
		super("nms_smsconfig");
	}

	public List getSmsConfigByObject(String objectId, String objectType) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_smsconfig where objectId='" + objectId + "' and objectType='" + objectType + "'";
			rs = conn.executeQuery(sql);
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return list;
	}

	public boolean saveSmsConfigList(String objectId, String objectType, ArrayList smsConfigList) {
		try {
			String sql = "";
			sql = "delete from nms_smsconfig where objectId='" + objectId + "' and objectType='" + objectType + "'";
			System.out.println(sql);
			try {
				conn.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Iterator iterator = smsConfigList.iterator();

			while (iterator.hasNext()) {
				SmsConfig smsConfig = (SmsConfig) iterator.next();
				sql = "insert into nms_smsconfig (objectid,objecttype,begintime,endtime,userids) values ('" + smsConfig.getObjectId() + "','" + smsConfig.getObjectType() + "','"
						+ smsConfig.getBeginTime() + "','" + smsConfig.getEndTime() + "','" + smsConfig.getUserIds() + "')";
				try {
					conn.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return true;
	}

	public boolean save(BaseVo baseVo) {
		return false;
	}

	public int save(User vo) {
		int result = -1;
		return result;
	}

	public boolean update(BaseVo baseVo) {
		StringBuffer sql = new StringBuffer(200);
		return saveOrUpdate(sql.toString());
	}

	public BaseVo loadFromRS(ResultSet rs) {
		SmsConfig smsConfig = new SmsConfig();
		try {
			smsConfig.setId(rs.getInt("id"));
			smsConfig.setObjectId(rs.getString("objectid"));
			smsConfig.setObjectType(rs.getString("objecttype"));
			smsConfig.setBeginTime(rs.getString("begintime"));
			smsConfig.setEndTime(rs.getString("endtime"));
			smsConfig.setUserIds(rs.getString("userids"));
		} catch (Exception ex) {
			ex.printStackTrace();
			smsConfig = null;
		}
		return smsConfig;
	}
}
