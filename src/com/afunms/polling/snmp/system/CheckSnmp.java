package com.afunms.polling.snmp.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.PingCollectEntity;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;

@SuppressWarnings("unchecked")
public class CheckSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector snmpVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}

		try {
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { "1.3.6.1.2.1.1.1", "1.3.6.1.2.1.1.3", "1.3.6.1.2.1.1.4", "1.3.6.1.2.1.1.5", "1.3.6.1.2.1.1.6", "1.3.6.1.2.1.1.7"
				// "1.3.6.1.2.1.2.2.1.6"
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
					valueArray = null;
				}
				if (valueArray != null) {
					// snmp服务开启
					PingCollectEntity hostdata = null;
					hostdata = new PingCollectEntity();
					hostdata.setIpaddress(node.getIpAddress());
					hostdata.setCollecttime(date);
					hostdata.setCategory("SNMPPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("0");
					snmpVector.add(hostdata);
				} else {
					// snmp服务未开启
					PingCollectEntity hostdata = null;
					hostdata = new PingCollectEntity();
					hostdata.setIpaddress(node.getIpAddress());
					hostdata.setCollecttime(date);
					hostdata.setCategory("SNMPPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("1");
					snmpVector.add(hostdata);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows", "snmp");
			for (int i = 0; i < snmpVector.size(); i++) {
				PingCollectEntity pingdata = (PingCollectEntity) snmpVector.elementAt(i);
				if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
					for (int m = 0; m < list.size(); m++) {
						AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(m);
						if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
							if (_alarmIndicatorsNode.getName().equalsIgnoreCase("snmp")) {
								CheckEventUtil checkeventutil = new CheckEventUtil();
								// SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
								checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
							}
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (snmpVector != null && snmpVector.size() > 0) {
				ipAllData.put("snmp", snmpVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (snmpVector != null && snmpVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("snmp", snmpVector);
			}
		}

		returnHash.put("snmp", snmpVector);
		snmpVector = null;
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostDatatempSystemRttosql tosql = new NetHostDatatempSystemRttosql();
			tosql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
