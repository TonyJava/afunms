package com.afunms.polling.snmp.interfaces;

import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpMac;
import com.gatherResulttosql.NetHostipmacRttosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArpSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector ipmacVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}

		logger.info("ArpSnmp " + node.getIpAddress());
		try {

			String[] oids = new String[] { "1.3.6.1.2.1.4.22.1.1", // 1.ipNetToMediaIfIndex
					"1.3.6.1.2.1.4.22.1.2", // 2.ipNetToMediaPhysAddress
					"1.3.6.1.2.1.4.22.1.3", // 3.ipNetToMediaNetAddress
					"1.3.6.1.2.1.4.22.1.4" }; // 4.ipNetToMediaType

			String[] ifOids = new String[] { "1.3.6.1.2.1.2.2.1.1", // ifIndex
					"1.3.6.1.2.1.2.2.1.2" // ifDescr
			};

			String[][] valueArray = SnmpUtils.getTable(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
			String[][] ifValueArray = SnmpUtils.getTable(node.getIpAddress(), node.getCommunity(), ifOids, node.getSnmpversion(), 3, 1000 * 30);

			Hashtable<String, String> ifHt = new Hashtable<String, String>();
			if (null != ifValueArray && ifValueArray.length > 0) {
				String ifIndex = (String) null;
				String value = (String) null;
				for (int j = 0; j < ifValueArray.length; j++) {
					ifIndex = parseString(ifValueArray[j][0]);
					value = parseString(ifValueArray[j][1]);
					if (ifIndex.equals("NaV"))
						continue;
					ifHt.put(ifIndex, value);
				}
			}

			if (null != valueArray && valueArray.length > 0) {
				IpMac ipmac = null;
				String ipNetToMediaIfIndex = (String) null;
				String ipNetToMediaPhysAddress = (String) null;
				String ipNetToMediaNetAddress = (String) null;
				String ipNetToMediaType = (String) null;
				for (int i = 0; i < valueArray.length; i++) {
					ipNetToMediaIfIndex = parseString(valueArray[i][0]);
					ipNetToMediaPhysAddress = parseString(valueArray[i][1]);
					ipNetToMediaNetAddress = parseString(valueArray[i][2]);
					ipNetToMediaType = parseString(valueArray[i][3]);

					if (ipNetToMediaIfIndex.equals("NaV") || ipNetToMediaNetAddress.equals("NaV") || ipNetToMediaPhysAddress.equals("NaV")) {
						continue;
					}
					ipmac = new IpMac();
					ipmac.setIfindex(ifHt.get(ipNetToMediaIfIndex));
					ipmac.setMac(ipNetToMediaPhysAddress);
					ipmac.setIpaddress(ipNetToMediaNetAddress);
					ipmac.setBak(ipNetToMediaType);
					ipmac.setIfband("0");
					ipmac.setIfsms("0");
					ipmac.setCollecttime(new GregorianCalendar());
					ipmac.setRelateipaddr(node.getIpAddress());
					ipmacVector.addElement(ipmac);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("ipmac", ipmacVector);
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipmacVector != null && ipmacVector.size() > 0) {
				ipAllData.put("ipmac", ipmacVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (ipmacVector != null && ipmacVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("ipmac", ipmacVector);
			}
		}
		NetHostipmacRttosql ipmactosql = new NetHostipmacRttosql();
		ipmactosql.CreateResultTosql(returnHash, node);
		return returnHash;
	}
}
