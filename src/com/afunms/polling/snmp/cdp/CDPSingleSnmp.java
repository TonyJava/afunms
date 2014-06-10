package com.afunms.polling.snmp.cdp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.discovery.CdpCachEntryInterface;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherResulttosql.NetHostNDPRttosql;

@SuppressWarnings("unchecked")
public class CDPSingleSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String ciscoIP2IP(String ciscoip) {
		String[] s = ciscoip.split(":");
		if (4 == s.length) {
			return "" + Integer.parseInt(s[0], 16) + "." + Integer.parseInt(s[1], 16) + "." + Integer.parseInt(s[2], 16) + "." + Integer.parseInt(s[3], 16);
		}
		return "";
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector cdpVector = new Vector();
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
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { "1.3.6.1.4.1.9.9.23.1.2.1.1.4", // 1.cdpCacheAddress
						"1.3.6.1.4.1.9.9.23.1.2.1.1.7", // 2.cdpCacheDevicePort
				};

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray == null) {
					return null;
				}
				CdpCachEntryInterface cdp = null;
				for (int i = 0; i < valueArray.length; i++) {
					cdp = new CdpCachEntryInterface();
					if (valueArray[i][0] == null) {
						continue;
					}
					cdp.setIp(ciscoIP2IP(valueArray[i][0]));
					cdp.setPortdesc(valueArray[i][1]);
					cdpVector.addElement(cdp);
				}
				valueArray = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("cdp", cdpVector);
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (cdpVector != null && cdpVector.size() > 0) {
				ipAllData.put("cdp", cdpVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (cdpVector != null && cdpVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("cdp", cdpVector);
			}

		}

		// 把采集结果生成sql
		NetHostNDPRttosql ndptosql = new NetHostNDPRttosql();
		ndptosql.CreateResultTosql(cdpVector, node);

		cdpVector = null;
		return returnHash;
	}

}
