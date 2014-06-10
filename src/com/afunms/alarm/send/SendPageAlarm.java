package com.afunms.alarm.send;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.EventList;
import com.afunms.util.DataGate;

@SuppressWarnings("unchecked")
public class SendPageAlarm implements SendAlarm {

	public void sendAlarm(EventList eventList, String uid) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			Calendar tempCal = (Calendar) eventList.getRecordtime();
			Date cc = tempCal.getTime();
			String recordtime = sdf.format(cc);
			if (eventList.getLevel1() > 0) {
				List list = new ArrayList();
				String liststr = getEventlist(startTime, endTime, eventList.getManagesign() + "", eventList.getLevel1() + "", eventList.getBusinessid(), eventList.getNodeid(),
						eventList.getSubentity());
				rs = stmt.executeQuery(liststr);
				while (rs.next()) {
					list.add(loadFromRS(rs));
				}
				if (list != null && list.size() > 0) {
					EventList vo = (EventList) list.get(0);
					StringBuffer sql = new StringBuffer();
					//更新则发生次数+1
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql.append("update system_eventlist set happenTimes=happenTimes+1,lasttime='" + recordtime + "',content='" + eventList.getContent() + "' where id=" + vo.getId());
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql.append("update system_eventlist set happenTimes=happenTimes+1,lasttime=to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS'),content='" + eventList.getContent() + "' where id="
								+ vo.getId());
					}
					stmt.executeUpdate(sql.toString());
				} else {
					eventList.setLasttime(recordtime);
					EventList vo = (EventList) eventList;
					tempCal = (Calendar) vo.getRecordtime();
					cc = tempCal.getTime();
					recordtime = sdf.format(cc);
					if (vo.getLasttime() == null) {
						vo.setLasttime(recordtime);
					}
					StringBuffer sql = new StringBuffer(300);
					sql
							.append("insert into system_eventlist(eventtype,eventlocation,content,level1,managesign,bak,recordtime,reportman,nodeid,businessid,oid,lasttime,subtype,managetime,subentity,happenTimes)values(");
					sql.append("'");
					sql.append(vo.getEventtype());
					sql.append("','");
					sql.append(vo.getEventlocation());
					sql.append("','");
					sql.append(vo.getContent());
					sql.append("',");
					sql.append(vo.getLevel1());
					sql.append(",");
					sql.append(vo.getManagesign());
					sql.append(",'");
					sql.append(vo.getBak());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql.append("','");
						sql.append(recordtime);
						sql.append("','");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql.append("',");
						sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
						sql.append(",'");
					}
					sql.append(vo.getReportman());
					sql.append("',");
					sql.append(vo.getNodeid());
					sql.append(",'");
					sql.append(vo.getBusinessid());
					sql.append("','");
					sql.append(vo.getOid());
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sql.append("','");
						sql.append(vo.getLasttime());
						sql.append("','");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sql.append("',");
						sql.append("to_date('" + vo.getLasttime() + "','YYYY-MM-DD HH24:MI:SS')");
						sql.append(",'");
					}
					sql.append(vo.getSubtype());
					sql.append("','");
					sql.append(vo.getManagetime());
					sql.append("','");
					sql.append(vo.getSubentity());
					sql.append("','");
					sql.append(1);//第一次插入,设置发生次数=1
					sql.append("')");
					stmt.executeUpdate(sql.toString());
				}
			} else {
				List list = new ArrayList();
				String liststr = getEventlist(startTime, endTime, eventList.getManagesign() + "", "99", eventList.getBusinessid(), eventList.getNodeid(), eventList.getSubentity());
				rs = stmt.executeQuery(liststr);
				while (rs.next()) {
					list.add(loadFromRS(rs));
				}
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						EventList vo = (EventList) list.get(i);
						if (vo.getLevel1() > 0) {
							String time = null;// 告警持续时间，默认分钟为单位
							long timeLong = 0;
							tempCal = (Calendar) vo.getRecordtime();
							cc = tempCal.getTime();
							String collecttime = sdf.format(cc);
							Date firstAlarmDate = null;
							try {
								firstAlarmDate = sdf.parse(collecttime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (firstAlarmDate != null) {
								timeLong = new Date().getTime() - firstAlarmDate.getTime();
							}
							if (timeLong < 1000 * 60) {// 小于1分钟,秒
								time = timeLong / 1000 + "秒";
							} else {// 小于1小时,分
								time = timeLong / (60 * 1000) + "分";
							}
							StringBuffer sql = new StringBuffer();
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("update system_eventlist set lasttime='" + recordtime + "',level1=" + 0 + ",content='" + vo.getContent() + " (该告警已恢复，告警持续时间" + time
										+ ")" + "',managesign=1 where id=" + vo.getId());
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("update system_eventlist set lasttime=to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS'),level1=" + 0 + ",content='"
										+ vo.getContent() + " (该告警已恢复，告警持续时间" + time + ")" + "',managesign=1 where id=" + vo.getId());
							}
							stmt.executeUpdate(sql.toString());
						}
					}
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void sendAlarm(List<EventList> list) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			for (int i = 0; i < list.size(); i++) {
				EventList vo = (EventList) list.get(i);
				Calendar tempCal = (Calendar) vo.getRecordtime();
				Date cc = tempCal.getTime();
				String recordtime = sdf.format(cc);
				if (vo.getLasttime() == null) {
					vo.setLasttime(recordtime);
				}
				StringBuffer sql = new StringBuffer(100);
				sql
						.append("insert into system_eventlist(id,eventtype,eventlocation,content,level1,managesign,bak,recordtime,reportman,nodeid,businessid,oid,subtype,managetime,subentity,lasttime)values(system_eventlist_seq.nextval,");
				sql.append("'");
				sql.append(vo.getEventtype());
				sql.append("','");
				sql.append(vo.getEventlocation());
				sql.append("','");
				sql.append(vo.getContent());
				sql.append("',");
				sql.append(vo.getLevel1());
				sql.append(",");
				sql.append(vo.getManagesign());
				sql.append(",'");
				sql.append(vo.getBak());
				sql.append("',");
				sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
				sql.append(",'");
				sql.append(vo.getReportman());
				sql.append("',");
				sql.append(vo.getNodeid());
				sql.append(",'");
				sql.append(vo.getBusinessid());
				sql.append("',");
				sql.append(vo.getOid());
				sql.append(",'");
				sql.append(vo.getSubtype());
				sql.append("','");
				sql.append(vo.getManagetime());
				sql.append("','");
				sql.append(vo.getSubentity());
				sql.append("',");
				sql.append("to_date('" + vo.getLasttime() + "','YYYY-MM-DD HH24:MI:SS')");
				sql.append(")");
				stmt.addBatch(sql.toString());
			}
			stmt.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void sendAlarm(EventList eventList, AlarmWayDetail alarmWayDetail) {

	}

	private BaseVo loadFromRS(ResultSet rs) {
		EventList vo = new EventList();
		try {
			vo.setId(rs.getInt("id"));
			vo.setEventtype(rs.getString("eventtype"));
			vo.setEventlocation(rs.getString("eventlocation"));
			vo.setContent(rs.getString("content"));
			vo.setLevel1(rs.getInt("level1"));
			vo.setManagesign(rs.getInt("managesign"));
			vo.setBak(rs.getString("bak"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("recordtime").getTime());
			cal.setTime(newdate);
			vo.setRecordtime(cal);
			vo.setReportman(rs.getString("reportman"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setBusinessid(rs.getString("businessid"));
			vo.setOid(rs.getInt("oid"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setManagetime(rs.getString("managetime"));
			vo.setSubentity(rs.getString("subentity"));
			vo.setLasttime(rs.getString("lasttime"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	private String getEventlist(String starttime, String totime, String status, String level, String businessid, Integer nodeid, String subentity) {
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime
					+ "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (!"99".equals(level)) {
			s.append(" and e.level1=" + level);
		}
		if (!"99".equals(status)) {
			s.append(" and e.managesign=" + status);
		}
		if (!"99".equals(subentity)) {
			s.append(" and e.subentity='" + subentity + "'");
		}
		if (nodeid != null) {
			if (nodeid.intValue() != 99) {
				s.append(" and nodeid=" + nodeid);
			}
		}
		int flag = 0;
		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s.append(" and ( businessid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								s.append(" or businessid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		s.append(" order by e.recordtime desc");
		String sql = s.toString();
		return sql;
	}
}
