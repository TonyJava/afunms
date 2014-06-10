package com.afunms.polling.snmp.disk;

/*
 * 磁盘IO利用率
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.MemoryCollectEntity;

@SuppressWarnings("unchecked")
public class NokiaDiskMirrorSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Hashtable mirrorHash = new Hashtable();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;
		// 判断是否在采集时间段内
		try {
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (node.getSysOid().startsWith("1.3.6.1.4.1.94.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.94.1.21.1.9.1" };
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							Vector mirrorVector = new Vector();
							String value = valueArray[i][0];
							if (value != null && Integer.parseInt(value) > 0) {
								int intvalue = Integer.parseInt(value);
								MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("mirror");
								memorycollectdata.setEntity("Total");
								memorycollectdata.setSubentity("1");
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("");
								memorycollectdata.setThevalue(intvalue + "");
								mirrorVector.addElement(memorycollectdata);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (node.getSysOid().startsWith("1.3.6.1.4.1.94.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.94.1.21.1.9.2.1.1", "1.3.6.1.4.1.94.1.21.1.9.2.1.2", "1.3.6.1.4.1.94.1.21.1.9.2.1.3",
							"1.3.6.1.4.1.94.1.21.1.9.2.1.4" };
					try {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 5000);
					} catch (Exception e) {
						valueArray = null;
						e.printStackTrace();
					}
					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							Vector mirrorVector = new Vector();
							String index = valueArray[i][0];
							String sindex = valueArray[i][1];
							String dindex = valueArray[i][2];
							String percentage = valueArray[i][3];
							if (index != null && sindex != null) {
								MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("mirror");
								memorycollectdata.setEntity("source");
								memorycollectdata.setSubentity(index);
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("");
								memorycollectdata.setThevalue(sindex);
								mirrorVector.addElement(memorycollectdata);
							}
							if (index != null && dindex != null) {
								MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("mirror");
								memorycollectdata.setEntity("destination");
								memorycollectdata.setSubentity(index);
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("");
								memorycollectdata.setThevalue(dindex);
								mirrorVector.addElement(memorycollectdata);
							}
							if (index != null && percentage != null) {
								MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("mirror");
								memorycollectdata.setEntity("percentage");
								memorycollectdata.setSubentity(index);
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("");
								memorycollectdata.setThevalue(percentage);
								mirrorVector.addElement(memorycollectdata);
							}
							mirrorHash.put(index, mirrorVector);
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
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (mirrorHash != null && mirrorHash.size() > 0)
				ipAllData.put("mirror", mirrorHash);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (mirrorHash != null && mirrorHash.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("mirror", mirrorHash);

		}
		returnHash.put("mirror", mirrorHash);

		return returnHash;
	}
}
