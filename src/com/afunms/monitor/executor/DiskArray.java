package com.afunms.monitor.executor;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.org.xone.telnet.TelnetWrapper;

import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class DiskArray extends SnmpMonitor implements MonitorInterface {
	private static HashMap<String, String> disksMap;
	private static HashMap<String, String> vgsMap;
	static {
		SAXBuilder builder = new SAXBuilder();
		disksMap = new HashMap<String, String>();
		vgsMap = new HashMap<String, String>();
		try {
			Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/disks.xml"));
			List disks = doc.getRootElement().getChildren("disk");
			for (int i = 0; i < disks.size(); i++) {
				Element ele = (Element) disks.get(i);
				disksMap.put(ele.getChildText("ip"), ele.getChildText("tag"));
				if (!"".equals(ele.getChildText("vg"))) {
					vgsMap.put(ele.getChildText("ip"), ele.getChildText("vg"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DiskArray() {
	}

	@Override
	public void analyseData(Node node, MonitoredItem item) {
		return;
	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {

	}

	public void collectData(Node node, MonitoredItem monitoredItem) {
		CommonItem item = (CommonItem) monitoredItem;
		Host host = (Host) node;
		item.setSingleResult(-1);
		item.setAlarm(false);

		if (disksMap.get(host.getIpAddress()) != null) {
			boolean exist = false;
			String diskTag = disksMap.get(host.getIpAddress());
			if (host.getSysOid().startsWith("1.3.6.1.4.1.311.")) {
				exist = diskExist(host.getIpAddress(), host.getCommunity(), diskTag);
			} else {
				exist = fileSystemExist(host.getIpAddress(), host.getUser(), host.getPassword(), host.getPrompt(), diskTag);
			}
			if (!exist) {
				item.setAlarm(true);
				item.setAlarmInfo("不能连接磁盘阵列");
			}
		}
		if (vgsMap.get(host.getIpAddress()) != null) {
			String command = vgsMap.get(host.getIpAddress());
			if (vgExist(host.getIpAddress(), host.getUser(), host.getPassword(), host.getPrompt(), command)) {
				item.setAlarmInfo("磁盘阵列正常");
				item.setAlarm(false);
			} else {
				item.setAlarm(true);
				item.setAlarmInfo("磁盘阵列不正常");
			}
		}
	}

	private boolean diskExist(String ip, String community, String diskTag) {
		boolean result = false;
		String[] oids = new String[] { "1.3.6.1.2.1.25.2.3.1.3" };

		String[][] valueArray = null;
		try {
			valueArray = snmp.getTableData(ip, community, oids);
			for (int i = 0; i < valueArray.length; i++) {
				if (diskTag.equals(valueArray[i][0].substring(0, 1))) {
					result = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean fileSystemExist(String ip, String user, String password, String prompt, String diskTag) {
		TelnetWrapper telnet = new TelnetWrapper();
		boolean result = false;
		try {
			telnet.connect(ip, 23, 5000);
			telnet.login(user, password);
			telnet.setPrompt(prompt);
			telnet.waitfor(prompt);
			String response = telnet.send("df -k");
			Pattern p = Pattern.compile("[\\S ]+\r\n");
			Matcher m = p.matcher(response);
			boolean first = true;

			while (m.find()) {
				if (first) {
					first = false;
				} else {
					String filesystem = response.substring(m.start(), m.end());
					Pattern pFilesystem = Pattern.compile("[\\S]+");
					Matcher mFilesystem = pFilesystem.matcher(filesystem);
					mFilesystem.find();
					mFilesystem.find();
					mFilesystem.find();
					mFilesystem.find();
					String used = filesystem.substring(mFilesystem.start(), mFilesystem.end());
					int index = used.indexOf(37);
					if (index > 0) {
						used = used.substring(0, index);
					}
					mFilesystem.find();
					mFilesystem.find();
					String iusedpercent = filesystem.substring(mFilesystem.start(), mFilesystem.end());
					index = iusedpercent.indexOf(37);
					if (index > 0) {
						iusedpercent = iusedpercent.substring(0, index);
					}
					mFilesystem.find();
					String mounted = filesystem.substring(mFilesystem.start(), mFilesystem.end());
					if (diskTag.equals(mounted)) {
						result = true;
						break;
					}
				}
			}
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
			try {
				telnet.disconnect();
			} catch (Exception e) {
			}
		}
		return result;
	}

	private boolean vgExist(String ip, String user, String password, String prompt, String command) {
		TelnetWrapper telnet = new TelnetWrapper();
		boolean result = false;
		try {
			telnet.connect(ip, 23, 5000);
			telnet.login(user, password);
			telnet.setPrompt(prompt);
			telnet.waitfor(prompt);

			String response = telnet.send(command);
			String[] temp = response.split("\n");
			String active1 = temp[3].substring(8, 25).trim();
			String active2 = temp[4].substring(8, 25).trim();
			if ("active".equals(active1) && "active".equals(active2)) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				telnet.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
