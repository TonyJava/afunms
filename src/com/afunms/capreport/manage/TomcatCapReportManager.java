/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.capreport.manage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.TomcatDao;
import com.afunms.application.manage.TomcatManager;
import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.DocumentException;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class TomcatCapReportManager extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();

	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String tomcatlist() {
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
		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}

		List list = null;
		TomcatDao dao = new TomcatDao();
		try {
			if (operator.getRole() == 0) {
				list = dao.loadAll();
			} else
				list = dao.getTomcatByBID(rbids);
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			dao.close();
		}
		if (list == null)
			list = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Tomcat vo = (Tomcat) list.get(i);
			Node tomcatNode = PollingEngine.getInstance().getTomcatByID(vo.getId());
			if (tomcatNode == null)
				vo.setStatus(0);
			else
				vo.setStatus(tomcatNode.getStatus());
		}
		request.setAttribute("list", list);
		return "/capreport/tomcat/tomcatlist.jsp";
	}

	private String tomcatping() {
		TomcatManager tomcatManager = new TomcatManager();
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

		String[] ids = getParaArrayValue("checkbox");
		Hashtable allcpuhash = new Hashtable();

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}

		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {

				Node tomcatNode = PollingEngine.getInstance().getTomcatByID(Integer.parseInt(ids[i]));
				Hashtable pinghash = new Hashtable();
				try {
					pinghash = tomcatManager.getCategory(tomcatNode.getIpAddress(), "TomcatPing", "ConnectUtilization",
						starttime, totime);
				} catch (Exception e) {
					SysLogger.error("", e);
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("tomcat", tomcatNode);
				ipmemhash.put("pinghash", pinghash);
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// 对orderList根据theValue进行排序
			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					Node node = (Node) _pinghash.get("tomcat");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					if (pinghash == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();

					String pingconavg = "";
					String downnum = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					if (pinghash.get("downnum") != null)
						downnum = (String) pinghash.get("downnum");
					List ipdiskList = new ArrayList();
					ipdiskList.add(ip);
					ipdiskList.add(equname);
					ipdiskList.add(node.getType());
					ipdiskList.add(pingconavg);
					ipdiskList.add(downnum);
					returnList.add(ipdiskList);
				}
			}
		}
		// **********************************************************

		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {

					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("avgping")) {
						String avgping = "";
						if (ipdiskList.get(3) != null) {
							avgping = (String) ipdiskList.get(3);
						}
						String _avgping = "";
						if (ipdiskList.get(3) != null) {
							_avgping = (String) _ipdiskList.get(3);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping
								.substring(0, _avgping.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("downnum")) {
						String downnum = "";
						if (ipdiskList.get(4) != null) {
							downnum = (String) ipdiskList.get(4);
						}
						String _downnum = "";
						if (ipdiskList.get(4) != null) {
							_downnum = (String) _ipdiskList.get(4);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// 得到排序后的Subentity的列表
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pinglist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// HostNodeDao dao = new HostNodeDao();
		request.setAttribute("pinglist", list);
		return "/capreport/tomcat/tomcatping.jsp";
	}

	private String downloadselftomcatreport() {

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
		String type = "";
		String typename = "TOMCAT";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		TomcatManager tomcatManager = new TomcatManager();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			System.out.println(ip + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			type = getParaValue("type");
			/*
			 * HostNodeDao dao = new HostNodeDao();
			 * System.out.println(dao.findByCondition("ip_address",
			 * ip)+"********************************"); HostNode node =
			 * (HostNode) dao.findByCondition("ip_address", ip).get(0);
			 * System.out.println(node+"-----------------node"); dao.close();
			 */
			// if(node == null)
			TomcatDao tomcatdao = null;
			com.afunms.polling.node.Tomcat node = null;
			int id = 0;
			try {
				tomcatdao = new TomcatDao();
				id = tomcatdao.getidByIp(ip);
				node = (com.afunms.polling.node.Tomcat) PollingEngine.getInstance().getTomcatByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				tomcatdao.close();
			}
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// zhushouzhi----------------
			Hashtable ConnectUtilizationhash = tomcatManager.getCategory(ip, "TomcatPing", "ConnectUtilization",
				starttime, totime);
			Hashtable hash = tomcatManager.getCategory(ip, "tomcat_jvm", "jvm_utilization", starttime, totime);
			
			List listp = (List) ConnectUtilizationhash.get("list");
			Vector vector1 = (Vector) listp.get(listp.size() - 1);
			String tomcatnow = "0.0";
			tomcatnow = (String) vector1.get(0);
			reporthash.put("Ping", tomcatnow);
			
			List list = (List) hash.get("list");
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
			// jvm------------tomcat

			String jvmconavg = "";
			if (hash.get("avg_tomcat_jvm") != null)
				jvmconavg = (String) hash.get("avg_tomcat_jvm");
			String jvmconmax = "";
			maxjvm.put("avg_tomcat_jvm", jvmconavg);
			if (hash.get("max_tomcat_jvm") != null) {
				jvmconmax = (String) hash.get("max_tomcat_jvm");
			}
			maxjvm.put("max_tomcat_jvm", jvmconmax);
			String jvmnow = "";
			if (list != null && list.size() > 0) {
				Vector vector = (Vector) list.get(list.size() - 1);
				jvmnow = (String) vector.get(0);
			}
			reporthash.put("jvmnow", jvmnow);
			reporthash.put("maxjvm", maxjvm);

			// zhushouzhi-----------------------------start
			// zhushouzhi----------------------------------------end

			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
			reporthash.put("ping", maxping);

			// tomcat get
			// 添加jvm数据
			TomcatDao tomcatDao = null;
			com.afunms.polling.node.Tomcat tomcat = null;
			int id1 = 0;
			try {
				tomcatDao = new TomcatDao();
				id1 = tomcatDao.getidByIp(ip);
				tomcat = (com.afunms.polling.node.Tomcat) PollingEngine.getInstance().getTomcatByID(id1);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				tomcatDao.close();
			}
			// end
			Hashtable hdata = (Hashtable) sharedata.get(ip);
			if (hdata == null)
				hdata = new Hashtable();
			// downum
			String downnum = (String) ConnectUtilizationhash.get("downnum");
			String grade = "优";
			if (!"0".equals(downnum)) {
				grade = "差";
			}
			reporthash.put("grade", grade);
			reporthash.put("downnum", downnum);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("ip", ip);
			reporthash.put("tomcat", tomcat);
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "TomcatPing", 576, 170);
			p_draw_line(hash, "JVM内存利用率", newip + "tomcat_jvm", 576, 170);
			// 画图-----------------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				report.createReport_tomcat("temp/tomcatnms_report.xls");// excel综合报表
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/tomcatnms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_tomcatDoc(fileName, "doc");// word综合报表
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/tomcatnewdoc_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_tomcatNewDoc(fileName, "doc");// word业务分析表
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/tomcatnms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_tomcatNewDoc(fileName, "pdf");// pdf业务分析表
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/iisnms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_tomcatDoc(fileName, "pdf");// pdf综合报表
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	/**
	 * @author sqc
	 * @return
	 */
	private String downloadselftomcatreportAll() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
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
		String type = "";
		String typename = "TOMCAT";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		TomcatManager tomcatManager = new TomcatManager();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					int id = 0;
					id = ids[i];
					// ip = getParaValue("ipaddress");
					System.out.println(ip + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					type = getParaValue("type");
					/*
					 * HostNodeDao dao = new HostNodeDao();
					 * System.out.println(dao.findByCondition("ip_address",
					 * ip)+"********************************"); HostNode node =
					 * (HostNode) dao.findByCondition("ip_address", ip).get(0);
					 * System.out.println(node+"-----------------node");
					 * dao.close();
					 */
					// if(node == null)
					TomcatDao tomcatdao = null;
					com.afunms.polling.node.Tomcat node = null;
					try {
						// tomcatdao = new TomcatDao();
						// id = tomcatdao.getidByIp(ip);
						node = (com.afunms.polling.node.Tomcat) PollingEngine.getInstance().getTomcatByID(id);
						ip = node.getIpAddress();
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						// tomcatdao.close();
					}
					equipname = node.getAlias() + "(" + ip + ")";
					equipnameDoc = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// zhushouzhi----------------
					Hashtable ConnectUtilizationhash = tomcatManager.getCategory(ip, "TomcatPing",
						"ConnectUtilization", starttime, totime);
					Hashtable hash = tomcatManager.getCategory(ip, "tomcat_jvm", "jvm_utilization", starttime, totime);
					
					List listp = (List) ConnectUtilizationhash.get("list");
					Vector vector1 = (Vector) listp.get(listp.size() - 1);
					String tomcatnow = "0.0";
					tomcatnow = (String) vector1.get(0);
					reporthash.put("Ping", tomcatnow);
					
					List list = (List) hash.get("list");
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					// jvm------------tomcat

					String jvmconavg = "";
					if (hash.get("avg_tomcat_jvm") != null)
						jvmconavg = (String) hash.get("avg_tomcat_jvm");
					String jvmconmax = "";
					maxjvm.put("avg_tomcat_jvm", jvmconavg);
					if (hash.get("max_tomcat_jvm") != null) {
						jvmconmax = (String) hash.get("max_tomcat_jvm");
					}
					maxjvm.put("max_tomcat_jvm", jvmconmax);
					String jvmnow = "";
					if (list != null && list.size() > 0) {
						Vector vector = (Vector) list.get(list.size() - 1);
						jvmnow = (String) vector.get(0);
					}
					reporthash.put("jvmnow", jvmnow);
					reporthash.put("maxjvm", maxjvm);

					// zhushouzhi-----------------------------start
					// zhushouzhi----------------------------------------end

					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					reporthash.put("ping", maxping);

					// tomcat get
					// 添加jvm数据
					TomcatDao tomcatDao = null;
					com.afunms.polling.node.Tomcat tomcat = null;
					int id1 = 0;
					try {
						tomcatDao = new TomcatDao();
						id1 = tomcatDao.getidByIp(ip);
						tomcat = (com.afunms.polling.node.Tomcat) PollingEngine.getInstance().getTomcatByID(id1);
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						tomcatDao.close();
					}
					// end
					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null)
						hdata = new Hashtable();
					// downum
					String downnum = (String) ConnectUtilizationhash.get("downnum");
					String grade = "优";
					if (!"0".equals(downnum)) {
						grade = "差";
					}
					reporthash.put("grade", grade);
					reporthash.put("downnum", downnum);
					reporthash.put("equipname", equipname);
					reporthash.put("equipnameDoc", equipnameDoc);
					reporthash.put("ip", ip);
					reporthash.put("tomcat", tomcat);
					reporthash.put("typename", typename);
					reporthash.put("startdate", startdate);

					// 画图----------------------
					String timeType = "minute";
					PollMonitorManager pollMonitorManager = new PollMonitorManager();
					p_draw_line(ConnectUtilizationhash, "连通率", newip + "TomcatPing", 576, 170);
					p_draw_line(hash, "JVM内存利用率", newip + "tomcat_jvm", 576, 170);
					allreporthash.put(ip, reporthash);
				}
				// 画图-----------------------------
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_tomcatAll("temp/tomcatnms_report.xls");// excel综合报表
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	private void p_draw_line(Hashtable hash, String title1, String title2, int w, int h) {
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
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
							.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			SysLogger.error("", e);
		}
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
					.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			SysLogger.error("", e);
		}
	}

	private String readyEdit() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/edit.jsp");
		return readyEdit(dao);
	}

	private String update() {
		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setManaged(getParaIntValue("managed") == 1 ? true : false);

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		host.setAlias(vo.getAlias());
		host.setManaged(vo.isManaged());

		// 更新数据库
		DaoInterface dao = new HostNodeDao();
		setTarget("/network.do?action=list");
		return update(dao, vo);
	}

	private String refreshsysname() {
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(getParaIntValue("id"));
		if (host != null) {
			host.setSysName(sysName);
			host.setAlias(sysName);
		}

		return "/network.do?action=list";
	}

	private String delete() {
		String id = getParaValue("radio");

		PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
		HostNodeDao dao = new HostNodeDao();
		dao.delete(id);
		return "/network.do?action=list";
	}

	// zhushouzhi主机内存利用率报表

	public String execute(String action) {
		if (action.equals("tomcatlist"))
			return tomcatlist();
		if (action.equals("downloadselftomcatreport"))
			return downloadselftomcatreport();
		if (action.equals("downloadselftomcatreportAll"))
			return downloadselftomcatreportAll();
		// 报表
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private void getTime(HttpServletRequest request, String[] time) {
		Calendar current = new GregorianCalendar();
		String key = getParaValue("beginhour");
		if (getParaValue("beginhour") == null) {
			Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			request.setAttribute("beginhour", new Integer(hour.intValue() - 1));
			request.setAttribute("endhour", hour);
			// mForm.setBeginhour(new Integer(hour.intValue()-1));
			// mForm.setEndhour(hour);
		}
		if (getParaValue("begindate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String begindate = "";
			begindate = timeFormatter.format(new java.util.Date());
			request.setAttribute("begindate", begindate);
			request.setAttribute("enddate", begindate);
			// mForm.setBegindate(begindate);
			// mForm.setEnddate(begindate);
		} else {
			String temp = getParaValue("begindate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("enddate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
		if (getParaValue("startdate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String startdate = "";
			startdate = timeFormatter.format(new java.util.Date());
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", startdate);
			// mForm.setStartdate(startdate);
			// mForm.setTodate(startdate);
		} else {
			String temp = getParaValue("startdate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("todate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
	}

	private String doip(String ip) {
		// String newip = "";
		// for (int i = 0; i < 3; i++) {
		// int p = ip.indexOf(".");
		// newip += ip.substring(0, p);
		// ip = ip.substring(p + 1);
		// }
		// newip += ip;
		// // System.out.println("newip="+newip);
		// return newip;
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}

	private void p_drawchartMultiLineMonth(Hashtable hash, String title1, String title2, int w, int h, String flag) {
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
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							// UtilHdx obj = (UtilHdx)vector.get(j);
							// Double v=new Double(obj.getThevalue());
							// Calendar temp = obj.getCollecttime();
							// new org.jfree.data.time.Hour(newTime)

							// Hour hour=new
							// Hour(tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
							// Day day=new
							// Day(tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
							// ss.addOrUpdate(new
							// org.jfree.data.time.Day(da),v);
							// ss.addOrUpdate(hour,v);
							Minute minute = new Minute(tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.HOUR_OF_DAY),
									tempCal.get(Calendar.DAY_OF_MONTH), tempCal.get(Calendar.MONTH) + 1, tempCal
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
				SysLogger.error("", e);
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLineYear(Hashtable hash, String title1, String title2, int w, int h, String flag) {
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
					TimeSeries ss = new TimeSeries(key, Hour.class);
					// TimeSeries ss = new TimeSeries(key,Minute.class);
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
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							ss.addOrUpdate(new org.jfree.data.time.Hour(da), v);
						}
					}
					// }
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", unit, title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void drawchartMultiLineMonth(Hashtable hash, String title1, String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			// String unit = (String)hash.get("unit");
			// hash.remove("unit");
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
						// 流速
						for (int j = 0; j < value.length; j++) {
							String val = value[j];
							if (val != null && val.indexOf("&") >= 0) {
								String[] splitstr = val.split("&");
								String splittime = splitstr[0];
								Double v = new Double(splitstr[1]);
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date da = sdf.parse(splittime);
								Calendar tempCal = Calendar.getInstance();
								tempCal.setTime(da);
								// org.jfree.data.time.Day(da),v);
								// ss.addOrUpdate(hour,v);
								Minute minute = new Minute(tempCal.get(Calendar.MINUTE), tempCal
										.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.DAY_OF_MONTH), tempCal
										.get(Calendar.MONTH) + 1, tempCal.get(Calendar.YEAR));
								ss.addOrUpdate(minute, v);
							}
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", "y(kb/s)", title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLine(Hashtable hash, String title1, String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			String unit = (String) hash.get("unit");
			hash.remove("unit");
			String[] keys = (String[]) hash.get("key");
			if (keys == null) {
				draw_blank(title1, title2, w, h);
				return;
			}
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					TimeSeries ss = new TimeSeries(key, Minute.class);
					Vector vector = (Vector) (hash.get(key));
					if (flag.equals("AllUtilHdxPerc")) {
						// 综合带宽利用率
						for (int j = 0; j < vector.size(); j++) {
							/*
							 * //if
							 * (title1.equals("带宽利用率")||title1.equals("端口流速")){
							 * AllUtilHdxPerc obj =
							 * (AllUtilHdxPerc)vector.get(j); Double v=new
							 * Double(obj.getThevalue()); Calendar temp =
							 * obj.getCollecttime(); Minute minute=new
							 * Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
							 * ss.addOrUpdate(minute,v); //}
							 */
						}
					} else if (flag.equals("AllUtilHdx")) {
						// 综合流速
						for (int j = 0; j < vector.size(); j++) {
							// if
							// (title1.equals("带宽利用率")||title1.equals("端口流速")){
							AllUtilHdx obj = (AllUtilHdx) vector.get(j);
							Double v = new Double(obj.getThevalue());
							Calendar temp = obj.getCollecttime();
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
							// }
						}
					} else if (flag.equals("UtilHdxPerc")) {
						// 带宽利用率
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}

					} else if (flag.equals("UtilHdx")) {
						// 流速
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("ErrorsPerc")) {
						// 流速
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("DiscardsPerc")) {
						// 流速
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("Packs")) {
						// 数据包
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private static CategoryDataset getDataSet() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(10, "", "values1");
		dataset.addValue(20, "", "values2");
		dataset.addValue(30, "", "values3");
		dataset.addValue(40, "", "values4");
		dataset.addValue(50, "", "values5");
		return dataset;
	}

	public void draw_column(Hashtable bighash, String title1, String title2, int w, int h) {
		if (bighash.size() != 0) {
			ChartGraph cg = new ChartGraph();
			int size = bighash.size();
			double[][] d = new double[1][size];
			String c[] = new String[size];
			Hashtable hash;
			for (int j = 0; j < size; j++) {
				hash = (Hashtable) bighash.get(new Integer(j));
				c[j] = (String) hash.get("name");
				d[0][j] = Double.parseDouble((String) hash.get("Utilization" + "value"));
			}
			String rowKeys[] = { "" };
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, c, d);// .createCategoryDataset(rowKeys,
			// columnKeys, data);
			cg.zhu(title1, title2, dataset, w, h);
		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}

	private void p_drawchartMultiLine(Hashtable hash, String title1, String title2, int w, int h) {
		if (hash.size() != 0) {
			String unit = (String) hash.get("unit");
			hash.remove("unit");
			String[] keys = (String[]) hash.get("key");
			if (keys == null) {
				draw_blank(title1, title2, w, h);
				return;
			}
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					TimeSeries ss = new TimeSeries(key, Minute.class);
					Vector vector = (Vector) (hash.get(key));
					for (int j = 0; j < vector.size(); j++) {
						// if (title1.equals("内存利用率")){
						Vector obj = (Vector) vector.get(j);
						// Memorycollectdata obj =
						// (Memorycollectdata)vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						// Calendar temp = obj.getCollecttime();
						Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
								.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
						ss.addOrUpdate(minute, v);
						// }
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				SysLogger.error("", e);
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	// zhushouzhi--------------------hostping--pdf

}
