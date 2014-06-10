package com.afunms.monitor.executor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import com.afunms.application.util.TomcatHelper;
import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitorResult;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Tomcat;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class TomcatJVM extends BaseMonitor implements MonitorInterface {
	public TomcatJVM() {

	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {

	}

	@SuppressWarnings("deprecation")
	public void collectData(Node node, MonitoredItem monitoredItem) {
		Tomcat tomcat = (Tomcat) node;
		CommonItem item = (CommonItem) monitoredItem;
		HttpClient client = null;
		MonitorResult mr = new MonitorResult();
		List list = new ArrayList();
		try {
			client = new HttpClient();
			HttpMethod method = new GetMethod(tomcat.getJspUrl());
			method.setDoAuthentication(true);
			client.setConnectionTimeout(3000);
			client.executeMethod(method);
			method.releaseConnection();

			TomcatHelper th = new TomcatHelper(tomcat.getXmlUrl());
			mr.setEntity("jvm");
			mr.setPercentage(th.getJVMUtil());
			mr.setValue((long) th.getFreeMemory());
		} catch (Exception e) {
			mr.setEntity("jvm");
			mr.setPercentage(-1);
			mr.setValue(-1);
		} finally {
			client = null;
		}
		list.add(mr);
		item.setMultiResults(list);
	}
}