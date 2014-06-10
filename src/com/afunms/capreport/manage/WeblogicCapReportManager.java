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

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.manage.WeblogicManager;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
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
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Weblogic;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.temp.dao.WeblogicDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.DocumentException;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class WeblogicCapReportManager extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();

	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String weblogiclist() {
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

		WeblogicConfigDao configdao = new WeblogicConfigDao();
		List list = null;
		try {
			if (operator.getRole() == 0) {
				list = configdao.loadAll();
			} else
				list = configdao.getWeblogicByBID(rbids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		if (list == null)
			list = new ArrayList();
		request.setAttribute("list", list);
		return "/capreport/tomcat/weblogiclist.jsp";
	}

	private String downloadselfweblogicreport() {
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
		String typename = "WEBLOGIC";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		WeblogicManager weblogicManager = new WeblogicManager();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			WeblogicConfigDao weblogicDao = null;
			Weblogic weblogic = null;
			int id = 0;
			try {
				weblogicDao = new WeblogicConfigDao();
				id = weblogicDao.getidByIp(ip);
				weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(id);
			} catch (Exception e) {

			} finally {
				weblogicDao.close();
			}
			// if(node == null)
			equipname = weblogic.getAlias() + "(" + ip + ")";
			equipnameDoc = weblogic.getAlias();

			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// zhushouzhi----------------
			Hashtable ConnectUtilizationhash = weblogicManager.getCategory(ip, "WeblogicPing", "ConnectUtilization",
				starttime, totime);
			String pingconavg = "";
			List list = (List) ConnectUtilizationhash.get("list");
			Vector vector = (Vector) list.get(list.size() - 1);
			String weblogicnow = "0.0";
			weblogicnow = (String) vector.get(0);
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去

			reporthash.put("ping", maxping);

			Hashtable hdata = (Hashtable) sharedata.get(ip);
			if (hdata == null)
				hdata = new Hashtable();
			// downum
			String downnum = (String) ConnectUtilizationhash.get("downnum");
			String grade = "优";
			if (!"0".equals(downnum)) {
				grade = "差";
			}
			// ---------------------------
			WeblogicConfigDao weblogicconfigdao = null;
			WeblogicConfig weblogicconf = null;
			int id1 = 0;
			try {
				weblogicconfigdao = new WeblogicConfigDao();
				id1 = weblogicconfigdao.getidByIp(ip);
				weblogicconfigdao = new WeblogicConfigDao();
				weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id1 + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				weblogicconfigdao.close();
			}
			SysLogger.info(weblogicconf + "--------------------weblogicconf------------------");

			// -------------------hash
			Hashtable hash = null;
			List normalValue = null;
			List queueValue = null;
			List jdbcValue = null;
			List webappValue = null;
			List heapValue = null;
			List serverValue = null;
			List jobValue = null;
			List servletValue = null;
			List logValue = null;
			List transValue = null;
			String runmodel = PollingEngine.getCollectwebflag();
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
			} else {
				List labList = new ArrayList();
				labList.add("normalValue");
				labList.add("queueValue");
				labList.add("jdbcValue");
				labList.add("webappValue");
				labList.add("heapValue");
				labList.add("serverValue");
				labList.add("jobValue");
				labList.add("servletValue");
				labList.add("logValue");
				labList.add("transValue");
				WeblogicDao weblogicdao = new WeblogicDao();
				hash = weblogicdao.getWeblogicData(labList, String.valueOf(id1));
			}
			if (hash != null) {
				normalValue = (List) hash.get("normalValue");
				queueValue = (List) hash.get("queueValue");
				jdbcValue = (List) hash.get("jdbcValue");
				webappValue = (List) hash.get("webappValue");
				heapValue = (List) hash.get("heapValue");
				serverValue = (List) hash.get("serverValue");
				jobValue = (List) hash.get("jobValue");
				servletValue = (List) hash.get("servletValue");
				logValue = (List) hash.get("logValue");
				transValue = (List) hash.get("transValue");
			}
			Hashtable weblogicnmphash = new Hashtable();
			if (normalValue != null) {
				weblogicnmphash.put("normalValue", normalValue);
			}
			if (queueValue != null) {
				weblogicnmphash.put("queueValue", queueValue);
			}
			if (jdbcValue != null) {
				weblogicnmphash.put("jdbcValue", jdbcValue);
			}
			if (webappValue != null) {
				weblogicnmphash.put("webappValue", webappValue);
			}
			if (heapValue != null) {
				weblogicnmphash.put("heapValue", heapValue);
			}
			if (serverValue != null) {
				weblogicnmphash.put("serverValue", serverValue);
			}
			if (servletValue != null) {
				weblogicnmphash.put("servletValue", servletValue);
			}
			if (logValue != null) {
				weblogicnmphash.put("logValue", logValue);
			}
			if (transValue != null) {
				weblogicnmphash.put("transValue", transValue);
			}
			// ------------------
			WeblogicNormal normalvalue = null;
			if (normalValue != null) {
				normalvalue = (WeblogicNormal) normalValue.get(0);
			}

			reporthash.put("weblogicnmphash", weblogicnmphash);
			reporthash.put("grade", grade);
			reporthash.put("weblogicnow", weblogicnow);
			reporthash.put("downnum", downnum);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("ip", ip);
			reporthash.put("weblogic", weblogic);
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			reporthash.put("weblogicconf", weblogicconf);
			reporthash.put("normalvalue", normalvalue);

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "WeblogicPing", 740, 150);
			// 画图-----------------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				report.createReport_weblogic("temp/weblogicnms_report.xls");// excel综合报表
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_weblogicDoc(fileName, "doc");// word综合报表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnmsnewdoc_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_weblogicNewDoc(fileName, "doc");// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_weblogicNewDoc(fileName, "pdf");// pdf业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnms_report1.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_weblogicDoc(fileName, "pdf");// pdf综合报表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	/**
	 * @return
	 */
	private String downloadselfweblogicreportAll() {
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
		String typename = "WEBLOGIC";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		WeblogicManager weblogicManager = new WeblogicManager();
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
					type = getParaValue("type");
					Weblogic weblogic = null;
					try {
						weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(id);
						ip = weblogic.getIpAddress();
					} catch (Exception e) {
						SysLogger.error("", e);
					}
					// if(node == null)
					equipname = weblogic.getAlias() + "(" + ip + ")";
					equipnameDoc = weblogic.getAlias();

					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// zhushouzhi----------------
					Hashtable ConnectUtilizationhash = weblogicManager.getCategory(ip, "WeblogicPing",
						"ConnectUtilization", starttime, totime);
					String pingconavg = "";
					List list = (List) ConnectUtilizationhash.get("list");
					Vector vector = (Vector) list.get(list.size() - 1);
					String weblogicnow = "0.0";
					weblogicnow = (String) vector.get(0);
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去

					reporthash.put("ping", maxping);

					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null)
						hdata = new Hashtable();
					// downum
					String downnum = (String) ConnectUtilizationhash.get("downnum");
					String grade = "优";
					if (!"0".equals(downnum)) {
						grade = "差";
					}
					// ---------------------------
					WeblogicConfigDao weblogicconfigdao = null;
					WeblogicConfig weblogicconf = null;
					int id1 = 0;
					try {
						weblogicconfigdao = new WeblogicConfigDao();
						id1 = weblogicconfigdao.getidByIp(ip);
						weblogicconfigdao = new WeblogicConfigDao();
						weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id1 + "");
					} catch (Exception e) {

					} finally {
						weblogicconfigdao.close();
					}
					SysLogger.info(weblogicconf + "--------------------weblogicconf------------------");

					// -------------------hash
					Hashtable hash = null;
					List normalValue = null;
					List queueValue = null;
					List jdbcValue = null;
					List webappValue = null;
					List heapValue = null;
					List serverValue = null;
					List jobValue = null;
					List servletValue = null;
					List logValue = null;
					List transValue = null;
					String runmodel = PollingEngine.getCollectwebflag();
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
					} else {
						List labList = new ArrayList();
						labList.add("normalValue");
						labList.add("queueValue");
						labList.add("jdbcValue");
						labList.add("webappValue");
						labList.add("heapValue");
						labList.add("serverValue");
						labList.add("jobValue");
						labList.add("servletValue");
						labList.add("logValue");
						labList.add("transValue");
						WeblogicDao weblogicdao = new WeblogicDao();
						hash = weblogicdao.getWeblogicData(labList, String.valueOf(id1));
					}
					if (hash != null) {
						normalValue = (List) hash.get("normalValue");
						queueValue = (List) hash.get("queueValue");
						jdbcValue = (List) hash.get("jdbcValue");
						webappValue = (List) hash.get("webappValue");
						heapValue = (List) hash.get("heapValue");
						serverValue = (List) hash.get("serverValue");
						jobValue = (List) hash.get("jobValue");
						servletValue = (List) hash.get("servletValue");
						logValue = (List) hash.get("logValue");
						transValue = (List) hash.get("transValue");
					}
					Hashtable weblogicnmphash = new Hashtable();
					if (normalValue != null) {
						weblogicnmphash.put("normalValue", normalValue);
					}
					if (queueValue != null) {
						weblogicnmphash.put("queueValue", queueValue);
					}
					if (jdbcValue != null) {
						weblogicnmphash.put("jdbcValue", jdbcValue);
					}
					if (webappValue != null) {
						weblogicnmphash.put("webappValue", webappValue);
					}
					if (heapValue != null) {
						weblogicnmphash.put("heapValue", heapValue);
					}
					if (serverValue != null) {
						weblogicnmphash.put("serverValue", serverValue);
					}
					if (servletValue != null) {
						weblogicnmphash.put("servletValue", servletValue);
					}
					if (logValue != null) {
						weblogicnmphash.put("logValue", logValue);
					}
					if (transValue != null) {
						weblogicnmphash.put("transValue", transValue);
					}
					// ------------------
					WeblogicNormal normalvalue = null;
					if (normalValue != null) {
						normalvalue = (WeblogicNormal) normalValue.get(0);
					}

					reporthash.put("weblogicnmphash", weblogicnmphash);
					reporthash.put("grade", grade);
					reporthash.put("weblogicnow", weblogicnow);
					reporthash.put("downnum", downnum);
					reporthash.put("equipname", equipname);
					reporthash.put("equipnameDoc", equipnameDoc);
					reporthash.put("ip", ip);
					reporthash.put("weblogic", weblogic);
					reporthash.put("typename", typename);
					reporthash.put("startdate", startdate);
					reporthash.put("weblogicconf", weblogicconf);
					reporthash.put("normalvalue", normalvalue);

					reporthash.put("weblogicnmphash", weblogicnmphash);
					reporthash.put("grade", grade);
					reporthash.put("weblogicnow", weblogicnow);
					reporthash.put("downnum", downnum);
					reporthash.put("equipname", equipname);
					reporthash.put("equipnameDoc", equipnameDoc);
					reporthash.put("ip", ip);
					reporthash.put("weblogic", weblogic);
					reporthash.put("typename", typename);
					reporthash.put("startdate", startdate);
					reporthash.put("weblogicconf", weblogicconf);
					reporthash.put("normalvalue", normalvalue);
					// 画图----------------------
					String timeType = "minute";
					PollMonitorManager pollMonitorManager = new PollMonitorManager();
					p_draw_line(ConnectUtilizationhash, "连通率", newip + "WeblogicPing", 740, 150);
				}
				allreporthash.put(ip, reporthash);
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_weblogicAll("temp/weblogicnms_report.xls");// excel综合报表
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/net/download.jsp";
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
			e.printStackTrace();
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
			e.printStackTrace();
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
		if (action.equals("weblogiclist"))
			return weblogiclist();

		if (action.equals("downloadselfweblogicreport"))
			return downloadselfweblogicreport();
		if (action.equals("downloadselfweblogicreportAll"))
			return downloadselfweblogicreportAll();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	// zhushouzhi--------------------hostping--pdf

}
