package com.afunms.polling.snmp.device;

/*
 * ibm���˽�����Ӳ����Ϣ�ɼ�
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DeviceCollectEntity;

@SuppressWarnings("unchecked")
public class IbmDeviceSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static Hashtable device_Status = null;
	static {
		device_Status = new Hashtable();
		device_Status.put("1", "δ֪");
		device_Status.put("2", "����");
		device_Status.put("3", "�澯");
		device_Status.put("4", "����");
		device_Status.put("5", "ֹͣ");
	};
	private static Hashtable device_Type = null;
	static {
		device_Type = new Hashtable();
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.1", "����");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.2", "δ֪");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.3", "��Դ");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.4", "����");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.5", "����");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.6", "����");
		device_Type.put("1.3.6.1.2.1.47.1.1.1.1.2.7", "����");
	};

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector deviceVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));

		try {
			DeviceCollectEntity devicedata = null;
			Calendar date = Calendar.getInstance();

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
				String[] oids = new String[] { "1.3.6.1.2.1.47.1.1.1.1.2.1", //
						"1.3.6.1.2.1.47.1.1.1.1.2.2", //
						"1.3.6.1.2.1.47.1.1.1.1.2.3", //
						"1.3.6.1.2.1.47.1.1.1.1.2.4" }; //

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < valueArray.length; i++) {
					devicedata = new DeviceCollectEntity();
					String devindex = valueArray[i][0];
					String type = valueArray[i][1];
					String name = valueArray[i][2];
					String status = valueArray[i][3];
					if (status == null)
						status = "";
					if (device_Status.containsKey(status))
						status = (String) device_Status.get(status);
					devicedata.setDeviceindex(devindex);
					devicedata.setIpaddress(node.getIpAddress());
					devicedata.setName(name);
					devicedata.setStatus(status);
					devicedata.setType((String) device_Type.get(type));
					deviceVector.addElement(devicedata);

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
			if (deviceVector != null && deviceVector.size() > 0)
				ipAllData.put("device", deviceVector);

			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (deviceVector != null && deviceVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("device", deviceVector);
		}

		returnHash.put("device", deviceVector);
		return returnHash;
	}
}
