package com.afunms.polling.snmp.fan;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.AlarmHelper;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.config.model.EnvConfig;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.gatherResulttosql.NetfanResultTosql;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class H3CFanSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	DecimalFormat df = new DecimalFormat("#.##");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector fanVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.info("HH3C Fan " + node.getIpAddress());
		try {
			Interfacecollectdata vo = new Interfacecollectdata();
			if (node.getSysOid().startsWith("1.3.6.1.4.1.2011.") || node.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.2011.2.23.1.9.1.1.1.1",// hwDevMFanNum
						"1.3.6.1.4.1.2011.2.23.1.9.1.1.1.2"// hwDevMFanStatus
				};
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (valueArray != null && valueArray.length > 0) {
					for (int i = 0; i < valueArray.length; i++) {
						vo = new Interfacecollectdata();
						vo.setIpaddress(node.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Fan");
						vo.setEntity(parseString(valueArray[i][2]));
						vo.setSubentity(parseString(valueArray[i][0]));
						vo.setRestype("dynamic");
						vo.setUnit("");
						vo.setThevalue(parseString(valueArray[i][1]));
						fanVector.addElement(vo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
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
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "h3c", "fan");

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
		NetfanResultTosql tosql = new NetfanResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		return returnHash;
	}
}
