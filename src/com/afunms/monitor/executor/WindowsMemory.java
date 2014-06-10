package com.afunms.monitor.executor;

import java.util.Hashtable;

import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.SnmpItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class WindowsMemory extends SnmpMonitor implements MonitorInterface {
	public static void main(String[] args) {
		WindowsMemory cm = new WindowsMemory();
		cm.collectData(null, null);
	}

	public WindowsMemory() {
	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {

	}

	public void collectData(Node node, MonitoredItem monitoredItem) {
		Host host = (Host) node;
		SnmpItem item = (SnmpItem) monitoredItem;
		int result = 0;
		try {
			String[][] valueArray = snmp.getTableData(host.getIpAddress(), host.getCommunity(), new String[] { "1.3.6.1.2.1.25.5.1.1.2" });
			String temp = snmp.getMibValue(host.getIpAddress(), host.getCommunity(), "1.3.6.1.2.1.25.2.2.0");

			int memorySize = 0, usedSize = 0;
			if (temp == null || valueArray == null || valueArray.length == 0) {
				item.setSingleResult(-1);
				return;
			}

			memorySize = Integer.parseInt(temp);
			if (memorySize == 0) {
				result = 0;
			} else {
				for (int i = 0; i < valueArray.length; i++) {
					usedSize += Integer.parseInt(valueArray[i][0]);
				}
				result = (usedSize * 100 / memorySize);

				if (result > 100) // 2006.10.13
				{
					result = 95;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		item.setSingleResult(result);
	}
}