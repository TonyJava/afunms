package com.afunms.polling.snmp.flash;

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
import com.afunms.polling.om.FlashCollectEntity;
import com.gatherResulttosql.NetDatetempFlashRtosql;
import com.gatherResulttosql.NetflashResultTosql;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class H3CFlashSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());
	DecimalFormat df = new DecimalFormat("#.##");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector flashVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.info("HH3C Flash " + node.getIpAddress());
		try {
			if (node.getSysOid().startsWith("1.3.6.1.4.1.2011.") || node.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
				String[][] valueArray = null;
				String[] oids = new String[] { "1.3.6.1.4.1.2011.6.1.3.1",// hwFlhTotalSize
						"1.3.6.1.4.1.2011.6.1.3.2"// hwFlhTotalFree
				};

				String[] newOids = new String[] { "1.3.6.1.4.1.25506.2.5.1.1.4.1.1.4",// hh3cFlhPartSpace
						"1.3.6.1.4.1.25506.2.5.1.1.4.1.1.5",// hh3cFlhPartSpaceFree
						".1.3.6.1.4.1.25506.2.5.1.1.4.1.1.10"// hh3cFlhPartName
				};
				valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				if (null == valueArray || valueArray.length == 0) {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), newOids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
				}
				if (valueArray != null && valueArray.length > 0) {
					FlashCollectEntity vo = null;
					double usePercent = 0.0;
					String size = (String) null;
					String free = (String) null;
					String index = (String) null;
					for (int i = 0; i < valueArray.length; i++) {
						size = parseString(valueArray[i][0]);
						free = parseString(valueArray[i][1]);
						index = parseString(valueArray[i][2]);
						if (size.equals("NaV") || free.equals("NaV")) {
							continue;
						}
						usePercent = (Long.parseLong(size) - Long.parseLong(free)) * 100.0 / (Long.parseLong(size));
						vo = new FlashCollectEntity();
						vo.setIpaddress(node.getIpAddress());
						vo.setCollecttime(date);
						vo.setCategory("Flash");
						vo.setEntity("Utilization");
						vo.setSubentity(index);
						vo.setRestype("dynamic");
						vo.setUnit("%");
						vo.setThevalue(df.format(usePercent));
						flashVector.addElement(vo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
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
