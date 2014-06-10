package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.system.model.SysLog;

@SuppressWarnings("unchecked")
public class SysLogDao extends BaseDao implements DaoInterface {
	public SysLogDao() {
		super("system_sys_log");
	}

	public List listByPage(int curpage) {
		List list = new ArrayList();
		int rc = 0;
		try {
			rs = conn.executeQuery("select count(*) from system_sys_log ");
			if (rs.next()) {
				rc = rs.getInt(1);
			}
			jspPage = new JspPage(curpage, rc);

			rs = conn.executeQuery("select * from system_sys_log order by id desc");
			int loop = 0;
			while (rs.next()) {
				loop++;
				if (loop < jspPage.getMinNum()) {
					continue;
				}
				list.add(loadFromRS(rs));
				if (loop == jspPage.getMaxNum()) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		SysLog vo = new SysLog();
		try {
			vo.setId(rs.getInt("id"));
			vo.setEvent(rs.getString("event"));
			vo.setLogTime(rs.getString("log_time"));
			vo.setIp(rs.getString("ip"));
			vo.setUser(rs.getString("username"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 增加一条记录
	 */
	public boolean save(BaseVo baseVo) {
		SysLog vo = (SysLog) baseVo;
		StringBuffer sb = new StringBuffer(200);
		sb.append("insert into system_sys_log(id,event,log_time,ip,username)values(");
		sb.append(getNextID());
		sb.append(",'");
		sb.append(vo.getEvent());
		sb.append("','");
		sb.append(vo.getLogTime());
		sb.append("','");
		sb.append(vo.getIp());
		sb.append("','");
		sb.append(vo.getUser());
		sb.append("')");
		return saveOrUpdate(sb.toString());
	}

	public boolean update(BaseVo baseVo) {
		return false;
	}
}
