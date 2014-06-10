package com.afunms.polling.snmp.disk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DiskCollectEntity;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostdiskResultosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsDiskSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Vector diskVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.debug("Windows Disk " + node.getIpAddress());
		try {
			DiskCollectEntity vo = null;
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}

			String[] oids = new String[] { "1.3.6.1.2.1.25.2.3.1.1", "1.3.6.1.2.1.25.2.3.1.2", //
					"1.3.6.1.2.1.25.2.3.1.3", //
					"1.3.6.1.2.1.25.2.3.1.4", //
					"1.3.6.1.2.1.25.2.3.1.5",//
					"1.3.6.1.2.1.25.2.3.1.6",//
					"1.3.6.1.2.1.25.2.3.1.7" };

			String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

			if (null != valueArray && valueArray.length > 0) {
				String description = (String) null;
				String byteUnit = (String) null;
				BigInteger allSize = (BigInteger) null;
				BigInteger usedSize = (BigInteger) null;

				BigInteger dChild = new BigInteger("1024");

				String unit = "M";
				int lenght = 0;

				DecimalFormat df = new DecimalFormat();
				df.applyPattern("0.0");

				String diskInc = "0.0";
				float utilization = 0.0f;
				float lastUtilization = 0.0f;

				long usAllBlock = 0L;
				long usUsedBlock = 0L;

				for (int i = 0; i < valueArray.length; i++) {
					utilization = 0.0f;
					description = parseString(valueArray[i][2]);
					if (description.equals("NaV") || description.indexOf("Memory") >= 0) {
						continue;
					} else {
						description = description.substring(0, 3).replace("\\", "/");
					}
					byteUnit = valueArray[i][3];
					usAllBlock = new Integer(valueArray[i][4]) & Integer.MAX_VALUE;
					if (valueArray[i][4].indexOf("-") >= 0) {
						usAllBlock |= 0x80000000L;
					}

					usUsedBlock = new Integer(valueArray[i][5]) & Integer.MAX_VALUE;
					if (valueArray[i][5].indexOf("-") >= 0) {
						usUsedBlock |= 0x80000000L;
					}

					allSize = new BigInteger(Long.toString(usAllBlock)).multiply(new BigInteger(byteUnit));
					usedSize = new BigInteger(Long.toString(usUsedBlock)).multiply(new BigInteger(byteUnit));
					if (allSize.compareTo(new BigInteger("0")) != 0) {
						utilization = new BigDecimal(valueArray[i][5]).divide(new BigDecimal(valueArray[i][4]), 2, BigDecimal.ROUND_HALF_UP).floatValue() * 100;
					}

					// allSize
					vo = new DiskCollectEntity();
					vo.setIpaddress(node.getIpAddress());
					vo.setCollecttime(date);
					vo.setCategory("Disk");
					vo.setEntity("AllSize");
					vo.setRestype("static");
					vo.setSubentity(description);
					lenght = allSize.abs().toString().length();
					if (lenght > 13) {
						unit = "T";
						allSize = allSize.divide(dChild.pow(4));
					} else if (lenght > 9) {
						unit = "G";
						allSize = allSize.divide(dChild.pow(3));
					} else if (lenght > 7) {
						unit = "M";
						allSize = allSize.divide(dChild.pow(2));
					} else if (lenght > 5) {
						unit = "K";
						allSize = allSize.divide(dChild.pow(1));
					}
					vo.setUnit(unit);
					vo.setThevalue(df.format(allSize.floatValue()));
					diskVector.addElement(vo);

					// usedSize
					vo = new DiskCollectEntity();
					vo.setIpaddress(node.getIpAddress());
					vo.setCollecttime(date);
					vo.setCategory("Disk");
					vo.setEntity("UsedSize");
					vo.setRestype("static");
					vo.setSubentity(description);
					lenght = usedSize.abs().toString().length();
					if (lenght > 13) {
						unit = "T";
						usedSize = usedSize.divide(dChild.pow(4));
					} else if (lenght > 9) {
						unit = "G";
						usedSize = usedSize.divide(dChild.pow(3));
					} else if (lenght > 7) {
						unit = "M";
						usedSize = usedSize.divide(dChild.pow(2));
					} else if (lenght > 5) {
						unit = "K";
						usedSize = usedSize.divide(dChild.pow(1));
					}
					vo.setUnit(unit);
					vo.setThevalue(df.format(usedSize.floatValue()));
					diskVector.addElement(vo);

					vo = new DiskCollectEntity();
					vo.setIpaddress(node.getIpAddress());
					vo.setCollecttime(date);
					vo.setCategory("Disk");
					vo.setEntity("Utilization");
					vo.setRestype("static");
					vo.setSubentity(description);
					vo.setUnit("%");
					vo.setThevalue(df.format(utilization));
					diskVector.addElement(vo);

					Vector tempVector = (Vector) ipAllData.get("disk");
					if (tempVector != null && tempVector.size() > 0) {
						for (int j = 0; j < tempVector.size(); j++) {
							DiskCollectEntity diskEntity = (DiskCollectEntity) tempVector.elementAt(j);
							if ((description).equals(diskEntity.getSubentity()) && "Utilization".equals(diskEntity.getEntity())) {
								lastUtilization = Float.parseFloat(diskEntity.getThevalue());
							}
						}
					} else {
						lastUtilization = utilization;
					}
					if (lastUtilization == 0) {
						lastUtilization = utilization;
					}
					if (utilization - lastUtilization > 0) {
						diskInc = (utilization - lastUtilization) + "";
					}

					vo = new DiskCollectEntity();
					vo.setIpaddress(node.getIpAddress());
					vo.setCollecttime(date);
					vo.setCategory("Disk");
					vo.setEntity("UtilizationInc");// 利用增长率百分比
					vo.setSubentity(description);
					vo.setRestype("dynamic");
					vo.setUnit("%");
					vo.setThevalue(diskInc);
					diskVector.addElement(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (diskVector != null && diskVector.size() > 0) {
				ipAllData.put("disk", diskVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (diskVector != null && diskVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("disk", diskVector);
			}
		}
		returnHash.put("disk", diskVector);
		// 进行磁盘告警检测
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows");
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
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows");
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

		HostdiskResultosql tosql = new HostdiskResultosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());

		String runmodel = PollingEngine.getCollectwebflag();
		if (!"0".equals(runmodel)) {
			HostDatatempDiskRttosql temptosql = new HostDatatempDiskRttosql();
			temptosql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}

	public static void main(String[] args) {
		BigDecimal bd = new BigDecimal("2426388223");
		BigDecimal u = new BigDecimal("8192");
		BigDecimal c = new BigDecimal("1024");
		c = c.pow(4);

		Integer i = new Integer("-1868579073");
		long unsignedValue = i & Integer.MAX_VALUE;
		System.out.println(unsignedValue);
		unsignedValue |= 0x80000000L;

		System.out.println(bd.multiply(u).divide(c, 2, BigDecimal.ROUND_HALF_UP));
		System.out.println(unsignedValue);

	}
}
