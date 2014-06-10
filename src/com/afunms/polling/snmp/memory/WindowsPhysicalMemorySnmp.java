package com.afunms.polling.snmp.memory;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

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

public class WindowsPhysicalMemorySnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	DecimalFormat df = new DecimalFormat("#.##");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (host == null) {
			return returnHash;
		} else {
			host.setLastTime(sdf.format(date.getTime()));
		}
		logger.debug("Windows PhysicalMemory " + host.getIpAddress());

		try {
			MemoryCollectEntity vo = new MemoryCollectEntity();
			try {
				String[] oids = new String[] { "1.3.6.1.2.1.25.2.3.1.1",//
						"1.3.6.1.2.1.25.2.3.1.2", //
						"1.3.6.1.2.1.25.2.3.1.3", //
						"1.3.6.1.2.1.25.2.3.1.4",//
						"1.3.6.1.2.1.25.2.3.1.5",//
						"1.3.6.1.2.1.25.2.3.1.6", //
						"1.3.6.1.2.1.25.2.3.1.7" };

				String[][] valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);

				double usePercent = 0.0;
				String descr = "unknown";
				String byteUnit = "65536";// in bytes
				int totalSize = -1;
				int usedSize = -1;
				if (valueArray != null && valueArray.length > 0) {
					for (int i = 0; i < valueArray.length; i++) {
						descr = parseString(valueArray[i][2]);
						if (descr.equals("NaV") || descr.indexOf("Physical Memory") < 0) {
							continue;
						}
						byteUnit = valueArray[i][3];
						totalSize = parseInt(valueArray[i][4]);
						usedSize = parseInt(valueArray[i][5]);
						if (totalSize != -1) {
							usePercent = usedSize * 100.0 / totalSize;
						}
					}
				}

				vo = new MemoryCollectEntity();
				vo.setIpaddress(host.getIpAddress());
				vo.setCollecttime(date);
				vo.setCategory("Memory");
				vo.setEntity("Utilization");
				vo.setSubentity("PhysicalMemory");
				vo.setRestype("dynamic");
				vo.setUnit("%");
				vo.setThevalue(df.format(usePercent));
				memoryVector.addElement(vo);

				vo = new MemoryCollectEntity();
				vo.setIpaddress(host.getIpAddress());
				vo.setCollecttime(date);
				vo.setCategory("Memory");
				vo.setEntity("Capability");
				vo.setRestype("static");
				vo.setSubentity("PhysicalMemory");

				double dTotalSize = totalSize * Integer.parseInt(byteUnit) * 1.0 / 1024 / 1024;
				if (dTotalSize >= 1024) {
					dTotalSize = dTotalSize / 1024;
					vo.setUnit("G");
				} else {
					vo.setUnit("M");
				}
				vo.setThevalue(df.format(dTotalSize));
				memoryVector.addElement(vo);

				vo = new MemoryCollectEntity();
				vo.setIpaddress(host.getIpAddress());
				vo.setCollecttime(date);
				vo.setCategory("Memory");
				vo.setEntity("UsedSize");
				vo.setRestype("static");
				vo.setSubentity("PhysicalMemory");
				double dUsedSize = usedSize * Integer.parseInt(byteUnit) * 1.0 / 1024 / 1024;
				if (dUsedSize >= 1024) {
					dUsedSize = dUsedSize / 1024;
					vo.setUnit("G");
				} else {
					vo.setUnit("M");
				}
				vo.setThevalue(df.format(dUsedSize));
				memoryVector.addElement(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (memoryVector != null && memoryVector.size() > 0) {
				ipAllData.put("memory", memoryVector);
			}
			ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		} else {
			if (memoryVector != null && memoryVector.size() > 0) {
				MemoryCollectEntity vo, _vo;
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
				if (null != ipAllData && null != ipAllData.get("memory")) {
					int i = 0;
					Vector inHeapVector = (Vector) ipAllData.get("memory");
					Enumeration mE = memoryVector.elements();
					boolean isExist = false;
					while (mE.hasMoreElements()) {
						vo = (MemoryCollectEntity) mE.nextElement();
						i = 0;
						for (Enumeration em = inHeapVector.elements(); em.hasMoreElements(); i++) {
							_vo = (MemoryCollectEntity) em.nextElement();
							if (vo.getSubentity().equals(_vo.getSubentity()) && vo.getEntity().equals(_vo.getEntity())) {
								// 如果已经存在实体对象,则替换
								inHeapVector.set(i, vo);
								isExist = true;
							}
						}
						if (!isExist) {
							inHeapVector.add(vo);
						}
					}
				} else {
					ipAllData.put("memory", memoryVector);
				}
			}
		}

		returnHash.put("memory", memoryVector);
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 把采集结果生成sql
		HostPhysicalMemoryResulttosql tosql = new HostPhysicalMemoryResulttosql();
		tosql.CreateResultTosql(returnHash, host.getIpAddress());

		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
			totempsql.CreateResultTosql(returnHash, host, "PhysicalMemory");
		}
		return returnHash;
	}
}
