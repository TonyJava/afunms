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

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.wasmonitor.Was5cache;
import com.afunms.application.wasmonitor.Was5jdbc;
import com.afunms.application.wasmonitor.Was5jvminfo;
import com.afunms.application.wasmonitor.Was5session;
import com.afunms.application.wasmonitor.Was5system;
import com.afunms.application.wasmonitor.Was5thread;
import com.afunms.application.wasmonitor.Was5trans;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.util.DBConvert;

@SuppressWarnings("unchecked")
public class SlaNodeConfigDao extends BaseDao implements DaoInterface {

	public SlaNodeConfigDao() {
		super("nms_sla_config_node");
	}

	public boolean delete(String[] ids) {
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				delete(ids[i]);
			}
		}
		return true;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {

			CreateTableManager ctable = new CreateTableManager();
			// RTT
			ctable.deleteTable("slartt", id, "slartt");// rtt
			ctable.deleteTable("slartthour", id, "slartthour");// rtthour
			ctable.deleteTable("slarttday", id, "slarttday");// rttday

			// STATUS
			ctable.deleteTable("slastatus", id, "slastatus");// status
			ctable.deleteTable("slastatushour", id, "slastatushour");// statushour
			ctable.deleteTable("slastatusday", id, "slastatusday");// statusday
			conn.addBatch("delete from nms_sla_config_node where id=" + id);
			conn.executeBatch();
			try {
				// STATUS
				ctable.deleteTable("slajitter", id, "slajitter");// jitter
				ctable.deleteTable("slajitterhour", id, "slajitterhour");// jitterhour
				ctable.deleteTable("slajitterday", id, "slajitterday");// jitterday
			} catch (Exception e) {

			}

			result = true;
			try {
				// 同时删除事件表里的相关数据
				EventListDao eventdao = new EventListDao();
				eventdao.delete(Integer.parseInt(id), "ciscosla");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return result;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		SlaNodeConfig vo = new SlaNodeConfig();

		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setSlatype(rs.getString("slatype"));
			vo.setDescr(rs.getString("descr"));
			vo.setBak(rs.getString("bak"));
			vo.setIntervals(rs.getInt("intervals"));
			vo.setIntervalunit(rs.getString("intervalunit"));
			vo.setTelnetconfig_id(rs.getInt("telnetconfig_id"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setBid(rs.getString("bid"));
			vo.setEntrynumber(rs.getString("entrynumber"));
			vo.setDestip(rs.getString("destip"));
			vo.setDevicetype(rs.getString("devicetype"));
			vo.setCollecttype(rs.getString("collecttype"));
			vo.setAdminsign(rs.getString("adminsign"));
			vo.setOperatesign(rs.getString("operatesign"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vo;
	}

	public boolean save(List list) {
		boolean flag = true;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SlaNodeConfig vo1 = (SlaNodeConfig) list.get(i);
				save(vo1);
			}
		}
		return flag;
	}

	public boolean save(BaseVo vo) {
		boolean flag = true;
		SlaNodeConfig vo1 = (SlaNodeConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql
				.append("insert into nms_sla_config_node(id,name,telnetconfig_id,slatype,intervals,intervalunit,descr,bak,mon_flag,bid,entrynumber,destip,devicetype,collecttype,adminsign,operatesign) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getName());
		sql.append("',");
		sql.append(vo1.getTelnetconfig_id());
		sql.append(",'");
		sql.append(vo1.getSlatype());
		sql.append("',");
		sql.append(vo1.getIntervals());
		sql.append(",'");
		sql.append(vo1.getIntervalunit());
		sql.append("','");
		sql.append(vo1.getDescr());
		sql.append("','");
		sql.append(vo1.getBak());
		sql.append("',");
		sql.append(vo1.getMon_flag());
		sql.append(",'");
		sql.append(vo1.getBid());
		sql.append("','");
		if (vo1.getEntrynumber() != null && vo1.getEntrynumber().trim().length() > 0) {
			sql.append(vo1.getEntrynumber());
		} else {
			sql.append(" ");
		}

		sql.append("','");
		sql.append(vo1.getDestip());
		sql.append("','");
		sql.append(vo1.getDevicetype());
		sql.append("','");
		sql.append(vo1.getCollecttype());
		sql.append("','");
		sql.append(vo1.getAdminsign());
		sql.append("','");
		sql.append(vo1.getOperatesign());
		sql.append("')");
		try {
			SysLogger.info(sql.toString());
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			// RTT
			ctable.createTable("slartt", vo1.getId() + "", "slartt");// rtt
			ctable.createTable("slartthour", vo1.getId() + "", "slartthour");// rtthour
			ctable.createTable("slarttday", vo1.getId() + "", "slarttday");// rttday

			// STATUS
			ctable.createTable("slastatus", vo1.getId() + "", "slastatus");// status
			ctable.createTable("slastatushour", vo1.getId() + "", "slastatushour");// statushour
			ctable.createTable("slastatusday", vo1.getId() + "", "slastatusday");// statusday
			if (vo1.getSlatype().equalsIgnoreCase("jitter")) {
				ctable.createTable("slajitter", vo1.getId() + "", "slajitter");// rtt
				ctable.createTable("slajitterhour", vo1.getId() + "", "slajitterhour");// rtthour
				ctable.createTable("slajitterday", vo1.getId() + "", "slajitterday");// rttday
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			conn.close();
		}
		return flag;
	}

	public boolean update(BaseVo vo) {
		boolean flag = true;
		SlaNodeConfig vo1 = (SlaNodeConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_sla_config_node set name=");
		sql.append("'");
		sql.append(vo1.getName());
		sql.append("',telnetconfig_id=");
		sql.append(vo1.getTelnetconfig_id());
		sql.append(",slatype='");
		sql.append(vo1.getSlatype());
		sql.append("',intervals=");
		sql.append(vo1.getIntervals());
		sql.append(",intervalunit='");
		sql.append(vo1.getIntervalunit());
		sql.append("',descr='");
		sql.append(vo1.getDescr());
		sql.append("',bak='");
		sql.append(vo1.getBak());
		sql.append("',mon_flag=");
		sql.append(vo1.getMon_flag());
		sql.append(",bid='");
		sql.append(vo1.getBid());
		sql.append("',entrynumber='");
		sql.append(vo1.getEntrynumber());
		sql.append("',destip='");
		sql.append(vo1.getDestip());
		sql.append("',devicetype='");
		sql.append(vo1.getDevicetype());
		sql.append("',collecttype='");
		sql.append(vo1.getCollecttype());
		sql.append("',adminsign='");
		sql.append(vo1.getAdminsign());
		sql.append("',operatesign='");
		sql.append(vo1.getOperatesign());
		sql.append("' where id=" + vo1.getId());

		try {
			saveOrUpdate(sql.toString());

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return flag;

	}

	public List getSlaByBID(Vector bids) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( netid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or netid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + ")";
		}
		sql.append("select * from nms_sla_config_node " + wstr);
		return findByCriteria(sql.toString());
	}

	public List getSlaByBIDAndSlatype(Vector bids, String slatype) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( netid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or netid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + " and slatype='" + slatype + "')";
		}
		sql.append("select * from nms_sla_config_node " + wstr);
		return findByCriteria(sql.toString());
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(WasConfig wasconf, PingCollectEntity pingdata) {
		if (pingdata == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = pingdata.getIpaddress();
			if (pingdata.getRestype().equals("dynamic")) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = "wasping" + allipstr;

				String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','"
						+ pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit()
						+ "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "','" + time + "')";
				conn.executeUpdate(sql);
				// 进行PING操作检查
				if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
					// 连通率进行判断
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();

					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(wasconf.getId()), "middleware", "was");
					for (int k = 0; k < list.size(); k++) {
						AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(k);
						if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
							if (_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")) {
								CheckEventUtil checkeventutil = new CheckEventUtil();
								checkeventutil.checkMiddlewareEvent(wasconf, _alarmIndicatorsNode, pingdata.getThevalue());
							}
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();

		}
		return true;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Interfacecollectdata pingdata, String tablesub) {
		if (pingdata == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String ip = pingdata.getIpaddress();
			if (pingdata.getRestype().equals("dynamic")) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','"
						+ pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit()
						+ "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "','" + time + "')";
				conn.executeUpdate(sql);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5system was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "";
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,freeMemory,cpuUsageSinceServerStarted,cpuUsageSinceLastMeasurement,recordtime) " + "values('" + ip + "','"
							+ was5.getFreeMemory() + "','" + was5.getCpuUsageSinceServerStarted() + "','" + was5.getCpuUsageSinceLastMeasurement() + "','" + time + "')";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,freeMemory,cpuUsageSinceServerStarted,cpuUsageSinceLastMeasurement,recordtime) " + "values('" + ip + "','"
							+ was5.getFreeMemory() + "','" + was5.getCpuUsageSinceServerStarted() + "','" + was5.getCpuUsageSinceLastMeasurement() + "',to_date('" + time
							+ "','YYYY-MM-DD HH24:MI:SS'))";
				}
				conn.executeUpdate(sql);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5trans was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "";
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into "
							+ tablename
							+ "(ipaddress,activeCount,committedCount,rolledbackCount,globalTranTime,globalBegunCount,localBegunCount,localActiveCount,localTranTime,localTimeoutCount,localRolledbackCount,globalTimeoutCount,recordtime) "
							+ "values('" + ip + "','" + was5.getActiveCount() + "','" + was5.getCommittedCount() + "','" + was5.getRolledbackCount() + "','"
							+ was5.getGlobalTranTime() + "','" + was5.getGlobalBegunCount() + "','" + was5.getLocalBegunCount() + "','" + was5.getLocalActiveCount() + "','"
							+ was5.getLocalTranTime() + "','" + was5.getLocalTimeoutCount() + "','" + was5.getLocalRolledbackCount() + "','" + was5.getGlobalTimeoutCount() + "','"
							+ time + "')";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into "
							+ tablename
							+ "(ipaddress,activeCount,committedCount,rolledbackCount,globalTranTime,globalBegunCount,localBegunCount,localActiveCount,localTranTime,localTimeoutCount,localRolledbackCount,globalTimeoutCount,recordtime) "
							+ "values('" + ip + "','" + was5.getActiveCount() + "','" + was5.getCommittedCount() + "','" + was5.getRolledbackCount() + "','"
							+ was5.getGlobalTranTime() + "','" + was5.getGlobalBegunCount() + "','" + was5.getLocalBegunCount() + "','" + was5.getLocalActiveCount() + "','"
							+ was5.getLocalTranTime() + "','" + was5.getLocalTimeoutCount() + "','" + was5.getLocalRolledbackCount() + "','" + was5.getGlobalTimeoutCount()
							+ "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
				}

				conn.executeUpdate(sql);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5jdbc was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "";
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into "
							+ tablename
							+ "(ipaddress,createCount,closeCount,poolSize,freePoolSize,waitingThreadCount,percentUsed,useTime,waitTime,allocateCount,prepStmtCacheDiscardCount,jdbcTime,faultCount,recordtime) "
							+ "values('" + ip + "','" + was5.getCreateCount() + "','" + was5.getCloseCount() + "','" + was5.getPoolSize() + "','" + was5.getFreePoolSize() + "','"
							+ was5.getWaitingThreadCount() + "','" + was5.getPercentUsed() + "','" + was5.getUseTime() + "','" + was5.getWaitTime() + "','"
							+ was5.getAllocateCount() + "','" + was5.getPrepStmtCacheDiscardCount() + "','" + was5.getJdbcTime() + "','" + was5.getFaultCount() + "','" + time
							+ "')";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into "
							+ tablename
							+ "(ipaddress,createCount,closeCount,poolSize,freePoolSize,waitingThreadCount,percentUsed,useTime,waitTime,allocateCount,prepStmtCacheDiscardCount,jdbcTime,faultCount,recordtime) "
							+ "values('" + ip + "','" + was5.getCreateCount() + "','" + was5.getCloseCount() + "','" + was5.getPoolSize() + "','" + was5.getFreePoolSize() + "','"
							+ was5.getWaitingThreadCount() + "','" + was5.getPercentUsed() + "','" + was5.getUseTime() + "','" + was5.getWaitTime() + "','"
							+ was5.getAllocateCount() + "','" + was5.getPrepStmtCacheDiscardCount() + "','" + was5.getJdbcTime() + "','" + was5.getFaultCount() + "',to_date('"
							+ time + "','YYYY-MM-DD HH24:MI:SS'))";
				}
				conn.executeUpdate(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5session was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "";
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,liveCount,createCount,invalidateCount,lifeTime,activeCount,timeoutInvalidationCount,recordtime) " + "values('"
							+ ip + "','" + was5.getLiveCount() + "','" + was5.getCreateCount() + "','" + was5.getInvalidateCount() + "','" + was5.getLifeTime() + "','"
							+ was5.getActiveCount() + "','" + was5.getInvalidateCount() + "','" + time + "')";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,liveCount,createCount,invalidateCount,lifeTime,activeCount,timeoutInvalidationCount,recordtime) " + "values('"
							+ ip + "','" + was5.getLiveCount() + "','" + was5.getCreateCount() + "','" + was5.getInvalidateCount() + "','" + was5.getLifeTime() + "','"
							+ was5.getActiveCount() + "','" + was5.getInvalidateCount() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
				}

				conn.executeUpdate(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5jvminfo was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "insert into " + tablename + "(ipaddress,heapSize,freeMemory,usedMemory,upTime,memPer,recordtime) " + "values('" + ip + "','" + was5.getHeapSize()
						+ "','" + was5.getFreeMemory() + "','" + was5.getUsedMemory() + "','" + was5.getUpTime() + "','" + was5.getMemPer() + "',"
						+ DBConvert.mysqlAndOracleConvert(time) + ")";
				conn.executeUpdate(sql);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5cache was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "insert into " + tablename + "(ipaddress,inMemoryCacheCount,maxInMemoryCacheCount,timeoutInvalidationCount,recordtime)" + "values('" + ip + "','"
						+ was5.getInMemoryCacheCount() + "','" + was5.getMaxInMemoryCacheCount() + "','" + was5.getTimeoutInvalidationCount() + "',"
						+ DBConvert.mysqlAndOracleConvert(time) + ")";
				conn.executeUpdate(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	public synchronized boolean createHostData(Was5thread was5, String tablesub) {
		if (was5 == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = was5.getIpaddress();
			if (ip != null) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) was5.getRecordtime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";

				tablename = tablesub + allipstr;
				String delsql = "delete from " + tablename;
				conn.executeUpdate(delsql);
				String sql = "insert into " + tablename + "(ipaddress,createCount,destroyCount,poolSize,activeCount,recordtime)" + "values('" + ip + "','" + was5.getCreateCount()
						+ "','" + was5.getDestroyCount() + "','" + was5.getPoolSize() + "','" + was5.getActiveCount() + "'," + DBConvert.mysqlAndOracleConvert(time) + ")";
				conn.executeUpdate(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			System.gc();
		}
		return true;
	}

	// 按采集间隔和是否监视获取列表信息
	public List getConfigByIntervalAndUnitAndFlag(int intervals, String intervalunit, int flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_sla_config_node where intervals = " + intervals + " and intervalunit='" + intervalunit + "' and mon_flag = " + flag);
		return findByCriteria(sql.toString());
	}

	// 按采集间隔、是否监视、采集方式获取列表信息
	public List getConfigByIntervalAndUnitAndFlagAndColltype(int intervals, String intervalunit, int flag, String collecttype) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_sla_config_node where intervals = " + intervals + " and intervalunit='" + intervalunit + "' and mon_flag = " + flag + " and collecttype='"
				+ collecttype + "'");
		return findByCriteria(sql.toString());
	}

	public Hashtable getCategory(String configid, String category, String subentity, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				StringBuffer sb = new StringBuffer();
				if (category.equals("RTT")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from slartt" + configid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from slartt" + configid + " h where ");
					}
				} else if (category.equals("status")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from slastatus" + configid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from slastatus" + configid + " h where ");
					}
				}

				sb.append(" h.collecttime >= ");
				sb.append(DBConvert.mysqlAndOracleConvert(starttime));
				sb.append(" and  h.collecttime <= ");
				sb.append(DBConvert.mysqlAndOracleConvert(endtime));
				sb.append(" order by h.collecttime asc");
				rs = dbmanager.executeQuery(sb.toString());
				List list1 = new ArrayList();
				String unit = "";
				double tempfloat = 0;
				double pingcon = 0;
				double rttcon = 0;
				int downnum = 0;
				int i = 0;
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					if (category.equals("RTT")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
						rttcon = rttcon + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);

					} else if (category.equals("status")) {
						pingcon = pingcon + getfloat(thevalue);
						if (i == 1)
							tempfloat = getfloat(thevalue);
						if (tempfloat > getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					}
					list1.add(v);
				}
				rs.close();

				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector) list1.get(0);
						unit = (String) tempV.get(2);
					}
				}
				if (category.equals("status")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				} else if (category.equals("RTT")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(rttcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
					}
				}
				hash.put("size", size);
				hash.put("max", CEIString.round(tempfloat, 2) + unit);
				hash.put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum / 100.0).doubleValue();
		}
		return snum;
	}

	private String emitStr(String num) {
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
		}
		return num;
	}

	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_sla_config_node where id=" + id);
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception e) {
			SysLogger.error("SlaNodeConfigDao.findByID()", e);
			vo = null;
		}
		return vo;
	}

	/**
	 * 得到下一个主键
	 */
	public synchronized int getNextID() {
		int id = 0;
		try {
			rs = conn.executeQuery("select max(id) from nms_sla_config_node");
			if (rs.next())
				id = rs.getInt(1) + 1;
		} catch (Exception ex) {
			SysLogger.error("BaseDao.getNextID()", ex);
			id = 0;
		}
		return id;
	}

	public static void main(String args[]) throws SQLException {
		WasConfigDao wasconf = new WasConfigDao();
		wasconf.findByID("137");

	}

}