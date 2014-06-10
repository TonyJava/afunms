package com.afunms.polling.snmp.ipaccounting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.ipaccounting.dao.IpAccountingBaseDao;
import com.afunms.ipaccounting.model.IpAccounting;
import com.afunms.ipaccounting.model.IpAccountingBase;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetipaccountResultTosql;

@SuppressWarnings("unchecked")
public class CiscoIPAccountSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("static-access")
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector ipaccountVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return returnHash;
		// 判断是否在采集时间段内
		try {
			Calendar date = Calendar.getInstance();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			try {

				SnmpUtils snmputils = new SnmpUtils();
				try {
					String value = snmputils.get(node.getIpAddress(), node.getCommunity(), ".1.3.6.1.4.1.9.2.4.11.0", 0, 3, 3000);
					snmputils.set(node.getIpAddress(), node.getWritecommunity(), ".1.3.6.1.4.1.9.2.4.11.0", value, 'i', 0, 3, 3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String[] oids = new String[] { "1.3.6.1.4.1.9.2.4.9.1.1",// src
						"1.3.6.1.4.1.9.2.4.9.1.2",// dst
						"1.3.6.1.4.1.9.2.4.9.1.3",// pkts
						"1.3.6.1.4.1.9.2.4.9.1.4"// byts
				};
				String[][] valueArray = null;

				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(),
							node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
					e.printStackTrace();
				}
				// 休眠10秒钟
				String str = "";
				for (int i = 0; i < 1000000; i++) {
					str = str.trim();
				}
				if (valueArray != null) {
					IpAccounting ipaccounting = null;
					int accountbaseid = 0;
					Calendar coldate = Calendar.getInstance();
					IpAccountingBaseDao basedao = new IpAccountingBaseDao();
					try {
						for (int i = 0; i < valueArray.length; i++) {
							try {
								ipaccounting = new IpAccounting();
								ipaccounting.setSrcip(valueArray[i][0]);
								ipaccounting.setDestip(valueArray[i][1]);
								if (valueArray[i][2] == null || valueArray[i][3] == null)
									continue;
								ipaccounting.setPkts(Integer.parseInt(valueArray[i][2]));
								ipaccounting.setByts(Integer.parseInt(valueArray[i][3]));
								if (ShareData.getAllipaccountipbases() != null) {
									if (ShareData.getAllipaccountipbases().containsKey(ipaccounting.getSrcip() + ":" + ipaccounting.getDestip() + ":" + node.getId())) {
										accountbaseid = Integer.parseInt((String) ShareData.getAllipaccountipbases().get(
												ipaccounting.getSrcip() + ":" + ipaccounting.getDestip() + ":" + node.getId()));
									} else {
										// 基础表存入数据库
										IpAccountingBase ipbase = new IpAccountingBase();
										ipbase.setDestip(ipaccounting.getDestip());
										ipbase.setSrcip(ipaccounting.getSrcip());
										ipbase.setNodeid(node.getId());
										ipbase.setProtocol("");

										try {
											basedao.save(ipbase);
										} catch (Exception e) {
											e.printStackTrace();
										}
										accountbaseid = ipbase.getId();
										ShareData.getAllipaccountipbases().put(ipaccounting.getSrcip() + ":" + ipaccounting.getDestip() + ":" + node.getId(), accountbaseid + "");
									}
								} else {
									// 基础表存入数据库
									IpAccountingBase ipbase = new IpAccountingBase();
									ipbase.setDestip(ipaccounting.getDestip());
									ipbase.setSrcip(ipaccounting.getSrcip());
									ipbase.setNodeid(node.getId());
									ipbase.setProtocol("");
									try {
										basedao.save(ipbase);
									} catch (Exception e) {
										e.printStackTrace();
									}
									accountbaseid = ipbase.getId();
									Hashtable allipaccountipbase = new Hashtable();
									allipaccountipbase.put(ipaccounting.getSrcip() + ":" + ipaccounting.getDestip() + ":" + node.getId(), accountbaseid + "");
									ShareData.setAllipaccountipbases(allipaccountipbase);

								}
								ipaccounting.setAccountingBaseID(accountbaseid);
								ipaccounting.setCollecttime(coldate);
								ipaccountVector.add(ipaccounting);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						basedao.close();
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
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (ipaccountVector != null && ipaccountVector.size() > 0)
				ipAllData.put("ipaccount", ipaccountVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (ipaccountVector != null && ipaccountVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("ipaccount", ipaccountVector);

		}
		returnHash.put("ipaccount", ipaccountVector);
		// 把结果转换成sql
		NetipaccountResultTosql tosql = new NetipaccountResultTosql();
		tosql.CreateResultTosql(ipaccountVector, node.getIpAddress());
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
			totempsql.CreateResultTosql(returnHash, node);
		}

		return returnHash;
	}
}
