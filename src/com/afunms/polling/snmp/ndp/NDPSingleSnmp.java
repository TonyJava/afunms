package com.afunms.polling.snmp.ndp;

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
import com.afunms.polling.om.NDP;
import com.gatherResulttosql.NetHostNDPRttosql;

@SuppressWarnings("unchecked")
public class NDPSingleSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector ndpVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;
		try {
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
				String[] oids = new String[] { "1.3.6.1.4.1.2011.6.7.5.6.1.1", // 1.hwNDPPortNbDeviceId
						"1.3.6.1.4.1.2011.6.7.5.6.1.2", // 2.hwNDPPortNbPortName
				};

				String[] oids1 = new String[] { "1.3.6.1.4.1.25506.8.7.5.6.1.1", // 1.hwNDPPortNbDeviceId
						"1.3.6.1.4.1.25506.8.7.5.6.1.2", // 2.hwNDPPortNbPortName
				};
				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.walkTable(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node
							.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray.length == 1) {
					if (valueArray[0][0] == null || valueArray[0][1] == null) {
						try {
							valueArray = SnmpUtils.walkTable(node.getIpAddress(), node.getCommunity(), oids1, node.getSnmpversion(), node.getSecuritylevel(), node
									.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (valueArray == null) {
					try {
						valueArray = SnmpUtils.walkTable(node.getIpAddress(), node.getCommunity(), oids1, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
								node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (valueArray == null)
					return null;
				NDP ndp = null;
				for (int i = 0; i < valueArray.length; i++) {
					ndp = new NDP();
					ndp.setDeviceId(valueArray[i][0]);
					ndp.setPortName(valueArray[i][1]);
					ndp.setNodeid(Long.parseLong(node.getId() + ""));
					ndp.setCollecttime(date);
					ndpVector.addElement(ndp);
				}
				valueArray = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("ndp", ndpVector);
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (ndpVector != null && ndpVector.size() > 0)
				ipAllData.put("ndp", ndpVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (ndpVector != null && ndpVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("ndp", ndpVector);

		}

		// �Ѳɼ��������sql
		NetHostNDPRttosql ndptosql = new NetHostNDPRttosql();
		ndptosql.CreateResultTosql(ndpVector, node);

		ndpVector = null;
		return returnHash;
	}

}
