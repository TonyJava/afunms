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
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.MemoryCollectEntity;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;

@SuppressWarnings("unchecked")
public class BrocadeMemorySnmp extends SnmpMonitor {

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		// yangjun
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
				String memtype = "";// 有两中类型 memp 代表百分比，memsize 代表大小
				String[] oids = new String[] { "1.3.6.1.4.1.1588.2.1.1.1.26.6" };//
				if (node.getSysOid().startsWith("1.3.6.1.4.1.1588.")) {
					oids = new String[] { "1.3.6.1.4.1.1588.2.1.1.1.26.6" };

				}
				memtype = "memp";
				String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				int allvalue = 0;

				int flag = 0;
				if (valueArray != null && valueArray.length > 0) {
					// 根据不同的类型来判断
					if (memtype.equals("memp")) {
						for (int i = 0; i < valueArray.length; i++) {
							String _value = valueArray[i][0];
							String index = i + "";
							int value = 0;
							try {
								value = Integer.parseInt(_value);
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								allvalue = allvalue + value;
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (value > 0) {
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add(index);
								alist.add(_value);
								// 内存
								memoryList.add(alist);
								MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
								memorycollectdata.setIpaddress(node.getIpAddress());
								memorycollectdata.setCollecttime(date);
								memorycollectdata.setCategory("Memory");
								memorycollectdata.setEntity("Utilization");
								memorycollectdata.setSubentity(index);
								memorycollectdata.setRestype("dynamic");
								memorycollectdata.setUnit("%");
								memorycollectdata.setThevalue(_value + "");
								memoryVector.addElement(memorycollectdata);
							}
						}

					}

					if (memtype.equals("memsize")) {
						for (int i = 0; i < valueArray.length; i++) {
							String sizevalue = valueArray[i][1];
							String freevalue = valueArray[i][0];
							String index = i + "";
							float value = 0.0f;
							String usedperc = "0";
							if (Long.parseLong(sizevalue) > 0) {
								value = (Long.parseLong(sizevalue) - Long.parseLong(freevalue)) * 100 / (Long.parseLong(sizevalue));
							}

							if (value > 0) {
								int intvalue = Math.round(value);
								allvalue = allvalue + intvalue;
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add("");
								alist.add(usedperc);
								// 内存
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
