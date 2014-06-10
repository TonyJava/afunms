package com.afunms.polling.snmp.vpn;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.ArrayVPNSystem;

@SuppressWarnings("unchecked")
public class ArrayVPNSystemSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {// Òª¸ÄÎªAlarmIndicatorsNode
		// alarmIndicatorsNode
		Hashtable returnHash = new Hashtable();
		Vector powerVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}
		try {

			Calendar date = Calendar.getInstance();
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}

			try {
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.7564.30.2", "1.3.6.1.4.1.7564.30.3"

				};

				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						ArrayVPNSystem arrayVPNSystem = new ArrayVPNSystem();
						String connectionsPerSec = valueArray[i][0];
						String requestsPerSec = valueArray[i][1];
						arrayVPNSystem.setRequestsPerSec(Integer.parseInt(requestsPerSec));
						arrayVPNSystem.setConnectionsPerSec(Integer.parseInt(connectionsPerSec));
						arrayVPNSystem.setIpaddress(node.getIpAddress());
						arrayVPNSystem.setCollecttime(date);
						arrayVPNSystem.setType("VPN");
						arrayVPNSystem.setSubtype("ArrayNetworks");
						powerVector.addElement(arrayVPNSystem);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("VPNSystem", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("VPNSystem", powerVector);
		return returnHash;
	}

}
