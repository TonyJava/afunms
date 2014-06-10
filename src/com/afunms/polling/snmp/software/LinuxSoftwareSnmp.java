package com.afunms.polling.snmp.software;

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
import com.afunms.polling.om.SoftwareCollectEntity;

@SuppressWarnings("unchecked")
public class LinuxSoftwareSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector softwareVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}
		try {
			SoftwareCollectEntity softwaredata = null;
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
				String[] oids = new String[] { "1.3.6.1.2.1.25.6.3.1.2", // 名称
						"1.3.6.1.2.1.25.6.3.1.3", // id
						"1.3.6.1.2.1.25.6.3.1.4", // 类别
						"1.3.6.1.2.1.25.6.3.1.5" }; // 安装日期

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
					valueArray = null;
				}
				for (int i = 0; i < valueArray.length; i++) {
					softwaredata = new SoftwareCollectEntity();
					String name = valueArray[i][0];
					String swid = valueArray[i][1];
					String type = valueArray[i][2];
					if (type.equalsIgnoreCase("4")) {
						type = "应用软件";
					} else {
						type = "系统软件";
					}
					String insdate = valueArray[i][3];
					String swdate = getDate(insdate);
					softwaredata.setIpaddress(node.getIpAddress());
					softwaredata.setName(name);
					softwaredata.setSwid(swid);
					softwaredata.setType(type);
					softwaredata.setInsdate(swdate);
					softwareVector.addElement(softwaredata);
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
			if (softwareVector != null && softwareVector.size() > 0) {
				ipAllData.put("software", softwareVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (softwareVector != null && softwareVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("software", softwareVector);
			}
		}

		returnHash.put("software", softwareVector);
		return returnHash;
	}

	public String getDate(String swdate) {
		String[] num = swdate.split(":");
		String num1 = Integer.valueOf(num[0], 16).toString();
		String num2 = Integer.valueOf(num[1], 16).toString();
		String num3 = Integer.valueOf(num[2], 16).toString();
		String num4 = Integer.valueOf(num[3], 16).toString();
		String num5 = Integer.valueOf(num[4], 16).toString();
		String num6 = Integer.valueOf(num[5], 16).toString();
		String num7 = Integer.valueOf(num[6], 16).toString();
		String num8 = Integer.valueOf(num[7], 16).toString();
		String swyear = Integer.parseInt(num1) * 256 + Integer.parseInt(num2) + "";
		String swnewdate = swyear + "-" + num3 + "-" + num4 + " " + num5 + ":" + num6 + ":" + num7 + ":" + num8;
		return swnewdate;

	}
}
