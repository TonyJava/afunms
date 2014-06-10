package com.afunms.polling.snmp.system;

import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.HdcMessage;
import com.gatherdb.GathersqlListManager;

@SuppressWarnings("unchecked")
public class HdcSysInfoSnmp extends SnmpMonitor {

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector sysinfo = new Vector();
		HdcMessage hdcMessage;
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return null;
		}
		if (node.getIpAddress().equals("")) {
			return null;
		}
		try {
			String[][] valueArray = null;
			String[] oids = new String[] { ".1.3.6.1.4.1.116.5.11.4.1.1.5.1.1",// raidlistSerialNumber
					// 产品序列号
					".1.3.6.1.4.1.116.5.11.4.1.1.5.1.2",// raidlistMibNickName
					// mib名称
					".1.3.6.1.4.1.116.5.11.4.1.1.5.1.3",// raidlistDKCMainVersion
					// dkc版本
					".1.3.6.1.4.1.116.5.11.4.1.1.5.1.4",// raidlistDKCProductName
			// dkc产品名
			};
			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node
					.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					String raidlistSerialNumber = valueArray[i][0];
					String raidlistMibNickName = valueArray[i][1];
					String raidlistDKCMainVersion = valueArray[i][2];
					String raidlistDKCProductName = valueArray[i][3];
					hdcMessage = new HdcMessage();
					hdcMessage.setRaidlistSerialNumber(raidlistSerialNumber);
					hdcMessage.setRaidlistMibNickName(raidlistMibNickName);
					hdcMessage.setRaidlistDKCMainVersion(raidlistDKCMainVersion);
					hdcMessage.setRaidlistDKCProductName(raidlistDKCProductName);
					sysinfo.addElement(hdcMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (sysinfo != null && sysinfo.size() > 0) {
				ipAllData.put("sysinfo", sysinfo);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (sysinfo != null && sysinfo.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("sysinfo", sysinfo);
			}
		}

		returnHash.put("sysinfo", sysinfo);
		// 把采集结果生成sql

		this.CreateResultTosql(returnHash, node);
		return returnHash;
	}

	public void CreateResultTosql(Hashtable dataresult, Host node) {
		if (dataresult != null && dataresult.size() > 0) {
			Vector sysInfoVector = null;
			HdcMessage hdcVo = null;
			String hendsql = "insert into hdc_sys_info (raidlistSerialNumber,raidlistMibNickName,raidlistDKCMainVersion,raidlistDKCProductName,nodeid) values(";
			String endsql = "')";
			String deleteSql = "delete from hdc_sys_info where nodeid='" + node.getId() + "'";
			sysInfoVector = (Vector) dataresult.get("sysinfo");
			Vector list = new Vector();
			if (sysInfoVector != null && sysInfoVector.size() > 0) {
				for (int i = 0; i < sysInfoVector.size(); i++) {
					hdcVo = (HdcMessage) sysInfoVector.elementAt(i);
					StringBuffer sbuffer = new StringBuffer(150);
					sbuffer.append(hendsql);
					sbuffer.append("'").append(hdcVo.getRaidlistDKCMainVersion()).append("',");
					sbuffer.append("'").append(hdcVo.getRaidlistDKCProductName()).append("',");
					sbuffer.append("'").append(hdcVo.getRaidlistMibNickName()).append("',");
					sbuffer.append("'").append(hdcVo.getRaidlistSerialNumber()).append("',");
					sbuffer.append("'").append(node.getId());
					sbuffer.append(endsql);
					list.add(sbuffer.toString());
					sbuffer = null;
				}
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list = null;
			}
		}
	}
}
