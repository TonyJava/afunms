package com.afunms.polling.snmp.temperature;

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
import com.gatherResulttosql.NetTemperatureResultTosql;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class H3CTemperatureSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector temperatureVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.info("HH3C Temperature " + node.getIpAddress());
		try {
			Interfacecollectdata interfacedata = new Interfacecollectdata();
			if (node.getSysOid().startsWith("1.3.6.1.4.1.25506.") || node.getSysOid().startsWith("1.3.6.1.4.1.2011.")) {
				String[] oids = new String[] { "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.12.14" };
				if (node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.25506.1.149")) {
					oids = new String[] { "1.3.6.1.4.1.2011.2.23.1.9.1.3.1.3" };
				}
				String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null && valueArray.length > 0) {
					String value = (String) null;
					for (int i = 0; i < valueArray.length; i++) {
						value = parseString(valueArray[i][0]);
						if (value.equals("NaV") || value.equals("0"))
							continue;

						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(node.getIpAddress());
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Temperature");
						interfacedata.setEntity("H3C");
						interfacedata.setSubentity(parseString(valueArray[i][1]));
						interfacedata.setRestype("dynamic");
						interfacedata.setUnit("¶È");
						interfacedata.setThevalue(value);
						temperatureVector.addElement(interfacedata);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (temperatureVector != null && temperatureVector.size() > 0) {
				ipAllData.put("temperature", temperatureVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (temperatureVector != null && temperatureVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("temperature", temperatureVector);
			}
		}

		returnHash.put("temperature", temperatureVector);
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "h3c", "temperature");
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.updateData(node, returnHash, "net", "temperature", alarmIndicatorsnode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		NetTemperatureResultTosql tosql = new NetTemperatureResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		return returnHash;
	}
}
