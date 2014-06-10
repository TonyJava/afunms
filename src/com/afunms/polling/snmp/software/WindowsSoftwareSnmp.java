package com.afunms.polling.snmp.software;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.SoftwareCollectEntity;
import com.gatherResulttosql.HostDatatempsoftwareRttosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsSoftwareSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	private static Hashtable<String, String> softwareTypeHt = new Hashtable<String, String>();
	static {
		softwareTypeHt.put("1", "unknown");
		softwareTypeHt.put("2", "系统程序");
		softwareTypeHt.put("3", "设备驱动");
		softwareTypeHt.put("4", "应用程序");
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector softwareVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}

		logger.debug("Windows Software " + node.getIpAddress());
		try {
			SoftwareCollectEntity vo = null;
			String[] oids = new String[] { "1.3.6.1.2.1.25.6.3.1.2", // 名称
					"1.3.6.1.2.1.25.6.3.1.3", // id
					"1.3.6.1.2.1.25.6.3.1.4", // 类别
					"1.3.6.1.2.1.25.6.3.1.5" }; // 安装日期

			String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

			if (null != valueArray && valueArray.length > 0) {
				for (int i = 0; i < valueArray.length; i++) {
					vo = new SoftwareCollectEntity();
					vo.setIpaddress(node.getIpAddress());
					vo.setName(parseString(valueArray[i][0]));
					vo.setSwid(parseString(valueArray[i][1]));
					vo.setType(softwareTypeHt.get((parseString(valueArray[i][2]))));
					vo.setInsdate(getDate(valueArray[i][3]));
					softwareVector.addElement(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
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
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			HostDatatempsoftwareRttosql totempsql = new HostDatatempsoftwareRttosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}

	public String getDate(String hexDateString) {
		String rtDateString = "1980-01-01 23:59:59:59";
		if (null != hexDateString && !hexDateString.equals("null")) {
			String[] dateArray = hexDateString.split(":");
			if (null != dateArray && dateArray.length > 0 && dateArray.length == 8) {
				String fYear = Integer.valueOf(dateArray[0], 16).toString();
				String sYear = Integer.valueOf(dateArray[1], 16).toString();
				String month = Integer.valueOf(dateArray[2], 16).toString();
				String day = Integer.valueOf(dateArray[3], 16).toString();
				String hour = Integer.valueOf(dateArray[4], 16).toString();
				String minute = Integer.valueOf(dateArray[5], 16).toString();
				String second = Integer.valueOf(dateArray[6], 16).toString();
				String milliSecond = Integer.valueOf(dateArray[7], 16).toString();
				String year = Integer.parseInt(fYear) * 256 + Integer.parseInt(sYear) + "";
				rtDateString = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + ":" + milliSecond;
			}
		}
		return rtDateString;
	}
}
