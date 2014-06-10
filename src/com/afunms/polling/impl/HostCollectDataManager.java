/*
 * Created on 2005-4-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.util.DbConversionUtil;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.ErrptlogDao;
import com.afunms.config.model.Errptlog;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Buffercollectdata;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.FlashCollectEntity;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpMacBand;
import com.afunms.polling.om.IpMacBase;
import com.afunms.polling.om.IpMacChange;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.MacHistory;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.snmp.Hostlastcollectdata;
import com.afunms.polling.task.FtpUtil;
import com.afunms.system.dao.FtpTransConfigDao;
import com.afunms.system.model.FtpTransConfig;
import com.afunms.system.vo.FlexVo;
import com.afunms.temp.service.TempDataService;
import com.afunms.topology.dao.DiskForAS400Dao;
import com.afunms.topology.dao.IpMacChangeDao;
import com.afunms.topology.dao.JobForAS400Dao;
import com.afunms.topology.dao.SubsystemForAS400Dao;
import com.afunms.topology.dao.SystemPoolForAS400Dao;
import com.afunms.topology.dao.SystemValueForAS400Dao;
import com.afunms.topology.model.DiskForAS400;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemPoolForAS400;
import com.afunms.topology.model.SystemValueForAS400;
import com.afunms.topology.util.HostCollectDataHelper;
import com.afunms.topology.util.XmlDataOperator;

@SuppressWarnings("unchecked")
public class HostCollectDataManager implements I_HostCollectData {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public HostCollectDataManager() {
		super();
	}

	public boolean createHostData(Hostcollectdata hostdata) throws Exception {
		return true;
	}

	public boolean refreshCollectLastTable() throws Exception {
		return true;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Vector hostdatavec, NodeGatherIndicators alarmIndicatorsNode) {
		if (hostdatavec == null || hostdatavec.size() == 0) {
			return false;
		}
		DBManager dbmanager = new DBManager();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < hostdatavec.size(); i++) {
				PingCollectEntity pingdata = (PingCollectEntity) hostdatavec.elementAt(i);
				String ip = pingdata.getIpaddress();
				if (pingdata.getRestype().equals("dynamic")) {
					String allipstr = SysUtil.doip(ip);

					Calendar tempCal = (Calendar) pingdata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
					String tablename = "ping" + allipstr;
					String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "'," + time + ")";
					try {
						dbmanager.executeUpdate(sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Host node = (Host) PollingEngine.getInstance().getNodeByIP(pingdata.getIpaddress());
					if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
						// 连通率进行判断
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();

						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), alarmIndicatorsNode.getType(), "");
						for (int k = 0; k < list.size(); k++) {
							AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(k);
							if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
								if (_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")) {
									CheckEventUtil checkeventutil = new CheckEventUtil();
									checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
								}
							}
						}

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
			dbmanager = null;
		}

		return true;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createSLAData(Hashtable alldata) {
		if (alldata == null || alldata.size() == 0) {
			return false;
		}
		DBManager dbmanager = new DBManager();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Enumeration newProEnu = alldata.keys();
			String sql = "";
			while (newProEnu.hasMoreElements()) {
				sql = "";
				String configid = (String) newProEnu.nextElement();
				Hashtable slanodeHash = (Hashtable) alldata.get(configid);
				if (slanodeHash != null && slanodeHash.size() > 0) {
					Enumeration newSlaEnu = slanodeHash.keys();
					while (newSlaEnu.hasMoreElements()) {
						String slaconfigid = (String) newSlaEnu.nextElement();
						Hashtable sladataV = (Hashtable) slanodeHash.get(slaconfigid);
						if (sladataV != null && sladataV.size() > 0) {
							PingCollectEntity statusdata = (PingCollectEntity) sladataV.get(0);
							Calendar tempCal = (Calendar) statusdata.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "slastatus" + slaconfigid;
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + statusdata.getIpaddress() + "','" + statusdata.getRestype() + "','" + statusdata.getCategory() + "','" + statusdata.getEntity() + "','" + statusdata.getSubentity() + "','" + statusdata.getUnit() + "','" + statusdata.getChname() + "','" + statusdata.getBak() + "'," + statusdata.getCount() + ",'" + statusdata.getThevalue() + "'," + time + ")";
							dbmanager.addBatch(sql);

							PingCollectEntity rttdata = (PingCollectEntity) sladataV.get(1);
							tablename = "slartt" + slaconfigid;
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + rttdata.getIpaddress() + "','" + rttdata.getRestype() + "','" + rttdata.getCategory() + "','" + rttdata.getEntity() + "','" + rttdata.getSubentity() + "','" + rttdata.getUnit() + "','" + rttdata.getChname() + "','" + rttdata.getBak() + "'," + rttdata.getCount() + ",'" + rttdata.getThevalue() + "'," + time + ")";
							dbmanager.addBatch(sql);
						}

					}
				}

			}
			try {
				dbmanager.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
			dbmanager = null;
			// System.gc();
		}

		return true;
	}

	// 处理所有SLA得到的数据，放到历史表里
	public synchronized boolean createAllSLAData(Hashtable alldata) {
		// SysLogger.info("all data size======="+alldata.size());
		if (alldata == null || alldata.size() == 0) {
			return false;
		}
		DBManager dbmanager = new DBManager();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Vector v = new Vector();
			Enumeration newProEnu = alldata.keys();
			String sql = "";
			while (newProEnu.hasMoreElements()) {
				sql = "";
				String configid = (String) newProEnu.nextElement();
				Hashtable slanodeHash = (Hashtable) alldata.get(configid);
				if (slanodeHash != null && slanodeHash.size() > 0) {
					Enumeration newSlaEnu = slanodeHash.keys();
					Calendar tempCal = null;
					Date cc = null;
					String time = "";
					String tablename = "";
					while (newSlaEnu.hasMoreElements()) {
						String slaconfigid = (String) newSlaEnu.nextElement();
						Hashtable sladataHash = (Hashtable) slanodeHash.get(slaconfigid);
						if (sladataHash != null && sladataHash.size() > 0) {
							PingCollectEntity statusdata = (PingCollectEntity) sladataHash.get("status");
							if (statusdata != null) {
								tempCal = (Calendar) statusdata.getCollecttime();
								cc = tempCal.getTime();
								time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								tablename = "slastatus" + slaconfigid;
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + statusdata.getIpaddress() + "','" + statusdata.getRestype() + "','" + statusdata.getCategory() + "','" + statusdata.getEntity() + "','" + statusdata.getSubentity() + "','" + statusdata.getUnit() + "','" + statusdata.getChname() + "','" + statusdata.getBak() + "'," + statusdata.getCount() + ",'" + statusdata.getThevalue() + "'," + time + ")";
								dbmanager.addBatch(sql);
							}

							PingCollectEntity rttdata = (PingCollectEntity) sladataHash.get("rtt");
							if (rttdata != null) {
								tempCal = (Calendar) rttdata.getCollecttime();
								cc = tempCal.getTime();
								time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								tablename = "slartt" + slaconfigid;
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + rttdata.getIpaddress() + "','" + rttdata.getRestype() + "','" + rttdata.getCategory() + "','" + rttdata.getEntity() + "','" + rttdata.getSubentity() + "','" + rttdata.getUnit() + "','" + rttdata.getChname() + "','" + rttdata.getBak() + "'," + rttdata.getCount() + ",'" + rttdata.getThevalue() + "'," + time + ")";
								dbmanager.addBatch(sql);
							}

							if (sladataHash.containsKey("jitter")) {
								Vector jitterV = (Vector) sladataHash.get("jitter");
								if (jitterV != null && jitterV.size() > 0) {
									for (int k = 0; k < jitterV.size(); k++) {
										PingCollectEntity jitterdata = (PingCollectEntity) jitterV.get(k);
										tempCal = (Calendar) jitterdata.getCollecttime();
										cc = tempCal.getTime();
										time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										tablename = "slajitter" + slaconfigid;
										sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + jitterdata.getIpaddress() + "','" + jitterdata.getRestype() + "','" + jitterdata.getCategory() + "','" + jitterdata.getEntity() + "','" + jitterdata.getSubentity() + "','" + jitterdata.getUnit() + "','" + jitterdata.getChname() + "','" + jitterdata.getBak() + "'," + jitterdata.getCount() + ",'" + jitterdata.getThevalue() + "'," + time + ")";
										dbmanager.addBatch(sql);
									}
								}
							}
						}

					}
				}

			}
			try {
				dbmanager.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
			dbmanager = null;
			// System.gc();
		}

		return true;
	}

	public synchronized boolean createMultiHostData(Hashtable datahash) {

		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			DBManager dbmanager = new DBManager();
			try {
				while (iphash.hasMoreElements()) {
					String ip = (String) iphash.nextElement();
					String allipstr = SysUtil.doip(ip);

					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata != null) {
						// 将数据放入内存中
						Hashtable sharedata = ShareData.getSharedata();
						if (ipdata != null && ipdata.size() > 0)
							sharedata.put(ip, ipdata);

						// Process
						Vector proVector = (Vector) ipdata.get("process");

						processHostProcData("pro" + allipstr, ip, proVector);
						// Memory
						Vector memoryVector = (Vector) ipdata.get("memory");
						if (memoryVector != null && memoryVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("memory", memoryVector);
							ShareData.getSharedata().put(ip, ipAllData);

							for (int si = 0; si < memoryVector.size(); si++) {
								MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
								if (memorydata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) memorydata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "memory" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}

						}

						// CPU
						Vector cpuVector = (Vector) ipdata.get("cpu");
						if (cpuVector != null && cpuVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("cpu", cpuVector);
							ShareData.getSharedata().put(ip, ipAllData);
							// 得到CPU平均
							CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
							if (cpudata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) cpudata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "cpu" + allipstr;
								String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "'," + time + ")";
								try {
									dbmanager.addBatch(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}

						}

						Vector diskVector = (Vector) ipdata.get("disk");
						if (diskVector != null && diskVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("disk", diskVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < diskVector.size(); si++) {
								DiskCollectEntity diskdata = (DiskCollectEntity) diskVector.elementAt(si);
								if (diskdata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) diskdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "diskincre" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								if (diskdata.getEntity().equals("Utilization")) {
									Calendar tempCal = (Calendar) diskdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "disk" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}

						// Interface
						Vector interfaceVector = (Vector) ipdata.get("interface");
						if (interfaceVector != null && interfaceVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("interface", interfaceVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < interfaceVector.size(); si++) {
								Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.elementAt(si);
								if (interfacedata.getRestype().equals("dynamic")) {
									// session.save(interfacedata);
								}
							}
						}

						// AllUtilHdxPerc

						// AllUtilHdx
						Vector allutilhdxVector = (Vector) ipdata.get("allutilhdx");
						if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("allutilhdx", allutilhdxVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < allutilhdxVector.size(); si++) {
								AllUtilHdx allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);

								if (allutilhdx.getRestype().equals("dynamic")) {
									if (allutilhdx.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) allutilhdx.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "allutilhdx" + allipstr;

									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "'," + time + ")";
									// System.out.println(sql);
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}

						// UtilHdxPerc
						Vector utilhdxpercVector = (Vector) ipdata.get("utilhdxperc");
						if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("utilhdxperc", utilhdxpercVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < utilhdxpercVector.size(); si++) {
								UtilHdxPerc utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);

								if (utilhdxperc.getRestype().equals("dynamic")) {
									// session.save(utilhdx);
									if (utilhdxperc.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) utilhdxperc.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "utilhdxperc" + allipstr;

									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "'," + time + ")";
									// System.out.println(sql);
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// conn.executeUpdate(sql);
								}
							}
						}

						// UtilHdx
						Vector utilhdxVector = (Vector) ipdata.get("utilhdx");
						if (utilhdxVector != null && utilhdxVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("utilhdx", utilhdxVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < utilhdxVector.size(); si++) {
								UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

								if (utilhdx.getRestype().equals("dynamic")) {
									// session.save(utilhdx);
									if (utilhdx.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) utilhdx.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "utilhdx" + allipstr;

									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "'," + time + ")";
									// System.out.println(sql);
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// conn.executeUpdate(sql);
								}
							}
						}

						// packs
						Vector packsVector = (Vector) ipdata.get("packs");
						if (packsVector != null && packsVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("packs", packsVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < packsVector.size(); si++) {
								Packs packs = (Packs) packsVector.elementAt(si);
								if (packs.getRestype().equals("dynamic")) {
									if (packs.getThevalue().equals("0"))
										continue;
									// session.save(packs);
									Calendar tempCal = (Calendar) packs.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "packs" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// conn.executeUpdate(sql);
								}
							}
						}

						// inpacks
						Vector inpacksVector = (Vector) ipdata.get("inpacks");
						if (inpacksVector != null && inpacksVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("inpacks", inpacksVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < inpacksVector.size(); si++) {
								InPkts packs = (InPkts) inpacksVector.elementAt(si);
								if (packs.getRestype().equals("dynamic")) {
									if (packs.getThevalue().equals("0"))
										continue;
									// session.save(packs);
									Calendar tempCal = (Calendar) packs.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "inpacks" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// conn.executeUpdate(sql);
								}
							}
						}

						// inpacks
						Vector outpacksVector = (Vector) ipdata.get("outpacks");
						if (outpacksVector != null && outpacksVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("outpacks", outpacksVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < outpacksVector.size(); si++) {
								OutPkts packs = (OutPkts) outpacksVector.elementAt(si);
								if (packs.getRestype().equals("dynamic")) {
									if (packs.getThevalue().equals("0"))
										continue;
									// session.save(packs);
									Calendar tempCal = (Calendar) packs.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "outpacks" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// conn.executeUpdate(sql);
								}
							}
						}

						// ipmac
						Vector ipmacVector = (Vector) ipdata.get("ipmac");
						Hashtable ipmacs = ShareData.getRelateipmacdata();
						Hashtable ipmacband = ShareData.getIpmacbanddata();
						IpMacChangeDao macchangedao = new IpMacChangeDao();
						Hashtable macHash = null;
						try {
							macHash = macchangedao.loadMacIpHash();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macchangedao.close();
						}

						if (ipmacVector != null && ipmacVector.size() > 0) {
							// 放入内存里
							ShareData.setRelateipmacdata(ip, ipmacVector);
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("ipmac", ipmacVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}
						int firstipmac = ShareData.getFirstipmac();
						if (firstipmac == 0) {
							// 第一次启动
							// 设置变量值，并装载IP-MAC对照表
							ShareData.setFirstipmac(1);
							// 装载该IP的IP-MAC对应绑定关系表
							ResultSet rs = dbmanager.executeQuery("select * from nms_ipmacbase");
							List list = loadFromIpMacBandRS(rs);
							if (list != null && list.size() > 0) {
								for (int i = 0; i < list.size(); i++) {
									IpMacBase _ipmacbase = (IpMacBase) list.get(i);
									if (ipmacband.containsKey(_ipmacbase.getMac())) {
										// 已经存在
										List existMacList = (List) ipmacband.get(_ipmacbase.getMac());
										existMacList.add(_ipmacbase);
										ipmacband.put(_ipmacbase.getMac(), existMacList);
									} else {
										List macList = new ArrayList();
										macList.add(_ipmacbase);
										ipmacband.put(_ipmacbase.getMac(), macList);
									}

								}
							}
							rs.close();

						}
						if (ipmacVector != null && ipmacVector.size() > 0) {
							// 把实时采集到的ARP表信息存放到共享数据里，然后再更新数据库
							ipmacs.put(ip, ipmacVector);
							String sql = "delete from ipmac where relateipaddr='" + ip + "'";
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception e) {
								e.printStackTrace();
							}

							String _localMAC = "";
							Hashtable _AllMAC = new Hashtable();
							Hashtable _sameMAC = new Hashtable();
							Vector tempVector = new Vector();
							for (int si = 0; si < ipmacVector.size(); si++) {
								IpMac ipmac = (IpMac) ipmacVector.elementAt(si);
								if (ipmac == null)
									continue;
								if (ipmac.getIpaddress() == null)
									continue;
								if (ipmac.getIpaddress().equals(ip)) {
									// 若是本机IP
									_localMAC = ipmac.getMac();
								}
								if (!_AllMAC.containsKey(ipmac.getMac())) {
									// 若不存在次IP
									_AllMAC.put(ipmac.getMac(), ipmac);
								} else {
									// 若存在

									tempVector.add(ipmac);
									// tempVector.add(_AllIp.get(ipmac.getIpaddress()));
									_sameMAC.put(ipmac.getMac(), tempVector);
								}
							}
							for (int si = 0; si < ipmacVector.size(); si++) {
								IpMac ipmac = (IpMac) ipmacVector.elementAt(si);
								try {
									if (ipmac != null && ipmac.getMac() != null && ipmac.getMac().length() == 17) {
										dbmanager.executeUpdate(ipmacInsertSQL(ipmac));
									}
									// 需要加是否对MAC地址进行审计的判断
									/*
									 * PingUtil pingU=new
									 * PingUtil(ipmac.getIpaddress()); Integer[]
									 * packet=pingU.ping();
									 * //vector=pingU.addhis(packet);
									 * SysLogger.info(ipmac.getIpaddress()+"
									 * pack====="+packet[0]);
									 * //把存活的MAC地址存入数据库nms_machistory
									 * if(packet[0]!=null){ if(packet[0] >0){
									 * //连通的MAC,将当前的MAC存入NMS_MACHISTORY表
									 * MacHistory machis = new MacHistory();
									 * machis
									 * .setRelateipaddr(ipmac.getRelateipaddr
									 * ()); machis.setBak(ipmac.getBak());
									 * machis
									 * .setCollecttime(ipmac.getCollecttime());
									 * machis.setIfindex(ipmac.getIfindex());
									 * machis
									 * .setIpaddress(ipmac.getIpaddress());
									 * machis.setMac(ipmac.getMac());
									 * machis.setThevalue("1");
									 * dbmanager.executeUpdate
									 * (machistoryInsertSQL(machis)); }else{
									 * //不在线的MAC }
									 * //hostdata.setThevalue(packet[
									 * 0].toString()); } else{ //不通的MAC
									 * //hostdata.setThevalue("0"); //continue;
									 * }
									 */
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								// session.save(ipmac);
								if (ipmac == null)
									continue;
								if (ipmac.getMac() == null)
									continue;
								if (ipmac.getMac().equalsIgnoreCase(_localMAC)) {
									// 若是本机MAC,IP不是本机，则过滤
									if (!ipmac.getIpaddress().equals(ip))
										continue;
								}
								// 同时更新IP-MAC绑定表，若IP-MAC地址变更，则采用新的IP-MAC信息，同时保存到历史变更表里
								if (ipmacband.containsKey(ipmac.getMac())) {
									List existMacList = (List) ipmacband.get(ipmac.getMac());
									if (existMacList != null && existMacList.size() > 0) {
										int changeflag = 0;
										int samenodeflag = 0;
										String ipstr = "";
										IpMacBase changeMacBase = null;
										for (int i = 0; i < existMacList.size(); i++) {
											IpMacBase temp_ipmacband = (IpMacBase) existMacList.get(i);
											ipstr = ipstr + "或" + temp_ipmacband.getRelateipaddr();
											if (temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())) {
												changeflag = 1;
											}
										}
										if (changeflag == 0) {
											// 没有相等的IP,判断从哪个设备上路过
											for (int i = 0; i < existMacList.size(); i++) {
												IpMacBase temp_ipmacband = (IpMacBase) existMacList.get(i);
												if (temp_ipmacband.getRelateipaddr().equalsIgnoreCase(ipmac.getRelateipaddr())) {
													samenodeflag = 1;
													changeMacBase = temp_ipmacband;
													break;
												}
											}
										}
										// 若存在此MAC，则判断IP是否相等
										// IpMacBase temp_ipmacband =
										// (IpMacBase)ipmacband.get(ipmac.getMac());

										// if(!temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())){
										if (changeMacBase != null) {
											// 若IP不相等，而且找到设备上路过,则用新的信息修改该IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

											String _ipaddress = changeMacBase.getIpaddress();
											changeMacBase.setIpaddress(ipmac.getIpaddress());
											// ShareData.setIpmacbanddata(ipmac.getMac(),temp_ipmacband);
											existMacList = (List) ipmacband.get(changeMacBase.getMac());
											existMacList.add(changeMacBase);
											ipmacband.put(changeMacBase.getMac(), existMacList);
											try {
												dbmanager.executeUpdate(ipmacbandUpdateSQL(changeMacBase));
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											if (_sameMAC.containsKey(ipmac.getMac())) {
												if (changeMacBase.getIfband() == 0)
													continue;
											}

											// 在历史表里追加一条新加入的提示信息
											IpMacChange _ipmacchange = new IpMacChange();
											_ipmacchange.setMac(ipmac.getMac());
											_ipmacchange.setChangetype("2");
											_ipmacchange.setDetail("该MAC地址从IP：" + _ipaddress + " 变换到IP：" + ipmac.getIpaddress());
											_ipmacchange.setIpaddress(ipmac.getIpaddress());
											_ipmacchange.setCollecttime(ipmac.getCollecttime());
											// 对发生IP变更的要进行PING操作，若能PING通，则进行下一不操作

											Vector vector = null;
											PingUtil pingU = new PingUtil(ipmac.getIpaddress());
											Integer[] packet = pingU.ping();
											// vector=pingU.addhis(packet);
											if (packet[0] != null) {
												// hostdata.setThevalue(packet[0].toString());
											} else {
												// hostdata.setThevalue("0");
												continue;
											}

											// 若IP-MAC已经绑定，则产生事件
											if (changeMacBase.getIfband() == 1) {
												try {
													dbmanager.executeUpdate(ipmacchangeInsertSQL(_ipmacchange));
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												// session.save(_ipmacchange);
												// 写事件
												/*
												 * I_Eventlist eventmanager=new
												 * EventlistManager(); Eventlist
												 * event = new Eventlist();
												 * event
												 * .setEventtype("network");
												 * //事件类型需要重新定义
												 * ：网络、主机、数据库、WEB服务、FTP服务、安全产品等
												 * event
												 * .setEventlocation(temp_ipmacband
												 * .getIpaddress());
												 * event.setManagesign(new
												 * Integer(0));
												 * event.setReportman
												 * ("monitorpc"); String time =
												 * sdf
												 * .format(ipmac.getCollecttime
												 * ().getTime());
												 * event.setRecordtime
												 * (ipmac.getCollecttime());
												 * event
												 * .setContent(time+"&IP地址更新&"
												 * +_ipaddress
												 * +"&该MAC地址从IP："+_ipaddress+"
												 * 变换到IP
												 * ："+ipmac.getIpaddress());
												 * event.setLevel1(new
												 * Integer(2)); Vector eventtmpV
												 * = new Vector();
												 * eventtmpV.add(event);
												 * eventmanager
												 * .createEventlist(eventtmpV);
												 */
											}
										} else if (changeflag == 0) {
											// 若IP不相等，但没找到相同的设备上路过,说明该MAC经过的设备发生了改变,则追加新的IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

											IpMacBase _ipmacband = new IpMacBase();
											_ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
											_ipmacband.setIfindex(ipmac.getIfindex());
											_ipmacband.setIpaddress(ipmac.getIpaddress());
											_ipmacband.setMac(ipmac.getMac());
											_ipmacband.setCollecttime(ipmac.getCollecttime());
											_ipmacband.setIfband(0);
											_ipmacband.setIfsms("0");
											_ipmacband.setEmployee_id(new Integer(0));
											_ipmacband.setRelateipaddr(ip);
											ipmacband.put(ipmac.getMac(), _ipmacband);
											try {
												dbmanager.addBatch(ipmacbaseInsertSQL(_ipmacband));
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
												continue;
											// 在历史表里追加一条新加入的提示信息
											IpMacChange _ipmacchange = new IpMacChange();
											_ipmacchange.setMac(ipmac.getMac());
											_ipmacchange.setChangetype("1");
											_ipmacchange.setDetail("该MAC地址从设备" + ipstr + " 变换到设备" + ipmac.getRelateipaddr() + "上,IP：" + ipmac.getIpaddress());
											_ipmacchange.setIpaddress(ipmac.getIpaddress());
											_ipmacchange.setCollecttime(ipmac.getCollecttime());
											_ipmacchange.setRelateipaddr(ip);
											_ipmacchange.setIfindex(ipmac.getIfindex());

											try {
												dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
												// session.save(_ipmacband);
											} catch (Exception ex) {
												ex.printStackTrace();
											}

										}
									}
								} else {
									// 判断变更表里是否已经有该MAC地址
									if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
										continue;
									// 在历史表里追加一条新加入的提示信息
									IpMacChange _ipmacchange = new IpMacChange();
									_ipmacchange.setMac(ipmac.getMac());
									_ipmacchange.setChangetype("1");
									_ipmacchange.setDetail("新增加的IP-MAC");
									_ipmacchange.setIpaddress(ipmac.getIpaddress());
									_ipmacchange.setCollecttime(ipmac.getCollecttime());
									_ipmacchange.setRelateipaddr(ip);
									_ipmacchange.setIfindex(ipmac.getIfindex());

									try {
										if (ipmac.getMac().length() == 17) {
											dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
										}
									} catch (Exception ex) {
										ex.printStackTrace();
									}

								}
							}
						}

						// iprouter
						Vector iprouterVector = (Vector) ipdata.get("iprouter");
						if (iprouterVector != null && iprouterVector.size() > 0) {
							// 放入内存里
							ShareData.setIprouterdata(ip, iprouterVector);
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("iprouter", iprouterVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// AIX 的 errpt 信息
						Vector errptVector = (Vector) ipdata.get("errptlog");
						if (errptVector != null && errptVector.size() > 0) {
							// 放入内存里
							ErrptlogDao errptlogDao = new ErrptlogDao();
							try {
								for (int i = 0; i < errptVector.size(); i++) {
									Errptlog errptlog = (Errptlog) errptVector.get(i);
									errptlogDao.save(errptlog);
									errptlogDao = new ErrptlogDao();
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								errptlogDao.close();
							}
						}

						// AIX 的 换页率 信息
						Hashtable paginghash = (Hashtable) ipdata.get("paginghash");
						if (paginghash != null && paginghash.size() > 0) {
							if (paginghash.get("Percent_Used") != null) {
								String pused = ((String) paginghash.get("Percent_Used")).replaceAll("%", "");
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "pgused" + allipstr;
								String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','','','','','','','',0,'" + pused + "'," + time + ")";
								try {
									dbmanager.addBatch(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

						// policydata
						Hashtable policyHash = (Hashtable) ipdata.get("policys");
						if (policyHash != null && policyHash.size() > 0) {
							// 放入内存里
							ShareData.setPolicydata(ip, policyHash);
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("policys", policyHash);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// 系统信息
						Vector sysVector = (Vector) ipdata.get("system");
						if (sysVector != null && sysVector.size() > 0) {
							// 放入内存里
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("system", sysVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// 硬件信息
						Vector deviceVector = (Vector) ipdata.get("device");
						if (deviceVector != null && deviceVector.size() > 0) {
							// 放入内存里
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("device", deviceVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// 硬件信息
						Vector storageVector = (Vector) ipdata.get("storage");
						if (storageVector != null && storageVector.size() > 0) {
							// 放入内存里
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("storage", storageVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}
					}
				}
				try {
					dbmanager.executeBatch();
					dbmanager.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbmanager.executeBatch();
					dbmanager.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dbmanager.close();
			}
		}

		return true;
	}

	public synchronized boolean createMultiHostData(Hashtable datahash, String type) {
		String runmodel = PollingEngine.getCollectwebflag();
		if (type.equalsIgnoreCase("host")) {
			if (datahash != null && datahash.size() > 0) {
				if ("1".equals(runmodel)) {
					// 采集与访问是分离模式 保存采集到的aix服务器的数据信息
					// 实时数据入库处理
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.processHostData(datahash);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		IpMacChangeDao macchangedao = new IpMacChangeDao();
		Hashtable macHash = null;
		try {
			macHash = macchangedao.loadMacIpHash();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			macchangedao.close();
		}

		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			DBManager dbmanager = new DBManager();
			Vector errptVector = null;
			MemoryCollectEntity memorydata = null;
			DiskCollectEntity diskdata = null;
			AllUtilHdx allutilhdx = null;
			UtilHdxPerc utilhdxperc = null;
			UtilHdx utilhdx = null;
			Packs packs = null;
			InPkts inpacks = null;
			OutPkts outpacks = null;
			List list = new ArrayList();
			Errptlog errptlog = null;
			StringBuffer errptbuffer = new StringBuffer();

			try {
				while (iphash.hasMoreElements()) {
					String ip = (String) iphash.nextElement();
					String allipstr = SysUtil.doip(ip);
					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata != null) {
						// 将数据放入内存中
						Hashtable sharedata = ShareData.getSharedata();
						if (ipdata != null && ipdata.size() > 0)
							sharedata.put(ip, ipdata);

						// Process
						Vector proVector = (Vector) ipdata.get("process");

						processHostProcData("pro" + allipstr, ip, proVector);

						// Memory
						Vector memoryVector = (Vector) ipdata.get("memory");
						if (memoryVector != null && memoryVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("memory", memoryVector);
							ShareData.getSharedata().put(ip, ipAllData);

							for (int si = 0; si < memoryVector.size(); si++) {
								memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
								if (memorydata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) memorydata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "memory" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								memorydata = null;
							}

						}

						// CPU
						CpuCollectEntity cpudata = null;
						Vector cpuVector = (Vector) ipdata.get("cpu");
						if (cpuVector != null && cpuVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("cpu", cpuVector);
							ShareData.getSharedata().put(ip, ipAllData);
							// 得到CPU平均
							cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
							if (cpudata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) cpudata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "cpu" + allipstr;
								String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "'," + time + ")";
								try {
									dbmanager.addBatch(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}

						}

						// CPU详细信息
						List cpuperflist = (List) ipdata.get("cpuperflist");
						if (cpuperflist != null && cpuperflist.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("cpuperflist", cpuperflist);
							ShareData.getSharedata().put(ip, ipAllData);
							Hashtable cpuperfhash = (Hashtable) cpuperflist.get(0);
							String[] items1 = { "usr", "sys", "wio", "idle" };
							String[] items2 = { "user", "nice", "system", "iowait", "steal", "idle" };
							String nice = (String) cpuperfhash.get("%nice");
							if (cpudata != null) {
								Calendar tempCal = (Calendar) cpudata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "cpudtl" + allipstr;
								String sql = "";
								if (nice == null || nice.equalsIgnoreCase("null")) {
									String values1[] = new String[4];
									values1[0] = (String) cpuperfhash.get("%usr");
									values1[1] = (String) cpuperfhash.get("%sys");
									values1[2] = (String) cpuperfhash.get("%wio");
									values1[3] = (String) cpuperfhash.get("%idle");
									for (int i = 0; i < items1.length; i++) {
										sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + items1[i] + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + values1[i] + "'," + time + ")";
										dbmanager.addBatch(sql);
									}
									values1 = null;
								} else {// Linux
									String values2[] = new String[6];
									values2[0] = (String) cpuperfhash.get("%user");
									values2[1] = (String) cpuperfhash.get("%nice");
									values2[2] = (String) cpuperfhash.get("%system");
									values2[3] = (String) cpuperfhash.get("%iowait");
									values2[4] = (String) cpuperfhash.get("%steal");
									values2[5] = (String) cpuperfhash.get("%idle");
									for (int i = 0; i < items2.length; i++) {
										if (values2[4] == null || values2[4].equalsIgnoreCase("null")) {
											continue;
										}
										sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + items2[i] + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + values2[i] + "'," + time + ")";
										dbmanager.addBatch(sql);
									}
									values2 = null;
								}
							}

						}

						// disk yangjun
						Vector diskVector = (Vector) ipdata.get("disk");
						if (diskVector != null && diskVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("disk", diskVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < diskVector.size(); si++) {
								diskdata = (DiskCollectEntity) diskVector.elementAt(si);
								if (diskdata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) diskdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "diskincre" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								if (diskdata.getEntity().equals("Utilization")) {
									Calendar tempCal = (Calendar) diskdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "disk" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								diskdata = null;
							}
						}

						// Interface
						Vector interfaceVector = (Vector) ipdata.get("interface");
						if (interfaceVector != null && interfaceVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("interface", interfaceVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// AllUtilHdxPerc

						// AllUtilHdx
						Vector allutilhdxVector = (Vector) ipdata.get("allutilhdx");
						if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("allutilhdx", allutilhdxVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < allutilhdxVector.size(); si++) {
								allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);
								if (allutilhdx.getRestype().equals("dynamic")) {
									if (allutilhdx.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) allutilhdx.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "allutilhdx" + allipstr;

									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								allutilhdx = null;
							}
						}

						// UtilHdxPerc
						Vector utilhdxpercVector = (Vector) ipdata.get("utilhdxperc");
						if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("utilhdxperc", utilhdxpercVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < utilhdxpercVector.size(); si++) {
								utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);

								if (utilhdxperc.getRestype().equals("dynamic")) {
									if (utilhdxperc.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) utilhdxperc.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "utilhdxperc" + allipstr;

									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								utilhdxperc = null;
							}
						}

						// UtilHdx
						Vector utilhdxVector = (Vector) ipdata.get("utilhdx");
						if (utilhdxVector != null && utilhdxVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("utilhdx", utilhdxVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < utilhdxVector.size(); si++) {
								utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

								if (utilhdx.getRestype().equals("dynamic")) {
									if (utilhdx.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) utilhdx.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "utilhdx" + allipstr;

									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}

						// packs
						Vector packsVector = (Vector) ipdata.get("packs");
						if (packsVector != null && packsVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("packs", packsVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < packsVector.size(); si++) {
								packs = (Packs) packsVector.elementAt(si);
								if (packs.getRestype().equals("dynamic")) {
									if (packs.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) packs.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "packs" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								packs = null;
							}
						}

						// inpacks
						Vector inpacksVector = (Vector) ipdata.get("inpacks");
						if (inpacksVector != null && inpacksVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("inpacks", inpacksVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < inpacksVector.size(); si++) {
								inpacks = (InPkts) inpacksVector.elementAt(si);
								if (inpacks.getRestype().equals("dynamic")) {
									if (inpacks.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) inpacks.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "inpacks" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + inpacks.getRestype() + "','" + inpacks.getCategory() + "','" + inpacks.getEntity() + "','" + inpacks.getSubentity() + "','" + inpacks.getUnit() + "','" + inpacks.getChname() + "','" + inpacks.getBak() + "'," + inpacks.getCount() + ",'" + inpacks.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}

						// inpacks
						Vector outpacksVector = (Vector) ipdata.get("outpacks");
						if (outpacksVector != null && outpacksVector.size() > 0) {
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("outpacks", outpacksVector);
							ShareData.getSharedata().put(ip, ipAllData);
							for (int si = 0; si < outpacksVector.size(); si++) {
								outpacks = (OutPkts) outpacksVector.elementAt(si);
								if (outpacks.getRestype().equals("dynamic")) {
									if (outpacks.getThevalue().equals("0"))
										continue;
									Calendar tempCal = (Calendar) outpacks.getCollecttime();
									Date cc = tempCal.getTime();
									String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
									String tablename = "outpacks" + allipstr;
									String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + outpacks.getRestype() + "','" + outpacks.getCategory() + "','" + outpacks.getEntity() + "','" + outpacks.getSubentity() + "','" + outpacks.getUnit() + "','" + outpacks.getChname() + "','" + outpacks.getBak() + "'," + outpacks.getCount() + ",'" + outpacks.getThevalue() + "'," + time + ")";
									try {
										dbmanager.addBatch(sql);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}
						// 执行批处理插入
						dbmanager.executeBatch();

						// ipmac
						if (ipdata.containsKey("ipmac")) {
							// ipmac
							DBManager ipmacdbmanager = new DBManager();
							IpMac ipmac = null;

							Hashtable ipmachash = (Hashtable) ipdata.get("ipmac");
							Vector ipmacVector = (Vector) ipmachash.get("ipmac");
							if (ipmacVector != null && ipmacVector.size() > 0) {
								// 放入内存里
								ShareData.setRelateipmacdata(ip, ipmacVector);
							}
							int firstipmac = ShareData.getFirstipmac();
							if (firstipmac == 0) {
								// 第一次启动
								// 设置变量值，并装载IP-MAC对照表
								ShareData.setFirstipmac(1);
								// 装载该IP的IP-MAC对应绑定关系表
								ResultSet rs = dbmanager.executeQuery("select * from nms_ipmacbase");
								List bandlist = loadFromIpMacBandRS(rs);
								if (bandlist != null && bandlist.size() > 0) {
									for (int i = 0; i < bandlist.size(); i++) {
										IpMacBase _ipmacbase = (IpMacBase) bandlist.get(i);
										if (ShareData.getIpmacbanddata().containsKey(_ipmacbase.getMac())) {
											// 已经存在
											List existMacList = (List) ShareData.getIpmacbanddata().get(_ipmacbase.getMac());
											existMacList.add(_ipmacbase);
											ShareData.getIpmacbanddata().put(_ipmacbase.getMac(), existMacList);
										} else {
											List macList = new ArrayList();
											macList.add(_ipmacbase);
											ShareData.getIpmacbanddata().put(_ipmacbase.getMac(), macList);
										}

									}
								}
								rs.close();

							}
							if (ipmacVector != null && ipmacVector.size() > 0) {
								// 把实时采集到的ARP表信息存放到共享数据里，然后再更新数据库
								ShareData.getRelateipmacdata().put(ip, ipmacVector);
								String sql = "delete from ipmac where relateipaddr='" + ip + "'";
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								String _localMAC = "";
								Hashtable _AllMAC = new Hashtable();
								Hashtable _sameMAC = new Hashtable();
								Vector tempVector = new Vector();
								for (int si = 0; si < ipmacVector.size(); si++) {
									ipmac = (IpMac) ipmacVector.elementAt(si);
									if (ipmac == null)
										continue;
									if (ipmac.getIpaddress() == null)
										continue;
									if (ipmac.getIpaddress().equals(ip)) {
										// 若是本机IP
										_localMAC = ipmac.getMac();
									}
									if (!_AllMAC.containsKey(ipmac.getMac())) {
										// 若不存在次IP
										_AllMAC.put(ipmac.getMac(), ipmac);
									} else {
										// 若存在

										tempVector.add(ipmac);
										_sameMAC.put(ipmac.getMac(), tempVector);
									}
								}
								for (int si = 0; si < ipmacVector.size(); si++) {
									ipmac = (IpMac) ipmacVector.elementAt(si);
									try {
										if (ipmac != null && ipmac.getMac() != null && ipmac.getMac().length() == 17) {
											ipmacdbmanager.addBatch(ipmacInsertSQL(ipmac));
										}
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// session.save(ipmac);
									if (ipmac == null)
										continue;
									if (ipmac.getMac() == null)
										continue;
									if (ipmac.getMac().equalsIgnoreCase(_localMAC)) {
										// 若是本机MAC,IP不是本机，则过滤
										if (!ipmac.getIpaddress().equals(ip))
											continue;
									}
									// 同时更新IP-MAC绑定表，若IP-MAC地址变更，则采用新的IP-MAC信息，同时保存到历史变更表里
									if (ShareData.getIpmacbanddata().containsKey(ipmac.getMac())) {
										if (ShareData.getIpmacbanddata().get(ipmac.getMac()) != null && ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).size() > 0) {
											int changeflag = 0;
											String ipstr = "";
											IpMacBase changeMacBase = null;
											for (int i = 0; i < ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).size(); i++) {
												IpMacBase temp_ipmacband = (IpMacBase) ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).get(i);
												ipstr = ipstr + "或" + temp_ipmacband.getRelateipaddr();
												if (temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())) {
													changeflag = 1;
												}
											}
											if (changeflag == 0) {
												// 没有相等的IP,判断从哪个设备上路过
												for (int i = 0; i < ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).size(); i++) {
													IpMacBase temp_ipmacband = (IpMacBase) ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).get(i);
													if (temp_ipmacband.getRelateipaddr().equalsIgnoreCase(ipmac.getRelateipaddr())) {
														changeMacBase = temp_ipmacband;
														break;
													}
												}
											}
											// 若存在此MAC，则判断IP是否相等
											// IpMacBase temp_ipmacband =
											// (IpMacBase)ipmacband.get(ipmac.getMac());

											// if(!temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())){
											if (changeMacBase != null) {
												// 若IP不相等，而且找到设备上路过,则用新的信息修改该IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

												String _ipaddress = changeMacBase.getIpaddress();
												changeMacBase.setIpaddress(ipmac.getIpaddress());
												// ShareData.setIpmacbanddata(ipmac.getMac(),temp_ipmacband);
												// existMacList = (List)
												// ShareData.getIpmacbanddata().get(changeMacBase.getMac());
												((List) ShareData.getIpmacbanddata().get(changeMacBase.getMac())).add(changeMacBase);
												ShareData.getIpmacbanddata().put(changeMacBase.getMac(), ShareData.getIpmacbanddata().get(changeMacBase.getMac()));
												try {
													dbmanager.executeUpdate(ipmacbandUpdateSQL(changeMacBase));
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												if (_sameMAC.containsKey(ipmac.getMac())) {
													if (changeMacBase.getIfband() == 0)
														continue;
												}

												// 在历史表里追加一条新加入的提示信息
												IpMacChange _ipmacchange = new IpMacChange();
												_ipmacchange.setMac(ipmac.getMac());
												_ipmacchange.setChangetype("2");
												_ipmacchange.setDetail("该MAC地址从IP：" + _ipaddress + " 变换到IP：" + ipmac.getIpaddress());
												_ipmacchange.setIpaddress(ipmac.getIpaddress());
												_ipmacchange.setCollecttime(ipmac.getCollecttime());
												// 对发生IP变更的要进行PING操作，若能PING通，则进行下一不操作

												Vector vector = null;
												PingUtil pingU = new PingUtil(ipmac.getIpaddress());
												Integer[] packet = pingU.ping();
												// vector=pingU.addhis(packet);
												if (packet[0] != null) {
													// hostdata.setThevalue(packet[0].toString());
												} else {
													// hostdata.setThevalue("0");
													continue;
												}

												// 若IP-MAC已经绑定，则产生事件
												if (changeMacBase.getIfband() == 1) {
													try {
														ipmacdbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
													} catch (Exception ex) {
														ex.printStackTrace();
													}
													// session.save(_ipmacchange);
													// 写事件
													/*
													 * I_Eventlist
													 * eventmanager=new
													 * EventlistManager();
													 * Eventlist event = new
													 * Eventlist();
													 * event.setEventtype
													 * ("network");
													 * //事件类型需要重新定义：
													 * 网络、主机、数据库、WEB服务
													 * 、FTP服务、安全产品等
													 * event.setEventlocation
													 * (temp_ipmacband
													 * .getIpaddress());
													 * event.setManagesign(new
													 * Integer(0));
													 * event.setReportman
													 * ("monitorpc"); String
													 * time = sdf.format(ipmac.
													 * getCollecttime
													 * ().getTime());
													 * event.setRecordtime
													 * (ipmac.getCollecttime());
													 * event
													 * .setContent(time+"&IP地址更新&"
													 * +
													 * _ipaddress+"&该MAC地址从IP："+
													 * _ipaddress+" 变换到IP：
													 * "+ipmac.getIpaddress());
													 * event.setLevel1(new
													 * Integer(2)); Vector
													 * eventtmpV = new Vector();
													 * eventtmpV.add(event);
													 * eventmanager
													 * .createEventlist
													 * (eventtmpV);
													 */
												}
												_ipmacchange = null;
											} else if (changeflag == 0) {
												// 若IP不相等，但没找到相同的设备上路过,说明该MAC经过的设备发生了改变,则追加新的IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

												IpMacBase _ipmacband = new IpMacBase();
												_ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
												_ipmacband.setIfindex(ipmac.getIfindex());
												_ipmacband.setIpaddress(ipmac.getIpaddress());
												_ipmacband.setMac(ipmac.getMac());
												_ipmacband.setCollecttime(ipmac.getCollecttime());
												_ipmacband.setIfband(0);
												_ipmacband.setIfsms("0");
												_ipmacband.setEmployee_id(new Integer(0));
												_ipmacband.setRelateipaddr(ip);
												ShareData.getIpmacbanddata().put(ipmac.getMac(), _ipmacband);
												try {
													ipmacdbmanager.addBatch(ipmacbaseInsertSQL(_ipmacband));
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												_ipmacband = null;
												if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
													continue;
												// 在历史表里追加一条新加入的提示信息
												IpMacChange _ipmacchange = new IpMacChange();
												_ipmacchange.setMac(ipmac.getMac());
												_ipmacchange.setChangetype("1");
												_ipmacchange.setDetail("该MAC地址从设备" + ipstr + " 变换到设备" + ipmac.getRelateipaddr() + "上,IP：" + ipmac.getIpaddress());
												_ipmacchange.setIpaddress(ipmac.getIpaddress());
												_ipmacchange.setCollecttime(ipmac.getCollecttime());
												_ipmacchange.setRelateipaddr(ip);
												_ipmacchange.setIfindex(ipmac.getIfindex());

												try {
													ipmacdbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
													// session.save(_ipmacband);
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												_ipmacchange = null;
											}
										}
									} else {
										// 若不存在次MAC，则为新加入的机器，则保存该IP-MAC，同时在历史表里追加一条新加入的提示信息
										/*
										 * IpMacBase _ipmacband = new
										 * IpMacBase();
										 * _ipmacband.setRelateipaddr
										 * (ipmac.getRelateipaddr());
										 * _ipmacband.
										 * setIfindex(ipmac.getIfindex());
										 * _ipmacband
										 * .setIpaddress(ipmac.getIpaddress());
										 * _ipmacband.setMac(ipmac.getMac());
										 * _ipmacband
										 * .setCollecttime(ipmac.getCollecttime
										 * ()); _ipmacband.setIfband(0);
										 * _ipmacband.setIfsms("0");
										 * _ipmacband.setEmployee_id(new
										 * Integer(0));
										 * _ipmacband.setRelateipaddr(ip);
										 * ipmacband
										 * .put(ipmac.getMac(),_ipmacband); try{
										 * dbmanager
										 * .executeUpdate(ipmacbandInsertSQL
										 * (_ipmacband));
										 * //session.save(_ipmacband);
										 * }catch(Exception ex){
										 * ex.printStackTrace(); }
										 */
										// 判断变更表里是否已经有该MAC地址
										if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
											continue;
										// 在历史表里追加一条新加入的提示信息
										IpMacChange _ipmacchange = new IpMacChange();
										_ipmacchange.setMac(ipmac.getMac());
										_ipmacchange.setChangetype("1");
										_ipmacchange.setDetail("新增加的IP-MAC");
										_ipmacchange.setIpaddress(ipmac.getIpaddress());
										_ipmacchange.setCollecttime(ipmac.getCollecttime());
										_ipmacchange.setRelateipaddr(ip);
										_ipmacchange.setIfindex(ipmac.getIfindex());

										try {
											if (ipmac.getMac().length() == 17) {
												dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
											}
											// session.save(_ipmacband);
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										_ipmacchange = null;

										// session.save(_ipmacchange);
									}
								}
							}
							try {
								ipmacdbmanager.executeBatch();
							} catch (Exception e) {
								e.printStackTrace();
								// ipmacdbmanager.close();
							} finally {
								ipmacdbmanager.close();
							}
						}

						// iprouter
						Vector iprouterVector = (Vector) ipdata.get("iprouter");
						if (iprouterVector != null && iprouterVector.size() > 0) {
							// 放入内存里
							ShareData.setIprouterdata(ip, iprouterVector);
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("iprouter", iprouterVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}
						// if (iprouterVector != null && iprouterVector.size() >
						// 0) {
						// String sql = "delete from iprouter where
						// relateipaddr='" + ip + "'";
						// //dbmanager.executeUpdate(sql);
						// for (int si = 0; si < iprouterVector.size(); si++) {
						// IpRouter iprouter = (IpRouter)
						// iprouterVector.elementAt(si);
						// try {
						// //session.save(iprouter);
						// } catch (Exception e) {
						// e.printStackTrace();
						// }
						// }
						// }

						// AIX 的 errpt 信息
						errptVector = (Vector) ipdata.get("errptlog");
						if (errptVector != null && errptVector.size() > 0) {
							// 放入内存里
							// DBManager errptdbmanager = new DBManager();
							errptbuffer = new StringBuffer();
							errptbuffer.append("insert into nms_errptlog(labels,identifier,collettime,seqnumber,nodeid,machineid,errptclass,errpttype,resourcename,resourceclass,resourcetype,locations,vpd,descriptions,hostid) values(");
							errptbuffer.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							dbmanager.setPrepareSql(errptbuffer.toString());
							// ErrptlogDao errptlogDao = new ErrptlogDao();
							try {
								for (int i = 0; i < errptVector.size(); i++) {
									errptlog = (Errptlog) errptVector.get(i);
									if (errptlog.getCollettime() == null) {
										continue;
									}
									Calendar tempCal = (Calendar) errptlog.getCollettime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									list = new ArrayList();
									list.add(errptlog.getLabels());
									list.add(errptlog.getIdentifier());
									list.add(time);
									list.add(errptlog.getSeqnumber());
									list.add(errptlog.getNodeid());
									list.add(errptlog.getMachineid());
									list.add(errptlog.getErrptclass());
									list.add(errptlog.getErrpttype());
									list.add(errptlog.getResourcename());
									list.add(errptlog.getResourceclass());
									list.add(errptlog.getRescourcetype());
									list.add(errptlog.getLocations());
									list.add(errptlog.getVpd());
									list.add(errptlog.getDescriptions());
									list.add(errptlog.getHostid());
									dbmanager.addPrepareErrptBatch(list);
									list = null;
									errptlog = null;
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								// errptlogDao.close();
							}
							errptbuffer = null;
						}

						// AIX 的 换页率 信息
						Hashtable paginghash = (Hashtable) ipdata.get("paginghash");
						if (paginghash != null && paginghash.size() > 0) {
							if (paginghash.get("Percent_Used") != null) {
								String pused = ((String) paginghash.get("Percent_Used")).replaceAll("%", "");
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "pgused" + allipstr;
								String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','','','','','','','',0,'" + pused + "','" + time + "')";
								try {
									// SysLogger.info(sql);
									dbmanager.addBatch(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

						// policydata
						Hashtable policyHash = (Hashtable) ipdata.get("policys");
						if (policyHash != null && policyHash.size() > 0) {
							// 放入内存里
							ShareData.setPolicydata(ip, policyHash);
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("policys", policyHash);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// 系统信息
						Vector sysVector = (Vector) ipdata.get("system");
						if (sysVector != null && sysVector.size() > 0) {
							// 放入内存里
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("system", sysVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// 硬件信息
						Vector deviceVector = (Vector) ipdata.get("device");
						if (deviceVector != null && deviceVector.size() > 0) {
							// 放入内存里
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("device", deviceVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}

						// 硬件信息
						Vector storageVector = (Vector) ipdata.get("storage");
						if (storageVector != null && storageVector.size() > 0) {
							// 放入内存里
							Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
							if (ipAllData == null)
								ipAllData = new Hashtable();
							ipAllData.put("storage", storageVector);
							ShareData.getSharedata().put(ip, ipAllData);
						}
					}
				}
				try {
					dbmanager.executeBatch();
					if (errptVector != null && errptVector.size() > 0) {
						dbmanager.executePreparedBatch();
					}

					dbmanager.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
				dbmanager = null;
			}
		}

		return true;
	}

	public synchronized boolean createMultiSLAData(Hashtable datahash, String type) {
		// SysLogger.info("---------------------------------------------------------------");
		String runmodel = PollingEngine.getCollectwebflag();
		// if(type.equalsIgnoreCase("host")){
		// if(datahash != null && datahash.size()>0){
		// if("1".equals(runmodel)){
		// //采集与访问是分离模式 保存采集到的aix服务器的数据信息
		// Date startdate = new Date();
		// //实时数据入库处理
		// ProcessNetData porcessData = new ProcessNetData();
		// try{
		// porcessData.processHostData(datahash);
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// }
		// }
		// }

		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			DBManager dbmanager = new DBManager();
			Vector errptVector = null;
			MemoryCollectEntity memorydata = null;
			DiskCollectEntity diskdata = null;
			AllUtilHdx allutilhdx = null;
			UtilHdxPerc utilhdxperc = null;
			UtilHdx utilhdx = null;
			Packs packs = null;
			InPkts inpacks = null;
			OutPkts outpacks = null;
			List list = new ArrayList();
			Errptlog errptlog = null;
			StringBuffer errptbuffer = new StringBuffer();

			try {
				while (iphash.hasMoreElements()) {
					String ip = (String) iphash.nextElement();
					String allipstr = SysUtil.doip(ip);
					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata != null) {
						// 将数据放入内存中
						Hashtable sharedata = ShareData.getSharedata();
						if (ipdata != null && ipdata.size() > 0)
							sharedata.put(ip, ipdata);

						// 处理主机设备的数据
						// I_Promoniconf proconf = new PromoniconfManager();
						String[] proArray = null;
						boolean probool[] = null;
						int j = 0, k = 0, m = 0, n = 0;
						Vector v = new Vector();
					}
				}
				try {
					dbmanager.executeBatch();
					if (errptVector != null && errptVector.size() > 0) {
						dbmanager.executePreparedBatch();
					}

					dbmanager.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
				dbmanager = null;
			}
		}

		return true;
	}

	public synchronized boolean createHostData(String ip, Hashtable datahash) {
		DBManager dbmanager = null;
		try {
			dbmanager = new DBManager();
			Hashtable hash = ShareData.getOctetsdata(ip);
			Hashtable sharedata = ShareData.getSharedata();
			if (datahash != null && datahash.size() > 0)
				sharedata.put(ip, datahash);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);

			ProcessCollectEntity prodata = null;
			Hostlastcollectdata lastdata = null;
			if (datahash == null || datahash.size() == 0) {
				Hashtable pingData = ShareData.getPingdata();
				Vector tempV = new Vector();
				dbmanager.close();
				return false;
			}

			// I_Promoniconf proconf = new PromoniconfManager();
			String[] proArray = null;
			boolean probool[] = null;
			int j = 0, k = 0, m = 0, n = 0;
			Vector v = new Vector();

			// Process
			Vector proVector = (Vector) datahash.get("process");

			/*
			 * nielin add 2010-08-18
			 * 
			 * 创建进程组告警
			 * 
			 * start ===============================
			 */
			// createProcessGroupEventList(ip, proVector);
			/*
			 * nielin add 2010-08-18
			 * 
			 * 创建进程组告警
			 * 
			 * end ===============================
			 */

			processHostProcData("pro" + allipstr, ip, proVector);

			// if (proVector != null && proVector.size() > 0) {
			// for (int i = 0; i < proVector.size(); i++) {
			// Processcollectdata processdata = (Processcollectdata)
			// proVector.elementAt(i);
			// processdata.setCount(null);
			//
			// if (processdata.getRestype().equals("dynamic")) {
			// Calendar tempCal = (Calendar) processdata.getCollecttime();
			// Date cc = tempCal.getTime();
			// String time=util.coversionTimeSql(sdf.format(cc));
			// String tablename = "pro" + allipstr;
			//
			// String sql = "insert into " + tablename
			// +
			// "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime)
			// "
			// + "values('" + ip + "','" + processdata.getRestype() + "','" +
			// processdata.getCategory() + "','"
			// + processdata.getEntity() + "','" + processdata.getSubentity() +
			// "','" + processdata.getUnit()
			// + "','" + processdata.getChname() + "','" + processdata.getBak()
			// + "'," + processdata.getCount()
			// + ",'" + processdata.getThevalue() + "','" + time + "')";
			// //System.out.println(sql);
			// try {
			// dbmanager.executeUpdate(sql);
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
			//
			// }
			// }
			// }
			// Memory
			Vector memoryVector = (Vector) datahash.get("memory");
			if (memoryVector != null && memoryVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("memory", memoryVector);
				ShareData.getSharedata().put(ip, ipAllData);

				for (int si = 0; si < memoryVector.size(); si++) {
					MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
					if (memorydata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) memorydata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "memory" + allipstr;
						// if
						// (memorydata.getIpaddress().equals("10.217.255.253")||memorydata.getIpaddress().equals("10.217.255.64")||memorydata.getIpaddress().equals("10.216.2.35")){
						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
						// System.out.println(sql);
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

			}

			// CPU
			CpuCollectEntity cpudata = null;
			Vector cpuVector = (Vector) datahash.get("cpu");
			if (cpuVector != null && cpuVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("cpu", cpuVector);
				ShareData.getSharedata().put(ip, ipAllData);
				// 得到CPU平均
				cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
				if (cpudata.getRestype().equals("dynamic")) {
					// session.save(cpudata);
					Calendar tempCal = (Calendar) cpudata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
					String tablename = "cpu" + allipstr;
					String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "'," + time + ")";
					// System.out.println(sql);
					try {
						dbmanager.addBatch(sql);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						// dbmanager.close();
					}
					// conn.executeUpdate(sql);
				}

			}

			// CPU详细信息
			List cpuperflist = (List) datahash.get("cpuperflist");
			if (cpuperflist != null && cpuperflist.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("cpuperflist", cpuperflist);
				ShareData.getSharedata().put(ip, ipAllData);
				// 得到CPU详细情况
				// for(int i=0;i<cpuperflist.size();i++){
				//
				// }
				Hashtable cpuperfhash = (Hashtable) cpuperflist.get(0);
				String[] items1 = { "usr", "sys", "wio", "idle" };
				String[] items2 = { "user", "nice", "system", "iowait", "steal", "idle" };
				String nice = (String) cpuperfhash.get("%nice");
				if (cpudata != null) {
					Calendar tempCal = (Calendar) cpudata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
					String tablename = "cpudtl" + allipstr;
					String sql = "";
					if (nice == null || nice.equalsIgnoreCase("null")) {
						String values1[] = new String[4];
						values1[0] = (String) cpuperfhash.get("%usr");
						values1[1] = (String) cpuperfhash.get("%sys");
						values1[2] = (String) cpuperfhash.get("%wio");
						values1[3] = (String) cpuperfhash.get("%idle");
						for (int i = 0; i < items1.length; i++) {
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + items1[i] + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + values1[i] + "','" + time + "')";
							// SysLogger.info(sql);
							dbmanager.addBatch(sql);
						}
						values1 = null;
					} else {// Linux
						String values2[] = new String[6];
						values2[0] = (String) cpuperfhash.get("%user");
						values2[1] = (String) cpuperfhash.get("%nice");
						values2[2] = (String) cpuperfhash.get("%system");
						values2[3] = (String) cpuperfhash.get("%iowait");
						values2[4] = (String) cpuperfhash.get("%steal");
						values2[5] = (String) cpuperfhash.get("%idle");
						for (int i = 0; i < items2.length; i++) {
							if (values2[4] == null || values2[4].equalsIgnoreCase("null")) {
								continue;
							}
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + items2[i] + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + values2[i] + "'," + time + ")";
							dbmanager.addBatch(sql);
						}
						values2 = null;
					}
				}

			}

			// disk yangjun
			Vector diskVector = (Vector) datahash.get("disk");
			if (diskVector != null && diskVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("disk", diskVector);
				ShareData.getSharedata().put(ip, ipAllData);
				// System.out.println("disk------------------------------");
				// System.out.println("disk--------"+diskVector.size());
				for (int si = 0; si < diskVector.size(); si++) {
					DiskCollectEntity diskdata = (DiskCollectEntity) diskVector.elementAt(si);
					if (diskdata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) diskdata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "diskincre" + allipstr;
						String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					if (diskdata.getEntity().equals("Utilization")) {
						Calendar tempCal = (Calendar) diskdata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "disk" + allipstr;
						String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			// Interface
			Vector interfaceVector = (Vector) datahash.get("interface");
			if (interfaceVector != null && interfaceVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("interface", interfaceVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < interfaceVector.size(); si++) {
					Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.elementAt(si);
					if (interfacedata.getRestype().equals("dynamic")) {
						// session.save(interfacedata);
					}
				}
			}

			// AllUtilHdxPerc

			// AllUtilHdx
			Vector allutilhdxVector = (Vector) datahash.get("allutilhdx");
			if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("allutilhdx", allutilhdxVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < allutilhdxVector.size(); si++) {
					AllUtilHdx allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);

					if (allutilhdx.getRestype().equals("dynamic")) {
						if (allutilhdx.getThevalue().equals("0"))
							continue;
						Calendar tempCal = (Calendar) allutilhdx.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "allutilhdx" + allipstr;

						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "'," + time + ")";
						// System.out.println(sql);
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			// UtilHdxPerc
			Vector utilhdxpercVector = (Vector) datahash.get("utilhdxperc");
			if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("utilhdxperc", utilhdxpercVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < utilhdxpercVector.size(); si++) {
					UtilHdxPerc utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);

					if (utilhdxperc.getRestype().equals("dynamic")) {
						// session.save(utilhdx);
						if (utilhdxperc.getThevalue().equals("0"))
							continue;
						Calendar tempCal = (Calendar) utilhdxperc.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "utilhdxperc" + allipstr;

						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "'," + time + ")";
						// System.out.println(sql);
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						// conn.executeUpdate(sql);
					}
				}
			}

			// UtilHdx
			Vector utilhdxVector = (Vector) datahash.get("utilhdx");
			if (utilhdxVector != null && utilhdxVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("utilhdx", utilhdxVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < utilhdxVector.size(); si++) {
					UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

					if (utilhdx.getRestype().equals("dynamic")) {
						// session.save(utilhdx);
						if (utilhdx.getThevalue().equals("0"))
							continue;
						Calendar tempCal = (Calendar) utilhdx.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "utilhdx" + allipstr;

						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "'," + time + ")";
						// System.out.println(sql);
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						// conn.executeUpdate(sql);
					}
				}
			}

			// packs
			Vector packsVector = (Vector) datahash.get("packs");
			if (packsVector != null && packsVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("packs", packsVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < packsVector.size(); si++) {
					Packs packs = (Packs) packsVector.elementAt(si);
					if (packs.getRestype().equals("dynamic")) {
						if (packs.getThevalue().equals("0"))
							continue;
						// session.save(packs);
						Calendar tempCal = (Calendar) packs.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "packs" + allipstr;
						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						// conn.executeUpdate(sql);
					}
				}
			}

			// inpacks
			Vector inpacksVector = (Vector) datahash.get("inpacks");
			if (inpacksVector != null && inpacksVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("inpacks", inpacksVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < inpacksVector.size(); si++) {
					InPkts packs = (InPkts) inpacksVector.elementAt(si);
					if (packs.getRestype().equals("dynamic")) {
						if (packs.getThevalue().equals("0"))
							continue;
						// session.save(packs);
						Calendar tempCal = (Calendar) packs.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "inpacks" + allipstr;
						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						// conn.executeUpdate(sql);
					}
				}
			}

			// inpacks
			Vector outpacksVector = (Vector) datahash.get("outpacks");
			if (outpacksVector != null && outpacksVector.size() > 0) {
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("outpacks", outpacksVector);
				ShareData.getSharedata().put(ip, ipAllData);
				for (int si = 0; si < outpacksVector.size(); si++) {
					OutPkts packs = (OutPkts) outpacksVector.elementAt(si);
					if (packs.getRestype().equals("dynamic")) {
						if (packs.getThevalue().equals("0"))
							continue;
						// session.save(packs);
						Calendar tempCal = (Calendar) packs.getCollecttime();
						Date cc = tempCal.getTime();
						String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
						String tablename = "outpacks" + allipstr;
						String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
						try {
							dbmanager.addBatch(sql);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						// conn.executeUpdate(sql);
					}
				}
			}

			// ipmac
			Vector ipmacVector = (Vector) datahash.get("ipmac");
			Hashtable ipmacs = ShareData.getRelateipmacdata();
			Hashtable ipmacband = ShareData.getIpmacbanddata();
			IpMacChangeDao macchangedao = new IpMacChangeDao();
			Hashtable macHash = null;
			try {
				macHash = macchangedao.loadMacIpHash();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				macchangedao.close();
			}

			if (ipmacVector != null && ipmacVector.size() > 0) {
				// 放入内存里
				ShareData.setRelateipmacdata(ip, ipmacVector);
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("ipmac", ipmacVector);
				ShareData.getSharedata().put(ip, ipAllData);
			}
			int firstipmac = ShareData.getFirstipmac();
			if (firstipmac == 0) {
				// 第一次启动
				// 设置变量值，并装载IP-MAC对照表
				ShareData.setFirstipmac(1);
				// 装载该IP的IP-MAC对应绑定关系表
				ResultSet rs = dbmanager.executeQuery("select * from nms_ipmacbase");
				List list = loadFromIpMacBandRS(rs);
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						IpMacBase _ipmacbase = (IpMacBase) list.get(i);
						if (ipmacband.containsKey(_ipmacbase.getMac())) {
							// 已经存在
							List existMacList = (List) ipmacband.get(_ipmacbase.getMac());
							existMacList.add(_ipmacbase);
							ipmacband.put(_ipmacbase.getMac(), existMacList);
						} else {
							List macList = new ArrayList();
							macList.add(_ipmacbase);
							ipmacband.put(_ipmacbase.getMac(), macList);
						}

					}
				}
				rs.close();

			}
			if (ipmacVector != null && ipmacVector.size() > 0) {
				// 把实时采集到的ARP表信息存放到共享数据里，然后再更新数据库
				ipmacs.put(ip, ipmacVector);
				String sql = "delete from ipmac where relateipaddr='" + ip + "'";
				try {
					dbmanager.addBatch(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String _localMAC = "";
				Hashtable _AllMAC = new Hashtable();
				Hashtable _sameMAC = new Hashtable();
				Vector tempVector = new Vector();
				for (int si = 0; si < ipmacVector.size(); si++) {
					IpMac ipmac = (IpMac) ipmacVector.elementAt(si);
					if (ipmac == null)
						continue;
					if (ipmac.getIpaddress() == null)
						continue;
					if (ipmac.getIpaddress().equals(ip)) {
						// 若是本机IP
						_localMAC = ipmac.getMac();
					}
					if (!_AllMAC.containsKey(ipmac.getMac())) {
						// 若不存在次IP
						_AllMAC.put(ipmac.getMac(), ipmac);
					} else {
						// 若存在

						tempVector.add(ipmac);
						// tempVector.add(_AllIp.get(ipmac.getIpaddress()));
						_sameMAC.put(ipmac.getMac(), tempVector);
					}
				}
				for (int si = 0; si < ipmacVector.size(); si++) {
					IpMac ipmac = (IpMac) ipmacVector.elementAt(si);
					try {
						if (ipmac != null && ipmac.getMac() != null && ipmac.getMac().length() == 17) {
							dbmanager.addBatch(ipmacInsertSQL(ipmac));
						}
						// 需要加是否对MAC地址进行审计的判断
						/*
						 * PingUtil pingU=new PingUtil(ipmac.getIpaddress());
						 * Integer[] packet=pingU.ping();
						 * //vector=pingU.addhis(packet);
						 * SysLogger.info(ipmac.getIpaddress()+"
						 * pack====="+packet[0]); //把存活的MAC地址存入数据库nms_machistory
						 * if(packet[0]!=null){ if(packet[0] >0){
						 * //连通的MAC,将当前的MAC存入NMS_MACHISTORY表 MacHistory machis =
						 * new MacHistory();
						 * machis.setRelateipaddr(ipmac.getRelateipaddr());
						 * machis.setBak(ipmac.getBak());
						 * machis.setCollecttime(ipmac.getCollecttime());
						 * machis.setIfindex(ipmac.getIfindex());
						 * machis.setIpaddress(ipmac.getIpaddress());
						 * machis.setMac(ipmac.getMac());
						 * machis.setThevalue("1");
						 * dbmanager.executeUpdate(machistoryInsertSQL(machis));
						 * }else{ //不在线的MAC }
						 * //hostdata.setThevalue(packet[0].toString()); } else{
						 * //不通的MAC //hostdata.setThevalue("0"); //continue; }
						 */
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// session.save(ipmac);
					if (ipmac == null)
						continue;
					if (ipmac.getMac() == null)
						continue;
					if (ipmac.getMac().equalsIgnoreCase(_localMAC)) {
						// 若是本机MAC,IP不是本机，则过滤
						if (!ipmac.getIpaddress().equals(ip))
							continue;
					}
					// 同时更新IP-MAC绑定表，若IP-MAC地址变更，则采用新的IP-MAC信息，同时保存到历史变更表里
					if (ipmacband.containsKey(ipmac.getMac())) {
						List existMacList = (List) ipmacband.get(ipmac.getMac());
						if (existMacList != null && existMacList.size() > 0) {
							int changeflag = 0;
							int samenodeflag = 0;
							String ipstr = "";
							IpMacBase changeMacBase = null;
							for (int i = 0; i < existMacList.size(); i++) {
								IpMacBase temp_ipmacband = (IpMacBase) existMacList.get(i);
								ipstr = ipstr + "或" + temp_ipmacband.getRelateipaddr();
								if (temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())) {
									changeflag = 1;
								}
							}
							if (changeflag == 0) {
								// 没有相等的IP,判断从哪个设备上路过
								for (int i = 0; i < existMacList.size(); i++) {
									IpMacBase temp_ipmacband = (IpMacBase) existMacList.get(i);
									if (temp_ipmacband.getRelateipaddr().equalsIgnoreCase(ipmac.getRelateipaddr())) {
										samenodeflag = 1;
										changeMacBase = temp_ipmacband;
										break;
									}
								}
							}
							// 若存在此MAC，则判断IP是否相等
							// IpMacBase temp_ipmacband =
							// (IpMacBase)ipmacband.get(ipmac.getMac());

							// if(!temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())){
							if (changeMacBase != null) {
								// 若IP不相等，而且找到设备上路过,则用新的信息修改该IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

								String _ipaddress = changeMacBase.getIpaddress();
								changeMacBase.setIpaddress(ipmac.getIpaddress());
								// ShareData.setIpmacbanddata(ipmac.getMac(),temp_ipmacband);
								existMacList = (List) ipmacband.get(changeMacBase.getMac());
								existMacList.add(changeMacBase);
								ipmacband.put(changeMacBase.getMac(), existMacList);
								try {
									dbmanager.addBatch(ipmacbandUpdateSQL(changeMacBase));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (_sameMAC.containsKey(ipmac.getMac())) {
									if (changeMacBase.getIfband() == 0)
										continue;
								}

								// 在历史表里追加一条新加入的提示信息
								IpMacChange _ipmacchange = new IpMacChange();
								_ipmacchange.setMac(ipmac.getMac());
								_ipmacchange.setChangetype("2");
								_ipmacchange.setDetail("该MAC地址从IP：" + _ipaddress + " 变换到IP：" + ipmac.getIpaddress());
								_ipmacchange.setIpaddress(ipmac.getIpaddress());
								_ipmacchange.setCollecttime(ipmac.getCollecttime());
								// 对发生IP变更的要进行PING操作，若能PING通，则进行下一不操作

								Vector vector = null;
								PingUtil pingU = new PingUtil(ipmac.getIpaddress());
								Integer[] packet = pingU.ping();
								// vector=pingU.addhis(packet);
								if (packet[0] != null) {
									// hostdata.setThevalue(packet[0].toString());
								} else {
									// hostdata.setThevalue("0");
									continue;
								}

								// 若IP-MAC已经绑定，则产生事件
								if (changeMacBase.getIfband() == 1) {
									try {
										dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// session.save(_ipmacchange);
									// 写事件
									/*
									 * I_Eventlist eventmanager=new
									 * EventlistManager(); Eventlist event = new
									 * Eventlist();
									 * event.setEventtype("network");
									 * //事件类型需要重新定义：网络、主机、数据库、WEB服务、FTP服务、安全产品等
									 * event
									 * .setEventlocation(temp_ipmacband.getIpaddress
									 * ()); event.setManagesign(new Integer(0));
									 * event.setReportman("monitorpc"); String
									 * time =
									 * sdf.format(ipmac.getCollecttime().getTime
									 * ());
									 * event.setRecordtime(ipmac.getCollecttime
									 * ());
									 * event.setContent(time+"&IP地址更新&"+_ipaddress
									 * +"&该MAC地址从IP："+_ipaddress+"
									 * 变换到IP："+ipmac.getIpaddress());
									 * event.setLevel1(new Integer(2)); Vector
									 * eventtmpV = new Vector();
									 * eventtmpV.add(event);
									 * eventmanager.createEventlist(eventtmpV);
									 */
								}
							} else if (changeflag == 0) {
								// 若IP不相等，但没找到相同的设备上路过,说明该MAC经过的设备发生了改变,则追加新的IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

								IpMacBase _ipmacband = new IpMacBase();
								_ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
								_ipmacband.setIfindex(ipmac.getIfindex());
								_ipmacband.setIpaddress(ipmac.getIpaddress());
								_ipmacband.setMac(ipmac.getMac());
								_ipmacband.setCollecttime(ipmac.getCollecttime());
								_ipmacband.setIfband(0);
								_ipmacband.setIfsms("0");
								_ipmacband.setEmployee_id(new Integer(0));
								_ipmacband.setRelateipaddr(ip);
								ipmacband.put(ipmac.getMac(), _ipmacband);
								try {
									dbmanager.addBatch(ipmacbaseInsertSQL(_ipmacband));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
									continue;
								// 在历史表里追加一条新加入的提示信息
								IpMacChange _ipmacchange = new IpMacChange();
								_ipmacchange.setMac(ipmac.getMac());
								_ipmacchange.setChangetype("1");
								_ipmacchange.setDetail("该MAC地址从设备" + ipstr + " 变换到设备" + ipmac.getRelateipaddr() + "上,IP：" + ipmac.getIpaddress());
								_ipmacchange.setIpaddress(ipmac.getIpaddress());
								_ipmacchange.setCollecttime(ipmac.getCollecttime());
								_ipmacchange.setRelateipaddr(ip);
								_ipmacchange.setIfindex(ipmac.getIfindex());

								try {
									dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
									// session.save(_ipmacband);
								} catch (Exception ex) {
									ex.printStackTrace();
								}

							}
						}
					} else {
						// 若不存在次MAC，则为新加入的机器，则保存该IP-MAC，同时在历史表里追加一条新加入的提示信息
						/*
						 * IpMacBase _ipmacband = new IpMacBase();
						 * _ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
						 * _ipmacband.setIfindex(ipmac.getIfindex());
						 * _ipmacband.setIpaddress(ipmac.getIpaddress());
						 * _ipmacband.setMac(ipmac.getMac());
						 * _ipmacband.setCollecttime(ipmac.getCollecttime());
						 * _ipmacband.setIfband(0); _ipmacband.setIfsms("0");
						 * _ipmacband.setEmployee_id(new Integer(0));
						 * _ipmacband.setRelateipaddr(ip);
						 * ipmacband.put(ipmac.getMac(),_ipmacband); try{
						 * dbmanager
						 * .executeUpdate(ipmacbandInsertSQL(_ipmacband));
						 * //session.save(_ipmacband); }catch(Exception ex){
						 * ex.printStackTrace(); }
						 */
						// 判断变更表里是否已经有该MAC地址
						if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
							continue;
						// 在历史表里追加一条新加入的提示信息
						IpMacChange _ipmacchange = new IpMacChange();
						_ipmacchange.setMac(ipmac.getMac());
						_ipmacchange.setChangetype("1");
						_ipmacchange.setDetail("新增加的IP-MAC");
						_ipmacchange.setIpaddress(ipmac.getIpaddress());
						_ipmacchange.setCollecttime(ipmac.getCollecttime());
						_ipmacchange.setRelateipaddr(ip);
						_ipmacchange.setIfindex(ipmac.getIfindex());

						try {
							if (ipmac.getMac().length() == 17) {
								dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
							}
							// session.save(_ipmacband);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// session.save(_ipmacchange);
					}
				}
			}

			// iprouter
			Vector iprouterVector = (Vector) datahash.get("iprouter");
			if (iprouterVector != null && iprouterVector.size() > 0) {
				// 放入内存里
				ShareData.setIprouterdata(ip, iprouterVector);
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("iprouter", iprouterVector);
				ShareData.getSharedata().put(ip, ipAllData);
			}
			if (iprouterVector != null && iprouterVector.size() > 0) {
				String sql = "delete from iprouter where relateipaddr='" + ip + "'";
				// dbmanager.executeUpdate(sql);
				for (int si = 0; si < iprouterVector.size(); si++) {
					IpRouter iprouter = (IpRouter) iprouterVector.elementAt(si);
					try {
						// session.save(iprouter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// AIX 的 errpt 信息
			Vector errptVector = (Vector) datahash.get("errptlog");
			if (errptVector != null && errptVector.size() > 0) {
				// 放入内存里
				ErrptlogDao errptlogDao = new ErrptlogDao();
				try {
					for (int i = 0; i < errptVector.size(); i++) {
						Errptlog errptlog = (Errptlog) errptVector.get(i);
						errptlogDao.save(errptlog);
						errptlogDao = new ErrptlogDao();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					errptlogDao.close();
				}
			}

			// AIX 的 换页率 信息
			Hashtable paginghash = (Hashtable) datahash.get("paginghash");
			if (paginghash != null && paginghash.size() > 0) {
				if (paginghash.get("Percent_Used") != null) {
					String pused = ((String) paginghash.get("Percent_Used")).replaceAll("%", "");
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
					String tablename = "pgused" + allipstr;
					String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','','','','','','','',0,'" + pused + "'," + time + ")";
					try {
						// SysLogger.info(sql);
						dbmanager.addBatch(sql);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// policydata
			Hashtable policyHash = (Hashtable) datahash.get("policys");
			if (policyHash != null && policyHash.size() > 0) {
				// 放入内存里
				ShareData.setPolicydata(ip, policyHash);
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("policys", policyHash);
				ShareData.getSharedata().put(ip, ipAllData);
			}

			// 系统信息
			Vector sysVector = (Vector) datahash.get("system");
			if (sysVector != null && sysVector.size() > 0) {
				// 放入内存里
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("system", sysVector);
				ShareData.getSharedata().put(ip, ipAllData);
			}

			// 硬件信息
			Vector deviceVector = (Vector) datahash.get("device");
			if (deviceVector != null && deviceVector.size() > 0) {
				// 放入内存里
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("device", deviceVector);
				ShareData.getSharedata().put(ip, ipAllData);
			}

			// 硬件信息
			Vector storageVector = (Vector) datahash.get("storage");
			if (storageVector != null && storageVector.size() > 0) {
				// 放入内存里
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
				if (ipAllData == null)
					ipAllData = new Hashtable();
				ipAllData.put("storage", storageVector);
				ShareData.getSharedata().put(ip, ipAllData);
			}

			/*
			 * 
			 * //tosdata List tosrouteList = (List)datahash.get("tosroute"); if
			 * (tosrouteList != null && tosrouteList.size()>0){ //放入内存里
			 * ShareData.setTosroutedata(ip, tosrouteList); }
			 * 
			 * //dominodata Hashtable dominoHash =
			 * (Hashtable)datahash.get("dominohash"); if (dominoHash != null &&
			 * dominoHash.size()>0){ //放入内存里
			 * ShareData.setDominodata(ip,dominoHash); }
			 * 
			 * //Memory DominoMem dominoMem = new DominoMem(); if (dominoHash ==
			 * null)dominoHash=new Hashtable(); if(dominoHash.get("Mem") !=
			 * null) dominoMem = (DominoMem)dominoHash.get("Mem");
			 * 
			 * //Vector memoryVector = (Vector)datahash.get("memory"); if
			 * (dominoMem != null){ Calendar tempCal = Calendar.getInstance();
			 * Date cc = tempCal.getTime(); String
			 * time=util.coversionTimeSql(sdf.format(cc)); String tablename =
			 * "domi"+allipstr; //已使用内存 String sql = "insert into
			 * "+tablename+"(ipaddress
			 * ,restype,category,entity,subentity,unit,chname
			 * ,bak,count,thevalue,collecttime) "
			 * +"values('"+ip+"','dynamic','Memory','Utilization','"
			 * +"Allocate','M','','',"
			 * +"0,'"+dominoMem.getMemAllocate()+"',to_date('"
			 * +time+"','YYYY-MM-DD HH24:MI:SS'))"; //System.out.println(sql);
			 * //stmt = con.prepareStatement(sql); //stmt.execute();
			 * //stmt.close(); //物理内存 sql = "insert into
			 * "+tablename+"(ipaddress,
			 * restype,category,entity,subentity,unit,chname
			 * ,bak,count,thevalue,collecttime) "
			 * +"values('"+ip+"','dynamic','Memory','Utilization','"
			 * +"Physical','M','','',"
			 * +"0,'"+dominoMem.getMemPhysical()+"',to_date('"
			 * +time+"','YYYY-MM-DD HH24:MI:SS'))"; //System.out.println(sql);
			 * //stmt = con.prepareStatement(sql); //stmt.execute();
			 * //stmt.close(); } this.endTransaction(true); session
			 * =super.beginTransaction();
			 * 
			 * 
			 * //}
			 */
			/*
			 * Hashtable pingData = ShareData.getPingdata(); Vector tempV = new
			 * Vector(); if (pingData != null && pingData.size()>0){ tempV =
			 * (Vector)pingData.get(ip); if (tempV != null && tempV.size()>0){
			 * for(int i=0;i<tempV.size();i++){ Pingcollectdata pingdata =
			 * (Pingcollectdata) tempV.elementAt(i); } } } //session.flush();
			 * this.endTransaction(true);
			 */
			/*
			 * //检查进程 if (proArray != null) { for (int procount = 0; procount <
			 * proArray.length; procount++) { String proname =
			 * proArray[procount]; Otherservicedata otherdata = new
			 * Otherservicedata();
			 * otherdata.setIpaddress(prodata.getIpaddress());
			 * otherdata.setCategory("ProcessTest");
			 * otherdata.setServicename(proname);
			 * otherdata.setCollecttime(prodata.getCollecttime()); if
			 * (probool[procount]) otherdata.setIs_valid(new Integer(1)); if
			 * (!probool[procount]) otherdata.setIs_valid(new Integer(0));
			 * v.add(otherdata); } I_Otherservicedata servicedataManager = new
			 * OtherservicedataManager();
			 * servicedataManager.createServiceData(v); }
			 */
			try {
				dbmanager.executeBatch();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dbmanager.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
			datahash = null;
			System.gc();
		}

		return true;
	}

	public synchronized boolean processHostProcData(String tablename, String ip, Vector proVector) {
		if (proVector != null && proVector.size() > 0) {
			// PreparedStatement ps = null;
			// Connection conn=null;
			DBManager dbmanager = new DBManager();
			List list = new ArrayList();
			try {
				// conn = DataGate.getCon();
				String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values(?,?,?,?,?,?,?,?,?,?,?)";
				dbmanager.setPrepareSql(sql);
				// ps = conn.prepareStatement(sql);
				for (int i = 0; i < proVector.size(); i++) {
					ProcessCollectEntity processdata = (ProcessCollectEntity) proVector.elementAt(i);
					processdata.setCount(1L);
					if (processdata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) processdata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						list = new ArrayList();
						list.add(ip);
						list.add(processdata.getRestype());
						list.add(processdata.getCategory());
						list.add(processdata.getEntity());
						list.add(processdata.getSubentity());
						list.add(processdata.getUnit());
						list.add(processdata.getChname());
						list.add(processdata.getBak());
						list.add(processdata.getCount());
						list.add(processdata.getThevalue());
						list.add(time);
						dbmanager.addPrepareProcLongBatch(list);
						list = null;

						// ps.setString(1, ip);
						// ps.setString(2, processdata.getRestype());
						// ps.setString(3, processdata.getCategory());
						// ps.setString(4, processdata.getEntity());
						// ps.setString(5, processdata.getSubentity());
						// ps.setString(6, processdata.getUnit());
						// ps.setString(7, processdata.getChname());
						// ps.setString(8, processdata.getBak());
						// ps.setLong(9, processdata.getCount());
						// ps.setString(10, processdata.getThevalue());
						// ps.setString(11, time);
						// ps.addBatch();
					}
				}
				try {
					dbmanager.executePreparedBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbmanager != null) {
					try {
						dbmanager.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			dbmanager = null;

		}
		return true;
	}

	public synchronized boolean createHostItemData(Hashtable datahash, String type) {
		String runmodel = PollingEngine.getCollectwebflag();
		if (type.equalsIgnoreCase("host")) {
			if (datahash != null && datahash.size() > 0) {
				if ("1".equals(runmodel)) {
					// 采集与访问是分离模式
					Date startdate = new Date();
					// 实时数据入库处理
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.processHostData("host", datahash);
					} catch (Exception e) {

					}
				} else {
					// 采集与访问集成模式，主要是处理数据接口模式的数据
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.generateXML("host", datahash);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Date startdate1 = new Date();
				Enumeration iphash = datahash.keys();
				DBManager dbmanager = new DBManager();
				// TempDataService tempDataService = new TempDataService();
				try {
					CpuCollectEntity cpudata = null;
					Vector cpuVector = null;

					Vector pingVector = null;
					PingCollectEntity pingdata = null;

					Vector memoryVector = null;
					MemoryCollectEntity memorydata = null;

					Vector diskVector = null;
					DiskCollectEntity diskdata = null;

					Vector inpacksVector = null;
					InPkts packs = null;

					Vector outpacksVector = null;
					OutPkts outpacks = null;

					Vector allutilhdxVector = null;
					AllUtilHdx allutilhdx = null;

					Vector utilhdxpercVector = null;
					UtilHdxPerc utilhdxperc = null;

					Vector utilhdxVector = null;
					UtilHdx utilhdx = null;

					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = null;

					while (iphash.hasMoreElements()) {
						String ip = (String) iphash.nextElement();
						String allipstr = SysUtil.doip(ip);
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						Hashtable ipdata = (Hashtable) datahash.get(ip);
						if (ipdata != null) {
							// 处理主机设备的数据
							if (ipdata.containsKey("cpu")) {
								// CPU
								Hashtable cpuhash = (Hashtable) ipdata.get("cpu");
								cpuVector = (Vector) cpuhash.get("cpu");
								cpuhash = null;
								if (cpuVector != null && cpuVector.size() > 0) {
									// 得到CPU平均
									cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
									if (cpudata.getRestype().equals("dynamic")) {
										Calendar tempCal = (Calendar) cpudata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										String tablename = "cpu" + allipstr;
										String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "'," + time + ")";
										// System.out.println(sql);
										try {
											dbmanager.addBatch(sql);
										} catch (Exception ex) {
											ex.printStackTrace();
										} finally {
											// dbmanager.close();
										}
										// conn.executeUpdate(sql);
									}
								}
								cpuhash = null;
								cpuVector = null;
							}

							if (ipdata.containsKey("ping")) {
								// //ping
								// Hashtable pinghash =
								// (Hashtable)ipdata.get("ping");
								// pingVector = (Vector) pinghash.get("ping");
								// pinghash = null;
								// if (pingVector != null && pingVector.size() >
								// 0) {
								// for(int i=0;i<pingVector.size();i++){
								// pingdata = (Pingcollectdata)
								// pingVector.elementAt(i);
								// if (pingdata.getRestype().equals("dynamic"))
								// {
								// Calendar tempCal = (Calendar)
								// pingdata.getCollecttime();
								// Date cc = tempCal.getTime();
								// String
								// time=util.coversionTimeSql(sdf.format(cc));
								// String tablename = "ping" + allipstr;
								// String sql = "insert into " + tablename
								// +
								// "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime)
								// "
								// + "values('" + ip + "','" +
								// pingdata.getRestype() + "','" +
								// pingdata.getCategory() + "','"
								// + pingdata.getEntity() + "','" +
								// pingdata.getSubentity() + "','" +
								// pingdata.getUnit() + "','"
								// + pingdata.getChname() + "','" +
								// pingdata.getBak() + "'," +
								// pingdata.getCount() + ",'"
								// + pingdata.getThevalue() + "','" + time +
								// "')";
								// //System.out.println(sql);
								// try {
								// dbmanager.addBatch(sql);
								// } catch (Exception ex) {
								// ex.printStackTrace();
								// }
								// }
								// pingdata = null;
								// }
								//
								// }
								// pingVector = null;
							}

							if (ipdata.containsKey("process")) {
								// Process
								Hashtable processhash = (Hashtable) ipdata.get("process");
								Vector proVector = (Vector) processhash.get("process");
								processHostProcData("pro" + allipstr, ip, proVector);
								proVector = null;
							}
							if (ipdata.containsKey("physicalmemory")) {
								// physicalmemory
								Hashtable physicalmemoryhash = (Hashtable) ipdata.get("physicalmemory");
								memoryVector = (Vector) physicalmemoryhash.get("memory");
								physicalmemoryhash = null;
								if (memoryVector != null && memoryVector.size() > 0) {
									for (int si = 0; si < memoryVector.size(); si++) {
										memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
										if (!memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory"))
											continue;
										if (memorydata.getRestype().equals("dynamic")) {
											Calendar tempCal = (Calendar) memorydata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "memory" + allipstr;
											// if
											// (memorydata.getIpaddress().equals("10.217.255.253")||memorydata.getIpaddress().equals("10.217.255.64")||memorydata.getIpaddress().equals("10.216.2.35")){
											String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}

								}
								memoryVector = null;
							}
							if (ipdata.containsKey("virtualmemory")) {
								// virtualmemory
								Hashtable virtualmemoryhash = (Hashtable) ipdata.get("virtualmemory");
								memoryVector = (Vector) virtualmemoryhash.get("memory");
								virtualmemoryhash = null;
								if (memoryVector != null && memoryVector.size() > 0) {
									for (int si = 0; si < memoryVector.size(); si++) {
										memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
										if (!memorydata.getSubentity().equalsIgnoreCase("VirtualMemory"))
											continue;
										if (memorydata.getRestype().equals("dynamic")) {
											Calendar tempCal = (Calendar) memorydata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "memory" + allipstr;
											// if
											// (memorydata.getIpaddress().equals("10.217.255.253")||memorydata.getIpaddress().equals("10.217.255.64")||memorydata.getIpaddress().equals("10.216.2.35")){
											String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}
									memorydata = null;

								}
							}
							if (ipdata.containsKey("disk")) {
								// disk
								Hashtable diskhash = (Hashtable) ipdata.get("disk");
								diskVector = (Vector) diskhash.get("disk");
								diskhash = null;
								if (diskVector != null && diskVector.size() > 0) {
									for (int si = 0; si < diskVector.size(); si++) {
										diskdata = (DiskCollectEntity) diskVector.elementAt(si);
										if (diskdata.getRestype().equals("dynamic")) {
											Calendar tempCal = (Calendar) diskdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "diskincre" + allipstr;
											String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
										if (diskdata.getEntity().equals("Utilization")) {
											Calendar tempCal = (Calendar) diskdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "disk" + allipstr;
											String sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "'," + time + ",'" + diskdata.getUnit() + "')";
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
										diskdata = null;
									}
								}
								diskVector = null;
							}
							if (ipdata.containsKey("packs")) {
								// inpacks
								Hashtable packshash = (Hashtable) ipdata.get("packs");
								inpacksVector = (Vector) packshash.get("inpacks");
								packshash = null;
								if (inpacksVector != null && inpacksVector.size() > 0) {
									for (int si = 0; si < inpacksVector.size(); si++) {
										packs = (InPkts) inpacksVector.elementAt(si);
										if (packs.getRestype().equals("dynamic")) {
											if (packs.getThevalue().equals("0"))
												continue;
											// session.save(packs);
											Calendar tempCal = (Calendar) packs.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "inpacks" + allipstr;
											String sql = "insert into " + tablename + "(id,ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values(inpacks_" + allipstr + "_seq.nextval,'" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
											try {
												dbmanager.executeUpdate(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											// conn.executeUpdate(sql);
										}
										packs = null;
									}
								}
								inpacksVector = null;

								// inpacks
								outpacksVector = (Vector) packshash.get("outpacks");
								if (outpacksVector != null && outpacksVector.size() > 0) {
									for (int si = 0; si < outpacksVector.size(); si++) {
										outpacks = (OutPkts) outpacksVector.elementAt(si);
										if (outpacks.getRestype().equals("dynamic")) {
											if (outpacks.getThevalue().equals("0"))
												continue;
											// session.save(packs);
											Calendar tempCal = (Calendar) packs.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "outpacks" + allipstr;
											String sql = "insert into " + tablename + "(id,ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values(outpacks_" + allipstr + "_seq.nextval,'" + ip + "','" + outpacks.getRestype() + "','" + outpacks.getCategory() + "','" + outpacks.getEntity() + "','" + outpacks.getSubentity() + "','" + outpacks.getUnit() + "','" + outpacks.getChname() + "','" + outpacks.getBak() + "'," + outpacks.getCount() + ",'" + outpacks.getThevalue() + "'," + time + ")";
											try {
												dbmanager.executeUpdate(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
										outpacks = null;
									}
								}
								outpacksVector = null;
							}
							if (ipdata.containsKey("interface")) {
								// interface
								Hashtable interfacehash = (Hashtable) ipdata.get("interface");
								ProcessNetData processnetdata = new ProcessNetData();
								try {
									processnetdata.processInterfaceData(nodeDTO.getId() + "", ip, type, nodeDTO.getSubtype(), interfacehash);
								} catch (Exception e) {
									e.printStackTrace();
								}
								processnetdata = null;
								// 综合流速
								allutilhdxVector = (Vector) interfacehash.get("allutilhdx");
								if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
									for (int si = 0; si < allutilhdxVector.size(); si++) {
										allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);

										if (allutilhdx.getRestype().equals("dynamic")) {
											if (allutilhdx.getThevalue().equals("0"))
												continue;
											Calendar tempCal = (Calendar) allutilhdx.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "allutilhdx" + allipstr;

											String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "'," + time + ")";
											// SysLogger.info(sql);
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
										allutilhdx = null;
									}

								}
								allutilhdxVector = null;

								// UtilHdxPerc
								utilhdxpercVector = (Vector) interfacehash.get("utilhdxperc");
								if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
									for (int si = 0; si < utilhdxpercVector.size(); si++) {
										utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);

										if (utilhdxperc.getRestype().equals("dynamic")) {
											// session.save(utilhdx);
											if (utilhdxperc.getThevalue().equals("0") || utilhdxperc.getThevalue().equals("0.0"))
												continue;
											Calendar tempCal = (Calendar) utilhdxperc.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "utilhdxperc" + allipstr;

											String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "'," + time + ")";
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											// conn.executeUpdate(sql);
										}
										utilhdxperc = null;
									}
								}
								utilhdxpercVector = null;

								// UtilHdx
								utilhdxVector = (Vector) interfacehash.get("utilhdx");
								if (utilhdxVector != null && utilhdxVector.size() > 0) {
									for (int si = 0; si < utilhdxVector.size(); si++) {
										utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

										if (utilhdx.getRestype().equals("dynamic")) {
											// session.save(utilhdx);
											if (utilhdx.getThevalue().equals("0"))
												continue;
											Calendar tempCal = (Calendar) utilhdx.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "utilhdx" + allipstr;

											String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "'," + time + ")";

											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											// conn.executeUpdate(sql);
										}
										utilhdx = null;
									}
								}
								utilhdxVector = null;

								interfacehash = null;
							}
						}
						ipdata = null;
						nodeDTO = null;
					}
					nodeUtil = null;

				} catch (Exception e) {

				} finally {
					try {
						dbmanager.executeBatch();

						dbmanager.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					dbmanager.close();
				}
			}
		} else if (type.equalsIgnoreCase("net")) {

			IpMacChangeDao macchangedao = new IpMacChangeDao();
			Hashtable macHash = null;
			try {
				macHash = macchangedao.loadMacIpHash();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				macchangedao.close();
			}
			if (datahash != null && datahash.size() > 0) {
				if ("1".equals(runmodel)) {
					// 采集与访问是分离模式
					Date startdate = new Date();
					// 实时数据入库处理
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.processNetData("net", datahash);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Date enddate = new Date();
					// SysLogger.info(enddate.getTime()-startdate.getTime()+"=========");
				} else {
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.generateXML("net", datahash);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// if (datahash!=null) {
				// System.out.println("111111111111111111111111111111111111111111111");
				// Hashtable cpu=null;
				// if(datahash.containsKey("10.10.1.3")){
				// Hashtable cpu2=(Hashtable) datahash.get("10.10.1.3");
				// if(cpu2.containsKey("cpu")){
				// cpu=(Hashtable) cpu2.get("cpu");
				// }
				// Vector ve=(Vector)cpu.get("cpu");
				// CPUcollectdata dataUcollectdata=(CPUcollectdata)ve.get(0);
				// System.out.println("///"+dataUcollectdata.getThevalue()+"
				// {{【"+datahash.size());
				// }
				// }
				// 获取端口配置
				Hashtable portconfigHash = ShareData.getPortConfigHash();
				Iterator ipIterator = datahash.keySet().iterator();
				DBManager dbmanager = new DBManager();
				try {
					CpuCollectEntity cpudata = null;
					Hashtable cpuhash = null;
					Vector cpuVector = null;
					StringBuffer sBuffer = null;

					Hashtable memoryhash = null;
					Vector memoryVector = null;
					MemoryCollectEntity memorydata = null;

					Hashtable flashhash = null;
					Vector flashVector = null;
					FlashCollectEntity flashdata = null;

					Hashtable temphash = null;
					Vector temperatureVector = null;
					Interfacecollectdata temperdata = null;

					Hashtable fanhash = null;
					Vector fanVector = null;
					Interfacecollectdata fandata = null;

					Hashtable powerhash = null;
					Vector powerVector = null;
					Interfacecollectdata powerdata = null;

					Hashtable voltagehash = null;
					Vector voltageVector = null;
					// Interfacecollectdata powerdata = null;

					Hashtable bufferhash = null;
					Vector bufferVector = null;
					Buffercollectdata bufferdata = null;

					Hashtable iprouterhash = null;
					Vector iprouterVector = null;
					IpRouter iprouter = null;

					Hashtable fdbhash = null;
					Vector fdbVector = null;
					IpMac ipmac = null;

					Hashtable pinghash = null;
					Vector pingVector = null;
					PingCollectEntity pingdata = null;

					while (ipIterator.hasNext()) {
						String ip = (String) ipIterator.next();
						String allipstr = SysUtil.doip(ip);

						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						if (host == null)
							continue;
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);

						Hashtable ipdata = (Hashtable) datahash.get(ip);
						if (ipdata != null) {
							// 处理网络设备的数据
							if (ipdata.containsKey("cpu")) {
								// CPU
								// System.out.println("////////////////////////////");
								cpuhash = (Hashtable) ipdata.get("cpu");
								cpuVector = (Vector) cpuhash.get("cpu");
								if (cpuVector != null && cpuVector.size() > 0) {
									// 得到CPU平均
									cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
									if (cpudata.getRestype().equals("dynamic")) {
										Calendar tempCal = (Calendar) cpudata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										String tablename = "cpu" + allipstr;
										long count = 0;
										if (cpudata.getCount() != null) {
											count = cpudata.getCount();
										}
										sBuffer = new StringBuffer();
										sBuffer.append("insert into ");
										sBuffer.append(tablename);
										sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
										sBuffer.append("values('");
										sBuffer.append(ip);
										sBuffer.append("','");
										sBuffer.append(cpudata.getRestype());
										sBuffer.append("','");
										sBuffer.append(cpudata.getCategory());
										sBuffer.append("','");
										sBuffer.append(cpudata.getEntity());
										sBuffer.append("','");
										sBuffer.append(cpudata.getSubentity());
										sBuffer.append("','");
										sBuffer.append(cpudata.getUnit());
										sBuffer.append("','");
										sBuffer.append(cpudata.getChname());
										sBuffer.append("','");
										sBuffer.append(cpudata.getBak());
										sBuffer.append("','");
										sBuffer.append(count);
										sBuffer.append("','");
										sBuffer.append(cpudata.getThevalue());
										sBuffer.append("',");
										sBuffer.append(time);
										sBuffer.append(")");
										try {
											// SysLogger.info("======================"+sBuffer.toString());
											dbmanager.addBatch(sBuffer.toString());
											// dbmanager.executeUpdate(sBuffer.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										tablename = null;
									}
									cpudata = null;
								}
								cpuhash = null;
								cpuVector = null;
							}

							if (ipdata.containsKey("memory")) {
								memoryhash = (Hashtable) ipdata.get("memory");
								memoryVector = (Vector) memoryhash.get("memory");
								if (memoryVector != null && memoryVector.size() > 0) {
									String tablename = "memory" + allipstr;
									for (int si = 0; si < memoryVector.size(); si++) {
										memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
										if (memorydata.getRestype().equals("dynamic")) {
											Calendar tempCal = (Calendar) memorydata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));

											long count = 0;
											if (memorydata.getCount() != null) {
												count = memorydata.getCount();
											}
											StringBuffer sqlBuffer = new StringBuffer();
											sqlBuffer.append("insert into ");
											sqlBuffer.append(tablename);
											sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
											sqlBuffer.append("values('");
											sqlBuffer.append(ip);
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getRestype());
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getCategory());
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getEntity());
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getSubentity());
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getUnit());
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getChname());
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getBak());
											sqlBuffer.append("','");
											sqlBuffer.append(count);
											sqlBuffer.append("','");
											sqlBuffer.append(memorydata.getThevalue());
											sqlBuffer.append("',");
											sqlBuffer.append(time);
											sqlBuffer.append(")");
											try {
												dbmanager.addBatch(sqlBuffer.toString());
												// dbmanager.executeUpdate(sqlBuffer.toString());
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
										memorydata = null;
									}
									tablename = null;
								}
								memoryhash = null;
								memoryVector = null;
							}
							if (ipdata.containsKey("flash")) {
								// 闪存
								flashhash = (Hashtable) ipdata.get("flash");
								flashVector = (Vector) flashhash.get("flash");
								if (flashVector != null && flashVector.size() > 0) {
									flashdata = (FlashCollectEntity) flashVector.elementAt(0);
									if (flashdata.getRestype().equals("dynamic")) {
										Calendar tempCal = (Calendar) flashdata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										String tablename = "flash" + allipstr;
										long count = 0;
										if (flashdata.getCount() != null) {
											count = flashdata.getCount();
										}
										sBuffer = new StringBuffer();
										sBuffer.append("insert into ");
										sBuffer.append(tablename);
										sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
										sBuffer.append("values('");
										sBuffer.append(ip);
										sBuffer.append("','");
										sBuffer.append(flashdata.getRestype());
										sBuffer.append("','");
										sBuffer.append(flashdata.getCategory());
										sBuffer.append("','");
										sBuffer.append(flashdata.getEntity());
										sBuffer.append("','");
										sBuffer.append(flashdata.getSubentity());
										sBuffer.append("','");
										sBuffer.append(flashdata.getUnit());
										sBuffer.append("','");
										sBuffer.append(flashdata.getChname());
										sBuffer.append("','");
										sBuffer.append(flashdata.getBak());
										sBuffer.append("','");
										sBuffer.append(count);
										sBuffer.append("','");
										sBuffer.append(flashdata.getThevalue());
										sBuffer.append("',");
										sBuffer.append(time);
										sBuffer.append(")");
										// System.out.println(sql);
										try {
											dbmanager.addBatch(sBuffer.toString());
											// dbmanager.executeUpdate(sBuffer.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
									}
								}
								flashhash = null;
								flashVector = null;
							}

							if (ipdata.containsKey("temperature")) {
								// 温度
								temphash = (Hashtable) ipdata.get("temperature");
								temperatureVector = (Vector) temphash.get("temperature");
								if (temperatureVector != null && temperatureVector.size() > 0) {
									// 得到温度
									temperdata = (Interfacecollectdata) temperatureVector.elementAt(0);
									if (temperdata.getRestype().equals("dynamic")) {
										Calendar tempCal = (Calendar) temperdata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										String tablename = "temper" + allipstr;
										long count = 0;
										if (temperdata.getCount() != null) {
											count = temperdata.getCount();
										}
										sBuffer = new StringBuffer();
										sBuffer.append("insert into ");
										sBuffer.append(tablename);
										sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
										sBuffer.append("values('");
										sBuffer.append(ip);
										sBuffer.append("','");
										sBuffer.append(temperdata.getRestype());
										sBuffer.append("','");
										sBuffer.append(temperdata.getCategory());
										sBuffer.append("','");
										sBuffer.append(temperdata.getEntity());
										sBuffer.append("','");
										sBuffer.append(temperdata.getSubentity());
										sBuffer.append("','");
										sBuffer.append(temperdata.getUnit());
										sBuffer.append("','");
										sBuffer.append(temperdata.getChname());
										sBuffer.append("','");
										sBuffer.append(temperdata.getBak());
										sBuffer.append("','");
										sBuffer.append(count);
										sBuffer.append("','");
										sBuffer.append(temperdata.getThevalue());
										sBuffer.append("',");
										sBuffer.append(time);
										sBuffer.append(")");
										// System.out.println(sql);
										try {
											dbmanager.addBatch(sBuffer.toString());
											// dbmanager.executeUpdate(sBuffer.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
									}
									temperdata = null;
								}
								temphash = null;
								temperatureVector = null;
							}

							if (ipdata.containsKey("fan")) {
								// 风扇
								fanhash = (Hashtable) ipdata.get("fan");
								fanVector = (Vector) fanhash.get("fan");
								if (fanVector != null && fanVector.size() > 0) {
									// 得到风扇
									fandata = (Interfacecollectdata) fanVector.elementAt(0);
									if (fandata.getRestype().equals("dynamic")) {
										// session.save(cpudata);
										Calendar tempCal = (Calendar) fandata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										String tablename = "fan" + allipstr;
										long count = 0;
										if (fandata.getCount() != null) {
											count = fandata.getCount();
										}
										sBuffer = new StringBuffer();
										sBuffer.append("insert into ");
										sBuffer.append(tablename);
										sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
										sBuffer.append("values('");
										sBuffer.append(ip);
										sBuffer.append("','");
										sBuffer.append(fandata.getRestype());
										sBuffer.append("','");
										sBuffer.append(fandata.getCategory());
										sBuffer.append("','");
										sBuffer.append(fandata.getEntity());
										sBuffer.append("','");
										sBuffer.append(fandata.getSubentity());
										sBuffer.append("','");
										sBuffer.append(fandata.getUnit());
										sBuffer.append("','");
										sBuffer.append(fandata.getChname());
										sBuffer.append("','");
										sBuffer.append(fandata.getBak());
										sBuffer.append("','");
										sBuffer.append(count);
										sBuffer.append("','");
										sBuffer.append(fandata.getThevalue());
										sBuffer.append("',");
										sBuffer.append(time);
										sBuffer.append(")");
										// System.out.println(sql);
										try {
											dbmanager.addBatch(sBuffer.toString());
											// dbmanager.executeUpdate(sBuffer.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
									}
									fandata = null;
								}
								fanhash = null;
								fanVector = null;
							}

							if (ipdata.containsKey("power")) {
								// 电源
								// SysLogger.info("添加电源信息
								// ################"+allipstr);
								powerhash = (Hashtable) ipdata.get("power");
								powerVector = (Vector) powerhash.get("power");
								if (powerVector != null && powerVector.size() > 0) {
									for (int i = 0; i < powerVector.size(); i++) {
										powerdata = (Interfacecollectdata) powerVector.elementAt(i);
										if (powerdata.getRestype().equals("dynamic")) {
											// session.save(cpudata);
											Calendar tempCal = (Calendar) powerdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "power" + allipstr;
											long count = 0;
											if (powerdata.getCount() != null) {
												count = powerdata.getCount();
											}
											sBuffer = new StringBuffer();
											sBuffer.append("insert into ");
											sBuffer.append(tablename);
											sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
											sBuffer.append("values('");
											sBuffer.append(ip);
											sBuffer.append("','");
											sBuffer.append(powerdata.getRestype());
											sBuffer.append("','");
											sBuffer.append(powerdata.getCategory());
											sBuffer.append("','");
											sBuffer.append(powerdata.getEntity());
											sBuffer.append("','");
											sBuffer.append(powerdata.getSubentity());
											sBuffer.append("','");
											sBuffer.append(powerdata.getUnit());
											sBuffer.append("','");
											sBuffer.append(powerdata.getChname());
											sBuffer.append("','");
											sBuffer.append(powerdata.getBak());
											sBuffer.append("','");
											sBuffer.append(count);
											sBuffer.append("','");
											sBuffer.append(powerdata.getThevalue());
											sBuffer.append("',");
											sBuffer.append(time);
											sBuffer.append(")");
											// SysLogger.info(sql);
											try {
												dbmanager.addBatch(sBuffer.toString());
												// dbmanager.executeUpdate(sBuffer.toString());
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											sBuffer = null;
										}
										powerdata = null;
									}
								}
								powerhash = null;
								powerVector = null;
							}

							if (ipdata.containsKey("voltage")) {
								// 电源
								voltagehash = (Hashtable) ipdata.get("voltage");
								voltageVector = (Vector) voltagehash.get("voltage");
								if (voltageVector != null && voltageVector.size() > 0) {
									for (int i = 0; i < voltageVector.size(); i++) {
										String tablename = "vol" + allipstr;
										powerdata = (Interfacecollectdata) voltageVector.elementAt(i);
										if (powerdata.getRestype().equals("dynamic")) {
											Calendar tempCal = (Calendar) powerdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											long count = 0;
											if (powerdata.getCount() != null) {
												count = powerdata.getCount();
											}
											sBuffer = new StringBuffer();
											sBuffer.append("insert into ");
											sBuffer.append(tablename);
											sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
											sBuffer.append("values('");
											sBuffer.append(ip);
											sBuffer.append("','");
											sBuffer.append(powerdata.getRestype());
											sBuffer.append("','");
											sBuffer.append(powerdata.getCategory());
											sBuffer.append("','");
											sBuffer.append(powerdata.getEntity());
											sBuffer.append("','");
											sBuffer.append(powerdata.getSubentity());
											sBuffer.append("','");
											sBuffer.append(powerdata.getUnit());
											sBuffer.append("','");
											sBuffer.append(powerdata.getChname());
											sBuffer.append("','");
											sBuffer.append(powerdata.getBak());
											sBuffer.append("','");
											sBuffer.append(count);
											sBuffer.append("','");
											sBuffer.append(powerdata.getThevalue());
											sBuffer.append("',");
											sBuffer.append(time);
											sBuffer.append(")");
											// SysLogger.info(sql);
											try {
												dbmanager.addBatch(sBuffer.toString());
												// dbmanager.executeUpdate(sBuffer.toString());
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											sBuffer = null;
										}
										powerdata = null;
									}
								}
								voltagehash = null;
								voltageVector = null;
							}

							if (ipdata.containsKey("interface")) {

								Hashtable interfacehash = (Hashtable) ipdata.get("interface");
								// 将当前采集到的接口数据入库
								ProcessNetData processnetdata = new ProcessNetData();
								try {
									processnetdata.processInterfaceData(host.getId() + "", ip, type, nodeDTO.getSubtype(), interfacehash);
								} catch (Exception e) {
									e.printStackTrace();
								}
								processnetdata = null;

								// 端口状态信息入库
								Vector interfaceVector = (Vector) interfacehash.get("interface");
								Calendar tempCal = null;
								Date cc = null;
								// StringBuffer sBuffer = null;
								String time = null;
								if (interfaceVector != null && interfaceVector.size() > 0) {
									String tablename = "portstatus" + allipstr;
									// PortconfigDao dao=new PortconfigDao();
									// List portlist = null;
									// if(ShareData.getPortConfigHash().containsKey(ip))
									// portlist =
									// (List)ShareData.getPortConfigHash().get(ip);
									// Vector vector=dao.getSmsByIp(ip);//接口索引
									if (ShareData.getPortConfigHash().get(ip) != null && ((List) ShareData.getPortConfigHash().get(ip)).size() > 0) {
										try {
											Interfacecollectdata interfacedata = null;

											for (int i = 0; i < interfaceVector.size(); i++) {
												interfacedata = (Interfacecollectdata) interfaceVector.elementAt(i);
												if (((List) ShareData.getPortConfigHash().get(ip)).contains(interfacedata.getSubentity()) && interfacedata.getEntity().equals("ifOperStatus")) {
													tempCal = (Calendar) interfacedata.getCollecttime();
													cc = tempCal.getTime();
													time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
													tempCal = null;
													cc = null;
													long count = 0;
													if (interfacedata.getCount() != null) {
														count = interfacedata.getCount();
													}
													sBuffer = new StringBuffer();
													sBuffer.append("insert into ");
													sBuffer.append(tablename);
													sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
													sBuffer.append("values('");
													sBuffer.append(ip);
													sBuffer.append("','");
													sBuffer.append(interfacedata.getRestype());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getCategory());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getEntity());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getSubentity());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getUnit());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getChname());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getBak());
													sBuffer.append("','");
													sBuffer.append(count);
													sBuffer.append("','");
													sBuffer.append(interfacedata.getThevalue());
													sBuffer.append("',");
													sBuffer.append(time);
													sBuffer.append(")");
													try {
														dbmanager.addBatch(sBuffer.toString());
														// dbmanager.executeUpdate(sBuffer.toString());
													} catch (Exception ex) {
														ex.printStackTrace();
													} finally {
														time = null;
														sBuffer = null;
													}
												}
												interfacedata = null;
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
								interfaceVector = null;

								//
								Vector allutilhdxVector = (Vector) interfacehash.get("allutilhdx");
								if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
									AllUtilHdx allutilhdx = null;
									String tablename = "allutilhdx" + allipstr;
									for (int si = 0; si < allutilhdxVector.size(); si++) {
										try {
											allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);
											if (allutilhdx.getRestype().equals("dynamic")) {
												if (allutilhdx.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) allutilhdx.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));

												long count = 0;
												if (allutilhdx.getCount() != null) {
													count = allutilhdx.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getRestype());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getCategory());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getEntity());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getSubentity());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getUnit());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getChname());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												// System.out.println(sql);
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												sBuffer = null;
												time = null;
											}
											allutilhdx = null;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									tablename = null;

								}
								allutilhdxVector = null;

								// UtilHdxPerc
								Vector utilhdxpercVector = (Vector) interfacehash.get("utilhdxperc");
								if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
									String tablename = "utilhdxperc" + allipstr;
									UtilHdxPerc utilhdxperc = null;
									for (int si = 0; si < utilhdxpercVector.size(); si++) {
										try {
											utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);
											if (utilhdxperc.getRestype().equals("dynamic")) {
												if (utilhdxperc.getThevalue().equals("0") || utilhdxperc.getThevalue().equals("0.0"))
													continue;
												tempCal = (Calendar) utilhdxperc.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (utilhdxperc.getCount() != null) {
													count = utilhdxperc.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getRestype());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getCategory());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getEntity());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getSubentity());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getUnit());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getChname());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												// System.out.println(sql);
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												sBuffer = null;
												time = null;
											}
											utilhdxperc = null;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									tablename = null;
								}
								utilhdxpercVector = null;

								// UtilHdx
								Vector utilhdxVector = (Vector) interfacehash.get("utilhdx");
								if (utilhdxVector != null && utilhdxVector.size() > 0) {
									String tablename = "utilhdx" + allipstr;
									UtilHdx utilhdx = null;
									for (int si = 0; si < utilhdxVector.size(); si++) {
										try {
											utilhdx = (UtilHdx) utilhdxVector.elementAt(si);
											if (utilhdx.getRestype().equals("dynamic")) {
												if (utilhdx.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) utilhdx.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (utilhdx.getCount() != null) {
													count = utilhdx.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(utilhdx.getRestype());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getCategory());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getEntity());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getSubentity());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getUnit());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getChname());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(utilhdx.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												// SysLogger.info(sBuffer.toString());
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										sBuffer = null;
										time = null;
										utilhdx = null;
									}
									tablename = null;
								}
								utilhdxVector = null;

								// discardsperc
								Vector discardspercVector = (Vector) interfacehash.get("discardsperc");
								if (discardspercVector != null && discardspercVector.size() > 0) {
									DiscardsPerc discardsperc = null;
									String tablename = "discardsperc" + allipstr;
									for (int si = 0; si < discardspercVector.size(); si++) {
										try {
											discardsperc = (DiscardsPerc) discardspercVector.elementAt(si);
											if (discardsperc.getRestype().equals("dynamic")) {
												if (discardsperc.getThevalue().equals("0.0"))
													continue;
												tempCal = (Calendar) discardsperc.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (discardsperc.getCount() != null) {
													count = discardsperc.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(discardsperc.getRestype());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getCategory());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getEntity());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getSubentity());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getUnit());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getChname());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(discardsperc.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										discardsperc = null;
									}
									tablename = null;
								}
								discardspercVector = null;

								// errorsperc
								Vector errorspercVector = (Vector) interfacehash.get("errorsperc");
								if (errorspercVector != null && errorspercVector.size() > 0) {
									ErrorsPerc errorsperc = null;
									String tablename = "errorsperc" + allipstr;
									for (int si = 0; si < errorspercVector.size(); si++) {
										try {
											errorsperc = (ErrorsPerc) errorspercVector.elementAt(si);
											if (errorsperc.getRestype().equals("dynamic")) {
												if (errorsperc.getThevalue().equals("0.0"))
													continue;
												tempCal = (Calendar) errorsperc.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (errorsperc.getCount() != null) {
													count = errorsperc.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(errorsperc.getRestype());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getCategory());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getEntity());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getSubentity());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getUnit());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getChname());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(errorsperc.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}

											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										errorsperc = null;
									}
									tablename = null;
								}
								errorspercVector = null;

								// packs
								Vector packsVector = (Vector) interfacehash.get("packs");
								if (packsVector != null && packsVector.size() > 0) {
									String tablename = "packs" + allipstr;
									Packs packs = null;
									for (int si = 0; si < packsVector.size(); si++) {
										try {
											packs = (Packs) packsVector.elementAt(si);
											if (packs.getRestype().equals("dynamic")) {
												if (packs.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) packs.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (packs.getCount() != null) {
													count = packs.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(packs.getRestype());
												sBuffer.append("','");
												sBuffer.append(packs.getCategory());
												sBuffer.append("','");
												sBuffer.append(packs.getEntity());
												sBuffer.append("','");
												sBuffer.append(packs.getSubentity());
												sBuffer.append("','");
												sBuffer.append(packs.getUnit());
												sBuffer.append("','");
												sBuffer.append(packs.getChname());
												sBuffer.append("','");
												sBuffer.append(packs.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(packs.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												// conn.executeUpdate(sql);
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										packs = null;
									}
									tablename = null;
								}
								packsVector = null;

								// inpacks
								Vector inpacksVector = (Vector) interfacehash.get("inpacks");
								if (inpacksVector != null && inpacksVector.size() > 0) {
									String tablename = "inpacks" + allipstr;
									InPkts packs = null;
									for (int si = 0; si < inpacksVector.size(); si++) {
										try {
											packs = (InPkts) inpacksVector.elementAt(si);
											if (packs.getRestype().equals("dynamic")) {
												if (packs.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) packs.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));

												long count = 0;
												if (packs.getCount() != null) {
													count = packs.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(packs.getRestype());
												sBuffer.append("','");
												sBuffer.append(packs.getCategory());
												sBuffer.append("','");
												sBuffer.append(packs.getEntity());
												sBuffer.append("','");
												sBuffer.append(packs.getSubentity());
												sBuffer.append("','");
												sBuffer.append(packs.getUnit());
												sBuffer.append("','");
												sBuffer.append(packs.getChname());
												sBuffer.append("','");
												sBuffer.append(packs.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(packs.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										packs = null;
									}
									tablename = null;
								}
								inpacksVector = null;

								// outpacks
								Vector outpacksVector = (Vector) interfacehash.get("outpacks");
								if (outpacksVector != null && outpacksVector.size() > 0) {
									String tablename = "outpacks" + allipstr;
									OutPkts packs = null;
									for (int si = 0; si < outpacksVector.size(); si++) {
										try {
											packs = (OutPkts) outpacksVector.elementAt(si);
											if (packs.getRestype().equals("dynamic")) {
												if (packs.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) packs.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (packs.getCount() != null) {
													count = packs.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(packs.getRestype());
												sBuffer.append("','");
												sBuffer.append(packs.getCategory());
												sBuffer.append("','");
												sBuffer.append(packs.getEntity());
												sBuffer.append("','");
												sBuffer.append(packs.getSubentity());
												sBuffer.append("','");
												sBuffer.append(packs.getUnit());
												sBuffer.append("','");
												sBuffer.append(packs.getChname());
												sBuffer.append("','");
												sBuffer.append(packs.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(packs.getThevalue());
												sBuffer.append("',");
												sBuffer.append(time);
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
													// dbmanager.executeUpdate(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}

										sBuffer = null;
										time = null;
										packs = null;
									}
									tablename = null;
								}
								outpacksVector = null;

								interfacehash = null;

							}

							if (ipdata.containsKey("buffer")) {
								// 缓存
								bufferhash = (Hashtable) ipdata.get("buffer");
								bufferVector = (Vector) bufferhash.get("buffer");
								if (bufferVector != null && bufferVector.size() > 0) {
									bufferdata = (Buffercollectdata) bufferVector.elementAt(0);
									if (bufferdata.getRestype().equals("dynamic")) {
										// session.save(cpudata);
										Calendar tempCal = (Calendar) bufferdata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										String tablename = "buffer" + allipstr;
										// String sql = "insert into " +
										// tablename
										// +
										// "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime)
										// "
										// + "values('" + ip + "','" +
										// bufferdata.getRestype() + "','" +
										// bufferdata.getCategory() + "','"
										// + bufferdata.getEntity() + "','" +
										// bufferdata.getSubentity() + "','" +
										// bufferdata.getUnit() + "','"
										// + bufferdata.getChname() + "','" +
										// bufferdata.getBak() + "'," +
										// bufferdata.getCount() + ",'"
										// + bufferdata.getThevalue() + "','" +
										// time + "')";
										long count = 0;
										if (bufferdata.getCount() != null) {
											count = bufferdata.getCount();
										}
										sBuffer = new StringBuffer();
										sBuffer.append("insert into ");
										sBuffer.append(tablename);
										sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
										sBuffer.append("values('");
										sBuffer.append(ip);
										sBuffer.append("','");
										sBuffer.append(bufferdata.getRestype());
										sBuffer.append("','");
										sBuffer.append(bufferdata.getCategory());
										sBuffer.append("','");
										sBuffer.append(bufferdata.getEntity());
										sBuffer.append("','");
										sBuffer.append(bufferdata.getSubentity());
										sBuffer.append("','");
										sBuffer.append(bufferdata.getUnit());
										sBuffer.append("','");
										sBuffer.append(bufferdata.getChname());
										sBuffer.append("','");
										sBuffer.append(bufferdata.getBak());
										sBuffer.append("','");
										sBuffer.append(count);
										sBuffer.append("','");
										sBuffer.append(bufferdata.getThevalue());
										sBuffer.append("',");
										sBuffer.append(time);
										sBuffer.append(")");
										// System.out.println(sql);
										try {
											dbmanager.addBatch(sBuffer.toString());
											// dbmanager.executeUpdate(sBuffer.toString());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
									}
									bufferdata = null;
									// try {
									// tempDataService.collectData(ip,
									// bufferdata, type, nodeDTO.getSubtype());
									// } catch (Exception e) {
									// e.printStackTrace();
									// }
								}
								bufferhash = null;
								bufferVector = null;
							}

							// if(ipdata.containsKey("iprouter")){
							// //iprouter
							// iprouterhash = (Hashtable)ipdata.get("iprouter");
							// iprouterVector = (Vector)
							// iprouterhash.get("iprouter");
							// if (iprouterVector != null &&
							// iprouterVector.size() > 0) {
							// //删除原来的数据
							// String deleteSql = "delete from
							// nms_route_data_temp where nodeid='" +host.getId()
							// + "'";
							// dbmanager.addBatch(deleteSql);
							// deleteSql = null;
							// //放入内存里
							// ShareData.setIprouterdata(ip, iprouterVector);
							// Calendar date=Calendar.getInstance();
							// String time = sdf.format(date.getTime());
							// StringBuffer sql = null;
							// for(int i=0;i<iprouterVector.size();i++){
							// iprouter = (IpRouter)
							// iprouterVector.elementAt(i);
							// try {
							// sql = new StringBuffer(500);
							// sql.append("insert into
							// nms_route_data_temp(nodeid,ip,type,subtype,ifindex,nexthop,proto,rtype,mask,collecttime,physaddress,dest)values('");
							// sql.append(host.getId());
							// sql.append("','");
							// sql.append(ip);
							// sql.append("','");
							// sql.append(type);
							// sql.append("','");
							// sql.append(nodeDTO.getSubtype());
							// sql.append("','");
							// sql.append(iprouter.getIfindex());
							// sql.append("','");
							// sql.append(iprouter.getNexthop());
							// sql.append("','");
							// sql.append(iprouterproto[Integer.parseInt(iprouter.getProto().longValue()+"")]);
							// sql.append("','");
							// sql.append(iproutertype[Integer.parseInt(iprouter.getType().longValue()+"")]);
							// sql.append("','");
							// sql.append(iprouter.getMask());
							// sql.append("','");
							// sql.append(time);
							// sql.append("','");
							// sql.append(iprouter.getPhysaddress());
							// sql.append("','");
							// sql.append(iprouter.getDest());
							// sql.append("')");
							// //iprouterdbmanager.addBatch(sql.toString());
							// dbmanager.addBatch(sql.toString());
							// sql = null;
							// //tempDataService.collectRouteData(ip, iprouter,
							// type, nodeDTO.getSubtype());
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							// iprouter = null;
							// sql = null;
							// }
							// }
							// iprouterhash = null;
							// iprouterVector = null;
							// }
							if (ipdata.containsKey("iprouter")) {
								// iprouter
								Hashtable iprouterHash = (Hashtable) ipdata.get("iprouter");
								// 处理数据接口模式数据
								ProcessNetData processnetdata = new ProcessNetData();
								try {
									processnetdata.processRouterData(host.getId() + "", ip, type, nodeDTO.getSubtype(), iprouterHash);
								} catch (Exception e) {
									e.printStackTrace();
								}
								processnetdata = null;

							}

							// if(ipdata.containsKey("fdb")){
							// //fdb
							// fdbhash = (Hashtable)ipdata.get("fdb");
							// fdbVector = (Vector) fdbhash.get("fdb");
							// if (fdbVector != null && fdbVector.size() > 0) {
							// //放入内存里
							// //ShareData.(ip, iprouterVector);
							// Calendar date=Calendar.getInstance();
							// String time = sdf.format(date.getTime());
							// // DBManager fdbdbmanager = new DBManager();
							// // //删除原来的数据
							// String deleteSql = "delete from nms_fdb_data_temp
							// where nodeid='" +host.getId() + "'";
							// // fdbdbmanager.addBatch(deleteSql);
							// dbmanager.addBatch(deleteSql);
							// deleteSql = null;
							// StringBuffer sql = null;
							// for(int i=0;i<fdbVector.size();i++){
							// ipmac = (IpMac) fdbVector.elementAt(i);
							// String mac = ipmac.getMac();
							// if(mac != null &&
							// !mac.contains(":")){//排除mac为乱码的情况
							// mac = "--";
							// }
							// try {
							// sql = new StringBuffer(500);
							// sql.append("insert into
							// nms_fdb_data_temp(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak)values('");
							// sql.append(host.getId());
							// sql.append("','");
							// sql.append(ip);
							// sql.append("','");
							// sql.append(type);
							// sql.append("','");
							// sql.append(nodeDTO.getSubtype());
							// sql.append("','");
							// sql.append(ipmac.getIfindex());
							// sql.append("','");
							// sql.append(ipmac.getIpaddress());
							// sql.append("','");
							// sql.append(mac);
							// sql.append("','");
							// sql.append(ipmac.getIfband());
							// sql.append("','");
							// sql.append(ipmac.getIfsms());
							// sql.append("','");
							// sql.append(time);
							// sql.append("','')");
							// //fdbdbmanager.addBatch(sql.toString());
							// dbmanager.addBatch(sql.toString());
							// //tempDataService.collectFdbData(ip, ipmac, type,
							// nodeDTO.getSubtype());
							// sql = null;
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							// ipmac = null;
							// sql = null;
							// }
							// // try{
							// // fdbdbmanager.executeBatch();
							// // }catch(Exception e){
							// //
							// // }finally{
							// // fdbdbmanager.close();
							// // }
							// }
							// fdbhash = null;
							// fdbVector = null;
							// }
							if (ipdata.containsKey("ping")) {
								// ping
								pinghash = (Hashtable) ipdata.get("ping");
								pingVector = (Vector) pinghash.get("ping");
								if (pingVector != null && pingVector.size() > 0) {
									String tablename = "ping" + allipstr;
									for (int i = 0; i < pingVector.size(); i++) {
										pingdata = (PingCollectEntity) pingVector.elementAt(i);
										if (pingdata.getRestype().equals("dynamic")) {
											Calendar tempCal = (Calendar) pingdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));

											// String sql = "insert into " +
											// tablename
											// +
											// "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime)
											// "
											// + "values('" + ip + "','" +
											// pingdata.getRestype() + "','" +
											// pingdata.getCategory() + "','"
											// + pingdata.getEntity() + "','" +
											// pingdata.getSubentity() + "','" +
											// pingdata.getUnit() + "','"
											// + pingdata.getChname() + "','" +
											// pingdata.getBak() + "'," +
											// pingdata.getCount() + ",'"
											// + pingdata.getThevalue() + "','"
											// + time + "')";
											long count = 0;
											if (pingdata.getCount() != null) {
												count = pingdata.getCount();
											}
											sBuffer = new StringBuffer();
											sBuffer.append("insert into ");
											sBuffer.append(tablename);
											sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
											sBuffer.append("values('");
											sBuffer.append(ip);
											sBuffer.append("','");
											sBuffer.append(pingdata.getRestype());
											sBuffer.append("','");
											sBuffer.append(pingdata.getCategory());
											sBuffer.append("','");
											sBuffer.append(pingdata.getEntity());
											sBuffer.append("','");
											sBuffer.append(pingdata.getSubentity());
											sBuffer.append("','");
											sBuffer.append(pingdata.getUnit());
											sBuffer.append("','");
											sBuffer.append(pingdata.getChname());
											sBuffer.append("','");
											sBuffer.append(pingdata.getBak());
											sBuffer.append("','");
											sBuffer.append(count);
											sBuffer.append("','");
											sBuffer.append(pingdata.getThevalue());
											sBuffer.append("',");
											sBuffer.append(time);
											sBuffer.append(")");
											// System.out.println(sBuffer.toString());
											try {
												dbmanager.addBatch(sBuffer.toString());
												// dbmanager.executeUpdate(sBuffer.toString());
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
										pingdata = null;
										sBuffer = null;
									}

								}
								pinghash = null;
								pingVector = null;
							}

							// if(ipdata.containsKey("systemgroup")){
							// Hashtable systemhash =
							// (Hashtable)ipdata.get("systemgroup");
							// Vector systemVector = (Vector)
							// systemhash.get("system");
							// if (systemVector != null && systemVector.size() >
							// 0) {
							// for(int i=0;i<systemVector.size();i++){
							// Systemcollectdata systemdata =
							// (Systemcollectdata) systemVector.elementAt(i);
							// try {
							// tempDataService.collectData(ip, systemdata, type,
							// nodeDTO.getSubtype());
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							// }
							// }
							// }
							// if(ipdata.containsKey("baseInfo")){
							// Hashtable systemhash =
							// (Hashtable)ipdata.get("baseInfo");
							// Vector systemVector = (Vector)
							// systemhash.get("baseInfo");
							// if (systemVector != null && systemVector.size() >
							// 0) {
							// for(int i=0;i<systemVector.size();i++){
							// Systemcollectdata systemdata =
							// (Systemcollectdata) systemVector.elementAt(i);
							// try {
							// tempDataService.collectData(ip, systemdata, type,
							// nodeDTO.getSubtype());
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							// }
							// }
							// }
							if (ipdata.containsKey("ipmac")) {
								// ipmac
								XmlDataOperator xmlOpr = new XmlDataOperator();
								boolean flag = false;
								DBManager ipmacdbmanager = new DBManager();

								Hashtable ipmachash = (Hashtable) ipdata.get("ipmac");
								Vector ipmacVector = (Vector) ipmachash.get("ipmac");
								if (ipmacVector != null && ipmacVector.size() > 0) {
									// 放入内存里
									ShareData.setRelateipmacdata(ip, ipmacVector);
								}
								int firstipmac = ShareData.getFirstipmac();
								if (firstipmac == 0) {
									// 第一次启动
									// 设置变量值，并装载IP-MAC对照表
									ShareData.setFirstipmac(1);
									// 装载该IP的IP-MAC对应绑定关系表
									ResultSet rs = dbmanager.executeQuery("select * from nms_ipmacbase");
									List list = loadFromIpMacBandRS(rs);
									if (list != null && list.size() > 0) {
										for (int i = 0; i < list.size(); i++) {
											IpMacBase _ipmacbase = (IpMacBase) list.get(i);
											if (ShareData.getIpmacbanddata().containsKey(_ipmacbase.getMac())) {
												// 已经存在
												List existMacList = (List) ShareData.getIpmacbanddata().get(_ipmacbase.getMac());
												existMacList.add(_ipmacbase);
												ShareData.getIpmacbanddata().put(_ipmacbase.getMac(), existMacList);
											} else {
												List macList = new ArrayList();
												macList.add(_ipmacbase);
												ShareData.getIpmacbanddata().put(_ipmacbase.getMac(), macList);
											}

										}
									}
									rs.close();

								}
								if (ipmacVector != null && ipmacVector.size() > 0) {
									// 数据接口模式 生成arp的xml文件
									xmlOpr = new XmlDataOperator();
									xmlOpr.setFile(type + "_arp" + ip + ".xml");
									xmlOpr.init4createXml();

									// 把实时采集到的ARP表信息存放到共享数据里，然后再更新数据库
									ShareData.getRelateipmacdata().put(ip, ipmacVector);
									String sql = "delete from ipmac where relateipaddr='" + ip + "'";
									try {
										dbmanager.executeUpdate(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									String _localMAC = "";
									Hashtable _AllMAC = new Hashtable();
									Hashtable _sameMAC = new Hashtable();
									Vector tempVector = new Vector();
									for (int si = 0; si < ipmacVector.size(); si++) {
										ipmac = (IpMac) ipmacVector.elementAt(si);
										if (ipmac == null)
											continue;
										if (ipmac.getIpaddress() == null)
											continue;
										if (ipmac.getIpaddress().equals(ip)) {
											// 若是本机IP
											_localMAC = ipmac.getMac();
										}
										if (!_AllMAC.containsKey(ipmac.getMac())) {
											// 若不存在次IP
											_AllMAC.put(ipmac.getMac(), ipmac);
										} else {
											// 若存在

											tempVector.add(ipmac);
											// tempVector.add(_AllIp.get(ipmac.getIpaddress()));
											_sameMAC.put(ipmac.getMac(), tempVector);
										}
									}
									for (int si = 0; si < ipmacVector.size(); si++) {
										ipmac = (IpMac) ipmacVector.elementAt(si);
										try {
											if (ipmac != null && ipmac.getMac() != null && ipmac.getMac().length() == 17) {
												ipmacdbmanager.addBatch(ipmacInsertSQL(ipmac));
												if (host.getTransfer() == 1) {

													String time = sdf.format(ipmac.getCollecttime().getTime());
													xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
													flag = true;
												}
											}
											// 需要加是否对MAC地址进行审计的判断
											/*
											 * PingUtil pingU=new
											 * PingUtil(ipmac.getIpaddress());
											 * Integer[] packet=pingU.ping();
											 * //vector=pingU.addhis(packet);
											 * SysLogger
											 * .info(ipmac.getIpaddress()+"
											 * pack====="+packet[0]);
											 * //把存活的MAC地址存入数据库nms_machistory
											 * if(packet[0]!=null){ if(packet[0]
											 * >0){
											 * //连通的MAC,将当前的MAC存入NMS_MACHISTORY表
											 * MacHistory machis = new
											 * MacHistory();
											 * machis.setRelateipaddr
											 * (ipmac.getRelateipaddr());
											 * machis.setBak(ipmac.getBak());
											 * machis
											 * .setCollecttime(ipmac.getCollecttime
											 * ());
											 * machis.setIfindex(ipmac.getIfindex
											 * ()); machis.setIpaddress(ipmac.
											 * getIpaddress());
											 * machis.setMac(ipmac.getMac());
											 * machis.setThevalue("1");
											 * dbmanager
											 * .executeUpdate(machistoryInsertSQL
											 * (machis)); }else{ //不在线的MAC }
											 * //hostdata
											 * .setThevalue(packet[0].toString
											 * ()); } else{ //不通的MAC
											 * //hostdata.setThevalue("0");
											 * //continue; }
											 */
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										// session.save(ipmac);
										if (ipmac == null)
											continue;
										if (ipmac.getMac() == null)
											continue;
										if (ipmac.getMac().equalsIgnoreCase(_localMAC)) {
											// 若是本机MAC,IP不是本机，则过滤
											if (!ipmac.getIpaddress().equals(ip))
												continue;
										}
										// 同时更新IP-MAC绑定表，若IP-MAC地址变更，则采用新的IP-MAC信息，同时保存到历史变更表里
										if (ShareData.getIpmacbanddata().containsKey(ipmac.getMac())) {
											// List existMacList = (List)
											// ShareData.getIpmacbanddata().get(ipmac.getMac());
											if (ShareData.getIpmacbanddata().get(ipmac.getMac()) != null && ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).size() > 0) {
												int changeflag = 0;
												int samenodeflag = 0;
												String ipstr = "";
												IpMacBase changeMacBase = null;
												for (int i = 0; i < ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).size(); i++) {
													IpMacBase temp_ipmacband = (IpMacBase) ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).get(i);
													ipstr = ipstr + "或" + temp_ipmacband.getRelateipaddr();
													if (temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())) {
														changeflag = 1;
													}
												}
												if (changeflag == 0) {
													// 没有相等的IP,判断从哪个设备上路过
													for (int i = 0; i < ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).size(); i++) {
														IpMacBase temp_ipmacband = (IpMacBase) ((List) ShareData.getIpmacbanddata().get(ipmac.getMac())).get(i);
														if (temp_ipmacband.getRelateipaddr().equalsIgnoreCase(ipmac.getRelateipaddr())) {
															samenodeflag = 1;
															changeMacBase = temp_ipmacband;
															break;
														}
													}
												}
												// 若存在此MAC，则判断IP是否相等
												// IpMacBase temp_ipmacband =
												// (IpMacBase)ipmacband.get(ipmac.getMac());

												// if(!temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())){
												if (changeMacBase != null) {
													// 若IP不相等，而且找到设备上路过,则用新的信息修改该IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

													String _ipaddress = changeMacBase.getIpaddress();
													changeMacBase.setIpaddress(ipmac.getIpaddress());
													// ShareData.setIpmacbanddata(ipmac.getMac(),temp_ipmacband);
													// existMacList = (List)
													// ShareData.getIpmacbanddata().get(changeMacBase.getMac());
													((List) ShareData.getIpmacbanddata().get(changeMacBase.getMac())).add(changeMacBase);
													ShareData.getIpmacbanddata().put(changeMacBase.getMac(), ShareData.getIpmacbanddata().get(changeMacBase.getMac()));
													try {
														dbmanager.executeUpdate(ipmacbandUpdateSQL(changeMacBase));
													} catch (Exception ex) {
														ex.printStackTrace();
													}
													if (_sameMAC.containsKey(ipmac.getMac())) {
														if (changeMacBase.getIfband() == 0)
															continue;
													}

													// 在历史表里追加一条新加入的提示信息
													IpMacChange _ipmacchange = new IpMacChange();
													_ipmacchange.setMac(ipmac.getMac());
													_ipmacchange.setChangetype("2");
													_ipmacchange.setDetail("该MAC地址从IP：" + _ipaddress + " 变换到IP：" + ipmac.getIpaddress());
													_ipmacchange.setIpaddress(ipmac.getIpaddress());
													_ipmacchange.setCollecttime(ipmac.getCollecttime());
													// 对发生IP变更的要进行PING操作，若能PING通，则进行下一不操作

													Vector vector = null;
													PingUtil pingU = new PingUtil(ipmac.getIpaddress());
													Integer[] packet = pingU.ping();
													// vector=pingU.addhis(packet);
													if (packet[0] != null) {
														// hostdata.setThevalue(packet[0].toString());
													} else {
														// hostdata.setThevalue("0");
														continue;
													}

													// 若IP-MAC已经绑定，则产生事件
													if (changeMacBase.getIfband() == 1) {
														try {
															ipmacdbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
															if (host.getTransfer() == 1) {

																String time = sdf.format(ipmac.getCollecttime().getTime());
																xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
																flag = true;
															}
														} catch (Exception ex) {
															ex.printStackTrace();
														}
														// session.save(_ipmacchange);
														// 写事件
														/*
														 * I_Eventlist
														 * eventmanager=new
														 * EventlistManager();
														 * Eventlist event = new
														 * Eventlist();
														 * event.setEventtype
														 * ("network");
														 * //事件类型需要重新定义
														 * ：网络、主机、数据库
														 * 、WEB服务、FTP服务、安全产品等
														 * event
														 * .setEventlocation
														 * (temp_ipmacband
														 * .getIpaddress());
														 * event
														 * .setManagesign(new
														 * Integer(0));
														 * event.setReportman
														 * ("monitorpc"); String
														 * time =
														 * sdf.format(ipmac
														 * .getCollecttime
														 * ().getTime());
														 * event.setRecordtime
														 * (ipmac
														 * .getCollecttime());
														 * event
														 * .setContent(time+
														 * "&IP地址更新&"
														 * +_ipaddress+
														 * "&该MAC地址从IP："
														 * +_ipaddress+" 变换到IP：
														 * "+ipmac.getIpaddress());
														 * event.setLevel1(new
														 * Integer(2)); Vector
														 * eventtmpV = new
														 * Vector();
														 * eventtmpV.add(event);
														 * eventmanager
														 * .createEventlist
														 * (eventtmpV);
														 */
														if (host.getTransfer() == 1) {

															String time = sdf.format(ipmac.getCollecttime().getTime());
															xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
															flag = true;
														}
													}
													_ipmacchange = null;
												} else if (changeflag == 0) {
													// 若IP不相等，但没找到相同的设备上路过,说明该MAC经过的设备发生了改变,则追加新的IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

													IpMacBase _ipmacband = new IpMacBase();
													_ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
													_ipmacband.setIfindex(ipmac.getIfindex());
													_ipmacband.setIpaddress(ipmac.getIpaddress());
													_ipmacband.setMac(ipmac.getMac());
													_ipmacband.setCollecttime(ipmac.getCollecttime());
													_ipmacband.setIfband(0);
													_ipmacband.setIfsms("0");
													_ipmacband.setEmployee_id(new Integer(0));
													_ipmacband.setRelateipaddr(ip);
													ShareData.getIpmacbanddata().put(ipmac.getMac(), _ipmacband);
													try {
														ipmacdbmanager.addBatch(ipmacbaseInsertSQL(_ipmacband));
														if (host.getTransfer() == 1) {

															String time = sdf.format(ipmac.getCollecttime().getTime());
															xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
															flag = true;
														}
													} catch (Exception ex) {
														ex.printStackTrace();
													}
													_ipmacband = null;
													if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
														continue;
													// 在历史表里追加一条新加入的提示信息
													IpMacChange _ipmacchange = new IpMacChange();
													_ipmacchange.setMac(ipmac.getMac());
													_ipmacchange.setChangetype("1");
													_ipmacchange.setDetail("该MAC地址从设备" + ipstr + " 变换到设备" + ipmac.getRelateipaddr() + "上,IP：" + ipmac.getIpaddress());
													_ipmacchange.setIpaddress(ipmac.getIpaddress());
													_ipmacchange.setCollecttime(ipmac.getCollecttime());
													_ipmacchange.setRelateipaddr(ip);
													_ipmacchange.setIfindex(ipmac.getIfindex());

													try {
														ipmacdbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
														// session.save(_ipmacband);
													} catch (Exception ex) {
														ex.printStackTrace();
													}
													_ipmacchange = null;
													if (host.getTransfer() == 1) {

														String time = sdf.format(ipmac.getCollecttime().getTime());
														xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
														flag = true;
													}
												}
											}
										} else {
											// 若不存在次MAC，则为新加入的机器，则保存该IP-MAC，同时在历史表里追加一条新加入的提示信息
											/*
											 * IpMacBase _ipmacband = new
											 * IpMacBase();
											 * _ipmacband.setRelateipaddr
											 * (ipmac.getRelateipaddr());
											 * _ipmacband
											 * .setIfindex(ipmac.getIfindex());
											 * _ipmacband
											 * .setIpaddress(ipmac.getIpaddress
											 * ());
											 * _ipmacband.setMac(ipmac.getMac
											 * ());
											 * _ipmacband.setCollecttime(ipmac
											 * .getCollecttime());
											 * _ipmacband.setIfband(0);
											 * _ipmacband.setIfsms("0");
											 * _ipmacband.setEmployee_id(new
											 * Integer(0));
											 * _ipmacband.setRelateipaddr(ip);
											 * ipmacband
											 * .put(ipmac.getMac(),_ipmacband);
											 * try{ dbmanager.executeUpdate(
											 * ipmacbandInsertSQL(_ipmacband));
											 * //session.save(_ipmacband);
											 * }catch(Exception ex){
											 * ex.printStackTrace(); }
											 */
											// 判断变更表里是否已经有该MAC地址
											if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
												continue;
											// 在历史表里追加一条新加入的提示信息
											IpMacChange _ipmacchange = new IpMacChange();
											_ipmacchange.setMac(ipmac.getMac());
											_ipmacchange.setChangetype("1");
											_ipmacchange.setDetail("新增加的IP-MAC");
											_ipmacchange.setIpaddress(ipmac.getIpaddress());
											_ipmacchange.setCollecttime(ipmac.getCollecttime());
											_ipmacchange.setRelateipaddr(ip);
											_ipmacchange.setIfindex(ipmac.getIfindex());

											try {
												if (ipmac.getMac().length() == 17) {
													dbmanager.addBatch(ipmacchangeInsertSQL(_ipmacchange));
													if (host.getTransfer() == 1) {

														String time = sdf.format(ipmac.getCollecttime().getTime());
														xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
														flag = true;
													}
												}
												// session.save(_ipmacband);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											_ipmacchange = null;

											// session.save(_ipmacchange);
										}
									}
								}
								try {
									ipmacdbmanager.executeBatch();
									if (flag) {
										xmlOpr.createXml();
										boolean uploadsuccess = false;
										FtpTransConfigDao ftpconfdao = new FtpTransConfigDao();
										FtpTransConfig ftpConfig = null;
										try {
											ftpConfig = ftpconfdao.getFtpTransMonitorConfig();
											if (ftpConfig != null) {
												FtpUtil ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());
												uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_arp" + host.getIpAddress() + ".xml");
											}

										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											ftpconfdao.close();
										}
									}
									xmlOpr = null;
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									ipmacdbmanager.close();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						dbmanager.executeBatch();
						dbmanager.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					dbmanager.close();
				}
			}
		} else if (type.equalsIgnoreCase("ups")) {
			if (datahash != null && datahash.size() > 0) {
				// 存临时数据到临时表
				if ("1".equals(runmodel)) {
					// 采集与访问是分离模式
					Date startdate = new Date();
					// 实时数据入库处理
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.processUPSData(datahash);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Date enddate = new Date();
					// SysLogger.info(enddate.getTime()-startdate.getTime()+"=========");
				}

				Enumeration iphash = datahash.keys();
				DBManager dbmanager = new DBManager();
				try {
					while (iphash.hasMoreElements()) {
						String ip = (String) iphash.nextElement();
						// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
						// String tempStr = "";
						String allipstr = SysUtil.doip(ip);
						// if (ip.indexOf(".") > 0) {
						// ip1 = ip.substring(0, ip.indexOf("."));
						// ip4 = ip.substring(ip.lastIndexOf(".") + 1,
						// ip.length());
						// tempStr = ip
						// .substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
						// }
						// ip2 = tempStr.substring(0, tempStr.indexOf("."));
						// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
						// tempStr.length());
						// allipstr = ip1 + ip2 + ip3 + ip4;
						Hashtable ipdata = (Hashtable) datahash.get(ip);
						if (ipdata != null) {
							// 处理主机设备的数据
							if (ipdata.containsKey("input")) {
								// CPU
								Hashtable inputhash = (Hashtable) ipdata.get("input");
								Vector inputVector = (Vector) inputhash.get("input");
								if (inputVector != null && inputVector.size() > 0) {
									// 得到CPU平均
									for (int i = 0; i < inputVector.size(); i++) {
										SystemCollectEntity inputdata = (SystemCollectEntity) inputVector.elementAt(i);
										if (inputdata.getRestype().equals("dynamic")) {

											Calendar tempCal = (Calendar) inputdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "input" + allipstr;
											String sql = "";
											if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + inputdata.getRestype() + "','" + inputdata.getCategory() + "','" + inputdata.getEntity() + "','" + inputdata.getSubentity() + "','" + inputdata.getUnit() + "','" + inputdata.getChname() + "','" + inputdata.getBak() + "'," + inputdata.getCount() + ",'" + inputdata.getThevalue() + "'," + time + ")";
											} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + inputdata.getRestype() + "','" + inputdata.getCategory() + "','" + inputdata.getEntity() + "','" + inputdata.getSubentity() + "','" + inputdata.getUnit() + "','" + inputdata.getChname() + "','" + inputdata.getBak() + "'," + inputdata.getCount() + ",'" + inputdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
											}

											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}
								}
							}
							if (ipdata.containsKey("output")) {
								Hashtable outputhash = (Hashtable) ipdata.get("output");
								Vector outputVector = (Vector) outputhash.get("output");
								if (outputVector != null && outputVector.size() > 0) {
									for (int i = 0; i < outputVector.size(); i++) {
										SystemCollectEntity outputdata = (SystemCollectEntity) outputVector.elementAt(i);
										Calendar tempCal = (Calendar) outputdata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
										if (outputdata.getRestype().equals("dynamic")) {
											String tablename = "output" + allipstr;
											String sql = "";
											if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + outputdata.getRestype() + "','" + outputdata.getCategory() + "','" + outputdata.getEntity() + "','" + outputdata.getSubentity() + "','" + outputdata.getUnit() + "','" + outputdata.getChname() + "','" + outputdata.getBak() + "'," + outputdata.getCount() + ",'" + outputdata.getThevalue() + "'," + time + ")";
											} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + outputdata.getRestype() + "','" + outputdata.getCategory() + "','" + outputdata.getEntity() + "','" + outputdata.getSubentity() + "','" + outputdata.getUnit() + "','" + outputdata.getChname() + "','" + outputdata.getBak() + "'," + outputdata.getCount() + ",'" + outputdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
											}

											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						dbmanager.executeBatch();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dbmanager.close();
					}
					datahash = null;
				}
			}
		} else {
			HostCollectDataHelper helper = new HostCollectDataHelper();
			helper.dealDominoData(datahash, type);
		}
		// 接口信息

		return true;
	}

	public synchronized boolean createInterfaceItemData(Hashtable datahash, String type) {
		String runmodel = PollingEngine.getCollectwebflag();
		if (type.equalsIgnoreCase("host")) {
			if (datahash != null && datahash.size() > 0) {
				if ("1".equals(runmodel)) {
					// 采集与访问是分离模式
					Date startdate = new Date();
					// 实时数据入库处理
					ProcessNetData porcessData = new ProcessNetData();
					try {
						porcessData.processHostData("host", datahash);
					} catch (Exception e) {

					}
					Date enddate = new Date();
				}

				Date startdate1 = new Date();
				Enumeration iphash = datahash.keys();
				DBManager dbmanager = new DBManager();
				TempDataService tempDataService = new TempDataService();
				try {
					while (iphash.hasMoreElements()) {
						String ip = (String) iphash.nextElement();
						String allipstr = SysUtil.doip(ip);

						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);

						Hashtable ipdata = (Hashtable) datahash.get(ip);
						if (ipdata != null) {
							// 处理主机设备的数据
							if (ipdata.containsKey("interface")) {
								// interface
								Hashtable interfacehash = (Hashtable) ipdata.get("interface");

								// 综合流速
								Vector allutilhdxVector = (Vector) interfacehash.get("allutilhdx");
								if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
									for (int si = 0; si < allutilhdxVector.size(); si++) {
										AllUtilHdx allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);

										if (allutilhdx.getRestype().equals("dynamic")) {
											if (allutilhdx.getThevalue().equals("0"))
												continue;
											Calendar tempCal = (Calendar) allutilhdx.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "allutilhdx" + allipstr;
											String sql = "";
											if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "','" + time + "')";
											} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
											}

											// SysLogger.info(sql);
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}

								}

								// UtilHdxPerc
								Vector utilhdxpercVector = (Vector) interfacehash.get("utilhdxperc");
								if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
									for (int si = 0; si < utilhdxpercVector.size(); si++) {
										UtilHdxPerc utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);

										if (utilhdxperc.getRestype().equals("dynamic")) {
											// session.save(utilhdx);
											if (utilhdxperc.getThevalue().equals("0") || utilhdxperc.getThevalue().equals("0.0"))
												continue;
											Calendar tempCal = (Calendar) utilhdxperc.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "utilhdxperc" + allipstr;
											String sql = "";
											if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "','" + time + "')";
											} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
											}
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											// conn.executeUpdate(sql);
										}
									}
								}

								// UtilHdx
								Vector utilhdxVector = (Vector) interfacehash.get("utilhdx");
								if (utilhdxVector != null && utilhdxVector.size() > 0) {
									for (int si = 0; si < utilhdxVector.size(); si++) {
										UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

										if (utilhdx.getRestype().equals("dynamic")) {
											// session.save(utilhdx);
											if (utilhdx.getThevalue().equals("0"))
												continue;
											Calendar tempCal = (Calendar) utilhdx.getCollecttime();
											Date cc = tempCal.getTime();
											String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
											String tablename = "utilhdx" + allipstr;
											String sql = "";
											if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "','" + time + "')";
											} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
												sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
											}
											// System.out.println(sql);
											try {
												dbmanager.addBatch(sql);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
											// conn.executeUpdate(sql);
										}
									}
								}
							}
						}
					}

				} catch (Exception e) {

				} finally {
					try {
						dbmanager.executeBatch();
						dbmanager.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					dbmanager.close();
				}
			}
		} else if (type.equalsIgnoreCase("net")) {
			if (datahash != null && datahash.size() > 0) {
				if ("1".equals(runmodel)) {
					// //采集与访问是分离模式
					// Date startdate = new Date();
					// //实时数据入库处理
					// ProcessNetData porcessData = new ProcessNetData();
					// try{
					// porcessData.processNetData("net",datahash);
					// }catch(Exception e){
					//
					// }
				}

				// Hashtable portconfigHash = ShareData.getPortConfigHash();
				Iterator ipIterator = datahash.keySet().iterator();
				DBManager dbmanager = new DBManager();
				try {
					while (ipIterator.hasNext()) {
						String ip = (String) ipIterator.next();
						String allipstr = SysUtil.doip(ip);
						// Host host =
						// (Host)PollingEngine.getInstance().getNodeByIP(ip);
						System.out.println("************* HostCollectDataManger interface-ip:" + ip);
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						nodeUtil = null;

						Hashtable ipdata = (Hashtable) datahash.get(ip);
						if (ipdata != null) {
							// 处理网络设备的数据
							if (ipdata.containsKey("interface")) {
								Hashtable interfacehash = (Hashtable) ipdata.get("interface");
								// 将当前采集到的接口数据入库
								System.out.println("************* HostCollectDataManger interface-ip:" + ip);
								ProcessNetData processnetdata = new ProcessNetData();
								try {
									processnetdata.processInterfaceData(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "", ip, type, nodeDTO.getSubtype(), (Hashtable) ipdata.get("interface"));
								} catch (Exception e) {
									e.printStackTrace();
								}
								processnetdata = null;

								// 端口状态信息入库
								Vector interfaceVector = (Vector) interfacehash.get("interface");
								Calendar tempCal = null;
								Date cc = null;
								StringBuffer sBuffer = null;
								String time = null;
								if (interfaceVector != null && interfaceVector.size() > 0) {
									String tablename = "portstatus" + allipstr;
									// PortconfigDao dao=new PortconfigDao();
									// List portlist = null;
									// if(ShareData.getPortConfigHash().containsKey(ip))
									// portlist =
									// (List)ShareData.getPortConfigHash().get(ip);
									// Vector vector=dao.getSmsByIp(ip);//接口索引
									if (ShareData.getPortConfigHash().get(ip) != null && ((List) ShareData.getPortConfigHash().get(ip)).size() > 0) {
										try {
											Interfacecollectdata interfacedata = null;

											for (int i = 0; i < interfaceVector.size(); i++) {
												interfacedata = (Interfacecollectdata) interfaceVector.elementAt(i);
												if (((List) ShareData.getPortConfigHash().get(ip)).contains(interfacedata.getSubentity()) && interfacedata.getEntity().equals("ifOperStatus")) {
													tempCal = (Calendar) interfacedata.getCollecttime();
													cc = tempCal.getTime();
													time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
													tempCal = null;
													cc = null;
													long count = 0;
													if (interfacedata.getCount() != null) {
														count = interfacedata.getCount();
													}
													sBuffer = new StringBuffer();
													sBuffer.append("insert into ");
													sBuffer.append(tablename);
													sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
													sBuffer.append("values('");
													sBuffer.append(ip);
													sBuffer.append("','");
													sBuffer.append(interfacedata.getRestype());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getCategory());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getEntity());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getSubentity());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getUnit());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getChname());
													sBuffer.append("','");
													sBuffer.append(interfacedata.getBak());
													sBuffer.append("','");
													sBuffer.append(count);
													sBuffer.append("','");
													sBuffer.append(interfacedata.getThevalue());
													sBuffer.append("',");
													if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
														sBuffer.append("'");
														sBuffer.append(time);
														sBuffer.append("'");
													} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
														sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
													}
													sBuffer.append(")");
													try {
														dbmanager.addBatch(sBuffer.toString());
													} catch (Exception ex) {
														ex.printStackTrace();
													} finally {
														time = null;
														sBuffer = null;
													}
												}
												interfacedata = null;
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
								interfaceVector = null;

								//
								Vector allutilhdxVector = (Vector) interfacehash.get("allutilhdx");
								if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
									AllUtilHdx allutilhdx = null;
									String tablename = "allutilhdx" + allipstr;
									for (int si = 0; si < allutilhdxVector.size(); si++) {
										try {
											allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);
											if (allutilhdx.getRestype().equals("dynamic")) {
												if (allutilhdx.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) allutilhdx.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));

												long count = 0;
												if (allutilhdx.getCount() != null) {
													count = allutilhdx.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getRestype());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getCategory());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getEntity());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getSubentity());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getUnit());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getChname());
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(allutilhdx.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												// System.out.println(sql);
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												sBuffer = null;
												time = null;
											}
											allutilhdx = null;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									tablename = null;

								}
								allutilhdxVector = null;

								// UtilHdxPerc
								Vector utilhdxpercVector = (Vector) interfacehash.get("utilhdxperc");
								if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
									String tablename = "utilhdxperc" + allipstr;
									UtilHdxPerc utilhdxperc = null;
									for (int si = 0; si < utilhdxpercVector.size(); si++) {
										try {
											utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);
											if (utilhdxperc.getRestype().equals("dynamic")) {
												if (utilhdxperc.getThevalue().equals("0") || utilhdxperc.getThevalue().equals("0.0"))
													continue;
												tempCal = (Calendar) utilhdxperc.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (utilhdxperc.getCount() != null) {
													count = utilhdxperc.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getRestype());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getCategory());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getEntity());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getSubentity());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getUnit());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getChname());
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(utilhdxperc.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												// System.out.println(sql);
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												sBuffer = null;
												time = null;
											}
											utilhdxperc = null;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									tablename = null;
								}
								utilhdxpercVector = null;

								// UtilHdx
								Vector utilhdxVector = (Vector) interfacehash.get("utilhdx");
								if (utilhdxVector != null && utilhdxVector.size() > 0) {
									String tablename = "utilhdx" + allipstr;
									UtilHdx utilhdx = null;
									for (int si = 0; si < utilhdxVector.size(); si++) {
										try {
											utilhdx = (UtilHdx) utilhdxVector.elementAt(si);
											if (utilhdx.getRestype().equals("dynamic")) {
												if (utilhdx.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) utilhdx.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (utilhdx.getCount() != null) {
													count = utilhdx.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(utilhdx.getRestype());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getCategory());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getEntity());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getSubentity());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getUnit());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getChname());
												sBuffer.append("','");
												sBuffer.append(utilhdx.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(utilhdx.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												// SysLogger.info(sBuffer.toString());
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										sBuffer = null;
										time = null;
										utilhdx = null;
									}
									tablename = null;
								}
								utilhdxVector = null;

								// discardsperc
								Vector discardspercVector = (Vector) interfacehash.get("discardsperc");
								if (discardspercVector != null && discardspercVector.size() > 0) {
									DiscardsPerc discardsperc = null;
									String tablename = "discardsperc" + allipstr;
									for (int si = 0; si < discardspercVector.size(); si++) {
										try {
											discardsperc = (DiscardsPerc) discardspercVector.elementAt(si);
											if (discardsperc.getRestype().equals("dynamic")) {
												if (discardsperc.getThevalue().equals("0.0"))
													continue;
												tempCal = (Calendar) discardsperc.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (discardsperc.getCount() != null) {
													count = discardsperc.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(discardsperc.getRestype());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getCategory());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getEntity());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getSubentity());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getUnit());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getChname());
												sBuffer.append("','");
												sBuffer.append(discardsperc.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(discardsperc.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										discardsperc = null;
									}
									tablename = null;
								}
								discardspercVector = null;

								// errorsperc
								Vector errorspercVector = (Vector) interfacehash.get("errorsperc");
								if (errorspercVector != null && errorspercVector.size() > 0) {
									ErrorsPerc errorsperc = null;
									String tablename = "errorsperc" + allipstr;
									for (int si = 0; si < errorspercVector.size(); si++) {
										try {
											errorsperc = (ErrorsPerc) errorspercVector.elementAt(si);
											if (errorsperc.getRestype().equals("dynamic")) {
												if (errorsperc.getThevalue().equals("0.0"))
													continue;
												tempCal = (Calendar) errorsperc.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (errorsperc.getCount() != null) {
													count = errorsperc.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(errorsperc.getRestype());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getCategory());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getEntity());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getSubentity());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getUnit());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getChname());
												sBuffer.append("','");
												sBuffer.append(errorsperc.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(errorsperc.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}

											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										errorsperc = null;
									}
									tablename = null;
								}
								errorspercVector = null;

								// packs
								Vector packsVector = (Vector) interfacehash.get("packs");
								if (packsVector != null && packsVector.size() > 0) {
									String tablename = "packs" + allipstr;
									Packs packs = null;
									for (int si = 0; si < packsVector.size(); si++) {
										try {
											packs = (Packs) packsVector.elementAt(si);
											if (packs.getRestype().equals("dynamic")) {
												if (packs.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) packs.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (packs.getCount() != null) {
													count = packs.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(packs.getRestype());
												sBuffer.append("','");
												sBuffer.append(packs.getCategory());
												sBuffer.append("','");
												sBuffer.append(packs.getEntity());
												sBuffer.append("','");
												sBuffer.append(packs.getSubentity());
												sBuffer.append("','");
												sBuffer.append(packs.getUnit());
												sBuffer.append("','");
												sBuffer.append(packs.getChname());
												sBuffer.append("','");
												sBuffer.append(packs.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(packs.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												// conn.executeUpdate(sql);
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										packs = null;
									}
									tablename = null;
								}
								packsVector = null;

								// inpacks
								Vector inpacksVector = (Vector) interfacehash.get("inpacks");
								if (inpacksVector != null && inpacksVector.size() > 0) {
									String tablename = "inpacks" + allipstr;
									InPkts packs = null;
									for (int si = 0; si < inpacksVector.size(); si++) {
										try {
											packs = (InPkts) inpacksVector.elementAt(si);
											if (packs.getRestype().equals("dynamic")) {
												if (packs.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) packs.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));

												long count = 0;
												if (packs.getCount() != null) {
													count = packs.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(packs.getRestype());
												sBuffer.append("','");
												sBuffer.append(packs.getCategory());
												sBuffer.append("','");
												sBuffer.append(packs.getEntity());
												sBuffer.append("','");
												sBuffer.append(packs.getSubentity());
												sBuffer.append("','");
												sBuffer.append(packs.getUnit());
												sBuffer.append("','");
												sBuffer.append(packs.getChname());
												sBuffer.append("','");
												sBuffer.append(packs.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(packs.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										sBuffer = null;
										time = null;
										packs = null;
									}
									tablename = null;
								}
								inpacksVector = null;

								// outpacks
								Vector outpacksVector = (Vector) interfacehash.get("outpacks");
								if (outpacksVector != null && outpacksVector.size() > 0) {
									String tablename = "outpacks" + allipstr;
									OutPkts packs = null;
									for (int si = 0; si < outpacksVector.size(); si++) {
										try {
											packs = (OutPkts) outpacksVector.elementAt(si);
											if (packs.getRestype().equals("dynamic")) {
												if (packs.getThevalue().equals("0"))
													continue;
												tempCal = (Calendar) packs.getCollecttime();
												cc = tempCal.getTime();
												time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
												long count = 0;
												if (packs.getCount() != null) {
													count = packs.getCount();
												}
												sBuffer = new StringBuffer();
												sBuffer.append("insert into ");
												sBuffer.append(tablename);
												sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
												sBuffer.append("values('");
												sBuffer.append(ip);
												sBuffer.append("','");
												sBuffer.append(packs.getRestype());
												sBuffer.append("','");
												sBuffer.append(packs.getCategory());
												sBuffer.append("','");
												sBuffer.append(packs.getEntity());
												sBuffer.append("','");
												sBuffer.append(packs.getSubentity());
												sBuffer.append("','");
												sBuffer.append(packs.getUnit());
												sBuffer.append("','");
												sBuffer.append(packs.getChname());
												sBuffer.append("','");
												sBuffer.append(packs.getBak());
												sBuffer.append("','");
												sBuffer.append(count);
												sBuffer.append("','");
												sBuffer.append(packs.getThevalue());
												sBuffer.append("',");
												if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("'");
													sBuffer.append(time);
													sBuffer.append("'");
												} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
													sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
												}
												sBuffer.append(")");
												try {
													dbmanager.addBatch(sBuffer.toString());
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										} catch (Exception ex) {
											ex.printStackTrace();
										}

										sBuffer = null;
										time = null;
										packs = null;
									}
									tablename = null;
								}
								outpacksVector = null;

								interfacehash = null;
							}
						} // /////
						ipdata = null;
						ip = null;
						allipstr = null;
						nodeDTO = null;
					}
					ipIterator = null;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						dbmanager.executeBatch();
					} catch (Exception e) {
						e.printStackTrace();
					}
					dbmanager.close();
					dbmanager = null;
				}
			}
		}
		datahash = null;
		// 接口信息
		return true;
	}

	public synchronized boolean createHostItemData(String ip, Hashtable datahash, String type, String subtype, String category) {
		DBManager dbmanager = null;
		try {
			dbmanager = new DBManager();
			Hashtable hash = ShareData.getOctetsdata(ip);

			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			if (type.equalsIgnoreCase("host")) {
				if ("cpu".equalsIgnoreCase(category)) {
					// CPU
					Vector cpuVector = (Vector) datahash.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						// 得到CPU平均
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						if (cpudata.getRestype().equals("dynamic")) {
							Calendar tempCal = (Calendar) cpudata.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "cpu" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							// System.out.println(sql);
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
								// dbmanager.close();
							}
							// conn.executeUpdate(sql);
						}
					}
				} else if ("process".equalsIgnoreCase(category)) {
					// Process
					Vector proVector = (Vector) datahash.get("process");

					/*
					 * nielin add 2010-08-18
					 * 
					 * 创建进程组告警
					 * 
					 * start ===============================
					 */
					// if(proVector != null && proVector.size()>0)
					// createProcessGroupEventList(ip , proVector);
					/*
					 * nielin add 2010-08-18
					 * 
					 * 创建进程组告警
					 * 
					 * end ===============================
					 */

					if (proVector != null && proVector.size() > 0) {
						for (int i = 0; i < proVector.size(); i++) {
							ProcessCollectEntity processdata = (ProcessCollectEntity) proVector.elementAt(i);
							processdata.setCount(null);
							if (processdata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) processdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "pro" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + processdata.getRestype() + "','" + processdata.getCategory() + "','" + processdata.getEntity() + "','" + processdata.getSubentity() + "','" + processdata.getUnit() + "','" + processdata.getChname() + "','" + processdata.getBak() + "'," + processdata.getCount() + ",'" + processdata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + processdata.getRestype() + "','" + processdata.getCategory() + "','" + processdata.getEntity() + "','" + processdata.getSubentity() + "','" + processdata.getUnit() + "','" + processdata.getChname() + "','" + processdata.getBak() + "'," + processdata.getCount() + ",'" + processdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
								}
								// System.out.println(sql);
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}

							}
						}
					}
				} else if ("physicalmemory".equalsIgnoreCase(category)) {
					// physicalmemory
					Vector memoryVector = (Vector) datahash.get("memory");
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int si = 0; si < memoryVector.size(); si++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
							if (!memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory"))
								continue;
							if (memorydata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) memorydata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "memory" + allipstr;
								// if
								// (memorydata.getIpaddress().equals("10.217.255.253")||memorydata.getIpaddress().equals("10.217.255.64")||memorydata.getIpaddress().equals("10.216.2.35")){
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
								}
								System.out.println(sql);
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

					}
				} else if ("virtualmemory".equalsIgnoreCase(category)) {
					// physicalmemory
					Vector memoryVector = (Vector) datahash.get("memory");
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int si = 0; si < memoryVector.size(); si++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
							if (!memorydata.getSubentity().equalsIgnoreCase("VirtualMemory"))
								continue;
							if (memorydata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) memorydata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "memory" + allipstr;
								// if
								// (memorydata.getIpaddress().equals("10.217.255.253")||memorydata.getIpaddress().equals("10.217.255.64")||memorydata.getIpaddress().equals("10.216.2.35")){
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
								}

								// System.out.println(sql);
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

					}
				} else if ("disk".equalsIgnoreCase(category)) {
					Vector diskVector = (Vector) datahash.get("disk");
					if (diskVector != null && diskVector.size() > 0) {
						for (int si = 0; si < diskVector.size(); si++) {
							DiskCollectEntity diskdata = (DiskCollectEntity) diskVector.elementAt(si);
							if (diskdata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) diskdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "diskincre" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "','" + time + "','" + diskdata.getUnit() + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'),'" + diskdata.getUnit() + "')";
								}

								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
							if (diskdata.getEntity().equals("Utilization")) {
								Calendar tempCal = (Calendar) diskdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "disk" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "','" + time + "','" + diskdata.getUnit() + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','" + diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','" + diskdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'),'" + diskdata.getUnit() + "')";
								}
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				} else if ("interface".equalsIgnoreCase(category)) {
					// UtilHdx
					Vector utilhdxVector = (Vector) datahash.get("utilhdx");
					if (utilhdxVector != null && utilhdxVector.size() > 0) {
						for (int si = 0; si < utilhdxVector.size(); si++) {
							UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

							if (utilhdx.getRestype().equals("dynamic")) {
								// session.save(utilhdx);
								if (utilhdx.getThevalue().equals("0"))
									continue;
								Calendar tempCal = (Calendar) utilhdx.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "utilhdx" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
								}
								// System.out.println(sql);
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								// conn.executeUpdate(sql);
							}
						}
					}
				}
			} else if (type.equalsIgnoreCase("net")) {
				// 处理网络设备的数据
				if ("cpu".equalsIgnoreCase(category)) {
					// CPU
					Vector cpuVector = (Vector) datahash.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						// 得到CPU平均
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						if (cpudata.getRestype().equals("dynamic")) {
							Calendar tempCal = (Calendar) cpudata.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "cpu" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','" + cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}

							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
							}
						}
					}
				} else if ("memory".equalsIgnoreCase(category)) {
					Vector memoryVector = (Vector) datahash.get("memory");
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int si = 0; si < memoryVector.size(); si++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
							if (memorydata.getRestype().equals("dynamic")) {
								Calendar tempCal = (Calendar) memorydata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "memory" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'" + memorydata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
								}
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

					}
				} else if ("flash".equalsIgnoreCase(category)) {
					// 闪存
					Vector flashVector = (Vector) datahash.get("flash");
					if (flashVector != null && flashVector.size() > 0) {
						FlashCollectEntity flashdata = (FlashCollectEntity) flashVector.elementAt(0);
						if (flashdata.getRestype().equals("dynamic")) {
							// session.save(cpudata);
							Calendar tempCal = (Calendar) flashdata.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "flash" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + flashdata.getRestype() + "','" + flashdata.getCategory() + "','" + flashdata.getEntity() + "','" + flashdata.getSubentity() + "','" + flashdata.getUnit() + "','" + flashdata.getChname() + "','" + flashdata.getBak() + "'," + flashdata.getCount() + ",'" + flashdata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + flashdata.getRestype() + "','" + flashdata.getCategory() + "','" + flashdata.getEntity() + "','" + flashdata.getSubentity() + "','" + flashdata.getUnit() + "','" + flashdata.getChname() + "','" + flashdata.getBak() + "'," + flashdata.getCount() + ",'" + flashdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}

							// System.out.println(sql);
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
								// dbmanager.close();
							}
							// conn.executeUpdate(sql);
						}
					}
				} else if ("temperature".equalsIgnoreCase(category)) {
					// 温度
					Vector temperatureVector = (Vector) datahash.get("temperature");
					if (temperatureVector != null && temperatureVector.size() > 0) {
						// 得到温度
						Interfacecollectdata temperdata = (Interfacecollectdata) temperatureVector.elementAt(0);
						if (temperdata.getRestype().equals("dynamic")) {
							// session.save(cpudata);
							Calendar tempCal = (Calendar) temperdata.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "temper" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + temperdata.getRestype() + "','" + temperdata.getCategory() + "','" + temperdata.getEntity() + "','" + temperdata.getSubentity() + "','" + temperdata.getUnit() + "','" + temperdata.getChname() + "','" + temperdata.getBak() + "'," + temperdata.getCount() + ",'" + temperdata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + temperdata.getRestype() + "','" + temperdata.getCategory() + "','" + temperdata.getEntity() + "','" + temperdata.getSubentity() + "','" + temperdata.getUnit() + "','" + temperdata.getChname() + "','" + temperdata.getBak() + "'," + temperdata.getCount() + ",'" + temperdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							// System.out.println(sql);
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
								// dbmanager.close();
							}
							// conn.executeUpdate(sql);
						}
					}
				} else if ("fan".equalsIgnoreCase(category)) {
					// 风扇
					Vector fanVector = (Vector) datahash.get("fan");
					if (fanVector != null && fanVector.size() > 0) {
						// 得到风扇
						Interfacecollectdata fandata = (Interfacecollectdata) fanVector.elementAt(0);
						if (fandata.getRestype().equals("dynamic")) {
							// session.save(cpudata);
							Calendar tempCal = (Calendar) fandata.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "fan" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + fandata.getRestype() + "','" + fandata.getCategory() + "','" + fandata.getEntity() + "','" + fandata.getSubentity() + "','" + fandata.getUnit() + "','" + fandata.getChname() + "','" + fandata.getBak() + "'," + fandata.getCount() + ",'" + fandata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + fandata.getRestype() + "','" + fandata.getCategory() + "','" + fandata.getEntity() + "','" + fandata.getSubentity() + "','" + fandata.getUnit() + "','" + fandata.getChname() + "','" + fandata.getBak() + "'," + fandata.getCount() + ",'" + fandata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							// System.out.println(sql);
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
								// dbmanager.close();
							}
							// conn.executeUpdate(sql);
						}
					}
				} else if ("power".equalsIgnoreCase(category)) {
					// 电源
					// SysLogger.info("添加电源信息 ################"+allipstr);
					Vector powerVector = (Vector) datahash.get("power");
					if (powerVector != null && powerVector.size() > 0) {
						for (int i = 0; i < powerVector.size(); i++) {
							Interfacecollectdata powerdata = (Interfacecollectdata) powerVector.elementAt(i);
							if (powerdata.getRestype().equals("dynamic")) {
								// session.save(cpudata);
								Calendar tempCal = (Calendar) powerdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "power" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','" + powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "'," + powerdata.getCount() + ",'" + powerdata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','" + powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "'," + powerdata.getCount() + ",'" + powerdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
								}
								// SysLogger.info(sql);
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
									// dbmanager.close();
								}
								// conn.executeUpdate(sql);
							}
						}
					}
				} else if ("voltage".equalsIgnoreCase(category)) {
					// 电源
					Vector voltageVector = (Vector) datahash.get("voltage");
					if (voltageVector != null && voltageVector.size() > 0) {
						for (int i = 0; i < voltageVector.size(); i++) {
							Interfacecollectdata powerdata = (Interfacecollectdata) voltageVector.elementAt(i);
							if (powerdata.getRestype().equals("dynamic")) {
								// session.save(cpudata);
								Calendar tempCal = (Calendar) powerdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
								String tablename = "vol" + allipstr;
								String sql = "";
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','" + powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "'," + powerdata.getCount() + ",'" + powerdata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','" + powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "'," + powerdata.getCount() + ",'" + powerdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
								}
								// SysLogger.info(sql);
								try {
									dbmanager.executeUpdate(sql);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
									// dbmanager.close();
								}
								// conn.executeUpdate(sql);
							}
						}
					}
				}
			}
			if ("interface".equalsIgnoreCase(category)) {
				// SysLogger.info("###########开始处理接口数据############");
				// AllUtilHdx
				Vector allutilhdxVector = (Vector) datahash.get("allutilhdx");
				if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
					for (int si = 0; si < allutilhdxVector.size(); si++) {
						AllUtilHdx allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);

						if (allutilhdx.getRestype().equals("dynamic")) {
							if (allutilhdx.getThevalue().equals("0"))
								continue;
							Calendar tempCal = (Calendar) allutilhdx.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "allutilhdx" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity() + "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'" + allutilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}

							// System.out.println(sql);
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}

				// UtilHdxPerc
				Vector utilhdxpercVector = (Vector) datahash.get("utilhdxperc");
				if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
					for (int si = 0; si < utilhdxpercVector.size(); si++) {
						UtilHdxPerc utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);

						if (utilhdxperc.getRestype().equals("dynamic")) {
							// session.save(utilhdx);
							if (utilhdxperc.getThevalue().equals("0"))
								continue;
							Calendar tempCal = (Calendar) utilhdxperc.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "utilhdxperc" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','" + utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "'," + utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);
						}
					}
				}

				// UtilHdx
				Vector utilhdxVector = (Vector) datahash.get("utilhdx");
				// SysLogger.info("地址为"+ip+"的流速共有 "+utilhdxVector.size()+"条");
				if (utilhdxVector != null && utilhdxVector.size() > 0) {
					for (int si = 0; si < utilhdxVector.size(); si++) {
						UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(si);

						if (utilhdx.getRestype().equals("dynamic")) {
							// session.save(utilhdx);
							if (utilhdx.getThevalue().equals("0"))
								continue;
							Calendar tempCal = (Calendar) utilhdx.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "utilhdx" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							// System.out.println(sql);
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);
						}
					}
				}
				// discardsperc
				Vector discardspercVector = (Vector) datahash.get("discardsperc");
				if (discardspercVector != null && discardspercVector.size() > 0) {
					for (int si = 0; si < discardspercVector.size(); si++) {
						DiscardsPerc discardsperc = (DiscardsPerc) discardspercVector.elementAt(si);

						if (discardsperc.getRestype().equals("dynamic")) {
							if (discardsperc.getThevalue().equals("0.0"))
								continue;
							// session.save(discardsperc);
							Calendar tempCal = (Calendar) discardsperc.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "discardsperc" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + discardsperc.getRestype() + "','" + discardsperc.getCategory() + "','" + discardsperc.getEntity() + "','" + discardsperc.getSubentity() + "','" + discardsperc.getUnit() + "','" + discardsperc.getChname() + "','" + discardsperc.getBak() + "'," + discardsperc.getCount() + ",'" + discardsperc.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + discardsperc.getRestype() + "','" + discardsperc.getCategory() + "','" + discardsperc.getEntity() + "','" + discardsperc.getSubentity() + "','" + discardsperc.getUnit() + "','" + discardsperc.getChname() + "','" + discardsperc.getBak() + "'," + discardsperc.getCount() + ",'" + discardsperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);
						}
					}
				}

				// errorsperc
				Vector errorspercVector = (Vector) datahash.get("errorsperc");
				if (errorspercVector != null && errorspercVector.size() > 0) {
					for (int si = 0; si < errorspercVector.size(); si++) {
						ErrorsPerc errorsperc = (ErrorsPerc) errorspercVector.elementAt(si);
						if (errorsperc.getRestype().equals("dynamic")) {
							if (errorsperc.getThevalue().equals("0.0"))
								continue;
							// session.save(errorsperc);
							Calendar tempCal = (Calendar) errorsperc.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "errorsperc" + allipstr;
							String sql = "";
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + errorsperc.getRestype() + "','" + errorsperc.getCategory() + "','" + errorsperc.getEntity() + "','" + errorsperc.getSubentity() + "','" + errorsperc.getUnit() + "','" + errorsperc.getChname() + "','" + errorsperc.getBak() + "'," + errorsperc.getCount() + ",'" + errorsperc.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + errorsperc.getRestype() + "','" + errorsperc.getCategory() + "','" + errorsperc.getEntity() + "','" + errorsperc.getSubentity() + "','" + errorsperc.getUnit() + "','" + errorsperc.getChname() + "','" + errorsperc.getBak() + "'," + errorsperc.getCount() + ",'" + errorsperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
							}
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);

						}
					}
				}
				// packs
				Vector packsVector = (Vector) datahash.get("packs");
				if (packsVector != null && packsVector.size() > 0) {
					for (int si = 0; si < packsVector.size(); si++) {
						Packs packs = (Packs) packsVector.elementAt(si);
						if (packs.getRestype().equals("dynamic")) {
							if (packs.getThevalue().equals("0"))
								continue;
							// session.save(packs);
							Calendar tempCal = (Calendar) packs.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "packs" + allipstr;
							String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);
						}
					}
				}
			} else if ("inpacks".equalsIgnoreCase(category)) {
				// inpacks
				Vector inpacksVector = (Vector) datahash.get("inpacks");
				if (inpacksVector != null && inpacksVector.size() > 0) {
					for (int si = 0; si < inpacksVector.size(); si++) {
						InPkts packs = (InPkts) inpacksVector.elementAt(si);
						if (packs.getRestype().equals("dynamic")) {
							if (packs.getThevalue().equals("0"))
								continue;
							// session.save(packs);
							Calendar tempCal = (Calendar) packs.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "inpacks" + allipstr;
							String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);
						}
					}
				}
			} else if ("outpacks".equalsIgnoreCase(category)) {
				// inpacks
				Vector outpacksVector = (Vector) datahash.get("outpacks");
				if (outpacksVector != null && outpacksVector.size() > 0) {
					for (int si = 0; si < outpacksVector.size(); si++) {
						OutPkts packs = (OutPkts) outpacksVector.elementAt(si);
						if (packs.getRestype().equals("dynamic")) {
							if (packs.getThevalue().equals("0"))
								continue;
							// session.save(packs);
							Calendar tempCal = (Calendar) packs.getCollecttime();
							Date cc = tempCal.getTime();
							String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
							String tablename = "outpacks" + allipstr;
							String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit() + "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
							try {
								dbmanager.executeUpdate(sql);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							// conn.executeUpdate(sql);
						}
					}
				}
			}
			/*
			 * //System
			 * 
			 * Vector systemVector = (Vector)datahash.get("system"); if
			 * (systemVector != null && systemVector.size()>0){ for(int
			 * si=0;si<systemVector.size();si++){ Systemcollectdata systemdata =
			 * (Systemcollectdata)systemVector.elementAt(si);
			 * 
			 * if (systemdata.getRestype().equals("dynamic")) {
			 * session.save(systemdata); } } } this.endTransaction(true);
			 * session =super.beginTransaction();
			 */

			// 缓存
			Vector bufferVector = (Vector) datahash.get("buffer");
			if (bufferVector != null && bufferVector.size() > 0) {
				Buffercollectdata bufferdata = (Buffercollectdata) bufferVector.elementAt(0);
				if (bufferdata.getRestype().equals("dynamic")) {
					// session.save(cpudata);
					Calendar tempCal = (Calendar) bufferdata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = DbConversionUtil.coversionTimeSql(sdf.format(cc));
					String tablename = "buffer" + allipstr;
					String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + bufferdata.getRestype() + "','" + bufferdata.getCategory() + "','" + bufferdata.getEntity() + "','" + bufferdata.getSubentity() + "','" + bufferdata.getUnit() + "','" + bufferdata.getChname() + "','" + bufferdata.getBak() + "'," + bufferdata.getCount() + ",'" + bufferdata.getThevalue() + "'," + time + ")";
					// System.out.println(sql);
					try {
						dbmanager.executeUpdate(sql);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						// dbmanager.close();
					}
					// conn.executeUpdate(sql);
				}
			}

			// this.endTransaction(true);
			// session =super.beginTransaction();
			/*
			 * //Disk Vector diskVector = (Vector)datahash.get("disk"); if
			 * (diskVector != null && diskVector.size()>0){ for(int
			 * si=0;si<diskVector.size();si++){ Diskcollectdata diskdata =
			 * (Diskcollectdata)diskVector.elementAt(si); if
			 * (diskdata.getRestype().equals("dynamic")) { Calendar tempCal =
			 * (Calendar)diskdata.getCollecttime(); Date cc = tempCal.getTime();
			 * String time=util.coversionTimeSql(sdf.format(cc)); String
			 * tablename = "disk"+allipstr; String sql = "insert into
			 * "+tablename+"
			 * (ipaddress,restype,category,entity,subentity,unit,chname
			 * ,bak,count,thevalue,collecttime) "
			 * +"values('"+ip+"','"+diskdata.getRestype
			 * ()+"','"+diskdata.getCategory()+"','"+diskdata.getEntity()+"','"
			 * +diskdata.getSubentity()+"','"+diskdata.getUnit()+"','"+diskdata.
			 * getChname()+"','"+diskdata.getBak()+"',"
			 * +diskdata.getCount()+",'"
			 * +diskdata.getThevalue()+"',to_date('"+time+"','YYYY-MM-DD
			 * HH24:MI:SS'))"; //System.out.println(sql); stmt =
			 * con.prepareStatement(sql); stmt.execute(); stmt.close(); } } }
			 * 
			 * //Node Vector nodeVector = (Vector)datahash.get("node"); if
			 * (nodeVector != null && nodeVector.size()>0){ for(int
			 * si=0;si<nodeVector.size();si++){ Nodecollectdata diskdata =
			 * (Nodecollectdata)nodeVector.elementAt(si); if
			 * (diskdata.getRestype().equals("dynamic")) { Calendar tempCal =
			 * (Calendar)diskdata.getCollecttime(); Date cc = tempCal.getTime();
			 * String time=util.coversionTimeSql(sdf.format(cc)); String
			 * tablename = "disk"+allipstr; //if
			 * (diskdata.getIpaddress().equals("10.216.2.35")){ String sql =
			 * "insert into
			 * "+tablename+"(ipaddress,restype,category,entity,subentity
			 * ,unit,chname,bak,count,thevalue,collecttime) "
			 * +"values('"+ip+"','"
			 * +diskdata.getRestype()+"','"+diskdata.getCategory
			 * ()+"','"+diskdata.getEntity()+"','"
			 * +diskdata.getSubentity()+"','"
			 * +diskdata.getUnit()+"','"+diskdata.getChname
			 * ()+"','"+diskdata.getBak()+"',"
			 * +diskdata.getCount()+",'"+diskdata
			 * .getThevalue()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
			 * stmt = con.prepareStatement(sql); stmt.execute(); stmt.close();
			 * //} //session.save(diskdata); } } }
			 * 
			 * //this.endTransaction(true); //session =super.beginTransaction();
			 * 
			 * //User
			 * 
			 * Vector userVector = (Vector)datahash.get("user"); if (userVector
			 * != null && userVector.size()>0){ for(int
			 * si=0;si<userVector.size();si++){ Usercollectdata userdata =
			 * (Usercollectdata)userVector.elementAt(si); if
			 * (userdata.getRestype().equals("dynamic")) {
			 * //session.save(userdata); } } } //this.endTransaction(true);
			 * //session =super.beginTransaction();
			 */

			// ipmac
			Vector ipmacVector = (Vector) datahash.get("ipmac");
			Hashtable ipmacs = ShareData.getRelateipmacdata();
			Hashtable ipmacband = ShareData.getIpmacbanddata();
			IpMacChangeDao macchangedao = new IpMacChangeDao();
			Hashtable macHash = null;
			try {
				macHash = macchangedao.loadMacIpHash();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				macchangedao.close();
			}

			if (ipmacVector != null && ipmacVector.size() > 0) {
				// 放入内存里
				ShareData.setRelateipmacdata(ip, ipmacVector);
			}
			int firstipmac = ShareData.getFirstipmac();
			if (firstipmac == 0) {
				// 第一次启动
				// 设置变量值，并装载IP-MAC对照表
				ShareData.setFirstipmac(1);
				// 装载该IP的IP-MAC对应绑定关系表
				ResultSet rs = dbmanager.executeQuery("select * from nms_ipmacbase");
				List list = loadFromIpMacBandRS(rs);
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						IpMacBase _ipmacbase = (IpMacBase) list.get(i);
						if (ipmacband.containsKey(_ipmacbase.getMac())) {
							// 已经存在
							List existMacList = (List) ipmacband.get(_ipmacbase.getMac());
							existMacList.add(_ipmacbase);
							ipmacband.put(_ipmacbase.getMac(), existMacList);
						} else {
							List macList = new ArrayList();
							macList.add(_ipmacbase);
							ipmacband.put(_ipmacbase.getMac(), macList);
						}

					}
				}
				rs.close();

			}
			if (ipmacVector != null && ipmacVector.size() > 0) {
				// 把实时采集到的ARP表信息存放到共享数据里，然后再更新数据库
				ipmacs.put(ip, ipmacVector);
				String sql = "delete from ipmac where relateipaddr='" + ip + "'";
				try {
					dbmanager.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String _localMAC = "";
				Hashtable _AllMAC = new Hashtable();
				Hashtable _sameMAC = new Hashtable();
				Vector tempVector = new Vector();
				for (int si = 0; si < ipmacVector.size(); si++) {
					IpMac ipmac = (IpMac) ipmacVector.elementAt(si);
					if (ipmac == null)
						continue;
					if (ipmac.getIpaddress() == null)
						continue;
					if (ipmac.getIpaddress().equals(ip)) {
						// 若是本机IP
						_localMAC = ipmac.getMac();
					}
					if (!_AllMAC.containsKey(ipmac.getMac())) {
						// 若不存在次IP
						_AllMAC.put(ipmac.getMac(), ipmac);
					} else {
						// 若存在

						tempVector.add(ipmac);
						// tempVector.add(_AllIp.get(ipmac.getIpaddress()));
						_sameMAC.put(ipmac.getMac(), tempVector);
					}
				}
				for (int si = 0; si < ipmacVector.size(); si++) {
					IpMac ipmac = (IpMac) ipmacVector.elementAt(si);
					try {
						if (ipmac != null && ipmac.getMac() != null && ipmac.getMac().length() == 17) {
							dbmanager.executeUpdate(ipmacInsertSQL(ipmac));
						}
						// 需要加是否对MAC地址进行审计的判断
						/*
						 * PingUtil pingU=new PingUtil(ipmac.getIpaddress());
						 * Integer[] packet=pingU.ping();
						 * //vector=pingU.addhis(packet);
						 * SysLogger.info(ipmac.getIpaddress()+"
						 * pack====="+packet[0]); //把存活的MAC地址存入数据库nms_machistory
						 * if(packet[0]!=null){ if(packet[0] >0){
						 * //连通的MAC,将当前的MAC存入NMS_MACHISTORY表 MacHistory machis =
						 * new MacHistory();
						 * machis.setRelateipaddr(ipmac.getRelateipaddr());
						 * machis.setBak(ipmac.getBak());
						 * machis.setCollecttime(ipmac.getCollecttime());
						 * machis.setIfindex(ipmac.getIfindex());
						 * machis.setIpaddress(ipmac.getIpaddress());
						 * machis.setMac(ipmac.getMac());
						 * machis.setThevalue("1");
						 * dbmanager.executeUpdate(machistoryInsertSQL(machis));
						 * }else{ //不在线的MAC }
						 * //hostdata.setThevalue(packet[0].toString()); } else{
						 * //不通的MAC //hostdata.setThevalue("0"); //continue; }
						 */
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// session.save(ipmac);
					if (ipmac == null)
						continue;
					if (ipmac.getMac() == null)
						continue;
					if (ipmac.getMac().equalsIgnoreCase(_localMAC)) {
						// 若是本机MAC,IP不是本机，则过滤
						if (!ipmac.getIpaddress().equals(ip))
							continue;
					}
					// 同时更新IP-MAC绑定表，若IP-MAC地址变更，则采用新的IP-MAC信息，同时保存到历史变更表里
					if (ipmacband.containsKey(ipmac.getMac())) {
						List existMacList = (List) ipmacband.get(ipmac.getMac());
						if (existMacList != null && existMacList.size() > 0) {
							int changeflag = 0;
							int samenodeflag = 0;
							String ipstr = "";
							IpMacBase changeMacBase = null;
							for (int i = 0; i < existMacList.size(); i++) {
								IpMacBase temp_ipmacband = (IpMacBase) existMacList.get(i);
								ipstr = ipstr + "或" + temp_ipmacband.getRelateipaddr();
								if (temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())) {
									changeflag = 1;
								}
							}
							if (changeflag == 0) {
								// 没有相等的IP,判断从哪个设备上路过
								for (int i = 0; i < existMacList.size(); i++) {
									IpMacBase temp_ipmacband = (IpMacBase) existMacList.get(i);
									if (temp_ipmacband.getRelateipaddr().equalsIgnoreCase(ipmac.getRelateipaddr())) {
										samenodeflag = 1;
										changeMacBase = temp_ipmacband;
										break;
									}
								}
							}
							// 若存在此MAC，则判断IP是否相等
							// IpMacBase temp_ipmacband =
							// (IpMacBase)ipmacband.get(ipmac.getMac());

							// if(!temp_ipmacband.getIpaddress().equalsIgnoreCase(ipmac.getIpaddress())){
							if (changeMacBase != null) {
								// 若IP不相等，而且找到设备上路过,则用新的信息修改该IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

								String _ipaddress = changeMacBase.getIpaddress();
								changeMacBase.setIpaddress(ipmac.getIpaddress());
								// ShareData.setIpmacbanddata(ipmac.getMac(),temp_ipmacband);
								existMacList = (List) ipmacband.get(changeMacBase.getMac());
								existMacList.add(changeMacBase);
								ipmacband.put(changeMacBase.getMac(), existMacList);
								try {
									dbmanager.executeUpdate(ipmacbandUpdateSQL(changeMacBase));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (_sameMAC.containsKey(ipmac.getMac())) {
									if (changeMacBase.getIfband() == 0)
										continue;
								}

								// 在历史表里追加一条新加入的提示信息
								IpMacChange _ipmacchange = new IpMacChange();
								_ipmacchange.setMac(ipmac.getMac());
								_ipmacchange.setChangetype("2");
								_ipmacchange.setDetail("该MAC地址从IP：" + _ipaddress + " 变换到IP：" + ipmac.getIpaddress());
								_ipmacchange.setIpaddress(ipmac.getIpaddress());
								_ipmacchange.setCollecttime(ipmac.getCollecttime());
								// 对发生IP变更的要进行PING操作，若能PING通，则进行下一不操作

								Vector vector = null;
								PingUtil pingU = new PingUtil(ipmac.getIpaddress());
								Integer[] packet = pingU.ping();
								// vector=pingU.addhis(packet);
								if (packet[0] != null) {
									// hostdata.setThevalue(packet[0].toString());
								} else {
									// hostdata.setThevalue("0");
									continue;
								}

								// 若IP-MAC已经绑定，则产生事件
								if (changeMacBase.getIfband() == 1) {
									try {
										dbmanager.executeUpdate(ipmacchangeInsertSQL(_ipmacchange));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									// session.save(_ipmacchange);
									// 写事件
									/*
									 * I_Eventlist eventmanager=new
									 * EventlistManager(); Eventlist event = new
									 * Eventlist();
									 * event.setEventtype("network");
									 * //事件类型需要重新定义：网络、主机、数据库、WEB服务、FTP服务、安全产品等
									 * event
									 * .setEventlocation(temp_ipmacband.getIpaddress
									 * ()); event.setManagesign(new Integer(0));
									 * event.setReportman("monitorpc"); String
									 * time =
									 * sdf.format(ipmac.getCollecttime().getTime
									 * ());
									 * event.setRecordtime(ipmac.getCollecttime
									 * ());
									 * event.setContent(time+"&IP地址更新&"+_ipaddress
									 * +"&该MAC地址从IP："+_ipaddress+"
									 * 变换到IP："+ipmac.getIpaddress());
									 * event.setLevel1(new Integer(2)); Vector
									 * eventtmpV = new Vector();
									 * eventtmpV.add(event);
									 * eventmanager.createEventlist(eventtmpV);
									 */
								}
							} else if (changeflag == 0) {
								// 若IP不相等，但没找到相同的设备上路过,说明该MAC经过的设备发生了改变,则追加新的IP-MAC绑定表，同时在历史表里追加一条新加入的提示信息

								IpMacBase _ipmacband = new IpMacBase();
								_ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
								_ipmacband.setIfindex(ipmac.getIfindex());
								_ipmacband.setIpaddress(ipmac.getIpaddress());
								_ipmacband.setMac(ipmac.getMac());
								_ipmacband.setCollecttime(ipmac.getCollecttime());
								_ipmacband.setIfband(0);
								_ipmacband.setIfsms("0");
								_ipmacband.setEmployee_id(new Integer(0));
								_ipmacband.setRelateipaddr(ip);
								ipmacband.put(ipmac.getMac(), _ipmacband);
								try {
									dbmanager.executeUpdate(ipmacbaseInsertSQL(_ipmacband));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
									continue;
								// 在历史表里追加一条新加入的提示信息
								IpMacChange _ipmacchange = new IpMacChange();
								_ipmacchange.setMac(ipmac.getMac());
								_ipmacchange.setChangetype("1");
								_ipmacchange.setDetail("该MAC地址从设备" + ipstr + " 变换到设备" + ipmac.getRelateipaddr() + "上,IP：" + ipmac.getIpaddress());
								_ipmacchange.setIpaddress(ipmac.getIpaddress());
								_ipmacchange.setCollecttime(ipmac.getCollecttime());
								_ipmacchange.setRelateipaddr(ip);
								_ipmacchange.setIfindex(ipmac.getIfindex());

								try {
									dbmanager.executeUpdate(ipmacchangeInsertSQL(_ipmacchange));
									// session.save(_ipmacband);
								} catch (Exception ex) {
									ex.printStackTrace();
								}

							}
						}
					} else {
						// 若不存在次MAC，则为新加入的机器，则保存该IP-MAC，同时在历史表里追加一条新加入的提示信息
						/*
						 * IpMacBase _ipmacband = new IpMacBase();
						 * _ipmacband.setRelateipaddr(ipmac.getRelateipaddr());
						 * _ipmacband.setIfindex(ipmac.getIfindex());
						 * _ipmacband.setIpaddress(ipmac.getIpaddress());
						 * _ipmacband.setMac(ipmac.getMac());
						 * _ipmacband.setCollecttime(ipmac.getCollecttime());
						 * _ipmacband.setIfband(0); _ipmacband.setIfsms("0");
						 * _ipmacband.setEmployee_id(new Integer(0));
						 * _ipmacband.setRelateipaddr(ip);
						 * ipmacband.put(ipmac.getMac(),_ipmacband); try{
						 * dbmanager
						 * .executeUpdate(ipmacbandInsertSQL(_ipmacband));
						 * //session.save(_ipmacband); }catch(Exception ex){
						 * ex.printStackTrace(); }
						 */
						// 判断变更表里是否已经有该MAC地址
						if (macHash.containsKey(ipmac.getRelateipaddr() + ":" + ipmac.getIpaddress() + ":" + ipmac.getMac()))
							continue;
						// 在历史表里追加一条新加入的提示信息
						IpMacChange _ipmacchange = new IpMacChange();
						_ipmacchange.setMac(ipmac.getMac());
						_ipmacchange.setChangetype("1");
						_ipmacchange.setDetail("新增加的IP-MAC");
						_ipmacchange.setIpaddress(ipmac.getIpaddress());
						_ipmacchange.setCollecttime(ipmac.getCollecttime());
						_ipmacchange.setRelateipaddr(ip);
						_ipmacchange.setIfindex(ipmac.getIfindex());

						try {
							if (ipmac.getMac().length() == 17) {
								dbmanager.executeUpdate(ipmacchangeInsertSQL(_ipmacchange));
							}
							// session.save(_ipmacband);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// session.save(_ipmacchange);
					}
				}
			}

			// iprouter
			Vector iprouterVector = (Vector) datahash.get("iprouter");
			if (iprouterVector != null && iprouterVector.size() > 0) {
				// 放入内存里
				ShareData.setIprouterdata(ip, iprouterVector);
			}
			if (iprouterVector != null && iprouterVector.size() > 0) {
				String sql = "delete from iprouter where relateipaddr='" + ip + "'";
				// dbmanager.executeUpdate(sql);
				for (int si = 0; si < iprouterVector.size(); si++) {
					IpRouter iprouter = (IpRouter) iprouterVector.elementAt(si);
					try {
						// session.save(iprouter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// policydata
			Hashtable policyHash = (Hashtable) datahash.get("policys");
			if (policyHash != null && policyHash.size() > 0) {
				// 放入内存里
				ShareData.setPolicydata(ip, policyHash);
			}
			/*
			 * 
			 * //tosdata List tosrouteList = (List)datahash.get("tosroute"); if
			 * (tosrouteList != null && tosrouteList.size()>0){ //放入内存里
			 * ShareData.setTosroutedata(ip, tosrouteList); }
			 * 
			 * //dominodata Hashtable dominoHash =
			 * (Hashtable)datahash.get("dominohash"); if (dominoHash != null &&
			 * dominoHash.size()>0){ //放入内存里
			 * ShareData.setDominodata(ip,dominoHash); }
			 * 
			 * //Memory DominoMem dominoMem = new DominoMem(); if (dominoHash ==
			 * null)dominoHash=new Hashtable(); if(dominoHash.get("Mem") !=
			 * null) dominoMem = (DominoMem)dominoHash.get("Mem");
			 * 
			 * //Vector memoryVector = (Vector)datahash.get("memory"); if
			 * (dominoMem != null){ Calendar tempCal = Calendar.getInstance();
			 * Date cc = tempCal.getTime(); String
			 * time=time=time=time=time=time=
			 * time=time=time=time=time=util.coversionTimeSql(sdf.format(cc));
			 * String tablename = "domi"+allipstr; //已使用内存 String sql = "insert
			 * into
			 * "+tablename+"(ipaddress,restype,category,entity,subentity,unit
			 * ,chname,bak,count,thevalue,collecttime) "
			 * +"values('"+ip+"','dynamic','Memory','Utilization','"
			 * +"Allocate','M','','',"
			 * +"0,'"+dominoMem.getMemAllocate()+"',to_date('"
			 * +time+"','YYYY-MM-DD HH24:MI:SS'))"; //System.out.println(sql);
			 * //stmt = con.prepareStatement(sql); //stmt.execute();
			 * //stmt.close(); //物理内存 sql = "insert into
			 * "+tablename+"(ipaddress,
			 * restype,category,entity,subentity,unit,chname
			 * ,bak,count,thevalue,collecttime) "
			 * +"values('"+ip+"','dynamic','Memory','Utilization','"
			 * +"Physical','M','','',"
			 * +"0,'"+dominoMem.getMemPhysical()+"',to_date('"
			 * +time+"','YYYY-MM-DD HH24:MI:SS'))"; //System.out.println(sql);
			 * //stmt = con.prepareStatement(sql); //stmt.execute();
			 * //stmt.close(); } this.endTransaction(true); session
			 * =super.beginTransaction();
			 * 
			 * 
			 * //}
			 */
			/*
			 * Hashtable pingData = ShareData.getPingdata(); Vector tempV = new
			 * Vector(); if (pingData != null && pingData.size()>0){ tempV =
			 * (Vector)pingData.get(ip); if (tempV != null && tempV.size()>0){
			 * for(int i=0;i<tempV.size();i++){ Pingcollectdata pingdata =
			 * (Pingcollectdata) tempV.elementAt(i); } } } //session.flush();
			 * this.endTransaction(true);
			 */
			/*
			 * //检查进程 if (proArray != null) { for (int procount = 0; procount <
			 * proArray.length; procount++) { String proname =
			 * proArray[procount]; Otherservicedata otherdata = new
			 * Otherservicedata();
			 * otherdata.setIpaddress(prodata.getIpaddress());
			 * otherdata.setCategory("ProcessTest");
			 * otherdata.setServicename(proname);
			 * otherdata.setCollecttime(prodata.getCollecttime()); if
			 * (probool[procount]) otherdata.setIs_valid(new Integer(1)); if
			 * (!probool[procount]) otherdata.setIs_valid(new Integer(0));
			 * v.add(otherdata); } I_Otherservicedata servicedataManager = new
			 * OtherservicedataManager();
			 * servicedataManager.createServiceData(v); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbmanager.close();
			dbmanager = null;
			datahash = null;
			System.gc();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#deleteHostData(java.lang.String
	 * )
	 */
	public boolean deleteHostData(String deletetime) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#getByHostdataid(java.lang.
	 * Integer)
	 */
	public Hostcollectdata getByHostdataid(Integer hostdataid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#getByIpaddress(java.lang.String
	 * , java.lang.String)
	 */
	public List getByIpaddress(String ipaddress, String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#getByIpCategory(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	public List getByIpCategory(String ipaddress, String Category, String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#getByIpCategoryEntity(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public List getByIpCategoryEntity(String ipaddress, String Category, String entity, String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#getBySearch(java.lang.String,
	 * java.lang.String)
	 */
	public List getBySearch(String searchfield, String searchkeyword) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dhcc.webnms.host.api.I_HostCollectData#getHostcollectdata()
	 */
	public List getHostcollectdata() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dhcc.webnms.host.api.I_HostCollectData#getHostcollectdata(java.lang
	 * .String)
	 */
	public List getHostcollectdata(String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	// max unit list
	public Hashtable getCategory1(String ip, String category, String subentity, String year, String month) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				if (category.equals("Ping") || category.equals("CPU") || category.equals("Memory") || category.equals("dominoCpu") || category.equals("domplatmem") || category.equals("domservmem") || category.equals("Process") || category.equals("MqPing") || category.equals("DomPing") || category.equals("Pings")) {
					sid = SysUtil.doip(ip);
				}

				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (category.equals("Ping")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from ping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from ping" + sid + " h where ");
					}
				} else if (category.equals("Pings")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from pings" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from pings" + sid + " h where ");
					}
				} else if (category.equals("ORAPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from oraping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from oraping" + sid + " h where ");
					}
				} else if (category.equals("SQLPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from sqlping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from sqlping" + sid + " h where ");
					}

				} else if (category.equals("DB2Ping")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from db2ping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from db2ping" + sid + " h where ");
					}

				} else if (category.equals("SYSPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from sysping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from sysping" + sid + " h where ");
					}

				} else if (category.equals("MYPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from myping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from myping" + sid + " h where ");
					}

				} else if (category.equals("INFORMIXPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from informixping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from informixping" + sid + " h where ");
					}

				} else if (category.equals("CPU")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from cpu" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from cpu" + sid + " h where ");
					}

				} else if (category.equals("Memory")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from memory" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from memory" + sid + " h where ");
					}

				} else if (category.equals("dominoCpu")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from dominocpu" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from dominocpu" + sid + " h where ");
					}

				} else if (category.equals("domplatmem")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from domplatmem" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from domplatmem" + sid + " h where ");
					}

				} else if (category.equals("domservmem")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from domservmem" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from domservmem" + sid + " h where ");
					}

				} else if (category.equals("Process")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from pro" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from pro" + sid + " h where ");
					}

				} else if (category.equals("MqPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from mqping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from mqping" + sid + " h where ");
					}

				} else if (category.equals("DomPing")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from dominoping" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from dominoping" + sid + " h where ");
					}

				} else if (category.equals("rpower")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rpower" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rpower" + sid + " h where ");
					}

				}
				sb.append(" h.category='");
				if (category.equalsIgnoreCase("Pings")) {
					sb.append("ping");
				} else {
					sb.append(category);
				}
				sb.append("' and h.subentity='");
				sb.append(subentity);
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime asc");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('");
					sb.append(starttime);
					sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
					sb.append(endtime);
					sb.append("','YYYY-MM-DD HH24:MI:SS')");
					sb.append(" order by h.collecttime asc");
				}

				sql = sb.toString();

				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				double pingMaxFloat = 0;
				double pingcon = 0;
				double cpucon = 0;
				double memory = 0;
				int downnum = 0;
				int i = 0;
				if (rs != null) {
					while (rs.next()) {
						i = i + 1;
						Vector v = new Vector();
						String thevalue = rs.getString("thevalue");
						String collecttime = rs.getString("collecttime");
						v.add(0, emitStr(thevalue));
						v.add(1, collecttime);
						v.add(2, rs.getString("unit"));
						if (category.equals("Ping") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("Ping") && subentity.equalsIgnoreCase("ResponseTime")) {
							pingcon = pingcon + getfloat(thevalue);
						} else if (category.equals("Pings") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("Pings") && subentity.equalsIgnoreCase("ResponseTime")) {
							pingcon = pingcon + getfloat(thevalue);
						} else if (category.equals("ORAPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("SQLPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("DB2Ping") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("SYSPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("INFORMIXPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("MYPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("MqPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						} else if (category.equals("DomPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
							pingcon = pingcon + getfloat(thevalue);
							if (thevalue.equals("0")) {
								downnum = downnum + 1;
							}
						}
						if (subentity.equalsIgnoreCase("ConnectUtilization")) {
							if (i == 1) {
								tempfloat = getfloat(thevalue);
								pingMaxFloat = getfloat(thevalue);
							}
							if (tempfloat > getfloat(thevalue))
								tempfloat = getfloat(thevalue);
							if (pingMaxFloat < getfloat(thevalue))
								pingMaxFloat = getfloat(thevalue);

						} else if (subentity.equalsIgnoreCase("ResponseTime")) {
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);
						} else if (category.equalsIgnoreCase("CPU")) {
							cpucon = cpucon + getfloat(thevalue);
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);
						} else if (category.equalsIgnoreCase("Memory")) {
							memory = memory + getfloat(thevalue);
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);
						} else if (category.equalsIgnoreCase("dominoCpu")) {
							cpucon = cpucon + getfloat(thevalue);
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);
						} else if (category.equalsIgnoreCase("domplatmem")) {
							memory = memory + getfloat(thevalue);
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);
						} else if (category.equalsIgnoreCase("domservmem")) {
							memory = memory + getfloat(thevalue);
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);
						} else {
							if (thevalue.equals("null"))
								thevalue = "5";
							if (tempfloat < getfloat(thevalue))
								tempfloat = getfloat(thevalue);

						}
						list1.add(v);
					}
					rs.close();
				}
				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector) list1.get(0);
						unit = (String) tempV.get(2);
					}
				}
				if ((category.equals("Ping") || category.equals("ORAPing") || category.equals("DB2Ping") || category.equals("SYSPing") || category.equals("SQLPing") || category.equals("INFORMIXPing") || category.equals("MYPing") || category.equals("MqPing") || category.equals("DomPing")) && subentity.equalsIgnoreCase("ConnectUtilization") || category.equals("Pings")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("pingMax", pingMaxFloat + "");// 最大连通率
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("pingMax", "0.0%");
						hash.put("downnum", "0");
					}
				} else if (category.equals("Ping") && subentity.equalsIgnoreCase("ResponseTime")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				} else if (category.equals("Pings") && subentity.equalsIgnoreCase("ResponseTime")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				}
				if (category.equals("CPU")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgcpucon", CEIString.round(cpucon / list1.size(), 2) + unit);
					} else {
						hash.put("avgcpucon", "0.0%");
					}
				} else if (category.equals("Memory")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgmemory", CEIString.round(memory / list1.size(), 2) + unit);
					} else {
						hash.put("avgmemory", "0.0%");
					}
				} else if (category.equals("dominoCpu")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgcpucon", CEIString.round(memory / list1.size(), 2) + unit);
					} else {
						hash.put("avgcpucon", "0.0%");
					}
				} else if (category.equals("domplatmem")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgmemory", CEIString.round(memory / list1.size(), 2) + unit);
					} else {
						hash.put("avgmemory", "0.0%");
					}
				} else if (category.equals("domservmem")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgmemory", CEIString.round(memory / list1.size(), 2) + unit);
					} else {
						hash.put("avgmemory", "0.0%");
					}
				}

				hash.put("size", size);
				hash.put("max", CEIString.round(tempfloat, 2) + unit);
				hash.put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getMemory(String ip, String category, String starttime, String endtime) throws Exception {
		ResultSet rs = null;
		Hashtable[] hash = new Hashtable[3];
		hash[0] = new Hashtable(); // 放图的y值
		hash[1] = new Hashtable(); // 放最大值
		hash[2] = new Hashtable(); // 放平均值
		DBManager dbmanager = new DBManager();

		try {
			String allipstr = SysUtil.doip(ip);
			String sql1 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select max(convert(h.thevalue,SIGNED)) as value,h.subentity,avg(convert(h.thevalue,SIGNED)) as avgvalue from memory" + allipstr + " h " + "where  h.category='Memory' and h.collecttime >= '" + starttime + "' " + "and h.collecttime <= '" + endtime + "' and h.thevalue != 'NaN' " + "group by h.subentity order by h.subentity";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select max(h.thevalue) as value,h.subentity,avg(h.thevalue) as avgvalue from memory" + allipstr + " h where " + " h.category='" + category + "' and h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') and h.thevalue != 'NaN' group by h.subentity order by h.subentity";
			}
			rs = dbmanager.executeQuery(sql1);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					Vector v = new Vector();
					String thevalue = rs.getString("value");
					String subentity = rs.getString("subentity");
					String avgvalue = rs.getString("avgvalue");
					v.add(0, thevalue);
					v.add(1, subentity);
					v.add(2, avgvalue);
					list1.add(v);
				}
			}
			if (list1.size() != 0) {
				String[] key = new String[list1.size()];
				String[] max = new String[list1.size()];
				String[] avg = new String[list1.size()];
				Vector[] vector = new Vector[list1.size()];
				for (int i = 0; i < list1.size(); i++) {
					Vector row = (Vector) list1.get(i);
					key[i] = (String) row.get(1);
					max[i] = (String) row.get(0);
					avg[i] = (String) row.get(2);
					vector[i] = new Vector();
				}
				List list2 = new ArrayList();
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (SystemConstant.DBType.equals("mysql")) {
					sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from memory" + allipstr + " h where ");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from memory" + allipstr + " h where ");
				}

				sb.append(" h.category='");
				sb.append(category);
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
				}

				sql = sb.toString();
				dbmanager.close();
				dbmanager = new DBManager();
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, rs.getString("subentity"));
					list2.add(v);
					v = new Vector();
					v.add(0, "50");
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, "一级阀值");
					list2.add(v);
					v = new Vector();
					v.add(0, "80");
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, "二级阀值");
					list2.add(v);
					v = new Vector();
					v.add(0, "90");
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, "三级阀值");
					list2.add(v);
				}
				rs.close();
				for (int i = 0; i < list2.size(); i++) {
					Vector obj = (Vector) list2.get(i);
					for (int j = 0; j < list1.size(); j++) {
						if (((String) obj.get(3)).equals(key[j])) {
							vector[j].add(obj);
						}
					}
				}
				String unit = "";
				if (list2.get(0) != null) {
					Vector obj = (Vector) list2.get(0);
					unit = (String) obj.get(2);
				}
				for (int i = 0; i < list1.size(); i++) {
					hash[0].put(key[i], vector[i]);
					hash[1].put(key[i], dofloat(max[i]) + unit);
					hash[2].put(key[i], dofloat(avg[i]) + unit);
				}
				hash[0].put("unit", unit);
				hash[0].put("key", key);
				if (category.equalsIgnoreCase("disk")) {
					hash[1].put("key", key);
				}
			}
			dbmanager.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable getCpuDetail(String ip, String starttime, String endtime) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
			String sql1 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select h.subentity,max(h.thevalue) as `maxvalue`,avg(h.thevalue) as `avgvalue` from cpudtl" + allipstr + " h where " + " h.collecttime >= '" + starttime + "' and h.collecttime <= '" + endtime + "' and h.thevalue != 'NaN' group by h.subentity order by h.subentity";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select h.subentity,max(h.thevalue) as maxvalue,avg(h.thevalue) as avgvalue from cpudtl" + allipstr + " h where " + " h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') and h.thevalue != 'NaN' group by h.subentity order by h.subentity";
			}
			rs = dbmanager.executeQuery(sql1);
			while (rs.next()) {
				Hashtable valuehash = new Hashtable();
				String maxvalue = rs.getString("maxvalue");
				String subentity = rs.getString("subentity");
				String avgvalue = rs.getString("avgvalue");
				valuehash.put("maxvalue", maxvalue);
				valuehash.put("avgvalue", avgvalue);
				hash.put(subentity, valuehash);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	public Hashtable getCpuHistroy(String ip, String category, String subentity, String starttime, String endtime) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		List list = new ArrayList();
		try {
			String allipstr = SysUtil.doip(ip);
			String sql1 = "";

			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select h.thevalue value,hour(h.collecttime) h1,minute(h.collecttime) m from cpu" + allipstr + " h where " + " h.collecttime >= '" + starttime + "' and h.collecttime <= '" + endtime + "' and h.thevalue != 'NaN' and  h.category='" + category + "' and h.subentity='" + subentity + "' order by h.collecttime";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select h.thevalue value,hour(h.collecttime) h1,minute(h.collecttime) m from cpu" + allipstr + " h where " + " h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') and h.thevalue != 'NaN' and  h.category='" + category + "' and h.subentity='" + subentity + "' order by h.collecttime";
			}
			rs = dbmanager.executeQuery(sql1);

			int index = 0;
			int minInt = 0;
			int hourInt = 0;
			int tempMin = 0;
			int tempHour = 0;
			boolean flag = false;
			while (rs.next()) {
				Hashtable valuehash = new Hashtable();
				String value = rs.getString("value");
				String hour = rs.getString("h1");
				String minute = rs.getString("m");
				Vector<String> vec = new Vector<String>();
				minInt = Integer.parseInt(minute);
				hourInt = Integer.parseInt(hour);

				if (tempMin != 0 && tempHour != minInt) {
					if (minInt < 59) {
						for (int i = minInt; i <= 59; i++) {
							if (i < 10) {
								vec.add(hour + ":0" + i);
							} else {
								vec.add(hour + ":" + i);
							}
							vec.add(value);
							list.add(vec);
						}
					} else {
						for (int i = tempMin; i < minInt; i++) {
							if (i < 10) {
								vec.add(hour + ":0" + i);
							} else {
								vec.add(hour + ":" + i);
							}
							vec.add(value);
							list.add(vec);
						}
					}
					// }
				} else {
					for (int i = tempMin; i < minInt; i++) {
						if (i < 10) {
							vec.add(hour + ":0" + i);
						} else {
							vec.add(hour + ":" + i);
						}
						vec.add(value);
						list.add(vec);
					}
				}
				tempMin = minInt;
				tempHour = hourInt;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		hash.put("list", list);
		return hash;
	}

	public Hashtable getPageingDetail(String ip, String starttime, String endtime) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {

			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql1 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select h.subentity,max(convert(h.thevalue,SIGNED)) as `maxvalue`,avg(convert(h.thevalue,SIGNED)) as `avgvalue` from pgused" + allipstr + " h where " + " h.collecttime >= '" + starttime + "' and h.collecttime <= '" + endtime + "' and h.thevalue != 'NaN' group by h.subentity order by h.subentity";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select h.subentity,max(h.thevalue) as maxvalue,avg(h.thevalue) as avgvalue from pgused" + allipstr + " h where " + " h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') and h.thevalue != 'NaN' group by h.subentity order by h.subentity";
			}
			// stmt = con.prepareStatement(sql1);
			rs = dbmanager.executeQuery(sql1);
			if (null != rs)
				while (rs.next()) {
					String maxvalue = rs.getString("maxvalue");
					String avgvalue = rs.getString("avgvalue");
					hash.put("maxvalue", maxvalue);
					hash.put("avgvalue", avgvalue);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	public Hashtable[] getCategoryDayAndMonth(String ip, String category, String starttime, String endtime, String tablename) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable(); // 放图的y值
		hash[1] = new Hashtable(); // 放最大值

		return hash;
	}

	private String dofloat(String num) {
		String snum = "0.0";
		if (num != null) {
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = Double.toString(inum / 100.0);
		}
		return snum;
	}

	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum / 100.0).doubleValue();
		}
		return snum;
	}

	private String emitStr(String num) {
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
		}
		return num;
	}

	public Hashtable[] getBand(String ip, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		hash[1] = new Hashtable(); // 最值

		return hash;
	}

	public Hashtable[] getBand_AllUtilHdxPerc(String ip, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		hash[1] = new Hashtable(); // 最值

		return hash;
	}

	public Hashtable[] getBand_AllUtilHdx(String ip, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		hash[1] = new Hashtable(); // 最值

		return hash;
	}

	public Vector[] getIfStatus(String ipaddress, String index, String starttime, String endtime) throws Exception {
		Vector[] vector = new Vector[2];
		vector[0] = new Vector();
		vector[1] = new Vector();

		return vector;
	}

	public Hashtable[] getIfBand(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		hash[1] = new Hashtable(); // 最值

		return hash;
	}

	public Hashtable[] getIfBand_UtilHdxPerc(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		hash[1] = new Hashtable(); // 最值
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		return hash;
	}

	public Hashtable[] getDiscardsPerc(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		DBManager dbmanager = new DBManager();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");
			String sql1 = "";
			String sql2 = "";
			// Query query = session.createQuery(sql2);
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select max(h.thevalue),h.entity from DiscardsPerc h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' " + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();
				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from discardsperc" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select max(h.thevalue),h.entity from DiscardsPerc h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') " + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();
				sql2 = "select to_char(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from discardsperc" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			}
			List list2 = new ArrayList();
			// stmt = con.prepareStatement(sql2);
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				// v.add(3,rs.getString("unit"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			Calendar curCal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date cc = curCal.getTime();
			String curdate = sdf1.format(cc);
			String currentTime = sdf.format(cc);

			int curHour = curCal.get(Calendar.HOUR_OF_DAY);
			// System.out.println("curHour : "+curHour);
			for (int i = 0; i <= curHour; i++) {
				String curTime = curdate + " " + i + ":00:00";
				Vector tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "InDiscardsPerc");
				vector[0].add(tempV);
				tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "OutDiscardsPerc");
				vector[1].add(tempV);
				// Date currentDate = sdf.parse(curTime);
			}
			Vector tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, currentTime);
			tempV.add(2, "InDiscardsPerc");
			vector[0].add(tempV);
			tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, currentTime);
			tempV.add(2, "OutDiscardsPerc");
			vector[1].add(tempV);

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "%";

			for (int i = 0; i < 2; i++) {
				// Object[] row = (Object[]) list1.get(i);
				// hash[1].put((String) row[1], dofloat((String) row[0]) +
				// unit);
				hash[0].put(bandch[i], vector[i]);
			}
			// if (list2.size() != 0) {
			hash[0].put("key", bandch);
			hash[0].put("unit", unit);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getErrorsPerc(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		// Connection con = null;
		// PreparedStatement stmt = null;
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");
			String sql1 = "";
			String sql2 = "";

			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select max(h.thevalue),h.entity from ErrorsPerc h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "'" + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();

				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from errorsperc" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select max(h.thevalue),h.entity from ErrorsPerc h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss')" + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();

				sql2 = "select to_char(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from errorsperc" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			}
			// Query query = session.createQuery(sql2);
			List list2 = new ArrayList();
			// stmt = con.prepareStatement(sql2);
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				// v.add(3,rs.getString("unit"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "%";
			Calendar curCal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date cc = curCal.getTime();
			String curdate = sdf1.format(cc);

			int curHour = curCal.get(Calendar.HOUR_OF_DAY);
			// System.out.println("curHour : "+curHour);
			for (int i = 0; i <= curHour; i++) {
				String curTime = curdate + " " + i + ":00:00";
				Vector tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "InErrorsPerc");
				vector[0].add(tempV);
				tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "OutErrorsPerc");
				vector[1].add(tempV);
			}
			Vector tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, sdf.format(cc));
			tempV.add(2, "InErrorsPerc");
			vector[0].add(tempV);
			tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, sdf.format(cc));
			tempV.add(2, "OutErrorsPerc");
			vector[1].add(tempV);

			for (int i = 0; i < 2; i++) {
				// Object[] row = (Object[]) list1.get(i);
				// hash[1].put((String) row[1], dofloat((String) row[0]) +
				// unit);
				hash[0].put(bandch[i], vector[i]);
			}
			// if (list2.size() != 0) {
			hash[0].put("key", bandch);
			hash[0].put("unit", unit);
			// }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getIfBand_UtilHdx(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		return hash;
	}

	public Hashtable[] getIfBand_Packs(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;

		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");
			String sql1 = "";
			String sql2 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select max(h.thevalue),h.entity from Packs h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' " + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();

				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from packs" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "' ";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select max(h.thevalue),h.entity from Packs h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >=to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') " + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();

				sql2 = "select to_char(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from packs" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >=to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "','yyyy-mm-dd hh24:mi:ss') ";
			}
			// Query query = session.createQuery(sql2);
			List list2 = new ArrayList();
			// stmt = con.prepareStatement(sql2);
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				// v.add(3,rs.getString("unit"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "个";

			for (int i = 0; i < 2; i++) {
				// Object[] row = (Object[]) list1.get(i);
				// hash[1].put((String) row[1], (String) row[0] + unit);
				hash[0].put(bandch[i], vector[i]);
			}
			if (list2.size() != 0) {
				hash[0].put("key", bandch);
				hash[0].put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getIfBand_InPacks(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;

		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");

			String sql2 = "";

			if (SystemConstant.DBType.equals("mysql")) {
				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from inpacks" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "' ";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql2 = "select to_char(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from inpacks" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >=to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "','yyyy-mm-dd hh24:mi:ss') ";
			}
			// Query query = session.createQuery(sql2);
			List list2 = new ArrayList();
			// stmt = con.prepareStatement(sql2);
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				// v.add(3,rs.getString("unit"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "个";

			for (int i = 0; i < 2; i++) {
				// Object[] row = (Object[]) list1.get(i);
				// hash[1].put((String) row[1], (String) row[0] + unit);
				hash[0].put(bandch[i], vector[i]);
			}
			if (list2.size() != 0) {
				hash[0].put("key", bandch);
				hash[0].put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getIfBand_OutPacks(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;

		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");

			String sql2 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from outpacks" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "' ";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql2 = "select to_char(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from outpacks" + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >=to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "','yyyy-mm-dd hh24:mi:ss') ";
			}
			// Query query = session.createQuery(sql2);
			List list2 = new ArrayList();
			// stmt = con.prepareStatement(sql2);
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				// v.add(3,rs.getString("unit"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "个";

			for (int i = 0; i < 2; i++) {
				// Object[] row = (Object[]) list1.get(i);
				// hash[1].put((String) row[1], (String) row[0] + unit);
				hash[0].put(bandch[i], vector[i]);
			}
			if (list2.size() != 0) {
				hash[0].put("key", bandch);
				hash[0].put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public String[] getAvailability(String ipaddress, String starttime, String endtime) throws Exception {
		String[] availability = { "", "" };

		return availability;
	}

	public Hashtable getbandutil(String ip, String orderflag, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();

		// System.out.println("in imple host availability="+availability[0]+"
		// availability="+availability[1]);
		return hash;
	}

	public Hashtable getbandutil_sub(String ip, String orderflag, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();

		// System.out.println("in imple host availability="+availability[0]+"
		// availability="+availability[1]);
		return hash;
	}

	public List loadFromIpMacBandRS(ResultSet rs) {
		if (rs == null) {
			return null;
		}
		List list = new ArrayList();
		try {
			while (rs.next()) {
				try {
					IpMacBase vo = new IpMacBase();

					vo.setId(new Long(rs.getInt("id")));
					vo.setIpaddress(rs.getString("ipaddress"));
					vo.setRelateipaddr(rs.getString("relateipaddr"));
					vo.setIfindex(rs.getString("ifindex"));
					vo.setMac(rs.getString("mac"));
					vo.setIfband(rs.getInt("ifband"));
					vo.setIfsms(rs.getString("ifsms"));
					Date timestamp = rs.getTimestamp("collecttime");
					Calendar date = Calendar.getInstance();
					date.setTime(timestamp);
					vo.setCollecttime(date);
					vo.setEmployee_id(rs.getInt("employee_id"));
					vo.setIftel(rs.getString("iftel"));
					vo.setIfemail(rs.getString("ifemail"));

					list.add(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;

	}

	public String ipmacInsertSQL(IpMac ipmac) {
		// SysLogger.info(ipmac.getMac());
		String sql = "";
		String time = sdf.format(ipmac.getCollecttime().getTime());
		sql = "insert into ipmac(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms)values('";
		sql = sql + ipmac.getRelateipaddr() + "','" + ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
		sql = sql + ipmac.getMac() + "','" + time + "','" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "')";
		return sql;
	}

	public String machistoryInsertSQL(MacHistory ipmac) {
		String sql = "";
		String time = sdf.format(ipmac.getCollecttime().getTime());
		sql = "insert into nms_machistory(relateipaddr,ifindex,ipaddress,mac,collecttime,thevalue)values('";
		sql = sql + ipmac.getRelateipaddr() + "','" + ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
		sql = sql + ipmac.getMac() + "','" + time + "','" + ipmac.getThevalue() + "')";
		return sql;
	}

	public String ipmacbandInsertSQL(IpMacBand ipmacband) {
		String sql = "";
		String time = sdf.format(ipmacband.getCollecttime().getTime());
		sql = "insert into ipmacband(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms,employee_id)values('";
		sql = sql + ipmacband.getRelateipaddr() + "','" + ipmacband.getIfindex() + "','" + ipmacband.getIpaddress() + "','";
		sql = sql + ipmacband.getMac() + "','" + time + "','" + ipmacband.getIfband() + "','" + ipmacband.getIfsms() + "'," + ipmacband.getEmployee_id() + ")";
		return sql;
	}

	public String ipmacbaseInsertSQL(IpMacBase ipmacband) {
		String sql = "";
		String time = sdf.format(ipmacband.getCollecttime().getTime());
		sql = "insert into nms_ipmacbase(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms,iftel,ifemail,employee_id)values('";
		sql = sql + ipmacband.getRelateipaddr() + "','" + ipmacband.getIfindex() + "','" + ipmacband.getIpaddress() + "','";
		sql = sql + ipmacband.getMac() + "','" + time + "','" + ipmacband.getIfband() + "','" + ipmacband.getIfsms() + "','" + ipmacband.getIftel() + "','" + ipmacband.getIfemail() + "'," + ipmacband.getEmployee_id() + ")";
		return sql;
	}

	public String ipmacbandUpdateSQL(IpMacBase ipmacband) {
		String sql = "";
		String time = sdf.format(ipmacband.getCollecttime().getTime());
		sql = "update ipmacband set ";
		sql = sql + "relateipaddr='" + ipmacband.getRelateipaddr() + "',ifindex='" + ipmacband.getIfindex() + "',ipaddress='" + ipmacband.getIpaddress() + "',";
		sql = sql + "mac='" + ipmacband.getMac() + "',collecttime='" + time + "',ifband='" + ipmacband.getIfband() + "',ifsms='" + ipmacband.getIfsms() + "',employee_id=" + ipmacband.getEmployee_id();
		sql = sql + " where id =" + ipmacband.getId();
		return sql;
	}

	public String ipmacchangeInsertSQL(IpMacChange ipmacchange) {
		String sql = "";
		String time = sdf.format(ipmacchange.getCollecttime().getTime());
		sql = "insert into nms_ipmacchange(ipaddress,mac,collecttime,detail,changetype,relateipaddr,ifindex)values('";
		sql = sql + ipmacchange.getIpaddress() + "','";
		sql = sql + ipmacchange.getMac() + "','" + time + "','" + ipmacchange.getDetail() + "','" + ipmacchange.getChangetype() + "','" + ipmacchange.getRelateipaddr() + "','" + ipmacchange.getIfindex() + "')";
		return sql;
	}

	// zhushouzhi------------------------start
	public Hashtable getAllutilhdx(String ip, String subentity, String starttime, String endtime, String need) {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		if (!starttime.equals("") && !endtime.equals("")) {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from allutilhdx" + allipstr + " h where ");
				sb.append("h.subentity='");

				sb.append(subentity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");

				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from allutilhdx" + allipstr + " h where ");
				sb.append("h.subentity='");

				sb.append(subentity);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double maxfloat = 0;
			double minfloat = 10000000;
			double tempfloat = 0;
			double avgput = 0;

			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					tempfloat = Double.parseDouble(thevalue);
					if (need.equals("max")) {
						if (i == 1)
							maxfloat = getfloat(thevalue);
					}
					if (minfloat > Double.parseDouble(thevalue))
						minfloat = Double.parseDouble(thevalue);
					if (maxfloat < getfloat(thevalue))
						maxfloat = getfloat(thevalue);
					list1.add(v);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				dbmanager.close();
			}
			// stmt.close();
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(maxfloat, 2) + "");
			hash.put("min", CEIString.round(minfloat, 2) + "");
			hash.put("temp", CEIString.round(tempfloat, 2) + "");
		}
		return hash;
	}

	// zhushouzhi-----------------------------end

	// yangjun------------------------start
	public Hashtable getAllutilhdx(String ip, String subentity, String starttime, String endtime, String need, String time) {
		Hashtable hash = new Hashtable();
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		if (!starttime.equals("") && !endtime.equals("")) {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			String tablename = "";

			if ("hour".equalsIgnoreCase(time)) {
				time = "h";
				tablename = "autilhdxh";
			} else if ("day".equalsIgnoreCase(time)) {
				time = "d";
				tablename = "autilhdxd";
			} else {
				tablename = "allutilhdx";
			}
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from " + tablename + allipstr + " h where ");
				sb.append("h.subentity='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
				sql = sb.toString();
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from " + tablename + allipstr + " h where ");
				sb.append(" h.subentity='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
				sql = sb.toString();
			}

			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;

			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (need.equals("max")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
					}
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			// stmt.close();
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public Hashtable getPingData(String ip, String subentity, String starttime, String endtime, String need, String time) {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from ping" + time + allipstr + " h where ");
				sb.append("h.subentity='");

				sb.append(subentity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");

				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from ping" + time + allipstr + " h where ");
				sb.append("h.subentity='");

				sb.append(subentity);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;

			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (need.equals("max")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
					}
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	/*
	 * 获取设备换页率明细
	 */
	public Hashtable getPgusedData(String ip, String starttime, String endtime, String need, String time) {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if (SystemConstant.DBType.equals("mysql")) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime from pgused" + time + allipstr + " h where ");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sb.append("select h.thevalue,to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss') as collecttime from pgused" + time + allipstr + " h where ");
			}
			// sb.append(" h.collecttime >= '");
			// sb.append(starttime);
			// sb.append("' and h.collecttime <= '");
			//
			// sb.append(endtime);
			// sb.append("' order by h.collecttime");

			if (SystemConstant.DBType.equals("mysql")) {
				sb.append(" h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");

				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sb.append(" h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') order by h.collecttime");
			}
			sql = sb.toString();
			// SysLogger.info(sql);

			// System.out.println(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;

			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, "%");
					avgput = avgput + getfloat(thevalue);
					if (need.equals("max")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
					}
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	// yangjun-----------------------------end
	public Hashtable getMemory(String ip, String category, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from memory" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from memory" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double maxfloat = 0;
			double minfloat = 10000000;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					tempfloat = Double.parseDouble(thevalue);
					if (maxfloat < Double.parseDouble(thevalue))
						maxfloat = Double.parseDouble(thevalue);
					if (minfloat > Double.parseDouble(thevalue))
						minfloat = Double.parseDouble(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "");// yangjun xiugai
			}
			hash.put("max", CEIString.round(maxfloat, 2) + "");
			hash.put("min", CEIString.round(minfloat, 2) + "");
			hash.put("temp", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	// yangjun-----------------------------end
	public Hashtable[] getIfBand_Packs(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime, String time) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		// hash[1] = new Hashtable(); //最值
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;

		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");
			String sql1 = "";
			String sql2 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select max(h.thevalue),h.entity from Packs h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' " + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();

				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from packs" + time + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "' ";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select max(h.thevalue),h.entity from Packs h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >=to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') " + " group by h.entity";
				// List list1 = session.createQuery(sql1).list();

				sql2 = "select to_char(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from packs" + time + allipstr + " h where " + " h.subentity='" + index + "' "
				// + sb.toString()
						+ " and h.collecttime >=to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime<=to_date('" + endtime
						// + "','YYYY-MM-DD HH24:MI:SS') order by
						// h.collecttime";
						+ "','yyyy-mm-dd hh24:mi:ss') ";
			}
			// Query query = session.createQuery(sql2);
			List list2 = new ArrayList();
			// stmt = con.prepareStatement(sql2);
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				// v.add(3,rs.getString("unit"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "个";

			for (int i = 0; i < 2; i++) {
				// Object[] row = (Object[]) list1.get(i);
				// hash[1].put((String) row[1], (String) row[0] + unit);
				hash[0].put(bandch[i], vector[i]);
			}
			if (list2.size() != 0) {
				hash[0].put("key", bandch);
				hash[0].put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable getDisk(String ip, String category, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);

			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from disk" + time + allipstr + " h where ");

			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from disk" + time + allipstr + " h where ");

			}

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.SUBENTITY=\"");
				sb.append(category);
				sb.append("\" and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String subentity = rs.getString("subentity");
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, subentity);
					v.add(2, rs.getString("unit"));
					v.add(3, collecttime);
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	/**
	 * 默认domino 磁盘 ,
	 * 
	 * @param ip
	 * @param category
	 * @param subentity
	 * @param starttime
	 * @param endtime
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public Hashtable getDisk(String ip, String category, String subentity, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {

			String allipstr = SysUtil.doip(ip);

			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from domdisk" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from domdisk" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}
			sql = sb.toString();
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String subentity1 = rs.getString("subentity");
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, subentity1);
					v.add(2, rs.getString("unit"));
					v.add(3, collecttime);
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public List<String> getSubentitysByTableName(String tablename) {
		if (tablename == null || tablename.equals("")) {
			return null;
		}
		List<String> subentityList = new ArrayList<String>();
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select subentity from ");
		sql.append(tablename);
		sql.append(" group by subentity");
		// System.out.println(sql);
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				String temp = rs.getString("subentity");
				subentityList.add(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dbManager.close();
		}

		return subentityList;
	}

	public Hashtable getCpuDetails(DBManager dbmanager, String ip, String category, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		// DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from cpudtl" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY=\"");
				sb.append(category);
				sb.append("\" and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from cpudtl" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')  and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')  order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String subentity = rs.getString("subentity");
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					// SysLogger.info(subentity+"==="+thevalue+"==="+collecttime);
					v.add(0, emitStr(thevalue));
					v.add(1, subentity);
					v.add(2, rs.getString("unit"));
					v.add(3, collecttime);
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				// dbmanager.close();
			}
			Integer size = new Integer(0);
			// SysLogger.info("list size ============"+list1.size());
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		// dbmanager.close();
		return hash;
	}

	public Hashtable[] getIfBand_InPacks(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime, String time) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;

		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);

			StringBuffer sb = new StringBuffer();
			// Session session = this.beginTransaction();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");

			String sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from inpacks" + time + allipstr + " h where " + " h.subentity='" + index + "' "
			// + sb.toString()
					+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			List list2 = new ArrayList();
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				list2.add(v);
			}
			rs.close();
			// stmt.close();

			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "个";

			for (int i = 0; i < 2; i++) {
				hash[0].put(bandch[i], vector[i]);
			}
			if (list2.size() != 0) {
				hash[0].put("key", bandch);
				hash[0].put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getIfBand_OutPacks(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime, String time) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			StringBuffer sb = new StringBuffer();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");

			String sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from outpacks" + time + allipstr + " h where " + " h.subentity='" + index + "' "
			// + sb.toString()
					+ " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			List list2 = new ArrayList();
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				list2.add(v);
			}
			rs.close();
			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "个";
			for (int i = 0; i < 2; i++) {
				hash[0].put(bandch[i], vector[i]);
			}
			if (list2.size() != 0) {
				hash[0].put("key", bandch);
				hash[0].put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	public Hashtable[] getDiscardsPerc(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime, String table) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		DBManager dbmanager = new DBManager();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);

			StringBuffer sb = new StringBuffer();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");
			String sql1 = "select max(h.thevalue),h.entity from DiscardsPerc h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' " + " group by h.entity";
			String sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from " + table + allipstr + " h where " + " h.subentity='" + index + "' " + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			List list2 = new ArrayList();
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				list2.add(v);
			}
			rs.close();

			Calendar curCal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date cc = curCal.getTime();
			String curdate = sdf1.format(cc);
			String currentTime = sdf.format(cc);

			int curHour = curCal.get(Calendar.HOUR_OF_DAY);
			for (int i = 0; i <= curHour; i++) {
				String curTime = curdate + " " + i + ":00:00";
				Vector tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "InDiscardsPerc");
				vector[0].add(tempV);
				tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "OutDiscardsPerc");
				vector[1].add(tempV);
			}
			Vector tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, currentTime);
			tempV.add(2, "InDiscardsPerc");
			vector[0].add(tempV);
			tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, currentTime);
			tempV.add(2, "OutDiscardsPerc");
			vector[1].add(tempV);
			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "%";

			for (int i = 0; i < 2; i++) {
				hash[0].put(bandch[i], vector[i]);
			}
			hash[0].put("key", bandch);
			hash[0].put("unit", unit);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable[] getErrorsPerc(String ip, String index, String[] bandkey, String[] bandch, String starttime, String endtime, String table) throws Exception {
		Hashtable[] hash = new Hashtable[2];
		hash[0] = new Hashtable();
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			// con=DataGate.getCon();
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String[] ipdot = ip.split(".");
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);

			StringBuffer sb = new StringBuffer();
			Vector[] vector = new Vector[bandkey.length];
			sb.append(" and (");
			for (int j = 0; j < bandkey.length; j++) {
				vector[j] = new Vector();
				if (j != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(bandkey[j]);
				sb.append("' ");
			}
			sb.append(") ");
			String sql1 = "select max(h.thevalue),h.entity from ErrorsPerc h where h.ipaddress='" + ip + "' and h.subentity='" + index + "' " + sb.toString() + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "'" + " group by h.entity";

			String sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from " + table + allipstr + " h where " + " h.subentity='" + index + "' " + " and h.collecttime >='" + starttime + "' and h.collecttime<='" + endtime + "' ";
			List list2 = new ArrayList();
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				list2.add(v);
			}
			rs.close();
			for (int k = 0; k < list2.size(); k++) {
				Vector obj = (Vector) list2.get(k);
				for (int i = 0; i < bandkey.length; i++) {
					if (((String) obj.get(2)).equalsIgnoreCase(bandkey[i])) {
						vector[i].add(obj);
						break;
					}
				}
			}
			String unit = "%";
			Calendar curCal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date cc = curCal.getTime();
			String curdate = sdf1.format(cc);
			int curHour = curCal.get(Calendar.HOUR_OF_DAY);
			for (int i = 0; i <= curHour; i++) {
				String curTime = curdate + " " + i + ":00:00";
				Vector tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "InErrorsPerc");
				vector[0].add(tempV);
				tempV = new Vector();
				tempV.add(0, "0");
				tempV.add(1, curTime);
				tempV.add(2, "OutErrorsPerc");
				vector[1].add(tempV);
			}
			Vector tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, sdf.format(cc));
			tempV.add(2, "InErrorsPerc");
			vector[0].add(tempV);
			tempV = new Vector();
			tempV.add(0, "0");
			tempV.add(1, sdf.format(cc));
			tempV.add(2, "OutErrorsPerc");
			vector[1].add(tempV);
			for (int i = 0; i < 2; i++) {
				hash[0].put(bandch[i], vector[i]);
			}
			hash[0].put("key", bandch);
			hash[0].put("unit", unit);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime, String time) throws Exception {
		Hashtable hash = new Hashtable();
		// Connection con = null;
		// PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String id = ip.replaceAll("\\.", "_");
		try {
			// con=DataGate.getCon();
			if (!starttime.equals("") && !endtime.equals("")) {
				// //con=DataGate.getCon();
				// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
				// String tempStr = "";
				// // String allipstr = "";
				// String oracleId = "";
				// if (category.equals("ORAPing")) {
				// String[] ips = ip.split(":");
				// ip = ips[0];
				// oracleId = ips[1];
				// }
				// if (ip.indexOf(".") > 0) {
				// ip1 = ip.substring(0, ip.indexOf("."));
				// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
				// tempStr = ip.substring(ip.indexOf(".") + 1,
				// ip.lastIndexOf("."));
				// }
				// ip2 = tempStr.substring(0, tempStr.indexOf("."));
				// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
				// tempStr.length());
				// allipstr = ip1 + ip2 + ip3 + ip4;
				// String allipstr = SysUtil.doip(ip);

				String sql = "";
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("Ping")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from ping" + id + " h where ");
					} else if (category.equals("ORAPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from oraping" + time + id + " h where ");
						// ip = ip + ":" + oracleId;
					} else if (category.equals("SQLPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from sqlping" + time + id + " h where ");
					} else if (category.equals("DB2Ping")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from db2ping" + time + id + " h where ");
					} else if (category.equals("SYSPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from sysping" + time + id + " h where ");
					} else if (category.equals("MYPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from myping" + time + id + " h where ");
					} else if (category.equals("CPU")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from cpu" + id + " h where ");
					} else if (category.equals("Process")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from pro" + time + id + " h where ");
					} else if (category.equals("INFORMIXPing")) {// yangjun
						// add
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from informixping" + time + id + " h where ");
					}
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("Ping")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from ping" + id + " h where ");
					} else if (category.equals("ORAPing")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from oraping" + time + id + " h where ");
						// ip = ip + ":" + oracleId;
					} else if (category.equals("SQLPing")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from sqlping" + time + id + " h where ");
					} else if (category.equals("DB2Ping")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from db2ping" + time + id + " h where ");
					} else if (category.equals("SYSPing")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from sysping" + time + id + " h where ");
					} else if (category.equals("MYPing")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from myping" + time + id + " h where ");
					} else if (category.equals("CPU")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from cpu" + id + " h where ");
					} else if (category.equals("Process")) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from pro" + time + id + " h where ");
					} else if (category.equals("INFORMIXPing")) {// yangjun
						// add
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from informixping" + time + id + " h where ");
					}
				}

				if (category.equals("Process")) {
					sb.append(" h.entity='");
					sb.append("MemoryUtilization");
					sb.append("' and h.subentity='");
					sb.append(subentity);
				} else {
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
				}
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime asc");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('");
					sb.append(starttime);
					sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
					sb.append(endtime);
					sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime,h.unit");
				}

				sql = sb.toString();
				// SysLogger.info(sql);

				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				double pingcon = 0;
				double cpucon = 0;
				double proces = 0;// yangjun add
				int downnum = 0;
				int i = 0;
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					if (category.equals("Ping") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("Ping") && subentity.equalsIgnoreCase("ResponseTime")) {
						pingcon = pingcon + getfloat(thevalue);
					} else if (category.equals("ORAPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("SQLPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("DB2Ping") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("SYSPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("MYPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					} else if (category.equals("INFORMIXPing") && subentity.equalsIgnoreCase("ConnectUtilization")) {// yangjun
						// add
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					}
					if (category.equals("Process")) {// yangjun add
						proces = proces + getfloat(thevalue);
					}
					if (subentity.equalsIgnoreCase("ConnectUtilization")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
						if (tempfloat > getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else if (subentity.equalsIgnoreCase("ResponseTime")) {
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else if (category.equalsIgnoreCase("CPU")) {
						cpucon = cpucon + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else {
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					}
					list1.add(v);
				}
				rs.close();
				// stmt.close();

				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector) list1.get(0);
						unit = (String) tempV.get(2);
					}
				}
				if ((category.equals("Ping") || category.equals("ORAPing") || category.equals("DB2Ping") || category.equals("INFORMIXPing") || category.equals("SYSPing") || category.equals("SQLPing") || category.equals("MYPing")) && subentity.equalsIgnoreCase("ConnectUtilization")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				} else if (category.equals("Ping") && subentity.equalsIgnoreCase("ResponseTime")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0%");
						hash.put("pingmax", "0.0%");
						hash.put("downnum", "0");
					}
				}
				if (category.equals("CPU")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgcpucon", CEIString.round(cpucon / list1.size(), 2) + unit);
					} else {
						hash.put("avgcpucon", "0.0%");
					}
				}
				if (category.equals("Process")) {// yangjun add
					// System.out.println("=================Process");
					if (list1 != null && list1.size() > 0) {
						hash.put("avgmemo", CEIString.round(proces / list1.size(), 2) + unit);
					} else {
						hash.put("avgmemo", "0.0%");
					}
				}
				hash.put("size", size);
				hash.put("max", CEIString.round(tempfloat, 2) + unit);
				hash.put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable getBusCategory(String id, String bid, String starttime, String endtime, String time) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String sql = "";
				StringBuffer sb = new StringBuffer();
				sb.append(" select h.THEVALUE,h.RESPONSETIME,DATE_FORMAT(h.COLLECTTIME,'%Y-%m-%d %H:%i:%s') as collecttime from bnode" + time + bid + "_" + id + " h where ");
				sb.append("h.COLLECTTIME >= '");
				sb.append(starttime);
				sb.append("' and h.COLLECTTIME <= '");
				sb.append(endtime);
				sb.append("' order by h.COLLECTTIME");
				sql = sb.toString();
				// SysLogger.info(sql);

				rs = dbmanager.executeQuery(sql);
				List list = new ArrayList();
				String max = "";
				double tempfloat = 0;
				double pingcon = 0;
				double responsetime = 0;
				int i = 0;
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("THEVALUE");
					String response = rs.getString("RESPONSETIME");
					String collecttime = rs.getString("collecttime");
					v.add(0, thevalue);
					v.add(1, response);
					v.add(2, collecttime);
					pingcon = pingcon + getfloat(thevalue);
					responsetime = responsetime + getfloat(response);
					list.add(v);
				}
				rs.close();
				Integer size = new Integer(0);
				hash.put("list", list);
				if (list.size() != 0) {
					size = new Integer(list.size());
				}
				if (list != null && list.size() > 0) {
					hash.put("avgpingcon", CEIString.round((pingcon / list.size()) * 100, 2));
					hash.put("avgresponcon", CEIString.round(responsetime / list.size(), 2));
				} else {
					hash.put("avgpingcon", "0.0");
					hash.put("avgresponcon", "0.0");
				}
				hash.put("size", size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	public Hashtable getFlash(String ip, String category, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from flash" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from flash" + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public Hashtable getEnviroment(String ip, String category, String starttime, String endtime, String time, String table) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			// String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			// String tempStr = "";
			// String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip.substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			String allipstr = SysUtil.doip(ip);

			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from " + table + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from " + table + time + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}
			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public boolean createHostDataForAS400(String ip, Hashtable datahash) {
		// TODO Auto-generated method stub
		Hashtable sharedata = ShareData.getSharedata();
		if (datahash != null && datahash.size() > 0) {
			sharedata.put(ip, datahash);
		}
		if (datahash == null) {
			return false;
		}

		Hashtable systemhashtable = (Hashtable) datahash.get("SystemStatus");

		Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);

		if (systemhashtable == null) {
			return false;
		}

		String cpuvalue = (String) systemhashtable.get("cpu");
		String dbCapabilityValue = (String) systemhashtable.get("DBCapability");
		String PercentSystemASPUsedValue = (String) systemhashtable.get("PercentSystemASPUsed");

		// SysLogger.info("IP: ====="+ip);
		// String ip1 ="",ip2="",ip3="",ip4="";
		// String tempStr = "";
		// String allipstr = "";
		// if (ip.indexOf(".")>0){
		// ip1=ip.substring(0,ip.indexOf("."));
		// ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());
		// tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
		// }
		// ip2=tempStr.substring(0,tempStr.indexOf("."));
		// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
		// allipstr=ip1+ip2+ip3+ip4;
		String allipstr = SysUtil.doip(ip);

		Date date = new Date();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String time = simpleDateFormat.format(date);

		DBManager conn = new DBManager();
		try {
			if ("null".equalsIgnoreCase(cpuvalue) && cpuvalue != null && cpuvalue.trim().length() > 0) {

				String tablename = "cpu" + allipstr;
				String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + "dynamic" + "','" + "CPU" + "','" + "Utilization" + "','" + "Utilization" + "','" + "%" + "','" + "" + "','" + "" + "','" + "0" + "','" + cpuvalue + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
				conn.addBatch(sql);
			}
			String tablename = "dbcapability" + allipstr;
			String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + "dynamic" + "','" + "DBCapability" + "','" + "Utilization" + "','" + "Utilization" + "','" + "%" + "','" + "" + "','" + "" + "','" + "0" + "','" + dbCapabilityValue + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";

			tablename = "systemasp" + allipstr;
			sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','" + "dynamic" + "','" + "PercentSystemASPUsed" + "','" + "Utilization" + "','" + "Utilization" + "','" + "%" + "','" + "" + "','" + "" + "','" + "0" + "','" + PercentSystemASPUsedValue + "','" + time + "')";

			conn.addBatch(sql);
			conn.executeBatch();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}

		/**
		 * 系统信息
		 */
		try {
			List systemValueForAS400List = new ArrayList();
			cpuvalue = (String) systemhashtable.get("cpu");
			dbCapabilityValue = (String) systemhashtable.get("DBCapability");
			PercentSystemASPUsedValue = (String) systemhashtable.get("PercentSystemASPUsed");
			String jobsInSystem = (String) systemhashtable.get("JobsInSystem");
			String systemASP = (String) systemhashtable.get("SystemASP");
			String currentUnprotectedStorageUsed = (String) systemhashtable.get("CurrentUnprotectedStorageUsed");
			String maximumUnprotectedStorageUsed = (String) systemhashtable.get("MaximumUnprotectedStorageUsed");
			String percentPermanentAddresses = (String) systemhashtable.get("PercentPermanentAddresses");
			String percentTemporaryAddresses = (String) systemhashtable.get("PercentTemporaryAddresses");

			SystemValueForAS400 systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("cpu");
			systemValueForAS400.setValue(cpuvalue);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("CPU");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("DBCapability");
			systemValueForAS400.setValue(dbCapabilityValue);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("数据库性能");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("PercentSystemASPUsed");
			systemValueForAS400.setValue(PercentSystemASPUsedValue);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("系统ASP使用百分比");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("JobsInSystem");
			systemValueForAS400.setValue(jobsInSystem);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("系统中的任务");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("SystemASP");
			systemValueForAS400.setValue(systemASP);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("系统ASP");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("CurrentUnprotectedStorageUsed");
			systemValueForAS400.setValue(currentUnprotectedStorageUsed);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("当前无保护状态下的使用情况");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("MaximumUnprotectedStorageUsed");
			systemValueForAS400.setValue(maximumUnprotectedStorageUsed);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("最大非保护状态");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("PercentPermanentAddresses");
			systemValueForAS400.setValue(percentPermanentAddresses);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("永久地址百分比");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			systemValueForAS400 = new SystemValueForAS400();
			systemValueForAS400.setNodeid(hostNode.getId() + "");
			systemValueForAS400.setIpaddress(ip);
			systemValueForAS400.setCategory("PercentTemporaryAddresses");
			systemValueForAS400.setValue(percentTemporaryAddresses);
			systemValueForAS400.setUnit("");
			systemValueForAS400.setDescription("临时地址百分比");
			systemValueForAS400.setCollectTime(time);
			systemValueForAS400List.add(systemValueForAS400);

			SystemValueForAS400Dao systemValueForAS400Dao = new SystemValueForAS400Dao();
			try {
				systemValueForAS400Dao.deleteByNodeid(hostNode.getId() + "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				systemValueForAS400Dao.close();
			}

			systemValueForAS400Dao = new SystemValueForAS400Dao();
			try {
				systemValueForAS400Dao.save(systemValueForAS400List);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				systemValueForAS400Dao.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			List systemPoolList = (List) datahash.get("SystemPool");
			List newSystemPoolList = new ArrayList();
			if (systemPoolList != null && systemPoolList.size() > 0) {
				for (int i = 0; i < systemPoolList.size(); i++) {
					Hashtable hashtable = (Hashtable) systemPoolList.get(i);
					String systemPool = (String) hashtable.get("id");
					String name = (String) hashtable.get("name");
					String size = (String) hashtable.get("size");
					String reservedSize = (String) hashtable.get("reservedSize");
					String maximumActiveThreads = (String) hashtable.get("maximumActiveThreads");

					SystemPoolForAS400 systemPoolForAS400 = new SystemPoolForAS400();

					systemPoolForAS400.setNodeid(hostNode.getId() + "");
					systemPoolForAS400.setIpaddress(ip);
					systemPoolForAS400.setSystemPool(systemPool);
					systemPoolForAS400.setName(name);
					systemPoolForAS400.setSize(size);
					systemPoolForAS400.setReservedSize(reservedSize);
					systemPoolForAS400.setMaximumActiveThreads(maximumActiveThreads);
					systemPoolForAS400.setCollectTime(time);
					newSystemPoolList.add(systemPoolForAS400);
				}

				SystemPoolForAS400Dao systemPoolForAS400Dao = new SystemPoolForAS400Dao();
				try {
					systemPoolForAS400Dao.deleteByNodeid(hostNode.getId() + "");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					systemPoolForAS400Dao.close();
				}
				try {
					systemPoolForAS400Dao = new SystemPoolForAS400Dao();
					try {
						systemPoolForAS400Dao.save(newSystemPoolList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					systemPoolForAS400Dao.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			List subSystemList = (List) datahash.get("subSystem");
			if (subSystemList != null && subSystemList.size() > 0) {
				List newSubSystemList = new ArrayList();
				for (int i = 0; i < subSystemList.size(); i++) {
					SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400) subSystemList.get(i);
					subsystemForAS400.setNodeid(hostNode.getId() + "");
					subsystemForAS400.setIpaddress(ip);
					subsystemForAS400.setCollectTime(time);
					newSubSystemList.add(subsystemForAS400);
				}
				SubsystemForAS400Dao subsystemForAS400Dao = new SubsystemForAS400Dao();
				try {
					subsystemForAS400Dao.deleteByNodeid(hostNode.getId() + "");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					subsystemForAS400Dao.close();
				}
				try {
					subsystemForAS400Dao = new SubsystemForAS400Dao();
					try {
						subsystemForAS400Dao.save(newSubSystemList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					subsystemForAS400Dao.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Vector diskVector = (Vector) datahash.get("disk");

			List diskList = new ArrayList();
			if (diskVector != null && diskVector.size() > 0) {
				for (int i = 0; i < diskVector.size(); i++) {
					Hashtable diskHashtable = (Hashtable) diskVector.get(i);
					String unit = (String) diskHashtable.get("Unit");
					String type = (String) diskHashtable.get("Type");
					String size = (String) diskHashtable.get("Size(M)");
					String used = (String) diskHashtable.get("%Used");
					String io_rqs = (String) diskHashtable.get("I/O Rqs");
					String request_size = (String) diskHashtable.get("Request Size(K)");
					String read_rqs = (String) diskHashtable.get("Read Rqs");
					String write_rqs = (String) diskHashtable.get("Write Rqs");
					String read = (String) diskHashtable.get("Read(K)");
					String write = (String) diskHashtable.get("Write(K)");
					String busy = (String) diskHashtable.get("%Busy");

					DiskForAS400 diskForAS400 = new DiskForAS400();
					diskForAS400.setUnit(unit);
					diskForAS400.setType(type);
					diskForAS400.setSize(size);
					diskForAS400.setUsed(used);
					diskForAS400.setIoRqs(io_rqs);
					diskForAS400.setRequestSize(request_size);
					diskForAS400.setReadRqs(read_rqs);
					diskForAS400.setWriteRqs(write_rqs);
					diskForAS400.setRead(read);
					diskForAS400.setWrite(write);
					diskForAS400.setBusy(busy);

					diskList.add(diskForAS400);
				}

				DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
				try {
					diskForAS400Dao.deleteByNodeid(hostNode.getId() + "");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					diskForAS400Dao.close();
				}
				try {
					diskForAS400Dao = new DiskForAS400Dao();
					try {
						diskForAS400Dao.save(diskList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					diskForAS400Dao.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List jobList = (List) datahash.get("Jobs");
		try {
			// Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
			List newJobList = new ArrayList();
			if (jobList != null && jobList.size() > 0) {
				for (int i = 0; i < jobList.size(); i++) {
					JobForAS400 jobForAS400 = (JobForAS400) jobList.get(i);
					jobForAS400.setIpaddress(ip);
					jobForAS400.setNodeid(hostNode.getId() + "");
					newJobList.add(jobForAS400);
				}

				JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
				try {
					jobForAS400Dao.deleteByNodeid(hostNode.getId() + "");
				} catch (RuntimeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					jobForAS400Dao.close();
				}
				try {
					jobForAS400Dao = new JobForAS400Dao();
					jobForAS400Dao.save(newJobList);
					datahash.put("Jobs", newJobList);
					sharedata.put(ip, datahash);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					jobForAS400Dao.close();
				}
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public Hashtable getLkuhdxp(String id, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from lkuhdxp" + time + id + " h where ");
				sb.append(" h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from lkuhdxp" + time + id + " h where ");
				sb.append(" h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}
			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public Hashtable getLkuhdx(String id, String entity, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from lkuhdx" + time + id + " h where ");
				sb.append(" h.entity ='");
				sb.append(entity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from lkuhdx" + time + id + " h where ");
				sb.append(" h.entity ='");
				sb.append(entity);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public Hashtable getLkPing(String id, String starttime, String endtime, String time) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from lkping" + time + id + " h where ");
				sb.append(" h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from lkping" + time + id + " h where ");
				sb.append(" h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	/**
	 * nielin add
	 * 
	 * @date 2010-08-18
	 * @param ip
	 * @param proVector
	 */
	// private void createProcessGroupEventList(String ip, Vector proVector) {
	//
	// try {
	// Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
	//
	// ProcessGroupConfigurationUtil processGroupConfigurationUtil = new
	// ProcessGroupConfigurationUtil();
	// List list = processGroupConfigurationUtil.getProcessGroupByIp(ip);
	//
	// if (list == null || list.size() == 0) {
	// return;
	// }
	//
	// for (int i = 0; i < list.size(); i++) {
	// ProcessGroup processGroup = (ProcessGroup) list.get(i);
	// List processGroupConfigurationList =
	// processGroupConfigurationUtil.getProcessGroupConfigurationByGroupId(String.valueOf(processGroup.getId()));
	//
	// if (processGroupConfigurationList == null ||
	// processGroupConfigurationList.size() == 0) {
	// continue;
	// }
	//
	// List wrongList = new ArrayList();
	//
	// // 有告警的黑名单进程列表
	// List blackWrongList = new ArrayList();
	//
	// for (int j = 0; j < processGroupConfigurationList.size(); j++) {
	// int num = 0;// 实际个数
	// ProcessGroupConfiguration processGroupConfiguration =
	// (ProcessGroupConfiguration) processGroupConfigurationList.get(j);
	// String status = processGroupConfiguration.getStatus();// 1:黑名单
	// // 0:白名单
	// for (int k = 0; k < proVector.size(); k++) {
	// Processcollectdata processdata = (Processcollectdata)
	// proVector.elementAt(k);
	// if ("Name".equals(processdata.getEntity())) {
	// if
	// (processGroupConfiguration.getName().trim().equals(processdata.getThevalue().trim()))
	// {
	// num++;
	// }
	// }
	//
	// }
	//
	// int times = Integer.parseInt(processGroupConfiguration.getTimes());//
	// 告警阀值
	//
	// // 增加白名单判断条件 Task
	// if (num < times && "0".equals(status)) {
	// // 丢失的个数
	// num = times - num;
	//
	// List wrongProlist = new ArrayList();
	// wrongProlist.add(processGroupConfiguration.getName());
	// wrongProlist.add(num);
	//
	// wrongList.add(wrongProlist);
	//
	// }
	//
	// // 黑名单进程 判断 增加黑名单判断条件 Task
	// if (num > times && "1".equals(status)) {
	// // 超过的个数
	// num = num - times;
	// List blackWrongProlist = new ArrayList();
	// blackWrongProlist.add(processGroupConfiguration.getName());
	// blackWrongProlist.add(num);
	// blackWrongList.add(blackWrongProlist);
	// }
	// }
	//
	// if (wrongList.size() > 0 || blackWrongList.size() > 0) {
	// StringBuffer message = new StringBuffer();
	// message.append(ip);
	// message.append(" 进程组为：");
	// message.append(processGroup.getName());
	// message.append(" 出现进程告警!");
	// for (int j = 0; j < wrongList.size(); j++) {
	// List wrongProList = (List) wrongList.get(j);
	// message.append(" 进程：");
	// message.append(wrongProList.get(0));
	// message.append("丢失");
	// message.append(wrongProList.get(1));
	// message.append("个;");
	// }
	// for (int j = 0; j < blackWrongList.size(); j++) {
	// List blackWrongProlist = (List) blackWrongList.get(j);
	// message.append(" 进程：");
	// message.append(blackWrongProlist.get(0));
	// message.append("超过");
	// message.append(blackWrongProlist.get(1));
	// message.append("个;");
	// }
	//
	// EventList eventList = new EventList();
	// eventList.setEventtype("poll");
	// eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
	// eventList.setContent(message.toString());
	// eventList.setLevel1(Integer.parseInt(processGroup.getAlarm_level()));
	// eventList.setManagesign(0);
	// eventList.setRecordtime(Calendar.getInstance());
	// eventList.setReportman("系统轮询");
	// eventList.setNodeid(hostNode.getId());
	// eventList.setBusinessid(hostNode.getBid());
	// eventList.setSubtype("host");
	// eventList.setSubentity("proc");
	//
	// hostNode = PollingEngine.getInstance().getNodeByID(hostNode.getId());
	// hostNode.setAlarm(true);
	// if (hostNode.getAlarmlevel() < eventList.getLevel1())
	// hostNode.setAlarmlevel(eventList.getLevel1());
	// hostNode.getAlarmMessage().add(message);
	//
	// /*
	// * 需要增加发送短信的功能
	// */
	// // 进行邮件告警
	// String userids = hostNode.getSendemail();
	// if (userids != null && userids.trim().length() > 0) {
	// SendMailAlarm sendMailAlarm = new SendMailAlarm();
	// try {
	// sendMailAlarm.sendAlarm(eventList, userids);
	// } catch (Exception e) {
	//
	// }
	// }
	// // EventListDao eventListDao = new EventListDao();
	// // try {
	// // eventListDao.save(eventList);
	// // } catch (RuntimeException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // } finally{
	// // eventListDao.close();
	// // }
	// try {
	// createSMS(eventList.getSubtype(), eventList.getSubentity(), ip,
	// hostNode.getId() + "", message.toString(), eventList.getLevel1(), 1,
	// processGroup.getName(), eventList
	// .getBusinessid(), hostNode.getAlias() + "(" + ip + ")");
	// } catch (Exception e) {
	//
	// }
	//
	// }
	//
	// }
	// } catch (NumberFormatException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// public void createSMS(String subtype, String subentity, String ipaddress,
	// String objid, String content, int flag, int checkday, String sIndex,
	// String bids, String sysLocation) {
	// // 建立短信
	// // 从内存里获得当前这个IP的PING的值
	// Calendar date = Calendar.getInstance();
	// Hashtable sendeddata = ShareData.getSendeddata();
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// // System.out.println("端口事件--------------------");
	// try {
	// if (!sendeddata.containsKey(subtype + ":" + subentity + ":" + ipaddress +
	// ":" + sIndex)) {
	// // 若不在，则建立短信，并且添加到发送列表里
	// Smscontent smscontent = new Smscontent();
	// String time = sdf.format(date.getTime());
	// smscontent.setLevel(flag + "");
	// smscontent.setObjid(objid);
	// smscontent.setMessage(content);
	// smscontent.setRecordtime(time);
	// smscontent.setSubtype(subtype);
	// smscontent.setSubentity(subentity);
	// smscontent.setIp(ipaddress);
	// // 发送短信
	// SmscontentDao smsmanager = new SmscontentDao();
	// smsmanager.sendURLSmscontent(smscontent);
	// sendeddata.put(subtype + ":" + subentity + ":" + ipaddress + ":" +
	// sIndex, date);
	//
	// } else {
	// // 若在，则从已发送短信列表里判断是否已经发送当天的短信
	// SmsDao smsDao = new SmsDao();
	// List list = new ArrayList();
	// String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
	// + " 00:00:00";
	// String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
	// Date());
	// try {
	// list = smsDao.findByEvent(content, startTime, endTime);
	// } catch (RuntimeException e) {
	// e.printStackTrace();
	// } finally {
	// smsDao.close();
	// }
	// if (list != null && list.size() > 0) {// 短信列表里已经发送当天的短信
	// Calendar formerdate = (Calendar) sendeddata.get(subtype + ":" + subentity
	// + ":" + ipaddress + ":" + sIndex);
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	// Date last = null;
	// Date current = null;
	// Calendar sendcalen = formerdate;
	// Date cc = sendcalen.getTime();
	// String tempsenddate = formatter.format(cc);
	//
	// Calendar currentcalen = date;
	// Date ccc = currentcalen.getTime();
	// last = formatter.parse(tempsenddate);
	// String currentsenddate = formatter.format(ccc);
	// current = formatter.parse(currentsenddate);
	//
	// long subvalue = current.getTime() - last.getTime();
	// if (checkday == 1) {
	// // 检查是否设置了当天发送限制,1为检查,0为不检查
	// if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
	// // 超过一天，则再发信息
	// Smscontent smscontent = new Smscontent();
	// String time = sdf.format(date.getTime());
	// smscontent.setLevel(flag + "");
	// smscontent.setObjid(objid);
	// smscontent.setMessage(content);
	// smscontent.setRecordtime(time);
	// smscontent.setSubtype(subtype);
	// smscontent.setSubentity(subentity);
	// smscontent.setIp(ipaddress);// 发送短信
	// SmscontentDao smsmanager = new SmscontentDao();
	// smsmanager.sendURLSmscontent(smscontent);
	// // 修改已经发送的短信记录
	// sendeddata.put(subtype + ":" + subentity + ":" + ipaddress + ":" +
	// sIndex, date);
	// } else {
	// // 开始写事件
	// // String sysLocation = "";
	// createEvent("poll", sysLocation, bids, content, flag, subtype, subentity,
	// ipaddress, objid);
	// }
	// }
	// } else {
	// Smscontent smscontent = new Smscontent();
	// String time = sdf.format(date.getTime());
	// smscontent.setLevel(flag + "");
	// smscontent.setObjid(objid);
	// smscontent.setMessage(content);
	// smscontent.setRecordtime(time);
	// smscontent.setSubtype(subtype);
	// smscontent.setSubentity(subentity);
	// smscontent.setIp(ipaddress);
	// // 发送短信
	// SmscontentDao smsmanager = new SmscontentDao();
	// smsmanager.sendURLSmscontent(smscontent);
	// sendeddata.put(subtype + ":" + subentity + ":" + ipaddress + ":" +
	// sIndex, date);
	// }
	//
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	private void createEvent(String eventtype, String eventlocation, String bid, String content, int level1, String subtype, String subentity, String ipaddress, String objid) {
		// 生成事件
		// SysLogger.info("##############开始生成事件############");
		EventList eventlist = new EventList();
		eventlist.setEventtype(eventtype);
		eventlist.setEventlocation(eventlocation);
		eventlist.setContent(content);
		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("系统轮询");
		eventlist.setBusinessid(bid);
		eventlist.setNodeid(Integer.parseInt(objid));
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);
		EventListDao eventlistdao = new EventListDao();
		try {
			eventlistdao.save(eventlist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventlistdao.close();
		}
	}

	public boolean createGSNData(String key, Hashtable datahash) {
		ShareData.setGSNdata(key, datahash);
		return true;
	}

	/**
	 * 得到指定天数的cpu平局值
	 * 
	 * @param ipAddress
	 * @return
	 * @author makewen
	 * @date Apr 22, 2011
	 */
	public ArrayList<FlexVo> getCPUDataAvgArray(String ipAddress, int num) throws Exception {

		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		ipAddress = ipAddress.replace('.', '_');
		String table = "cpu" + ipAddress;
		flexDataList = getCPUDataAvg(table, num);

		return flexDataList;
	}

	public ArrayList<FlexVo> getCPUDataAvg(String table, int num) throws Exception {
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		try {

			Calendar startCaldendar = Calendar.getInstance();
			startCaldendar.add(Calendar.DAY_OF_MONTH, 0);
			String startDate = new SimpleDateFormat("yyyy-MM-dd").format(startCaldendar.getTime());
			String starttime = startDate + " 23:59:59";

			Calendar endCaldendar = Calendar.getInstance();
			endCaldendar.add(Calendar.DAY_OF_MONTH, -num);
			String endDate = new SimpleDateFormat("yyyy-MM-dd").format(endCaldendar.getTime());
			String endTime = endDate + " 00:00:00";
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = " select avg(h.thevalue) as thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d') as collecttime,h.unit from " + table + " h where h.category='CPU' and h.subentity='Utilization' and h.collecttime >= '" + endTime + "' and h.collecttime <= '" + starttime + "' group by DATE_FORMAT(h.collecttime,'%Y-%m-%d')";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = " select  avg(h.thevalue) as thevalue,to_char(h.collecttime,'YYYY-MM-DD') as collecttime,h.unit from  " + table + " h where h.category='CPU' and h.subentity='Utilization' and h.collecttime >=to_date('" + endTime + "','YYYY-MM-DD HH24:MI:SS')  and h.collecttime <= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') group by collecttime,h.unit order by collecttime";
			}

			rs = dbmanager.executeQuery(sql);
			for (int j = num; j >= 0; j--) {
				Calendar curDate_s = Calendar.getInstance();
				curDate_s.add(Calendar.DAY_OF_MONTH, -j);
				String date = new SimpleDateFormat("yyyy-MM-dd").format(curDate_s.getTime());
				Hashtable cpuHash = new Hashtable();
				FlexVo fVo = new FlexVo();
				fVo.setObjectName(date);
				fVo.setObjectNumber("");
				flexDataList.add(fVo);
			}
			if (rs != null) {
				while (rs.next()) {
					String theValue = rs.getString("thevalue");
					float value = Float.parseFloat(theValue);
					theValue = ((float) Math.round(value * 100) / 100) + "";
					String collecttime = rs.getString("collecttime");
					for (int i = 0; i < flexDataList.size(); i++) {
						FlexVo fVo = (FlexVo) flexDataList.get(i);
						if (fVo.getObjectName().equals(collecttime)) {
							try {
								FlexVo fVo1 = new FlexVo();
								fVo1.setObjectName(collecttime);
								fVo1.setObjectNumber(theValue);
								flexDataList.set(i, fVo1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			dbmanager.close();
		}
		return flexDataList;
	}

	public Hashtable getCurByCategory(String ip, String category, String subentity) {

		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String starttime = sdf.format(new Date()) + " 00:00:00";
		String endtime = sdf.format(new Date()) + " 23:59:59";
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = "";
				String sid = ip;

				if (category.equals("CPU") || category.equals("Memory") || category.equals("dominoCpu") || category.equals("domplatmem") || category.equals("domservmem") || category.equals("Process") || category.equals("MqPing") || category.equals("DomPing")) {
					sid = SysUtil.doip(ip);
				}
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					if (category.equals("Ping")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from ping" + sid + " h where ");
					} else if (category.equals("ORAPing")) {

						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from oraping" + sid + " h where ");
					} else if (category.equals("SQLPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from sqlping" + sid + " h where ");
					} else if (category.equals("DB2Ping")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from db2ping" + sid + " h where ");
					} else if (category.equals("SYSPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from sysping" + sid + " h where ");
					} else if (category.equals("MYPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from myping" + sid + " h where ");
					} else if (category.equals("INFORMIXPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from informixping" + sid + " h where ");

					} else if (category.equals("CPU")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from cpu" + sid + " h where ");
					} else if (category.equals("Memory")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from memory" + sid + " h where ");
					} else if (category.equals("Process")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from pro" + sid + " h where ");
					} else if (category.equals("MqPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from mqping" + sid + " h where ");
					} else if (category.equals("TomcatPing")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping" + sid + " h where ");
					} else if (category.equals("tomcat_jvm")) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm" + sid + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);

					sb.append("' group by h.collecttime order by  h.collecttime desc limit 1");
					sql = sb.toString();
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from disk" + allipstr + " h where ");
					sb.append(" h.CATEGORY='");
					sb.append(category);
					sb.append("' and h.collecttime >= to_date('");
					sb.append(starttime);
					sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
					sb.append(endtime);
					sb.append("' order by h.subentity,h.collecttime");
					sql = sb.toString();

					if (category.equals("Ping")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from ping" + sid + " h where ");
					} else if (category.equals("ORAPing")) {

						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from oraping" + sid + " h where ");
					} else if (category.equals("SQLPing")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from sqlping" + sid + " h where ");
					} else if (category.equals("DB2Ping")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from db2ping" + sid + " h where ");
					} else if (category.equals("SYSPing")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from sysping" + sid + " h where ");
					} else if (category.equals("MYPing")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from myping" + sid + " h where ");
					} else if (category.equals("INFORMIXPing")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from informixping" + sid + " h where ");

					} else if (category.equals("CPU")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from cpu" + sid + " h where ");
					} else if (category.equals("Memory")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from memory" + sid + " h where ");
					} else if (category.equals("Process")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from pro" + sid + " h where ");
					} else if (category.equals("MqPing")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from mqping" + sid + " h where ");
					} else if (category.equals("TomcatPing")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from tomcatping" + sid + " h where ");
					} else if (category.equals("tomcat_jvm")) {
						sb.append(" select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from tomcat_jvm" + sid + " h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') >= '");
					sb.append(starttime);
					sb.append("' and TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') <= '");
					sb.append(endtime);

					sb.append("' group by h.collecttime order by  h.collecttime");
					sql = sb.toString();
				}
				System.out.println("------------:" + sql);
				rs = dbmanager.executeQuery(sql);

				double pingcon = 0;
				int downnum = 0;
				int i = 0;
				String unit = "";
				while (rs.next()) {
					pingcon = getfloat(rs.getString("thevalue"));
					unit = rs.getString("unit");
				}

				hash.put("pingCur", CEIString.round(pingcon, 2) + unit);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			dbmanager.close();
		}

		return hash;

	}

	// 获取网络设备内存利用率信息
	public Hashtable getNetMemeory(String ip, String category, String starttime, String endtime) {
		Hashtable hashtable = new Hashtable();

		ResultSet rs = null;
		double curValue = 0;
		DBManager dbmanager = new DBManager();
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		String allipstr = SysUtil.doip(ip);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String starttime1 = sdf.format(new Date()) + " 00:00:00";
		String endtime1 = sdf.format(new Date()) + " 23:59:59";
		double tempfloat = 0;
		double sum = 0;
		String sql = "";
		StringBuffer sb = new StringBuffer();
		if (SystemConstant.DBType.equals("mysql")) {
			sql = "select avg(convert(h.thevalue,SIGNED)) as avgvalue,h.collecttime from memory" + allipstr + " h where " + " h.category='" + category + "' and h.collecttime >= '" + starttime + "' and h.collecttime <= '" + endtime + "' and h.thevalue != 'NaN' group by h.collecttime order by h.collecttime";

			sb.append("select avg(convert(h.thevalue,SIGNED)) as avgvalue,h.collecttime from memory" + allipstr + " h where ");
			sb.append(" h.category='");
			sb.append(category);
			sb.append("' and h.collecttime >= '");
			sb.append(starttime1);
			sb.append("' and h.collecttime <= '");
			sb.append(endtime1);
			sb.append("' group by h.collecttime order by  h.collecttime desc limit 1");
		} else {
			sql = "select avg(h.thevalue) as avgvalue,h.collecttime from memory" + allipstr + " h where  h.category='Memory' and to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss')>= '" + starttime + "' and to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss') <= '" + endtime + "' and h.thevalue != 'NaN' group by h.collecttime order by h.collecttime";

			sb.append("select avg(h.thevalue) as avgvalue,h.collecttime from memory" + allipstr + " h where ");
			sb.append(" h.category='");
			sb.append(category);
			sb.append("' and to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss') >= '");
			sb.append(starttime1);
			sb.append("' and to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss') <= '");
			sb.append(endtime1);
			sb.append("' group by h.collecttime order by  h.collecttime ");
		}

		try {

			rs = dbmanager.executeQuery(sql);

			while (rs.next()) {
				Vector v = new Vector();

				String avgvalue = rs.getString("avgvalue");
				String collecttime = rs.getString("collecttime");
				// curValue=getfloat(avgvalue);
				if (tempfloat < getfloat(avgvalue))
					tempfloat = getfloat(avgvalue);
				sum += getfloat(avgvalue);
				v.add(0, avgvalue);
				v.add(1, collecttime);
				list1.add(v);
			}
			dbmanager.close();
			dbmanager = new DBManager();
			rs = dbmanager.executeQuery(sb.toString());
			if (rs != null) {
				while (rs.next()) {

					Vector v = new Vector();
					String avgvalue = rs.getString("avgvalue");
					curValue = getfloat(avgvalue);
					String collecttime = rs.getString("collecttime");

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			dbmanager.close();
		}
		if (list1 == null || list1.size() == 0)
			return null;
		hashtable.put("absList", list1);
		hashtable.put("max", CEIString.round(tempfloat, 2) + "");

		hashtable.put("avg", CEIString.round(sum / list1.size(), 2) + "");
		hashtable.put("cur", CEIString.round(curValue, 2) + "");
		return hashtable;
	}

	// 获取服务器磁盘利用率
	public Hashtable getDiskHistroy(String ip, String category, String starttime, String endtime) {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {

			String allipstr = SysUtil.doip(ip);

			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {

				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from disk" + allipstr + " h where ");
				sb.append(" h.CATEGORY='");
				sb.append(category);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.subentity,h.collecttime");
				sql = sb.toString();
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from disk" + allipstr + " h where ");
				sb.append(" h.CATEGORY='");
				sb.append(category);
				sb.append("' and TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') >= '");
				sb.append(starttime);
				sb.append("' and TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') <= '");
				sb.append(endtime);
				sb.append("' order by h.subentity,h.collecttime");
				sql = sb.toString();
			}

			rs = dbmanager.executeQuery(sql);
			int i = 1;
			boolean flag = true;
			double tempfloat = 0;
			double avgput = 0;
			try {
				String tempSub = "";
				while (rs.next()) {

					Vector v = new Vector();
					String subentity = rs.getString("subentity");
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					String value = CEIString.round(Float.parseFloat(emitStr(thevalue)), 2) + "";

					if (flag) {
						tempSub = subentity;
						flag = false;
					}
					if (tempSub.equals(subentity)) {

						v.add(0, value);
						v.add(1, subentity);
						v.add(2, rs.getString("unit"));
						v.add(3, collecttime);
						avgput = avgput + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
						list1.add(v);
					} else {
						if (list1 != null && list1.size() > 0) {
							hash.put("avg" + i, CEIString.round(avgput / list1.size(), 2) + "");
						} else {
							hash.put("avg" + i, "0.0");
						}

						hash.put("max" + i, CEIString.round(tempfloat, 2) + "");
						hash.put("list" + i, list1);
						list1 = new ArrayList();
						v.add(0, value);
						v.add(1, subentity);
						v.add(2, rs.getString("unit"));
						v.add(3, collecttime);
						tempfloat = 0;
						avgput = 0;
						avgput = avgput + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
						list1.add(v);
						i++;
					}
					tempSub = subentity;
				}
				if (list1 != null && list1.size() > 0) {
					hash.put("avg" + i, CEIString.round(avgput / list1.size(), 2) + "");
				} else {
					hash.put("avg" + i, "0.0");
				}
				hash.put("max" + i, CEIString.round(tempfloat, 2) + "");
				hash.put("list" + i, list1);
				hash.put("count", i + "");// 不想以后再遍历了
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {

					}
				}
				dbmanager.close();
			}

		}

		return hash;
	}

	// Oracle表空间利用率（包括某个时间段的利用率，平均、最大利用率）
	public Hashtable getOraSpaceHistroy(String ip, String category, String starttime, String endtime) {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {

			String allipstr = SysUtil.doip(ip);

			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			sb.append("select h.percent_free,h.tablespace,DATE_FORMAT(h.mon_time,'%Y-%m-%d %H:%i:%s') as mon_time from nms_oraspaces" + " h where ");
			sb.append(" h.mon_time >= '");
			sb.append(starttime);
			sb.append("' and h.mon_time <= '");
			sb.append(endtime);
			sb.append("' order by h.tablespace,h.mon_time");

			sql = sb.toString();
			rs = dbmanager.executeQuery(sql);
			int i = 1;
			boolean flag = true;
			double tempfloat = 0;
			double avgput = 0;
			try {
				String tempSub = "";
				while (rs.next()) {

					Vector v = new Vector();
					String name = rs.getString("tablespace");
					String thevalue = rs.getString("percent_free");
					String collecttime = rs.getString("mon_time");
					String value = CEIString.round(Float.parseFloat(emitStr(thevalue)), 2) + "";

					if (flag) {
						tempSub = name;
						flag = false;
					}
					if (tempSub.equals(name)) {

						v.add(0, value);
						v.add(1, name);
						v.add(2, collecttime);
						avgput = avgput + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
						list1.add(v);
					} else {
						if (list1 != null && list1.size() > 0) {
							hash.put("avg" + i, CEIString.round(avgput / list1.size(), 2) + "");
						} else {
							hash.put("avg" + i, "0.0");
						}

						hash.put("max" + i, CEIString.round(tempfloat, 2) + "");
						hash.put("list" + i, list1);
						list1 = new ArrayList();
						v.add(0, value);
						v.add(1, name);
						v.add(2, collecttime);
						tempfloat = 0;
						avgput = 0;
						avgput = avgput + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
						list1.add(v);
						i++;
					}
					tempSub = name;
				}
				if (list1 != null && list1.size() > 0) {
					hash.put("avg" + i, CEIString.round(avgput / list1.size(), 2) + "");
				} else {
					hash.put("avg" + i, "0.0");
				}
				hash.put("max" + i, CEIString.round(tempfloat, 2) + "");
				hash.put("list" + i, list1);
				hash.put("count", i + "");// 不想以后再遍历了
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {

				dbmanager.close();
			}

		}

		return hash;
	}

	/**
	 * 
	 * @param ip
	 * @param sindex
	 * @param starttime
	 * @param endtime
	 * @param entity
	 * @return
	 */
	public Hashtable getUtilhdx(String ip, String sindex, String starttime, String endtime) {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		if (!starttime.equals("") && !endtime.equals("")) {

			String allipstr = SysUtil.doip(ip);
			String sql = "";
			List inList = new ArrayList();
			List outList = new ArrayList();

			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.entity from utilhdx" + allipstr + " h where ");
				sb.append("h.subentity='");
				sb.append(sindex);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {

				sb.append("select h.thevalue,TO_CHAR(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') as collecttime,h.entity from utilhdx" + allipstr + " h where ");
				sb.append("h.subentity='");
				sb.append(sindex);
				sb.append("' and to_date(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') >= '");
				sb.append(starttime);
				sb.append("' and to_date(h.collecttime, 'YYYY-MM-DD HH24:MI:SS') <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			}

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempin = 0;
			double avgin = 0;
			double curin = 0;
			double minin = 0;// 最小值
			int j = 0;
			double tempout = 0;
			double avgout = 0;
			double curout = 0;
			double minout = 0;// 最小值
			try {
				while (rs.next()) {

					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					String entityStr = rs.getString("entity");
					if (entityStr.equals("InBandwidthUtilHdx")) {
						i = i + 1;
						v.add(0, emitStr(thevalue));
						v.add(1, collecttime);
						avgin = avgin + getfloat(thevalue);
						if (i == 1) {
							tempin = getfloat(thevalue);
							minin = getfloat(thevalue);
						}
						if (tempin < getfloat(thevalue))
							tempin = getfloat(thevalue);
						if (minin > getfloat(thevalue))
							minin = getfloat(thevalue);
						curin = getfloat(thevalue);
						inList.add(v);
					} else if (entityStr.equals("OutBandwidthUtilHdx")) {
						j = j + 1;
						v.add(0, emitStr(thevalue));
						v.add(1, collecttime);
						avgout = avgout + getfloat(thevalue);
						if (j == 1) {
							tempout = getfloat(thevalue);
							minout = getfloat(thevalue);
						}
						if (tempout < getfloat(thevalue))
							tempout = getfloat(thevalue);
						if (minout > getfloat(thevalue))
							minout = getfloat(thevalue);
						outList.add(v);
						curout = getfloat(thevalue);
					}

				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				dbmanager.close();
			}

			hash.put("inList", inList);
			hash.put("outList", outList);

			if (inList != null && inList.size() > 0) {
				hash.put("avgin", CEIString.round(avgin / inList.size(), 2) + "");
			} else {
				hash.put("avgin", "0.0");
			}
			if (outList != null && outList.size() > 0) {
				hash.put("avgout", CEIString.round(avgout / outList.size(), 2) + "");
			} else {
				hash.put("avgout", "0.0");
			}
			hash.put("maxin", CEIString.round(tempin, 2) + "");
			hash.put("curin", CEIString.round(curin, 2) + "");
			hash.put("maxout", CEIString.round(tempout, 2) + "");
			hash.put("curout", CEIString.round(curout, 2) + "");
			hash.put("minin", CEIString.round(minin, 2) + "");
			hash.put("minout", CEIString.round(minout, 2) + "");
		}
		return hash;
	}

	/**
	 * 
	 * @param ip
	 * @param sindex
	 * @param starttime
	 * @param endtime
	 * @param entity
	 * @return
	 */
	public Hashtable getUtilhdxper(String ip, String sindex, String starttime, String endtime, String time) {
		Hashtable rsHt = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		
		if (!starttime.equals("") && !endtime.equals("")) {
			String allipstr = SysUtil.doip(ip);
			List inBandwidthUtilHdxPercList = new ArrayList();
			List outBandwidthUtilHdxPercList = new ArrayList();

			StringBuffer sb = new StringBuffer();
			sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.entity from utilhdxperc" + allipstr + " h where ");
			sb.append("h.subentity='");
			sb.append(sindex);
			sb.append("' and h.collecttime >= '");
			sb.append(starttime);
			sb.append("' and h.collecttime <= '");
			sb.append(endtime);
			sb.append("' order by h.collecttime");

			rs = dbmanager.executeQuery(sb.toString());
			int i = 0;
			double maxInBandwidthUtilHdxPerc = 0.0;
			double allInBandwidthUtilHdxPerc = 0.0;
			double currentInBandwidthUtilHdxPerc = 0.0;
			int j = 0;
			double maxOutBandwidthUtilHdxPerc = 0.0;
			double allOutBandwidthUtilHdxPerc = 0.0;
			double currentOutBandwidthUtilHdxPerc = 0.0;
			try {
				Vector tempVector = new Vector();
				String value = (String) null;
				String collectTime = (String) null;
				String entity = (String) null;
				while (rs.next()) {
					tempVector = new Vector();
					value = rs.getString("thevalue");
					collectTime = rs.getString("collecttime");
					entity = rs.getString("entity");
					if (entity.equals("InBandwidthUtilHdxPerc")) {
						currentInBandwidthUtilHdxPerc = getfloat(value);
						allInBandwidthUtilHdxPerc = allInBandwidthUtilHdxPerc + currentInBandwidthUtilHdxPerc;
						if (maxInBandwidthUtilHdxPerc < currentInBandwidthUtilHdxPerc) {
							maxInBandwidthUtilHdxPerc = currentInBandwidthUtilHdxPerc;
						}
						tempVector.add(0, emitStr(value));
						tempVector.add(1, collectTime);
						inBandwidthUtilHdxPercList.add(tempVector);
					} else if (entity.equals("OutBandwidthUtilHdxPerc")) {
						currentOutBandwidthUtilHdxPerc = getfloat(value);
						allOutBandwidthUtilHdxPerc = allOutBandwidthUtilHdxPerc + currentOutBandwidthUtilHdxPerc;
						if (maxOutBandwidthUtilHdxPerc < currentOutBandwidthUtilHdxPerc) {
							maxOutBandwidthUtilHdxPerc = currentOutBandwidthUtilHdxPerc;
						}
						tempVector.add(0, emitStr(value));
						tempVector.add(1, collectTime);
						outBandwidthUtilHdxPercList.add(tempVector);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != rs) {
						rs.close();
					}
					if (null != dbmanager) {
						dbmanager.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			rsHt.put("inList", inBandwidthUtilHdxPercList);
			rsHt.put("outList", outBandwidthUtilHdxPercList);

			if (inBandwidthUtilHdxPercList != null && inBandwidthUtilHdxPercList.size() > 0) {
				rsHt.put("avgin", CEIString.round(allInBandwidthUtilHdxPerc / inBandwidthUtilHdxPercList.size(), 2) + "");
			} else {
				rsHt.put("avgin", "0.0");
			}
			if (outBandwidthUtilHdxPercList != null && outBandwidthUtilHdxPercList.size() > 0) {
				rsHt.put("avgout", CEIString.round(allOutBandwidthUtilHdxPerc / outBandwidthUtilHdxPercList.size(), 2) + "");
			} else {
				rsHt.put("avgout", "0.0");
			}
			rsHt.put("maxin", CEIString.round(maxInBandwidthUtilHdxPerc, 2) + "");
			rsHt.put("curin", CEIString.round(currentInBandwidthUtilHdxPerc, 2) + "");
			rsHt.put("maxout", CEIString.round(maxOutBandwidthUtilHdxPerc, 2) + "");
			rsHt.put("curout", CEIString.round(currentOutBandwidthUtilHdxPerc, 2) + "");
		}
		return rsHt;
	}

	public Hashtable getUpsData(String ip, String category, String starttime, String endtime, String time, String table) throws Exception {
		ResultSet rs = null;
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		if (!starttime.equals("") && !endtime.equals("")) {
			String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
			String tempStr = "";
			String allipstr = "";
			// if (ip.indexOf(".") > 0) {
			// ip1 = ip.substring(0, ip.indexOf("."));
			// ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
			// tempStr = ip
			// .substring(ip.indexOf(".") + 1, ip.lastIndexOf("."));
			// }
			// ip2 = tempStr.substring(0, tempStr.indexOf("."));
			// ip3 = tempStr.substring(tempStr.indexOf(".") + 1,
			// tempStr.length());
			// allipstr = ip1 + ip2 + ip3 + ip4;
			allipstr = SysUtil.doip(ip);

			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();
			sb.append("select h.thevalue,h.collecttime as collecttime,h.unit,h.subentity from " + table + time + allipstr + " h where ");
			sb.append(" h.SUBENTITY='");
			sb.append(category);

			sb.append("' and h.collecttime >= '");
			sb.append(starttime);
			sb.append("' and h.collecttime <= '");
			sb.append(endtime);
			sb.append("' order by h.collecttime");

			sql = sb.toString();
			SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public Hashtable getDataHash(String tablename, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		if (!starttime.equals("") && !endtime.equals("")) {
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			// sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d
			// %H:%i:%s') as collecttime,h.unit from " + tablename + " h where
			// ");
			// sb.append("h.collecttime >= '");
			// sb.append(starttime);
			// sb.append("' and h.collecttime <= '");
			// sb.append(endtime);
			// sb.append("' order by h.collecttime");

			if (SystemConstant.DBType.equals("mysql")) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from " + tablename + " h where ");
				sb.append("h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if (SystemConstant.DBType.equalsIgnoreCase("oracle")) {
				sb.append("select h.thevalue,to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss') as collecttime,h.unit from " + tablename + " h where ");
				sb.append("h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','yyyy-mm-dd hh24:mi:ss') order by h.collecttime");
			}
			sql = sb.toString();
			SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;

			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		dbmanager.close();
		return hash;
	}

	public Hashtable getDataHash(String tablename, String starttime, String endtime, String category) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		double avgInput = 0;

		// System.out.println(category+"------------------------------------------");
		if (!starttime.equals("") && !endtime.equals("")) {
			String sql = "";
			List list1 = new ArrayList();
			String unit = "";
			StringBuffer sb = new StringBuffer();

			if (SystemConstant.DBType.equalsIgnoreCase("mysql")) {
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from " + tablename + " h where SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
			} else if (SystemConstant.DBType.equalsIgnoreCase("oracle")) {
				sb.append("select h.thevalue,to_char(h.collecttime,'yyyy-mm-dd hh24:mi:ss') as collecttime,h.unit from " + tablename + " h where SUBENTITY='");
				sb.append(category);
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','yyyy-mm-dd hh24:mi:ss') order by h.collecttime");
			}
			// sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d
			// %H:%i:%s') as collecttime,h.unit from " + tablename + " h where
			// SUBENTITY='");
			// sb.append(category);
			// sb.append("' and h.collecttime >= '");
			// sb.append(starttime);
			// sb.append("' and h.collecttime <= '");
			// sb.append(endtime);
			// sb.append("' order by h.collecttime");

			sql = sb.toString();
			// SysLogger.info(sql);
			rs = dbmanager.executeQuery(sql);
			int i = 0;
			double tempfloat = 0;
			double avgput = 0;
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					avgput = avgput + getfloat(thevalue);
					if (tempfloat < getfloat(thevalue))
						tempfloat = getfloat(thevalue);
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				dbmanager.close();
			}
			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					unit = (String) tempV.get(2);
				}
			}
			if (list1 != null && list1.size() > 0) {
				hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
			} else {
				hash.put("avgput", "0.0");// yangjun xiugai
			}
			hash.put("max", CEIString.round(tempfloat, 2) + "");
		}
		// for(int i = 0;i<=hash.size();i++){
		// System.out.println("================="+hash.get("list"));
		// }
		dbmanager.close();
		return hash;
	}

	// alex
	public Hashtable[] getDiskByIp(String ip, String category, String starttime, String endtime) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Hashtable[] hash = new Hashtable[3];
		hash[0] = new Hashtable(); // 放图的y值
		hash[1] = new Hashtable(); // 放最大值
		hash[2] = new Hashtable(); // 放平均值
		DBManager dbmanager = new DBManager();

		try {

			String allipstr = SysUtil.doip(ip);
			String sql1 = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql1 = "select max(convert(h.thevalue,SIGNED)) as value,h.subentity,avg(convert(h.thevalue,SIGNED)) as avgvalue from disk" + allipstr + " h " + "where  h.category='" + category + "' and h.collecttime >= '" + starttime + "' " + "and h.collecttime <= '" + endtime + "' and h.thevalue != 'NaN' " + "group by h.subentity order by h.subentity";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql1 = "select max(h.thevalue) as value,h.subentity,avg(h.thevalue) as avgvalue from memory" + allipstr + " h where " + " h.category='" + category + "' and h.collecttime >= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') and h.collecttime <= to_date('" + endtime + "','yyyy-mm-dd hh24:mi:ss') and h.thevalue != 'NaN' group by h.subentity order by h.subentity";
			}
			// stmt = con.prepareStatement(sql1);
			rs = dbmanager.executeQuery(sql1);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					Vector v = new Vector();
					String thevalue = rs.getString("value");
					String subentity = rs.getString("subentity");
					String avgvalue = rs.getString("avgvalue");
					v.add(0, thevalue);
					v.add(1, subentity);
					v.add(2, avgvalue);
					list1.add(v);
				}
			}
			if (list1.size() != 0) {
				String[] key = new String[list1.size()];
				String[] max = new String[list1.size()];
				String[] avg = new String[list1.size()];
				Vector[] vector = new Vector[list1.size()];
				for (int i = 0; i < list1.size(); i++) {
					Vector row = (Vector) list1.get(i);
					key[i] = (String) row.get(1);
					max[i] = (String) row.get(0);
					avg[i] = (String) row.get(2);
					vector[i] = new Vector();
				}
				List list2 = new ArrayList();
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (SystemConstant.DBType.equals("mysql")) {
					sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit,h.subentity from disk" + allipstr + " h where ");
				} else if (SystemConstant.DBType.equals("oracle")) {
					sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit,h.subentity from disk" + allipstr + " h where ");
				}

				// sb.append(ip);
				sb.append(" h.category='");
				sb.append(category);

				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
				}

				sql = sb.toString();
				// System.out.println(sql);
				dbmanager.close();
				dbmanager = new DBManager();
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, rs.getString("subentity"));
					list2.add(v);
					v = new Vector();
					v.add(0, "50");
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, "一级阀值");
					list2.add(v);
					v = new Vector();
					v.add(0, "80");
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, "二级阀值");
					list2.add(v);
					v = new Vector();
					v.add(0, "90");
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, "三级阀值");
					list2.add(v);
				}
				rs.close();
				for (int i = 0; i < list2.size(); i++) {
					Vector obj = (Vector) list2.get(i);
					for (int j = 0; j < list1.size(); j++) {
						if (((String) obj.get(3)).equals(key[j])) {
							vector[j].add(obj);
						}
					}
				}
				String unit = "";
				if (list2.get(0) != null) {
					Vector obj = (Vector) list2.get(0);
					unit = (String) obj.get(2);
				}
				for (int i = 0; i < list1.size(); i++) {
					hash[0].put(key[i], vector[i]);
					hash[1].put(key[i], dofloat(max[i]) + unit);
					hash[2].put(key[i], dofloat(avg[i]) + unit);
				}
				hash[0].put("unit", unit);
				hash[0].put("key", key);
				if (category.equalsIgnoreCase("disk")) {
					hash[1].put("key", key);
				}
			}
			dbmanager.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}
}
