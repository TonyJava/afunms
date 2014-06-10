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
import com.afunms.polling.om.ArrayVPNVClientApp;

@SuppressWarnings("unchecked")
public class ArrayVPNVClientAppSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
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
				String[] oids = new String[] { "1.3.6.1.4.1.7564.35.1.2.1.1", "1.3.6.1.4.1.7564.35.1.2.1.2", "1.3.6.1.4.1.7564.35.1.2.1.3", "1.3.6.1.4.1.7564.35.1.2.1.4" };
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						ArrayVPNVClientApp arrayVPNVClientApp = new ArrayVPNVClientApp();
						String vclientAppIndex = valueArray[i][0];
						String vclientAppVirtualSite = valueArray[i][1];
						String vclientAppBytesIn = valueArray[i][2];
						String vclientAppBytesOut = valueArray[i][3];
						arrayVPNVClientApp.setVclientAppBytesIn(Long.parseLong(vclientAppBytesIn));
						arrayVPNVClientApp.setVclientAppBytesOut(Long.parseLong(vclientAppBytesOut));
						arrayVPNVClientApp.setVclientAppIndex(Integer.parseInt(vclientAppIndex));
						arrayVPNVClientApp.setVclientAppVirtualSite(vclientAppVirtualSite);
						arrayVPNVClientApp.setIpaddress(node.getIpAddress());
						arrayVPNVClientApp.setCollecttime(date);
						arrayVPNVClientApp.setType("VPN");
						arrayVPNVClientApp.setSubtype("ArrayNetworks");
						powerVector.addElement(arrayVPNVClientApp);
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
		ipAllData.put("VPNVClient", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("VPNVClient", powerVector);
		return returnHash;
	}

}
