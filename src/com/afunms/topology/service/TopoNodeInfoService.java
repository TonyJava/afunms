package com.afunms.topology.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ShareData;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.topology.util.NodeHelper;

@SuppressWarnings("unchecked")
public class TopoNodeInfoService {

	private static TopoNodeInfoService instance;

	public static TopoNodeInfoService getInstance() {
		if (instance == null) {
			syncInit();

		}

		return instance;

	}

	private static synchronized void syncInit() {

		if (instance == null) {

			instance = new TopoNodeInfoService();

		}

	}

	private TopoNodeInfoService() {

	}

	public String getAlarmInfo(Host nodes) {
		List alarmList = nodes.getAlarmMessage();
		String alarmmessage = "";
		if (alarmList != null && alarmList.size() > 0) {
			for (int k = 0; k < alarmList.size(); k++) {
				alarmmessage = alarmmessage + alarmList.get(k) + "<br>";
			}
		}
		return alarmmessage;
	}

	public String getBusClor(String value) {
		String st1 = "<table border=0 height=13 width='100%' bgcolor=#CAE6FA>" + "<tr><td width='50%'><table width='100%' height=13><tr><td>������:</td><td>" + value
				+ "%</td></tr></table>" + "<td width='50%'><table width='100%' height=12><tr><td width='" + value + "%' bgcolor='green'></td>" + "<td width='"
				+ (100 - Integer.parseInt(value)) + "%' bgcolor=red></td></tr></table></td></tr></table>";
		return st1;
	}

	public String getBusClor1(String value) {
		String st1 = "<table border=0 height=13 width='100%' bgcolor=#CAE6FA>" + "<tr><td width='50%'><table width='100%' height=13><tr><td>������:</td><td>" + value
				+ "%</td></tr></table>" + "<td width='50%'><table width='100%' height=12><tr><td width='" + value + "%' bgcolor='green'></td>" + "<td width='"
				+ (100 - Integer.parseInt(value)) + "%' bgcolor=red></td></tr></table></td></tr></table>";
		return st1;
	}

	private String getClor(String value) {
		String st1 = "<table border=0 height=13 width='100%' bgcolor=#CAE6FA>" + "<tr><td width='50%'>������:</td><td>" + value + "%</td>" + "<td width='" + value
				+ "%' bgcolor='green'></td>" + "<td width='" + (100 - Integer.parseInt(value)) + "%' bgcolor=red></td></tr></table>";
		return st1;
	}

	public String getNodeInfo(List moidList, Host nodes) {
		if (nodes.getCategory() <= 12) {
			return getPerformanceInfo(moidList, nodes);
		} else {
			return getPerformanceInfo(moidList, nodes) + "<br>" + getAlarmInfo(nodes);
		}
	}

