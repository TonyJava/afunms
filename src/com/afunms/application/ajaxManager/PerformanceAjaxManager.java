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
import com.afunms.alarm.dao.AlarmPortDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateAmColumnPic;
import com.afunms.common.util.CreateBarPic;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.NodeAlarmUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.TitleModel;
import com.afunms.common.util.UserAuditUtil;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.NetNodeCfgFileDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.PortConfigCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
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
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.dao.IpMacChangeDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.NodeEquipDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.dao.RelationDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.dao.RemotePingNodeDao;
import com.afunms.topology.dao.RepairLinkDao;
import com.afunms.topology.dao.VMWareConnectConfigDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.MonitorNetDTO;
import com.afunms.topology.model.MonitorNodeDTO;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.Relation;
import com.afunms.topology.util.XmlOperator;

@SuppressWarnings("rawtypes")
public class PerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// ��ȡָ���û�ҵ��SQL
	private String getBidSql() {
		User currentUser = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer bidSQL = new StringBuffer();
		// ƴ�ӱ�־
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
			// ��������Ա
			return "";
		} else {
			return bidSQL.toString();
		}
	}

	// �������ͻ�ȡ��Ԫ
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
			monitorNodeDTO.setCategory("·����");
		} else if (category == 2 || category == 3) {
			monitorNodeDTO.setCategory("������");
		} else if (category == 4) {
			monitorNodeDTO.setCategory("������");
		} else if (category == 7) {
			monitorNodeDTO.setCategory("����·����");
		} else if (category == 8) {
			monitorNodeDTO.setCategory("����ǽ");
		} else if (category == 9) {
			monitorNodeDTO.setCategory("ATM");
		} else if (category == 10) {
			monitorNodeDTO.setCategory("�ʼ���ȫ����");
		} else if (category == 11) {
			monitorNodeDTO.setCategory("F5");
		} else if (category == 12) {
			monitorNodeDTO.setCategory("VPN");
		} else if (category == 13) {
			monitorNodeDTO.setCategory("CMTS");
		} else if (category == 14) {
			monitorNodeDTO.setCategory("�洢");
		} else if (category == 15) {
			monitorNodeDTO.setCategory("���⻯");
		}

		Host node = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		int alarmLevel = 0;
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.creatNodeDTOByHost(node);
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
		// �澯״̬
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
			// �ɼ�������Ǽ���ģʽ
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
									// linux��aix
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

		// ���òɼ���ʽ
		if (SystemConstant.COLLECTTYPE_SNMP == hostNode.getCollecttype()) {
			collectType = "snmp";
		} else if (SystemConstant.COLLECTTYPE_PING == hostNode.getCollecttype()) {
			collectType = "ping";
		} else if (SystemConstant.COLLECTTYPE_REMOTEPING == hostNode.getCollecttype()) {
			collectType = "REMOTEPING";
		} else if (SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
			collectType = "����";
		} else if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype()) {
			collectType = "ssh";
		} else if (SystemConstant.COLLECTTYPE_TELNET == hostNode.getCollecttype()) {
			collectType = "telnet";
		} else if (SystemConstant.COLLECTTYPE_WMI == hostNode.getCollecttype()) {
			collectType = "wmi";
		} else if (SystemConstant.COLLECTTYPE_DATAINTERFACE == hostNode.getCollecttype()) {
			collectType = "�ӿ�";
		}

		// �趨cpu���ڴ�ĸ澯��ɫ
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
	}

	private void getHostNodeDataByType() {
		String type = getParaValue("type");

		List nodeList = new ArrayList();
		nodeList = getNodeListByCategory(type);

		// ȡ���ӿ�����
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
				if (null != hostInterfaceHt.get(monitorNodeDTO.getId() + "")) {
					monitorNodeDTO.setEntityNumber(Integer.parseInt(String.valueOf(hostInterfaceHt.get(monitorNodeDTO.getId() + ""))));
				}
				jsonString.append("{nodeId:'");
				jsonString.append(monitorNodeDTO.getId());
				jsonString.append("',");

				jsonString.append("type:'");
				jsonString.append(monitorNodeDTO.getType());
				jsonString.append("',");

				jsonString.append("alias:'");
				jsonString.append(monitorNodeDTO.getAlias());
				jsonString.append("',");

				jsonString.append("ip:'");
				jsonString.append(monitorNodeDTO.getIpAddress());
				jsonString.append("',");

				jsonString.append("status:'");
				jsonString.append(monitorNodeDTO.getStatus());
				jsonString.append("',");

				jsonString.append("pingValue:'");
				jsonString.append(monitorNodeDTO.getPingValue());
				jsonString.append("',");

				jsonString.append("cpuValue:'");
				jsonString.append(monitorNodeDTO.getCpuValue());
				jsonString.append("',");

				jsonString.append("cpuColor:'");
				jsonString.append(monitorNodeDTO.getCpuValueColor());
				jsonString.append("',");

				jsonString.append("physicalMemoryValue:'");
				jsonString.append(monitorNodeDTO.getMemoryValue());
				jsonString.append("',");

				jsonString.append("physicalMemoryColor:'");
				jsonString.append(monitorNodeDTO.getMemoryValueColor());
				jsonString.append("',");

				jsonString.append("virtualMemoryValue:'");
				jsonString.append(monitorNodeDTO.getVirtualMemoryValue());
				jsonString.append("',");

				jsonString.append("virtualMemoryColor:'");
				jsonString.append(monitorNodeDTO.getVirtualMemoryValueColor());
				jsonString.append("',");

				jsonString.append("inUtilHdx:'");
				jsonString.append(monitorNodeDTO.getInutilhdxValue());
				jsonString.append("',");

				jsonString.append("outUtilHdx:'");
				jsonString.append(monitorNodeDTO.getOututilhdxValue());
				jsonString.append("',");

				jsonString.append("cType:'");
				jsonString.append(monitorNodeDTO.getCollectType());
				jsonString.append("',");

				jsonString.append("ifNumber:'");
				jsonString.append(monitorNodeDTO.getEntityNumber());
				jsonString.append("',");

				jsonString.append("isM:'");
				jsonString.append(hostNode.isManaged());
				jsonString.append("'}");

				if (i != nodeList.size() - 1) {
					jsonString.append(",");
				}

			}
		}
		jsonString.append("],total:" + nodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}
	
	private void getNetNodeDataByType() {
		String type = getParaValue("type");

		List nodeList = new ArrayList();
		nodeList = getNodeListByCategory(type);

		// ȡ���ӿ�����
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
				if (null != hostInterfaceHt.get(monitorNodeDTO.getId() + "")) {
					monitorNodeDTO.setEntityNumber(Integer.parseInt(String.valueOf(hostInterfaceHt.get(monitorNodeDTO.getId() + ""))));
				}
				jsonString.append("{nodeId:'");
				jsonString.append(monitorNodeDTO.getId());
				jsonString.append("',");

				jsonString.append("type:'");
				jsonString.append(monitorNodeDTO.getType());
				jsonString.append("',");

				jsonString.append("alias:'");
				jsonString.append(monitorNodeDTO.getAlias());
				jsonString.append("',");

				jsonString.append("ip:'");
				jsonString.append(monitorNodeDTO.getIpAddress());
				jsonString.append("',");

				jsonString.append("status:'");
				jsonString.append(monitorNodeDTO.getStatus());
				jsonString.append("',");

				jsonString.append("pingValue:'");
				jsonString.append(monitorNodeDTO.getPingValue());
				jsonString.append("',");

				jsonString.append("cpuValue:'");
				jsonString.append(monitorNodeDTO.getCpuValue());
				jsonString.append("',");

				jsonString.append("cpuColor:'");
				jsonString.append(monitorNodeDTO.getCpuValueColor());
				jsonString.append("',");

				jsonString.append("physicalMemoryValue:'");
				jsonString.append(monitorNodeDTO.getMemoryValue());
				jsonString.append("',");

				jsonString.append("physicalMemoryColor:'");
				jsonString.append(monitorNodeDTO.getMemoryValueColor());
				jsonString.append("',");

				jsonString.append("inUtilHdx:'");
				jsonString.append(monitorNodeDTO.getInutilhdxValue());
				jsonString.append("',");

				jsonString.append("outUtilHdx:'");
				jsonString.append(monitorNodeDTO.getOututilhdxValue());
				jsonString.append("',");

				jsonString.append("cType:'");
				jsonString.append(monitorNodeDTO.getCollectType());
				jsonString.append("',");

				jsonString.append("ifNumber:'");
				jsonString.append(monitorNodeDTO.getEntityNumber());
				jsonString.append("',");

				jsonString.append("isM:'");
				jsonString.append(hostNode.isManaged());
				jsonString.append("'}");

				if (i != nodeList.size() - 1) {
					jsonString.append(",");
				}

			}
		}
		jsonString.append("],total:" + nodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings("unchecked")
	private void deleteNodes() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}

		CreateTableManager createTableManager = new CreateTableManager();
		if (ids != null && ids.length > 0) {
			// �����޸�
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				// ȡ���ɼ�����
				PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
				HostNodeDao dao = new HostNodeDao();
				HostNode host = null;
				try {
					host = (HostNode) dao.findByID(id);
					dao.delete(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
				LinkDao linkdao = new LinkDao();
				List linklist = new ArrayList();
				Link link = null;
				try {
					linklist = linkdao.findByNodeId(host.getId() + "");
					if (linklist != null && linklist.size() > 0) {
						link = (Link) linklist.get(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					linkdao.close();
				}

				// ˢ���ڴ��вɼ�ָ��
				NodeGatherIndicatorsUtil gatherutil = new NodeGatherIndicatorsUtil();
				gatherutil.refreshShareDataGather();

				String ip = host.getIpAddress();
				String allipstr = SysUtil.doip(ip);
				CreateTableManager ctable = new CreateTableManager();
				try {
					if (host.getCategory() < 4 || host.getCategory() == 7 || host.getCategory() == 8 || host.getCategory() == 9 || host.getCategory() == 10 || host.getCategory() == 11
							|| host.getCategory() == 12 || host.getCategory() == 13 || host.getCategory() == 14 || host.getCategory() == 15 || host.getCategory() == 16 || host.getCategory() == 17) {
						// ��ɾ�������豸��
						// ��ͨ��
						try {
							ctable.deleteTable("ping", allipstr, "ping");// Ping
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("pinghour", allipstr, "pinghour");// Ping
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("pingday", allipstr, "pingday");// Ping
						} catch (Exception e) {
							e.printStackTrace();
						}

						// �ڴ�
						try {
							ctable.deleteTable("memory", allipstr, "mem");// �ڴ�
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryhour", allipstr, "memhour");// �ڴ�
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryday", allipstr, "memday");// �ڴ�
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("portstatus", allipstr, "port");// �˿�״̬
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (host.getCategory() != 12) {
							// VPN����ʱ��û�����±���Ϣ
							ctable.deleteTable("flash", allipstr, "flash");// ����
							ctable.deleteTable("flashhour", allipstr, "flashhour");// ����
							ctable.deleteTable("flashday", allipstr, "flashday");// ����

							ctable.deleteTable("buffer", allipstr, "buffer");// ����
							ctable.deleteTable("bufferhour", allipstr, "bufferhour");// ����
							ctable.deleteTable("bufferday", allipstr, "bufferday");// ����

							ctable.deleteTable("fan", allipstr, "fan");// ����
							ctable.deleteTable("fanhour", allipstr, "fanhour");// ����
							ctable.deleteTable("fanday", allipstr, "fanday");// ����

							ctable.deleteTable("power", allipstr, "power");// ��Դ
							ctable.deleteTable("powerhour", allipstr, "powerhour");// ��Դ
							ctable.deleteTable("powerday", allipstr, "powerday");// ��Դ

							ctable.deleteTable("vol", allipstr, "vol");// ��ѹ
							ctable.deleteTable("volhour", allipstr, "volhour");// ��ѹ
							ctable.deleteTable("volday", allipstr, "volday");// ��ѹ
						}
						if (host.getCategory() == 13) {
							// CMTS�豸
							ctable.deleteTable("status", allipstr, "status");// ͨ��״̬
							ctable.deleteTable("statushour", allipstr, "statushour");// ͨ��״̬
							ctable.deleteTable("statusday", allipstr, "statusday");// ͨ��״̬

							ctable.deleteTable("noise", allipstr, "noise");// ͨ�������
							ctable.deleteTable("noisehour", allipstr, "noisehour");// ͨ�������
							ctable.deleteTable("noiseday", allipstr, "noiseday");// ͨ�������

							ctable.deleteTable("ipmac", allipstr, "ipmac");// IPMAC��Ϣ�������û���Ϣ��
						} else if (host.getCategory() == 14) {
							// �洢�豸��
							ctable.deleteTable("pings", allipstr, "pings");// ��ͨ��
							ctable.deleteTable("pinghours", allipstr, "pinghours");// ��ͨ��
							ctable.deleteTable("pingdays", allipstr, "pingdays");// ��ͨ��

							ctable.deleteTable("env", allipstr, "env");//
							ctable.deleteTable("efan", allipstr, "efan");//
							ctable.deleteTable("epower", allipstr, "epower");//
							ctable.deleteTable("eenv", allipstr, "eenv");//
							ctable.deleteTable("edrive", allipstr, "edrive");//

							ctable.deleteTable("rcpu", allipstr, "rcpu");
							ctable.deleteTable("rcable", allipstr, "rcable");// ����״�壺�ڲ�����״̬
							ctable.deleteTable("rcache", allipstr, "rcache");// ����״�壺����״̬
							ctable.deleteTable("rmemory", allipstr, "rmemory");// ����״�壺�����ڴ�״̬
							ctable.deleteTable("rpower", allipstr, "rpower");// ����״�壺��Դ״̬
							ctable.deleteTable("rbutter", allipstr, "rbutter");// ����״�壺���״̬
							ctable.deleteTable("rfan", allipstr, "rfan");// ����״�壺����״̬
							ctable.deleteTable("renv", allipstr, "renv");// �洢�豸����-����״̬

							ctable.deleteTable("rluncon", allipstr, "rluncon");
							ctable.deleteTable("rsluncon", allipstr, "rsluncon");
							ctable.deleteTable("rwwncon", allipstr, "rwwncon");
							ctable.deleteTable("rsafety", allipstr, "rsafety");
							ctable.deleteTable("rnumber", allipstr, "rnumber");
							ctable.deleteTable("rswitch", allipstr, "rswitch");

							ctable.deleteTable("events", allipstr, "events");// �¼�

							ctable.deleteTable("emcdiskper", allipstr, "emcdiskper");
							ctable.deleteTable("emclunper", allipstr, "emclunper");
							ctable.deleteTable("emcenvpower", allipstr, "emcenvpower");
							ctable.deleteTable("emcenvstore", allipstr, "emcenvstore");
							ctable.deleteTable("emcbakpower", allipstr, "emcbakpower");
							if (host.getOstype() == 44) {// hp�洢ɾ��ping�� ��
								// ��������
								ctable.deleteTable("ping", allipstr, "ping");// ��ͨ��
								ctable.deleteTable("pinghour", allipstr, "pinghour");// ��ͨ��
								ctable.deleteTable("pingday", allipstr, "pingday");// ��ͨ��
							}

						} else if (host.getCategory() == 15) {
							// VMWare�豸��
							ctable.deleteTable("pings", allipstr, "pings");// Ping
							ctable.deleteTable("pinghours", allipstr, "pinghours");// Ping
							ctable.deleteTable("pingdays", allipstr, "pingdays");// Ping

							ctable.deleteTable("memory", allipstr, "memory");// �ڴ�������
							ctable.deleteTable("memoryhour", allipstr, "memhour");// �ڴ�
							ctable.deleteTable("memoryday", allipstr, "memday");// �ڴ�

							ctable.deleteTable("cpu", allipstr, "cpu");// CPU
							ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
							ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU

							ctable.deleteTable("state", allipstr, "state");// �������Դ״�����򿪻�رգ���
							ctable.deleteTable("gstate", allipstr, "gstate");// �ͻ�������ϵͳ��״��������أ���

							ctable.deleteTable("pings", allipstr, "pings");// ��ͨ��
							ctable.deleteTable("pinghours", allipstr, "pinghours");// ��ͨ��
							ctable.deleteTable("pingdays", allipstr, "pingdays");// ��ͨ��

							ctable.deleteTable("vm_host", allipstr, "vm_host");// ����VMWare
							// �������������Ϣ��
							ctable.deleteTable("vm_guesthost", allipstr, "vm_guesthost");// ����VMWare
							// �������������Ϣ��
							ctable.deleteTable("vm_cluster", allipstr, "vm_cluster");// ����VMWare
							// ��Ⱥ��������Ϣ��
							ctable.deleteTable("vm_datastore", allipstr, "vm_datastore");// ����VMWare
							// �洢��������Ϣ��
							ctable.deleteTable("vm_resourcepool", allipstr, "vm_resourcepool");// ����VMWare
							// ��Դ�ص�������Ϣ��
							// vm_basephysical
							ctable.deleteTable("vm_basephysical", allipstr, "vm_basephysical");// ����VMWare
							// ������Ļ�����Ϣ��
							ctable.deleteTable("vm_basevmware", allipstr, "vm_basevmware");// ����VMWare
							// ������Ļ�����Ϣ��
							ctable.deleteTable("vm_baseyun", allipstr, "vm_baseyun");// ����VMWare
							// ����Դ�Ļ�����Ϣ��
							ctable.deleteTable("vm_basedatastore", allipstr, "vm_basedatastore");// ����VMWare
							// �洢�Ļ�����Ϣ��
							ctable.deleteTable("vm_basedatacenter", allipstr, "vm_basedatacenter");// ����VMWare
							// �������ĵĻ�����Ϣ��
							ctable.deleteTable("vm_baseresource", allipstr, "vm_baseresource");// ����VMWare
							// ��Դ�صĻ�����Ϣ��
						} else if (host.getCategory() == 16) {// aircondition
							// ɾ���յ���ʱ���е�����
							String[] nmsTempDataTables = { "nms_emeairconhum", "nms_emeairconparinfo", "nms_emeaircontem" };
							String[] uniqueKeyValues = { host.getIpAddress() };
							createTableManager.clearTablesData(nmsTempDataTables, "ipaddress", uniqueKeyValues);
						} else if (host.getCategory() == 17) {// UPS
							ctable.deleteTable("input", allipstr, "input");
							ctable.deleteTable("inputhour", allipstr, "inputhour");
							ctable.deleteTable("inputday", allipstr, "inputday");
							ctable.deleteTable("output", allipstr, "output");
							ctable.deleteTable("outputhour", allipstr, "outputhour");
							ctable.deleteTable("outputday", allipstr, "outputday");
						}

						// CPU
						try {
							ctable.deleteTable("cpu", allipstr, "cpu");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ����������
						try {
							ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ����
						try {
							ctable.deleteTable("utilhdx", allipstr, "hdx");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("utilhdxday", allipstr, "hdxday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// �ۺ�����
						try {
							ctable.deleteTable("allutilhdx", allipstr, "allhdx");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("autilhdxd", allipstr, "ahdxd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ������
						try {
							ctable.deleteTable("discardsperc", allipstr, "dcardperc");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("dcarperh", allipstr, "dcarperh");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("dcarperd", allipstr, "dcarperd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ������
						try {
							ctable.deleteTable("errorsperc", allipstr, "errperc");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("errperch", allipstr, "errperch");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("errpercd", allipstr, "errpercd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ���ݰ�
						try {
							ctable.deleteTable("packs", allipstr, "packs");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("packshour", allipstr, "packshour");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("packsday", allipstr, "packsday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ������ݰ�
						try {
							ctable.deleteTable("inpacks", allipstr, "inpacks");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("ipacksh", allipstr, "ipacksh");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("ipackd", allipstr, "ipackd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// �������ݰ�
						try {
							ctable.deleteTable("outpacks", allipstr, "outpacks");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("opackh", allipstr, "opackh");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("opacksd", allipstr, "opacksd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// �¶�
						try {
							ctable.deleteTable("temper", allipstr, "temper");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("temperh", allipstr, "temperh");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("temperd", allipstr, "temperd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
						try {
							dcDao.deleteMonitor(host.getId(), host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dcDao.close();
						}

						EventListDao eventdao = new EventListDao();
						try {
							// ͬʱɾ���¼�������������
							if (host.getCategory() == 12) {
								eventdao.delete(host.getId(), "vpn");
							} else if (host.getCategory() == 13) {
								eventdao.delete(host.getId(), "cmts");
							} else if (host.getCategory() == 14) {
								eventdao.delete(host.getId(), "storage");
							} else if (host.getCategory() == 16) {
								eventdao.delete(host.getId(), "air");
							} else if (host.getCategory() == 17) {
								eventdao.delete(host.getId(), "ups");
							} else {
								eventdao.delete(host.getId(), "net");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}

						PortconfigDao portconfigdao = new PortconfigDao();
						try {
							// ͬʱɾ���˿����ñ�����������
							portconfigdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portconfigdao.close();
						}

						AlarmPortDao portdao = new AlarmPortDao();
						try {
							// ͬʱɾ���˿ڼ������ñ�����������
							portdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portdao.close();
						}

						// ɾ��nms_ipmacchange����Ķ�Ӧ������
						IpMacChangeDao macchangebasedao = new IpMacChangeDao();
						try {
							macchangebasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macchangebasedao.close();
						}

						// ɾ�������豸�����ļ�����Ķ�Ӧ������
						NetNodeCfgFileDao configdao = new NetNodeCfgFileDao();
						try {
							configdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							configdao.close();
						}

						// ɾ�������豸SYSLOG���ձ���Ķ�Ӧ������
						NetSyslogDao syslogdao = new NetSyslogDao();
						try {
							syslogdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							syslogdao.close();
						}

						// ɾ�������豸�˿�ɨ�����Ķ�Ӧ������
						PortScanDao portscandao = new PortScanDao();
						try {
							portscandao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portscandao.close();
						}

						// ɾ�������豸���ͼ����Ķ�Ӧ������
						IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
						try {
							addresspaneldao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							addresspaneldao.close();
						}

						// ɾ�������豸�ӿڱ���Ķ�Ӧ������
						HostInterfaceDao interfacedao = new HostInterfaceDao();
						try {
							interfacedao.deleteByHostId(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							interfacedao.close();
						}

						// ɾ�������豸IP��������Ķ�Ӧ������
						IpAliasDao ipaliasdao = new IpAliasDao();
						try {
							ipaliasdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipaliasdao.close();
						}

						// ɾ�������豸�ֹ����õ���·����Ķ�Ӧ������
						RepairLinkDao repairdao = new RepairLinkDao();
						try {
							repairdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							repairdao.close();
						}

						// ɾ�������豸IPMAC����Ķ�Ӧ������
						IpMacDao ipmacdao = new IpMacDao();
						try {
							ipmacdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipmacdao.close();
						}

						if (host.getCategory() == 15 && host.getOstype() == 40 && host.getCollecttype() == 10) {
							VMWareConnectConfigDao vmwaredao = new VMWareConnectConfigDao();
							try {
								vmwaredao.delete(Long.parseLong(host.getId() + ""));
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								vmwaredao.close();
							}
						}

						// ɾ�����豸�Ĳɼ�ָ��
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							if (host.getCategory() == 12) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "vpn", "");
							} else if (host.getCategory() == 13) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "cmts", "");
							} else if (host.getCategory() == 14) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "storage", "");
							} else if (host.getCategory() == 15) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "virtual", "");
							} else if (host.getCategory() == 16) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "air", "");
							} else if (host.getCategory() == 17) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "ups", "");
							} else {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "net", "");
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// ɾ�������豸ָ��ɼ�����Ķ�Ӧ������
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							if (host.getCategory() == 12) {
								indicatdao.deleteByNodeId(host.getId() + "", "vpn");
							} else if (host.getCategory() == 13) {
								indicatdao.deleteByNodeId(host.getId() + "", "cmts");
							} else if (host.getCategory() == 14) {
								indicatdao.deleteByNodeId(host.getId() + "", "storage");
							} else if (host.getCategory() == 15) {
								indicatdao.deleteByNodeId(host.getId() + "", "virtual");
							} else if (host.getCategory() == 16) {
								indicatdao.deleteByNodeId(host.getId() + "", "air");
							} else if (host.getCategory() == 17) {
								indicatdao.deleteByNodeId(host.getId() + "", "ups");
							} else {
								indicatdao.deleteByNodeId(host.getId() + "", "net");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						// ɾ��IP-MAC-BASE����Ķ�Ӧ������
						IpMacBaseDao macbasedao = new IpMacBaseDao();
						try {
							macbasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macbasedao.close();
						}
						// ɾ���豸��ǰ���¸澯��Ϣ���е�����
						NodeAlarmUtil.deleteByDeviceIdAndDeviceType(host);

						// ɾ��SYSLOG�����
						NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
						try {
							ruledao.deleteByNodeID(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ruledao.close();
						}
					} else if (host.getCategory() == 4) {
						// ɾ������������
						try {
							ctable.deleteTable("pro", allipstr, "pro");// ����
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("prohour", allipstr, "prohour");// ����Сʱ
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("proday", allipstr, "proday");// ������
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("log", allipstr, "log");// ������
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("memory", allipstr, "mem");// �ڴ�
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryhour", allipstr, "memhour");// �ڴ�
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryday", allipstr, "memday");// �ڴ�
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("cpu", allipstr, "cpu");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpudtl", allipstr, "cpudtl");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpudtlhour", allipstr, "cpudtlhour");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("cpudtlday", allipstr, "cpudtlday");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("disk", allipstr, "disk");// disk
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("diskhour", allipstr, "diskhour");// disk
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("diskday", allipstr, "diskday");// disk
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("diskincre", allipstr, "diskincre");// ��������
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("diskinch", allipstr, "diskinch");// ��������
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("diskincd", allipstr, "diskincd");// ��������
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("ping", allipstr, "ping");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("pinghour", allipstr, "pinghour");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("pingday", allipstr, "pingday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("utilhdx", allipstr, "hdx");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("utilhdxday", allipstr, "hdxday");
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("software", allipstr, "software");
						} catch (Exception e) {
							e.printStackTrace();
						}

						ctable.deleteTable("allutilhdx", allipstr, "allhdx");
						ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
						ctable.deleteTable("autilhdxd", allipstr, "ahdxd");

						ctable.deleteTable("discardsperc", allipstr, "dcardperc");
						ctable.deleteTable("dcarperh", allipstr, "dcarperh");
						ctable.deleteTable("dcarperd", allipstr, "dcarperd");

						ctable.deleteTable("errorsperc", allipstr, "errperc");
						ctable.deleteTable("errperch", allipstr, "errperch");
						ctable.deleteTable("errpercd", allipstr, "errpercd");

						ctable.deleteTable("packs", allipstr, "packs");
						ctable.deleteTable("packshour", allipstr, "packshour");
						ctable.deleteTable("packsday", allipstr, "packsday");

						ctable.deleteTable("inpacks", allipstr, "inpacks");
						ctable.deleteTable("ipacksh", allipstr, "ipacksh");
						ctable.deleteTable("ipackd", allipstr, "ipackd");

						ctable.deleteTable("outpacks", allipstr, "outpacks");
						ctable.deleteTable("opackh", allipstr, "opackh");
						ctable.deleteTable("opacksd", allipstr, "opacksd");
						if (host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.2.3.1.2.1.1")) {
							// ɾ����ҳ��
							try {
								ctable.deleteTable("pgused", allipstr, "pgused");
								ctable.deleteTable("pgusedhour", allipstr, "pgusedhour");
								ctable.deleteTable("pgusedday", allipstr, "pgusedday");
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

						// ɾ���ɼ�ָ��
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "host", "");
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// ɾ���澯ָ��
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							indicatdao.deleteByNodeId(host.getId() + "", "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						EventListDao eventdao = new EventListDao();
						try {
							// ͬʱɾ���¼�������������
							eventdao.delete(host.getId(), "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}
						// ɾ��diskconfig
						String[] otherTempData = new String[] { "nms_diskconfig" };
						String[] ipStrs = new String[] { host.getIpAddress() };
						ctable.clearTablesData(otherTempData, "ipaddress", ipStrs);
						// ɾ�������������
						ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
						processGroupConfigurationUtil.deleteProcessGroupAndConfigurationByNodeid(host.getId() + "");

						// ɾ��SYSLOG�����
						NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
						try {
							ruledao.deleteByNodeID(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ruledao.close();
						}

						// ɾ���豸��ǰ���¸澯��Ϣ���е�����
						NodeAlarmUtil.deleteByDeviceIdAndDeviceType(host);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// 2.����xml
				if (host.getCategory() < 4) {
					// �����豸
					ManageXmlDao mdao = new ManageXmlDao();
					List<ManageXml> list = mdao.loadAll();
					if (list.size() > 0) {
						for (int k = 0; k < list.size(); k++) {
							ManageXml manageXml = list.get(k);
							XmlOperator xopr = new XmlOperator();
							String xmlName = manageXml.getXmlName();
							String nodeid = "net" + host.getId();
							xopr.setFile(xmlName);
							xopr.init4updateXml();
							if (xopr.isNodeExist(nodeid)) {
								xopr.deleteNodeByID(nodeid);
							}
							xopr.writeXml();
							RelationDao rdao = new RelationDao();
							Relation vo = (Relation) rdao.findByNodeId(id, xmlName);
							if (vo != null) {
								rdao.deleteByNode(id, xmlName);
							} else {
								rdao.close();
							}
							// ɾ������ͼԪ�������
							NodeEquipDao nodeEquipDao = new NodeEquipDao();
							if (nodeEquipDao.findByNode(nodeid) != null) {
								nodeEquipDao.deleteByNode(nodeid);
							} else {
								nodeEquipDao.close();
							}
						}
					}
				} else if (host.getCategory() == 4) {
					// ����������
					try {
						XmlOperator opr = new XmlOperator();
						opr.setFile("server.jsp");
						opr.init4updateXml();
						opr.deleteNodeByID(host.getId() + "");
						opr.writeXml();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// ɾ��ָ��ȫ����ֵ���Ӧ������
				NodeMonitorDao nodeMonitorDao = new NodeMonitorDao();
				try {
					nodeMonitorDao.deleteByID(id);
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				} finally {
					nodeMonitorDao.close();
				}

				if (host.getOstype() == 15) {
					// as400
					CreateTableManager ctable2 = new CreateTableManager();
					try {
						ctable2.deleteTable("systemasp", allipstr, "systemasp");
						ctable2.deleteTable("dbcapability", allipstr, "dbcapability");
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}

				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				try {
					timeShareConfigUtil.deleteTimeShareConfig(id, timeShareConfigUtil.getObjectType("0"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ������ɾ��
				if (linklist != null && linklist.size() > 0) {
					for (int l = 0; l < linklist.size(); l++) {
						link = (Link) linklist.get(l);
						if (link != null) {
							LinkDao ldao = new LinkDao();
							try {
								ldao.delete(link.getId() + "");
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								ldao.close();
							}
						}
					}
				}

				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				try {
					timeGratherConfigUtil.deleteTimeGratherConfig(id, timeGratherConfigUtil.getObjectType("0"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ����ҵ����ͼ
				NodeDependDao nodedependao = new NodeDependDao();
				List list = nodedependao.findByNode("net" + id);
				if (list != null && list.size() > 0) {
					for (int j = 0; j < list.size(); j++) {
						NodeDepend vo = (NodeDepend) list.get(j);
						if (vo != null) {
							LineDao lineDao = new LineDao();
							lineDao.deleteByidXml("net" + id, vo.getXmlfile());
							NodeDependDao nodeDependDao = new NodeDependDao();
							if (nodeDependDao.isNodeExist("net" + id, vo.getXmlfile())) {
								nodeDependDao.deleteByIdXml("net" + id, vo.getXmlfile());
							} else {
								nodeDependDao.close();
							}

							User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
							ManageXmlDao mXmlDao = new ManageXmlDao();
							List xmlList = new ArrayList();
							try {
								xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								mXmlDao.close();
							}
							try {
								ChartXml chartxml;
								chartxml = new ChartXml("tree");
								chartxml.addViewTree(xmlList);
							} catch (Exception e) {
								e.printStackTrace();
							}

							ManageXmlDao subMapDao = new ManageXmlDao();
							ManageXml manageXml = (ManageXml) subMapDao.findByXml(vo.getXmlfile());
							if (manageXml != null) {
								NodeDependDao nodeDepenDao = new NodeDependDao();
								try {
									List lists = nodeDepenDao.findByXml(vo.getXmlfile());
									ChartXml chartxml;
									chartxml = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml"));
									chartxml.addBussinessXML(manageXml.getTopoName(), lists);
									ChartXml chartxmlList;
									chartxmlList = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
									chartxmlList.addListXML(manageXml.getTopoName(), lists);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									nodeDepenDao.close();
								}
							}
						}
					}
				}

				// �û��������
				User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				UserAuditUtil useraudit = new UserAuditUtil();
				String useraction = "";
				useraction = useraction + "ɾ���豸 IP:" + host.getIpAddress() + " ����:" + host.getAlias() + " ����:" + host.getType();
				try {
					useraudit.saveUserAudit(current_user, time, useraction);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// �����Զ��ping ɾ������Ϣ
				RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
				try {
					remotePingHostDao.deleteByNodeId(id);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					remotePingHostDao.close();
				}

				RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
				try {
					remotePingNodeDao.deleteByNodeId(id);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					remotePingNodeDao.close();
				}

				remotePingNodeDao = new RemotePingNodeDao();
				try {
					remotePingNodeDao.deleteByChildNodeId(id);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					remotePingNodeDao.close();
				}

				if (host.getOstype() == 46) {// netapp
					String[] netAppDataTables = { "netappaggregate", "netappconsistencypoint", "netappdisk", "netappdump", "netappdumplist", "netappenvironment", "netappplex",
							"netappproductinformation", "netappquota", "netappraid", "netapprestore", "netappsnapshot", "netappspare", "netapptree", "netappvfiler", "netappvfileripentity",
							"netappvfilerpathentity", "netappvfilerprotocolentity", "netappvolume" };
					createTableManager.clearNetAppDatas(netAppDataTables, ip, id);
				}

			}

			// ɾ���豸����ʱ�����д洢������
			String[] nmsTempDataTables = { "nms_cpu_data_temp", "nms_device_data_temp", "nms_disk_data_temp", "nms_diskperf_data_temp", "nms_envir_data_temp", "nms_fdb_data_temp",
					"nms_fibrecapability_data_temp", "nms_fibreconfig_data_temp", "nms_flash_data_temp", "nms_interface_data_temp", "nms_lights_data_temp", "nms_memory_data_temp",
					"nms_other_data_temp", "nms_ping_data_temp", "nms_process_data_temp", "nms_route_data_temp", "nms_sercice_data_temp", "nms_software_data_temp", "nms_storage_data_temp",
					"nms_system_data_temp", "nms_user_data_temp", "nms_nodeconfig", "nms_nodecpuconfig", "nms_nodediskconfig", "nms_nodememconfig", "nms_vmwarevid", "nms_emcdiskcon", "nms_emcluncon",
					"nms_emchard", "nms_emcraid", "nms_emcsystem", "nms_connect" };
			createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);

		}
		HostNodeDao hostNodeDao = new HostNodeDao();
		Hashtable nodehash = new Hashtable();
		try {
			List hostlist = hostNodeDao.loadIsMonitored(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode node = (HostNode) hostlist.get(i);
					if (nodehash.containsKey(node.getCategory() + "")) {
						((List) nodehash.get(node.getCategory() + "")).add(node);
					} else {
						List nodelist = new ArrayList();
						nodelist.add(node);
						nodehash.put(node.getCategory() + "", nodelist);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		ShareData.setNodehash(nodehash);

		PortConfigCenter.getInstance().setPortHastable();

		out.print("�ɹ�ɾ��");
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
				jsonString.append("{ifIndex:'");
				jsonString.append(strs[0]);
				jsonString.append("',");

				jsonString.append("ifName:'");
				jsonString.append(strs[1]);
				jsonString.append("',");

				jsonString.append("ifSpeed:'");
				jsonString.append(strs[2]);
				jsonString.append("',");

				jsonString.append("ifOperStatus:'");
				jsonString.append(strs[3]);
				jsonString.append("',");

				jsonString.append("outBandwidthUtilHdxPerc:'");
				jsonString.append(strs[4]);
				jsonString.append("',");

				jsonString.append("inBandwidthUtilHdxPerc:'");
				jsonString.append(strs[5]);
				jsonString.append("',");

				jsonString.append("outBandwidthUtilHdx:'");
				jsonString.append(strs[6]);
				jsonString.append("',");

				jsonString.append("inBandwidthUtilHdx:'");
				jsonString.append(strs[7]);
				jsonString.append("'}");

				if (i != ifVector.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + ifVector.size() + "}");
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

		Hashtable tempHt = new Hashtable();// ���ڼ�����м����
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

		tempHt = new Hashtable();// ���ڼ�����м����
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
				jsonString.append("{processId:'");
				jsonString.append(processInfo.getPid());
				jsonString.append("',");

				jsonString.append("processName:'");
				jsonString.append(processInfo.getName());
				jsonString.append("',");

				jsonString.append("processNumber:'");
				jsonString.append(processInfo.getCount());
				jsonString.append("',");

				jsonString.append("processType:'");
				jsonString.append(processInfo.getType());
				jsonString.append("',");

				jsonString.append("cpuKeepTime:'");
				jsonString.append(processInfo.getCpuTime());
				jsonString.append("',");

				jsonString.append("memorySpendRate:'");
				jsonString.append(floatFormate((Float) processInfo.getMemoryUtilization()));
				jsonString.append("',");

				jsonString.append("memorySpendValue:'");
				jsonString.append(floatFormate((Float) processInfo.getMemory()));
				jsonString.append("',");

				jsonString.append("processStatus:'");
				jsonString.append(processInfo.getStatus());
				jsonString.append("'}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
		}
		jsonString.append("],total:" + detailHt.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostServiceDetail() {
		String ip = getParaValue("ip");
		Vector serviceV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				serviceV = (Vector) ipAllData.get("winservice");
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
				jsonString.append("{serviceName:'");
				jsonString.append(serviceCollectEntity.getName());
				jsonString.append("',");

				jsonString.append("operatingState:'");
				jsonString.append(serviceCollectEntity.getOpstate());
				jsonString.append("',");

				jsonString.append("installedState:'");
				jsonString.append(serviceCollectEntity.getInstate());
				jsonString.append("',");

				jsonString.append("canBeUninstalled:'");
				jsonString.append(serviceCollectEntity.getUninst());
				jsonString.append("',");

				jsonString.append("canBePaused:'");
				jsonString.append(serviceCollectEntity.getPaused());
				jsonString.append("'}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
		}
		jsonString.append("],total:" + serviceV.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getHostSoftwareDetail() {
		String ip = getParaValue("ip");
		Vector softwareV = new Vector();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		if (ipAllData != null) {
			try {
				softwareV = (Vector) ipAllData.get("software");
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
				jsonString.append("{softwareId:'");
				jsonString.append(softwareCollectEntity.getSwid());
				jsonString.append("',");

				jsonString.append("softwareName:'");
				jsonString.append(softwareCollectEntity.getName());
				jsonString.append("',");

				jsonString.append("softwareType:'");
				jsonString.append(softwareCollectEntity.getType());
				jsonString.append("',");

				jsonString.append("softwareInstallDate:'");
				jsonString.append(softwareCollectEntity.getInsdate());
				jsonString.append("'}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
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
				deviceV = (Vector) ipAllData.get("device");
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
				jsonString.append("{deviceId:'");
				jsonString.append(deviceCollectEntity.getDeviceindex());
				jsonString.append("',");

				jsonString.append("deviceName:'");
				jsonString.append(deviceCollectEntity.getName().replace("\\", "/"));
				jsonString.append("',");

				jsonString.append("deviceType:'");
				jsonString.append(deviceCollectEntity.getType());
				jsonString.append("',");

				jsonString.append("deviceState:'");
				jsonString.append(deviceCollectEntity.getStatus());
				jsonString.append("'}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
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
				storageV = (Vector) ipAllData.get("storage");
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
				jsonString.append("{storageIndex:'");
				jsonString.append(storageCollectEntity.getStorageindex());
				jsonString.append("',");

				jsonString.append("storageName:'");
				jsonString.append(storageCollectEntity.getName().replace("\\", "/"));
				jsonString.append("',");

				jsonString.append("storageType:'");
				jsonString.append(storageCollectEntity.getType());
				jsonString.append("',");

				jsonString.append("storageCap:'");
				jsonString.append(storageCollectEntity.getCap());
				jsonString.append("'}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
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
				arpV = (Vector) ipAllData.get("ipmac");
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
				jsonString.append("{ifName:'");
				jsonString.append(ipMac.getIfindex());
				jsonString.append("',");

				jsonString.append("ip:'");
				jsonString.append(ipMac.getIpaddress());
				jsonString.append("',");

				jsonString.append("mac:'");
				jsonString.append(ipMac.getMac());
				jsonString.append("',");

				jsonString.append("cTime:'");
				jsonString.append(sdf.format(ipMac.getCollecttime().getTime()));
				jsonString.append("'}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
		}
		jsonString.append("],total:" + arpV.size() + "}");
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

		// ��ͼ����֯�ַ���

		// ���̵������� �ַ���
		CreateAmColumnPic amColumnPic = new CreateAmColumnPic();
		String diskString = amColumnPic.createWinDiskChart(diskHt);

		// CPU
		CreateMetersPic cmp = new CreateMetersPic();
		cmp.createCpuPic(ip, Integer.parseInt(cpuPercent));
		cmp.createMaxCpuPic(ip, cpuMax);
		cmp.createAvgCpuPic(ip, removeUnit(cpuAvg, 1));

		// ��Ӧʱ��
		CreateBarPic cbp = new CreateBarPic();
		TitleModel tm = new TitleModel();
		double[] dataArray = { new Double(responseTimePercent), new Double(responseTimeMax), new Double(removeUnit(responseTimeAvg, 2)) };
		String[] labelArray = { "��ǰ��Ӧʱ��(ms)", "�����Ӧʱ��(ms)", "ƽ����Ӧʱ��(ms)" };
		tm = new TitleModel();
		tm.setPicName(ip + "response");//
		tm.setBgcolor(0xffffff);
		tm.setXpic(450);// ͼƬ����
		tm.setYpic(180);// ͼƬ�߶�
		tm.setX1(30);// �������
		tm.setX2(20);// �������
		tm.setX3(400);// ��ͼ���
		tm.setX4(130);// ��ͼ�߶�
		tm.setX5(10);
		tm.setX6(115);
		cbp.createTimeBarPic(dataArray, labelArray, tm, 40);

		// �ڴ��ַ���
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

		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
		xmlStr.append("<chart><series>");
		String[] titleStr = new String[] { "��ǰ����", "��ǰ����", "ƽ������", "ƽ������", "�������", "�������" };
		String[] title = new String[] { "��ǰ����", "��ǰδ��", "ƽ������", "ƽ��δ��", "�������", "���δ��" };

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

		// ��ͨ���ַ���
		StringBuffer pingPercentSB = new StringBuffer();
		pingPercentSB.append("��ͨ;").append(Math.round(Double.valueOf(pingPercent))).append(";false;7CFC00\\n");
		pingPercentSB.append("δ��ͨ;").append(100 - Math.round(Double.valueOf(pingPercent))).append(";false;FF0000\\n");
		StringBuffer pingMaxSB = new StringBuffer();
		pingMaxSB.append("��ͨ;").append(Math.round(Double.valueOf(pingMax))).append(";false;7CFC00\\n");
		pingMaxSB.append("δ��ͨ;").append(100 - Math.round(Double.valueOf(pingMax))).append(";false;FF0000\\n");
		StringBuffer pingAvgSB = new StringBuffer();
		pingAvgSB.append("��ͨ;").append(Math.round(Double.valueOf(removeUnit(pingAvg, 1)))).append(";false;7CFC00\\n");
		pingAvgSB.append("δ��ͨ;").append(100 - Math.round(Double.valueOf(removeUnit(pingAvg, 1)))).append(";false;FF0000\\n");

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

	private void getHostNodeConfig() {
		String ip = getParaValue("ip");
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
		String sysUpTime = null;
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

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{nodeId:'");
		jsonString.append(node.getId());
		jsonString.append("',");

		jsonString.append("nodeIp:'");
		jsonString.append(node.getIpAddress());
		jsonString.append("',");

		jsonString.append("nodeAlias:'");
		jsonString.append(node.getAlias());
		jsonString.append("',");

		jsonString.append("nodeSysName:'");
		jsonString.append(node.getSysName());
		jsonString.append("',");

		jsonString.append("type:'");
		jsonString.append(nodeDTO.getType());
		jsonString.append("',");

		jsonString.append("subType:'");
		jsonString.append(nodeDTO.getSubtype());
		jsonString.append("',");

		jsonString.append("sysUpTime:'");
		jsonString.append(sysUpTime);
		jsonString.append("',");

		jsonString.append("sysDescr:'");
		jsonString.append(node.getSysDescr());
		jsonString.append("',");

		jsonString.append("physicalMemoryCap:'");
		jsonString.append(physicalMemoryCap);
		jsonString.append("',");

		jsonString.append("virtualMemoryCap:'");
		jsonString.append(virtualMemoryCap);
		jsonString.append("',");

		jsonString.append("physicalMemoryRate:'");
		jsonString.append(physicalMemoryPercent);
		jsonString.append("',");

		jsonString.append("virtualMemoryRate:'");
		jsonString.append(virtualMemoryPercent);
		jsonString.append("',");

		jsonString.append("ctTime:'");
		jsonString.append(ctTime);
		jsonString.append("'}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("getHostNodeDataByType")) {
			getHostNodeDataByType();
		} else if (action.equals("deleteNodes")) {
			deleteNodes();
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
		}else if (action.equals("getNetNodeDataByType")) {
			getNetNodeDataByType();
		}else if (action.equals("getNetInterfaceDetail")) {
			getNetInterfaceDetail();
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
				sumOfCPU = Float.parseFloat(CpuTime.replace("��", ""));
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
		int scale = 2;// ����λ��
		int roundingMode = 4;// ��ʾ�������룬����ѡ��������ֵ��ʽ������ȥβ���ȵ�.
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
