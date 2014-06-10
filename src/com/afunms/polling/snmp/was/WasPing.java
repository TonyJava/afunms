package com.afunms.polling.snmp.was;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.model.WasConfig;
import com.afunms.application.wasmonitor.UrlConncetWas;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class WasPing extends SnmpMonitor {

	public WasPing() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
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
			Hashtable hst = (Hashtable) ShareData.getWasdata().get(wasconf.getIpaddress());
			if (hst == null) {
				hst = new Hashtable();
			}
			UrlConncetWas conWas = new UrlConncetWas();
			boolean collectWasIsOK = false;
			// 采集数据
			try {
				collectWasIsOK = conWas.connectWasIsOK(wasconf.getIpaddress(), wasconf.getPortnum());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String pingValue = "0";
			if (collectWasIsOK) {
				pingValue = "100";
				hst.put("ping", "100");
			} else {
				hst.put("ping", "0");
			}

			// 保存数据库
			PingCollectEntity hostdata = new PingCollectEntity();
			hostdata.setIpaddress(wasconf.getIpaddress());
			Calendar date = Calendar.getInstance();
			hostdata.setCollecttime(date);
			hostdata.setCategory("WasPing");
			hostdata.setEntity("Utilization");
			hostdata.setSubentity("ConnectUtilization");
			hostdata.setRestype("dynamic");
			hostdata.setUnit("%");
			hostdata.setThevalue(pingValue);
			WasConfigDao wasconfigdao = new WasConfigDao();
			try {
				wasconfigdao.createHostData(wasconf, hostdata);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wasconfigdao.close();
			}
			ShareData.getWasdata().put(wasconf.getIpaddress(), hst);
			// 告警，只告警PING值
			if (pingValue != null) {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(wasconf);
				// 判断是否存在此告警指标
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
					if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
						if (pingValue != null) {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, pingValue);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