	public String getOtherNodeInfo(List listalarm, Node nodes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer msg = new StringBuffer(300);
		StringBuffer alarmMsg = new StringBuffer(200);
		if (listalarm != null && listalarm.size() > 0) {
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			for (int i = 0; i < listalarm.size(); i++) {
				try {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) listalarm.get(i);
					if (alarmIndicatorsNode != null) {
						int flag = 0;
						if (checkEventHash != null && checkEventHash.size() > 0) {
							if (checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
									+ alarmIndicatorsNode.getName()) != null) {
								flag = (Integer) checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
										+ alarmIndicatorsNode.getName()); 
							}
						}
						try {
							if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
								if (flag > 0) {
									// �и澯����
									alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (alarmIndicatorsNode.getType().equalsIgnoreCase("db") && alarmIndicatorsNode.getName().equalsIgnoreCase("tablespace")) {
							String chexkname = nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName()
									+ ":";
							for (Iterator it = checkEventHash.keySet().iterator(); it.hasNext();) {
								String key = (String) it.next();
								if (key.startsWith(chexkname)) {
									alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (alarmMsg.toString().length() > 0) {
			Calendar date = Calendar.getInstance();
			Date c1 = date.getTime();
			String lastTime = sdf.format(c1);
			msg.append("<font color='red'>--������Ϣ:--</font><br>");
			msg.append(alarmMsg.toString());
			msg.append("����ʱ��:" + lastTime);
		}
		return msg.toString();
	};

	public String getPerformanceInfo(List moidList, Host nodes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		double cpuvalue = 0;
		double memoryvalue = 0;
		String pingvalue = null;
		String inhdx = "0";
		String outhdx = "0";
		String time = "";
		String lastTime = "";
		Vector ipPingData = null;
		Vector memoryVector = null;
		Vector cpuV = null;
		Vector diskVector = null;
		Vector interfaceVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag();
		Hashtable ipAllData = null;
		String ipAddress = nodes.getIpAddress();
		if ("0".equals(runmodel)) {
			// �ɼ�������Ǽ���ģʽ
			ipAllData = (Hashtable) ShareData.getSharedata().get(ipAddress);
			ipPingData = (Vector) ShareData.getPingdata().get(ipAddress);
		} else {
			// �ɼ�������Ƿ���ģʽ
			ipAllData = (Hashtable) ShareData.getAllNetworkData().get(ipAddress);
			ipPingData = (Vector) ShareData.getAllNetworkPingData().get(ipAddress);
		}

		if (ipAllData != null) {
			cpuV = (Vector) ipAllData.get("cpu");
			memoryVector = (Vector) ipAllData.get("memory");
			diskVector = (Vector) ipAllData.get("disk");
			Vector allutil = (Vector) ipAllData.get("allutilhdx");
			// �õ�ϵͳ����ʱ��
			if (allutil != null && allutil.size() == 3) {
				AllUtilHdx inutilhdx = (AllUtilHdx) allutil.get(0);
				inhdx = inutilhdx.getThevalue();
				AllUtilHdx oututilhdx = (AllUtilHdx) allutil.get(1);
				outhdx = oututilhdx.getThevalue();
			}
			interfaceVector = (Vector) ipAllData.get("interface");
		}
		if (cpuV != null && cpuV.size() > 0) {
			CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
			if (cpu != null && cpu.getThevalue() != null) {
				cpuvalue = new Double(cpu.getThevalue());
			}
		}
		if (memoryVector != null && memoryVector.size() > 0) {
			MemoryCollectEntity memory = (MemoryCollectEntity) memoryVector.get(0);
			if (memory != null && memory.getThevalue() != null) {
				memoryvalue = new Double(memory.getThevalue());
			}
		}
		if (ipPingData != null && ipPingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) ipPingData.get(0);
			pingvalue = pingdata.getThevalue();
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			time = sdf.format(cc);
			lastTime = time;
		}

		StringBuffer msg = new StringBuffer(300);
		msg.append("<font color='green'>����:");
		msg.append(NodeHelper.getNodeCategory(nodes.getCategory()));
		msg.append("</font><br>");
		if (nodes.getCategory() == 4) {
			msg.append("��������");
			msg.append(nodes.getSysName());
			msg.append("<br>����:");
			msg.append(nodes.getAlias());
			msg.append("<br>");
		} else {
			msg.append("�豸��ǩ:");
			msg.append(nodes.getAlias());
			msg.append("<br>");
		}
		msg.append("IP��ַ:");
		msg.append(ipAddress);
		msg.append("<br>");
		msg.append("�豸λ��:");
		msg.append(nodes.getLocation());
		msg.append("<br>");
		msg.append("------------------------------");
		msg.append("<br>");

		if (!nodes.isManaged()) {
			return msg.toString();
		}
		if (pingvalue == null || pingvalue.trim().length() == 0) {
			pingvalue = "0";
		}
		StringBuffer alarmMsg = new StringBuffer(200);
		if (moidList != null && moidList.size() > 0) {
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			try {
				for (int i = 0; i < moidList.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) moidList.get(i);
					if (alarmIndicatorsNode != null) {
						int flag = 0;
						try {
							if (checkEventHash != null && checkEventHash.size() > 0) {
								if (checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
										+ alarmIndicatorsNode.getName()) != null) {
									flag = (Integer) checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
											+ alarmIndicatorsNode.getName());
								}
							}
							if (alarmIndicatorsNode.getType().equalsIgnoreCase("net")) {
								// �����豸
								if (alarmIndicatorsNode.getName().equals("cpu")) {
									// CPU������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
										}
										msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("memory")) {
									// CPU������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
										}
										msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("ping")) {
									// ������ͨ��
									if (pingvalue == null || pingvalue.trim().length() == 0) {
										// û�ж�PING���ݽ��вɼ�,����Ҫ�澯���
										pingvalue = "0";
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											msg.append(getClor(pingvalue));
										}
									} else {
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (flag > 0) {
												// �и澯����
												alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
											}
											msg.append(getClor(pingvalue));
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")) {
									// �ӿ���Ϣ
									// �������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (inhdx != null) {
											msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "��ǰֵ:" + inhdx + " ��ֵ:" + alarmIndicatorsNode.getLimenvalue0() + "<br>");
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")) {
									// �ӿ���Ϣ
									// ��������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (outhdx != null) {
											msg.append(alarmIndicatorsNode.getDescr() + ":" + outhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "��ǰֵ:" + outhdx + " ��ֵ:" + alarmIndicatorsNode.getLimenvalue0() + "<br>");
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("interface")) {
									// �ӿ���Ϣ

									if (interfaceVector != null && interfaceVector.size() > 0) {
										// �ӿ�DOWN�澯
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											List portconfiglist = new ArrayList();
											Portconfig portconfig = null;
											Hashtable allportconfighash = ShareData.getPortConfigHash();
											;
											if (allportconfighash != null && allportconfighash.size() > 0) {
												if (allportconfighash.containsKey(ipAddress)) {
													portconfiglist = (List) allportconfighash.get(ipAddress);
												}
											}
											Hashtable portconfigHash = new Hashtable();
											if (portconfiglist != null && portconfiglist.size() > 0) {
												for (int j = 0; j < portconfiglist.size(); j++) {
													portconfig = (Portconfig) portconfiglist.get(j);
													portconfigHash.put(portconfig.getPortindex() + "", portconfig);
												}
											}
											portconfig = null;

											for (int m = 0; m < interfaceVector.size(); m++) {
												Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.get(m);
												if (interfacedata != null) {

													if (interfacedata.getCategory().equalsIgnoreCase("Interface") && interfacedata.getEntity().equalsIgnoreCase("ifOperStatus")
															&& interfacedata.getSubentity() != null) {
														if (portconfigHash.containsKey(interfacedata.getSubentity())) {
															// ���ڶ˿�����,���ж��Ƿ�DOWN
															portconfig = (Portconfig) portconfigHash.get(interfacedata.getSubentity());
															if (!"up".equalsIgnoreCase(interfacedata.getThevalue())) {
																// �и澯����
																alarmMsg.append("�˿� " + portconfig.getName() + " " + portconfig.getLinkuse() + " down" + "<br>");
															}
														}
													}
												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifInMulticastPkts")) {
										// ��ڵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("inpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													InPkts inpacks = (InPkts) tempv.elementAt(k);
													if (inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + inpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifInBroadcastPkts")) {
										// ��ڷǵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("inpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													InPkts inpacks = (InPkts) tempv.elementAt(k);
													if (inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + inpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("ifOutMulticastPkts")) {
										// ���ڵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("outpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													OutPkts outpacks = (OutPkts) tempv.elementAt(k);
													if (outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + outpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("ifOutBroadcastPkts")) {
										// ���ڷǵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("outpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													OutPkts outpacks = (OutPkts) tempv.elementAt(k);
													if (outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + outpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}
												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("discardsperc")) {
										// �˿ڶ�����
										Hashtable sharedata = ShareData.getSharedata();
										Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
										if (ipdata == null) {
											ipdata = new Hashtable();
										}
										Vector tempv = (Vector) ipdata.get("discardsperc");
										if (tempv != null && tempv.size() > 0) {
											for (int k = 0; k < tempv.size(); k++) {
												DiscardsPerc discardsPerc = (DiscardsPerc) tempv.elementAt(k);
												if (flag > 0) {
													// �и澯����
													alarmMsg.append("��" + discardsPerc.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
															+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
												} else {
													if (discardsPerc.getThevalue() != null) {
														msg.append(alarmIndicatorsNode.getDescr() + ":" + discardsPerc.getThevalue() + " "
																+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("errorsperc")) {
										// �˿ڴ�����
										Hashtable sharedata = ShareData.getSharedata();
										Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
										if (ipdata == null) {
											ipdata = new Hashtable();
										}
										Vector tempv = (Vector) ipdata.get("errorsperc");
										if (tempv != null && tempv.size() > 0) {
											for (int k = 0; k < tempv.size(); k++) {
												ErrorsPerc errorsPerc = (ErrorsPerc) tempv.elementAt(k);
												if (flag > 0) {
													// �и澯����
													alarmMsg.append("��" + errorsPerc.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
															+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
												} else {
													if (errorsPerc.getThevalue() != null) {
														msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " " + alarmIndicatorsNode.getThreshlod_unit()
																+ "<br>");
													}
												}
											}
										}
									}
								}
							} else if (alarmIndicatorsNode.getType().equalsIgnoreCase("firewall")) {
								// �����豸
								if (alarmIndicatorsNode.getName().equals("cpu")) {
									// CPU������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
										}
										msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("memory")) {
									// CPU������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
										}
										msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("ping")) {
									// ������ͨ��
									if (pingvalue == null || pingvalue.trim().length() == 0) {
										// û�ж�PING���ݽ��вɼ�,����Ҫ�澯���
										pingvalue = "0";
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											msg.append(getClor(pingvalue));
										}
									} else {
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (flag > 0) {
												// �и澯����
												alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
											}
											msg.append(getClor(pingvalue));
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")) {
									// �ӿ���Ϣ
									// �������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (inhdx != null) {
											msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "��ǰֵ:" + inhdx + " ��ֵ:" + alarmIndicatorsNode.getLimenvalue0() + "<br>");
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")) {
									// �ӿ���Ϣ
									// ��������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (inhdx != null) {
											msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "��ǰֵ:" + inhdx + " ��ֵ:" + alarmIndicatorsNode.getLimenvalue0() + "<br>");
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("interface")) {
									// �ӿ���Ϣ
									if (interfaceVector != null && interfaceVector.size() > 0) {
										// �ӿ�DOWN�澯
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											List portconfiglist = new ArrayList();
											Portconfig portconfig = null;
											Hashtable allportconfighash = ShareData.getPortConfigHash();
											;
											if (allportconfighash != null && allportconfighash.size() > 0) {
												if (allportconfighash.containsKey(nodes.getIpAddress())) {
													portconfiglist = (List) allportconfighash.get(nodes.getIpAddress());
												}
											}
											Hashtable portconfigHash = new Hashtable();
											if (portconfiglist != null && portconfiglist.size() > 0) {
												for (int k = 0; k < portconfiglist.size(); k++) {
													portconfig = (Portconfig) portconfiglist.get(k);
													portconfigHash.put(portconfig.getPortindex() + "", portconfig);
												}
											}
											portconfig = null;
											for (int m = 0; m < interfaceVector.size(); m++) {
												Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.get(m);
												if (interfacedata != null) {
													if (interfacedata.getCategory().equalsIgnoreCase("Interface") && interfacedata.getEntity().equalsIgnoreCase("ifOperStatus")
															&& interfacedata.getSubentity() != null) {
														if (portconfigHash.containsKey(interfacedata.getSubentity())) {
															// ���ڶ˿�����,���ж��Ƿ�DOWN
															portconfig = (Portconfig) portconfigHash.get(interfacedata.getSubentity());
															if (!"up".equalsIgnoreCase(interfacedata.getThevalue())) {
																// �и澯����
																alarmMsg.append("�˿� " + portconfig.getName() + " " + portconfig.getLinkuse() + " down" + "<br>");
															}
														}
													}
												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("ifInMulticastPkts")) {
										// ��ڵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("inpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													InPkts inpacks = (InPkts) tempv.elementAt(k);
													if (inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + inpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}
												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("ifInBroadcastPkts")) {
										// ��ڷǵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("inpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													InPkts inpacks = (InPkts) tempv.elementAt(k);
													if (inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + inpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("ifOutMulticastPkts")) {
										// ���ڵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("outpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													OutPkts outpacks = (OutPkts) tempv.elementAt(k);
													if (outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + outpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifOutBroadcastPkts")) {
										// ���ڷǵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("outpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													OutPkts outpacks = (OutPkts) tempv.elementAt(k);
													if (outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + outpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("discardsperc")) {
										// �˿ڶ�����
										Hashtable sharedata = ShareData.getSharedata();
										Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
										if (ipdata == null) {
											ipdata = new Hashtable();
										}
										Vector tempv = (Vector) ipdata.get("discardsperc");
										if (tempv != null && tempv.size() > 0) {
											for (int k = 0; k < tempv.size(); k++) {
												DiscardsPerc discardsPerc = (DiscardsPerc) tempv.elementAt(k);
												if (flag > 0) {
													// �и澯����
													alarmMsg.append("��" + discardsPerc.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
															+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
												} else {
													if (discardsPerc.getThevalue() != null) {
														msg.append(alarmIndicatorsNode.getDescr() + ":" + discardsPerc.getThevalue() + " "
																+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}

											}
										}
									} else if (alarmIndicatorsNode.getName().equals("errorsperc")) {
										// �˿ڴ�����
										Hashtable sharedata = ShareData.getSharedata();
										Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
										if (ipdata == null) {
											ipdata = new Hashtable();
										}
										Vector tempv = (Vector) ipdata.get("errorsperc");
										if (tempv != null && tempv.size() > 0) {
											for (int k = 0; k < tempv.size(); k++) {
												ErrorsPerc errorsPerc = (ErrorsPerc) tempv.elementAt(k);
												if (flag > 0) {
													// �и澯����
													alarmMsg.append("��" + errorsPerc.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
															+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
												} else {
													if (errorsPerc.getThevalue() != null) {
														msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " " + alarmIndicatorsNode.getThreshlod_unit()
																+ "<br>");
													}
												}
											}
										}
									}
								}
							} else if (alarmIndicatorsNode.getType().equalsIgnoreCase("host")) {

								// �����豸
								if (alarmIndicatorsNode.getName().equals("cpu")) {
									// CPU������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
										}
										msg.append(alarmIndicatorsNode.getDescr() + ":" + cpuvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("memory")) {
									// CPU������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
										}
										msg.append(alarmIndicatorsNode.getDescr() + ":" + memoryvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("ping")) {
									// ������ͨ��
									if (pingvalue == null || pingvalue.trim().length() == 0) {
										// û�ж�PING���ݽ��вɼ�,����Ҫ�澯���
										pingvalue = "0";
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											msg.append(getClor(pingvalue));
										}
									} else {
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (flag > 0) {
												// �и澯����
												alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
											}
											msg.append(getClor(pingvalue));
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("diskperc")) {
									try {
										// ������Ϣ
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (diskVector != null && diskVector.size() > 0) {
												Hashtable alldiskalarmdata = new Hashtable();
												try {
													alldiskalarmdata = ShareData.getAlldiskalarmdata();
												} catch (Exception e) {
													e.printStackTrace();
												}
												if (alldiskalarmdata == null) {
													alldiskalarmdata = new Hashtable();
												}
												for (int si = 0; si < diskVector.size(); si++) {
													DiskCollectEntity diskdata = null;
													diskdata = (DiskCollectEntity) diskVector.elementAt(si);
													if (diskdata.getEntity().equalsIgnoreCase("Utilization")) {
														// ������
														flag = 0;
														if (nodes.getOstype() == 5 || nodes.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
															if (checkEventHash != null && checkEventHash.size() > 0) {
																if (checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype()
																		+ ":" + alarmIndicatorsNode.getName() + ":" + diskdata.getSubentity().substring(0, 3)) != null) {
																	flag = (Integer) checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":"
																			+ alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName() + ":"
																			+ diskdata.getSubentity().substring(0, 3));// elist.size();
																}
															}
														} else {
															if (checkEventHash != null && checkEventHash.size() > 0) {
																if (checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype()
																		+ ":" + alarmIndicatorsNode.getName() + ":" + diskdata.getSubentity()) != null) {
																	flag = (Integer) checkEventHash.get(nodes.getId() + ":" + alarmIndicatorsNode.getType() + ":"
																			+ alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName() + ":"
																			+ diskdata.getSubentity());// elist.size();
																}
															}
														}
														if (flag > 0) {
															// �и澯����
															Diskconfig diskconfig = null;
															if (nodes.getOstype() == 5 || nodes.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
																diskconfig = (Diskconfig) alldiskalarmdata.get(ipAddress + ":" + diskdata.getSubentity().substring(0, 3) + ":"
																		+ "��������ֵ");
															} else {
																diskconfig = (Diskconfig) alldiskalarmdata.get(ipAddress + ":" + diskdata.getSubentity() + ":" + "��������ֵ");
															}

															int limevalue = 0;
															if (flag == 1) {
																limevalue = diskconfig.getLimenvalue();
															} else if (flag == 2) {
																limevalue = diskconfig.getLimenvalue1();
															} else {
																limevalue = diskconfig.getLimenvalue2();
															}

															if (nodes.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
																// �и澯����
																alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + " " + diskdata.getSubentity().substring(0, 3) + " ��ֵ:"
																		+ limevalue + "<br>");
															} else {
																// �и澯����
																alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + " " + diskdata.getSubentity() + " ��ֵ:" + limevalue + "<br>");
															}
														}
														if (nodes.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
															// WINDOWS������
															msg.append(alarmIndicatorsNode.getDescr() + ":" + diskdata.getSubentity().substring(0, 3) + " "
																	+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(), 2) + " "
																	+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
														} else {
															// UNIX������
															msg.append(alarmIndicatorsNode.getDescr() + ":" + diskdata.getSubentity() + " "
																	+ CEIString.round(new Double(diskdata.getThevalue()).doubleValue(), 2) + " "
																	+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
														}
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equalsIgnoreCase("physicalmemory")) {
									try {
										// �����ڴ���Ϣ
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (memoryVector != null && memoryVector.size() > 0) {
												for (int si = 0; si < memoryVector.size(); si++) {
													MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
													if (memorydata.getEntity().equalsIgnoreCase("Utilization") && memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
														// ������
														if (flag > 0) {
															// �и澯����
															alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
														}
														msg.append(alarmIndicatorsNode.getDescr() + ":" + memorydata.getSubentity() + " "
																+ CEIString.round(new Double(memorydata.getThevalue()).doubleValue(), 2) + " "
																+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equalsIgnoreCase("virtualmemory")) {
									try {
										// �����ڴ���Ϣ
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (memoryVector != null && memoryVector.size() > 0) {
												for (int si = 0; si < memoryVector.size(); si++) {
													MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
													if (memorydata.getEntity().equalsIgnoreCase("Utilization") && memorydata.getSubentity().equalsIgnoreCase("VirtualMemory")) {
														// ������
														if (flag > 0) {
															// �и澯����
															alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
														}
														msg.append(alarmIndicatorsNode.getDescr() + ":" + memorydata.getSubentity() + " "
																+ CEIString.round(new Double(memorydata.getThevalue()).doubleValue(), 2) + " "
																+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equalsIgnoreCase("swapmemory")) {
									try {
										// �����ڴ���Ϣ
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (memoryVector != null && memoryVector.size() > 0) {
												for (int si = 0; si < memoryVector.size(); si++) {
													MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
													if (memorydata.getEntity().equalsIgnoreCase("Utilization") && memorydata.getSubentity().equalsIgnoreCase("SwapMemory")) {
														// ������
														if (flag > 0) {
															// �и澯����
															alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
														}
														msg.append(alarmIndicatorsNode.getDescr() + ":" + memorydata.getSubentity() + " "
																+ CEIString.round(new Double(memorydata.getThevalue()).doubleValue(), 2) + " "
																+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")) {// �ӿ���Ϣ
									// �������
									if (inhdx != null) {
										msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")) {
									// ��������
									if (outhdx != null) {
										msg.append(alarmIndicatorsNode.getDescr() + ":" + outhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")) {
									// �ӿ���Ϣ
									// �������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (inhdx != null) {
											msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "��ǰֵ:" + inhdx + " ��ֵ:" + alarmIndicatorsNode.getLimenvalue0() + "<br>");
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")) {
									// �ӿ���Ϣ
									// ��������
									if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
										if (inhdx != null) {
											msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
										if (flag > 0) {
											// �и澯����
											alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "��ǰֵ:" + inhdx + " ��ֵ:" + alarmIndicatorsNode.getLimenvalue0() + "<br>");
										}
									}
								} else if (alarmIndicatorsNode.getName().equals("interface")) {
									// �ӿ���Ϣ

									if (interfaceVector != null && interfaceVector.size() > 0) {
										// �ӿ�DOWN�澯
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											List portconfiglist = new ArrayList();
											Portconfig portconfig = null;
											Hashtable allportconfighash = ShareData.getPortConfigHash();
											;
											if (allportconfighash != null && allportconfighash.size() > 0) {
												if (allportconfighash.containsKey(nodes.getIpAddress())) {
													portconfiglist = (List) allportconfighash.get(nodes.getIpAddress());
												}
											}
											Hashtable portconfigHash = new Hashtable();
											if (portconfiglist != null && portconfiglist.size() > 0) {
												for (int k = 0; k < portconfiglist.size(); k++) {
													portconfig = (Portconfig) portconfiglist.get(k);
													portconfigHash.put(portconfig.getPortindex() + "", portconfig);
												}
											}
											portconfig = null;

											for (int m = 0; m < interfaceVector.size(); m++) {
												Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.get(m);
												if (interfacedata != null) {

													if (interfacedata.getCategory().equalsIgnoreCase("Interface") && interfacedata.getEntity().equalsIgnoreCase("ifOperStatus")
															&& interfacedata.getSubentity() != null) {
														if (portconfigHash.containsKey(interfacedata.getSubentity())) {
															// ���ڶ˿�����,���ж��Ƿ�DOWN
															portconfig = (Portconfig) portconfigHash.get(interfacedata.getSubentity());
															if (!"up".equalsIgnoreCase(interfacedata.getThevalue())) {
																// �и澯����
																alarmMsg.append("�˿� " + portconfig.getName() + " " + portconfig.getLinkuse() + " down" + "<br>");
															}
														}
													}
												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifInMulticastPkts")) {
										// ��ڵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("inpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													InPkts inpacks = (InPkts) tempv.elementAt(k);
													if (inpacks.getEntity().equalsIgnoreCase("ifInMulticastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + inpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifInBroadcastPkts")) {
										// ��ڷǵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("inpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													InPkts inpacks = (InPkts) tempv.elementAt(k);
													if (inpacks.getEntity().equalsIgnoreCase("ifInBroadcastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + inpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifOutMulticastPkts")) {
										// ���ڵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("outpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													OutPkts outpacks = (OutPkts) tempv.elementAt(k);
													if (outpacks.getEntity().equalsIgnoreCase("ifOutMulticastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + outpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}

									} else if (alarmIndicatorsNode.getName().equals("ifOutBroadcastPkts")) {
										// ���ڷǵ���
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											Hashtable sharedata = ShareData.getSharedata();
											Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
											if (ipdata == null) {
												ipdata = new Hashtable();
											}
											Vector tempv = (Vector) ipdata.get("outpacks");
											if (tempv != null && tempv.size() > 0) {
												for (int k = 0; k < tempv.size(); k++) {
													OutPkts outpacks = (OutPkts) tempv.elementAt(k);
													if (outpacks.getEntity().equalsIgnoreCase("ifOutBroadcastPkts")) {
														if (flag > 0) {
															// �и澯����
															alarmMsg.append("��" + outpacks.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
																	+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
														}
													}

												}
											}
										}
									} else if (alarmIndicatorsNode.getName().equals("discardsperc")) {
										// �˿ڶ�����
										Hashtable sharedata = ShareData.getSharedata();
										Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
										if (ipdata == null) {
											ipdata = new Hashtable();
										}
										Vector tempv = (Vector) ipdata.get("discardsperc");
										if (tempv != null && tempv.size() > 0) {
											for (int k = 0; k < tempv.size(); k++) {
												DiscardsPerc discardsPerc = (DiscardsPerc) tempv.elementAt(k);
												if (flag > 0) {
													// �и澯����
													alarmMsg.append("��" + discardsPerc.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
															+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
												} else {
													if (discardsPerc.getThevalue() != null) {
														msg.append(alarmIndicatorsNode.getDescr() + ":" + discardsPerc.getThevalue() + " "
																+ alarmIndicatorsNode.getThreshlod_unit() + "<br>");
													}
												}

											}
										}
									} else if (alarmIndicatorsNode.getName().equals("errorsperc")) {
										// �˿ڴ�����
										Hashtable sharedata = ShareData.getSharedata();
										Hashtable ipdata = (Hashtable) sharedata.get(ipAddress);
										if (ipdata == null) {
											ipdata = new Hashtable();
										}
										Vector tempv = (Vector) ipdata.get("errorsperc");
										if (tempv != null && tempv.size() > 0) {
											for (int k = 0; k < tempv.size(); k++) {
												ErrorsPerc errorsPerc = (ErrorsPerc) tempv.elementAt(k);
												if (flag > 0) {
													// �и澯����
													alarmMsg.append("��" + errorsPerc.getSubentity() + "�˿�" + alarmIndicatorsNode.getAlarm_info()
															+ alarmIndicatorsNode.getLimenvalue0() + "<br>");
												} else {
													if (errorsPerc.getThevalue() != null) {
														msg.append(alarmIndicatorsNode.getDescr() + ":" + errorsPerc.getThevalue() + " " + alarmIndicatorsNode.getThreshlod_unit()
																+ "<br>");
													}
												}
											}
										}
									}
								}
							} else if (alarmIndicatorsNode.getType().equalsIgnoreCase("virtual")) {
								// ���⻯�豸
								if (alarmIndicatorsNode.getName().equals("ping")) {
									try {
										// ��ͨ��
										if ("1".equalsIgnoreCase(alarmIndicatorsNode.getEnabled())) {
											if (pingvalue == null || pingvalue.trim().length() == 0) {
												pingvalue = "0";
											} else {
												if (flag > 0) {
													// �и澯����
													alarmMsg.append(alarmIndicatorsNode.getAlarm_info() + "<br>");
												}
											}
											msg.append(alarmIndicatorsNode.getDescr() + ":" + pingvalue + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

								} else if (alarmIndicatorsNode.getName().equals("diskperc")) {
									try {
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equalsIgnoreCase("physicalmemory")) {
									try {
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equalsIgnoreCase("virtualmemory")) {
									try {
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equalsIgnoreCase("swapmemory")) {
									try {
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (alarmIndicatorsNode.getName().equals("AllInBandwidthUtilHdx")) {// �ӿ���Ϣ
									// �������
									if (inhdx != null) {
										msg.append(alarmIndicatorsNode.getDescr() + ":" + inhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("AllOutBandwidthUtilHdx")) {
									// ��������
									if (outhdx != null) {
										msg.append(alarmIndicatorsNode.getDescr() + ":" + outhdx + " " + alarmIndicatorsNode.getThreshlod_unit() + "<br>");
									}
								} else if (alarmIndicatorsNode.getName().equals("process")) {
								} else if (alarmIndicatorsNode.getName().equals("interface")) {
								} else if (alarmIndicatorsNode.getName().equals("responsetime")) {
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (alarmMsg.toString().length() > 0) {
			msg.append("<font color='red'>----������Ϣ--------------</font><br>");
			msg.append(alarmMsg.toString());
		}
		msg.append("------------------------------");
		msg.append("<br>");
		msg.append("����ʱ��:" + lastTime);
		return msg.toString();
	}

}
