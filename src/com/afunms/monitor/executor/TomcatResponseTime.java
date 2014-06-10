package com.afunms.monitor.executor;

import java.util.Hashtable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Tomcat;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class TomcatResponseTime extends BaseMonitor implements MonitorInterface {
	public TomcatResponseTime() {
	}

	@Override
	public void analyseData(Node node, MonitoredItem item) {
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
		String tomcatManagerURL = "http://" + tomcat.getIpAddress() + ":" + tomcat.getPort() + "/manager/status";

		HttpClient client = null;
		int result = 0;
		try {
			client = new HttpClient();
			UsernamePasswordCredentials upc = new UsernamePasswordCredentials(tomcat.getUser(), tomcat.getPassword());
			client.getState().setCredentials(null, null, upc);
			HttpMethod method = new GetMethod(tomcatManagerURL);
			method.setDoAuthentication(true);
			client.setConnectionTimeout(3000);
			long startTime = System.currentTimeMillis();
			client.executeMethod(method);
			result = (int) (System.currentTimeMillis() - startTime);

			int statusCode = method.getStatusCode();
			if (statusCode == 401) {
				result = -1;
			} else if (statusCode == 400 || statusCode == 403 || statusCode == 404) {
				result = -1;
			}
			if (statusCode == 500) {
				result = -1;
			}
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			client = null;
		}
		item.setSingleResult(result);
		if (result != -1) {
			tomcat.setNormalTimes(tomcat.getNormalTimes() + 1);
		} else {
			tomcat.setFailTimes(tomcat.getFailTimes() + 1);
		}
	}
}