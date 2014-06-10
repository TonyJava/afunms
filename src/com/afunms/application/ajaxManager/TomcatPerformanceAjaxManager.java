package com.afunms.application.ajaxManager;

import java.sql.ResultSet;
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
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.manage.HostApplyManager;
import com.afunms.application.model.Tomcat;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.TomcatLoader;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;

public class TomcatPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void execute(String action) {
		if (action.equals("addTomcatNode")) {
			addTomcatNode();
		} else if (action.equals("getTomcatNodeData")) {
			getTomcatNodeData();
		} else if (action.equals("deleteTomcatNodes")) {
			deleteTomcatNodes();
		} else if (action.equals("beforeEditTomcatNode")) {
			beforeEditTomcatNode();
		} else if (action.equals("editTomcatNode")) {
			editTomcatNode();
		} else if (action.equals("getTomcatConfig")) {
			getTomcatConfig();
		} else if (action.equals("getTomcatParameter")) {
			getTomcatParameter();
		} else if (action.equals("getTomcatApplicationList")) {
			getTomcatApplicationList();
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
				sql = "update app_tomcat_node set monflag=1 where id=" + ids[i];
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
				sql = "update app_tomcat_node set monflag=0 where id=" + ids[i];
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

	private void getTomcatApplicationList() {
		String ip = getParaValue("ip");
		String nodeId = getParaValue("nodeId");
		Hashtable tomcatShareDataHt = ShareData.getTomcatdata();
		Hashtable tomcatNodeHt = new Hashtable();
		if (null != tomcatShareDataHt) {
			if (null != tomcatShareDataHt.get(ip + ":" + nodeId)) {
				tomcatNodeHt = (Hashtable) tomcatShareDataHt.get(ip + ":" + nodeId);
				StringBuffer jsonString = new StringBuffer("{Rows:[");
				if (null != tomcatNodeHt && tomcatNodeHt.size() > 0) {
					Vector managerVevor = (Vector) tomcatNodeHt.get("manager");
					if (null != managerVevor && managerVevor.size() > 0) {
						String managerString = null;
						String[] arr = null;
						for (int i = 0; i < managerVevor.size(); i++) {
							managerString = (String) managerVevor.get(i);
							arr = managerString.split(":");
							if (null == arr || arr.length < 1) {
								continue;
							}
							jsonString.append("{\"applicationName\":\"");
							jsonString.append(arr[0]);
							jsonString.append("\",");

							jsonString.append("\"maxSession\":\"");
							jsonString.append(arr[1]);
							jsonString.append("\",");

							jsonString.append("\"activeSession\":\"");
							jsonString.append(arr[2]);
							jsonString.append("\",");

							jsonString.append("\"session\":\"");
							jsonString.append(arr[3]);
							jsonString.append("\"}");

							if (i != managerVevor.size() - 1) {
								jsonString.append(",");
							}
						}
					}
					jsonString.append("],total:" + managerVevor.size() + "}");
				}
				out.print(jsonString.toString());
				out.flush();
			}
		}
	}

	private void getTomcatParameter() {
		String ip = getParaValue("ip");
		String nodeId = getParaValue("nodeId");
		Hashtable tomcatShareDataHt = ShareData.getTomcatdata();
		Hashtable tomcatNodeHt = new Hashtable();
		if (null != tomcatShareDataHt) {
			if (null != tomcatShareDataHt.get(ip + ":" + nodeId)) {
				tomcatNodeHt = (Hashtable) tomcatShareDataHt.get(ip + ":" + nodeId);
				if (null != tomcatNodeHt && tomcatNodeHt.size() > 0) {
					// VM参数
					StringBuffer InputArgumentsSB = new StringBuffer();
					String InputArguments[] = (String[]) tomcatNodeHt.get("InputArguments");
					if (null != InputArguments && InputArguments.length > 0) {
						for (int i = 0; i < InputArguments.length; i++) {
							InputArgumentsSB.append(InputArguments[i]);
							InputArgumentsSB.append(";");
						}
					}
					// 类路径
					String classPath = "";
					classPath = (String) tomcatNodeHt.get("classPath");
					// 库路径
					String libraryPath = "";
					libraryPath = (String) tomcatNodeHt.get("libraryPath");

					StringBuffer jsonString = new StringBuffer("{Rows:[");
					jsonString.append("{\"InputArguments\":\"");
					jsonString.append(InputArgumentsSB.toString().replace("\\", "\\\\"));
					jsonString.append("\",");

					jsonString.append("\"classPath\":\"");
					jsonString.append(classPath.replace("\\", "\\\\"));
					jsonString.append("\",");

					jsonString.append("\"libraryPath\":\"");
					jsonString.append(libraryPath.replace("\\", "\\\\"));
					jsonString.append("\"}");
					jsonString.append("],total:1}");

					out.print(jsonString.toString());
					out.flush();
				}
			}
		}

	}

	private void getTomcatConfig() {
		String ip = getParaValue("ip");
		String nodeId = getParaValue("nodeId");
		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTimeQuery = date + " 00:00:00";
		String toTimeQuery = date + " 23:59:59";
		String avgPingString = "0";
		String avgJjvmString = "0";
		double avgJvm = 0.0f;
		// 连通率字符串
		StringBuffer pingPercentSB = new StringBuffer();
		try {
			Hashtable pingHt = getCategory(ip, "TomcatPing", "ConnectUtilization", startTimeQuery, toTimeQuery, "");
			Hashtable jvmHt = getCategory(ip, "tomcat_jvm", "jvm_utilization", startTimeQuery, toTimeQuery, "");
			if (null != jvmHt) {
				CreateMetersPic cmp = new CreateMetersPic();
				String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashboard1.png";
				if (null != jvmHt.get("avg_tomcat_jvm")) {
					avgJjvmString = (String) jvmHt.get("avg_tomcat_jvm");
					avgJvm = Double.parseDouble(avgJjvmString.replace("%", ""));
					cmp.createPic(ip, avgJvm, path, "JVM利用率", "tomcat_jvm");
				}

			}
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

		Hashtable tomcatShareDataHt = ShareData.getTomcatdata();
		Hashtable tomcatNodeHt = new Hashtable();
		if (null != tomcatShareDataHt) {
			if (null != tomcatShareDataHt.get(ip + ":" + nodeId)) {
				tomcatNodeHt = (Hashtable) tomcatShareDataHt.get(ip + ":" + nodeId);
				if (null != tomcatNodeHt && tomcatNodeHt.size() > 0) {
					// Tomcat版本
					String serverInfo = "";
					serverInfo = (String) tomcatNodeHt.get("serverInfo");
					// JDK版本
					String implementationVersion = "";
					implementationVersion = (String) tomcatNodeHt.get("implementationVersion");
					// JDK供应商
					String vmVendor = "";
					vmVendor = (String) tomcatNodeHt.get("vmVendor");
					// 虚拟机
					String vmNameVer = "";
					vmNameVer = (String) tomcatNodeHt.get("vmNameVer");
					// 启动时间
					String startTime = "";
					startTime = (String) tomcatNodeHt.get("startTime");
					// 工作时间
					String upTime = "";
					upTime = (String) tomcatNodeHt.get("upTime");
					// 操作系统
					String operatingSystemName = "";
					operatingSystemName = (String) tomcatNodeHt.get("operatingSystemName");
					// 物理内存总量
					String totalPhysicalMemorySize = "";
					long totalPhysicalMemorySizeL = -1;
					totalPhysicalMemorySize = (String) tomcatNodeHt.get("totalPhysicalMemorySize");
					if (null != totalPhysicalMemorySize && !"".equals(totalPhysicalMemorySize)) {
						totalPhysicalMemorySizeL = Long.parseLong(totalPhysicalMemorySize) / 1024 / 1024;
					}
					// 可用物理内存
					String freePhysicalMemorySize = "";
					long freePhysicalMemorySizeL = -1;
					freePhysicalMemorySize = (String) tomcatNodeHt.get("freePhysicalMemorySize");
					if (null != freePhysicalMemorySize && !"".equals(freePhysicalMemorySize)) {
						freePhysicalMemorySizeL = Long.parseLong(freePhysicalMemorySize) / 1024 / 1024;
					}
					// 交换空间总量
					String totalSwapSpaceSize = "";
					long totalSwapSpaceSizeL = -1;
					totalSwapSpaceSize = (String) tomcatNodeHt.get("totalSwapSpaceSize");
					if (null != totalSwapSpaceSize && !"".equals(totalSwapSpaceSize)) {
						totalSwapSpaceSizeL = Long.parseLong(totalSwapSpaceSize) / 1024 / 1024;
					}
					// 可用交换空间
					String freeSwapSpaceSize = "";
					long freeSwapSpaceSizeL = -1;
					freeSwapSpaceSize = (String) tomcatNodeHt.get("freeSwapSpaceSize");
					if (null != freeSwapSpaceSize && !"".equals(freeSwapSpaceSize)) {
						freeSwapSpaceSizeL = Long.parseLong(freeSwapSpaceSize) / 1024 / 1024;
					}
					// 分配虚拟内存
					String committedVirtualMemorySize = "";
					long committedVirtualMemorySizeL = -1;
					committedVirtualMemorySize = (String) tomcatNodeHt.get("committedVirtualMemorySize");
					if (null != committedVirtualMemorySize && !"".equals(committedVirtualMemorySize)) {
						committedVirtualMemorySizeL = Long.parseLong(committedVirtualMemorySize) / 1024 / 1024;
					}
					// 体系架构
					String arch = "";
					arch = (String) tomcatNodeHt.get("arch");
					// 可用CPU数
					String availableProcessors = "";
					availableProcessors = (String) tomcatNodeHt.get("availableProcessors");
					// 处理Cpu时间
					String processCpuTime = "";
					processCpuTime = (String) tomcatNodeHt.get("processCpuTime");
					// 活动线程
					String ThreadCount = "";
					ThreadCount = (String) tomcatNodeHt.get("ThreadCount");
					// 峰
					String PeakThreadCount = "";
					PeakThreadCount = (String) tomcatNodeHt.get("PeakThreadCount");
					// 守护线程
					String DaemonThreadCount = "";
					DaemonThreadCount = (String) tomcatNodeHt.get("DaemonThreadCount");
					// 已启动线程
					String TotalStartedThreadCount = "";
					TotalStartedThreadCount = (String) tomcatNodeHt.get("TotalStartedThreadCount");
					// 当前装入类
					String loadedClassCount = "";
					loadedClassCount = (String) tomcatNodeHt.get("loadedClassCount");
					// 已装入类总数
					String totalLoadedClassCount = "";
					totalLoadedClassCount = (String) tomcatNodeHt.get("totalLoadedClassCount");
					// 已卸载类
					String unloadedClassCount = "";
					unloadedClassCount = (String) tomcatNodeHt.get("unloadedClassCount");
					// 当前堆大小
					long heapUsedMemoryL = -1;
					if (null != tomcatNodeHt.get("heapUsedMemory")) {
						heapUsedMemoryL = (Long) tomcatNodeHt.get("heapUsedMemory") / 1024 / 1024;
					}
					// 分配的内存
					long heapCommitMemoryL = -1;
					if (null != tomcatNodeHt.get("heapCommitMemory")) {
						heapCommitMemoryL = (Long) tomcatNodeHt.get("heapCommitMemory") / 1024 / 1024;
					}
					// 堆最大值
					long heapMaxMemoryL = -1;
					if (null != tomcatNodeHt.get("heapMaxMemory")) {
						heapMaxMemoryL = (Long) tomcatNodeHt.get("heapMaxMemory") / 1024 / 1024;
					}

					String collectorString = "";
					Vector collector = new Vector();
					collector = (Vector) tomcatNodeHt.get("collector");
					if (null != collector && collector.size() > 0) {
						for (int i = 0; i < collector.size(); i++) {
							collectorString = collectorString + collector.get(i) + " ; ";
						}
					}
					StringBuffer jsonString = new StringBuffer("{Rows:[");
					jsonString.append("{\"serverInfo\":\"");
					jsonString.append(serverInfo);
					jsonString.append("\",");

					jsonString.append("\"implementationVersion\":\"");
					jsonString.append(implementationVersion);
					jsonString.append("\",");

					jsonString.append("\"vmVendor\":\"");
					jsonString.append(vmVendor);
					jsonString.append("\",");

					jsonString.append("\"vmNameVer\":\"");
					jsonString.append(vmNameVer);
					jsonString.append("\",");

					jsonString.append("\"startTime\":\"");
					jsonString.append(startTime);
					jsonString.append("\",");

					jsonString.append("\"upTime\":\"");
					jsonString.append(upTime);
					jsonString.append("\",");

					jsonString.append("\"operatingSystemName\":\"");
					jsonString.append(operatingSystemName);
					jsonString.append("\",");

					jsonString.append("\"totalPhysicalMemorySizeL\":\"");
					jsonString.append(totalPhysicalMemorySizeL);
					jsonString.append("\",");

					jsonString.append("\"freePhysicalMemorySizeL\":\"");
					jsonString.append(freePhysicalMemorySizeL);
					jsonString.append("\",");

					jsonString.append("\"totalSwapSpaceSizeL\":\"");
					jsonString.append(totalSwapSpaceSizeL);
					jsonString.append("\",");

					jsonString.append("\"freeSwapSpaceSizeL\":\"");
					jsonString.append(freeSwapSpaceSizeL);
					jsonString.append("\",");

					jsonString.append("\"committedVirtualMemorySizeL\":\"");
					jsonString.append(committedVirtualMemorySizeL);
					jsonString.append("\",");

					jsonString.append("\"arch\":\"");
					jsonString.append(arch);
					jsonString.append("\",");

					jsonString.append("\"processCpuTime\":\"");
					jsonString.append(processCpuTime);
					jsonString.append("\",");

					jsonString.append("\"availableProcessors\":\"");
					jsonString.append(availableProcessors);
					jsonString.append("\",");

					jsonString.append("\"ThreadCount\":\"");
					jsonString.append(ThreadCount);
					jsonString.append("\",");

					jsonString.append("\"PeakThreadCount\":\"");
					jsonString.append(PeakThreadCount);
					jsonString.append("\",");

					jsonString.append("\"DaemonThreadCount\":\"");
					jsonString.append(DaemonThreadCount);
					jsonString.append("\",");

					jsonString.append("\"TotalStartedThreadCount\":\"");
					jsonString.append(TotalStartedThreadCount);
					jsonString.append("\",");

					jsonString.append("\"loadedClassCount\":\"");
					jsonString.append(loadedClassCount);
					jsonString.append("\",");

					jsonString.append("\"totalLoadedClassCount\":\"");
					jsonString.append(totalLoadedClassCount);
					jsonString.append("\",");

					jsonString.append("\"unloadedClassCount\":\"");
					jsonString.append(unloadedClassCount);
					jsonString.append("\",");

					jsonString.append("\"heapUsedMemoryL\":\"");
					jsonString.append(heapUsedMemoryL);
					jsonString.append("\",");

					jsonString.append("\"heapCommitMemoryL\":\"");
					jsonString.append(heapCommitMemoryL);
					jsonString.append("\",");

					jsonString.append("\"heapMaxMemoryL\":\"");
					jsonString.append(heapMaxMemoryL);
					jsonString.append("\",");

					jsonString.append("\"avgPingString\":\"");
					jsonString.append(pingPercentSB.toString());
					jsonString.append("\",");

					jsonString.append("\"collectorString\":\"");
					jsonString.append(collectorString);
					jsonString.append("\"}");
					jsonString.append("],total:1}");

					out.print(jsonString.toString());
					out.flush();
				}
			}
		}
	}

	private void editTomcatNode() {
		Tomcat vo = new Tomcat();
		TomcatDao dao = new TomcatDao();
		StringBuffer jsonString = new StringBuffer("修改");
		try {
			String id = getParaValue("nodeId");
			vo = (Tomcat) dao.findByID(id);
			vo.setAlias(getParaValue("alias"));
			vo.setMonflag(getParaIntValue("isM"));
			vo.setUser(getParaValue("user"));
			vo.setPassword(getParaValue("password"));
			vo.setPort(getParaValue("port"));
			if (getParaValue("bid") == null || getParaValue("bid").equals("notSet") || getParaValue("bid").equals("")) {
				vo.setBid(getParaValue("bids"));
			} else {
				vo.setBid(getParaValue("bid"));
			}
			dao = new TomcatDao();
			dao.update(vo);
			jsonString.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			jsonString.append("失败");
		} finally {
			dao.close();
		}
		out.print(jsonString.toString());
		out.flush();

	}

	private void beforeEditTomcatNode() {
		String nodeId = getParaValue("nodeId");
		TomcatDao tomcatDao = new TomcatDao();
		Tomcat tomcatVo = null;
		try {
			tomcatVo = (Tomcat) tomcatDao.findByID(nodeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatDao.close();
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
		String bidValue = tomcatVo.getBid();
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
		jsonString.append(tomcatVo.getId());
		jsonString.append("\",");

		jsonString.append("\"ip\":\"");
		jsonString.append(tomcatVo.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"alias\":\"");
		jsonString.append(tomcatVo.getAlias());
		jsonString.append("\",");

		jsonString.append("\"isM\":\"");
		jsonString.append(tomcatVo.getMonflag());
		jsonString.append("\",");

		jsonString.append("\"user\":\"");
		jsonString.append(tomcatVo.getUser());
		jsonString.append("\",");

		jsonString.append("\"password\":\"");
		jsonString.append(tomcatVo.getPassword());
		jsonString.append("\",");

		jsonString.append("\"port\":\"");
		jsonString.append(tomcatVo.getPort());
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

	private void deleteTomcatNodes() {
		StringBuffer sb = new StringBuffer("删除");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		if (ids != null && ids.length > 0) {
			TomcatDao dao = null;
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				dao = new TomcatDao();
				try {
					Node node = PollingEngine.getInstance().getTomcatByID(Integer.parseInt(id));
					HostApplyDao hostApplyDao = null;
					try {
						hostApplyDao = new HostApplyDao();
						hostApplyDao.delete(" where ipaddress = '" + node.getIpAddress() + "' and subtype = 'tomcat' and nodeid = '" + id + "'");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (hostApplyDao != null) {
							hostApplyDao.close();
						}
					}
					PollingEngine.getInstance().deleteTomcatByID(Integer.parseInt(id));
					dao.delete(id);

					NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
					try {
						gatherdao.deleteByNodeIdAndTypeAndSubtype(id, "middleware", "tomcat");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						gatherdao.close();
					}
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(id, "middleware", "tomcat");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
					String[] nmsTempDataTables = { "nms_tomcat_temp" };
					String[] idArr = new String[] { id };
					CreateTableManager createTableManager = new CreateTableManager();
					createTableManager.clearNmsTempDatas(nmsTempDataTables, idArr);
					sb.append("成功");
				} catch (Exception e) {
					e.printStackTrace();
					sb.append("失败");
				} finally {
					dao.close();
				}
			}
			try {
				dao = new TomcatDao();
				List tomcatList = new ArrayList();
				tomcatList = dao.loadAll();
				ShareData.setTomcatlist(tomcatList);
				TomcatLoader loader = new TomcatLoader();
				loader.clearRubbish(tomcatList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getTomcatNodeData() {
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
		List tomcatNodeList = new ArrayList();
		TomcatDao dao = new TomcatDao();
		try {
			if (user.getRole() == 0) {
				tomcatNodeList = dao.loadAll();
			} else {
				tomcatNodeList = dao.getTomcatByBID(rbids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		Hashtable tomcatShareDataHt = ShareData.getTomcatdata();
		Hashtable tomcatNodeData = new Hashtable();
		Tomcat vo = new Tomcat();
		String pingValue = "0";
		String jvmValue = "0";
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < tomcatNodeList.size(); i++) {
			vo = (Tomcat) tomcatNodeList.get(i);
			Node tomcatNode = PollingEngine.getInstance().getTomcatByID(vo.getId());
			if (tomcatNode == null) {
				vo.setStatus(0);
			} else {
				vo.setStatus(tomcatNode.getStatus());
			}
			if (null != tomcatShareDataHt) {
				if (null != tomcatShareDataHt.get(vo.getIpAddress() + ":" + vo.getId())) {
					tomcatNodeData = (Hashtable) tomcatShareDataHt.get(vo.getIpAddress() + ":" + vo.getId());
				}
				if (null != tomcatNodeData) {
					if (null != tomcatNodeData.get("linkFlag")) {
						int flag = (Integer) tomcatNodeData.get("linkFlag");
						if (flag == 1) {
							pingValue = "100";
						}
					}
					if (null != tomcatNodeData.get("heapPercent")) {
						jvmValue = (String) tomcatNodeData.get("heapPercent");
					}
				}
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
			jsonString.append(vo.getPort());
			jsonString.append("\",");

			jsonString.append("\"status\":\"");
			jsonString.append(vo.getStatus());
			jsonString.append("\",");

			jsonString.append("\"pingValue\":\"");
			jsonString.append(pingValue);
			jsonString.append("\",");

			jsonString.append("\"jvm\":\"");
			jsonString.append(jvmValue);
			jsonString.append("\",");

			jsonString.append("\"version\":\"");
			jsonString.append(vo.getVersion());
			jsonString.append("\",");

			jsonString.append("\"jdkVersion\":\"");
			jsonString.append(vo.getJvmversion());
			jsonString.append("\",");

			jsonString.append("\"os\":\"");
			jsonString.append(vo.getOs());
			jsonString.append("\",");

			jsonString.append("\"isM\":\"");
			jsonString.append(vo.getMonflag());
			jsonString.append("\"}");

			if (i != tomcatNodeList.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total:" + tomcatNodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void addTomcatNode() {
		StringBuffer sb = new StringBuffer("操作");
		Tomcat vo = new Tomcat();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip"));
		vo.setPort(getParaValue("port"));
		vo.setMonflag(getParaIntValue("isM"));
		vo.setBid(getParaValue("bid"));
		// 刷新内存监视
		TomcatLoader loader = new TomcatLoader();
		try {
			loader.loadOne(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TomcatDao dao = new TomcatDao();
		try {
			dao.save(vo);
			// 初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", "middleware", "tomcat", "1");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			// 初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "middleware", "tomcat");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			// 保存应用
			HostApplyManager.save(vo);
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("失败");
		} finally {
			dao.close();
		}
		out.print(sb.toString());
		out.flush();
	}

	private Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime, String time) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("TomcatPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping" + time + allipstr + " h where ");
					}
					if (category.equals("tomcat_jvm")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm" + allipstr + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("TomcatPing")) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from tomcatping" + time + allipstr + " h where ");
					}
					if (category.equals("tomcat_jvm")) {
						sb.append(" select h.thevalue,h.collecttime,h.unit from tomcat_jvm" + allipstr + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= ");
					sb.append("to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" and h.collecttime <= ");
					sb.append("to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" order by h.collecttime");
				}

				sql = sb.toString();
				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				double tempfloat = 0;
				double pingcon = 0;
				double tomcat_jvm_con = 0;
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
					if (category.equals("TomcatPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
					} else if (category.equalsIgnoreCase("tomcat_jvm")) {
						tomcat_jvm_con = tomcat_jvm_con + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
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
				if (category.equals("TomcatPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
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
				if (category.equals("tomcat_jvm")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avg_tomcat_jvm", CEIString.round(tomcat_jvm_con / list1.size(), 2) + unit);
					} else {
						hash.put("avg_tomcat_jvm", "0.0%");
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

}
