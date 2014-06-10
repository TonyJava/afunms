package com.afunms.application.ajaxManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CreateAmColumnPic;
import com.afunms.common.util.CreateBarPic;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.DateE;
import com.afunms.common.util.MeterModel;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.StageColor;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.TitleModel;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DeviceCollectEntity;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessInfo;
import com.afunms.polling.om.ServiceCollectEntity;
import com.afunms.polling.om.SoftwareCollectEntity;
import com.afunms.polling.om.StorageCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.MonitorNetDTO;
import com.afunms.topology.model.MonitorNodeDTO;

@SuppressWarnings("rawtypes")
public class HostPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// 获取指定用户业务SQL
	private String getBidSql() {
		User currentUser = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer bidSQL = new StringBuffer();
		// 拼接标志
		int flag = 0;
		if (currentUser.getBusinessids() != null) {
			if (currentUser.getBusinessids() != "-1") {
				String[] bids = currentUser.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								bidSQL.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								bidSQL.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					bidSQL.append(") ");
				}
			}
		}
		if (currentUser.getRole() == 0) {
			// 超级管理员
			return "";
		} else {
			return bidSQL.toString();
		}
	}

	// 根据类型获取网元
	private List getNodeListByCategory(String type) {
		List nodeList = new ArrayList();
		String where = "";
		if ("windows".equals(type)) {
			where = " where category=4 and ostype=5";
		} else if ("linux".equals(type)) {
			where = " where category=4 and ostype=9";
		} else if ("aix".equals(type)) {
			where = " where category=4 and ostype=6";
		} else if ("route".equals(type)) {
			where = " where  category=1";
		} else if ("switch".equals(type)) {
			where = " where category=2 or category=3 or category=7";
		} else if ("firewall".equals(type)) {
			where = " where category=8";
		}
		where = where + getBidSql();
		HostNodeDao dao = new HostNodeDao();
		try {
			nodeList = dao.findByCondition(where);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return nodeList;
	}

	public MonitorNodeDTO convertToNodeDTOByHostNode(HostNode hostNode) {
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		MonitorNodeDTO monitorNodeDTO = new MonitorNetDTO();
		String ipAddress = hostNode.getIpAddress();
		int nodeId = hostNode.getId();
		String alias = hostNode.getAlias();
		int category = hostNode.getCategory();
		String type = hostNode.getType();

		if (category == 1) {
			monitorNodeDTO.setCategory("路由器");
		} else if (category == 2 || category == 3) {
			monitorNodeDTO.setCategory("交换机");
		} else if (category == 4) {
			monitorNodeDTO.setCategory("服务器");
		} else if (category == 7) {
			monitorNodeDTO.setCategory("无线路由器");
		} else if (category == 8) {
			monitorNodeDTO.setCategory("防火墙");
		} else if (category == 9) {
			monitorNodeDTO.setCategory("ATM");
		} else if (category == 10) {
			monitorNodeDTO.setCategory("邮件安全网关");
		} else if (category == 11) {
			monitorNodeDTO.setCategory("F5");
		} else if (category == 12) {
			monitorNodeDTO.setCategory("VPN");
		} else if (category == 13) {
			monitorNodeDTO.setCategory("CMTS");
		} else if (category == 14) {
			monitorNodeDTO.setCategory("存储");
		} else if (category == 15) {
			monitorNodeDTO.setCategory("虚拟化");
		}

		NodeDTO nodeDTO = null;

		Host node = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		if (node == null) {
			// 从数据库里获取
			HostNodeDao hostdao = new HostNodeDao();
			HostNode nodeFromDb = null;
			try {
				nodeFromDb = (HostNode) hostdao.findByID(nodeId + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				hostdao.close();
			}
			HostLoader loader = new HostLoader();
			loader.loadOne(nodeFromDb);
			node = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		} else {
			NodeUtil nodeUtil = new NodeUtil();
			nodeDTO = nodeUtil.creatNodeDTOByHost(node);
		}
		if (null != nodeDTO) {
			int alarmLevel = 0;
			String chexkname = nodeDTO.getId() + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
				try {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 告警状态
			if (node != null) {
				monitorNodeDTO.setStatus(alarmLevel + "");
			} else {
				monitorNodeDTO.setStatus("0");
			}

			String cpuValue = "0";
			String physicalMemoryValue = "0";
			String virtualMemoryValue = "0";
			String inUtilHdxValue = "0";
			String outUtilHdxValue = "0";
			String pingValue = "0";
			String collectType = "";

			String cpuValueColor = "green";
			String physicalMemoryValueColor = "green";
			String virtualMemoryValueColor = "green";
			double cpuValueDouble = 0;
			double physicalMemeryValueDouble = 0;
			double virtualMemeryValueDouble = 0;

			int interfaceNubmer = 0;
			String runModel = PollingEngine.getCollectwebflag();
			if ("0".equals(runModel)) {
				// 采集与访问是集成模式
				Hashtable shareData = ShareData.getSharedata();
				Hashtable ipAllData = (Hashtable) shareData.get(ipAddress);
				Hashtable allPingData = ShareData.getPingdata();
				if (ipAllData != null) {
					Vector cpuV = (Vector) ipAllData.get("cpu");
					if (cpuV != null && cpuV.size() > 0) {
						CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
						if (cpu != null && cpu.getThevalue() != null) {
							cpuValueDouble = Double.valueOf(cpu.getThevalue());
							cpuValue = df.format(cpuValueDouble);
						}
					}
					Vector memoryVector = (Vector) ipAllData.get("memory");
					int allmemoryvalue = 0;
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int si = 0; si < memoryVector.size(); si++) {
							MemoryCollectEntity memoryData = (MemoryCollectEntity) memoryVector.elementAt(si);
							if (memoryData.getEntity().equalsIgnoreCase("Utilization")) {
								if (category == 4) {
									if (hostNode.getOstype() == 5) {
										// windows
										if (memoryData.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
											physicalMemeryValueDouble = Double.valueOf(memoryData.getThevalue());
										} else if (memoryData.getSubentity().equalsIgnoreCase("VirtualMemory")) {
											virtualMemeryValueDouble = Double.valueOf(memoryData.getThevalue());
										}
									} else {
										// linux、aix
										if (memoryData.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
											physicalMemeryValueDouble = Double.valueOf(memoryData.getThevalue());
										} else if (memoryData.getSubentity().equalsIgnoreCase("SwapMemory")) {
											virtualMemeryValueDouble = Double.valueOf(memoryData.getThevalue());
										}
									}
								} else if (category == 1 || category == 2 || category == 3 || category == 8 || category == 11) {
									allmemoryvalue = allmemoryvalue + Integer.parseInt(memoryData.getThevalue());
									if (si == memoryVector.size() - 1) {
										physicalMemeryValueDouble = allmemoryvalue / memoryVector.size();
									}
								}
							}
						}
						physicalMemoryValue = df.format(physicalMemeryValueDouble);
						virtualMemoryValue = df.format(virtualMemeryValueDouble);
					}

					Vector allUtilHdx = (Vector) ipAllData.get("allutilhdx");
					if (allUtilHdx != null && allUtilHdx.size() == 3) {
						AllUtilHdx inUtilHdx = (AllUtilHdx) allUtilHdx.get(0);
						inUtilHdxValue = inUtilHdx.getThevalue();

						AllUtilHdx outUtilHdx = (AllUtilHdx) allUtilHdx.get(1);
						outUtilHdxValue = outUtilHdx.getThevalue();
					}
				}
				if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype() || SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
					if (ipAllData != null && ipAllData.containsKey("interfaceNumber")) {
						interfaceNubmer = (Integer) ipAllData.get("interfaceNumber");
					}

				}
				if (allPingData != null) {
					Vector pingData = (Vector) allPingData.get(ipAddress);
					if (pingData != null && pingData.size() > 0) {
						PingCollectEntity pingcollectdata = (PingCollectEntity) pingData.get(0);
						pingValue = pingcollectdata.getThevalue();
					}
				}
			}

			// 设置采集方式
			if (SystemConstant.COLLECTTYPE_SNMP == hostNode.getCollecttype()) {
				collectType = "snmp";
			} else if (SystemConstant.COLLECTTYPE_PING == hostNode.getCollecttype()) {
				collectType = "ping";
			} else if (SystemConstant.COLLECTTYPE_REMOTEPING == hostNode.getCollecttype()) {
				collectType = "REMOTEPING";
			} else if (SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
				collectType = "代理";
			} else if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype()) {
				collectType = "ssh";
			} else if (SystemConstant.COLLECTTYPE_TELNET == hostNode.getCollecttype()) {
				collectType = "telnet";
			} else if (SystemConstant.COLLECTTYPE_WMI == hostNode.getCollecttype()) {
				collectType = "wmi";
			} else if (SystemConstant.COLLECTTYPE_DATAINTERFACE == hostNode.getCollecttype()) {
				collectType = "接口";
			}

			// 设定cpu和内存的告警颜色
			if (category == 4) {
				type = "host";
			} else {
				type = "net";
			}
			AlarmIndicatorsNodeDao nmDao = new AlarmIndicatorsNodeDao();
			List nodeMonitorList = null;
			try {
				nodeMonitorList = nmDao.findByNodeIdAndTypeAndSubType(nodeId + "", type, "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nmDao.close();
			}
			if (nodeMonitorList != null) {
				for (int j = 0; j < nodeMonitorList.size(); j++) {
					AlarmIndicatorsNode nodeMonitor = (AlarmIndicatorsNode) nodeMonitorList.get(j);
					if ("cpu".equals(nodeMonitor.getName())) {
						if (Double.parseDouble(cpuValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
							cpuValueColor = "red";
						} else if (Double.parseDouble(cpuValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
							cpuValueColor = "orange";
						} else if (Double.parseDouble(cpuValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
							cpuValueColor = "yellow";
						}
					}
					if ("memory".equals(nodeMonitor.getName())) {
						if (Double.parseDouble(physicalMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
							physicalMemoryValueColor = "red";
						} else if (Double.parseDouble(physicalMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
							physicalMemoryValueColor = "orange";
						} else if (Double.parseDouble(physicalMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
							physicalMemoryValueColor = "yellow";
						}
					}
					if ("physicalmemory".equals(nodeMonitor.getName())) {
						if (Double.parseDouble(physicalMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
							physicalMemoryValueColor = "red";
						} else if (Double.parseDouble(physicalMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
							physicalMemoryValueColor = "orange";
						} else if (Double.parseDouble(physicalMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
							physicalMemoryValueColor = "yellow";
						}
					}
					if ("SwapMemory".equals(nodeMonitor.getName())) {
						if (Double.parseDouble(virtualMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
							virtualMemoryValueColor = "red";
						} else if (Double.parseDouble(virtualMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
							virtualMemoryValueColor = "orange";
						} else if (Double.parseDouble(virtualMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
							virtualMemoryValueColor = "yellow";
						}
					}
				}
			}
			if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype() || SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
				monitorNodeDTO.setEntityNumber(interfaceNubmer);
			}
			if (pingValue != null && Integer.parseInt(pingValue) > 0) {
				pingValue = "100";
			} else {
				inUtilHdxValue = "0";
				outUtilHdxValue = "0";
			}

			monitorNodeDTO.setId(nodeId);
			monitorNodeDTO.setIpAddress(ipAddress);
			monitorNodeDTO.setAlias(alias);
			monitorNodeDTO.setType(type);
			monitorNodeDTO.setSubtype(category + "");
			monitorNodeDTO.setCpuValue(cpuValue);
			monitorNodeDTO.setMemoryValue(physicalMemoryValue);
			monitorNodeDTO.setVirtualMemoryValue(virtualMemoryValue);
			monitorNodeDTO.setInutilhdxValue(inUtilHdxValue);
			monitorNodeDTO.setOututilhdxValue(outUtilHdxValue);
			monitorNodeDTO.setPingValue(pingValue);
			monitorNodeDTO.setCollectType(collectType);
			monitorNodeDTO.setCpuValueColor(cpuValueColor);
			monitorNodeDTO.setVirtualMemoryValueColor(virtualMemoryValueColor);
			monitorNodeDTO.setMemoryValueColor(physicalMemoryValueColor);
			return monitorNodeDTO;
		} else {
			return null;
		}
	}

	private void getHostNodeDataByType() {
		String type = getParaValue("type");

		List nodeList = new ArrayList();
		nodeList = getNodeListByCategory(type);

		// 取出接口数量
		HostInterfaceDao hostInterfaceDao = new HostInterfaceDao();
		Hashtable hostInterfaceHt = null;
		try {
			hostInterfaceHt = hostInterfaceDao.getHostInterfaceList(nodeList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostInterfaceDao.close();
		}

		HostNode hostNode = null;
		MonitorNodeDTO monitorNodeDTO = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != nodeList && nodeList.size() > 0) {
			for (int i = 0; i < nodeList.size(); i++) {
				hostNode = (HostNode) nodeList.get(i);
				monitorNodeDTO = convertToNodeDTOByHostNode(hostNode);
				if (null == monitorNodeDTO)
					continue;

				if (null != hostInterfaceHt.get(monitorNodeDTO.getId() + "")) {
					monitorNodeDTO.setEntityNumber(Integer.parseInt(String.valueOf(hostInterfaceHt.get(monitorNodeDTO.getId() + ""))));
				}
				jsonString.append("{\"nodeId\":\"");
				jsonString.append(monitorNodeDTO.getId());
				jsonString.append("\",");

				jsonString.append("\"type\":\"");
				jsonString.append(monitorNodeDTO.getType());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(monitorNodeDTO.getAlias());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(monitorNodeDTO.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(monitorNodeDTO.getStatus());
				jsonString.append("\",");

				jsonString.append("\"pingValue\":\"");
				jsonString.append(monitorNodeDTO.getPingValue());
				jsonString.append("\",");

				jsonString.append("\"cpuValue\":\"");
				jsonString.append(monitorNodeDTO.getCpuValue());
				jsonString.append("\",");

				jsonString.append("\"cpuColor\":\"");
				jsonString.append(monitorNodeDTO.getCpuValueColor());
				jsonString.append("\",");

				jsonString.append("\"physicalMemoryValue\":\"");
				jsonString.append(monitorNodeDTO.getMemoryValue());
				jsonString.append("\",");

				jsonString.append("\"physicalMemoryColor\":\"");
				jsonString.append(monitorNodeDTO.getMemoryValueColor());
				jsonString.append("\",");

				jsonString.append("\"virtualMemoryValue\":\"");
				jsonString.append(monitorNodeDTO.getVirtualMemoryValue());
				jsonString.append("\",");

				jsonString.append("\"virtualMemoryColor\":\"");
				jsonString.append(monitorNodeDTO.getVirtualMemoryValueColor());
				jsonString.append("\",");

				jsonString.append("\"inUtilHdx\":\"");
				jsonString.append(monitorNodeDTO.getInutilhdxValue());
				jsonString.append("\",");

				jsonString.append("\"outUtilHdx\":\"");
				jsonString.append(monitorNodeDTO.getOututilhdxValue());
				jsonString.append("\",");

				jsonString.append("\"cType\":\"");
				jsonString.append(monitorNodeDTO.getCollectType());
				jsonString.append("\",");

				jsonString.append("\"ifNumber\":\"");
				jsonString.append(monitorNodeDTO.getEntityNumber());
				jsonString.append("\",");

				jsonString.append("\"isM\":\"");
				jsonString.append(hostNode.isManaged());
				jsonString.append("\"}");

				if (i != nodeList.size() - 1) {
					jsonString.append(",");
				}

			}
		}
		jsonString.append("],total:" + nodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostInterfaceDetail() {
		String[] time = { "", "" };
		getTime(request, time);
		String startTime = time[0];
		String endTime = time[1];

		String ip = getParaValue("ip");
		Vector ifVector = new Vector();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdxPerc", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			ifVector = hostlastmanager.getInterface_share(ip, netInterfaceItem, "index", startTime, endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != ifVector && ifVector.size() > 0) {
			for (int i = 0; i < ifVector.size(); i++) {
				String[] strs = (String[]) ifVector.get(i);
				jsonString.append("{\"ifIndex\":\"");
				jsonString.append(strs[0]);
				jsonString.append("\",");

				jsonString.append("\"ifName\":\"");
				jsonString.append(strs[1]);
				jsonString.append("\",");

				jsonString.append("\"ifSpeed\":\"");
				jsonString.append(strs[2]);
				jsonString.append("\",");

				jsonString.append("\"ifOperStatus\":\"");
				String temp = strs[3];
				if (strs[3].equals("1")) {
					temp = "up";
				}
				jsonString.append(temp);
				jsonString.append("\",");

				jsonString.append("\"outBandwidthUtilHdxPerc\":\"");
				jsonString.append(strs[4]);
				jsonString.append("\",");

				jsonString.append("\"inBandwidthUtilHdxPerc\":\"");
				jsonString.append(strs[5]);
				jsonString.append("\",");

				jsonString.append("\"outBandwidthUtilHdx\":\"");
				jsonString.append(strs[6]);
				jsonString.append("\",");

				jsonString.append("\"inBandwidthUtilHdx\":\"");
				jsonString.append(strs[7]);
				jsonString.append("\"}");

				if (i != ifVector.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + ifVector.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostEventDetail() {
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		tmp = request.getParameter("ip");
		Host host = (Host) PollingEngine.getInstance().getNodeByIp(tmp);
		try {
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}

			String[] time = { "", "" };
			getTime(request, time);
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";

			String starttime1 = "1970-01-01 00:00:00";
			String totime1 = "2050-12-31 23:59:59";

			EventListDao dao = new EventListDao();
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
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
				String eventlocation = eventlist.getEventlocation();
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

	@SuppressWarnings("unchecked")
	private void getHostProcessDetail() {
		String[] time = { "", "" };
		getTime(request, time);
		String startTime = time[0];
		String endTime = time[1];

		String ip = getParaValue("ip");
		Hashtable processHt = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		try {
			processHt = hostlastmanager.getProcess_share(ip, "Process", "MemoryUtilization", startTime, endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable tempHt = new Hashtable();// 用于计算的中间变量
		Hashtable detailHt = new Hashtable();
		ProcessInfo processInfo = null;
		List<ProcessInfo> singleProcessList = new ArrayList<ProcessInfo>();
		if (processHt != null) {
			for (int m = 0; m < processHt.size(); m++) {
				tempHt = (Hashtable) processHt.get(m);
				if (tempHt != null) {
					processInfo = new ProcessInfo();
					processInfo.setName((String) tempHt.get("Name"));
					processInfo.setUSER((String) tempHt.get("USER"));
					processInfo.setType((String) tempHt.get("Type"));
					processInfo.setStatus((String) tempHt.get("Status"));
					processInfo.setCpuTime(handleCpuTime((String) tempHt.get("CpuTime")));
					processInfo.setMemoryUtilization(getFloatDigitByRemoveUnit((String) tempHt.get("MemoryUtilization")));
					processInfo.setMemory(getFloatDigitByRemoveUnit((String) tempHt.get("Memory")));
					processInfo.setCpuUtilization(getFloatDigitByRemoveUnit((String) tempHt.get("CpuUtilization")));
					processInfo.setPid((String) tempHt.get("process_id"));
					processInfo.setThreadCount((String) tempHt.get("ThreadCount"));
					processInfo.setHandleCount((String) tempHt.get("HandleCount"));
					singleProcessList.add(processInfo);
				}
			}
		}

		tempHt = new Hashtable();// 用于计算的中间变量
		ProcessInfo tempProcessInfo = null;
		if (null != singleProcessList && singleProcessList.size() > 0) {
			for (int i = 0; i < singleProcessList.size(); i++) {
				processInfo = singleProcessList.get(i);
				if (null != tempHt.get(processInfo.getName())) {
					tempProcessInfo = (ProcessInfo) tempHt.get(processInfo.getName());
					tempProcessInfo.setCpuTime((Float) tempProcessInfo.getCpuTime() + (Float) processInfo.getCpuTime());
					tempProcessInfo.setCpuUtilization((Float) tempProcessInfo.getCpuUtilization() + (Float) processInfo.getCpuUtilization());
					tempProcessInfo.setMemory((Float) tempProcessInfo.getMemory() + (Float) processInfo.getMemoryUtilization());
					tempProcessInfo.setMemoryUtilization((Float) tempProcessInfo.getMemoryUtilization() + (Float) processInfo.getMemoryUtilization());
					tempProcessInfo.setCount(tempProcessInfo.getCount() + 1);
				} else {
					processInfo.setCount(1);
					tempHt.put(processInfo.getName(), processInfo);
				}
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != tempHt && tempHt.size() > 0) {
			Enumeration e = tempHt.elements();
			while (e.hasMoreElements()) {
				processInfo = (ProcessInfo) e.nextElement();
				jsonString.append("{\"processId\":\"");
				jsonString.append(processInfo.getPid());
				jsonString.append("\",");

				jsonString.append("\"processName\":\"");
				jsonString.append(processInfo.getName());
				jsonString.append("\",");

				jsonString.append("\"processNumber\":\"");
				jsonString.append(processInfo.getCount());
				jsonString.append("\",");

				jsonString.append("\"processType\":\"");
				jsonString.append(processInfo.getType());
				jsonString.append("\",");

				jsonString.append("\"cpuKeepTime\":\"");
				jsonString.append(processInfo.getCpuTime());
				jsonString.append("\",");

				jsonString.append("\"memorySpendRate\":\"");
				jsonString.append(floatFormate((Float) processInfo.getMemoryUtilization()));
				jsonString.append("\",");

				jsonString.append("\"memorySpendValue\":\"");
				jsonString.append(floatFormate((Float) processInfo.getMemory()));
				jsonString.append("\",");

				jsonString.append("\"processStatus\":\"");
				jsonString.append(processInfo.getStatus());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + detailHt.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostServiceDetail() {
		String ip = getParaValue("ip");
		String type = getParaValue("type");
		Vector serviceV = new Vector();
		List serviceL = new ArrayList();
		List serviceA = new ArrayList();

		int size = 0;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != type && type.equals("windows")) {
					if (null != ipAllData.get("winservice")) {
						serviceV = (Vector) ipAllData.get("winservice");
					}
				} else if (null != type && type.equals("linux")) {
					if (null != ipAllData.get("servicelist")) {
						serviceL = (List) ipAllData.get("servicelist");
					}
				} else if (null != type && type.equals("aix")) {
					if (null != ipAllData.get("servicelist")) {
						serviceA = (List) ipAllData.get("servicelist");
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		ServiceCollectEntity serviceCollectEntity = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != serviceV && serviceV.size() > 0) {
			Enumeration e = serviceV.elements();
			while (e.hasMoreElements()) {
				serviceCollectEntity = (ServiceCollectEntity) e.nextElement();
				jsonString.append("{\"serviceName\":\"");
				jsonString.append(serviceCollectEntity.getName());
				jsonString.append("\",");

				jsonString.append("\"operatingState\":\"");
				jsonString.append(serviceCollectEntity.getOpstate());
				jsonString.append("\",");

				jsonString.append("\"installedState\":\"");
				jsonString.append(serviceCollectEntity.getInstate());
				jsonString.append("\",");

				jsonString.append("\"canBeUninstalled\":\"");
				jsonString.append(serviceCollectEntity.getUninst());
				jsonString.append("\",");

				jsonString.append("\"canBePaused\":\"");
				jsonString.append(serviceCollectEntity.getPaused());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = serviceV.size();
		}
		if (null != serviceA && serviceA.size() > 0) {
			for (int i = 0; i < serviceA.size(); i++) {
				Hashtable service = (Hashtable) serviceA.get(i);
				jsonString.append("{\"serviceName\":\"");
				jsonString.append(service.get("DisplayName"));
				jsonString.append("\",");

				jsonString.append("\"serviceGroup\":\"");
				jsonString.append(service.get("groupstr"));
				jsonString.append("\",");

				jsonString.append("\"processId\":\"");
				jsonString.append(service.get("pid"));
				jsonString.append("\",");

				jsonString.append("\"state\":\"");
				jsonString.append(service.get("State"));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = serviceA.size();
		}

		if (null != serviceL && serviceL.size() > 0) {
			for (int i = 0; i < serviceL.size(); i++) {
				Hashtable service = (Hashtable) serviceL.get(i);
				jsonString.append("{\"serviceName\":\"");
				jsonString.append(service.get("name"));
				jsonString.append("\",");

				jsonString.append("\"operatingState\":\"");
				jsonString.append(service.get("state"));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = serviceL.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostConfigDetail() {
		String ip = getParaValue("ip");
		String type = getParaValue("type");
		List cpuconfigL = new ArrayList();
		Vector memoryVector = new Vector();
		Vector userVector = new Vector();
		String TotalVisibleMemorySize = "0";
		String TotalSwapMemorySize = "0";

		int size = 0;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			if (null != type && type.equals("cpuconfig")) {
				try {
					if (null != ipAllData.get("cpuconfiglist")) {
						cpuconfigL = (List) ipAllData.get("cpuconfiglist");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (null != type && type.equals("memoryconfig")) {
				if (null != ipAllData.get("memory")) {
					memoryVector = (Vector) ipAllData.get("memory");
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int i = 0; i < memoryVector.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(i);
							if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory") && memorydata.getEntity().equalsIgnoreCase("Capability"))
								TotalVisibleMemorySize = memorydata.getThevalue() + memorydata.getUnit();
							if (memorydata.getSubentity().equalsIgnoreCase("SwapMemory") && memorydata.getEntity().equalsIgnoreCase("Capability"))
								TotalSwapMemorySize = memorydata.getThevalue() + memorydata.getUnit();
						}
					}
				}
			}

			if (null != type && type.equals("userconfig")) {
				try {
					if (null != ipAllData.get("user")) {
						userVector = (Vector) ipAllData.get("user");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != cpuconfigL && cpuconfigL.size() > 0) {
			Nodecpuconfig cpuconfig = null;
			for (int i = 0; i < cpuconfigL.size(); i++) {
				cpuconfig = (Nodecpuconfig) cpuconfigL.get(i);
				jsonString.append("{\"processorId\":\"");
				jsonString.append(cpuconfig.getProcessorId());
				jsonString.append("\",");
				jsonString.append("\"configName\":\"");
				jsonString.append(cpuconfig.getName());
				jsonString.append("\",");

				jsonString.append("\"cpuSpeed\":\"");
				jsonString.append(cpuconfig.getProcessorSpeed());
				jsonString.append("\",");

				jsonString.append("\"cacheSize\":\"");
				jsonString.append(cpuconfig.getL2CacheSize());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = cpuconfigL.size();
		}

		if (null != memoryVector && memoryVector.size() > 0) {
			jsonString.append("{\"visibleMemorySize\":\"");
			jsonString.append(TotalVisibleMemorySize);
			jsonString.append("\",");
			jsonString.append("\"swapMemorySize\":\"");
			jsonString.append(TotalSwapMemorySize);
			jsonString.append("\"}");
			size = 1;
		}

		if (null != userVector && userVector.size() > 0) {
			Enumeration e = userVector.elements();
			while (e.hasMoreElements()) {
				Usercollectdata user = (Usercollectdata) e.nextElement();
				jsonString.append("{\"userName\":\"");
				jsonString.append(user.getThevalue());
				jsonString.append("\",");

				jsonString.append("\"userGroup\":\"");
				jsonString.append(user.getSubentity());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = userVector.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostAixConfigDetail() {
		String ip = getParaValue("ip");
		String type = getParaValue("type");
		List cpuconfigL = new ArrayList();
		Vector memoryVector = new Vector();
		List netmedialist = new ArrayList();
		Vector userVector = new Vector();
		String TotalVisibleMemorySize = "0";
		String TotalSwapMemorySize = "0";

		int size = 0;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			if (null != type && type.equals("cpuconfig")) {
				try {
					if (null != ipAllData.get("cpuconfiglist")) {
						cpuconfigL = (List) ipAllData.get("cpuconfiglist");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (null != type && type.equals("memoryconfig")) {
				if (null != ipAllData.get("memory")) {
					memoryVector = (Vector) ipAllData.get("memory");
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int i = 0; i < memoryVector.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(i);
							if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory") && memorydata.getEntity().equalsIgnoreCase("Capability"))
								TotalVisibleMemorySize = memorydata.getThevalue() + memorydata.getUnit();
							if (memorydata.getSubentity().equalsIgnoreCase("SwapMemory") && memorydata.getEntity().equalsIgnoreCase("Capability"))
								TotalSwapMemorySize = memorydata.getThevalue() + memorydata.getUnit();
						}
					}
				}
			}

			if (null != type && type.equals("netconfig")) {
				try {
					if (null != ipAllData.get("netmedialist")) {
						netmedialist = (List) ipAllData.get("netmedialist");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (null != type && type.equals("userconfig")) {
				try {
					if (null != ipAllData.get("user")) {
						userVector = (Vector) ipAllData.get("user");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != cpuconfigL && cpuconfigL.size() > 0) {
			Nodecpuconfig cpuconfig = null;
			for (int i = 0; i < cpuconfigL.size(); i++) {
				cpuconfig = (Nodecpuconfig) cpuconfigL.get(i);
				jsonString.append("{\"dataWidth\":\"");
				jsonString.append(cpuconfig.getDataWidth());
				jsonString.append("\",");

				jsonString.append("\"processorId\":\"");
				jsonString.append(cpuconfig.getProcessorId());
				jsonString.append("\",");

				jsonString.append("\"processorName\":\"");
				jsonString.append(cpuconfig.getName());
				jsonString.append("\",");

				jsonString.append("\"processorType\":\"");
				jsonString.append(cpuconfig.getProcessorType());
				jsonString.append("\",");

				jsonString.append("\"processorSpeed\":\"");
				jsonString.append(cpuconfig.getProcessorSpeed());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = cpuconfigL.size();
		}

		if (null != memoryVector && memoryVector.size() > 0) {
			jsonString.append("{\"visibleMemorySize\":\"");
			jsonString.append(TotalVisibleMemorySize);
			jsonString.append("\",");
			jsonString.append("\"swapMemorySize\":\"");
			jsonString.append(TotalSwapMemorySize);
			jsonString.append("\"}");
			size = 1;
		}

		if (null != netmedialist && netmedialist.size() > 0) {
			for (int i = 0; i < netmedialist.size(); i++) {
				Hashtable netmedia = (Hashtable) netmedialist.get(i);
				jsonString.append("{\"netmediaName\":\"");
				jsonString.append(netmedia.get("desc"));
				jsonString.append("\",");

				jsonString.append("\"netMac\":\"");
				jsonString.append(netmedia.get("mac"));
				jsonString.append("\",");

				jsonString.append("\"netSpeed\":\"");
				jsonString.append(netmedia.get("speed"));
				jsonString.append("\",");

				jsonString.append("\"netStatus\":\"");
				jsonString.append(netmedia.get("status"));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = netmedialist.size();
		}

		if (null != userVector && userVector.size() > 0) {
			Enumeration e = userVector.elements();
			while (e.hasMoreElements()) {
				Usercollectdata user = (Usercollectdata) e.nextElement();
				jsonString.append("{\"userName\":\"");
				jsonString.append(user.getThevalue());
				jsonString.append("\",");

				jsonString.append("\"userGroup\":\"");
				jsonString.append(user.getSubentity());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			size = userVector.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostSoftwareDetail() {
		String ip = getParaValue("ip");
		Vector softwareV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("software")) {
					softwareV = (Vector) ipAllData.get("software");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		SoftwareCollectEntity softwareCollectEntity = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != softwareV && softwareV.size() > 0) {
			Enumeration e = softwareV.elements();
			while (e.hasMoreElements()) {
				softwareCollectEntity = (SoftwareCollectEntity) e.nextElement();
				jsonString.append("{\"softwareId\":\"");
				jsonString.append(softwareCollectEntity.getSwid());
				jsonString.append("\",");

				jsonString.append("\"softwareName\":\"");
				jsonString.append(softwareCollectEntity.getName());
				jsonString.append("\",");

				jsonString.append("\"softwareType\":\"");
				jsonString.append(softwareCollectEntity.getType());
				jsonString.append("\",");

				jsonString.append("\"softwareInstallDate\":\"");
				jsonString.append(softwareCollectEntity.getInsdate());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + softwareV.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostDeviceDetail() {
		String ip = getParaValue("ip");
		Vector deviceV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("device")) {
					deviceV = (Vector) ipAllData.get("device");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		DeviceCollectEntity deviceCollectEntity = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != deviceV && deviceV.size() > 0) {
			Enumeration e = deviceV.elements();
			while (e.hasMoreElements()) {
				deviceCollectEntity = (DeviceCollectEntity) e.nextElement();
				jsonString.append("{\"deviceId\":\"");
				jsonString.append(deviceCollectEntity.getDeviceindex());
				jsonString.append("\",");

				jsonString.append("\"deviceName\":\"");
				jsonString.append(deviceCollectEntity.getName().replace("\\", "\\\\"));
				jsonString.append("\",");

				jsonString.append("\"deviceType\":\"");
				jsonString.append(deviceCollectEntity.getType());
				jsonString.append("\",");

				jsonString.append("\"deviceState\":\"");
				jsonString.append(deviceCollectEntity.getStatus());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + deviceV.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostStorageDetail() {
		String ip = getParaValue("ip");
		Vector storageV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("storage")) {
					storageV = (Vector) ipAllData.get("storage");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		StorageCollectEntity storageCollectEntity = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != storageV && storageV.size() > 0) {
			Enumeration e = storageV.elements();
			while (e.hasMoreElements()) {
				storageCollectEntity = (StorageCollectEntity) e.nextElement();
				jsonString.append("{\"storageIndex\":\"");
				jsonString.append(storageCollectEntity.getStorageindex());
				jsonString.append("\",");

				jsonString.append("\"storageName\":\"");
				jsonString.append(storageCollectEntity.getName().replace("\\", "\\\\"));
				jsonString.append("\",");

				jsonString.append("\"storageType\":\"");
				jsonString.append(storageCollectEntity.getType());
				jsonString.append("\",");

				jsonString.append("\"storageCap\":\"");
				jsonString.append(storageCollectEntity.getCap());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + storageV.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostArpDetail() {
		String ip = getParaValue("ip");
		Vector arpV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("ipmac")) {
					arpV = (Vector) ipAllData.get("ipmac");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		IpMac ipMac = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != arpV && arpV.size() > 0) {
			Enumeration e = arpV.elements();
			while (e.hasMoreElements()) {
				ipMac = (IpMac) e.nextElement();
				jsonString.append("{\"ifName\":\"");
				jsonString.append(ipMac.getIfindex());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(ipMac.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"mac\":\"");
				jsonString.append(ipMac.getMac());
				jsonString.append("\",");

				jsonString.append("\"cTime\":\"");
				jsonString.append(sdf.format(ipMac.getCollecttime().getTime()));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + arpV.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostFileDetail() {
		String ip = getParaValue("ip");
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Hashtable diskhash = null;
		// 文件系统
		try {
			diskhash = hostlastmanager.getDisk_share(ip, "Disk", "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		CreateAmColumnPic linuxColumnPic = new CreateAmColumnPic();
		String valueStr = linuxColumnPic.createDiskChartTop5(diskhash);

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != valueStr && valueStr.length() > 0) {
			jsonString.append("{\"valueStr\":\"");
			jsonString.append(valueStr);
			jsonString.append("\"}");
		}
		jsonString.append("],total:" + 1 + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostFileSystemDetail() {
		String ip = getParaValue("ip");
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Hashtable diskhash = null;
		// 文件系统
		try {
			diskhash = hostlastmanager.getDisk_share(ip, "Disk", "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != diskhash && diskhash.size() > 0) {
			for (int i = 0; i < diskhash.size(); i++) {
				Hashtable dhash = (Hashtable) (diskhash.get(i));
				jsonString.append("{\"fileName\":\"");
				jsonString.append(dhash.get("name"));
				jsonString.append("\",");

				jsonString.append("\"allSize\":\"");
				jsonString.append(dhash.get("AllSize"));
				jsonString.append("\",");

				jsonString.append("\"usedSize\":\"");
				jsonString.append(dhash.get("UsedSize"));
				jsonString.append("\",");

				jsonString.append("\"utilization\":\"");
				jsonString.append(dhash.get("Utilization"));
				jsonString.append("\"}");

				if (i != diskhash.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + diskhash.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostAixPageDetail() {
		String ip = getParaValue("ip");
		Hashtable pagehash = new Hashtable();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("pagehash")) {
					pagehash = (Hashtable) ipAllData.get("pagehash");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != pagehash && pagehash.size() > 0) {
			jsonString.append("{\"pageRe\":\"");
			jsonString.append(pagehash.get("re"));
			jsonString.append("\",");

			jsonString.append("\"pagePi\":\"");
			jsonString.append(pagehash.get("pi"));
			jsonString.append("\",");

			jsonString.append("\"pagePo\":\"");
			jsonString.append(pagehash.get("po"));
			jsonString.append("\",");

			jsonString.append("\"pageFr\":\"");
			jsonString.append(pagehash.get("fr"));
			jsonString.append("\",");

			jsonString.append("\"pageSr\":\"");
			jsonString.append(pagehash.get("sr"));
			jsonString.append("\",");

			jsonString.append("\"pageCy\":\"");
			jsonString.append(pagehash.get("cy"));
			jsonString.append("\"}");

		}
		jsonString.append("],total:" + 1 + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostSyslogDetail() {
		String ip = getParaValue("ip");

		String b_time = "";
		String t_time = "";
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (b_time == null) {
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			t_time = sdf.format(new Date());
		}
		String starttime2 = b_time + " 00:00:00";
		String totime2 = t_time + " 23:59:59";
		String priorityname = getParaValue("priorityname");
		if (priorityname == null)
			priorityname = "all";

		List list = new ArrayList();
		SyslogDao syslogdao = new SyslogDao();
		try {
			list = syslogdao.getQuery(ip, starttime2, totime2, priorityname);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Syslog syslog = (Syslog) list.get(i);

				Date cc = syslog.getRecordtime().getTime();
				int priority = syslog.getPriority();
				String processName = syslog.getProcessname();
				String priorityName = syslog.getPriorityName();
				int eventid = syslog.getEventid();
				String hostname = syslog.getHostname();
				String bak = syslog.getUsername();
				String ctime = sdf.format(cc);

				jsonString.append("{\"priorityName\":\"");
				jsonString.append(priorityName);
				jsonString.append("\",");

				jsonString.append("\"cTime\":\"");
				jsonString.append(ctime);
				jsonString.append("\",");

				jsonString.append("\"processName\":\"");
				jsonString.append(processName);
				jsonString.append("\",");

				jsonString.append("\"processName1\":\"");
				jsonString.append(processName);
				jsonString.append("\",");

				jsonString.append("\"eventid\":\"");
				jsonString.append(eventid);
				jsonString.append("\",");

				jsonString.append("\"userName\":\"");
				jsonString.append(bak);
				jsonString.append("\",");

				jsonString.append("\"hostName\":\"");
				jsonString.append(hostname);
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

	private void getHostAixRouteDetail() {
		String ip = getParaValue("ip");
		List routeList = new ArrayList();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("routelist")) {
					routeList = (List) ipAllData.get("routelist");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != routeList && routeList.size() > 0) {
			for (int i = 0; i < routeList.size(); i++) {
				String routeStr = (String) routeList.get(i);
				routeStr.replace(" ", "     ");
				jsonString.append("{\"routeMessage\":\"");
				jsonString.append(routeStr);
				jsonString.append("\"}");

				if (i != routeList.size() - 1) {
					jsonString.append(",");
				}
			}

		}
		jsonString.append("],total:" + 1 + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostDiskperDetail() {
		String ip = getParaValue("ip");
		String type = getParaValue("type");
		List alldiskperf = new ArrayList();

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		alldiskperf = (List) ipAllData.get("alldiskperf");
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != alldiskperf && alldiskperf.size() > 0) {
			if (type != null && type.equals("aix")) {
				for (int i = 0; i < alldiskperf.size(); i++) {
					Hashtable dhash = (Hashtable) (alldiskperf.get(i));
					jsonString.append("{\"diskName\":\"");
					jsonString.append(dhash.get("disklebel"));
					jsonString.append("\",");

					jsonString.append("\"busy\":\"");
					jsonString.append(dhash.get("%busy"));
					jsonString.append("\",");

					jsonString.append("\"avque\":\"");
					jsonString.append(dhash.get("avque"));
					jsonString.append("\",");

					jsonString.append("\"r+w/s\":\"");
					jsonString.append(dhash.get("r+w/s"));
					jsonString.append("\",");

					jsonString.append("\"Kbs/s\":\"");
					jsonString.append(dhash.get("Kbs/s"));
					jsonString.append("\",");

					jsonString.append("\"avwait\":\"");
					jsonString.append(dhash.get("avwait"));
					jsonString.append("\",");

					jsonString.append("\"avserv\":\"");
					jsonString.append(dhash.get("avserv"));
					jsonString.append("\"}");

					if (i != alldiskperf.size() - 1) {
						jsonString.append(",");
					}
				}
			} else {
				for (int i = 0; i < alldiskperf.size(); i++) {
					Hashtable dhash = (Hashtable) (alldiskperf.get(i));
					jsonString.append("{\"diskName\":\"");
					jsonString.append(dhash.get("disklebel"));
					jsonString.append("\",");

					jsonString.append("\"busy\":\"");
					jsonString.append(dhash.get("%busy"));
					jsonString.append("\",");

					jsonString.append("\"tps\":\"");
					jsonString.append(dhash.get("tps"));
					jsonString.append("\",");

					jsonString.append("\"rd_sec\":\"");
					jsonString.append(dhash.get("rd_sec/s"));
					jsonString.append("\",");

					jsonString.append("\"wr_sec\":\"");
					jsonString.append(dhash.get("wr_sec/s"));
					jsonString.append("\",");

					jsonString.append("\"avgrq\":\"");
					jsonString.append(dhash.get("avgrq-sz"));
					jsonString.append("\",");

					jsonString.append("\"avgqu\":\"");
					jsonString.append(dhash.get("avgqu-sz"));
					jsonString.append("\",");

					jsonString.append("\"await\":\"");
					jsonString.append(dhash.get("await"));
					jsonString.append("\",");

					jsonString.append("\"svctm\":\"");
					jsonString.append(dhash.get("svctm"));
					jsonString.append("\",");

					jsonString.append("\"util\":\"");
					jsonString.append(dhash.get("%util"));
					jsonString.append("\"}");

					if (i != alldiskperf.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + alldiskperf.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getAllPerfData() {
		String ip = getParaValue("ip");
		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTime = date + " 00:00:00";
		String toTime = date + " 23:59:59";

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		Vector pingData = (Vector) ShareData.getPingdata().get(ip);

		Hashtable diskHt = new Hashtable();
		Hashtable cpuHt = new Hashtable();
		Hashtable pingHt = new Hashtable();
		Hashtable responseHt = new Hashtable();
		Hashtable[] memoryHt = null;

		I_HostLastCollectData hostLastCollectDataManager = new HostLastCollectDataManager();
		I_HostCollectData hostCollectDataManager = new HostCollectDataManager();
		try {
			cpuHt = hostCollectDataManager.getCategory(ip, "CPU", "Utilization", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			memoryHt = hostCollectDataManager.getMemory(ip, "Memory", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			diskHt = hostLastCollectDataManager.getDisk_share(ip, "Disk", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			pingHt = hostCollectDataManager.getCategory(ip, "Ping", "ConnectUtilization", startTime, toTime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			responseHt = hostCollectDataManager.getCategory(ip, "Ping", "ResponseTime", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String pingPercent = "0";
		String pingMax = "0";
		String pingAvg = "0";

		String responseTimePercent = "0";
		String responseTimeMax = "0";
		String responseTimeAvg = "0";

		String cpuPercent = "0";
		String cpuMax = "0";
		String cpuAvg = "0";

		String physicalMemoryPercent = "0";
		String physicalMemoryMax = "0";
		String physicalMemoryAvg = "0";

		String virtualMemoryPercent = "0";
		String virtualMemoryMax = "0";
		String virtualMemoryAvg = "0";

		Vector cpuV = new Vector();
		Vector memoryV = new Vector();
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("cpu")) {
					cpuV = (Vector) ipAllData.get("cpu");
					if (cpuV != null && cpuV.size() > 0) {
						CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
						if (cpu != null && cpu.getThevalue() != null) {
							cpuPercent = cpu.getThevalue();
						}
					}
				}
				if (null != ipAllData.get("memory")) {
					memoryV = (Vector) ipAllData.get("memory");
					if (memoryV != null && memoryV.size() > 0) {
						for (int i = 0; i < memoryV.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryV.get(i);
							if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
								virtualMemoryPercent = memorydata.getThevalue();
							} else if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
								virtualMemoryPercent = memorydata.getThevalue();
							}
							if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
								physicalMemoryPercent = memorydata.getThevalue();
							}
						}
					}
				}
				if (pingData != null && pingData.size() > 0) {
					pingPercent = ((PingCollectEntity) pingData.get(0)).getThevalue();
					responseTimePercent = ((PingCollectEntity) pingData.get(1)).getThevalue();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (null != pingHt) {
			if (null != pingHt.get("pingmax")) {
				pingMax = (String) pingHt.get("pingmax");
			}
			if (null != pingHt.get("avgpingcon")) {
				pingAvg = (String) pingHt.get("avgpingcon");
			}
		}
		if (null != responseHt) {
			if (null != responseHt.get("pingmax")) {
				responseTimeMax = (String) responseHt.get("pingmax");
			}
			if (null != responseHt.get("avgpingcon")) {
				responseTimeAvg = (String) responseHt.get("avgpingcon");
			}
		}

		if (null != cpuHt) {
			if (null != cpuHt.get("max")) {
				cpuMax = (String) cpuHt.get("max");
			}
			if (null != cpuHt.get("avgcpucon")) {
				cpuAvg = (String) cpuHt.get("avgcpucon");
			}
		}
		if (null != memoryHt) {
			Hashtable max = memoryHt[1];
			Hashtable avg = memoryHt[2];
			if (null != max.get("PhysicalMemory")) {
				physicalMemoryMax = (String) max.get("PhysicalMemory");
			}
			if (null != max.get("VirtualMemory")) {
				virtualMemoryMax = (String) max.get("VirtualMemory");
			} else if (null != max.get("SwapMemory")) {
				virtualMemoryMax = (String) max.get("SwapMemory");
			}

			if (null != avg.get("PhysicalMemory")) {
				physicalMemoryAvg = (String) avg.get("PhysicalMemory");
			}
			if (null != avg.get("VirtualMemory")) {
				virtualMemoryAvg = (String) avg.get("VirtualMemory");
			} else if (null != avg.get("SwapMemory")) {
				virtualMemoryAvg = (String) avg.get("SwapMemory");
			}
		}

		// 画图、组织字符串

		// 磁盘的利用率 字符串
		CreateAmColumnPic amColumnPic = new CreateAmColumnPic();
		String diskString = amColumnPic.createWinDiskChart(diskHt);

		// CPU
		CreateMetersPic cmp = new CreateMetersPic();
		cmp.createCpuPic(ip, Integer.parseInt(cpuPercent.replace(".0", "")));
		cmp.createMaxCpuPic(ip, cpuMax);
		cmp.createAvgCpuPic(ip, removeUnit(cpuAvg, 1));

		// 响应时间
		CreateBarPic cbp = new CreateBarPic();
		TitleModel tm = new TitleModel();
		double[] dataArray = { new Double(responseTimePercent), new Double(responseTimeMax), new Double(removeUnit(responseTimeAvg, 2)) };
		String[] labelArray = { "当前响应时间(ms)", "最大响应时间(ms)", "平均响应时间(ms)" };
		tm = new TitleModel();
		tm.setPicName(ip + "response");//
		tm.setBgcolor(0xffffff);
		tm.setXpic(450);// 图片长度
		tm.setYpic(180);// 图片高度
		tm.setX1(30);// 左面距离
		tm.setX2(20);// 上面距离
		tm.setX3(400);// 内图宽度
		tm.setX4(130);// 内图高度
		tm.setX5(10);
		tm.setX6(115);
		cbp.createTimeBarPic(dataArray, labelArray, tm, 40);

		// 内存字符串
		String memoryString = null;
		double currentPhysicalMemory = new Double(physicalMemoryPercent);
		double maxPhysicalMemory = new Double(removeUnit(physicalMemoryMax, 1));
		double avgPhysicalMemory = new Double(removeUnit(physicalMemoryAvg, 1));

		double currentVirtualMemory = new Double(virtualMemoryPercent);
		double maxVirtualMemory = new Double(removeUnit(virtualMemoryMax, 1));
		double avgVirtualMemory = new Double(removeUnit(virtualMemoryAvg, 1));

		double[] cPhysicalArray = { currentPhysicalMemory, 100 - currentPhysicalMemory };
		double[] cVirtualArray = { currentVirtualMemory, 100 - currentVirtualMemory };

		double[] maxPhysicalArray = { maxPhysicalMemory, 100 - maxPhysicalMemory };
		double[] maxVirtualArray = { maxVirtualMemory, 100 - maxVirtualMemory };

		double[] avgPhysicalArray = { avgPhysicalMemory, 100 - avgPhysicalMemory };
		double[] avgVirtualArray = { avgVirtualMemory, 100 - avgVirtualMemory };

		StringBuffer xmlStr = new StringBuffer("0");
		xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
		xmlStr.append("<chart><series>");
		String[] titleStr = new String[] { "当前物理", "当前虚拟", "平均物理", "平均虚拟", "最大物理", "最大虚拟" };
		String[] title = new String[] { "当前已用", "当前未用", "平均已用", "平均未用", "最大已用", "最大未用" };

		for (int i = 0; i < 6; i++) {
			xmlStr.append("<value xid='").append(i).append("'>").append(titleStr[i]).append("</value>");
		}
		xmlStr.append("</series><graphs>");
		long cp = Math.round(cPhysicalArray[0]);
		long cv = Math.round(cVirtualArray[0]);
		long mp = Math.round(maxPhysicalArray[0]);
		long mv = Math.round(maxVirtualArray[0]);
		long ap = Math.round(avgPhysicalArray[0]);
		long av = Math.round(avgVirtualArray[0]);

		long[] data = { cp, cv, 100 - cp, 100 - cv, ap, av, 100 - ap, 100 - av, mp, mv, 100 - mp, 100 - mv };
		int tempInt = 0, tempId = 0;
		for (int i = 0; i < 6; i++) {
			if (i == 1)
				tempId = 0;
			if (i == 3)
				tempId = 2;
			if (i == 5)
				tempId = 4;
			xmlStr.append("<graph gid='").append(i).append("' title='").append(title[i]).append("'>").append("<value xid='" + tempId + "'> " + data[tempInt]).append("</value>");
			xmlStr.append("<value xid='" + (++tempId) + "'>" + data[++tempInt] + "</value>");
			xmlStr.append("</graph>");
			tempId++;
			tempInt++;
		}
		xmlStr.append("</graphs></chart>");
		memoryString = xmlStr.toString();

		// 连通率字符串
		StringBuffer pingPercentSB = new StringBuffer();
		pingPercentSB.append("连通;").append(Math.round(Double.valueOf(pingPercent))).append(";false;7CFC00\\n");
		pingPercentSB.append("未连通;").append(100 - Math.round(Double.valueOf(pingPercent))).append(";false;FF0000\\n");
		StringBuffer pingMaxSB = new StringBuffer();
		pingMaxSB.append("连通;").append(Math.round(Double.valueOf(pingMax))).append(";false;7CFC00\\n");
		pingMaxSB.append("未连通;").append(100 - Math.round(Double.valueOf(pingMax))).append(";false;FF0000\\n");
		StringBuffer pingAvgSB = new StringBuffer();
		pingAvgSB.append("连通;").append(Math.round(Double.valueOf(removeUnit(pingAvg, 1)))).append(";false;7CFC00\\n");
		pingAvgSB.append("未连通;").append(100 - Math.round(Double.valueOf(removeUnit(pingAvg, 1)))).append(";false;FF0000\\n");

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"pingPercentString\":\"");
		jsonString.append(pingPercentSB.toString());
		jsonString.append("\",");

		jsonString.append("\"pingMaxString\":\"");
		jsonString.append(pingMaxSB.toString());
		jsonString.append("\",");

		jsonString.append("\"pingAvgString\":\"");
		jsonString.append(pingAvgSB.toString());
		jsonString.append("\",");

		jsonString.append("\"diskString\":\"");
		jsonString.append(diskString);
		jsonString.append("\",");

		jsonString.append("\"memoryString\":\"");
		jsonString.append(memoryString);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getAixAllPerfData() {
		String ip = getParaValue("ip");
		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTime = date + " 00:00:00";
		String toTime = date + " 23:59:59";

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		Vector pingData = (Vector) ShareData.getPingdata().get(ip);

		Hashtable diskHt = new Hashtable();
		Hashtable cpuHt = new Hashtable();
		Hashtable pingHt = new Hashtable();
		Hashtable responseHt = new Hashtable();
		Hashtable[] memoryHt = null;

		I_HostLastCollectData hostLastCollectDataManager = new HostLastCollectDataManager();
		I_HostCollectData hostCollectDataManager = new HostCollectDataManager();
		try {
			cpuHt = hostCollectDataManager.getCategory(ip, "CPU", "Utilization", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			memoryHt = hostCollectDataManager.getMemory(ip, "Memory", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			diskHt = hostLastCollectDataManager.getDisk_share(ip, "Disk", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			pingHt = hostCollectDataManager.getCategory(ip, "Ping", "ConnectUtilization", startTime, toTime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			responseHt = hostCollectDataManager.getCategory(ip, "Ping", "ResponseTime", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String pingPercent = "0";
		String pingMax = "0";
		String pingAvg = "0";

		String responseTimePercent = "0";
		String responseTimeMax = "0";
		String responseTimeAvg = "0";

		String cpuPercent = "0";
		String cpuMax = "0";
		String cpuAvg = "0";

		String physicalMemoryPercent = "0";
		String physicalMemoryMax = "0";
		String physicalMemoryAvg = "0";

		String virtualMemoryPercent = "0";
		String virtualMemoryMax = "0";
		String virtualMemoryAvg = "0";

		Vector cpuV = new Vector();
		Vector memoryV = new Vector();
		if (ipAllData != null) {
			try {
				if (null != ipAllData.get("cpu")) {
					cpuV = (Vector) ipAllData.get("cpu");
					if (cpuV != null && cpuV.size() > 0) {
						CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
						if (cpu != null && cpu.getThevalue() != null) {
							cpuPercent = cpu.getThevalue();
						}
					}
				}
				if (null != ipAllData.get("memory")) {
					memoryV = (Vector) ipAllData.get("memory");
					if (memoryV != null && memoryV.size() > 0) {
						for (int i = 0; i < memoryV.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryV.get(i);
							if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
								virtualMemoryPercent = memorydata.getThevalue();
							} else if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
								virtualMemoryPercent = memorydata.getThevalue();
							}
							if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
								physicalMemoryPercent = memorydata.getThevalue();
							}
						}
					}
				}
				if (pingData != null && pingData.size() > 0) {
					pingPercent = ((PingCollectEntity) pingData.get(0)).getThevalue();
					responseTimePercent = ((PingCollectEntity) pingData.get(1)).getThevalue();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (null != pingHt) {
			if (null != pingHt.get("pingmax")) {
				pingMax = (String) pingHt.get("pingmax");
			}
			if (null != pingHt.get("avgpingcon")) {
				pingAvg = (String) pingHt.get("avgpingcon");
			}
		}
		if (null != responseHt) {
			if (null != responseHt.get("pingmax")) {
				responseTimeMax = (String) responseHt.get("pingmax");
			}
			if (null != responseHt.get("avgpingcon")) {
				responseTimeAvg = (String) responseHt.get("avgpingcon");
			}
		}

		if (null != cpuHt) {
			if (null != cpuHt.get("max")) {
				cpuMax = (String) cpuHt.get("max");
			}
			if (null != cpuHt.get("avgcpucon")) {
				cpuAvg = (String) cpuHt.get("avgcpucon");
			}
		}
		if (null != memoryHt) {
			Hashtable max = memoryHt[1];
			Hashtable avg = memoryHt[2];
			if (null != max.get("PhysicalMemory")) {
				physicalMemoryMax = (String) max.get("PhysicalMemory");
			}
			if (null != max.get("VirtualMemory")) {
				virtualMemoryMax = (String) max.get("VirtualMemory");
			} else if (null != max.get("SwapMemory")) {
				virtualMemoryMax = (String) max.get("SwapMemory");
			}

			if (null != avg.get("PhysicalMemory")) {
				physicalMemoryAvg = (String) avg.get("PhysicalMemory");
			}
			if (null != avg.get("VirtualMemory")) {
				virtualMemoryAvg = (String) avg.get("VirtualMemory");
			} else if (null != avg.get("SwapMemory")) {
				virtualMemoryAvg = (String) avg.get("SwapMemory");
			}
		}

		// 画图、组织字符串

		// 磁盘的利用率 字符串
		CreateAmColumnPic amColumnPic = new CreateAmColumnPic();
		String diskString = amColumnPic.createWinDiskChart(diskHt);

		// CPU
		CreateMetersPic cmp = new CreateMetersPic();
		cmp.createCpuPic(ip, Integer.parseInt(cpuPercent.replace(".0", "")));
		cmp.createMaxCpuPic(ip, cpuMax);
		cmp.createAvgCpuPic(ip, removeUnit(cpuAvg, 1));

		// 响应时间
		CreateBarPic cbp = new CreateBarPic();
		TitleModel tm = new TitleModel();
		double[] dataArray = { new Double(responseTimePercent), new Double(responseTimeMax), new Double(removeUnit(responseTimeAvg, 2)) };
		String[] labelArray = { "当前响应时间(ms)", "最大响应时间(ms)", "平均响应时间(ms)" };
		tm = new TitleModel();
		tm.setPicName(ip + "response");//
		tm.setBgcolor(0xffffff);
		tm.setXpic(450);// 图片长度
		tm.setYpic(180);// 图片高度
		tm.setX1(30);// 左面距离
		tm.setX2(20);// 上面距离
		tm.setX3(400);// 内图宽度
		tm.setX4(130);// 内图高度
		tm.setX5(10);
		tm.setX6(115);
		cbp.createTimeBarPic(dataArray, labelArray, tm, 40);

		// 内存字符串
		String memoryString = null;
		double currentPhysicalMemory = new Double(physicalMemoryPercent);
		double maxPhysicalMemory = new Double(removeUnit(physicalMemoryMax, 1));
		double avgPhysicalMemory = new Double(removeUnit(physicalMemoryAvg, 1));

		double currentVirtualMemory = new Double(virtualMemoryPercent);
		double maxVirtualMemory = new Double(removeUnit(virtualMemoryMax, 1));
		double avgVirtualMemory = new Double(removeUnit(virtualMemoryAvg, 1));

		double[] cPhysicalArray = { currentPhysicalMemory, 100 - currentPhysicalMemory };
		double[] cVirtualArray = { currentVirtualMemory, 100 - currentVirtualMemory };

		double[] maxPhysicalArray = { maxPhysicalMemory, 100 - maxPhysicalMemory };
		double[] maxVirtualArray = { maxVirtualMemory, 100 - maxVirtualMemory };

		double[] avgPhysicalArray = { avgPhysicalMemory, 100 - avgPhysicalMemory };
		double[] avgVirtualArray = { avgVirtualMemory, 100 - avgVirtualMemory };

		StringBuffer xmlStr = new StringBuffer("0");
		xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
		xmlStr.append("<chart><series>");
		String[] titleStr = new String[] { "当前物理", "当前虚拟", "平均物理", "平均虚拟", "最大物理", "最大虚拟" };
		String[] title = new String[] { "当前已用", "当前未用", "平均已用", "平均未用", "最大已用", "最大未用" };

		for (int i = 0; i < 6; i++) {
			xmlStr.append("<value xid='").append(i).append("'>").append(titleStr[i]).append("</value>");
		}
		xmlStr.append("</series><graphs>");
		long cp = Math.round(cPhysicalArray[0]);
		long cv = Math.round(cVirtualArray[0]);
		long mp = Math.round(maxPhysicalArray[0]);
		long mv = Math.round(maxVirtualArray[0]);
		long ap = Math.round(avgPhysicalArray[0]);
		long av = Math.round(avgVirtualArray[0]);

		long[] data = { cp, cv, 100 - cp, 100 - cv, ap, av, 100 - ap, 100 - av, mp, mv, 100 - mp, 100 - mv };
		int tempInt = 0, tempId = 0;
		for (int i = 0; i < 6; i++) {
			if (i == 1)
				tempId = 0;
			if (i == 3)
				tempId = 2;
			if (i == 5)
				tempId = 4;
			xmlStr.append("<graph gid='").append(i).append("' title='").append(title[i]).append("'>").append("<value xid='" + tempId + "'> " + data[tempInt]).append("</value>");
			xmlStr.append("<value xid='" + (++tempId) + "'>" + data[++tempInt] + "</value>");
			xmlStr.append("</graph>");
			tempId++;
			tempInt++;
		}
		xmlStr.append("</graphs></chart>");
		memoryString = xmlStr.toString();

		// 连通率字符串
		StringBuffer pingPercentSB = new StringBuffer();
		pingPercentSB.append("连通;").append(Math.round(Double.valueOf(pingPercent))).append(";false;7CFC00\\n");
		pingPercentSB.append("未连通;").append(100 - Math.round(Double.valueOf(pingPercent))).append(";false;FF0000\\n");
		StringBuffer pingMaxSB = new StringBuffer();
		pingMaxSB.append("连通;").append(Math.round(Double.valueOf(pingMax))).append(";false;7CFC00\\n");
		pingMaxSB.append("未连通;").append(100 - Math.round(Double.valueOf(pingMax))).append(";false;FF0000\\n");
		StringBuffer pingAvgSB = new StringBuffer();
		pingAvgSB.append("连通;").append(Math.round(Double.valueOf(removeUnit(pingAvg, 1)))).append(";false;7CFC00\\n");
		pingAvgSB.append("未连通;").append(100 - Math.round(Double.valueOf(removeUnit(pingAvg, 1)))).append(";false;FF0000\\n");

		// 换页率
		Hashtable cpudetailhash = new Hashtable();
		Hashtable pagedetailhash = new Hashtable();
		int pageingused = 0;
		String totalpageing = "0";
		Hashtable paginghash = new Hashtable();
		int innerAreaColor1 = 0xFF9900;
		int innerAreaColor2 = 0xffff00;
		int innerAreaColor3 = 0x234793;
		paginghash = (Hashtable) ipAllData.get("paginghash");
		if (paginghash == null)
			paginghash = new Hashtable();
		if (paginghash.get("Total_Paging_Space") != null) {
			pageingused = Integer.parseInt(((String) paginghash.get("Percent_Used")).replaceAll("%", ""));
			totalpageing = (String) paginghash.get("Total_Paging_Space");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			cpudetailhash = hostmanager.getCpuDetail(ip, starttime1, totime1);
			pagedetailhash = hostmanager.getPageingDetail(ip, starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String maxpageoutused = "0";
		String avgpageoutused = "0";
		if (pagedetailhash != null) {

			if (pagedetailhash.containsKey("maxvalue"))
				maxpageoutused = (String) pagedetailhash.get("maxvalue");
			if (pagedetailhash.containsKey("avgvalue"))
				avgpageoutused = (String) pagedetailhash.get("avgvalue");
		}
		if (maxpageoutused == null || maxpageoutused.equals("")) {
			maxpageoutused = "0";
			avgpageoutused = "0";
		}
		// 当前换页率
		cmp = new CreateMetersPic();
		MeterModel mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("换页率");
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "pageout");//
		mm.setValue(pageingused);//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离
		mm.setOutPointerColor(0x80ff80);// 设置指针外部颜色
		mm.setInPointerColor(0x8080ff);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		List<StageColor> sm = new ArrayList<StageColor>();
		StageColor sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		StageColor sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		StageColor sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		cmp.createSimpleMeter(mm);

		// 生成换页率最大值仪表盘
		mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("换页率");
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "pageoutmax");//
		mm.setValue(new Double(maxpageoutused.replaceAll("%", "")));//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离
		mm.setOutPointerColor(0x80ff80);// 设置指针外部颜色
		mm.setInPointerColor(0x8080ff);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		sm = new ArrayList<StageColor>();
		sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		cmp.createSimpleMeter(mm);

		// 生成换页率平均值仪表盘
		mm = new MeterModel();
		mm.setBgColor(0xffffff);
		mm.setInnerRoundColor(0xececec);
		mm.setOutRingColor(0x80ff80);
		mm.setTitle("换页率");
		mm.setPicx(150);//
		mm.setPicy(150);//
		mm.setMeterX(80);//
		mm.setMeterY(80);//
		mm.setPicName(ip + "pageoutavg");//
		mm.setValue(new Double(avgpageoutused.replaceAll("%", "")));//
		mm.setMeterSize(60);// 设置仪表盘大小
		mm.setTitleY(79);// 设置标题离左边距离
		mm.setTitleTop(122);// 设置标题离顶部距离
		mm.setValueY(78);// 设置值离左边距离
		mm.setValueTop(105);// 设置值离顶部距离
		mm.setOutPointerColor(0x80ff80);// 设置指针外部颜色
		mm.setInPointerColor(0x8080ff);// 设置指针内部颜色
		mm.setFontSize(10);// 设置字体大小
		sm = new ArrayList<StageColor>();
		sc1 = new StageColor();
		sc1.setColor(innerAreaColor3);
		sc1.setStart(0);
		sc1.setEnd(60);
		sc2 = new StageColor();
		sc2.setColor(innerAreaColor2);
		sc2.setStart(60);
		sc2.setEnd(80);
		sc3 = new StageColor();
		sc3.setColor(innerAreaColor1);
		sc3.setStart(80);
		sc3.setEnd(100);
		sm.add(sc1);
		sm.add(sc2);
		sm.add(sc3);
		mm.setList(sm);
		cmp.createSimpleMeter(mm);

		// cpu详细信息
		List cpuperflist = new ArrayList();
		Hashtable cpuperfhash = new Hashtable();
		cpuperflist = (List) ipAllData.get("cpuperflist");
		if (cpuperflist != null)
			cpuperfhash = (Hashtable) cpuperflist.get(0);
		CreateAmColumnPic cpudetail = new CreateAmColumnPic();
		String cpuxinxiStr = cpudetail.createCpuDetailAmChart(cpuperfhash, cpudetailhash);

		StringBuffer user = new StringBuffer();
		StringBuffer syss = new StringBuffer();
		StringBuffer wios = new StringBuffer();
		StringBuffer idles = new StringBuffer();
		if (cpuperfhash != null && cpuperfhash.size() > 0) {
			String usr = (String) cpuperfhash.get("%usr");

			String sys = (String) cpuperfhash.get("%sys");
			String wio = (String) cpuperfhash.get("%wio");
			String idle = (String) cpuperfhash.get("%idle");
			String maxusr = "0";
			String maxsys = "0";
			String maxwio = "0";
			String maxidle = "0";
			String avgusr = "0";
			String avgsys = "0";
			String avgwio = "0";
			String avgidle = "0";
			if (cpudetailhash != null) {
				if (cpudetailhash.containsKey("usr")) {
					Hashtable usrhash = (Hashtable) cpudetailhash.get("usr");
					if (usrhash != null) {
						if (usrhash.containsKey("maxvalue"))
							maxusr = (String) usrhash.get("maxvalue");
						if (usrhash.containsKey("avgvalue"))
							avgusr = (String) usrhash.get("avgvalue");
					}
					user.append(user);
					user.append(",");
					user.append(maxusr);
					user.append(",");
					user.append(avgusr);
				}
				if (cpudetailhash.containsKey("sys")) {
					Hashtable syshash = (Hashtable) cpudetailhash.get("sys");
					if (syshash != null) {
						if (syshash.containsKey("maxvalue"))
							maxsys = (String) syshash.get("maxvalue");
						if (syshash.containsKey("avgvalue"))
							avgsys = (String) syshash.get("avgvalue");
					}
					syss.append(sys);
					syss.append(",");
					syss.append(maxsys);
					syss.append(",");
					syss.append(avgsys);
				}
				if (cpudetailhash.containsKey("wio")) {
					Hashtable wiohash = (Hashtable) cpudetailhash.get("wio");
					if (wiohash != null) {
						if (wiohash.containsKey("maxvalue"))
							maxwio = (String) wiohash.get("maxvalue");
						if (wiohash.containsKey("avgvalue"))
							avgwio = (String) wiohash.get("avgvalue");
					}
					wios.append(wio);
					wios.append(",");
					wios.append(maxwio);
					wios.append(",");
					wios.append(avgwio);
				}
				if (cpudetailhash.containsKey("idle")) {
					Hashtable idlehash = (Hashtable) cpudetailhash.get("idle");
					if (idlehash != null) {
						if (idlehash.containsKey("maxvalue"))
							maxidle = (String) idlehash.get("maxvalue");
						if (idlehash.containsKey("avgvalue"))
							avgidle = (String) idlehash.get("avgvalue");
					}
					idles.append(idle);
					idles.append(",");
					idles.append(maxidle);
					idles.append(",");
					idles.append(avgidle);
				}
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"pingPercentString\":\"");
		jsonString.append(pingPercentSB.toString());
		jsonString.append("\",");

		jsonString.append("\"pingMaxString\":\"");
		jsonString.append(pingMaxSB.toString());
		jsonString.append("\",");

		jsonString.append("\"pingAvgString\":\"");
		jsonString.append(pingAvgSB.toString());
		jsonString.append("\",");

		jsonString.append("\"diskString\":\"");
		jsonString.append(diskString);
		jsonString.append("\",");

		jsonString.append("\"memoryString\":\"");
		jsonString.append(memoryString);
		jsonString.append("\",");

		jsonString.append("\"cpuxinxiString\":\"");
		jsonString.append(cpuxinxiStr);
		jsonString.append("\"}");

		jsonString.append(",");

		jsonString.append("{\"usr\":\"");
		jsonString.append(user.toString());
		jsonString.append("\",");

		jsonString.append("\"sys\":\"");
		jsonString.append(syss.toString());
		jsonString.append("\",");

		jsonString.append("\"wio\":\"");
		jsonString.append(wios.toString());
		jsonString.append("\",");

		jsonString.append("\"idle\":\"");
		jsonString.append(idles.toString());
		jsonString.append("\",");

		jsonString.append("\"totalSwap\":\"");
		jsonString.append(totalpageing);
		jsonString.append("\",");

		jsonString.append("\"currSwap\":\"");
		jsonString.append(pageingused);
		jsonString.append("\",");

		jsonString.append("\"avgSwap\":\"");
		jsonString.append(avgpageoutused);
		jsonString.append("\",");

		jsonString.append("\"maxSwap\":\"");
		jsonString.append(maxpageoutused);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostNodeConfig() {
		String ip = getParaValue("ip");

		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTime = date + " 00:00:00";
		String toTime = date + " 23:59:59";

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		String ctTime = null;
		HostNodeDao hostNodeDao = new HostNodeDao();
		HostNode node = null;
		NodeDTO nodeDTO = null;
		NodeUtil util = new NodeUtil();
		try {
			node = hostNodeDao.findByIpaddress(ip);
			nodeDTO = util.creatNodeDTOByNode(node);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		Vector memoryV = new Vector();
		String virtualMemoryPercent = null;
		String physicalMemoryPercent = null;
		String virtualMemoryCap = null;
		String physicalMemoryCap = null;
		String sysUpTime = null;

		if (null != ipAllData) {
			if (null != ipAllData.get("memory")) {
				memoryV = (Vector) ipAllData.get("memory");
				if (memoryV != null && memoryV.size() > 0) {
					for (int i = 0; i < memoryV.size(); i++) {
						MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryV.get(i);
						if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
							virtualMemoryPercent = memorydata.getThevalue();
						} else if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
							virtualMemoryPercent = memorydata.getThevalue();
						}
						if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
							physicalMemoryPercent = memorydata.getThevalue();
						}
						if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Capability".equalsIgnoreCase(memorydata.getEntity())) {
							virtualMemoryCap = df.format(Float.parseFloat(memorydata.getThevalue()));
							virtualMemoryCap = virtualMemoryCap + memorydata.getUnit();
						} else if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Capability".equalsIgnoreCase(memorydata.getEntity())) {
							virtualMemoryCap = df.format(Float.parseFloat(memorydata.getThevalue()));
							virtualMemoryCap = virtualMemoryCap + memorydata.getUnit();
						}
						if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Capability".equalsIgnoreCase(memorydata.getEntity())) {
							physicalMemoryCap = df.format(Float.parseFloat(memorydata.getThevalue()));
							physicalMemoryCap = physicalMemoryCap + memorydata.getUnit();
						}
					}
				}
			}
			Vector systemV = new Vector();
			if (null != ipAllData.get("system")) {
				systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemCollectEntity = (SystemCollectEntity) systemV.get(i);
						if (systemCollectEntity.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysUpTime = systemCollectEntity.getThevalue();
							ctTime = sdf.format(systemCollectEntity.getCollecttime().getTime());
						}
					}
				}
			}
		}

		Hashtable cpuHt = new Hashtable();
		Hashtable pingHt = new Hashtable();
		Hashtable responseHt = new Hashtable();
		Hashtable[] memoryHt = null;

		I_HostCollectData hostCollectDataManager = new HostCollectDataManager();
		try {
			cpuHt = hostCollectDataManager.getCategory(ip, "CPU", "Utilization", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			memoryHt = hostCollectDataManager.getMemory(ip, "Memory", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			pingHt = hostCollectDataManager.getCategory(ip, "Ping", "ConnectUtilization", startTime, toTime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			responseHt = hostCollectDataManager.getCategory(ip, "Ping", "ResponseTime", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String pingAvg = "0";
		String responseTimeAvg = "0";
		int responseTimeAvgInt = -1;
		String cpuAvg = "0";
		String physicalMemoryAvg = "0";

		if (null != pingHt) {
			if (null != pingHt.get("avgpingcon")) {
				pingAvg = (String) pingHt.get("avgpingcon");
			}
		}
		if (null != responseHt) {
			if (null != responseHt.get("avgpingcon")) {
				responseTimeAvg = (String) responseHt.get("avgpingcon");
				if (null != responseTimeAvg) {
					responseTimeAvgInt = Integer.parseInt(responseTimeAvg.substring(0, responseTimeAvg.indexOf(".")));
				}
			}
		}

		if (null != cpuHt) {
			if (null != cpuHt.get("avgcpucon")) {
				cpuAvg = (String) cpuHt.get("avgcpucon");
			}
		}
		if (null != memoryHt) {
			Hashtable avg = memoryHt[2];
			if (null != avg.get("PhysicalMemory")) {
				physicalMemoryAvg = ((String) avg.get("PhysicalMemory")).replace("%", "");
			}
		}

		// 画图
		CreateMetersPic cmp = new CreateMetersPic();
		String pathPing = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashBoardGray.png";
		String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashBoard1.png";
		// 内存
		cmp.createPic(ip, new Double(physicalMemoryAvg.replaceAll("%", "")), path, "内存利用率", "avgpmemory");
		// 连通率
		cmp.createChartByParam(ip, removeUnit(pingAvg, 1), pathPing, "连通率", "pingdata");
		// CPU
		cmp.createAvgCpuPic(ip, removeUnit(cpuAvg, 1));

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Hashtable memoryHtForConfig = new Hashtable();
		Hashtable diskHtForConfig = new Hashtable();
		try {
			memoryHtForConfig = hostlastmanager.getMemory_share(ip, "Memory", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			diskHtForConfig = hostlastmanager.getDisk_share(ip, "Disk", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 组装字符串
		// 内存最大、平均利用率
		String diskString = "0";
		String lastWeekCpuString = "0";
		String memoryString = "0";
		CreateAmColumnPic columnPic = new CreateAmColumnPic();
		memoryString = columnPic.createAmMemoryChart(ip, memoryHtForConfig);
		// 最近一周CPU平均利用率
		lastWeekCpuString = columnPic.createCpuChartLastWeek(ip);
		// 磁盘利用率
		diskString = columnPic.createDiskChart(diskHtForConfig);

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"nodeId\":\"");
		jsonString.append(node.getId());
		jsonString.append("\",");

		jsonString.append("\"nodeIp\":\"");
		jsonString.append(node.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"nodeAlias\":\"");
		jsonString.append(node.getAlias());
		jsonString.append("\",");

		jsonString.append("\"nodeSysName\":\"");
		jsonString.append(node.getSysName());
		jsonString.append("\",");

		jsonString.append("\"type\":\"");
		jsonString.append(nodeDTO.getType());
		jsonString.append("\",");

		jsonString.append("\"subType\":\"");
		jsonString.append(nodeDTO.getSubtype());
		jsonString.append("\",");

		jsonString.append("\"sysUpTime\":\"");
		jsonString.append(sysUpTime);
		jsonString.append("\",");

		jsonString.append("\"sysDescr\":\"");
		jsonString.append(node.getSysDescr());
		jsonString.append("\",");

		jsonString.append("\"physicalMemoryCap\":\"");
		jsonString.append(physicalMemoryCap);
		jsonString.append("\",");

		jsonString.append("\"virtualMemoryCap\":\"");
		jsonString.append(virtualMemoryCap);
		jsonString.append("\",");

		jsonString.append("\"physicalMemoryRate\":\"");
		jsonString.append(physicalMemoryPercent);
		jsonString.append("\",");

		jsonString.append("\"virtualMemoryRate\":\"");
		jsonString.append(virtualMemoryPercent);
		jsonString.append("\",");

		jsonString.append("\"responseTimeAvgInt\":\"");
		jsonString.append(responseTimeAvgInt);
		jsonString.append("\",");

		jsonString.append("\"diskString\":\"");
		jsonString.append(diskString);
		jsonString.append("\",");

		jsonString.append("\"lastWeekCpuString\":\"");
		jsonString.append(lastWeekCpuString);
		jsonString.append("\",");

		jsonString.append("\"memoryString\":\"");
		jsonString.append(memoryString);
		jsonString.append("\",");

		jsonString.append("\"ctTime\":\"");
		jsonString.append(ctTime);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("getHostNodeDataByType")) {
			getHostNodeDataByType();
		} else if (action.equals("getHostInterfaceDetail")) {
			getHostInterfaceDetail();
		} else if (action.equals("getHostProcessDetail")) {
			getHostProcessDetail();
		} else if (action.equals("getHostServiceDetail")) {
			getHostServiceDetail();
		} else if (action.equals("getHostSoftwareDetail")) {
			getHostSoftwareDetail();
		} else if (action.equals("getHostDeviceDetail")) {
			getHostDeviceDetail();
		} else if (action.equals("getHostStorageDetail")) {
			getHostStorageDetail();
		} else if (action.equals("getHostArpDetail")) {
			getHostArpDetail();
		} else if (action.equals("getAllPerfData")) {
			getAllPerfData();
		} else if (action.equals("getHostNodeConfig")) {
			getHostNodeConfig();
		} else if (action.equals("getHostConfigDetail")) {
			getHostConfigDetail();
		} else if (action.equals("getHostFileDetail")) {
			getHostFileDetail();
		} else if (action.equals("getHostFileSystemDetail")) {
			getHostFileSystemDetail();
		} else if (action.equals("getHostDiskperDetail")) {
			getHostDiskperDetail();
		} else if (action.equals("getHostEventDetail")) {
			getHostEventDetail();
		} else if (action.equals("getAixAllPerfData")) {
			getAixAllPerfData();
		} else if (action.equals("getHostAixConfigDetail")) {
			getHostAixConfigDetail();
		} else if (action.equals("getHostAixPageDetail")) {
			getHostAixPageDetail();
		} else if (action.equals("getHostAixRouteDetail")) {
			getHostAixRouteDetail();
		} else if (action.equals("getHostSyslogDetail")) {
			getHostSyslogDetail();
		}

	}

	private float handleCpuTime(String CpuTime) {
		float sumOfCPU = 0.0f;
		Pattern p = Pattern.compile("(\\d+):(\\d+)");
		if (CpuTime != null) {
			if (CpuTime.indexOf(":") != -1) {
				Matcher matcher = p.matcher(CpuTime);
				if (matcher.find()) {
					String t1 = matcher.group(1);
					String t2 = matcher.group(2);
					sumOfCPU = Float.parseFloat(t1) * 60 + Float.parseFloat(t2);
				}
			} else {
				sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
			}
		}
		return sumOfCPU;
	}

	private String removeUnit(String str, int l) {
		if (str != null && str.trim().length() > 0) {
			return str.substring(0, str.length() - l);
		} else {
			return null;
		}
	}

	private float getFloatDigitByRemoveUnit(String str) {
		float floatDigit = 0.0f;
		if (str != null && str.trim().length() > 0) {
			floatDigit = Float.parseFloat(str.substring(0, str.length() - 1));
		}
		return floatDigit;
	}

	private float floatFormate(Float f) {
		int scale = 2;// 设置位数
		int roundingMode = 4;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
		BigDecimal bd = new BigDecimal((double) f);
		bd = bd.setScale(scale, roundingMode);
		f = bd.floatValue();
		return f;
	}

	private void getTime(HttpServletRequest request, String[] time) {
		DateE datemanager = new DateE();
		Calendar current = new GregorianCalendar();
		if (getParaValue("beginhour") == null) {
			Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			request.setAttribute("beginhour", new Integer(hour.intValue() - 1));
			request.setAttribute("endhour", hour);
		}
		if (getParaValue("begindate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String begindate = "";
			begindate = timeFormatter.format(new java.util.Date());
			request.setAttribute("begindate", begindate);
			request.setAttribute("enddate", begindate);
		} else {
			String temp = getParaValue("begindate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("enddate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
		if (getParaValue("startdate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String startdate = "";
			startdate = timeFormatter.format(new java.util.Date());
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", startdate);
		} else {
			String temp = getParaValue("startdate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("todate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
	}
}
