package com.afunms.application.ajaxManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.manage.HostApplyManager;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicHeap;
import com.afunms.application.weblogicmonitor.WeblogicJdbc;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicQueue;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicServlet;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.application.weblogicmonitor.WeblogicWeb;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.WeblogicLoader;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;

public class WeblogicPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void execute(String action) {
		if (action.equals("addWeblogicNode")) {
			addWeblogicNode();
		} else if (action.equals("getWeblogicNodeData")) {
			getWeblogicNodeData();
		} else if (action.equals("getWeblogicConfig")) {
			getWeblogicConfig();
		} else if (action.equals("getWeblogicJDBCList")) {
			getWeblogicJDBCList();
		} else if (action.equals("getWeblogicApplicationList")) {
			getWeblogicApplicationList();
		} else if (action.equals("getWeblogicJVMList")) {
			getWeblogicJVMList();
		} else if (action.equals("getWeblogicQueueList")) {
			getWeblogicQueueList();
		} else if (action.equals("getWeblogicServletList")) {
			getWeblogicServletList();
		} else if (action.equals("getWeblogicTransactionList")) {
			getWeblogicServletList();
		} else if (action.equals("deleteWeblogicNodes")) {
			deleteWeblogicNodes();
		} else if (action.equals("beforeEditWeblogicNode")) {
			beforeEditWeblogicNode();
		} else if (action.equals("updateWeblogicNode")) {
			updateWeblogicNode();
		} else if (action.equals("batchAddMonitor")) {
			batchAddMonitor();
		} else if (action.equals("batchCancleMonitor")) {
			batchCancleMonitor();
		}
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
				sql = "update nms_weblogicconfig set mon_flag=1 where id=" + ids[i];
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
				sql = "update nms_weblogicconfig set mon_flag=0 where id=" + ids[i];
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

