package com.afunms.polling.snmp.cpu;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CpuCollectEntity;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetcpuResultTosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CiscoCpuSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector cpuVector = new Vector();
		List cpuList = new ArrayList();
		List tempCpuList = new ArrayList();

		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}

		logger.debug("Cisco CPU " + node.getIpAddress());
		try {
			CpuCollectEntity vo = null;
			String[] oids = new String[] { "1.3.6.1.4.1.9.2.1.57" };
			if (node.getSysOid().trim().equals("1.3.6.1.4.1.9.12.3.1.3.587")) {
				oids = new String[] { "1.3.6.1.4.1.9.9.305.1.1.1" };
			}
			String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
			int allValue = 0;
			int counter = 0;
			if (valueArray != null && valueArray.length > 0) {
				String value = "-1";
				String index = (String) null;
				for (int i = 0; i < valueArray.length; i++) {
					value = parseString(valueArray[i][0]);
					index = parseString(valueArray[i][1]);
					if (value.equals("NaV") || value.equals("0"))
						continue;
					allValue += parseInt(value);
					counter++;

					tempCpuList = new ArrayList();
					tempCpuList.add(index);
					tempCpuList.add(value);
					cpuList.add(tempCpuList);
				}
			}
			vo = new CpuCollectEntity();
			vo.setIpaddress(node.getIpAddress());
			vo.setCollecttime(date);
			vo.setCategory("CPU");
			vo.setEntity("Utilization");
			vo.setSubentity("Utilization");
			vo.setRestype("dynamic");
			vo.setUnit("%");
			vo.setThevalue(String.valueOf(divide(allValue, counter)));
			cpuVector.addElement(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (cpuVector != null && cpuVector.size() > 0) {
				ipAllData.put("cpu", cpuVector);
			}
			if (cpuList != null && cpuList.size() > 0) {
				ipAllData.put("cpulist", cpuList);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (cpuVector != null && cpuVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("cpu", cpuVector);
			}
			if (cpuList != null && cpuList.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("cpulist", cpuList);
			}
		}
		returnHash.put("cpu", cpuVector);
		Hashtable collectHash = new Hashtable();
		collectHash.put("cpu", cpuVector);
		try {
			if (cpuVector != null && cpuVector.size() > 0) {
				for (int i = 0; i < cpuVector.size(); i++) {
					CpuCollectEntity cpucollectdata = (CpuCollectEntity) cpuVector.get(0);
					if ("Utilization".equals(cpucollectdata.getEntity())) {
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.updateData(node, nodeGatherIndicators, cpucollectdata.getThevalue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		NetcpuResultTosql tosql = new NetcpuResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
