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
import com.afunms.polling.om.VpnCluster;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class ArrayVPNClusterSnmp extends SnmpMonitor {
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
				String[] oids = new String[] { "1.3.6.1.4.1.7564.18.1.3.1.1", "1.3.6.1.4.1.7564.18.1.3.1.2", "1.3.6.1.4.1.7564.18.1.3.1.3", "1.3.6.1.4.1.7564.18.1.3.1.4",
						"1.3.6.1.4.1.7564.18.1.3.1.5", "1.3.6.1.4.1.7564.18.1.3.1.6", "1.3.6.1.4.1.7564.18.1.3.1.7", "1.3.6.1.4.1.7564.18.1.3.1.8", "1.3.6.1.4.1.7564.18.1.3.1.9",
						"1.3.6.1.4.1.7564.18.1.3.1.10" };

				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
						.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						VpnCluster vpnCluster = new VpnCluster();
						String clusterVirIndex = valueArray[i][0];
						String clusterId = valueArray[i][1];
						String clusterVirState = valueArray[i][2];
						String clusterVirIfname = valueArray[i][3];
						String clusterVirAddr = valueArray[i][4];
						String clusterVirAuthType = valueArray[i][5];
						String clusterVirAuthPasswd = valueArray[i][6];
						String clusterVirPreempt = valueArray[i][7];
						String clusterVirInterval = valueArray[i][8];
						String clusterVirPriority = valueArray[i][9];
						vpnCluster.setClusterId(Integer.parseInt(clusterId));
						vpnCluster.setClusterVirAddr(clusterVirAddr);
						vpnCluster.setClusterVirIndex(Integer.parseInt(clusterVirIndex));
						vpnCluster.setClusterVirIfname(clusterVirIfname);
						vpnCluster.setClusterVirAuthType(Integer.parseInt(clusterVirAuthType));
						vpnCluster.setClusterVirIfname(clusterVirIfname);
						vpnCluster.setClusterVirPreempt(Integer.parseInt(clusterVirPreempt));
						vpnCluster.setClusterVirPriority(Integer.parseInt(clusterVirPriority));
						vpnCluster.setClusterVirState(Integer.parseInt(clusterVirState));
						vpnCluster.setClusterVirInterval(Integer.parseInt(clusterVirInterval));
						vpnCluster.setClusterVirAuthPasswd(clusterVirAuthPasswd);
						vpnCluster.setCollecttime(date);
						vpnCluster.setIpaddress(node.getIpAddress());
						vpnCluster.setType("VPN");
						vpnCluster.setSubtype("ArrayNetworks");
						powerVector.addElement(vpnCluster);
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
		ipAllData.put("Cluster", powerVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("Cluster", powerVector);

		return returnHash;
	}

	public void collectData(HostNode node) {

	}

	public void collectData(Node node, MonitoredItem item) {

	}

}
