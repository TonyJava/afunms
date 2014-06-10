package com.afunms.polling.snmp.statue;

/*
 * @author yangjun@dhcc.com.cn
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
public class EmsStatueSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector statuVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}
		SystemCollectEntity systemdata = null;
		Calendar date = Calendar.getInstance();
		try {
			final String[] desc = SnmpMibConstants.UpsMibStatueDesc;
			final String[] chname = SnmpMibConstants.UpsMibStatueChname;
			final String[] unit = SnmpMibConstants.UpsMibStatueUnit;
			String[] valueArray = new String[8];
			if (node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")) {//

				String[] oids = new String[] { ".1.3.6.1.4.1.13400.2.1.3.3.10.1.3.0",// 并机设置总台数
						".1.3.6.1.4.1.13400.2.1.3.3.10.1.2.0",// 并机设置冗余台数
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.1.0",// 并机系统A相输出总有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.2.0",// 并机系统B相输出总有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.3.0",// 并机系统C相输出总有功功率

						".1.3.6.1.4.1.13400.2.1.3.3.10.2.4.0",// 并机系统A相输出总视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.5.0",// 并机系统B相输出总视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.10.2.6.0",// 并机系统C相输出总视在功率

				// "1.3.6.1.4.1.13400.2.20.2.2.7.0",//并机系统A相输出总无功功率
				// "1.3.6.1.4.1.13400.2.20.2.2.8.0",//
				// "1.3.6.1.4.1.13400.2.20.2.2.9.0"
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
					systemdata.setCategory("Statue");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					if (value != null && !value.equals("noSuchObject")) {
						if (desc[i].equals("JXRL") || desc[i].equals("BJTH")) {
							systemdata.setThevalue(value + "");
						} else {
							systemdata.setThevalue((Float.parseFloat(value) / 10) + "");
						}
					} else {
						systemdata.setThevalue("");
					}
					statuVector.addElement(systemdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("statue", statuVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("statue", statuVector);

		Hashtable ipdata = new Hashtable();
		ipdata.put("statue", returnHash);
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