package com.afunms.monitor.executor;

import java.util.Hashtable;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class HuaweiSnmp extends SnmpMonitor implements MonitorInterface {
	public HuaweiSnmp() {
	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {
		HostNode host = node;
		try {
			String temp = snmp.getMibValue(host.getIpAddress(), host.getCommunity(), "1.3.6.1.4.1.2011.6.1.1.1.4.0");
			if (temp == null) {
				temp = snmp.getMibValue(host.getIpAddress(), host.getCommunity(), "1.3.6.1.4.2011.10.2.6.1.1.1.1.6.0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void collectData(Node node, MonitoredItem item) {

	}
}
