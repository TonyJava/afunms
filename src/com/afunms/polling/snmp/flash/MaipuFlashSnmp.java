package com.afunms.polling.snmp.flash;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.FlashCollectEntity;
import com.gatherResulttosql.NetDatetempFlashRtosql;
import com.gatherResulttosql.NetflashResultTosql;

@SuppressWarnings("unchecked")
public class MaipuFlashSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector flashVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}

		try {
			Calendar date = Calendar.getInstance();

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
				if (node.getSysOid().startsWith("1.3.6.1.4.1.5651.")) {
					String[][] valueArray = null;
					String[] oids = new String[] { "1.3.6.1.4.1.5651.3.600.2.1.1.1",// flash
							// partition
							"1.3.6.1.4.1.5651.3.600.2.1.1.12",// Size of flash
							// partition
							"1.3.6.1.4.1.5651.3.600.2.1.1.13"// Free space of
					// the flash
					// device
					// partition
					};
					valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node
							.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					int flag = 0;
					if (valueArray != null) {
						for (int i = 0; i < valueArray.length; i++) {
							String allflashvalue = valueArray[i][1];
							String freevalue = valueArray[i][2];
							String index = valueArray[i][3];
							float value = 0.0f;
							String usedperc = "0";
							try {
								if (Long.parseLong(allflashvalue) > 0) {
									value = (Long.parseLong(allflashvalue) - Long.parseLong(freevalue)) * 100 / (Long.parseLong(allflashvalue));
								}
							} catch (Exception e) {

							}
							if (value > 0) {
								int intvalue = Math.round(value);
								flag = flag + 1;
								List alist = new ArrayList();
								alist.add("");
								alist.add(usedperc);
								FlashCollectEntity flashcollectdata = new FlashCollectEntity();
								flashcollectdata.setIpaddress(node.getIpAddress());
								flashcollectdata.setCollecttime(date);
								flashcollectdata.setCategory("Flash");
								flashcollectdata.setEntity("Utilization");
								flashcollectdata.setSubentity(index);
								flashcollectdata.setRestype("dynamic");
								flashcollectdata.setUnit("");
								flashcollectdata.setThevalue(intvalue + "");
								flashVector.addElement(flashcollectdata);
							}
						}
					}
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
			if (flashVector != null && flashVector.size() > 0) {
				ipAllData.put("flash", flashVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (flashVector != null && flashVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("flash", flashVector);
			}

		}
		returnHash.put("flash", flashVector);
		// 把采集结果生成sql
		NetflashResultTosql tosql = new NetflashResultTosql();
		tosql.CreateResultTosql(returnHash, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetDatetempFlashRtosql totempsql = new NetDatetempFlashRtosql();
			totempsql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
