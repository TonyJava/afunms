package com.afunms.polling.snmp.disk;

import java.text.SimpleDateFormat;
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
import com.afunms.polling.om.DiskCollectEntity;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostdiskResultosql;

@SuppressWarnings("unchecked")
public class LinuxDiskSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector diskVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		// 判断是否在采集时间段内
		try {
			DiskCollectEntity diskdata = null;
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
				String[] oids = new String[] { "1.3.6.1.2.1.25.2.3.1.1", "1.3.6.1.2.1.25.2.3.1.2", "1.3.6.1.2.1.25.2.3.1.3", "1.3.6.1.2.1.25.2.3.1.4", "1.3.6.1.2.1.25.2.3.1.5",
						"1.3.6.1.2.1.25.2.3.1.6", "1.3.6.1.2.1.25.2.3.1.7" };

				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
					SysLogger.error(node.getIpAddress() + "_LinuxSnmp");
				}
				for (int i = 0; i < valueArray.length; i++) {
					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(node.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");
					diskdata.setRestype("static");
					diskdata.setUnit("%");
					String descriptions = valueArray[i][2];
					String byteunit = valueArray[i][3];
					String desc = "";
					if (descriptions == null)
						descriptions = "";
					if (descriptions.indexOf("\\") >= 0) {
						desc = descriptions.substring(0, descriptions.indexOf("\\")) + "/" + descriptions.substring(descriptions.indexOf("\\") + 1, descriptions.length());
					} else {
						desc = descriptions;
					}
					diskdata.setSubentity(desc);
					float value = 0.0f;
					String svb4 = valueArray[i][4];
					String svb5 = valueArray[i][5];
					if (svb4 == null || svb5 == null)
						continue;
					int allsize = Integer.parseInt(svb4.trim());
					int used = Integer.parseInt(svb5.trim());
					if (allsize != 0) {
						value = used * 100.0f / allsize;
					} else {
						value = 0.0f;
					}
					diskdata.setThevalue(Float.toString(value));

					if (diskdata.getSubentity().equals("Memory Buffers")) {
					} else if (diskdata.getSubentity().equals("Real Memory")) {
					} else if (diskdata.getSubentity().equals("Swap Space")) {
					} else
						diskVector.addElement(diskdata);

					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(node.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("AllSize");
					diskdata.setRestype("static");
					diskdata.setSubentity(desc);
					float size = 0.0f;
					size = allsize * Long.parseLong(byteunit) * 1.0f / 1024 / 1024;
					String unit = "";
					if (size >= 1024.0f) {
						size = size / 1024;
						diskdata.setUnit("G");
						unit = "G";
					} else {
						diskdata.setUnit("M");
						unit = "M";
					}
					diskdata.setThevalue(Float.toString(size));
					if (!diskdata.getSubentity().equals("Memory Buffers") && !diskdata.getSubentity().equals("Real Memory") && !diskdata.getSubentity().equals("Swap Space"))
						diskVector.addElement(diskdata);
					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(node.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UsedSize");
					diskdata.setRestype("static");
					diskdata.setSubentity(desc);
					size = used * Long.parseLong(byteunit) * 1.0f / 1024 / 1024;
					if ("G".equals(unit)) {
						size = size / 1024;
						diskdata.setUnit("G");
					} else {
						diskdata.setUnit("M");
					}
					diskdata.setThevalue(Float.toString(size));
					if (!diskdata.getSubentity().equals("Memory Buffers") && !diskdata.getSubentity().equals("Real Memory") && !diskdata.getSubentity().equals("Swap Space"))
						diskVector.addElement(diskdata);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// end
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (diskVector != null && diskVector.size() > 0)
				ipAllData.put("disk", diskVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (diskVector != null && diskVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("disk", diskVector);

		}
		returnHash.put("disk", diskVector);

		// 进行磁盘告警检测
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "linux");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskperc")) {
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkDisk(node, diskVector, alarmIndicatorsnode);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 进行磁盘告警检测
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "linux");
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskinc")) {
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkDisk(node, diskVector, alarmIndicatorsnode);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 把采集结果生成sql
		HostdiskResultosql tosql = new HostdiskResultosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());

		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			HostDatatempDiskRttosql temptosql = new HostDatatempDiskRttosql();
			temptosql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
