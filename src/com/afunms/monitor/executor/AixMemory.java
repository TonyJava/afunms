package com.afunms.monitor.executor;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.org.xone.telnet.TelnetWrapper;

import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class AixMemory extends BaseMonitor implements MonitorInterface {
	public AixMemory() {
	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {

	}

	public void collectData(Node node, MonitoredItem monitoredItem) {
		CommonItem item = (CommonItem) monitoredItem;
		Host host = (Host) node;
		if (host.getUser() == null || "".equals(host.getUser())) {
			item.setMultiResults(null);
			return;
		}
		double result = getMemoryRate(host.getIpAddress(), host.getUser(), host.getPassword(), host.getPrompt());
		item.setSingleResult(result);
	}

	public double getMemoryRate(String ip, String user, String password, String prompt) {
		TelnetWrapper telnet = new TelnetWrapper();
		double memoryRate = 0;
		try {
			telnet.connect(ip, 23, 3000);
			telnet.login(user, password);
			telnet.setPrompt(prompt);
			telnet.waitfor(prompt);
			String memoryResponse = telnet.send("svmon -G");
			DecimalFormat df = new DecimalFormat("#");
			Pattern p = Pattern.compile("memory[ ]+[0-9]+[ ]+[0-9]+");
			Matcher m = p.matcher(memoryResponse);
			m.find();
			String memoryStr = memoryResponse.substring(m.start(), m.end());
			p = Pattern.compile("[ ]+[0-9]+");
			m = p.matcher(memoryStr);
			m.find();
			double memorysize = Double.parseDouble(memoryStr.substring(m.start(), m.end()));
			m.find();
			double memoryused = Double.parseDouble(memoryStr.substring(m.start(), m.end()));
			memoryRate = Double.parseDouble(df.format(memoryused / memorysize * 100));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				telnet.disconnect();
			} catch (Exception e) {
			}
		}
		return memoryRate;
	}

}
