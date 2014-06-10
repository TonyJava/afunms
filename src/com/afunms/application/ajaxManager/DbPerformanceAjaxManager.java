package com.afunms.application.ajaxManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.jfree.data.general.DefaultPieDataset;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.MySqlSpaceConfigDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.manage.HostApplyManager;
import com.afunms.application.manage.OracleManager;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.util.DBPool;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CreateAmColumnPic;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.CreatePiePicture;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.loader.DBLoader;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;

@SuppressWarnings("rawtypes")
public class DbPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String[] oracleTbArray = null;
	private static String[] sqlServerTbArray = null;
	private static Hashtable<String, String> rsc_type_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> req_mode_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> mode_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> req_status_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> req_ownertype_ht = new Hashtable<String, String>();

	static {
		oracleTbArray = new String[] { "nms_oracontrfile", "nms_oracursors", "nms_oradbio", "nms_oraextent", "nms_oraisarchive", "nms_orakeepobj", "nms_oralock", "nms_oralogfile", "nms_oramemperfvalue", "nms_oramemvalue", "nms_orarollback", "nms_orasessiondata", "nms_oraspaces", "nms_orastatus", "nms_orasys", "nms_oratables", "nms_oratopsql", "nms_orauserinfo", "nms_orawait", "nms_oratopsql_sort", "nms_oratopsql_readwrite", "nms_oralockinfo", "nms_orabaseinfo" };
		sqlServerTbArray = new String[] { "nms_sqlservercaches", "nms_sqlserverconns", "nms_sqlserverdbvalue", "nms_sqlserverinfo_v", "nms_sqlserverlockinfo_v", "nms_sqlserverlocks", "nms_sqlservermems", "nms_sqlserverpages", "nms_sqlserverscans", "nms_sqlserversqls", "nms_sqlserverstatisticshash", "nms_sqlserverstatus", "nms_sqlserversysvalue" };

		rsc_type_ht.put("1", "NULL 资源（未使用）");
		rsc_type_ht.put("2", "数据库");
		rsc_type_ht.put("3", "文件");
		rsc_type_ht.put("4", "索引");
		rsc_type_ht.put("5", "表");
		rsc_type_ht.put("6", "页");
		rsc_type_ht.put("7", "键");
		rsc_type_ht.put("8", "扩展盘区");
		rsc_type_ht.put("9", "RID（行 ID)");
		rsc_type_ht.put("10", "应用程序");

		req_mode_ht.put("0", "NULL");
		req_mode_ht.put("1", "Sch-S");
		req_mode_ht.put("2", "Sch-M");
		req_mode_ht.put("3", "S");
		req_mode_ht.put("4", "U");
		req_mode_ht.put("5", "X");
		req_mode_ht.put("6", "IS");
		req_mode_ht.put("7", "IU");
		req_mode_ht.put("8", "IX");
		req_mode_ht.put("9", "SIU");
		req_mode_ht.put("10", "SIX");
		req_mode_ht.put("11", "UIX");
		req_mode_ht.put("12", "BU");
		req_mode_ht.put("13", "RangeS_S");
		req_mode_ht.put("14", "RangeS_U");
		req_mode_ht.put("15", "RangeI_N");
		req_mode_ht.put("16", "RangeI_S");
		req_mode_ht.put("17", "RangeI_U");
		req_mode_ht.put("18", "RangeI_X");
		req_mode_ht.put("19", "RangeX_S");
		req_mode_ht.put("20", "RangeX_U");
		req_mode_ht.put("21", "RangeX_X");

		req_status_ht.put("1", "已授予	");
		req_status_ht.put("2", "正在转换");
		req_status_ht.put("3", "正在等待");

		req_ownertype_ht.put("1", "事务");
		req_ownertype_ht.put("2", "游标");
		req_ownertype_ht.put("3", "会话");
		req_ownertype_ht.put("4", "ExSession");

		mode_ht.put("0", "不授权访问资源");
		mode_ht.put("1", "架构稳定性，确保不在任何会话控制架构元素上的架构稳定性锁时除去架构元素，如表或索引。");
		mode_ht.put("2", "架构修改，必须由任何要更改指定资源架构的会话进行控制。确保没有其它的会话正在引用指定的对象。");
		mode_ht.put("3", "共享，授权控制会话对资源进行共享访问。");
		mode_ht.put("4", "更新，表示在最终可能更新的资源上获取更新锁。用于防止常见形式的死锁，这类死锁在多个会话锁定资源并且稍后可能更新资源时发生。");
		mode_ht.put("5", "排它，授权控制会话对资源进行排它访问。");
		mode_ht.put("6", "意向共享，表示有意将 S 锁放置在锁层次结构内的某个从属资源上。");
		mode_ht.put("7", "意向更新，表示有意将 U 锁放置在锁层次结构内的某个从属资源上。");
		mode_ht.put("8", "意向排它，表示有意将 X 锁放置在锁层次结构内的某个从属资源上。");
		mode_ht.put("9", "共享意向更新，表示对有意在锁层次结构内的从属资源上获取更新锁的资源进行共享访问。");
		mode_ht.put("10", "共享意向排它，表示对有意在锁层次结构内的从属资源上获取排它锁的资源进行共享访问。");
		mode_ht.put("11", "更新意向排它，表示更新锁控制有意在锁层次结构内的从属资源上获取排它锁的资源。");
		mode_ht.put("12", "大容量操作");
		mode_ht.put("13", "共享键范围和共享资源锁，表示可串行范围扫描。");
		mode_ht.put("14", "共享键范围和更新资源锁，表示可串行更新扫描。");
		mode_ht.put("15", "插入键范围和空资源锁，用于在索引中插入新键之前测试范围。");
		mode_ht.put("16", "通过 RangeI_N 和 S 锁的重叠创建的键范围转换锁");
		mode_ht.put("17", "通过 RangeI_N 和 U 锁的重叠创建的键范围转换锁");
		mode_ht.put("18", "通过 RangeI_N 和 X 锁的重叠创建的键范围转换锁");
		mode_ht.put("19", "通过 RangeI_N 和 RangeS_S 锁的重叠创建的键范围转换锁");
		mode_ht.put("20", "通过 RangeI_N 和 RangeS_U 锁的重叠创建的键范围转换锁");
		mode_ht.put("21", "排它键范围和排它资源锁，该转换锁在更新范围中的键时使用。");

	}

	// by lyl
	private void getDbNodeDataByType() {
		String type = getParaValue("type");
		int dbtype = 1;
		dbtype = getDbtypeFromString(type);
		List list = new ArrayList();
		String sql = "";
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String curentbids = current_user.getBusinessids();

		String selectbids = getParaValue("selectbids");

		StringBuffer sql1 = new StringBuffer();
		StringBuffer s1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;

		if (selectbids != null) {
			if (selectbids != "-1") {
				String[] bids = selectbids.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
								flag = 1;
							} else {
								s2.append(" or bid like '%" + bids[i].trim() + "%' ");
							}
						}
					}
					s2.append(") ");
				}

			}
		}
		sql2.append("select * from app_db_node where 1=1 and dbtype=" + dbtype + s2.toString());

		flag = 0;
		if (current_user.getRole() != 0 && curentbids != null) {
			if (curentbids != "-1") {
				String[] bids = curentbids.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s1.append(" and ( bid like '%" + bids[i].trim() + "%' ");
								flag = 1;
							} else {
								s1.append(" or bid like '%" + bids[i].trim() + "%' ");
							}
						}
					}
					s1.append(") ");
				}

			}
		}
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1.append("select * from (" + sql2.toString() + ") as t where 1=1 " + s1.toString());
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1.append("select * from (" + sql2.toString() + ") where 1=1 " + s1.toString());
		}

		String treeBid = request.getParameter("treeBid");
		if (treeBid != null && treeBid.trim().length() > 0) {
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if (treeBids != null) {
				for (int i = 0; i < treeBids.length; i++) {
					if (treeBids[i].trim().length() > 0) {
						sql1 = sql1.append(" and " + "bid" + " like '%," + treeBids[i].trim() + ",%'");
					}
				}
			}
		}

		sql = sql1.toString();
		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		List monitorDBDTOList = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);
			MonitorDBDTO monitorDBDTO = null;
			monitorDBDTO = getMonitorDBDTOByDBVo(vo, 0);
			monitorDBDTOList.add(monitorDBDTO);
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != monitorDBDTOList && monitorDBDTOList.size() > 0) {
			for (int i = 0; i < monitorDBDTOList.size(); i++) {
				MonitorDBDTO monitorDBDTO = (MonitorDBDTO) monitorDBDTOList.get(i);
				Hashtable eventListSummary = monitorDBDTO.getEventListSummary();
				String generalAlarm = (String) eventListSummary.get("generalAlarm");
				String urgentAlarm = (String) eventListSummary.get("urgentAlarm");
				String seriousAlarm = (String) eventListSummary.get("seriousAlarm");

				String status = monitorDBDTO.getStatus();

				String statusImg = "";
				if ("1".equals(status)) {
					statusImg = "a_level_1.gif";
				} else if ("2".equals(status)) {
					statusImg = "a_level_2.gif";
				} else if ("3".equals(status)) {
					statusImg = "a_level_3.gif";
				} else {
					statusImg = "a_level_0.gif";
				}
				jsonString.append("{\"dbid\":\"");
				jsonString.append(monitorDBDTO.getId());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(monitorDBDTO.getAlias());
				jsonString.append("\",");

				jsonString.append("\"dbtype\":\"");
				jsonString.append(monitorDBDTO.getDbtype());
				jsonString.append("\",");

				jsonString.append("\"dbname\":\"");
				jsonString.append(monitorDBDTO.getDbname());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(monitorDBDTO.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"port\":\"");
				jsonString.append(monitorDBDTO.getPort());
				jsonString.append("\",");

				jsonString.append("\"managed\":\"");
				jsonString.append(monitorDBDTO.getMon_flag());
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(statusImg);
				jsonString.append("\",");

				jsonString.append("\"generalAlarm\":\"");
				jsonString.append(generalAlarm);
				jsonString.append("\",");

				jsonString.append("\"urgentAlarm\":\"");
				jsonString.append(urgentAlarm);
				jsonString.append("\",");

				jsonString.append("\"seriousAlarm\":\"");
				jsonString.append(seriousAlarm);
				jsonString.append("\",");

				jsonString.append("\"pingvalue\":\"");
				jsonString.append(monitorDBDTO.getPingValue());
				jsonString.append("\"}");

				if (i != monitorDBDTOList.size() - 1) {
					jsonString.append(",");
				}

			}
		}
		jsonString.append("],total:" + monitorDBDTOList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	/**
	 * 通过 DBVo 来组装 MonitorDBDTO
	 * 
	 * @author nielin
	 * @date 2010-08-13
	 * @param <code>DBVo</code>
	 * @return
	 */
	public MonitorDBDTO getMonitorDBDTOByDBVo(DBVo vo, int sid) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());
		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = vo.getId(); // id
		String ipAddress = vo.getIpAddress(); // ipaddress
		String alias = vo.getAlias(); // 名称
		String dbname = vo.getDbName(); // 数据库名称
		String port = vo.getPort(); // 端口
		String mon_flag = "否";

		String dbtype = ""; // 数据库类型
		String status = ""; // 状态

		String pingValue = "未知"; // 可用性
		int alarmLevel = 0;
		Hashtable eventListSummary = new Hashtable(); // 告警

		if (vo.getManaged() == 1) {
			mon_flag = "是";
		}
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		Node DBNode = null;
		if (sid != 0) {
			DBNode = PollingEngine.getInstance().getDbByID(sid);
		} else {
			DBNode = PollingEngine.getInstance().getDbByID(vo.getId());
		}
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(DBNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}
		status = alarmLevel + "";
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo dbTypeVo = null;
		try {
			dbTypeVo = (DBTypeVo) typedao.findByID(String.valueOf(vo.getDbtype()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		if (dbTypeVo != null) {
			dbtype = dbTypeVo.getDbtype();
		} else {
			dbtype = "未知";
		}

		if ("Oracle".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			Hashtable oracleHash = (Hashtable) ShareData.getSharedata().get(ipAddress + ":" + id);
			try {
				String statusStr = "-1";
				if (oracleHash != null) {
					statusStr = String.valueOf(oracleHash.get("ping"));
				}
				if ("100".equals(statusStr)) {
					pingValue = "正在运行";
				} else if ("0".equals(statusStr)) {
					pingValue = "服务停止";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		} else if ("SQLServer".equalsIgnoreCase(dbtype)) {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable sqlserverHash = (Hashtable) ShareData.getSharedata().get(serverip);
			try {
				String statusStr = "-1";
				if (sqlserverHash != null) {
					statusStr = String.valueOf(sqlserverHash.get("ping"));
				}
				if ("100".equals(statusStr)) {
					pingValue = "正在运行";
				} else if ("0".equals(statusStr)) {
					pingValue = "服务停止";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("MySql".equalsIgnoreCase(dbtype)) {
			Hashtable mysqlHashtable = (Hashtable) ShareData.getMySqlmonitordata().get(vo.getIpAddress());
			if (null != mysqlHashtable) {
				pingValue = (String) mysqlHashtable.get("runningflag");
			}
		} else if ("DB2".equalsIgnoreCase(dbtype)) {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			DBDao dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			String statusStr = "0";
			Hashtable tempStatusHashtable = null;
			try {
				tempStatusHashtable = dao.getDB2_nmsstatus(serverip + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				statusStr = (String) tempStatusHashtable.get("status");
			}
			if (statusStr.equals("1")) {
				pingValue = "正在运行";
			}
		} else if ("Sybase".equalsIgnoreCase(dbtype)) {
			Hashtable sysbaseHashtable = (Hashtable) ShareData.getSysbasedata().get(vo.getIpAddress());
			if (null != sysbaseHashtable) {
				pingValue = (String) sysbaseHashtable.get("runningflag");
			}
		} else if ("Informix".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String statusStr;
			try {
				statusStr = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
				if ("1".equalsIgnoreCase(statusStr)) {
					pingValue = "正在运行";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where subtype = 'db' and nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where subtype = 'db' and nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		MonitorDBDTO monitorDBDTO = new MonitorDBDTO();
		monitorDBDTO.setId(id);
		monitorDBDTO.setAlias(alias);
		monitorDBDTO.setDbname(dbname);
		monitorDBDTO.setDbtype(dbtype);
		monitorDBDTO.setPingValue(pingValue);
		monitorDBDTO.setEventListSummary(eventListSummary);
		monitorDBDTO.setIpAddress(ipAddress);
		monitorDBDTO.setPort(port);
		monitorDBDTO.setStatus(status);
		monitorDBDTO.setMon_flag(mon_flag);

		return monitorDBDTO;
	}

	@SuppressWarnings("unchecked")
	private void addDb() {
		DBVo vo = new DBVo();
		vo.setUser(getParaValue("user"));
		String password = getParaValue("password");
		String enpassword = "";
		try {
			enpassword = EncryptUtil.encode(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		vo.setPassword(enpassword);
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setBid(getParaValue("bid"));
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setId(KeyGenerator.getInstance().getNextKey());
		DBLoader dbloader = new DBLoader();
		dbloader.loadOne(vo);
		// 放到内存中
		ShareData.getDBList().add(vo);
		DBDao dao = new DBDao();
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// 保存应用
		HostApplyManager.save(vo);
		// 刷新内存中的数据库列表
		new DBLoader().refreshDBConfiglist();

		DBTypeVo dbTypeVo = null;
		DBTypeDao typedao = null;
		try {
			typedao = new DBTypeDao();
			dbTypeVo = (DBTypeVo) typedao.findByID(getParaValue("dbtype"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}

		// 初始化告警指标
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), AlarmConstant.TYPE_DB, dbTypeVo.getDbtype());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// 初始化采集指标
		try {
			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			if (vo.getCollecttype() == 2) {
				vo.setCollecttype(1);
			}
			nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", AlarmConstant.TYPE_DB, dbTypeVo.getDbtype(), "1", vo.getCollecttype());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// 对数据库进行数据采集
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// 获取被启用的数据库所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(vo.getId() + "", 1, "db", dbTypeVo.getDbtype());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null) {
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		}
		Hashtable gatherHash = new Hashtable();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			// 数据库采集指标
			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}

		out.print("添加成功");
		out.flush();
	}

	private void deleteDbs() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		if (ids != null && ids.length > 0) {
			DBTypeDao typeDao = null;
			DBDao dbDao = null;
			DBTypeVo typeVo = null;
			DBVo vo = null;

			NodeGatherIndicatorsDao nodeDao = null;
			AlarmIndicatorsNodeDao alarmDao = null;

			String hex = (String) null;
			String serverIp = (String) null;
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				try {
					typeDao = new DBTypeDao();
					dbDao = new DBDao();
					vo = (DBVo) dbDao.findByID(id);
					typeVo = (DBTypeVo) typeDao.findByID(String.valueOf(vo.getDbtype()));

					// 删除采集指标
					nodeDao = new NodeGatherIndicatorsDao();
					try {
						nodeDao.deleteByNodeIdAndTypeAndSubtype(id, "db", typeVo.getDbtype().toLowerCase());
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						nodeDao.close();
					}
					// 删除告警阀值
					alarmDao = new AlarmIndicatorsNodeDao();
					try {
						alarmDao.deleteByNodeId(id, "db", typeVo.getDbtype().toLowerCase());
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						alarmDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (typeVo.getDbtype().toLowerCase().equals("oracle")) {
					dbDao = new DBDao();
					OraspaceconfigDao configDao = new OraspaceconfigDao();
					hex = IpTranslation.formIpToHex(vo.getIpAddress());
					serverIp = hex + ":" + id;
					try {
						configDao.deleteByIP(id);
						dbDao.clearTable("system_oraspaceconf", serverIp);
						dbDao.clearTablesData(oracleTbArray, serverIp);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						if (null != dbDao) {
							dbDao.close();
						}
						if (null != configDao) {
							configDao.close();
						}
					}
				} else if (typeVo.getDbtype().toLowerCase().equals("sqlserver")) {
					dbDao = new DBDao();
					SqldbconfigDao sqlConfigDao = new SqldbconfigDao();
					hex = IpTranslation.formIpToHex(vo.getIpAddress());
					serverIp = hex + ":" + vo.getAlias();
					try {
						sqlConfigDao.deleteByIP(id);
						dbDao.clearTable("system_sqldbconf", vo.getIpAddress());
						dbDao.clearTablesData(sqlServerTbArray, serverIp);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						if (null != dbDao) {
							dbDao.close();
						}
						if (null != sqlConfigDao) {
							sqlConfigDao.close();
						}
					}
					ShareData.getSqlserverdata().remove(vo.getIpAddress());
				} else if (typeVo.getDbtype().toLowerCase().equals("mysql")) {
					dbDao = new DBDao();
					MySqlSpaceConfigDao mysqlConfigDao = new MySqlSpaceConfigDao();
					hex = IpTranslation.formIpToHex(vo.getIpAddress());
					serverIp = hex + ":" + vo.getId();
					try {
						mysqlConfigDao.deleteByIP(id);
						dbDao.clearTable("system_mysqlspaceconf", vo.getIpAddress());
						dbDao.clearTableData("nms_mysqlinfo", serverIp);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						if (null != dbDao) {
							dbDao.close();
						}
						if (null != mysqlConfigDao) {
							mysqlConfigDao.close();
						}
					}
					ShareData.getMySqlmonitordata().remove(vo.getIpAddress());
				}

				dbDao = new DBDao();
				try {
					dbDao.delete(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != dbDao) {
						dbDao.close();
					}
				}
				int nodeId = Integer.parseInt(id);
				PollingEngine.getInstance().deleteDbByID(nodeId);
				DBPool.getInstance().removeConnect(nodeId);
				new DBLoader().refreshDBConfiglist();
			}
			out.print("操作成功");
			out.flush();
		}
	}

	private void getMysqlNodeSystem() {
		DBVo vo = new DBVo();
		String pingconavg = "0";
		String basePath = ""; // 基本路径
		String dataPath = ""; // 数据路径
		String logerrorPath = ""; // 数据路径
		String version = ""; // 数据库版本
		String hostOS = ""; // 服务器操作系统
		int doneFlag = 0;
		String id = getParaValue("id");
		String dbtype = "";
		String managed = "被管理";
		String runstr = "<font color=red>服务停止</font>";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			dbtype = typevo.getDbdesc();

			if (vo.getManaged() == 0)
				managed = "未管理";

			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			DBDao dbDao = null;
			Hashtable ipData = null;
			try {
				dbDao = new DBDao();
				ipData = dbDao.getMysqlDataByServerip(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbDao != null) {
					dbDao.close();
				}
			}
			if (ipData != null && ipData.size() > 0) {
				runstr = (String) ipData.get("runningflag");
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// 判断是否已经获取了当前的配置信息
					if (doneFlag == 1)
						break;
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (returnValue.containsKey("configVal")) {
								Hashtable configVal = (Hashtable) returnValue.get("configVal");
								if (configVal.containsKey("basePath"))
									basePath = (String) configVal.get("basePath");
								if (configVal.containsKey("dataPath"))
									dataPath = (String) configVal.get("dataPath");
								if (configVal.containsKey("logerrorPath"))
									logerrorPath = (String) configVal.get("logerrorPath");
								if (configVal.containsKey("version"))
									version = (String) configVal.get("version");
								if (configVal.containsKey("hostOS"))
									hostOS = (String) configVal.get("hostOS");
								doneFlag = 1;
							}
						}
					}
				}

			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = vo.getIpAddress();

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			double avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			String pingavg = String.valueOf(Math.round(avgpingcon));
			CreateMetersPic cmp = new CreateMetersPic();
			String pathPing = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashBoardGray.png";
			cmp.createChartByParam(newip, pingavg, pathPing, "连通率", "pingdata");
			CreatePiePicture _cpp = new CreatePiePicture();
			_cpp.createAvgPingPic(newip, avgpingcon);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != vo) {
			jsonString.append("{\"alias\":\"");
			jsonString.append(vo.getAlias());
			jsonString.append("\",");

			jsonString.append("\"dbname\":\"");
			jsonString.append(vo.getDbName());
			jsonString.append("\",");

			jsonString.append("\"dbtype\":\"");
			jsonString.append(dbtype);
			jsonString.append("\",");

			jsonString.append("\"ipaddress\":\"");
			jsonString.append(vo.getIpAddress());
			jsonString.append("\",");

			jsonString.append("\"port\":\"");
			jsonString.append(vo.getPort());
			jsonString.append("\",");

			jsonString.append("\"managed\":\"");
			jsonString.append(managed);
			jsonString.append("\",");

			jsonString.append("\"status\":\"");
			jsonString.append(runstr);
			jsonString.append("\",");

			jsonString.append("\"version\":\"");
			jsonString.append(version);
			jsonString.append("\",");

			jsonString.append("\"hostOS\":\"");
			jsonString.append(hostOS);
			jsonString.append("\",");

			jsonString.append("\"basePath\":\"");
			jsonString.append(basePath);
			jsonString.append("\",");

			jsonString.append("\"dataPath\":\"");
			jsonString.append(dataPath);
			jsonString.append("\",");

			jsonString.append("\"logerrorPath\":\"");
			jsonString.append(logerrorPath);
			jsonString.append("\"}");

		}
		jsonString.append("],total:" + 1 + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void dbReadyEdit() {
		DBDao dao = new DBDao();
		BusinessDao bussdao = new BusinessDao();

		DBVo vo = null;
		List allbuss = new ArrayList();
		List bidlist = new ArrayList();
		String bid = "";
		try {
			vo = (DBVo) dao.findByID(getParaValue("id"));
			allbuss = bussdao.loadAll();
			bid = vo.getBid();
			String id[] = bid.split(",");
			bidlist = new ArrayList();
			if (id != null && id.length > 0) {
				for (int i = 0; i < id.length; i++) {
					bidlist.add(id[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (bussdao != null) {
				bussdao.close();
			}
		}
		String bussName = "";
		if (allbuss.size() > 0) {
			for (int i = 0; i < allbuss.size(); i++) {
				Business buss = (Business) allbuss.get(i);
				if (bidlist.contains(buss.getId() + "")) {
					bussName = bussName + ',' + buss.getName();
				}
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (vo != null) {
			jsonString.append("{\"alias\":\"");
			jsonString.append(vo.getAlias());
			jsonString.append("\",");

			jsonString.append("\"ipaddress\":\"");
			jsonString.append(vo.getIpAddress());
			jsonString.append("\",");

			jsonString.append("\"port\":\"");
			jsonString.append(vo.getPort());
			jsonString.append("\",");

			jsonString.append("\"dbname\":\"");
			jsonString.append(vo.getDbName());
			jsonString.append("\",");

			jsonString.append("\"category\":\"");
			jsonString.append(vo.getCategory());
			jsonString.append("\",");

			jsonString.append("\"dbuse\":\"");
			jsonString.append(vo.getDbuse());
			jsonString.append("\",");

			jsonString.append("\"bid\":\"");
			jsonString.append(bussName);
			jsonString.append("\",");

			jsonString.append("\"bidvalue\":\"");
			jsonString.append(bid);
			jsonString.append("\",");

			jsonString.append("\"managed\":\"");
			jsonString.append(vo.getManaged());
			jsonString.append("\",");

			jsonString.append("\"collecttype\":\"");
			jsonString.append(vo.getCollecttype());
			jsonString.append("\",");

			jsonString.append("\"user\":\"");
			jsonString.append(vo.getUser());
			jsonString.append("\",");

			jsonString.append("\"password\":\"");
			jsonString.append(vo.getPassword());
			jsonString.append("\"}");
			jsonString.append("],total:" + 1 + "}");
		} else {
			jsonString.append("],total:" + 0 + "}");
		}
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings("unchecked")
	private void getMysqlConnectDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String hex = IpTranslation.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// 判断是否已经获取了当前的配置信息
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("sessionsDetail")) {
							// 存在数据库连接信息
							sessionlist.add((List) returnValue.get("sessionsDetail"));
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		int size = 0;
		if (sessionlist != null && sessionlist.size() > 0) {
			for (int i = 0; i < sessionlist.size(); i++) {
				List ipsessionlist = (List) sessionlist.get(i);
				size = ipsessionlist.size();
				if (ipsessionlist != null && ipsessionlist.size() > 0) {
					for (int k = 0; k < ipsessionlist.size(); k++) {
						String[] sessions = (String[]) ipsessionlist.get(k);
						if (sessions != null && sessions.length == 5) {
							jsonString.append("{\"dbname\":\"");
							jsonString.append(sessions[4]);
							jsonString.append("\",");

							jsonString.append("\"username\":\"");
							jsonString.append(sessions[0]);
							jsonString.append("\",");

							jsonString.append("\"hostname\":\"");
							jsonString.append(sessions[1]);
							jsonString.append("\",");

							jsonString.append("\"comm\":\"");
							jsonString.append(sessions[2]);
							jsonString.append("\",");

							jsonString.append("\"conntime\":\"");
							jsonString.append(sessions[3]);
							jsonString.append("\"}");
							if (k != ipsessionlist.size() - 1) {
								jsonString.append(",");
							}
						}
					}
				}
			}
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString);
		out.flush();
	}

	private void getMysqlTablesDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String hex = IpTranslation.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		List tableslist = new ArrayList();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// 判断是否已经获取了当前的配置信息
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("tablesDetail")) {
							// 存在数据库连接信息
							tableslist = (List) returnValue.get("tablesDetail");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableslist != null && tableslist.size() > 0) {
			for (int i = 0; i < tableslist.size(); i++) {
				String[] tables = (String[]) tableslist.get(i);
				if (tables != null && tables.length == 4) {
					jsonString.append("{\"tablename\":\"");
					jsonString.append(tables[0]);
					jsonString.append("\",");

					jsonString.append("\"tablerows\":\"");
					jsonString.append(tables[1]);
					jsonString.append("\",");

					jsonString.append("\"tablesize\":\"");
					jsonString.append(tables[2]);
					jsonString.append("\",");

					jsonString.append("\"createtime\":\"");
					jsonString.append(tables[3]);
					jsonString.append("\"}");
					if (i != tableslist.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + tableslist.size() + "}");
		out.print(jsonString);
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlSpacesDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector Val = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// 判断是否已经获取了当前的配置信息
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("Val")) {
							Val = (Vector) returnValue.get("Val");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (Val != null && Val.size() > 0) {
			for (int i = 0; i < Val.size(); i++) {
				Hashtable return_value = (Hashtable) Val.get(i);
				if (return_value != null && return_value.size() > 0) {
					String name = return_value.get("variable_name").toString();
					String value = return_value.get("value").toString();
					name = getValueFromNameForMysqlSpace(name);
					jsonString.append("{\"name\":\"");
					jsonString.append(name);
					jsonString.append("\",");

					jsonString.append("\"value\":\"");
					jsonString.append(value);
					jsonString.append("\"}");
					if (i != Val.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + Val.size() + "}");
		out.print(jsonString);
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlStatusDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector tableinfo_v = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// 判断是否已经获取了当前的配置信息
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("global_status")) {
							tableinfo_v = (Vector) returnValue.get("global_status");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableinfo_v != null && tableinfo_v.size() > 0) {
			for (int i = 0; i < tableinfo_v.size(); i++) {
				Hashtable return_value = (Hashtable) tableinfo_v.get(i);
				if (return_value != null && return_value.size() > 0) {
					String name = return_value.get("variable_name").toString();
					String value = return_value.get("value").toString();
					name = getSizeForMysqlStatus(name);
					jsonString.append("{\"name\":\"");
					jsonString.append(name);
					jsonString.append("\",");

					jsonString.append("\"value\":\"");
					jsonString.append(value);
					jsonString.append("\"}");
					if (i != tableinfo_v.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + tableinfo_v.size() + "}");
		out.print(jsonString);
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlVariablesDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector tableinfo_v = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// 判断是否已经获取了当前的配置信息
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("variables")) {
							// 存在数据库连接信息
							tableinfo_v = (Vector) returnValue.get("variables");
						}
					}
				}
			}
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableinfo_v != null && tableinfo_v.size() > 0) {
			for (int i = 0; i < tableinfo_v.size(); i++) {
				Hashtable ht = (Hashtable) tableinfo_v.get(i);
				String name = (String) ht.get("variable_name");
				String value = (String) ht.get("value");
				value = value.replace("\"", "'");
				size++;
				name = getConfigForMysqlConfig(name);

				jsonString.append("{\"variablesname\":\"");
				jsonString.append(name);
				jsonString.append("\",");

				jsonString.append("\"variablesvalue\":\"");
				jsonString.append(value);
				jsonString.append("\"}");

				if (i != tableinfo_v.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlConfigDetail1() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector tableinfo_v = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// 判断是否已经获取了当前的配置信息
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("variables")) {
							tableinfo_v = (Vector) returnValue.get("variables");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableinfo_v != null && tableinfo_v.size() > 0) {
			for (int i = 0; i < tableinfo_v.size(); i++) {
				Hashtable return_value = (Hashtable) tableinfo_v.get(i);
				if (return_value != null && return_value.size() > 0) {
					String name = return_value.get("variable_name").toString();
					String value = return_value.get("value").toString();
					if (value == null || value.equals("")) {
						value = "--";
					}
					name = getConfigForMysqlConfig(name);
					jsonString.append("{\"configname\":\"");
					jsonString.append(name);
					jsonString.append("\",");

					jsonString.append("\"configvalue\":\"");
					jsonString.append(value);
					jsonString.append("\"}");
					if (i != tableinfo_v.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + tableinfo_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getDBEventDetail() {
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		int id = getParaIntValue("id");

		try {
			String starttime1 = "1970-01-01 00:00:00";
			String totime1 = "2050-12-31 23:59:59";

			EventListDao dao = new EventListDao();
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				list = dao.getQuery(starttime1, totime1, "db", status + "", level1 + "", vo.getBusinessids(), id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				EventList eventlist = (EventList) list.get(i);
				java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date cc = eventlist.getRecordtime().getTime();
				Integer eventid = eventlist.getId();
				String content = eventlist.getContent();
				String level = String.valueOf(eventlist.getLevel1());
				String eventstatus = String.valueOf(eventlist.getManagesign());
				if ("0".equals(level)) {
					level = "提示信息";
				}
				if ("1".equals(level)) {
					level = "普通告警";
				}
				if ("2".equals(level)) {
					level = "严重告警";
				}
				if ("3".equals(level)) {
					level = "紧急告警";
				}
				if ("0".equals(eventstatus)) {
					eventstatus = "未处理";
				}
				if ("1".equals(eventstatus)) {
					eventstatus = "处理中";
				}
				if ("2".equals(eventstatus)) {
					eventstatus = "处理完成";
				}
				String rptman = eventlist.getReportman();
				String rptTime = _sdf.format(cc);

				jsonString.append("{\"eventId\":\"");
				jsonString.append(eventid + "");
				jsonString.append("\",");

				jsonString.append("\"eventLevel\":\"");
				jsonString.append(level);
				jsonString.append("\",");

				jsonString.append("\"eventContent\":\"");
				jsonString.append(content);
				jsonString.append("\",");

				jsonString.append("\"rptTime\":\"");
				jsonString.append(rptTime);
				jsonString.append("\",");

				jsonString.append("\"rptMan\":\"");
				jsonString.append(rptman);
				jsonString.append("\",");

				jsonString.append("\"eventStatus\":\"");
				jsonString.append(eventstatus);
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void dbEdit() {
		DBVo vo = new DBVo();

		vo.setId(getParaIntValue("id"));
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		if (getParaValue("bid") == null || getParaValue("bid").equals("notSet") || getParaValue("bid").equals("")) {
			vo.setBid(getParaValue("bids"));
		} else {
			vo.setBid(getParaValue("bid"));
		}
		vo.setManaged(getParaIntValue("managed"));
		DBDao dao = new DBDao();
		boolean flag = true;
		try {
			flag = dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String jsonString = "修改成功";
		if (!flag) {
			jsonString = "修改失败";
		}
		out.print(jsonString);
		out.flush();
	}

	private void getOracleSystem() {
		DBVo vo = new DBVo();

		Hashtable memPerfValue = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		Hashtable cursors = new Hashtable();
		Hashtable memValue = new Hashtable();
		Vector tableinfo = new Vector();
		String lstrnStatu = "";
		String pingconavg = "0";
		String dbtype = "";
		String managed = "被管理";
		String runstr = "服务停止";
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			dbtype = typevo.getDbdesc();
			if (vo.getManaged() == 0)
				managed = "未管理";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				memValue = dao.getOracle_nmsoramemvalue(serverip);
				tableinfo = dao.getOracle_nmsoraspaces(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			double avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			String pingavg = String.valueOf(Math.round(avgpingcon));
			CreateMetersPic cmp = new CreateMetersPic();
			String pathPing = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashBoardGray.png";
			cmp.createChartByParam(vo.getIpAddress(), pingavg, pathPing, "连通率", "pingdata");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String buffercache = "0";
		String opencurstr = "0";
		String curconnectstr = "0";
		String dictionarycache = "0";
		String librarycache = "0";
		String pctmemorysorts = "";
		String pctbufgets = "0";
		if (memPerfValue != null) {
			if (memPerfValue.containsKey("buffercache") && memPerfValue.get("buffercache") != null)
				buffercache = (String) memPerfValue.get("buffercache");
			if (memPerfValue.containsKey("dictionarycache") && memPerfValue.get("dictionarycache") != null)
				dictionarycache = (String) memPerfValue.get("dictionarycache");
			if (memPerfValue.containsKey("librarycache") && memPerfValue.get("librarycache") != null)
				librarycache = (String) memPerfValue.get("librarycache");
			if (memPerfValue.containsKey("pctmemorysorts") && memPerfValue.get("pctmemorysorts") != null)
				pctmemorysorts = (String) memPerfValue.get("pctmemorysorts");
			if (memPerfValue.containsKey("pctbufgets") && memPerfValue.get("pctbufgets") != null)
				pctbufgets = (String) memPerfValue.get("pctbufgets");
			if (cursors.containsKey("opencur") && cursors.get("opencur") != null) {
				opencurstr = (String) cursors.get("opencur");
			}
			if (cursors.containsKey("curconnect") && cursors.get("curconnect") != null) {
				curconnectstr = (String) cursors.get("curconnect");
			}
			if (pctmemorysorts.equals(""))
				pctmemorysorts = "0";
			if (dictionarycache.equals(""))
				dictionarycache = "0";
			if (librarycache.equals(""))
				librarycache = "0";
			if (buffercache.equals(""))
				buffercache = "0";
			if (pctbufgets.equals(""))
				pctbufgets = "0";
		}
		// sqlserver 各种命中率图
		String[] title = { "缓冲区命中率", "数据字典命中率", "库缓存命中率", "内存中的排序" };
		String[] data = { buffercache, dictionarycache, librarycache, pctmemorysorts };
		CreateAmColumnPic amColumn = new CreateAmColumnPic();
		String dbdata = amColumn.createSqlUtilChart(data, title);
		// 表空间利用率
		String tabledata = amColumn.createOraTableSpaceUtilChart(tableinfo);
		// sga
		String sgadata = amColumn.createSGAChart(memValue);
		// pga
		String pgadata = amColumn.createPGAChart(memValue);
		String created = "";
		if (isArchive_h != null && isArchive_h.containsKey("CREATED")) {
			created = (String) isArchive_h.get("CREATED");
		}
		OracleManager om = new OracleManager();
		Hashtable orasys = om.geHashtable(vo.getIpAddress(), sid);
		String hostname = (String) orasys.get("HOST_NAME");
		String dbname = (String) orasys.get("DBNAME");
		String version = (String) orasys.get("VERSION");
		String instancename = (String) orasys.get("INSTANCE_NAME");
		String status = (String) orasys.get("STATUS");
		String startup_time = (String) orasys.get("STARTUP_TIME");
		String archiver = (String) orasys.get("ARCHIVER");

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"alias\":\"");
		jsonString.append(vo.getAlias());
		jsonString.append("\",");

		jsonString.append("\"dbname\":\"");
		jsonString.append(vo.getDbName());
		jsonString.append("\",");

		jsonString.append("\"dbtype\":\"");
		jsonString.append(dbtype);
		jsonString.append("\",");

		jsonString.append("\"ipaddress\":\"");
		jsonString.append(vo.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"port\":\"");
		jsonString.append(vo.getPort());
		jsonString.append("\",");

		jsonString.append("\"managed\":\"");
		jsonString.append(managed);
		jsonString.append("\",");

		jsonString.append("\"status\":\"");
		jsonString.append(runstr);
		jsonString.append("\",");

		jsonString.append("\"lstrnStatu\":\"");
		jsonString.append(lstrnStatu);
		jsonString.append("\",");

		jsonString.append("\"hostname\":\"");
		jsonString.append(hostname);
		jsonString.append("\",");

		jsonString.append("\"DBname\":\"");
		jsonString.append(dbname);
		jsonString.append("\",");

		jsonString.append("\"version\":\"");
		jsonString.append(version);
		jsonString.append("\",");

		jsonString.append("\"instancename\":\"");
		jsonString.append(instancename);
		jsonString.append("\",");

		jsonString.append("\"instancestatus\":\"");
		jsonString.append(status);
		jsonString.append("\",");

		jsonString.append("\"startup_time\":\"");
		jsonString.append(startup_time);
		jsonString.append("\",");

		jsonString.append("\"archiver\":\"");
		jsonString.append(archiver);
		jsonString.append("\",");

		jsonString.append("\"created\":\"");
		jsonString.append(created);
		jsonString.append("\",");

		jsonString.append("\"memsql\":\"");
		jsonString.append(pctmemorysorts);
		jsonString.append("\",");

		jsonString.append("\"opencurstr\":\"");
		jsonString.append(opencurstr);
		jsonString.append("\",");

		jsonString.append("\"curconnectstr\":\"");
		jsonString.append(curconnectstr);
		jsonString.append("\",");

		jsonString.append("\"tabledata\":\"");
		jsonString.append(tabledata);
		jsonString.append("\",");

		jsonString.append("\"sgadata\":\"");
		jsonString.append(sgadata);
		jsonString.append("\",");

		jsonString.append("\"pgadata\":\"");
		jsonString.append(pgadata);
		jsonString.append("\",");

		jsonString.append("\"dbdata\":\"");
		jsonString.append(dbdata);
		jsonString.append("\"}");

		jsonString.append("],total: 1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleLockDetail() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("ip");
		DBVo vo = new DBVo();
		Vector lockinfo_v = new Vector();
		OracleLockInfo oracleLockInfo = null;
		String id = "";
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				id = getParaValue("id");
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				lockinfo_v = dao.getOracle_nmsoralock(serverip);
				oracleLockInfo = dao.getOracle_nmsoralockinfo(serverip);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (type != null && type.equals("lock")) {
			if (lockinfo_v != null && lockinfo_v.size() > 0) {
				for (int i = 0; i < lockinfo_v.size(); i++) {
					Hashtable ht = (Hashtable) lockinfo_v.get(i);
					String username = ht.get("username").toString().trim();
					String status = ht.get("status").toString().trim();
					String machine = ht.get("machine").toString().trim();
					String sessiontype = ht.get("sessiontype").toString().trim();
					String logontime = ht.get("logontime").toString().trim();
					String program = ht.get("program").toString().trim();
					String locktype = ht.get("locktype").toString().trim();
					String lmode = ht.get("lmode").toString().trim();
					String requeststr = ht.get("request").toString().trim();
					jsonString.append("{\"alias\":\"");
					jsonString.append(vo.getAlias());
					jsonString.append("\",");

					jsonString.append("\"username\":\"");
					jsonString.append(username);
					jsonString.append("\",");

					jsonString.append("\"status\":\"");
					jsonString.append(status);
					jsonString.append("\",");

					jsonString.append("\"machine\":\"");
					jsonString.append(machine);
					jsonString.append("\",");

					jsonString.append("\"sessiontype\":\"");
					jsonString.append(sessiontype);
					jsonString.append("\",");

					jsonString.append("\"logontime\":\"");
					jsonString.append(logontime);
					jsonString.append("\",");

					jsonString.append("\"program\":\"");
					jsonString.append(program);
					jsonString.append("\",");

					jsonString.append("\"locktype\":\"");
					jsonString.append(locktype);
					jsonString.append("\",");

					jsonString.append("\"lmode\":\"");
					jsonString.append(lmode);
					jsonString.append("\",");

					jsonString.append("\"requeststr\":\"");
					jsonString.append(requeststr);
					jsonString.append("\"}");

					if (i != lockinfo_v.size() - 1) {
						jsonString.append(",");
					}
				}
				size = lockinfo_v.size();
			}
		}

		if (type != null && type.equals("lockInfo")) {
			if (oracleLockInfo != null) {
				String deadlockcount = oracleLockInfo.getDeadlockcount();
				String lockwaitcount = oracleLockInfo.getLockwaitcount();
				String maxprocesscount = oracleLockInfo.getMaxprocesscount();
				String processcount = oracleLockInfo.getProcesscount();
				String currentsessioncount = oracleLockInfo.getCurrentsessioncount();
				String useablesessioncount = oracleLockInfo.getUseablesessioncount();
				String useablesessionpercent = oracleLockInfo.getUseablesessionpercent();
				String lockdsessioncount = oracleLockInfo.getLockdsessioncount();
				String rollbacks = oracleLockInfo.getRollbacks();
				String rollbackcommitpercent = oracleLockInfo.getRollbackcommitpercent();
				jsonString.append("{\"deadlockcount\":\"");
				jsonString.append(deadlockcount);
				jsonString.append("\",");

				jsonString.append("\"lockwaitcount\":\"");
				jsonString.append(lockwaitcount);
				jsonString.append("\",");

				jsonString.append("\"maxprocesscount\":\"");
				jsonString.append(maxprocesscount);
				jsonString.append("\",");

				jsonString.append("\"processcount\":\"");
				jsonString.append(processcount);
				jsonString.append("\",");

				jsonString.append("\"currentsessioncount\":\"");
				jsonString.append(currentsessioncount);
				jsonString.append("\",");

				jsonString.append("\"useablesessioncount\":\"");
				jsonString.append(useablesessioncount);
				jsonString.append("\",");

				jsonString.append("\"useablesessionpercent\":\"");
				jsonString.append(useablesessionpercent);
				jsonString.append("\",");

				jsonString.append("\"lockdsessioncount\":\"");
				jsonString.append(lockdsessioncount);
				jsonString.append("\",");

				jsonString.append("\"rollbacks\":\"");
				jsonString.append(rollbacks);
				jsonString.append("\",");

				jsonString.append("\"rollbackcommitpercent\":\"");
				jsonString.append(rollbackcommitpercent);
				jsonString.append("\"}");
			}
			size = 1;
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();

	}

	private void getOracleSessionDetail() {
		String ipaddress = getParaValue("ip");
		Vector sessioninfo_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				sessioninfo_v = dao.getOracle_nmsorasessiondata(serverip);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (sessioninfo_v != null && sessioninfo_v.size() > 0) {
			for (int i = 0; i < sessioninfo_v.size(); i++) {
				Hashtable ht = (Hashtable) sessioninfo_v.get(i);
				String machine = ht.get("machine").toString();
				String username = ht.get("username").toString();
				String program = ht.get("program").toString();
				String status = ht.get("status").toString();
				String sessiontype = ht.get("sessiontype").toString();
				String command = ht.get("command").toString();
				String logontime = ht.get("logontime").toString();

				jsonString.append("{\"machine\":\"");
				jsonString.append(machine);
				jsonString.append("\",");

				jsonString.append("\"username\":\"");
				jsonString.append(username);
				jsonString.append("\",");

				jsonString.append("\"program\":\"");
				jsonString.append(program);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(status);
				jsonString.append("\",");

				jsonString.append("\"sessiontype\":\"");
				jsonString.append(sessiontype);
				jsonString.append("\",");

				jsonString.append("\"command\":\"");
				jsonString.append(command);
				jsonString.append("\",");

				jsonString.append("\"logontime\":\"");
				jsonString.append(logontime);
				jsonString.append("\"}");

				if (i != sessioninfo_v.size() - 1) {
					jsonString.append(",");
				}
			}

		}
		jsonString.append("],total:" + sessioninfo_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleRollbackDetail() {
		String ipaddress = getParaValue("ip");
		Vector rollbackinfo_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				rollbackinfo_v = dao.getOracle_nmsorarollback(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (rollbackinfo_v != null && rollbackinfo_v.size() > 0) {
			for (int i = 0; i < rollbackinfo_v.size(); i++) {
				Hashtable ht = (Hashtable) rollbackinfo_v.get(i);
				String rollback = ht.get("rollback_segment").toString().trim();
				String wraps = ht.get("wraps").toString();
				String shrink = ht.get("shrinks").toString();
				String ashrink = ht.get("average_shrink").toString();
				String extend = ht.get("extends").toString();

				jsonString.append("{\"rollback\":\"");
				jsonString.append(rollback);
				jsonString.append("\",");

				jsonString.append("\"wraps\":\"");
				jsonString.append(wraps);
				jsonString.append("\",");

				jsonString.append("\"shrink\":\"");
				jsonString.append(shrink);
				jsonString.append("\",");

				jsonString.append("\"ashrink\":\"");
				jsonString.append(ashrink);
				jsonString.append("\",");

				jsonString.append("\"extend\":\"");
				jsonString.append(extend);
				jsonString.append("\"}");

				if (i != rollbackinfo_v.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + rollbackinfo_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleTableDetail() {
		String ipaddress = getParaValue("ip");
		Vector table_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				table_v = dao.getOracle_nmsoratables(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (table_v != null && table_v.size() > 0) {
			DecimalFormat df = new DecimalFormat("#.##");
			for (int i = 0; i < table_v.size(); i++) {
				Hashtable ht = (Hashtable) table_v.get(i);
				String spaces = ht.get("spaces").toString().trim();
				String segment_name = ht.get("segment_name").toString();

				jsonString.append("{\"tablename\":\"");
				jsonString.append(segment_name);
				jsonString.append("\",");

				jsonString.append("\"spaces\":\"");
				jsonString.append(df.format(Double.parseDouble(spaces)));
				jsonString.append("\"}");

				if (i != table_v.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + table_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleSpacesDetail() {
		String ipaddress = getParaValue("ip");
		Vector tablespace_v = new Vector();
		Hashtable dbio = new Hashtable();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				tablespace_v = dao.getOracle_nmsoraspaces(serverip);
				dbio = dao.getOracle_nmsoradbio(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tablespace_v != null) {
			for (int i = 0; i < tablespace_v.size(); i++) {
				Hashtable ht = (Hashtable) tablespace_v.get(i);
				String filename = ht.get("file_name").toString();
				String tablespace = ht.get("tablespace").toString();
				String size = ht.get("size_mb").toString();
				String free = ht.get("free_mb").toString();
				String percent = ht.get("percent_free").toString();
				String status = ht.get("status").toString();
				String pyr = "";
				String pbr = "";
				String pyw = "";
				String pbw = "";
				if (dbio.containsKey(filename)) {
					Hashtable iodetail = (Hashtable) dbio.get(filename);
					if (iodetail != null && iodetail.size() > 0) {
						pyr = (String) iodetail.get("pyr");
						pbr = (String) iodetail.get("pbr");
						pyw = (String) iodetail.get("pyw");
						pbw = (String) iodetail.get("pbw");
					}
				}

				jsonString.append("{\"filename\":\"");
				jsonString.append(filename);
				jsonString.append("\",");

				jsonString.append("\"tablespace\":\"");
				jsonString.append(tablespace);
				jsonString.append("\",");

				jsonString.append("\"size\":\"");
				jsonString.append(size);
				jsonString.append("\",");

				jsonString.append("\"free\":\"");
				jsonString.append(free);
				jsonString.append("\",");

				jsonString.append("\"percent\":\"");
				jsonString.append(percent);
				jsonString.append("\",");

				jsonString.append("\"pyr\":\"");
				jsonString.append(pyr);
				jsonString.append("\",");

				jsonString.append("\"pbr\":\"");
				jsonString.append(pbr);
				jsonString.append("\",");

				jsonString.append("\"pyw\":\"");
				jsonString.append(pyw);
				jsonString.append("\",");

				jsonString.append("\"pbw\":\"");
				jsonString.append(pbw);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(status);
				jsonString.append("\"}");

				if (i != tablespace_v.size() - 1) {
					jsonString.append(",");
				}
			}

		}
		jsonString.append("],total:" + tablespace_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleTopsqlDetail() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("ip");
		Vector sql_v = new Vector();
		Vector sql_readwrite_v = new Vector();
		Vector sql_sort_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				if (type.equals("memsql") && type != null) {
					sql_v = dao.getOracle_nmsoratopsql(serverip);
				} else if (type.equals("disksql") && type != null) {
					sql_readwrite_v = dao.getOracle_nmsoratopsql_readwrite(serverip);
				} else if (type.equals("sortsql") && type != null) {
					sql_sort_v = dao.getOracle_nmsoratopsql_sort(serverip);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (sql_v != null && sql_v.size() > 0) {
			for (int i = 0; i < sql_v.size(); i++) {
				Hashtable ht = (Hashtable) sql_v.get(i);
				String memsql = ht.get("sql_text").toString();
				String pct_bufgets = ht.get("pct_bufgets").toString();
				String username = ht.get("username").toString();

				jsonString.append("{\"memsql\":\"");
				jsonString.append(memsql);
				jsonString.append("\",");

				jsonString.append("\"pct_bufgets\":\"");
				jsonString.append(pct_bufgets);
				jsonString.append("\",");

				jsonString.append("\"username\":\"");
				jsonString.append(username);
				jsonString.append("\"}");

				if (i != sql_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = sql_v.size();
		}

		if (sql_readwrite_v != null && sql_readwrite_v.size() > 0) {
			for (int i = 0; i < sql_readwrite_v.size(); i++) {
				Hashtable ht = (Hashtable) sql_readwrite_v.get(i);
				String sqltext = ht.get("sqltext").toString();
				String totaldisk = ht.get("totaldisk").toString();
				String totalexec = ht.get("totalexec").toString();
				String diskreads = ht.get("diskreads").toString();

				jsonString.append("{\"disksql\":\"");
				jsonString.append(sqltext);
				jsonString.append("\",");

				jsonString.append("\"totaldisk\":\"");
				jsonString.append(totaldisk);
				jsonString.append("\",");

				jsonString.append("\"totalexec\":\"");
				jsonString.append(totalexec);
				jsonString.append("\",");

				jsonString.append("\"diskreads\":\"");
				jsonString.append(diskreads);
				jsonString.append("\"}");

				if (i != sql_readwrite_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = sql_readwrite_v.size();
		}

		if (sql_sort_v != null && sql_sort_v.size() > 0) {
			for (int i = 0; i < sql_sort_v.size(); i++) {
				Hashtable ht = (Hashtable) sql_sort_v.get(i);
				String sqltext = ht.get("sqltext").toString();
				String sorts = ht.get("sorts").toString();
				String executions = ht.get("executions").toString();
				String sortsexec = ht.get("sortsexec").toString();

				jsonString.append("{\"sortsql\":\"");
				jsonString.append(sqltext);
				jsonString.append("\",");

				jsonString.append("\"sorts\":\"");
				jsonString.append(sorts);
				jsonString.append("\",");

				jsonString.append("\"executions\":\"");
				jsonString.append(executions);
				jsonString.append("\",");

				jsonString.append("\"sortsexec\":\"");
				jsonString.append(sortsexec);
				jsonString.append("\"}");

				if (i != sql_sort_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = sql_sort_v.size();
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleUserDetail() {
		String ipaddress = getParaValue("ip");
		Hashtable userinfo_h = new Hashtable();
		Vector returnVal = null;
		Vector returnVal1 = null;
		Vector returnVal2 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				userinfo_h = dao.getOracle_nmsorauserinfo(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (userinfo_h != null) {
			returnVal = (Vector) userinfo_h.get("returnVal0");
			returnVal1 = (Vector) userinfo_h.get("returnVal1");
			returnVal2 = (Vector) userinfo_h.get("returnVal2");
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		int size = 0;
		if (returnVal2 != null) {
			for (int i = 0; i < returnVal2.size(); i++) {
				Hashtable ht2 = (Hashtable) returnVal2.get(i);
				String username = ht2.get("username").toString().trim();
				String userid = ht2.get("user_id").toString().trim();
				String status = ht2.get("account_status").toString().trim();
				int k = 0;
				if (returnVal1 != null) {
					for (int j = 0; j < returnVal1.size(); j++) {
						Hashtable ht1 = (Hashtable) returnVal1.get(j);
						String user_ = ht1.get("user#").toString().trim();
						if (user_.equals(userid)) {
							k++;
						}
					}
				}
				if (k > 0) {
					status = "ACTIVE(" + k + ")";
				}
				String cpu_time = "---";
				String sorts = "---";
				String buffer_gets = "---";
				String runtime_mem = "---";
				String cursor = "---";
				String disk_reads = "---";
				String disk_write = "---";
				if (returnVal != null) {
					for (int j = 0; j < returnVal.size(); j++) {
						Hashtable ht = (Hashtable) returnVal.get(j);
						String user_ = ht.get("parsing_user_id").toString().trim();
						if (user_.equals(userid)) {
							cpu_time = ht.get("sum(a.cpu_time)").toString().trim();
							cpu_time = Long.parseLong(cpu_time.toString()) / 60000000 + "分";
							sorts = ht.get("sum(a.sorts)").toString().trim();
							buffer_gets = ht.get("sum(a.buffer_gets)").toString().trim();
							runtime_mem = ht.get("sum(a.runtime_mem)").toString().trim();
							cursor = ht.get("sum(a.version_count)").toString().trim();
							disk_reads = ht.get("sum(a.disk_reads)").toString().trim();
							break;
						}
					}
				}

				jsonString.append("{\"username\":\"");
				jsonString.append(username);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(status);
				jsonString.append("\",");

				jsonString.append("\"cpu_time\":\"");
				jsonString.append(cpu_time);
				jsonString.append("\",");

				jsonString.append("\"sorts\":\"");
				jsonString.append(sorts);
				jsonString.append("\",");

				jsonString.append("\"buffer_gets\":\"");
				jsonString.append(buffer_gets);
				jsonString.append("\",");

				jsonString.append("\"runtime_mem\":\"");
				jsonString.append(runtime_mem);
				jsonString.append("\",");

				jsonString.append("\"cursor\":\"");
				jsonString.append(cursor);
				jsonString.append("\",");

				jsonString.append("\"disk_reads\":\"");
				jsonString.append(disk_reads);
				jsonString.append("\",");

				jsonString.append("\"disk_write\":\"");
				jsonString.append(disk_write);
				jsonString.append("\"}");

				if (i != returnVal2.size() - 1) {
					jsonString.append(",");
				}

			}
			size = returnVal2.size();
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleWaitDetail() {
		String ipaddress = getParaValue("ip");
		Vector wait_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				wait_v = dao.getOracle_nmsorawait(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (wait_v != null && wait_v.size() > 0) {
			for (int i = 0; i < wait_v.size(); i++) {
				Hashtable ht2 = (Hashtable) wait_v.get(i);
				String event = ht2.get("event").toString().trim();
				String prev = ht2.get("prev").toString().trim();
				String curr = ht2.get("curr").toString().trim();
				String total = ht2.get("tot").toString().trim();

				jsonString.append("{\"event\":\"");
				jsonString.append(event);
				jsonString.append("\",");

				jsonString.append("\"prev\":\"");
				jsonString.append(prev);
				jsonString.append("\",");

				jsonString.append("\"curr\":\"");
				jsonString.append(curr);
				jsonString.append("\",");

				jsonString.append("\"total\":\"");
				jsonString.append(total);
				jsonString.append("\"}");

				if (i != wait_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = wait_v.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleBaseInfoDetail() {
		String ipaddress = getParaValue("ip");
		Hashtable baseinfoHash = new Hashtable();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				baseinfoHash = dao.getOracle_nmsorabaseinfo(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (baseinfoHash != null) {
			@SuppressWarnings("unchecked")
			Iterator<String> keyIterator = baseinfoHash.keySet().iterator();
			while (keyIterator.hasNext()) {
				size++;
				String subentity = keyIterator.next();
				String thevalue = String.valueOf(baseinfoHash.get(subentity));

				jsonString.append("{\"subentity\":\"");
				jsonString.append(subentity);
				jsonString.append("\",");

				jsonString.append("\"thevalue\":\"");
				jsonString.append(thevalue);
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleJobDetail() {
		String ipaddress = getParaValue("ip");
		Vector job_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				job_v = dao.getOracle_nmsorajobs(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (job_v != null && job_v.size() > 0) {
			for (int i = 0; i < job_v.size(); i++) {
				Hashtable ht2 = (Hashtable) job_v.get(i);
				String job = ht2.get("job").toString().trim();
				String loguser = ht2.get("loguser").toString().trim();
				String lastdate = ht2.get("lastdate").toString().trim();
				String failures = ht2.get("failures").toString().trim();

				jsonString.append("{\"job\":\"");
				jsonString.append(job);
				jsonString.append("\",");

				jsonString.append("\"loguser\":\"");
				jsonString.append(loguser);
				jsonString.append("\",");

				jsonString.append("\"lastdate\":\"");
				jsonString.append(lastdate);
				jsonString.append("\",");

				jsonString.append("\"total\":\"");
				jsonString.append(failures);
				jsonString.append("\"}");

				if (i != job_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = job_v.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleFileDetail() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("ip");
		Vector contrFile_v = new Vector();
		Vector logFile_v = new Vector();
		Vector keepObj_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				if (type != null && type.equals("contr")) {
					contrFile_v = dao.getOracle_nmsoracontrfile(serverip);
				} else if (type != null && type.equals("log")) {
					logFile_v = dao.getOracle_nmsoralogfile(serverip);
				} else if (type != null && type.equals("keepobj")) {
					keepObj_v = dao.getOracle_nmsorakeepobj(serverip);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (contrFile_v != null && contrFile_v.size() > 0) {
			for (int i = 0; i < contrFile_v.size(); i++) {
				Hashtable ht = (Hashtable) contrFile_v.get(i);
				String name = ht.get("name").toString().trim();
				String status = ht.get("status").toString();

				jsonString.append("{\"contrName\":\"");
				jsonString.append(name);
				jsonString.append("\",");

				jsonString.append("\"contrStatus\":\"");
				jsonString.append(status);
				jsonString.append("\"}");

				if (i != contrFile_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = contrFile_v.size();
		}

		if (logFile_v != null && logFile_v.size() > 0) {
			for (int i = 0; i < logFile_v.size(); i++) {
				Hashtable ht = (Hashtable) logFile_v.get(i);
				String group = ht.get("group#").toString().trim();
				String logstatus = ht.get("status").toString();
				String logtype = ht.get("type").toString();
				String logname = ht.get("member").toString();

				jsonString.append("{\"group\":\"");
				jsonString.append(group);
				jsonString.append("\",");

				jsonString.append("\"logstatus\":\"");
				jsonString.append(logstatus);
				jsonString.append("\",");

				jsonString.append("\"logtype\":\"");
				jsonString.append(logtype);
				jsonString.append("\",");

				jsonString.append("\"logname\":\"");
				jsonString.append(logname);
				jsonString.append("\"}");

				if (i != logFile_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = logFile_v.size();
		}

		if (keepObj_v != null && keepObj_v.size() > 0) {
			for (int i = 0; i < keepObj_v.size(); i++) {
				Hashtable ht = (Hashtable) keepObj_v.get(i);
				String owner = ht.get("owner").toString().trim();
				String name = ht.get("name").toString();
				String namespace = ht.get("namespace").toString();
				String types = ht.get("type").toString();
				String sharable_mem = ht.get("sharable_mem").toString();
				String pins = ht.get("pins").toString();
				String kept = ht.get("kept").toString();

				jsonString.append("{\"owner\":\"");
				jsonString.append(owner);
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(name);
				jsonString.append("\",");

				jsonString.append("\"namespace\":\"");
				jsonString.append(namespace);
				jsonString.append("\",");

				jsonString.append("\"type\":\"");
				jsonString.append(types);
				jsonString.append("\",");

				jsonString.append("\"sharable_mem\":\"");
				jsonString.append(sharable_mem);
				jsonString.append("\",");

				jsonString.append("\"pins\":\"");
				jsonString.append(pins);
				jsonString.append("\",");

				jsonString.append("\"kept\":\"");
				jsonString.append(kept);
				jsonString.append("\"}");

				if (i != keepObj_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = keepObj_v.size();
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("getDbNodeDataByType")) {
			getDbNodeDataByType();
		} else if (action.equals("addDb")) {
			addDb();
		} else if (action.equals("dbReadyEdit")) {
			dbReadyEdit();
		} else if (action.equals("dbEdit")) {
			dbEdit();
		} else if (action.equals("deleteDbs")) {
			deleteDbs();
		} else if (action.equals("getMysqlVariablesDetail")) {
			getMysqlVariablesDetail();
		} else if (action.equals("getMysqlNodeSystem")) {
			getMysqlNodeSystem();
		} else if (action.equals("getMysqlConnectDetail")) {
			getMysqlConnectDetail();
		} else if (action.equals("getMysqlTablesDetail")) {
			getMysqlTablesDetail();
		} else if (action.equals("getMysqlSpacesDetail")) {
			getMysqlSpacesDetail();
		} else if (action.equals("getMysqlStatusDetail")) {
			getMysqlStatusDetail();
		} else if (action.equals("getMysqlConfigDetail1")) {
			getMysqlConfigDetail1();
		} else if (action.equals("getDBEventDetail")) {
			getDBEventDetail();
		} else if (action.equals("getOracleLockDetail")) {
			getOracleLockDetail();
		} else if (action.equals("getOracleSessionDetail")) {
			getOracleSessionDetail();
		} else if (action.equals("getOracleRollbackDetail")) {
			getOracleRollbackDetail();
		} else if (action.equals("getOracleTableDetail")) {
			getOracleTableDetail();
		} else if (action.equals("getOracleSpacesDetail")) {
			getOracleSpacesDetail();
		} else if (action.equals("getOracleTopsqlDetail")) {
			getOracleTopsqlDetail();
		} else if (action.equals("getOracleUserDetail")) {
			getOracleUserDetail();
		} else if (action.equals("getOracleWaitDetail")) {
			getOracleWaitDetail();
		} else if (action.equals("getOracleBaseInfoDetail")) {
			getOracleBaseInfoDetail();
		} else if (action.equals("getOracleJobDetail")) {
			getOracleJobDetail();
		} else if (action.equals("getOracleSystem")) {
			getOracleSystem();
		} else if (action.equals("getOracleFileDetail")) {
			getOracleFileDetail();
		} else if (action.equals("batchAddMonitor")) {
			batchAddMonitor();
		} else if (action.equals("batchCancleMonitor")) {
			batchCancleMonitor();
		} else if (action.equals("getSqlserverConfig")) {
			getSqlserverConfig();
		} else if (action.equals("getSqlserverSystem")) {
			getSqlserverSystem();
		} else if (action.equals("getSqlserverProcess")) {
			getSqlserverProcess();
		} else if (action.equals("getSqlserverLock")) {
			getSqlserverLock();
		} else if (action.equals("getSqlserverPerformance")) {
			getSqlserverPerformance();
		} else if (action.equals("getSqlserverDb")) {
			getSqlserverDb();
		}
	}

	private void getSqlserverDb() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Hashtable connHt = new Hashtable();
		Hashtable cacheHt = new Hashtable();
		Hashtable sqlHt = new Hashtable();
		Hashtable scanHt = new Hashtable();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			connHt = dbDao.getSqlserver_nmsconns(serverIp);
			cacheHt = dbDao.getSqlserver_nmscaches(serverIp);
			sqlHt = dbDao.getSqlserver_nmssqls(serverIp);
			scanHt = dbDao.getSqlserver_nmsscans(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{");
		if (null != cacheHt && cacheHt.size() > 0) {
			jsonString.append("cacheRows:[");
			Enumeration cacheE = cacheHt.keys();
			String cacheKey = (String) null;
			while (cacheE.hasMoreElements()) {
				cacheKey = (String) cacheE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(cacheKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(cacheHt.get(cacheKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != connHt && connHt.size() > 0) {
			jsonString.append("connRows:[");
			Enumeration connE = connHt.keys();
			String connKey = (String) null;
			while (connE.hasMoreElements()) {
				connKey = (String) connE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(connKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(connHt.get(connKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != sqlHt && sqlHt.size() > 0) {
			jsonString.append("sqlRows:[");
			Enumeration sqlE = sqlHt.keys();
			String sqlKey = (String) null;
			while (sqlE.hasMoreElements()) {
				sqlKey = (String) sqlE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(sqlKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(sqlHt.get(sqlKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != scanHt && scanHt.size() > 0) {
			jsonString.append("scanRows:[");
			Enumeration scanE = scanHt.keys();
			String scanKey = (String) null;
			while (scanE.hasMoreElements()) {
				scanKey = (String) scanE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(scanKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(scanHt.get(scanKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverPerformance() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Hashtable pagesHt = new Hashtable();
		Hashtable dataBaseHt = new Hashtable();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			pagesHt = dbDao.getSqlserver_nmspages(serverIp);
			dataBaseHt = dbDao.getSqlserver_nmsdbvalue(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		CreateAmColumnPic amColumn = new CreateAmColumnPic();
		String hitDataString = "-1";
		String dataBaseString = "-1";
		String logFileString = "-1";

		String bufferCacheHitRatio = (String) null;
		String planCacheHitRatio = (String) null;
		String cursorManagerByTypeHitRatio = (String) null;
		String catalogMetadataHitRatio = (String) null;

		double dBufferCacheHitRatio = 0.0;
		double dPlanCacheHitRatio = 0.0;
		double dCursorManagerByTypeHitRatio = 0.0;
		double dCatalogMetadataHitRatio = 0.0;

		if (null != pagesHt && pagesHt.size() > 0) {
			bufferCacheHitRatio = (String) pagesHt.get("bufferCacheHitRatio");
			if (null != bufferCacheHitRatio && !bufferCacheHitRatio.equals(""))
				dBufferCacheHitRatio = Double.valueOf(bufferCacheHitRatio);
			planCacheHitRatio = (String) pagesHt.get("planCacheHitRatio");
			if (null != planCacheHitRatio && !planCacheHitRatio.equals(""))
				dPlanCacheHitRatio = Double.valueOf(planCacheHitRatio);
			cursorManagerByTypeHitRatio = (String) pagesHt.get("cursorManagerByTypeHitRatio");
			if (null != cursorManagerByTypeHitRatio && !cursorManagerByTypeHitRatio.equals(""))
				dCursorManagerByTypeHitRatio = Double.valueOf(cursorManagerByTypeHitRatio);
			catalogMetadataHitRatio = (String) pagesHt.get("catalogMetadataHitRatio");
			if (null != catalogMetadataHitRatio && !catalogMetadataHitRatio.equals(""))
				dCatalogMetadataHitRatio = Double.valueOf(catalogMetadataHitRatio);

			String[] items = { "缓冲区命中率 ", "planCache命中率", "CursorManagerByType命中率", "CatalogMetadata命中率" };
			String[] data = { dBufferCacheHitRatio + "", dPlanCacheHitRatio + "", dCursorManagerByTypeHitRatio + "", dCatalogMetadataHitRatio + "" };

			hitDataString = amColumn.createSqlUtilChart(data, items);
		}

		Hashtable tempDataBaseHt = null;
		if (dataBaseHt != null && dataBaseHt.size() > 0)
			tempDataBaseHt = (Hashtable) dataBaseHt.get("database");
		if (tempDataBaseHt != null && tempDataBaseHt.size() > 0) {
			StringBuffer seriesSb = new StringBuffer();
			StringBuffer graphsSb = new StringBuffer();
			seriesSb.append("<chart><series>");
			graphsSb.append("<graphs><graph gid='0'>");
			int i = 0;
			Hashtable tablespaceHt = new Hashtable();
			for (Iterator it = tempDataBaseHt.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				tablespaceHt = (Hashtable) tempDataBaseHt.get(key);
				if (tablespaceHt != null && tablespaceHt.size() > 0) {
					seriesSb.append("<value xid='").append(i).append("'>").append(tablespaceHt.get("dbname")).append("</value>");
					graphsSb.append("<value xid='").append(i).append("' color='").append("#" + generateColor()).append("'>" + tablespaceHt.get("usedperc")).append("</value>");
					i++;
				}
			}
			seriesSb.append("</series>");
			seriesSb.append(graphsSb);
			seriesSb.append("</graph></graphs></chart>");

			dataBaseString = seriesSb.toString();
		}

		Hashtable tempLogFileHt = null;
		if (dataBaseHt != null && dataBaseHt.size() > 0)
			tempLogFileHt = (Hashtable) dataBaseHt.get("logfile");
		if (tempLogFileHt != null && tempLogFileHt.size() > 0) {
			StringBuffer seriesSb = new StringBuffer();
			StringBuffer graphsSb = new StringBuffer();
			seriesSb.append("<chart><series>");
			graphsSb.append("<graphs><graph gid='0'>");
			int i = 0;
			Hashtable tablespaceHt = new Hashtable();
			for (Iterator it = tempLogFileHt.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				tablespaceHt = (Hashtable) tempLogFileHt.get(key);
				if (tablespaceHt != null && tablespaceHt.size() > 0) {
					seriesSb.append("<value xid='").append(i).append("'>").append(tablespaceHt.get("logname")).append("</value>");
					graphsSb.append("<value xid='").append(i).append("' color='").append("#" + generateColor()).append("'>" + tablespaceHt.get("usedperc")).append("</value>");
					i++;
				}
			}
			seriesSb.append("</series>");
			seriesSb.append(graphsSb);
			seriesSb.append("</graph></graphs></chart>");

			logFileString = seriesSb.toString();
		}
		StringBuffer jsonString = new StringBuffer("{");
		if (null != tempDataBaseHt && tempDataBaseHt.size() > 0) {
			jsonString.append("tableSpaceRows:[");

			Enumeration tableSpaceE = tempDataBaseHt.elements();
			Hashtable tableSpaceHt = new Hashtable();
			while (tableSpaceE.hasMoreElements()) {
				tableSpaceHt = (Hashtable) tableSpaceE.nextElement();
				jsonString.append("{\"dbName\":\"");
				jsonString.append(tableSpaceHt.get("dbname"));
				jsonString.append("\",");

				jsonString.append("\"capacity\":\"");
				jsonString.append(tableSpaceHt.get("sizes"));
				jsonString.append("\",");

				jsonString.append("\"used\":\"");
				jsonString.append(tableSpaceHt.get("usedsize"));
				jsonString.append("\",");

				jsonString.append("\"usePercent\":\"");
				jsonString.append(tableSpaceHt.get("usedperc"));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != hitDataString && !hitDataString.equals("-1")) {
			jsonString.append("\"hitRateFlexString\":\"" + hitDataString + "\",");
		}
		if (null != dataBaseString && !dataBaseString.equals("-1")) {
			jsonString.append("\"tableSpaceFlexString\":\"" + dataBaseString + "\",");
		}
		if (null != logFileString && !logFileString.equals("-1")) {
			jsonString.append("\"logFileFlexString\":\"" + logFileString + "\",");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverLock() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Hashtable lockHt = new Hashtable();
		Vector lockinfoVector = new Vector();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			lockHt = dbDao.getSqlserver_nmslocks(serverIp);
			lockinfoVector = dbDao.getSqlserver_nmslockinfo_v(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		StringBuffer jsonString = new StringBuffer("{");
		if (null != lockHt && lockHt.size() > 0) {
			jsonString.append("lockRows:[");
			Enumeration sysE = lockHt.keys();
			String sysKey = (String) null;
			while (sysE.hasMoreElements()) {
				sysKey = (String) sysE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(sysKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(lockHt.get(sysKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != lockinfoVector && lockinfoVector.size() > 0) {
			jsonString.append("lockDetailRows:[");
			Hashtable tempHt = new Hashtable();
			String resourceType = "-1";
			String req_mode = "-1";
			String req_status = "-1";
			String req_ownertype = "-1";
			for (int i = 0; i < lockinfoVector.size(); i++) {
				tempHt = (Hashtable) lockinfoVector.get(i);

				if (null != tempHt.get("rsc_type")) {
					resourceType = (String) tempHt.get("rsc_type");
				}

				if (null != tempHt.get("req_mode")) {
					req_mode = (String) tempHt.get("req_mode");
				}

				if (null != tempHt.get("req_status")) {
					req_status = (String) tempHt.get("req_status");
				}

				if (null != tempHt.get("req_ownertype")) {
					req_ownertype = (String) tempHt.get("req_ownertype");
				}
				jsonString.append("{\"lockResource\":\"");
				jsonString.append(tempHt.get("rsc_text"));
				jsonString.append("\",");

				jsonString.append("\"dataBase\":\"");
				jsonString.append(tempHt.get("dbname"));
				jsonString.append("\",");

				jsonString.append("\"resourceType\":\"");
				jsonString.append(rsc_type_ht.get(resourceType));
				jsonString.append("\",");

				jsonString.append("\"requestType\":\"");
				jsonString.append(req_mode_ht.get(req_mode));
				jsonString.append("\",");

				jsonString.append("\"requestStatus\":\"");
				jsonString.append(req_status_ht.get(req_status));
				jsonString.append("\",");

				jsonString.append("\"useCount\":\"");
				jsonString.append(tempHt.get("req_refcnt"));
				jsonString.append("\",");

				jsonString.append("\"lockTTL\":\"");
				jsonString.append(tempHt.get("req_lifetime"));
				jsonString.append("\",");

				jsonString.append("\"processId\":\"");
				jsonString.append(tempHt.get("req_spid"));
				jsonString.append("\",");

				jsonString.append("\"objectType\":\"");
				jsonString.append(req_ownertype_ht.get(req_ownertype));
				jsonString.append("\"}");

				if (i != lockinfoVector.size() - 1) {
					jsonString.append(",");
				}
			}
			jsonString.append("]");
		}
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverProcess() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Vector processVector = new Vector();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			processVector = dbDao.getSqlserver_nmsinfo_v(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != processVector && processVector.size() > 0) {
			Hashtable tempHt = new Hashtable();
			for (int i = 0; i < processVector.size(); i++) {
				tempHt = (Hashtable) processVector.get(i);
				if (null != tempHt) {
					jsonString.append("{\"processId\":\"");
					jsonString.append(tempHt.get("spid"));
					jsonString.append("\",");

					jsonString.append("\"dataBase\":\"");
					jsonString.append(tempHt.get("dbname"));
					jsonString.append("\",");

					jsonString.append("\"user\":\"");
					jsonString.append(tempHt.get("username"));
					jsonString.append("\",");

					jsonString.append("\"cpuTime\":\"");
					jsonString.append(tempHt.get("cpu"));
					jsonString.append("\",");

					jsonString.append("\"diskIO\":\"");
					jsonString.append(tempHt.get("physical_io"));
					jsonString.append("\",");

					jsonString.append("\"pages\":\"");
					jsonString.append(tempHt.get("memusage"));
					jsonString.append("\",");

					jsonString.append("\"status\":\"");
					jsonString.append(tempHt.get("status"));
					jsonString.append("\",");

					jsonString.append("\"workStation\":\"");
					jsonString.append(tempHt.get("hostname"));
					jsonString.append("\",");

					jsonString.append("\"application\":\"");
					jsonString.append(tempHt.get("program_name"));
					jsonString.append("\",");

					jsonString.append("\"startTime\":\"");
					jsonString.append(tempHt.get("login_time"));
					jsonString.append("\"}");

					if (i != processVector.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + processVector.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverSystem() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		Hashtable pageHt = new Hashtable();
		Hashtable statisticsHt = new Hashtable();
		Hashtable memoryHt = new Hashtable();

		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			pageHt = dbDao.getSqlserver_nmspages(serverIp);
			statisticsHt = dbDao.getSqlserver_nmsstatisticsHash(serverIp);
			memoryHt = dbDao.getSqlserver_nmsmems(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{");
		if (null != pageHt && pageHt.size() > 0) {
			jsonString.append("pageRows:[");
			Enumeration pageE = pageHt.keys();
			String pageKey = (String) null;
			while (pageE.hasMoreElements()) {
				pageKey = (String) pageE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(pageKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(pageHt.get(pageKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}
		if (null != memoryHt && memoryHt.size() > 0) {
			jsonString.append("memoryRows:[");
			Enumeration memoryE = memoryHt.keys();
			String memoryKey = (String) null;
			while (memoryE.hasMoreElements()) {
				memoryKey = (String) memoryE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(memoryKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(memoryHt.get(memoryKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}
		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("avgWaitRows:[");
			Enumeration avgWaitE = statisticsHt.keys();
			String avgWaitKey = (String) null;
			while (avgWaitE.hasMoreElements()) {
				avgWaitKey = (String) avgWaitE.nextElement();
				if (!avgWaitKey.startsWith("pingjun_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(avgWaitKey.replace("pingjun_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(avgWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("processWaitRows:[");
			Enumeration processWaitE = statisticsHt.keys();
			String processWaitKey = (String) null;
			while (processWaitE.hasMoreElements()) {
				processWaitKey = (String) processWaitE.nextElement();
				if (!processWaitKey.startsWith("jingxing_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(processWaitKey.replace("jingxing_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(processWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("startUpWaitRows:[");
			Enumeration startUpWaitE = statisticsHt.keys();
			String startUpWaitKey = (String) null;
			while (startUpWaitE.hasMoreElements()) {
				startUpWaitKey = (String) startUpWaitE.nextElement();
				if (!startUpWaitKey.startsWith("qidong_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(startUpWaitKey.replace("qidong_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(startUpWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("countWaitRows:[");
			Enumeration countWaitE = statisticsHt.keys();
			String countWaitKey = (String) null;
			while (countWaitE.hasMoreElements()) {
				countWaitKey = (String) countWaitE.nextElement();
				if (!countWaitKey.startsWith("leiji_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(countWaitKey.replace("leiji_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(countWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverConfig() {
		String id = getParaValue("id");
		Hashtable statusHt = new Hashtable();
		Hashtable sysValueHt = new Hashtable();
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		String isM = "被管理";
		if (vo.getManaged() == 0)
			isM = "未管理";

		String status = "<font color=red>服务停止</font>";
		String hostName = "";
		String version = "";
		String servicePackage = "";
		String processId = "";
		String userMode = "";
		String securityMode = "";
		String integratedInstance = "";
		try {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			statusHt = dbDao.getSqlserver_nmsstatus(serverIp);
			sysValueHt = dbDao.getSqlserver_nmssysvalue(serverIp);
			String p_status = (String) statusHt.get("status");

			if (p_status != null && p_status.length() > 0) {
				if ("1".equalsIgnoreCase(p_status)) {
					status = "正在运行";
				}
			}
			if (null != sysValueHt && sysValueHt.size() > 0) {
				if (sysValueHt.get("MACHINENAME") != null) {
					hostName = (String) sysValueHt.get("MACHINENAME");
				}
				if (sysValueHt.get("VERSION") != null) {
					version = (String) sysValueHt.get("VERSION");
					version = version.replaceAll("(\r\n|\r|\n|\n\r)", "");
				}
				if (sysValueHt.get("productlevel") != null) {
					servicePackage = (String) sysValueHt.get("productlevel");
				}
				if (sysValueHt.get("ProcessID") != null) {
					processId = (String) sysValueHt.get("ProcessID");
				}
				if (sysValueHt.get("IsSingleUser") != null) {
					userMode = (String) sysValueHt.get("IsSingleUser");
				}
				if (sysValueHt.get("IsIntegratedSecurityOnly") != null) {
					securityMode = (String) sysValueHt.get("IsIntegratedSecurityOnly");
				}
				if (sysValueHt.get("IsClustered") != null) {
					integratedInstance = (String) sysValueHt.get("IsClustered");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"nodeIp\":\"");
		jsonString.append(vo.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"nodeAlias\":\"");
		jsonString.append(vo.getAlias());
		jsonString.append("\",");

		jsonString.append("\"isM\":\"");
		jsonString.append(isM);
		jsonString.append("\",");

		jsonString.append("\"status\":\"");
		jsonString.append(status);
		jsonString.append("\",");

		jsonString.append("\"hostName\":\"");
		jsonString.append(hostName);
		jsonString.append("\",");

		jsonString.append("\"version\":\"");
		jsonString.append(version);
		jsonString.append("\",");

		jsonString.append("\"servicePackage\":\"");
		jsonString.append(servicePackage);
		jsonString.append("\",");

		jsonString.append("\"processId\":\"");
		jsonString.append(processId);
		jsonString.append("\",");

		jsonString.append("\"userMode\":\"");
		jsonString.append(userMode);
		jsonString.append("\",");

		jsonString.append("\"securityMode\":\"");
		jsonString.append(securityMode);
		jsonString.append("\",");

		jsonString.append("\"integratedInstance\":\"");
		jsonString.append(integratedInstance);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void batchAddMonitor() {
		StringBuffer sb = new StringBuffer("启用监控");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update app_db_node set managed=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void batchCancleMonitor() {
		StringBuffer sb = new StringBuffer("取消监控");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update app_db_node set managed=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private int getDbtypeFromString(String type) {
		int dbtype = 1;
		if (type.equals("oracle")) {
			dbtype = 1;
		} else if (type.equals("sqlserver")) {
			dbtype = 2;
		} else if (type.equals("mysql")) {
			dbtype = 4;
		} else if (type.equals("db2")) {
			dbtype = 5;
		} else if (type.equals("sybase")) {
			dbtype = 6;
		} else if (type.equals("informix")) {
			dbtype = 7;
		}
		return dbtype;
	}

	private String getValueFromNameForMysqlSpace(String name) {
		if (name.equalsIgnoreCase("Max_used_connections")) {
			name = "服务器相应的最大连接数";
		}
		if (name.equalsIgnoreCase("Handler_read_first")) {
			name = "索引中第一条被读的次数";
		}
		if (name.equalsIgnoreCase("Handler_read_key")) {
			name = "根据键读一行的请求数";
		}
		if (name.equalsIgnoreCase("Handler_read_next")) {
			name = "按照键顺序读下一行的请求数";
		}
		if (name.equalsIgnoreCase("Handler_read_prev")) {
			name = "按照键顺序读前一行的请求数";
		}
		if (name.equalsIgnoreCase("Handler_read_rnd")) {
			name = "H根据固定位置读一行的请求数";
		}
		if (name.equalsIgnoreCase("Handler_read_rnd_next")) {
			name = "在数据文件中读下一行的请求数";
		}
		if (name.equalsIgnoreCase("Open_tables")) {
			name = "当前打开的表的数量";
		}
		if (name.equalsIgnoreCase("Opened_tables")) {
			name = "已经打开的表的数量";
		}
		if (name.equalsIgnoreCase("Threads_cached")) {
			name = "线程缓存内的线程的数量";
		}
		if (name.equalsIgnoreCase("Threads_connected")) {
			name = "当前打开的连接的数量";
		}
		if (name.equalsIgnoreCase("Threads_created")) {
			name = "创建用来处理连接的线程数";
		}
		if (name.equalsIgnoreCase("Threads_running")) {
			name = "激活的非睡眠状态的线程数";
		}
		if (name.equalsIgnoreCase("Table_locks_immediate")) {
			name = "立即获得的表的锁的次数";
		}
		if (name.equalsIgnoreCase("Table_locks_waited")) {
			name = "不能立即获得的表的锁的次数";
		}
		if (name.equalsIgnoreCase("Key_read_requests")) {
			name = "从缓存读键的数据块的请求数";
		}
		if (name.equalsIgnoreCase("Key_reads")) {
			name = "从硬盘读取键的数据块的次数";
		}
		if (name.equalsIgnoreCase("log_slow_queries")) {
			name = "是否记录慢查询";
		}
		if (name.equalsIgnoreCase("slow_launch_time")) {
			name = "创建线程的时间超过该秒数，服务器增加Slow_launch_threads状态变量";
		}
		return name;

	}

	private String getSizeForMysqlStatus(String size) {
		if (size.equalsIgnoreCase("Aborted_clients")) {
			size = "由于客户端没有正确关闭连接导致客户端终止而中断的连接数";
		}
		if (size.equalsIgnoreCase("Aborted_connects")) {
			size = "试图连接到MySQL服务器而失败的连接数";
		}
		if (size.equalsIgnoreCase("Binlog_cache_disk_use")) {
			size = "使用临时二进制日志缓存但超过binlog_cache_size值并使用临时文件来保存事务中的语句的事务数量";
		}
		if (size.equalsIgnoreCase("Binlog_cache_use")) {
			size = "使用临时二进制日志缓存的事务数量";
		}
		if (size.equalsIgnoreCase("Bytes_received")) {
			size = "从所有客户端接收到的字节数";
		}
		if (size.equalsIgnoreCase("Bytes_sent")) {
			size = "发送给所有客户端的字节数";
		}
		if (size.equalsIgnoreCase("Com_admin_commands")) {
			size = "admin_commands语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_alter_db")) {
			size = "alter_db语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_alter_table")) {
			size = "alter_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_analyze")) {
			size = "analyze语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_backup_table")) {
			size = "backup_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_begin")) {
			size = "begin语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_change_db")) {
			size = "change_db语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_change_master")) {
			size = "change_master语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_check")) {
			size = "check语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_checksum")) {
			size = "checksum语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_commit")) {
			size = "commit语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_create_db")) {
			size = "create_db语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_create_function")) {
			size = "create_function语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_create_index")) {
			size = "create_index语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_create_table")) {
			size = "create_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_dealloc_sql")) {
			size = "dealloc_sql语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_delete")) {
			size = "delete语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_delete_multi")) {
			size = "delete_multi语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_do")) {
			size = "do语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_drop_db")) {
			size = "drop_db语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_drop_function")) {
			size = "drop_function语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_drop_index")) {
			size = "drop_index语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_drop_table")) {
			size = "drop_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_drop_user")) {
			size = "drop_user语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_execute_sql")) {
			size = "execute_sql语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_flush")) {
			size = "flush语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_grant")) {
			size = "grant语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_ha_close")) {
			size = "ha_close语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_ha_open")) {
			size = "ha_open语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_ha_read")) {
			size = "ha_read语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_help")) {
			size = "help语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_insert")) {
			size = "insert语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_insert_select")) {
			size = "insert_select语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_kill")) {
			size = "kill语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_load")) {
			size = "load语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_load_master_data")) {
			size = "load_master_data语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_load_master_table")) {
			size = "load_master_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_lock_tables")) {
			size = "lock_tables语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_optimize")) {
			size = "optimize语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_preload_keys")) {
			size = "preload_keys语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_prepare_sql")) {
			size = "prepare_sql语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_purge")) {
			size = "purge语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_purge_before_date")) {
			size = "purge_before_date语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_rename_table")) {
			size = "rename_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_repair")) {
			size = "repair语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_replace")) {
			size = "replace语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_replace_select")) {
			size = "replace_select语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_reset")) {
			size = "reset语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_restore_table")) {
			size = "restore_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_revoke")) {
			size = "revoke语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_revoke_all")) {
			size = "revoke_all语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_rollback")) {
			size = "rollback语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_savepoint")) {
			size = "savepoint语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_select")) {
			size = "select语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_set_option")) {
			size = "set_option语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_binlog_events")) {
			size = "show_binlog_events语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_binlogs")) {
			size = "show_binlogs语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_charsets")) {
			size = "show_charsets语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_collations")) {
			size = "show_collations语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_column_types")) {
			size = "how_column_types语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_create_db")) {
			size = "show_create_db语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_create_table")) {
			size = "show_create_table语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_databases")) {
			size = "show_databases语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_errors")) {
			size = "show_errors语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_fields")) {
			size = "show_fields语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_grants")) {
			size = "show_grants语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_innodb_status")) {
			size = "show_innodb_status语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_keys")) {
			size = "show_keys语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_logs")) {
			size = "show_logs语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_master_status")) {
			size = "show_master_status语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_ndb_status")) {
			size = "show_ndb_status语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_new_master")) {
			size = "show_new_master语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_open_tables")) {
			size = "show_open_tables语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_privileges")) {
			size = "show_privileges语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_processlist")) {
			size = "show_processlist语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_slave_hosts")) {
			size = "show_slave_hosts语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_slave_status")) {
			size = "show_slave_status语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_status")) {
			size = "show_status语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_storage_engines")) {
			size = "show_storage_engines语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_tables")) {
			size = "show_tables语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_triggers")) {
			size = "show_triggers语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_variables")) {
			size = "show_variables语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_show_warnings")) {
			size = "show_warnings语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_slave_start")) {
			size = "slave_start语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_slave_stop")) {
			size = "slave_stop语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_truncate")) {
			size = "truncate语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_unlock_tables")) {
			size = "unlock_tables语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_update")) {
			size = "update语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_update_multi")) {
			size = "update_multi语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_xa_commit")) {
			size = "xa_commi语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_xa_end")) {
			size = "xa_end语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_xa_prepare")) {
			size = "xa_prepare语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_xa_recover")) {
			size = "xa_recover语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_xa_rollback")) {
			size = "xa_rollback语句执行的次数";
		}
		if (size.equalsIgnoreCase("Com_xa_start")) {
			size = "xa_start语句执行的次数";
		}
		if (size.equalsIgnoreCase("Connections")) {
			size = "试图连接到(不管是否成功)MySQL服务器的连接数";
		}
		if (size.equalsIgnoreCase("Created_tmp_disk_tables")) {
			size = "服务器执行语句时在硬盘上自动创建的临时表的数量";
		}
		if (size.equalsIgnoreCase("Created_tmp_files")) {
			size = "mysqld已经创建的临时文件的数量";
		}
		if (size.equalsIgnoreCase("Created_tmp_tables")) {
			size = "服务器执行语句时自动创建的内存中的临时表的数量";
		}
		if (size.equalsIgnoreCase("Delayed_errors")) {
			size = "用INSERT DELAYED写的出现错误的行数(可能为duplicate key)";
		}
		if (size.equalsIgnoreCase("Delayed_insert_threads")) {
			size = "用使用的INSERT DELAYED处理器线程数";
		}
		if (size.equalsIgnoreCase("Delayed_writes")) {
			size = "写入的INSERT DELAYED行数";
		}
		if (size.equalsIgnoreCase("Flush_commands")) {
			size = "执行的FLUSH语句数";
		}
		if (size.equalsIgnoreCase("Handler_commit")) {
			size = "内部提交语句数";
		}
		if (size.equalsIgnoreCase("Handler_delete")) {
			size = "行从表中删除的次数";
		}
		if (size.equalsIgnoreCase("Handler_discover")) {
			size = "MySQL服务器可以问NDB CLUSTER存储引擎是否知道某一名字的表";
		}
		if (size.equalsIgnoreCase("Handler_read_first")) {
			size = "索引中第一条被读的次数";
		}
		if (size.equalsIgnoreCase("Handler_read_key")) {
			size = "根据键读一行的请求数";
		}
		if (size.equalsIgnoreCase("Handler_read_next")) {
			size = "按照键顺序读下一行的请求数";
		}
		if (size.equalsIgnoreCase("Handler_read_prev")) {
			size = "按照键顺序读前一行的请求数";
		}
		if (size.equalsIgnoreCase("Handler_read_rnd")) {
			size = "根据固定位置读一行的请求数";
		}
		if (size.equalsIgnoreCase("Handler_read_rnd_next")) {
			size = "在数据文件中读下一行的请求数";
		}
		if (size.equalsIgnoreCase("Handler_rollback")) {
			size = "内部ROLLBACK语句的数量";
		}
		if (size.equalsIgnoreCase("Handler_update")) {
			size = "在表内更新一行的请求数";
		}
		if (size.equalsIgnoreCase("Handler_write")) {
			size = "在表内插入一行的请求数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_data")) {
			size = "包含数据的页数(脏或干净)";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_dirty")) {
			size = "当前的脏页数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_flushed")) {
			size = "求清空的缓冲池页数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_free")) {
			size = "空页数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_latched")) {
			size = "在InnoDB缓冲池中锁定的页数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_misc")) {
			size = "忙的页数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_total")) {
			size = "缓冲池总大小（页数）";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_read_ahead_rnd")) {
			size = "InnoDB初始化的“随机”read-aheads数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_read_ahead_seq")) {
			size = "InnoDB初始化的顺序read-aheads数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_read_requests")) {
			size = "InnoDB已经完成的逻辑读请求数";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_reads")) {
			size = "不能满足InnoDB必须单页读取的缓冲池中的逻辑读数量";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_wait_free")) {
			size = "没有特殊情况 通过后台向InnoDB缓冲池写";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_write_requests")) {
			size = "向InnoDB缓冲池的写数量";
		}
		if (size.equalsIgnoreCase("Innodb_data_fsyncs")) {
			size = "fsync()操作数";
		}
		if (size.equalsIgnoreCase("Innodb_data_pending_fsyncs")) {
			size = "当前挂起的fsync()操作数";
		}
		if (size.equalsIgnoreCase("Innodb_data_pending_reads")) {
			size = "当前挂起的读数";
		}
		if (size.equalsIgnoreCase("Innodb_data_pending_writes")) {
			size = "当前挂起的写数";
		}
		if (size.equalsIgnoreCase("Innodb_data_read")) {
			size = "至此已经读取的数据数量（字节）";
		}
		if (size.equalsIgnoreCase("Innodb_data_reads")) {
			size = "数据读总数量";
		}
		if (size.equalsIgnoreCase("IInnodb_data_writes")) {
			size = "数据写总数量";
		}
		if (size.equalsIgnoreCase("Innodb_data_written")) {
			size = "至此已经写入的数据量（字节）";
		}
		if (size.equalsIgnoreCase("Innodb_dblwr_pages_written")) {
			size = "为已经执行的双写操作数量写好的页数";
		}
		if (size.equalsIgnoreCase("Innodb_dblwr_writes")) {
			size = "已经执行的双写操作数量";
		}
		if (size.equalsIgnoreCase("Innodb_log_waits")) {
			size = "必须等待的时间，因为日志缓冲区太小，在继续前必须先等待对它清空";
		}
		if (size.equalsIgnoreCase("Innodb_log_write_requests")) {
			size = "日志写请求数";
		}
		if (size.equalsIgnoreCase("Innodb_log_writes")) {
			size = "向日志文件的物理写数量";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_fsyncs")) {
			size = "向日志文件完成的fsync()写数量";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_pending_fsyncs")) {
			size = "挂起的日志文件fsync()操作数量";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_pending_writes")) {
			size = "挂起的日志文件写操作";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_written")) {
			size = "写入日志文件的字节数";
		}
		if (size.equalsIgnoreCase("Innodb_page_size")) {
			size = "编译的InnoDB页大小(默认16KB) 许多值用页来记数 页的大小很容易转换为字节";
		}
		if (size.equalsIgnoreCase("Innodb_pages_created")) {
			size = "创建的页数";
		}
		if (size.equalsIgnoreCase("Innodb_pages_read")) {
			size = "读取的页数";
		}
		if (size.equalsIgnoreCase("Innodb_pages_written")) {
			size = "写入的页数";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_current_waits")) {
			size = "当前等待的待锁定的行数";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_time")) {
			size = "行锁定花费的总时间，单位毫秒";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_time_avg")) {
			size = "行锁定的平均时间，单位毫秒";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_time_max")) {
			size = "行锁定的最长时间，单位毫秒";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_waits")) {
			size = "一行锁定必须等待的时间数";
		}
		if (size.equalsIgnoreCase("Innodb_rows_deleted")) {
			size = "从InnoDB表删除的行数";
		}
		if (size.equalsIgnoreCase("Innodb_rows_inserted")) {
			size = "插入到InnoDB表的行数";
		}
		if (size.equalsIgnoreCase("Innodb_rows_read")) {
			size = "从InnoDB表读取的行数";
		}
		if (size.equalsIgnoreCase("Innodb_rows_updated")) {
			size = "InnoDB表内更新的行数";
		}
		if (size.equalsIgnoreCase("Key_blocks_not_flushed")) {
			size = "键缓存内已经更改但还没有清空到硬盘上的键的数据块数量";
		}
		if (size.equalsIgnoreCase("Key_blocks_unused")) {
			size = "键缓存内未使用的块数量";
		}
		if (size.equalsIgnoreCase("Key_blocks_used")) {
			size = "键缓存内使用的块数量";
		}
		if (size.equalsIgnoreCase("Key_read_requests")) {
			size = "从缓存读键的数据块的请求数";
		}
		if (size.equalsIgnoreCase("Key_reads")) {
			size = "从硬盘读取键的数据块的次数";
		}
		if (size.equalsIgnoreCase("Key_write_requests")) {
			size = "将键的数据块写入缓存的请求数";
		}
		if (size.equalsIgnoreCase("Key_writes")) {
			size = "向硬盘写入将键的数据块的物理写操作的次数";
		}
		if (size.equalsIgnoreCase("Last_query_cost")) {
			size = "用查询优化器计算的最后编译的查询的总成本";
		}
		if (size.equalsIgnoreCase("Max_used_connections")) {
			size = "服务器启动后已经同时使用的连接的最大数量";
		}
		if (size.equalsIgnoreCase("Not_flushed_delayed_rows")) {
			size = "等待写入INSERT DELAY队列的行数";
		}
		if (size.equalsIgnoreCase("Open_files")) {
			size = "打开的文件的数目";
		}
		if (size.equalsIgnoreCase("Open_streams")) {
			size = "打开的流的数量(主要用于记录)";
		}
		if (size.equalsIgnoreCase("Open_tables")) {
			size = "当前打开的表的数量";
		}
		if (size.equalsIgnoreCase("Opened_tables")) {
			size = "已经打开的表的数量";
		}
		if (size.equalsIgnoreCase("Qcache_free_blocks")) {
			size = "查询缓存内自由内存块的数量";
		}
		if (size.equalsIgnoreCase("Qcache_free_memory")) {
			size = "用于查询缓存的自由内存的数量";
		}
		if (size.equalsIgnoreCase("Qcache_hits")) {
			size = "查询缓存被访问的次数";
		}
		if (size.equalsIgnoreCase("Qcache_inserts")) {
			size = "加入到缓存的查询数量";
		}
		if (size.equalsIgnoreCase("Qcache_lowmem_prunes")) {
			size = "由于内存较少从缓存删除的查询数量";
		}
		if (size.equalsIgnoreCase("Qcache_not_cached")) {
			size = "非缓存查询数(不可缓存，或由于query_cache_type设定值未缓存)";
		}
		if (size.equalsIgnoreCase("Qcache_queries_in_cache")) {
			size = "登记到缓存内的查询的数量";
		}
		if (size.equalsIgnoreCase("Qcache_total_blocks")) {
			size = "查询缓存内的总块数";
		}
		if (size.equalsIgnoreCase("Questions")) {
			size = "已经发送给服务器的查询的个数";
		}
		if (size.equalsIgnoreCase("Rpl_status")) {
			size = "失败安全复制状态(还未使用)";
		}
		if (size.equalsIgnoreCase("Select_full_join")) {
			size = "没有使用索引的联接的数量。如果该值不为0,你应仔细检查表的索引";
		}
		if (size.equalsIgnoreCase("Select_full_range_join")) {
			size = "在引用的表中使用范围搜索的联接的数量";
		}
		if (size.equalsIgnoreCase("Select_range")) {
			size = "在第一个表中使用范围的联接的数量";
		}
		if (size.equalsIgnoreCase("Select_range_check")) {
			size = "在每一行数据后对键值进行检查的不带键值的联接的数量";
		}
		if (size.equalsIgnoreCase("Select_scan")) {
			size = "对第一个表进行完全扫描的联接的数量";
		}
		if (size.equalsIgnoreCase("Slave_open_temp_tables")) {
			size = "当前由从SQL线程打开的临时表的数量";
		}
		if (size.equalsIgnoreCase("Slave_retried_transactions")) {
			size = "启动后复制从服务器SQL线程尝试事务的总次数";
		}
		if (size.equalsIgnoreCase("Slave_running")) {
			size = "如果该服务器是连接到主服务器的从服务器，则该值为ON";
		}
		if (size.equalsIgnoreCase("Slow_launch_threads")) {
			size = "创建时间超过slow_launch_time秒的线程数";
		}
		if (size.equalsIgnoreCase("Slow_queries")) {
			size = "查询时间超过long_query_time秒的查询的个数";
		}
		if (size.equalsIgnoreCase("Sort_merge_passes")) {
			size = "排序算法已经执行的合并的数量";
		}
		if (size.equalsIgnoreCase("Sort_range")) {
			size = "在范围内执行的排序的数量";
		}
		if (size.equalsIgnoreCase("Sort_rows")) {
			size = "已经排序的行数";
		}
		if (size.equalsIgnoreCase("Sort_scan")) {
			size = "通过扫描表完成的排序的数量";
		}
		if (size.equalsIgnoreCase("Ssl_accept_renegotiates")) {
			size = "用于SSL连接的变量";
		}
		if (size.equalsIgnoreCase("Ssl_accept_renegotiates")) {
			size = "用于SSL连接的变量accept_renegotiates";
		}
		if (size.equalsIgnoreCase("Ssl_accepts")) {
			size = "用于SSL连接的变量accepts";
		}
		if (size.equalsIgnoreCase("Ssl_callback_cache_hits")) {
			size = "用于SSL连接的变量callback_cache_hits";
		}
		if (size.equalsIgnoreCase("Ssl_cipher")) {
			size = "用于SSL连接的变量cipher";
		}
		if (size.equalsIgnoreCase("Ssl_cipher_list")) {
			size = "用于SSL连接的变量cipher_list";
		}
		if (size.equalsIgnoreCase("Ssl_client_connects")) {
			size = "用于SSL连接的变量client_connects";
		}
		if (size.equalsIgnoreCase("Ssl_connect_renegotiates")) {
			size = "用于SSL连接的变量connect_renegotiates";
		}
		if (size.equalsIgnoreCase("Ssl_ctx_verify_depth")) {
			size = "用于SSL连接的变量ctx_verify_depth";
		}
		if (size.equalsIgnoreCase("Ssl_ctx_verify_mode")) {
			size = "用于SSL连接的变量ctx_verify_mode";
		}
		if (size.equalsIgnoreCase("Ssl_default_timeout")) {
			size = "用于SSL连接的变量default_timeout";
		}
		if (size.equalsIgnoreCase("Ssl_finished_accepts")) {
			size = "用于SSL连接的变量finished_accepts";
		}
		if (size.equalsIgnoreCase("Ssl_finished_connects")) {
			size = "用于SSL连接的变量finished_connects";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_hits")) {
			size = "用于SSL连接的变量session_cache_hits";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_misses")) {
			size = "用于SSL连接的变量session_cache_misses";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_mode")) {
			size = "用于SSL连接的变量session_cache_mode";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_overflows")) {
			size = "用于SSL连接的变量session_cache_overflows";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_size")) {
			size = "用于SSL连接的变量session_cache_size";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_timeouts")) {
			size = "用于SSL连接的变量session_cache_timeouts";
		}
		if (size.equalsIgnoreCase("Ssl_sessions_reused")) {
			size = "用于SSL连接的变量sessions_reused";
		}
		if (size.equalsIgnoreCase("Ssl_used_session_cache_entries")) {
			size = "用于SSL连接的变量used_session_cache_entries";
		}
		if (size.equalsIgnoreCase("Ssl_verify_depth")) {
			size = "用于SSL连接的变量verify_depth";
		}
		if (size.equalsIgnoreCase("Ssl_verify_mode")) {
			size = "用于SSL连接的变量verify_mode";
		}
		if (size.equalsIgnoreCase("Ssl_version")) {
			size = "用于SSL连接的变量version";
		}
		if (size.equalsIgnoreCase("Table_locks_immediate")) {
			size = "立即获得的表的锁的次数";
		}
		if (size.equalsIgnoreCase("Table_locks_waited")) {
			size = "不能立即获得的表的锁的次数";
		}
		if (size.equalsIgnoreCase("Threads_cached")) {
			size = "线程缓存内的线程的数量";
		}
		if (size.equalsIgnoreCase("Threads_connected")) {
			size = "当前打开的连接的数量";
		}
		if (size.equalsIgnoreCase("Threads_created")) {
			size = "创建用来处理连接的线程数";
		}
		if (size.equalsIgnoreCase("Threads_running")) {
			size = "激活的（非睡眠状态）线程数";
		}
		if (size.equalsIgnoreCase("Uptime")) {
			size = "服务器已经运行的时间（以秒为单位）";
		}
		return size;
	}

	private String getConfigForMysqlConfig(String size) {
		if (size.equalsIgnoreCase("auto_increment_increment")) {
			size = "控制列中的值的增量值";
		}
		if (size.equalsIgnoreCase("auto_increment_offset")) {
			size = "确定AUTO_INCREMENT列值的起点";
		}
		if (size.equalsIgnoreCase("automatic_sp_privileges")) {
			size = "automatic_sp_privileges";
		}
		if (size.equalsIgnoreCase("back_log")) {
			size = "连接请求的数量";
		}
		if (size.equalsIgnoreCase("basedir")) {
			size = "MySQL安装基准目录";
		}
		if (size.equalsIgnoreCase("binlog_cache_size")) {
			size = "容纳二进制日志SQL语句的缓存大小";
		}
		if (size.equalsIgnoreCase("bulk_insert_buffer_size")) {
			size = "每线程的字节数限制缓存树的大小";
		}
		if (size.equalsIgnoreCase("character_set_client")) {
			size = "来自客户端的语句的字符集";
		}
		if (size.equalsIgnoreCase("character_set_connection")) {
			size = "没有字符集导入符的字符串转换";
		}
		if (size.equalsIgnoreCase("character_set_database")) {
			size = "默认数据库使用的字符集";
		}
		if (size.equalsIgnoreCase("character_set_filesystem")) {
			size = "character_set_filesystem";
		}
		if (size.equalsIgnoreCase("character_set_results")) {
			size = "用于向客户端返回查询结果的字符集";
		}
		if (size.equalsIgnoreCase("character_set_server")) {
			size = "服务器的默认字符集";
		}
		if (size.equalsIgnoreCase("character_set_system")) {
			size = "服务器用来保存识别符的字符集";
		}
		if (size.equalsIgnoreCase("character_sets_dir")) {
			size = "字符集安装目录";
		}
		if (size.equalsIgnoreCase("collation_connection")) {
			size = "连接字符集的校对规则";
		}
		if (size.equalsIgnoreCase("collation_database")) {
			size = "默认数据库使用的校对规则";
		}
		if (size.equalsIgnoreCase("collation_server")) {
			size = "服务器的默认校对规则";
		}
		if (size.equalsIgnoreCase("completion_type")) {
			size = "事务结束类型";
		}
		if (size.equalsIgnoreCase("concurrent_insert")) {
			size = "存储值情况";
		}
		if (size.equalsIgnoreCase("connect_timeout")) {
			size = "服务器用Bad handshake响应前等待连接包的秒数";
		}
		if (size.equalsIgnoreCase("datadir")) {
			size = "MySQL数据目录";
		}
		if (size.equalsIgnoreCase("date_format")) {
			size = "date_format(为被使用)";
		}
		if (size.equalsIgnoreCase("datetime_format")) {
			size = "datetime_format(为被使用)";
		}
		if (size.equalsIgnoreCase("default_week_format")) {
			size = "WEEK() 函数使用的默认模式";
		}
		if (size.equalsIgnoreCase("delay_key_write")) {
			size = "使用的DELAY_KEY_WRITE表选项的处理";
		}
		if (size.equalsIgnoreCase("delayed_insert_limit")) {
			size = "INSERT DELAYED处理器线程检查是否有挂起的SELECT语句";
		}
		if (size.equalsIgnoreCase("delayed_insert_timeout")) {
			size = "INSERT DELAYED处理器线程终止前应等待INSERT语句的时间";
		}
		if (size.equalsIgnoreCase("delayed_queue_size")) {
			size = "处理INSERT DELAYED语句时队列中行的数量限制";
		}
		// if (size.equalsIgnoreCase("div_precision_increment")) {
		// size = "用/操作符执行除操作的结果可增加的精确度的位数";
		// }
		if (size.equalsIgnoreCase("engine_condition_pushdown")) {
			size = "适用于NDB的检测";
		}
		if (size.equalsIgnoreCase("expire_logs_days")) {
			size = "二进制日志自动删除的天数";
		}
		if (size.equalsIgnoreCase("flush")) {
			size = "flush选项启动mysqld值";
		}
		if (size.equalsIgnoreCase("flush_time")) {
			size = "查看释放资源情况";
		}
		if (size.equalsIgnoreCase("ft_boolean_syntax")) {
			size = "使用IN BOOLEAN MODE执行的布尔全文搜索支持的操作符系列";
		}
		if (size.equalsIgnoreCase("ft_max_word_len")) {
			size = "FULLTEXT索引中所包含的字的最大长度";
		}
		if (size.equalsIgnoreCase("ft_min_word_len")) {
			size = "FULLTEXT索引中所包含的字的最小长度";
		}
		if (size.equalsIgnoreCase("ft_query_expansion_limit")) {
			size = "使用WITH QUERY EXPANSION进行全文搜索的最大匹配数";
		}
		if (size.equalsIgnoreCase("ft_stopword_file")) {
			size = "用于读取全文搜索的停止字清单的文件";
		}
		if (size.equalsIgnoreCase("group_concat_max_len")) {
			size = "允许的GROUP_CONCAT()函数结果的最大长度";
		}
		if (size.equalsIgnoreCase("have_archive")) {
			size = "mysqld支持ARCHIVE表支持表情况";
		}
		if (size.equalsIgnoreCase("have_bdb")) {
			size = "mysqld支持BDB表情况";
		}
		if (size.equalsIgnoreCase("have_blackhole_engine")) {
			size = "mysqld支持BLACKHOLE表情况";
		}
		if (size.equalsIgnoreCase("have_compress")) {
			size = "是否zlib压缩库适合该服务器";
		}
		if (size.equalsIgnoreCase("have_crypt")) {
			size = "是否crypt()系统调用适合该服务器";
		}
		if (size.equalsIgnoreCase("have_csv")) {
			size = "mysqld支持ARCHIVE表情况";
		}
		if (size.equalsIgnoreCase("have_example_engine")) {
			size = "mysqld支持EXAMPLE表情况";
		}
		if (size.equalsIgnoreCase("have_federated_engine")) {
			size = "mysqld支持FEDERATED表情况";
		}
		if (size.equalsIgnoreCase("have_geometry")) {
			size = "是否服务器支持空间数据类型";
		}
		if (size.equalsIgnoreCase("have_innodb")) {
			size = "mysqld支持InnoDB表情况";
		}
		if (size.equalsIgnoreCase("have_isam")) {
			size = "向后兼容";
		}
		if (size.equalsIgnoreCase("have_ndbcluster")) {
			size = "mysqld支持NDB CLUSTER表情况";
		}
		if (size.equalsIgnoreCase("have_openssl")) {
			size = "mysqld支持客户端/服务器协议的SSL(加密)情况";
		}
		if (size.equalsIgnoreCase("have_query_cache")) {
			size = "mysqld支持查询缓存情况";
		}
		if (size.equalsIgnoreCase("have_raid")) {
			size = "mysqld支持RAID选项情况";
		}
		if (size.equalsIgnoreCase("have_rtree_keys")) {
			size = "RTREE索引是否可用";
		}
		if (size.equalsIgnoreCase("have_symlink")) {
			size = "是否启用符号链接支持";
		}
		if (size.equalsIgnoreCase("init_connect")) {
			size = "字符串处理";
		}
		if (size.equalsIgnoreCase("init_file")) {
			size = "启动服务器时用--init-file选项指定的文件名";
		}
		if (size.equalsIgnoreCase("init_slave")) {
			size = "SQL线程启动时从服务器应执行该字符串";
		}
		if (size.equalsIgnoreCase("innodb_additional_mem_pool_size")) {
			size = "InnoDB用来存储数据内存大小情况";
		}
		if (size.equalsIgnoreCase("innodb_autoextend_increment")) {
			size = "表空间被填满之时扩展表空间的尺寸";
		}
		if (size.equalsIgnoreCase("innodb_buffer_pool_awe_mem_mb")) {
			size = "缓冲池被放在32位Windows的AWE内存里缓存池大小";
		}
		if (size.equalsIgnoreCase("innodb_buffer_pool_size")) {
			size = "InnoDB用来缓存它的数据和索引的内存缓冲区的大小";
		}
		if (size.equalsIgnoreCase("innodb_checksums")) {
			size = "InnoDB在所有对磁盘的页面读取上的状态";
		}
		if (size.equalsIgnoreCase("innodb_commit_concurrency")) {
			size = "innodb_commit_concurrency";
		}
		if (size.equalsIgnoreCase("innodb_concurrency_tickets")) {
			size = "innodb_concurrency_tickets";
		}
		if (size.equalsIgnoreCase("innodb_data_file_path")) {
			size = "单独数据文件和它们尺寸的路径";
		}
		if (size.equalsIgnoreCase("innodb_data_home_dir")) {
			size = "目录路径对所有InnoDB数据文件的共同部分";
		}
		if (size.equalsIgnoreCase("innodb_doublewrite")) {
			size = "InnoDB存储所有数据情况";
		}
		if (size.equalsIgnoreCase("innodb_fast_shutdown")) {
			size = "InnoDB在关闭情况的值选择";
		}
		if (size.equalsIgnoreCase("innodb_file_io_threads")) {
			size = "InnoDB中文件I/O线程的数";
		}
		if (size.equalsIgnoreCase("innodb_file_per_table")) {
			size = "确定是否InnoDB用自己的.ibd文件为存储数据和索引创建每一个新表";
		}
		if (size.equalsIgnoreCase("innodb_flush_log_at_trx_commit")) {
			size = "InnoDB对日志操作情况";
		}
		if (size.equalsIgnoreCase("innodb_flush_method")) {
			size = "InnoDB使用fsync()来刷新数据和日志文件";
		}
		if (size.equalsIgnoreCase("innodb_force_recovery")) {
			size = "损坏的数据库转储表的方案";
		}
		if (size.equalsIgnoreCase("innodb_lock_wait_timeout")) {
			size = "InnoDB事务在被回滚之前可以等待一个锁定的超时秒数";
		}
		if (size.equalsIgnoreCase("innodb_locks_unsafe_for_binlog")) {
			size = "InnoDB搜索和索引扫描中关闭下一键锁定";
		}
		if (size.equalsIgnoreCase("innodb_log_arch_dir")) {
			size = "使用日志档案 被完整写入的日志文件所在的目录的归档值";
		}
		if (size.equalsIgnoreCase("innodb_log_archive")) {
			size = "日志处理情况";
		}
		if (size.equalsIgnoreCase("innodb_log_buffer_size")) {
			size = "InnoDB用来往磁盘上的日志文件写操作的缓冲区的大小";
		}
		if (size.equalsIgnoreCase("innodb_log_file_size")) {
			size = "日志组里每个日志文件的大小";
		}
		if (size.equalsIgnoreCase("innodb_log_files_in_group")) {
			size = "日志组里日志文件的数目";
		}
		if (size.equalsIgnoreCase("innodb_log_group_home_dir")) {
			size = "InnoDB日志文件的目录路径";
		}
		if (size.equalsIgnoreCase("innodb_max_dirty_pages_pct")) {
			size = "InnoDB中处理脏页的情况";
		}
		if (size.equalsIgnoreCase("innodb_max_purge_lag")) {
			size = "净化操作被滞后之时，如何延迟INSERT,UPDATE和DELETE操作";
		}
		if (size.equalsIgnoreCase("innodb_mirrored_log_groups")) {
			size = "为数据库保持的日志组内同样拷贝的数量";
		}
		if (size.equalsIgnoreCase("innodb_open_files")) {
			size = "定InnoDB一次可以保持打开的.ibd文件的最大数";
		}
		if (size.equalsIgnoreCase("innodb_support_xa")) {
			size = "InnoDB支持在XA事务中的双向提交情况";
		}
		if (size.equalsIgnoreCase("innodb_sync_spin_loops")) {
			size = "innodb_sync_spin_loops";
		}
		if (size.equalsIgnoreCase("innodb_table_locks")) {
			size = "InnoDB对表的锁定情况";
		}
		if (size.equalsIgnoreCase("innodb_thread_concurrency")) {
			size = "InnoDB试着在InnoDB内保持操作系统线程的数量少于或等于这个参数给出的限制范围";
		}
		if (size.equalsIgnoreCase("innodb_thread_sleep_delay")) {
			size = "让InnoDB为周期的SHOW INNODB STATUS输出创建一个文件<datadir>/innodb_status";
		}
		if (size.equalsIgnoreCase("interactive_timeout")) {
			size = "服务器关闭交互式连接前等待活动的秒数";
		}
		if (size.equalsIgnoreCase("join_buffer_size")) {
			size = "用于完全联接的缓冲区的大小";
		}
		if (size.equalsIgnoreCase("key_buffer_size")) {
			size = "索引块缓冲区的大小";
		}
		if (size.equalsIgnoreCase("key_cache_age_threshold")) {
			size = "控制将缓冲区从键值缓存热子链(sub-chain)降级到温子链(sub-chain)的值";
		}
		if (size.equalsIgnoreCase("key_cache_block_size")) {
			size = "键值缓存内块的字节大小";
		}
		if (size.equalsIgnoreCase("key_cache_division_limit")) {
			size = "键值缓存缓冲区链热子链和温子链的划分点";
		}
		if (size.equalsIgnoreCase("language")) {
			size = "错误消息所用语言";
		}
		if (size.equalsIgnoreCase("large_files_support")) {
			size = "mysqld编译时是否使用了大文件支持选项";
		}
		if (size.equalsIgnoreCase("large_page_size")) {
			size = "large_page_size";
		}
		if (size.equalsIgnoreCase("large_pages")) {
			size = "是否启用了大页面支持";
		}
		if (size.equalsIgnoreCase("license")) {
			size = "服务器的许可类型";
		}
		if (size.equalsIgnoreCase("local_infile")) {
			size = "是否LOCAL支持LOAD DATA INFILE语句";
		}
		if (size.equalsIgnoreCase("log")) {
			size = "是否启用将所有查询记录到常规查询日志中";
		}
		if (size.equalsIgnoreCase("log_bin")) {
			size = "是否启用二进制日志";
		}
		if (size.equalsIgnoreCase("log_bin_trust_function_creators")) {
			size = "是否可以信任保存的程序的作者不会创建向二进制日志写入不安全事件的程序";
		}
		if (size.equalsIgnoreCase("log_error")) {
			size = "错误日志的位置";
		}
		if (size.equalsIgnoreCase("log_slave_updates")) {
			size = "是否从服务器从主服务器收到的更新应记入从服务器自己的二进制日志";
		}
		if (size.equalsIgnoreCase("log_slow_queries")) {
			size = "是否记录慢查询";
		}
		if (size.equalsIgnoreCase("log_warnings")) {
			size = "是否产生其它警告消息";
		}
		if (size.equalsIgnoreCase("long_query_time")) {
			size = "查询时间超过该值，则增加Slow_queries状态变量";
		}
		if (size.equalsIgnoreCase("low_priority_updates")) {
			size = "表示sql语句等待语句将等待直到受影响的表没有挂起的SELECT或LOCK TABLE READ";
		}
		if (size.equalsIgnoreCase("lower_case_file_system")) {
			size = "说明是否数据目录所在的文件系统对文件名的大小写敏感";
		}
		if (size.equalsIgnoreCase("lower_case_table_names")) {
			size = "为1表示表名用小写保存到硬盘上，并且表名比较时不对大小写敏感";
		}
		if (size.equalsIgnoreCase("max_allowed_packet")) {
			size = "包或任何生成的/中间字符串的最大大小";
		}
		if (size.equalsIgnoreCase("max_binlog_cache_size")) {
			size = "多语句事务需要更大的内存时出现的情况";
		}
		if (size.equalsIgnoreCase("max_binlog_size")) {
			size = "多语句事务需要更大的内存时出现的情况";
		}
		if (size.equalsIgnoreCase("max_connect_errors")) {
			size = "断的与主机的连接的最大限制数";
		}
		if (size.equalsIgnoreCase("max_connections")) {
			size = "允许的并行客户端连接数目";
		}
		if (size.equalsIgnoreCase("max_delayed_threads")) {
			size = "启动线程来处理INSERT DELAYED语句的限制数";
		}
		if (size.equalsIgnoreCase("max_error_count")) {
			size = "存由SHOW ERRORS或SHOW WARNINGS显示的错误、警告和注解的最大数目";
		}
		if (size.equalsIgnoreCase("max_heap_table_size")) {
			size = "设置MEMORY (HEAP)表可以增长到的最大空间大小";
		}
		if (size.equalsIgnoreCase("max_insert_delayed_threads")) {
			size = "启动线程来处理INSERT DELAYED语句的限制数(同max_delayed_threads)";
		}
		if (size.equalsIgnoreCase("max_join_size")) {
			size = "不允许可能需要检查多于max_join_size行的情况";
		}
		if (size.equalsIgnoreCase("max_length_for_sort_data")) {
			size = "确定使用的filesort算法的索引值大小的限值";
		}
		if (size.equalsIgnoreCase("max_prepared_stmt_count")) {
			size = "max_prepared_stmt_count";
		}
		if (size.equalsIgnoreCase("max_relay_log_size")) {
			size = "如果复制从服务器写入中继日志时超出给定值，则滚动中继日";
		}
		if (size.equalsIgnoreCase("max_seeks_for_key")) {
			size = "限制根据键值寻找行时的最大搜索数";
		}
		if (size.equalsIgnoreCase("max_sort_length")) {
			size = "排序BLOB或TEXT值时使用的字节数";
		}
		if (size.equalsIgnoreCase("max_sp_recursion_depth")) {
			size = "max_sp_recursion_depth";
		}
		if (size.equalsIgnoreCase("max_tmp_tables")) {
			size = "客户端可以同时打开的临时表的最大数";
		}
		if (size.equalsIgnoreCase("max_user_connections")) {
			size = "给定的MySQL账户允许的最大同时连接数";
		}
		if (size.equalsIgnoreCase("max_write_lock_count")) {
			size = "超过写锁定限制后，允许部分读锁定";
		}
		if (size.equalsIgnoreCase("multi_range_count")) {
			size = "multi_range_count";
		}
		if (size.equalsIgnoreCase("myisam_data_pointer_size")) {
			size = "默认指针大小的值";
		}
		if (size.equalsIgnoreCase("myisam_max_sort_file_size")) {
			size = "重建MyISAM索引时，允许MySQL使用的临时文件的最大空间大小";
		}
		if (size.equalsIgnoreCase("myisam_recover_options")) {
			size = "myisam-recover选项的值";
		}
		if (size.equalsIgnoreCase("myisam_repair_threads")) {
			size = "如果该值大于1，在Repair by sorting过程中并行创建MyISAM表索引";
		}
		if (size.equalsIgnoreCase("myisam_sort_buffer_size")) {
			size = "在REPAIR TABLE或用CREATE INDEX创建索引或ALTER TABLE过程中排序MyISAM索引分配的缓冲区";
		}
		if (size.equalsIgnoreCase("myisam_stats_method")) {
			size = "MyISAM表搜集关于索引值分发的统计信息时服务器如何处理NULL值";
		}
		if (size.equalsIgnoreCase("named_pipe")) {
			size = "明服务器是否支持命名管道连接";
		}
		if (size.equalsIgnoreCase("net_buffer_length")) {
			size = "在查询之间将通信缓冲区重设为该值";
		}
		if (size.equalsIgnoreCase("net_read_timeout")) {
			size = "中断读前等待连接的其它数据的秒数";
		}
		if (size.equalsIgnoreCase("net_retry_count")) {
			size = "表示某个通信端口的读操作中断了，在放弃前重试多次";
		}
		if (size.equalsIgnoreCase("net_write_timeout")) {
			size = "中断写之前等待块写入连接的秒数";
		}
		if (size.equalsIgnoreCase("new")) {
			size = "表示在MySQL 4.0中使用该变量来打开4.1中的一些行为，并用于向后兼容性";
		}
		if (size.equalsIgnoreCase("old_passwords")) {
			size = "是否服务器应为MySQL用户账户使用pre-4.1-style密码性";
		}
		if (size.equalsIgnoreCase("open_files_limit")) {
			size = "操作系统允许mysqld打开的文件的数量";
		}
		if (size.equalsIgnoreCase("optimizer_prune_level")) {
			size = "在查询优化从优化器搜索空间裁减低希望局部计划中使用的控制方法 0表示禁用方法";
		}
		if (size.equalsIgnoreCase("optimizer_search_depth")) {
			size = "查询优化器进行的搜索的最大深度";
		}
		if (size.equalsIgnoreCase("pid_file")) {
			size = "进程ID (PID)文件的路径名";
		}
		if (size.equalsIgnoreCase("prepared_stmt_count")) {
			size = "prepared_stmt_count";
		}
		if (size.equalsIgnoreCase("port")) {
			size = "服务器帧听TCP/IP连接所用端口";
		}
		if (size.equalsIgnoreCase("preload_buffer_size")) {
			size = "重载索引时分配的缓冲区大小";
		}
		if (size.equalsIgnoreCase("protocol_version")) {
			size = "MySQL服务器使用的客户端/服务器协议的版本";
		}
		if (size.equalsIgnoreCase("query_alloc_block_size")) {
			size = "为查询分析和执行过程中创建的对象分配的内存块大小";
		}
		if (size.equalsIgnoreCase("query_cache_limit")) {
			size = "不要缓存大于该值的结果";
		}
		if (size.equalsIgnoreCase("query_cache_min_res_unit")) {
			size = "查询缓存分配的最小块的大小(字节)";
		}
		if (size.equalsIgnoreCase("query_cache_size")) {
			size = "为缓存查询结果分配的内存的数量";
		}
		if (size.equalsIgnoreCase("query_cache_type")) {
			size = "设置查询缓存类型";
		}
		if (size.equalsIgnoreCase("query_cache_wlock_invalidate")) {
			size = "对表进行WRITE锁定的设置值";
		}
		if (size.equalsIgnoreCase("query_prealloc_size")) {
			size = "用于查询分析和执行的固定缓冲区的大小";
		}
		if (size.equalsIgnoreCase("range_alloc_block_size")) {
			size = "范围优化时分配的块的大小";
		}
		if (size.equalsIgnoreCase("read_buffer_size")) {
			size = "每个线程连续扫描时为扫描的每个表分配的缓冲区的大小(字节)";
		}
		if (size.equalsIgnoreCase("read_only")) {
			size = "变量对复制从服务器设置为ON时，服务器是否允许更新";
		}
		if (size.equalsIgnoreCase("read_only")) {
			size = "变量对复制从服务器设置为ON时，从服务器不允许更新";
		}
		if (size.equalsIgnoreCase("relay_log_purge")) {
			size = "当不再需要中继日志时禁用或启用自动清空中继日志";
		}
		if (size.equalsIgnoreCase("read_rnd_buffer_size")) {
			size = "当排序后按排序后的顺序读取行时，则通过该缓冲区读取行，避免搜索硬盘";
		}
		if (size.equalsIgnoreCase("secure_auth")) {
			size = "如果用--secure-auth选项启动了MySQL服务器，是否将阻塞有旧格式(4.1之前)密码的所有账户所发起的连接";
		}
		if (size.equalsIgnoreCase("shared_memory")) {
			size = "(只用于Windows)服务器是否允许共享内存连接";
		}
		if (size.equalsIgnoreCase("shared_memory_base_name")) {
			size = "(只用于Windows)说明服务器是否允许共享内存连接，并为共享内存设置识别符";
		}
		if (size.equalsIgnoreCase("server_id")) {
			size = "用于主复制服务器和从复制服务器";
		}
		if (size.equalsIgnoreCase("skip_external_locking")) {
			size = "mysqld是否使用外部锁定";
		}
		if (size.equalsIgnoreCase("skip_networking")) {
			size = "如果服务器只允许本地(非TCP/IP)连接";
		}
		if (size.equalsIgnoreCase("skip_show_database")) {
			size = "防止不具有SHOW DATABASES权限的人们使用SHOW DATABASES语句";
		}
		if (size.equalsIgnoreCase("slave_compressed_protocol")) {
			size = "如果主、从服务器均支持，确定是否使用从/主压缩协议";
		}
		if (size.equalsIgnoreCase("slave_load_tmpdir")) {
			size = "从服务器为复制LOAD DATA INFILE语句创建临时文件的目录名";
		}
		if (size.equalsIgnoreCase("slave_net_timeout")) {
			size = "放弃读操作前等待主/从连接的更多数据的等待秒数";
		}
		if (size.equalsIgnoreCase("slave_skip_errors")) {
			size = "从服务器应跳过(忽视)的复制错误";
		}
		if (size.equalsIgnoreCase("slave_transaction_retries")) {
			size = "复制从服务器SQL线程未能执行事务，在提示错误并停止前它自动重复slave_transaction_retries次";
		}
		if (size.equalsIgnoreCase("slow_launch_time")) {
			size = "如果创建线程的时间超过该秒数，服务器增加Slow_launch_threads状态变量";
		}
		if (size.equalsIgnoreCase("sort_buffer_size")) {
			size = "每个排序线程分配的缓冲区的大小";
		}
		if (size.equalsIgnoreCase("sql_mode")) {
			size = "当前的服务器SQL模式，可以动态设置";
		}
		if (size.equalsIgnoreCase("storage_engine")) {
			size = "该变量是table_typeis的同义词。在MySQL 5.1中,首选storage_engine";
		}
		if (size.equalsIgnoreCase("sync_binlog")) {
			size = "如果为正，当每个sync_binlog'th写入该二进制日志后，MySQL服务器将它的二进制日志同步到硬盘上";
		}
		if (size.equalsIgnoreCase("sync_frm")) {
			size = "如果该变量设为1,当创建非临时表时它的.frm文件是否被同步到硬盘上";
		}
		if (size.equalsIgnoreCase("system_time_zone")) {
			size = "服务器系统时区";
		}
		if (size.equalsIgnoreCase("table_cache")) {
			size = "所有线程打开的表的数目";
		}
		if (size.equalsIgnoreCase("table_type")) {
			size = "默认表类型(存储引擎)";
		}
		if (size.equalsIgnoreCase("thread_cache_size")) {
			size = "服务器应缓存多少线程以便重新使用";
		}
		if (size.equalsIgnoreCase("thread_stack")) {
			size = "每个线程的堆栈大小";
		}
		if (size.equalsIgnoreCase("time_format")) {
			size = "该变量为使用";
		}
		if (size.equalsIgnoreCase("time_zone")) {
			size = "当前的时区";
		}
		if (size.equalsIgnoreCase("tmp_table_size")) {
			size = "如果内存内的临时表超过该值，MySQL自动将它转换为硬盘上的MyISAM表";
		}
		if (size.equalsIgnoreCase("tmpdir")) {
			size = "保存临时文件和临时表的目录";
		}
		if (size.equalsIgnoreCase("transaction_alloc_block_size")) {
			size = "为保存将保存到二进制日志中的事务的查询而分配的内存块的大小(字节)";
		}
		if (size.equalsIgnoreCase("transaction_prealloc_size")) {
			size = "transaction_alloc_blocks分配的固定缓冲区的大小（字节），在两次查询之间不会释放";
		}
		if (size.equalsIgnoreCase("tx_isolation")) {
			size = "默认事务隔离级别";
		}
		if (size.equalsIgnoreCase("updatable_views_with_limit")) {
			size = "该变量控制如果更新包含LIMIT子句，是否可以在当前表中使用不包含主关键字的视图进行更新";
		}
		if (size.equalsIgnoreCase("version")) {
			size = "服务器版本号";
		}
		if (size.equalsIgnoreCase("version_bdb")) {
			size = "BDB存储引擎版本";
		}
		if (size.equalsIgnoreCase("version_comment")) {
			size = "configure脚本有一个--with-comment选项，当构建MySQL时可以进行注释";
		}
		if (size.equalsIgnoreCase("version_compile_machine")) {
			size = "MySQL构建的机器或架构的类型";
		}
		if (size.equalsIgnoreCase("version_compile_os")) {
			size = "MySQL构建的操作系统的类型";
		}
		if (size.equalsIgnoreCase("wait_timeout")) {
			size = "服务器关闭非交互连接之前等待活动的秒数";
		}
		if (size == null || size.equals("")) {
			size = "";
		}
		return size;
	}

	private String generateColor() {
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return (r + g + b);
	}

}
