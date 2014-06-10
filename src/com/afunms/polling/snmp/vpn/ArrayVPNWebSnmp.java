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
import com.afunms.polling.om.ArrayVPNWeb;

@SuppressWarnings("unchecked")
public class ArrayVPNWebSnmp extends SnmpMonitor {
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
				String[] oids = new String[] { "1.3.6.1.4.1.7564.33.1.2.1.2", "1.3.6.1.4.1.7564.33.1.2.1.3", "1.3.6.1.4.1.7564.33.1.2.1.4", "1.3.6.1.4.1.7564.33.1.2.1.5",
						"1.3.6.1.4.1.7564.33.1.2.1.6" };
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						ArrayVPNWeb arrayVPNWeb = new ArrayVPNWeb();
						String webId = valueArray[i][0];
						String webAuthorizedReq = valueArray[i][1];
						String webUnauthorizedReq = valueArray[i][2];
						String webBytesIn = valueArray[i][3];
						String webBytesOut = valueArray[i][4];
						arrayVPNWeb.setWebAuthorizedReq(Integer.parseInt(webAuthorizedReq));
						arrayVPNWeb.setWebId(webId);
						arrayVPNWeb.setWebBytesIn(Long.parseLong(webBytesIn));
						arrayVPNWeb.setWebBytesOut(Long.parseLong(webBytesOut));
						arrayVPNWeb.setWebUnauthorizedReq(Integer.parseInt(webUnauthorizedReq));
						arrayVPNWeb.setIpaddress(node.getIpAddress());
						arrayVPNWeb.setCollecttime(date);
						arrayVPNWeb.setType("VPN");
						arrayVPNWeb.setSubtype("ArrayNetworks");
						powerVector.addElement(arrayVPNWeb);
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
		ipAllData.put("VPNWeb", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("VPNWeb", powerVector);
		return returnHash;
	}

}
