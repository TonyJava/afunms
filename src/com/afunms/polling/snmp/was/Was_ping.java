package com.afunms.polling.snmp.was;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.model.WasConfig;
import com.afunms.application.wasmonitor.UrlConncetWas;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class Was_ping {
	private Hashtable wasdata = ShareData.getWasdata();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Was_ping() {
	}

	public void collect_Data(NodeGatherIndicators tomcatIndicators) {
		WasConfig wasconf = null;
		String id = tomcatIndicators.getNodeid();
		try {
			String ipaddress = "";
			WasConfigDao dao = new WasConfigDao();
			try {
				wasconf = (WasConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			UrlConncetWas conWas = new UrlConncetWas();
			Hashtable hst = new Hashtable();
			com.afunms.polling.node.Was _tnode = (com.afunms.polling.node.Was) PollingEngine.getInstance().getWasByID(wasconf.getId());

			Calendar _date = Calendar.getInstance();
			Date _cc = _date.getTime();
			String _tempsenddate = sdf.format(_cc);
			_tnode.setLastTime(_tempsenddate);
			_tnode.setAlarm(false);
			_tnode.getAlarmMessage().clear();
			_tnode.setStatus(0);

			// 对可用性进行检测
			boolean collectWasIsOK = false;
			try {
				collectWasIsOK = conWas.connectWasIsOK(wasconf.getIpaddress(), wasconf.getPortnum());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (collectWasIsOK) {
				// 运行状态
				PingCollectEntity hostdata = null;
				hostdata = new PingCollectEntity();
				hostdata.setIpaddress(wasconf.getIpaddress());
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("WasPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue("100");
				WasConfigDao wasconfigdao = new WasConfigDao();
				try {
					wasconfigdao.createHostData(wasconf, hostdata);
					if (wasdata.containsKey("was" + ":" + wasconf.getIpaddress()))
						wasdata.remove("was" + ":" + wasconf.getIpaddress());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					wasconfigdao.close();
				}
				// 进行数据采集
				try {
					hst = conWas.ConncetWas(wasconf.getIpaddress(), String.valueOf(wasconf.getPortnum()), "", "", wasconf.getVersion(), null);
				} catch (Exception e) {

				}
			} else {
				// 依据配置判断是否需要告警

				try {
					Vector ipPingData = (Vector) ShareData.getPingdata().get(wasconf.getIpaddress());
					ipaddress = wasconf.getIpaddress();
					if (ipPingData != null) {
						PingCollectEntity pingdata = (PingCollectEntity) ipPingData.get(0);
						String pingvalue = pingdata.getThevalue();
						if (pingvalue == null || pingvalue.trim().length() == 0)
							pingvalue = "0";
						double pvalue = new Double(pingvalue);
						if (pvalue == 0) {
							_tnode.setAlarm(true);
							_tnode.setStatus(1);
							List alarmList = _tnode.getAlarmMessage();
							if (alarmList == null)
								alarmList = new ArrayList();
							_tnode.getAlarmMessage().add("WAS服务停止");
							PingCollectEntity hostdata = null;
							hostdata = new PingCollectEntity();
							hostdata.setIpaddress(ipaddress);
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("WasPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");
							WasConfigDao wasconfigdao = new WasConfigDao();
							try {
								wasconfigdao.createHostData(wasconf, hostdata);
								if (wasdata.containsKey("was" + ":" + wasconf.getIpaddress()))
									wasdata.remove("was" + ":" + wasconf.getIpaddress());
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								wasconfigdao.close();
							}
						} else {
							_tnode.setAlarm(true);
							_tnode.setStatus(3);
							List alarmList = _tnode.getAlarmMessage();
							if (alarmList == null)
								alarmList = new ArrayList();
							_tnode.getAlarmMessage().add("WAS服务停止");
							PingCollectEntity hostdata = null;
							hostdata = new PingCollectEntity();
							hostdata.setIpaddress(ipaddress);
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("WasPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");
							WasConfigDao wasconfigdao = new WasConfigDao();
							try {
								wasconfigdao.createHostData(wasconf, hostdata);
								if (wasdata.containsKey("was" + ":" + wasconf.getIpaddress()))
									wasdata.remove("was" + ":" + wasconf.getIpaddress());
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								wasconfigdao.close();
							}
						}

					} else {
						_tnode.setAlarm(true);
						_tnode.setStatus(3);
						List alarmList = _tnode.getAlarmMessage();
						if (alarmList == null)
							alarmList = new ArrayList();
						_tnode.getAlarmMessage().add("WAS服务停止");
						PingCollectEntity hostdata = null;
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(ipaddress);
						Calendar date = Calendar.getInstance();
						hostdata.setCollecttime(date);
						hostdata.setCategory("WasPing");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						hostdata.setThevalue("0");
						WasConfigDao wasconfigdao = new WasConfigDao();
						try {
							wasconfigdao.createHostData(wasconf, hostdata);
							if (wasdata.containsKey("was" + ":" + wasconf.getIpaddress()))
								wasdata.remove("was" + ":" + wasconf.getIpaddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							wasconfigdao.close();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (hst != null) {
				ShareData.getWasdata().put(wasconf.getIpaddress(), hst);
			}
			hst = null;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
