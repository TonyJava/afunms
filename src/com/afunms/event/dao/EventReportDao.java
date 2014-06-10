package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.EventReport;

@SuppressWarnings("unchecked")
public class EventReportDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public EventReportDao() {
		super("system_eventreport");
	}

	/**
	 * 批量添加事件报告
	 * 
	 * @param managesign
	 * @param ids
	 * @return
	 */
	public boolean batchAddEventReport(String[] ids, Hashtable updataHash) {
		if (ids == null || updataHash == null || updataHash.isEmpty()) {
			return false;
		}
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		try {
			String deal_time = "";
			String report_content = "";
			String report_man = "";
			String report_time = "";
			if (updataHash.containsKey("deal_time")) {
				deal_time = (String) updataHash.get("deal_time");
			}
			if (updataHash.containsKey("report_content")) {
				report_content = (String) updataHash.get("report_content");
			}
			if (updataHash.containsKey("report_man")) {
				report_man = (String) updataHash.get("report_man");
			}
			if (updataHash.containsKey("report_time")) {
				report_time = (String) updataHash.get("report_time");
			}
			if (deal_time.equals("")) {
				deal_time = sdf.format(new Date());
			}
			if (report_time.equals("")) {
				report_time = sdf.format(new Date());
			}
			for (String eventid : ids) {
				if (eventid != null && !eventid.equals("")) {
					// 只给状态为“处理中”的事件增加事件报告 managesign：1
					String managesign = "";
					try {
						rs = dbManager.executeQuery("select managesign from system_eventlist where id = '" + eventid + "'");
						if (rs.next()) {
							managesign = rs.getString("managesign");
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null) {
							rs.close();
						}
					}
					if (managesign.equals("1")) {
						StringBuffer sql = new StringBuffer();
						sql.append("insert into system_eventreport(eventid,report_man,report_content,deal_time,report_time) values");
						sql.append("('");
						sql.append(eventid);
						sql.append("','");
						sql.append(report_man);
						sql.append("','");
						sql.append(report_content);
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql.append("','");
							sql.append(deal_time);
							sql.append("','");
							sql.append(report_time);
							sql.append("')");
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							sql.append("',");
							sql.append("to_date('" + deal_time + "','YYYY-MM-DD HH24:MI:SS')");
							sql.append(",");
							sql.append("to_date('" + report_time + "','YYYY-MM-DD HH24:MI:SS')");
							sql.append(")");
						}
						conn.addBatch(sql.toString());
					}
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();

			dbManager.close();
		}
		return true;
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from system_eventreport where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo findByEventId(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_eventreport where eventid=" + id);
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
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_eventreport where id=" + id);
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
			rs = conn.executeQuery("select * from system_eventreport order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		EventReport vo = new EventReport();
		try {

			vo.setId(rs.getInt("id"));
			vo.setEventid(rs.getInt("eventid"));

			String report_content = rs.getClob("report_content").getSubString(1, (int) rs.getClob("report_content").length());
			vo.setReport_content(report_content);

			vo.setReport_man(rs.getString("report_man"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("deal_time").getTime());
			cal.setTime(newdate);
			vo.setDeal_time(cal);
			newdate.setTime(rs.getTimestamp("report_time").getTime());
			cal.setTime(newdate);
			vo.setReport_time(cal);
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		EventReport vo = (EventReport) baseVo;
		Calendar tempCal = vo.getDeal_time();
		Date cc = tempCal.getTime();
		String dealtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_eventreport(eventid,report_content,deal_time,report_time,report_man)values(");
		sql.append("");
		sql.append(vo.getEventid());
		sql.append(",'");
		sql.append(vo.getReport_content());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("','");
			sql.append(dealtime);
			sql.append("','");
			sql.append(dealtime);
			sql.append("','");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',");
			sql.append("to_date('" + dealtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",");
			sql.append("to_date('" + dealtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",'");

		}
		sql.append(vo.getReport_man());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	// ---------------update a business----------------
	public boolean update(BaseVo baseVo) {
		EventReport vo = (EventReport) baseVo;
		Calendar tempCal = vo.getDeal_time();
		Date cc = tempCal.getTime();
		String dealtime = sdf.format(cc);
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_eventreport set eventid=");
		sql.append("");
		sql.append(vo.getEventid());
		sql.append(",report_content='");
		sql.append(vo.getReport_content());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',deal_time='");
			sql.append(dealtime);
			sql.append("',report_time='");
			sql.append(dealtime);
			sql.append("',report_man='");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',deal_time=");
			sql.append("to_date('" + dealtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",report_time=");
			sql.append("to_date('" + dealtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",report_man='");
		}
		sql.append(vo.getReport_man());
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
