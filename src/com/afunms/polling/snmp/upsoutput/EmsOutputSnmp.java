package com.afunms.polling.snmp.upsoutput;

/*
 * @author yangjun@dhcc.com.cn
 *
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
public class EmsOutputSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector outputVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}
		SystemCollectEntity systemdata = null;
		Calendar date = Calendar.getInstance();
		try {
			final String[] desc = SnmpMibConstants.UpsMibOutputDesc;
			final String[] chname = SnmpMibConstants.UpsMibOutputChname;
			final String[] unit = SnmpMibConstants.UpsMibOutputUnit;
			String[] valueArray = new String[22];
			if (node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")) {//
				String[] oids = new String[] { ".1.3.6.1.4.1.13400.2.1.3.3.4.2.1.0",// 输出A相电压
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.2.0",// 输出B相电压
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.3.0",// 输出C相电压
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.4.0",// A相输出电流
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.5.0",// B相输出电流
						".1.3.6.1.4.1.13400.2.1.3.3.3.3.6.0",// C相输出电流

						".1.3.6.1.4.1.13400.2.1.3.3.4.2.7.0",// 输出频率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.8.0",// A相输出功率因数
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.9.0",// B相输出功率因数
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.10.0",// C相输出功率因数

						".1.3.6.1.4.1.13400.2.1.3.3.4.2.11.0",// A相输出有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.12.0",// B相输出有功功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.13.0",// C相输出有功功率

						".1.3.6.1.4.1.13400.2.1.3.3.4.2.14.0",// A相输出视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.15.0",// B相输出视在功率
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.16.0",// C相输出视在功率

						// "1.3.6.1.4.1.13400.2.20.2.4.29.0",//A相输出无功功率
						// "1.3.6.1.4.1.13400.2.20.2.4.30.0",
						// "1.3.6.1.4.1.13400.2.20.2.4.31.0",

						".1.3.6.1.4.1.13400.2.1.3.3.4.2.17.0",// A相输出负载百分比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.18.0",// B相输出负载百分比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.19.0",// C相输出负载百分比

						".1.3.6.1.4.1.13400.2.1.3.3.4.2.20.0",// A相输出峰值比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.21.0",// B相输出峰值比
						".1.3.6.1.4.1.13400.2.1.3.3.4.2.22.0"// C相输出峰值比
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
					systemdata.setCategory("Output");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					if (value != null && !value.equals("noSuchObject")) {
						if (desc[i].equals("AXSCGLYS") || desc[i].equals("BXSCGLYS") || desc[i].equals("CXSCGLYS")) {
							systemdata.setThevalue((Float.parseFloat(value) / 100) + "");
						} else {
							systemdata.setThevalue((Float.parseFloat(value) / 10) + "");
						}
					} else {
						systemdata.setThevalue("0");
					}
					outputVector.addElement(systemdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("output", outputVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("output", outputVector);

		Hashtable ipdata = new Hashtable();
		ipdata.put("output", returnHash);
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