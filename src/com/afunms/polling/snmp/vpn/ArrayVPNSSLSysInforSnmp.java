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
import com.afunms.polling.om.ArrayVPNSSLSysInfor;

@SuppressWarnings("unchecked")
public class ArrayVPNSSLSysInforSnmp extends SnmpMonitor {
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
				String[] oids = new String[] { "1.3.6.1.4.1.7564.20.1.1", "1.3.6.1.4.1.7564.20.1.2", "1.3.6.1.4.1.7564.20.2.1", "1.3.6.1.4.1.7564.20.2.2",
						"1.3.6.1.4.1.7564.20.2.3",

				};
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						ArrayVPNSSLSysInfor arrayVPNSSLSysInfor = new ArrayVPNSSLSysInfor();
						String sslStatus = valueArray[i][0];
						String vhostNum = valueArray[i][1];
						String totalOpenSSLConns = valueArray[i][2];
						String totalAcceptedConns = valueArray[i][3];
						String totalRequestedConns = valueArray[i][4];
						arrayVPNSSLSysInfor.setSslStatus(sslStatus);
						arrayVPNSSLSysInfor.setTotalAcceptedConns(Long.parseLong(totalAcceptedConns));
						arrayVPNSSLSysInfor.setTotalOpenSSLConns(Long.parseLong(totalOpenSSLConns));
						arrayVPNSSLSysInfor.setTotalRequestedConns(Long.parseLong(totalRequestedConns));
						arrayVPNSSLSysInfor.setVhostNum(Integer.parseInt(vhostNum));
						arrayVPNSSLSysInfor.setIpaddress(node.getIpAddress());
						arrayVPNSSLSysInfor.setCollecttime(date);
						arrayVPNSSLSysInfor.setType("VPN");
						arrayVPNSSLSysInfor.setSubtype("ArrayNetworks");
						powerVector.addElement(arrayVPNSSLSysInfor);
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
		ipAllData.put("VPNSSLInfor", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("VPNSSLInfor", powerVector);
		return returnHash;
	}


}
