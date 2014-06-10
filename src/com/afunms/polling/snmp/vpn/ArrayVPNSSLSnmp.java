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
import com.afunms.polling.om.ArrayVPNSSL;

@SuppressWarnings("unchecked")
public class ArrayVPNSSLSnmp extends SnmpMonitor {
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
				String[] oids = new String[] { "1.3.6.1.4.1.7564.20.2.4.1.1", "1.3.6.1.4.1.7564.20.2.4.1.2", "1.3.6.1.4.1.7564.20.2.4.1.3", "1.3.6.1.4.1.7564.20.2.4.1.4",
						"1.3.6.1.4.1.7564.20.2.4.1.5", "1.3.6.1.4.1.7564.20.2.4.1.6", "1.3.6.1.4.1.7564.20.2.4.1.7", "1.3.6.1.4.1.7564.20.2.4.1.8" };
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						ArrayVPNSSL arrayVPNSSL = new ArrayVPNSSL();
						String sslIndex = valueArray[i][0];
						String vhostName = valueArray[i][1];
						String openSSLConns = valueArray[i][2];
						String acceptedConns = valueArray[i][3];
						String requestedConns = valueArray[i][4];
						String resumedSess = valueArray[i][5];
						String resumableSess = valueArray[i][6];
						String missSess = valueArray[i][7];
						arrayVPNSSL.setSslIndex(Integer.parseInt(sslIndex));
						arrayVPNSSL.setVhostName(vhostName);
						arrayVPNSSL.setOpenSSLConns(Integer.parseInt(openSSLConns));
						arrayVPNSSL.setAcceptedConns(Integer.parseInt(acceptedConns));
						arrayVPNSSL.setRequestedConns(Integer.parseInt(requestedConns));
						arrayVPNSSL.setResumedSess(Integer.parseInt(resumedSess));
						arrayVPNSSL.setResumableSess(Integer.parseInt(resumableSess));
						arrayVPNSSL.setMissSess(Integer.parseInt(missSess));
						arrayVPNSSL.setIpaddress(node.getIpAddress());
						arrayVPNSSL.setCollecttime(date);
						arrayVPNSSL.setType("VPN");
						arrayVPNSSL.setSubType("ArrayNetworks");
						powerVector.addElement(arrayVPNSSL);
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
		ipAllData.put("SSL", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("SSL", powerVector);
		return returnHash;
	}

}
