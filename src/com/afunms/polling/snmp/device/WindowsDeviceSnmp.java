package com.afunms.polling.snmp.device;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DeviceCollectEntity;
import com.gatherResulttosql.HostDatatempDeviceRttosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsDeviceSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	private static Hashtable<String, String> device_Status = new Hashtable<String, String>();
	static {
		device_Status.put("1", "δ֪");
		device_Status.put("2", "����");
		device_Status.put("3", "�澯");
		device_Status.put("4", "����");
		device_Status.put("5", "ֹͣ");
	};
	private static Hashtable<String, String> device_Type = new Hashtable<String, String>();
	static {
		device_Type.put("1.3.6.1.2.1.25.3.1.1", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.2", "δ֪");
		device_Type.put("1.3.6.1.2.1.25.3.1.3", "CPU");
		device_Type.put("1.3.6.1.2.1.25.3.1.4", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.5", "��ӡ��");
		device_Type.put("1.3.6.1.2.1.25.3.1.6", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.10", "�Կ�");
		device_Type.put("1.3.6.1.2.1.25.3.1.11", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.12", "Э������");
		device_Type.put("1.3.6.1.2.1.25.3.1.13", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.14", "���ƽ����");
		device_Type.put("1.3.6.1.2.1.25.3.1.15", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.16", "��ӡ��");
		device_Type.put("1.3.6.1.2.1.25.3.1.17", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.18", "�Ŵ�");
		device_Type.put("1.3.6.1.2.1.25.3.1.19", "ʱ��");
		device_Type.put("1.3.6.1.2.1.25.3.1.20", "��̬�ڴ�");
		device_Type.put("1.3.6.1.2.1.25.3.1.21", "�̶��ڴ�");
	};

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector deviceVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}

		logger.debug("Windows Device " + node.getIpAddress());
		try {
			DeviceCollectEntity vo = null;
			String[] oids = new String[] { "1.3.6.1.2.1.25.3.2.1.1", // hrDeviceIndex
					"1.3.6.1.2.1.25.3.2.1.2", // hrDeviceType
					"1.3.6.1.2.1.25.3.2.1.3", // hrDeviceDescr
					"1.3.6.1.2.1.25.3.2.1.5" }; // hrDeviceStatus

			String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
			if (null != valueArray && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					vo = new DeviceCollectEntity();
					vo.setDeviceindex(parseString(valueArray[i][0]));
					vo.setIpaddress(node.getIpAddress());
					vo.setName(parseString(valueArray[i][2]));
					vo.setStatus(device_Status.get(parseString(valueArray[i][3])));
					vo.setType(device_Type.get(parseString(valueArray[i][1])));
					deviceVector.addElement(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (deviceVector != null && deviceVector.size() > 0)
				ipAllData.put("device", deviceVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (deviceVector != null && deviceVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("device", deviceVector);
		}
		returnHash.put("device", deviceVector);
		String runmodel = PollingEngine.getCollectwebflag();// �ɼ������ģʽ
		if (!"0".equals(runmodel)) {
			HostDatatempDeviceRttosql totempsql = new HostDatatempDeviceRttosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
