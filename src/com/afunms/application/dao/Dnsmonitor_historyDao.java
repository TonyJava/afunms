package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.model.Dnsmonitor_history;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;


@SuppressWarnings("unchecked")
public class Dnsmonitor_historyDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Dnsmonitor_historyDao() {
		super("nms_dns_history");
	}

	public boolean update(BaseVo baseVo) {
		return false;
	}

	public boolean save(BaseVo baseVo) {
		Dnsmonitor_history vo = (Dnsmonitor_history) baseVo;
		Calendar tempCal = (Calendar) vo.getMon_time();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_dns_history(dns_id,is_canconnected,reason,mon_time)values(");
		sql.append("'");
		sql.append(vo.getDns_id());
		sql.append("','");
		sql.append(vo.getIs_canconnected());
		sql.append("','");
		sql.append(vo.getReason());

		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("','");
			sql.append(time);
			sql.append("'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',");
			sql.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
		}

		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_dns_history where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}


	public Vector getByDnsid(Integer dnsid, String starttime, String totime, Integer isconnected) throws Exception {
		Vector returnVal = new Vector();
		try {
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select is_canconnected," + "reason,mon_time from nms_dns_history where " + "dns_id=" + dnsid + " and is_canconnected=" + isconnected + " and "
						+ " mon_time >= '" + starttime + "' " + " and  mon_time <= '" + totime + "'";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select is_canconnected," + "reason,mon_time from nms_dns_history where " + "dns_id=" + dnsid + " and is_canconnected=" + isconnected + " and "
						+ " mon_time >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')" + " and  mon_time <= to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')";
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
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					newdate.setTime(rs.getTimestamp("mon_time").getTime());
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					newdate.setTime(rs.getDate("mon_time").getTime());
				}
				cal.setTime(newdate);
				obj[2] = sdf.format(cal.getTime());
				ht.put("conn", obj[0]);
				if (obj[1] == null) {
					obj[1] = "";
				}
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

	public String[] getAvailability(Integer dns_id, String starttime, String totime, String type) throws Exception {
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

			String sql = "select sum(aa." + type + ") as stype ,COUNT(aa.dns_id) as countid from nms_dns_history aa where aa.dns_id=" + dns_id + " and " + parm;
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

	public String[] getAvailability(String ip, String starttime, String totime, String type) throws Exception {
		String[] value = { "", "" };
		String allip = SysUtil.doip(ip);
		try {
			String parm = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				parm = " aa.collecttime >= '";
				parm = parm + starttime;
				parm = parm + "' and aa.collecttime <= '";
				parm = parm + totime;
				parm = parm + "'";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				parm = " aa.collecttime >=";
				parm = parm + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')";
				parm = parm + " and aa.collecttime <= ";
				parm = parm + "to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')";
			}
			String sql = "select sum(aa." + type + ") as stype ,COUNT(aa.ipaddress) as countid from dnsping" + allip + " aa where aa.ipaddress='" + ip + "' and " + parm;
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
		Dnsmonitor_history vo = new Dnsmonitor_history();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIs_canconnected(rs.getInt("is_canconnected"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("mon_time").getTime());
			cal.setTime(newdate);
			vo.setMon_time(cal);
			vo.setReason(rs.getString("reason"));
			vo.setDns_id(rs.getInt("dns_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
}