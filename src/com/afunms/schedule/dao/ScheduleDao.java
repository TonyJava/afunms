package com.afunms.schedule.dao;

import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.schedule.model.Schedule;

@SuppressWarnings("unchecked")
public class ScheduleDao extends BaseDao implements DaoInterface {// 

	public ScheduleDao() {
		super("nms_schedule");
	}

	public boolean delete(String where) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_schedule " + where);
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_schedule where id='" + id[i] + "'");
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	public Schedule findByID(String id) {
		Schedule vo = null;
		try {
			rs = conn.executeQuery("select * from nms_schedule where id='" + id + "'");
			if (rs.next()) {
				vo = (Schedule) loadFromRS(rs);
			}
		} catch (Exception ex) {
			 ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return vo;
	}

	public int getCountByDate(String date) {
		int count = 0;
		try {
			rs = conn.executeQuery("select count(1) from nms_schedule where on_date >= '" + date + "'");
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}

	public Schedule getHandover(int userid) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(d);
		d.setTime(d.getTime() - 24 * 60 * 60 * 1000);

		Schedule vo = null;
		try {
			String sql = "select s.*,p.start_time from nms_schedule s,nms_period p where s.period = p.id and watcher=" + userid + " and on_date = '" + date + "'";
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				vo = (Schedule) loadFromRS(rs);// 有值班安排
			} else {
				sql = "select * from nms_schedule where watcher = " + userid + " and on_date < '" + date + "' order by on_date desc limit 1";
				rs = conn.executeQuery(sql);
				if (rs.next()) {
					vo = (Schedule) loadFromRS(rs);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return vo;
	}

	public List<String> getPosition(String sql) {
		List<String> list = new ArrayList<String>();
		try {
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}


	public Schedule getTakeover(int userid) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(d);
		Schedule vo = null;
		try {
			String sql = "select s.*,p.start_time from nms_schedule s,nms_period p where s.period = p.id and watcher=" + userid + " and on_date = '" + date + "'";
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				vo = (Schedule) loadFromRS(rs);// 有值班安排
				String sid = vo.getId();// 本次要值班记录的id号
				String cstatus = vo.getStatus();// 当前的状态 2表示已经接班
				String currStartTime = rs.getString("start_time");
				sql = "select count(1) from nms_schedule where on_date < '" + date + "' and position='" + vo.getPosition() + "'";
				rs = conn.executeQuery(sql);
				int count = 0;
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count == 0) {// 第一天值班
					sql = "select s.*,pe.start_time from nms_schedule s,nms_period pe where s.on_date = '" + date + "' and s.position = '" + vo.getPosition()
							+ "' and s.period = pe.id";
					rs = conn.executeQuery(sql);
					Time t1 = null, t2 = null;
					while (rs.next()) {
						if (rs.getString("watcher").equals(userid + "")) {
							t1 = rs.getTime("start_time");
						} else {
							t2 = rs.getTime("start_time");
						}
					}
					if (null != t1 && null != t2) {
						if (t1.before(t2)) {// 第一个班次 白班
							if ("1".equals(cstatus)) {
								cstatus = "0";
							} else if ("2".equals(cstatus)) {
								cstatus = "02";
							} else if ("3".equals(cstatus)) {
								cstatus = "03";
							}
						} else {// 第一天的第二个班次:查找第一个班次的值班信息
							sql = "select s.*,u.name from nms_schedule s,system_user u where s.watcher = u.id AND s.POSITION = '" + vo.getPosition() + "' and s.period != '"
									+ vo.getPeriod() + "' and s.on_date = '" + date + "'";
							rs = conn.executeQuery(sql);
							if (rs.next()) {
								vo = (Schedule) loadFromRS(rs);
								vo.setName(rs.getString("name"));
								vo.setSid(sid);
							}
						}
					}
				} else {// 查询上一班的值班记录
					sql = "SELECT s.*,p.start_time,u.name FROM nms_schedule s,nms_period p,SYSTEM_USER u WHERE s.period=p.id AND s.watcher = u.id AND s.POSITION = '"
							+ vo.getPosition() + "' AND s.on_date <= '" + date + "' AND s.id != '" + sid + "' ORDER BY on_date DESC,start_time DESC LIMIT 0, 2";
					rs = conn.executeQuery(sql);
					List<Schedule> list = new ArrayList<Schedule>();
					String[] startTimes = new String[2];
					int i = 0;
					while (rs.next()) {
						Schedule vo1 = (Schedule) loadFromRS(rs);
						vo1.setName(rs.getString("name"));
						vo1.setSid(sid);
						list.add(vo1);
						startTimes[i++] = rs.getString("start_time");
					}

					if (list.size() > 0) {
						vo = list.get(0);
						if (list.size() == 2) {
							String queryDate = sdf.format(vo.getOn_date());
							if (date.equals(queryDate) && currStartTime.compareTo(startTimes[0]) < 0) {
								vo = list.get(1);
							}
						}
					}
				}
				vo.setCstatus(cstatus);
			} else {// 今天没有值班安排
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return vo;
	}

	@Override
	public List listByPage(int curpage, int perpage) {
		return super.listByPage(curpage, perpage);
	}

	/**
	 * 载入所有记录
	 */
	public List<Schedule> loadAll() {
		List<Schedule> list = new ArrayList<Schedule>();
		try {
			rs = conn.executeQuery("select * from nms_schedule order by on_date,position,period");
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add((Schedule) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return list;
	}

	public List<Schedule> loadAll(String where) {
		List<Schedule> list = new ArrayList<Schedule>();
		try {
			rs = conn.executeQuery("select * from nms_schedule  " + where + " order by on_date,position,period");
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add((Schedule) loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Schedule vo = new Schedule();
		try {
			vo.setId(rs.getString("id"));
			vo.setOn_date(rs.getTimestamp("on_date"));
			vo.setWatcher(rs.getInt("watcher"));
			vo.setDescription(rs.getString("description"));
			vo.setPeriod(rs.getString("period"));
			vo.setPosition(rs.getString("position"));
			vo.setCreated_by(rs.getString("created_by"));
			vo.setCreated_on(rs.getTimestamp("created_on"));
			vo.setUpdated_by(rs.getString("updated_by"));
			vo.setUpdated_on(rs.getTimestamp("updated_on"));
			vo.setLog(rs.getString("log"));
			vo.setStatus(rs.getString("status"));
		} catch (Exception ex) {
			ex.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public Date query(String sql) {
		java.sql.Date date = null;
		try {
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				date = rs.getDate(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return date;
	}


	public boolean save(BaseVo vo) {
		return false;
	}

	public int save(List<Schedule> list) {
		int result = -1;
		try {
			for (int i = 0; i < list.size(); i++) {
				Schedule vo = list.get(i);
				StringBuffer sqlBf = new StringBuffer(100);
				sqlBf.append("insert into nms_schedule(on_date," + "watcher,description,period,position,CREATED_BY,CREATED_ON,status)");
				sqlBf.append("values('");
				sqlBf.append(vo.getOn_date());
				sqlBf.append("',");
				sqlBf.append(vo.getWatcher());
				sqlBf.append(",'");
				sqlBf.append(vo.getDescription());
				sqlBf.append("','");
				sqlBf.append(vo.getPeriod());
				sqlBf.append("','");
				sqlBf.append(vo.getPosition());
				sqlBf.append("','");
				sqlBf.append(vo.getCreated_by());
				sqlBf.append("','");
				sqlBf.append(vo.getCreated_on());
				sqlBf.append("','");
				sqlBf.append(vo.getStatus());
				sqlBf.append("')");
				conn.addBatch(sqlBf.toString());
			}
			conn.executeBatch();
			result = 1;
		} catch (Exception e) {
			result = -1;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public int save(Schedule vo) {
		int result = -1;
		try {
			StringBuffer sqlBf = new StringBuffer(100);
			sqlBf.append("insert into nms_schedule(on_date," + "watcher,description,period,position,CREATED_BY,CREATED_ON,status)");
			sqlBf.append("values('");
			sqlBf.append(vo.getOn_date());
			sqlBf.append("',");
			sqlBf.append(vo.getWatcher());
			sqlBf.append(",'");
			sqlBf.append(vo.getDescription());
			sqlBf.append("','");
			sqlBf.append(vo.getPeriod());
			sqlBf.append("','");
			sqlBf.append(vo.getPosition());
			sqlBf.append("','");
			sqlBf.append(vo.getCreated_by());
			sqlBf.append("','");
			sqlBf.append(vo.getCreated_on());
			sqlBf.append("','");
			sqlBf.append(vo.getStatus());
			sqlBf.append("')");
			conn.executeUpdate(sqlBf.toString());
			result = 1;
		} catch (Exception e) {
			result = -1;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean update(BaseVo vo) {
		Schedule schedule = (Schedule) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_schedule set status=");
		sql.append(schedule.getStatus());
		sql.append(",log='");
		sql.append(schedule.getLog());
		sql.append("',updated_by='");
		sql.append(schedule.getUpdated_by());
		sql.append("',updated_on='");
		sql.append(schedule.getUpdated_on());
		sql.append("' where id='");
		sql.append(schedule.getId());
		sql.append("'");
		return saveOrUpdate(sql.toString());
	}

	public void update(String id, int value) {
		try {
			conn.executeUpdate("update nms_schedule set status = " + value + " where id = '" + id + "'");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
