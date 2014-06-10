package com.afunms.polling.snmp.voltage;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.gatherResulttosql.NetDatatempvoltageRtosql;
import com.gatherResulttosql.NetvoltageResultTosql;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CiscoVoltageSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector voltageVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.info("Cisco Voltage " + node.getIpAddress());
		try {
			Interfacecollectdata vo = null;
			if (node.getSysOid().startsWith("1.3.6.1.4.1.9.")) {
				String[] oids = new String[] { "1.3.6.1.4.1.9.9.13.1.2.1.3",// ciscoEnvMonVoltageStatusValue
						"1.3.6.1.4.1.9.9.13.1.2.1.7"// ciscoEnvMonVoltageState
				};
				String[][] valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null && valueArray.length > 0) {
					String value = (String) null;
					String state = (String) null;
					String index = (String) null;
					for (int i = 0; i < valueArray.length; i++) {
						value = parseString(valueArray[i][0]);
						index = parseString(valueArray[i][1]);
						state = parseString(valueArray[i][2]);
						if (value.equals("NaV"))
							continue;
						vo = new Interfacecollectdata();
						vo.setIpaddress(node.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Voltage");
						vo.setEntity(index);
						vo.setSubentity(state);
						vo.setRestype("dynamic");
						vo.setUnit("");
						vo.setThevalue(value);
						voltageVector.addElement(vo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (voltageVector != null && voltageVector.size() > 0) {
				ipAllData.put("voltage", voltageVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (voltageVector != null && voltageVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("voltage", voltageVector);
			}
		}
		returnHash.put("voltage", voltageVector);
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "cisco", "voltage");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				CheckEventUtil checkutil = new CheckEventUtil();
				if (null != voltageVector && voltageVector.size() > 0) {
					for (int j = 0; j < voltageVector.size(); j++) {
						Interfacecollectdata data = (Interfacecollectdata) voltageVector.get(j);
						if (data != null) {
							checkutil.checkEvent(node, alarmIndicatorsnode, data.getThevalue(), data.getSubentity());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		NetvoltageResultTosql tosql = new NetvoltageResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetDatatempvoltageRtosql totempsql = new NetDatatempvoltageRtosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
