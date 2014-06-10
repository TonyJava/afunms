package com.afunms.polling.snmp.system;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class WindowsSystemSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(AlarmIndicatorsNode alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector systemVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.debug("Windows System " + node.getIpAddress());
		try {
			SystemCollectEntity vo = null;
			try {
				final String[] desc = SnmpMibConstants.NetWorkMibSystemDesc;
				final String[] chname = SnmpMibConstants.NetWorkMibSystemChname;
				String[] oids = new String[] { "1.3.6.1.2.1.1.1", //
						"1.3.6.1.2.1.1.3", //
						"1.3.6.1.2.1.1.4", //
						"1.3.6.1.2.1.1.5", //
						"1.3.6.1.2.1.1.6", //
						"1.3.6.1.2.1.1.7"//
				};
				String[][] valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

				if (valueArray != null && valueArray.length > 0) {
					for (int i = 0; i < valueArray.length; i++) {
						for (int j = 0; j < 6; j++) {
							vo = new SystemCollectEntity();
							vo.setIpaddress(node.getIpAddress());
							vo.setCollecttime(date);
							vo.setCategory("System");
							vo.setEntity(desc[i]);
							vo.setSubentity(desc[j]);
							vo.setChname(chname[j]);
							vo.setRestype("static");
							vo.setUnit("");
							String value = valueArray[i][j];
							if (j == 0) {
								vo.setThevalue(value);
							} else {
								vo.setThevalue(value);
							}
							systemVector.addElement(vo);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { "1.3.6.1.2.1.2.2.1.6" };
				String[][] valueArray = null;
				try {
					valueArray = snmp.getTableData(node.getIpAddress(), node.getCommunity(), oids);
				} catch (Exception e) {
					e.printStackTrace();
					valueArray = null;
				}
				vo = new SystemCollectEntity();
				vo.setIpaddress(node.getIpAddress());
				vo.setCollecttime(date);
				vo.setCategory("System");
				vo.setEntity("MacAddr");
				vo.setSubentity("MacAddr");
				vo.setRestype("static");
				vo.setUnit(" ");
				if (valueArray != null) {
					for (int i = 0; i < valueArray.length; i++) {
						String value = valueArray[i][0];
						if (value == null || value.length() == 0) {
							continue;
						}
						vo.setThevalue(value);
						break;
					}
				}
				systemVector.addElement(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (systemVector != null && systemVector.size() > 0) {
				ipAllData.put("system", systemVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (systemVector != null && systemVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("system", systemVector);
			}
		}
		returnHash.put("system", systemVector);
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostDatatempSystemRttosql tosql = new NetHostDatatempSystemRttosql();
			tosql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
