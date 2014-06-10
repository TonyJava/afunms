package com.afunms.polling.snmp.hillstone;

import java.util.Hashtable;

import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

@SuppressWarnings("unchecked")
public class HillstoneBasedOnSourceIPSnmp extends SnmpMonitor {
	public static Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}

		String[] oids = new String[] { ".1.3.6.1.4.1.28557.2.3.5.1.1.1", ".1.3.6.1.4.1.28557.2.3.5.1.1.2" };
		try {
			@SuppressWarnings("unused")
			String[][] theValue = snmp.getCpuTableData(node.getIpAddress(), node.getCommunity(), oids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnHash;
	}

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}