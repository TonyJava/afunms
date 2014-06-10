package com.afunms.polling.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.Errptlog;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DeviceCollectEntity;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.FlashCollectEntity;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessCollectEntity;
import com.afunms.polling.om.ServiceCollectEntity;
import com.afunms.polling.om.SoftwareCollectEntity;
import com.afunms.polling.om.StorageCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.task.FtpUtil;
import com.afunms.system.dao.FtpTransConfigDao;
import com.afunms.system.model.FtpTransConfig;
import com.afunms.temp.model.ServiceNodeTemp;
import com.afunms.topology.util.XmlDataOperator;

public class ProcessNetData {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static String[] iproutertype = { "", "", "", "direct(3)", "indirect(4)" };

	private static String[] iprouterproto = { "", "other(1)", "local(2)", "netmgmt(3)", "icmp(4)", "egp(5)", "ggp(6)", "hello(7)", "rip(8)", "is-is(9)", "es-is(10)", "ciscoIgrp(11)", "bbnSpfIgp(12)", "ospf(13)", "bgp(14)" };

	public ProcessNetData() {
	}

	public synchronized boolean processHostProcData(String tablename, String ip, Vector proVector) {
		if (proVector != null && proVector.size() > 0) {
			DBManager dbmanager = new DBManager();

			try {
				String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values(?,?,?,?,?,?,?,?,?,?,?)";
				dbmanager.setPrepareSql(sql);
				// ps = conn.prepareStatement(sql);
				Calendar tempCal = null;
				Date cc = null;
				String time = null;
				List list = new ArrayList();
				for (int i = 0; i < proVector.size(); i++) {
					ProcessCollectEntity processdata = (ProcessCollectEntity) proVector.elementAt(i);
					processdata.setCount(1L);
					if (processdata.getRestype().equals("dynamic")) {
						tempCal = (Calendar) processdata.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
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
						dbmanager.addPrepareProcBatch(list);
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

				}
			} catch (Exception e) {

			} finally {

				if (dbmanager != null) {
					try {
						dbmanager.close();
					} catch (Exception e) {

					}
					dbmanager = null;
				}

			}

		}
		return true;
	}

	/**
	 * 处理路由表信息
	 * 
	 * @param hostid
	 * @param ip
	 * @param type
	 * @param subtype
	 * @param iprouterhash
	 * @return
	 */
	public boolean processRouterData(String hostid, String ip, String type, String subtype, Hashtable iprouterhash) {
		boolean flag = false;
		Vector iprouterVector = null;
		IpRouter vo = null;
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(hostid));
		try {
			XmlDataOperator xmlOpr = new XmlDataOperator();
			if (host.getTransfer() == 1) {
				xmlOpr.setFile("net_router" + ip + ".xml");
				xmlOpr.init4createXml();
			}

			iprouterVector = (Vector) iprouterhash.get("iprouter");
			if (iprouterVector != null && iprouterVector.size() > 0) {

				// 放入内存里
				ShareData.setIprouterdata(ip, iprouterVector);
				Calendar date = Calendar.getInstance();
				String time = sdf.format(date.getTime());
				StringBuffer sql = null;
				for (int i = 0; i < iprouterVector.size(); i++) {
					vo = (IpRouter) iprouterVector.elementAt(i);
					if (host.getTransfer() == 1) {

						xmlOpr.addIPNode(ip, type, subtype, vo.getIfindex(), vo.getNexthop(), vo.getProto().longValue() + "", vo.getType().longValue() + "", vo.getMask(), time, vo.getPhysaddress(), vo.getDest());
						flag = true;
					}
					vo = null;
					sql = null;
				}
				if (flag) {
					xmlOpr.createXml();
					boolean uploadsuccess = false;
					FtpTransConfigDao ftpconfdao = new FtpTransConfigDao();
					FtpTransConfig ftpConfig = null;
					try {
						ftpConfig = ftpconfdao.getFtpTransMonitorConfig();
						if (ftpConfig != null) {
							FtpUtil ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/net_router", host.getIpAddress() + ".xml");
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ftpconfdao.close();
					}
				}
				xmlOpr = null;
			}
			iprouterhash = null;
			iprouterVector = null;

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean processInterfaceData(String hostid, String ip, String type, String subtype, Hashtable interfacehash) {
		DBManager deleteManager = new DBManager();
		DBManager dbmanager = new DBManager();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(hostid));
		boolean flag = false;
		try {
			XmlDataOperator xmlOpr = new XmlDataOperator();
			if (node.getTransfer() == 1) {
				xmlOpr.setFile(type + "_" + ip + ".xml");
				xmlOpr.init4createXml();
			}

			String deleteSql = "delete from nms_interface_data_temp where nodeid='" + hostid + "'";
			StringBuffer sql = new StringBuffer();
			sql.append("insert into nms_interface_data_temp");
			sql.append("(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) ");
			sql.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			// SysLogger.info(sql.toString());
			dbmanager.setPrepareSql(sql.toString());
			Calendar tempCal = null;
			Date cc = null;
			String time = null;
			sql = null;
			List list = new ArrayList();

			// 开始设置综合流速
			// Vector<AllUtilHdx> allutilhdxVector =
			// (Vector<AllUtilHdx>)interfacehash.get("allutilhdx");
			if (interfacehash.get("allutilhdx") != null && ((Vector<AllUtilHdx>) interfacehash.get("allutilhdx")).size() > 0) {
				AllUtilHdx allutilhdx = null;
				try {
					for (int i = 0; i < ((Vector<AllUtilHdx>) interfacehash.get("allutilhdx")).size(); i++) {
						allutilhdx = (AllUtilHdx) ((Vector<AllUtilHdx>) interfacehash.get("allutilhdx")).elementAt(i);
						tempCal = (Calendar) allutilhdx.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(allutilhdx.getCategory());
						list.add(allutilhdx.getEntity());
						list.add(allutilhdx.getSubentity());
						list.add(allutilhdx.getThevalue());
						list.add(allutilhdx.getChname());
						list.add(allutilhdx.getRestype());
						list.add(time);
						list.add(allutilhdx.getUnit());
						list.add(allutilhdx.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, allutilhdx.getCategory(), allutilhdx.getEntity(), allutilhdx.getSubentity(), allutilhdx.getThevalue(), allutilhdx.getChname(), allutilhdx.getRestype(), time, allutilhdx.getUnit(), allutilhdx.getBak());
							flag = true;
						}
						list = null;
						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, allutilhdx.getCategory());
						// ps.setString(6, allutilhdx.getEntity());
						// ps.setString(7, allutilhdx.getSubentity());
						// ps.setString(8, allutilhdx.getThevalue());
						// ps.setString(9, allutilhdx.getChname());
						// ps.setString(10, allutilhdx.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, allutilhdx.getUnit());
						// ps.setString(13, allutilhdx.getBak());
						// ps.addBatch();
						allutilhdx = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置综合流速

			// 开始设置带宽利用率
			// Vector utilhdxpercVector = (Vector)
			// interfacehash.get("utilhdxperc");
			if (interfacehash.get("utilhdxperc") != null && ((Vector) interfacehash.get("utilhdxperc")).size() > 0) {
				UtilHdxPerc utilhdxperc = null;
				try {
					for (int i = 0; i < ((Vector) interfacehash.get("utilhdxperc")).size(); i++) {
						utilhdxperc = (UtilHdxPerc) ((Vector) interfacehash.get("utilhdxperc")).elementAt(i);
						tempCal = (Calendar) utilhdxperc.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(utilhdxperc.getCategory());
						list.add(utilhdxperc.getEntity());
						list.add(utilhdxperc.getSubentity());
						list.add(utilhdxperc.getThevalue());
						list.add(utilhdxperc.getChname());
						list.add(utilhdxperc.getRestype());
						list.add(time);
						list.add(utilhdxperc.getUnit());
						list.add(utilhdxperc.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, utilhdxperc.getCategory(), utilhdxperc.getEntity(), utilhdxperc.getSubentity(), utilhdxperc.getThevalue(), utilhdxperc.getChname(), utilhdxperc.getRestype(), time, utilhdxperc.getUnit(), utilhdxperc.getBak());
							flag = true;
						}
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, utilhdxperc.getCategory());
						// ps.setString(6, utilhdxperc.getEntity());
						// ps.setString(7, utilhdxperc.getSubentity());
						// ps.setString(8, utilhdxperc.getThevalue());
						// ps.setString(9, utilhdxperc.getChname());
						// ps.setString(10, utilhdxperc.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, utilhdxperc.getUnit());
						// ps.setString(13, utilhdxperc.getBak());
						// ps.addBatch();
						utilhdxperc = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置带宽利用率

			// 开始设置流速
			// Vector utilhdxVector = (Vector) interfacehash.get("utilhdx");
			if (interfacehash.get("utilhdx") != null && ((Vector) interfacehash.get("utilhdx")).size() > 0) {
				try {
					UtilHdx utilhdx = null;
					for (int i = 0; i < ((Vector) interfacehash.get("utilhdx")).size(); i++) {
						utilhdx = (UtilHdx) ((Vector) interfacehash.get("utilhdx")).elementAt(i);
						tempCal = (Calendar) utilhdx.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(utilhdx.getCategory());
						list.add(utilhdx.getEntity());
						list.add(utilhdx.getSubentity());
						list.add(utilhdx.getThevalue());
						list.add(utilhdx.getChname());
						list.add(utilhdx.getRestype());
						list.add(time);
						list.add(utilhdx.getUnit());
						list.add(utilhdx.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, utilhdx.getCategory(), utilhdx.getEntity(), utilhdx.getSubentity(), utilhdx.getThevalue(), utilhdx.getChname(), utilhdx.getRestype(), time, utilhdx.getUnit(), utilhdx.getBak());
							flag = true;
						}
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, utilhdx.getCategory());
						// ps.setString(6, utilhdx.getEntity());
						// ps.setString(7, utilhdx.getSubentity());
						// ps.setString(8, utilhdx.getThevalue());
						// ps.setString(9, utilhdx.getChname());
						// ps.setString(10, utilhdx.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, utilhdx.getUnit());
						// ps.setString(13, utilhdx.getBak());
						// ps.addBatch();
						utilhdx = null;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置流速

			// 开始设置流速
			// Vector interfaceVector = (Vector) interfacehash.get("interface");
			if ((Vector) interfacehash.get("interface") != null && ((Vector) interfacehash.get("interface")).size() > 0) {
				try {
					Interfacecollectdata interfacedata = null;
					for (int i = 0; i < ((Vector) interfacehash.get("interface")).size(); i++) {
						interfacedata = (Interfacecollectdata) ((Vector) interfacehash.get("interface")).elementAt(i);
						tempCal = (Calendar) interfacedata.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(interfacedata.getCategory());
						list.add(interfacedata.getEntity());
						list.add(interfacedata.getSubentity());
						list.add(interfacedata.getThevalue());
						list.add(interfacedata.getChname());
						list.add(interfacedata.getRestype());
						list.add(time);
						list.add(interfacedata.getUnit());
						list.add(interfacedata.getBak());
						dbmanager.addPrepareBatch(list);
						// SysLogger.info("=================================");
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, interfacedata.getCategory(), interfacedata.getEntity(), interfacedata.getSubentity(), interfacedata.getThevalue(), interfacedata.getChname(), interfacedata.getRestype(), time, interfacedata.getUnit(), interfacedata.getBak());
							flag = true;
						}// SysLogger.info("=================================");
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, interfacedata.getCategory());
						// ps.setString(6, interfacedata.getEntity());
						// ps.setString(7, interfacedata.getSubentity());
						// ps.setString(8, interfacedata.getThevalue());
						// ps.setString(9, interfacedata.getChname());
						// ps.setString(10, interfacedata.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, interfacedata.getUnit());
						// ps.setString(13, interfacedata.getBak());
						// ps.addBatch();
						interfacedata = null;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置流速

			// 开始设置丢包率
			// Vector discardspercVector = (Vector)
			// interfacehash.get("discardsperc");
			if ((Vector) interfacehash.get("discardsperc") != null && ((Vector) interfacehash.get("discardsperc")).size() > 0) {
				try {
					DiscardsPerc discardsperc = null;
					for (int i = 0; i < ((Vector) interfacehash.get("discardsperc")).size(); i++) {
						discardsperc = (DiscardsPerc) ((Vector) interfacehash.get("discardsperc")).elementAt(i);
						tempCal = (Calendar) discardsperc.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(discardsperc.getCategory());
						list.add(discardsperc.getEntity());
						list.add(discardsperc.getSubentity());
						list.add(discardsperc.getThevalue());
						list.add(discardsperc.getChname());
						list.add(discardsperc.getRestype());
						list.add(time);
						list.add(discardsperc.getUnit());
						list.add(discardsperc.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, discardsperc.getCategory(), discardsperc.getEntity(), discardsperc.getSubentity(), discardsperc.getThevalue(), discardsperc.getChname(), discardsperc.getRestype(), time, discardsperc.getUnit(), discardsperc.getBak());
							flag = true;
						}
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, discardsperc.getCategory());
						// ps.setString(6, discardsperc.getEntity());
						// ps.setString(7, discardsperc.getSubentity());
						// ps.setString(8, discardsperc.getThevalue());
						// ps.setString(9, discardsperc.getChname());
						// ps.setString(10, discardsperc.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, discardsperc.getUnit());
						// ps.setString(13, discardsperc.getBak());
						// ps.addBatch();
						discardsperc = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置丢包率

			// 开始设置错误率
			// Vector errorspercVector = (Vector)
			// interfacehash.get("errorsperc");
			if ((Vector) interfacehash.get("errorsperc") != null && ((Vector) interfacehash.get("errorsperc")).size() > 0) {
				try {
					ErrorsPerc errorsperc = null;
					for (int i = 0; i < ((Vector) interfacehash.get("errorsperc")).size(); i++) {
						errorsperc = (ErrorsPerc) ((Vector) interfacehash.get("errorsperc")).elementAt(i);
						tempCal = (Calendar) errorsperc.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(errorsperc.getCategory());
						list.add(errorsperc.getEntity());
						list.add(errorsperc.getSubentity());
						list.add(errorsperc.getThevalue());
						list.add(errorsperc.getChname());
						list.add(errorsperc.getRestype());
						list.add(time);
						list.add(errorsperc.getUnit());
						list.add(errorsperc.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, errorsperc.getCategory(), errorsperc.getEntity(), errorsperc.getSubentity(), errorsperc.getThevalue(), errorsperc.getChname(), errorsperc.getRestype(), time, errorsperc.getUnit(), errorsperc.getBak());
							flag = true;
						}
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, errorsperc.getCategory());
						// ps.setString(6, errorsperc.getEntity());
						// ps.setString(7, errorsperc.getSubentity());
						// ps.setString(8, errorsperc.getThevalue());
						// ps.setString(9, errorsperc.getChname());
						// ps.setString(10, errorsperc.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, errorsperc.getUnit());
						// ps.setString(13, errorsperc.getBak());
						// ps.addBatch();
						errorsperc = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置错误率

			// 开始设置数据包
			// Vector packsVector = (Vector) interfacehash.get("packs");
			if ((Vector) interfacehash.get("packs") != null && ((Vector) interfacehash.get("packs")).size() > 0) {
				try {
					Packs packs = null;
					for (int i = 0; i < ((Vector) interfacehash.get("packs")).size(); i++) {
						packs = (Packs) ((Vector) interfacehash.get("packs")).elementAt(i);
						tempCal = (Calendar) packs.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(packs.getCategory());
						list.add(packs.getEntity());
						list.add(packs.getSubentity());
						list.add(packs.getThevalue());
						list.add(packs.getChname());
						list.add(packs.getRestype());
						list.add(time);
						list.add(packs.getUnit());
						list.add(packs.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, packs.getCategory(), packs.getEntity(), packs.getSubentity(), packs.getThevalue(), packs.getChname(), packs.getRestype(), time, packs.getUnit(), packs.getBak());
							flag = true;
						}
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, packs.getCategory());
						// ps.setString(6, packs.getEntity());
						// ps.setString(7, packs.getSubentity());
						// ps.setString(8, packs.getThevalue());
						// ps.setString(9, packs.getChname());
						// ps.setString(10, packs.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, packs.getUnit());
						// ps.setString(13, packs.getBak());
						// ps.addBatch();
						packs = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置数据包

			// 开始设置入口广播和多播数据包
			// Vector inpacksVector = (Vector) interfacehash.get("inpacks");
			if ((Vector) interfacehash.get("inpacks") != null && ((Vector) interfacehash.get("inpacks")).size() > 0) {
				try {
					InPkts packs = null;
					for (int i = 0; i < ((Vector) interfacehash.get("inpacks")).size(); i++) {
						packs = (InPkts) ((Vector) interfacehash.get("inpacks")).elementAt(i);
						tempCal = (Calendar) packs.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(packs.getCategory());
						list.add(packs.getEntity());
						list.add(packs.getSubentity());
						list.add(packs.getThevalue());
						list.add(packs.getChname());
						list.add(packs.getRestype());
						list.add(time);
						list.add(packs.getUnit());
						list.add(packs.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, packs.getCategory(), packs.getEntity(), packs.getSubentity(), packs.getThevalue(), packs.getChname(), packs.getRestype(), time, packs.getUnit(), packs.getBak());
							flag = true;
						}
						list = null;
						//								
						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, packs.getCategory());
						// ps.setString(6, packs.getEntity());
						// ps.setString(7, packs.getSubentity());
						// ps.setString(8, packs.getThevalue());
						// ps.setString(9, packs.getChname());
						// ps.setString(10, packs.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, packs.getUnit());
						// ps.setString(13, packs.getBak());
						// ps.addBatch();
						packs = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置入口广播和多播数据包

			// 开始设置出口广播和多播数据包
			// Vector outpacksVector = (Vector) interfacehash.get("outpacks");
			if ((Vector) interfacehash.get("outpacks") != null && ((Vector) interfacehash.get("outpacks")).size() > 0) {
				try {
					OutPkts packs = null;
					for (int i = 0; i < ((Vector) interfacehash.get("outpacks")).size(); i++) {
						packs = (OutPkts) ((Vector) interfacehash.get("outpacks")).elementAt(i);
						tempCal = (Calendar) packs.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						list = new ArrayList();
						list.add(hostid);
						list.add(ip);
						list.add(type);
						list.add(subtype);
						list.add(packs.getCategory());
						list.add(packs.getEntity());
						list.add(packs.getSubentity());
						list.add(packs.getThevalue());
						list.add(packs.getChname());
						list.add(packs.getRestype());
						list.add(time);
						list.add(packs.getUnit());
						list.add(packs.getBak());
						dbmanager.addPrepareBatch(list);
						if (node.getTransfer() == 1) {
							xmlOpr.addNode(type, subtype, packs.getCategory(), packs.getEntity(), packs.getSubentity(), packs.getThevalue(), packs.getChname(), packs.getRestype(), time, packs.getUnit(), packs.getBak());
							flag = true;
						}
						list = null;

						// ps.setString(1, hostid);
						// ps.setString(2, ip);
						// ps.setString(3, type);
						// ps.setString(4, subtype);
						// ps.setString(5, packs.getCategory());
						// ps.setString(6, packs.getEntity());
						// ps.setString(7, packs.getSubentity());
						// ps.setString(8, packs.getThevalue());
						// ps.setString(9, packs.getChname());
						// ps.setString(10, packs.getRestype());
						// ps.setString(11, time);
						// ps.setString(12, packs.getUnit());
						// ps.setString(13, packs.getBak());
						// ps.addBatch();
						packs = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 结束设置出口广播和多播数据包

			try {
				deleteManager.executeUpdate(deleteSql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			deleteSql = null;
			// 开始执行批处理
			try {
				dbmanager.executePreparedBatch();
			} catch (Exception e) {

			}
			if (flag) {
				xmlOpr.createXml();
				boolean uploadsuccess = false;
				FtpTransConfigDao ftpconfdao = new FtpTransConfigDao();
				FtpTransConfig ftpConfig = null;
				try {
					// downloadflag = ftputil.ftpOne();
					ftpConfig = ftpconfdao.getFtpTransMonitorConfig();
					if (ftpConfig != null) {
						FtpUtil ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_" + node.getIpAddress() + ".xml");
					}

				} catch (Exception e) {

				} finally {
					ftpconfdao.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if(ps != null){
			// try{
			// ps.clearBatch();
			// ps.close();
			// }catch(Exception e){
			//				
			// }
			// ps = null;
			// }
			// if(conn != null){
			// try{
			// conn.close();
			// }catch(Exception e){
			//				
			// }
			// conn = null;
			// }
			if (dbmanager != null) {
				try {
					dbmanager.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				dbmanager = null;
			}
			if (deleteManager != null) {
				deleteManager.close();
				deleteManager = null;
			}
		}
		return true;
	}

	/**
	 * windows
	 * 
	 * @param type
	 * @param datahash
	 * @return
	 */
	public boolean processHostData(String type, Hashtable datahash) {
		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			Hashtable allpinghash = new Hashtable();
			Hashtable allcpuhash = new Hashtable();
			Hashtable allpmemoryhash = new Hashtable();
			Hashtable allvmemoryhash = new Hashtable();
			Hashtable alldiskhash = new Hashtable();
			Hashtable allstoragehash = new Hashtable();
			Hashtable allhardwarehash = new Hashtable();
			Hashtable allsoftwarehash = new Hashtable();
			Hashtable allservicehash = new Hashtable();
			Hashtable allinpackshash = new Hashtable();
			Hashtable alloutpackshash = new Hashtable();
			Hashtable allipmachash = new Hashtable();
			Hashtable allinterfacehash = new Hashtable();
			Hashtable allsystemgrouphash = new Hashtable();
			Hashtable allprocesshash = new Hashtable();
			NodeUtil nodeUtil = new NodeUtil();
			DBManager dbmanager = new DBManager();
			List list = new ArrayList();
			try {
				while (iphash.hasMoreElements()) {
					String ip = (String) iphash.nextElement();
					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata != null) {

						// Enumeration ipdatahash = ipdata.keys();
						// while(iphash.hasMoreElements()){
						// String name = (String)iphash.nextElement();
						// }

						// 处理主机设备的数据
						// ping信息
						if (ipdata.containsKey("ping")) {
							Hashtable pinghash = (Hashtable) ipdata.get("ping");
							Vector pingVector = (Vector) pinghash.get("ping");
							if (pingVector != null && pingVector.size() > 0) {
								allpinghash.put(ip, pingVector);
							}
						}

						// CPU信息
						if (ipdata.containsKey("cpu")) {
							Hashtable cpuhash = (Hashtable) ipdata.get("cpu");
							Vector cpuVector = (Vector) cpuhash.get("cpu");
							if (cpuVector != null && cpuVector.size() > 0) {
								allcpuhash.put(ip, cpuVector);
							}
						}
						// physicalmemory
						if (ipdata.containsKey("physicalmemory")) {
							Hashtable physicalmemoryhash = (Hashtable) ipdata.get("physicalmemory");
							Vector memoryVector = (Vector) physicalmemoryhash.get("memory");
							if (memoryVector != null && memoryVector.size() > 0) {
								allpmemoryhash.put(ip, memoryVector);
							}
						}

						// virtualmemory
						if (ipdata.containsKey("virtualmemory")) {
							Hashtable virtualmemoryhash = (Hashtable) ipdata.get("virtualmemory");
							Vector memoryVector = (Vector) virtualmemoryhash.get("memory");
							if (memoryVector != null && memoryVector.size() > 0) {
								allvmemoryhash.put(ip, memoryVector);
							}
						}
						// ---------------------------
						// 进程
						if (ipdata.containsKey("process")) {
							Hashtable processhash = (Hashtable) ipdata.get("process");
							Vector processVector = (Vector) processhash.get("process");
							if (processVector != null && processVector.size() > 0) {
								allprocesshash.put(ip, processVector);
							}
						}

						// disk
						if (ipdata.containsKey("disk")) {
							Hashtable diskhash = (Hashtable) ipdata.get("disk");
							Vector diskVector = (Vector) diskhash.get("disk");
							if (diskVector != null && diskVector.size() > 0) {
								alldiskhash.put(ip, diskVector);
							}
						}

						// 存储
						if (ipdata.containsKey("storage")) {
							Hashtable storhash = (Hashtable) ipdata.get("storage");
							Vector storVector = (Vector) storhash.get("storage");
							if (storVector != null && storVector.size() > 0) {
								allstoragehash.put(ip, storVector);
							}
						}

						// 硬件信息
						if (ipdata.containsKey("hardware")) {
							Hashtable devhash = (Hashtable) ipdata.get("hardware");
							Vector devVector = (Vector) devhash.get("device");
							if (devVector != null && devVector.size() > 0) {
								allhardwarehash.put(ip, devVector);
							}
						}

						// 软件信息
						if (ipdata.containsKey("software")) {
							Hashtable softhash = (Hashtable) ipdata.get("software");
							Vector sofVector = (Vector) softhash.get("software");
							if (sofVector != null && sofVector.size() > 0) {
								allsoftwarehash.put(ip, sofVector);
							}
						}
						// -------------
						// 服务信息
						if (ipdata.containsKey("service")) {
							Hashtable servhash = (Hashtable) ipdata.get("service");
							Vector servVector = (Vector) servhash.get("winservice");
							if (servVector != null && servVector.size() > 0) {
								allservicehash.put(ip, servVector);
							}
						}

						// 数据包信息
						if (ipdata.containsKey("packs")) {
							Hashtable packshash = (Hashtable) ipdata.get("packs");
							Vector inpacksVector = (Vector) packshash.get("inpacks");
							if (inpacksVector != null && inpacksVector.size() > 0) {
								allinpackshash.put(ip, inpacksVector);
							}
							Vector outpacksVector = (Vector) packshash.get("outpacks");
							if (outpacksVector != null && outpacksVector.size() > 0) {
								alloutpackshash.put(ip, outpacksVector);
							}
						}

						// ARP信息
						if (ipdata.containsKey("ipmac")) {
							Hashtable ipmachash = (Hashtable) ipdata.get("ipmac");
							Vector ipmacVector = (Vector) ipmachash.get("ipmac");
							if (ipmacVector != null && ipmacVector.size() > 0) {
								allipmachash.put(ip, ipmacVector);
							}
						}

						// 接口信息
						if (ipdata.containsKey("interface")) {
							Hashtable interfacehash = (Hashtable) ipdata.get("interface");
							if (interfacehash != null && interfacehash.size() > 0) {
								allinterfacehash.put(ip, interfacehash);
							}
						}

						// 系统属性信息
						if (ipdata.containsKey("systemgroup")) {
							Hashtable systemhash = (Hashtable) ipdata.get("systemgroup");
							Vector systemVector = (Vector) systemhash.get("system");
							if (systemVector != null && systemVector.size() > 0) {
								allsystemgrouphash.put(ip, systemVector);
							}
						}
					}
				}
				boolean uploadsuccess = false;
				FtpTransConfigDao ftpconfdao = new FtpTransConfigDao();
				FtpTransConfig ftpConfig = null;
				try {
					ftpConfig = ftpconfdao.getFtpTransMonitorConfig();
					FtpUtil ftputil = null;
					if (ftpConfig != null) {
						ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ftpconfdao.close();
				}
				if (ftpConfig != null) {
					FtpUtil ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());

					// 处理PING入库
					if (allpinghash != null && allpinghash.size() > 0) {
						boolean flag = false;
						XmlDataOperator xmlOpr = null;
						Enumeration pinghash = allpinghash.keys();
						String pingsql = "insert into nms_ping_data_temp" + "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
						dbmanager.setPrepareSql(pingsql);
						// pingps = conn.prepareStatement(pingsql);
						while (pinghash.hasMoreElements()) {
							String ip = (String) pinghash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector pingVector = (Vector) allpinghash.get(ip);
							if (pingVector != null && pingVector.size() > 0) {
								String deleteSql = "delete from nms_ping_data_temp where nodeid='" + host.getId() + "'";
								dbmanager.addBatch(deleteSql);
								for (int i = 0; i < pingVector.size(); i++) {
									PingCollectEntity vo = (PingCollectEntity) pingVector.elementAt(i);
									try {
										Calendar tempCal = (Calendar) vo.getCollecttime();
										Date cc = tempCal.getTime();
										String time = sdf.format(cc);
										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(vo.getCategory());
										list.add(vo.getEntity());
										list.add(vo.getSubentity());
										list.add(vo.getThevalue());
										list.add(vo.getChname());
										list.add(vo.getRestype());
										list.add(time);
										list.add(vo.getUnit());
										list.add(vo.getBak());
										dbmanager.addPrepareBatch(list);
										// SysLogger.info("===ip:"+ip+"
										// vo.getSubentity():"+vo.getThevalue());
										if (host.getTransfer() == 1) {
											if (!flag) {
												xmlOpr = new XmlDataOperator();
												xmlOpr.setFile("host_ping.xml");
												xmlOpr.init4createXml();
											}
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
										list = null;

										// pingps.setString(1, host.getId()+"");
										// pingps.setString(2, ip);
										// pingps.setString(3, type);
										// pingps.setString(4,
										// nodeDTO.getSubtype());
										// pingps.setString(5,
										// vo.getCategory());
										// pingps.setString(6, vo.getEntity());
										// pingps.setString(7,
										// vo.getSubentity());
										// pingps.setString(8,
										// vo.getThevalue());
										// pingps.setString(9, vo.getChname());
										// pingps.setString(10,
										// vo.getRestype());
										// pingps.setString(11, time);
										// pingps.setString(12, vo.getUnit());
										// pingps.setString(13, vo.getBak());
										// pingps.addBatch();
										// stmt.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "host_ping.xml");

						}
						xmlOpr = null;
						try {
							dbmanager.executeBatch();
							dbmanager.executePreparedBatch();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					// 处理CPU入库
					if (allcpuhash != null && allcpuhash.size() > 0) {
						boolean flag = false;
						XmlDataOperator xmlOpr = null;
						Enumeration cpuhash = allcpuhash.keys();
						while (cpuhash.hasMoreElements()) {
							String ip = (String) cpuhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector cpuVector = (Vector) allcpuhash.get(ip);
							String deleteSql = "delete from nms_cpu_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							// 得到CPU平均
							CpuCollectEntity vo = (CpuCollectEntity) cpuVector.elementAt(0);
							try {
								Calendar tempCal = (Calendar) vo.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);

								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_cpu_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(host.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(type);
								sql.append("','");
								sql.append(nodeDTO.getSubtype());
								sql.append("','");
								sql.append(vo.getCategory());
								sql.append("','");
								sql.append(vo.getEntity());
								sql.append("','");
								sql.append(vo.getSubentity());
								sql.append("','");
								sql.append(vo.getThevalue());
								sql.append("','");
								sql.append(vo.getChname());
								sql.append("','");
								sql.append(vo.getRestype());
								sql.append("','");
								sql.append(time);
								sql.append("','");
								sql.append(vo.getUnit());
								sql.append("','");
								sql.append(vo.getBak());
								sql.append("')");
								dbmanager.addBatch(sql.toString());
								if (host.getTransfer() == 1) {
									if (!flag) {
										xmlOpr = new XmlDataOperator();
										xmlOpr.setFile("host_cpu.xml");
										xmlOpr.init4createXml();
									}
									xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
									flag = true;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "host_cpu.xml");

						}
						xmlOpr = null;
					}

					// 处理物理内存入库
					boolean memFlag = false;
					XmlDataOperator xmlOprt = null;
					if (allpmemoryhash != null && allpmemoryhash.size() > 0) {

						Enumeration pmhash = allpmemoryhash.keys();
						while (pmhash.hasMoreElements()) {
							String ip = (String) pmhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector memoryVector = (Vector) allpmemoryhash.get(ip);
							String deleteSql = "delete from nms_memory_data_temp where nodeid='" + host.getId() + "' and sindex='PhysicalMemory'";
							dbmanager.addBatch(deleteSql);
							if (memoryVector != null && memoryVector.size() > 0) {
								for (int i = 0; i < memoryVector.size(); i++) {
									MemoryCollectEntity vo = (MemoryCollectEntity) memoryVector.elementAt(i);
									if (!vo.getSubentity().equalsIgnoreCase("PhysicalMemory"))
										continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(vo.getCategory());
										sql.append("','");
										sql.append(vo.getEntity());
										sql.append("','");
										sql.append(vo.getSubentity());
										sql.append("','");
										sql.append(vo.getThevalue());
										sql.append("','");
										sql.append(vo.getChname());
										sql.append("','");
										sql.append(vo.getRestype());
										sql.append("','");
										sql.append(time);
										sql.append("','");
										sql.append(vo.getUnit());
										sql.append("','");
										sql.append(vo.getBak());
										sql.append("')");
										// SysLogger.info(sql.toString());
										dbmanager.addBatch(sql.toString());
										if (host.getTransfer() == 1) {
											if (!memFlag) {
												xmlOprt = new XmlDataOperator();
												xmlOprt.setFile("host_mem.xml");
												xmlOprt.init4createXml();

											}
											xmlOprt.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											memFlag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}

					}

					// 处理虚拟内存入库
					if (allvmemoryhash != null && allvmemoryhash.size() > 0) {

						Enumeration vmhash = allvmemoryhash.keys();
						while (vmhash.hasMoreElements()) {
							String ip = (String) vmhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector memoryVector = (Vector) allvmemoryhash.get(ip);
							String deleteSql = "delete from nms_memory_data_temp where nodeid='" + host.getId() + "' and sindex='VirtualMemory'";
							dbmanager.addBatch(deleteSql);
							if (memoryVector != null && memoryVector.size() > 0) {
								for (int i = 0; i < memoryVector.size(); i++) {
									MemoryCollectEntity vo = (MemoryCollectEntity) memoryVector.elementAt(i);
									if (!vo.getSubentity().equalsIgnoreCase("VirtualMemory"))
										continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(vo.getCategory());
										sql.append("','");
										sql.append(vo.getEntity());
										sql.append("','");
										sql.append(vo.getSubentity());
										sql.append("','");
										sql.append(vo.getThevalue());
										sql.append("','");
										sql.append(vo.getChname());
										sql.append("','");
										sql.append(vo.getRestype());
										sql.append("','");
										sql.append(time);
										sql.append("','");
										sql.append(vo.getUnit());
										sql.append("','");
										sql.append(vo.getBak());
										sql.append("')");
										dbmanager.addBatch(sql.toString());
										if (host.getTransfer() == 1) {
											if (!memFlag) {
												xmlOprt = new XmlDataOperator();
												xmlOprt.setFile("host_mem.xml");
												xmlOprt.init4createXml();
											}
											xmlOprt.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											memFlag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					if (memFlag) {
						xmlOprt.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "host_mem.xml");

					}
					xmlOprt = null;
					// 处理进程信息入库
					if (allprocesshash != null && allprocesshash.size() > 0) {
						Enumeration processhash = allprocesshash.keys();
						while (processhash.hasMoreElements()) {
							String ip = (String) processhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector processVector = (Vector) allprocesshash.get(ip);
							String deleteSql = "delete from nms_process_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							if (processVector != null && processVector.size() > 0) {
								for (int i = 0; i < processVector.size(); i++) {
									ProcessCollectEntity vo = (ProcessCollectEntity) processVector.elementAt(i);
									// if (vo.getSubentity().equals("Physical
									// Memory") ||
									// vo.getSubentity().equals("Virtual
									// Memory")||
									// vo.getSubentity().trim().length()==0)continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									String thevalue = vo.getThevalue();
									if (thevalue != null) {
										thevalue = thevalue.replaceAll("\\\\", "/");
										if (thevalue.length() > 50) {
											thevalue = thevalue.substring(0, 50) + "...";
										}
									}
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_process_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(vo.getCategory());
										sql.append("','");
										sql.append(vo.getEntity());
										sql.append("','");
										sql.append(vo.getSubentity());
										sql.append("','");
										sql.append(thevalue);
										sql.append("','");
										sql.append(vo.getChname());
										sql.append("','");
										sql.append(vo.getRestype());
										sql.append("','");
										sql.append(time);
										sql.append("','");
										sql.append(vo.getUnit());
										sql.append("','");
										sql.append(vo.getBak());
										sql.append("')");
										// SysLogger.info(sql.toString());
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					// 处理磁盘入库
					if (alldiskhash != null && alldiskhash.size() > 0) {
						boolean flag = false;
						XmlDataOperator xmlOpr = null;
						Enumeration diskhash = alldiskhash.keys();
						while (diskhash.hasMoreElements()) {
							String ip = (String) diskhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector diskVector = (Vector) alldiskhash.get(ip);
							String deleteSql = "delete from nms_disk_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							if (diskVector != null && diskVector.size() > 0) {
								for (int i = 0; i < diskVector.size(); i++) {
									DiskCollectEntity vo = (DiskCollectEntity) diskVector.elementAt(i);
									if (vo.getSubentity().equals("Physical Memory") || vo.getSubentity().equals("Virtual Memory") || vo.getSubentity().trim().length() == 0)
										continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_disk_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(vo.getCategory());
										sql.append("','");
										sql.append(vo.getEntity());
										sql.append("','");
										sql.append(vo.getSubentity());
										sql.append("','");
										sql.append(vo.getThevalue());
										sql.append("','");
										sql.append(vo.getChname());
										sql.append("','");
										sql.append(vo.getRestype());
										sql.append("','");
										sql.append(time);
										sql.append("','");
										sql.append(vo.getUnit());
										sql.append("','");
										sql.append(vo.getBak());
										sql.append("')");
										// SysLogger.info(sql.toString());
										dbmanager.addBatch(sql.toString());
										if (host.getTransfer() == 1) {
											if (!flag) {
												xmlOpr = new XmlDataOperator();
												xmlOpr.setFile("host_disk.xml");
												xmlOpr.init4createXml();
											}
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "host_disk.xml");

						}
					}

					// 处理存储信息入库
					if (allstoragehash != null && allstoragehash.size() > 0) {
						Enumeration storagehash = allstoragehash.keys();
						while (storagehash.hasMoreElements()) {
							String ip = (String) storagehash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector storageVector = (Vector) allstoragehash.get(ip);
							String deleteSql = "delete from nms_storage_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							if (storageVector != null && storageVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < storageVector.size(); i++) {
									StorageCollectEntity vo = (StorageCollectEntity) storageVector.elementAt(i);
									try {
										if (vo.getName() == null)
											vo.setName("");
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_storage_data_temp(nodeid,ip,type,subtype,name,stype,cap,storageindex,collecttime)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(vo.getName().replace("\\", "/"));
										sql.append("','");
										sql.append(vo.getType());
										sql.append("','");
										sql.append(vo.getCap());
										sql.append("','");
										sql.append(vo.getStorageindex());
										sql.append("','");
										sql.append(time);
										sql.append("')");
										// SysLogger.info(sql.toString());
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					// 处理硬件信息入库
					if (allhardwarehash != null && allhardwarehash.size() > 0) {
						Enumeration hardwarehash = allhardwarehash.keys();
						while (hardwarehash.hasMoreElements()) {
							String ip = (String) hardwarehash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector hardwareVector = (Vector) allhardwarehash.get(ip);
							String deleteSql = "delete from nms_device_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							if (hardwareVector != null && hardwareVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < hardwareVector.size(); i++) {
									DeviceCollectEntity vo = (DeviceCollectEntity) hardwareVector.elementAt(i);
									String name = vo.getName();
									if (name != null) {
										name = name.replaceAll("\\\\", "/").replaceAll("'", "");
									}
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_device_data_temp(nodeid,ip,type,subtype,name,deviceindex,dtype,status,collecttime)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(name);
										sql.append("','");
										sql.append(vo.getDeviceindex());
										sql.append("','");
										sql.append(vo.getType());
										sql.append("','");
										sql.append(vo.getStatus());
										sql.append("','");
										sql.append(time);
										sql.append("')");
										// SysLogger.info(sql.toString());
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					// 处理软件信息入库
					if (allsoftwarehash != null && allsoftwarehash.size() > 0) {
						String softwaresql = "insert into nms_software_data_temp" + "(nodeid,ip,type,subtype,insdate,name,stype,swid,collecttime) " + "values(?,?,?,?,?,?,?,?,?)";
						dbmanager.setPrepareSql(softwaresql);
						// softwareps = conn.prepareStatement(softwaresql);
						Enumeration softwarehash = allsoftwarehash.keys();
						while (softwarehash.hasMoreElements()) {
							String ip = (String) softwarehash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector softwareVector = (Vector) allsoftwarehash.get(ip);
							String deleteSql = "delete from nms_software_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							if (softwareVector != null && softwareVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < softwareVector.size(); i++) {
									SoftwareCollectEntity vo = (SoftwareCollectEntity) softwareVector.elementAt(i);
									String name = vo.getName();
									if (name != null) {
										name = name.replaceAll("'", "");
									} else {
										name = "";
									}
									name = CommonUtil.removeIllegalStr("GB2312", name);
									try {
										list = new ArrayList();
										list.add(host.getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(vo.getInsdate());
										list.add(name);
										list.add(vo.getType());
										list.add(vo.getSwid());
										list.add(time);
										dbmanager.addPrepareSoftwareBatch(list);
										list = null;
										// softwareps.setString(1,
										// host.getId()+"");
										// softwareps.setString(2, ip);
										// softwareps.setString(3, type);
										// softwareps.setString(4,
										// nodeDTO.getSubtype());
										// softwareps.setString(5,
										// vo.getInsdate());
										// softwareps.setString(6, name);
										// softwareps.setString(7,
										// vo.getType());
										// softwareps.setString(8,
										// vo.getSwid());
										// softwareps.setString(9, time);
										// softwareps.addBatch();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						try {
							dbmanager.executeBatch();
							dbmanager.executePreparedBatch();
						} catch (Exception e) {

						}
					}

					// 处理服务信息入库
					if (allservicehash != null && allservicehash.size() > 0) {
						String servicesql = "insert into nms_sercice_data_temp" + "(nodeid,ip,type,subtype,name,instate,opstate,paused,uninst,collecttime) " + "values(?,?,?,?,?,?,?,?,?,?)";
						dbmanager.setPrepareSql(servicesql);
						// serviceps = conn.prepareStatement(servicesql);
						Enumeration servicehash = allservicehash.keys();
						while (servicehash.hasMoreElements()) {
							String ip = (String) servicehash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector serviceVector = (Vector) allservicehash.get(ip);
							String deleteSql = "delete from nms_sercice_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							if (serviceVector != null && serviceVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < serviceVector.size(); i++) {
									ServiceCollectEntity vo = (ServiceCollectEntity) serviceVector.elementAt(i);
									try {
										list = new ArrayList();
										list.add(host.getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(new String(vo.getName().getBytes(), "UTF-8"));
										list.add(vo.getInstate());
										list.add(vo.getOpstate());
										list.add(vo.getPaused());
										list.add(vo.getUninst());
										list.add(time);
										dbmanager.addPrepareServiceBatch(list);
										list = null;

										// serviceps.setString(1,
										// host.getId()+"");
										// serviceps.setString(2, ip);
										// serviceps.setString(3, type);
										// serviceps.setString(4,
										// nodeDTO.getSubtype());
										// serviceps.setString(5, new
										// String(vo.getName().getBytes(),"UTF-8"));
										// serviceps.setString(6,
										// vo.getInstate());
										// serviceps.setString(7,
										// vo.getOpstate());
										// serviceps.setString(8,
										// vo.getPaused());
										// serviceps.setString(9,
										// vo.getUninst());
										// serviceps.setString(10, time);
										// serviceps.addBatch();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						try {
							dbmanager.executeBatch();
							dbmanager.executePreparedBatch();
						} catch (Exception e) {

						}
					}

					String sql = "insert into nms_interface_data_temp" + "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
					// ps = conn.prepareStatement(sql);
					dbmanager.setPrepareSql(sql);
					Hashtable hasdeletedHash = new Hashtable();
					// 处理入口数据包信息入库
					if (allinpackshash != null && allinpackshash.size() > 0) {
						Enumeration inpackshash = allinpackshash.keys();
						while (inpackshash.hasMoreElements()) {
							String ip = (String) inpackshash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector inpacksVector = (Vector) allinpackshash.get(ip);
							String deleteSql = "delete from nms_interface_data_temp where nodeid='" + host.getId() + "'";
							hasdeletedHash.put(host.getId() + "", host.getId() + "");
							dbmanager.addBatch(deleteSql);
							if (inpacksVector != null && inpacksVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < inpacksVector.size(); i++) {
									InPkts vo = (InPkts) inpacksVector.elementAt(i);
									try {
										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(vo.getCategory());
										list.add(vo.getEntity());
										list.add(vo.getSubentity());
										list.add(vo.getThevalue());
										list.add(vo.getChname());
										list.add(vo.getRestype());
										list.add(time);
										list.add(vo.getUnit());
										list.add(vo.getBak());
										dbmanager.addPrepareBatch(list);
										list = null;

										// ps.setString(1, host.getId()+"");
										// ps.setString(2, ip);
										// ps.setString(3, type);
										// ps.setString(4,
										// nodeDTO.getSubtype());
										// ps.setString(5, vo.getCategory());
										// ps.setString(6, vo.getEntity());
										// ps.setString(7, vo.getSubentity());
										// ps.setString(8, vo.getThevalue());
										// ps.setString(9, vo.getChname());
										// ps.setString(10, vo.getRestype());
										// ps.setString(11, time);
										// ps.setString(12, vo.getUnit());
										// ps.setString(13, vo.getBak());
										// ps.addBatch();
										// stmt.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					// 处理出口数据包信息入库
					if (alloutpackshash != null && alloutpackshash.size() > 0) {
						Enumeration outpackshash = alloutpackshash.keys();
						while (outpackshash.hasMoreElements()) {
							String ip = (String) outpackshash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector outpacksVector = (Vector) alloutpackshash.get(ip);
							String deleteSql = "delete from nms_interface_data_temp where nodeid='" + host.getId() + "'";
							// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
							if (!hasdeletedHash.containsKey(host.getId() + "")) {
								hasdeletedHash.put(host.getId() + "", host.getId() + "");
								dbmanager.addBatch(deleteSql);
							}
							if (outpacksVector != null && outpacksVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < outpacksVector.size(); i++) {
									OutPkts vo = (OutPkts) outpacksVector.elementAt(i);
									try {
										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(vo.getCategory());
										list.add(vo.getEntity());
										list.add(vo.getSubentity());
										list.add(vo.getThevalue());
										list.add(vo.getChname());
										list.add(vo.getRestype());
										list.add(time);
										list.add(vo.getUnit());
										list.add(vo.getBak());
										dbmanager.addPrepareBatch(list);
										list = null;

										// ps.setString(1, host.getId()+"");
										// ps.setString(2, ip);
										// ps.setString(3, type);
										// ps.setString(4,
										// nodeDTO.getSubtype());
										// ps.setString(5, vo.getCategory());
										// ps.setString(6, vo.getEntity());
										// ps.setString(7, vo.getSubentity());
										// ps.setString(8, vo.getThevalue());
										// ps.setString(9, vo.getChname());
										// ps.setString(10, vo.getRestype());
										// ps.setString(11, time);
										// ps.setString(12, vo.getUnit());
										// ps.setString(13, vo.getBak());
										// ps.addBatch();
										// stmt.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					// 处理IPMAC信息入库
					if (allipmachash != null && allipmachash.size() > 0) {
						XmlDataOperator xmlOpr = null;
						boolean flag = false;
						Enumeration ipmachash = allipmachash.keys();
						while (ipmachash.hasMoreElements()) {
							String ip = (String) ipmachash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector ipmacVector = (Vector) allipmachash.get(ip);
							String deleteSql = "delete from ipmac where relateipaddr='" + ip + "'";
							dbmanager.addBatch(deleteSql);
							if (ipmacVector != null && ipmacVector.size() > 0) {
								for (int i = 0; i < ipmacVector.size(); i++) {
									IpMac ipmac = (IpMac) ipmacVector.elementAt(i);
									String mac = ipmac.getMac();
									if (mac == null) {
										mac = "";
									}
									mac = CommonUtil.removeIllegalStr(mac);
									try {
										String _sql = "";
										String time = sdf.format(ipmac.getCollecttime().getTime());
										_sql = "insert into ipmac(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms)values('";
										_sql = _sql + ipmac.getRelateipaddr() + "','" + ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
										_sql = _sql + new String(mac.getBytes(), "UTF-8") + "','" + time + "','" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "')";
										dbmanager.addBatch(_sql.toString());
										if (host.getTransfer() == 1) {
											if (!flag) {
												xmlOpr = new XmlDataOperator();
												xmlOpr.setFile("host_arp.xml");
												xmlOpr.init4createXml();
											}

											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
											flag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "host_arp.xml");

						}
						xmlOpr = null;
					}

					// 处理接口信息入库
					if (allinterfacehash != null && allinterfacehash.size() > 0) {
						Enumeration interfacehash = allinterfacehash.keys();
						while (interfacehash.hasMoreElements()) {
							String ip = (String) interfacehash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Hashtable _interfacehash = (Hashtable) allinterfacehash.get(ip);
							Hashtable deletedHash = new Hashtable();

							Vector utilhdxVector = (Vector) _interfacehash.get("utilhdx");
							String deleteSql = "delete from nms_interface_data_temp where nodeid='" + host.getId() + "'";
							// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
							if (!deletedHash.containsKey(host.getId() + "")) {
								deletedHash.put(host.getId() + "", host.getId() + "");
								dbmanager.addBatch(deleteSql);
							}
							// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
							if (!hasdeletedHash.containsKey(host.getId() + "")) {
								hasdeletedHash.put(host.getId() + "", host.getId() + "");
								dbmanager.addBatch(deleteSql);
							}
							if (utilhdxVector != null && utilhdxVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < utilhdxVector.size(); i++) {
									UtilHdx vo = (UtilHdx) utilhdxVector.elementAt(i);
									try {
										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(vo.getCategory());
										list.add(vo.getEntity());
										list.add(vo.getSubentity());
										list.add(vo.getThevalue());
										list.add(vo.getChname());
										list.add(vo.getRestype());
										list.add(time);
										list.add(vo.getUnit());
										list.add(vo.getBak());
										dbmanager.addPrepareBatch(list);
										list = null;

										// ps.setString(1, host.getId()+"");
										// ps.setString(2, ip);
										// ps.setString(3, type);
										// ps.setString(4,
										// nodeDTO.getSubtype());
										// ps.setString(5, vo.getCategory());
										// ps.setString(6, vo.getEntity());
										// ps.setString(7, vo.getSubentity());
										// ps.setString(8, vo.getThevalue());
										// ps.setString(9, vo.getChname());
										// ps.setString(10, vo.getRestype());
										// ps.setString(11, time);
										// ps.setString(12, vo.getUnit());
										// ps.setString(13, vo.getBak());
										// ps.addBatch();
										// stmt.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							Vector utilhdxpercVector = (Vector) _interfacehash.get("utilhdxperc");
							if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
								// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
								if (!deletedHash.containsKey(host.getId() + "")) {
									deletedHash.put(host.getId() + "", host.getId() + "");
									dbmanager.addBatch(deleteSql);
								}
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < utilhdxpercVector.size(); i++) {
									UtilHdxPerc vo = (UtilHdxPerc) utilhdxpercVector.elementAt(i);
									try {
										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(vo.getCategory());
										list.add(vo.getEntity());
										list.add(vo.getSubentity());
										list.add(vo.getThevalue());
										list.add(vo.getChname());
										list.add(vo.getRestype());
										list.add(time);
										list.add(vo.getUnit());
										list.add(vo.getBak());
										dbmanager.addPrepareBatch(list);
										list = null;

										// ps.setString(1, host.getId()+"");
										// ps.setString(2, ip);
										// ps.setString(3, type);
										// ps.setString(4,
										// nodeDTO.getSubtype());
										// ps.setString(5, vo.getCategory());
										// ps.setString(6, vo.getEntity());
										// ps.setString(7, vo.getSubentity());
										// ps.setString(8, vo.getThevalue());
										// ps.setString(9, vo.getChname());
										// ps.setString(10, vo.getRestype());
										// ps.setString(11, time);
										// ps.setString(12, vo.getUnit());
										// ps.setString(13, vo.getBak());
										// ps.addBatch();
										// stmt.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							// 开始设置流速
							Vector interfaceVector = (Vector) _interfacehash.get("interface");
							if (interfaceVector != null && interfaceVector.size() > 0) {
								try {
									for (int i = 0; i < interfaceVector.size(); i++) {
										Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.elementAt(i);
										Calendar tempCal = (Calendar) interfacedata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = sdf.format(cc);
										String thevalue = interfacedata.getThevalue();
										thevalue = CommonUtil.removeIllegalStr(thevalue);

										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(interfacedata.getCategory());
										list.add(interfacedata.getEntity());
										list.add(interfacedata.getSubentity());
										list.add(thevalue);
										list.add(interfacedata.getChname());
										list.add(interfacedata.getRestype());
										list.add(time);
										list.add(interfacedata.getUnit());
										list.add(interfacedata.getBak());
										// SysLogger.info(interfacedata.getCategory()+"===="+interfacedata.getThevalue()+"===="+thevalue);
										dbmanager.addPrepareBatch(list);
										list = null;

										// ps.setString(1, host.getId()+"");
										// ps.setString(2, ip);
										// ps.setString(3, type);
										// ps.setString(4,
										// nodeDTO.getSubtype());
										// ps.setString(5,
										// interfacedata.getCategory());
										// ps.setString(6,
										// interfacedata.getEntity());
										// ps.setString(7,
										// interfacedata.getSubentity());
										// ps.setString(8, thevalue);
										// ps.setString(9,
										// interfacedata.getChname());
										// ps.setString(10,
										// interfacedata.getRestype());
										// ps.setString(11, time);
										// ps.setString(12,
										// interfacedata.getUnit());
										// ps.setString(13,
										// interfacedata.getBak());
										// ps.addBatch();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							// 结束设置流速

							// 开始设置端口综合流速
							Vector allutilhdxVector = (Vector) _interfacehash.get("allutilhdx");
							if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
								try {
									for (int i = 0; i < allutilhdxVector.size(); i++) {
										AllUtilHdx interfacedata = (AllUtilHdx) allutilhdxVector.elementAt(i);
										Calendar tempCal = (Calendar) interfacedata.getCollecttime();
										Date cc = tempCal.getTime();
										String time = sdf.format(cc);

										list = new ArrayList();
										list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
										list.add(ip);
										list.add(type);
										list.add(nodeDTO.getSubtype());
										list.add(interfacedata.getCategory());
										list.add(interfacedata.getEntity());
										list.add(interfacedata.getSubentity());
										list.add(interfacedata.getThevalue());
										list.add(interfacedata.getChname());
										list.add(interfacedata.getRestype());
										list.add(time);
										list.add(interfacedata.getUnit());
										list.add(interfacedata.getBak());
										dbmanager.addPrepareBatch(list);
										list = null;

										// ps.setString(1, host.getId()+"");
										// ps.setString(2, ip);
										// ps.setString(3, type);
										// ps.setString(4,
										// nodeDTO.getSubtype());
										// ps.setString(5,
										// interfacedata.getCategory());
										// ps.setString(6,
										// interfacedata.getEntity());
										// ps.setString(7,
										// interfacedata.getSubentity());
										// ps.setString(8,
										// interfacedata.getThevalue());
										// ps.setString(9,
										// interfacedata.getChname());
										// ps.setString(10,
										// interfacedata.getRestype());
										// ps.setString(11, time);
										// ps.setString(12,
										// interfacedata.getUnit());
										// ps.setString(13,
										// interfacedata.getBak());
										// ps.addBatch();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					// 处理系统组信息入库
					if (allsystemgrouphash != null && allsystemgrouphash.size() > 0) {
						Enumeration systemgrouphash = allsystemgrouphash.keys();
						while (systemgrouphash.hasMoreElements()) {
							String ip = (String) systemgrouphash.nextElement();
							// SysLogger.info(ip+"==========system");
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector systemgroupVector = (Vector) allsystemgrouphash.get(ip);
							String deleteSql = "delete from nms_system_data_temp where nodeid='" + host.getId() + "'";
							// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
							dbmanager.addBatch(deleteSql);
							if (systemgroupVector != null && systemgroupVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < systemgroupVector.size(); i++) {
									SystemCollectEntity vo = (SystemCollectEntity) systemgroupVector.elementAt(i);
									try {
										StringBuffer _sql = new StringBuffer(500);
										_sql.append("insert into nms_system_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
										_sql.append(host.getId());
										_sql.append("','");
										_sql.append(ip);
										_sql.append("','");
										_sql.append(type);
										_sql.append("','");
										_sql.append(nodeDTO.getSubtype());
										_sql.append("','");
										_sql.append(vo.getCategory());
										_sql.append("','");
										_sql.append(vo.getEntity());
										_sql.append("','");
										_sql.append(vo.getSubentity());
										_sql.append("','");
										_sql.append(vo.getThevalue());
										_sql.append("','");
										_sql.append(vo.getChname());
										_sql.append("','");
										_sql.append(vo.getRestype());
										_sql.append("','");
										_sql.append(time);
										_sql.append("','");
										_sql.append(vo.getUnit());
										_sql.append("','");
										_sql.append(vo.getBak());
										_sql.append("')");
										// SysLogger.info(_sql.toString());
										dbmanager.addBatch(_sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbmanager != null) {
					try {
						dbmanager.close();
					} catch (Exception e) {

					}
				}
			}

		}
		return true;
	}

	/**
	 * 存UPS临时数据
	 * 
	 * @author HONGLI
	 * @param datahash
	 *            ups数据集合
	 * @return
	 */
	public boolean processUPSData(Hashtable datahash) {
		boolean flag = false;
		if (datahash != null && !datahash.isEmpty()) {
			Iterator iterator = datahash.keySet().iterator();
			Hashtable allinputHash = new Hashtable();
			Hashtable alloutputHash = new Hashtable();
			Hashtable allbatteryHash = new Hashtable();
			Hashtable allstatueHash = new Hashtable();
			Hashtable allsystemgroupHash = new Hashtable();
			Hashtable allbypassHash = new Hashtable();
			DBManager dbmanager = new DBManager();
			try {
				// 分类型添加到集合
				while (iterator.hasNext()) {
					String ip = (String) iterator.next();
					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata.containsKey("input")) {
						Hashtable inputhash = (Hashtable) ipdata.get("input");
						allinputHash.put(ip, inputhash.get("input"));
					}
					if (ipdata.containsKey("output")) {
						Hashtable outputhash = (Hashtable) ipdata.get("output");
						alloutputHash.put(ip, outputhash.get("output"));
					}
					if (ipdata.containsKey("battery")) {
						Hashtable batteryhash = (Hashtable) ipdata.get("battery");
						allbatteryHash.put(ip, batteryhash.get("battery"));
					}
					if (ipdata.containsKey("statue")) {
						Hashtable statuehash = (Hashtable) ipdata.get("statue");
						allstatueHash.put(ip, statuehash.get("statue"));
					}
					if (ipdata.containsKey("systemgroup")) {
						Hashtable systemgrouphash = (Hashtable) ipdata.get("systemgroup");
						allsystemgroupHash.put(ip, systemgrouphash.get("system"));
					}
					if (ipdata.containsKey("bypass")) {
						Hashtable bypasshash = (Hashtable) ipdata.get("bypass");
						allbypassHash.put(ip, bypasshash.get("bypass"));
					}
				}
				// 统一入库
				if (allinputHash != null && !allinputHash.isEmpty()) {
					Iterator inputIterator = allinputHash.keySet().iterator();
					while (inputIterator.hasNext()) {
						String ip = (String) inputIterator.next();
						Vector inputVector = (Vector) allinputHash.get(ip);
						// 删除原来的数据
						String deleteSql = "delete from nms_ups_input_data_temp where ipaddress='" + ip + "'";
						dbmanager.addBatch(deleteSql);
						if (inputVector != null) {
							for (int i = 0; i < inputVector.size(); i++) {
								SystemCollectEntity inputdata = (SystemCollectEntity) inputVector.elementAt(i);
								if (inputdata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) inputdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									String tablename = "nms_ups_input_data_temp";
									StringBuffer sql = new StringBuffer();
									sql.append("insert into ");
									sql.append(tablename);
									sql.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
									sql.append("values('");
									sql.append(ip);
									sql.append("','");
									sql.append(inputdata.getRestype());
									sql.append("','");
									sql.append(inputdata.getCategory());
									sql.append("','");
									sql.append(inputdata.getEntity());
									sql.append("','");
									sql.append(inputdata.getSubentity());
									sql.append("','");
									sql.append(inputdata.getUnit());
									sql.append("','");
									sql.append(inputdata.getChname());
									sql.append("','");
									sql.append(inputdata.getBak());
									sql.append("',");
									sql.append(inputdata.getCount());
									sql.append(",'");
									sql.append(inputdata.getThevalue());
									sql.append("','");
									sql.append(time);
									sql.append("')");
									// if(i == 0){
									// System.out.println(sql.toString());
									// }
									try {
										dbmanager.addBatch(sql.toString());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									sql = null;
								}
							}
						}
					}
				}
				if (alloutputHash != null && !alloutputHash.isEmpty()) {
					Iterator outputIterator = alloutputHash.keySet().iterator();
					while (outputIterator.hasNext()) {
						String ip = (String) outputIterator.next();
						Vector outputVector = (Vector) alloutputHash.get(ip);
						// 删除原来的数据
						String deleteSql = "delete from nms_ups_output_data_temp where ipaddress='" + ip + "'";
						dbmanager.addBatch(deleteSql);
						if (outputVector != null) {
							for (int i = 0; i < outputVector.size(); i++) {
								SystemCollectEntity outputdata = (SystemCollectEntity) outputVector.elementAt(i);
								if (outputdata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) outputdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									String tablename = "nms_ups_output_data_temp";
									StringBuffer sql = new StringBuffer();
									sql.append("insert into ");
									sql.append(tablename);
									sql.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
									sql.append("values('");
									sql.append(ip);
									sql.append("','");
									sql.append(outputdata.getRestype());
									sql.append("','");
									sql.append(outputdata.getCategory());
									sql.append("','");
									sql.append(outputdata.getEntity());
									sql.append("','");
									sql.append(outputdata.getSubentity());
									sql.append("','");
									sql.append(outputdata.getUnit());
									sql.append("','");
									sql.append(outputdata.getChname());
									sql.append("','");
									sql.append(outputdata.getBak());
									sql.append("',");
									sql.append(outputdata.getCount());
									sql.append(",'");
									sql.append(outputdata.getThevalue());
									sql.append("','");
									sql.append(time);
									sql.append("')");
									// if(i == 0){
									// System.out.println(sql.toString());
									// }
									try {
										dbmanager.addBatch(sql.toString());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}
					}
				}
				// 电池信息
				if (allbatteryHash != null && !allbatteryHash.isEmpty()) {
					Iterator batteryIterator = allbatteryHash.keySet().iterator();
					while (batteryIterator.hasNext()) {
						String ip = (String) batteryIterator.next();
						Vector batteryVector = (Vector) allbatteryHash.get(ip);
						// 删除原来的数据
						String deleteSql = "delete from nms_ups_battery_data_temp where ipaddress='" + ip + "'";
						dbmanager.addBatch(deleteSql);
						if (batteryVector != null) {
							for (int i = 0; i < batteryVector.size(); i++) {
								SystemCollectEntity batterydata = (SystemCollectEntity) batteryVector.elementAt(i);
								Calendar tempCal = (Calendar) batterydata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								String tablename = "nms_ups_battery_data_temp";
								StringBuffer sql = new StringBuffer();
								sql.append("insert into ");
								sql.append(tablename);
								sql.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
								sql.append("values('");
								sql.append(ip);
								sql.append("','");
								sql.append(batterydata.getRestype());
								sql.append("','");
								sql.append(batterydata.getCategory());
								sql.append("','");
								sql.append(batterydata.getEntity());
								sql.append("','");
								sql.append(batterydata.getSubentity());
								sql.append("','");
								sql.append(batterydata.getUnit());
								sql.append("','");
								sql.append(batterydata.getChname());
								sql.append("','");
								sql.append(batterydata.getBak());
								sql.append("',");
								sql.append(batterydata.getCount());
								sql.append(",'");
								sql.append(batterydata.getThevalue());
								sql.append("','");
								sql.append(time);
								sql.append("')");
								// if(i == 0){
								// System.out.println(sql.toString());
								// }
								try {
									dbmanager.addBatch(sql.toString());
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								sql = null;
							}
						}
					}
				}
				// 状态信息
				if (allstatueHash != null && !allstatueHash.isEmpty()) {
					Iterator statueIterator = allstatueHash.keySet().iterator();
					while (statueIterator.hasNext()) {
						String ip = (String) statueIterator.next();
						Vector statueVector = (Vector) allstatueHash.get(ip);
						// 删除原来的数据
						String deleteSql = "delete from nms_ups_statue_data_temp where ipaddress='" + ip + "'";
						dbmanager.addBatch(deleteSql);
						if (statueVector != null) {
							for (int i = 0; i < statueVector.size(); i++) {
								SystemCollectEntity statuedata = (SystemCollectEntity) statueVector.elementAt(i);
								if (statuedata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) statuedata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									String tablename = "nms_ups_statue_data_temp";
									StringBuffer sql = new StringBuffer();
									sql.append("insert into ");
									sql.append(tablename);
									sql.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
									sql.append("values('");
									sql.append(ip);
									sql.append("','");
									sql.append(statuedata.getRestype());
									sql.append("','");
									sql.append(statuedata.getCategory());
									sql.append("','");
									sql.append(statuedata.getEntity());
									sql.append("','");
									sql.append(statuedata.getSubentity());
									sql.append("','");
									sql.append(statuedata.getUnit());
									sql.append("','");
									sql.append(statuedata.getChname());
									sql.append("','");
									sql.append(statuedata.getBak());
									sql.append("',");
									sql.append(statuedata.getCount());
									sql.append(",'");
									sql.append(statuedata.getThevalue());
									sql.append("','");
									sql.append(time);
									sql.append("')");
									// if(i == 0){
									// System.out.println(sql.toString());
									// }
									try {
										dbmanager.addBatch(sql.toString());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									sql = null;
								}
							}
						}
					}
				}
				// 系统组
				if (allsystemgroupHash != null && !allsystemgroupHash.isEmpty()) {
					Iterator systemgroupIterator = allsystemgroupHash.keySet().iterator();
					while (systemgroupIterator.hasNext()) {
						String ip = (String) systemgroupIterator.next();
						Vector systemgroupVector = (Vector) allsystemgroupHash.get(ip);
						// 删除原来的数据
						String deleteSql = "delete from nms_ups_systemgroup_data_temp where ipaddress='" + ip + "'";
						dbmanager.addBatch(deleteSql);
						if (systemgroupVector != null) {
							for (int i = 0; i < systemgroupVector.size(); i++) {
								SystemCollectEntity systemgroupdata = (SystemCollectEntity) systemgroupVector.elementAt(i);
								Calendar tempCal = (Calendar) systemgroupdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								String tablename = "nms_ups_systemgroup_data_temp";
								StringBuffer sql = new StringBuffer();
								sql.append("insert into ");
								sql.append(tablename);
								sql.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
								sql.append("values('");
								sql.append(ip);
								sql.append("','");
								sql.append(systemgroupdata.getRestype());
								sql.append("','");
								sql.append(systemgroupdata.getCategory());
								sql.append("','");
								sql.append(systemgroupdata.getEntity());
								sql.append("','");
								sql.append(systemgroupdata.getSubentity());
								sql.append("','");
								sql.append(systemgroupdata.getUnit());
								sql.append("','");
								sql.append(systemgroupdata.getChname());
								sql.append("','");
								sql.append(systemgroupdata.getBak());
								sql.append("',");
								sql.append(systemgroupdata.getCount());
								sql.append(",'");
								sql.append(systemgroupdata.getThevalue());
								sql.append("','");
								sql.append(time);
								sql.append("')");
								// if(i == 0){
								// System.out.println(sql.toString());
								// }
								try {
									dbmanager.addBatch(sql.toString());
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								sql = null;
							}
						}
					}
				}
				// 旁路信息
				if (allbypassHash != null && !allbypassHash.isEmpty()) {
					Iterator bypassIterator = allbypassHash.keySet().iterator();
					while (bypassIterator.hasNext()) {
						String ip = (String) bypassIterator.next();
						Vector bypassVector = (Vector) allbypassHash.get(ip);
						// 删除原来的数据
						String deleteSql = "delete from nms_ups_bypass_data_temp where ipaddress='" + ip + "'";
						dbmanager.addBatch(deleteSql);
						if (bypassVector != null) {
							for (int i = 0; i < bypassVector.size(); i++) {
								SystemCollectEntity bypassdata = (SystemCollectEntity) bypassVector.elementAt(i);
								if (bypassdata.getRestype().equals("dynamic")) {
									Calendar tempCal = (Calendar) bypassdata.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									String tablename = "nms_ups_bypass_data_temp";
									StringBuffer sql = new StringBuffer();
									sql.append("insert into ");
									sql.append(tablename);
									sql.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
									sql.append("values('");
									sql.append(ip);
									sql.append("','");
									sql.append(bypassdata.getRestype());
									sql.append("','");
									sql.append(bypassdata.getCategory());
									sql.append("','");
									sql.append(bypassdata.getEntity());
									sql.append("','");
									sql.append(bypassdata.getSubentity());
									sql.append("','");
									sql.append(bypassdata.getUnit());
									sql.append("','");
									sql.append(bypassdata.getChname());
									sql.append("','");
									sql.append(bypassdata.getBak());
									sql.append("',");
									sql.append(bypassdata.getCount());
									sql.append(",'");
									sql.append(bypassdata.getThevalue());
									sql.append("','");
									sql.append(time);
									sql.append("')");
									// if(i == 0){
									// System.out.println(sql.toString());
									// }
									try {
										dbmanager.addBatch(sql.toString());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									sql = null;
								}
							}
						}
					}
				}
				dbmanager.executeBatch();
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
		}
		return flag;
	}

	public boolean processNetData(String type, Hashtable datahash) {
		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			Hashtable allpinghash = new Hashtable();
			Hashtable allcpuhash = new Hashtable();
			Hashtable allpmemoryhash = new Hashtable();
			Hashtable allrouterhash = new Hashtable();
			Hashtable allvmemoryhash = new Hashtable();
			Hashtable allpowerhash = new Hashtable();
			Hashtable allfanhash = new Hashtable();
			Hashtable alltemperaturehash = new Hashtable();
			Hashtable allvoltagehash = new Hashtable();

			Hashtable allhardwarehash = new Hashtable();
			Hashtable allsoftwarehash = new Hashtable();
			Hashtable allservicehash = new Hashtable();
			Hashtable allinpackshash = new Hashtable();
			Hashtable allfdbhash = new Hashtable();
			Hashtable allflashhash = new Hashtable();
			Hashtable allinterfacehash = new Hashtable();
			Hashtable allsystemgrouphash = new Hashtable();
			NodeUtil nodeUtil = new NodeUtil();
			DBManager dbmanager = new DBManager();
			List list = new ArrayList();
			boolean uploadsuccess = false;
			FtpTransConfigDao ftpconfdao = new FtpTransConfigDao();
			FtpTransConfig ftpConfig = null;
			FtpUtil ftputil = null;
			try {
				ftpConfig = ftpconfdao.getFtpTransMonitorConfig();
				if (ftpConfig != null) {
					ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());
					;
				}
			} catch (Exception e) {

			} finally {
				ftpconfdao.close();
			}
			try {
				while (iphash.hasMoreElements()) {
					String ip = (String) iphash.nextElement();
					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata != null) {
						// 处理网络设备的数据

						// ping信息
						if (ipdata.containsKey("ping")) {
							Hashtable pinghash = (Hashtable) ipdata.get("ping");
							Vector pingVector = (Vector) pinghash.get("ping");
							if (pingVector != null && pingVector.size() > 0) {
								allpinghash.put(ip, pingVector);
							}
						}

						// CPU信息
						if (ipdata.containsKey("cpu")) {
							Hashtable cpuhash = (Hashtable) ipdata.get("cpu");
							Vector cpuVector = (Vector) cpuhash.get("cpu");
							if (cpuVector != null && cpuVector.size() > 0) {
								// SysLogger.info(" ip ===:"+ip+"
								// cpusize:"+cpuVector.size());
								allcpuhash.put(ip, cpuVector);
							}
						}
						// 内存信息
						if (ipdata.containsKey("memory")) {
							Hashtable physicalmemoryhash = (Hashtable) ipdata.get("memory");
							Vector memoryVector = (Vector) physicalmemoryhash.get("memory");
							if (memoryVector != null && memoryVector.size() > 0) {
								allpmemoryhash.put(ip, memoryVector);
							}
						}
						// 路由信息
						if (ipdata.containsKey("iprouter")) {
							Hashtable iprouterhash = (Hashtable) ipdata.get("iprouter");
							Vector routerVector = (Vector) iprouterhash.get("iprouter");
							if (routerVector != null && routerVector.size() > 0) {
								allrouterhash.put(ip, routerVector);
							}
						}

						// 闪存信息
						if (ipdata.containsKey("flash")) {
							Hashtable flashhash = (Hashtable) ipdata.get("flash");
							Vector flashVector = (Vector) flashhash.get("flash");
							if (flashVector != null && flashVector.size() > 0) {
								allflashhash.put(ip, flashVector);
							}
						}

						// 电源信息
						if (ipdata.containsKey("power")) {
							Hashtable powerhash = (Hashtable) ipdata.get("power");
							Vector powerVector = (Vector) powerhash.get("power");
							if (powerVector != null && powerVector.size() > 0) {
								allpowerhash.put(ip, powerVector);
							}
						}
						// 风扇信息
						if (ipdata.containsKey("fan")) {
							Hashtable fanhash = (Hashtable) ipdata.get("fan");
							Vector fanVector = (Vector) fanhash.get("fan");
							if (fanVector != null && fanVector.size() > 0) {
								allfanhash.put(ip, fanVector);
							}
						}

						// 温度信息
						if (ipdata.containsKey("temperature")) {
							Hashtable temperaturehash = (Hashtable) ipdata.get("temperature");
							Vector temperatureVector = (Vector) temperaturehash.get("temperature");
							if (temperatureVector != null && temperatureVector.size() > 0) {
								alltemperaturehash.put(ip, temperatureVector);
							}
						}

						// 电压信息
						if (ipdata.containsKey("voltage")) {
							Hashtable voltagehash = (Hashtable) ipdata.get("voltage");
							Vector voltageVector = (Vector) voltagehash.get("voltage");
							if (voltageVector != null && voltageVector.size() > 0) {
								allvoltagehash.put(ip, voltageVector);
							}
						}

						// 系统属性信息
						if (ipdata.containsKey("systemgroup")) {
							Hashtable systemhash = (Hashtable) ipdata.get("systemgroup");
							Vector systemVector = (Vector) systemhash.get("system");
							if (systemVector != null && systemVector.size() > 0) {
								allsystemgrouphash.put(ip, systemVector);
							}
						}
						// fdb 信息
						if (ipdata.containsKey("fdb")) {
							Hashtable fdbhash = (Hashtable) ipdata.get("fdb");
							Vector fdbVector = (Vector) fdbhash.get("fdb");
							if (fdbVector != null && fdbVector.size() > 0) {
								allfdbhash.put(ip, fdbVector);
							}
						}
					}
				}

				// 处理PING入库
				if (allpinghash != null && allpinghash.size() > 0) {
					XmlDataOperator xmlOpr = null;

					Enumeration pinghash = allpinghash.keys();
					String pingsql = "insert into nms_ping_data_temp" + "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
					dbmanager.setPrepareSql(pingsql);
					// pingps = conn.prepareStatement(pingsql);
					NodeDTO nodeDTO = null;
					String ip = null;
					PingCollectEntity vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					boolean flag = false;
					while (pinghash.hasMoreElements()) {
						ip = (String) pinghash.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						Vector pingVector = (Vector) allpinghash.get(ip);
						if (pingVector != null && pingVector.size() > 0) {
							String deleteSql = "delete from nms_ping_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
							dbmanager.addBatch(deleteSql);
							for (int i = 0; i < pingVector.size(); i++) {
								vo = (PingCollectEntity) pingVector.elementAt(i);
								try {
									tempCal = (Calendar) vo.getCollecttime();
									cc = tempCal.getTime();
									time = sdf.format(cc);
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(type);
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									if (host.getTransfer() == 1) {
										if (!flag) {
											xmlOpr = new XmlDataOperator();
											xmlOpr.setFile(type + "_ping.xml");
											xmlOpr.init4createXml();
										}
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										flag = true;
									}
									list = null;

									//														    
								} catch (Exception e) {
									e.printStackTrace();
								}
								tempCal = null;
								cc = null;
								time = null;
								vo = null;
							}
						}
						pingVector = null;
						nodeDTO = null;
						ip = null;
					}
					if (flag) {
						xmlOpr.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "network_ping.xml");

					}
					xmlOpr = null;
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {

					}
				}
				allpinghash = null;

				// 处理CPU入库
				if (allcpuhash != null && allcpuhash.size() > 0) {
					Enumeration cpuhash = allcpuhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					CpuCollectEntity vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					StringBuffer sql = null;
					Vector cpuVector = null;
					XmlDataOperator xmlOpr = null;

					boolean flag = false;
					while (cpuhash.hasMoreElements()) {
						ip = (String) cpuhash.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						cpuVector = (Vector) allcpuhash.get(ip);

						String deleteSql = "delete from nms_cpu_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						dbmanager.addBatch(deleteSql);
						// 得到CPU平均
						vo = (CpuCollectEntity) cpuVector.elementAt(0);
						try {
							tempCal = (Calendar) vo.getCollecttime();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							sql = new StringBuffer(500);
							sql.append("insert into nms_cpu_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
							sql.append(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId());
							sql.append("','");
							sql.append(ip);
							sql.append("','");
							sql.append(type);
							sql.append("','");
							sql.append(nodeDTO.getSubtype());
							sql.append("','");
							sql.append(vo.getCategory());
							sql.append("','");
							sql.append(vo.getEntity());
							sql.append("','");
							sql.append(vo.getSubentity());
							sql.append("','");
							sql.append(vo.getThevalue());
							sql.append("','");
							sql.append(vo.getChname());
							sql.append("','");
							sql.append(vo.getRestype());
							sql.append("','");
							sql.append(time);
							sql.append("','");
							sql.append(vo.getUnit());
							sql.append("','");
							sql.append(vo.getBak());
							sql.append("')");
							dbmanager.addBatch(sql.toString());
							if (host.getTransfer() == 1) {
								if (!flag) {
									xmlOpr = new XmlDataOperator();
									xmlOpr.setFile(type + "_cpu.xml");
									xmlOpr.init4createXml();
								}
								xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
								flag = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						cpuVector = null;
						vo = null;
						sql = null;

					}
					if (flag) {
						xmlOpr.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "network_cpu.xml");

					}
					xmlOpr = null;
				}
				allcpuhash = null;

				// 处理物理内存入库
				if (allpmemoryhash != null && allpmemoryhash.size() > 0) {
					Enumeration pmhash = allpmemoryhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					MemoryCollectEntity vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					StringBuffer sql = null;
					Vector memoryVector = null;
					XmlDataOperator xmlOpr = null;

					boolean flag = false;
					while (pmhash.hasMoreElements()) {
						ip = (String) pmhash.nextElement();
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						memoryVector = (Vector) allpmemoryhash.get(ip);
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

						String deleteSql = "delete from nms_memory_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (memoryVector != null && memoryVector.size() > 0) {
							for (int i = 0; i < memoryVector.size(); i++) {
								vo = (MemoryCollectEntity) memoryVector.elementAt(i);
								tempCal = (Calendar) vo.getCollecttime();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								try {
									sql = new StringBuffer(500);
									sql.append("insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(type);
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getCategory());
									sql.append("','");
									sql.append(vo.getEntity());
									sql.append("','");
									sql.append(vo.getSubentity());
									sql.append("','");
									sql.append(vo.getThevalue());
									sql.append("','");
									sql.append(vo.getChname());
									sql.append("','");
									sql.append(vo.getRestype());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getUnit());
									sql.append("','");
									sql.append(vo.getBak());
									sql.append("')");
									dbmanager.addBatch(sql.toString());
									if (host.getTransfer() == 1) {
										if (!flag) {
											xmlOpr = new XmlDataOperator();
											xmlOpr.setFile(type + "_mem.xml");
											xmlOpr.init4createXml();
										}
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										flag = true;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								vo = null;
								sql = null;

							}

						}
						memoryVector = null;
					}
					if (flag) {
						xmlOpr.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "network_mem.xml");
					}
					xmlOpr = null;
				}
				allpmemoryhash = null;

				try {
					dbmanager.executeBatch();
				} catch (Exception e) {
					SysLogger.error("---批处理实时内存出错---", e);
				}

				// 处理路由信息
				if (allrouterhash != null && allrouterhash.size() > 0) {
					Enumeration routerhash = allrouterhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					IpRouter iprouter = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					Vector routerVector = null;
					XmlDataOperator xmlOpr = null;

					boolean flag = false;
					while (routerhash.hasMoreElements()) {
						ip = (String) routerhash.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						if (host == null)
							continue;
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						routerVector = (Vector) allrouterhash.get(ip);
						if (routerVector != null && routerVector.size() > 0) {
							// 删除原来的数据
							String deleteSql = "delete from nms_route_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);

							try {

								StringBuffer sql = null;
								for (int i = 0; i < routerVector.size(); i++) {
									iprouter = (IpRouter) routerVector.elementAt(i);
									tempCal = (Calendar) iprouter.getCollecttime();
									cc = tempCal.getTime();
									time = sdf.format(cc);
									try {
										sql = new StringBuffer(500);
										sql.append("insert into nms_route_data_temp(nodeid,ip,type,subtype,ifindex,nexthop,proto,rtype,mask,collecttime,physaddress,dest)values('");
										sql.append(host.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(type);
										sql.append("','");
										sql.append(nodeDTO.getSubtype());
										sql.append("','");
										sql.append(iprouter.getIfindex());
										sql.append("','");
										sql.append(iprouter.getNexthop());
										sql.append("','");
										sql.append(iprouterproto[Integer.parseInt(iprouter.getProto().longValue() + "")]);
										sql.append("','");
										sql.append(iproutertype[Integer.parseInt(iprouter.getType().longValue() + "")]);
										sql.append("','");
										sql.append(iprouter.getMask());
										sql.append("','");
										sql.append(time);
										sql.append("','");
										sql.append(iprouter.getPhysaddress());
										sql.append("','");
										sql.append(iprouter.getDest());
										sql.append("')");
										dbmanager.addBatch(sql.toString());
										sql = null;
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									// if(host.getTransfer()==1){
									// if (!flag) {
									// xmlOpr = new XmlDataOperator();
									// xmlOpr.setFile("network_iprouter.xml");
									// xmlOpr.init4createXml();
									// }
									// xmlOpr.addIPNode(ip,type,
									// nodeDTO.getSubtype(),iprouter.getIfindex(),
									// iprouter.getNexthop(),
									// iprouter.getProto().longValue()+"",iprouter.getType().longValue()+"",
									// iprouter.getMask(), time,
									// iprouter.getPhysaddress(),
									// iprouter.getDest());
									// flag=true;
									// }
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						routerVector = null;
						iprouter = null;

						try {
							dbmanager.executeBatch();
						} catch (Exception e) {
							SysLogger.error("---批处理实时路由表出错---", e);
						}
					}
					// if(flag){
					// xmlOpr.createXml();
					// uploadsuccess=ftputil.uploadFile(ftpConfig.getIp(),
					// ftpConfig.getUsername(), ftpConfig.getPassword(),
					// ResourceCenter.getInstance().getSysPath()+"/linuxserver/network_cpu.xml");
					//				
					// }
					// xmlOpr=null;
				}
				allrouterhash = null;

				// String sql = "insert into nms_interface_data_temp"
				// +
				// "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)
				// "
				// + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				// ps = conn.prepareStatement(sql);
				//			
				// Hashtable hasdeletedHash = new Hashtable();
				// //处理入口数据包信息入库
				// if(allinpackshash != null && allinpackshash.size()>0){
				// Enumeration inpackshash = allinpackshash.keys();
				// while(inpackshash.hasMoreElements()){
				// String ip = (String)inpackshash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector inpacksVector = (Vector)allinpackshash.get(ip);
				// String deleteSql = "delete from nms_interface_data_temp where
				// nodeid='" +host.getId() + "'";
				// hasdeletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// if(inpacksVector != null && inpacksVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<inpacksVector.size();i++){
				// InPkts vo = (InPkts) inpacksVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }
				//			
				// //处理出口数据包信息入库
				// if(alloutpackshash != null && alloutpackshash.size()>0){
				// Enumeration outpackshash = alloutpackshash.keys();
				// while(outpackshash.hasMoreElements()){
				// String ip = (String)outpackshash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector outpacksVector = (Vector)alloutpackshash.get(ip);
				// String deleteSql = "delete from nms_interface_data_temp where
				// nodeid='" +host.getId() + "'";
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!hasdeletedHash.containsKey(host.getId()+"")){
				// hasdeletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// if(outpacksVector != null && outpacksVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<outpacksVector.size();i++){
				// OutPkts vo = (OutPkts) outpacksVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				String envsql = "insert into nms_envir_data_temp" + "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				dbmanager.setPrepareSql(envsql);
				// ps = conn.prepareStatement(envsql);

				Hashtable envdeletedHash = new Hashtable();
				// ////////////////////////////////

				// ///////////////////////////
				// 处理电源信息入库
				HashMap<String, XmlDataOperator> map = new HashMap<String, XmlDataOperator>();
				XmlDataOperator xmlOpr = new XmlDataOperator();
				xmlOpr.setFile("net_envir.xml");
				xmlOpr.init4createXml();
				boolean enFlag = false;
				if (allpowerhash != null && allpowerhash.size() > 0) {
					Enumeration powerhash = allpowerhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					Interfacecollectdata vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					Vector powerVector = null;

					while (powerhash.hasMoreElements()) {
						ip = (String) powerhash.nextElement();
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						powerVector = (Vector) allpowerhash.get(ip);
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

						String deleteSql = "delete from nms_envir_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						envdeletedHash.put(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "", ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
						dbmanager.addBatch(deleteSql);
						if (powerVector != null && powerVector.size() > 0) {
							tempCal = Calendar.getInstance();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							for (int i = 0; i < powerVector.size(); i++) {
								vo = (Interfacecollectdata) powerVector.elementAt(i);
								try {
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(type);
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									if (host.getTransfer() == 1) {

										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										enFlag = true;
									}
									list = null;
									// ps.setString(1,
									// ((Host)PollingEngine.getInstance().getNodeByIP(ip)).getId()+"");
									// ps.setString(2, ip);
									// ps.setString(3, type);
									// ps.setString(4, nodeDTO.getSubtype());
									// ps.setString(5, vo.getCategory());
									// ps.setString(6, vo.getEntity());
									// ps.setString(7, vo.getSubentity());
									// ps.setString(8, vo.getThevalue());
									// ps.setString(9, vo.getChname());
									// ps.setString(10, vo.getRestype());
									// ps.setString(11, time);
									// ps.setString(12, vo.getUnit());
									// ps.setString(13, vo.getBak());
									// ps.addBatch();
									// stmt.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							vo = null;
						}
						nodeDTO = null;
						powerVector = null;

					}
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {

					}
				}
				allpowerhash = null;

				// 处理风扇信息入库
				if (allfanhash != null && allfanhash.size() > 0) {
					Enumeration fanhash = allfanhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					Interfacecollectdata vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					Vector fanVector = null;

					while (fanhash.hasMoreElements()) {
						ip = (String) fanhash.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						fanVector = (Vector) allfanhash.get(ip);

						String deleteSql = "delete from nms_envir_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
						if (!envdeletedHash.containsKey(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "")) {
							envdeletedHash.put(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "", ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
							dbmanager.addBatch(deleteSql);
						}
						if (fanVector != null && fanVector.size() > 0) {
							tempCal = Calendar.getInstance();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							for (int i = 0; i < fanVector.size(); i++) {
								vo = (Interfacecollectdata) fanVector.elementAt(i);
								try {
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(type);
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									if (host.getTransfer() == 1) {
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										enFlag = true;
									}
									list = null;
									// ps.setString(1,
									// ((Host)PollingEngine.getInstance().getNodeByIP(ip)).getId()+"");
									// ps.setString(2, ip);
									// ps.setString(3, type);
									// ps.setString(4, nodeDTO.getSubtype());
									// ps.setString(5, vo.getCategory());
									// ps.setString(6, vo.getEntity());
									// ps.setString(7, vo.getSubentity());
									// ps.setString(8, vo.getThevalue());
									// ps.setString(9, vo.getChname());
									// ps.setString(10, vo.getRestype());
									// ps.setString(11, time);
									// ps.setString(12, vo.getUnit());
									// ps.setString(13, vo.getBak());
									// ps.addBatch();
									// stmt.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
								vo = null;
							}
						}

						nodeDTO = null;
						fanVector = null;
					}
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {
						SysLogger.error("---批处理实时风扇出错---", e);
					}
				}
				allfanhash = null;

				// 处理电压信息入库
				if (allvoltagehash != null && allvoltagehash.size() > 0) {
					Enumeration voltagehash = allvoltagehash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					Interfacecollectdata vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					Vector fanVector = null;
					while (voltagehash.hasMoreElements()) {
						ip = (String) voltagehash.nextElement();
						nodeDTO = nodeUtil.creatNodeDTOByNode(((Host) PollingEngine.getInstance().getNodeByIP(ip)));
						fanVector = (Vector) allvoltagehash.get(ip);
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

						String deleteSql = "delete from nms_envir_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
						if (!envdeletedHash.containsKey(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "")) {
							envdeletedHash.put(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "", ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
							dbmanager.addBatch(deleteSql);
						}
						if (fanVector != null && fanVector.size() > 0) {
							tempCal = Calendar.getInstance();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							for (int i = 0; i < fanVector.size(); i++) {
								vo = (Interfacecollectdata) fanVector.elementAt(i);
								try {
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(type);
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									if (host.getTransfer() == 1) {
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										enFlag = true;
									}
									list = null;
									// ps.setString(1,
									// ((Host)PollingEngine.getInstance().getNodeByIP(ip)).getId()+"");
									// ps.setString(2, ip);
									// ps.setString(3, type);
									// ps.setString(4, nodeDTO.getSubtype());
									// ps.setString(5, vo.getCategory());
									// ps.setString(6, vo.getEntity());
									// ps.setString(7, vo.getSubentity());
									// ps.setString(8, vo.getThevalue());
									// ps.setString(9, vo.getChname());
									// ps.setString(10, vo.getRestype());
									// ps.setString(11, time);
									// ps.setString(12, vo.getUnit());
									// ps.setString(13, vo.getBak());
									// ps.addBatch();
									// stmt.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
								vo = null;
							}
						}

						fanVector = null;
						nodeDTO = null;
						ip = null;
					}
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {
						SysLogger.error("---批处理实时电压出错---", e);
					}
				}
				allvoltagehash = null;

				// 处理温度信息入库
				if (alltemperaturehash != null && alltemperaturehash.size() > 0) {
					Enumeration temperaturehash = alltemperaturehash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					Interfacecollectdata vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					Vector temperatureVector = null;
					while (temperaturehash.hasMoreElements()) {
						ip = (String) temperaturehash.nextElement();
						// Host host =
						// (Host)PollingEngine.getInstance().getNodeByIP(ip);
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						temperatureVector = (Vector) alltemperaturehash.get(ip);

						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						String deleteSql = "delete from nms_envir_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
						if (!envdeletedHash.containsKey(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "")) {
							envdeletedHash.put(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "", ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
							dbmanager.addBatch(deleteSql);
						}
						if (temperatureVector != null && temperatureVector.size() > 0) {
							tempCal = Calendar.getInstance();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							for (int i = 0; i < temperatureVector.size(); i++) {
								vo = (Interfacecollectdata) temperatureVector.elementAt(i);
								try {
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(type);
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									if (host.getTransfer() == 1) {
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										enFlag = true;
									}
									list = null;
									// ps.setString(1,
									// ((Host)PollingEngine.getInstance().getNodeByIP(ip)).getId()+"");
									// ps.setString(2, ip);
									// ps.setString(3, type);
									// ps.setString(4, nodeDTO.getSubtype());
									// ps.setString(5, vo.getCategory());
									// ps.setString(6, vo.getEntity());
									// ps.setString(7, vo.getSubentity());
									// ps.setString(8, vo.getThevalue());
									// ps.setString(9, vo.getChname());
									// ps.setString(10, vo.getRestype());
									// ps.setString(11, time);
									// ps.setString(12, vo.getUnit());
									// ps.setString(13, vo.getBak());
									// ps.addBatch();
									// stmt.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
								vo = null;
							}
						}
						temperatureVector = null;
						nodeDTO = null;
						ip = null;
					}
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {
						SysLogger.error("---批处理实时温度出错---", e);
					}
				}
				alltemperaturehash = null;

				// 处理闪存信息入库
				if (allflashhash != null && allflashhash.size() > 0) {
					Enumeration flashhash = allflashhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					FlashCollectEntity vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					while (flashhash.hasMoreElements()) {
						ip = (String) flashhash.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector flashVector = (Vector) allflashhash.get(ip);
						String deleteSql = "delete from nms_envir_data_temp where nodeid='" + host.getId() + "'";
						// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
						if (!envdeletedHash.containsKey(host.getId() + "")) {
							envdeletedHash.put(host.getId() + "", host.getId() + "");
							dbmanager.addBatch(deleteSql);
						}
						if (flashVector != null && flashVector.size() > 0) {
							tempCal = Calendar.getInstance();
							cc = tempCal.getTime();
							time = sdf.format(cc);
							for (int i = 0; i < flashVector.size(); i++) {
								vo = (FlashCollectEntity) flashVector.elementAt(i);
								try {
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(type);
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									if (host.getTransfer() == 1) {
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										enFlag = true;
									}
									list = null;
									// ps.setString(1, host.getId()+"");
									// ps.setString(2, ip);
									// ps.setString(3, type);
									// ps.setString(4, nodeDTO.getSubtype());
									// ps.setString(5, vo.getCategory());
									// ps.setString(6, vo.getEntity());
									// ps.setString(7, vo.getSubentity());
									// ps.setString(8, vo.getThevalue());
									// ps.setString(9, vo.getChname());
									// ps.setString(10, vo.getRestype());
									// ps.setString(11, time);
									// ps.setString(12, vo.getUnit());
									// ps.setString(13, vo.getBak());
									// ps.addBatch();
									// stmt.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {
						SysLogger.error("---批处理实时闪存出错---", e);
					}
				}
				allflashhash = null;
				if (enFlag) {
					xmlOpr.createXml();
					uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "network_envir.xml");

				}
				xmlOpr = null;

				// //处理接口信息入库
				// if(allinterfacehash != null && allinterfacehash.size()>0){
				// Enumeration interfacehash = allinterfacehash.keys();
				// while(interfacehash.hasMoreElements()){
				// String ip = (String)interfacehash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Hashtable _interfacehash =
				// (Hashtable)allinterfacehash.get(ip);
				// Hashtable deletedHash = new Hashtable();
				// Vector utilhdxVector = (Vector)
				// _interfacehash.get("utilhdx");
				// String deleteSql = "delete from nms_interface_data_temp where
				// nodeid='" +host.getId() + "'";
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!deletedHash.containsKey(host.getId()+"")){
				// deletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!hasdeletedHash.containsKey(host.getId()+"")){
				// hasdeletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// if(utilhdxVector != null && utilhdxVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<utilhdxVector.size();i++){
				// UtilHdx vo = (UtilHdx) utilhdxVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				//					
				//					
				// Vector utilhdxpercVector = (Vector)
				// _interfacehash.get("utilhdxperc");
				// if(utilhdxpercVector != null && utilhdxpercVector.size()>0){
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!deletedHash.containsKey(host.getId()+"")){
				// deletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<utilhdxpercVector.size();i++){
				// UtilHdxPerc vo = (UtilHdxPerc)
				// utilhdxpercVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// 处理系统组信息入库
				if (allsystemgrouphash != null && allsystemgrouphash.size() > 0) {
					Enumeration systemgrouphash = allsystemgrouphash.keys();
					XmlDataOperator xmlOprt = new XmlDataOperator();
					xmlOprt.setFile(type + "_systemgroup.xml");
					xmlOprt.init4createXml();
					boolean flag = false;
					while (systemgrouphash.hasMoreElements()) {
						String ip = (String) systemgrouphash.nextElement();
						// SysLogger.info(ip+"==========system");
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector systemgroupVector = (Vector) allsystemgrouphash.get(ip);
						String deleteSql = "delete from nms_system_data_temp where nodeid='" + host.getId() + "'";
						// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
						dbmanager.addBatch(deleteSql);
						if (systemgroupVector != null && systemgroupVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < systemgroupVector.size(); i++) {
								SystemCollectEntity vo = (SystemCollectEntity) systemgroupVector.elementAt(i);
								try {
									StringBuffer _sql = new StringBuffer(500);
									_sql.append("insert into nms_system_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									_sql.append(host.getId());
									_sql.append("','");
									_sql.append(ip);
									_sql.append("','");
									_sql.append(type);
									_sql.append("','");
									_sql.append(nodeDTO.getSubtype());
									_sql.append("','");
									_sql.append(vo.getCategory());
									_sql.append("','");
									_sql.append(vo.getEntity());
									_sql.append("','");
									_sql.append(vo.getSubentity());
									_sql.append("','");
									_sql.append(vo.getThevalue());
									_sql.append("','");
									_sql.append(vo.getChname());
									_sql.append("','");
									_sql.append(vo.getRestype());
									_sql.append("','");
									_sql.append(time);
									_sql.append("','");
									_sql.append(vo.getUnit());
									_sql.append("','");
									_sql.append(vo.getBak());
									_sql.append("')");
									// SysLogger.info(_sql.toString());
									dbmanager.addBatch(_sql.toString());
									// SysLogger.info("=================================="+ip);
									if (host.getTransfer() == 1) {
										xmlOprt.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
										flag = true;
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					if (flag) {
						xmlOprt.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "net_systemgroup.xml");

					}
					xmlOprt = null;
					try {
						dbmanager.executeBatch();
						dbmanager.executePreparedBatch();
					} catch (Exception e) {
						SysLogger.error("---批处理实时系统出错---", e);
					}
				}
				// 处理fdb 信息

				if (allfdbhash != null && allfdbhash.size() > 0) {
					Enumeration pmhash = allfdbhash.keys();
					NodeDTO nodeDTO = null;
					String ip = null;
					IpMac vo = null;
					Calendar tempCal = null;
					Date cc = null;
					String time = null;
					StringBuffer sql = null;
					Vector fdbVector = null;
					XmlDataOperator xmlOprt = null;

					boolean flag = false;
					while (pmhash.hasMoreElements()) {
						ip = (String) pmhash.nextElement();
						nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
						fdbVector = (Vector) allfdbhash.get(ip);
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

						String deleteSql = "delete from nms_fdb_data_temp where nodeid='" + ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (fdbVector != null && fdbVector.size() > 0) {
							deleteSql = null;
							tempCal = Calendar.getInstance();
							time = sdf.format(tempCal.getTime());
							for (int i = 0; i < fdbVector.size(); i++) {
								vo = (IpMac) fdbVector.elementAt(i);
								String mac = vo.getMac();
								if (mac != null && !mac.contains(":")) {// 排除mac为乱码的情况
									mac = "--";
								}
								try {
									sql = new StringBuffer(500);
									sql.append("insert into nms_fdb_data_temp(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak)values('");
									sql.append(host.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(type);
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getIfindex());
									sql.append("','");
									sql.append(vo.getIpaddress());
									sql.append("','");
									sql.append(mac);
									sql.append("','");
									sql.append(vo.getIfband());
									sql.append("','");
									sql.append(vo.getIfsms());
									sql.append("','");
									sql.append(time);
									sql.append("','" + vo.getBak() + "')");
									dbmanager.addBatch(sql.toString());
									if (host.getTransfer() == 1) {
										if (!flag) {
											xmlOpr = new XmlDataOperator();
											xmlOpr.setFile(type + "_fdb.xml");
											xmlOpr.init4createXml();
										}
										xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getRelateipaddr(), mac, vo.getIfindex(), time, vo.getIfband(), vo.getIfsms(), vo.getBak());
										flag = true;
									}
									sql = null;
								} catch (Exception e) {
									e.printStackTrace();
								}
								vo = null;
								sql = null;
							}

						}
						fdbVector = null;
					}
					if (flag) {
						xmlOprt.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_fdb.xml");
					}
					xmlOprt = null;
				}
				allfdbhash = null;
				try {
					dbmanager.executeBatch();
					dbmanager.executePreparedBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {

			} finally {

				if (dbmanager != null) {
					try {
						dbmanager.close();
					} catch (Exception e) {

					}
				}
			}

		}
		return true;
	}

	public boolean generateXML(String type, Hashtable datahash) {
		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			Hashtable allpinghash = new Hashtable();
			Hashtable allcpuhash = new Hashtable();
			Hashtable allpmemoryhash = new Hashtable();
			Hashtable allrouterhash = new Hashtable();
			Hashtable allvmemoryhash = new Hashtable();
			Hashtable allpowerhash = new Hashtable();
			Hashtable allfanhash = new Hashtable();
			Hashtable alltemperaturehash = new Hashtable();
			Hashtable allvoltagehash = new Hashtable();

			Hashtable allhardwarehash = new Hashtable();
			Hashtable allsoftwarehash = new Hashtable();
			Hashtable allservicehash = new Hashtable();
			Hashtable allipmachash = new Hashtable();// arp信息
			Hashtable allfdbhash = new Hashtable(); // fdb信息
			Hashtable allflashhash = new Hashtable();// 闪存信息
			Hashtable allinterfacehash = new Hashtable();
			Hashtable allsystemgrouphash = new Hashtable();

			Hashtable alldiskhash = null;
			NodeUtil nodeUtil = new NodeUtil();
			DBManager dbmanager = new DBManager();
			List list = new ArrayList();
			boolean uploadsuccess = false;
			FtpTransConfigDao ftpconfdao = new FtpTransConfigDao();
			FtpTransConfig ftpConfig = null;
			try {
				ftpConfig = ftpconfdao.getFtpTransMonitorConfig();
				FtpUtil ftputil = null;
				if (ftpConfig != null) {
					ftputil = new FtpUtil(ftpConfig.getIp(), ftpConfig.getUsername(), ftpConfig.getPassword());
					;
				}
				try {
					while (iphash.hasMoreElements()) {
						String ip = (String) iphash.nextElement();
						Hashtable ipdata = (Hashtable) datahash.get(ip);
						if (ipdata != null) {
							// 处理网络设备的数据

							// ping信息
							if (ipdata.containsKey("ping")) {
								Hashtable pinghash = (Hashtable) ipdata.get("ping");
								Vector pingVector = (Vector) pinghash.get("ping");
								if (pingVector != null && pingVector.size() > 0) {
									allpinghash.put(ip, pingVector);
								}
							}

							// CPU信息
							if (ipdata.containsKey("cpu")) {
								Hashtable cpuhash = (Hashtable) ipdata.get("cpu");
								Vector cpuVector = (Vector) cpuhash.get("cpu");
								if (cpuVector != null && cpuVector.size() > 0) {
									// SysLogger.info(" ip ===:"+ip+"
									// cpusize:"+cpuVector.size());
									allcpuhash.put(ip, cpuVector);
								}
							}

							// /////////////////服务器/////////////////////////
							if (type.equals("host")) {
								// 磁盘信息
								alldiskhash = new Hashtable();
								if (ipdata.containsKey("disk")) {
									Hashtable diskhash = (Hashtable) ipdata.get("disk");
									Vector diskVector = (Vector) diskhash.get("disk");
									if (diskVector != null && diskVector.size() > 0) {
										alldiskhash.put(ip, diskVector);
									}
								}
								// 物理内存信息
								if (ipdata.containsKey("physicalmemory")) {
									Hashtable physicalmemoryhash = (Hashtable) ipdata.get("physicalmemory");
									Vector memoryVector = (Vector) physicalmemoryhash.get("memory");
									if (memoryVector != null && memoryVector.size() > 0) {
										allpmemoryhash.put(ip, memoryVector);
									}
								}
								// 虚拟内存信息
								if (ipdata.containsKey("virtualmemory")) {
									Hashtable virtualmemoryhash = (Hashtable) ipdata.get("virtualmemory");
									Vector memoryVector = (Vector) virtualmemoryhash.get("memory");
									Vector memVector = null;
									if (memoryVector != null && memoryVector.size() > 0) {
										if (allpmemoryhash.containsKey(ip)) {
											memVector = (Vector) allpmemoryhash.get(ip);
										}
										for (int i = 0; i < memoryVector.size(); i++) {
											MemoryCollectEntity data = (MemoryCollectEntity) memoryVector.get(i);
											memVector.add(data);
										}
										allpmemoryhash.put(ip, memVector);
									}
								}
								// ARP信息
								if (ipdata.containsKey("ipmac")) {
									Hashtable ipmachash = (Hashtable) ipdata.get("ipmac");
									Vector ipmacVector = (Vector) ipmachash.get("ipmac");
									if (ipmacVector != null && ipmacVector.size() > 0) {
										allipmachash.put(ip, ipmacVector);
									}
								}
							}

							// /////////////////////网络设备///////////////////
							if (type.equals("net")) {

								// 内存信息
								if (ipdata.containsKey("memory")) {
									Hashtable physicalmemoryhash = (Hashtable) ipdata.get("memory");
									Vector memoryVector = (Vector) physicalmemoryhash.get("memory");
									if (memoryVector != null && memoryVector.size() > 0) {
										allpmemoryhash.put(ip, memoryVector);

									}
								}

								// 闪存信息
								if (ipdata.containsKey("flash")) {
									Hashtable flashhash = (Hashtable) ipdata.get("flash");
									Vector flashVector = (Vector) flashhash.get("flash");
									if (flashVector != null && flashVector.size() > 0) {
										allflashhash.put(ip, flashVector);
									}
								}
								// 路由信息
								// if(ipdata.containsKey("iprouter")){
								// Hashtable iprouterhash =
								// (Hashtable)ipdata.get("iprouter");
								// Vector routerVector = (Vector)
								// iprouterhash.get("iprouter");
								// if (routerVector != null &&
								// routerVector.size() > 0) {
								// allrouterhash.put(ip, routerVector);
								// }
								// }
								// 电源信息
								if (ipdata.containsKey("power")) {
									Hashtable powerhash = (Hashtable) ipdata.get("power");
									Vector powerVector = (Vector) powerhash.get("power");
									if (powerVector != null && powerVector.size() > 0) {
										allpowerhash.put(ip, powerVector);
									}
								}
								// 风扇信息
								if (ipdata.containsKey("fan")) {
									Hashtable fanhash = (Hashtable) ipdata.get("fan");
									Vector fanVector = (Vector) fanhash.get("fan");
									if (fanVector != null && fanVector.size() > 0) {
										allfanhash.put(ip, fanVector);
									}
								}

								// 温度信息
								if (ipdata.containsKey("temperature")) {
									Hashtable temperaturehash = (Hashtable) ipdata.get("temperature");
									Vector temperatureVector = (Vector) temperaturehash.get("temperature");
									if (temperatureVector != null && temperatureVector.size() > 0) {
										alltemperaturehash.put(ip, temperatureVector);
									}
								}

								// 电压信息
								if (ipdata.containsKey("voltage")) {
									Hashtable voltagehash = (Hashtable) ipdata.get("voltage");
									Vector voltageVector = (Vector) voltagehash.get("voltage");
									if (voltageVector != null && voltageVector.size() > 0) {
										allvoltagehash.put(ip, voltageVector);
									}
								}
								if (ipdata.containsKey("fdb")) {
									Hashtable fdbhash = (Hashtable) ipdata.get("fdb");
									Vector fdbVector = (Vector) fdbhash.get("fdb");
									if (fdbVector != null && fdbVector.size() > 0) {
										allfdbhash.put(ip, fdbVector);
									}
								}
							}
							// 系统属性信息
							if (ipdata.containsKey("systemgroup")) {
								Hashtable systemhash = (Hashtable) ipdata.get("systemgroup");
								Vector systemVector = (Vector) systemhash.get("system");
								if (systemVector != null && systemVector.size() > 0) {
									allsystemgrouphash.put(ip, systemVector);
								}
							}

						}
					}

					// 处理PING入库
					if (allpinghash != null && allpinghash.size() > 0) {
						XmlDataOperator xmlOpr = new XmlDataOperator();

						xmlOpr.setFile(type + "_ping.xml");
						xmlOpr.init4createXml();
						Enumeration pinghash = allpinghash.keys();
						boolean flag = false;
						NodeDTO nodeDTO = null;
						String ip = null;
						PingCollectEntity vo = null;
						String time = null;
						Calendar tempCal = null;
						while (pinghash.hasMoreElements()) {
							ip = (String) pinghash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							if (host == null)
								continue;
							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							Vector pingVector = (Vector) allpinghash.get(ip);
							if (pingVector != null && pingVector.size() > 0) {
								for (int i = 0; i < pingVector.size(); i++) {
									vo = (PingCollectEntity) pingVector.elementAt(i);
									try {
										tempCal = (Calendar) vo.getCollecttime();
										Date cc = tempCal.getTime();
										time = sdf.format(cc);
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									time = null;
									vo = null;
								}
							}
							pingVector = null;
							nodeDTO = null;
							ip = null;
						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_ping.xml");
						}
					}
					allpinghash = null;

					// 处理CPU入库
					if (allcpuhash != null && allcpuhash.size() > 0) {
						Enumeration cpuhash = allcpuhash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						CpuCollectEntity vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						Vector cpuVector = null;
						XmlDataOperator xmlOpr = new XmlDataOperator();
						xmlOpr.setFile(type + "_cpu.xml");
						xmlOpr.init4createXml();
						boolean flag = false;
						while (cpuhash.hasMoreElements()) {
							ip = (String) cpuhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							cpuVector = (Vector) allcpuhash.get(ip);

							// 得到CPU平均
							vo = (CpuCollectEntity) cpuVector.elementAt(0);
							try {
								tempCal = (Calendar) vo.getCollecttime();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								if (host.getTransfer() == 1) {
									xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
									flag = true;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							cpuVector = null;
							vo = null;

						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_cpu.xml");
						}
					}
					allcpuhash = null;

					// 处理物理内存入库
					if (allpmemoryhash != null && allpmemoryhash.size() > 0) {
						Enumeration pmhash = allpmemoryhash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						MemoryCollectEntity vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						StringBuffer sql = null;
						Vector memoryVector = null;
						XmlDataOperator xmlOpr = new XmlDataOperator();
						xmlOpr.setFile(type + "_mem.xml");
						xmlOpr.init4createXml();
						boolean flag = false;
						while (pmhash.hasMoreElements()) {
							ip = (String) pmhash.nextElement();
							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							memoryVector = (Vector) allpmemoryhash.get(ip);
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							if (memoryVector != null && memoryVector.size() > 0) {
								for (int i = 0; i < memoryVector.size(); i++) {
									vo = (MemoryCollectEntity) memoryVector.elementAt(i);
									tempCal = (Calendar) vo.getCollecttime();
									cc = tempCal.getTime();
									time = sdf.format(cc);
									try {
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									vo = null;
									sql = null;

								}

							}
							memoryVector = null;
						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_mem.xml");
						}
					}
					allpmemoryhash = null;

					// 处理路由信息
					// if(allrouterhash != null && allrouterhash.size()>0){
					// Enumeration routerhash = allrouterhash.keys();
					// NodeDTO nodeDTO = null;
					// String ip = null;
					// IpRouter vo = null;
					// Calendar tempCal = null;
					// Date cc = null;
					// String time = null;
					// StringBuffer sql = null;
					// Vector routerVector = null;
					// XmlDataOperator xmlOpr = null;
					//	    		
					// boolean flag=false;
					// while(routerhash.hasMoreElements()){
					// ip = (String)routerhash.nextElement();
					// Host host =
					// (Host)PollingEngine.getInstance().getNodeByIP(ip);
					// if(host==null)continue;
					// nodeDTO =
					// nodeUtil.creatNodeDTOByNode((Host)PollingEngine.getInstance().getNodeByIP(ip));
					// routerVector = (Vector)allrouterhash.get(ip);
					//					
					//					
					//					
					// try {
					//						
					// if (routerVector!=null&&routerVector.size()>0) {
					//						
					// for(int i=0;i<routerVector.size();i++){
					// vo = (IpRouter) routerVector.get(i);
					// tempCal = (Calendar) vo.getCollecttime();
					// cc = tempCal.getTime();
					// time = sdf.format(cc);
					// if(host.getTransfer()==1){
					// if (!flag) {
					// xmlOpr = new XmlDataOperator();
					// xmlOpr.setFile("net_iprouter.xml");
					// xmlOpr.init4createXml();
					// }
					// xmlOpr.addIPNode(ip,type,
					// nodeDTO.getSubtype(),vo.getIfindex(), vo.getNexthop(),
					// vo.getProto().longValue()+"",vo.getType().longValue()+"",
					// vo.getMask(), time,
					// vo.getPhysaddress(), vo.getDest());
					// flag=true;
					// }
					// }
					//						
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// routerVector = null;
					// vo = null;
					//					
					//					
					// }
					// if(flag){
					// xmlOpr.createXml();
					// uploadsuccess=ftputil.uploadFile(ftpConfig.getIp(),
					// ftpConfig.getUsername(), ftpConfig.getPassword(),
					// ResourceCenter.getInstance().getSysPath()+"/linuxserver/net_cpu.xml");
					//				
					// }
					// xmlOpr=null;
					// }
					// routerhash = null;
					if (alldiskhash != null && alldiskhash.size() > 0) {
						boolean flag = false;
						XmlDataOperator xmlOpr = null;
						Enumeration diskhash = alldiskhash.keys();
						while (diskhash.hasMoreElements()) {
							String ip = (String) diskhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector diskVector = (Vector) alldiskhash.get(ip);

							if (diskVector != null && diskVector.size() > 0) {
								for (int i = 0; i < diskVector.size(); i++) {
									DiskCollectEntity vo = (DiskCollectEntity) diskVector.elementAt(i);
									if (vo.getSubentity().equals("Physical Memory") || vo.getSubentity().equals("Virtual Memory") || vo.getSubentity().trim().length() == 0)
										continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									try {

										if (host.getTransfer() == 1) {
											if (!flag) {
												xmlOpr = new XmlDataOperator();
												xmlOpr.setFile("host_disk.xml");
												xmlOpr.init4createXml();
											}
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						if (flag) {
							xmlOpr.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", "net_ping.xml");

						}
						xmlOpr = null;
					}

					Hashtable envdeletedHash = new Hashtable();

					// 处理电源信息入库
					HashMap<String, XmlDataOperator> map = new HashMap<String, XmlDataOperator>();
					XmlDataOperator xmlOpr = new XmlDataOperator();
					xmlOpr.setFile(type + "_envir.xml");
					xmlOpr.init4createXml();
					boolean flag = false;
					if (allpowerhash != null && allpowerhash.size() > 0) {
						Enumeration powerhash = allpowerhash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						Interfacecollectdata vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						Vector powerVector = null;

						while (powerhash.hasMoreElements()) {
							ip = (String) powerhash.nextElement();
							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							powerVector = (Vector) allpowerhash.get(ip);
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

							envdeletedHash.put(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "", ((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
							if (powerVector != null && powerVector.size() > 0) {
								tempCal = Calendar.getInstance();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								for (int i = 0; i < powerVector.size(); i++) {
									vo = (Interfacecollectdata) powerVector.elementAt(i);
									try {
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
										list = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								vo = null;
							}
							nodeDTO = null;
							powerVector = null;

						}
					}
					allpowerhash = null;

					// 处理风扇信息
					if (allfanhash != null && allfanhash.size() > 0) {
						Enumeration fanhash = allfanhash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						Interfacecollectdata vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						Vector fanVector = null;

						while (fanhash.hasMoreElements()) {
							ip = (String) fanhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							fanVector = (Vector) allfanhash.get(ip);

							if (fanVector != null && fanVector.size() > 0) {
								tempCal = Calendar.getInstance();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								for (int i = 0; i < fanVector.size(); i++) {
									vo = (Interfacecollectdata) fanVector.elementAt(i);
									try {
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
										list = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
									vo = null;
								}
							}

							nodeDTO = null;
							fanVector = null;
						}
					}
					allfanhash = null;

					// 处理电压信息入库
					if (allvoltagehash != null && allvoltagehash.size() > 0) {
						Enumeration voltagehash = allvoltagehash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						Interfacecollectdata vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						Vector fanVector = null;
						while (voltagehash.hasMoreElements()) {
							ip = (String) voltagehash.nextElement();
							nodeDTO = nodeUtil.creatNodeDTOByNode(((Host) PollingEngine.getInstance().getNodeByIP(ip)));
							fanVector = (Vector) allvoltagehash.get(ip);
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

							if (fanVector != null && fanVector.size() > 0) {
								tempCal = Calendar.getInstance();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								for (int i = 0; i < fanVector.size(); i++) {
									vo = (Interfacecollectdata) fanVector.elementAt(i);
									try {
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
										list = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
									vo = null;
								}
							}

							fanVector = null;
							nodeDTO = null;
							ip = null;
						}
					}
					allvoltagehash = null;

					// 处理温度信息入库
					if (alltemperaturehash != null && alltemperaturehash.size() > 0) {
						Enumeration temperaturehash = alltemperaturehash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						Interfacecollectdata vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						Vector temperatureVector = null;
						while (temperaturehash.hasMoreElements()) {
							ip = (String) temperaturehash.nextElement();
							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							temperatureVector = (Vector) alltemperaturehash.get(ip);

							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							if (temperatureVector != null && temperatureVector.size() > 0) {
								tempCal = Calendar.getInstance();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								for (int i = 0; i < temperatureVector.size(); i++) {
									vo = (Interfacecollectdata) temperatureVector.elementAt(i);
									try {
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
										list = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
									vo = null;
								}
							}
							temperatureVector = null;
							nodeDTO = null;
							ip = null;
						}
					}
					alltemperaturehash = null;

					// 处理闪存信息入库
					if (allflashhash != null && allflashhash.size() > 0) {
						Enumeration flashhash = allflashhash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						FlashCollectEntity vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						while (flashhash.hasMoreElements()) {
							ip = (String) flashhash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector flashVector = (Vector) allflashhash.get(ip);
							if (flashVector != null && flashVector.size() > 0) {
								tempCal = Calendar.getInstance();
								cc = tempCal.getTime();
								time = sdf.format(cc);
								for (int i = 0; i < flashVector.size(); i++) {
									vo = (FlashCollectEntity) flashVector.elementAt(i);
									try {
										if (host.getTransfer() == 1) {
											xmlOpr.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flag = true;
										}
										list = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					allflashhash = null;
					if (flag) {
						xmlOpr.createXml();
						uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_envir.xml");

					}

					// 处理系统组信息入库
					if (allsystemgrouphash != null && allsystemgrouphash.size() > 0) {
						Enumeration systemgrouphash = allsystemgrouphash.keys();
						XmlDataOperator xmlOprt = new XmlDataOperator();
						xmlOprt.setFile(type + "_systemgroup.xml");
						xmlOprt.init4createXml();
						boolean flags = false;
						while (systemgrouphash.hasMoreElements()) {
							String ip = (String) systemgrouphash.nextElement();
							// SysLogger.info(ip+"==========system");
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector systemgroupVector = (Vector) allsystemgrouphash.get(ip);
							if (systemgroupVector != null && systemgroupVector.size() > 0) {
								Calendar tempCal = Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for (int i = 0; i < systemgroupVector.size(); i++) {
									SystemCollectEntity vo = (SystemCollectEntity) systemgroupVector.elementAt(i);
									try {
										if (host.getTransfer() == 1) {
											xmlOprt.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getCategory(), vo.getEntity(), vo.getSubentity(), vo.getThevalue(), vo.getChname(), vo.getRestype(), time, vo.getUnit(), vo.getBak());
											flags = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						if (flags) {
							xmlOprt.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_systemgroup.xml");
						}
					}
					if (allfdbhash != null && allfdbhash.size() > 0) {
						Enumeration pmhash = allfdbhash.keys();
						NodeDTO nodeDTO = null;
						String ip = null;
						IpMac vo = null;
						Calendar tempCal = null;
						Date cc = null;
						String time = null;
						StringBuffer sql = null;
						Vector fdbVector = null;
						XmlDataOperator xmlOprt = null;

						boolean flags = false;
						while (pmhash.hasMoreElements()) {
							ip = (String) pmhash.nextElement();
							nodeDTO = nodeUtil.creatNodeDTOByNode((Host) PollingEngine.getInstance().getNodeByIP(ip));
							fdbVector = (Vector) allfdbhash.get(ip);
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);

							if (fdbVector != null && fdbVector.size() > 0) {

								tempCal = Calendar.getInstance();
								time = sdf.format(tempCal.getTime());
								for (int i = 0; i < fdbVector.size(); i++) {
									vo = (IpMac) fdbVector.elementAt(i);
									String mac = vo.getMac();
									if (mac != null && !mac.contains(":")) {// 排除mac为乱码的情况
										mac = "--";
									}
									try {

										if (host.getTransfer() == 1) {
											if (!flags) {
												xmlOprt = new XmlDataOperator();
												xmlOprt.setFile(type + "_fdb.xml");
												xmlOprt.init4createXml();
											}

											xmlOprt.addIPNode(ip, type, nodeDTO.getSubtype(), vo.getRelateipaddr(), mac, vo.getIfindex(), time, vo.getIfband(), vo.getIfsms(), vo.getBak());
											flags = true;
										}
										sql = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
									vo = null;
									sql = null;
								}

							}
							fdbVector = null;
						}
						if (flags) {
							xmlOprt.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_fdb.xml");
						}
						xmlOprt = null;
					}
					// 处理IPMAC信息入库
					if (allipmachash != null && allipmachash.size() > 0) {
						XmlDataOperator xmlOprt = null;
						boolean flags = false;
						Enumeration ipmachash = allipmachash.keys();
						while (ipmachash.hasMoreElements()) {
							String ip = (String) ipmachash.nextElement();
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
							NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector ipmacVector = (Vector) allipmachash.get(ip);
							if (ipmacVector != null && ipmacVector.size() > 0) {
								for (int i = 0; i < ipmacVector.size(); i++) {
									IpMac ipmac = (IpMac) ipmacVector.elementAt(i);
									String mac = ipmac.getMac();
									if (mac == null) {
										mac = "";
									}
									mac = CommonUtil.removeIllegalStr(mac);
									try {
										String _sql = "";
										String time = sdf.format(ipmac.getCollecttime().getTime());
										if (host.getTransfer() == 1) {
											if (!flags) {
												xmlOprt = new XmlDataOperator();
												xmlOprt.setFile(type + "_arp.xml");
												xmlOprt.init4createXml();
											}

											xmlOprt.addIPNode(ip, type, nodeDTO.getSubtype(), ipmac.getRelateipaddr(), ipmac.getIfindex(), ipmac.getMac(), time, ipmac.getIfband(), ipmac.getIfsms(), ipmac.getBak());
											flags = true;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						if (flags) {
							xmlOprt.createXml();
							uploadsuccess = ftputil.upload(ResourceCenter.getInstance().getSysPath() + "/linuxserver/", type + "_arp.xml");

						}
						xmlOpr = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ftpconfdao.close();
			}
		}

		return true;
	}

	/**
	 * aix\linux\hp-unix\solaris
	 * 
	 * @author HONGLI 2011-02-11
	 * @param type
	 * @param datahash
	 * @return
	 */
	public boolean processHostData(Hashtable datahash) {
		if (datahash == null || datahash.isEmpty()) {
			return false;
		}
		if (datahash != null && datahash.size() > 0) {
			Enumeration iphash = datahash.keys();
			Hashtable allpinghash = new Hashtable();
			Hashtable allcpuhash = new Hashtable();
			Hashtable allcpuconfighash = new Hashtable();// Cpu配置信息
			Hashtable allpmemoryhash = new Hashtable();
			Hashtable allvmemoryhash = new Hashtable();
			Hashtable alldiskhash = new Hashtable();
			Hashtable alldiskperfhash = new Hashtable();// 磁盘性能
			Hashtable alldiskioHash = new Hashtable();// 磁盘性能IO
			Hashtable allstoragehash = new Hashtable();
			Hashtable allhardwarehash = new Hashtable();
			Hashtable allsoftwarehash = new Hashtable();
			Hashtable allservicehash = new Hashtable();
			Hashtable allinpackshash = new Hashtable();
			Hashtable alloutpackshash = new Hashtable();
			Hashtable allipmachash = new Hashtable();
			Hashtable allinterfacehash = new Hashtable();
			Hashtable allsystemgrouphash = new Hashtable();
			Hashtable allprocesshash = new Hashtable();
			Hashtable alluserhash = new Hashtable();
			Hashtable allcpuperfhash = new Hashtable();
			Hashtable allerrpthash = new Hashtable();
			Hashtable otherhash = new Hashtable();
			Hashtable allutilhdxhash = new Hashtable();
			Hashtable allnetmediahash = new Hashtable();
			Hashtable allpagehash = new Hashtable();// 页面性能
			Hashtable allpaginghash = new Hashtable();// Paging Space利用率
			Hashtable allsystemhash = new Hashtable();
			Hashtable allroutehash = new Hashtable();
			Hashtable allcollecttimehash = new Hashtable();
			Hashtable allvolumehash = new Hashtable();
			Hashtable allifhash = new Hashtable();
			Hashtable allpolicyhash = new Hashtable();
			Hashtable alldevicehash = new Hashtable();
			Hashtable allnodeconfighash = new Hashtable();
			Hashtable allnetflowhash = new Hashtable();
			Hashtable allnetworkconfighash = new Hashtable();
			Hashtable allmemoryconfigHash = new Hashtable();
			Hashtable allhostinfoHash = new Hashtable();
			Hashtable allphysicaldiskhash = new Hashtable();
			Hashtable allcpuconfighash_wmi = new Hashtable();
			Hashtable alldiskperfhash_wmi = new Hashtable();
			Hashtable alllogicdiskperformancehash = new Hashtable();
			Hashtable allpefmemoryhash = new Hashtable();
			Hashtable allnetworkstatushash = new Hashtable();
			NodeUtil nodeUtil = new NodeUtil();
			DBManager dbmanager = new DBManager();
			List list = new ArrayList();
			int flag = 0;
			try {
				// conn = DataGate.getCon();
				// conn.setAutoCommit(false);
				// stmt = conn.createStatement();
				while (iphash.hasMoreElements()) {
					String ip = (String) iphash.nextElement();
					Hashtable ipdata = (Hashtable) datahash.get(ip);
					if (ipdata != null) {
						Host node = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						// ###########aix\linux\hp-unix\solaris SHELL方式采集
						// 处理主机设备的数据
						// ping信息
						if (ipdata.containsKey("ping")) {
							Hashtable pinghash = (Hashtable) ipdata.get("ping");
							Vector pingVector = (Vector) pinghash.get("ping");
							if (pingVector != null && pingVector.size() > 0) {
								allpinghash.put(ip, pingVector);
							}
						}
						// CPU信息
						if (ipdata.containsKey("cpu")) {
							Vector cpuVector = (Vector) ipdata.get("cpu");
							if (cpuVector != null && cpuVector.size() > 0) {
								allcpuhash.put(ip, cpuVector);
							}
						}
						// Cpu配置信息
						if (ipdata.containsKey("cpuconfiglist")) {
							List cpuconfiglist = (List) ipdata.get("cpuconfiglist");
							if (cpuconfiglist != null && cpuconfiglist.size() > 0) {
								allcpuconfighash.put(ip, cpuconfiglist);
							}
						}

						// memory
						if (ipdata.containsKey("memory")) {
							Vector memoryVector = (Vector) ipdata.get("memory");
							if (memoryVector != null && memoryVector.size() > 0) {
								allpmemoryhash.put(ip, memoryVector);
							}
						}

						// virtualmemory
						// if(ipdata.containsKey("SwapMemory")){
						// Hashtable virtualmemoryhash =
						// (Hashtable)ipdata.get("virtualmemory");
						// Vector memoryVector = (Vector)
						// virtualmemoryhash.get("memory");
						// if (memoryVector != null && memoryVector.size() > 0)
						// {
						// allvmemoryhash.put(ip, memoryVector);
						// }
						// }

						// 进程
						if (ipdata.containsKey("process")) {
							// Hashtable processhash =
							// (Hashtable)ipdata.get("process");
							Vector processVector = (Vector) ipdata.get("process");
							if (processVector != null && processVector.size() > 0) {
								allprocesshash.put(ip, processVector);
							}
						}
						//					
						// //disk
						if (ipdata.containsKey("disk")) {
							// Hashtable diskhash =
							// (Hashtable)ipdata.get("disk");
							Vector diskVector = (Vector) ipdata.get("disk");
							if (diskVector != null && diskVector.size() > 0) {
								alldiskhash.put(ip, diskVector);
							}
						}
						// alldiskperf
						if (ipdata.containsKey("alldiskperf")) {
							List diskprefList = (ArrayList) ipdata.get("alldiskperf");
							if (diskprefList != null && diskprefList.size() > 0) {
								alldiskperfhash.put(ip, diskprefList);
							}
						}
						if (ipdata.containsKey("alldiskio")) {
							List diskioList = (ArrayList) ipdata.get("alldiskio");
							if (diskioList != null && diskioList.size() > 0) {
								alldiskioHash.put(ip, diskioList);
							}
						}
						// //user
						if (ipdata.containsKey("user")) {
							// windows WMI方式采集时 ，user的数据结构为List<Hashtable>
							if (node.getCollecttype() == SystemConstant.COLLECTTYPE_WMI) {
								List userList = (ArrayList) ipdata.get("user");
								Vector userVector = convertWMIUserListToVector(userList);
								if (userVector != null && userVector.size() > 0) {
									alluserhash.put(ip, userVector);
								}
								// 调试代码-查看key的值
								// Iterator iterator =
								// ipdata.keySet().iterator();
								// StringBuffer keyBuffer = new StringBuffer();
								// while(iterator.hasNext()){
								// String key = (String)iterator.next();
								// keyBuffer.append(key+"-");
								// }
								// System.out.println("aaa");
							} else {
								// Hashtable storhash =
								// (Hashtable)ipdata.get("user");
								Vector userVector = (Vector) ipdata.get("user");
								if (userVector != null && userVector.size() > 0) {
									alluserhash.put(ip, userVector);
								}
							}
						}

						// 网络配置 (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("networkconfig")) {
							if (ipdata.get("networkconfig") instanceof ArrayList) {
								List networkconfigList = (ArrayList) ipdata.get("networkconfig");
								allnetworkconfighash.put(ip, networkconfigList);
							}
						}
						// 内存配置 (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("memoryconfig")) {
							if (ipdata.get("memoryconfig") instanceof Hashtable) {
								Hashtable memoryconfigHash = (Hashtable) ipdata.get("memoryconfig");
								allmemoryconfigHash.put(ip, memoryconfigHash);
							}
						}
						// 主机信息 (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("hostinfo")) {
							if (ipdata.get("hostinfo") instanceof Hashtable) {
								Hashtable hostinfoHash = (Hashtable) ipdata.get("hostinfo");
								allhostinfoHash.put(ip, hostinfoHash);
							}
						}
						// 物理硬盘 (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("physicaldisklist")) {
							if (ipdata.get("physicaldisklist") instanceof ArrayList) {
								List physicaldisklist = (ArrayList) ipdata.get("physicaldisklist");
								allphysicaldiskhash.put(ip, physicaldisklist);
							}
						}
						// cpu配置信息(WINDOWS WMI 采集的信息) cpuconfig
						if (ipdata.containsKey("cpuconfig")) {
							if (ipdata.get("cpuconfig") instanceof ArrayList) {
								List cpuconfiglist = (ArrayList) ipdata.get("cpuconfig");
								allcpuconfighash_wmi.put(ip, cpuconfiglist);
							}
						}
						// diskperforlist (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("diskperforlist")) {
							if (ipdata.get("diskperforlist") instanceof ArrayList) {
								List diskperforlist = (ArrayList) ipdata.get("diskperforlist");
								alldiskperfhash_wmi.put(ip, diskperforlist);
								alldiskperfhash = alldiskperfhash_wmi;
							}
						}
						// logicdiskperformancelist (WINDOWS WMI 采集的信息) 【测试环境为空】
						if (ipdata.containsKey("logicdiskperformancelist")) {
							if (ipdata.get("logicdiskperformancelist") instanceof ArrayList) {
								List logicdiskperformancelist = (ArrayList) ipdata.get("logicdiskperformancelist");
								alllogicdiskperformancehash.put(ip, logicdiskperformancelist);
							}
						}
						// pefmemory (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("pefmemory")) {
							if (ipdata.get("pefmemory") instanceof Hashtable) {
								Hashtable pefmemoryHash = (Hashtable) ipdata.get("pefmemory");
								allpefmemoryhash.put(ip, pefmemoryHash);
							}
						}
						// networkstatus (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("networkstatus")) {
							if (ipdata.get("networkstatus") instanceof ArrayList) {
								List networkstatusList = (ArrayList) ipdata.get("networkstatus");
								allnetworkstatushash.put(ip, networkstatusList);
							}
						}
						// service服务信息 (WINDOWS WMI 采集的信息)
						if (ipdata.containsKey("service")) {
							List servicelist = (ArrayList) ipdata.get("service");
							if (servicelist != null && servicelist.size() > 0) {
								allservicehash.put(ip, servicelist);
							}
						}
						// cpu性能
						if (ipdata.containsKey("cpuperflist")) {
							// Hashtable storhash =
							// (Hashtable)ipdata.get("cpuperflist");
							List cpuperfVector = (ArrayList) ipdata.get("cpuperflist");
							if (cpuperfVector != null && cpuperfVector.size() > 0) {
								allcpuperfhash.put(ip, cpuperfVector);
							}
						}

						// errptlog
						if (ipdata.containsKey("errptlog")) {
							Vector errptVector = (Vector) ipdata.get("errptlog");
							if (errptVector != null && errptVector.size() > 0) {
								allerrpthash.put(ip, errptVector);
							}
						}

						// utilhdx
						if (ipdata.containsKey("utilhdx")) {
							Vector utilhdxVector = (Vector) ipdata.get("utilhdx");
							if (utilhdxVector != null && utilhdxVector.size() > 0) {
								allutilhdxhash.put(ip, utilhdxVector);
							}
						}
						// 网卡配置信息
						if (ipdata.containsKey("netmedialist")) {
							List netmedialist = (ArrayList) ipdata.get("netmedialist");
							if (netmedialist != null && netmedialist.size() > 0) {
								allnetmediahash.put(ip, netmedialist);
							}
						}
						// 网卡配置信息
						if (ipdata.containsKey("netconflist")) {
							List netmedialist = (ArrayList) ipdata.get("netconflist");
							if (netmedialist != null && netmedialist.size() > 0) {
								allnetmediahash.put(ip, netmedialist);
							}
						}
						// 页面性能
						if (ipdata.containsKey("pagehash")) {
							Hashtable pagehash = (Hashtable) ipdata.get("pagehash");
							if (pagehash != null && pagehash.size() > 0) {
								allpagehash.put(ip, pagehash);
							}
						}
						// Paging Space利用率
						if (ipdata.containsKey("paginghash")) {
							Hashtable paginghash = (Hashtable) ipdata.get("paginghash");
							if (paginghash != null && paginghash.size() > 0) {
								allpaginghash.put(ip, paginghash);
							}
						}
						// 系统信息
						if (ipdata.containsKey("system")) {
							Vector systemVector = (Vector) ipdata.get("system");
							if (systemVector != null && systemVector.size() > 0) {
								allsystemhash.put(ip, systemVector);
							}
						}
						// 路由信息 routelist
						if (ipdata.containsKey("routelist")) {
							List routeList = (ArrayList) ipdata.get("routelist");
							if (routeList != null && routeList.size() > 0) {
								allroutehash.put(ip, routeList);
							}
						}
						// interface
						if (ipdata.containsKey("interface")) {
							Vector interfaceVector = (Vector) ipdata.get("interface");
							if (interfaceVector != null && interfaceVector.size() > 0) {
								allinterfacehash.put(ip, interfaceVector);
							}
						}
						// 服务信息 servicelist
						if (ipdata.containsKey("servicelist")) {
							List servicelist = (ArrayList) ipdata.get("servicelist");
							if (servicelist != null && servicelist.size() > 0) {
								allservicehash.put(ip, servicelist);
							}
						}
						// 数据采集时间 collecttime
						if (ipdata.containsKey("collecttime")) {
							String collecttime = (String) ipdata.get("collecttime");
							if (collecttime != null && !collecttime.trim().equals("")) {
								allcollecttimehash.put(ip, collecttime);
							}
						} else {
							allcollecttimehash.put(ip, "");
						}

						// volume
						if (ipdata.containsKey("volume")) {
							Vector volumeVector = (Vector) ipdata.get("volume");
							if (volumeVector != null && volumeVector.size() > 0) {
								allvolumehash.put(ip, volumeVector);
							}
						}
						// iflist
						if (ipdata.containsKey("iflist")) {
							List iflist = (ArrayList) ipdata.get("iflist");
							if (iflist != null && iflist.size() > 0) {
								allifhash.put(ip, iflist);
							}
						}
						// policydata
						if (ipdata.containsKey("policys")) {
							Hashtable policyHash = (Hashtable) ipdata.get("policys");
							if (policyHash != null && policyHash.size() > 0) {
								allpolicyhash.put(ip, policyHash);
							}
						}
						// 硬件信息
						if (ipdata.containsKey("device")) {
							Vector deviceVector = (Vector) ipdata.get("device");
							if (deviceVector != null && deviceVector.size() > 0) {
								alldevicehash.put(ip, deviceVector);
							}
						}
						// 存储信息
						if (ipdata.containsKey("storage")) {
							Vector storageVector = (Vector) ipdata.get("storage");
							if (storageVector != null && storageVector.size() > 0) {
								allstoragehash.put(ip, storageVector);
							}
						}
						// nodeconfig信息
						if (ipdata.containsKey("nodeconfig")) {
							Nodeconfig nodeconfig = (Nodeconfig) ipdata.get("nodeconfig");
							if (nodeconfig != null) {
								allnodeconfighash.put(ip, nodeconfig);
							}
						}
						// netflowlist
						if (ipdata.containsKey("netflowlist")) {
							List netflowlist = (ArrayList) ipdata.get("netflowlist");
							if (netflowlist != null && netflowlist.size() > 0) {
								allnetflowhash.put(ip, netflowlist);
							}
						}
					}
				}
				// 处理PING入库
				if (allpinghash != null && allpinghash.size() > 0) {
					Enumeration pingEnumeration = allpinghash.keys();
					String pingsql = "insert into nms_ping_data_temp" + "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
					dbmanager.setPrepareSql(pingsql);
					flag = 1;
					// pingps = conn.prepareStatement(pingsql);
					while (pingEnumeration.hasMoreElements()) {
						String ip = (String) pingEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// System.out.println("-------"+nodeDTO.getIpaddress());
						Vector pingVector = (Vector) allpinghash.get(ip);
						if (pingVector != null && pingVector.size() > 0) {
							String deleteSql = "delete from nms_ping_data_temp where nodeid='" + host.getId() + "'";
							dbmanager.addBatch(deleteSql);
							for (int i = 0; i < pingVector.size(); i++) {
								PingCollectEntity vo = (PingCollectEntity) pingVector.elementAt(i);
								try {
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									list = new ArrayList();
									list.add(((Host) PollingEngine.getInstance().getNodeByIP(ip)).getId() + "");
									list.add(ip);
									list.add(nodeDTO.getType());
									list.add(nodeDTO.getSubtype());
									list.add(vo.getCategory());
									list.add(vo.getEntity());
									list.add(vo.getSubentity());
									list.add(vo.getThevalue());
									list.add(vo.getChname());
									list.add(vo.getRestype());
									list.add(time);
									list.add(vo.getUnit());
									list.add(vo.getBak());
									dbmanager.addPrepareBatch(list);
									list = null;

									// pingps.setString(1, host.getId()+"");
									// pingps.setString(2, ip);
									// pingps.setString(3,
									// nodeDTO.getType());//type
									// pingps.setString(4,
									// nodeDTO.getSubtype());//subype
									// pingps.setString(5,
									// vo.getCategory());//entity
									// pingps.setString(6,
									// vo.getEntity());//subentity
									// pingps.setString(7,
									// vo.getSubentity());//sindex
									// pingps.setString(8,
									// vo.getThevalue());//thevalue
									// pingps.setString(9,
									// vo.getChname());//chname
									// pingps.setString(10,
									// vo.getRestype());//restype
									// pingps.setString(11, time);//collecttime
									// pingps.setString(12, vo.getUnit());//unit
									// pingps.setString(13, vo.getBak());//bak
									// pingps.addBatch();
									// stmt.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// WINDOWS WMI allnetworkstatushash
				if (allnetworkstatushash != null && !allnetworkstatushash.isEmpty()) {
					Iterator iterator = allnetworkstatushash.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'networkstatus'";
						dbmanager.addBatch(deleteSql);
						// 遍历每个ip对应的配置信息
						List networkstatusList = (ArrayList) allnetworkstatushash.get(ip);
						if (networkstatusList == null || networkstatusList.size() == 0) {
							continue;
						}
						for (int i = 0; i < networkstatusList.size(); i++) {
							Hashtable tempHash = (Hashtable) networkstatusList.get(i);
							Iterator tempIterator = tempHash.keySet().iterator();
							while (tempIterator.hasNext()) {
								String key = (String) tempIterator.next();
								String value = String.valueOf(tempHash.get(key));
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(nodeDTO.getType());// type
								sql.append("','");
								sql.append(nodeDTO.getSubtype());// subtype
								sql.append("','");
								sql.append("networkstatus");// entity
								sql.append("','");
								sql.append(key);// subentity
								sql.append("','");
								sql.append(i);// sindex
								sql.append("','");
								sql.append(value);// thevalue
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append(time);
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("')");
								dbmanager.addBatch(sql.toString());
							}
						}
					}
				}

				// WINDOWS WMI allpefmemoryhash
				if (allpefmemoryhash != null && !allpefmemoryhash.isEmpty()) {
					Iterator iterator = allpefmemoryhash.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'pefmemory'";
						dbmanager.addBatch(deleteSql);
						Hashtable pefmemoryhash = (Hashtable) allpefmemoryhash.get(ip);
						Iterator tempIterator = pefmemoryhash.keySet().iterator();
						while (tempIterator.hasNext()) {
							String key = (String) tempIterator.next();
							String value = String.valueOf(pefmemoryhash.get(key));
							StringBuffer sql = new StringBuffer();
							sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
							sql.append(nodeDTO.getId());
							sql.append("','");
							sql.append(ip);
							sql.append("','");
							sql.append(nodeDTO.getType());// type
							sql.append("','");
							sql.append(nodeDTO.getSubtype());// subtype
							sql.append("','");
							sql.append("pefmemory");// entity
							sql.append("','");
							sql.append(key);// subentity
							sql.append("','");
							sql.append("");// sindex
							sql.append("','");
							sql.append(value);// thevalue
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append(time);
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("");
							sql.append("')");
							dbmanager.addBatch(sql.toString());
						}
					}
				}

				// WINDOWS WMI cpuconfig
				if (allcpuconfighash_wmi != null && !allcpuconfighash_wmi.isEmpty()) {
					Iterator iterator = allcpuconfighash_wmi.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'cpuconfig'";
						dbmanager.addBatch(deleteSql);
						// 遍历每个ip对应的配置信息
						List cpuconfigList = (ArrayList) allcpuconfighash_wmi.get(ip);
						if (cpuconfigList == null || cpuconfigList.size() == 0) {
							continue;
						}
						for (int i = 0; i < cpuconfigList.size(); i++) {
							Hashtable tempHash = (Hashtable) cpuconfigList.get(i);
							Iterator tempIterator = tempHash.keySet().iterator();
							while (tempIterator.hasNext()) {
								String key = (String) tempIterator.next();
								String value = String.valueOf(tempHash.get(key));
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(nodeDTO.getType());// type
								sql.append("','");
								sql.append(nodeDTO.getSubtype());// subtype
								sql.append("','");
								sql.append("cpuconfig");// entity
								sql.append("','");
								sql.append(key);// subentity
								sql.append("','");
								sql.append(i);// sindex
								sql.append("','");
								sql.append(value);// thevalue
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append(time);
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("')");
								dbmanager.addBatch(sql.toString());
							}
						}
					}
				}

				// WINDOWS WMI physicaldisk
				if (allphysicaldiskhash != null && !allphysicaldiskhash.isEmpty()) {
					Iterator iterator = allphysicaldiskhash.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'physicaldisklist'";
						dbmanager.addBatch(deleteSql);
						// 遍历每个ip对应的配置信息
						List physicaldiskList = (ArrayList) allphysicaldiskhash.get(ip);
						if (physicaldiskList == null || physicaldiskList.size() == 0) {
							continue;
						}
						for (int i = 0; i < physicaldiskList.size(); i++) {
							Hashtable tempHash = (Hashtable) physicaldiskList.get(i);
							Iterator tempIterator = tempHash.keySet().iterator();
							while (tempIterator.hasNext()) {
								String key = (String) tempIterator.next();
								String value = String.valueOf(tempHash.get(key));
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(nodeDTO.getType());// type
								sql.append("','");
								sql.append(nodeDTO.getSubtype());// subtype
								sql.append("','");
								sql.append("physicaldisklist");// entity
								sql.append("','");
								sql.append(key);// subentity
								sql.append("','");
								sql.append(i);// sindex
								sql.append("','");
								sql.append(value);// thevalue
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append(time);
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("')");
								dbmanager.addBatch(sql.toString());
							}
						}
					}
				}

				// WINDOWS WMI hostinfo
				if (allhostinfoHash != null && !allhostinfoHash.isEmpty()) {
					Iterator iterator = allhostinfoHash.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'hostinfo'";
						dbmanager.addBatch(deleteSql);
						Hashtable hostinfoHash = (Hashtable) allhostinfoHash.get(ip);
						Iterator tempIterator = hostinfoHash.keySet().iterator();
						while (tempIterator.hasNext()) {
							String key = (String) tempIterator.next();
							String value = String.valueOf(hostinfoHash.get(key));
							StringBuffer sql = new StringBuffer();
							sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
							sql.append(nodeDTO.getId());
							sql.append("','");
							sql.append(ip);
							sql.append("','");
							sql.append(nodeDTO.getType());// type
							sql.append("','");
							sql.append(nodeDTO.getSubtype());// subtype
							sql.append("','");
							sql.append("hostinfo");// entity
							sql.append("','");
							sql.append(key);// subentity
							sql.append("','");
							sql.append("");// sindex
							sql.append("','");
							sql.append(value);// thevalue
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append(time);
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("");
							sql.append("')");
							dbmanager.addBatch(sql.toString());
						}
					}
				}

				// WINDOWS WMI memoryconfig
				if (allmemoryconfigHash != null && !allmemoryconfigHash.isEmpty()) {
					Iterator iterator = allmemoryconfigHash.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'memoryconfig'";
						dbmanager.addBatch(deleteSql);
						Hashtable memoryconfigHash = (Hashtable) allmemoryconfigHash.get(ip);
						Iterator tempIterator = memoryconfigHash.keySet().iterator();
						while (tempIterator.hasNext()) {
							String key = (String) tempIterator.next();
							String value = String.valueOf(memoryconfigHash.get(key));
							String unit = "";
							if (value.indexOf("M") != -1) {
								unit = "M";
								value = value.replace("M", "");
							}
							StringBuffer sql = new StringBuffer();
							sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
							sql.append(nodeDTO.getId());
							sql.append("','");
							sql.append(ip);
							sql.append("','");
							sql.append(nodeDTO.getType());// type
							sql.append("','");
							sql.append(nodeDTO.getSubtype());// subtype
							sql.append("','");
							sql.append("memoryconfig");// entity
							sql.append("','");
							sql.append(key);// subentity
							sql.append("','");
							sql.append("");// sindex
							sql.append("','");
							sql.append(value);// thevalue
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append(time);
							sql.append("','");
							sql.append(unit);// unit
							sql.append("','");
							sql.append("");// bak
							sql.append("')");
							dbmanager.addBatch(sql.toString());
						}
					}
				}

				// WINDOWS WMI 网络配置信息
				if (allnetworkconfighash != null && !allnetworkconfighash.isEmpty()) {
					Iterator iterator = allnetworkconfighash.keySet().iterator();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (iterator.hasNext()) {
						String ip = (String) iterator.next();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						// 清除历史信息
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'networkconfig'";
						dbmanager.addBatch(deleteSql);
						// 遍历每个ip对应的配置信息
						List networkconfigList = (ArrayList) allnetworkconfighash.get(ip);
						if (networkconfigList == null || networkconfigList.size() == 0) {
							continue;
						}
						for (int i = 0; i < networkconfigList.size(); i++) {
							Hashtable tempHash = (Hashtable) networkconfigList.get(i);
							Iterator tempIterator = tempHash.keySet().iterator();
							while (tempIterator.hasNext()) {
								String key = (String) tempIterator.next();
								String value = String.valueOf(tempHash.get(key));
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(nodeDTO.getType());// type
								sql.append("','");
								sql.append(nodeDTO.getSubtype());// subtype
								sql.append("','");
								sql.append("networkconfig");// entity
								sql.append("','");
								sql.append(key);// subentity
								sql.append("','");
								sql.append(i);// sindex
								sql.append("','");
								sql.append(value);// thevalue
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append(time);
								sql.append("','");
								sql.append("");
								sql.append("','");
								sql.append("");
								sql.append("')");
								dbmanager.addBatch(sql.toString());
							}
						}
					}
				}

				// 处理CPU入库
				if (allcpuhash != null && allcpuhash.size() > 0) {
					Enumeration tempEnumeration = allcpuhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector cpuVector = (Vector) allcpuhash.get(ip);
						// 得到CPU平均
						String deleteSql = "delete from nms_cpu_data_temp where nodeid='" + host.getId() + "' and entity = 'CPU'";
						dbmanager.addBatch(deleteSql);
						if (cpuVector != null && cpuVector.size() > 0) {
							for (int i = 0; i < cpuVector.size(); i++) {
								CpuCollectEntity vo = (CpuCollectEntity) cpuVector.elementAt(i);
								try {
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_cpu_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getCategory());
									sql.append("','");
									sql.append(vo.getEntity());
									sql.append("','");
									sql.append(vo.getSubentity());
									sql.append("','");
									sql.append(vo.getThevalue());
									sql.append("','");
									sql.append(vo.getChname());
									sql.append("','");
									sql.append(vo.getRestype());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getUnit());
									sql.append("','");
									sql.append(vo.getBak());
									sql.append("')");
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// 处理Cpu配置信息入库 nms_nodecpuconfig
				if (allcpuconfighash != null && allcpuconfighash.size() > 0) {
					Enumeration tempEnumeration = allcpuconfighash.keys();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List cpuconfiglist = (ArrayList) allcpuconfighash.get(ip);
						String deleteSql = "delete from nms_nodecpuconfig where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						// 得到CPU平均
						int index = 0;
						if (cpuconfiglist != null && cpuconfiglist.size() > 0) {
							for (int i = 0; i < cpuconfiglist.size(); i++) {
								Nodecpuconfig cpuconfig = (Nodecpuconfig) cpuconfiglist.get(i);
								if (cpuconfig.getProcessorSpeed() == null) {
									cpuconfig.setProcessorSpeed("");
								}
								if (cpuconfig.getProcessorType() == null) {
									cpuconfig.setProcessorType("");
								}
								try {
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_nodecpuconfig(nodeid,dataWidth,processorId,name,l2CacheSize,l2CacheSpeed,descrOfProcessors,processorType,processorSpeed)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(cpuconfig.getDataWidth());// dataWidth
									sql.append("','");
									sql.append(cpuconfig.getProcessorId());// processorId
									sql.append("','");
									sql.append(cpuconfig.getName());// name
									sql.append("','");
									sql.append(cpuconfig.getL2CacheSize());// l2CacheSize
									sql.append("','");
									sql.append(cpuconfig.getL2CacheSpeed());// l2CacheSpeed
									sql.append("','");
									sql.append(cpuconfig.getDescrOfProcessors());// descrOfProcessors
									sql.append("','");
									sql.append(cpuconfig.getProcessorType());// processorType
									sql.append("','");
									sql.append(cpuconfig.getProcessorSpeed());// processorSpeed
									sql.append("')");// unit
									// System.out.println(sql.toString());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// 处理CPU性能入库 nms_other_data_temp
				if (allcpuperfhash != null && allcpuperfhash.size() > 0) {
					Enumeration tempEnumeration = allcpuperfhash.keys();
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List cpuperflist = (ArrayList) allcpuperfhash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'cpuperflist'";
						dbmanager.addBatch(deleteSql);
						// 得到CPU平均
						int index = 0;
						if (cpuperflist != null && cpuperflist.size() > 0) {
							for (int i = 0; i < cpuperflist.size(); i++) {
								Hashtable hash = (Hashtable) cpuperflist.get(i);
								Enumeration en = hash.keys();
								while (en.hasMoreElements()) {
									index++;
									String key = (String) en.nextElement();
									String value = (String) hash.get(key);
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit)values('");
										sql.append(nodeDTO.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(nodeDTO.getType());
										sql.append("','");
										sql.append(nodeDTO.getSubtype());// subtype
										sql.append("','cpuperflist','");// entity
										sql.append(key);// subentity
										sql.append("','");
										sql.append(index);// sindex
										sql.append("','");
										sql.append(value);// thevalue
										sql.append("','");
										sql.append(key);// chname
										sql.append("','static','");// restype
										sql.append(time);// collecttime
										sql.append("','')");// unit
										// System.out.println(sql.toString());
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				// 处理内存入库
				if (allpmemoryhash != null && allpmemoryhash.size() > 0) {
					Enumeration tempEnumeration = allpmemoryhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector memoryVector = (Vector) allpmemoryhash.get(ip);
						String deleteSql = "delete from nms_memory_data_temp where nodeid='" + host.getId() + "' and entity='Memory'";
						dbmanager.addBatch(deleteSql);
						if (memoryVector != null && memoryVector.size() > 0) {
							for (int i = 0; i < memoryVector.size(); i++) {
								MemoryCollectEntity vo = (MemoryCollectEntity) memoryVector.elementAt(i);
								// if(!vo.getSubentity().equalsIgnoreCase("PhysicalMemory"))continue;
								Calendar tempCal = (Calendar) vo.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								try {
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getCategory());
									sql.append("','");
									sql.append(vo.getEntity());
									sql.append("','");
									sql.append(vo.getSubentity());
									sql.append("','");
									sql.append(vo.getThevalue());
									sql.append("','");
									sql.append(vo.getChname());
									sql.append("','");
									sql.append(vo.getRestype());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getUnit());
									sql.append("','");
									sql.append(vo.getBak());
									sql.append("')");
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// //处理虚拟内存入库
				// if(allvmemoryhash != null && allvmemoryhash.size()>0){
				// Enumeration vmhash = allvmemoryhash.keys();
				// while(vmhash.hasMoreElements()){
				// String ip = (String)vmhash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector memoryVector = (Vector)allvmemoryhash.get(ip);
				// String deleteSql = "delete from nms_memory_data_temp where
				// nodeid='" +host.getId() + "' and sindex='VirtualMemory'";
				// stmt.addBatch(deleteSql);
				// if(memoryVector != null && memoryVector.size()>0){
				// for(int i=0;i<memoryVector.size();i++){
				// Memorycollectdata vo = (Memorycollectdata)
				// memoryVector.elementAt(i);
				// if(!vo.getSubentity().equalsIgnoreCase("VirtualMemory"))continue;
				// Calendar tempCal = (Calendar) vo.getCollecttime();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// try {
				// StringBuffer sql = new StringBuffer(500);
				// sql.append("insert into
				// nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
				// sql.append(host.getId());
				// sql.append("','");
				// sql.append(ip);
				// sql.append("','");
				// sql.append(type);
				// sql.append("','");
				// sql.append(nodeDTO.getSubtype());
				// sql.append("','");
				// sql.append(vo.getCategory());
				// sql.append("','");
				// sql.append(vo.getEntity());
				// sql.append("','");
				// sql.append(vo.getSubentity());
				// sql.append("','");
				// sql.append(vo.getThevalue());
				// sql.append("','");
				// sql.append(vo.getChname());
				// sql.append("','");
				// sql.append(vo.getRestype());
				// sql.append("','");
				// sql.append(time);
				// sql.append("','");
				// sql.append(vo.getUnit());
				// sql.append("','");
				// sql.append(vo.getBak());
				// sql.append("')");
				// stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// 处理进程信息入库
				if (allprocesshash != null && allprocesshash.size() > 0) {
					Enumeration tempEnumeration = allprocesshash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector processVector = (Vector) allprocesshash.get(ip);
						String deleteSql = "delete from nms_process_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (processVector != null && processVector.size() > 0) {
							for (int i = 0; i < processVector.size(); i++) {
								ProcessCollectEntity vo = (ProcessCollectEntity) processVector.elementAt(i);
								// if (vo.getSubentity().equals("Physical
								// Memory") || vo.getSubentity().equals("Virtual
								// Memory")||
								// vo.getSubentity().trim().length()==0)continue;
								Calendar tempCal = (Calendar) vo.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								String thevalue = vo.getThevalue();
								if (thevalue != null) {
									thevalue = thevalue.replaceAll("\\\\", "/");
								}
								try {
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_process_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getCategory());
									sql.append("','");
									sql.append(vo.getEntity());
									sql.append("','");
									sql.append(vo.getSubentity());
									sql.append("','");
									sql.append(thevalue);
									sql.append("','");
									sql.append(vo.getChname());
									sql.append("','");
									sql.append(vo.getRestype());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getUnit());
									sql.append("','");
									sql.append(vo.getBak());
									sql.append("')");
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// 处理磁盘入库
				if (alldiskhash != null && alldiskhash.size() > 0) {
					Enumeration tempEnumeration = alldiskhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector diskVector = (Vector) alldiskhash.get(ip);
						String deleteSql = "delete from nms_disk_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (diskVector != null && diskVector.size() > 0) {
							for (int i = 0; i < diskVector.size(); i++) {
								DiskCollectEntity vo = (DiskCollectEntity) diskVector.elementAt(i);
								if (vo.getSubentity().equals("Physical Memory") || vo.getSubentity().equals("Virtual Memory") || vo.getSubentity().trim().length() == 0)
									continue;
								Calendar tempCal = (Calendar) vo.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								try {
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_disk_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getCategory());
									sql.append("','");
									sql.append(vo.getEntity());
									sql.append("','");
									sql.append(vo.getSubentity());
									sql.append("','");
									sql.append(vo.getThevalue());
									sql.append("','");
									sql.append(vo.getChname());
									sql.append("','");
									sql.append(vo.getRestype());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getUnit());
									sql.append("','");
									sql.append(vo.getBak());
									sql.append("')");
									// SysLogger.info("+++++++++++++++sindex--"+vo.getSubentity());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// List alldiskperf = (List) datahash.get("alldiskperf");
				// if (alldiskperf != null && alldiskperf.size() > 0) {
				// // Hashtable ipAllData = (Hashtable)
				// ShareData.getSharedata().get(ip);
				// // if (ipAllData == null)
				// // ipAllData = new Hashtable();
				// // ipAllData.put("alldiskperf", alldiskperf);
				// // ShareData.getSharedata().put(ip, ipAllData);
				// // TempDataService tempDataService = new TempDataService();
				// // tempDataService.deleteDiskPerfTempData(ip);
				// for (int i = 0; i < alldiskperf.size(); i++){
				// Hashtable diskperfhash = (Hashtable) alldiskperf.get(i);
				// try {
				// // tempDataService.collectHashData(ip, hash,
				// nodeDTO.getType(),nodeDTO.getSubtype(),"alldiskperf",i+1);
				// // NodeTemp vo = (NodeTemp)baseVo;
				// StringBuffer sql = new StringBuffer(500);
				// sql.append("insert into
				// nms_diskperf_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
				// sql.append(host.getId());
				// sql.append("','");
				// sql.append(vo.getIp());
				// sql.append("','");
				// sql.append(vo.getType());
				// sql.append("','");
				// sql.append(vo.getSubtype());
				// sql.append("','");
				// sql.append(vo.getEntity());
				// sql.append("','");
				// sql.append(vo.getSubentity());
				// sql.append("','");
				// sql.append(vo.getSindex());
				// sql.append("','");
				// sql.append(vo.getThevalue());
				// sql.append("','");
				// sql.append(vo.getChname());
				// sql.append("','");
				// sql.append(vo.getRestype());
				// sql.append("',to_date('");
				// sql.append(vo.getCollecttime());
				// sql.append("','YYYY-MM-DD HH24:MI:SS'),'");
				// sql.append(vo.getUnit());
				// sql.append("','");
				// sql.append(vo.getBak());
				// sql.append("')");
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				//			
				if (alldiskperfhash != null && alldiskperfhash.size() > 0) {
					Enumeration diskperfEnumeration = alldiskperfhash.keys();
					Date cc = new Date();
					String time = sdf.format(cc);
					while (diskperfEnumeration.hasMoreElements()) {
						String ip = (String) diskperfEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List diskperfList = (ArrayList) alldiskperfhash.get(ip);
						String deleteSql = "delete from nms_diskperf_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						for (int i = 0; i < diskperfList.size(); i++) {
							Hashtable diskprefHash = (Hashtable) diskperfList.get(i);
							try {
								Enumeration en = diskprefHash.keys();
								while (en.hasMoreElements()) {
									String key = (String) en.nextElement();
									String value = String.valueOf(diskprefHash.get(key));
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_diskperf_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','alldiskperf");// entity
									sql.append("','");
									sql.append(key);// subentity
									sql.append("','");
									sql.append(i + 1);// sindex
									sql.append("','");
									sql.append(value);// thevalue
									sql.append("','");
									sql.append(key);// chname
									sql.append("','static");// restype
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("',' ',' ')");// unit
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (alldiskioHash != null && alldiskioHash.size() > 0) {
					Enumeration diskioEnumeration = alldiskioHash.keys();
					Date cc = new Date();
					String time = sdf.format(cc);
					while (diskioEnumeration.hasMoreElements()) {
						String ip = (String) diskioEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List diskioList = (ArrayList) alldiskioHash.get(ip);
						String deleteSql = "delete from nms_diskio_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						for (int i = 0; i < diskioList.size(); i++) {
							Hashtable diskioHash = (Hashtable) diskioList.get(i);
							try {
								Enumeration en = diskioHash.keys();
								while (en.hasMoreElements()) {
									String key = (String) en.nextElement();
									String value = String.valueOf(diskioHash.get(key));
									StringBuffer sql = new StringBuffer();
									sql.append("insert into nms_diskio_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','alldiskio");// entity
									sql.append("','");
									sql.append(key);// subentity
									sql.append("','");
									sql.append(i + 1);// sindex
									sql.append("','");
									sql.append(value);// thevalue
									sql.append("','");
									sql.append(key);// chname
									sql.append("','static");// restype
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("',' ',' ')");// unit
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 用户信息入库
				if (alluserhash != null && alluserhash.size() > 0) {
					Enumeration tempEnumeration = alluserhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector userVector = (Vector) alluserhash.get(ip);
						String deleteSql = "delete from nms_user_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (userVector != null && userVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < userVector.size(); i++) {
								Usercollectdata vo = (Usercollectdata) userVector.elementAt(i);
								try {
									// if(vo.getName() == null)vo.setName("");
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_user_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','");
									sql.append(vo.getCategory());// entity
									sql.append("','");
									sql.append(vo.getEntity());// subentity
									sql.append("','");
									sql.append(vo.getSubentity().trim());// sindex
									sql.append("','");
									sql.append(vo.getThevalue());// thevalue
									sql.append("','");
									sql.append(vo.getChname());
									sql.append("','");
									sql.append(vo.getRestype());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getUnit());
									sql.append("','");
									sql.append(vo.getBak());
									sql.append("')");
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// 处理errpt信息入库
				if (allerrpthash != null && allerrpthash.size() > 0) {
					Enumeration tempEnumeration = allerrpthash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector errptVector = (Vector) allerrpthash.get(ip);
						String deleteSql = "delete from nms_errptlog where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (errptVector != null && errptVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < errptVector.size(); i++) {
								Errptlog vo = (Errptlog) errptVector.get(i);
								try {
									// if(vo.getName() == null)vo.setName("");
									StringBuffer sql = new StringBuffer(100);
									sql.append("insert into nms_errptlog(labels,identifier,collettime,seqnumber,nodeid,machineid,errptclass,errpttype,resourcename,resourceclass,resourcetype,locations,vpd,descriptions,hostid) values('");
									sql.append(vo.getLabels());
									sql.append("','");
									sql.append(vo.getIdentifier());
									sql.append("','");
									sql.append(time);
									sql.append("','");
									sql.append(vo.getSeqnumber());
									sql.append("','");
									sql.append(vo.getNodeid());
									sql.append("','");
									sql.append(vo.getMachineid());
									sql.append("','");
									sql.append(vo.getErrptclass());
									sql.append("','");
									sql.append(vo.getErrpttype());
									sql.append("','");
									sql.append(vo.getResourcename());
									sql.append("','");
									sql.append(vo.getResourceclass());
									sql.append("','");
									sql.append(vo.getRescourcetype());
									sql.append("','");
									sql.append(vo.getLocations());
									sql.append("','");
									sql.append(vo.getVpd());
									sql.append("','");
									sql.append(vo.getDescriptions());
									sql.append("','");
									sql.append(vo.getHostid());
									sql.append("')");
									// System.out.println(sql.toString());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// END
				// 处理端口流速信息入库
				if (allutilhdxhash != null && allutilhdxhash.size() > 0) {
					Enumeration tempEnumeration = allutilhdxhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector utilhdxVector = (Vector) allutilhdxhash.get(ip);
						String deleteSql = "delete from nms_interface_data_temp where nodeid='" + host.getId() + "' and subentity in ('InBandwidthUtilHdx','OutBandwidthUtilHdx')";
						dbmanager.addBatch(deleteSql);
						if (utilhdxVector != null && utilhdxVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < utilhdxVector.size(); i++) {
								UtilHdx vo = (UtilHdx) utilhdxVector.elementAt(i);
								try {
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_interface_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());// type
									sql.append("','");
									sql.append(nodeDTO.getSubtype());// subtype
									sql.append("','");
									sql.append(vo.getCategory());// entity
									sql.append("','");
									sql.append(vo.getEntity());// subentity
									sql.append("','");
									sql.append(vo.getSubentity());// sindex
									sql.append("','");
									sql.append(vo.getThevalue());// thevalue
									sql.append("','");
									sql.append(vo.getChname());// chname
									sql.append("','");
									sql.append(vo.getRestype());// restype
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("','','");// unit
									sql.append(vo.getBak());// bak
									sql.append("')");
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// 处理网卡配置信息
				if (allnetmediahash != null && allnetmediahash.size() > 0) {
					Enumeration tempEnumeration = allnetmediahash.keys();
					Date cc = new Date();
					String time = sdf.format(cc);
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List netmediaList = (ArrayList) allnetmediahash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'netmedialist'";
						dbmanager.addBatch(deleteSql);
						for (int i = 0; i < netmediaList.size(); i++) {
							Hashtable netmediaHash = (Hashtable) netmediaList.get(i);
							try {
								Enumeration en = netmediaHash.keys();
								while (en.hasMoreElements()) {
									String key = (String) en.nextElement();
									String value = (String) netmediaHash.get(key);
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());
									sql.append("','");
									sql.append(nodeDTO.getSubtype());
									sql.append("','netmedialist','");// entity
									sql.append(key);// subentity
									sql.append("','");
									sql.append(i + 1);// sindex
									sql.append("','");
									sql.append(value);// thevalue
									sql.append("','");
									sql.append(key);// chname
									sql.append("','static");// restype
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("',' ',' ')");// unit
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}// END
				// 处理页面信息入库
				if (allpagehash != null && allpagehash.size() > 0) {
					Enumeration tempEnumeration = allpagehash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Hashtable pageHashtable = (Hashtable) allpagehash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'pagehash'";
						dbmanager.addBatch(deleteSql);
						Enumeration en = pageHashtable.keys();
						int sindex = 0;
						while (en.hasMoreElements()) {
							sindex++;
							String key = (String) en.nextElement();
							String value = (String) pageHashtable.get(key);
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							try {
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(nodeDTO.getType());
								sql.append("','");
								sql.append(nodeDTO.getSubtype());
								sql.append("','pagehash','");// entity
								sql.append(key);// subentity
								sql.append("','");
								sql.append(sindex);// sindex
								sql.append("','");
								sql.append(value);// thevalue
								sql.append("','");
								sql.append(key);// chname
								sql.append("','static");// restype
								sql.append("','");
								sql.append(time);// collecttime
								sql.append("',' ',' ')");// unit
								dbmanager.addBatch(sql.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 处理Paging Space利用率信息入库
				if (allpaginghash != null && allpaginghash.size() > 0) {
					Enumeration tempEnumeration = allpaginghash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Hashtable pagingHashtable = (Hashtable) allpaginghash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'paginghash'";
						dbmanager.addBatch(deleteSql);
						Enumeration en = pagingHashtable.keys();
						int sindex = 0;
						while (en.hasMoreElements()) {
							sindex++;
							String key = (String) en.nextElement();
							String value = (String) pagingHashtable.get(key);
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							try {
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(ip);
								sql.append("','");
								sql.append(nodeDTO.getType());
								sql.append("','");
								sql.append(nodeDTO.getSubtype());
								sql.append("','paginghash','");// entity
								sql.append(key);// subentity
								sql.append("','");
								sql.append(sindex);// sindex
								sql.append("','");
								sql.append(value);// thevalue
								sql.append("','");
								sql.append(key);// chname
								sql.append("','static");// restype
								sql.append("','");
								sql.append(time);// collecttime
								sql.append("',' ',' ')");// unit
								dbmanager.addBatch(sql.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				// 处理系统信息入库
				if (allsystemhash != null && allsystemhash.size() > 0) {
					Enumeration tempEnumeration = allsystemhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector systemVector = (Vector) allsystemhash.get(ip);
						String deleteSql = "delete from nms_system_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (systemVector != null && systemVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < systemVector.size(); i++) {
								SystemCollectEntity vo = (SystemCollectEntity) systemVector.elementAt(i);
								try {
									// if(vo.getName() == null)vo.setName("");
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_system_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());// type
									sql.append("','");
									sql.append(nodeDTO.getSubtype());// subtype
									sql.append("','");
									sql.append(vo.getCategory());// entity
									sql.append("','");
									sql.append(vo.getEntity());// subentity
									sql.append("','");
									sql.append(vo.getSubentity());// sindex
									sql.append("','");
									sql.append(vo.getThevalue());// thevalue
									sql.append("','");
									sql.append(vo.getChname());// chname
									sql.append("','");
									sql.append(vo.getRestype());// restype
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("','','");// unit
									sql.append(vo.getBak());// bak
									sql.append("')");
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

				// 处理端口信息入库 interface
				if (allinterfacehash != null && allinterfacehash.size() > 0) {
					Enumeration tempEnumeration = allinterfacehash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector interfaceVector = (Vector) allinterfacehash.get(ip);
						String deleteSql = "delete from nms_interface_data_temp where nodeid='" + host.getId() + "' and subentity not in('InBandwidthUtilHdx','OutBandwidthUtilHdx')";
						dbmanager.addBatch(deleteSql);
						if (interfaceVector != null && interfaceVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < interfaceVector.size(); i++) {
								Interfacecollectdata vo = (Interfacecollectdata) interfaceVector.elementAt(i);
								try {
									// if(vo.getName() == null)vo.setName("");
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_interface_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());// type
									sql.append("','");
									sql.append(nodeDTO.getSubtype());// subtype
									sql.append("','");
									sql.append(vo.getCategory());// entity
									sql.append("','");
									sql.append(vo.getEntity());// subentity
									sql.append("','");
									sql.append(vo.getSubentity());// sindex
									sql.append("','");
									sql.append(vo.getThevalue());// thevalue
									sql.append("','");
									sql.append(vo.getChname());// chname
									sql.append("','");
									sql.append(vo.getRestype());// restype
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("','','");// unit
									sql.append(vo.getBak());// bak
									sql.append("')");
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// 处理路由信息入库
				if (allroutehash != null && allroutehash.size() > 0) {
					Enumeration tempEnumeration = allroutehash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List routeList = (ArrayList) allroutehash.get(ip);
						String deleteSql = "delete from nms_route_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (routeList != null && routeList.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < routeList.size(); i++) {
								try {
									// if(vo.getName() == null)vo.setName("");
									String routeValue = String.valueOf(routeList.get(i));
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_route_data_temp(nodeid,ip,type,subtype,ifindex,rtype,collecttime)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());// type
									sql.append("','");
									sql.append(nodeDTO.getSubtype());// subtype
									sql.append("','");
									sql.append(i + 1);// ifindex
									sql.append("','");
									sql.append(routeValue);// rtype
									// 注意：将路由信息存到rtype中
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("')");
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// 数据采集时间collecttime
				if (allcollecttimehash != null && allcollecttimehash.size() > 0) {
					Enumeration tempEnumeration = allcollecttimehash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						String collecttime = (String) allcollecttimehash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'collecttime'";
						dbmanager.addBatch(deleteSql);
						try {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							StringBuffer sql = new StringBuffer(500);
							sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,thevalue,collecttime)values('");
							sql.append(nodeDTO.getId());
							sql.append("','");
							sql.append(ip);
							sql.append("','");
							sql.append(nodeDTO.getType());// type
							sql.append("','");
							sql.append(nodeDTO.getSubtype());// subtype
							sql.append("','collecttime','");// entity
							sql.append(collecttime);// thevalue数据采集时间
							sql.append("','");
							sql.append(time);// collecttime数据保存时间
							sql.append("')");
							// System.out.println(sql.toString());
							dbmanager.addBatch(sql.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// 处理volume信息入库
				if (allvolumehash != null && allvolumehash.size() > 0) {
					Enumeration tempEnumeration = allvolumehash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Vector volumeVector = (Vector) allvolumehash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'volume'";
						dbmanager.addBatch(deleteSql);
						if (volumeVector != null && volumeVector.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < volumeVector.size(); i++) {
								Hashtable volumeItemHash = (Hashtable) volumeVector.elementAt(i);
								Enumeration tempEnumeration2 = volumeItemHash.keys();
								while (tempEnumeration2.hasMoreElements()) {
									String key = (String) tempEnumeration2.nextElement();
									String value = (String) volumeItemHash.get(key);
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,collecttime)values('");
										sql.append(nodeDTO.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(nodeDTO.getType());// type
										sql.append("','");
										sql.append(nodeDTO.getSubtype());// subtype
										sql.append("','");
										sql.append("volume");// entity
										sql.append("','");
										sql.append(key);// subentity
										sql.append("','");
										sql.append(i + 1);// sindex
										sql.append("','");
										sql.append(value);// thevalue
										sql.append("','");
										sql.append(time);// collecttime
										sql.append("')");
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}

				// 处理iflist信息入库
				if (allifhash != null && allifhash.size() > 0) {
					Enumeration tempEnumeration = allifhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List ifList = (ArrayList) allifhash.get(ip);
						String deleteSql = "delete from nms_other_data_temp where nodeid='" + host.getId() + "' and entity = 'iflist'";
						dbmanager.addBatch(deleteSql);
						if (ifList != null && ifList.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < ifList.size(); i++) {
								Hashtable ifItemHash = (Hashtable) ifList.get(i);
								Enumeration tempEnumeration2 = ifItemHash.keys();
								while (tempEnumeration2.hasMoreElements()) {
									String key = (String) tempEnumeration2.nextElement();
									String value = (String) ifItemHash.get(key);
									try {
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,collecttime)values('");
										sql.append(nodeDTO.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(nodeDTO.getType());// type
										sql.append("','");
										sql.append(nodeDTO.getSubtype());// subtype
										sql.append("','");
										sql.append("iflist");// entity
										sql.append("','");
										sql.append(key);// subentity
										sql.append("','");
										sql.append(i + 1);// sindex
										sql.append("','");
										sql.append(value);// thevalue
										sql.append("','");
										sql.append(time);// collecttime
										sql.append("')");
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				// 处理servicelist信息入库
				// nms_service_data_temp的表的字段名和集合中的key
				// 不一致，因此未将改servicelist信息存入nms_service_data_temp表中
				// if(allservicehash != null && allservicehash.size()>0){
				// Enumeration tempEnumeration = allservicehash.keys();
				// while(tempEnumeration.hasMoreElements()){
				// String ip = (String)tempEnumeration.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// List serviceList = (ArrayList)allservicehash.get(ip);
				// String deleteSql = "delete from nms_other_data_temp where
				// nodeid='" +host.getId() + "' and entity = 'servicelist'";
				// stmt.addBatch(deleteSql);
				// if(serviceList != null && serviceList.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<serviceList.size();i++){
				// Hashtable serviceItemHash = (Hashtable) serviceList.get(i);
				// Enumeration tempEnumeration2 = serviceItemHash.keys();
				// while(tempEnumeration2.hasMoreElements()){
				// String key = (String)tempEnumeration2.nextElement();
				// String value = (String)serviceItemHash.get(key);
				// if(value != null && value.indexOf("\\") != -1){
				// value = value.replaceAll("\\\\", "/");
				// }
				// try {
				// StringBuffer sql = new StringBuffer(500);
				// sql.append("insert into
				// nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,collecttime)values('");
				// sql.append(nodeDTO.getId());
				// sql.append("','");
				// sql.append(ip);
				// sql.append("','");
				// sql.append(nodeDTO.getType());//type
				// sql.append("','");
				// sql.append(nodeDTO.getSubtype());//subtype
				// sql.append("','");
				// sql.append("servicelist");//entity
				// sql.append("','");
				// sql.append(key);//subentity
				// sql.append("','");
				// sql.append(i+1);//sindex
				// sql.append("','");
				// sql.append(value);//thevalue
				// sql.append("','");
				// sql.append(time);//collecttime
				// sql.append("')");
				// stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }
				// }
				// nms_service_data_temp的表的字段名和集合中的key
				// 不一致，因此未将改servicelist信息存入nms_service_data_temp表中
				if (allservicehash != null && allservicehash.size() > 0) {
					Enumeration tempEnumeration = allservicehash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List serviceList = (ArrayList) allservicehash.get(ip);
						String deleteSql = "delete from nms_sercice_data_temp where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (serviceList != null && serviceList.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < serviceList.size(); i++) {
								Hashtable serviceItemHash = (Hashtable) serviceList.get(i);
								Enumeration tempEnumeration2 = serviceItemHash.keys();
								ServiceNodeTemp serviceNodeTemp = null;
								serviceNodeTemp = getServiceNodeTempByHashtable(serviceItemHash);
								if (serviceNodeTemp.getPathName() != null && serviceNodeTemp.getPathName().trim().length() > 0) {
									serviceNodeTemp.setPathName(serviceNodeTemp.getPathName().replaceAll("\"", ""));
								}
								try {
									StringBuffer sql = new StringBuffer(500);
									sql.append("insert into nms_sercice_data_temp(nodeid,ip,type,subtype,name,instate,opstate,paused,uninst,collecttime,startMode,pathName,description,serviceType,pid,groupstr)values('");
									sql.append(nodeDTO.getId());
									sql.append("','");
									sql.append(ip);
									sql.append("','");
									sql.append(nodeDTO.getType());// type
									sql.append("','");
									sql.append(nodeDTO.getSubtype());// subtype
									sql.append("','");
									sql.append(serviceNodeTemp.getName());// name
									sql.append("','");
									sql.append(serviceNodeTemp.getInstate());// instate
									sql.append("','");
									sql.append(serviceNodeTemp.getOpstate());// opstate
									sql.append("','");
									sql.append(serviceNodeTemp.getPaused());// paused
									sql.append("','");
									sql.append(serviceNodeTemp.getUninst());// uninst
									sql.append("','");
									sql.append(time);// collecttime
									sql.append("','");
									sql.append(serviceNodeTemp.getStartMode());// startMode
									sql.append("',\"");
									sql.append(serviceNodeTemp.getPathName());// pathName
									sql.append("\",'");
									sql.append(serviceNodeTemp.getDescription());// description
									sql.append("','");
									sql.append(serviceNodeTemp.getServiceType());// serviceType
									sql.append("','");
									sql.append(serviceNodeTemp.getPid());// pid
									sql.append("','");
									sql.append(serviceNodeTemp.getGroupstr());// groupstr
									sql.append("')");
									// SysLogger.info(sql.toString());
									dbmanager.addBatch(sql.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// 处理nodeconfig信息入库
				if (allnodeconfighash != null && allnodeconfighash.size() > 0) {
					Enumeration tempEnumeration = allnodeconfighash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						Nodeconfig nodeconfig = (Nodeconfig) allnodeconfighash.get(ip);
						String deleteSql = "delete from nms_nodeconfig where nodeid='" + host.getId() + "'";
						dbmanager.addBatch(deleteSql);
						if (nodeconfig != null) {
							try {
								StringBuffer sql = new StringBuffer(500);
								sql.append("insert into nms_nodeconfig(nodeid,hostname,sysname,serialNumber,cSDVersion,numberOfProcessors,mac)values('");
								sql.append(nodeDTO.getId());
								sql.append("','");
								sql.append(nodeconfig.getHostname());// hostname
								sql.append("','");
								sql.append(nodeconfig.getSysname());// sysname
								sql.append("','");
								sql.append(nodeconfig.getSerialNumber());// serialNumber
								sql.append("','");
								sql.append(nodeconfig.getCSDVersion());// cSDVersion
								sql.append("','");
								sql.append(nodeconfig.getNumberOfProcessors());// numberOfProcessors
								sql.append("','");
								sql.append(nodeconfig.getMac());// mac
								sql.append("')");
								// System.out.println(sql.toString());
								dbmanager.addBatch(sql.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 处理 netflowlist信息入库
				if (allnetflowhash != null && allnetflowhash.size() > 0) {
					Enumeration tempEnumeration = allnetflowhash.keys();
					while (tempEnumeration.hasMoreElements()) {
						String ip = (String) tempEnumeration.nextElement();
						Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
						List netflowList = (ArrayList) allnetflowhash.get(ip);
						String deleteSql = "delete from nms_interface_data_temp where nodeid='" + host.getId() + "' and subentity = 'netflowlist'";
						dbmanager.addBatch(deleteSql);
						if (netflowList != null && netflowList.size() > 0) {
							Calendar tempCal = Calendar.getInstance();
							Date cc = tempCal.getTime();
							String time = sdf.format(cc);
							for (int i = 0; i < netflowList.size(); i++) {
								Hashtable netflowHashtable = (Hashtable) netflowList.get(i);
								Enumeration enumeration = netflowHashtable.keys();
								while (enumeration.hasMoreElements()) {
									String key = (String) enumeration.nextElement();
									String value = (String) netflowHashtable.get(key);
									try {
										// if(vo.getName() ==
										// null)vo.setName("");
										StringBuffer sql = new StringBuffer(500);
										sql.append("insert into nms_interface_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
										sql.append(nodeDTO.getId());
										sql.append("','");
										sql.append(ip);
										sql.append("','");
										sql.append(nodeDTO.getType());// type
										sql.append("','");
										sql.append(nodeDTO.getSubtype());// subtype
										sql.append("','");
										sql.append("netflowlist");// entity
										sql.append("','");
										sql.append(key);// subentity
										sql.append("','");
										sql.append(i + "");// sindex
										sql.append("','");
										sql.append(value);// thevalue
										sql.append("','");
										sql.append(key);// chname
										sql.append("','");
										sql.append("");// restype
										sql.append("','");
										sql.append(time);// collecttime
										sql.append("','','");// unit
										sql.append("");// bak
										sql.append("')");
										// SysLogger.info(sql.toString());
										dbmanager.addBatch(sql.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				// //处理存储信息入库
				// if(allstoragehash != null && allstoragehash.size()>0){
				// Enumeration storagehash = allstoragehash.keys();
				// while(storagehash.hasMoreElements()){
				// String ip = (String)storagehash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector storageVector = (Vector)allstoragehash.get(ip);
				// String deleteSql = "delete from nms_storage_data_temp where
				// nodeid='" +host.getId() + "'";
				// stmt.addBatch(deleteSql);
				// if(storageVector != null && storageVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<storageVector.size();i++){
				// Storagecollectdata vo = (Storagecollectdata)
				// storageVector.elementAt(i);
				// try {
				// if(vo.getName() == null)vo.setName("");
				// StringBuffer sql = new StringBuffer(500);
				// sql.append("insert into
				// nms_storage_data_temp(nodeid,ip,type,subtype,name,stype,cap,storageindex,collecttime)values('");
				// sql.append(host.getId());
				// sql.append("','");
				// sql.append(ip);
				// sql.append("','");
				// sql.append(type);
				// sql.append("','");
				// sql.append(nodeDTO.getSubtype());
				// sql.append("','");
				// sql.append(vo.getName().replace("\\", "/"));
				// sql.append("','");
				// sql.append(vo.getType());
				// sql.append("','");
				// sql.append(vo.getCap());
				// sql.append("','");
				// sql.append(vo.getStorageindex());
				// sql.append("','");
				// sql.append(time);
				// sql.append("')");
				// stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// //处理硬件信息入库
				// if(allhardwarehash != null && allhardwarehash.size()>0){
				// Enumeration hardwarehash = allhardwarehash.keys();
				// while(hardwarehash.hasMoreElements()){
				// String ip = (String)hardwarehash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector hardwareVector = (Vector)allhardwarehash.get(ip);
				// String deleteSql = "delete from nms_device_data_temp where
				// nodeid='" +host.getId() + "'";
				// stmt.addBatch(deleteSql);
				// if(hardwareVector != null && hardwareVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<hardwareVector.size();i++){
				// Devicecollectdata vo = (Devicecollectdata)
				// hardwareVector.elementAt(i);
				// try {
				// StringBuffer sql = new StringBuffer(500);
				// sql.append("insert into
				// nms_device_data_temp(nodeid,ip,type,subtype,name,deviceindex,dtype,status,collecttime)values('");
				// sql.append(host.getId());
				// sql.append("','");
				// sql.append(ip);
				// sql.append("','");
				// sql.append(type);
				// sql.append("','");
				// sql.append(nodeDTO.getSubtype());
				// sql.append("','");
				// sql.append(vo.getName().replace("\\", "/"));
				// sql.append("','");
				// sql.append(vo.getDeviceindex());
				// sql.append("','");
				// sql.append(vo.getType());
				// sql.append("','");
				// sql.append(vo.getStatus());
				// sql.append("','");
				// sql.append(time);
				// sql.append("')");
				// stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// //处理软件信息入库
				// if(allsoftwarehash != null && allsoftwarehash.size()>0){
				// String softwaresql = "insert into nms_software_data_temp"
				// +
				// "(nodeid,ip,type,subtype,insdate,name,stype,swid,collecttime)
				// "
				// + "values(?,?,?,?,?,?,?,?,?)";
				// softwareps = conn.prepareStatement(softwaresql);
				// Enumeration softwarehash = allsoftwarehash.keys();
				// while(softwarehash.hasMoreElements()){
				// String ip = (String)softwarehash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector softwareVector = (Vector)allsoftwarehash.get(ip);
				// String deleteSql = "delete from nms_software_data_temp where
				// nodeid='" +host.getId() + "'";
				// stmt.addBatch(deleteSql);
				// if(softwareVector != null && softwareVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<softwareVector.size();i++){
				// Softwarecollectdata vo = (Softwarecollectdata)
				// softwareVector.elementAt(i);
				// try {
				// softwareps.setString(1, host.getId()+"");
				// softwareps.setString(2, ip);
				// softwareps.setString(3, type);
				// softwareps.setString(4, nodeDTO.getSubtype());
				// softwareps.setString(5, vo.getInsdate());
				// softwareps.setString(6, vo.getName().replace("'", ""));
				// softwareps.setString(7, vo.getType());
				// softwareps.setString(8, vo.getSwid());
				// softwareps.setString(9, time);
				// softwareps.addBatch();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// //处理服务信息入库
				// if(allservicehash != null && allservicehash.size()>0){
				// String servicesql = "insert into nms_sercice_data_temp"
				// +
				// "(nodeid,ip,type,subtype,name,instate,opstate,paused,uninst,collecttime)
				// "
				// + "values(?,?,?,?,?,?,?,?,?,?)";
				// serviceps = conn.prepareStatement(servicesql);
				// Enumeration servicehash = allservicehash.keys();
				// while(servicehash.hasMoreElements()){
				// String ip = (String)servicehash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector serviceVector = (Vector)allservicehash.get(ip);
				// String deleteSql = "delete from nms_sercice_data_temp where
				// nodeid='" +host.getId() + "'";
				// stmt.addBatch(deleteSql);
				// if(serviceVector != null && serviceVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<serviceVector.size();i++){
				// Servicecollectdata vo = (Servicecollectdata)
				// serviceVector.elementAt(i);
				// try {
				// serviceps.setString(1, host.getId()+"");
				// serviceps.setString(2, ip);
				// serviceps.setString(3, type);
				// serviceps.setString(4, nodeDTO.getSubtype());
				// serviceps.setString(5, new
				// String(vo.getName().getBytes(),"UTF-8"));
				// serviceps.setString(6, vo.getInstate());
				// serviceps.setString(7, vo.getOpstate());
				// serviceps.setString(8, vo.getPaused());
				// serviceps.setString(9, vo.getUninst());
				// serviceps.setString(10, time);
				// serviceps.addBatch();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// String sql = "insert into nms_interface_data_temp"
				// +
				// "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)
				// "
				// + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				// ps = conn.prepareStatement(sql);
				//			
				// Hashtable hasdeletedHash = new Hashtable();
				// //处理入口数据包信息入库
				// if(allinpackshash != null && allinpackshash.size()>0){
				// Enumeration inpackshash = allinpackshash.keys();
				// while(inpackshash.hasMoreElements()){
				// String ip = (String)inpackshash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector inpacksVector = (Vector)allinpackshash.get(ip);
				// String deleteSql = "delete from nms_interface_data_temp where
				// nodeid='" +host.getId() + "'";
				// hasdeletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// if(inpacksVector != null && inpacksVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<inpacksVector.size();i++){
				// InPkts vo = (InPkts) inpacksVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// //处理出口数据包信息入库
				// if(alloutpackshash != null && alloutpackshash.size()>0){
				// Enumeration outpackshash = alloutpackshash.keys();
				// while(outpackshash.hasMoreElements()){
				// String ip = (String)outpackshash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector outpacksVector = (Vector)alloutpackshash.get(ip);
				// String deleteSql = "delete from nms_interface_data_temp where
				// nodeid='" +host.getId() + "'";
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!hasdeletedHash.containsKey(host.getId()+"")){
				// hasdeletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// if(outpacksVector != null && outpacksVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<outpacksVector.size();i++){
				// OutPkts vo = (OutPkts) outpacksVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// //处理IPMAC信息入库
				// if(allipmachash != null && allipmachash.size()>0){
				// Enumeration ipmachash = allipmachash.keys();
				// while(ipmachash.hasMoreElements()){
				// String ip = (String)ipmachash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector ipmacVector = (Vector)allipmachash.get(ip);
				// String deleteSql = "delete from ipmac where relateipaddr='" +
				// ip + "'";
				// stmt.addBatch(deleteSql);
				// if(ipmacVector != null && ipmacVector.size()>0){
				// for(int i=0;i<ipmacVector.size();i++){
				// IpMac ipmac = (IpMac) ipmacVector.elementAt(i);
				// try {
				// String _sql = "";
				// String time = sdf.format(ipmac.getCollecttime().getTime());
				// _sql = "insert into
				// ipmac(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms)values('";
				// _sql = _sql + ipmac.getRelateipaddr() + "','" +
				// ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
				// _sql = _sql + new String(ipmac.getMac().getBytes(),"UTF-8") +
				// "','" + time + "','" + ipmac.getIfband() + "','" +
				// ipmac.getIfsms() + "')";
				// stmt.addBatch(_sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }

				// 处理接口信息入库
				// if(allinterfacehash != null && allinterfacehash.size()>0){
				// Enumeration interfacehash = allinterfacehash.keys();
				// while(interfacehash.hasMoreElements()){
				// String ip = (String)interfacehash.nextElement();
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Hashtable _interfacehash =
				// (Hashtable)allinterfacehash.get(ip);
				// Hashtable deletedHash = new Hashtable();
				//		 			
				// Vector utilhdxVector = (Vector)
				// _interfacehash.get("utilhdx");
				// String deleteSql = "delete from nms_interface_data_temp where
				// nodeid='" +host.getId() + "'";
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!deletedHash.containsKey(host.getId()+"")){
				// deletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!hasdeletedHash.containsKey(host.getId()+"")){
				// hasdeletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// if(utilhdxVector != null && utilhdxVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<utilhdxVector.size();i++){
				// UtilHdx vo = (UtilHdx) utilhdxVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				//					
				//					
				// Vector utilhdxpercVector = (Vector)
				// _interfacehash.get("utilhdxperc");
				// if(utilhdxpercVector != null && utilhdxpercVector.size()>0){
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// if(!deletedHash.containsKey(host.getId()+"")){
				// deletedHash.put(host.getId()+"", host.getId()+"");
				// stmt.addBatch(deleteSql);
				// }
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<utilhdxpercVector.size();i++){
				// UtilHdxPerc vo = (UtilHdxPerc)
				// utilhdxpercVector.elementAt(i);
				// try {
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, vo.getCategory());
				// ps.setString(6, vo.getEntity());
				// ps.setString(7, vo.getSubentity());
				// ps.setString(8, vo.getThevalue());
				// ps.setString(9, vo.getChname());
				// ps.setString(10, vo.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, vo.getUnit());
				// ps.setString(13, vo.getBak());
				// ps.addBatch();
				// //stmt.addBatch(sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				//					
				// //开始设置流速
				// Vector interfaceVector =
				// (Vector)_interfacehash.get("interface");
				// if (interfaceVector != null && interfaceVector.size() > 0) {
				// try{
				// for (int i = 0; i < interfaceVector.size(); i++) {
				// Interfacecollectdata interfacedata = (Interfacecollectdata)
				// interfaceVector.elementAt(i);
				// Calendar tempCal = (Calendar) interfacedata.getCollecttime();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// ps.setString(1, host.getId()+"");
				// ps.setString(2, ip);
				// ps.setString(3, type);
				// ps.setString(4, nodeDTO.getSubtype());
				// ps.setString(5, interfacedata.getCategory());
				// ps.setString(6, interfacedata.getEntity());
				// ps.setString(7, interfacedata.getSubentity());
				// ps.setString(8, interfacedata.getThevalue());
				// ps.setString(9, interfacedata.getChname());
				// ps.setString(10, interfacedata.getRestype());
				// ps.setString(11, time);
				// ps.setString(12, interfacedata.getUnit());
				// ps.setString(13, interfacedata.getBak());
				// ps.addBatch();
				// }
				// }catch(Exception e){
				// e.printStackTrace();
				// }
				// }
				// //结束设置流速
				// }
				// }

				// //处理系统组信息入库
				// if(allsystemgrouphash != null &&
				// allsystemgrouphash.size()>0){
				// Enumeration systemgrouphash = allsystemgrouphash.keys();
				// while(systemgrouphash.hasMoreElements()){
				// String ip = (String)systemgrouphash.nextElement();
				// //SysLogger.info(ip+"==========system");
				// Host host =
				// (Host)PollingEngine.getInstance().getNodeByIP(ip);
				// NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
				// Vector systemgroupVector =
				// (Vector)allsystemgrouphash.get(ip);
				// String deleteSql = "delete from nms_system_data_temp where
				// nodeid='" +host.getId() + "'";
				// //判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
				// stmt.addBatch(deleteSql);
				// if(systemgroupVector != null && systemgroupVector.size()>0){
				// Calendar tempCal=Calendar.getInstance();
				// Date cc = tempCal.getTime();
				// String time = sdf.format(cc);
				// for(int i=0;i<systemgroupVector.size();i++){
				// Systemcollectdata vo = (Systemcollectdata)
				// systemgroupVector.elementAt(i);
				// try {
				// StringBuffer _sql = new StringBuffer(500);
				// _sql.append("insert into
				// nms_system_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
				// _sql.append(host.getId());
				// _sql.append("','");
				// _sql.append(ip);
				// _sql.append("','");
				// _sql.append(type);
				// _sql.append("','");
				// _sql.append(nodeDTO.getSubtype());
				// _sql.append("','");
				// _sql.append(vo.getCategory());
				// _sql.append("','");
				// _sql.append(vo.getEntity());
				// _sql.append("','");
				// _sql.append(vo.getSubentity());
				// _sql.append("','");
				// _sql.append(vo.getThevalue());
				// _sql.append("','");
				// _sql.append(vo.getChname());
				// _sql.append("','");
				// _sql.append(vo.getRestype());
				// _sql.append("','");
				// _sql.append(time);
				// _sql.append("','");
				// _sql.append(vo.getUnit());
				// _sql.append("','");
				// _sql.append(vo.getBak());
				// _sql.append("')");
				// //SysLogger.info(_sql.toString());
				// stmt.addBatch(_sql.toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// }
				// }
				// }
				try {
					dbmanager.executeBatch();
					if (flag == 1)
						dbmanager.executePreparedBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				// conn.rollback();
				e.printStackTrace();
			} finally {
				if (dbmanager != null) {
					try {
						dbmanager.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					dbmanager = null;
				}
			}
		}
		return true;
	}

	/**
	 * 根据service的Hashtable得到ServiceNodeTemp对象
	 * 
	 * @param serviceItemHash
	 * @return
	 */
	public ServiceNodeTemp getServiceNodeTempByHashtable(Hashtable serviceItemHash) {
		ServiceNodeTemp serviceNodeTemp = new ServiceNodeTemp();
		Iterator iterator = serviceItemHash.keySet().iterator();
		while (iterator.hasNext()) {
			String key = String.valueOf(iterator.next());
			String value = String.valueOf(serviceItemHash.get(key));
			if (value != null && value.indexOf("\\") != -1) {
				value = value.replaceAll("\\\\", "/");
			}
			if ("DisplayName".equalsIgnoreCase(key) || "name".equalsIgnoreCase(key)) {
				serviceNodeTemp.setName(value);
			}
			if ("instate".equalsIgnoreCase(key) || "State".equalsIgnoreCase(key) || "status".equalsIgnoreCase(key)) {
				serviceNodeTemp.setInstate(value);
			}
			if ("opstate".equalsIgnoreCase(key)) {
				serviceNodeTemp.setOpstate(value);
			}
			if ("uninst".equalsIgnoreCase(key)) {
				serviceNodeTemp.setUninst(value);
			}
			if ("paused".equalsIgnoreCase(key)) {
				serviceNodeTemp.setPaused(value);
			}
			if ("StartMode".equalsIgnoreCase(key)) {
				serviceNodeTemp.setStartMode(value);
			}
			if ("PathName".equalsIgnoreCase(key)) {
				serviceNodeTemp.setPathName(value);
			}
			if ("Description".equalsIgnoreCase(key)) {
				serviceNodeTemp.setDescription(value);
			}
			if ("ServiceType".equalsIgnoreCase(key)) {
				serviceNodeTemp.setServiceType(value);
			}
			if ("pid".equalsIgnoreCase(key)) {
				serviceNodeTemp.setPid(value);
			}
			if ("groupstr".equalsIgnoreCase(key) || "group".equalsIgnoreCase(key)) {
				serviceNodeTemp.setGroupstr(value);
			}
		}
		return serviceNodeTemp;
	}

	/*
	 * sql.append(vo.getCategory());//entity sql.append("','");
	 * sql.append(vo.getEntity());//subentity sql.append("','");
	 * sql.append(vo.getSubentity().trim());//sindex sql.append("','");
	 * sql.append(vo.getThevalue());//thevalue sql.append("','");
	 * sql.append(vo.getChname()); sql.append("','");
	 * sql.append(vo.getRestype()); sql.append("','"); sql.append(time);
	 * sql.append("','"); sql.append(vo.getUnit()); sql.append("','");
	 * sql.append(vo.getBak());
	 */
	/**
	 * 将WMI中的User信息的List类型集合转换为Vector类型
	 * 
	 * @param userList
	 * @return
	 * 
	 */
	private Vector convertWMIUserListToVector(List userList) {
		Vector retVector = new Vector();
		int sindex = 0;
		for (int i = 0; i < userList.size(); i++) {
			Hashtable tempHash = (Hashtable) userList.get(i);
			Iterator iterator = tempHash.keySet().iterator();
			sindex++;// userList的每个元素对应一个sindex
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = (String) tempHash.get(key);
				Usercollectdata vo = new Usercollectdata();// 每条键值对对应一条数据库记录
				vo.setSubentity(sindex + "");// 将name设置成sindex
				vo.setCategory("User");
				vo.setEntity(key);
				vo.setThevalue(value);
				vo.setUnit("");
				retVector.add(vo);
			}
		}
		return retVector;
	}
}