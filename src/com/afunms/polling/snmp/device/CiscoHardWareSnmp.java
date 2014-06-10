package com.afunms.polling.snmp.device;

/*
 * cisco光纤交换机硬件信息
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.snmp.SnmpMibConstants;

@SuppressWarnings("unchecked")
public class CiscoHardWareSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector hardwareVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;

		try {
			SystemCollectEntity systemdata = null;
			Calendar date = Calendar.getInstance();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// start
			try {
				final String[] desc = SnmpMibConstants.NetWorkMibHardwareDesc;
				final String[] chname = SnmpMibConstants.NetWorkMibHardwareChname;
				String[] oids = new String[] { "1.3.6.1.2.1.47.1.1.1.1.2.278", "1.3.6.1.2.1.47.1.1.1.1.2.279", "1.3.6.1.2.1.47.1.1.1.1.2.470", "1.3.6.1.2.1.47.1.1.1.1.2.471" };

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray != null && valueArray.length > 0) {
					for (int i = 0; i < valueArray.length; i++) {
						for (int j = 0; j < 6; j++) {
							systemdata = new SystemCollectEntity();
							systemdata.setIpaddress(node.getIpAddress());
							systemdata.setCollecttime(date);
							systemdata.setCategory("device");
							systemdata.setEntity(desc[i]);
							systemdata.setSubentity(desc[j]);
							systemdata.setChname(chname[j]);
							systemdata.setRestype("static");
							systemdata.setUnit("");
							String value = valueArray[i][j];
							if (j == 0) {
								systemdata.setThevalue(value);
							} else
								systemdata.setThevalue(value);
							hardwareVector.addElement(systemdata);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (hardwareVector != null && hardwareVector.size() > 0)
				ipAllData.put("device", hardwareVector);

			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (hardwareVector != null && hardwareVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("device", hardwareVector);
		}

		returnHash.put("device", hardwareVector);
		return returnHash;
	}
}
