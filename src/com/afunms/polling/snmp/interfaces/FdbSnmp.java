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
import com.afunms.polling.om.IpMac;
import com.gatherResulttosql.NetDatatempFdbRtosql;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FdbSnmp extends SnmpMonitor {
	private Logger logger = Logger.getLogger(this.getClass());

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector fdbVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		} else {
			node.setLastTime(sdf.format(date.getTime()));
		}
		logger.info("FdbSnmp " + node.getIpAddress());
		try {
			String[][] valueArray = SnmpUtils.getFdb(node.getIpAddress(), node.getCommunity(), node.getSnmpversion(), 3, 1000 * 30);
			if (valueArray != null && valueArray.length > 0) {
				IpMac ipmac = null;
				
				String ipNetToMediaPhysAddress = (String) null;
				String dot1dTpFdbPort = (String) null;
				String ifDescr = (String) null;
				String dot1dTpFdbAddress = (String) null;
				for (int i = 0; i < valueArray.length; i++) {
					ipNetToMediaPhysAddress = parseString(valueArray[i][0]);
					dot1dTpFdbPort = parseString(valueArray[i][1]);
					ifDescr = parseString(valueArray[i][4]);
					dot1dTpFdbAddress = parseString(valueArray[i][3]);
					if (ipNetToMediaPhysAddress.equals("NaV") || dot1dTpFdbAddress.equals("NaV")) {
						continue;
					}
					ipmac = new IpMac();
					ipmac.setIfindex(dot1dTpFdbPort);// 物理端口号
					ipmac.setMac(ipNetToMediaPhysAddress);// 物理地址
					ipmac.setIfband("0");
					ipmac.setIfsms("0");
					ipmac.setBak(ifDescr);// 物理端口描述
					ipmac.setCollecttime(new GregorianCalendar());
					ipmac.setIpaddress(dot1dTpFdbAddress);// 转发IP
					ipmac.setRelateipaddr(node.getIpAddress());// 设置交换机IP
					fdbVector.add(ipmac);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (fdbVector != null && fdbVector.size() > 0) {
				ipAllData.put("fdb", fdbVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (fdbVector != null && fdbVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("fdb", fdbVector);
			}
		}
		returnHash.put("fdb", fdbVector);
		NetDatatempFdbRtosql totempsql = new NetDatatempFdbRtosql();
		totempsql.CreateResultTosql(returnHash, node);
		return returnHash;
	}

}
