package com.afunms.polling.snmp.was;

import java.util.Hashtable;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.model.WasConfig;
import com.afunms.application.wasmonitor.UrlConncetWas;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;

@SuppressWarnings("unchecked")
public class WasSysInfoMonitor extends SnmpMonitor {

	public WasSysInfoMonitor() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		WasConfig wasconf = null;
		String id = nodeGatherIndicators.getNodeid();
		try {
			WasConfigDao dao = new WasConfigDao();
			try {
				wasconf = (WasConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			UrlConncetWas conWas = new UrlConncetWas();
			// 采集数据
			try {
				returndata = conWas.ConncetWas(wasconf.getIpaddress(), String.valueOf(wasconf.getPortnum()), "", "", wasconf.getVersion());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 更新内存
			ShareData.getWasdata().put(wasconf.getIpaddress(), returndata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
