package com.afunms.monitor.executor;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.org.xone.telnet.TelnetWrapper;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;

@SuppressWarnings("unchecked")
public class DiskArray2 {
	private static HashMap<String, String> disksMap;
	static {
		SAXBuilder builder = new SAXBuilder();
		disksMap = new HashMap<String, String>();
		try {
			Document doc = builder.build(new File("D:/afunms/src/disks.xml"));
			List disks = doc.getRootElement().getChildren("disk");
			for (int i = 0; i < disks.size(); i++) {
				Element ele = (Element) disks.get(i);
				disksMap.put(ele.getChildText("ip"), ele.getChildText("tag"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public DiskArray2() {
	}

	public void analyseData(Node node, MonitoredItem item) {
		return;
	}

	public void vgExist(String ip, String user, String password, String prompt, String command) {
		TelnetWrapper telnet = new TelnetWrapper();
		try {
			telnet.connect(ip, 23, 5000);
			telnet.login(user, password);
			telnet.setPrompt(prompt);
			telnet.waitfor(prompt);
			telnet.send(command);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				telnet.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
