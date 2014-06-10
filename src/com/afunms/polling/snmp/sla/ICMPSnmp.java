package com.afunms.polling.snmp.sla;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class ICMPSnmp extends SnmpMonitor {

	private static Hashtable ifEntity_ifStatus = null;
	static {
		ifEntity_ifStatus = new Hashtable();
		ifEntity_ifStatus.put("1", "up");
		ifEntity_ifStatus.put("2", "down");
		ifEntity_ifStatus.put("3", "testing");
		ifEntity_ifStatus.put("5", "unknow");
		ifEntity_ifStatus.put("7", "unknow");
	};

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(SlaNodeConfig vo, Huaweitelnetconf telnetconfig) {

		Hashtable returnHash = new Hashtable();
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(telnetconfig.getIpaddress());
		if (host == null) {
			return returnHash;
		}
		try {
			Calendar date = Calendar.getInstance();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {

			}
			try {
				Hashtable hash = ShareData.getOctetsdata(host.getIpAddress());
				// 取得轮询间隔时间
				if (hash == null) {
					hash = new Hashtable();
				}

				String[] icmp_oids = new String[] { "1.3.6.1.4.1.9.9.42.1.2.10.1.2", // 最后完成状态
						"1.3.6.1.4.1.9.9.42.1.2.10.1.1"// RTT值
				};

				String[][] valueArrayICMP = null;
				try {
					valueArrayICMP = SnmpUtils.getTemperatureTableData(host.getIpAddress(), host.getCommunity(), icmp_oids, host.getSnmpversion(), host.getSecuritylevel(), host
							.getSecurityName(), host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// 开始测试ICMP
				if (valueArrayICMP != null) {
					Hashtable dataHash = new Hashtable();
					for (int i = 0; i < valueArrayICMP.length; i++) {
						String RTT = valueArrayICMP[i][1];
						String index = valueArrayICMP[i][2];
						String RTT_Status = valueArrayICMP[i][0];
						PingCollectEntity hostdata = null;
						if (!index.equalsIgnoreCase(vo.getEntrynumber() + "")) {
							continue;
						}
						// RTT响应时间
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(telnetconfig.getIpaddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("ResponseTime");
						hostdata.setSubentity("ResponseTime");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("毫秒");
						hostdata.setThevalue(RTT);
						dataHash.put(1, hostdata);

						// 状态
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(telnetconfig.getIpaddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						if ("1".equalsIgnoreCase(RTT_Status)) {
							hostdata.setThevalue("100");
						} else {
							hostdata.setThevalue("0");
						}
						dataHash.put(0, hostdata);
						returnHash.put(vo.getId() + "", dataHash);
						ShareData.getSlaHash().put(vo.getId() + "", dataHash);
						break;
					}
				}
				// 结束测试ICMP
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnHash;
	}

	public int getInterval(float d, String t) {
		int interval = 0;
		if (t.equals("d")) {
			interval = (int) d * 24 * 60 * 60; // 天数
		} else if (t.equals("h")) {
			interval = (int) d * 60 * 60; // 小时
		} else if (t.equals("m")) {
			interval = (int) d * 60; // 分钟
		} else if (t.equals("s")) {
			interval = (int) d; // 秒
		}
		return interval;
	}

}
