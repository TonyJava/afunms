package com.afunms.polling.snmp.upsinput;

/*
 * 艾默生UPS旁路信息组
 */

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.snmp.SnmpMibConstants;

@SuppressWarnings("unchecked")
public class EmsBypassSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector passVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}
		SystemCollectEntity systemdata = null;
		Calendar date = Calendar.getInstance();
		try {
			final String[] desc = SnmpMibConstants.UpsMibBypassDesc;
			final String[] chname = SnmpMibConstants.UpsMibBypassChname;
			final String[] unit = SnmpMibConstants.UpsMibBypassUnit;
			String[] valueArray = new String[4];
			if (node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")) {//
				String[] oids = new String[] { ".1.3.6.1.4.1.13400.2.1.3.3.3.3.1.0",// A相旁路电压
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.2.0",// B相旁路电压
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.3.0",// C相旁路电压
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.7.0"// 旁路频率
				};

				for (int j = 0; j < oids.length; j++) {
					try {
						valueArray[j] = snmp.getMibValue(node.getIpAddress(), node.getCommunity(), oids[j]);
					} catch (Exception e) {
						valueArray = null;
						e.printStackTrace();
					}
				}
			}
			if (valueArray != null && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					systemdata = new SystemCollectEntity();
					systemdata.setIpaddress(node.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("Bypass");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					if (value != null && !value.equals("noSuchObject")) {
						systemdata.setThevalue((Float.parseFloat(value) / 10) + "");
					} else {
						systemdata.setThevalue("");
					}
					passVector.addElement(systemdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("bypass", passVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("bypass", passVector);

		Hashtable ipdata = new Hashtable();
		ipdata.put("bypass", returnHash);
		Hashtable alldata = new Hashtable();
		alldata.put(node.getIpAddress(), ipdata);
		HostCollectDataManager hostdataManager = new HostCollectDataManager();
		try {
			hostdataManager.createHostItemData(alldata, "ups");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnHash;
	}
}