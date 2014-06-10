package com.afunms.polling.snmp.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.ProcessCollectEntity;

@SuppressWarnings("unchecked")
public class NokiaProcessSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		Hashtable processHash = new Hashtable();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (host == null) {
			return returnHash;
		}

		try {
			ProcessCollectEntity processdata = new ProcessCollectEntity();
			Calendar date = Calendar.getInstance();
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				String[] oids = new String[] { "1.3.6.1.4.1.94.1.21.1.7.2.1.1", // 进程id
						"1.3.6.1.4.1.94.1.21.1.7.2.1.2",// PID
						"1.3.6.1.4.1.94.1.21.1.7.2.1.3",// 用户
						"1.3.6.1.4.1.94.1.21.1.7.2.1.4",// 内存占有量
						"1.3.6.1.4.1.94.1.21.1.7.2.1.5"// cpu利用率
				};
				String[][] valueArray1 = null;
				try {
					valueArray1 = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(),
							host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
					valueArray1 = null;
				}
				int allMemorySize = 0;
				int allUsedMemorySize = 0;
				if (valueArray1 != null) {
					for (int i = 0; i < valueArray1.length; i++) {
						String svb0 = valueArray1[i][0];
						if (svb0 == null) {
							continue;
						}
						allMemorySize = Integer.parseInt(svb0);
					}
				}

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), 3, 5000);
				} catch (Exception e) {
					e.printStackTrace();
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						if (allMemorySize != 0) {
							String vbstring0 = valueArray[i][0];
							String vbstring1 = valueArray[i][1];
							String vbstring2 = valueArray[i][2];
							String vbstring3 = valueArray[i][3];
							String vbstring4 = valueArray[i][4];
							String processID = vbstring0.trim();
							Vector processVector = new Vector();
							float value = 0.0f;
							value = Integer.parseInt(vbstring3.trim()) * 100.0f / (allMemorySize * 1000);
							allUsedMemorySize = allUsedMemorySize + Integer.parseInt(vbstring3.trim());
							String processName = vbstring0.trim();
							processdata = new ProcessCollectEntity();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("MemoryUtilization");
							processdata.setSubentity(processID);
							processdata.setRestype("dynamic");
							processdata.setUnit("%");
							processdata.setThevalue(Float.toString(value));
							processdata.setChname(processName);
							processVector.addElement(processdata);

							String processMemory = vbstring3.trim();
							processdata = new ProcessCollectEntity();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("Memory");
							processdata.setSubentity(processID);
							processdata.setRestype("static");
							processdata.setUnit("K");
							processdata.setThevalue(processMemory);
							processdata.setChname(processName);
							processVector.addElement(processdata);

							String ppid = vbstring1.trim();
							processdata = new ProcessCollectEntity();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("PPID");
							processdata.setSubentity(processID);
							processdata.setRestype("static");
							processdata.setUnit(" ");
							processdata.setThevalue(ppid);
							processdata.setChname(processName);
							processVector.addElement(processdata);

							processdata = new ProcessCollectEntity();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("User");
							processdata.setSubentity(processID);
							processdata.setRestype("static");
							processdata.setUnit(" ");
							processdata.setThevalue(vbstring2);
							processVector.addElement(processdata);

							String processCpu = vbstring4.trim();
							processdata = new ProcessCollectEntity();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("CpuUtilization");
							processdata.setSubentity(processID);
							processdata.setRestype("static");
							processdata.setUnit("%");
							processdata.setThevalue(processCpu);
							processdata.setChname(processName);
							processVector.addElement(processdata);

							processHash.put(processID, processVector);
						} else {
							throw new Exception("Process is 0");
						}
					}
				}
				if (allUsedMemorySize > 0) {
					int intvalue = allUsedMemorySize * 100 / allMemorySize;
					MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
					memorycollectdata.setIpaddress(host.getIpAddress());
					memorycollectdata.setCollecttime(date);
					memorycollectdata.setCategory("Memory");
					memorycollectdata.setEntity("AllSize");
					memorycollectdata.setSubentity("1");
					memorycollectdata.setRestype("dynamic");
					memorycollectdata.setUnit("%");
					memorycollectdata.setThevalue(intvalue + "");
					memoryVector.addElement(memorycollectdata);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (memoryVector != null && memoryVector.size() > 0) {
				ipAllData.put("memory", memoryVector);
			}
			if (processHash != null && processHash.size() > 0) {
				ipAllData.put("process", processHash);
			}
			ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		} else {
			if (memoryVector != null && memoryVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("memory", memoryVector);
			}
			if (processHash != null && processHash.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("process", processHash);
			}
		}

		returnHash.put("process", processHash);
		returnHash.put("memory", memoryVector);

		List proEventList = new ArrayList();
		boolean alarm = false;
		if (proEventList != null && proEventList.size() > 0) {
			alarm = true;
		}
		if (alarm) {
			Host node = (Host) PollingEngine.getInstance().getNodeByID(host.getId());
			StringBuffer msg = new StringBuffer(200);
			msg.append("<font color='red'>--报警信息:--</font><br>");
			msg.append(node.getAlarmMessage().toString());
			if (proEventList != null && proEventList.size() > 0) {
				for (int i = 0; i < proEventList.size(); i++) {
					EventList eventList = (EventList) proEventList.get(i);
					msg.append(eventList.getContent() + "<br>");
					if (eventList.getLevel1() > node.getAlarmlevel()) {
						node.setAlarmlevel(eventList.getLevel1());
					}
				}
			}
			node.getAlarmMessage().clear();
			node.getAlarmMessage().add(msg.toString());
			node.setStatus(node.getAlarmlevel());
			node.setAlarm(true);
		}
		return returnHash;
	}

}
