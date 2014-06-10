package com.afunms.polling.snmp.battery;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class EmsBatterySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector batteryVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));

		if (node == null) {
			return null;
		}
		SystemCollectEntity systemdata = null;
		Calendar date = Calendar.getInstance();
		try {
			final String[] desc = SnmpMibConstants.UpsMibBatteryDesc;
			final String[] chname = SnmpMibConstants.UpsMibBatteryChname;
			final String[] unit = SnmpMibConstants.UpsMibBatteryUnit;
			String[] valueArray = new String[7];
			if (node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")) {//
				String[] oids = new String[] { ".1.3.6.1.4.1.13400.2.1.3.3.2.2.1.0",// 电池电压
						".1.3.6.1.4.1.13400.2.1.3.3.2.2.2.0",// 电池电流
						".1.3.6.1.4.1.13400.2.1.3.3.2.2.3.0",// 电池剩余后备时间
						".1.3.6.1.4.1.13400.2.1.3.3.2.2.4.0",// 电池温度
						".1.3.6.1.4.1.13400.2.1.3.3.2.2.5.0",// 环境温度
						".1.3.6.1.2.1.2.2.1.2.1",// 设备描述
						".1.3.6.1.4.1.13400.2.1.2.1.1.2.0"// 设备名称
				};// UPS电池信息
				for (int j = 0; j < oids.length; j++) {
					try {
						valueArray[j] = snmp.getMibValue(node.getIpAddress(), node.getCommunity(), oids[j]);
					} catch (Exception e) {
						valueArray = null;
						e.printStackTrace();
					}
				}
			}
			if (valueArray != null && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					systemdata = new SystemCollectEntity();
					systemdata.setIpaddress(node.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("Battery");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("static");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					if (i == 5 || i == 6) {
						systemdata.setThevalue(value);
					} else {
						if (value != null && !value.equals("noSuchObject")) {
							systemdata.setThevalue((Float.parseFloat(value) / 10) + "");
						} else {
							systemdata.setThevalue("");
						}
					}
					batteryVector.addElement(systemdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("battery", batteryVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("battery", batteryVector);

		Hashtable ipdata = new Hashtable();
		ipdata.put("battery", returnHash);
		Hashtable alldata = new Hashtable();
		alldata.put(node.getIpAddress(), ipdata);
		HostCollectDataManager hostdataManager = new HostCollectDataManager();
		try {
			hostdataManager.createHostItemData(alldata, "ups");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 告警
		try {
			if (batteryVector != null || batteryVector.size() < 0) {
				for (int i = 0; i < batteryVector.size(); i++) {
					SystemCollectEntity collectdata = (SystemCollectEntity) batteryVector.get(i);
					if (collectdata.getSubentity().equals("DCDY")) {
						HostNodeDao hostnodedao = new HostNodeDao();
						HostNode hostnode = (HostNode) hostnodedao.findByID(node.getId() + "");
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(hostnode);
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
						CheckEventUtil checkEventUtil = new CheckEventUtil();
						for (int j = 0; j < list.size(); j++) {
							AlarmIndicatorsNode alarIndicatorsNode = (AlarmIndicatorsNode) list.get(j);
							if ("batteryvoltage".equalsIgnoreCase(alarIndicatorsNode.getName())) {
								checkEventUtil.checkEvent(node, alarIndicatorsNode, Math.abs(Float.parseFloat(collectdata.getThevalue()) - 404.6) + "");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnHash;
	}
}
