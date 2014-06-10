package com.afunms.polling.snmp.cpu;

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
import com.afunms.polling.om.CpuCollectEntity;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetcpuResultTosql;

@SuppressWarnings("unchecked")
public class MotorolaCpuSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector cpuVector = new Vector();
		List cpuList = new ArrayList();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		}

		try {
			CpuCollectEntity cpudata = null;
			int result = 0;
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
				// start
				String temp = "0";
				if (node.getSysOid().startsWith("1.3.6.1.4.1.388.11.1.2")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6"// CPU利用率
					};
					String[] oids2 = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.6" };
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
							.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					if (valueArray == null || valueArray.length == 0) {// hukelei
						valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids2, node.getSnmpversion(), node.getSecuritylevel(), node
								.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					}
					int allvalue = 0;
					int flag = 0;
					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							String _value = valueArray[i][0];
							String index = valueArray[i][1];
							int value = 0;
							value = Integer.parseInt(_value);
							allvalue = allvalue + Integer.parseInt(_value);
							if (value > 0) {
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add(index);
								alist.add(_value);
								cpuList.add(alist);
							}
						}
					}

					if (flag > 0) {
						int intvalue = (allvalue / flag);
						temp = intvalue + "";
					}
				}

				if (temp == null) {
					result = 0;
				} else {
					try {
						if (temp.equalsIgnoreCase("noSuchObject")) {
							result = 0;
						} else {
							result = Integer.parseInt(temp);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						result = 0;
					}
				}

				cpudata = new CpuCollectEntity();
				cpudata.setIpaddress(node.getIpAddress());
				cpudata.setCollecttime(date);
				cpudata.setCategory("CPU");
				cpudata.setEntity("Utilization");
				cpudata.setSubentity("Utilization");
				cpudata.setRestype("dynamic");
				cpudata.setUnit("%");
				cpudata.setThevalue(result + "");
				cpuVector.add(0, cpudata);
				cpuVector.add(1, cpuList);
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

		// 对CPU值进行告警检测
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
		// 把结果转换成sql
		NetcpuResultTosql tosql = new NetcpuResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			// 采集与访问是分离模式,则不需要将监视数据写入临时表格
			NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
			totempsql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}

}
