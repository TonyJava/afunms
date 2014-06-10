package com.afunms.monitor.executor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Hashtable;
import java.util.List;

import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.ServiceItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.sysset.model.Service;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class ServiceMonitor extends BaseMonitor implements MonitorInterface {
	public ServiceMonitor() {
	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {

	}

	public void collectData(Node node, MonitoredItem monitoredItem) {
		ServiceItem item = (ServiceItem) monitoredItem;
		List list = ResourceCenter.getInstance().getServiceList();
		for (int i = 0; i < list.size(); i++) {
			Service service = (Service) list.get(i);
			Socket socket = new Socket();
			int result = 0;
			try {
				InetAddress addr = InetAddress.getByName(node.getIpAddress());
				SocketAddress sockaddr = new InetSocketAddress(addr, service.getPort());
				socket.connect(sockaddr, 1000);
				result = 1;
			} catch (Exception ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			item.getServicesStatus()[i] = result;
		}
	}
}