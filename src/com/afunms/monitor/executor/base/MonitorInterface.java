
package com.afunms.monitor.executor.base;

import java.util.Hashtable;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public interface MonitorInterface {
	public void analyseData(Node node, MonitoredItem item);

	public Hashtable collect_Data(HostNode node);

	public void collectData(HostNode node);

	public void collectData(Node node, MonitoredItem item);
}