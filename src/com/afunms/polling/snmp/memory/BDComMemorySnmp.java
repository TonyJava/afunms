package com.afunms.polling.snmp.memory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.MemoryCollectEntity;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;

@SuppressWarnings("unchecked")
public class BDComMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		List memoryList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
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
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.3320.9.48.1.2.1.2"// bdcomMemoryPoolUtilization5Min
				};
				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
						.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				int flag = 0;
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						String pervalue = valueArray[i][0];
						String index = valueArray[i][1];
						float value = 0.0f;
						String usedperc = "0";
						if (Long.parseLong(pervalue) > 0) {
							value = Long.parseLong(pervalue);
						}
						if (value > 0) {
							int intvalue = Math.round(value);
							flag = flag + 1;
							List alist = new ArrayList();
							alist.add("");
							alist.add(usedperc);
							memoryList.add(alist);
							MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
							memorycollectdata.setIpaddress(node.getIpAddress());
							memorycollectdata.setCollecttime(date);
							memorycollectdata.setCategory("Memory");
							memorycollectdata.setEntity("Utilization");
							memorycollectdata.setSubentity(index);
							memorycollectdata.setRestype("dynamic");
							memorycollectdata.setUnit("");
							memorycollectdata.setThevalue(intvalue + "");
							SysLogger.info(node.getIpAddress() + " 内存： " + Integer.parseInt(intvalue + ""));
							memoryVector.addElement(memorycollectdata);

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
