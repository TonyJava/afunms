package com.afunms.polling.snmp.memory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.MemoryCollectEntity;

@SuppressWarnings("unchecked")
public class CiscoIronportMemoryStatueSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		List memoryList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}

		try {
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			try {
				if (node.getSysOid().startsWith("1.3.6.1.4.1.15497.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.15497.1.1.1.7" };
					int flag = 0;
					String usedperc = "0";
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
							.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							String value = valueArray[i][0];
							if (value != null && Integer.parseInt(value) > 0) {
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add("");
								alist.add(usedperc);
								memoryList.add(alist);
								MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("MemoryStatue");
								memorycollectdata.setEntity("Statue");
								memorycollectdata.setSubentity("ÄÚ´æ×´Ì¬");
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("");
								memorycollectdata.setThevalue(value);
								memoryVector.addElement(memorycollectdata);

							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (memoryVector != null && memoryVector.size() > 0) {
				ipAllData.put("memorystatue", memoryVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (memoryVector != null && memoryVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("memorystatue", memoryVector);
			}

		}

		returnHash.put("memorystatue", memoryVector);
		return returnHash;
	}
}
