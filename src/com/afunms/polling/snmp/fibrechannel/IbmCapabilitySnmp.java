package com.afunms.polling.snmp.fibrechannel;

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
import com.afunms.polling.om.Channelcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;

@SuppressWarnings("unchecked")
public class IbmCapabilitySnmp extends SnmpMonitor {

	private static Hashtable ifEntity_ifStatus = null;
	static {
		ifEntity_ifStatus = new Hashtable();
		ifEntity_ifStatus.put("1", "online");
		ifEntity_ifStatus.put("2", "offline");
		ifEntity_ifStatus.put("3", "testing");
		ifEntity_ifStatus.put("4", "linkFailure");
	};

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Hashtable channelHash = new Hashtable();
		Vector inframesVector = new Vector();
		Vector outframesVector = new Vector();
		Vector inOctetsVector = new Vector();
		Vector discardsVector = new Vector();
		Vector outOctetsVector = new Vector();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));

		try {
			Channelcollectdata channeldata = null;
			Calendar date = Calendar.getInstance();

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Hashtable hash = ShareData.getOctetsdata(host.getIpAddress());
				if (hash == null) {
					hash = new Hashtable();
				}
				String[] oids = new String[] { "1.3.6.1.2.1.75.1.1.5.1.2", "1.3.6.1.2.1.75.1.2.2.1.1", "1.3.6.1.2.1.75.1.2.2.1.2", "1.3.6.1.2.1.75.1.4.3.1.1",
						"1.3.6.1.2.1.75.1.4.3.1.2", "1.3.6.1.2.1.75.1.4.3.1.3", "1.3.6.1.2.1.75.1.4.3.1.4", "1.3.6.1.2.1.75.1.4.3.1.5" };
				final String[] desc = SnmpMibConstants.NetWorkMibCapabilityDesc;
				final String[] chname = SnmpMibConstants.NetWorkMibCapabilityChname;
				final String[] unit = SnmpMibConstants.NetWorkMibCapabilityUnit0;
				final int[] scale = SnmpMibConstants.NetWorkMibCapabilityScale0;
				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), host.getSecuritylevel(), host.getSecurityName(),
							host.getV3_ap(), host.getAuthpassphrase(), host.getV3_privacy(), host.getPrivacyPassphrase(), 3, 1000 * 30);
				} catch (Exception e) {
				}

				Vector tempV = new Vector();
				Hashtable tempHash = new Hashtable();
				if (valueArray != null && valueArray.length > 0) {
					for (int i = 0; i < valueArray.length; i++) {
						if (valueArray[i][0] == null) {
							continue;
						}
						String sportName = valueArray[i][0].toString();
						tempV.add(sportName);
						tempHash.put(i, sportName);
						Vector channelVector = new Vector();
						for (int j = 0; j < 8; j++) {
							String sValue = valueArray[i][j];
							channeldata = new Channelcollectdata();
							channeldata.setIpaddress(host.getIpAddress());
							channeldata.setCollecttime(date);
							channeldata.setCategory("channel");
							channeldata.setEntity(desc[j]);
							channeldata.setSubentity(sportName);
							// 端口状态不保存，只作为静态数据放到临时表里
							if (j == 0 || j == 1 || j == 2) {
								channeldata.setRestype("static");
							} else {
								channeldata.setRestype("dynamic");
							}
							channeldata.setUnit(unit[j]);
							if ((j == 0 || j == 1 || j == 2) && sValue != null) {// 预期状态和当前状态

								if (ifEntity_ifStatus.get(sValue) != null) {
									channeldata.setThevalue(ifEntity_ifStatus.get(sValue).toString());

								} else {
									channeldata.setThevalue(sValue);
								}
							} else {
								if (sValue != null) {
									channeldata.setThevalue(Long.toString(Long.parseLong(sValue) / scale[j]));
								} else {
									channeldata.setThevalue("0");
								}
							}
							channeldata.setChname(chname[j]);
							channelVector.addElement(channeldata);
						} // end for j
						channelHash.put(i, channelVector);
					} // end for valueArray
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// end
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}

			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (channelHash != null && channelHash.size() > 0) {
				ipAllData.put("channel", channelHash);
			}
			if (inframesVector != null && inframesVector.size() > 0) {
				ipAllData.put("allinframes", discardsVector);
			}
			if (outframesVector != null && outframesVector.size() > 0) {
				ipAllData.put("alloutframes", outframesVector);
			}
			if (inOctetsVector != null && inOctetsVector.size() > 0) {
				ipAllData.put("inOctets", inOctetsVector);
			}
			if (outOctetsVector != null && outOctetsVector.size() > 0) {
				ipAllData.put("outOctets", outOctetsVector);
			}
			if (discardsVector != null && discardsVector.size() > 0) {
				ipAllData.put("discards", discardsVector);
			}
			ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		} else {

			if (channelHash != null && channelHash.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("channel", channelHash);
			}
			if (inframesVector != null && inframesVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("allinframes", discardsVector);
			}
			if (outframesVector != null && outframesVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("alloutframes", outframesVector);
			}
			if (inOctetsVector != null && inOctetsVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("inOctets", inOctetsVector);
			}
			if (outOctetsVector != null && outOctetsVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("outOctets", outOctetsVector);
			}
			if (discardsVector != null && discardsVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(host.getIpAddress())).put("discards", discardsVector);
			}

		}

		returnHash.put("channel", channelHash);
		returnHash.put("allinframes", discardsVector);
		returnHash.put("alloutframes", outframesVector);
		returnHash.put("inOctets", inOctetsVector);
		returnHash.put("outOctets", outOctetsVector);
		returnHash.put("discards", discardsVector);
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
