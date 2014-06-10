package com.afunms.polling.snmp.fan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.AlarmHelper;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.EnvConfig;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.gatherResulttosql.NetDatatempfanRtosql;
import com.gatherResulttosql.NetfanResultTosql;

@SuppressWarnings("unchecked")
public class BrocadeFanSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector fanVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;
		try {
			Interfacecollectdata interfacedata = new Interfacecollectdata();
			Calendar date = Calendar.getInstance();
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
			if (ipAllData == null)
				ipAllData = new Hashtable();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (node.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.1588.2.1.1.1.1.22.1.1",// swSensorIndex
							// 温度、风扇、电源
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.2",// swSensorType
							// 类别
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.3",// swSensorStatus
							// 状态
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.4",// swSensorValue
							// 值
							"1.3.6.1.4.1.1588.2.1.1.1.1.22.1.5",// swSensorInfo
					};
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
							.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							String swSensorType = valueArray[i][1];
							String swSensorStatus = valueArray[i][2];
							String swSensorValue = valueArray[i][3];
							String swSensorInfo = valueArray[i][4];
							String index = valueArray[i][5];
							int value = 0;
							if (swSensorValue != null) {
								value = Integer.parseInt(swSensorValue);
								if (value > 0 && "2".equals(swSensorType)) {// 1-5
									if (swSensorStatus.equals("1")) {// unknown
										swSensorStatus = "未知";
									} else if (swSensorStatus.equals("2")) {// faulty
										swSensorStatus = "错误";
									} else if (swSensorStatus.equals("3")) {// below-min
										swSensorStatus = "低于最小值";
									} else if (swSensorStatus.equals("4")) {// nominal
										swSensorStatus = "正常";
									} else if (swSensorStatus.equals("5")) {// above-max
										swSensorStatus = "超过最大值";
									} else if (swSensorStatus.equals("6")) {// absent
										swSensorStatus = "缺失";
									}
									interfacedata = new Interfacecollectdata();
									interfacedata.setIpaddress(node.getIpAddress());
									interfacedata.setCollecttime(date);
									interfacedata.setCategory("Fan");
									interfacedata.setEntity(index);
									interfacedata.setSubentity(swSensorInfo);
									interfacedata.setRestype("dynamic");
									interfacedata.setUnit("");
									interfacedata.setThevalue(swSensorValue);
									interfacedata.setBak(swSensorStatus);
									SysLogger.info(node.getIpAddress() + "风扇:" + swSensorInfo + " value： " + swSensorValue);
									fanVector.addElement(interfacedata);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (fanVector != null && fanVector.size() > 0)
				ipAllData.put("fan", fanVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (fanVector != null && fanVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("fan", fanVector);
		}
		returnHash.put("fan", fanVector);

		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "brocade", "fan");

			AlarmHelper helper = new AlarmHelper();
			Hashtable<String, EnvConfig> envHashtable = helper.getAlarmConfig(node.getIpAddress(), "Fan");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				// 对风扇值进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				if (fanVector.size() > 0) {
					for (int j = 0; j < fanVector.size(); j++) {
						Interfacecollectdata data = (Interfacecollectdata) fanVector.get(j);
						if (data != null) {
							EnvConfig config = envHashtable.get(data.getEntity());
							if (config != null && config.getEnabled() == 1) {
								alarmIndicatorsnode.setAlarm_level(config.getAlarmlevel());
								alarmIndicatorsnode.setAlarm_times(config.getAlarmtimes() + "");
								alarmIndicatorsnode.setLimenvalue0(config.getAlarmvalue() + "");
								checkutil.checkEvent(node, alarmIndicatorsnode, data.getThevalue(), data.getSubentity());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 把采集结果生成sql
		NetfanResultTosql tosql = new NetfanResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetDatatempfanRtosql totempsql = new NetDatatempfanRtosql();
			totempsql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
