package com.afunms.polling.snmp.service;

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
import com.afunms.polling.om.ServiceCollectEntity;
import com.gatherResulttosql.HostDatatempserciceRttosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsServiceSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	private static Hashtable<String, String> svSvcInstalledStateHt = new Hashtable<String, String>();
	private static Hashtable<String, String> svSvcOperatingStateHt = new Hashtable<String, String>();
	private static Hashtable<String, String> svSvcCanBeUninstalledHt = new Hashtable<String, String>();
	private static Hashtable<String, String> svSvcCanBePausedHt = new Hashtable<String, String>();

	static {
		svSvcInstalledStateHt.put("1", "��ж��");
		svSvcInstalledStateHt.put("2", "��װ����");
		svSvcInstalledStateHt.put("3", "ж�ش���");
		svSvcInstalledStateHt.put("4", "�Ѱ�װ");

		svSvcOperatingStateHt.put("1", "���");
		svSvcOperatingStateHt.put("2", "�����");
		svSvcOperatingStateHt.put("3", "��ͣ����");
		svSvcOperatingStateHt.put("4", "��ͣ��");

		svSvcCanBeUninstalledHt.put("1", "����ж��");
		svSvcCanBeUninstalledHt.put("2", "��ж��");

		svSvcCanBePausedHt.put("1", "������ͣ");
		svSvcCanBePausedHt.put("2", "����ͣ");

	}

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicator) {
		Hashtable returnHash = new Hashtable();
		Vector serviceVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicator.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}

		logger.debug("Windows Service " + node.getIpAddress());
		try {
			ServiceCollectEntity vo = null;
			String[] oids = new String[] { "1.3.6.1.4.1.77.1.2.3.1.1", // ����
					"1.3.6.1.4.1.77.1.2.3.1.2", // ��װ״̬
					"1.3.6.1.4.1.77.1.2.3.1.3",// ����״̬
					"1.3.6.1.4.1.77.1.2.3.1.4",// �Ƿ��ж��
					"1.3.6.1.4.1.77.1.2.3.1.5"// �Ƿ��ֹͣ
			};
			String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
			if (null != valueArray && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					vo = new ServiceCollectEntity();
					vo.setIpaddress(node.getIpAddress());
					vo.setName(valueArray[i][0]);
					vo.setInstate(svSvcInstalledStateHt.get(parseString(valueArray[i][1])));
					vo.setOpstate(svSvcOperatingStateHt.get(parseString(valueArray[i][2])));
					vo.setUninst(svSvcCanBeUninstalledHt.get(parseString(valueArray[i][3])));
					vo.setPaused(svSvcCanBePausedHt.get(parseString(valueArray[i][4])));
					serviceVector.addElement(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (serviceVector != null && serviceVector.size() > 0) {
				ipAllData.put("winservice", serviceVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (serviceVector != null && serviceVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("winservice", serviceVector);
			}
		}

		try {
			if (serviceVector != null && serviceVector.size() > 0) {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId() + "", "host", "windows");
				AlarmIndicatorsNode alarmIndicatorsNode = null;
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if (alarmIndicatorsNode != null && "service".equals(alarmIndicatorsNode.getName())) {
							CheckEventUtil checkutil = new CheckEventUtil();
							checkutil.createHostServiceGroupEventList(node.getIpAddress(), serviceVector, alarmIndicatorsNode);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("winservice", serviceVector);
		String runmodel = PollingEngine.getCollectwebflag();// �ɼ������ģʽ
		if (!"0".equals(runmodel)) {
			HostDatatempserciceRttosql totempsql = new HostDatatempserciceRttosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
