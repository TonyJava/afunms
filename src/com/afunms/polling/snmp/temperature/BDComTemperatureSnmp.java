package com.afunms.polling.snmp.temperature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.gatherResulttosql.NetDatatempTemperatureRtosql;
import com.gatherResulttosql.NetTemperatureResultTosql;

@SuppressWarnings("unchecked")
public class BDComTemperatureSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector temperatureVector = new Vector();
		Vector alarmVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}
		try {
			Interfacecollectdata interfacedata = new Interfacecollectdata();
			Calendar date = Calendar.getInstance();
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
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
				String temp = "0";
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.3320.2.1.78",// bdenvTestPt1Descr
						"1.3.6.1.4.1.3320.2.1.79"// bdenvTestPt1Measure
				};
				valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
						.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null) {
					int flag = 0;
					int result = 0;
					int allvalue = 0;
					for (int i = 0; i < valueArray.length; i++) {
						String _value = valueArray[i][1];
						String index = valueArray[i][2];
						String desc = valueArray[i][0];
						int value = 0;

						if (_value != null) {
							value = Integer.parseInt(_value);
							if (value > 0) {
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add(index);
								alist.add(_value);
								alist.add(desc);
								interfacedata = new Interfacecollectdata();
								interfacedata.setIpaddress(node.getIpAddress());
								interfacedata.setCollecttime(date);
								interfacedata.setCategory("Temperature");
								interfacedata.setEntity(index);
								interfacedata.setSubentity(desc);
								interfacedata.setRestype("dynamic");
								interfacedata.setUnit("度");
								interfacedata.setThevalue(_value + "");
								SysLogger.info(node.getIpAddress() + " 温度： " + _value);
								temperatureVector.addElement(interfacedata);
							}
						}

					}
					if (flag > 0) {
						int intvalue = (allvalue / flag);
						temp = intvalue + "";
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
					interfacedata = new Interfacecollectdata();
					interfacedata.setIpaddress(node.getIpAddress());
					interfacedata.setCollecttime(date);
					interfacedata.setCategory("Temperature");
					interfacedata.setEntity("");
					interfacedata.setSubentity("");
					interfacedata.setRestype("dynamic");
					interfacedata.setUnit("度");
					interfacedata.setThevalue(result + "");
					alarmVector.add(0, interfacedata);
				}
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
		Hashtable collectHash = new Hashtable();
		collectHash.put("temperature", alarmVector);
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "bdcom", "temperature");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node, collectHash, "net", "temperature", alarmIndicatorsnode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 把采集结果生成sql
		NetTemperatureResultTosql tosql = new NetTemperatureResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetDatatempTemperatureRtosql temptosql = new NetDatatempTemperatureRtosql();
			temptosql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
