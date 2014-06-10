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
import com.afunms.common.base.JspPage;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.EventList;
import com.afunms.system.vo.FlexVo;

@SuppressWarnings("unchecked")
public class EventListDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public EventListDao() {
		super("system_eventlist");
	}

	public boolean batchEditAlarmLevel(String[] ids, String alermlevel) {
		boolean result = false;
		try {
			for (int i = 0; i < ids.length; i++) {
				if (ids[i] != null && !ids[i].equals("null") && !ids[i].equals("")) {
					conn.addBatch("update system_eventlist set level1='" + alermlevel + "' where id=" + ids[i]);
				}
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * ����ids �����޸� managesign
	 * 
	 * @param managesignFrom
	 *            ԭʼ״̬
	 * @param managesignTo
	 *            �������ĺ��״̬
	 * @param ids
	 * @return
	 */
	public boolean batchUpdataManagesignByIds(String managesignFrom, String managesignTo, String[] ids) {
		if (ids == null) {
			return false;
		}
		try {
			for (String id : ids) {
				if (id != null && !id.equals("")) {
					String sql = "update system_eventlist set managesign='" + managesignTo + "' where id=" + id + " and managesign = '" + managesignFrom + "'";
					conn.addBatch(sql);
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
		}
		return true;
	}

	public boolean delete(int nodeid, String subtype) {
		String sql = "delete from system_eventlist where nodeid=" + nodeid + " and subtype='" + subtype + "'";
		return saveOrUpdate(sql);
	}

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from system_eventlist where id=" + id[i]);
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

	public boolean deleteByIdAndSubentity(int nodeid, String subentity) {
		String sql = "delete from system_eventlist where nodeid=" + nodeid + " and subentity='" + subentity + "'";
		return saveOrUpdate(sql);
	}

	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from system_eventlist where id=" + id);
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

	/**
	 * ���� where ��ȡ���澯��
	 * 
	 * @author nielin
	 * @date 2010-08-05
	 * @param where
	 * @return
	 */
	public String getCountByWhere(String where) {
		try {
			String sql = "select count(*) as cnt from system_eventlist" + where;
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				return rs.getString("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "0";
	}

	public List getEventlist(String starttime, String totime, String status, String level, String businessid, Integer nodeid) {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (!"99".equals(level)) {
			s.append(" and e.level1<>" + level);
		}
		if (!"99".equals(status)) {
			s.append(" and e.managesign=" + status);
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
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getEventlist(String starttime, String totime, String status, String level, String businessid, Integer nodeid, String subentity) {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
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
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getEventList(String starttime, String totime) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select count(level1),e.level1 as level1 from system_eventlist e where e.level1<>0 and e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select count(level1),e.level1 as level1 from system_eventlist e where e.level1<>0 and e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + "and e.recordtime<=to_date('" + totime
					+ "','YYYY-MM-DD HH24:MI:SS') ");
		}

		s.append(" group by e.level1 order by e.level1 desc");
		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				FlexVo flexVo = new FlexVo();
				flexVo.setObjectName(rs.getString("level1"));
				flexVo.setObjectNumber(rs.getString("count(level1)"));
				list.add(flexVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getEventList(String starttime, String totime, String businessid) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select count(level1),e.level1 as level1 from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select count(level1),e.level1 as level1 from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
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
		s.append(" group by e.level1 order by e.level1 desc");
		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				FlexVo flexVo = new FlexVo();
				flexVo.setObjectName(rs.getString("level1"));
				flexVo.setObjectNumber(rs.getString("count(level1)"));
				list.add(flexVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * ��ȡ�澯�б�
	 * 
	 * @param starttime
	 * @param totime
	 * @param nodeid
	 * @param type
	 * @param level1
	 * @param businessid
	 * @param status
	 * @return
	 */
	public List<EventList> getEventList(String starttime, String totime, String nodeid, String type, String level1, String eventlocation, String subentity, String businessid, String status) {
		StringBuffer sql = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append(" where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append(" where recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (nodeid != null && !"-1".equals(nodeid)) {
			sql.append(" and nodeid='" + nodeid + "'");
		}
		if (type != null && !"-1".equals(type)) {
			sql.append(" and subtype='" + type + "'");
		}
		if (level1 != null && !"-1".equals(level1)) {
			sql.append(" and level1='" + level1 + "'");
		}
		if (eventlocation != null && !"-1".equals(eventlocation)) {
			sql.append(" and eventlocation='" + eventlocation + "'");
		}
		if (subentity != null && !"-1".equals(subentity)) {
			sql.append(" and subentity='" + subentity + "'");
		}
		if (status != null && !"-1".equals(status)) {
			sql.append(" and managesign='" + status + "'");
		}

		int flag = 0;
		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								sql.append(" and ( businessid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								sql.append(" or businessid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					sql.append(") ");
				}
			}
		}
		return findByCondition(sql.toString());
	}

	public List<EventList> getLastest(String bid) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currDate = simpleDateFormat.format(new Date());
		String starttime = currDate + " 00:00:00";
		String totime = currDate + " 23:59:59";
		return getLastest(bid, starttime, totime);
	}

	public List<EventList> getLastest(String bid, String starttime, String totime) {
		List<EventList> list = new ArrayList<EventList>();
		StringBuffer sb = new StringBuffer();
		StringBuffer bidSQL = new StringBuffer();
		int flag = 0;
		if (bid != null) {
			if (bid != "-1") {
				String[] bids = bid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								bidSQL.append(" and ( businessid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								bidSQL.append(" or businessid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					bidSQL.append(") ");
				}
			}
		}

		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sb.append("select * , max(recordtime) as recordtime from system_eventlist");
			sb.append(" where level1=3 and recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "'" + bidSQL.toString());
			sb.append(" Group By level1,nodeid,subentity,subtype ");
			sb.append(" ORDER BY recordtime LIMIT 10");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sb.append("select * from system_eventlist where id in (select id from (select level1,nodeid,subentity,subtype ,eventlocation, count(*) , max(recordtime) as recordtime,max(id) as id from system_eventlist");
			sb.append(" where level1=3 and recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')" + bidSQL.toString());
			sb.append(" Group By level1,nodeid,subentity,subtype,eventlocation");
			sb.append(" ORDER BY recordtime) where rownum <=10)");
		}

		String sql = sb.toString();
		ResultSet resultSet = null;
		try {
			resultSet = conn.executeQuery(sql);
			while (resultSet.next()) {
				BaseVo vo = loadFromRS(resultSet);
				EventList eventList = (EventList) vo;
				list.add(eventList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return list;

	}

	public List getQuery(String starttime, String totime, Integer nodeid) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (nodeid != null) {
			s.append(" and nodeid=" + nodeid);
		}
		s.append(" order by e.recordtime desc");
		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getQuery(String starttime, String totime, String status, String level, String businessid, Integer nodeid) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (!"99".equals(level)) {
			s.append(" and e.level1=" + level);
		}
		if (!"99".equals(status)) {
			s.append(" and e.managesign=" + status);
		}

		if (nodeid != null) {
			if (nodeid.intValue() != 0 && nodeid.intValue() != -1) {
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
								s.append(" and ( businessid like '%" + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								s.append(" or businessid like '%" + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		s.append(" order by e.recordtime desc");
		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getQuery(String starttime, String totime, String status, String level, String businessid, Integer nodeid, String subType) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (!"99".equals(level)) {
			s.append(" and e.level1=" + level);
		}
		if (!"99".equals(status)) {
			s.append(" and e.managesign=" + status);
		}

		if (nodeid != null) {
			if (nodeid.intValue() != 0 && nodeid.intValue() != -1) {
				s.append(" and nodeid=" + nodeid);
			}
		}
		s.append(" and subtype='" + subType);
		s.append("'");
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
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getQuery(String starttime, String totime, String subtype, String status, String level, String businessid, Integer nodeid) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (subtype != null && subtype.trim().length() > 0) {
			s.append(" and e.subtype='" + subtype + "' ");
		}
		if (!"99".equals(level)) {
			s.append(" and e.level1=" + level);
		}
		if (!"99".equals(status)) {
			if ("-1".equalsIgnoreCase(status)) {
				s.append(" and e.managesign=0");
			} else {
				s.append(" and e.managesign=" + status);
			}
		}
		if (nodeid != null) {
			if (nodeid.intValue() != -1) {
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
								s.append(" and ( businessid like '%" + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								s.append(" or businessid like '%" + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}
			}
		}
		s.append(" order by e.recordtime desc");
		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getQuery_flex(String starttime, String totime, String status, String level, String businessid, Integer nodeid) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= '" + starttime + "' " + "and e.recordtime<='" + totime + "'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("select * from system_eventlist e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and e.recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}
		if (!"99".equals(level)) {
			s.append(" and e.level1=" + level);
		}
		if (!"99".equals(status)) {
			s.append(" and e.managesign=" + status);
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
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append(" order by e.recordtime desc LIMIT 8");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append(" order by e.recordtime desc");
		}

		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getQueryForEventList(String starttime, String totime, String businessid,String likeContent) throws Exception {
		List list = new ArrayList();
		StringBuffer s = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("SELECT * FROM system_eventlist where recordtime>='" + starttime + "' and recordtime<='" + totime + "' and managesign='0' and  content like '%"+likeContent+"%'");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			s.append("SELECT * from  system_eventlist where recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')"
					+ " and managesign='0' and (eventlocation like '%%' or content like '%%')");
		}
		int flag = 0;
		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s.append(" and ( businessid like '%" + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								s.append(" or businessid like '%" + bids[i].trim() + ",%' ");
							}
						}
					}
					if (flag == 1) {
						s.append(") ");
					}
				}
			}
		}
		s.append(" ORDER BY id DESC ");
		String sql = s.toString();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				EventList vo = new EventList();
				vo.setEventlocation(rs.getString("eventlocation"));
				vo.setContent(rs.getString("content"));
				vo.setLevel1(rs.getInt("level1"));
				Calendar cal = Calendar.getInstance();
				Date newdate = new Date();
				newdate.setTime(rs.getTimestamp("recordtime").getTime());
				cal.setTime(newdate);
				vo.setRecordtime(cal);
				vo.setNodeid(rs.getInt("nodeid"));
				vo.setLasttime(rs.getString("lasttime"));
				vo.setSubentity(rs.getString("subentity"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public List getSummary(String where, int curpage, int perpage) {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			//select * , count(*)->select * ,
			sb.append("select * , max(recordtime) from system_eventlist " + where + " Group By level1,nodeid,subentity,subtype");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sb.append("select * from system_eventlist where id in (select id from (select level1,nodeid,subentity,subtype ,eventlocation, count(*) , max(recordtime),max(id) as id from system_eventlist " + where
					+ " Group By level1,nodeid,subentity,subtype,eventlocation))");
		}
		String sql = sb.toString();
		ResultSet rSet = null;
		ResultSet rs_count = null;
		Hashtable tempHash = new Hashtable();
		try {
			rs = conn.executeQuery("select count(*) from (" + sql + ") temp");
			try {
				if (rs.next()) {
					jspPage = new JspPage(perpage, curpage, rs.getInt(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.close();
				}
			}

			if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				conn = new DBManager();// ����resultset���ܹ���һ��statement,��Ҫ������������
				try {
					sb = new StringBuffer();
					sb.append("select level1,nodeid,subentity,subtype ,eventlocation, count(*) , max(recordtime) as recordtime,max(id) as id from system_eventlist " + where + " Group By level1,nodeid,subentity,subtype,eventlocation");
					rs_count = conn.executeQuery(sb.toString());
					while (rs_count.next()) {
						tempHash.put(rs_count.getInt("id"), rs_count.getString("count(*)"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.close();
				}
			}

			conn = new DBManager(); // ����resultset���ܹ���һ��statement,��Ҫ������������
			rSet = conn.executeQuery(sql);
			int loop = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while (rSet.next()) {
				loop++;
				if (loop < jspPage.getMinNum()) {
					continue;
				}
				List templist = new ArrayList();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					templist.add(loadFromRS(rSet));
					templist.add(rSet.getString("happenTimes"));
					templist.add(rSet.getString("max(recordtime)"));
					templist.add(rSet.getString("lasttime"));
					list.add(templist);
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					try {
						EventList vo = (EventList) loadFromRS(rSet);
						String count = "0";
						if (tempHash.containsKey(vo.getId())) {
							count = (String) tempHash.get(vo.getId());
						}
						templist.add(vo);
						templist.add(count);
						Calendar cal = Calendar.getInstance();
						Date newdate = new Date();
						newdate.setTime(rSet.getTimestamp("recordtime").getTime());
						cal.setTime(newdate);
						vo.setRecordtime(cal);
						String deal_time = sdf.format(newdate);
						templist.add(deal_time);
						templist.add(vo.getLasttime());
						list.add(templist);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (loop == jspPage.getMaxNum()) {
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rSet != null) {
				try {
					rSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs_count != null) {
				try {
					rs_count.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return list;
	}

	/**
	 * ��ȡ�澯�б�
	 * 
	 * @param starttime
	 * @param totime
	 * @param nodeid
	 * @param type
	 * @param level1
	 * @param businessid
	 * @param status
	 * @return
	 */
	public List<Object> getSummary(String starttime, String totime, String nodeid, String type, String level1, String businessid, String status) {
		StringBuffer sql = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append(" where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append(" where recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + " and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
		}

		if (nodeid != null && !"-1".equals(nodeid)) {
			sql.append(" and nodeid='" + nodeid + "'");
		}
		if (type != null && !"-1".equals(type)) {
			sql.append(" and subtype='" + type + "'");
		}
		if (level1 != null && !"-1".equals(level1)) {
			sql.append(" and level1='" + level1 + "'");
		}

		if (status != null && !"-1".equals(status)) {
			sql.append(" and managesign='" + status + "'");
		}

		int flag = 0;
		if (businessid != null) {
			if (businessid != "-1") {
				String[] bids = businessid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								sql.append(" and ( businessid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								sql.append(" or businessid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					sql.append(") ");
				}
			}
		}
		return getSummary(sql.toString(), -1, -1);
	}

	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from system_eventlist order by id");
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

	public List loadByWhere(String where) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from system_eventlist " + where);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
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
			vo.setHappenTimes(rs.getInt("happenTimes"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		EventList vo = (EventList) baseVo;
		Calendar tempCal = (Calendar) vo.getRecordtime();
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		if (vo.getLasttime() == null) {
			vo.setLasttime(recordtime);
		}
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_eventlist(eventtype,eventlocation,content,level1,managesign,bak,recordtime,reportman,nodeid,businessid,oid,lasttime,subtype,managetime,subentity)values(");
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
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}

	public boolean save(List<EventList> list) {
		boolean result = false;
		try {
			for (int i = 0; i < list.size(); i++) {
				EventList vo = (EventList) list.get(i);
				Calendar tempCal = (Calendar) vo.getRecordtime();
				Date cc = tempCal.getTime();
				String recordtime = sdf.format(cc);
				if (vo.getLasttime() == null) {
					vo.setLasttime(recordtime);
				}
				StringBuffer sql = new StringBuffer(100);
				sql.append("insert into system_eventlist(id,eventtype,eventlocation,content,level1,managesign,bak,recordtime,reportman,nodeid,businessid,oid,subtype,managetime,subentity,lasttime)values(system_eventlist_seq.nextval,");
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
				conn.addBatch(sql.toString());
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo baseVo) {
		EventList vo = (EventList) baseVo;
		Calendar tempCal = (Calendar) vo.getRecordtime();
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update system_eventlist set eventtype='");
		sql.append(vo.getEventtype());
		sql.append("',eventlocation='");
		sql.append(vo.getEventlocation());
		sql.append("',content='");
		sql.append(vo.getContent());
		sql.append("',level1=");
		sql.append(vo.getLevel1());
		sql.append(",managesign=");
		sql.append(vo.getManagesign());
		sql.append(",bak='");
		sql.append(vo.getBak());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',recordtime='");
			sql.append(recordtime);
			sql.append("',reportman='");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',recordtime=");
			sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",reportman='");
		}
		sql.append(vo.getReportman());
		sql.append("',nodeid=");
		sql.append(vo.getNodeid());
		sql.append(",businessid='");
		sql.append(vo.getBusinessid());
		sql.append("',oid=");
		sql.append(vo.getOid());
		sql.append(",subtype='");
		sql.append(vo.getSubtype());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',managetime='");
			sql.append(vo.getManagetime());
			sql.append("',subentity='");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',managetime=");
			// Ĭ������´���ʱ��Ϊ��ǰ�¼�����ʱ��,��Ȼ��ORACLE���ݿ����޷���NULL�������ݿ�
			sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",subentity='");
		}
		sql.append(vo.getSubentity());
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

	public boolean update(String lasttime, String content, String id) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("update system_eventlist set lasttime='" + lasttime + "',content='" + content + "' where id=" + id);
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("update system_eventlist set lasttime=to_date('" + lasttime + "','YYYY-MM-DD HH24:MI:SS'),content='" + content + "' where id=" + id);
		}
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

	public boolean update(String lasttime, String level1, String content, String id) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("update system_eventlist set lasttime='" + lasttime + "',level1=" + level1 + ",content='" + content + "',managesign=1 where id=" + id);
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("update system_eventlist set lasttime=to_date('" + lasttime + "','YYYY-MM-DD HH24:MI:SS'),level1=" + level1 + ",content='" + content + "',managesign=1 where id=" + id);
		}

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ���� id ���޸� managesign
	 * 
	 * @param managesign
	 * @param id
	 * @return
	 */
	public boolean updateManagesignById(String managesign, String id) {
		String sql = "update system_eventlist set managesign='" + managesign + "' where id=" + id;
		return saveOrUpdate(sql);
	}
}
