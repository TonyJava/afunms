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
public class CiscoMemorySnmp extends SnmpMonitor {
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
		logger.info("Cisco Memory " + node.getIpAddress());

		try {
			MemoryCollectEntity vo = null;
			if (node.getSysOid().startsWith("1.3.6.1.4.1.9.")) {
				String[] oids = new String[] { "1.3.6.1.4.1.9.9.48.1.1.1.5",// ciscoMemoryPoolUsed
						"1.3.6.1.4.1.9.9.48.1.1.1.6"// ciscoMemoryPoolFree
				};
				String[][] valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null && valueArray.length > 0) {
					String use = (String) null;
					String free = (String) null;
					String index = (String) null;
					double usedpercent = 0.0;
					for (int i = 0; i < valueArray.length; i++) {
						use = parseString(valueArray[i][0]);
						free = parseString(valueArray[i][1]);
						index = parseString(valueArray[i][2]);
						if (use.equals("NaV") || use.equals("0") || free.equals("NaV"))
							continue;
						if (Long.parseLong(use) + Long.parseLong(free) > 0) {
							usedpercent = Long.parseLong(use) * 100.0 / (Long.parseLong(use) + Long.parseLong(free));
						}
						vo = new MemoryCollectEntity();
						vo.setIpaddress(node.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Memory");
						vo.setEntity("Utilization");
						vo.setSubentity(index);
						vo.setRestype("dynamic");
						vo.setUnit("");
						vo.setThevalue(df.format(usedpercent));
						memoryVector.addElement(vo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
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
