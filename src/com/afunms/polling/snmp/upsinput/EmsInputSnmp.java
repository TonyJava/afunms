package com.afunms.polling.snmp.upsinput;

/*
 * ��Ĭ��UPS ������Ϣ��
 */

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.snmp.SnmpMibConstants;

@SuppressWarnings("unchecked")
public class EmsInputSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector inputVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}
		SystemCollectEntity systemdata = null;
		Calendar date = Calendar.getInstance();
		try {
			final String[] desc = SnmpMibConstants.UpsMibInputDesc;
			final String[] chname = SnmpMibConstants.UpsMibInputChname;
			final String[] unit = SnmpMibConstants.UpsMibInputUnit;
			String[] valueArray = new String[8];
			if (node.getSysOid().startsWith("1.3.6.1.4.1.13400.2.1")) {//
				String[] oids = new String[] { ".1.3.6.1.4.1.13400.2.1.3.3.3.2.1.0",// �����ߵ�ѹAB
						".1.3.6.1.4.1.13400.2.1.3.3.3.2.2.0",// �����ߵ�ѹBC
						".1.3.6.1.4.1.13400.2.1.3.3.3.2.3.0",// �����ߵ�ѹCA
						// "1.3.6.1.4.1.13400.2.20.2.4.1.0",//A�������ѹ
						// "1.3.6.1.4.1.13400.2.20.2.4.2.0",
						// "1.3.6.1.4.1.13400.2.20.2.4.3.0",

						".1.3.6.1.4.1.13400.2.1.3.3.3.2.4.0",// A���������
						".1.3.6.1.4.1.13400.2.1.3.3.3.2.5.0",// B���������
						".1.3.6.1.4.1.13400.2.1.3.3.3.2.6.0",// C���������

						".1.3.6.1.4.1.13400.2.1.3.3.3.2.7.0",// ����Ƶ��
						".1.3.6.1.4.1.13400.2.1.3.3.3.2.8.0",// ���빦������
				// "1.3.6.1.4.1.13400.2.20.2.4.12.0",
				// "1.3.6.1.4.1.13400.2.20.2.4.13.0"
				};
				for (int j = 0; j < oids.length; j++) {
					try {
						valueArray[j] = snmp.getMibValue(node.getIpAddress(), node.getCommunity(), oids[j]);
					} catch (Exception e) {
						valueArray = null;
						e.printStackTrace();
					}
				}
			}
			if (valueArray != null && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					systemdata = new SystemCollectEntity();
					systemdata.setIpaddress(node.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("Input");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					if (value != null && !value.equals("noSuchObject")) {
						if (desc[i].equals("SRGLYS")) {
							systemdata.setThevalue((Float.parseFloat(value) / 100) + "");
						} else {
							systemdata.setThevalue((Float.parseFloat(value) / 10) + "");
						}
					} else {
						systemdata.setThevalue("0");
					}
					inputVector.addElement(systemdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		ipAllData.put("input", inputVector);
		ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		returnHash.put("input", inputVector);

		Hashtable ipdata = new Hashtable();
		ipdata.put("input", returnHash);
		Hashtable alldata = new Hashtable();
		alldata.put(node.getIpAddress(), ipdata);
		HostCollectDataManager hostdataManager = new HostCollectDataManager();
		try {
			hostdataManager.createHostItemData(alldata, "ups");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnHash;
	}
}