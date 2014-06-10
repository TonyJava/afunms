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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class H3CCpuSnmp extends SnmpMonitor {
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
		logger.info("HH3C CPU " + node.getIpAddress());
		try {
			CpuCollectEntity vo = null;
			try {
				int allValue = 0;
				int counter = 0;
				if (node.getSysOid().startsWith("1.3.6.1.4.1.2011.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.2011.6.1.1.1.4" };
					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.26.2") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.19") // S6506
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.21") // s3526E
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.22") // s2026
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.24") // s2026E
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.27") // s3526E-FM
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.28") // s3526E-FS
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.29") // s3050C
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.30") // s6503
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.37") // s3552G
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.39") // s3528G
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.40") // s3528P
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.80") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.99")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.19")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.42") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.55") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.149") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.161") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.191") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.219") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.246") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.297")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.1") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.13")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.188") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.515")) {//
						oids = new String[] { "1.3.6.1.4.1.2011.5.1.1.1.4" };
					}
					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.4") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.2")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.8") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.6.2") // MA5200
					) {
						// NE40E-3 1.3.6.1.4.1.2011.2.62.2.8 昆山广电 以WALK的MIB文件验证过
						// NE80E 1.3.6.1.4.1.2011.2.62.2.2 昆山广电 以WALK的MIB文件验证过
						// NE5000EMulti 1.3.6.1.4.1.2011.2.62.2.4
						oids = new String[] { "1.3.6.1.4.1.2011.6.3.4.1.4" }; // 前5分钟利用率
					}
					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.31")) {
						oids = new String[] { "1.3.6.1.4.1.2011.2.17.4.4.1.7" };
					}

					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12809") // ar28-09
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12810")// ar28-10
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12811") // ar28-11
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12830")// ar28-30
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12831") // ar28-31
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12840")// ar28-40
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.1.1.1.12880")) // ar28-80
					{
						oids = new String[] { "1.3.6.1.4.1.2011.2.2.4.12" };
					}

					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.49") // NE20
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.5")// NE40E
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.88.2") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.3")// NE5000E
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.62.2.9")) {
						oids = new String[] { "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5" };
					}

					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.97") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.95") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.23.91")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.1") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.3") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.183.1")) {//
						oids = new String[] { "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5" };
					}

					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.88")) {
						oids = new String[] { "1.3.6.1.4.1.2011.5.12.2.1.1.1.1.5" };
					}
					if (node.getSysOid().equals("1.3.6.1.4.1.2011.2.45") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.10.1.88")) {
						oids = new String[] { "1.3.6.1.4.1.2011.5.12.2.1.1.1.1.5" };
					}
					if (node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.2") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.2011.2.170.3")) {
						oids = new String[] { "1.3.6.1.4.1.2011.6.3.4.1.2" };
					}

					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					if (valueArray == null || valueArray.length == 0) {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					}
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
				} else if (node.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6" };
					String[] oids2 = new String[] { "1.3.6.1.4.1.25506.2.6.1.1.1.1.6" };
					String[] oids3 = new String[] { "1.3.6.1.4.1.2011.6.1.1.1.4" };

					if (node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.149") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.297")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.19") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.42")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.55")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.149")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.161") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.191") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.219") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.246")//
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.1") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.13") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.188") //
							|| node.getSysOid().trim().equals("1.3.6.1.4.1.25506.1.515")) {
						oids = new String[] { "1.3.6.1.4.1.2011.6.1.1.1.4" };
					}
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					if (valueArray == null || valueArray.length == 0) {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids2, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					}
					if (valueArray == null || valueArray.length == 0) {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids3, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					}
					if (valueArray != null && valueArray.length > 0) {
						String value = "-1";
						for (int i = 0; i < valueArray.length; i++) {
							value = parseString(valueArray[i][0]);
							if (value.equals("NaV") || value.equals("0"))
								continue;
							allValue += parseInt(parseString(valueArray[i][0]));
							counter++;

							tempCpuList = new ArrayList();
							tempCpuList.add(parseString(valueArray[i][1]));
							tempCpuList.add(value);
							cpuList.add(tempCpuList);
						}
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
				cpuVector.add(0, vo);
				cpuVector.add(1, cpuList);
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
