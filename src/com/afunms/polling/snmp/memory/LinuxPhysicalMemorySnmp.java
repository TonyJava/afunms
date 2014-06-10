package com.afunms.polling.snmp.memory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
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
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

@SuppressWarnings("unchecked")
public class LinuxPhysicalMemorySnmp extends SnmpMonitor {

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		try {
			MemoryCollectEntity memorydata = new MemoryCollectEntity();
			Calendar date = Calendar.getInstance();
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				String[] oids = { "1.3.6.1.2.1.25.2.3.1.2", "1.3.6.1.2.1.25.2.3.1.4", "1.3.6.1.2.1.25.2.3.1.5", "1.3.6.1.2.1.25.2.3.1.6" };

				String[][] results = null;

				Float result1 = null;

				try {
					results = SnmpUtils.getTemperatureTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), host.getSecuritylevel(), host
							.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
				}
				float physize = 0f;
				float phyused = 0f;
				String phybyte = "";
				if (results != null) {
					for (int i = 0; i < results.length; ++i) {
						String type = results[i][0];
						if ("1.3.6.1.2.1.25.2.1.2".equals(type)) {
							phybyte = results[i][1];
							physize = Float.parseFloat(results[i][2]);
							phyused = Float.parseFloat(results[i][3]);
							result1 = Float.valueOf(100.0F * phyused / physize);
						}
					}
				}

				date = Calendar.getInstance();
				if (result1 != null) {
					memorydata = new MemoryCollectEntity();
					memorydata.setIpaddress(host.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Utilization");
					memorydata.setSubentity("PhysicalMemory");
					memorydata.setRestype("dynamic");
					memorydata.setUnit("%");
					memorydata.setThevalue(Float.toString(result1));

					memoryVector.add(memorydata);
					memorydata = new MemoryCollectEntity();
					memorydata.setIpaddress(host.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Capability");
					memorydata.setRestype("static");
					memorydata.setSubentity("PhysicalMemory");

					float size = 0.0f;
					size = physize * Long.parseLong(phybyte) * 1.0f / 1024 / 1024;
					if (size >= 1024.0f) {
						size = size / 1024;
						memorydata.setUnit("G");
					} else {
						memorydata.setUnit("M");
					}
					memorydata.setThevalue(Float.toString(size));
					memoryVector.addElement(memorydata);
					memorydata = new MemoryCollectEntity();
					memorydata.setIpaddress(host.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("UsedSize");
					memorydata.setRestype("static");
					memorydata.setSubentity("PhysicalMemory");
					size = phyused * 1.0f / 1024;
					if (size >= 1024.0f) {
						size = size / 1024;
						memorydata.setUnit("G");
					} else {
						memorydata.setUnit("M");
					}
					memorydata.setThevalue(Float.toString(size));
					memoryVector.addElement(memorydata);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		Vector toAddVector = new Vector();
		Hashtable formerHash = new Hashtable();

		if ((ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			if (((Hashtable) (ShareData.getSharedata().get(host.getIpAddress()))).containsKey("memory")) {
				Vector formerMemoryVector = (Vector) ((Hashtable) (ShareData.getSharedata().get(host.getIpAddress()))).get("memory");
				if (formerMemoryVector != null && formerMemoryVector.size() > 0) {
					for (int i = 0; i < formerMemoryVector.size(); i++) {
						MemoryCollectEntity memorydata = (MemoryCollectEntity) formerMemoryVector.get(i);
						formerHash.put(memorydata.getSubentity() + ":" + memorydata.getEntity(), memorydata);
					}
				}
			}
		}

		if (memoryVector != null && memoryVector.size() > 0) {
			for (int j = 0; j < memoryVector.size(); j++) {
				MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(j);
				if (formerHash.containsKey(memorydata.getSubentity() + ":" + memorydata.getEntity())) {
					formerHash.remove(memorydata.getSubentity() + ":" + memorydata.getEntity());
					formerHash.put(memorydata.getSubentity() + ":" + memorydata.getEntity(), memorydata);
				} else {
					toAddVector.add(memorydata);
				}
			}
		}
		if (formerHash.elements() != null && formerHash.size() > 0) {
			for (Enumeration enumeration = formerHash.keys(); enumeration.hasMoreElements();) {
				String keys = (String) enumeration.nextElement();
				MemoryCollectEntity memorydata = (MemoryCollectEntity) formerHash.get(keys);
				toAddVector.add(memorydata);
			}
		}

		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (toAddVector != null && toAddVector.size() > 0) {
				ipAllData.put("memory", toAddVector);
			}
			ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		} else {
			if (toAddVector != null && toAddVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("memory", toAddVector);
			}

		}
		if (memoryVector != null && memoryVector.size() > 0) {
			int thevalue = 0;
			for (int i = 0; i < memoryVector.size(); i++) {
				MemoryCollectEntity memorycollectdata = (MemoryCollectEntity) memoryVector.get(i);
				if ("Utilization".equals(memorycollectdata.getEntity())) {
					if (Double.parseDouble(memorycollectdata.getThevalue()) > thevalue) {
						thevalue = (int) Double.parseDouble(memorycollectdata.getThevalue());
					}
				}
			}
			CheckEventUtil checkutil = new CheckEventUtil();
			checkutil.updateData(host, nodeGatherIndicators, thevalue + "", null);
		}
		returnHash.put("memory", toAddVector);

		HostPhysicalMemoryResulttosql tosql = new HostPhysicalMemoryResulttosql();
		tosql.CreateResultTosql(returnHash, host.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
			totempsql.CreateResultTosql(returnHash, host);
		}

		return returnHash;
	}
}