	private void updateWeblogicNode() {
		WeblogicConfig vo = new WeblogicConfig();
		WeblogicConfigDao configdao = new WeblogicConfigDao();
		boolean flag = true;
		try {
			try {
				String id = getParaValue("nodeId");
				vo = (WeblogicConfig) configdao.findByID(id);
				vo.setId(getParaIntValue("nodeId"));
				vo.setAlias(getParaValue("alias"));
				vo.setIpAddress(getParaValue("ip"));
				vo.setCommunity(getParaValue("community"));
				vo.setPortnum(getParaIntValue("portnum"));
				vo.setMon_flag(getParaIntValue("mon_flag"));
				if (getParaValue("bid") == null || getParaValue("bid").equals("notSet") || getParaValue("bid").equals("")) {
					vo.setNetid(getParaValue("bids"));
				} else {
					vo.setNetid(getParaValue("bid"));
				}
				flag = configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			if (PollingEngine.getInstance().getWeblogicByID(vo.getId()) != null) {
				com.afunms.polling.node.Weblogic weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(vo.getId());
				weblogic.setAlias(vo.getAlias());
				weblogic.setPortnum(vo.getPortnum());
				weblogic.setIpAddress(vo.getIpAddress());
				weblogic.setCommunity(vo.getCommunity());
				weblogic.setSendemail(vo.getSendemail());
				weblogic.setSendmobiles(vo.getSendmobiles());
				weblogic.setSendphone(vo.getSendphone());
				weblogic.setBid(vo.getNetid());
				weblogic.setMon_flag(vo.getMon_flag());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flag) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void beforeEditWeblogicNode() {

		String nodeId = getParaValue("nodeId");
		WeblogicConfigDao dao = new WeblogicConfigDao();
		WeblogicConfig vo = null;
		try {
			vo = (WeblogicConfig) dao.findByID(nodeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		BusinessDao bidDao = new BusinessDao();
		List businessList = new ArrayList();
		Hashtable<String, String> businessHt = new Hashtable<String, String>();
		Business businessVo = null;
		try {
			businessList = bidDao.loadAll();
			if (null != businessList && businessList.size() > 0) {
				for (int i = 0; i < businessList.size(); i++) {
					businessVo = (Business) businessList.get(i);
					businessHt.put(businessVo.getId(), businessVo.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bidDao.close();
		}
		String bidValue = vo.getNetid();
		StringBuffer bidText = new StringBuffer();
		String[] bidValueArray = bidValue.split(",");
		for (int i = 0; i < bidValueArray.length; i++) {
			if (null != businessHt.get(bidValueArray[i])) {
				bidText.append(businessHt.get(bidValueArray[i]));
				bidText.append(",");
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"nodeId\":\"");
		jsonString.append(vo.getId());
		jsonString.append("\",");

		jsonString.append("\"ip\":\"");
		jsonString.append(vo.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"alias\":\"");
		jsonString.append(vo.getAlias());
		jsonString.append("\",");

		jsonString.append("\"isM\":\"");
		jsonString.append(vo.getMon_flag());
		jsonString.append("\",");

		jsonString.append("\"community\":\"");
		jsonString.append(vo.getCommunity());
		jsonString.append("\",");

		jsonString.append("\"port\":\"");
		jsonString.append(vo.getPortnum());
		jsonString.append("\",");

		jsonString.append("\"bidValue\":\"");
		jsonString.append(bidValue);
		jsonString.append("\",");

		jsonString.append("\"bid\":\"");
		jsonString.append(bidText);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteWeblogicNodes() {
		StringBuffer sb = new StringBuffer("删除");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		List list = new ArrayList();
		if (ids != null && ids.length > 0) {
			WeblogicConfigDao weblogicConfigDao = new WeblogicConfigDao();
			try {
				for (int i = 0; i < ids.length; i++) {
					PollingEngine.getInstance().deleteWeblogicByID(Integer.parseInt(ids[i]));
					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						gatherdao.deleteByNodeIdAndTypeAndSubtype(ids[i], "middleware", "weblogic");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(ids[i], "middleware", "weblogic");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
				}
				weblogicConfigDao.delete(ids);
				// 删除设备在临时表里中存储的数据
				String[] nmsTempDataTables = { "nms_weblogic_queue", "nms_weblogic_jdbc", "nms_weblogic_webapps", "nms_weblogic_heap", "nms_weblogic_server", "nms_weblogic_servlet", "nms_weblogic_normal" };
				CreateTableManager createTableManager = new CreateTableManager();
				createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
				WeblogicLoader loader = new WeblogicLoader();
				loader.loading();
				sb.append("成功");
			} catch (Exception e) {
				e.printStackTrace();
				sb.append("失败");
			} finally {
				weblogicConfigDao.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void getWeblogicServletList() {
		String nodeId = getParaValue("nodeId");

		WeblogicServlet vo = new WeblogicServlet();
		List<WeblogicServlet> weblogicServletList = new ArrayList<WeblogicServlet>();
		DBManager dBM = new DBManager();
		ResultSet rs = null;
		String serverSQL = "select * from nms_weblogic_servlet where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(serverSQL);
			while (rs.next()) {
				vo = new WeblogicServlet();
				vo.setServletRuntimeType(rs.getString("RunType"));
				vo.setServletRuntimeName(rs.getString("RunName"));
				vo.setServletRuntimeServletName(rs.getString("RunServletName"));
				vo.setServletRuntimeReloadTotalCount(rs.getString("RunReloadTotalCnt"));
				vo.setServletRuntimeInvocationTotalCount(rs.getString("RunInvoTotCnt"));
				vo.setServletRuntimePoolMaxCapacity(rs.getString("RunPoolMaxCapacity"));
				vo.setServletRuntimeExecutionTimeTotal(rs.getString("RunExecTimeTotal"));
				vo.setServletRuntimeExecutionTimeHigh(rs.getString("RunExecTimeHigh"));
				vo.setServletRuntimeExecutionTimeLow(rs.getString("RunExecTimeLow"));
				vo.setServletRuntimeExecutionTimeAverage(rs.getString("RunExecTimeAvg"));
				vo.setServletRuntimeURL(rs.getString("RunURL"));
				weblogicServletList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != dBM) {
				dBM.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != weblogicServletList && weblogicServletList.size() > 0) {
			for (int i = 0; i < weblogicServletList.size(); i++) {
				vo = weblogicServletList.get(i);
				jsonString.append("{\"RunType\":\"");
				jsonString.append(vo.getServletRuntimeType());
				jsonString.append("\",");

				jsonString.append("\"RunName\":\"");
				jsonString.append(vo.getServletRuntimeName());
				jsonString.append("\",");

				jsonString.append("\"RunServletName\":\"");
				jsonString.append(vo.getServletRuntimeServletName());
				jsonString.append("\",");

				jsonString.append("\"RunReloadTotalCnt\":\"");
				jsonString.append(vo.getServletRuntimeReloadTotalCount());
				jsonString.append("\",");

				jsonString.append("\"RunInvoTotCnt\":\"");
				jsonString.append(vo.getServletRuntimeInvocationTotalCount());
				jsonString.append("\",");

				jsonString.append("\"RunPoolMaxCapacity\":\"");
				jsonString.append(vo.getServletRuntimePoolMaxCapacity());
				jsonString.append("\",");

				jsonString.append("\"RunExecTimeTotal\":\"");
				jsonString.append(vo.getServletRuntimeExecutionTimeTotal());
				jsonString.append("\",");

				jsonString.append("\"RunExecTimeHigh\":\"");
				jsonString.append(vo.getServletRuntimeExecutionTimeHigh());
				jsonString.append("\",");

				jsonString.append("\"RunExecTimeLow\":\"");
				jsonString.append(vo.getServletRuntimeExecutionTimeLow());
				jsonString.append("\",");

				jsonString.append("\"RunExecTimeAvg\":\"");
				jsonString.append(vo.getServletRuntimeExecutionTimeAverage());
				jsonString.append("\",");

				jsonString.append("\"RunURL\":\"");
				jsonString.append(vo.getServletRuntimeURL());
				jsonString.append("\"}");

				if (i != weblogicServletList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + weblogicServletList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getWeblogicQueueList() {
		String nodeId = getParaValue("nodeId");

		WeblogicQueue vo = new WeblogicQueue();
		List<WeblogicQueue> weblogicQueueList = new ArrayList<WeblogicQueue>();
		DBManager dBM = new DBManager();
		ResultSet rs = null;
		String serverSQL = "select * from nms_weblogic_queue where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(serverSQL);
			while (rs.next()) {
				vo = new WeblogicQueue();
				vo.setExecuteQueueRuntimeName(rs.getString("executeQueueRuntimeName"));
				vo.setThreadPoolRuntimeExecuteThreadIdleCount(rs.getString("thdPoolRunExeThdIdleCnt"));
				vo.setExecuteQueueRuntimePendingRequestOldestTime(rs.getString("exeQueRunPendReqOldTime"));
				vo.setExecuteQueueRuntimePendingRequestCurrentCount(rs.getString("exeQueRunPendReqCurCount"));
				vo.setExecuteQueueRuntimePendingRequestTotalCount(rs.getString("exeQueRunPendReqTotCount"));
				weblogicQueueList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != dBM) {
				dBM.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != weblogicQueueList && weblogicQueueList.size() > 0) {
			for (int i = 0; i < weblogicQueueList.size(); i++) {
				vo = weblogicQueueList.get(i);
				jsonString.append("{\"executeQueueRuntimeName\":\"");
				jsonString.append(vo.getExecuteQueueRuntimeName());
				jsonString.append("\",");

				jsonString.append("\"thdPoolRunExeThdIdleCnt\":\"");
				jsonString.append(vo.getThreadPoolRuntimeExecuteThreadIdleCount());
				jsonString.append("\",");

				jsonString.append("\"exeQueRunPendReqOldTime\":\"");
				jsonString.append(vo.getExecuteQueueRuntimePendingRequestOldestTime());
				jsonString.append("\",");

				jsonString.append("\"exeQueRunPendReqCurCount\":\"");
				jsonString.append(vo.getExecuteQueueRuntimePendingRequestCurrentCount());
				jsonString.append("\",");

				jsonString.append("\"exeQueRunPendReqTotCount\":\"");
				jsonString.append(vo.getExecuteQueueRuntimePendingRequestTotalCount());
				jsonString.append("\"}");

				if (i != weblogicQueueList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + weblogicQueueList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getWeblogicJVMList() {
		String nodeId = getParaValue("nodeId");

		WeblogicHeap vo = new WeblogicHeap();
		List<WeblogicHeap> weblogicJVMList = new ArrayList<WeblogicHeap>();
		DBManager dBM = new DBManager();
		ResultSet rs = null;
		String serverSQL = "select * from nms_weblogic_heap where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(serverSQL);
			while (rs.next()) {
				vo = new WeblogicHeap();
				vo.setJvmRuntimeName(rs.getString("jvmRuntimeName"));
				vo.setJvmRuntimeHeapSizeCurrent(rs.getString("jvmRuntimeHeapSizeCurrent"));
				vo.setJvmRuntimeHeapFreeCurrent(rs.getString("jvmRuntimeHeapFreeCurrent"));
				weblogicJVMList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != dBM) {
				dBM.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != weblogicJVMList && weblogicJVMList.size() > 0) {
			for (int i = 0; i < weblogicJVMList.size(); i++) {
				vo = weblogicJVMList.get(i);
				jsonString.append("{\"jvmRuntimeName\":\"");
				jsonString.append(vo.getJvmRuntimeName());
				jsonString.append("\",");

				jsonString.append("\"jvmRuntimeHeapSizeCurrent\":\"");
				jsonString.append(toMb(vo.getJvmRuntimeHeapSizeCurrent()));
				jsonString.append("\",");

				jsonString.append("\"jvmRuntimeHeapFreeCurrent\":\"");
				jsonString.append(toMb(vo.getJvmRuntimeHeapFreeCurrent()));
				jsonString.append("\"}");

				if (i != weblogicJVMList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + weblogicJVMList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private String toMb(String arg) {
		String rt = "";
		if (null != arg && !arg.equals("")) {
			rt = df.format(Long.parseLong(arg) * 1.0 / 1024 / 1024);
		}
		return rt;
	}

	private void getWeblogicApplicationList() {
		String nodeId = getParaValue("nodeId");

		WeblogicWeb vo = new WeblogicWeb();
		List<WeblogicWeb> weblogicApplicationList = new ArrayList<WeblogicWeb>();
		DBManager dBM = new DBManager();
		ResultSet rs = null;
		String serverSQL = "select * from nms_weblogic_webapps where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(serverSQL);
			while (rs.next()) {
				vo = new WeblogicWeb();
				vo.setWebAppComponentRuntimeComponentName(rs.getString("CompRunComptName"));
				vo.setWebAppComponentRuntimeStatus(rs.getString("CompRunStatus"));
				vo.setWebAppComponentRuntimeOpenSessionsCurrentCount(rs.getString("CompRunOpenSessCurCount"));
				vo.setWebAppComponentRuntimeOpenSessionsHighCount(rs.getString("CompRunOpenSessHighCount"));
				vo.setWebAppComponentRuntimeSessionsOpenedTotalCount(rs.getString("CompRunSessOpenedTotCount"));
				weblogicApplicationList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != dBM) {
				dBM.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != weblogicApplicationList && weblogicApplicationList.size() > 0) {
			for (int i = 0; i < weblogicApplicationList.size(); i++) {
				vo = weblogicApplicationList.get(i);
				jsonString.append("{\"CompRunComptName\":\"");
				jsonString.append(vo.getWebAppComponentRuntimeComponentName());
				jsonString.append("\",");

				jsonString.append("\"CompRunStatus\":\"");
				jsonString.append(vo.getWebAppComponentRuntimeStatus());
				jsonString.append("\",");

				jsonString.append("\"CompRunOpenSessCurCount\":\"");
				jsonString.append(vo.getWebAppComponentRuntimeOpenSessionsCurrentCount());
				jsonString.append("\",");

				jsonString.append("\"CompRunOpenSessHighCount\":\"");
				jsonString.append(vo.getWebAppComponentRuntimeOpenSessionsHighCount());
				jsonString.append("\",");

				jsonString.append("\"CompRunSessOpenedTotCount\":\"");
				jsonString.append(vo.getWebAppComponentRuntimeSessionsOpenedTotalCount());
				jsonString.append("\"}");

				if (i != weblogicApplicationList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + weblogicApplicationList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getWeblogicJDBCList() {
		String nodeId = getParaValue("nodeId");

		WeblogicJdbc vo = new WeblogicJdbc();
		List<WeblogicJdbc> weblogicJDBCList = new ArrayList<WeblogicJdbc>();
		DBManager dBM = new DBManager();
		ResultSet rs = null;
		String serverSQL = "select * from nms_weblogic_jdbc where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(serverSQL);
			while (rs.next()) {
				vo = new WeblogicJdbc();
				vo.setJdbcConnectionPoolName(rs.getString("jdbcConnectionPoolName"));
				vo.setJdbcConnectionPoolRuntimeActiveConnectionsAverageCount(rs.getString("ConPoolRunActConsAvgCount"));
				vo.setJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount(rs.getString("ConPoolRunActConnsCurCount"));
				vo.setJdbcConnectionPoolRuntimeHighestNumAvailable(rs.getString("ConPoolRunHighestNumAvai"));
				vo.setJdbcConnectionPoolRuntimeMaxCapacity(rs.getString("ConPoolRunMaxCapacity"));
				vo.setJdbcConnectionPoolRuntimeVersionJDBCDriver(rs.getString("ConPoolRunVerJDBCDriver"));
				vo.setJdbcLeaked(rs.getString("Leaked"));
				vo.setJdbcWaitCurrent(rs.getString("WaitCurrent"));
				vo.setJdbcWaitMaxTime(rs.getString("WaitMaxTime"));
				weblogicJDBCList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != dBM) {
				dBM.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != weblogicJDBCList && weblogicJDBCList.size() > 0) {
			for (int i = 0; i < weblogicJDBCList.size(); i++) {
				vo = weblogicJDBCList.get(i);
				jsonString.append("{\"jdbcConnectionPoolName\":\"");
				jsonString.append(vo.getJdbcConnectionPoolName());
				jsonString.append("\",");

				jsonString.append("\"ConPoolRunActConnsCurCount\":\"");
				jsonString.append(vo.getJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount());
				jsonString.append("\",");

				jsonString.append("\"ConPoolRunVerJDBCDriver\":\"");
				jsonString.append(vo.getJdbcConnectionPoolRuntimeVersionJDBCDriver());
				jsonString.append("\",");

				jsonString.append("\"ConPoolRunMaxCapacity\":\"");
				jsonString.append(vo.getJdbcConnectionPoolRuntimeMaxCapacity());
				jsonString.append("\",");

				jsonString.append("\"ConPoolRunActConsAvgCount\":\"");
				jsonString.append(vo.getJdbcConnectionPoolRuntimeActiveConnectionsAverageCount());
				jsonString.append("\",");

				jsonString.append("\"ConPoolRunHighestNumAvai\":\"");
				jsonString.append(vo.getJdbcConnectionPoolRuntimeHighestNumAvailable());
				jsonString.append("\",");

				jsonString.append("\"Leaked\":\"");
				jsonString.append(vo.getJdbcLeaked());
				jsonString.append("\",");

				jsonString.append("\"WaitMaxTime\":\"");
				jsonString.append(vo.getJdbcWaitMaxTime());
				jsonString.append("\",");

				jsonString.append("\"WaitCurrent\":\"");
				jsonString.append(vo.getJdbcWaitCurrent());
				jsonString.append("\"}");

				if (i != weblogicJDBCList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + weblogicJDBCList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getWeblogicConfig() {
		String nodeId = getParaValue("nodeId");
		String ip = getParaValue("ip");
		String serverRuntimeName = null;
		String serverRuntimeListenAddress = null;
		String serverRuntimeListenPort = null;
		String RunOpenSocketsCurCount = null;
		String serverRuntimeState = null;

		String domainName = null;
		String domainAdministrationPort = null;
		String domainConfigurationVersion = null;

		DBManager dBM = new DBManager();
		ResultSet rs = null;
		String serverSQL = "select * from nms_weblogic_server where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(serverSQL);
			while (rs.next()) {
				serverRuntimeName = rs.getString("serverRuntimeName");
				serverRuntimeListenAddress = rs.getString("serverRuntimeListenAddress");
				serverRuntimeListenPort = rs.getString("serverRuntimeListenPort");
				RunOpenSocketsCurCount = rs.getString("RunOpenSocketsCurCount");
				serverRuntimeState = rs.getString("serverRuntimeState");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String domainSQL = "select * from nms_weblogic_normal where nodeid=" + nodeId;
		try {
			rs = dBM.executeQuery(domainSQL);
			while (rs.next()) {
				domainName = rs.getString("domainName");
				domainAdministrationPort = rs.getString("domainAdministrationPort");
				domainConfigurationVersion = rs.getString("domainConfigurationVersion");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != dBM) {
				dBM.close();
			}
		}
		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTimeQuery = date + " 00:00:00";
		String toTimeQuery = date + " 23:59:59";
		// 连通率字符串
		String avgPingString = "0";
		StringBuffer pingPercentSB = new StringBuffer();
		try {
			Hashtable pingHt = getCategory(ip, "WeblogicPing", "ConnectUtilization", startTimeQuery, toTimeQuery);
			if (null != pingHt) {
				if (null != pingHt.get("avgpingcon")) {
					avgPingString = (String) pingHt.get("avgpingcon");
					avgPingString = avgPingString.replace("%", "");
					pingPercentSB.append("连通;").append(Math.round(Double.valueOf(avgPingString))).append(";false;7CFC00\\n");
					pingPercentSB.append("未连通;").append(100 - Math.round(Double.valueOf(avgPingString))).append(";false;FF0000\\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"serverRuntimeName\":\"");
		jsonString.append(serverRuntimeName);
		jsonString.append("\",");

		jsonString.append("\"serverRuntimeListenAddress\":\"");
		jsonString.append(serverRuntimeListenAddress);
		jsonString.append("\",");

		jsonString.append("\"serverRuntimeListenPort\":\"");
		jsonString.append(serverRuntimeListenPort);
		jsonString.append("\",");

		jsonString.append("\"RunOpenSocketsCurCount\":\"");
		jsonString.append(RunOpenSocketsCurCount);
		jsonString.append("\",");

		jsonString.append("\"serverRuntimeState\":\"");
		jsonString.append(serverRuntimeState);
		jsonString.append("\",");

		jsonString.append("\"domainName\":\"");
		jsonString.append(domainName);
		jsonString.append("\",");

		jsonString.append("\"domainAdministrationPort\":\"");
		jsonString.append(domainAdministrationPort);
		jsonString.append("\",");

		jsonString.append("\"avgPingString\":\"");
		jsonString.append(pingPercentSB.toString());
		jsonString.append("\",");

		jsonString.append("\"domainConfigurationVersion\":\"");
		jsonString.append(domainConfigurationVersion);
		jsonString.append("\"}");
		jsonString.append("],total:1}");

		out.print(jsonString.toString());
		out.flush();
	}

	private void getWeblogicNodeData() {
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = user.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}
		WeblogicConfigDao configDao = new WeblogicConfigDao();
		List weblogicNodeList = null;
		try {
			if (user.getRole() == 0) {
				weblogicNodeList = configDao.loadAll();
			} else {
				weblogicNodeList = configDao.getWeblogicByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configDao.close();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != weblogicNodeList && weblogicNodeList.size() > 0) {
			WeblogicConfig vo = (WeblogicConfig) null;
			String tbIp = (String) null;
			String pingValue = (String) null;
			ResultSet rs = null;
			try {
				DBManager dBM = new DBManager();
				String sql = (String) null;
				for (int i = 0; i < weblogicNodeList.size(); i++) {
					vo = (WeblogicConfig) weblogicNodeList.get(i);
					try {
						tbIp = SysUtil.doip(vo.getIpAddress());
						sql = "select thevalue as value from weblogicping" + tbIp + " order by collecttime desc limit 1";
						rs = dBM.executeQuery(sql);
						while (rs.next()) {
							pingValue = rs.getString("value");
						}
					} catch (Exception e) {
						System.err.println("SQL执行出错");
						pingValue = "0";
					}
					jsonString.append("{\"nodeId\":\"");
					jsonString.append(vo.getId());
					jsonString.append("\",");

					jsonString.append("\"ip\":\"");
					jsonString.append(vo.getIpAddress());
					jsonString.append("\",");

					jsonString.append("\"alias\":\"");
					jsonString.append(vo.getAlias());
					jsonString.append("\",");

					jsonString.append("\"port\":\"");
					jsonString.append(vo.getPortnum());
					jsonString.append("\",");

					jsonString.append("\"status\":\"");
					jsonString.append(vo.getStatus());
					jsonString.append("\",");

					jsonString.append("\"pingValue\":\"");
					jsonString.append(pingValue);
					jsonString.append("\",");

					jsonString.append("\"community\":\"");
					jsonString.append(vo.getCommunity());
					jsonString.append("\",");

					jsonString.append("\"serverName\":\"");
					jsonString.append(vo.getServerName());
					jsonString.append("\",");

					jsonString.append("\"serverAddr\":\"");
					jsonString.append(vo.getServerAddr());
					jsonString.append("\",");

					jsonString.append("\"listenPort\":\"");
					jsonString.append(vo.getServerPort());
					jsonString.append("\",");

					jsonString.append("\"domainName\":\"");
					jsonString.append(vo.getDomainName());
					jsonString.append("\",");

					jsonString.append("\"domainPort\":\"");
					jsonString.append(vo.getDomainPort());
					jsonString.append("\",");

					jsonString.append("\"domainVersion\":\"");
					jsonString.append(vo.getDomainVersion());
					jsonString.append("\",");

					jsonString.append("\"isM\":\"");
					jsonString.append(vo.getMon_flag());
					jsonString.append("\"}");

					if (i != weblogicNodeList.size() - 1) {
						jsonString.append(",");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(null!=rs){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		jsonString.append("],total:" + weblogicNodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void addWeblogicNode() {
		StringBuffer sb = new StringBuffer("操作");
		WeblogicConfig vo = new WeblogicConfig();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip"));
		vo.setCommunity(getParaValue("community"));
		vo.setPortnum(getParaIntValue("port"));
		vo.setMon_flag(getParaIntValue("isM"));
		vo.setNetid(getParaValue("bid"));

		WeblogicConfigDao configdao = new WeblogicConfigDao();
		try {
			// 补充配置信息
			WeblogicSnmp weblogicsnmp = null;
			List domainList = new ArrayList();
			List serverList = new ArrayList();
			try {
				weblogicsnmp = new WeblogicSnmp(vo.getIpAddress(), vo.getCommunity(), vo.getPortnum());
				domainList = weblogicsnmp.collectNormalData();
				serverList = weblogicsnmp.collectServerData();

				if (null != domainList && domainList.size() > 0) {
					WeblogicNormal weblogicNormal = null;
					for (int i = 0; i < domainList.size(); i++) {
						weblogicNormal = (WeblogicNormal) domainList.get(i);
						vo.setDomainName(weblogicNormal.getDomainName());
						vo.setDomainPort(weblogicNormal.getDomainAdministrationPort());
						vo.setDomainVersion(weblogicNormal.getDomainConfigurationVersion());
						break;
					}
				}

				if (null != serverList && serverList.size() > 0) {
					WeblogicServer server = null;
					for (int i = 0; i < serverList.size(); i++) {
						server = (WeblogicServer) serverList.get(i);
						vo.setServerAddr(server.getServerRuntimeListenAddress());
						vo.setServerName(server.getServerRuntimeName());
						vo.setServerPort(server.getServerRuntimeListenPort());
					}
				}
				configdao.save(vo);
				// 初始化采集指标
				try {
					NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
					nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "middleware", "weblogic", "1");
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				// 初始化指标阀值
				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "middleware", "weblogic");
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				// 保存应用
				HostApplyManager.save(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}

			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("失败");
		} finally {
			configdao.close();
		}
		out.print(sb.toString());
		out.flush();
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (category.equals("WeblogicPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from weblogicping" + allipstr + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,collecttime,h.unit from weblogicping" + allipstr + " h where ");
					}
				}
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.subentity='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= ");
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("'");
					sb.append(starttime);
					sb.append("'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')");
				}

				sb.append(" and h.collecttime <= ");
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("'");
					sb.append(endtime);
					sb.append("'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
				}

				sb.append(" order by h.collecttime");
				sql = sb.toString();
				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				double pingcon = 0;
				double cpucon = 0;
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
					if (category.equals("WeblogicPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					}
					if (subentity.equalsIgnoreCase("ConnectUtilization")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
						if (tempfloat > getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else {
						if (tempfloat < getfloat(thevalue))
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
				if (category.equals("WeblogicPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
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
}
