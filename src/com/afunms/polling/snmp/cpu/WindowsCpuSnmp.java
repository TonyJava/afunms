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
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;

public class WindowsCpuSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector cpuVector = new Vector();
		List cpuList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.debug("Windows CPU " + node.getIpAddress());
		try {
			CpuCollectEntity vo = null;
			try {
				String valueString="-1";
				String[] oids = new String[] { "1.3.6.1.2.1.25.3.3.1.2" };
				String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

				if (valueArray != null && valueArray.length > 0) {
					String value = (String) null;
					String index = (String) null;
					int addValue = 0;
					int counter = 0;
					List tempList = new ArrayList();
					for (int i = 0; i < valueArray.length; i++) {
						value = valueArray[i][0];
						index = valueArray[i][1];
						// 如果值为空或者是非数值
						if (parseInt(value) == -1) {
							continue;
						}
						addValue += parseInt(value);
						counter++;

						tempList = new ArrayList();
						tempList.add(index);
						tempList.add(value);
						cpuList.add(tempList);
					}
					if(counter>0){
						valueString=String.valueOf(addValue/counter);
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
				vo.setThevalue(valueString);
				cpuVector.addElement(vo);
				
				// 对CPU值进行告警检测
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

			} catch (Exception e) {
				e.printStackTrace();
			}
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
		HostcpuResultTosql restosql = new HostcpuResultTosql();
		restosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
