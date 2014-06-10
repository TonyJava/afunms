package com.afunms.polling.snmp.vpn;

/*
 * author ChengFeng
 *
 */

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.ArrayVPNFlowRate;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class ArrayVPNFlowRateSnmp extends SnmpMonitor {
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
				String[] oids = new String[] { "1.3.6.1.4.1.7564.28..1", "1.3.6.1.4.1.7564.28..2", "1.3.6.1.4.1.7564.28..3", "1.3.6.1.4.1.7564.28..4", "1.3.6.1.4.1.7564.28..5",
						"1.3.6.1.4.1.7564.28..6", "1.3.6.1.4.1.7564.28..7" };
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						ArrayVPNFlowRate arrayVPNFlowRate = new ArrayVPNFlowRate();
						String totalBytesRcvd = valueArray[i][0];
						String totalBytesSent = valueArray[i][1];
						String rcvdBytesPerSec = valueArray[i][2];
						String sentBytesPerSec = valueArray[i][3];
						String peakRcvdBytesPerSec = valueArray[i][4];
						String peakSentBytesPerSec = valueArray[i][5];
						String activeTransac = valueArray[i][6];
						arrayVPNFlowRate.setTotalBytesRcvd(Integer.parseInt(totalBytesRcvd));
						arrayVPNFlowRate.setTotalBytesSent(Integer.parseInt(totalBytesSent));
						arrayVPNFlowRate.setActiveTransac(Integer.parseInt(activeTransac));
						arrayVPNFlowRate.setPeakRcvdBytesPerSec(Integer.parseInt(peakRcvdBytesPerSec));
						arrayVPNFlowRate.setPeakSentBytesPerSec(Integer.parseInt(peakSentBytesPerSec));
						arrayVPNFlowRate.setRcvdBytesPerSec(Integer.parseInt(rcvdBytesPerSec));
						arrayVPNFlowRate.setSentBytesPerSec(Integer.parseInt(sentBytesPerSec));
						arrayVPNFlowRate.setIpaddress(node.getIpAddress());
						arrayVPNFlowRate.setCollecttime(date);
						arrayVPNFlowRate.setType("VPN");
						arrayVPNFlowRate.setSubtype("ArrayNetworks");
						powerVector.addElement(arrayVPNFlowRate);
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
		ipAllData.put("FlowRate", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("FlowRate", powerVector);
		return returnHash;
	}

	public void collectData(HostNode node) {

	}

	public void collectData(Node node, MonitoredItem item) {

	}

}
