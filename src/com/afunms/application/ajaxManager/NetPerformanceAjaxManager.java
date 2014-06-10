package com.afunms.application.ajaxManager;

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

import javax.servlet.http.HttpServletRequest;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CreateBarPic;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.TitleModel;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.FlashCollectEntity;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.MonitorNetDTO;
import com.afunms.topology.model.MonitorNodeDTO;

@SuppressWarnings("rawtypes")
public class NetPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
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
			Enumeration eventE = checkEventHashtable.keys();
			String key = (String) null;
			while (eventE.hasMoreElements()) {
				try {
					key = (String) eventE.nextElement();
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

	private void getNetNodeDataByType() {
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

				jsonString.append("\"vender\":\"");
				jsonString.append(convertOSType(hostNode.getOstype()));
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

	private void getNetInterfaceDetail() {
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
				jsonString.append(strs[3]);
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

	private void getNetArpDetail() {
		String ip = getParaValue("ip");
		Vector arpV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (null != ipAllData) {
			if (ipAllData.get("ipmac") != null) {
				try {
					arpV = (Vector) ipAllData.get("ipmac");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
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

	private void getNetRouteDetail() {
		String ip = getParaValue("ip");
		Hashtable<Long, String> typeHt = new Hashtable<Long, String>();
		Hashtable<Long, String> protoHt = new Hashtable<Long, String>();

		typeHt.put(new Long(3), "direct");
		typeHt.put(new Long(4), "indirect");

		protoHt.put(new Long(1), "other");
		protoHt.put(new Long(2), "local");
		protoHt.put(new Long(3), "netmgmt");
		protoHt.put(new Long(4), "icmp");
		protoHt.put(new Long(5), "egp");
		protoHt.put(new Long(6), "ggp");
		protoHt.put(new Long(7), "hello");
		protoHt.put(new Long(8), "rip");
		protoHt.put(new Long(9), "is-is");
		protoHt.put(new Long(10), "es-is");
		protoHt.put(new Long(11), "ciscoIgrp");
		protoHt.put(new Long(12), "bbnSpfIgp");
		protoHt.put(new Long(13), "ospf");
		protoHt.put(new Long(14), "bgp");

		Vector routerVector = new Vector();
		Hashtable ipRouterHash = ShareData.getIprouterdata();
		if (null != ipRouterHash.get(ip)) {
			routerVector = (Vector) ipRouterHash.get(ip);
		}
		IpRouter iprouter = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != routerVector && routerVector.size() > 0) {
			Enumeration e = routerVector.elements();
			while (e.hasMoreElements()) {
				iprouter = (IpRouter) e.nextElement();
				jsonString.append("{\"ipRouteIfIndex\":\"");
				jsonString.append(iprouter.getIfindex());
				jsonString.append("\",");

				jsonString.append("\"ipRouteDest\":\"");
				jsonString.append(iprouter.getDest());
				jsonString.append("\",");

				jsonString.append("\"ipRouteNextHop\":\"");
				jsonString.append(iprouter.getNexthop());
				jsonString.append("\",");

				jsonString.append("\"ipRouteType\":\"");
				jsonString.append(typeHt.get(iprouter.getType()));
				jsonString.append("\",");

				jsonString.append("\"ipRouteProto\":\"");
				jsonString.append(protoHt.get(iprouter.getProto()));
				jsonString.append("\",");

				jsonString.append("\"ipRouteMask\":\"");
				jsonString.append(iprouter.getMask());
				jsonString.append("\",");

				jsonString.append("\"cTime\":\"");
				jsonString.append(sdf.format(iprouter.getCollecttime().getTime()));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + routerVector.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getNetIpListDetail() {
		String ip = getParaValue("ip");
		Hashtable<String, String> ifTypeHt = new Hashtable<String, String>();

		
		
		ifTypeHt.put("1", "other(1)");
		ifTypeHt.put("5", "others(5)");//rfc877x25
		ifTypeHt.put("6", "ethernetCsmacd(6)");
		ifTypeHt.put("23", "ppp(23)");
		ifTypeHt.put("28", "slip(28)");
		ifTypeHt.put("33", "Console port");//rs232
		ifTypeHt.put("53", "propVirtual(53)");
		ifTypeHt.put("54", "others(54)");//propMultiplexor
		ifTypeHt.put("117", "gigabitEthernet(117)");
		ifTypeHt.put("125", "fast");
		ifTypeHt.put("126", "ip");
		ifTypeHt.put("131", "tunnel(131)");
		ifTypeHt.put("135", "l2vlan");
		ifTypeHt.put("136", "l3ipvlan");
		ifTypeHt.put("142", "others(142)");//ipForward

		IpAliasDao ipAliasDao = new IpAliasDao();
		List netIpList = new ArrayList();
		try {
			netIpList = ipAliasDao.loadByIpaddress(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ipAliasDao.close();
		}
		IpAlias vo = null;
		String ifType = "other";
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != netIpList && netIpList.size() > 0) {
			for (int i = 0; i < netIpList.size(); i++) {
				vo = (IpAlias) netIpList.get(i);
				ifType = ifTypeHt.get(vo.getTypes());
				jsonString.append("{\"ifIndex\":\"");
				jsonString.append(vo.getIndexs());
				jsonString.append("\",");

				jsonString.append("\"ifDescr\":\"");
				jsonString.append(vo.getDescr());
				jsonString.append("\",");

				jsonString.append("\"ifSpeed\":\"");
				jsonString.append(vo.getSpeed());
				jsonString.append("\",");

				jsonString.append("\"ipAdEntAddr\":\"");
				jsonString.append(vo.getAliasip());
				jsonString.append("\",");

				jsonString.append("\"ifType\":\"");
				jsonString.append(ifType);
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + netIpList.size() + "}");
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

		Hashtable cpuHt = new Hashtable();
		Hashtable pingHt = new Hashtable();
		Hashtable responseHt = new Hashtable();
		Vector allUtilhdxVector = new Vector();

		I_HostLastCollectData hostLastCollectDataManager = new HostLastCollectDataManager();
		I_HostCollectData hostCollectDataManager = new HostCollectDataManager();
		try {
			cpuHt = hostCollectDataManager.getCategory(ip, "CPU", "Utilization", startTime, toTime);
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

		// 平均和最大流速
		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
		Hashtable allFluxAvgAndMaxHt = new Hashtable();
		try {
			allFluxAvgAndMaxHt = daymanager.getAllAvgAndMaxHisHdx(ip, startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 当前流速
		try {
			allUtilhdxVector = (Vector) hostLastCollectDataManager.getAllUtilHdxInterface(ip, startTime, toTime);
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

		String currentInFlux = "0";
		String currentOutFlux = "0";
		String avgtInFlux = "0";
		String avgOutFlux = "0";
		String maxInFlux = "0";
		String maxOutFlux = "0";
		String minInFlux = "0";
		String minOutFlux = "0";

		Vector cpuV = new Vector();
		Vector memoryV = new Vector();
		Vector flashV = new Vector();
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
				}

				if (null != ipAllData.get("flash")) {
					flashV = (Vector) ipAllData.get("flash");
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

		if (null != allUtilhdxVector && allUtilhdxVector.size() > 0) {
			for (int i = 0; i < allUtilhdxVector.size(); i++) {
				AllUtilHdx allutilhdx = (AllUtilHdx) allUtilhdxVector.get(i);
				if ("AllInBandwidthUtilHdx".equals(allutilhdx.getSubentity())) {
					currentInFlux = allutilhdx.getThevalue();
				}
				if ("AllOutBandwidthUtilHdx".equals(allutilhdx.getSubentity())) {
					currentOutFlux = allutilhdx.getThevalue();
				}
			}
		}
		if (allFluxAvgAndMaxHt != null && allFluxAvgAndMaxHt.size() > 0) {
			if (null != allFluxAvgAndMaxHt.get("agvin")) {
				avgtInFlux = String.valueOf(allFluxAvgAndMaxHt.get("agvin"));
			}
			if (null != allFluxAvgAndMaxHt.get("maxin")) {
				maxInFlux = String.valueOf(allFluxAvgAndMaxHt.get("maxin"));
			}
			if (null != allFluxAvgAndMaxHt.get("minin")) {
				minInFlux = String.valueOf(allFluxAvgAndMaxHt.get("minin"));
			}
			if (null != allFluxAvgAndMaxHt.get("agvout")) {
				avgOutFlux = String.valueOf(allFluxAvgAndMaxHt.get("agvout"));
			}
			if (null != allFluxAvgAndMaxHt.get("maxout")) {
				maxOutFlux = String.valueOf(allFluxAvgAndMaxHt.get("maxout"));
			}
			if (null != allFluxAvgAndMaxHt.get("minout")) {
				minOutFlux = String.valueOf(allFluxAvgAndMaxHt.get("minout"));
			}
		}
		// 画图、组织字符串

		// CPU
		CreateMetersPic cmp = new CreateMetersPic();
		cmp.createCpuPic(ip, Integer.parseInt(cpuPercent));
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
		StringBuffer memorySB = new StringBuffer("0");
		String[] colorStr = new String[] { "#33CCFF", "#003366", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266", "#33CCFF", "#003366", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266", "#33CCFF", "#003366", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266" };
		if (memoryV != null && memoryV.size() > 0) {
			MemoryCollectEntity memoryCollectEntity = null;
			memorySB.append("<chart><series>");
			for (int i = 0; i < memoryV.size(); i++) {
				memoryCollectEntity = (MemoryCollectEntity) memoryV.get(i);
				memorySB.append("<value xid='").append(i).append("'>").append("内存模块" + memoryCollectEntity.getSubentity()).append("</value>");
			}
			memorySB.append("</series><graphs><graph gid='0'>");
			for (int i = 0; i < memoryV.size(); i++) {
				memoryCollectEntity = (MemoryCollectEntity) memoryV.get(i);
				memorySB.append("<value xid='").append(i).append("' color='").append(colorStr[i]).append("'>" + memoryCollectEntity.getThevalue().replace("%", "")).append("</value>");
			}
			memorySB.append("</graph></graphs></chart>");
		}
		memoryString = memorySB.toString();

		// 闪存字符串
		String flashString = null;
		StringBuffer flashSB = new StringBuffer("0");
		if (flashV != null && flashV.size() > 0) {
			FlashCollectEntity flashCollectEntity = null;
			flashSB.append("<chart><series>");
			for (int i = 0; i < flashV.size(); i++) {
				flashCollectEntity = (FlashCollectEntity) flashV.get(i);
				flashSB.append("<value xid='").append(i).append("'>").append("闪存模块" + flashCollectEntity.getSubentity()).append("</value>");
			}
			flashSB.append("</series><graphs><graph gid='0'>");
			for (int i = 0; i < flashV.size(); i++) {
				flashCollectEntity = (FlashCollectEntity) flashV.get(i);
				flashSB.append("<value xid='").append(i).append("' color='").append(colorStr[i]).append("'>" + flashCollectEntity.getThevalue().replace("%", "")).append("</value>");
			}
			flashSB.append("</graph></graphs></chart>");
		}
		flashString = flashSB.toString();

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

		String fluxString = null;
		String[] labels1 = { "入口流速", "出口流速" };// 数据的种类
		double[] data = { Double.valueOf(currentInFlux), Double.valueOf(currentOutFlux), Double.valueOf(maxInFlux), Double.valueOf(maxOutFlux), Double.valueOf(minInFlux), Double.valueOf(minOutFlux), Double.valueOf(avgtInFlux), Double.valueOf(avgOutFlux) };

		int tempInt = 0;
		StringBuffer xmlStr = new StringBuffer("0");
		xmlStr.append("<chart><series>");
		xmlStr.append("<value xid='0'>").append(labels1[0]).append("</value><value xid='1'>").append(labels1[1]).append("</value>");
		xmlStr.append("</series><graphs>");
		for (int i = 0; i < 4; i++) {
			xmlStr.append("<graph gid='").append(i).append("'><value xid='0'>" + data[tempInt] + "</value>");
			xmlStr.append("<value xid='1'>" + data[++tempInt] + "</value>");
			xmlStr.append("</graph>");
			++tempInt;
		}
		xmlStr.append("</graphs></chart>");
		fluxString = xmlStr.toString();

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

		jsonString.append("\"memoryString\":\"");
		jsonString.append(memoryString);
		jsonString.append("\",");

		jsonString.append("\"fluxString\":\"");
		jsonString.append(fluxString);
		jsonString.append("\",");

		jsonString.append("\"flashString\":\"");
		jsonString.append(flashString);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getNetNodeConfig() {
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
			if (null != avg && avg.size() > 0) {
				Enumeration e = avg.elements();
				float all = 0.0f;
				while (e.hasMoreElements()) {
					all = all + Float.parseFloat(((String) e.nextElement()).replace("%", ""));
				}
				physicalMemoryAvg = String.valueOf(all / avg.size());
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

		Vector systemV = new Vector();
		String sysUpTime = null;
		if (null != ipAllData) {
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

		String keyPortString = "";
		String fluxString = "";
		String bandWidthString = "";

		Vector fluxVector = new Vector();
		Vector bandWidthVector = new Vector();
		StringBuffer fluxSB = new StringBuffer("0");
		StringBuffer bandWidthSB = new StringBuffer("0");
		String[] fluxColorStr = new String[] { "#D4B829", "#F57A29", "#B5DB2F", "#3189B5", "#AE3174", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266" };
		String[] bandWidthcolorStr = new String[] { "#0000FF", "#36DB43", "#3DA4D8", "#556B2F", "#8470F4", "#8A2BE2", "#23f266", "#F7FD31", "#8B4513", "FFD700" };
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] fluxItem = { "ifDescr", "InBandwidthUtilHdx" };
		String[] bandWidthItem = { "ifDescr", "InBandwidthUtilHdxPerc" };
		try {
			fluxVector = hostlastmanager.getInterface_share(ip, fluxItem, "InBandwidthUtilHdx", startTime, toTime);
			bandWidthVector = hostlastmanager.getInterface_share(ip, bandWidthItem, "InBandwidthUtilHdxPerc", startTime, toTime);
			if (fluxVector != null && fluxVector.size() > 0) {
				fluxSB.append("<chart><series>");
				for (int i = 0; i < 5 && i < fluxVector.size(); i++) {
					String[] fluxArray = (String[]) fluxVector.get(i);
					String ifName = fluxArray[0];
					fluxSB.append("<value xid='" + i);
					fluxSB.append("'>");
					fluxSB.append(ifName + "</value>");
				}
				fluxSB.append("</series><graphs><graph>");
				String fluxSpeed = "";
				for (int i = 0; i < 5 && i < fluxVector.size(); i++) {
					String[] strs = (String[]) fluxVector.get(i);
					fluxSpeed = strs[1].replaceAll("kb/s", "").replaceAll("kb/秒", "");
					fluxSB.append("<value xid='" + i).append("' color='").append(fluxColorStr[i]);
					fluxSB.append("'>");
					fluxSB.append(fluxSpeed + "</value>");
				}
				fluxSB.append("</graph></graphs></chart>");
			}
			fluxString = fluxSB.toString();

			if (bandWidthVector != null && bandWidthVector.size() > 0) {
				bandWidthSB.append("<chart><series>");
				for (int i = 0; i < 5 && i < bandWidthVector.size(); i++) {
					String[] bandWidthArray = (String[]) bandWidthVector.get(i);
					String ifName = bandWidthArray[0];
					bandWidthSB.append("<value xid='" + i);
					bandWidthSB.append("'>");
					bandWidthSB.append(ifName + "</value>");
				}
				bandWidthSB.append("</series><graphs><graph>");
				String bandWidthPercent = "";
				for (int i = 0; i < 5 && i < bandWidthVector.size(); i++) {
					String[] bandWidthArray = (String[]) bandWidthVector.get(i);
					bandWidthPercent = bandWidthArray[1].replaceAll("%", "");
					bandWidthSB.append("<value xid='" + i).append("' color='").append(bandWidthcolorStr[i]);
					bandWidthSB.append("'>");
					bandWidthSB.append(bandWidthPercent + "</value>");
				}
				bandWidthSB.append("</graph></graphs></chart>");
			}
			bandWidthString = bandWidthSB.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Vector portVec = new Vector();
		PortScanDao portScanDao = new PortScanDao();
		try {
			portVec = portScanDao.getCurrentStatus(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portScanDao.close();
		}
		String[] keyPortColors = { "#3DA4D8", "#FF0000" };
		StringBuffer xmlStr = new StringBuffer("0");
		if (portVec != null && portVec.size() > 0) {
			xmlStr.append("<chart><series>");
			for (int i = 0; i < portVec.size(); i++) {
				String[] strs = (String[]) portVec.get(i);
				xmlStr.append("<value xid='" + i);
				xmlStr.append("'>");
				xmlStr.append(strs[0] + "</value>");
			}
			String value = "";
			xmlStr.append("</series><graphs><graph>");
			for (int i = 0; i < portVec.size(); i++) {
				String[] strs = (String[]) portVec.get(i);
				value = strs[1];
				xmlStr.append("<value xid='" + i).append("' color='");
				if (value.equals("up")) {
					xmlStr.append(keyPortColors[0]).append("'>");
					xmlStr.append("1</value>");
				} else {
					xmlStr.append(keyPortColors[1]).append("'>");
					xmlStr.append("0</value>");
				}
			}
			xmlStr.append("</series><graphs><graph>");
		}
		keyPortString = xmlStr.toString();

		HostNode node = null;
		NodeDTO nodeDTO = null;
		NodeUtil util = new NodeUtil();
		HostNodeDao hostNodeDao = new HostNodeDao();
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		try {
			node = hostNodeDao.findByIpaddress(ip);
			if (null != node) {
				nodeDTO = util.creatNodeDTOByNode(node);
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
				jsonString.append(node.getSysDescr().replace("\r\n", " "));
				jsonString.append("\",");

				jsonString.append("\"location\":\"");
				jsonString.append(node.getSysLocation());
				jsonString.append("\",");

				jsonString.append("\"mac\":\"");
				jsonString.append(node.getBridgeAddress());
				jsonString.append("\",");

				jsonString.append("\"oid\":\"");
				jsonString.append(node.getSysOid());
				jsonString.append("\",");

				jsonString.append("\"keyPortString\":\"");
				jsonString.append(keyPortString);
				jsonString.append("\",");

				jsonString.append("\"fluxString\":\"");
				jsonString.append(fluxString);
				jsonString.append("\",");

				jsonString.append("\"bandWidthString\":\"");
				jsonString.append(bandWidthString);
				jsonString.append("\",");

				jsonString.append("\"responseTimeAvgInt\":\"");
				jsonString.append(responseTimeAvgInt);
				jsonString.append("\",");

				jsonString.append("\"ctTime\":\"");
				jsonString.append(ctTime);
				jsonString.append("\"}");

				jsonString.append("],total:1}");
				out.print(jsonString.toString());
				out.flush();
			} else {
				jsonString.append("],total:0}");
				out.print(jsonString.toString());
				out.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		System.out.println(jsonString.toString());
	}

	private void getSpeedAndBandWidthByType() {
		String ip = getParaValue("ip");
		String type = getParaValue("type");
		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTime = date + " 00:00:00";
		String toTime = date + " 23:59:59";
		String fluxString = "";
		String bandWidthString = "";

		Vector fluxVector = new Vector();
		Vector bandWidthVector = new Vector();
		StringBuffer fluxSB = new StringBuffer("0");
		StringBuffer bandWidthSB = new StringBuffer("0");
		String[] fluxColorStr = new String[] { "#D4B829", "#F57A29", "#B5DB2F", "#3189B5", "#AE3174", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266" };
		String[] bandWidthcolorStr = new String[] { "#0000FF", "#36DB43", "#3DA4D8", "#556B2F", "#8470F4", "#8A2BE2", "#23f266", "#F7FD31", "#8B4513", "FFD700" };
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] fluxItem = { "ifDescr", type + "BandwidthUtilHdx" };
		String[] bandWidthItem = { "ifDescr", type + "BandwidthUtilHdxPerc" };
		try {
			fluxVector = hostlastmanager.getInterface_share(ip, fluxItem, "InBandwidthUtilHdx", startTime, toTime);
			bandWidthVector = hostlastmanager.getInterface_share(ip, bandWidthItem, "InBandwidthUtilHdxPerc", startTime, toTime);
			if (fluxVector != null && fluxVector.size() > 0) {
				fluxSB.append("<chart><series>");
				for (int i = 0; i < 5 && i < fluxVector.size(); i++) {
					String[] fluxArray = (String[]) fluxVector.get(i);
					String ifName = fluxArray[0];
					fluxSB.append("<value xid='" + i);
					fluxSB.append("'>");
					fluxSB.append(ifName + "</value>");
				}
				fluxSB.append("</series><graphs><graph>");
				String fluxSpeed = "";
				for (int i = 0; i < 5 && i < fluxVector.size(); i++) {
					String[] strs = (String[]) fluxVector.get(i);
					fluxSpeed = strs[1].replaceAll("kb/s", "").replaceAll("kb/秒", "");
					fluxSB.append("<value xid='" + i).append("' color='").append(fluxColorStr[i]);
					fluxSB.append("'>");
					fluxSB.append(fluxSpeed + "</value>");
				}
				fluxSB.append("</graph></graphs></chart>");
			}
			fluxString = fluxSB.toString();

			if (bandWidthVector != null && bandWidthVector.size() > 0) {
				bandWidthSB.append("<chart><series>");
				for (int i = 0; i < 5 && i < bandWidthVector.size(); i++) {
					String[] bandWidthArray = (String[]) bandWidthVector.get(i);
					String ifName = bandWidthArray[0];
					bandWidthSB.append("<value xid='" + i);
					bandWidthSB.append("'>");
					bandWidthSB.append(ifName + "</value>");
				}
				bandWidthSB.append("</series><graphs><graph>");
				String bandWidthPercent = "";
				for (int i = 0; i < 5 && i < bandWidthVector.size(); i++) {
					String[] bandWidthArray = (String[]) bandWidthVector.get(i);
					bandWidthPercent = bandWidthArray[1].replaceAll("%", "");
					bandWidthSB.append("<value xid='" + i).append("' color='").append(bandWidthcolorStr[i]);
					bandWidthSB.append("'>");
					bandWidthSB.append(bandWidthPercent + "</value>");
				}
				bandWidthSB.append("</graph></graphs></chart>");
			}
			bandWidthString = bandWidthSB.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"fluxString\":\"");
		jsonString.append(fluxString);
		jsonString.append("\",");

		jsonString.append("\"bandWidthString\":\"");
		jsonString.append(bandWidthString);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();

	}

	private void getNetSyslogDetail() {
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

	public void execute(String action) {
		if (action.equals("getNetNodeDataByType")) {
			getNetNodeDataByType();
		} else if (action.equals("getNetInterfaceDetail")) {
			getNetInterfaceDetail();
		} else if (action.equals("getNetNodeConfig")) {
			getNetNodeConfig();
		} else if (action.equals("getNetArpDetail")) {
			getNetArpDetail();
		} else if (action.equals("getNetRouteDetail")) {
			getNetRouteDetail();
		} else if (action.equals("getNetIpListDetail")) {
			getNetIpListDetail();
		} else if (action.equals("getAllPerfData")) {
			getAllPerfData();
		} else if (action.equals("getSpeedAndBandWidthByType")) {
			getSpeedAndBandWidthByType();
		} else if (action.equals("getNetSyslogDetail")) {
			getNetSyslogDetail();
		} else if (action.equals("getNetEventDetail")) {
			getNetEventDetail();
		}

	}

	private void getNetEventDetail() {

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

	private String removeUnit(String str, int l) {
		if (str != null && str.trim().length() > 0) {
			return str.substring(0, str.length() - l);
		} else {
			return null;
		}
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

	private String convertOSType(int i) {
		String rt = null;
		if (i == 1) {
			rt = "思科";
		} else if (i == 2) {
			rt = "H3C";
		} else if (i == 38) {
			rt = "天融信";
		}
		return rt;
	}

}
