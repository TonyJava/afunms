package com.afunms.polling.snmp.memory;

import java.text.DecimalFormat;
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
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class H3CMemorySnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	DecimalFormat df = new DecimalFormat("#.##");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector memoryVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.debug("H3C  Memory " + node.getIpAddress());

		try {

			String type = "P";// 有两中类型 memp 代表百分比，memsize 代表大小
			if (node.getSysOid().startsWith("1.3.6.1.4.1.2011.") //
					|| node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.151") //
					|| node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.149")) {//
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.2011.6.1.2.1.1.2",// hwMemSize
						"1.3.6.1.4.1.2011.6.1.2.1.1.3"// hwMemFree
				};
				type = "AF";
				// add at 2012-06-05 by hp
				if (node.getSysOid().trim().startsWith("1.3.6.1.4.1.2011.2.62.2")) {
					oids = new String[] { "1.3.6.1.4.1.2011.6.3.5.1.1.2",// 总共的内存
							"1.3.6.1.4.1.2011.6.3.5.1.1.3"// 空闲内存
					};
					type = "AF";
				}
				if (node.getSysOid().equals("1.3.6.1.4.1.2011.1.1.1.12811") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.10.1.89")//
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.1.1.1.2631")) {// QuidWay
																					// R2631
					oids = new String[] { "1.3.6.1.4.1.2011.2.2.5.1",// 已用内存
							"1.3.6.1.4.1.2011.2.2.5.2"// 空闲内存
					};
					type = "UF";
				}
				if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.170.2")//
						|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.145") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.170.3")) {
					// 5700
					// Huawei S9306 1.3.6.1.4.1.2011.2.170.2
					// Huawei S9312 1.3.6.1.4.1.2011.2.170.3
					oids = new String[] { "1.3.6.1.4.1.2011.6.3.5.1.1.2",// 已用内存
							"1.3.6.1.4.1.2011.6.3.5.1.1.3"// 空闲内存
					};
					type = "UF";
				}
				if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.45") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.10.1.211") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.46")) {
					// Quidway AR46-40 1.3.6.1.4.1.2011.2.45
					// Quidway AR46-80 1.3.6.1.4.1.2011.2.46
					// Quidway S3552P EA 1.3.6.1.4.1.2011.10.1.211
					oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.8"// 内存利用率
					};
				}
				if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.31")) {
					// NE 20s 1.3.6.1.4.1.2011.2.31
					oids = new String[] { "1.3.6.1.4.1.2011.2.17.6.9.1.2"// 内存利用率
					};
				}
				if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.5") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.224.5")// ar2220
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.88.2") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.9") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.3") //
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.62.2.8")) {
					// NE40E-3 1.3.6.1.4.1.2011.2.62.2.8
					// NE40E 1.3.6.1.4.1.2011.2.62.2.5
					// NE 5000E 1.3.6.1.4.1.2011.2.62.2.3
					// NE 20E-8 1.3.6.1.4.1.2011.2.88.2
					oids = new String[] { "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7"// 内存利用率
					};
				}
				if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.23.97")//
						|| node.getSysOid().equals("1.3.6.1.4.1.2011.2.23.95")) {
					oids = new String[] { "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7"// 内存利用率
					};
				}
				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null && valueArray.length > 0) {
					MemoryCollectEntity vo = null;
					// 根据不同的类型来判断
					if (type.equals("P")) {
						String usePercent = "-1";
						for (int i = 0; i < valueArray.length; i++) {
							usePercent = parseString(valueArray[i][0]);
							if (usePercent.equals("NaV") || usePercent.equals("0"))
								continue;
							vo = new MemoryCollectEntity();
							vo.setIpaddress(node.getIpAddress());
							vo.setCollecttime(date);
							vo.setCategory("Memory");
							vo.setEntity("Utilization");
							vo.setSubentity(parseString(valueArray[i][1]));
							vo.setRestype("dynamic");
							vo.setUnit("%");
							vo.setThevalue(usePercent);
							memoryVector.addElement(vo);
						}
					} else if (type.equals("AF")) {
						double usePercent = 0.0;
						String size = (String) null;
						String free = (String) null;
						String index = (String) null;
						for (int i = 0; i < valueArray.length; i++) {
							size = parseString(valueArray[i][0]);
							free = parseString(valueArray[i][1]);
							index = parseString(valueArray[i][2]);
							if (size.equals("NaV") || size.equals("0") || free.equals("NaV"))
								continue;

							usePercent = (Long.parseLong(size) - Long.parseLong(free) * 100.0) / Long.parseLong(size);

							vo = new MemoryCollectEntity();
							vo.setIpaddress(node.getIpAddress());
							vo.setCollecttime(date);
							vo.setCategory("Memory");
							vo.setEntity("Utilization");
							vo.setSubentity(index);
							vo.setRestype("dynamic");
							vo.setUnit("%");
							vo.setThevalue(df.format(usePercent));
							memoryVector.addElement(vo);
						}
					} else if (type.equals("UF")) {
						double usePercent = 0.0;
						String use = (String) null;
						String free = (String) null;
						String index = (String) null;
						for (int i = 0; i < valueArray.length; i++) {
							use = parseString(valueArray[i][0]);
							free = parseString(valueArray[i][1]);
							index = parseString(valueArray[i][2]);
							if (use.equals("NaV") || free.equals("NaV"))
								continue;

							if ((Long.parseLong(use) + Long.parseLong(free)) > 0)
								usePercent = (Long.parseLong(use)) * 100.0 / (Long.parseLong(use) + Long.parseLong(free));

							vo = new MemoryCollectEntity();
							vo.setIpaddress(node.getIpAddress());
							vo.setCollecttime(date);
							vo.setCategory("Memory");
							vo.setEntity("Utilization");
							vo.setSubentity(index);
							vo.setRestype("dynamic");
							vo.setUnit("%");
							vo.setThevalue(df.format(usePercent));
							memoryVector.addElement(vo);
						}
					}
				}
			} else if (node.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.8" };
				String[] oids_ = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.8" };

				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 2, 1000 * 10);
				if (valueArray == null || valueArray.length == 0) {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids_, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 2, 1000 * 10);
				}
				if (valueArray != null && valueArray.length > 0) {
					MemoryCollectEntity vo = null;
					String usePercent = "-1";
					for (int i = 0; i < valueArray.length; i++) {
						usePercent = parseString(valueArray[i][0]);
						if (usePercent.equals("NaV") || usePercent.equals("0"))
							continue;
						vo = new MemoryCollectEntity();
						vo.setIpaddress(node.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Memory");
						vo.setEntity("Utilization");
						vo.setSubentity(parseString(valueArray[i][1]));
						vo.setRestype("dynamic");
						vo.setUnit("%");
						vo.setThevalue(usePercent);
						memoryVector.addElement(vo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (memoryVector != null && memoryVector.size() > 0)
				ipAllData.put("memory", memoryVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (memoryVector != null && memoryVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("memory", memoryVector);
		}

		returnHash.put("memory", memoryVector);
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
