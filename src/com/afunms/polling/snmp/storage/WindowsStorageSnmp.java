package com.afunms.polling.snmp.storage;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.StorageCollectEntity;
import com.gatherResulttosql.HostDatatempstorageRttosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsStorageSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	private static Hashtable<String, String> storage_Type = new Hashtable<String, String>();
	static {
		storage_Type.put("1.3.6.1.2.1.25.2.1.1", "其他");
		storage_Type.put("1.3.6.1.2.1.25.2.1.2", "物理内存");
		storage_Type.put("1.3.6.1.2.1.25.2.1.3", "虚拟内存");
		storage_Type.put("1.3.6.1.2.1.25.2.1.4", "硬盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.5", "移动硬盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.6", "软盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.7", "光盘");
		storage_Type.put("1.3.6.1.2.1.25.2.1.8", "内存盘");
	};

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector storageVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}

		logger.debug("Windows Storage " + node.getIpAddress());
		try {
			StorageCollectEntity vo = null;
			String[] oids = new String[] { "1.3.6.1.2.1.25.2.3.1.1", // hrStorageIndex
					"1.3.6.1.2.1.25.2.3.1.2", // hrStorageType
					"1.3.6.1.2.1.25.2.3.1.3", // hrStorageDescr
					"1.3.6.1.2.1.25.2.3.1.4", // hrStorageAllocationUnits
					"1.3.6.1.2.1.25.2.3.1.5" }; // hrStorageSize

			String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

			if (valueArray != null && valueArray.length > 0) {
				BigInteger size = (BigInteger) null;
				String unit = "M";
				int lenght = 0;

				DecimalFormat df = new DecimalFormat();
				df.applyPattern("0.0");

				long usAllBlock = 0L;

				BigInteger dChild = new BigInteger("1024");
				for (int i = 0; i < valueArray.length; i++) {
					if (parseString(valueArray[i][4]).equals("NaV")) {
						continue;
					}
					usAllBlock = new Integer(valueArray[i][4]) & Integer.MAX_VALUE;
					if (valueArray[i][4].indexOf("-") >= 0) {
						usAllBlock |= 0x80000000L;
					}

					size = new BigInteger(Long.toString(usAllBlock)).multiply(new BigInteger(valueArray[i][3]));

					vo = new StorageCollectEntity();
					vo.setStorageindex(valueArray[i][0]);
					vo.setIpaddress(node.getIpAddress());
					vo.setName(valueArray[i][2]);
					vo.setType((String) storage_Type.get(parseString(valueArray[i][1])));

					lenght = size.abs().toString().length();
					if (lenght > 13) {
						unit = "T";
						size = size.divide(dChild.pow(4));
					} else if (lenght > 9) {
						unit = "G";
						size = size.divide(dChild.pow(3));
					} else if (lenght > 7) {
						unit = "M";
						size = size.divide(dChild.pow(2));
					} else if (lenght > 5) {
						unit = "K";
						size = size.divide(dChild.pow(1));
					}
					vo.setCap(df.format(size.floatValue()) + unit);
					storageVector.addElement(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (storageVector != null && storageVector.size() > 0) {
				ipAllData.put("storage", storageVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (storageVector != null && storageVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("storage", storageVector);
			}
		}

		returnHash.put("storage", storageVector);
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			HostDatatempstorageRttosql totempsql = new HostDatatempstorageRttosql();
			totempsql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
