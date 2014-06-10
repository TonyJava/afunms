package com.afunms.polling.snmp.interfaces;

import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpRouter;
import com.gatherResulttosql.NetDatatempRouterRtosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RouterSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector iprouterVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		
		logger.debug("RouteSnmp " + node.getIpAddress());
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.4.21.1.2", // 0.ifIndex
					"1.3.6.1.2.1.4.21.1.1", // 1.ipRouterDest
					"1.3.6.1.2.1.4.21.1.7", // 7.ipRouterNextHop
					"1.3.6.1.2.1.4.21.1.8", // 8.ipRouterType
					"1.3.6.1.2.1.4.21.1.9", // 9.ipRouterProto
					"1.3.6.1.2.1.4.21.1.11" }; // 11.ipRouterMask

			String[][] valueArray = SnmpUtils.getBulkFc(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);

			if (null != valueArray && valueArray.length > 0) {
				IpRouter iprouter = null;
				String ifIndex = (String) null;
				String ipRouterDest = (String) null;
				String ipRouterNextHop = (String) null;
				String ipRouterType = (String) null;
				String ipRouterProto = (String) null;
				String ipRouterMask = (String) null;
				for (int i = 0; i < valueArray.length; i++) {
					ifIndex = parseString(valueArray[i][0]);
					ipRouterDest = parseString(valueArray[i][1]);
					ipRouterNextHop = parseString(valueArray[i][2]);
					ipRouterType = parseString(valueArray[i][3]);
					ipRouterProto = parseString(valueArray[i][4]);
					ipRouterMask = parseString(valueArray[i][5]);

					if (ipRouterDest.equals("NaV") || ipRouterDest.equals("0.0.0.0") || ipRouterDest.equals("127.0.0.1")) {
						continue;
					}
					iprouter = new IpRouter();
					iprouter.setRelateipaddr(node.getIpAddress());
					iprouter.setIfindex(ifIndex);
					iprouter.setDest(ipRouterDest);
					iprouter.setNexthop(ipRouterNextHop);
					iprouter.setType(new Long(ipRouterType));
					iprouter.setProto(new Long(ipRouterProto));
					iprouter.setMask(ipRouterMask);
					iprouter.setCollecttime(new GregorianCalendar());
					iprouterVector.addElement(iprouter);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (iprouterVector != null && iprouterVector.size() > 0)
				ipAllData.put("iprouter", iprouterVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (iprouterVector != null && iprouterVector.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("iprouter", iprouterVector);
		}

		returnHash.put("iprouter", iprouterVector);
		ShareData.setIprouterdata(node.getIpAddress(), iprouterVector);
		iprouterVector = null;
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			NetDatatempRouterRtosql temptosql = new NetDatatempRouterRtosql();
			temptosql.CreateResultTosql(returnHash, node);
		}
		return returnHash;
	}
}
