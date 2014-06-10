package com.afunms.polling.snmp.memory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
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
public class VenusMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector<MemoryCollectEntity> memoryVector = new Vector<MemoryCollectEntity>();
		List memoryList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}

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
			if (node.getSysOid().startsWith("1.3.6.1.4.1.15227.")) {
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.15227.1.3.1.1.2" };// venusMemory

				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
						.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				int flag = 0;
				String index = "";
				String usedperc = "0";
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						String usedvalue = valueArray[i][0];
						index = valueArray[i][1];
						if (!"".equals(usedvalue) && usedvalue != null) {
							if (usedvalue.indexOf("%") > 0) {
								String[] values = usedvalue.split("%");
								for (int k = 0; k < values.length; k++) {
									usedvalue = values[k];
								}
							}
						}
						float value = 0.0f;
						try {
							if (Math.round(Double.valueOf(usedvalue)) > 0) {
								value = Math.round(Double.valueOf(usedvalue));
							}

						} catch (Exception e) {
							e.printStackTrace();
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
							memorycollectdata.setUnit("%");
							memorycollectdata.setThevalue(intvalue + "");
							memoryVector.addElement(memorycollectdata);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable collectHash = new Hashtable();
		collectHash.put("memory", memoryVector);
		// 对内存值进行告警检测
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_FIREWALL, "venus", "memory");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				// 对虚拟内存值进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, collectHash, "firewall", "venus", alarmIndicatorsnode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = new Hashtable();
		try {
			ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		if (memoryVector != null && memoryVector.size() > 0) {
			ipAllData.put("memory", memoryVector);
		}
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("memory", memoryVector);
		ipAllData = null;
		memoryVector = null;
		NetmemoryResultTosql tosql = new NetmemoryResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
		totempsql.CreateResultTosql(returnHash, node);

		return returnHash;
	}
}
