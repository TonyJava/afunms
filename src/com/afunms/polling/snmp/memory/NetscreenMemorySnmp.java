package com.afunms.polling.snmp.memory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.MemoryCollectEntity;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;

@SuppressWarnings("unchecked")
public class NetscreenMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		}
		try {
			MemoryCollectEntity memorydata = null;
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

				String[] oids = new String[] { "1.3.6.1.4.1.3224.16.2.1",// All
						"1.3.6.1.4.1.3224.16.2.2" }; // process Left Size
				String[][] valueArray = null;
				int allMemorySize = 0;
				float value = 0.0f;
				int leftsize = 0;
				try {
					if (node.getSnmpversion() == 0 || node.getSnmpversion() == 1) {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
					} else if (node.getSnmpversion() == 3) {
						valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
								.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

					}
				} catch (Exception e) {
					e.printStackTrace();
					valueArray = null;
				}
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						if (valueArray[i][0] != null && valueArray[i][1] != null) {
							leftsize = Integer.parseInt(valueArray[i][0]);
							allMemorySize = Integer.parseInt(valueArray[i][1]);
							break;
						}
					}
				}
				if (allMemorySize != 0) {
					value = (allMemorySize - leftsize) * 100f / allMemorySize;
				} else {
					value = 0;
				}
				if (valueArray == null || valueArray.length == 0) {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				}
				memorydata = new MemoryCollectEntity();
				memorydata.setIpaddress(node.getIpAddress());
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Utilization");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("dynamic");
				memorydata.setUnit("%");
				memorydata.setThevalue((int) Math.rint(value / 1) + "");
				memoryVector.addElement(memorydata);

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
				ipAllData.put("memory", memoryVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (memoryVector != null && memoryVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("memory", memoryVector);
			}

		}
		returnHash.put("memory", memoryVector);

		Hashtable collectHash = new Hashtable();
		collectHash.put("memory", memoryVector);

		// 对内存值进行告警检测
		try {
			if (memoryVector != null && memoryVector.size() > 0) {
				int thevalue = 0;
				for (int i = 0; i < memoryVector.size(); i++) {
					MemoryCollectEntity memorycollectdata = (MemoryCollectEntity) memoryVector.get(i);
					if ("Utilization".equals(memorycollectdata.getEntity())) {
						if (Integer.parseInt(memorycollectdata.getThevalue()) > thevalue) {
							thevalue = Integer.parseInt(memorycollectdata.getThevalue());
						}
					}
				}
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, nodeGatherIndicators, thevalue + "", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		memoryVector = null;
		// 把采集结果生成sql
		NetmemoryResultTosql tosql = new NetmemoryResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
			totempsql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
