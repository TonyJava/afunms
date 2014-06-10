package com.afunms.topology.manage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.ajaxManager.PerformancePanelAjaxManager;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.util.AssetHelper;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.NodeAlarmUtil;
import com.afunms.common.util.PollDataUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpPing;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.UserAuditUtil;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.NetNodeCfgFileDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.interfaceInfo.InterfaceInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.PortConfigCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.VMWareConnectConfig;
import com.afunms.polling.om.VMWareVid;
import com.afunms.polling.snmp.LoadWindowsWMIFile;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.temp.model.NodeTemp;
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
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.dao.RemotePingNodeDao;
import com.afunms.topology.dao.RepairLinkDao;
import com.afunms.topology.dao.VMWareConnectConfigDao;
import com.afunms.topology.dao.VMWareVidDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.MonitorNetDTO;
import com.afunms.topology.model.MonitorNodeDTO;
import com.afunms.topology.model.NetSyslogNodeRule;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.vo.VMWareDao;

@SuppressWarnings("unchecked")
public class PerformanceManager extends BaseManager implements ManagerInterface {

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();

	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String add() {
		String assetid = getParaValue("assetid");// 设备资产编号
		String location = getParaValue("location");// 机房位置
		String ipAddress = getParaValue("ip_address");
		String alias = getParaValue("alias");
		int snmpversion = getParaIntValue("snmpversion");
		String community = getParaValue("community");
		String writecommunity = getParaValue("writecommunity");
		String vmwareusername = getParaValue("uname");
		String vmwarepassword = getParaValue("pw");
		int type = getParaIntValue("type");
		int transfer = getParaIntValue("transfer");

		int ostype = 0;
		try {
			ostype = getParaIntValue("ostype");
		} catch (Exception e) {

		}
		int collecttype = 0;
		try {
			collecttype = getParaIntValue("collecttype");
		} catch (Exception e) {

		}
		String bid = getParaValue("bid");

		String sendmobiles = getParaValue("sendmobiles");
		String sendemail = getParaValue("sendemail");
		String sendphone = getParaValue("sendphone");
		int supperid = getParaIntValue("supper");// snow add 2010-5-18

		if (sendmobiles == null) {
			sendmobiles = "";
		}
		if (sendemail == null) {
			sendemail = "";
		}
		if (sendphone == null) {
			sendphone = "";
		}
		int manageInt = getParaIntValue("manage");
		boolean managed = false;
		if (manageInt == 1) {
			managed = true;
		}
		// SNMP V3
		int securityLevel = getParaIntValue("securityLevel");
		String securityName = getParaValue("securityName");
		int v3_ap = getParaIntValue("v3_ap");
		String authPassPhrase = getParaValue("authPassPhrase");
		int v3_privacy = getParaIntValue("v3_privacy");
		String privacyPassPhrase = getParaValue("privacyPassPhrase");
		if (securityName == null) {
			securityName = "";
		}
		if (authPassPhrase == null) {
			authPassPhrase = "";
		}
		if (privacyPassPhrase == null) {
			privacyPassPhrase = "";
		}

		/**
		 * @author nielin modify int addResult =
		 *         helper.addHost(ipAddress,alias,community,writecommunity,type,ostype,
		 *         collecttype); //加入一台服务器
		 */

		TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
		int addResult = 0;
		if (supperid >= 0) {
			if (collecttype == 3) {
				if (PollingEngine.getAddiplist() != null && PollingEngine.getAddiplist().size() > 0) {
					List list = PollingEngine.getAddiplist();
					for (int i = 0; i < list.size(); i++) {
						Hashtable hst = (Hashtable) list.get(i);
						NetworkUtil test = new NetworkUtil();
						List listip = test.parseAllIp((String) hst.get("startip"), (String) hst.get("endip"));
						for (int j = 0; j < listip.size(); j++) {
							String iplist = (String) listip.get(j);
							addResult = helper.addHost(assetid, location, iplist, iplist, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid,
									sendmobiles, sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
							Host node = (Host) PollingEngine.getInstance().getNodeByIP(iplist);
							try {
								if (node.getEndpoint() == 2) {
								} else {
									if (node.getCategory() == 4) {
										// 初始化服务器采集指标和阀值
										try {
											AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
											alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()));

											// 判断采集方式
											if (node.getCollecttype() == 1) {// snmp方式采集
												NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
												nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "1");
											} else if (node.getCollecttype() > 1) {// 其他方式采集

												NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
												nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()),
														"0", node.getCollecttype());

											}
										} catch (RuntimeException e) {
											e.printStackTrace();
										}

									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					PollingEngine.setAddiplist(new ArrayList());
					addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles,
							sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
				} else {
					addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles,
							sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
				}
			} else {
				addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles,
						sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
			}
		}
		if (addResult > 0) {
			String[] fs = getParaArrayValue("fcheckbox");
			String faci_str = "";
			if (fs != null && fs.length > 0) {
				for (int i = 0; i < fs.length; i++) {
					String fa = fs[i];
					faci_str = faci_str + fa + ",";
				}
			}

			NetSyslogNodeRuleDao netruledao = new NetSyslogNodeRuleDao();
			NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
			try {
				String strNodeId = "";
				try {
					strNodeId = netruledao.findIdByIpaddress(ipAddress);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String strFacility = "";
				List rulelist = new ArrayList();
				try {
					rulelist = ruledao.loadAll();
				} catch (Exception e) {

				} finally {
					ruledao.close();
				}
				if (rulelist != null && rulelist.size() > 0 && "".equals(faci_str)) {
					NetSyslogRule logrule = (NetSyslogRule) rulelist.get(0);
					strFacility = logrule.getFacility();
				} else {
					strFacility = faci_str;
				}
				String strSql = "";
				strSql = "insert into nms_netsyslogrule_node(nodeid,facility)values('" + strNodeId + "','" + strFacility + "')";
				try {
					netruledao.saveOrUpdate(strSql);
				} catch (Exception e) {
					e.printStackTrace();
				}
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				try {
					timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(addResult), timeShareConfigUtil.getObjectType("0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				try {
					timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(addResult), timeGratherConfigUtil.getObjectType("0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				/* snow close */
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				netruledao.close();
				ruledao.close();
			}
		}
		if (addResult == 0) {
			setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
			return null;
		}
		if (addResult == -1) {
			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
			return null;
		}
		if (addResult == -2) {
			setErrorCode(ErrorMessage.PING_FAILURE);
			return null;
		}
		if (addResult == -3) {
			setErrorCode(ErrorMessage.SNMP_FAILURE);
			return null;
		}

		// 2.更新xml
		if (type == 4) {
			// 主机服务器
			XmlOperator opr = new XmlOperator();
			opr.setFile("server.jsp");
			opr.init4updateXml();
			opr.addNode(helper.getHost());
			opr.writeXml();
		} else if (type < 4) {
			// 网络设备
			XmlOperator opr = new XmlOperator();
			opr.setFile("network.jsp");
			opr.init4updateXml();
			opr.addNode(helper.getHost());
			opr.writeXml();
		} else {

		}

		Host node = (Host) PollingEngine.getInstance().getNodeByID(addResult);

		// 采集设备信息
		try {
			if (node.getEndpoint() == 2) {
			} else {
				if (node.getCategory() == 4) {
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()));
						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "1");
					} catch (RuntimeException e) {
						e.printStackTrace();
					}

				} else if (node.getCategory() < 4 || node.getCategory() == 7) {
					// 初始化服务器采集指标和阀值
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()));
						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 8) {
					// 初始化防火墙采集指标和阀值
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_FIREWALL, getSutType(node.getSysOid()));

						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_FIREWALL, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 9) {
					// 初始化ATM设备采集指标
					// ATM设备
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
						// 只用PING检测连通性
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "atm", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "atm", "1", "ping");
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else if (node.getCategory() == 13) {
					// 初始化CMTS设备采集指标
					// HDS设备
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
						// 只用PING检测连通性
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()), "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()), "1", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					} else {
						// 正常采集状态
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()));
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()), "1");
							PortConfigCenter.getInstance().setPortHastable();
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
				} else if (node.getCategory() == 14) {
					// 初始化存储设备采集指标
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_STORAGE, getSutType(node.getSysOid()));
						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_STORAGE, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 15) {
					// 初始化VMWare设备采集指标
					// vmware设备
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
						// 只用PING检测连通性
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "1", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					} else {
						// 正常采集状态
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "1");
							PortConfigCenter.getInstance().setPortHastable();
							VMWareConnectConfigDao vmwaredao = new VMWareConnectConfigDao();
							try {
								VMWareConnectConfig vmware = new VMWareConnectConfig();
								vmware.setNodeid(Long.parseLong(node.getId() + ""));
								vmware.setUsername(vmwareusername);
								vmware.setPwd(EncryptUtil.encode(vmwarepassword));
								vmware.setHosturl("");
								vmware.setBak("");
								vmwaredao.save(vmware);
								HashMap vmids = new HashMap();
								try {
									if (vmids != null && vmids.size() > 0) {
										Iterator iterator = vmids.keySet().iterator();
										List vmwarevids = new ArrayList();
										while (iterator.hasNext()) {
											String vmid = (String) iterator.next();
											String guestname = (String) vmids.get(vmid);
											VMWareVid vmwarevid = new VMWareVid();
											vmwarevid.setNodeid(Long.parseLong(node.getId() + ""));
											vmwarevid.setVid(vmid);
											vmwarevid.setGuestname(guestname);
											vmwarevid.setBak("");
											vmwarevids.add(vmwarevid);
										}
										VMWareVidDao vmwareviddao = new VMWareVidDao();
										try {
											vmwareviddao.save(vmwarevids);
										} catch (Exception e) {
										} finally {
											vmwareviddao.close();
										}
									}

									try {
										String url = "https://" + node.getIpAddress() + "/sdk";
										HashMap<String, Object> summaryresultMap = (HashMap<String, Object>) VIMMgr.syncVIMObjs(url, vmwareusername, vmwarepassword);
										ArrayList<HashMap<String, Object>> dslist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("Datastore");
										ArrayList<HashMap<String, Object>> wulist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("HostSystem");
										ArrayList<HashMap<String, Object>> rplist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("ResourcePool");
										ArrayList<HashMap<String, Object>> vmlist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("VirtualMachine");
										ArrayList<HashMap<String, Object>> dclist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("Datacenter");
										ArrayList<HashMap<String, Object>> crlist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("ComputeResource");// 群集
										PerformancePanelAjaxManager per = new PerformancePanelAjaxManager();
										per.savePhysical(wulist, node.getId() + "", node.getIpAddress());
										per.saveVmware(vmlist, node.getId() + "", node.getIpAddress());
										per.saveDatastore(dslist, node.getId() + "", node.getIpAddress());
										per.saveResourcepool(rplist, node.getId() + "", node.getIpAddress());
										per.saveDatacenter(dclist, node.getId() + "", node.getIpAddress());
										per.saveYun(crlist, node.getId() + "", node.getIpAddress());
										// 初始化告警
										if (wulist != null && wulist.size() > 0) {
											String vid = "";
											for (int i = 0; i < wulist.size(); i++) {
												vid = wulist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL,
														getSutType(node.getSysOid()), "physical", vid);
											}
										}
										if (vmlist != null && vmlist.size() > 0) {
											String vid = "";
											for (int i = 0; i < vmlist.size(); i++) {
												vid = vmlist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL,
														getSutType(node.getSysOid()), "vmware", vid);
											}
										}
										if (dslist != null && dslist.size() > 0) {
											String vid = "";
											for (int i = 0; i < dslist.size(); i++) {
												vid = dslist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL,
														getSutType(node.getSysOid()), "datastore", vid);
											}
										}
										if (rplist != null && rplist.size() > 0) {
											String vid = "";
											for (int i = 0; i < rplist.size(); i++) {
												vid = rplist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL,
														getSutType(node.getSysOid()), "resourcepool", vid);
											}
										}
										if (crlist != null && crlist.size() > 0) {
											String vid = "";
											for (int i = 0; i < crlist.size(); i++) {
												vid = crlist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL,
														getSutType(node.getSysOid()), "yun", vid);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								vmwaredao.close();
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
				}

				// 若只用PING TELNET SSH方式检测可用性,则性能数据不采集,跳过
				if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING || node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT
						|| node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
					if (node.getCategory() < 4 || node.getCategory() == 7) {
						PollDataUtil polldata = new PollDataUtil();
						polldata.collectNetData(node.getId() + "");
					} else if (node.getCategory() == 4) {
						collectHostData(node);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		HostNodeDao hostNodeDao = new HostNodeDao();
		Hashtable nodehash = new Hashtable();
		try {
			List hostlist = hostNodeDao.loadIsMonitored(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode _node = (HostNode) hostlist.get(i);
					if (nodehash.containsKey(_node.getCategory() + "")) {
						((List) nodehash.get(_node.getCategory() + "")).add(_node);
					} else {
						List nodelist = new ArrayList();
						nodelist.add(_node);
						nodehash.put(_node.getCategory() + "", nodelist);
					}
				}
			}
		} catch (Exception e) {

		} finally {
			hostNodeDao.close();
		}
		ShareData.setNodehash(nodehash);
		return "/perform.do?action=monitornodelist";
	}

	private String cancelmanage() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				try {
					HostNodeDao dao = new HostNodeDao();
					HostNode host = null;
					try {
						host = (HostNode) dao.findByID(ids[i]);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dao.close();
					}
					host.setManaged(false);
					dao = new HostNodeDao();
					try {
						dao.update(host);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dao.close();
					}
					PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(ids[i]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		return "/perform.do?action=monitornodelist";
	}

	private void collectHostData(Host node) {
		try {
			Hashtable hashv = null;
			LoadWindowsWMIFile windowswmi = null;
			I_HostCollectData hostdataManager = new HostCollectDataManager();
			if (node.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL) {
				try {
					if (node.getOstype() == 6) {
					} else if (node.getOstype() == 9) {
					} else if (node.getOstype() == 7) {
					} else if (node.getOstype() == 8) {
					} else if (node.getOstype() == 5) {
						try {
							windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
							hashv = windowswmi.getTelnetMonitorDetail();
							hostdataManager.createHostData(node.getIpAddress(), hashv);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				hashv = null;
			}

			else if (node.getCollecttype() == SystemConstant.COLLECTTYPE_WMI) {
				try {
					windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
					hashv = windowswmi.getTelnetMonitorDetail();
					hostdataManager.createHostData(node.getIpAddress(), hashv);
				} catch (Exception e) {
					e.printStackTrace();
				}
				hashv = null;
			}

			else {
				// SNMP采集方式
				HostNode hostnode = new HostNode();
				hostnode.setId(node.getId());
				hostnode.setSysName(node.getSysName());
				hostnode.setCategory(node.getCategory());
				hostnode.setCommunity(node.getCommunity());
				hostnode.setSnmpversion(node.getSnmpversion());
				hostnode.setIpAddress(node.getIpAddress());
				hostnode.setLocalNet(node.getLocalNet());
				hostnode.setNetMask(node.getNetMask());
				hostnode.setAlias(node.getAlias());
				hostnode.setSysDescr(node.getSysDescr());
				hostnode.setSysOid(node.getSysOid());
				hostnode.setType(node.getType());
				hostnode.setManaged(node.isManaged());
				hostnode.setOstype(node.getOstype());
				hostnode.setCollecttype(node.getCollecttype());
				hostnode.setSysLocation(node.getSysLocation());
				hostnode.setSendemail(node.getSendemail());
				hostnode.setSendmobiles(node.getSendmobiles());
				hostnode.setSendphone(node.getSendphone());
				hostnode.setBid(node.getBid());
				hostnode.setEndpoint(node.getEndpoint());
				hostnode.setStatus(0);
				hostnode.setSupperid(node.getSupperid());

				try {
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的所有被监视指标
						monitorItemList = indicatorsdao.findByNodeIdAndTypeAndSubtype(hostnode.getId() + "", "host", "");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList != null && monitorItemList.size() > 0) {
						for (int i = 0; i < monitorItemList.size(); i++) {
							NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
							PollDataUtil polldatautil = new PollDataUtil();
							polldatautil.collectHostData(nodeGatherIndicators);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private String delete() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
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

				} finally {
					linkdao.close();
				}

				String ip = host.getIpAddress();
				String allipstr = SysUtil.doip(ip);

				CreateTableManager ctable = new CreateTableManager();
				try {
					if (host.getCategory() < 4 || host.getCategory() == 7 || host.getCategory() == 8 || host.getCategory() == 9) {
						// 先删除网络设备表
						// 连通率
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

						// 内存
						try {
							ctable.deleteTable("memory", allipstr, "mem");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryday", allipstr, "memday");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("portstatus", allipstr, "port");// 端口状态
						} catch (Exception e) {
							e.printStackTrace();
						}

						ctable.deleteTable("flash", allipstr, "flash");// 闪存
						ctable.deleteTable("flashhour", allipstr, "flashhour");// 闪存
						ctable.deleteTable("flashday", allipstr, "flashday");// 闪存

						ctable.deleteTable("buffer", allipstr, "buffer");// 缓存
						ctable.deleteTable("bufferhour", allipstr, "bufferhour");// 缓存
						ctable.deleteTable("bufferday", allipstr, "bufferday");// 缓存

						ctable.deleteTable("fan", allipstr, "fan");// 风扇
						ctable.deleteTable("fanhour", allipstr, "fanhour");// 风扇
						ctable.deleteTable("fanday", allipstr, "fanday");// 风扇

						ctable.deleteTable("power", allipstr, "power");// 电源
						ctable.deleteTable("powerhour", allipstr, "powerhour");// 电源
						ctable.deleteTable("powerday", allipstr, "powerday");// 电源

						ctable.deleteTable("vol", allipstr, "vol");// 电压
						ctable.deleteTable("volhour", allipstr, "volhour");// 电压
						ctable.deleteTable("volday", allipstr, "volday");// 电压

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

						// 带宽利用率
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

						// 端口状态
						try {
							ctable.deleteTable("portstatus", allipstr, "port");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 流速
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

						// 综合流速
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

						// 丢包率
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

						// 错误率
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

						// 数据包
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

						// 入口数据包
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

						// 出口数据包
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

						// 温度
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
							// 同时删除事件表里的相关数据
							eventdao.delete(host.getId(), "network");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}

						PortconfigDao portconfigdao = new PortconfigDao();
						try {
							// 同时删除端口配置表里的相关数据
							portconfigdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portconfigdao.close();
						}

						// 删除nms_ipmacchange表里的对应的数据
						IpMacChangeDao macchangebasedao = new IpMacChangeDao();
						try {
							macchangebasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macchangebasedao.close();
						}

						// 删除网络设备配置文件表里的对应的数据
						NetNodeCfgFileDao configdao = new NetNodeCfgFileDao();
						try {
							configdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							configdao.close();
						}

						// 删除网络设备SYSLOG接收表里的对应的数据
						NetSyslogDao syslogdao = new NetSyslogDao();
						try {
							syslogdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							syslogdao.close();
						}

						// 删除网络设备端口扫描表里的对应的数据
						PortScanDao portscandao = new PortScanDao();
						try {
							portscandao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portscandao.close();
						}

						// 删除网络设备面板图表里的对应的数据
						IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
						try {
							addresspaneldao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							addresspaneldao.close();
						}

						// 删除网络设备接口表里的对应的数据
						HostInterfaceDao interfacedao = new HostInterfaceDao();
						try {
							interfacedao.deleteByHostId(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							interfacedao.close();
						}

						// 删除网络设备IP别名表里的对应的数据
						IpAliasDao ipaliasdao = new IpAliasDao();
						try {
							ipaliasdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipaliasdao.close();
						}

						// 删除网络设备手工配置的链路表里的对应的数据
						RepairLinkDao repairdao = new RepairLinkDao();
						try {
							repairdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							repairdao.close();
						}

						// 删除网络设备IPMAC表里的对应的数据
						IpMacDao ipmacdao = new IpMacDao();
						try {
							ipmacdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipmacdao.close();
						}

						// 删除该设备的采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "net", "");
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// 删除网络设备指标采集表里的对应的数据
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							indicatdao.deleteByNodeId(host.getId() + "", "net");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						// 删除IP-MAC-BASE表里的对应的数据
						IpMacBaseDao macbasedao = new IpMacBaseDao();
						try {
							macbasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macbasedao.close();
						}
						// 删除SYSLOG规则表
						NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
						try {
							ruledao.deleteByNodeID(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ruledao.close();
						}

						// 删除设备当前最新告警信息表中的数据
						NodeAlarmUtil.deleteByDeviceIdAndDeviceType(host);
					} else if (host.getCategory() == 4) {
						// 删除主机服务器
						try {
							ctable.deleteTable("pro", allipstr, "pro");// 进程
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("prohour", allipstr, "prohour");// 进程小时
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("proday", allipstr, "proday");// 进程天
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("log", allipstr, "log");// 进程天
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("memory", allipstr, "mem");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("memoryday", allipstr, "memday");// 内存
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
							ctable.deleteTable("diskincre", allipstr, "diskincre");// 磁盘增长
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("diskinch", allipstr, "diskinch");// 磁盘增长
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							ctable.deleteTable("diskincd", allipstr, "diskincd");// 磁盘增长
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
							// 删除换页率
							try {
								ctable.deleteTable("pgused", allipstr, "pgused");
								ctable.deleteTable("pgusedhour", allipstr, "pgusedhour");
								ctable.deleteTable("pgusedday", allipstr, "pgusedday");
							} catch (Exception e) {

							}
						}
						// 删除该设备的采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "host", "");
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// 删除服务器指标采集表里的对应的数据
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
							// 同时删除事件表里的相关数据
							eventdao.delete(host.getId(), "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}
						// 删除diskconfig
						String[] otherTempData = new String[] { "nms_diskconfig" };
						String[] ipStrs = new String[] { host.getIpAddress() };
						ctable.clearTablesData(otherTempData, "ipaddress", ipStrs);
						// 删除进程组的数据
						ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
						processGroupConfigurationUtil.deleteProcessGroupAndConfigurationByNodeid(host.getId() + "");

						// 删除SYSLOG规则表
						NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
						try {
							ruledao.deleteByNodeID(host.getId() + "");
						} catch (Exception e) {

						} finally {
							ruledao.close();
						}
						// 删除设备当前最新告警信息表中的数据
						NodeAlarmUtil.deleteByDeviceIdAndDeviceType(host);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// 2.更新xml
				if (host.getCategory() < 4) {
					// 网络设备
					XmlOperator opr = new XmlOperator();
					opr.setFile("network.jsp");
					opr.init4updateXml();
					opr.deleteNodeByID(host.getId() + "");
					opr.writeXml();
				} else if (host.getCategory() == 4) {
					// 主机服务器
					XmlOperator opr = new XmlOperator();
					opr.setFile("server.jsp");
					opr.init4updateXml();
					opr.deleteNodeByID(host.getId() + "");
					opr.writeXml();
				}
				// 删除指标全局阈值表对应的数据
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

				if (linklist != null && linklist.size() > 0) {
					for (int l = 0; l < linklist.size(); l++) {
						link = (Link) linklist.get(l);
						if (link != null) {
							LinkDao ldao = new LinkDao();
							try {
								ldao.delete(link.getId() + "");
							} catch (Exception e) {

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

				// 更新业务视图
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

							// yangjun
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

				// 用户操作审计
				User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				UserAuditUtil useraudit = new UserAuditUtil();
				String useraction = "";
				useraction = useraction + "删除设备 IP:" + host.getIpAddress() + " 别名:" + host.getAlias() + " 类型:" + host.getType();
				try {
					useraudit.saveUserAudit(current_user, time, useraction);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 如果是远程ping 删除其信息

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
			}

			// 刷新内存中采集指标
			NodeGatherIndicatorsUtil gatherutil = new NodeGatherIndicatorsUtil();
			gatherutil.refreshShareDataGather();

			// 删除设备在临时表里中存储的数据
			String[] nmsTempDataTables = { "nms_cpu_data_temp", "nms_device_data_temp", "nms_disk_data_temp", "nms_diskperf_data_temp", "nms_envir_data_temp", "nms_fdb_data_temp",
					"nms_fibrecapability_data_temp", "nms_fibreconfig_data_temp", "nms_flash_data_temp", "nms_interface_data_temp", "nms_lights_data_temp", "nms_memory_data_temp",
					"nms_other_data_temp", "nms_ping_data_temp", "nms_process_data_temp", "nms_route_data_temp", "nms_sercice_data_temp", "nms_software_data_temp",
					"nms_storage_data_temp", "nms_system_data_temp", "nms_user_data_temp", "nms_nodeconfig", "nms_nodecpuconfig", "nms_nodediskconfig", "nms_nodememconfig" };
			CreateTableManager createTableManager = new CreateTableManager();
			createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
		}
		return "/perform.do?action=monitornodelist";
	}

	private String detailcancelmanage() {
		String id = getParaValue("id");
		try {
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			host.setManaged(false);
			dao = new HostNodeDao();
			try {
				dao.update(host);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/perform.do?action=list";
	}

	private String downloadnetworklistfuck() {
		Hashtable reporthash = new Hashtable();
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);

		report.createReport_networklist("/temp/networklist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/performance/downloadreport.jsp";
	}

	// quzhi
	private String editall() {
		String[] ids = getParaArrayValue("checkbox");
		String hostid = "";
		if (ids != null && ids.length > 0) {
			// 进行修改
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				HostNodeDao dao = new HostNodeDao();
				HostNode host = null;
				try {
					host = (HostNode) dao.findByID(id);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					dao.close();
				}
				hostid = hostid + host.getId() + ",";

			}
			request.setAttribute("hostid", hostid);
		}
		return "/performance/editall.jsp";
	}

	private String endpointnodelist() {
		HostNodeDao dao = new HostNodeDao();
		setTarget("/performance/endponitnodelist.jsp");
		return list(dao, " where (category=2 or category=3) and endpoint=1");
	}

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("monitornodelist")) {
			return monitornodelist();
		}
		if (action.equals("monitornetlist")) {
			return monitornetlist();
		}
		if (action.equals("endpointnodelist")) {
			return endpointnodelist();
		}
		if (action.equals("monitorhostlist")) {
			return monitorhostlist();
		}
		if (action.equals("panelnodelist")) {
			return panelnodelist();
		}
		if (action.equals("monitorswitchlist")) {
			return monitorswitchlist();
		}
		if (action.equals("monitorroutelist")) {
			return monitorroutelist();
		}
		if (action.equals("monitorfirewalllist")) {
			return monitorfirewalllist();
		}
		if (action.equals("read")) {
			return read();
		}
		if (action.equals("ready_edit")) {
			return readyEdit();
		}
		if (action.equals("ready_editalias")) {
			return readyEditAlias();
		}
		if (action.equals("ready_editsysgroup")) {
			return readyEditSysGroup();
		}
		if (action.equals("ready_editsnmp")) {
			return readyEditSnmp();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("cancelmanage")) {
			return cancelmanage();
		}
		if (action.equals("menucancelmanage")) {
			return menucancelmanage();
		}
		if (action.equals("menuaddmanage")) {
			return menuaddmanage();
		}
		if (action.equals("menucancelendpoint")) {
			return menucancelendpoint();
		}
		if (action.equals("menuaddendpoint")) {
			return menuaddendpoint();
		}
		if (action.equals("detailcancelmanage")) {
			return detailcancelmanage();
		}
		if (action.equals("updatealias")) {
			return updatealias();
		}
		if (action.equals("updatesysgroup")) {
			return updatesysgroup();
		}
		if (action.equals("updatesnmp")) {
			return updatesnmp();
		}
		if (action.equals("refreshsysname")) {
			return refreshsysname();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("find")) {
			return find();
		}
		if (action.equals("ready_add")) {
			return ready_add();
		}
		if (action.equals("monitorfind")) {
			return monitorfind();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("telnet")) {
			return telnet();
		}
		if (action.equals("save")) {
			return save();
		}
		if (action.equals("listbynameasc")) {
			return listByNameAsc();
		}
		if (action.equals("listbynamedesc")) {
			return listByNameDesc();
		}
		if (action.equals("listbyipasc")) {
			return listByIpAsc();
		}
		if (action.equals("listbyipdesc")) {
			return listByIpDesc();
		}
		if (action.equals("listbybripasc")) {
			return listByBrIpAsc();
		}
		if (action.equals("listbybripdesc")) {
			return listByBrIpDesc();
		}
		if (action.equals("listbynodeasc")) {
			return listByNodeAsc();
		}
		if (action.equals("listbynodedesc")) {
			return listByNodeDesc();
		}
		if (action.equals("listbynodenameasc")) {
			return listByNodeNameAsc();
		}
		if (action.equals("listbynodenamedesc")) {
			return listByNodeNameDesc();
		}
		if (action.equals("monitorswitchbynameasc")) {
			return monitorswitchlistByNameAsc();
		}
		if (action.equals("monitorswitchbynamedesc")) {
			return monitorswitchlistByNameDesc();
		}
		if (action.equals("monitorswitchbyipasc")) {
			return monitorswitchlistByIpAsc();
		}
		if (action.equals("monitorswitchbyipdesc")) {
			return monitorswitchlistByIpDesc();
		}

		if (action.equals("monitorhostbynameasc")) {
			return monitorhostlistByNameAsc();
		}
		if (action.equals("monitorhostbynamedesc")) {
			return monitorhostlistByNameDesc();
		}
		if (action.equals("monitorhostbyipasc")) {
			return monitorhostlistByIpAsc();
		}
		if (action.equals("monitorhostbyipdesc")) {
			return monitorhostlistByIpDesc();
		}
		if (action.equals("editall")) {
			return editall();
		}
		if (action.equals("updateBid")) {
			return updateBid();
		}
		// quzhi add
		if (action.equals("netChoce")) {
			return netChoce();
		}
		if (action.equals("hostChoce")) {
			return hostChoce();
		}
		if (action.equals("netchocereport")) {
			return netchocereport();
		}
		if (action.equals("hostchocereport")) {
			return hostchocereport();
		}
		if (action.equals("downloadnetworklistfuck")) {
			return downloadnetworklistfuck();
		}
		if (action.equals("remotePing")) {
			return remotePing();
		}
		// linan add
		if (action.equals("updatemac")) {
			return updatemac();
		}
		if (action.equals("monitorNodelist")) {
			return monitorNodelist();
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String find() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.findByCondition(key, value));

		return "/performance/find.jsp";
	}

	/**
	 * 获得业务权限的 SQL 语句
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 * @return
	 */
	public String getBidSql() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}

		// SysLogger.info("select * from topo_host_node where managed=1 "+s);

		String sql = "";
		if (current_user.getRole() == 0) {
			sql = "";
		} else {
			sql = s.toString();
		}

		String treeBid = request.getParameter("treeBid");
		if (treeBid != null && treeBid.trim().length() > 0) {
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if (treeBids != null) {
				for (int i = 0; i < treeBids.length; i++) {
					if (treeBids[i].trim().length() > 0) {
						sql = sql + " and " + "bid" + " like '%," + treeBids[i].trim() + ",%'";
					}
				}
			}
		}
		request.setAttribute("treeBid", treeBid);
		return sql;
	}

	private MonitorDBDTO getDBDTOByDBVo(DBVo vo, int sid) {
		int id = vo.getId(); // id
		String ipAddress = vo.getIpAddress(); // ipaddress
		String alias = vo.getAlias(); // 名称
		String dbname = vo.getDbName(); // 数据库名称
		String port = vo.getPort(); // 端口

		String dbtype = ""; // 数据库类型

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
		}// 状态
		if ("Oracle".equalsIgnoreCase(dbtype)) {
			id = sid;
		}

		MonitorDBDTO monitorDBDTO = new MonitorDBDTO();
		monitorDBDTO.setId(id);
		monitorDBDTO.setAlias(alias);
		monitorDBDTO.setDbname(dbname);
		monitorDBDTO.setDbtype(dbtype);

		monitorDBDTO.setIpAddress(ipAddress);
		monitorDBDTO.setPort(port);

		return monitorDBDTO;
	}

	private List getDbList() {
		List monitorDBDTOList = new ArrayList();
		DBTypeVo oraVo = null;
		DBTypeDao typedao = new DBTypeDao();
		try {
			oraVo = (DBTypeVo) typedao.findByDbtype("Oracle");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}

		List list = new ArrayList();

		String sql = "select * from app_db_node";

		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);

			MonitorDBDTO monitorDBDTO = null;

			if (vo.getDbtype() == oraVo.getId()) {
				OraclePartsDao odao = null;
				List oracles = new ArrayList();
				try {
					odao = new OraclePartsDao();

					oracles = odao.findOracleParts(vo.getId());

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					odao.close();
				}
				for (int j = 0; j < oracles.size(); j++) {
					OracleEntity ora = (OracleEntity) oracles.get(j);

					String ipAddress = vo.getIpAddress();

					vo.setIpAddress(vo.getIpAddress() + ":" + ora.getId());

					monitorDBDTO = getDBDTOByDBVo(vo, ora.getId());

					monitorDBDTO.setSid(String.valueOf(ora.getId()));

					monitorDBDTO.setIpAddress(ipAddress);

					monitorDBDTOList.add(monitorDBDTO);
				}
			} else {
				monitorDBDTO = getDBDTOByDBVo(vo, 0);
				monitorDBDTOList.add(monitorDBDTO);
			}

		}
		if (monitorDBDTOList == null) {
			monitorDBDTOList = new ArrayList();
		}
		return monitorDBDTOList;
	}

	public List getMonitorListByCategory(String category) {

		String where = "";

		if ("node".equals(category)) {
			where = " where managed=1";
		} else if ("net_server".equals(category)) {
			where = " where managed=1 and category=4";
		} else if ("net".equals(category)) {
			where = " where managed=1 and (category=1 or category=2 or category=3 or category=7) ";
		} else if ("net_router".equals(category)) {
			where = " where managed=1 and category=1";
		} else if ("net_switch".equals(category)) {
			where = " where managed=1 and (category=2 or category=3 or category=7) ";
		} else if ("safeequip".equals(category)) {
			where = " where managed=1 and (category=8) ";
		} else if ("net_firewall".equals(category)) {
			where = " where managed=1 and (category=8) ";
		} else if ("net_atm".equals(category)) {
			where = " where managed=1 and (category=9) ";
		} else if ("net_gateway".equals(category)) {
			where = " where managed=1 and (category=10) ";
		} else if ("net_f5".equals(category)) {
			where = " where managed=1 and (category=11) ";
		} else if ("net_vpn".equals(category)) {
			where = " where managed=1 and (category=12) ";
		} else if ("net_cmts".equals(category)) {
			where = " where managed=1 and (category=13) ";
		} else if ("net_storage".equals(category)) {
			where = " where managed=1 and (category=14) ";
		} else if ("net_virtual".equals(category)) {
			where = " where managed=1 and (category=15) ";
		} else if ("net_vmware".equals(category)) {
			where = " where managed=1 and category=15 and ostype=40 ";
		} else {
			where = " where managed=1";
		}
		where = where + getBidSql();

		String key = getParaValue("key");

		String value = getParaValue("value");
		if (key != null && key.trim().length() > 0 && value != null && value.trim().length() > 0) {
			where = where + " and " + key + " like '%" + value + "%'";
		}
		HostNodeDao dao = new HostNodeDao();
		try {
			list(dao, where);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		List list = (List) request.getAttribute("list");
		return list;
	}

	/**
	 * 通过 hostNode 来组装 MonitorNodeDTO
	 * 
	 * @param hostNode
	 * @return
	 */
	public MonitorNodeDTO getMonitorNodeDTOByHostNode(HostNode hostNode) {

		Hashtable checkEventHashtable = ShareData.getCheckEventHash();

		NumberFormat df = new DecimalFormat("#.##");

		MonitorNodeDTO monitorNodeDTO = null;

		String ipAddress = hostNode.getIpAddress();
		int nodeId = hostNode.getId();
		String alias = hostNode.getAlias();

		int category = hostNode.getCategory();
		String type = hostNode.getType();
		monitorNodeDTO = new MonitorNetDTO();
		if (category == 1) {
			monitorNodeDTO.setCategory("路由器");
		} else if (category == 2) {
			monitorNodeDTO.setCategory("路由交换机");
		} else if (category == 3) {
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

		// 设置id
		monitorNodeDTO.setId(nodeId);
		// 设置ip
		monitorNodeDTO.setIpAddress(ipAddress);
		// 设置名称
		monitorNodeDTO.setAlias(alias);
		// 类型
		monitorNodeDTO.setType(type);

		monitorNodeDTO.setSubtype(category + "");

		Host node = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		int alarmLevel = 0;
		NodeDTO nodeDTO = null;
		NodeUtil nodeUtil = new NodeUtil();
		nodeDTO = nodeUtil.creatNodeDTOByHost(node);
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

		String cpuValue = "0"; // cpu 默认为 0
		String memoryValue = "0"; // memory 默认为 0
		String virtualMemoryValue = "0"; // memory 默认为 0
		String inutilhdxValue = "0"; // inutilhdx 默认为 0
		String oututilhdxValue = "0"; // oututilhdx 默认为 0
		String pingValue = "0"; // ping 默认为 0
		String collectType = ""; // 采集类型

		String cpuValueColor = "green"; // cpu 颜色
		String memoryValueColor = "green"; // memory 颜色
		String virtualMemoryValueColor = "green"; // memory 颜色

		double cpuValueDouble = 0;
		double memeryValueDouble = 0;
		double virtualMemeryValueDouble = 0;
		int interfaceNubmer = 0;
		String runmodel = PollingEngine.getCollectwebflag();
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipAllData = (Hashtable) sharedata.get(ipAddress);
			Hashtable allpingdata = ShareData.getPingdata();
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
						MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
						if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
							if (category == 4 && hostNode.getSysOid().startsWith("1.3.6.1.4.1.2.3.1.2.")) {// 服务器的情况
								if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
									memeryValueDouble = Double.valueOf(memorydata.getThevalue());
								}
								if (memorydata.getSubentity().equalsIgnoreCase("SwapMemory")) {
									virtualMemeryValueDouble = Double.valueOf(memorydata.getThevalue());
								}
							} else if (category == 4 && hostNode.getType().equalsIgnoreCase("Linux")) {// Linux服务器的情况
								if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
									memeryValueDouble = Double.valueOf(memorydata.getThevalue());
								}
								if (memorydata.getSubentity().equalsIgnoreCase("SwapMemory")) {
									virtualMemeryValueDouble = Double.valueOf(memorydata.getThevalue());
								}
							} else if (category == 4) {
								if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
									memeryValueDouble = Double.valueOf(memorydata.getThevalue());
								}
								if (memorydata.getSubentity().equalsIgnoreCase("VirtualMemory")) {
									virtualMemeryValueDouble = Double.valueOf(memorydata.getThevalue());
								}
							}
							if (category == 1 || category == 2 || category == 3 || category == 11) {// 网络设备的情况
								allmemoryvalue = allmemoryvalue + Integer.parseInt(memorydata.getThevalue());
								if (si == memoryVector.size() - 1) {
									memeryValueDouble = allmemoryvalue / memoryVector.size();
								}
							}
						}
					}
					memoryValue = df.format(memeryValueDouble);
					virtualMemoryValue = df.format(virtualMemeryValueDouble);
				}
				if (category == 8) {
					// 内存信息
					memoryVector = (Vector) ipAllData.get("memory");
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int i = 0; i < memoryVector.size(); i++) {
							MemoryCollectEntity memorycollectdata = (MemoryCollectEntity) memoryVector.get(i);
							allmemoryvalue = allmemoryvalue + Integer.parseInt(memorycollectdata.getThevalue());
						}
						memoryValue = (allmemoryvalue / memoryVector.size()) + "";
					}
				}
				Vector allutil = (Vector) ipAllData.get("allutilhdx");
				if (allutil != null && allutil.size() == 3) {
					AllUtilHdx inutilhdx = (AllUtilHdx) allutil.get(0);
					inutilhdxValue = inutilhdx.getThevalue();

					AllUtilHdx oututilhdx = (AllUtilHdx) allutil.get(1);
					oututilhdxValue = oututilhdx.getThevalue();
				}
			}
			if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype() || SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
				if (ipAllData != null && ipAllData.containsKey("interfaceNumber")) {
					interfaceNubmer = (Integer) ipAllData.get("interfaceNumber");
				}

			}
			if (allpingdata != null) {
				Vector pingData = (Vector) allpingdata.get(ipAddress);
				if (pingData != null && pingData.size() > 0) {
					PingCollectEntity pingcollectdata = (PingCollectEntity) pingData.get(0);
					pingValue = pingcollectdata.getThevalue();
				}
			}
		}

		// 设置采集方式
		if (SystemConstant.COLLECTTYPE_SNMP == hostNode.getCollecttype()) {
			collectType = "SNMP";
		} else if (SystemConstant.COLLECTTYPE_PING == hostNode.getCollecttype()) {
			collectType = "PING";
		} else if (SystemConstant.COLLECTTYPE_REMOTEPING == hostNode.getCollecttype()) {
			collectType = "REMOTEPING";
		} else if (SystemConstant.COLLECTTYPE_SHELL == hostNode.getCollecttype()) {
			collectType = "代理";
		} else if (SystemConstant.COLLECTTYPE_SSH == hostNode.getCollecttype()) {
			collectType = "SSH";
		} else if (SystemConstant.COLLECTTYPE_TELNET == hostNode.getCollecttype()) {
			collectType = "TELNET";
		} else if (SystemConstant.COLLECTTYPE_WMI == hostNode.getCollecttype()) {
			collectType = "WMI";
		} else if (SystemConstant.COLLECTTYPE_DATAINTERFACE == hostNode.getCollecttype()) {
			collectType = "接口";
		}

		// 设定cpu和内存的告警颜色
		double memoryValueDouble = 0;

		String cpuValueStr = monitorNodeDTO.getCpuValue();
		String memoryValueStr = monitorNodeDTO.getMemoryValue();
		if (cpuValueStr != null) {
			cpuValueDouble = Double.parseDouble(cpuValueStr);
		}
		if (memoryValueStr != null) {
			memoryValueDouble = Double.parseDouble(memoryValueStr);
			memoryValueDouble = Double.parseDouble(df.format(memoryValueDouble));
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
					} else {
						cpuValueColor = "green";
					}
				}
				if ("memory".equals(nodeMonitor.getName())) {
					if (Double.parseDouble(memoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
						memoryValueColor = "red";
					} else if (Double.parseDouble(memoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
						memoryValueColor = "orange";
					} else if (Double.parseDouble(memoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
						memoryValueColor = "yellow";
					} else {
						memoryValueColor = "green";
					}
				}
				if ("physicalmemory".equals(nodeMonitor.getName())) {
					if (Double.parseDouble(memoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
						memoryValueColor = "red";
					} else if (Double.parseDouble(memoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
						memoryValueColor = "orange";
					} else if (Double.parseDouble(memoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
						memoryValueColor = "yellow";
					} else {
						memoryValueColor = "green";
					}
				}
				if ("SwapMemory".equals(nodeMonitor.getName())) {
					if (Double.parseDouble(virtualMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue2())) {
						virtualMemoryValueColor = "red";
					} else if (Double.parseDouble(virtualMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue1())) {
						virtualMemoryValueColor = "orange";
					} else if (Double.parseDouble(virtualMemoryValue) > Double.parseDouble(nodeMonitor.getLimenvalue0())) {
						virtualMemoryValueColor = "yellow";
					} else {
						virtualMemoryValueColor = "green";
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
			inutilhdxValue = "0";
			oututilhdxValue = "0";
		}
		monitorNodeDTO.setCpuValue(cpuValue);
		monitorNodeDTO.setMemoryValue(memoryValue);
		monitorNodeDTO.setVirtualMemoryValue(virtualMemoryValue);
		monitorNodeDTO.setInutilhdxValue(inutilhdxValue);
		monitorNodeDTO.setOututilhdxValue(oututilhdxValue);
		monitorNodeDTO.setPingValue(pingValue);
		monitorNodeDTO.setCollectType(collectType);
		monitorNodeDTO.setCpuValueColor(cpuValueColor);
		monitorNodeDTO.setVirtualMemoryValueColor(virtualMemoryValueColor);
		monitorNodeDTO.setMemoryValueColor(memoryValueColor);
		return monitorNodeDTO;
	}

	private List getNodeList(List monitornodelist, List list) {
		if (monitornodelist != null) {
			for (int i = 0; i < monitornodelist.size(); i++) {
				HostNode hostNode = (HostNode) monitornodelist.get(i);
				MonitorNodeDTO monitorNodeDTO = getMonitorNodeDTOByHostNode(hostNode);

				list.add(monitorNodeDTO);
			}
		}

		// 取出接口数量
		HostInterfaceDao hostInterfaceDao = new HostInterfaceDao();
		Hashtable hostInterfacehash = null;
		try {
			hostInterfacehash = hostInterfaceDao.getHostInterfaceList(monitornodelist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostInterfaceDao.close();
		}

		for (int i = 0; i < list.size(); i++) {
			MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO) list.get(i);
			// 接口数量
			if (hostInterfacehash != null && !hostInterfacehash.isEmpty()) {
				Iterator iterator = hostInterfacehash.keySet().iterator();
				while (iterator.hasNext()) {
					String nodeid = (String) iterator.next();
					Integer entityNumber = Integer.parseInt(String.valueOf(hostInterfacehash.get(nodeid)));
					if ((monitorNodeDTO.getId() + "").equals(nodeid)) {
						monitorNodeDTO.setEntityNumber(entityNumber);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * 根据 oids 来判断子类型
	 * 
	 * @param oids
	 *            设备oids
	 * @return 返回子类型
	 */
	public String getSutType(String oids) {
		String subtype = "";
		if (oids.startsWith("1.3.6.1.4.1.311.")) {
			subtype = "windows";
		} else if (oids.startsWith("1.3.6.1.4.1.2021") || oids.startsWith("1.3.6.1.4.1.8072")) {
			subtype = "linux";
		} else if (oids.startsWith("as400")) {
			subtype = "as400";

		} else if (oids.startsWith("1.3.6.1.4.1.42.2.1.1")) {
			subtype = "solaris";
		} else if (oids.startsWith("1.3.6.1.4.1.2.3.1.2.1.1")) {
			subtype = "aix";
		} else if (oids.startsWith("1.3.6.1.4.1.11.2.3.10.1")) {
			subtype = "hpunix";
		} else if (oids.startsWith("1.3.6.1.4.1.9.")) {
			subtype = "cisco";
		} else if (oids.startsWith("1.3.6.1.4.1.25506.") || oids.startsWith("1.3.6.1.4.1.2011.")) {
			subtype = "h3c";
		} else if (oids.startsWith("1.3.6.1.4.1.4881.")) {
			subtype = "redgiant";
		} else if (oids.startsWith("1.3.6.1.4.1.5651.")) {
			subtype = "maipu";
		} else if (oids.startsWith("1.3.6.1.4.1.171.")) {
			subtype = "dlink";
		} else if (oids.startsWith("1.3.6.1.4.1.2272.")) {
			subtype = "northtel";
		} else if (oids.startsWith("1.3.6.1.4.1.89.")) {
			subtype = "radware";
		} else if (oids.startsWith("1.3.6.1.4.1.3320.")) {
			subtype = "bdcom";
		} else if (oids.startsWith("1.3.6.1.4.1.1588.2.1.")) {
			subtype = "brocade";
		}

		return subtype;

	}

	public String hostChoce() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/performance/hostChoce.jsp";
	}

	// quzhi
	private String hostchocereport() {
		String oids = getParaValue("ids");
		if (oids == null) {
			oids = "";
		}
		// SysLogger.info("ids========="+oids);
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0) {
				ids = new Integer[_ids.length];
			}
			for (int i = 0; i < _ids.length; i++) {
				if (_ids[i] == null || _ids[i].trim().length() == 0) {
					continue;
				}
				ids[i] = new Integer(_ids[i]);
			}
		}
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String equipname = "";

		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();

		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					Hashtable reporthash = new Hashtable();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();

					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
					s.append(" and nodeid=" + node.getId());

					List infolist = eventdao.findByCriteria(s.toString());
					int levelone = 0;
					int levletwo = 0;
					int levelthree = 0;
					if (infolist != null && infolist.size() > 0) {

						for (int j = 0; j < infolist.size(); j++) {
							EventList eventlist = (EventList) infolist.get(j);
							if (eventlist.getContent() == null) {
								eventlist.setContent("");
							}
							if (eventlist.getLevel1() == null) {
								continue;
							}
							if (eventlist.getLevel1() == 1) {
								levelone = levelone + 1;
							} else if (eventlist.getLevel1() == 2) {
								levletwo = levletwo + 1;
							} else if (eventlist.getLevel1() == 3) {
								levelthree = levelthree + 1;
							}
						}
					}
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					ip = node.getIpAddress();
					equipname = node.getAlias();
					memhash = hostlastmanager.getMemory_share(ip, "Memory", startdate, todate);
					diskhash = hostlastmanager.getDisk_share(ip, "Disk", startdate, todate);
					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
					Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
					memmaxhash = memoryhash[1];
					memavghash = memoryhash[2];
					maxhash = new Hashtable();
					String cpumax = "";
					String avgcpu = "";
					if (cpuhash.get("max") != null) {
						cpumax = (String) cpuhash.get("max");
					}
					if (cpuhash.get("avgcpucon") != null) {
						avgcpu = (String) cpuhash.get("avgcpucon");

					}
					maxhash.put("cpumax", cpumax);
					maxhash.put("avgcpu", avgcpu);

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					// Hashtable reporthash = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					}

					// CPU
					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null) {
						hdata = new Hashtable();
					}
					Vector cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						for (int si = 0; si < cpuVector.size(); si++) {
							CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
							maxhash.put("cpu", cpudata.getThevalue());
							reporthash.put("CPU", maxhash);
						}
					} else {
						reporthash.put("CPU", maxhash);
					}
					reporthash.put("Memory", memhash);
					reporthash.put("Disk", diskhash);
					reporthash.put("equipname", equipname);
					reporthash.put("memmaxhash", memmaxhash);
					reporthash.put("memavghash", memavghash);
					allreporthash.put(ip, reporthash);
				}

			}
			request.setAttribute("startdate", startdate);
			request.setAttribute("allreporthash", allreporthash);
			request.getSession().setAttribute("allreporthash", allreporthash);
			request.setAttribute("todate", todate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/performance/hostchocereport.jsp";
	}

	private String list() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		request.setAttribute("actionlist", "list");
		setTarget("/performance/list.jsp");
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 ");
		} else {
			return list(dao, "where 1=1 " + s);
		}

	}

	/**
	 * 根据MAC地址 升序排列
	 * 
	 * @return
	 */
	private String listByBrIpAsc() {

		request.setAttribute("actionlist", "listbybripasc");
		setTarget("/performance/list.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 order by bridge_address asc");
		} else {
			return list(dao, "where 1=1 " + s + " order by bridge_address asc");
		}

		// return list(dao,"order by bridge_address asc");
	}

	/**
	 * 根据MAC地址 降序排列
	 * 
	 * @return
	 */
	private String listByBrIpDesc() {

		request.setAttribute("actionlist", "listbybripdesc");
		setTarget("/performance/list.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 order by bridge_address desc");
		} else {
			return list(dao, "where 1=1 " + s + " order by bridge_address desc");
		}
		// return list(dao,"order by bridge_address desc");
	}

	/**
	 * 根据IP地址 升序排列
	 * 
	 * @return
	 */
	private String listByIpAsc() {

		request.setAttribute("actionlist", "listbyipasc");
		setTarget("/performance/list.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 order by ip_address asc");
		} else {
			return list(dao, "where 1=1 " + s + " order by ip_address asc");
		}

		// return list(dao,"order by ip_address asc");
	}

	/**
	 * 根据IP地址 降序排列
	 * 
	 * @return
	 */
	private String listByIpDesc() {

		request.setAttribute("actionlist", "listbyipdesc");
		setTarget("/performance/list.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 order by ip_address desc");
		} else {
			return list(dao, "where 1=1 " + s + " order by ip_address desc");
		}

		// return list(dao,"order by ip_address desc");
	}

	/**
	 * 根据设备名称 升序排列
	 * 
	 * @return
	 */
	private String listByNameAsc() {

		request.setAttribute("actionlist", "listbynameasc");
		setTarget("/performance/list.jsp");

		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 order by sys_name asc");
		} else {
			return list(dao, "where 1=1 " + s + " order by sys_name asc");
		}

		// return list(dao,"order by sys_name asc");
	}

	/**
	 * 根据设备名称 降序排列
	 * 
	 * @return
	 */
	private String listByNameDesc() {

		request.setAttribute("actionlist", "listbynamedesc");
		setTarget("/performance/list.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 order by sys_name desc");
		} else {
			return list(dao, "where 1=1 " + s + " order by sys_name desc");
		}

		// return list(dao,"order by sys_name desc");
	}

	/**
	 * 监视对象 根据IP地址 升序排列
	 * 
	 * @return
	 */
	private String listByNodeAsc() {

		request.setAttribute("actionlist", "listbynodeasc");
		setTarget("/performance/monitornodelist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where managed=1 order by ip_address asc");
		} else {
			return list(dao, "where managed=1 " + s + " order by ip_address asc");
		}

		// return list(dao,"where managed=1 order by ip_address asc");
	}

	/**
	 * 监视对象 根据IP地址 降序排列
	 * 
	 * @return
	 */
	private String listByNodeDesc() {

		request.setAttribute("actionlist", "listbynodedesc");
		setTarget("/performance/monitornodelist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where managed=1 order by ip_address desc");
		} else {
			return list(dao, "where managed=1 " + s + " order by ip_address desc");
		}

		// return list(dao,"where managed=1 order by ip_address desc");
	}

	/**
	 * 监视对象 根据设备名称 升序排列
	 * 
	 * @return
	 */
	private String listByNodeNameAsc() {

		request.setAttribute("actionlist", "listbynodenameasc");
		setTarget("/performance/monitornodelist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where managed=1 order by sys_name asc");
		} else {
			return list(dao, "where managed=1 " + s + " order by sys_name asc");
		}

		// return list(dao,"where managed=1 order by sys_name asc");
	}

	/**
	 * 监视对象 根据设备名称 降序排列
	 * 
	 * @return
	 */
	private String listByNodeNameDesc() {

		request.setAttribute("actionlist", "listbynodenamedesc");
		setTarget("/performance/monitornodelist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where managed=1 order by sys_name desc");
		} else {
			return list(dao, "where managed=1 " + s + " order by sys_name desc");
		}
		// return list(dao,"where managed=1 order by sys_name desc");
	}

	private String menuaddendpoint() {
		String id = getParaValue("id");
		try {
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			host.setEndpoint(1);
			dao = new HostNodeDao();
			try {
				dao.update(host);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			// 更新内存
			Host _host = (Host) PollingEngine.getInstance().getNodeByID(host.getId());
			if (_host != null) {
				_host.setEndpoint(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/perform.do?action=list";
	}

	private String menuaddmanage() {
		String id = getParaValue("id");
		try {
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			host.setManaged(true);
			dao = new HostNodeDao();
			try {
				dao.update(host);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			HostLoader hl = new HostLoader();
			try {
				hl.loadOne(host);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/perform.do?action=list";
	}

	private String menucancelendpoint() {
		String id = getParaValue("id");
		try {
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			host.setEndpoint(0);
			dao = new HostNodeDao();
			try {
				dao.update(host);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			// PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
			// 更新内存
			Host _host = (Host) PollingEngine.getInstance().getNodeByID(host.getId());
			if (_host != null) {
				_host.setEndpoint(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/perform.do?action=list";
	}

	private String menucancelmanage() {
		String id = getParaValue("id");
		try {
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			host.setManaged(false);
			dao = new HostNodeDao();
			try {
				dao.update(host);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/perform.do?action=list";
	}

	/*
	 * 查询设备
	 */
	private String monitorfind() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		HostNodeDao dao = new HostNodeDao();
		List searchList = new ArrayList();
		try {
			searchList = dao.findByCondition(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("list", searchList);

		return "/performance/monitorfind.jsp";
	}

	private String monitorfirewalllist() {
		HostNodeDao dao = new HostNodeDao();
		setTarget("/performance/monitorfirewalllist.jsp");
		return list(dao, " where managed=1 and category=8");
	}

	/**
	 * 主机服务器监控列表
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 */
	private String monitorhostlist() {
		String jsp = "/performance/monitorhostlist.jsp";
		setTarget(jsp);

		List monitorhostlist = getMonitorListByCategory("net_server");

		List list = new ArrayList();

		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);

		if (monitorhostlist != null) {
			for (int i = 0; i < monitorhostlist.size(); i++) {

				HostNode hostNode = (HostNode) monitorhostlist.get(i);
				MonitorNodeDTO monitorHostDTO = getMonitorNodeDTOByHostNode(hostNode);
				list.add(monitorHostDTO);
			}
		}
		String field = getParaValue("field");
		String sorttype = getParaValue("sorttype");
		if (field != null) {
			if (sorttype == null || sorttype.trim().length() == 0) {
				sorttype = "asc";
			} else if ("asc".equals(sorttype)) {
				sorttype = "desc";
			} else if ("desc".equals(sorttype)) {
				sorttype = "asc";
			}

			monitorListSort(list, "host", field, sorttype);

			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		}

		request.setAttribute("list", list);
		return jsp;
	}

	/**
	 * 服务器根据IP 升序
	 * 
	 * @return
	 */
	private String monitorhostlistByIpAsc() {

		request.setAttribute("actionlist", "monitorhostbyipasc");
		setTarget("/performance/monitorhostlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and category=4 order by ip_address asc ");
		} else {
			return list(dao, " where managed=1  " + s + " and category=4 order by ip_address asc ");
		}

	}

	/**
	 * 服务器根据IP 降序
	 * 
	 * @return
	 */
	private String monitorhostlistByIpDesc() {

		request.setAttribute("actionlist", "monitorhostbyipdesc");
		setTarget("/performance/monitorhostlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and category=4 order by ip_address desc ");
		} else {
			return list(dao, " where managed=1  " + s + " and category=4 order by ip_address desc ");
		}

	}

	/**
	 * 服务器根据名称 升序
	 * 
	 * @return
	 */
	private String monitorhostlistByNameAsc() {

		request.setAttribute("actionlist", "monitorhostbynameasc");
		setTarget("/performance/monitorhostlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and category=4 order by alias asc ");
		} else {
			return list(dao, " where managed=1 " + s + " and category=4 order by alias asc ");
		}

	}

	/**
	 * 服务器根据名称 降序
	 * 
	 * @return
	 */
	private String monitorhostlistByNameDesc() {

		request.setAttribute("actionlist", "monitorhostbynamedesc");
		setTarget("/performance/monitorhostlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and category=4 order by alias desc ");
		} else {
			return list(dao, " where managed=1 " + s + " and category=4 order by alias desc ");
		}

	}

	/**
	 * 对监控列表进行排序
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 * @param montinorList
	 *            <code>监控列表</code>
	 * @param category
	 *            <code>设备类型</code>
	 * @param field
	 *            <code>排序字段</code>
	 * @param type
	 *            <code>排序类型</code>
	 * @return
	 */
	public List monitorListSort(List montinorList, String category, String field, String type) {
		for (int i = 0; i < montinorList.size() - 1; i++) {
			for (int j = i + 1; j < montinorList.size(); j++) {
				MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO) montinorList.get(i);
				MonitorNodeDTO monitorNodeDTO2 = (MonitorNodeDTO) montinorList.get(j);

				String fieldValue = "";

				String fieldValue2 = "";
				if ("name".equals(field)) {
					fieldValue = monitorNodeDTO.getAlias();

					fieldValue2 = monitorNodeDTO2.getAlias();
					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) < 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) > 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}

				} else if ("ipaddress".equals(field)) {
					fieldValue = monitorNodeDTO.getIpAddress();

					fieldValue2 = monitorNodeDTO2.getIpAddress();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) < 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) > 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("cpu".equals(field)) {
					fieldValue = monitorNodeDTO.getCpuValue();

					fieldValue2 = monitorNodeDTO2.getCpuValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("ping".equals(field)) {
					fieldValue = monitorNodeDTO.getPingValue();

					fieldValue2 = monitorNodeDTO2.getPingValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("memory".equals(field)) {
					fieldValue = monitorNodeDTO.getMemoryValue();

					fieldValue2 = monitorNodeDTO2.getMemoryValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("virtualMemory".equals(field)) {
					fieldValue = monitorNodeDTO.getVirtualMemoryValue();

					fieldValue2 = monitorNodeDTO2.getVirtualMemoryValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("inutilhdx".equals(field)) {
					fieldValue = monitorNodeDTO.getInutilhdxValue();

					fieldValue2 = monitorNodeDTO2.getInutilhdxValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("oututilhdx".equals(field)) {
					fieldValue = monitorNodeDTO.getOututilhdxValue();

					fieldValue2 = monitorNodeDTO2.getOututilhdxValue();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (Double.valueOf(fieldValue) < Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (Double.valueOf(fieldValue) > Double.valueOf(fieldValue2)) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				} else if ("category".equals(field)) {
					fieldValue = monitorNodeDTO.getCategory();

					fieldValue2 = monitorNodeDTO2.getCategory();

					if ("desc".equals(type)) {
						// 如果是降序 则 前一个 小于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) < 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					} else if ("asc".equals(type)) {
						// 如果是升序 则 前一个 大于 后一个 则交换
						if (fieldValue.compareTo(fieldValue2) > 0) {
							montinorList.set(i, monitorNodeDTO2);
							montinorList.set(j, monitorNodeDTO);
						}
					}
				}

			}
		}
		// }

		return montinorList;
	}

	/**
	 * 网络设备监控列表
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 * @return
	 */
	private String monitornetlist() {
		// HostNodeDao dao = new HostNodeDao();
		// setTarget("/performance/monitorroutelist.jsp");
		// return list(dao," where managed=1 and category=1");

		String jsp = "/performance/monitornetlist.jsp";
		setTarget(jsp);

		List monitornetlist = getMonitorListByCategory("net");

		List list = new ArrayList();

		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);

		if (monitornetlist != null) {
			for (int i = 0; i < monitornetlist.size(); i++) {

				HostNode hostNode = (HostNode) monitornetlist.get(i);

				MonitorNodeDTO monitorNetDTO = getMonitorNodeDTOByHostNode(hostNode);

				list.add(monitorNetDTO);
			}
		}

		String field = getParaValue("field");
		String sorttype = getParaValue("sorttype");
		if (field != null) {
			if (sorttype == null || sorttype.trim().length() == 0) {
				sorttype = "asc";
			} else if ("asc".equals(sorttype)) {
				sorttype = "desc";
			} else if ("desc".equals(sorttype)) {
				sorttype = "asc";
			}

			monitorListSort(list, "net", field, sorttype);

			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		}
		request.setAttribute("list", list);
		return jsp;
	}

	/**
	 * 设备监控列表
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 */
	private String monitornodelist() {

		String category = request.getParameter("category");
		request.setAttribute("category", category);
		List monitornodelist = getMonitorListByCategory(category);
		String jsp = "/performance/monitornodelist.jsp";

		List list = new ArrayList();

		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);

		if ("net_vmware".equals(category) || "net_virtual".equals(category)) {
			VMWareDao vm = new VMWareDao();
			HashMap<String, String> allHostnum = vm.queryAllSize("physical");
			HashMap<String, String> allVMnum = vm.queryAllSize("vmware");
			HashMap<String, String> allPoolnum = vm.queryAllSize("yun");

			String hostnum = "0";
			String vmnum = "0";
			String poolnum = "0";
			VMWareDao vmdao = null;
			VMWareVidDao viddao = null;

			if (monitornodelist != null) {
				try {
					vmdao = new VMWareDao();
					viddao = new VMWareVidDao();

					for (int i = 0; i < monitornodelist.size(); i++) {
						HostNode hostNode = (HostNode) monitornodelist.get(i);
						MonitorNodeDTO monitorNodeDTO = getMonitorNodeDTOByHostNode(hostNode);
						// start存储容量
						double capacity = 0;
						List l = new ArrayList();
						List list_vid = viddao.queryVidFlag("datastore", monitorNodeDTO.getId() + "", "");
						List<HashMap<String, Object>> ds_list = vmdao.getbyvid(list_vid, "vm_basedatastore", monitorNodeDTO.getIpAddress());
						for (int j = 0; j < ds_list.size(); j++) {
							double num = Double.parseDouble(ds_list.get(j).get("capacity").toString().replaceAll(" GB", ""));
							l.add(num);
						}
						for (int j = 0; j < l.size(); j++) {
							capacity += (Double) l.get(j);
						}
						DecimalFormat df = new DecimalFormat("0.00");
						double num = capacity / 1024;
						monitorNodeDTO.setNum(df.format(num));
						// end存储容量

						hostnum = allHostnum.get(hostNode.getId() + "") != null ? allHostnum.get(hostNode.getId() + "") : "0";
						vmnum = allVMnum.get(hostNode.getId() + "") != null ? allVMnum.get(hostNode.getId() + "") : "0";
						poolnum = allPoolnum.get(hostNode.getId() + "") != null ? allPoolnum.get(hostNode.getId() + "") : "0";
						monitorNodeDTO.setHostnum(hostnum);
						monitorNodeDTO.setVMnum(vmnum);
						monitorNodeDTO.setPoolnum(poolnum);
						list.add(monitorNodeDTO);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (vmdao != null) {
						vmdao.close();
					}
					if (viddao != null) {
						viddao.close();
					}
				}

			}
		} else {
			{
				if (monitornodelist != null) {
					for (int i = 0; i < monitornodelist.size(); i++) {
						HostNode hostNode = (HostNode) monitornodelist.get(i);
						MonitorNodeDTO monitorNodeDTO = getMonitorNodeDTOByHostNode(hostNode);
						list.add(monitorNodeDTO);
					}
				}
			}
		}

		// 如果是“采集与访问集成模式”，在上面的for循环中已经做出采集
		String runmodel = PollingEngine.getCollectwebflag();
		if ("1".equals(runmodel)) {
			// 采集与访问是分离模式
			// 取出可用性
			PingInfoService pingInfoService = new PingInfoService();
			List<NodeTemp> pingInfoList = pingInfoService.getPingInfo(monitornodelist);
			// 取出网络设备的CPU利用率信息
			CpuInfoService cpuInfoService = new CpuInfoService();
			List<NodeTemp> cpuInfoList = cpuInfoService.getCpuPerListInfo(monitornodelist);
			// 取出网络设备的内存利用率信息
			MemoryInfoService memoryInfoService = new MemoryInfoService();
			List<NodeTemp> memoryList = memoryInfoService.getMemoryInfo(monitornodelist);
			// 取出端口流速信息
			InterfaceInfoService interfaceInfoService = new InterfaceInfoService();
			List<NodeTemp> interfaceList = interfaceInfoService.getInterfaceInfo(monitornodelist);
			// 将取出的信息组合成List<MonitorNodeDTO>
			for (int i = 0; i < list.size(); i++) {
				MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO) list.get(i);
				if (pingInfoList != null) {
					for (int j = 0; j < pingInfoList.size(); j++) {
						NodeTemp nodeTemp = pingInfoList.get(j);
						if ((monitorNodeDTO.getId() + "").equals(nodeTemp.getNodeid())) {
							if (nodeTemp.getThevalue() != null && Double.parseDouble(nodeTemp.getThevalue()) != 0) {
								monitorNodeDTO.setPingValue("100");
							} else {
								monitorNodeDTO.setPingValue("0");
							}
						}
					}
				}
				if (cpuInfoList != null) {
					for (int j = 0; j < cpuInfoList.size(); j++) {
						NodeTemp nodeTemp = cpuInfoList.get(j);
						if ((monitorNodeDTO.getId() + "").equals(nodeTemp.getNodeid())) {
							monitorNodeDTO.setCpuValue(numberFormat.format(Double.parseDouble(nodeTemp.getThevalue())));
						}
					}
				}
				if (memoryList != null) {
					for (int j = 0; j < memoryList.size(); j++) {
						NodeTemp nodeTemp = memoryList.get(j);
						if ((monitorNodeDTO.getId() + "").equals(nodeTemp.getNodeid())) {
							monitorNodeDTO.setMemoryValue(numberFormat.format(Double.parseDouble(nodeTemp.getThevalue())));
						}
					}
				}
				if (interfaceList != null) {
					for (int j = 0; j < interfaceList.size(); j++) {
						NodeTemp nodeTemp = interfaceList.get(j);
						if ((monitorNodeDTO.getId() + "").equals(nodeTemp.getNodeid())) {
							if (nodeTemp.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx")) {
								monitorNodeDTO.setInutilhdxValue(nodeTemp.getThevalue());
							}
							if (nodeTemp.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")) {
								monitorNodeDTO.setOututilhdxValue(nodeTemp.getThevalue());
							}
						}
					}
				}

			}
		}
		// 取出接口数量
		HostInterfaceDao hostInterfaceDao = new HostInterfaceDao();
		Hashtable hostInterfacehash = null;
		try {
			hostInterfacehash = hostInterfaceDao.getHostInterfaceList(monitornodelist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostInterfaceDao.close();
		}
		// 设定cpu和内存的告警颜色
		double memoryValueDouble = 0;
		for (int i = 0; i < list.size(); i++) {
			MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO) list.get(i);
			String memoryValueStr = monitorNodeDTO.getMemoryValue();
			if (memoryValueStr != null) {
				memoryValueDouble = Double.parseDouble(memoryValueStr);
				memoryValueDouble = Double.parseDouble(numberFormat.format(memoryValueDouble));
			}
			// 接口数量
			if (hostInterfacehash != null && !hostInterfacehash.isEmpty()) {
				Iterator iterator = hostInterfacehash.keySet().iterator();
				while (iterator.hasNext()) {
					String nodeid = (String) iterator.next();
					Integer entityNumber = Integer.parseInt(String.valueOf(hostInterfacehash.get(nodeid)));
					if ((monitorNodeDTO.getId() + "").equals(nodeid)) {
						monitorNodeDTO.setEntityNumber(entityNumber);
					}
				}
			}
		}
		String field = getParaValue("field");
		String sorttype = getParaValue("sorttype");
		if (field != null) {
			if (sorttype == null || sorttype.trim().length() == 0) {
				sorttype = "asc";
			} else if ("asc".equals(sorttype)) {
				sorttype = "desc";
			} else if ("desc".equals(sorttype)) {
				sorttype = "asc";
			}

			monitorListSort(list, "net", field, sorttype);
			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		}
		request.setAttribute("list", list);
		return jsp;

	}

	/**
	 * 设备监控列表
	 * 
	 * @author wxy
	 * @date 2011-06-02
	 */
	private String monitorNodelist() {

		String category = "net";
		request.setAttribute("category", category);

		String jsp = "/equip/assetReport.jsp";

		List monitornodelist = new ArrayList();
		List networkList = new ArrayList();
		List serverList = new ArrayList();
		List dbList = new ArrayList();
		List midwareList = new ArrayList();
		monitornodelist = getMonitorListByCategory("net");

		getNodeList(monitornodelist, networkList);
		monitornodelist = null;
		monitornodelist = getMonitorListByCategory("net_server");
		getNodeList(monitornodelist, serverList);
		monitornodelist = null;
		dbList = getDbList();
		AssetHelper helper = new AssetHelper();
		midwareList = helper.getMidwareList();

		request.setAttribute("networkList", networkList);
		request.setAttribute("serverList", serverList);
		request.setAttribute("dbList", dbList);
		request.setAttribute("midwareList", midwareList);

		return jsp;

	}

	/**
	 * 路由器监控列表
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 */
	private String monitorroutelist() {
		String jsp = "/performance/monitorroutelist.jsp";
		setTarget(jsp);

		List monitornetlist = getMonitorListByCategory("net_router");

		List list = new ArrayList();

		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);

		if (monitornetlist != null) {
			for (int i = 0; i < monitornetlist.size(); i++) {

				HostNode hostNode = (HostNode) monitornetlist.get(i);

				MonitorNodeDTO monitorNetDTO = getMonitorNodeDTOByHostNode(hostNode);

				list.add(monitorNetDTO);
			}
		}
		String field = getParaValue("field");
		String sorttype = getParaValue("sorttype");
		if (field != null) {
			if (sorttype == null || sorttype.trim().length() == 0) {
				sorttype = "asc";
			} else if ("asc".equals(sorttype)) {
				sorttype = "desc";
			} else if ("desc".equals(sorttype)) {
				sorttype = "asc";
			}

			monitorListSort(list, "net", field, sorttype);

			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		}
		request.setAttribute("list", list);
		return jsp;
	}

	/**
	 * 交换机监控列表
	 * 
	 * @author nielin
	 * @date 2010-08-09
	 */
	private String monitorswitchlist() {

		String jsp = "/performance/monitorswitchlist.jsp";
		setTarget(jsp);
		List monitornetlist = getMonitorListByCategory("net_switch");

		List list = new ArrayList();

		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);

		if (monitornetlist != null) {
			for (int i = 0; i < monitornetlist.size(); i++) {

				HostNode hostNode = (HostNode) monitornetlist.get(i);

				MonitorNodeDTO monitorNetDTO = getMonitorNodeDTOByHostNode(hostNode);

				list.add(monitorNetDTO);
			}
		}

		String field = getParaValue("field");
		String sorttype = getParaValue("sorttype");
		if (field != null) {
			if (sorttype == null || sorttype.trim().length() == 0) {
				sorttype = "asc";
			} else if ("asc".equals(sorttype)) {
				sorttype = "desc";
			} else if ("desc".equals(sorttype)) {
				sorttype = "asc";
			}

			monitorListSort(list, "net", field, sorttype);

			request.setAttribute("field", field);
			request.setAttribute("sorttype", sorttype);
		}
		request.setAttribute("list", list);
		return jsp;
	}

	/**
	 * 交换机根据IP 升序
	 * 
	 * @return
	 */
	private String monitorswitchlistByIpAsc() {

		request.setAttribute("actionlist", "monitorswitchbyipasc");
		setTarget("/performance/monitorswitchlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and (category=2 or category=3 or category=7) order by ip_address asc ");
		} else {
			return list(dao, " where managed=1 " + s + " and (category=2 or category=3 or category=7) order by ip_address asc ");
		}

	}

	/**
	 * 交换机根据IP 降序
	 * 
	 * @return
	 */
	private String monitorswitchlistByIpDesc() {

		request.setAttribute("actionlist", "monitorswitchbyipdesc");
		setTarget("/performance/monitorswitchlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and (category=2 or category=3 or category=7) order by ip_address desc ");
		} else {
			return list(dao, " where managed=1 " + s + " and (category=2 or category=3 or category=7) order by ip_address desc ");
		}

	}

	/**
	 * 交换机根据名称 升序
	 * 
	 * @return
	 */
	private String monitorswitchlistByNameAsc() {

		request.setAttribute("actionlist", "monitorswitchbynameasc");
		setTarget("/performance/monitorswitchlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and (category=2 or category=3 or category=7) order by alias asc ");
		} else {
			return list(dao, " where managed=1 " + s + " and (category=2 or category=3 or category=7) order by alias asc ");
		}
	}

	/**
	 * 交换机根据名称 降序
	 * 
	 * @return
	 */
	private String monitorswitchlistByNameDesc() {

		request.setAttribute("actionlist", "monitorswitchbynamedesc");
		setTarget("/performance/monitorswitchlist.jsp");
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, " where managed=1 and (category=2 or category=3 or category=7) order by alias desc ");
		} else {
			return list(dao, " where managed=1 " + s + " and (category=2 or category=3 or category=7) order by alias desc ");
		}

	}

	public String netChoce() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadNetwork(1));
		return "/performance/netChoce.jsp";
	}

	private String netchocereport() {
		String oids = getParaValue("ids");

		if (oids == null) {
			oids = "";
		}
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0) {
				ids = new Integer[_ids.length];
			}
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String equipname = "";

		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Vector vector = new Vector();
		try {
			Hashtable allreporthash = new Hashtable();

			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					if (node == null) {
						continue;
					}
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();
					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
					s.append(" and nodeid=" + node.getId());

					List infolist = eventdao.findByCriteria(s.toString());
					int levelone = 0;
					int levletwo = 0;
					int levelthree = 0;
					if (infolist != null && infolist.size() > 0) {
						for (int j = 0; j < infolist.size(); j++) {
							EventList eventlist = (EventList) infolist.get(j);
							if (eventlist.getContent() == null) {
								eventlist.setContent("");
							}
							if (eventlist.getContent() == null) {
								eventlist.setContent("");
							}

							if (eventlist.getLevel1() != 1) {
								levelone = levelone + 1;
							} else if (eventlist.getLevel1() == 2) {
								levletwo = levletwo + 1;
							} else if (eventlist.getLevel1() == 3) {
								levelthree = levelthree + 1;
							}
						}
					}
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String orderflag = "index";

					String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };

					vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
					PortconfigDao portdao = new PortconfigDao();
					Hashtable portconfigHash = portdao.getIpsHash(ip);
					reporthash.put("portconfigHash", portconfigHash);

					List reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
					reporthash.put("reportports", reportports);

					reporthash.put("netifVector", vector);

					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);

					maxhash = new Hashtable();
					String cpumax = "";
					String avgcpu = "";
					if (cpuhash.get("max") != null) {
						cpumax = (String) cpuhash.get("max");
					}
					if (cpuhash.get("avgcpucon") != null) {
						avgcpu = (String) cpuhash.get("avgcpucon");
					}

					maxhash.put("cpumax", cpumax);
					maxhash.put("avgcpu", avgcpu);
					// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
					Hashtable _IpRouterHash = ShareData.getIprouterdata();
					vector = (Vector) _IpRouterHash.get(ip);
					if (vector != null) {
						reporthash.put("iprouterVector", vector);
					}

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata.getSubentity().equals("ConnectUtilization")) {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);
							}
						}
					}

					// CPU
					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null) {
						hdata = new Hashtable();
					}
					Vector cpuVector = new Vector();
					if (hdata.get("cpu") != null) {
						cpuVector = (Vector) hdata.get("cpu");
					}
					if (cpuVector != null && cpuVector.size() > 0) {
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						maxhash.put("cpu", cpudata.getThevalue());
						reporthash.put("CPU", maxhash);
					} else {
						reporthash.put("CPU", maxhash);
					}// -----流速
					Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
					Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
					String avgput = "";
					if (streaminHash.get("avgput") != null) {
						avgput = (String) streaminHash.get("avgput");
						reporthash.put("avginput", avgput);
					}
					if (streamoutHash.get("avgput") != null) {
						avgput = (String) streamoutHash.get("avgput");
						reporthash.put("avgoutput", avgput);
					}
					Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "max");
					Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "max");
					String maxput = "";
					if (streammaxinHash.get("max") != null) {
						maxput = (String) streammaxinHash.get("max");
						reporthash.put("maxinput", maxput);
					}
					if (streammaxoutHash.get("max") != null) {
						maxput = (String) streammaxoutHash.get("max");
						reporthash.put("maxoutput", maxput);
					}

					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					reporthash.put("equipname", equipname);
					reporthash.put("memmaxhash", memmaxhash);
					reporthash.put("memavghash", memavghash);

					allreporthash.put(ip, reporthash);

				}
			}

			request.setAttribute("startdate", startdate);
			request.setAttribute("allreporthash", allreporthash);
			request.getSession().setAttribute("allreporthash", allreporthash);
			request.setAttribute("todate", todate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/performance/netchocereport.jsp";
	}

	private String panelnodelist() {
		HostNodeDao dao = new HostNodeDao();
		setTarget("/performance/panelnodelist.jsp");
		return list(dao, " where managed=1 and (category<4 or category=7 or category=8)");
	}

	private String read() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/performance/read.jsp");
		return readyEdit(dao);
	}

	/**
	 * @author nielin
	 * @since 2009-12-28
	 * @return add.jsp
	 */
	private String ready_add() {
		List allbuss = null;
		BusinessDao bussdao = new BusinessDao();
		try {
			allbuss = bussdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bussdao.close();
		}
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);

		request.setAttribute("allbuss", allbuss);
		return "/performance/add.jsp";
	}

	private String readyEdit() {

		setTarget("/performance/edit.jsp");
		String nodeId = getParaValue("id");

		NetSyslogNodeRuleDao noderuledao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule noderule = null;
		try {
			noderule = (NetSyslogNodeRule) noderuledao.findByID(nodeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			noderuledao.close();
		}
		if (noderule != null) {
			String nodefacility = noderule.getFacility();
			String[] nodefacilitys = nodefacility.split(",");
			List nodeflist = new ArrayList();
			if (nodefacilitys != null && nodefacilitys.length > 0) {
				for (int i = 0; i < nodefacilitys.length; i++) {
					nodeflist.add(nodefacilitys[i]);
				}
			}
			request.setAttribute("nodefacilitys", nodeflist);
		}
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(nodeId, timeShareConfigUtil.getObjectType("0"));
		request.setAttribute("timeShareConfigList", timeShareConfigList);

		// 提供已选择的供应商信息
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		request.setAttribute("allSupper", allSupper);
		// 提供已设置的采集时间信息
		TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
		List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(nodeId, tg.getObjectType("0"));
		for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
			timeGratherConfig.setHourAndMin();
		}
		request.setAttribute("timeGratherConfigList", timeGratherConfigList);

		DaoInterface dao = new HostNodeDao();
		return readyEdit(dao);
	}

	private String readyEditAlias() {
		HostNodeDao dao = new HostNodeDao();
		String targetJsp = "/performance/editalias.jsp";
		BaseVo vo = null;
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String readyEditSnmp() {
		HostNodeDao dao = new HostNodeDao();
		String targetJsp = "/performance/editsnmp.jsp";
		BaseVo vo = null;
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;

	}

	private String readyEditSysGroup() {
		HostNodeDao dao = new HostNodeDao();
		String targetJsp = "/performance/editsysgroup.jsp";
		BaseVo vo = null;
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String refreshsysname() {
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		try {
			sysName = dao.refreshSysName(getParaIntValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(getParaIntValue("id"));
		if (host != null) {
			host.setSysName(sysName);
			host.setAlias(sysName);
		}

		return "/perform.do?action=list";
	}

	/**
	 * remotePing 方法
	 * 
	 * @return
	 * @author snow
	 */
	private String remotePing() {
		String value = "";
		SnmpPing snmpPing = null;
		String ip = getParaValue("ip");
		try {
			snmpPing = new SnmpPing(getParaValue("ipaddress"), "161");
			String community = getParaValue("community");
			if (community == null || "".equals(community)) {
				request.setAttribute("pingResult", "团体名为空，请配置团体名称");
				return "/tool/remotePing2.jsp";
			}

			String version = getParaValue("version");
			if (version != null && (version.equals("v2") || version.equals("V2"))) {
				System.out.println("you have set your version to v2");
				snmpPing.setVersion(1);
			} else {
				snmpPing.setVersion(0);
			}

			snmpPing.setCommunity(community); // 写团体
			snmpPing.setTimeout("5000"); // 单位 ms
			// 0 ， 1 ， 3
			value = snmpPing.ping(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (snmpPing != null) {
				snmpPing.close(); // 关闭snmp的连接
			}
		}

		StringBuffer pingResult = new StringBuffer("<br>Snmp RemotePing Ip：" + ip + "<br>");
		if (value == null) {
			pingResult.append("传入ip为空");
		} else if ("Null".equals(value)) {
			pingResult.append("该IP无法ping通");
		} else if ("Uncertain".equals(value)) {
			pingResult.append("结果不确定，可能在某处出现异常");
		} else if (value.matches("\\d+")) {
			pingResult.append("平均响应时间为： " + value + " 毫秒");
		} else {
			pingResult.append("出现异常");
		}
		request.setAttribute("pingResult", pingResult.toString());
		return "/tool/remotePing2.jsp";
	}

	private String save() {
		String xmlString = request.getParameter("hidXml");
		String vlanString = request.getParameter("vlan");
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		if (vlanString != null && vlanString.equals("1")) {
			xmlOpr.setFile("networkvlan.jsp");
		} else {
			xmlOpr.setFile("network.jsp");
		}
		xmlOpr.saveImage(xmlString);

		return "/performance/save.jsp";
	}

	private String telnet() {
		request.setAttribute("ipaddress", getParaValue("ipaddress"));

		return "/tool/telnet.jsp";
	}

	private String update() {
		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAssetid(getParaValue("assetid"));
		vo.setLocation(getParaValue("location"));
		vo.setAlias(getParaValue("alias"));
		vo.setManaged(getParaIntValue("managed") == 1 ? true : false);
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supper"));// snow add at 2010-5-18
		vo.setBid(getParaValue("bid"));

		String ipaddress = getParaValue("ipaddress");
		vo.setIpAddress(ipaddress);
		// 更新内存
		String formerip = "";
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		if (host != null) {
			host.setAlias(vo.getAlias());
			host.setManaged(vo.isManaged());
			host.setSendemail(vo.getSendemail());
			host.setSendmobiles(vo.getSendmobiles());
			host.setSupperid(vo.getSupperid());
		} else {
			if (getParaIntValue("managed") == 1) {
				HostNodeDao dao = new HostNodeDao();
				HostNode hostnode = null;
				try {
					hostnode = dao.loadHost(vo.getId());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
				hostnode.setAlias(getParaValue("alias"));
				hostnode.setManaged(getParaIntValue("managed") == 1 ? true : false);
				hostnode.setSendemail(getParaValue("sendemail"));
				hostnode.setSendmobiles(getParaValue("sendmobiles"));
				hostnode.setSendphone(getParaValue("sendphone"));
				hostnode.setSupperid(getParaIntValue("supper"));
				HostLoader loader = new HostLoader();
				try {
					loader.loadOne(hostnode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 重新获取一下内存中的对象
		host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());

		if (!host.getIpAddress().equalsIgnoreCase(ipaddress)) {
			// IP地址已经被修改,需要更新相关的IP关联的信息
			formerip = host.getIpAddress();

			String ip = formerip;
			String allipstr = SysUtil.doip(ip);

			CreateTableManager ctable = new CreateTableManager();
			try {
				if (host.getCategory() < 4 || host.getCategory() == 7 || host.getCategory() == 8 || host.getCategory() == 9) {
					// 先删除网络设备表
					// 连通率
					try {
						ctable.deleteTable("ping", allipstr, "ping");// Ping
						ctable.deleteTable("pinghour", allipstr, "pinghour");// PingHour
						ctable.deleteTable("pingday", allipstr, "pingday");// PingDay
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 内存
					try {
						ctable.deleteTable("memory", allipstr, "mem");// 内存
						ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
						ctable.deleteTable("memoryday", allipstr, "memday");// 内存
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("flash", allipstr, "flash");// 闪存
						ctable.deleteTable("flashhour", allipstr, "flashhour");// 闪存
						ctable.deleteTable("flashday", allipstr, "flashday");// 闪存
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("buffer", allipstr, "buffer");// 缓存
						ctable.deleteTable("bufferhour", allipstr, "bufferhour");// 缓存
						ctable.deleteTable("bufferday", allipstr, "bufferday");// 缓存
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("fan", allipstr, "fan");// 风扇
						ctable.deleteTable("fanhour", allipstr, "fanhour");// 风扇
						ctable.deleteTable("fanday", allipstr, "fanday");// 风扇
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("power", allipstr, "power");// 电源
						ctable.deleteTable("powerhour", allipstr, "powerhour");// 电源
						ctable.deleteTable("powerday", allipstr, "powerday");// 电源
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("vol", allipstr, "vol");// 电压
						ctable.deleteTable("volhour", allipstr, "volhour");// 电压
						ctable.deleteTable("volday", allipstr, "volday");// 电压
					} catch (Exception e) {
						e.printStackTrace();
					}

					// CPU
					try {
						ctable.deleteTable("cpu", allipstr, "cpu");// CPU
						ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 带宽利用率
					try {
						ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
						ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
						ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 流速
					try {
						ctable.deleteTable("utilhdx", allipstr, "hdx");
						ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
						ctable.deleteTable("utilhdxday", allipstr, "hdxday");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 综合流速
					try {
						ctable.deleteTable("allutilhdx", allipstr, "allhdx");
						ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
						ctable.deleteTable("autilhdxd", allipstr, "ahdxd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 丢包率
					try {
						ctable.deleteTable("discardsperc", allipstr, "dcardperc");
						ctable.deleteTable("dcarperh", allipstr, "dcarperh");
						ctable.deleteTable("dcarperd", allipstr, "dcarperd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 错误率
					try {
						ctable.deleteTable("errorsperc", allipstr, "errperc");
						ctable.deleteTable("errperch", allipstr, "errperch");
						ctable.deleteTable("errpercd", allipstr, "errpercd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 数据包
					try {
						ctable.deleteTable("packs", allipstr, "packs");
						ctable.deleteTable("packshour", allipstr, "packshour");
						ctable.deleteTable("packsday", allipstr, "packsday");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 入口数据包
					try {
						ctable.deleteTable("inpacks", allipstr, "inpacks");
						ctable.deleteTable("ipacksh", allipstr, "ipacksh");
						ctable.deleteTable("ipackd", allipstr, "ipackd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 出口数据包
					try {
						ctable.deleteTable("outpacks", allipstr, "outpacks");
						ctable.deleteTable("opackh", allipstr, "opackh");
						ctable.deleteTable("opacksd", allipstr, "opacksd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 温度
					try {
						ctable.deleteTable("temper", allipstr, "temper");
						ctable.deleteTable("temperh", allipstr, "temperh");
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
						// 同时删除事件表里的相关数据
						eventdao.delete(host.getId(), "network");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventdao.close();
					}

					PortconfigDao portconfigdao = new PortconfigDao();
					try {
						// 同时删除端口配置表里的相关数据
						portconfigdao.deleteByIpaddress(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						portconfigdao.close();
					}

					// 删除IP-MAC-BASE表里的对应的数据
					IpMacChangeDao macchangebasedao = new IpMacChangeDao();
					try {

						macchangebasedao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						macchangebasedao.close();
					}

					// 删除网络设备配置文件表里的对应的数据
					NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
					try {

						dao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}

					// 删除网络设备SYSLOG接收表里的对应的数据
					NetSyslogDao syslogdao = new NetSyslogDao();
					try {

						syslogdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						syslogdao.close();
					}

					// 删除网络设备端口扫描表里的对应的数据
					PortScanDao portscandao = new PortScanDao();
					try {

						portscandao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						portscandao.close();
					}

					// 删除网络设备面板图表里的对应的数据
					IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
					try {

						addresspaneldao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						addresspaneldao.close();
					}

					// 删除网络设备接口表里的对应的数据
					HostInterfaceDao interfacedao = new HostInterfaceDao();
					try {

						interfacedao.deleteByHostId(host.getId() + "");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						interfacedao.close();
					}

					// 删除网络设备IP别名表里的对应的数据
					IpAliasDao ipaliasdao = new IpAliasDao();
					try {

						ipaliasdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ipaliasdao.close();
					}

					// 删除网络设备手工配置的链路表里的对应的数据
					RepairLinkDao repairdao = new RepairLinkDao();
					try {

						repairdao.updatestartlinkip(host.getIpAddress(), ipaddress);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						repairdao.close();
					}
					// 删除网络设备手工配置的链路表里的对应的数据
					repairdao = new RepairLinkDao();
					try {

						repairdao.updateendlinkip(host.getIpAddress(), ipaddress);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						repairdao.close();
					}

					// 删除网络设备IPMAC表里的对应的数据
					IpMacDao ipmacdao = new IpMacDao();
					try {

						ipmacdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ipmacdao.close();
					}
					// 删除nms_ipmacchange表里的对应的数据
					IpMacBaseDao macbasedao = new IpMacBaseDao();
					try {

						macbasedao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						macbasedao.close();
					}
					host.setIpAddress(ipaddress);
					vo.setIpAddress(ipaddress);
					ip = ipaddress;
					allipstr = SysUtil.doip(ip);
					try {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

						ctable.createTable("memory", allipstr, "mem");// 内存
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

						ctable.createTable("flash", allipstr, "flash");// 闪存
						ctable.createTable("flashhour", allipstr, "flashhour");// 闪存
						ctable.createTable("flashday", allipstr, "flashday");// 闪存

						ctable.createTable("buffer", allipstr, "buffer");// 缓存
						ctable.createTable("bufferhour", allipstr, "bufferhour");// 缓存
						ctable.createTable("bufferday", allipstr, "bufferday");// 缓存

						ctable.createTable("fan", allipstr, "fan");// 风扇
						ctable.createTable("fanhour", allipstr, "fanhour");// 风扇
						ctable.createTable("fanday", allipstr, "fanday");// 风扇

						ctable.createTable("power", allipstr, "power");// 电源
						ctable.createTable("powerhour", allipstr, "powerhour");// 电源
						ctable.createTable("powerday", allipstr, "powerday");// 电源

						ctable.createTable("vol", allipstr, "vol");// 电压
						ctable.createTable("volhour", allipstr, "volhour");// 电压
						ctable.createTable("volday", allipstr, "volday");// 电压

						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						ctable.createTable("utilhdxperc", allipstr, "hdperc");
						ctable.createTable("hdxperchour", allipstr, "hdperchour");
						ctable.createTable("hdxpercday", allipstr, "hdpercday");

						ctable.createTable("utilhdx", allipstr, "hdx");
						ctable.createTable("utilhdxhour", allipstr, "hdxhour");
						ctable.createTable("utilhdxday", allipstr, "hdxday");

						ctable.createTable("allutilhdx", allipstr, "allhdx");
						ctable.createTable("autilhdxh", allipstr, "ahdxh");
						ctable.createTable("autilhdxd", allipstr, "ahdxd");

						ctable.createTable("discardsperc", allipstr, "dcardperc");
						ctable.createTable("dcarperh", allipstr, "dcarperh");
						ctable.createTable("dcarperd", allipstr, "dcarperd");

						ctable.createTable("errorsperc", allipstr, "errperc");
						ctable.createTable("errperch", allipstr, "errperch");
						ctable.createTable("errpercd", allipstr, "errpercd");

						ctable.createTable("packs", allipstr, "packs");
						ctable.createTable("packshour", allipstr, "packshour");
						ctable.createTable("packsday", allipstr, "packsday");

						ctable.createTable("inpacks", allipstr, "inpacks");
						ctable.createTable("ipacksh", allipstr, "ipacksh");
						ctable.createTable("ipackd", allipstr, "ipackd");

						ctable.createTable("outpacks", allipstr, "outpacks");
						ctable.createTable("opackh", allipstr, "opackh");
						ctable.createTable("opacksd", allipstr, "opacksd");

						ctable.createTable("temper", allipstr, "temper");
						ctable.createTable("temperh", allipstr, "temperh");
						ctable.createTable("temperd", allipstr, "temperd");
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		String[] fs = getParaArrayValue("fcheckbox");
		String faci_str = "";
		if (fs != null && fs.length > 0) {
			for (int i = 0; i < fs.length; i++) {
				String fa = fs[i];
				faci_str = faci_str + fa + ",";
			}
		}

		NetSyslogNodeRuleDao nodeRuleDao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule noderule = null;
		try {
			noderule = (NetSyslogNodeRule) nodeRuleDao.findByID(vo.getId() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strSql = "";
		if (noderule == null) {
			strSql = "insert into nms_netsyslogrule_node(id,nodeid,facility)values(0,'" + vo.getId() + "','" + faci_str + "')";
		} else {
			strSql = "update nms_netsyslogrule_node set facility='" + faci_str + "' where nodeid='" + vo.getId() + "'";
		}
		try {
			nodeRuleDao.saveOrUpdate(strSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeRuleDao.close();
		}

		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		try {
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
		try {
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		vo.setEndpoint(host.getEndpoint());
		// 更新数据库
		DaoInterface dao = new HostNodeDao();
		setTarget("/perform.do?action=list");
		return update(dao, vo);
	}

	private String updatealias() {
		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setBid(getParaValue("bid"));
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		host.setAlias(vo.getAlias());
		host.setManaged(vo.isManaged());
		host.setSendemail(vo.getSendemail());
		host.setSendmobiles(vo.getSendmobiles());
		host.setBid(vo.getBid());
		vo.setManaged(true);

		// 更新数据库
		DaoInterface dao = new HostNodeDao();
		setTarget("/perform.do?action=list");
		return update(dao, vo);
	}

	// quzhi
	private String updateBid() {
		String ids = request.getParameter("ids");
		String[] businessids = getParaArrayValue("checkboxbid");
		String[] bids = ids.split(",");
		if (bids != null && bids.length > 0) {
			// 进行修改
			for (int i = 0; i < bids.length; i++) {
				String hostid = bids[i];
				String allbid = ",";
				if (businessids != null && businessids.length > 0) {
					for (int j = 0; j < businessids.length; j++) {
						String bid = businessids[j];
						allbid = allbid + bid + ",";
					}
					HostNodeDao dao = new HostNodeDao();
					try {
						dao.updatebid(allbid, hostid);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dao.close();
					}
					// 更新内存
					Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(hostid));
					if (host != null) {
						host.setBid(allbid);
					}
				}
			}
		}
		return "/perform.do?action=list";
	}

	public String updatemac() {
		HostNode vo = new HostNode();
		int id = getParaIntValue("id");
		String mac = getParaValue("mac");
		HostNodeDao hostdao = new HostNodeDao();
		hostdao.UpdateAixMac(id, mac);
		String id2 = "net" + id;

		DaoInterface dao = new HostNodeDao();
		setTarget("/detail/dispatcher.jsp?flag=1&id=" + id2);
		return update(dao, vo);
	}

	private String updatesnmp() {
		HostNode vo = new HostNode();
		HostNodeDao dao = new HostNodeDao();
		try {
			vo = dao.loadHost(getParaIntValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		vo.setId(getParaIntValue("id"));
		vo.setCommunity(getParaValue("readcommunity"));
		vo.setWriteCommunity(getParaValue("writecommunity"));
		vo.setSnmpversion(getParaIntValue("snmpversion"));

		dao = new HostNodeDao();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		host.setCommunity(getParaValue("readcommunity"));
		host.setWritecommunity(getParaValue("writecommunity"));
		host.setSnmpversion(getParaIntValue("snmpversion"));
		try {
			dao.updatesnmp(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/performance/networkview.jsp?id=" + vo.getId() + "&ipaddress=" + vo.getIpAddress();
	}

	private String updatesysgroup() {
		HostNode vo = new HostNode();
		HostNodeDao dao = new HostNodeDao();
		try {
			vo = dao.loadHost(getParaIntValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		vo.setId(getParaIntValue("id"));
		vo.setSysName(getParaValue("sysname"));
		vo.setSysContact(getParaValue("syscontact"));
		vo.setSysLocation(getParaValue("syslocation"));

		Hashtable mibvalues = new Hashtable();
		mibvalues.put("sysContact", getParaValue("syscontact"));
		mibvalues.put("sysName", getParaValue("sysname"));
		mibvalues.put("sysLocation", getParaValue("syslocation"));

		dao = new HostNodeDao();
		boolean flag = false;
		try {
			flag = dao.updatesysgroup(vo, mibvalues);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (flag) {
			// 更新内存
			Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
			host.setSysName(getParaValue("sysname"));
			host.setSysContact(getParaValue("syscontact"));
			host.setSysLocation(getParaValue("syslocation"));
		}
		return "/performance/networkview.jsp?id=" + vo.getId() + "&ipaddress=" + vo.getIpAddress();
	}
}
