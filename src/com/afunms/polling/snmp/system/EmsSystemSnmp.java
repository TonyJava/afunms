package com.afunms.polling.snmp.system;

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
public class EmsSystemSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector systemVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}
		SystemCollectEntity systemdata = null;
		Calendar date = Calendar.getInstance();
		try {
			final String[] desc = SnmpMibConstants.NetWorkMibSystemDesc;
			final String[] chname = SnmpMibConstants.NetWorkMibSystemChname;
			String[] oids = new String[] { ".1.3.6.1.2.1.1.1.0",// 设备描述
					".1.3.6.1.2.1.1.3.0",// 运行时间
					".1.3.6.1.2.1.1.4.0",// 联系人
					".1.3.6.1.2.1.1.5.0",// 设备名称
					".1.3.6.1.2.1.1.6.0",// 设备位置
					".1.3.6.1.2.1.1.7.0" // 服务类型
			};
			String[] valueArray = new String[6];
			for (int j = 0; j < oids.length; j++) {
				try {
					valueArray[j] = snmp.getMibValue(node.getIpAddress(), node.getCommunity(), oids[j]);
				} catch (Exception e) {
					valueArray = null;
					e.printStackTrace();
				}
			}
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					systemdata = new SystemCollectEntity();
					systemdata.setIpaddress(node.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("static");
					systemdata.setUnit("");
					String value = valueArray[i];
					systemdata.setThevalue(value);
					systemVector.addElement(systemdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("systemgroup", systemVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("systemgroup", systemVector);

		Hashtable ipdata = new Hashtable();
		ipdata.put("systemgroup", returnHash);
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