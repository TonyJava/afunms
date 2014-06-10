package com.afunms.capreport.manage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.manage.IISManager;
import com.afunms.application.manage.TomcatManager;
import com.afunms.application.manage.WeblogicManager;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.system.model.User;
import com.afunms.system.util.UserView;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.ibm.db2.jcc.a.a;
import com.lowagie.text.DocumentException;

public class ServiceCapReport extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String doip(String ip) {
//		String newip = "";
//		for (int i = 0; i < 3; i++) {
//			int p = ip.indexOf(".");
//			newip += ip.substring(0, p);
//			ip = ip.substring(p + 1);
//		}
//		newip += ip;
//		// System.out.println("newip="+newip);
//		return newip;
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}

	private String downloadselfservicechocereport() {

		// String oids = getParaValue("ids");
		String netoids = getParaValue("netoids");
		String hostoids = getParaValue("hostoids");
		String dboids = getParaValue("dboids");
		String tomcatoids = getParaValue("tomcatoids");
		String iisoids = getParaValue("iisoids");
		String weblogicoids = getParaValue("weblogicoids");
		Integer[] netids = null;
		if (netoids != null && netoids.length() > 0
				&& netoids.split(",").length > 0) {
			String[] _ids = netoids.split(",");
			if (_ids != null && _ids.length > 0)
				netids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				netids[i] = new Integer(_ids[i]);
			}
		}

		// host主机页面返回
		Integer[] hostids = null;
		if (hostoids != null && hostoids.length() > 0
				&& hostoids.split(",").length > 0) {
			String[] host_ids = hostoids.split(",");
			if (host_ids != null && host_ids.length > 0)
				hostids = new Integer[host_ids.length];
			for (int i = 0; i < host_ids.length; i++) {
				hostids[i] = new Integer(host_ids[i]);
			}
		}

		// db数据库页面返回值
		Integer[] dbids = null;
		if (dboids != null && dboids.length() > 0
				&& dboids.split(",").length > 0) {
			String[] db_ids = dboids.split(",");
			if (db_ids != null && db_ids.length > 0)
				dbids = new Integer[db_ids.length];
			for (int i = 0; i < db_ids.length; i++) {
				dbids[i] = new Integer(db_ids[i]);
			}
		}

		// tomcat页面返回值
		Integer[] tomcatids = null;
		if (tomcatoids != null && tomcatoids.length() > 0
				&& tomcatoids.split(",").length > 0) {
			String[] tomcat_ids = tomcatoids.split(",");
			if (tomcat_ids != null && tomcat_ids.length > 0)
				tomcatids = new Integer[tomcat_ids.length];
			for (int i = 0; i < tomcat_ids.length; i++) {
				tomcatids[i] = new Integer(tomcat_ids[i]);
			}
		}

		// iis页面返回值
		Integer[] iisids = null;
		if (iisoids != null && iisoids.length() > 0
				&& iisoids.split(",").length > 0) {
			String[] iis_ids = iisoids.split(",");
			if (iis_ids != null && iis_ids.length > 0)
				iisids = new Integer[iis_ids.length];
			for (int i = 0; i < iis_ids.length; i++) {
				iisids[i] = new Integer(iis_ids[i]);
			}
		}

		// weblogic页面返回值
		Integer[] weblogicids = null;
		if (weblogicoids != null && weblogicoids.length() > 0
				&& weblogicoids.split(",").length > 0) {
			String[] weblogic_ids = weblogicoids.split(",");
			if (weblogic_ids != null && weblogic_ids.length > 0)
				weblogicids = new Integer[weblogic_ids.length];
			for (int i = 0; i < weblogic_ids.length; i++) {
				weblogicids[i] = new Integer(weblogic_ids[i]);
			}
		}
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Hashtable allcpuhash = new Hashtable();
		String ip = "";
		String equipname = "";

		Hashtable hash = new Hashtable();// "Cpu",--current

		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserView view = new UserView();

		String positionname = view.getPosition(vo.getPosition());
		String username = vo.getName();
		// String position = vo.getPosition();
		Vector vector = new Vector();
		// Hashtable reporthash = new Hashtable();
		try {
			Hashtable allreporthash = new Hashtable();
			if (netids != null && netids.length > 0) {

				Hashtable netreporthash = new Hashtable();
				for (int i = 0; i < netids.length; i++) {
					Hashtable memhash = new Hashtable();// mem--current
					Hashtable diskhash = new Hashtable();
					Hashtable memmaxhash = new Hashtable();// mem--max
					Hashtable memavghash = new Hashtable();// mem--avg
					Hashtable maxhash = new Hashtable();// "Cpu"--max
					Hashtable maxping = new Hashtable();// Ping--max
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(netids[i]);
					System.out.println(node + "-----------------------------");
					System.out.println();
					dao.close();
					if (node == null)
						continue;
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();
					s
							.append("select * from system_eventlist where recordtime>= '"
									+ starttime
									+ "' "
									+ "and recordtime<='"
									+ totime + "' ");
					s.append(" and nodeid=" + node.getId());

					List infolist = eventdao.findByCriteria(s.toString());
					int levelone = 0;
					int levletwo = 0;
					int levelthree = 0;
					if (infolist != null && infolist.size() > 0) {
						for (int j = 0; j < infolist.size(); j++) {
							EventList eventlist = (EventList) infolist.get(j);
							if (eventlist.getContent() == null)
								eventlist.setContent("");
							String content = eventlist.getContent();
							String subentity = eventlist.getSubentity();

							if (eventlist.getContent() == null)
								eventlist.setContent("");

							if (eventlist.getLevel1() != 1) {
								levelone = levelone + 1;
							} else if (eventlist.getLevel1() == 2) {
								levletwo = levletwo + 1;
							} else if (eventlist.getLevel1() == 3) {
								levelthree = levelthree + 1;
							}
						}
					}

					int level4 = levelone + levletwo + levelthree;
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					reporthash.put("level4", level4 + "");
					eventdao.close();
					// ------------------------事件end
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// 按排序标志取各端口最新记录的列表
					String orderflag = "index";

					String[] netInterfaceItem = { "index", "ifname", "ifSpeed",
							"ifOperStatus", "OutBandwidthUtilHdx",
							"InBandwidthUtilHdx" };

					vector = hostlastmanager.getInterface_share(ip,
							netInterfaceItem, orderflag, startdate, todate);
					PortconfigDao portdao = new PortconfigDao();
					Hashtable portconfigHash = portdao.getIpsHash(ip);
					// Hashtable portconfigHash =
					// portconfigManager.getIpsHash(ip);
					reporthash.put("portconfigHash", portconfigHash);

					List reportports = portdao.getByIpAndReportflag(ip,
							new Integer(1));
					reporthash.put("reportports", reportports);
					portdao.close();
					if (reportports != null && reportports.size() > 0) {
						// 显示端口的流速图形
						I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
						String unit = "kb/s";
						String title = startdate + "至" + todate + "端口流速";
						String[] banden3 = { "InBandwidthUtilHdx",
								"OutBandwidthUtilHdx" };
						String[] bandch3 = { "入口流速", "出口流速" };

						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports
									.get(k);
							// 按分钟显示报表
							Hashtable value = new Hashtable();
							value = daymanager.getmultiHisHdx(ip, "ifspeed",
									portconfig.getPortindex() + "", banden3,
									bandch3, startdate, todate, "UtilHdx");
							String reportname = "第" + portconfig.getPortindex()
									+ "(" + portconfig.getName() + ")端口流速"
									+ startdate + "至" + todate + "报表(按分钟显示)";
							p_drawchartMultiLineMonth(value, reportname,
									newip + portconfig.getPortindex()
											+ "ifspeed_day", 800, 200,
									"UtilHdx");
							// String url1 =
							// "/resource/image/jfreechart/"+newip+portconfig.getPortindex()+"ifspeed_day.png";
						}
					}

					reporthash.put("netifVector", vector);

					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU",
							"Utilization", starttime, totime);
					String pingconavg = "";

					maxhash = new Hashtable();
					String cpumax = "";
					String avgcpu = "";
					if (cpuhash.get("max") != null) {
						cpumax = (String) cpuhash.get("max");
					}
					if (cpuhash.get("avgcpucon") != null) {
						avgcpu = (String) cpuhash.get("avgcpucon");
					}

					maxhash.put("cpumax", cpumax);
					maxhash.put("avgcpu", avgcpu);
					// 画图
					p_draw_line(cpuhash, "", newip + "cpu", 740, 120);
					Hashtable ConnectUtilizationhash = hostmanager
							.getCategory(ip, "Ping", "ConnectUtilization",
									starttime, totime);
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash
								.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash
								.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					p_draw_line(ConnectUtilizationhash, "", newip
							+ "ConnectUtilization", 740, 120);

					// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
					Hashtable _IpRouterHash = ShareData.getIprouterdata();
					vector = (Vector) _IpRouterHash.get(ip);
					if (vector != null)
						reporthash.put("iprouterVector", vector);

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata
									.get(m);
							if (hostdata.getSubentity().equals(
									"ConnectUtilization")) {
								reporthash.put("time", hostdata
										.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);
							}
						}
					}

					// CPU
					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null)
						hdata = new Hashtable();
					Vector cpuVector = new Vector();
					if (hdata.get("cpu") != null)
						cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						// for(int si=0;si<cpuVector.size();si++){
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector
								.elementAt(0);
						maxhash.put("cpu", cpudata.getThevalue());
						reporthash.put("CPU", maxhash);
						// }
					} else {
						reporthash.put("CPU", maxhash);
					}// -----流速
					Hashtable streaminHash = hostmanager.getAllutilhdx(ip,
							"AllInBandwidthUtilHdx", starttime, totime, "avg");
					Hashtable streamoutHash = hostmanager.getAllutilhdx(ip,
							"AllOutBandwidthUtilHdx", starttime, totime, "avg");
					String avgput = "";
					if (streaminHash.get("avgput") != null) {
						avgput = (String) streaminHash.get("avgput");
						reporthash.put("avginput", avgput);
					}
					if (streamoutHash.get("avgput") != null) {
						avgput = (String) streamoutHash.get("avgput");
						reporthash.put("avgoutput", avgput);
					}
					Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip,
							"AllInBandwidthUtilHdx", starttime, totime, "max");
					Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip,
							"AllOutBandwidthUtilHdx", starttime, totime, "max");
					String maxput = "";
					if (streammaxinHash.get("max") != null) {
						maxput = (String) streammaxinHash.get("max");
						reporthash.put("maxinput", maxput);
					}
					if (streammaxoutHash.get("max") != null) {
						maxput = (String) streammaxoutHash.get("max");
						reporthash.put("maxoutput", maxput);
					}
					// ------流速end

					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					reporthash.put("equipname", equipname);
					reporthash.put("memmaxhash", memmaxhash);
					reporthash.put("memavghash", memavghash);
					reporthash.put("ip", ip);
					netreporthash.put(node, reporthash);
				}

				allreporthash.put("netreporthash", netreporthash);

			}

			// host==============================================================================================
			if (hostids != null && hostids.length > 0) {

				Hashtable hostreporthash = new Hashtable();
				for (int i = 0; i < hostids.length; i++) {
					Hashtable memhash = new Hashtable();// mem--current
					Hashtable diskhash = new Hashtable();
					Hashtable memmaxhash = new Hashtable();// mem--max
					Hashtable memavghash = new Hashtable();// mem--avg
					Hashtable maxhash = new Hashtable();// "Cpu"--max
					Hashtable maxping = new Hashtable();// Ping--max
					HostNodeDao dao = new HostNodeDao();
					Hashtable reporthash = new Hashtable();
					HostNode node = (HostNode) dao.loadHost(hostids[i]);
					dao.close();
					// ----------------------------shijian
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();

					s
							.append("select * from system_eventlist where recordtime>= '"
									+ starttime
									+ "' "
									+ "and recordtime<='"
									+ totime + "' ");
					s.append(" and nodeid=" + node.getId());

					List infolist = eventdao.findByCriteria(s.toString());
					int levelone = 0;
					int levletwo = 0;
					int levelthree = 0;
					int level4 = 0;
					if (infolist != null && infolist.size() > 0) {

						for (int j = 0; j < infolist.size(); j++) {
							EventList eventlist = (EventList) infolist.get(j);
							if (eventlist.getContent() == null)
								eventlist.setContent("");
							String content = eventlist.getContent();
							if (eventlist.getLevel1() == null)
								continue;
							if (eventlist.getLevel1() == 1) {
								levelone = levelone + 1;
							} else if (eventlist.getLevel1() == 2) {
								levletwo = levletwo + 1;
							} else if (eventlist.getLevel1() == 3) {
								levelthree = levelthree + 1;
							}
						}
					}
					level4 = levelone + levletwo + levelthree;
					reporthash.put("level4", level4 + "");
					eventdao.close();
					// ----------------------------------shijian
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					String[] time = { "", "" };
					// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
					String[] item = { "CPU" };
					memhash = hostlastmanager.getMemory_share(ip, "Memory",
							startdate, todate);
					diskhash = hostlastmanager.getDisk_share(ip, "Disk",
							startdate, todate);
					// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU",
							"Utilization", starttime, totime);
					Hashtable[] memoryhash = hostmanager.getMemory(ip,
							"Memory", starttime, totime);
					// 各memory最大值
					memmaxhash = memoryhash[1];
					memavghash = memoryhash[2];
					// cpu最大值
					maxhash = new Hashtable();
					String cpumax = "";
					String avgcpu = "";
					if (cpuhash.get("max") != null) {
						cpumax = (String) cpuhash.get("max");
					}
					if (cpuhash.get("avgcpucon") != null) {
						avgcpu = (String) cpuhash.get("avgcpucon");

					}

					maxhash.put("cpumax", cpumax);
					maxhash.put("avgcpu", avgcpu);

					Hashtable ConnectUtilizationhash = hostmanager
							.getCategory(ip, "Ping", "ConnectUtilization",
									starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash
								.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash
								.get("max");

					}
					maxping.put("pingmax", ConnectUtilizationmax);

					// Hashtable reporthash = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata
									.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals(
											"ConnectUtilization")) {
										reporthash.put("time", hostdata
												.getCollecttime());
										reporthash.put("Ping", hostdata
												.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata
											.getCollecttime());
									reporthash.put("Ping", hostdata
											.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata
										.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					}

					// CPU
					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null)
						hdata = new Hashtable();
					Vector cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						for (int si = 0; si < cpuVector.size(); si++) {
							CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector
									.elementAt(si);
							maxhash.put("cpu", cpudata.getThevalue());
							reporthash.put("CPU", maxhash);
						}
					} else {
						reporthash.put("CPU", maxhash);
					}
					reporthash.put("Memory", memhash);
					reporthash.put("Disk", diskhash);
					reporthash.put("equipname", equipname);
					reporthash.put("memmaxhash", memmaxhash);
					reporthash.put("memavghash", memavghash);
					reporthash.put("ip", ip);
					hostreporthash.put(node, reporthash);
				}
				allreporthash.put("hostreporthash", hostreporthash);

			}

			// database==============================================================================================
			if (dbids != null && dbids.length > 0) {

				Hashtable dbreporthash = new Hashtable();
				Hashtable returnhashdb2 = new Hashtable();
				for (int i = 0; i < dbids.length; i++) {
					Hashtable maxping = new Hashtable();// Ping--max
					DBDao dao = new DBDao();
					Hashtable reporthash = new Hashtable();

					DBVo node = (DBVo) dao.findByID(String.valueOf(dbids[i]));
					DBTypeDao typedao = new DBTypeDao();
					int dbtype = node.getDbtype();
					DBTypeVo typevo = (DBTypeVo) typedao.findByID(dbtype + "");

					typedao.close();
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// db连通率
					String dbtype1 = typevo.getDbtype();
					ServiceCapReport serviceCapReport = new ServiceCapReport();
					String ping = "";
					Hashtable pinghash = null;
					String ConnectUtilizationmax = "";
					String pingconavg = "";
					int maxspace = 0;
					int mixspace = 0;
					if (dbtype1.equals("Oracle")) {

						Vector tableinfo_v = new Vector();
						pinghash = serviceCapReport.pinghash(starttime, totime,
								ip, "ORAPing");
						dao = new DBDao();
						try {
							tableinfo_v = dao.getOracleTableinfo(node
									.getIpAddress(), Integer.parseInt(node
									.getPort()), node.getDbName(), node
									.getUser(), node.getPassword());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dao.close();
						}
						List sizeList = new ArrayList();
						for (int oraNum = 0; oraNum < tableinfo_v.size(); oraNum++) {
							Hashtable ht = (Hashtable) tableinfo_v.get(oraNum);
							String size = ht.get("size mb").toString();
							int sizeint = Integer.valueOf(size);
							sizeList.add(sizeint);
						}

						for (int sizeListNum = 0; sizeListNum < sizeList.size(); sizeListNum++) {
							int size = (Integer) sizeList.get(sizeListNum);
							if (sizeListNum == 0) {
								maxspace = size;
								mixspace = size;
							}
							if (maxspace < size) {
								maxspace = size;
							}
							if (mixspace > size) {
								mixspace = size;
							}
						}
						if (pinghash.get("avgpingcon") != null)
							pingconavg = (String) pinghash.get("avgpingcon");

						maxping.put("avgpingcon", pingconavg);
						if (pinghash.get("max") != null) {
							ConnectUtilizationmax = (String) pinghash
									.get("max");
						}
						maxping.put("pingmax", ConnectUtilizationmax);
					} else if (dbtype1.equals("SQLServer")) {
						pinghash = serviceCapReport.pinghash(starttime, totime,
								ip, "SQLPing");
						if (pinghash.get("avgpingcon") != null)
							pingconavg = (String) pinghash.get("avgpingcon");

						maxping.put("avgpingcon", pingconavg);
						if (pinghash.get("max") != null) {
							ConnectUtilizationmax = (String) pinghash
									.get("max");
						}
						maxping.put("pingmax", ConnectUtilizationmax);
					} else if (dbtype1.equals("Sybase")) {
						pinghash = serviceCapReport.pinghash(starttime, totime,
								ip, "SYSPing");
						if (pinghash.get("avgpingcon") != null)
							pingconavg = (String) pinghash.get("avgpingcon");

						maxping.put("avgpingcon", pingconavg);
						if (pinghash.get("max") != null) {
							ConnectUtilizationmax = (String) pinghash
									.get("max");
						}
						maxping.put("pingmax", ConnectUtilizationmax);
					} else if (dbtype1.equals("DB2")) {
						pinghash = serviceCapReport.pinghash(starttime, totime,
								ip, "DB2Ping");
						// db2表空间
						dao = new DBDao();
						try {
							returnhashdb2 = dao.getDB2Space(
									node.getIpAddress(), Integer.parseInt(node
											.getPort()), node.getDbName(), node
											.getUser(), node.getPassword());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dao.close();
						}
						Enumeration dbs = returnhashdb2.keys();
						System.out.println("returnhash.keys()==" + dbs);
						List retList = new ArrayList();
						while (dbs.hasMoreElements()) {

							String obj = (String) dbs.nextElement();
							retList = (List) returnhashdb2.get(obj);
							System.out.println("retList.size()=="
									+ retList.size());

							List sizeList = new ArrayList();
							for (int db2num = 0; db2num < retList.size(); db2num++) {
								Hashtable ht = (Hashtable) retList.get(db2num);
								if (ht == null)
									continue;
								String size = "";
								int sizeint = 0;
								if (ht.get("totalspac") != null)
									size = (String) ht.get("totalspac");
								sizeint = Integer.valueOf(size);
								sizeList.add(sizeint);

							}
							for (int sizeListNum = 0; sizeListNum < sizeList
									.size(); sizeListNum++) {
								int size = (Integer) sizeList.get(sizeListNum);
								if (sizeListNum == 0) {
									maxspace = size;
									mixspace = size;
								}
								if (maxspace < size) {
									maxspace = size;
								}
								if (mixspace > size) {
									mixspace = size;
								}
							}
						}
						if (pinghash.get("avgpingcon") != null)
							pingconavg = (String) pinghash.get("avgpingcon");

						maxping.put("avgpingcon", pingconavg);
						if (pinghash.get("max") != null) {
							ConnectUtilizationmax = (String) pinghash
									.get("max");
						}
						maxping.put("pingmax", ConnectUtilizationmax);
					} else if (dbtype1.equals("Informix")) {
						pinghash = serviceCapReport.pinghash(starttime, totime,
								ip, "INFORMIXPing");
						if (pinghash.get("avgpingcon") != null)
							pingconavg = (String) pinghash.get("avgpingcon");

						maxping.put("avgpingcon", pingconavg);
						if (pinghash.get("max") != null) {
							ConnectUtilizationmax = (String) pinghash
									.get("max");
						}
						maxping.put("pingmax", ConnectUtilizationmax);
						// informix表空间

						DBVo informixvo = new DBVo();
						DBDao infomixdao = new DBDao();
						ArrayList dbspaces = new ArrayList();
						Hashtable sysValue = new Hashtable();
						// Hashtable sValue = new Hashtable();
						Hashtable dbValue = new Hashtable();
						informixvo = (DBVo) infomixdao
								.findByID(getParaValue(String.valueOf(dbids[i])));
						// sysValue = ShareData.getInformixmonitordata();
						// sValue =
						// (Hashtable)sysValue.get(informixvo.getIpAddress());
						// dbValue =
						// (Hashtable)sValue.get(informixvo.getDbName());
						// dbspaces =
						// (ArrayList)dbValue.get("informixspaces");//数据库基本信息

						// HONGLI ADD START
						IpTranslation tranfer = new IpTranslation();
						String hex = tranfer.formIpToHex(informixvo
								.getIpAddress());
						String serverip = hex + ":" + informixvo.getDbName();
						String statusStr = String
								.valueOf(((Hashtable) dao
										.getInformix_nmsstatus(serverip))
										.get("status"));
						List ioList = dao.getInformix_nmsio(serverip);
						List databaseList = dao
								.getInformix_nmsdatabase(serverip);
						dbspaces = (ArrayList) dao
								.getInformix_nmsspace(serverip);
						dbValue.put("databaselist", databaseList);
						dbValue.put("iolist", ioList);
						dbValue.put("informixspaces", dbspaces);
						// HONGLI ADD END

						if (dbspaces != null && dbspaces.size() > 0) {
							List sizeList = new ArrayList();
							for (int spacenum = 0; spacenum < dbspaces.size(); spacenum++) {
								Hashtable tablesVO = (Hashtable) dbspaces
										.get(spacenum);
								double all = 100.00;
								double show = all
										- Double.parseDouble(tablesVO.get(
												"percent_free").toString());
								String str = show + "";
								if (str.length() > 5) {
									str = str.substring(0, 5);
								}
								int sizeint = 0;
								String size = (String) tablesVO
										.get("pages_size");
								sizeint = Integer.valueOf(size);
								sizeList.add(sizeint);
							}
							for (int sizeListNum = 0; sizeListNum < sizeList
									.size(); sizeListNum++) {
								int size = (Integer) sizeList.get(sizeListNum);
								if (sizeListNum == 0) {
									maxspace = size;
									mixspace = size;
								}
								if (maxspace < size) {
									maxspace = size;
								}
								if (mixspace > size) {
									mixspace = size;
								}
							}

						}
					}
					// Hashtable pinghash = serviceCapReport.pinghash();

					// 表空间
					dao.close();
					reporthash.put("maxping", maxping);
					reporthash.put("maxspace", maxspace);
					reporthash.put("mixspace", mixspace);

					reporthash.put("dbtype", dbtype1);
					reporthash.put("ip", ip);
					reporthash.put("dbids", dbids);
					// dbreporthash.put(dbids[i], reporthash);
					dbreporthash.put(node, reporthash);
				}
				allreporthash.put("dbreporthash", dbreporthash);

			}
			// 中间件=========================================================================
			// tomcat========================================================================
			Vector rbids = new Vector();
			TomcatManager tomcatManager = new TomcatManager();
			List tomcatlist = null;
			TomcatDao tomcatdao = new TomcatDao();
			User operator = (User) session
					.getAttribute(SessionConstant.CURRENT_USER);

			if (tomcatids != null && tomcatids.length > 0) {

				Hashtable tomcatreporthash = new Hashtable();
				for (int i = 0; i < tomcatids.length; i++) {
					Hashtable maxping = new Hashtable();// Ping--max
					rbids.add(tomcatids[i]);
					Hashtable reporthash = new Hashtable();
					try {
						if (operator.getRole() == 0) {
							tomcatlist = tomcatdao.loadAll();
						} else
							tomcatlist = tomcatdao.getTomcatByBID(rbids);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						tomcatdao.close();
					}
					Tomcat tomcatvo = (Tomcat) tomcatlist.get(i);
					Node node = PollingEngine.getInstance().getTomcatByID(
							tomcatvo.getId());
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// tomcat连通率

					Hashtable pinghash = null;
					try {
						pinghash = tomcatManager.getCategory(ip, "TomcatPing",
								"ConnectUtilization", starttime, totime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Hashtable pinghash = serviceCapReport.pinghash();
					String pingconavg = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (pinghash.get("max") != null) {
						ConnectUtilizationmax = (String) pinghash.get("max");
					}

					maxping.put("pingmax", ConnectUtilizationmax);
					String tomcattype = "TOMCAT";
					reporthash.put("ip", ip);
					reporthash.put("maxping", maxping);
					reporthash.put("TOMCAT", tomcattype);
					tomcatreporthash.put(node, reporthash);
				}
				allreporthash.put("tomcatreporthash", tomcatreporthash);

			}
			// iis=================================================================================
			Vector iisrbids = new Vector();
			IISManager iisManager = new IISManager();
			List iislist = null;
			IISConfigDao iisConfigDao = new IISConfigDao();

			if (iisids != null && iisids.length > 0) {
				Hashtable maxping = new Hashtable();// Ping--max
				Hashtable iisreporthash = new Hashtable();
				for (int i = 0; i < iisids.length; i++) {
					iisrbids.add(iisids[i]);
					Hashtable reporthash = new Hashtable();
					try {
						if (operator.getRole() == 0) {
							iislist = iisConfigDao.loadAll();
						} else
							iislist = iisConfigDao.getIISByBID(iisrbids);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						iisConfigDao.close();
					}
					IISConfig iisvo = (IISConfig) iislist.get(i);
					Node node = PollingEngine.getInstance().getIisByID(
							iisvo.getId());
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// tomcat连通率

					Hashtable pinghash = null;
					try {
						pinghash = iisManager.getCategory(ip, "IISPing",
								"ConnectUtilization", starttime, totime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Hashtable pinghash = serviceCapReport.pinghash();
					String pingconavg = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (pinghash.get("max") != null) {
						ConnectUtilizationmax = (String) pinghash.get("max");
					}

					maxping.put("pingmax", ConnectUtilizationmax);
					String iistype = "IIS";
					reporthash.put("ip", ip);
					reporthash.put("maxping", maxping);
					reporthash.put("IIS", iistype);
					iisreporthash.put(node, reporthash);
				}
				allreporthash.put("iisreporthash", iisreporthash);

			}
			// ===========weblogic===================================
			Vector weblogicrbids = new Vector();
			WeblogicManager weblogiManager = new WeblogicManager();
			List weblogiclist = null;
			WeblogicConfigDao weblogicConfigDao = new WeblogicConfigDao();

			if (weblogicids != null && weblogicids.length > 0) {
				Hashtable maxping = new Hashtable();// Ping--max
				Hashtable weblogicreporthash = new Hashtable();
				for (int i = 0; i < weblogicids.length; i++) {
					weblogicrbids.add(weblogicids[i]);
					Hashtable reporthash = new Hashtable();
					try {
						if (operator.getRole() == 0) {
							weblogiclist = weblogicConfigDao.loadAll();
						} else
							weblogiclist = weblogicConfigDao
									.getWeblogicByBID(weblogicrbids);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						weblogicConfigDao.close();
					}
					WeblogicConfig weblogicsvo = (WeblogicConfig) weblogiclist
							.get(i);
					Node node = PollingEngine.getInstance().getWeblogicByID(
							weblogicsvo.getId());
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// tomcat连通率

					Hashtable pinghash = null;
					try {
						pinghash = weblogiManager.getCategory(ip,
								"WeblogicPing", "ConnectUtilization",
								starttime, totime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Hashtable pinghash = serviceCapReport.pinghash();
					String pingconavg = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (pinghash.get("max") != null) {
						ConnectUtilizationmax = (String) pinghash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					String weblogictype = "WEBLOGIC";
					reporthash.put("ip", ip);
					reporthash.put("maxping", maxping);
					reporthash.put("WEBLOGIC", weblogictype);
					weblogicreporthash.put(node, reporthash);
				}
				allreporthash.put("weblogicreporthash", weblogicreporthash);
			}
			// -------------------------------------------
			String file = "temp/serviceknms_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
					allreporthash);
			report.createReport_serviceworkchoce(starttime, totime, fileName,
					username, positionname);
			request.setAttribute("filename", fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/service/download.jsp";
		// return mapping.findForward("report_info");
	}

	/**
	 * @author wxy add
	 * @Time 2010-3-27 service report
	 * @return
	 */
	private String downloadPingReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			report.createReport_ServicePingExc("temp/servicenms_report.xls");
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/serviceknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径
				report1.createReport_ServicePingDoc(fileName);// word综合报表
				request.setAttribute("filename", fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/serviceknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径
				// report1.createReport_hostPDF(fileName);// pdf综合报表
				report1.createReport_ServicePingPDF(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径

				report1.createReport_hostNewPDF(fileName);// pdf业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "/capreport/service/download.jsp";

	}
	
	//公共Ping  方法
	private String PublicdownloadPingReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		String type = request.getParameter("type");
		if ("0".equals(str)) {
			// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			report.createReport_ServicePingExc("temp/"+type+"Ping_report.xls",type);
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/"+type+"Ping_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径
				report1.createReport_ServicePingDoc(fileName,type);// word综合报表
				request.setAttribute("filename", fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/"+type+"Ping_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath()
						+ file;// 获取系统文件夹路径
				// report1.createReport_hostPDF(fileName);// pdf综合报表
				report1.createReport_ServicePingPDF(fileName,type);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
//		else if ("3".equals(str)) {
//			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
//					reporthash);
//			try {
//				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
//				String fileName = ResourceCenter.getInstance().getSysPath()
//						+ file;// 获取系统文件夹路径
//
//				report1.createReport_hostNewPDF(fileName);// pdf业务分析表
//
//				request.setAttribute("filename", fileName);
//			} catch (DocumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return "/capreport/service/download.jsp";

	}

	/**
	 * @author wxy add
	 * @Time 2010-3-27 综合报表
	 * @return
	 * @throws DocumentException 
	 */
	private String downloadServiceCompReport(){

		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/serviceWebCompReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				
					try {
						report1.createReport_ServiceCompDoc(fileName);
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/serviceWebCompReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			
				 report1.createReport_ServiceCompExc(file);
			// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/serviceWebCompReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			 try {
			
				report1.createReport_ServiceCompPdf(fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
			 e.printStackTrace();
			 }//pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	/**
	 * @author wxy add
	 * @Time 2010-3-27 service event report
	 * @return
	 */
	private String downloadEventReport() {

		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/serviceEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_ServiceEventDoc(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/serviceEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_ServiceEventExc(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/serviceEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_ServiceEventPdf(fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";

	}

	public String execute(String action) {
		if (action.equals("downloadselfservicechocereport"))
			return downloadselfservicechocereport();
		if (action.equals("service"))
			return service();
		if (action.equals("downloadPingReport"))
			return downloadPingReport();
		if (action.equals("downloadWebCompReport"))
			return downloadServiceCompReport();
		if (action.equals("downloadEventReport"))
			return downloadEventReport();
		if(action.equalsIgnoreCase("PublicdownloadPingReport")){
			return PublicdownloadPingReport();
		}
		return null;
	}

	private void p_drawchartMultiLineMonth(Hashtable hash, String title1,
			String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			// String unit = (String)hash.get("unit");
			// hash.remove("unit");
			String unit = "";
			String[] keys = (String[]) hash.get("key");
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					// TimeSeries ss = new TimeSeries(key,Hour.class);
					TimeSeries ss = new TimeSeries(key, Minute.class);
					String[] value = (String[]) hash.get(key);
					if (flag.equals("UtilHdx")) {
						unit = "y(kb/s)";
					} else {
						unit = "y(%)";
					}
					// 流速
					for (int j = 0; j < value.length; j++) {
						String val = value[j];
						if (val != null && val.indexOf("&") >= 0) {
							String[] splitstr = val.split("&");
							String splittime = splitstr[0];
							Double v = new Double(splitstr[1]);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							Minute minute = new Minute(tempCal
									.get(Calendar.MINUTE), tempCal
									.get(Calendar.HOUR_OF_DAY), tempCal
									.get(Calendar.DAY_OF_MONTH), tempCal
									.get(Calendar.MONTH) + 1, tempCal
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
					// }
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", unit, title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp
					.get(Calendar.HOUR_OF_DAY),
					temp.get(Calendar.DAY_OF_MONTH),
					temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void p_draw_line(Hashtable hash, String title1, String title2,
			int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();

				TimeSeries ss = new TimeSeries(title1, Minute.class);
				TimeSeries[] s = { ss };
				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp
							.get(Calendar.HOUR_OF_DAY), temp
							.get(Calendar.DAY_OF_MONTH), temp
							.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg
						.timewave(s, "x(时间)", "y(" + unit + ")", title1,
								title2, w, h);

			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String service() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadNetwork(1));
		dao.close();
		HostNodeDao dao1 = new HostNodeDao();
		request.setAttribute("listhost", dao1.loadHostByFlag(1));
		dao1.close();
		DBDao dbdao = new DBDao();
		List dblist = dbdao.loadAll();
		request.setAttribute("dblist", dblist);
		dbdao.close();

		// tomcat
		User operator = (User) session
				.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}

		List tomcatlist = null;
		TomcatDao tomcatdao = new TomcatDao();
		try {
			if (operator.getRole() == 0) {
				tomcatlist = tomcatdao.loadAll();
			} else
				tomcatlist = tomcatdao.getTomcatByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatdao.close();
		}
		if (tomcatlist == null)
			tomcatlist = new ArrayList();
		for (int i = 0; i < tomcatlist.size(); i++) {
			Tomcat vo = (Tomcat) tomcatlist.get(i);
			Node tomcatNode = PollingEngine.getInstance().getTomcatByID(
					vo.getId());
			if (tomcatNode == null)
				vo.setStatus(0);
			else
				vo.setStatus(tomcatNode.getStatus());
		}
		// end tomcat
		// iis-----------------------------------------
		String iisbids = operator.getBusinessids();
		String iisbid[] = iisbids.split(",");
		Vector iisrbids = new Vector();
		if (iisbid != null && iisbid.length > 0) {
			for (int i = 0; i < iisbid.length; i++) {
				if (iisbid[i] != null && iisbid[i].trim().length() > 0)
					iisrbids.add(iisbid[i].trim());
			}
		}
		IISConfigDao configdao = new IISConfigDao();
		List iislist = new ArrayList();
		try {
			iislist = configdao.getIISByBID(iisrbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		request.setAttribute("iislist", iislist);
		// weblogic==================================
		String weblogicbids = operator.getBusinessids();
		String weblogicbid[] = weblogicbids.split(",");
		Vector weblogicrbids = new Vector();
		if (weblogicbid != null && weblogicbid.length > 0) {
			for (int i = 0; i < weblogicbid.length; i++) {
				if (weblogicbid[i] != null
						&& weblogicbid[i].trim().length() > 0)
					weblogicrbids.add(weblogicbid[i].trim());
			}
		}
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		List weblogiclist = null;
		try {
			if (operator.getRole() == 0) {
				weblogiclist = weblogicconfigdao.loadAll();
			} else
				weblogiclist = weblogicconfigdao
						.getWeblogicByBID(weblogicrbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			weblogicconfigdao.close();
		}
		request.setAttribute("weblogiclist", weblogiclist);
		request.setAttribute("tomcatlist", tomcatlist);
		return "/capreport/service/service.jsp";
	}

	public Hashtable pinghash(String starttime, String totime, String ip,
			String ping) {
		Hashtable pinghash = null;
		try {
			pinghash = hostmanager.getCategory(ip, ping, "ConnectUtilization",
					starttime, totime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Hashtable ConnectUtilizationhash =
		// hostmanager.getCategory(ip,"Ping","ConnectUtilization",starttime,totime);
		return pinghash;
	}
}
