package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.JBossmonitor_history;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;


@SuppressWarnings("unchecked")
public class JBossmonitor_historyDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public JBossmonitor_historyDao() {
		super("nms_jboss_history");
	}

	public boolean update(BaseVo baseVo) {
		return false;
	}

	public boolean save(BaseVo baseVo) {
		JBossmonitor_history vo = (JBossmonitor_history) baseVo;
		Calendar tempCal = (Calendar) vo.getMon_time();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_jboss_history(jboss_id,is_canconnected,reason,mon_time)values(");
		sql.append("'");
		sql.append(vo.getJboss_id());
		sql.append("','");
		sql.append(vo.getIs_canconnected());
		sql.append("','");
		sql.append(vo.getReason());
		sql.append("',");
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("'");
			sql.append(time);
			sql.append("'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
		}
		sql.append(")");

		return saveOrUpdate(sql.toString());
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_jboss_history where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}


	public Vector getByJBossid(Integer jbossid, String starttime, String totime, Integer isconnected) throws Exception {
		Vector returnVal = new Vector();
		try {
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.is_canconnected,a.reason,a.mon_time from nms_jboss_history a where " + "a.jboss_id=" + jbossid + " and (a.mon_time >= '" + starttime
						+ "' and  a.mon_time <= '" + totime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.is_canconnected,a.reason,a.mon_time from nms_jboss_history a where " + "a.jboss_id=" + jbossid + " and (a.mon_time >= to_date('" + starttime
						+ "','YYYY-MM-DD HH24:MI:SS') and  a.mon_time <= to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS'))";
			}
			SysLogger.info(sql);
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				Object[] obj = new Object[3];
				obj[0] = rs.getString("is_canconnected");
				Hashtable ht = new Hashtable();
				obj[1] = rs.getString("reason");
				Calendar cal = Calendar.getInstance();
				Date newdate = new Date();
				newdate.setTime(rs.getTimestamp("mon_time").getTime());
				cal.setTime(newdate);
				obj[2] = sdf.format(cal.getTime());
				ht.put("conn", obj[0]);
				ht.put("reason", obj[1]);
				ht.put("mon_time", obj[2]);
				returnVal.addElement(ht);
				ht = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
	}

	public String[] getAvailability(Integer jboss_id, String starttime, String totime, String type) throws Exception {
		String[] value = { "", "" };
		try {
			String parm = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				parm = " aa.mon_time >= '";
				parm = parm + starttime;
				parm = parm + "' and aa.mon_time <= '";
				parm = parm + totime;
				parm = parm + "'";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				parm = " aa.mon_time >= ";
				parm = parm + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')";
				parm = parm + " and aa.mon_time <= ";
				parm = parm + "to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')";
			}
			String sql = "select sum(aa." + type + ") as stype ,COUNT(aa.jboss_id) as countid from nms_jboss_history aa where aa.jboss_id=" + jboss_id + " and " + parm;
			System.out.println(sql);
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				value[0] = rs.getInt("stype") + "";
				value[1] = rs.getInt("countid") + "";
				value[1] = new Integer(new Integer(value[1]).intValue() - new Integer(value[0]).intValue()).toString();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		JBossmonitor_history vo = new JBossmonitor_history();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIs_canconnected(rs.getInt("is_canconnected"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("mon_time").getTime());
			cal.setTime(newdate);
			vo.setMon_time(cal);
			vo.setReason(rs.getString("reason"));
			vo.setJboss_id(rs.getInt("jboss_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public Hashtable getPingData(Integer jboss_id, String starttime, String endtime, String type) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.is_canconnected," + " a.reason,a.mon_time from nms_jboss_history a where " + "a.jboss_id=" + jboss_id + " and (a.mon_time >= '" + starttime
						+ "' and  a.mon_time <= '" + endtime + "')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.is_canconnected," + " a.reason,a.mon_time from nms_jboss_history a where " + "a.jboss_id=" + jboss_id + " and (a.mon_time >= to_date('" + starttime
						+ "','YYYY-MM-DD HH24:MI:SS') and  a.mon_time <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS'))";
			}
			int i = 0;
			double avgput1 = 0;
			rs = conn.executeQuery(sql);
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("is_canconnected");
					String collecttime = rs.getString("mon_time");
					v.add(0, thevalue);
					v.add(1, collecttime);
					avgput1 = avgput1 + Float.parseFloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			hash.put("list", list1);
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput1", CEIString.round(avgput1 / list1.size(), 2) + "");
			} else {
				hash.put("avgput1", "0.0");
			}
		}
		return hash;
	}

}
