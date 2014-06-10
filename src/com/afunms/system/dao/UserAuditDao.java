package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SystemConstant;
import com.afunms.system.model.UserAudit;

public class UserAuditDao extends BaseDao implements DaoInterface {
	public UserAuditDao() {
		super("nms_user_audit");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		UserAudit userAudit = new UserAudit();
		try {
			userAudit.setId(rs.getInt("id"));

			userAudit.setUserId(rs.getInt("userid"));

			userAudit.setAction(rs.getString("action"));

			userAudit.setTime(rs.getString("time"));
			;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userAudit;
	}

	public boolean save(BaseVo vo) {
		UserAudit userAudit = (UserAudit) vo;
		StringBuffer sql = new StringBuffer();
		if (SystemConstant.DBType.equals("mysql")) {
			sql.append("insert into nms_user_audit(userid,action,time)values(");
			sql.append("'");
			sql.append(userAudit.getUserId());
			sql.append("','");
			sql.append(userAudit.getAction());
			sql.append("','");
			sql.append(userAudit.getTime());
			sql.append("'");
			sql.append(")");
		} else if (SystemConstant.DBType.equals("oracle")) {
			sql.append("insert into nms_user_audit(id,userid,action,time)values(nms_user_audit_seq.nextval,"); // jhl
																												// Ìí¼ÓID
			sql.append("'");
			sql.append(userAudit.getUserId());
			sql.append("','");
			sql.append(userAudit.getAction());
			sql.append("',to_date('" + userAudit.getTime() + "','YYYY-MM-DD HH24:MI:SS')"); // jhl
																							// Ìí¼Ó
			sql.append(")");
		}
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public boolean deleteByUserId(String userId) {
		String sql = "delete from nms_user_audit where userid='" + userId + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
