/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.capreport.manage;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import jxl.write.WritableWorkbook;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.util.IpTranslation;
import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.model.ReportValue;
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
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.amchart.AmChartTool;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.system.model.User;
import com.afunms.system.util.UserView;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class HostCapReportManager extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();

	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String list() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/list.jsp";
	}

	/**
	 * @date 2011-4-19
	 * @author wxy add
	 * @服务器报表，按照业务查询
	 * @return
	 */
	private String find() {
		int hostflag = 0;
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		String bid = getParaValue("bidtext");
		hostflag = getParaIntValue("hostflag");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		if (bid != null && !bid.equals("")) {
			HostNodeDao dao = new HostNodeDao();
			request.setAttribute("list", dao.loadNetworkByBid(4, bid));
		} else {
			request.setAttribute("list", new ArrayList());
		}
		if (hostflag == 1) {
			return "/capreport/host/list.jsp";
		} else if (hostflag == 2) {
			return "/capreport/host/memorylist.jsp";
		} else if (hostflag == 3) {
			return "/capreport/host/disklist.jsp";
		} else if (hostflag == 4) {
			return "/capreport/host/cpulist.jsp";
		} else if (hostflag == 5) {
			return "/capreport/host/eventlist.jsp";
		} else if (hostflag == 6) {
			return "/capreport/host/multilist.jsp";
		} else {
			return "/capreport/host/list.jsp";
		}
	}

	/**
	 * @date 2011-5-13
	 * @author wxy add
	 * @服务器报表
	 * @return
	 */
	public String serverReport() {
		StringBuffer s = new StringBuffer();

		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int ii = 0; ii < bids.length; ii++) {
						if (bids[ii].trim().length() > 0) {
							s.append(" bid like '%").append(bids[ii]).append("%' ");
							if (ii != bids.length - 1)
								s.append(" or ");
						}
					}

				}

			}
		}
		// InterfaceTempDao interfaceDao = new InterfaceTempDao();
		List interfaceList = new ArrayList();

		List list = new ArrayList();
		HostNodeDao dao = new HostNodeDao();
		try {
			list = dao.loadNetworkByBid(4, current_user.getBusinessids());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		PortconfigDao portconfigDao = new PortconfigDao();

		try {
			interfaceList = portconfigDao.getAllBySms();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portconfigDao.close();
		}

		request.setAttribute("list", list);
		request.setAttribute("interfaceList", interfaceList);
		return "/capreport/host/serverReport.jsp";
	}

	private String memorylist() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/memorylist.jsp";
	}

	private String disklist() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/disklist.jsp";
	}

	private String cpulist() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/cpulist.jsp";
	}

	private String eventlist() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/eventlist.jsp";
	}

	private String multilist() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/multilist.jsp";
	}

	private String hostping() {
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
		if(ids != null && ids.length>0){
			session.setAttribute("ids", ids);
		}else{
			ids = (String[])session.getAttribute("ids");
		}

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));

				Hashtable pinghash = new Hashtable();
				try {
					pinghash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", starttime,
						totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable responsehash = new Hashtable();
				try {
					responsehash = hostmanager.getCategory(node.getIpAddress(),
							"Ping", "ResponseTime", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("hostnode", node);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("responsehash", responsehash);
				ipmemhash.put("ipaddress",node.getIpAddress()+"("+node.getAlias()+")");
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")
				|| orderflag.equalsIgnoreCase("responseavg")
				|| orderflag.equalsIgnoreCase("responsemax")) {
			returnList = (List) session.getAttribute("pinglist");
			orderList = (List) session.getAttribute("orderList");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable responsehash = (Hashtable) _pinghash.get("responsehash");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					if (pinghash == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();

					String pingconavg = "";
					String downnum = "";
					String responseavg = "";
					String responsemax = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					if (pinghash.get("downnum") != null)
						downnum = (String) pinghash.get("downnum");
					// 获取响应时间
					if (responsehash.get("avgpingcon") != null)
						responseavg = (String) responsehash.get("avgpingcon");
					if (responsehash.get("pingmax") != null)
						responsemax = (String) responsehash.get("pingmax");
					List ipdiskList = new ArrayList();
					ipdiskList.add(ip);
					ipdiskList.add(equname);
					ipdiskList.add(node.getType());
					ipdiskList.add(pingconavg);
					ipdiskList.add(downnum);
					ipdiskList.add(responseavg);
					ipdiskList.add(responsemax);
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
					}else if (orderflag.equalsIgnoreCase("responseavg")) {
						String avgping = "";
						if (ipdiskList.get(5) != null) {
							avgping = (String) ipdiskList.get(5);
						}
						String _avgping = "";
						if (ipdiskList.get(5) != null) {
							_avgping = (String) _ipdiskList.get(5);
						}
						if (new Double(avgping.substring(0,
								avgping.length() - 2)).doubleValue() < new Double(
								_avgping.substring(0, _avgping.length() - 2))
								.doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("responsemax")) {
						String downnum = "";
						if (ipdiskList.get(6) != null) {
							downnum = (String) ipdiskList.get(6);
						}
						String _downnum = "";
						if (ipdiskList.get(6) != null) {
							_downnum = (String) _ipdiskList.get(6);
						}
						if (new Double(downnum).doubleValue() < new Double(
								_downnum).doubleValue()) {
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
		String pingChartDivStr =  ReportHelper.getChartDivStr(orderList, "ping");
		String responsetimeDivStr =  ReportHelper.getChartDivStr(orderList, "responsetime");

		ReportValue pingReportValue =  ReportHelper.getReportValue(orderList,"ping");
		ReportValue responseReportValue =  ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "连通率", "时间", "");
		String responsetimepath = new ReportExport().makeJfreeChartData(responseReportValue.getListValue(), responseReportValue.getIpList(), "响应时间", "时间", "");
		 
		request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("responsetimepath", responsetimepath);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		session.setAttribute("pinglist", list);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("pinglist", list);
		return "/capreport/host/hostping.jsp";
	}
	
	/**
	 * 获取连通率图形chart的字符串
	 * @param orderList
	 * @return
	 */
	private String getChartDivStr(List orderList, String indicatorName){
		List valueList = new ArrayList();
		List ipList = new ArrayList();
		for(int i=0; i < orderList.size(); i++){
			Hashtable tempHash = (Hashtable)orderList.get(i);
			Hashtable valueHash = null;//连通率hash
			if(indicatorName.equalsIgnoreCase("ping")){
				if(tempHash.containsKey("pinghash")){
					valueHash = (Hashtable)tempHash.get("pinghash");
				}
			}else if(indicatorName.equalsIgnoreCase("responsetime")){
				if(tempHash.containsKey("responsehash")){
					valueHash = (Hashtable)tempHash.get("responsehash");
				}
			}
			//取出连通率的数据值
			List<Vector> dataList = null;
			if(valueHash.containsKey("list")){
				dataList = (List<Vector>)valueHash.get("list");
			}
			valueList.add(dataList);
			ipList.add(tempHash.get("ipaddress"));
		}
		AmChartTool amChartTool = new AmChartTool();
		String pingdata = amChartTool.makeAmChartData(valueList, valueList);
		return pingdata; 
	}

	private String hostevent() {
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
		
		if(ids != null && ids.length>0){
			session.setAttribute("ids", ids);
		}else{
			ids = (String[])session.getAttribute("ids");
		}
		
		Hashtable allcpuhash = new Hashtable();

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				dao.close();
				if (node == null)
					continue;
				EventListDao eventdao = new EventListDao();
				// 得到事件列表
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
						+ totime + "' ");
				s.append(" and nodeid=" + node.getId());

				List infolist = eventdao.findByCriteria(s.toString());

				// if (infolist != null && infolist.size()>0){
				int levelone = 0;
				int levletwo = 0;
				int levelthree = 0;
				int pingvalue = 0;
				int memvalue = 0;
				int diskvalue = 0;
				int cpuvalue = 0;

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

					/*
					 * if(content.indexOf("连通率")>=0){ pingvalue = pingvalue + 1;
					 * }else if(content.indexOf("内存利用率")>=0){ memvalue =
					 * memvalue + 1; }else if(content.indexOf("硬盘利用率")>=0){
					 * diskvalue = diskvalue + 1; }else
					 * if(content.indexOf("CPU利用率")>=0){ cpuvalue = cpuvalue +
					 * 1; }
					 */
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
				String equname = node.getAlias();
				String ip = node.getIpAddress();
				List ipeventList = new ArrayList();
				ipeventList.add(ip);
				ipeventList.add(equname);
				ipeventList.add(node.getType());
				ipeventList.add((levelone + levletwo + levelthree) + "");
				ipeventList.add(levelone + "");
				ipeventList.add(levletwo + "");
				ipeventList.add(levelthree + "");
				ipeventList.add(pingvalue + "");
				ipeventList.add(memvalue + "");
				ipeventList.add(diskvalue + "");
				ipeventList.add(cpuvalue + "");
				orderList.add(ipeventList);

				// }
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("one") || orderflag.equalsIgnoreCase("two")
				|| orderflag.equalsIgnoreCase("three") || orderflag.equalsIgnoreCase("ping")
				|| orderflag.equalsIgnoreCase("mem") || orderflag.equalsIgnoreCase("disk")
				|| orderflag.equalsIgnoreCase("cpu") || orderflag.equalsIgnoreCase("sum")) {
			returnList = (List) session.getAttribute("eventlist");
		} else {
			returnList = orderList;
		}

		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("sum")) {
						String sum = "";
						if (ipdiskList.get(3) != null) {
							sum = (String) ipdiskList.get(3);
						}
						String _sum = "";
						if (ipdiskList.get(3) != null) {
							_sum = (String) _ipdiskList.get(3);
						}
						if (new Double(sum).doubleValue() < new Double(_sum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("one")) {
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
					} else if (orderflag.equalsIgnoreCase("two")) {
						String downnum = "";
						if (ipdiskList.get(5) != null) {
							downnum = (String) ipdiskList.get(5);
						}
						String _downnum = "";
						if (ipdiskList.get(5) != null) {
							_downnum = (String) _ipdiskList.get(5);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("three")) {
						String downnum = "";
						if (ipdiskList.get(6) != null) {
							downnum = (String) ipdiskList.get(6);
						}
						String _downnum = "";
						if (ipdiskList.get(6) != null) {
							_downnum = (String) _ipdiskList.get(6);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("ping")) {
						String downnum = "";
						if (ipdiskList.get(7) != null) {
							downnum = (String) ipdiskList.get(7);
						}
						String _downnum = "";
						if (ipdiskList.get(7) != null) {
							_downnum = (String) _ipdiskList.get(7);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("mem")) {
						String downnum = "";
						if (ipdiskList.get(8) != null) {
							downnum = (String) ipdiskList.get(8);
						}
						String _downnum = "";
						if (ipdiskList.get(8) != null) {
							_downnum = (String) _ipdiskList.get(8);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("disk")) {
						String downnum = "";
						if (ipdiskList.get(9) != null) {
							downnum = (String) ipdiskList.get(9);
						}
						String _downnum = "";
						if (ipdiskList.get(9) != null) {
							_downnum = (String) _ipdiskList.get(9);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("cpu")) {
						String downnum = "";
						if (ipdiskList.get(10) != null) {
							downnum = (String) ipdiskList.get(10);
						}
						String _downnum = "";
						if (ipdiskList.get(10) != null) {
							_downnum = (String) _ipdiskList.get(10);
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
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", list);
		session.setAttribute("eventlist", list);
		return "/capreport/host/hostevent.jsp";
	}

	private String hostmemory() {
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
		
		if(ids != null && ids.length>0){
			session.setAttribute("ids", ids);
		}else{
			ids = (String[])session.getAttribute("ids");
		}
		
		Hashtable allcpuhash = new Hashtable();

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		String runmodel = PollingEngine.getCollectwebflag();

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				dao.close();
				if (node == null)
					continue;
				// Monitoriplist monitoriplist = monitorManager.getById(ids[i]);
				Hashtable memhash = new Hashtable();

				try {
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						memhash = hostlastmanager.getMemory_share(node.getIpAddress(), "Memory", starttime, totime);
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()),
								nodeDTO.getType(), nodeDTO.getSubtype());
						memhash = memoryInfoService.getCurrMemoryListInfo();
						// //取出当前的硬盘信息
						// DiskInfoService diskInfoService = new
						// DiskInfoService(String.valueOf(nodeDTO.getId()),nodeDTO.getType(),nodeDTO.getSubtype());
						// diskhash = diskInfoService.getCurrDiskListInfo();
						// PingInfoService pingInfoService = new
						// PingInfoService(String.valueOf(nodeDTO.getId()),nodeDTO.getType(),nodeDTO.getSubtype());
						// pdata = pingInfoService.getPingInfo();
						// //CPU信息
						// CpuInfoService cpuInfoService = new
						// CpuInfoService(String.valueOf(nodeDTO.getId()),nodeDTO.getType(),nodeDTO.getSubtype());
						// cpuVector = cpuInfoService.getCpuInfo();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable[] memoryhash = null;
				try {
					memoryhash = hostmanager.getMemory(node.getIpAddress(), "Memory", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("node", node);
				ipmemhash.put("memoryhash", memoryhash);
				ipmemhash.put("ipmemhash", memhash);
				// allcpuhash.put(monitoriplist.getIpaddress(), memhash);
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("phyavg") || orderflag.equalsIgnoreCase("phymax")
				|| orderflag.equalsIgnoreCase("viravg") || orderflag.equalsIgnoreCase("virmax")) {
			returnList = (List) session.getAttribute("memlist");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************

			// I_MonitorIpList monitorManager=new MonitoriplistManager();
			// List memlist = (List)session.getAttribute("memlist");
			List memlist = orderList;
			if (memlist != null && memlist.size() > 0) {
				for (int i = 0; i < memlist.size(); i++) {
					Hashtable _memhash = (Hashtable) memlist.get(i);
					HostNode node = (HostNode) _memhash.get("node");
					String osname = node.getType();
					Hashtable[] memoryhash = (Hashtable[]) _memhash.get("memoryhash");
					if (memoryhash == null)
						continue;
					Hashtable memmaxhash = memoryhash[1];
					Hashtable memavghash = memoryhash[2];
					Hashtable memhash = (Hashtable) _memhash.get("ipmemhash");
					if (memhash == null)
						memhash = new Hashtable();

					Hashtable mhash = new Hashtable();
					if (node.getSysOid().startsWith("1.3.6.1.4.1.311")) {
						mhash = (Hashtable) memhash.get(1);
					} else {
						mhash = (Hashtable) memhash.get(0);
					}
					if (mhash == null)
						continue;

					// Hashtable mhash = (Hashtable)memhash.get(0);
					String name = "";
					if (mhash.get("name") != null) {
						name = (String) mhash.get("name");
					}
					String Capability = "";
					if (mhash.get("Capability") != null) {
						Capability = (String) mhash.get("Capability");
					}
					String maxvalue = "";
					if (memmaxhash.get(name) != null) {
						maxvalue = (String) memmaxhash.get(name);
					}
					String avgvalue = "";
					if (memavghash.get(name) != null) {
						avgvalue = (String) memavghash.get(name);
					}
					// mhash = (Hashtable)(memhash.get(1));
					if (node.getSysOid().startsWith("1.3.6.1.4.1.311")) {
						mhash = (Hashtable) memhash.get(0);
					} else {
						mhash = (Hashtable) memhash.get(1);
					}
					if (mhash == null)
						continue;

					String name1 = "";
					if (mhash.get("name") != null) {
						name1 = (String) mhash.get("name");
					}
					String Capability1 = "";
					if (mhash.get("Capability") != null) {
						Capability1 = (String) mhash.get("Capability");
					}
					String maxvalue1 = "";
					if (memmaxhash.get(name1) != null) {
						maxvalue1 = (String) memmaxhash.get(name1);
					}
					String avgvalue1 = "";
					if (memavghash.get(name1) != null) {
						avgvalue1 = (String) memavghash.get(name1);
					}
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					List ipmemList = new ArrayList();
					ipmemList.add(ip);
					ipmemList.add(equname);
					ipmemList.add(node.getType());
					ipmemList.add(Capability);
					ipmemList.add(avgvalue);
					ipmemList.add(maxvalue);
					ipmemList.add(Capability1);
					ipmemList.add(avgvalue1);
					ipmemList.add(maxvalue1);
					returnList.add(ipmemList);
				}
			}
		}

		// **********************************************************

		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipmemList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipmemList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("phyavg")) {
						String memmax = "";
						String avgmem = "";
						if (ipmemList.get(5) != null) {
							memmax = (String) ipmemList.get(5);
						}
						if (ipmemList.get(4) != null) {
							avgmem = (String) ipmemList.get(4);
						}

						String _memmax = "";
						String _avgmem = "";
						if (ipmemList.get(5) != null) {
							_memmax = (String) _ipmemList.get(5);
						}
						if (ipmemList.get(4) != null) {
							_avgmem = (String) _ipmemList.get(4);
						}
						if (new Double(avgmem.substring(0, avgmem.length() - 2)).doubleValue() < new Double(_avgmem
								.substring(0, _avgmem.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					} else if (orderflag.equalsIgnoreCase("phymax")) {
						String memmax = "";
						String avgmem = "";
						if (ipmemList.get(5) != null) {
							memmax = (String) ipmemList.get(5);
						}
						if (ipmemList.get(4) != null) {
							avgmem = (String) ipmemList.get(4);
						}

						String _memmax = "";
						String _avgmem = "";
						if (ipmemList.get(5) != null) {
							_memmax = (String) _ipmemList.get(5);
						}
						if (ipmemList.get(4) != null) {
							_avgmem = (String) _ipmemList.get(4);
						}
						if (new Double(memmax.substring(0, memmax.length() - 2)).doubleValue() < new Double(_memmax
								.substring(0, _memmax.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					} else if (orderflag.equalsIgnoreCase("viravg")) {
						String memmax = "";
						String avgmem = "";
						if (ipmemList.get(8) != null) {
							memmax = (String) ipmemList.get(8);
						}
						if (ipmemList.get(7) != null) {
							avgmem = (String) ipmemList.get(7);
						}

						String _memmax = "";
						String _avgmem = "";
						if (ipmemList.get(8) != null) {
							_memmax = (String) _ipmemList.get(8);
						}
						if (ipmemList.get(7) != null) {
							_avgmem = (String) _ipmemList.get(7);
						}
						if (new Double(avgmem.substring(0, avgmem.length() - 2)).doubleValue() < new Double(_avgmem
								.substring(0, _avgmem.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					} else if (orderflag.equalsIgnoreCase("virmax")) {
						String memmax = "";
						String avgmem = "";
						if (ipmemList.get(8) != null) {
							memmax = (String) ipmemList.get(8);
						}
						if (ipmemList.get(7) != null) {
							avgmem = (String) ipmemList.get(7);
						}

						String _memmax = "";
						String _avgmem = "";
						if (ipmemList.get(8) != null) {
							_memmax = (String) _ipmemList.get(8);
						}
						if (ipmemList.get(7) != null) {
							_avgmem = (String) _ipmemList.get(7);
						}
						if (new Double(memmax.substring(0, memmax.length() - 2)).doubleValue() < new Double(_memmax
								.substring(0, _memmax.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					}
				}
				// 得到排序后的Subentity的列表
				list.add(ipmemList);
				ipmemList = null;
			}
		}

		// setListProperty(capReportForm, request, list);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("memlist", list);
		session.setAttribute("memlist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/capreport/host/hostmemory.jsp";
	}

	private String hostcpu() {
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
		
		if(ids != null && ids.length>0){
			session.setAttribute("ids", ids);
		}else{
			ids = (String[])session.getAttribute("ids");
		}
		
		Hashtable allcpuhash = new Hashtable();

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				dao.close();
				if (node == null)
					continue;
				Hashtable cpuhash = null;
				try {
					cpuhash = hostmanager.getCategory(node.getIpAddress(), "CPU", "Utilization", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				cpuhash.put("ipaddress", node.getIpAddress());
				allcpuhash.put(node.getIpAddress(), cpuhash);
				orderList.add(cpuhash);
			}

		}
		if (orderflag.equalsIgnoreCase("cpuavg") || orderflag.equalsIgnoreCase("cpumax")) {
			orderList = (List) session.getAttribute("cpulist");
		}
		// 对orderList根据theValue进行排序
		// List tempList = new ArrayList();

		List list = new ArrayList();
		if (orderList != null && orderList.size() > 0) {
			for (int m = 0; m < orderList.size(); m++) {
				Hashtable cpuhash = (Hashtable) orderList.get(m);
				for (int n = m + 1; n < orderList.size(); n++) {
					Hashtable _cpuhash = (Hashtable) orderList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
						/*
						 * if (new Double(hdata.getThevalue()).doubleValue()>new
						 * Double(hosdata.getThevalue()).doubleValue()){
						 * orderList.remove(m); orderList.add(m,hosdata);
						 * orderList.remove(n); orderList.add(n,hdata); hdata =
						 * hosdata; hosdata = null; }
						 */
					} else if (orderflag.equalsIgnoreCase("cpuavg")) {
						String cpumax = "";
						String avgcpu = "";
						if (cpuhash.get("max") != null) {
							cpumax = (String) cpuhash.get("max");
						}
						if (cpuhash.get("avgcpucon") != null) {
							avgcpu = (String) cpuhash.get("avgcpucon");
						}
						String _cpumax = "";
						String _avgcpu = "";
						if (_cpuhash.get("max") != null) {
							_cpumax = (String) _cpuhash.get("max");
						}
						if (_cpuhash.get("avgcpucon") != null) {
							_avgcpu = (String) _cpuhash.get("avgcpucon");
						}
						if (new Double(avgcpu.substring(0, avgcpu.length() - 2)).doubleValue() < new Double(_avgcpu
								.substring(0, _avgcpu.length() - 2)).doubleValue()) {
							// if (new
							// Double(hdata.getThevalue()).doubleValue()<new
							// Double(hosdata.getThevalue()).doubleValue()){
							orderList.remove(m);
							orderList.add(m, _cpuhash);
							orderList.remove(n);
							orderList.add(n, cpuhash);
							cpuhash = _cpuhash;
							_cpuhash = null;
						}
					} else if (orderflag.equalsIgnoreCase("cpumax")) {
						String cpumax = "";
						String avgcpu = "";
						if (cpuhash.get("max") != null) {
							cpumax = (String) cpuhash.get("max");
						}
						if (cpuhash.get("avgcpucon") != null) {
							avgcpu = (String) cpuhash.get("avgcpucon");
						}
						String _cpumax = "";
						String _avgcpu = "";
						if (_cpuhash.get("max") != null) {
							_cpumax = (String) _cpuhash.get("max");
						}
						if (_cpuhash.get("avgcpucon") != null) {
							_avgcpu = (String) _cpuhash.get("avgcpucon");
						}
						if (new Double(cpumax.substring(0, avgcpu.length() - 2)).doubleValue() < new Double(_cpumax
								.substring(0, _avgcpu.length() - 2)).doubleValue()) {
							// if (new
							// Double(hdata.getThevalue()).doubleValue()<new
							// Double(hosdata.getThevalue()).doubleValue()){
							orderList.remove(m);
							orderList.add(m, _cpuhash);
							orderList.remove(n);
							orderList.add(n, cpuhash);
							cpuhash = _cpuhash;
							_cpuhash = null;
						}
					}
				}
				// 得到排序后的Subentity的列表
				list.add(cpuhash);
				cpuhash = null;
			}
		}
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("allcpuhash", allcpuhash);
		request.setAttribute("allcpuhash", allcpuhash);
		request.setAttribute("cpulist", list);
		session.setAttribute("cpulist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/capreport/host/hostcpu.jsp";
	}

	private String hostdisk() {
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
		
		if(ids != null && ids.length>0){
			session.setAttribute("ids", ids);
		}else{
			ids = (String[])session.getAttribute("ids");
		}
		
		Hashtable allcpuhash = new Hashtable();

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}
		String runmodel = PollingEngine.getCollectwebflag();

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				dao.close();
				if (node == null)
					continue;
				Hashtable diskhash = null;
				try {
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						diskhash = hostlastmanager.getDisk_share(node.getIpAddress(), "Disk", starttime, totime);
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						// 取出当前的硬盘信息
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
						// PingInfoService pingInfoService = new
						// PingInfoService(String.valueOf(nodeDTO.getId()),nodeDTO.getType(),nodeDTO.getSubtype());
						// pdata = pingInfoService.getPingInfo();
						// //CPU信息
						// CpuInfoService cpuInfoService = new
						// CpuInfoService(String.valueOf(nodeDTO.getId()),nodeDTO.getType(),nodeDTO.getSubtype());
						// cpuVector = cpuInfoService.getCpuInfo();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("node", node);
				ipmemhash.put("diskhash", diskhash);
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("allsize") || orderflag.equalsIgnoreCase("utilization")) {
			returnList = (List) session.getAttribute("disklist");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************

			// I_MonitorIpList monitorManager=new MonitoriplistManager();
			List disklist = orderList;
			if (disklist != null && disklist.size() > 0) {
				for (int i = 0; i < disklist.size(); i++) {
					Hashtable _diskhash = (Hashtable) disklist.get(i);
					HostNode node = (HostNode) _diskhash.get("node");
					String osname = node.getType();
					Hashtable diskhash = (Hashtable) _diskhash.get("diskhash");
					if (diskhash == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					String[] diskItem = { "AllSize", "UsedSize", "Utilization" };
					for (int k = 0; k < diskhash.size(); k++) {
						Hashtable dhash = (Hashtable) (diskhash.get(new Integer(k)));
						String name = "";
						if (dhash.get("name") != null) {
							name = (String) dhash.get("name");
						}
						String allsizevalue = "";
						String usedsizevalue = "";
						String utilization = "";
						if (dhash.get("AllSize") != null) {
							allsizevalue = (String) dhash.get("AllSize");
						}
						if (dhash.get("UsedSize") != null) {
							usedsizevalue = (String) dhash.get("UsedSize");
						}
						if (dhash.get("Utilization") != null) {
							utilization = (String) dhash.get("Utilization");
						}

						List ipdiskList = new ArrayList();
						ipdiskList.add(ip);
						ipdiskList.add(equname);
						ipdiskList.add(node.getType());
						ipdiskList.add(name);
						ipdiskList.add(allsizevalue);
						ipdiskList.add(usedsizevalue);
						ipdiskList.add(utilization);
						returnList.add(ipdiskList);

					}
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
					} else if (orderflag.equalsIgnoreCase("allsize")) {
						String allsizevalue = "";
						if (ipdiskList.get(4) != null) {
							allsizevalue = (String) ipdiskList.get(4);
						}
						String _allsizevalue = "";
						if (ipdiskList.get(4) != null) {
							_allsizevalue = (String) _ipdiskList.get(4);
						}
						if (new Double(allsizevalue.substring(0, allsizevalue.length() - 2)).doubleValue() < new Double(
								_allsizevalue.substring(0, _allsizevalue.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("utilization")) {
						String utilization = "";
						if (ipdiskList.get(6) != null) {
							utilization = (String) ipdiskList.get(6);
						}
						String _utilization = "";
						if (ipdiskList.get(6) != null) {
							_utilization = (String) _ipdiskList.get(6);
						}
						if (new Double(utilization.substring(0, utilization.length() - 2)).doubleValue() < new Double(
								_utilization.substring(0, _utilization.length() - 2)).doubleValue()) {
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

		// setListProperty(capReportForm, request, list);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("disklist", list);
		session.setAttribute("disklist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/capreport/host/hostdisk.jsp";
	}

	private String downloadPingReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			report.createExcelReport_ping("temp/hostnms_report.xls");
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			// ExcelReport1 report1 = new ExcelReport1(new
			// IpResourceReport(),memhash);
			try {
				String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_pingDoc(fileName);// word综合报表
				// report1.createReport_hostmem(fileName);
				request.setAttribute("filename", fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostNewDoc(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_hostNewPDF(fileName);// pdf业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				// report1.createReport_hostPDF(fileName);// pdf综合报表
				report1.createReport_pingPDF(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "/capreport/net/download.jsp";

		/*
		 * Date d = new Date(); String startdate = getParaValue("startdate");
		 * if(startdate==null){ startdate = sdf0.format(d); } String todate =
		 * getParaValue("todate"); if(todate==null){ todate = sdf0.format(d); }
		 * String starttime = startdate + " 00:00:00"; String totime = todate + "
		 * 23:59:59"; Hashtable allcpuhash = new Hashtable();
		 * 
		 * List returnList = new ArrayList(); //I_MonitorIpList
		 * monitorManager=new MonitoriplistManager(); List pinglist =
		 * (List)session.getAttribute("pinglist"); Hashtable reporthash = new
		 * Hashtable(); reporthash.put("pinglist", pinglist);
		 * reporthash.put("starttime", starttime); reporthash.put("totime",
		 * totime);
		 * 
		 * AbstractionReport1 report = new ExcelReport1(new
		 * IpResourceReport(),reporthash);
		 * report.createReport_hostping("/temp/hostping_report.xls");
		 * request.setAttribute("filename", report.getFileName()); return
		 * "/capreport/net/download.jsp";
		 */
		// return mapping.findForward("report_info");
	}

	private String downloadCapacityReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		// 画图-----------------------------
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			report.createExcelReport_capacity("temp/hostnms_report.xls");
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			// ExcelReport1 report1 = new ExcelReport1(new
			// IpResourceReport(),memhash);
			try {
				String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_capacityDoc(fileName);// word综合报表
				// report1.createReport_hostmem(fileName);
				request.setAttribute("filename", fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostNewDoc(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_hostNewPDF(fileName);// pdf业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				// report1.createReport_hostPDF(fileName);// pdf综合报表
				report1.createReport_capacityPDF(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "/capreport/net/download.jsp";
	}

	private String downloadmemreport() {
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

		List returnList = new ArrayList();
		List memlist = (List) session.getAttribute("memlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("memlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostmem("/temp/hostmem_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}

	private String downloaddiskreport() {
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

		List returnList = new ArrayList();
		List memlist = (List) session.getAttribute("disklist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("disklist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostdisk("/temp/hostdisk_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}

	private String downloadcpureport() {
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

		List orderList = new ArrayList();
		orderList = (List) session.getAttribute("cpulist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("cpulist", orderList);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostcpu("/temp/hostcpu_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}

	private String downloadeventreport() {
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

		List eventlist = (List) session.getAttribute("eventlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", eventlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostevent("/temp/hostping_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}

	private String downloadneteventreport() {
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

		List returnList = new ArrayList();
		// I_MonitorIpList monitorManager=new MonitoriplistManager();
		List memlist = (List) session.getAttribute("eventlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_netevent("/temp/hostping_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	private String downlaodDiskReport() {

		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			report.createExcelReport_disk("temp/hostnms_report.xls");
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			// ExcelReport1 report1 = new ExcelReport1(new
			// IpResourceReport(),memhash);
			try {
				String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_diskDoc(fileName);// word综合报表
				// report1.createReport_hostmem(file);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostNewDoc(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_hostNewPDF(fileName);// pdf业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				// report1.createReport_hostPDF(fileName);// pdf综合报表
				report1.createReport_diskPDF(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "/capreport/net/download.jsp";
	}

	private String downloadCompositeReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			// ExcelReport1 report1 = new ExcelReport1(new
			// IpResourceReport(),memhash);
			try {
				String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostDoc(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostNewDoc(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_hostNewPDF(fileName);// pdf业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostPDF(fileName);// pdf综合报表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "/capreport/net/download.jsp";
	}

	private String downloadAnalyseReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			report.createReport_host("temp/hostnms_report.xls");// excel综合报表
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			// ExcelReport1 report1 = new ExcelReport1(new
			// IpResourceReport(),memhash);
			try {
				String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostDoc(fileName);// word综合报表
				// report1.createReport_hostmem(file);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostNewDoc(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_hostNewPDF(fileName);// pdf业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostPDF(fileName);// pdf综合报表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("5".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			// ExcelReport1 report1 = new ExcelReport1(new
			// IpResourceReport(),memhash);
			try {
				String file = "temp/hostAnalyseReport.xls";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_hostNewXls(fileName);// word综合报表
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "/capreport/net/download.jsp";
	}

	private String downloadHardwareReport() {
		String str = (String) request.getParameter("str");
		Vector deviceV = (Vector) session.getAttribute("deviceV");
		Hashtable reporthash = new Hashtable();
		if (deviceV != null) {
			reporthash.put("deviceV", deviceV);
		} else {
			deviceV = new Vector();
			reporthash.put("deviceV", deviceV);
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
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.setRequest(request);
		String fileName = ResourceCenter.getInstance().getSysPath();
		if (str.equals("1")) {
			String filename = fileName + "temp/host_hardware_report.doc";
			report.createWordReport_hardware(filename);
			SysLogger.info("filename" + report.getFileName());
			System.out.println(report.getFileName() + "111111111111111111");
			request.setAttribute("filename", report.getFileName());
		}
		if (str.equals("0")) {
			String filename = fileName + "temp/host_hardware_report.xls";
			report.createExcelReport_hardware(filename);
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		}
		if (str.equals("4")) {
			String filename = fileName + "temp/host_hardware_report.pdf";
			report.createPdfReport_hardware(filename);
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());

		}
		return "/capreport/net/download.jsp";
	}

	/**
	 * @return
	 */
	private String downloadHardwareReportNew() {
		String str = (String) request.getParameter("str");
		List networkList = (List) session.getAttribute("networkList");
		List serverList = (List) session.getAttribute("serverList");
		List dbList = (List) session.getAttribute("dbList");
		List midwareList = (List) session.getAttribute("midwareList");
		Hashtable reporthash = new Hashtable();
		reporthash.put("networkList", networkList);
		reporthash.put("serverList", serverList);
		reporthash.put("dbList", dbList);
		reporthash.put("midwareList", midwareList);
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
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.setRequest(request);
		String fileName = ResourceCenter.getInstance().getSysPath();
		if (str.equals("1")) {
			String filename = fileName + "temp/host_hardware_report.doc";
			report.createReport_hardwareNew(filename, "doc");
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if (str.equals("0")) {
			String filename = fileName + "temp/host_hardware_report.xls";
			report.createExcelReport_hardwareNew(filename);
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if (str.equals("4")) {
			String filename = fileName + "temp/host_hardware_report.pdf";
			report.createReport_hardwareNew(filename, "pdf");
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		}
		return "/capreport/net/download.jsp";
	}

	private String showPingReport() {
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		// Hashtable hash = new Hashtable();// "Cpu",--current
		// Hashtable memhash = new Hashtable();// mem--current
		// Hashtable diskhash = new Hashtable();
		// Hashtable memmaxhash = new Hashtable();// mem--max
		// Hashtable memavghash = new Hashtable();// mem--avg
		// Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Vector pdata = new Vector();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			String runmodel = PollingEngine.getCollectwebflag();
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			// memhash = hostlastmanager.getMemory_share(ip, "Memory",
			// starttime,
			// totime);
			// diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime,
			// totime);
			// // 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			// Hashtable cpuhash = hostmanager.getCategory(ip, "CPU",
			// "Utilization", starttime, totime);
			// Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory",
			// starttime, totime);
			// 各memory最大值
			// memmaxhash = memoryhash[1];
			// memavghash = memoryhash[2];
			// // cpu最大值
			// maxhash = new Hashtable();
			// String cpumax = "";
			// String avgcpu = "";
			// if (cpuhash.get("max") != null) {
			// cpumax = (String) cpuhash.get("max");
			// }
			// if (cpuhash.get("avgcpucon") != null) {
			// avgcpu = (String) cpuhash.get("avgcpucon");
			// }

			// maxhash.put("cpumax", cpumax);
			// maxhash.put("avgcpu", avgcpu);

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			// StringBuffer s = new StringBuffer();
			// s.append("select * from system_eventlist where recordtime>= '"
			// + starttime + "' " + "and recordtime<='" + totime + "' ");
			// s.append(" and nodeid=" + node.getId());
			//
			// EventListDao eventdao = new EventListDao();
			// List infolist = eventdao.findByCriteria(s.toString());
			//
			// if (infolist != null && infolist.size() > 0) {
			//
			// for (int j = 0; j < infolist.size(); j++) {
			// EventList eventlist = (EventList) infolist.get(j);
			// if (eventlist.getContent() == null)
			// eventlist.setContent("");
			// String content = eventlist.getContent();
			// if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
			// pingvalue = pingvalue + 1;
			// } else if (eventlist.getSubentity().equalsIgnoreCase(
			// "memory")) {
			// memvalue = memvalue + 1;
			// } else if (eventlist.getSubentity()
			// .equalsIgnoreCase("disk")) {
			// diskvalue = diskvalue + 1;
			// } else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
			// cpuvalue = cpuvalue + 1;
			// }
			// }
			// }
			// // 运行评价===========================
			// String grade = "优";
			// if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
			// grade = "良";
			// }
			//
			// if (pingvalue > 0) {
			// grade = "差";
			// }
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			// request.setAttribute("hash", hash);
			// request.setAttribute("max", maxhash);
			// request.setAttribute("memmaxhash", memmaxhash);
			// request.setAttribute("memavghash", memavghash);
			// request.setAttribute("diskhash", diskhash);
			// request.setAttribute("memhash", memhash);
			// request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				pdata = (Vector) pingdata.get(ip);
			} else {
				// 采集与访问是分离模式
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(node);
				pdata = new PingInfoService(nodedto.getId() + "", nodedto.getType(), nodedto.getSubtype())
						.getPingInfo();
			}
			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
						reporthash.put("Ping", hostdata.getThevalue());
						reporthash.put("ping", maxping);
					}
				}
			}
			// // CPU
			// Hashtable hdata = (Hashtable) sharedata.get(ip);
			// if (hdata == null)
			// hdata = new Hashtable();
			// Vector cpuVector = (Vector) hdata.get("cpu");
			// if (cpuVector != null && cpuVector.size() > 0) {
			// for (int si = 0; si < cpuVector.size(); si++) {
			// CPUcollectdata cpudata = (CPUcollectdata) cpuVector
			// .elementAt(si);
			// maxhash.put("cpu", cpudata.getThevalue());
			// reporthash.put("CPU", maxhash);
			// }
			// } else {
			// reporthash.put("CPU", maxhash);
			// }
			//
			// reporthash.put("Memory", memhash);
			// reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			// reporthash.put("equipnameDoc", equipnameDoc);
			// reporthash.put("memmaxhash", memmaxhash);
			// reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			// reporthash.put("grade", grade);
			// if ("host".equals(type)) {
			// typename = "服务器";
			//
			// }
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率",
				newip + "ConnectUtilization", 740, 150);
			// pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750,
			// 150);
			// pollMonitorManager.draw_column(diskhash, "", newip + "disk",
			// 750,150);
			// pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip
			// + "memory", 750, 150);
			// 画图-----------------------------
			session.setAttribute("reporthash", reporthash);
			if((Hashtable) reporthash.get("ping") == null){
				request.setAttribute("pingmax", "");
			}else{
				request.setAttribute("pingmax", ((Hashtable) reporthash.get("ping")).get("pingmax"));
			}
//			request.setAttribute("pingmax", ((Hashtable) reporthash.get("ping")).get("pingmax"));
			request.setAttribute("Ping", reporthash.get("Ping"));
			if((Hashtable) reporthash.get("ping") == null){
				request.setAttribute("avgpingcon", "");
			}else{
				request.setAttribute("avgpingcon", ((Hashtable) reporthash.get("ping")).get("avgpingcon"));
			}
//			request.setAttribute("avgpingcon", ((Hashtable) reporthash.get("ping")).get("avgpingcon"));
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("nodeid", node.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showPingReport.jsp";
	}

	/**
	 * @author HONGLI 2010-10-27 根据日期查询ORACLE数据库可用性
	 */
	private String showPingReportByDate() {

		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";// 开始日期
		String totime = todate + " 23:59:59";// 截止日期
		String ip = getParaValue("ipaddress");// ip地址
		String type = getParaValue("type");// 类型
		String sid = getParaValue("sid");
		// SysLogger.info("######HONG starttime-totime-ip-sid----" + starttime
		// + " - " + totime + " - " + ip + " - " + sid);
		// 取该oracle数据库的ping数据
		DBVo vo = new DBVo();
		// Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		// Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		// Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null, chart2 = null, chart3 = null, responseTime = null;
		// String sid=this.getParaValue("sid");
		String id = (String) session.getAttribute("id");
		// String sid = (String) session.getAttribute("sid");
		String pingmin = "";// HONGLI ADD 最小连通率
		String pingnow = "0.0";// HONGLI ADD 当前连通率
		try {
			DBDao dao = new DBDao();
			try {
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			OraclePartsDao oracledao = new OraclePartsDao();
			try {
				OracleEntity oracle = (OracleEntity) oracledao.getOracleById(Integer.parseInt(sid));
				vo.setDbName(oracle.getSid());
				vo.setCollecttype(oracle.getCollectType());
				vo.setManaged(oracle.getManaged());
				vo.setBid(oracle.getBid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oracledao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0)
				managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			/* modify zhao ---------------------------- */
			// Hashtable alloracledata = ShareData.getAlloracledata();
			// Hashtable iporacledata = new Hashtable();
			// if (alloracledata != null && alloracledata.size() > 0) {
			// if (alloracledata.containsKey(vo.getIpAddress() + ":" + sid)) {
			// iporacledata = (Hashtable) alloracledata.get(vo
			// .getIpAddress()
			// + ":" + sid);
			// if (iporacledata.containsKey("status")) {
			// String sta = (String) iporacledata.get("status");
			// if ("1".equals(sta)) {
			// runstr = "正在运行";
			// pingnow = "100.0";//HONGLI ADD
			// }
			// }
			// if (iporacledata.containsKey("cursors")) {
			// cursors = (Hashtable) iporacledata.get("cursors");
			// }
			// }
			// if (iporacledata.containsKey("sysValue")) {
			// sysValue = (Hashtable) iporacledata.get("sysValue");
			// }
			// if (iporacledata.containsKey("tableinfo_v")) {
			// tableinfo_v = (Vector) iporacledata.get("tableinfo_v");
			// }
			// if (iporacledata.containsKey("dbio")) {
			// dbio = (Hashtable) iporacledata.get("dbio");
			// }
			// if (iporacledata.containsKey("cursors")) {
			// cursors = (Hashtable) iporacledata.get("cursors");
			// }
			// if (iporacledata.containsKey("lstrnStatu")) {
			// lstrnStatu = (String) iporacledata.get("lstrnStatu");
			// }
			// if (iporacledata.containsKey("isArchive_h")) {
			// isArchive_h = (Hashtable) iporacledata.get("isArchive_h");
			// }
			// }
			// request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			// Hashtable memPerfValue = new Hashtable();
			// if (iporacledata.containsKey("memPerfValue")) {
			// memPerfValue = (Hashtable) iporacledata.get("memPerfValue");
			// }
			// request.setAttribute("memPerfValue", memPerfValue);
			// if (iporacledata.containsKey("sysValue")) {
			// sysValue = (Hashtable) iporacledata.get("sysValue");
			// }
			// request.setAttribute("sysvalue", sysValue);
			// 2010-HONGLI
			// Hashtable memPerfValue = new Hashtable();
			dao = new DBDao();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
			// memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
			// sysValue = dao.getOracle_nmsorasys(serverip);
			String statusStr = String.valueOf(statusHashtable.get("status"));
			lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
			isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
			if ("1".equals(statusStr)) {
				pingnow = "100.0";// HONGLI ADD
			}
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			session.setAttribute("Mytime1", time1);
			session.setAttribute("Mystarttime1", startdate);
			session.setAttribute("Mytotime1", todate);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing",
					"ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			pingmin = (String) ConnectUtilizationhash.get("pingmax");// HONGLI
			// ADD
			// 最小连通率
			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

			// 画图
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);

			// pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750,
			// 150);
			// pollMonitorManager.draw_column(diskhash, "", newip + "disk",
			// 750,150);
			// pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip
			// + "memory", 750, 150);
			// 画图-----------------------------
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		// request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		// request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		request.setAttribute("pingmin", pingmin);// HONGLI ADD 最小连通率
		request.setAttribute("pingnow", pingnow);// HONGLI ADD 当前连通率
		return "/capreport/db/showDbPingReport.jsp";

	}

	/**
	 * @author HONGLI 2010-10-27 根据时间查询，oracle数据库性能报表
	 * @return
	 */
	private String showOraclPerformaceReportByDate() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";// 开始日期
		String totime = todate + " 23:59:59";// 截止日期
		String ip = getParaValue("ipaddress");// ip地址
		String type = getParaValue("type");// 类型
		String sid = getParaValue("sid");
		SysLogger
				.info("######HONG starttime-totime-ip-sid----" + starttime + " - " + totime + " - " + ip + " - " + sid);

		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null, chart2 = null, chart3 = null, responseTime = null;
		// String sid=this.getParaValue("sid");
		String id = (String) session.getAttribute("id");
		String pingnow = "0.0";
		try {
			DBDao dao = new DBDao();
			try {
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			OraclePartsDao oracledao = new OraclePartsDao();
			try {
				OracleEntity oracle = (OracleEntity) oracledao.getOracleById(Integer.parseInt(sid));
				vo.setDbName(oracle.getSid());
				vo.setCollecttype(oracle.getCollectType());
				vo.setManaged(oracle.getManaged());
				vo.setBid(oracle.getBid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oracledao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0)
				managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			/* modify zhao ---------------------------- */
			// Hashtable alloracledata = ShareData.getAlloracledata();
			// Hashtable iporacledata = new Hashtable();
			// if(alloracledata != null && alloracledata.size()>0){
			// if(alloracledata.containsKey(vo.getIpAddress()+":"+sid)){
			// iporacledata = (Hashtable)alloracledata.get(ip+":"+sid);
			// if(iporacledata.containsKey("status")){
			// String sta=(String)iporacledata.get("status");
			// if("1".equals(sta)){
			// runstr = "正在运行";
			// pingnow = "100.0";
			// }
			// }
			// if(iporacledata.containsKey("cursors")){
			// cursors=(Hashtable)iporacledata.get("cursors");
			// }
			// }
			// if(iporacledata.containsKey("sysValue")){
			// sysValue=(Hashtable)iporacledata.get("sysValue");
			// }
			// if(iporacledata.containsKey("tableinfo_v")){
			// tableinfo_v=(Vector)iporacledata.get("tableinfo_v");
			// }
			// if(iporacledata.containsKey("dbio")){
			// dbio=(Hashtable)iporacledata.get("dbio");
			// }
			// // if(iporacledata.containsKey("cursors")){
			// // cursors=(Hashtable)iporacledata.get("cursors");
			// // }
			// if(iporacledata.containsKey("lstrnStatu")){
			// lstrnStatu=(String)iporacledata.get("lstrnStatu");
			// }
			// if(iporacledata.containsKey("isArchive_h")){
			// isArchive_h=(Hashtable)iporacledata.get("isArchive_h");
			// }
			// }
			// 2010-HONGLI
			Hashtable memPerfValue = new Hashtable();
			dao = new DBDao();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
			// memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
			// sysValue = dao.getOracle_nmsorasys(serverip);
			String statusStr = String.valueOf(statusHashtable.get("status"));
			lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
			// isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
			dbio = dao.getOracle_nmsoradbio(serverip);
			tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
			// waitv = dao.getOracle_nmsorawait(serverip);
			// userinfo_h = dao.getOracle_nmsorauserinfo(serverip);
			// sessioninfo_v = dao.getOracle_nmsorasessiondata(serverip);
			// lockinfo_v = dao.getOracle_nmsoralock(serverip);
			// table_v = dao.getOracle_nmsoratables(serverip);
			// contrFile_v = dao.getOracle_nmsoracontrfile(serverip);
			// logFile_v = dao.getOracle_nmsoralogfile(serverip);
			// extent_v = dao.getOracle_nmsoraextent(serverip);
			// keepObj_v = dao.getOracle_nmsorakeepobj(serverip);
			cursors = dao.getOracle_nmsoracursors(serverip);
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
				pingnow = "100.0";// HONGLI ADD 当前连通率
			}
			dao.close();
			request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			// request.setAttribute("isArchive_h", isArchive_h);
			// Hashtable memPerfValue = new Hashtable();
			// if(iporacledata.containsKey("memPerfValue"))
			// {
			// memPerfValue=(Hashtable)iporacledata.get("memPerfValue");
			// }
			// request.setAttribute("memPerfValue", memPerfValue);
			// if(iporacledata.containsKey("sysValue")){
			// sysValue=(Hashtable)iporacledata.get("sysValue");
			// }
			// request.setAttribute("sysvalue", sysValue);
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			session.setAttribute("Mytime1", time1);
			session.setAttribute("Mystarttime1", startdate);
			session.setAttribute("Mytotime1", todate);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing",
					"ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");// ll
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			// 画图
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);
			String pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
			String pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			request.setAttribute("avgpingcon", avgpingcon);// 平均连通率
			// double notpingcon = 100 - avgpingcon;
			request.setAttribute("notpingcon", 100 - avgpingcon);
			request.setAttribute("pingmin", pingmin);// 最小连通率
			request.setAttribute("pingmax", pingmax);// 最大连通率
			request.setAttribute("pingnow", pingnow);// 当前连通率
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);

		return "/capreport/db/showDbReport.jsp";
	}

	/**
	 * @author HONGLI ADD 按时间查询oracle综合性能报表
	 * @return
	 */
	private String showDbOracleCldReportByDate() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("todate", todate);
		request.setAttribute("startdate", startdate);
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Hashtable allcpuhash = new Hashtable();
		String ip = "";
		String ip2 = "";
		String dbname = "";

		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null, chart2 = null, chart3 = null, responseTime = null;
		// String sid=this.getParaValue("sid");
		String sid = (String) session.getAttribute("sid");// HONGLI ADD
		Hashtable memValue = new Hashtable();// HONGLI ADD 数据库的内存配置信息
		String pingnow = "0.0";// HONGLI ADD 当前连通率
		String pingmin = "0.0";// HONGLI ADD 最小连通率
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			OraclePartsDao oracledao = new OraclePartsDao();
			try {
				OracleEntity oracle = (OracleEntity) oracledao.getOracleById(Integer.parseInt(sid));
				vo.setDbName(oracle.getSid());
				vo.setCollecttype(oracle.getCollectType());
				vo.setManaged(oracle.getManaged());
				vo.setBid(oracle.getBid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oracledao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0)
				managed = "未管理";
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			/* modify zhao ---------------------------- */
			// Hashtable alloracledata = ShareData.getAlloracledata();
			// Hashtable iporacledata = new Hashtable();
			// if(alloracledata != null && alloracledata.size()>0){
			// if(alloracledata.containsKey(vo.getIpAddress()+":"+sid)){
			// iporacledata =
			// (Hashtable)alloracledata.get(vo.getIpAddress()+":"+sid);
			// if(iporacledata.containsKey("status")){
			// String sta=(String)iporacledata.get("status");
			// if("1".equals(sta)){
			// runstr = "正在运行";
			// pingnow = "100.0";//HONGLI ADD
			// }
			// }
			// if(iporacledata.containsKey("cursors")){
			// cursors=(Hashtable)iporacledata.get("cursors");
			// }
			// }
			// if(iporacledata.containsKey("sysValue")){
			// sysValue=(Hashtable)iporacledata.get("sysValue");
			// }
			// if(iporacledata.containsKey("tableinfo_v")){
			// tableinfo_v=(Vector)iporacledata.get("tableinfo_v");
			// }
			// if(iporacledata.containsKey("dbio")){
			// dbio=(Hashtable)iporacledata.get("dbio");
			// }
			// if(iporacledata.containsKey("cursors")){
			// cursors=(Hashtable)iporacledata.get("cursors");
			// }
			// if(iporacledata.containsKey("lstrnStatu")){
			// lstrnStatu=(String)iporacledata.get("lstrnStatu");
			// }
			// if(iporacledata.containsKey("isArchive_h")){
			// isArchive_h=(Hashtable)iporacledata.get("isArchive_h");
			// }
			// //HONGLI ADD START1
			// if(iporacledata.containsKey("memValue")){
			// memValue = (Hashtable)iporacledata.get("memValue");
			// }
			// //HONGLI ADD END1
			// }
			// 2010-HONGLI
			Hashtable memPerfValue = new Hashtable();
			dao = new DBDao();
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
			memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
			sysValue = dao.getOracle_nmsorasys(serverip);
			String statusStr = String.valueOf(statusHashtable.get("status"));
			lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
			isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
			memValue = dao.getOracle_nmsoramemvalue(serverip);
			dbio = dao.getOracle_nmsoradbio(serverip);
			tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
			// waitv = dao.getOracle_nmsorawait(serverip);
			// userinfo_h = dao.getOracle_nmsorauserinfo(serverip);
			// sessioninfo_v = dao.getOracle_nmsorasessiondata(serverip);
			// lockinfo_v = dao.getOracle_nmsoralock(serverip);
			// table_v = dao.getOracle_nmsoratables(serverip);
			// contrFile_v = dao.getOracle_nmsoracontrfile(serverip);
			// logFile_v = dao.getOracle_nmsoralogfile(serverip);
			// extent_v = dao.getOracle_nmsoraextent(serverip);
			// keepObj_v = dao.getOracle_nmsorakeepobj(serverip);
			cursors = dao.getOracle_nmsoracursors(serverip);
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
				pingnow = "100.0";// HONGLI ADD
			}
			dao.close();
			// HONGLI ADD START2
			// 去除单位MB\KB
			String[] sysItem = { "shared_pool", "large_pool", "buffer_cache", "java_pool",
					"aggregate_PGA_target_parameter", "total_PGA_allocated", "maximum_PGA_allocated" };
			DecimalFormat df = new DecimalFormat("#.##");
			if (memValue != null) {
				for (int i = 0; i < sysItem.length; i++) {
					String value = "";
					if (memValue.get(sysItem[i]) != null) {
						value = (String) memValue.get(sysItem[i]);
					}
					if (!value.equals("")) {
						if (value.indexOf("MB") != -1) {
							value = value.replace("MB", "");
						}
						if (value.indexOf("KB") != -1) {
							value = value.replace("KB", "");
						}
					} else {
						value = "0";
					}
					memValue.put(sysItem[i], df.format(Double.parseDouble(value)));
				}
			}
			request.setAttribute("memValue", memValue);
			// HONGLI ADD END2
			request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			// Hashtable memPerfValue = new Hashtable();
			// if(iporacledata.containsKey("memPerfValue"))
			// {
			// memPerfValue=(Hashtable)iporacledata.get("memPerfValue");
			// }
			request.setAttribute("memPerfValue", memPerfValue);
			// if(iporacledata.containsKey("sysValue")){
			// sysValue=(Hashtable)iporacledata.get("sysValue");
			// }
			request.setAttribute("sysvalue", sysValue);
			/*---------------modify  end ------------------*/
			/*---modify  zhao 注释掉*/
			/*
			 * dao = new DBDao(); try{ if(dao.getOracleIsOK(vo.getIpAddress(),
			 * Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(),
			 * vo.getPassword())){ runstr = "正在运行"; } }catch(Exception e){
			 * e.printStackTrace(); }finally{ dao.close(); }
			 */
			/*
			 * dao = new DBDao(); try{ sysValue =
			 * dao.getOracleSys(vo.getIpAddress(),Integer.parseInt(vo.getPort()),vo.getDbName(),vo.getUser(),vo.getPassword());
			 * }catch(Exception e){ e.printStackTrace(); }finally{ dao.close(); }
			 */
			/*---------------modify end ------------------------*/
			request.setAttribute("runstr", runstr);

			/*
			 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String
			 * time1 = sdf.format(new Date()); String
			 * newip=SysUtil.doip(vo.getIpAddress());
			 * 
			 * String starttime1 = time1 + " 00:00:00"; String totime1 = time1 + "
			 * 23:59:59";
			 */

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing",
					"ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingmin = (String) ConnectUtilizationhash.get("pingmax");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
				pingmin = pingmin.replace("%", "");
			}
			// 画图
			// p_draw_line(responsehash,"连通率",newip+"response",750,200);
			String newip = SysUtil.doip(vo.getIpAddress());
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			/*----------modify  zhao-------------*/
			/*
			 * dao = new DBDao(); try{ tableinfo_v =
			 * dao.getOracleTableinfo(vo.getIpAddress(),Integer.parseInt(vo.getPort()),vo.getDbName(),vo.getUser(),vo.getPassword());
			 * }catch(Exception e){ e.printStackTrace(); }finally{ dao.close(); }
			 */
			/*-----------modify  end---------------*/
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

			// 求oracle告警次数
			String downnum = "0";
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = hostmanager.getCategory(vo.getIpAddress() + ":" + sid, "ORAPing", "ConnectUtilization",
					starttime, totime);
				if (pinghash.get("downnum") != null)
					downnum = (String) pinghash.get("downnum");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// ========end downnum
			// 表空间==========告警
			DBTypeDao dbTypeDao = new DBTypeDao();
			int count = 0;
			try {
				count = dbTypeDao.finddbcountbyip(ip);
				request.setAttribute("count", count);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbTypeDao.close();
			}
			// 数据库运行等级=====================
			String grade = "优";
			if (count > 0) {
				grade = "良";
			}

			if (!"0".equals(downnum)) {
				grade = "差";
			}
			request.setAttribute("grade", grade);
			request.setAttribute("downnum", downnum);

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user
							.getBusinessids(), Integer.parseInt(sid));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = SysUtil.doip(vo.getIpAddress());// HONGLI ADD
		request.setAttribute("newip", newip);// HONGLI ADD
		request.setAttribute("list", eventList);
		request.setAttribute("ipaddresid", vo.getIpAddress() + ":" + sid);// HONGLI
		// ADD
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon + "");
		request.setAttribute("pingmin", pingmin);// HONGLI ADD 最小连通率
		request.setAttribute("pingnow", pingnow);// HONGLI ADD 当前连通率

		return "/capreport/db/showDbOracleCldReport.jsp";
	}

	/**
	 * @author HONGLI ADD 2010-11-2 根据时间查询oracle事件报表
	 * @return
	 */
	private String showDbOracleEventReport() {
		String ip = request.getParameter("ipaddress");
		String id = request.getParameter("id");
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
		// DBCapReportManager dbcrm = new DBCapReportManager();
		request.setAttribute("todate", todate);
		request.setAttribute("startdate", startdate);
		request.setAttribute("id", id);
		Sdbevent(startdate, todate, id);
		return "/capreport/db/showOraEventReport.jsp";
	}

	/**
	 * @author HONGLI ADD 2010-11-2
	 * @param startdate
	 * @param todate
	 * @param ids
	 */
	private void Sdbevent(String startdate, String todate, String ids) {
		String ip = null;
		String tyname = null;
		int pingvalue = 0;
		String dbname = null;
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		// 按排序标志取各端口最新记录的列表
		DBTypeDao typedao = new DBTypeDao();
		DBDao dao = new DBDao();
		DBVo vo = null;
		DBTypeVo typevo = null;
		try {
			vo = (DBVo) dao.findByID(ids);
			typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			tyname = typevo.getDbtype();
			ip = vo.getIpAddress();
			dbname = vo.getDbName();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			typedao.close();
		}
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
				+ totime + "' ");
		s.append(" and nodeid=" + vo.getId());
		s.append(" and content like '%数据库服务停止%'");
		List infolist = eventdao.findByCriteria(s.toString());
		if (infolist != null && infolist.size() != 0) {
			pingvalue = infolist.size();
		}
		session.setAttribute("_tyname", tyname);
		session.setAttribute("_ip", ip);
		session.setAttribute("_dbname", dbname);
		session.setAttribute("_pingvalue", pingvalue);
	}

	private String showCompositeReport() {
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		// Hashtable pingdata = new Hashtable();
		// Hashtable sharedata = ShareData.getSharedata();
		Vector pdata = new Vector();
		Vector cpuVector = new Vector();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			String runmodel = PollingEngine.getCollectwebflag();
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				Hashtable sharedata = ShareData.getSharedata();
				memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
				diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
				Hashtable pingdata = ShareData.getPingdata();
				pdata = (Vector) pingdata.get(ip);
				Hashtable hdata = (Hashtable) sharedata.get(ip);
				if (hdata == null)
					hdata = new Hashtable();
				cpuVector = (Vector) hdata.get("cpu");
			} else {
				// 采集与访问是分离模式
				// 取出当前的内存信息
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
				MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				memhash = memoryInfoService.getCurrMemoryListInfo();
				// 取出当前的硬盘信息
				DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				diskhash = diskInfoService.getCurrDiskListInfo();
				PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				pdata = pingInfoService.getPingInfo();
				// CPU信息
				CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(),
						nodeDTO.getSubtype());
				cpuVector = cpuInfoService.getCpuInfo();
			}
			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			

			if(SystemConstant.DBType.equals("mysql")){
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
						+ totime + "' ");
			}else if(SystemConstant.DBType.equalsIgnoreCase("oracle")){
				s.append("select * from system_eventlist where recordtime>=to_date( '" + starttime + "','yyyy-mm-dd hh24:mi:ss') " + "and recordtime<=to_date('"
						+ totime + "','yyyy-mm-dd hh24:mi:ss') ");
			}
			s.append(" and nodeid=" + node.getId());

			EventListDao eventdao = new EventListDao();
//			System.out.println(s);
			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
			}
			// 运行评价===========================
			String grade = "优";
			if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
				grade = "良";
			}

			if (pingvalue > 0) {
				grade = "差";
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);
			request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
						reporthash.put("Ping", hostdata.getThevalue());
						reporthash.put("ping", maxping);
					}
				}
			}
			// CPU
			if (cpuVector != null && cpuVector.size() > 0) {
				for (int si = 0; si < cpuVector.size(); si++) {
					CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
					maxhash.put("cpu", cpudata.getThevalue());
					reporthash.put("CPU", maxhash);
				}
			} else {
				reporthash.put("CPU", maxhash);
			}

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			reporthash.put("grade", grade);
			if ("host".equals(type)) {
				typename = "服务器";

			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			// pollMonitorManager.chooseDrawLineType(timeType,
			// ConnectUtilizationhash, "连通率", newip + "ConnectUtilization" +
			// "by" + timeType
			// , 740, 150);
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率",
				newip + "ConnectUtilization", 740, 150);
			pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 750, 150);
			pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip + "memory", 750, 150);
			// 画图-----------------------------

			// dddddddddddddddddddd
			Hashtable Memory = (Hashtable) reporthash.get("Memory");
			Hashtable memMaxHash = (Hashtable) reporthash.get("memmaxhash");
			Hashtable memAvgHash = (Hashtable) reporthash.get("memavghash");
			String[] memoryItemch = { "内存容量", "当前利用率", "最大利用率", "平均利用率" };
			String[] memoryItem = { "Capability", "Utilization" };
			Hashtable mhash = null;
			String[] names = null;
			if (Memory != null && Memory.size() > 0) {
				Table aTable2 = new Table(6);
				float[] widthss = { 220f, 300f, 220f, 220f, 220f, 220f };
				aTable2.setWidths(widthss);
				aTable2.setWidth(100); // 占页面宽度 90%
				aTable2.setAlignment(Element.ALIGN_CENTER);// 居中显示
				aTable2.setAutoFillEmptyCells(true); // 自动填满
				aTable2.setBorderWidth(1); // 边框宽度
				aTable2.setBorderColor(new Color(0, 125, 255)); // 边框颜色
				aTable2.setPadding(2);// 衬距，看效果就知道什么意思了
				aTable2.setSpacing(0);// 即单元格之间的间距
				aTable2.setBorder(2);// 边框
				aTable2.endHeaders();
				aTable2.addCell("内存使用情况");
				aTable2.addCell("内存名");
				// 内存的标题

				for (int i = 0; i < memoryItemch.length; i++) {
					Cell cell = new Cell(memoryItemch[i]);// "内存容量", "当前利用率",
					// "最大利用率", "平均利用率"
					aTable2.addCell(cell);
				}
				// 写内存

				for (int i = 0; i < Memory.size(); i++) {
					aTable2.addCell("");
					mhash = (Hashtable) (Memory.get(new Integer(i)));
					String name = (String) mhash.get("name");// VirtualMemory,PhysicalMemory
					Cell cell1 = new Cell(name);
					aTable2.addCell(cell1);
					for (int j = 0; j < memoryItem.length; j++) {
						String value = "";
						if (mhash.get(memoryItem[j]) != null) {
							value = (String) mhash.get(memoryItem[j]);// "Capability",
							// "Utilization"
						}
						Cell cell2 = new Cell(value);
						aTable2.addCell(cell2);
					}
					String value = "";
					if (memMaxHash.get(name) != null) {
						value = (String) memMaxHash.get(name);// 最大利用率
						Cell cell3 = new Cell(value);
						aTable2.addCell(cell3);
					}
					String avgvalue = "";
					if (memAvgHash.get(name) != null) {
						avgvalue = (String) memAvgHash.get(name);// 平均利用率
						aTable2.addCell(avgvalue);
					}

				} // end 写内存

			} else {
				Table aTable2 = new Table(6);
				float[] widthss = { 220f, 300f, 220f, 220f, 220f, 220f };
				aTable2.setWidths(widthss);
				aTable2.setWidth(100); // 占页面宽度 90%
				aTable2.setAlignment(Element.ALIGN_CENTER);// 居中显示
				aTable2.setAutoFillEmptyCells(true); // 自动填满
				aTable2.setBorderWidth(1); // 边框宽度
				aTable2.setBorderColor(new Color(0, 125, 255)); // 边框颜色
				aTable2.setPadding(2);// 衬距，看效果就知道什么意思了
				aTable2.setSpacing(0);// 即单元格之间的间距
				aTable2.setBorder(2);// 边框
				aTable2.endHeaders();
				aTable2.addCell("内存使用情况");
				aTable2.addCell("内存名");
				// 内存的标题

				for (int i = 0; i < memoryItemch.length; i++) {
					Cell cell = new Cell(memoryItemch[i]);// "内存容量", "当前利用率",
					// "最大利用率", "平均利用率"
					aTable2.addCell(cell);
				}
				// 写内存

				HostNodeDao hostDao = new HostNodeDao();
				HostNode hostNode = (HostNode) hostDao.findByCondition("ip_address", ip).get(0);
				// Monitoriplist monitor = monitorManager.getByIpaddress(ip);
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.311")) {
					names = new String[] { "PhysicalMemory", "VirtualMemory" };
				} else {
					names = new String[] { "PhysicalMemory", "SwapMemory" };
				}
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					aTable2.addCell("");
					Cell cell = new Cell(names[i]);// "PhysicalMemory",
					// "VirtualMemory"或者"PhysicalMemory",
					// "SwapMemory"
					aTable2.addCell(cell);

					for (int j = 0; j < memoryItem.length; j++) {
						// 因为当前没有瞬间值和利用率
						String value = "";
						Cell cell1 = new Cell(value);// "Capability",
						// "Utilization"
						aTable2.addCell(cell1);
					}
					String value = "";
					if (memMaxHash.get(name) != null) {
						value = (String) memMaxHash.get(name);
						Cell cell2 = new Cell(value);
						aTable2.addCell(cell2);
					} else {
						Cell cell3 = new Cell(value);
						aTable2.addCell(cell3);
					}
					String avgvalue = "";
					if (memAvgHash.get(name) != null) {
						avgvalue = (String) memAvgHash.get(name);
						Cell cell4 = new Cell(avgvalue);
						aTable2.addCell(cell4);
					} else {
						Cell cell5 = new Cell(avgvalue);
						aTable2.addCell(cell5);
					}

				}
			}
			// dddddddddddddddddddd
			session.setAttribute("reporthash", reporthash);
//			request.setAttribute("pingmax", ((Hashtable) reporthash.get("ping")).get("pingmax"));
			if((Hashtable) reporthash.get("ping") == null){
				request.setAttribute("pingmax", "");
			}else{
				request.setAttribute("pingmax", ((Hashtable) reporthash.get("ping")).get("pingmax"));
			}
			request.setAttribute("Ping", reporthash.get("Ping"));
//			request.setAttribute("avgpingcon", ((Hashtable) reporthash.get("ping")).get("avgpingcon"));
			if((Hashtable) reporthash.get("ping") == null){
				request.setAttribute("avgpingcon", "");
			}else{
				request.setAttribute("avgpingcon", ((Hashtable) reporthash.get("ping")).get("avgpingcon"));
			}
			request.setAttribute("Disk", diskhash);
			// dddddddddddddddddddd
			request.setAttribute("Memory", Memory);
			request.setAttribute("mhash", mhash);
			request.setAttribute("memMaxHash", memMaxHash);
			request.setAttribute("memAvgHash", memAvgHash);
			request.setAttribute("names", names);

			request.setAttribute("cpu", ((Hashtable) reporthash.get("CPU")).get("cpu"));
			request.setAttribute("cpumax", ((Hashtable) reporthash.get("CPU")).get("cpumax"));
			request.setAttribute("avgcpu", ((Hashtable) reporthash.get("CPU")).get("avgcpu"));
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("nodeid", node.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showCompositeReport.jsp";
	}

	private String showAnalyseReport() {
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Vector cpuVector = new Vector();
		Vector pdata = new Vector();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			// type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			type = nodeDTO.getType();
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			String runmodel = PollingEngine.getCollectwebflag();
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				Hashtable pingdata = ShareData.getPingdata();
				Hashtable sharedata = ShareData.getSharedata();
				memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
				diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
				pdata = (Vector) pingdata.get(ip);
				Hashtable hdata = (Hashtable) sharedata.get(ip);
				if (hdata == null)
					hdata = new Hashtable();
				cpuVector = (Vector) hdata.get("cpu");
			} else {
				// 采集与访问是分离模式
				MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				memhash = memoryInfoService.getCurrMemoryListInfo();
				// 取出当前的硬盘信息
				DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				diskhash = diskInfoService.getCurrDiskListInfo();
				PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				pdata = pingInfoService.getPingInfo();
				// CPU信息
				CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(),
						nodeDTO.getSubtype());
				cpuVector = cpuInfoService.getCpuInfo();

			}
			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			
			if(SystemConstant.DBType.equalsIgnoreCase("mysql")){
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
						+ totime + "' ");
			}else if(SystemConstant.DBType.equalsIgnoreCase("oracle")){
				s.append("select * from system_eventlist where recordtime>= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') " + "and recordtime<=to_date('"
						+ totime + "','yyyy-mm-dd hh24:mi:ss') ");
			}
			s.append(" and nodeid=" + node.getId());

			EventListDao eventdao = new EventListDao();
			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
			}
			// 运行评价===========================
			String grade = "优";
			if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
				grade = "良";
			}

			if (pingvalue > 0) {
				grade = "差";
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);
			request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
						reporthash.put("Ping", hostdata.getThevalue());
						reporthash.put("ping", maxping);
					}
				}
			}
			// CPU
			if (cpuVector != null && cpuVector.size() > 0) {
				for (int si = 0; si < cpuVector.size(); si++) {
					CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
					maxhash.put("cpu", cpudata.getThevalue());
					reporthash.put("CPU", maxhash);
				}
			} else {
				reporthash.put("CPU", maxhash);
			}

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			reporthash.put("grade", grade);
			if ("host".equalsIgnoreCase(type)) {
				typename = "服务器";
			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "ConnectUtilization"
					+ "by" + timeType, 740, 150);
			pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 750, 150);
			pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip + "memory", 750, 150);
			// 画图-----------------------------

			// dddddddddddddddddddd
			Hashtable Memory = (Hashtable) reporthash.get("Memory");
			Hashtable memMaxHash = (Hashtable) reporthash.get("memmaxhash");
			Hashtable memAvgHash = (Hashtable) reporthash.get("memavghash");
			String[] memoryItemch = { "内存容量", "当前利用率", "最大利用率", "平均利用率" };
			String[] memoryItem = { "Capability", "Utilization" };
			Hashtable mhash = null;
			String[] names = null;
			if (Memory != null && Memory.size() > 0) {
				Table aTable2 = new Table(6);
				float[] widthss = { 220f, 300f, 220f, 220f, 220f, 220f };
				aTable2.setWidths(widthss);
				aTable2.setWidth(100); // 占页面宽度 90%
				aTable2.setAlignment(Element.ALIGN_CENTER);// 居中显示
				aTable2.setAutoFillEmptyCells(true); // 自动填满
				aTable2.setBorderWidth(1); // 边框宽度
				aTable2.setBorderColor(new Color(0, 125, 255)); // 边框颜色
				aTable2.setPadding(2);// 衬距，看效果就知道什么意思了
				aTable2.setSpacing(0);// 即单元格之间的间距
				aTable2.setBorder(2);// 边框
				aTable2.endHeaders();
				aTable2.addCell("内存使用情况");
				aTable2.addCell("内存名");
				// 内存的标题

				for (int i = 0; i < memoryItemch.length; i++) {
					Cell cell = new Cell(memoryItemch[i]);// "内存容量", "当前利用率",
					// "最大利用率", "平均利用率"
					aTable2.addCell(cell);
				}
				// 写内存

				for (int i = 0; i < Memory.size(); i++) {
					aTable2.addCell("");
					mhash = (Hashtable) (Memory.get(new Integer(i)));
					String name = (String) mhash.get("name");// VirtualMemory,PhysicalMemory
					Cell cell1 = new Cell(name);
					aTable2.addCell(cell1);
					for (int j = 0; j < memoryItem.length; j++) {
						String value = "";
						if (mhash.get(memoryItem[j]) != null) {
							value = (String) mhash.get(memoryItem[j]);// "Capability",
							// "Utilization"
						}
						Cell cell2 = new Cell(value);
						aTable2.addCell(cell2);
					}
					String value = "";
					if (memMaxHash.get(name) != null) {
						value = (String) memMaxHash.get(name);// 最大利用率
						Cell cell3 = new Cell(value);
						aTable2.addCell(cell3);
					}
					String avgvalue = "";
					if (memAvgHash.get(name) != null) {
						avgvalue = (String) memAvgHash.get(name);// 平均利用率
						aTable2.addCell(avgvalue);
					}

				} // end 写内存

			} else {
				Table aTable2 = new Table(6);
				float[] widthss = { 220f, 300f, 220f, 220f, 220f, 220f };
				aTable2.setWidths(widthss);
				aTable2.setWidth(100); // 占页面宽度 90%
				aTable2.setAlignment(Element.ALIGN_CENTER);// 居中显示
				aTable2.setAutoFillEmptyCells(true); // 自动填满
				aTable2.setBorderWidth(1); // 边框宽度
				aTable2.setBorderColor(new Color(0, 125, 255)); // 边框颜色
				aTable2.setPadding(2);// 衬距，看效果就知道什么意思了
				aTable2.setSpacing(0);// 即单元格之间的间距
				aTable2.setBorder(2);// 边框
				aTable2.endHeaders();
				aTable2.addCell("内存使用情况");
				aTable2.addCell("内存名");
				// 内存的标题

				for (int i = 0; i < memoryItemch.length; i++) {
					Cell cell = new Cell(memoryItemch[i]);// "内存容量", "当前利用率",
					// "最大利用率", "平均利用率"
					aTable2.addCell(cell);
				}
				// 写内存

				HostNodeDao hostDao = new HostNodeDao();
				HostNode hostNode = (HostNode) hostDao.findByCondition("ip_address", ip).get(0);
				// Monitoriplist monitor = monitorManager.getByIpaddress(ip);
				if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.311")) {
					names = new String[] { "PhysicalMemory", "VirtualMemory" };
				} else {
					names = new String[] { "PhysicalMemory", "SwapMemory" };
				}
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					aTable2.addCell("");
					Cell cell = new Cell(names[i]);// "PhysicalMemory",
					// "VirtualMemory"或者"PhysicalMemory",
					// "SwapMemory"
					aTable2.addCell(cell);

					for (int j = 0; j < memoryItem.length; j++) {
						// 因为当前没有瞬间值和利用率
						String value = "";
						Cell cell1 = new Cell(value);// "Capability",
						// "Utilization"
						aTable2.addCell(cell1);
					}
					String value = "";
					if (memMaxHash.get(name) != null) {
						value = (String) memMaxHash.get(name);
						Cell cell2 = new Cell(value);
						aTable2.addCell(cell2);
					} else {
						Cell cell3 = new Cell(value);
						aTable2.addCell(cell3);
					}
					String avgvalue = "";
					if (memAvgHash.get(name) != null) {
						avgvalue = (String) memAvgHash.get(name);
						Cell cell4 = new Cell(avgvalue);
						aTable2.addCell(cell4);
					} else {
						Cell cell5 = new Cell(avgvalue);
						aTable2.addCell(cell5);
					}

				}
			}
			// dddddddddddddddddddd
			// EventListDao eventListDao = new EventListDao();
			Hashtable CPU = (Hashtable) reporthash.get("CPU");
			if (CPU == null)
				CPU = new Hashtable();
			String cpu = "";
			if (CPU.get("cpu") != null)
				cpu = (String) CPU.get("cpu");
			cpumax = "0.0%";
			if (CPU.get("cpumax") != null)
				cpumax = (String) CPU.get("cpumax");
			avgcpu = "";
			if (CPU.get("avgcpu") != null)
				avgcpu = (String) CPU.get("avgcpu");
			String hostname = (String) reporthash.get("equipname");
			String hostnameDoc = (String) reporthash.get("equipnameDoc");
			Table aTable = new Table(8);

			aTable.endHeaders();
			Cell cell = null;
			request.setAttribute("hostname", hostname);
			request.setAttribute("hostnameDoc", hostnameDoc);
			request.setAttribute("typename", typename);
			request.setAttribute("grade", grade);
			request.setAttribute("avgcpu", avgcpu);
			request.setAttribute("cpumax", cpumax);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			String avgvalue = "0.0%";
			if (memAvgHash.get("PhysicalMemory") != null) {

				avgvalue = (String) memAvgHash.get("PhysicalMemory");
				request.setAttribute("avgvalue", avgvalue);
			} else {
				request.setAttribute("avgvalue", avgvalue);
			}
			String value = "0.0%";
			if (memMaxHash.get("PhysicalMemory") != null) {

				value = (String) memMaxHash.get("PhysicalMemory");
				request.setAttribute("PhysicalMemory", value);
			} else {
				request.setAttribute("PhysicalMemory", value);
			}

			request.setAttribute("cpuvalue", cpuvalue);
			request.setAttribute("memvalue", memvalue);
			request.setAttribute("pingvalue", pingvalue);
			request.setAttribute("diskvalue", diskvalue);
			String strcpu = "";
			String strmem = "";
			String strping = "";
			String strdisk = "";
			if (cpuvalue > 0) {
				strcpu = "    2   在该段时间内，设备发生" + cpuvalue + "次CPU越值时间，需要管理员确定该事件。" + "\n" + "\n";
			} else {
				strcpu = "    2   在该段时间内，CPU运行正常，未出现利用率过大情况。" + " \n" + "\n";
			}
			if (memvalue > 0) {
				strmem = "    3   发生" + memvalue + "次内存超越阀值事件，提醒管理员注意设备内存情况。" + "\n" + "\n";
			} else {
				strmem = "    3   该段时间内，设备内存运行正常，未出现内存利用率过大情况。" + "\n" + "\n";
			}
			if (pingvalue > 0) {
				strping = "    4   发生" + pingvalue + "次服务器连通率事件，提醒管理员注意观察设备的连通状况。" + "\n" + "\n";
			} else {
				strping = "    4   该段时间内，设备连通较好，未出现宕机情况" + "\n" + "\n";
			}
			if (diskvalue > 0) {

				strdisk = "    5   发生" + diskvalue + "磁盘利用率事件，请管理员注意检查服务器磁盘使用状况。";
			} else {
				strdisk = "    5   该段时间内，磁盘利用率正常，使用情况良好。";
			}
			request.setAttribute("runningStatus", "    1   在该段时间内，运行状况稳定，概述设备处于稳定运行状态。<br/>" + " \n" + "\n" + strcpu
					+ "<br/>" + strmem + "<br/>" + strping + "<br/>" + strdisk + "\n" + "\n" + "\n");
			// dddddddddddddddddddd
			session.setAttribute("reporthash", reporthash);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("nodeid", node.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showAnalyseReport.jsp";
	}

	private String showHardwareReport() {
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

		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable userhash = new Hashtable();// "user"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable processhash = new Hashtable();
		Vector vector = new Vector();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");
		/*
		 * HostNodeDao hostdao = new HostNodeDao(); List hostlist =
		 * hostdao.loadHost();
		 * 
		 * Host host =
		 * (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		 * 
		 * String newip=doip(host.getIpAddress());
		 */
		String ipaddress = (String) request.getParameter("ipaddress");
		String newip = doip(ipaddress);
		String[] time = { "", "" };
		getTime(request, time);
		// String starttime = time[0];
		String endtime = time[1];
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		String[] item = { "CPU" };

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		try {
			// hash =
			// hostlastmanager.getbyCategories(host.getIpAddress(),item,starttime,endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(ipaddress, "CPU", "Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String pingconavg ="";
		// cpu最大值
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpu平均值
		// maxhash = new Hashtable();
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipaddress);
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
			vector = (Vector) ipAllData.get("device");
		}
		// 按order将进程信息排序
		String order = "MemoryUtilization";
		if ((request.getParameter("orderflag") != null) && (!request.getParameter("orderflag").equals(""))) {
			order = request.getParameter("orderflag");
		}
		// try{
		// processhash =
		// hostlastmanager.getProcess_share(host.getIpAddress(),"Process",order,starttime,endtime);
		// }catch(Exception ex){
		// ex.printStackTrace();
		// }
		String[] _item = { "User" };
		try {
			userhash = hostlastmanager.getbyCategories_share(ipaddress, _item, starttime, endtime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(ipaddress, "Ping", "ConnectUtilization", starttime1,
				totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(ipaddress);
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("vector", vector);
		request.setAttribute("userhash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/capreport/host/showHardwareReport.jsp";
	}

	private String showDiskReport() {
		String nodeexist = "0";
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		String runmodel = PollingEngine.getCollectwebflag(); // 系统运行模式
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;
		String newip = "";

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = null;
			try {
				node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			} catch (Exception e) {
				dao.close();
			} finally {

			}

			if (node != null) {
				equipname = node.getAlias() + "(" + ip + ")";
				equipnameDoc = node.getAlias();
				String remoteip = request.getRemoteAddr();
				newip = doip(ip);
			} else {
				nodeexist = "1";
			}

			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			// String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			// memhash = hostlastmanager.getMemory_share(ip, "Memory",
			// starttime,totime);
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
			} else {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(node);
				DiskInfoService diskInfoService = new DiskInfoService(nodedto.getId() + "", nodedto.getType(), nodedto
						.getSubtype());
				diskhash = diskInfoService.getCurrDiskListInfo();
			}

			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			// Hashtable cpuhash = hostmanager.getCategory(ip, "CPU",
			// "Utilization", starttime, totime);
			// Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory",
			// starttime, totime);
			// 各memory最大值
			// memmaxhash = memoryhash[1];
			// memavghash = memoryhash[2];
			// // cpu最大值
			// maxhash = new Hashtable();
			// String cpumax = "";
			// String avgcpu = "";
			// if (cpuhash.get("max") != null) {
			// cpumax = (String) cpuhash.get("max");
			// }
			// if (cpuhash.get("avgcpucon") != null) {
			// avgcpu = (String) cpuhash.get("avgcpucon");
			// }
			//
			// maxhash.put("cpumax", cpumax);
			// maxhash.put("avgcpu", avgcpu);

			// Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip,
			// "Ping", "ConnectUtilization", starttime, totime);
			// String pingconavg = "";
			// if (ConnectUtilizationhash.get("avgpingcon") != null)
			// pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			// String ConnectUtilizationmax = "";
			// maxping.put("avgpingcon", pingconavg);
			// if (ConnectUtilizationhash.get("max") != null) {
			// ConnectUtilizationmax = (String)
			// ConnectUtilizationhash.get("max");
			// }
			// maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			// StringBuffer s = new StringBuffer();
			// s.append("select * from system_eventlist where recordtime>= '"
			// + starttime + "' " + "and recordtime<='" + totime + "' ");
			// s.append(" and nodeid=" + node.getId());
			//
			// EventListDao eventdao = new EventListDao();
			// List infolist = eventdao.findByCriteria(s.toString());
			//
			// if (infolist != null && infolist.size() > 0) {
			//
			// for (int j = 0; j < infolist.size(); j++) {
			// EventList eventlist = (EventList) infolist.get(j);
			// if (eventlist.getContent() == null)
			// eventlist.setContent("");
			// String content = eventlist.getContent();
			// if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
			// pingvalue = pingvalue + 1;
			// } else if (eventlist.getSubentity().equalsIgnoreCase(
			// "memory")) {
			// memvalue = memvalue + 1;
			// } else if (eventlist.getSubentity()
			// .equalsIgnoreCase("disk")) {
			// diskvalue = diskvalue + 1;
			// } else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
			// cpuvalue = cpuvalue + 1;
			// }
			// }
			// }
			// //运行评价===========================
			// String grade = "优";
			// if (cpuvalue>0||memvalue>0||diskvalue >0) {
			// grade = "良";
			// }
			//			
			// if (pingvalue >0) {
			// grade = "差";
			// }
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);
			// request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			// Vector pdata = (Vector) pingdata.get(ip);
			// // 把ping得到的数据加进去
			// if (pdata != null && pdata.size() > 0) {
			// for (int m = 0; m < pdata.size(); m++) {
			// Pingcollectdata hostdata = (Pingcollectdata) pdata.get(m);
			// if (hostdata.getSubentity().equals("ConnectUtilization")) {
			// reporthash.put("time", hostdata.getCollecttime());
			// reporthash.put("Ping", hostdata.getThevalue());
			// reporthash.put("ping", maxping);
			// }
			// }
			// }
			// // CPU
			// Hashtable hdata = (Hashtable) sharedata.get(ip);
			// if (hdata == null)
			// hdata = new Hashtable();
			// Vector cpuVector = (Vector) hdata.get("cpu");
			// if (cpuVector != null && cpuVector.size() > 0) {
			// for (int si = 0; si < cpuVector.size(); si++) {
			// CPUcollectdata cpudata = (CPUcollectdata) cpuVector
			// .elementAt(si);
			// maxhash.put("cpu", cpudata.getThevalue());
			// reporthash.put("CPU", maxhash);
			// }
			// } else {
			// reporthash.put("CPU", maxhash);
			// }

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			// reporthash.put("grade", grade);
			if ("host".equals(type)) {
				typename = "服务器";

			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			// pollMonitorManager.chooseDrawLineType(timeType,ConnectUtilizationhash,
			// "连通率", newip + "ConnectUtilization" + "by" + timeType, 740, 150);
			// pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750,
			// 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 650, 150);
			// pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip+
			// "memory", 750, 150);
			// 画图-----------------------------
			session.setAttribute("reporthash", reporthash);
			// ddddddddd
			Hashtable Disk = (Hashtable) reporthash.get("Disk");
			// ddddddddd
			request.setAttribute("Disk", Disk);
			request.setAttribute("newip", newip);
			request.setAttribute("nodeexist", nodeexist);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("nodeid", node.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showDiskReport.jsp";
	}

	private String showToolbarDiskReport() {
		String nodeexist = "0";
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			if (!node.isManaged())
				nodeexist = "1";
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
			diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			// Hashtable cpuhash = hostmanager.getCategory(ip,
			// "CPU","Utilization", starttime, totime);
			// Hashtable[] memoryhash = hostmanager.getMemory(ip,
			// "Memory",starttime, totime);
			// // 各memory最大值
			// memmaxhash = memoryhash[1];
			// memavghash = memoryhash[2];
			// // cpu最大值
			// maxhash = new Hashtable();
			// String cpumax = "";
			// String avgcpu = "";
			// if (cpuhash.get("max") != null) {
			// cpumax = (String) cpuhash.get("max");
			// }
			// if (cpuhash.get("avgcpucon") != null) {
			// avgcpu = (String) cpuhash.get("avgcpucon");
			// }
			//
			// maxhash.put("cpumax", cpumax);
			// maxhash.put("avgcpu", avgcpu);

			// Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip,
			// "Ping", "ConnectUtilization", starttime, totime);
			// String pingconavg = "";
			// if (ConnectUtilizationhash.get("avgpingcon") != null)
			// pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			// String ConnectUtilizationmax = "";
			// maxping.put("avgpingcon", pingconavg);
			// if (ConnectUtilizationhash.get("max") != null) {
			// ConnectUtilizationmax = (String) ConnectUtilizationhash
			// .get("max");
			// }
			// maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
					+ totime + "' ");
			s.append(" and nodeid=" + node.getId());

			EventListDao eventdao = new EventListDao();
			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
			}
			// 运行评价===========================
			String grade = "优";
			if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
				grade = "良";
			}

			if (pingvalue > 0) {
				grade = "差";
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);
			request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
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
					CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
					maxhash.put("cpu", cpudata.getThevalue());
					reporthash.put("CPU", maxhash);
				}
			} else {
				reporthash.put("CPU", maxhash);
			}

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			reporthash.put("grade", grade);
			if ("host".equals(type)) {
				typename = "服务器";

			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			// pollMonitorManager.chooseDrawLineType(timeType,ConnectUtilizationhash,
			// "连通率", newip + "ConnectUtilization" + "by" + timeType, 740, 150);
			// pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750,
			// 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 650, 150);
			// pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip+
			// "memory", 750, 150);
			// 画图-----------------------------
			session.setAttribute("reporthash", reporthash);
			// ddddddddd
			Hashtable Disk = (Hashtable) reporthash.get("Disk");
			// ddddddddd
			request.setAttribute("Disk", Disk);
			request.setAttribute("nodeexist", nodeexist);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showToolbarDiskReport.jsp";
	}

	/*
	 * guzhiming 生成服务器报表 报表订阅模块使用
	 */
	public void createselfhostreport(String startdate, String todate, String ip, String type, String str,
			WritableWorkbook wb, String filena) {
		Date d = new Date();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Hashtable allcpuhash = new Hashtable();
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {

			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			// String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
			diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
					+ totime + "' ");
			s.append(" and nodeid=" + node.getId());

			EventListDao eventdao = new EventListDao();
			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
			}
			// 运行评价===========================
			String grade = "优";
			if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
				grade = "良";
			}

			if (pingvalue > 0) {
				grade = "差";
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			/*
			 * request.setAttribute("hash", hash); request.setAttribute("max",
			 * maxhash); request.setAttribute("memmaxhash", memmaxhash);
			 * request.setAttribute("memavghash", memavghash);
			 * request.setAttribute("diskhash", diskhash);
			 * request.setAttribute("memhash", memhash);
			 * request.setAttribute("grade", grade);
			 */
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
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
					CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
					maxhash.put("cpu", cpudata.getThevalue());
					reporthash.put("CPU", maxhash);
				}
			} else {
				reporthash.put("CPU", maxhash);
			}

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			reporthash.put("grade", grade);
			if ("host".equals(type)) {
				typename = "服务器";

			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "ConnectUtilization"
					+ "by" + timeType, 740, 150);
			pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 750, 150);
			pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip + "memory", 750, 150);
			// 画图-----------------------------
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			// String str = request.getParameter("str");//
			// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				// report.createReport_host("temp/hostnms_report.xls");//
				// excel综合报表
				report.createReport_hostWithoutClose(filena, wb);
				SysLogger.info("filename" + report.getFileName());
				// request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				// ExcelReport1 report1 = new ExcelReport1(new
				// IpResourceReport(),memhash);
				try {
					String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostDoc(fileName);// word综合报表
					// report1.createReport_hostmem(file);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostNewDoc(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_hostNewPDF(fileName);// pdf业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostPDF(fileName);// pdf综合报表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String downloadselfhostreport() {
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			// hash = hostlastmanager.getbyCategories(ip, item, starttime,
			// totime);
			memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
			diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
					+ totime + "' ");
			s.append(" and nodeid=" + node.getId());

			EventListDao eventdao = new EventListDao();
			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
			}
			// 运行评价===========================
			String grade = "优";
			if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
				grade = "良";
			}

			if (pingvalue > 0) {
				grade = "差";
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);
			request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
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
					CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
					maxhash.put("cpu", cpudata.getThevalue());
					reporthash.put("CPU", maxhash);
				}
			} else {
				reporthash.put("CPU", maxhash);
			}

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			reporthash.put("grade", grade);
			if ("host".equals(type)) {
				typename = "服务器";

			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			// pollMonitorManager.chooseDrawLineType(timeType,
			// ConnectUtilizationhash, "连通率", newip + "ConnectUtilization" +
			// "by" + timeType
			// , 740, 150);
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率",
				newip + "ConnectUtilization", 740, 150);
			pollMonitorManager.p_draw_line(cpuhash, "CPU利用率", newip + "cpu", 750, 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 750, 150);
			pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "内存利用率", newip + "memory", 750, 150);
			// 画图-----------------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				report.createReport_host("temp/hostnms_report.xls");// excel综合报表
				// SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				// ExcelReport1 report1 = new ExcelReport1(new
				// IpResourceReport(),memhash);
				try {
					String file = "temp/hostknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostDoc(fileName);// word综合报表
					// report1.createReport_hostmem(file);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/hostNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostNewDoc(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_hostNewPDF(fileName);// pdf业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/hostknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostPDF(fileName);// pdf综合报表

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

	private String downloadmultihostreport() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		// SysLogger.info("ids========="+oids);
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				if (_ids[i] == null || _ids[i].trim().length() == 0)
					continue;
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
		String equipname = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		// Hashtable pingdata = ShareData.getPingdata();
		// Hashtable sharedata = ShareData.getSharedata();
		Vector pdata = new Vector();
		Vector cpuVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag();
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					ip = node.getIpAddress();
					equipname = node.getAlias() + "(" + ip + ")";
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					String[] time = { "", "" };
					// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
					String[] item = { "CPU" };
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						Hashtable pingdata = ShareData.getPingdata();
						Hashtable sharedata = ShareData.getSharedata();
						// hash =
						// hostlastmanager.getbyCategories_share(ip,item,starttime,endtime);
						// hash =
						// hostlastmanager.getbyCategories(ip,item,startdate,todate);
						memhash = hostlastmanager.getMemory_share(ip, "Memory", startdate, todate);
						// memhash =
						// hostlastmanager.getMemory(ip,"Memory",starttime,endtime);
						diskhash = hostlastmanager.getDisk_share(ip, "Disk", startdate, todate);
						// diskhash =
						// hostlastmanager.getDisk(ip,"Disk",starttime,endtime);
						pdata = (Vector) pingdata.get(ip);
						// CPU
						Hashtable hdata = (Hashtable) sharedata.get(ip);
						if (hdata == null)
							hdata = new Hashtable();
						cpuVector = (Vector) hdata.get("cpu");
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						// 取出当前的硬盘信息
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
						PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						pdata = pingInfoService.getPingInfo();
						// CPU信息
						CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						cpuVector = cpuInfoService.getCpuInfo();
					}
					// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
					Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization",
						starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
					draw_column(diskhash, "", newip + "disk", 750, 150);
					p_drawchartMultiLine(memoryhash[0], "", newip + "memory", 750, 150);

					p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

					draw_column(diskhash, "", newip + "disk", 750, 150);

					Hashtable reporthash = new Hashtable();

					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					}

					// CPU
					if (cpuVector != null && cpuVector.size() > 0) {
						for (int si = 0; si < cpuVector.size(); si++) {
							CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
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
					allreporthash.put(ip, reporthash);
				}
				AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_hostall("/temp/hostnms_report.xls");
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

	// 之下zhushouzhi主机连通率报表
	public void createDocContext(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();						
		Date cc = tempCal.getTime();
		String time = sdf1.format(cc);
		
		
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("报表生成时间:" + time, contextFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("数据统计时间段: " + starttime + " 至 " + totime, contextFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		
		
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(8);
		int width[] = { 50, 50, 50, 70, 50, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();
		Cell c = new Cell("序号");
		c.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(c);
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("设备名称");
		Cell cell2 = new Cell("操作系统");
		Cell cell3 = new Cell("平均连通率");
		Cell cell4 = new Cell("宕机次数(个)");
		Cell cell13 = new Cell("平均响应时间(ms)");
		Cell cell14 = new Cell("最大响应时间(ms)");
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell13.setBackgroundColor(Color.LIGHT_GRAY);
		cell14.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell13);
		aTable.addCell(cell14);
		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String osname = (String) _pinglist.get(2);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				String responseavg = (String) _pinglist.get(5);
				String responsemax = (String) _pinglist.get(6);
				Cell cell5 = new Cell(i + 1 + "");
				Cell cell6 = new Cell(ip);
				Cell cell7 = new Cell(equname);
				Cell cell8 = new Cell(osname);
				Cell cell9 = new Cell(avgping);
				Cell cell10 = new Cell(downnum);
				Cell cell15 = new Cell(responseavg.replace("毫秒", ""));
				Cell cell16 = new Cell(responsemax.replace("毫秒", ""));
				cell5.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
			}
		}
		//导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(90);
		document.add(img);
		//导出响应时间图片
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		img = Image.getInstance(responsetimepath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(90);
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// 调用主机连通率报表zhushouzhi
	public String createdoc() {
		String file = "/temp/liantonglvbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContext(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi主机磁盘利用率
	public void createDocContextDisk(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器磁盘利用率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List disklist = (List) session.getAttribute("disklist");// 读取磁盘list内容

		Table aTable = new Table(8);
		int width[] = { 50, 50, 50, 50, 50, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();
		Cell cell = new Cell("序号");
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell);
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("设备名称");
		Cell cell2 = new Cell("操作系统");
		Cell cell3 = new Cell("磁盘名称");
		Cell cell4 = new Cell("总大小");
		Cell cell5 = new Cell("已用大小");
		Cell cell6 = new Cell("利用率");
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);

		if (disklist != null && disklist.size() > 0) {
			for (int i = 0; i < disklist.size(); i++) {
				List _disklist = (List) disklist.get(i);
				String ip = (String) _disklist.get(0);
				String equname = (String) _disklist.get(1);
				String osname = (String) _disklist.get(2);
				String name = (String) _disklist.get(3);
				String allsizevalue = (String) _disklist.get(4);
				String usedsizevalue = (String) _disklist.get(5);
				String utilization = (String) _disklist.get(6);
				Cell cell9 = new Cell(i + 1 + "");
				Cell cell10 = new Cell(ip);
				Cell cell12 = new Cell(equname);
				Cell cell13 = new Cell(osname);
				Cell cell14 = new Cell(name);
				Cell cell15 = new Cell(allsizevalue);
				Cell cell16 = new Cell(usedsizevalue);
				Cell cell17 = new Cell(utilization);

				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell12);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
				aTable.addCell(cell17);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// zhushouzhi磁盘利用率
	public String createdocDisk() {
		String file = "/temp/cipanbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextDisk(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi主机内存利用率报表
	public void createDocContextMem(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器内存利用率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List memlist = (List) session.getAttribute("memlist");// 读取list内容

		Table aTable = new Table(10);
		int width[] = { 30, 50, 50, 50, 50, 50, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();
		Cell cell = new Cell("序号");
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell);
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("设备名称");
		Cell cell2 = new Cell("操作系统");
		Cell cell3 = new Cell("物理内存大小");
		Cell cell4 = new Cell("平均利用率");
		Cell cell5 = new Cell("最大利用率");
		Cell cell6 = new Cell("虚拟内存总大小");
		Cell cell7 = new Cell("平均利用率");
		Cell cell8 = new Cell("最大利用率");
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);

		if (memlist != null && memlist.size() > 0) {
			for (int i = 0; i < memlist.size(); i++) {
				List _memlist = (List) memlist.get(i);
				String ip = (String) _memlist.get(0);
				String equname = (String) _memlist.get(1);
				String osname = (String) _memlist.get(2);
				String Capability = (String) _memlist.get(3);
				String maxvalue = (String) _memlist.get(5);
				String avgvalue = (String) _memlist.get(4);
				String Capability1 = (String) _memlist.get(6);
				String maxvalue1 = (String) _memlist.get(8);
				String avgvalue1 = (String) _memlist.get(7);
				Cell cell9 = new Cell(i + 1 + "");
				Cell cell10 = new Cell(ip);
				Cell cell12 = new Cell(equname);
				Cell cell13 = new Cell(osname);
				Cell cell14 = new Cell(Capability);
				Cell cell15 = new Cell(maxvalue);
				Cell cell16 = new Cell(avgvalue);
				Cell cell17 = new Cell(Capability1);
				Cell cell18 = new Cell(maxvalue1);
				Cell cell19 = new Cell(avgvalue1);

				cell9.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell12.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell13.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell14.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell19.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中

				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell12);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
				aTable.addCell(cell19);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// zhushouzhi调用主机内存报表方法
	public String createdocMem() {
		String file = "/temp/neicunbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextMem(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// 之上zhushouzhi

	// zhushouzhi调用主机cpu利用率报表
	public String createdocCpu() {
		String file = "/temp/cipanbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextCpu(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createDocContextCpu(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器CPU利用率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);

		List cpulist = (List) session.getAttribute("cpulist");// cpu利用率

		Table aTable = new Table(6);
		this.setTableFormat(aTable);
		// int width[] = { 50, 50, 50, 50, 50, 50 };
		// aTable.setWidths(width);
		// aTable.setWidth(100); // 占页面宽度 100%
		// aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		// aTable.setAutoFillEmptyCells(true); // 自动填满
		// aTable.setBorderWidth(1); // 边框宽度
		// aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		// aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		// aTable.setSpacing(0);// 即单元格之间的间距
		// aTable.setBorder(2);// 边框
		// aTable.endHeaders();

		aTable.addCell(this.setCellFormat(new Cell("序号"), true));
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("设备名称");
		Cell cell2 = new Cell("操作系统");
		Cell cell3 = new Cell("平均值%");
		Cell cell4 = new Cell("最大值%");
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell4, true);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);

		if (cpulist != null && cpulist.size() > 0) {
			for (int i = 0; i < cpulist.size(); i++) {
				Hashtable cpuhash = (Hashtable) cpulist.get(i);
				String ip = (String) cpuhash.get("ipaddress");
				if (cpuhash == null)
					cpuhash = new Hashtable();
				String cpumax = "";
				String avgcpu = "";
				if (cpuhash.get("max") != null) {
					cpumax = (String) cpuhash.get("max");
				}
				if (cpuhash.get("avgcpucon") != null) {
					avgcpu = (String) cpuhash.get("avgcpucon");
				}
				HostNodeDao dao = new HostNodeDao();
				HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
				String equname = node.getAlias();
				String osname = node.getType();
				Cell cell9 = new Cell(i + 1 + "");
				Cell cell10 = new Cell(ip);
				Cell cell12 = new Cell(equname);
				Cell cell13 = new Cell(osname);
				Cell cell14 = new Cell(avgcpu);
				Cell cell15 = new Cell(cpumax);

				this.setCellFormat(cell9, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell12, false);
				this.setCellFormat(cell13, false);
				this.setCellFormat(cell14, false);
				this.setCellFormat(cell15, false);

				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell12);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// zhushouzhi调用主机事件报表
	public String createdocEvent() {
		String file = "/temp/zhujishijianbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextEvent(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi主机事件报表
	public void createDocContextEvent(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器事件报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);

		Table aTable = new Table(12);
		int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 90%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();
		Cell c = new Cell("序号");
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(c);
		Cell cell1 = new Cell("IP地址");
		Cell cell2 = new Cell("设备名称");
		Cell cell3 = new Cell("操作系统");
		Cell cell4 = new Cell("事件总数");
		Cell cell5 = new Cell("普通");
		Cell cell6 = new Cell("紧急");
		Cell cell7 = new Cell("严重");
		Cell cell8 = new Cell("连通率事件");
		Cell cell9 = new Cell("cpu事件");
		Cell cell10 = new Cell("端口事件");
		Cell cell11 = new Cell("留宿事件");

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		cell9.setBackgroundColor(Color.LIGHT_GRAY);
		cell10.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);
		aTable.addCell(cell11);

		List eventlist = (List) session.getAttribute("eventlist");
		if (eventlist != null && eventlist.size() > 0) {
			for (int i = 0; i < eventlist.size(); i++) {
				List _eventlist = (List) eventlist.get(i);
				String ip = (String) _eventlist.get(0);
				String equname = (String) _eventlist.get(1);
				String osname = (String) _eventlist.get(2);
				String sum = (String) _eventlist.get(3);
				String levelone = (String) _eventlist.get(4);
				String leveltwo = (String) _eventlist.get(5);
				String levelthree = (String) _eventlist.get(6);
				String pingvalue = (String) _eventlist.get(7);
				String memvalue = (String) _eventlist.get(8);
				String diskvalue = (String) _eventlist.get(9);
				String cpuvalue = (String) _eventlist.get(10);
				Cell cell13 = new Cell(i + 1 + "");
				Cell cell14 = new Cell(ip);
				Cell cell15 = new Cell(equname);
				Cell cell16 = new Cell(osname);
				Cell cell17 = new Cell(sum);
				Cell cell18 = new Cell(levelone);
				Cell cell19 = new Cell(leveltwo);
				Cell cell20 = new Cell(levelthree);
				Cell cell21 = new Cell(pingvalue);
				Cell cell22 = new Cell(memvalue);
				Cell cell23 = new Cell(diskvalue);
				Cell cell24 = new Cell(cpuvalue);
				cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
				/*
				 * cell5.setHorizontalAlignment(Element.ALIGN_CENTER); //居中
				 * cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				 */
				/*
				 * if(i == 1){ Cell cell12 = new Cell(i + 1 + ""+ip);
				 * cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * cell12.setColspan(2);//合并两列的单元格 aTable.addCell(cell12);
				 * aTable.addCell(cell7); aTable.addCell(cell8);
				 * aTable.addCell(cell9); aTable.addCell(cell10); }
				 */

				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
				aTable.addCell(cell19);
				aTable.addCell(cell20);
				aTable.addCell(cell21);
				aTable.addCell(cell22);
				aTable.addCell(cell23);
				aTable.addCell(cell24);

			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	/**
	 * host性能报表
	 * 
	 * @return
	 */
	private String showCapacityReport() {
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
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable pingdata = new Hashtable();
		Hashtable reporthash = new Hashtable();
		Vector pdata = new Vector();

		int pingvalue = 0;
		int memvalue = 0;
		int diskvalue = 0;
		int cpuvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			// if(node == null)
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
			String[] item = { "CPU" };
			String runmodel = PollingEngine.getCollectwebflag();
			if ("0".equals(runmodel)) {
				pingdata = ShareData.getPingdata();
				pdata = (Vector) pingdata.get(ip);

				// 采集与访问是集成模式
				// hash = hostlastmanager.getbyCategories(ip, item, starttime,
				// totime);
				memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
				// diskhash = hostlastmanager.getDisk_share(ip, "Disk",
				// starttime,
				// totime);
				// CPU
				Hashtable hdata = (Hashtable) sharedata.get(ip);
				if (hdata == null)
					hdata = new Hashtable();
				Vector cpuVector = (Vector) hdata.get("cpu");
				if (cpuVector != null && cpuVector.size() > 0) {
					for (int si = 0; si < cpuVector.size(); si++) {
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
						maxhash.put("cpu", cpudata.getThevalue());
						reporthash.put("CPU", maxhash);
					}
				} else {
					reporthash.put("CPU", maxhash);
				}
			} else {
				// 取出当前的内存信息
				MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				memhash = memoryInfoService.getCurrMemoryListInfo();

				// 取出当前CPU的利用率
				CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(),
						nodeDTO.getSubtype());
				String currCpuAvgInfo = cpuInfoService.getCurrCpuAvgInfo();
				maxhash.put("cpu", currCpuAvgInfo);
				reporthash.put("CPU", maxhash);

				// 连通率
				PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
						.getType(), nodeDTO.getSubtype());
				pdata = pingInfoService.getPingInfo();
			}
			// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
			// 各memory最大值
			memmaxhash = memoryhash[1];
			memavghash = memoryhash[2];
			// cpu最大值
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

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// zhushouzhi-----------------------------start
			// 得到事件列表
			// StringBuffer s = new StringBuffer();
			// s.append("select * from system_eventlist where recordtime>= '"
			// + starttime + "' " + "and recordtime<='" + totime + "' ");
			// s.append(" and nodeid=" + node.getId());
			//
			// EventListDao eventdao = new EventListDao();
			// List infolist = eventdao.findByCriteria(s.toString());
			//
			// if (infolist != null && infolist.size() > 0) {
			//
			// for (int j = 0; j < infolist.size(); j++) {
			// EventList eventlist = (EventList) infolist.get(j);
			// if (eventlist.getContent() == null)
			// eventlist.setContent("");
			// String content = eventlist.getContent();
			// if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
			// pingvalue = pingvalue + 1;
			// } else if (eventlist.getSubentity().equalsIgnoreCase(
			// "memory")) {
			// memvalue = memvalue + 1;
			// } else if (eventlist.getSubentity()
			// .equalsIgnoreCase("disk")) {
			// diskvalue = diskvalue + 1;
			// } else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
			// cpuvalue = cpuvalue + 1;
			// }
			// }
			// }
			// // 运行评价===========================
			// String grade = "优";
			// if (cpuvalue > 0 || memvalue > 0 || diskvalue > 0) {
			// grade = "良";
			// }
			//
			// if (pingvalue > 0) {
			// grade = "差";
			// }
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			// zhushouzhi----------------------------------------end

			// request.setAttribute("imgurl",imgurlhash);
			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);
			// request.setAttribute("grade", grade);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			// Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
			if (pdata != null && pdata.size() > 0) {
				for (int m = 0; m < pdata.size(); m++) {
					PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
					if (hostdata.getSubentity().equals("ConnectUtilization")) {
						reporthash.put("time", hostdata.getCollecttime());
						reporthash.put("Ping", hostdata.getThevalue());
						reporthash.put("ping", maxping);
					}
				}
			}
			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("memmaxhash", memmaxhash);
			reporthash.put("memavghash", memavghash);
			reporthash.put("ip", ip);
			// reporthash.put("grade", grade);
			if ("host".equals(type)) {
				typename = "服务器";

			}
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			/*
			 * Enumeration eh1 =memoryhash[0].keys();
			 * while(eh1.hasMoreElements()) { String s1 =
			 * (String)eh1.nextElement(); }
			 */
			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			// pollMonitorManager.chooseDrawLineType(timeType,ConnectUtilizationhash,
			// "连通率", newip + "ConnectUtilization", 740, 150);
			pollMonitorManager.p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
			// pollMonitorManager.p_drawchartMultiLine(cpuhash, "", newip +
			// "cpu", 750, 150);
			// pollMonitorManager.draw_column(diskhash, "", newip + "disk",
			// 750,150);
			pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "", newip + "memory", 750, 150);
			// 画图-----------------------------

			// dddddddddddddddddddd
			Hashtable Memory = (Hashtable) reporthash.get("Memory");
			Hashtable memMaxHash = (Hashtable) reporthash.get("memmaxhash");
			Hashtable memAvgHash = (Hashtable) reporthash.get("memavghash");
			String[] memoryItemch = { "内存容量", "当前利用率", "最大利用率", "平均利用率" };
			String[] memoryItem = { "Capability", "Utilization" };
			Hashtable mhash = null;
			String[] names = null;
			// if (Memory != null && Memory.size() > 0) {
			// Table aTable2 = new Table(6);
			// float[] widthss = { 220f, 300f, 220f, 220f, 220f, 220f };
			// aTable2.setWidths(widthss);
			// aTable2.setWidth(100); // 占页面宽度 90%
			// aTable2.setAlignment(Element.ALIGN_CENTER);// 居中显示
			// aTable2.setAutoFillEmptyCells(true); // 自动填满
			// aTable2.setBorderWidth(1); // 边框宽度
			// aTable2.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			// aTable2.setPadding(2);// 衬距，看效果就知道什么意思了
			// aTable2.setSpacing(0);// 即单元格之间的间距
			// aTable2.setBorder(2);// 边框
			// aTable2.endHeaders();
			// aTable2.addCell("内存使用情况");
			// aTable2.addCell("内存名");
			// // 内存的标题
			//
			// for (int i = 0; i < memoryItemch.length; i++) {
			// Cell cell = new Cell(memoryItemch[i]);// "内存容量", "当前利用率",
			// // "最大利用率", "平均利用率"
			// aTable2.addCell(cell);
			// }
			// // 写内存
			//
			// for (int i = 0; i < Memory.size(); i++) {
			// aTable2.addCell("");
			// mhash = (Hashtable) (Memory.get(new Integer(i)));
			// String name = (String) mhash.get("name");//
			// VirtualMemory,PhysicalMemory
			// Cell cell1 = new Cell(name);
			// aTable2.addCell(cell1);
			// for (int j = 0; j < memoryItem.length; j++) {
			// String value = "";
			// if (mhash.get(memoryItem[j]) != null) {
			// value = (String) mhash.get(memoryItem[j]);// "Capability",
			// // "Utilization"
			// }
			// Cell cell2 = new Cell(value);
			// aTable2.addCell(cell2);
			// }
			// String value = "";
			// if (memMaxHash.get(name) != null) {
			// value = (String) memMaxHash.get(name);// 最大利用率
			// Cell cell3 = new Cell(value);
			// aTable2.addCell(cell3);
			// }
			// String avgvalue = "";
			// if (memAvgHash.get(name) != null) {
			// avgvalue = (String) memAvgHash.get(name);// 平均利用率
			// aTable2.addCell(avgvalue);
			// }
			//
			// } // end 写内存
			//
			// } else {
			// Table aTable2 = new Table(6);
			// float[] widthss = { 220f, 300f, 220f, 220f, 220f, 220f };
			// aTable2.setWidths(widthss);
			// aTable2.setWidth(100); // 占页面宽度 90%
			// aTable2.setAlignment(Element.ALIGN_CENTER);// 居中显示
			// aTable2.setAutoFillEmptyCells(true); // 自动填满
			// aTable2.setBorderWidth(1); // 边框宽度
			// aTable2.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			// aTable2.setPadding(2);// 衬距，看效果就知道什么意思了
			// aTable2.setSpacing(0);// 即单元格之间的间距
			// aTable2.setBorder(2);// 边框
			// aTable2.endHeaders();
			// aTable2.addCell("内存使用情况");
			// aTable2.addCell("内存名");
			// // 内存的标题
			//
			// for (int i = 0; i < memoryItemch.length; i++) {
			// Cell cell = new Cell(memoryItemch[i]);// "内存容量", "当前利用率",
			// // "最大利用率", "平均利用率"
			// aTable2.addCell(cell);
			// }
			// // 写内存
			//
			// HostNodeDao hostDao = new HostNodeDao();
			// HostNode hostNode = (HostNode) hostDao.findByCondition(
			// "ip_address", ip).get(0);
			// // Monitoriplist monitor = monitorManager.getByIpaddress(ip);
			// if (hostNode.getSysOid().startsWith("1.3.6.1.4.1.311")) {
			// names = new String[] { "PhysicalMemory", "VirtualMemory" };
			// } else {
			// names = new String[] { "PhysicalMemory", "SwapMemory" };
			// }
			// for (int i = 0; i < names.length; i++) {
			// String name = names[i];
			// aTable2.addCell("");
			// Cell cell = new Cell(names[i]);// "PhysicalMemory",
			// // "VirtualMemory"或者"PhysicalMemory",
			// // "SwapMemory"
			// aTable2.addCell(cell);
			//
			// for (int j = 0; j < memoryItem.length; j++) {
			// // 因为当前没有瞬间值和利用率
			// String value = "";
			// Cell cell1 = new Cell(value);// "Capability",
			// // "Utilization"
			// aTable2.addCell(cell1);
			// }
			// String value = "";
			// if (memMaxHash.get(name) != null) {
			// value = (String) memMaxHash.get(name);
			// Cell cell2 = new Cell(value);
			// aTable2.addCell(cell2);
			// } else {
			// Cell cell3 = new Cell(value);
			// aTable2.addCell(cell3);
			// }
			// String avgvalue = "";
			// if (memAvgHash.get(name) != null) {
			// avgvalue = (String) memAvgHash.get(name);
			// Cell cell4 = new Cell(avgvalue);
			// aTable2.addCell(cell4);
			// } else {
			// Cell cell5 = new Cell(avgvalue);
			// aTable2.addCell(cell5);
			// }
			//
			// }
			// }
			// dddddddddddddddddddd
			request.setAttribute("Memory", Memory);
			request.setAttribute("mhash", mhash);
			request.setAttribute("memMaxHash", memMaxHash);
			request.setAttribute("memAvgHash", memAvgHash);
			request.setAttribute("names", names);

			session.setAttribute("reporthash", reporthash);
			request.setAttribute("cpu", ((Hashtable) reporthash.get("CPU")).get("cpu"));
			request.setAttribute("cpumax", ((Hashtable) reporthash.get("CPU")).get("cpumax"));
			request.setAttribute("avgcpu", ((Hashtable) reporthash.get("CPU")).get("avgcpu"));
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("nodeid", node.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showCapacityReport.jsp";
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		// wxy add
		if (action.equals("find"))
			return find();
		if (action.equals("serverReport")) {
			return serverReport();
		}
		if (action.equals("memorylist"))
			return memorylist();
		if (action.equals("disklist"))
			return disklist();
		if (action.equals("cpulist"))
			return cpulist();
		if (action.equals("eventlist"))
			return eventlist();
		if (action.equals("multilist"))
			return multilist();
		if (action.equals("hostping"))
			return hostping();
		if (action.equals("hostmemory"))
			return hostmemory();
		if (action.equals("hostcpu"))
			return hostcpu();
		if (action.equals("hostdisk"))
			return hostdisk();
		if (action.equals("hostevent"))
			return hostevent();
		if (action.equals("downloadPingReport"))
			return downloadPingReport();
		if (action.equals("downloadCapacityReport")) {
			return downloadCapacityReport();
		}
		if (action.equals("downloadDiskReport")) {
			return downlaodDiskReport();
		}
		if (action.equals("downloadCompositeReport")) {
			return downloadCompositeReport();
		}
		if (action.equals("downloadAnalyseReport")) {
			return downloadAnalyseReport();
		}
		if (action.equals("downloadHardwareReport")) {
			return downloadHardwareReport();
		}
		if (action.equals("downloadHardwareReportNew")) {
			return downloadHardwareReportNew();
		}
		if (action.equals("showPingReport")) {
			return showPingReport();
		}
		if (action.equals("showDiskReport")) {
			return showDiskReport();
		}
		if (action.equals("showToolbarDiskReport")) {
			return showToolbarDiskReport();
		}
		if (action.equals("showCapacityReport")) {
			return showCapacityReport();
		}
		if (action.equals("showCompositeReport")) {
			return showCompositeReport();
		}
		if (action.equals("showAnalyseReport")) {
			return showAnalyseReport();
		}
		if (action.equals("showHardwareReport")) {
			return showHardwareReport();
		}
		if (action.equals("downloadneteventreport"))
			return downloadneteventreport();
		if (action.equals("downloadselfhostreport"))
			return downloadselfhostreport();
		if (action.equals("downloadmultihostreport"))
			return downloadmultihostreport();
		if (action.equals("downloadmemreport"))
			return downloadmemreport();
		if (action.equals("downloaddiskreport"))
			return downloaddiskreport();
		if (action.equals("downloadcpureport"))
			return downloadcpureport();
		if (action.equals("downloadeventreport"))
			return downloadeventreport();
		if (action.equals("createdoc"))
			return createdoc();
		if (action.equals("createdocMem"))
			return createdocMem();
		if (action.equals("createdocDisk"))
			return createdocDisk();
		if (action.equals("createdocCpu"))
			return createdocCpu();
		if (action.equals("createdocEvent"))
			return createdocEvent();
		// 报表
		if (action.equals("createpdf"))
			return createpdf();
		if (action.equals("createMemPDF"))
			return createMemPDF();
		if (action.equals("createDiskPDF"))
			return createDiskPDF();
		if (action.equals("createEventPDF"))
			return createEventPDF();
		if (action.equals("hostchoce"))
			return hostchoce();
		if (action.equals("showHostchoce"))
			return showHostchoce();
		if (action.equals("downloadselfhostchocedoc"))
			return downloadselfhostchocedoc();
		if (action.equals("createpdfCpu"))
			return createpdfCpu();
		if (action.equals("showPingReportByDate")) {// HONGLI ADD oracle 连通性
			// 根据日期查询
			return showPingReportByDate();
		}
		if (action.equals("showOraclPerformaceReportByDate")) {// HONGLI ADD
			// oracle 数据库性能
			// 根据日期查询
			return showOraclPerformaceReportByDate();
		}
		if (action.equals("showDbOracleCldReportByDate")) {// HONGLI ADD oracle
			// 综合报表 根据日期查询
			return showDbOracleCldReportByDate();
		}
		if (action.equals("showDbOracleEventReport")) {// HONGLI ADD oracle
			// 事件报表 根据日期查询
			return showDbOracleEventReport();
		}
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
		// String newip="";
		// for(int i=0;i<3;i++){
		// int p=ip.indexOf(".");
		// newip+=ip.substring(0,p);
		// ip=ip.substring(p+1);
		// }
		// newip+=ip;
		String allipstr = SysUtil.doip(ip);
		// System.out.println("newip="+newip);
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
							// UtilHdx obj = (UtilHdx)vector.get(j);
							// Double v=new Double(obj.getThevalue());
							// Calendar temp = obj.getCollecttime();
							// new org.jfree.data.time.Hour(newTime)

							// Hour hour=new
							// Hour(tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
							// Day day=new
							// Day(tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
							ss.addOrUpdate(new org.jfree.data.time.Hour(da), v);
							// Minute minute=new
							// Minute(tempCal.get(Calendar.MINUTE),tempCal.get(Calendar.HOUR_OF_DAY),tempCal.get(Calendar.DAY_OF_MONTH),tempCal.get(Calendar.MONTH)+1,tempCal.get(Calendar.YEAR));
							// ss.addOrUpdate(day,v);
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
			// columnKeys,
			// data);
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
	public void createContextpdf(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();						
		Date cc = tempCal.getTime();
		String time = sdf1.format(cc);
		
		
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("报表生成时间:" + time, contextFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("数据统计时间段: " + starttime + " 至 " + totime, contextFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		
		
		// 设置 Table 表格
		document.add(new Paragraph("\n"));
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List pinglist = (List) session.getAttribute("pinglist");
		PdfPTable aTable = new PdfPTable(8);
		int width[] = { 50, 50, 50, 70, 50, 50 , 50, 50};
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell(new Phrase("序号", contextFont));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell);
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("设备名称", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("操作系统", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("平均连通率", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("宕机次数(个)", contextFont));
		PdfPCell cell15 = new PdfPCell(new Phrase("平均响应时间(ms)", contextFont));
		PdfPCell cell16 = new PdfPCell(new Phrase("最大响应时间(ms)", contextFont));
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell15.setBackgroundColor(Color.LIGHT_GRAY);
		cell16.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell16.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell15);
		aTable.addCell(cell16);

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String osname = (String) _pinglist.get(2);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				String responseavg = (String) _pinglist.get(5);
				String responsemax = (String) _pinglist.get(6);

				PdfPCell cell5 = new PdfPCell(new Phrase(i + 1 + ""));
				PdfPCell cell6 = new PdfPCell(new Phrase(ip));
				PdfPCell cell7 = new PdfPCell(new Phrase(equname, contextFont));
				PdfPCell cell8 = new PdfPCell(new Phrase(osname));
				PdfPCell cell9 = new PdfPCell(new Phrase(avgping));
				PdfPCell cell10 = new PdfPCell(new Phrase(downnum));

				PdfPCell cell17 = new PdfPCell(new Phrase(responseavg.replace(
						"毫秒", "")));
				PdfPCell cell18 = new PdfPCell(new Phrase(responsemax.replace(
						"毫秒", "")));

				cell5.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
			}
		}
		//导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(69);
		document.add(img);
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		img = Image.getInstance(responsetimepath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(69);
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// 调用主机连通率报表zhushouzhi
	public String createpdf() {
		String file = "/temp/liantonglvbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createContextpdf(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// -------------------------------------------------------
	// zhushouzhi主机内存利用率报表---mem-pdf
	public void createContextMemPDF(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器内存利用率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Phrase("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List memlist = (List) session.getAttribute("memlist");// 读取list内容

		PdfPTable aTable = new PdfPTable(10);
		int width[] = { 30, 50, 50, 50, 50, 50, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell(new Phrase("序号", contextFont));
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell);
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("设备名称", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("操作系统", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("物理内存大小", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("平均利用率", contextFont));
		PdfPCell cell5 = new PdfPCell(new Phrase("最大利用率", contextFont));
		PdfPCell cell6 = new PdfPCell(new Phrase("虚拟内存总大小", contextFont));
		PdfPCell cell7 = new PdfPCell(new Phrase("平均利用率", contextFont));
		PdfPCell cell8 = new PdfPCell(new Phrase("最大利用率", contextFont));
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);

		if (memlist != null && memlist.size() > 0) {
			for (int i = 0; i < memlist.size(); i++) {
				List _memlist = (List) memlist.get(i);
				String ip = (String) _memlist.get(0);
				String equname = (String) _memlist.get(1);
				String osname = (String) _memlist.get(2);
				String Capability = (String) _memlist.get(3);
				String maxvalue = (String) _memlist.get(5);
				String avgvalue = (String) _memlist.get(4);
				String Capability1 = (String) _memlist.get(6);
				String maxvalue1 = (String) _memlist.get(8);
				String avgvalue1 = (String) _memlist.get(7);
				PdfPCell cell9 = new PdfPCell(new Phrase(i + 1 + ""));
				PdfPCell cell10 = new PdfPCell(new Phrase(ip));
				PdfPCell cell12 = new PdfPCell(new Phrase(equname, contextFont));
				PdfPCell cell13 = new PdfPCell(new Phrase(osname));
				PdfPCell cell14 = new PdfPCell(new Phrase(osname));
				PdfPCell cell15 = new PdfPCell(new Phrase(maxvalue));
				PdfPCell cell16 = new PdfPCell(new Phrase(avgvalue));
				PdfPCell cell17 = new PdfPCell(new Phrase(Capability1));
				PdfPCell cell18 = new PdfPCell(new Phrase(maxvalue1));
				PdfPCell cell19 = new PdfPCell(new Phrase(avgvalue1));
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell12);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
				aTable.addCell(cell19);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// zhushouzhi调用主机内存报表方法
	public String createMemPDF() {
		String file = "/temp/neicunbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createContextMemPDF(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// 之上zhushouzhi----mem-pdf
	// zhushouzhi主机磁盘利用率------disk-pdf
	public void createContextDiskPDF(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器磁盘利用率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Phrase("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List disklist = (List) session.getAttribute("disklist");// 读取磁盘list内容

		PdfPTable aTable = new PdfPTable(8);
		int width[] = { 50, 50, 50, 50, 50, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell(new Phrase("序号", contextFont));
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell);
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("设备名称", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("操作系统", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("磁盘名称", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("总大小", contextFont));
		PdfPCell cell5 = new PdfPCell(new Phrase("已用大小", contextFont));
		PdfPCell cell6 = new PdfPCell(new Phrase("利用率", contextFont));
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);

		if (disklist != null && disklist.size() > 0) {
			for (int i = 0; i < disklist.size(); i++) {
				List _disklist = (List) disklist.get(i);
				String ip = (String) _disklist.get(0);
				String equname = (String) _disklist.get(1);
				String osname = (String) _disklist.get(2);
				String name = (String) _disklist.get(3);
				String allsizevalue = (String) _disklist.get(4);
				String usedsizevalue = (String) _disklist.get(5);
				String utilization = (String) _disklist.get(6);
				PdfPCell cell9 = new PdfPCell(new Phrase(i + 1 + ""));
				PdfPCell cell10 = new PdfPCell(new Phrase(ip));
				PdfPCell cell12 = new PdfPCell(new Phrase(equname, contextFont));
				PdfPCell cell13 = new PdfPCell(new Phrase(osname));
				PdfPCell cell14 = new PdfPCell(new Phrase(name));
				PdfPCell cell15 = new PdfPCell(new Phrase(allsizevalue));
				PdfPCell cell16 = new PdfPCell(new Phrase(usedsizevalue));
				PdfPCell cell17 = new PdfPCell(new Phrase(utilization));
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell12);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
				aTable.addCell(cell17);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// zhushouzhi磁盘利用率
	public String createDiskPDF() {
		String file = "/temp/cipanbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createContextDiskPDF(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi主机内存利用率报表------------disk-pdf
	// zhushouzhi-------------------event--pdf
	// zhushouzhi主机事件报表
	public void createContextEventPDF(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器事件报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Phrase("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);

		PdfPTable aTable = new PdfPTable(12);
		int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55 };
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);
		PdfPCell cc = new PdfPCell(new Phrase("序号"));
		cc.setBackgroundColor(Color.LIGHT_GRAY);
		cc.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cc);
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("设备名称", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("操作系统", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("事件总数", contextFont));
		PdfPCell cell5 = new PdfPCell(new Phrase("普通", contextFont));
		PdfPCell cell6 = new PdfPCell(new Phrase("紧急", contextFont));
		PdfPCell cell7 = new PdfPCell(new Phrase("严重", contextFont));
		PdfPCell cell8 = new PdfPCell(new Phrase("连通率事件", contextFont));
		PdfPCell cell9 = new PdfPCell(new Phrase("cpu事件", contextFont));
		PdfPCell cell10 = new PdfPCell(new Phrase("端口事件", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("流速事件", contextFont));

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		cell9.setBackgroundColor(Color.LIGHT_GRAY);
		cell10.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);
		aTable.addCell(cell11);

		List eventlist = (List) session.getAttribute("eventlist");
		if (eventlist != null && eventlist.size() > 0) {
			for (int i = 0; i < eventlist.size(); i++) {
				List _eventlist = (List) eventlist.get(i);
				String ip = (String) _eventlist.get(0);
				String equname = (String) _eventlist.get(1);
				String osname = (String) _eventlist.get(2);
				String sum = (String) _eventlist.get(3);
				String levelone = (String) _eventlist.get(4);
				String leveltwo = (String) _eventlist.get(5);
				String levelthree = (String) _eventlist.get(6);
				String pingvalue = (String) _eventlist.get(7);

				String memvalue = (String) _eventlist.get(8);
				String diskvalue = (String) _eventlist.get(9);
				String cpuvalue = (String) _eventlist.get(10);
				PdfPCell cell13 = new PdfPCell(new Phrase(i + 1 + ""));
				PdfPCell cell14 = new PdfPCell(new Phrase(ip));
				PdfPCell cell15 = new PdfPCell(new Phrase(equname, contextFont));
				PdfPCell cell16 = new PdfPCell(new Phrase(osname));
				PdfPCell cell17 = new PdfPCell(new Phrase(sum));
				PdfPCell cell18 = new PdfPCell(new Phrase(levelone));
				PdfPCell cell19 = new PdfPCell(new Phrase(leveltwo));
				PdfPCell cell20 = new PdfPCell(new Phrase(levelthree));
				PdfPCell cell21 = new PdfPCell(new Phrase(pingvalue));
				PdfPCell cell22 = new PdfPCell(new Phrase(memvalue));
				PdfPCell cell23 = new PdfPCell(new Phrase(diskvalue));
				PdfPCell cell24 = new PdfPCell(new Phrase(cpuvalue));
				cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
				aTable.addCell(cell19);
				aTable.addCell(cell20);
				aTable.addCell(cell21);
				aTable.addCell(cell22);
				aTable.addCell(cell23);
				aTable.addCell(cell24);

			}
		}

		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// zhushouzhi调用主机事件报表
	public String createEventPDF() {
		String file = "/temp/zhujishijianbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createContextEventPDF(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi-----------------event---pdf

	private String showHostchoce(){
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		// SysLogger.info("ids========="+oids);
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				if (_ids[i] == null || _ids[i].trim().length() == 0)
					continue;
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
		String equipname = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Vector cpuVector = new Vector();
		Vector pdata = new Vector();
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();

		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserView view = new UserView();

		String positionname = view.getPosition(vo.getPosition());
		String username = vo.getName();
		String runmodel = PollingEngine.getCollectwebflag();
		// String position = vo.getPosition();
		String runAppraise = "良";//运行评价
		int levelOneAlarmNum = 0;//告警的条数
		int levelTwoAlarmNum = 0;//告警的条数
		int levelThreeAlarmNum = 0;//告警的条数
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					Hashtable reporthash = new Hashtable();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					// ----------------------------shijian
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();

					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' "
							+ "and recordtime<='" + totime + "' ");
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
					levelOneAlarmNum = levelOneAlarmNum + levelone;
					levelTwoAlarmNum = levelTwoAlarmNum + levletwo;
					levelThreeAlarmNum = levelThreeAlarmNum + levelthree;
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					// ----------------------------------shijian
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					String[] time = { "", "" };
					// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
					String[] item = { "CPU" };
					// hash =
					// hostlastmanager.getbyCategories_share(ip,item,starttime,endtime);
					/*
					 * //hash = hostlastmanager.getbyCategories(ip, item,
					 * startdate, todate);
					 */
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						memhash = hostlastmanager.getMemory_share(ip, "Memory", startdate, todate);
						// memhash =
						// hostlastmanager.getMemory(ip,"Memory",starttime,endtime);
						diskhash = hostlastmanager.getDisk_share(ip, "Disk", startdate, todate);

						Hashtable hdata = (Hashtable) sharedata.get(ip);
						if (hdata == null)
							hdata = new Hashtable();
						cpuVector = (Vector) hdata.get("cpu");
						pdata = (Vector) pingdata.get(ip);
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						// 内存
						MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()),
								nodeDTO.getType(), nodeDTO.getSubtype());
						memhash = memoryInfoService.getCurrMemoryListInfo();
						// 取出当前的硬盘信息
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
						PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						pdata = pingInfoService.getPingInfo();
						// CPU信息
						CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						cpuVector = cpuInfoService.getCpuInfo();
					}
					// diskhash =
					// hostlastmanager.getDisk(ip,"Disk",starttime,endtime);
					// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
					Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization",
						starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					// p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
					// draw_column(diskhash, "", newip + "disk", 750, 150);
					// p_drawchartMultiLine(memoryhash[0], "", newip + "memory",
					// 750, 150);
					//
					// draw_column(diskhash, "", newip + "disk", 750, 150);

					// Hashtable reporthash = new Hashtable();

					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					}

					// CPU

					if (cpuVector != null && cpuVector.size() > 0) {
						for (int si = 0; si < cpuVector.size(); si++) {
							CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
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
					allreporthash.put(ip, reporthash);
				}
				runAppraise = SysUtil.getRunAppraise(levelOneAlarmNum, levelTwoAlarmNum, levelThreeAlarmNum);
			}

			
			
//			String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
//			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
//			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
//			// System.out.println(diskhash+"=======================diskhash========================");
//			report.createReport_hostchoce(starttime, totime, fileName, username, positionname);
//			request.setAttribute("filename", fileName);
			request.setAttribute("allreporthash", allreporthash);
			request.setAttribute("startdate", starttime);
			request.setAttribute("todate", totime);
			request.setAttribute("username", username);
			request.setAttribute("positionname", positionname);
			request.setAttribute("oids", oids);
			request.setAttribute("runAppraise", runAppraise);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/showHostchoce.jsp";
	}
	
	// zhushouzhi-------------------------choce host
	private String hostchoce() {
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
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/hostchoce.jsp";
	}

	// zhushouzhi----------------------------hostchocedoc决策支持
	private String downloadselfhostchocedoc() {
		String oids = getParaValue("ids");
		String businessAnalytics = getParaValue("businessAnalytics");//业务系统运行分析
		String runAppraise = getParaValue("runAppraise");//业务系统运行状况
		if(businessAnalytics == null){
			businessAnalytics = "";
		}
		if (oids == null)
			oids = "";
		// SysLogger.info("ids========="+oids);
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				if (_ids[i] == null || _ids[i].trim().length() == 0)
					continue;
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
		String starttime = startdate ;
		String totime = todate ;
		Hashtable allcpuhash = new Hashtable();
		String ip = "";
		String equipname = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Vector cpuVector = new Vector();
		Vector pdata = new Vector();
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();

		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserView view = new UserView();

		String positionname = view.getPosition(vo.getPosition());
		String username = vo.getName();
		String runmodel = PollingEngine.getCollectwebflag();
		// String position = vo.getPosition();
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					Hashtable reporthash = new Hashtable();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					// ----------------------------shijian
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();

					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' "
							+ "and recordtime<='" + totime + "' ");
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
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					// ----------------------------------shijian
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					String[] time = { "", "" };
					// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
					String[] item = { "CPU" };
					// hash =
					// hostlastmanager.getbyCategories_share(ip,item,starttime,endtime);
					/*
					 * //hash = hostlastmanager.getbyCategories(ip, item,
					 * startdate, todate);
					 */
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						memhash = hostlastmanager.getMemory_share(ip, "Memory", startdate, todate);
						// memhash =
						// hostlastmanager.getMemory(ip,"Memory",starttime,endtime);
						diskhash = hostlastmanager.getDisk_share(ip, "Disk", startdate, todate);

						Hashtable hdata = (Hashtable) sharedata.get(ip);
						if (hdata == null)
							hdata = new Hashtable();
						cpuVector = (Vector) hdata.get("cpu");
						pdata = (Vector) pingdata.get(ip);
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						// 内存
						MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()),
								nodeDTO.getType(), nodeDTO.getSubtype());
						memhash = memoryInfoService.getCurrMemoryListInfo();
						// 取出当前的硬盘信息
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
						PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						pdata = pingInfoService.getPingInfo();
						// CPU信息
						CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						cpuVector = cpuInfoService.getCpuInfo();
					}
					// diskhash =
					// hostlastmanager.getDisk(ip,"Disk",starttime,endtime);
					// 从collectdata中取一段时间的cpu利用率，内存利用率的历史数据以画曲线图，同时取出最大值
					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
					Hashtable[] memoryhash = hostmanager.getMemory(ip, "Memory", starttime, totime);
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

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization",
						starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					// p_draw_line(cpuhash, "", newip + "cpu", 750, 150);
					// draw_column(diskhash, "", newip + "disk", 750, 150);
					// p_drawchartMultiLine(memoryhash[0], "", newip + "memory",
					// 750, 150);
					//
					// draw_column(diskhash, "", newip + "disk", 750, 150);

					// Hashtable reporthash = new Hashtable();

					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					}

					// CPU

					if (cpuVector != null && cpuVector.size() > 0) {
						for (int si = 0; si < cpuVector.size(); si++) {
							CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(si);
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
					allreporthash.put(ip, reporthash);
				}
			}

			String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			// System.out.println(diskhash+"=======================diskhash========================");
			Hashtable dataHash = new Hashtable();
			dataHash.put("businessAnalytics", businessAnalytics);//运行分析
			dataHash.put("runAppraise", runAppraise);//运行评价
			report.setDataHash(dataHash);
			report.createReport_hostchoce(starttime, totime, fileName, username, positionname);
			request.setAttribute("filename", fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/host/download.jsp";
		// return mapping.findForward("report_info");
	}

	// zhushouzhi调用主机cpu利用率报表
	public String createpdfCpu() {
		String file = "/temp/cipanbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createpdfContextCpu(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createpdfContextCpu(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 12, Font.NORMAL);
		Paragraph title = new Paragraph("主机服务器CPU利用率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);

		List cpulist = (List) session.getAttribute("cpulist");// cpu利用率

		Table aTable = new Table(6);
		this.setTableFormat(aTable);
		// int width[] = { 50, 50, 50, 50, 50, 50 };
		// aTable.setWidths(width);
		// aTable.setWidthPercentage(100);
		aTable.addCell(this.setCellFormat(new Phrase("序号", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP地址", contextFont));
		Cell cell11 = new Cell(new Phrase("设备名称", contextFont));
		Cell cell2 = new Cell(new Phrase("操作系统", contextFont));
		Cell cell3 = new Cell(new Phrase("平均值%", contextFont));
		Cell cell4 = new Cell(new Phrase("最大值%", contextFont));
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell4, true);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);

		if (cpulist != null && cpulist.size() > 0) {
			for (int i = 0; i < cpulist.size(); i++) {
				HostNodeDao dao = new HostNodeDao();
				Hashtable cpuhash = (Hashtable) cpulist.get(i);
				String ip = (String) cpuhash.get("ipaddress");
				if (cpuhash == null)
					cpuhash = new Hashtable();
				String cpumax = "";
				String avgcpu = "";
				if (cpuhash.get("max") != null) {
					cpumax = (String) cpuhash.get("max");
				}
				if (cpuhash.get("avgcpucon") != null) {
					avgcpu = (String) cpuhash.get("avgcpucon");
				}
				HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
				String equname = node.getAlias();
				String osname = node.getType();
				Cell cell9 = new Cell(new Phrase(i + 1 + ""));
				Cell cell10 = new Cell(new Phrase(ip));
				Cell cell12 = new Cell(new Phrase(equname, contextFont));
				Cell cell13 = new Cell(new Phrase(osname));
				Cell cell14 = new Cell(new Phrase(avgcpu.replace("%", "")));
				Cell cell15 = new Cell(new Phrase(cpumax.replace("%", "")));

				this.setCellFormat(cell9, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell12, false);
				this.setCellFormat(cell13, false);
				this.setCellFormat(cell14, false);
				this.setCellFormat(cell15, false);

				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell12);
				aTable.addCell(cell13);
				aTable.addCell(cell14);
				aTable.addCell(cell15);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
		// zhushouzhi------------------------------host-cpu pdf
	}
}
