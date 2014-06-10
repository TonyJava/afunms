package com.afunms.polling.snmp.weblogic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;

/**
 * weblogic log ²É¼¯
 * 
 * @author yangjun 2013/3/18
 * 
 */
public class LogSnmp extends SnmpMonitor {

	public LogSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		WeblogicConfig weblogicconf = null;
		String id = nodeGatherIndicators.getNodeid();
		try {
			WeblogicConfigDao dao = new WeblogicConfigDao();
			try {
				weblogicconf = (WeblogicConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			List logValue = new ArrayList();
			WeblogicSnmp weblogicsnmp = null;
			try {
				weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(), weblogicconf.getCommunity(), weblogicconf.getPortnum());
				logValue = weblogicsnmp.collectLogData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (logValue != null) {
				returndata.put("logValue", logValue);
				if (!(ShareData.getWeblogicdata().containsKey(weblogicconf.getIpAddress()))) {
					ShareData.getWeblogicdata().put(weblogicconf.getIpAddress(), returndata);
				} else {
					Hashtable hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
					hash.put("logValue", returndata.get("logValue"));
				}

			}
			weblogicsnmp = null;
			logValue = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returndata;
	}
}
