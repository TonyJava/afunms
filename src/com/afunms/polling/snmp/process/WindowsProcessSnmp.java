package com.afunms.polling.snmp.process;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.ProcessCollectEntity;
import com.gatherResulttosql.HostDatatempProcessRtTosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsProcessSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	private DecimalFormat df = new DecimalFormat("#.##");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicator) {
		Hashtable returnHash = new Hashtable();
		Vector processVector = new Vector();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicator.getNodeid()));
		if (host == null) {
			return returnHash;
		} else {
			host.setLastTime(sdf.format(date.getTime()));
		}

		logger.debug("Windows Process " + host.getIpAddress());

		try {
			ProcessCollectEntity vo = new ProcessCollectEntity();
			String[] oids = new String[] { "1.3.6.1.2.1.25.4.2.1.1", // index
					"1.3.6.1.2.1.25.4.2.1.2",// name
					"1.3.6.1.2.1.25.4.2.1.5", // parameter
					"1.3.6.1.2.1.25.4.2.1.6", // type
					"1.3.6.1.2.1.25.4.2.1.7",// status
					"1.3.6.1.2.1.25.5.1.1.2", // mem
					"1.3.6.1.2.1.25.5.1.1.1",// cpu
			};
			String[] hrMemorySizeOID = new String[] { "1.3.6.1.2.1.25.2.2" };// all
			String[][] memValueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), hrMemorySizeOID, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);

			int allMemorySize = 0;
			if (memValueArray != null && memValueArray.length > 0) {
				for (int i = 0; i < memValueArray.length; i++) {
					if (memValueArray[i][0] == null) {
						continue;
					} else {
						allMemorySize = parseInt(memValueArray[i][0]);
						break;
					}
				}
			}

			String[][] valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);

			long allTime = -1L;
			if (valueArray != null && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					if (parseString(valueArray[i][6]).equals("NaV")) {
						continue;
					} else {
						allTime += Long.parseLong(parseString(valueArray[i][6]));
					}
				}
			}
			if (allMemorySize > 0) {
				if (valueArray != null && valueArray.length > 0) {
					String hrSWRunIndex = (String) null;
					String hrSWRunName = (String) null;
					String hrSWRunPerMem = (String) null;
					String hrSWRunPerCpu = (String) null;
					String hrSWRunType = (String) null;
					String hrSWRunParameters = (String) null;
					String hrSWRunStatus = (String) null;
					for (int i = 0; i < valueArray.length; i++) {
						hrSWRunIndex = parseString(valueArray[i][0]);
						hrSWRunName = parseString(valueArray[i][1]);
						hrSWRunParameters = parseString(valueArray[i][2]);
						hrSWRunType = parseString(valueArray[i][3]);
						hrSWRunStatus = parseString(valueArray[i][4]);
						hrSWRunPerMem = parseString(valueArray[i][5]);
						hrSWRunPerCpu = parseString(valueArray[i][6]);

						if ("NaV".equals(hrSWRunPerMem) || "NaV".equals(hrSWRunPerCpu)) {
							continue;
						}
						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("MemoryUtilization");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("dynamic");
						vo.setUnit("%");
						vo.setThevalue(df.format(Double.parseDouble(hrSWRunPerMem) * 100 / allMemorySize));
						vo.setChname(hrSWRunName);
						processVector.addElement(vo);

						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("Memory");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("static");
						vo.setUnit("K");
						vo.setThevalue(hrSWRunPerMem);
						vo.setChname(hrSWRunName);
						processVector.addElement(vo);

						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("Type");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("static");
						vo.setUnit(" ");
						vo.setThevalue(HOST_hrSWRun_hrSWRunType.get(hrSWRunType).toString());
						vo.setChname(hrSWRunName);
						processVector.addElement(vo);

						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("Path");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("static");
						vo.setUnit(" ");
						vo.setThevalue(hrSWRunParameters);
						vo.setChname(hrSWRunName);
						processVector.addElement(vo);

						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("Status");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("static");
						vo.setUnit(" ");
						vo.setThevalue(HOST_hrSWRun_hrSWRunStatus.get(hrSWRunStatus).toString());
						vo.setChname(hrSWRunName);
						processVector.addElement(vo);

						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("Name");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("static");
						vo.setUnit(" ");
						vo.setThevalue(hrSWRunName);
						processVector.addElement(vo);

						vo = new ProcessCollectEntity();
						vo.setIpaddress(host.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Process");
						vo.setEntity("CpuTime");
						vo.setSubentity(hrSWRunIndex);
						vo.setRestype("dynamic");
						vo.setUnit("秒");
						vo.setThevalue(df.format(Double.parseDouble(hrSWRunPerCpu) / allTime * 100));
						vo.setChname(hrSWRunName);
						processVector.addElement(vo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (processVector != null && processVector.size() > 0) {
				ipAllData.put("process", processVector);
			}
			ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		} else {
			if (processVector != null && processVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("process", processVector);
			}
		}

		try {
			if (processVector != null && processVector.size() > 0) {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(host.getId() + "", "host", "windows");
				AlarmIndicatorsNode alarmIndicatorsNode = null;
				if (null != list && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if (alarmIndicatorsNode != null && "process".equals(alarmIndicatorsNode.getName())) {
							CheckEventUtil checkutil = new CheckEventUtil();
							checkutil.createProcessGroupEventList(host.getIpAddress(), processVector, alarmIndicatorsNode);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnHash.put("process", processVector);
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			HostDatatempProcessRtTosql temptosql = new HostDatatempProcessRtTosql();
			temptosql.CreateResultTosql(returnHash, host);
		}
		return returnHash;
	}
}
