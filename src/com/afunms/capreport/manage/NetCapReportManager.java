package com.afunms.capreport.manage;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;

import com.afunms.application.util.DbConversionUtil;
import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.dao.SubscribeResourcesDao;
import com.afunms.capreport.dao.UtilReportDao;
import com.afunms.capreport.model.ReportValue;
import com.afunms.capreport.model.SubscribeResources;
import com.afunms.capreport.model.UtilReport;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.detail.net.service.NetService;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.node.ArrayInfo;
import com.afunms.polling.node.Battery;
import com.afunms.polling.node.Controller;
import com.afunms.polling.node.CtrlPort;
import com.afunms.polling.node.DIMM;
import com.afunms.polling.node.Disk;
import com.afunms.polling.node.Enclosure;
import com.afunms.polling.node.EnclosureFru;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Lun;
import com.afunms.polling.node.Port;
import com.afunms.polling.node.Processor;
import com.afunms.polling.node.SubSystemInfo;
import com.afunms.polling.node.VFP;
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
import com.afunms.polling.om.IpMacBase;
import com.afunms.polling.om.IpMacChange;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessCollectEntity;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.afunms.system.model.User;
import com.afunms.system.util.UserView;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.IpMacChangeDao;
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


@SuppressWarnings("unchecked")
public class NetCapReportManager extends BaseManager implements ManagerInterface {
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
		request.setAttribute("list", dao.loadNetwork(1));
		return "/capreport/net/list.jsp";
	}

	private String find() {
		int netflag = 0;
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		String bid = getParaValue("bidtext");
		netflag = getParaIntValue("netflag");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadNetworkByBid(1, bid));
		if (netflag == 1) {
			return "/capreport/net/list.jsp";
		} else if (netflag == 2) {
			return "/capreport/net/neteventlist.jsp";
		} else if (netflag == 3) {
			return "/capreport/net/netmultilist.jsp";
		} else
			return "/capreport/net/list.jsp";
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
		request.setAttribute("list", dao.loadNetwork(1));
		return "/capreport/net/neteventlist.jsp";
	}

	private String netmultilist() {
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
		return "/capreport/net/netmultilist.jsp";
	}

	
	private String netping() {
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
					pinghash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable responsehash = new Hashtable();
				try {
					responsehash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ResponseTime", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("hostnode", node);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("responsehash", responsehash);
				ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum") || orderflag.equalsIgnoreCase("responseavg")
				|| orderflag.equalsIgnoreCase("responsemax")) {
			returnList = (List) session.getAttribute("pinglist");
			orderList = (ArrayList) session.getAttribute("orderList");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					Hashtable responsehash = (Hashtable) _pinghash.get("responsehash");
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
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
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
					} else if (orderflag.equalsIgnoreCase("responseavg")) {
						String avgping = "";
						if (ipdiskList.get(5) != null) {
							avgping = (String) ipdiskList.get(5);
						}
						String _avgping = "";
						if (ipdiskList.get(5) != null) {
							_avgping = (String) _ipdiskList.get(5);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
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
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		String responsetimeDivStr = ReportHelper.getChartDivStr(orderList, "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "连通率", "时间", "");
		String responsetimepath = new ReportExport().makeJfreeChartData(responseReportValue.getListValue(), responseReportValue.getIpList(), "响应时间", "时间", "");

		request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		session.setAttribute("pinglist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("pinglist", list);
		return "/capreport/net/netping.jsp";
	}

	// /**
	// * 获取响应时间图形chart的字符串
	// * @param orderList
	// * @return
	// */
	// private String getResonsetimeChartDivStr(List orderList){
	// List pingList = new ArrayList();
	// List pingipList = new ArrayList();
	// for(int i=0; i < orderList.size(); i++){
	// Hashtable tempHash = (Hashtable)orderList.get(i);
	// Hashtable responsetimeHash = null;//连通率hash
	// if(tempHash.containsKey("responsehash")){
	// responsetimeHash = (Hashtable)tempHash.get("responsehash");
	// }
	// //取出连通率的数据值
	// List<Vector> dataList = null;
	// if(responsetimeHash.containsKey("list")){
	// dataList = (List<Vector>)responsetimeHash.get("list");
	// }
	// pingList.add(dataList);
	// pingipList.add(tempHash.get("ipaddress"));
	// }
	// AmChartTool amChartTool = new AmChartTool();
	// String pingdata = amChartTool.makeAmChartData(pingList, pingipList);
	// return pingdata;
	// }

	/**
	 * 根据网络设备采集的连通率和响应时间等的集合，获取jfreechart接口所需要的数据集合
	 * 
	 * @param orderList
	 * @return
	 * @throws ParseException
	 */
	private Hashtable<String, Object> getChartDataHash(List orderList) throws ParseException {
		Hashtable<String, Object> retHash = new Hashtable<String, Object>();
		if (orderList == null) {
			return null;
		}
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		double[][] datas = null;// 数据二维数组
		String[] rowKeys = null;// 曲线的名称的数组
		String columnKeys[] = null;// 列单元格的名称的数组
		datas = new double[orderList.size()][];
		for (int i = 0; i < orderList.size(); i++) {
			Hashtable tempHash = (Hashtable) orderList.get(i);
			Hashtable pinghash = null;// 连通率hash
			// Hashtable responsehash = null;//响应时间hash
			if (tempHash.containsKey("pinghash")) {
				pinghash = (Hashtable) tempHash.get("pinghash");
			}
			// if(tempHash.containsKey("responsehash")){
			// responsehash = (Hashtable)tempHash.get("responsehash");
			// }
			// 取出连通率的数据值
			List<Vector> dataList = null;
			if (pinghash.containsKey("list")) {
				dataList = (List<Vector>) pinghash.get("list");
			}
			if (rowKeys == null) {
				rowKeys = new String[orderList.size()];
			}
			double[] data = new double[dataList.size()];
			if (columnKeys == null) {
				columnKeys = new String[dataList.size()];
			}
			if (dataList != null) {
				for (int j = 0; j < dataList.size(); j++) {
					Vector tempPingVector = (Vector) dataList.get(j);
					String value = (String) tempPingVector.get(0);// value
					data[j] = Double.valueOf(value);
					String collecttime = (String) tempPingVector.get(1);// collecttime
					// tempPingVector.get(2);//unit
					columnKeys[j] = collecttime;
					// Date date = sdf.parse(collecttime);
					// columnKeys[j] = String.valueOf("");
					if (tempHash.containsKey("ipaddress")) {
						rowKeys[i] = (String) tempHash.get("ipaddress");
					}
				}
			}
			datas[i] = data;
		}
		retHash.put("datas", datas);
		retHash.put("rowKeys", rowKeys);
		retHash.put("columnKeys", columnKeys);
		return retHash;
	}

	private String netevent() {
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
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				if (node == null)
					continue;
				EventListDao eventdao = new EventListDao();
				// 得到事件列表
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
				s.append(" and nodeid=" + node.getId());

				List infolist = eventdao.findByCriteria(s.toString());

				// List infolist =
				// eventqueryManager.getQuery(starttime,totime,"99","99",99,node.getId());
				if (infolist != null && infolist.size() > 0) {
					// mainreport = mainreport+ " \r\n";
					int levelone = 0;
					int levletwo = 0;
					int levelthree = 0;
					int pingvalue = 0;
					int cpuvalue = 0;
					int updownvalue = 0;
					int utilvalue = 0;

					for (int j = 0; j < infolist.size(); j++) {
						EventList eventlist = (EventList) infolist.get(j);
						if (eventlist.getContent() == null)
							eventlist.setContent("");
						String content = eventlist.getContent();
						String subentity = eventlist.getSubentity();
						if (eventlist.getLevel1() == 1) {
							levelone = levelone + 1;
						} else if (eventlist.getLevel1() == 2) {
							levletwo = levletwo + 1;
						} else if (eventlist.getLevel1() == 3) {
							levelthree = levelthree + 1;
						}
						// if(content.indexOf("连通率")>=0){
						// pingvalue = pingvalue + 1;
						// }else if(content.indexOf("CPU利用率")>=0){
						// cpuvalue = cpuvalue + 1;
						// }else if(content.indexOf("up")>=0 ||
						// content.indexOf("down")>=0){
						// updownvalue = updownvalue + 1;
						// }else if(content.indexOf("流速")>=0){
						// utilvalue = utilvalue + 1;
						// }

						if (("ping").equals(subentity)) {// 联通率
							pingvalue = pingvalue + 1;
						} else if (("cpu").equals(subentity)) {// cpu
							cpuvalue = cpuvalue + 1;
						} else if (("interface").equals(subentity) && content.indexOf("端口") >= 0) {// 端口
							updownvalue = updownvalue + 1;
						} else if (("interface").equals(subentity) && content.indexOf("端口") < 0) {// 流速
							utilvalue = utilvalue + 1;
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
					ipeventList.add(cpuvalue + "");
					ipeventList.add(updownvalue + "");
					ipeventList.add(utilvalue + "");
					orderList.add(ipeventList);

				}
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("one") || orderflag.equalsIgnoreCase("two") || orderflag.equalsIgnoreCase("three") || orderflag.equalsIgnoreCase("ping")
				|| orderflag.equalsIgnoreCase("cpu") || orderflag.equalsIgnoreCase("updown") || orderflag.equalsIgnoreCase("util") || orderflag.equalsIgnoreCase("sum")) {
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
					} else if (orderflag.equalsIgnoreCase("cpu")) {
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
					} else if (orderflag.equalsIgnoreCase("updown")) {
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
					} else if (orderflag.equalsIgnoreCase("util")) {
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

		// setListProperty(capReportForm, request, list);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", list);
		session.setAttribute("eventlist", list);
		return "/capreport/net/netevent.jsp";
	}

	private String downloadpingreport() {
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
		List memlist = (List) session.getAttribute("pinglist");
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		Hashtable reporthash = new Hashtable();
		reporthash.put("pinglist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);
		reporthash.put("responsetimepath", responsetimepath);
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostping("/temp/hostping_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	private String downloadnetpingreport() {
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
		List memlist = (List) session.getAttribute("pinglist");
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		// 导出响应时间图片
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		Hashtable reporthash = new Hashtable();
		reporthash.put("pinglist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);
		reporthash.put("responsetimepath", responsetimepath);

		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_netping("/temp/netping_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
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

	/**
	 * 导出网络设备的流量信息
	 * 
	 * @return
	 */
	private String downloadnetnetflowreport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		if (!startdate.contains("00:00:00")) {
			startdate = startdate + " 00:00:00";
		}
		if (!todate.contains("23:59:59")) {
			todate = todate + " 23:59:59";
		}
		Hashtable allcpuhash = new Hashtable();

		// I_MonitorIpList monitorManager=new MonitoriplistManager();
		Hashtable netflowHash = (Hashtable) session.getAttribute("netflowHash");// 流速柱状图所需的数据集合
		List flowLineList = (ArrayList) session.getAttribute("flowLineList");// 流速曲线图所需的数据集合
		WritableWorkbook wb = null;
		String fileName = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + "/netflow.xls";
			wb = Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet = wb.createSheet("流量报表", 0);
			WritableCellFormat labelFormat = new WritableCellFormat();
			labelFormat.setBackground(jxl.format.Colour.GRAY_25);
			WritableCellFormat contentlabelFormat = new WritableCellFormat();
			contentlabelFormat.setBackground(jxl.format.Colour.WHITE);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			int row = 0;// 行数
			Label tmpLabel = null;
			tmpLabel = new Label(0, row, "网络设备流量报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, row, 6, 0);// 设定单元格占的column数
			row++;
			tmpLabel = new Label(0, 1, "报表生成时间:" + sdf.format(new Date()), contentlabelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, row, 6, 1);
			row++;
			tmpLabel = new Label(0, 2, "数据统计时间段: " + startdate + " 至 " + todate, contentlabelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, row, 6, 2);
			sheet.setColumnView(0, 15);// 设定列宽
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 15);
			row++;
			// 增加表头
			tmpLabel = new Label(0, row, "序号", labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, row, "源IP地址", labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, row, "总流量(KBytes)", labelFormat);
			sheet.addCell(tmpLabel);
			Iterator tempIterator = netflowHash.keySet().iterator();
			row++;
			int i = 0;
			while (tempIterator.hasNext()) {
				i++;
				String srcip = (String) tempIterator.next();
				double thevalue = (Double) netflowHash.get(srcip);
				thevalue = Double.parseDouble(CommonUtil.format(thevalue, 3));
				tmpLabel = new Label(0, row, i + "", contentlabelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(1, row, srcip, contentlabelFormat);
				sheet.addCell(tmpLabel);
				tmpLabel = new Label(2, row, thevalue + "", contentlabelFormat);
				sheet.addCell(tmpLabel);
				row++;
			}
			row++;
			// 使用jfreeChart生成柱状图
			String path = makeNetFlowJfreeChartData(netflowHash, "流量统计柱状图", "源地址", "总流量(KBytes)", 800, 300);
			// 设置图片
			File file = new File(path);
			// 向sheet里面增加图片,0,0,5,1分别代表列,行,图片宽度占位多少个列,高度占位多少个行
			// allRow = allRow+2;
			sheet.addImage(new WritableImage(0, row, 10, 12, file));
			row = row + 12;
			// 使用jfreeChart生成流速的曲线图
			String netflowLinePicPath = makeNetFlowLinesJfreeChartData(flowLineList, "流量统计曲线图", "日期", "总流量(KBytes)", 800, 300);
			// 设置图片
			File linefile = new File(netflowLinePicPath);
			sheet.addImage(new WritableImage(0, row, 10, 12, linefile));
			// allRow = allRow+7;
			wb.write();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Hashtable reporthash = new Hashtable();
		// reporthash.put("eventlist", memlist);
		request.setAttribute("filename", fileName);
		// request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}

	/**
	 * 导出网络设备的流量信息详细
	 * 
	 * @return
	 */
	private String downloadnetnetflowdetailreport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		if (!startdate.contains("00:00:00")) {
			startdate = startdate + " 00:00:00";
		}
		if (!todate.contains("23:59:59")) {
			todate = todate + " 23:59:59";
		}
		Hashtable allcpuhash = new Hashtable();

		// I_MonitorIpList monitorManager=new MonitoriplistManager();
		List flowDetailList = (ArrayList) session.getAttribute("flowDetailList");
		WritableWorkbook wb = null;
		String fileName = null;
		try {
			fileName = ResourceCenter.getInstance().getSysPath() + "/netflowdetail.xls";
			wb = Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet = wb.createSheet("流量详细报表", 0);
			WritableCellFormat labelFormat = new WritableCellFormat();
			labelFormat.setBackground(jxl.format.Colour.GRAY_25);
			WritableCellFormat contentlabelFormat = new WritableCellFormat();
			contentlabelFormat.setBackground(jxl.format.Colour.WHITE);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			int row = 0;// 行数
			Label tmpLabel = null;
			tmpLabel = new Label(0, row, "网络设备流量报表", labelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, row, 6, 0);// 设定单元格占的column数
			row++;
			tmpLabel = new Label(0, 1, "报表生成时间:" + sdf.format(new Date()), contentlabelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, row, 6, 1);
			row++;
			tmpLabel = new Label(0, 2, "数据统计时间段: " + startdate + " 至 " + todate, contentlabelFormat);
			sheet.addCell(tmpLabel);
			sheet.mergeCells(0, row, 6, 2);
			sheet.setColumnView(0, 15);// 设定列宽
			sheet.setColumnView(1, 15);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			row++;
			// 增加表头
			tmpLabel = new Label(0, row, "序号", labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(1, row, "源IP地址", labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(2, row, "目的IP地址", labelFormat);
			sheet.addCell(tmpLabel);
			tmpLabel = new Label(3, row, "总流量(KBytes)", labelFormat);
			sheet.addCell(tmpLabel);
			row++;
			if (flowDetailList != null) {
				for (int i = 0; i < flowDetailList.size(); i++) {
					Vector vec = (Vector) flowDetailList.get(i);
					tmpLabel = new Label(0, row, String.valueOf(i + 1), contentlabelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(1, row, String.valueOf(vec.get(0)), contentlabelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(2, row, String.valueOf(vec.get(1)), contentlabelFormat);
					sheet.addCell(tmpLabel);
					tmpLabel = new Label(3, row, String.valueOf(vec.get(2)), contentlabelFormat);
					sheet.addCell(tmpLabel);
					row++;
				}
			}
			row++;
			// 使用jfreeChart生成柱状图
			String path = makeNetFlowJfreeChartData(flowDetailList, "流量统计柱状图", "目的地址", "总流量(KBytes)", 800, 300);
			// 设置图片
			File file = new File(path);
			// 向sheet里面增加图片,0,0,5,1分别代表列,行,图片宽度占位多少个列,高度占位多少个行
			// allRow = allRow+2;
			sheet.addImage(new WritableImage(0, row, 10, 12, file));
			// allRow = allRow+7;
			wb.write();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Hashtable reporthash = new Hashtable();
		// reporthash.put("eventlist", memlist);
		request.setAttribute("filename", fileName);
		// request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}

	/**
	 * 生成流量明细jfreeChart图
	 * 
	 * @param flowDetailList
	 * @param ipList
	 * @param title
	 * @param xdesc
	 * @param ydesc
	 * @param chartWith
	 * @param chartHigh
	 * @return
	 */
	private String makeNetFlowJfreeChartData(List flowDetailList, String title, String xdesc, String ydesc, int chartWith, int chartHigh) {
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp" + File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (flowDetailList != null) {
			for (int i = 0; i < flowDetailList.size(); i++) {
				Vector vec = (Vector) flowDetailList.get(i);
				String srcip = String.valueOf(vec.get(1));
				Double thevalue = Double.valueOf(vec.get(2) + "");
				dataset.addValue(thevalue, "", srcip);
			}
		}
		String chartkey = ChartCreator.createBarChart(title, xdesc, ydesc, dataset, chartWith, chartHigh);
		JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter.getInstance().getChartStorage().get(chartkey);
		try {
			File f = new File(chartPath);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f);
			ChartUtilities.writeChartAsPNG(fos, chart.getChart(), chart.getWidth(), chart.getHeight());
		} catch (IOException ioe) {
			chartPath = "";
			SysLogger.error("", ioe);
		}
		return chartPath;
	}

	/**
	 * 生成流量jfreeChart图
	 * 
	 * @param dataList
	 * @param ipList
	 * @param title
	 * @param xdesc
	 * @param ydesc
	 * @param chartWith
	 * @param chartHigh
	 * @return
	 */
	private String makeNetFlowJfreeChartData(Hashtable netflowHash, String title, String xdesc, String ydesc, int chartWith, int chartHigh) {
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp" + File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Iterator tempIterator = netflowHash.keySet().iterator();
		int i = 0;
		while (tempIterator.hasNext()) {
			i++;
			String srcip = (String) tempIterator.next();
			double thevalue = (Double) netflowHash.get(srcip);
			thevalue = Double.parseDouble(CommonUtil.format(thevalue, 3));
			dataset.addValue(thevalue, "", srcip);
		}
		String chartkey = ChartCreator.createBarChart(title, xdesc, ydesc, dataset, chartWith, chartHigh);
		JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter.getInstance().getChartStorage().get(chartkey);
		try {
			File f = new File(chartPath);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f);
			ChartUtilities.writeChartAsPNG(fos, chart.getChart(), chart.getWidth(), chart.getHeight());
		} catch (IOException ioe) {
			chartPath = "";
			SysLogger.error("", ioe);
		}
		return chartPath;
	}

	/**
	 * 生成流量jfreeChart曲线图
	 * 
	 * @param dataList
	 * @param ipList
	 * @param title
	 * @param xdesc
	 * @param ydesc
	 * @param chartWith
	 * @param chartHigh
	 * @return
	 */
	private String makeNetFlowLinesJfreeChartData(List netflowdata, String title, String xdesc, String ydesc, int chartWith, int chartHigh) {
		// 构建日期的List集合
		List dateList = new ArrayList();
		// 源IP地址的集合
		List ipList = new ArrayList();
		for (int i = 0; i < netflowdata.size(); i++) {
			Vector vec = (Vector) netflowdata.get(i);
			if (!ipList.contains(vec.get(0))) {
				ipList.add(vec.get(0));
			}
			if (dateList.contains(vec.get(2))) {
				continue;
			} else {
				dateList.add(vec.get(2));
			}
		}
		List dataList = new ArrayList();
		for (int i = 0; i < ipList.size(); i++) {
			String srcip = (String) ipList.get(i);
			List tempList = new ArrayList();
			for (int j = 0; j < netflowdata.size(); j++) {
				Vector tempVector = (Vector) netflowdata.get(j);
				Vector v = new Vector();
				v.add(0, (String) tempVector.get(1));// 值
				v.add(1, (String) tempVector.get(2));// 日期
				v.add(2, "KBytes");// 单位
				String srciptemp = (String) tempVector.get(0);
				if (srcip.equals(srciptemp)) {
					tempList.add(v);
				}
			}
			dataList.add(tempList);
		}
		// dataList iplist
		ReportExport reportExport = new ReportExport();
		String chartPath = reportExport.makeJfreeChartData(dataList, ipList, title, xdesc, ydesc);
		return chartPath;
	}

	/*
	 * guzhiming 网络设备综合报表 报表订阅模块使用
	 */
	public void createselfnetreport(String startdate, String todate, String ip, String type, String str, WritableWorkbook wb, String filena) {
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
		String equipname = "";
		// zhushouzhi--------------------------start
		String typename = "";
		String equipnameNetDoc = "";
		// zhushouzhi-----------------------------end
		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Vector vector = new Vector();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int cpuvalue = 0;
		int updownvalue = 0;
		int utilvalue = 0;

		try {
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			// ip=node.getIpAddress();
			equipname = node.getAlias() + "(" + ip + ")";
			// zhushouzhi---------------------start
			equipnameNetDoc = node.getAlias();
			// zhushouzhi-----------------------end
			// String remoteip = request.getRemoteAddr();
			String newip = doip(ip);

			// 按排序标志取各端口最新记录的列表
			String orderflag = "index";

			String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };

			vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
			PortconfigDao portdao = new PortconfigDao();
			Hashtable portconfigHash = portdao.getIpsHash(ip);
			List reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
			if (reportports != null && reportports.size() > 0) {
				// SysLogger.info("reportports size
				// ##############"+reportports.size());
				// 显示端口的流速图形
				I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
				String unit = "kb/s";
				String title = starttime + "至" + totime + "端口流速";
				String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
				String[] bandch3 = { "入口流速", "出口流速" };

				for (int i = 0; i < reportports.size(); i++) {
					SysLogger.info(reportports.get(i).getClass() + "===============");
					com.afunms.config.model.Portconfig portconfig = null;
					try {
						portconfig = (com.afunms.config.model.Portconfig) reportports.get(i);
						// 按分钟显示报表
						Hashtable value = new Hashtable();
						value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
						String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
						p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
						String url1 = "../images/jfreechart/" + newip + portconfig.getPortindex() + "ifspeed_day.png";
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}

			reporthash.put("portconfigHash", portconfigHash);
			reporthash.put("reportports", reportports);
			reporthash.put("netifVector", vector);
			// zhushouzhi--------------------start
			reporthash.put("startdate", startdate);
			reporthash.put("todate", todate);
			reporthash.put("totime", totime);
			reporthash.put("starttime", starttime);
			// zhushouzhi-------------------------end
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
			Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
			String pingconavg = "";
			String avgput = "";
			// String avgoutput = "";
			maxhash = new Hashtable();
			String cpumax = "";
			String avgcpu = "";
			String maxput = "";
			if (cpuhash.get("max") != null) {
				cpumax = (String) cpuhash.get("max");
			}
			if (cpuhash.get("avgcpucon") != null) {
				avgcpu = (String) cpuhash.get("avgcpucon");
			}
			// zhushouzhi-----------------------start
			if (streaminHash.get("avgput") != null) {
				avgput = (String) streaminHash.get("avgput");
				reporthash.put("avginput", avgput);
			}
			if (streamoutHash.get("avgput") != null) {
				avgput = (String) streamoutHash.get("avgput");
				reporthash.put("avgoutput", avgput);
			}
			Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "max");
			Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "max");
			if (streammaxinHash.get("max") != null) {
				maxput = (String) streammaxinHash.get("max");
				reporthash.put("maxinput", maxput);
			}
			if (streammaxoutHash.get("max") != null) {
				maxput = (String) streammaxoutHash.get("max");
				reporthash.put("maxoutput", maxput);
			}
			// zhushouzhi--------------------------------------end
			maxhash.put("cpumax", cpumax);
			maxhash.put("avgcpu", avgcpu);

			// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector) _IpRouterHash.get(ip);
			if (vector != null)
				reporthash.put("iprouterVector", vector);
			// zhushouzhi--------------------start
			EventListDao eventdao = new EventListDao();
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
			s.append(" and nodeid=" + node.getId());

			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {
				// mainreport = mainreport+ " \r\n";

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					String subentity = eventlist.getSubentity();
					if (("ping").equals(subentity)) {// 联通率
						pingvalue = pingvalue + 1;
					} else if (("cpu").equals(subentity)) {// cpu
						cpuvalue = cpuvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") >= 0) {// 端口
						updownvalue = updownvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") < 0) {// 流速
						utilvalue = utilvalue + 1;
					}
				}
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("cpuvalue", cpuvalue);
			reporthash.put("updownvalue", updownvalue);
			reporthash.put("utilvalue", utilvalue);
			// zhushouzhi-------------------------end

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
			Vector cpuVector = new Vector();
			if (hdata.get("cpu") != null)
				cpuVector = (Vector) hdata.get("cpu");
			if (cpuVector != null && cpuVector.size() > 0) {
				// for(int si=0;si<cpuVector.size();si++){
				CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
				maxhash.put("cpu", cpudata.getThevalue());
				reporthash.put("CPU", maxhash);
				// }
			} else {
				reporthash.put("CPU", maxhash);
			}
			// 流速

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameNetDoc", equipnameNetDoc);
			reporthash.put("ip", ip);
			if ("network".equals(type)) {
				typename = "网络设备";

			}
			reporthash.put("typename", typename);

			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			// 画图

			p_draw_line(cpuhash, "", newip + "cpu", 740, 120);

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

			// String str = request.getParameter("str");//
			// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			// zhushouzhi--------------------------------
			if ("0".equals(str)) {
				report.createReport_networkWithoutClose(filena, wb);
				SysLogger.info("filename---" + report.getFileName());// excel综合报表
				// request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkDoc(fileName);// word综合报表

					// request.setAttribute("filename", fileName);
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
					String file = "temp/networkNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkNewDoc(fileName);// word运行分析报表

					// request.setAttribute("filename", fileName);
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
					String file = "temp/networkNewknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkNewPdf(fileName);// pdf网络设备业务分析表

					// request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if ("5".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/networkknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkPDF(fileName);

					// request.setAttribute("filename", fileName);
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

		// zhushouzhi---------------------------end
		// return mapping.findForward("report_info");

	}

	private String downloadselfnetreport() {
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
		String ip = "";
		String equipname = "";
		String type = "";
		String typename = "";
		String equipnameNetDoc = "";
		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Vector vector = new Vector();
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int cpuvalue = 0;
		int updownvalue = 0;
		int utilvalue = 0;

		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameNetDoc = node.getAlias();
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);

			// 按排序标志取各端口最新记录的列表
			String orderflag = "index";

			String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };

			vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
			PortconfigDao portdao = new PortconfigDao();
			Hashtable portconfigHash = new Hashtable();

			List reportports = new ArrayList();
			try {
				portconfigHash = portdao.getIpsHash(ip);
				reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
			} catch (Exception e) {

			} finally {
				portdao.close();
			}

			if (reportports != null && reportports.size() > 0) {
				// SysLogger.info("reportports size
				// ##############"+reportports.size());
				// 显示端口的流速图形
				I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
				String unit = "kb/s";
				String title = starttime + "至" + totime + "端口流速";
				String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
				String[] bandch3 = { "入口流速", "出口流速" };

				for (int i = 0; i < reportports.size(); i++) {
					// SysLogger.info(reportports.get(i).getClass()+
					// "===============");
					com.afunms.config.model.Portconfig portconfig = null;
					try {
						portconfig = (com.afunms.config.model.Portconfig) reportports.get(i);
						// 按分钟显示报表
						Hashtable value = new Hashtable();
						value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
						String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
						p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
						String url1 = "../images/jfreechart/" + newip + portconfig.getPortindex() + "ifspeed_day.png";
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}

			reporthash.put("portconfigHash", portconfigHash);
			reporthash.put("reportports", reportports);
			reporthash.put("netifVector", vector);
			// zhushouzhi--------------------start
			reporthash.put("startdate", startdate);
			reporthash.put("todate", todate);
			reporthash.put("totime", totime);
			reporthash.put("starttime", starttime);
			// zhushouzhi-------------------------end
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
			Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
			String pingconavg = "";
			String avgput = "";
			// String avgoutput = "";
			maxhash = new Hashtable();
			String cpumax = "";
			String avgcpu = "";
			String maxput = "";
			if (cpuhash.get("max") != null) {
				cpumax = (String) cpuhash.get("max");
			}
			if (cpuhash.get("avgcpucon") != null) {
				avgcpu = (String) cpuhash.get("avgcpucon");
			}
			// zhushouzhi-----------------------start
			if (streaminHash.get("avgput") != null) {
				avgput = (String) streaminHash.get("avgput");
				reporthash.put("avginput", avgput);
			}
			if (streamoutHash.get("avgput") != null) {
				avgput = (String) streamoutHash.get("avgput");
				reporthash.put("avgoutput", avgput);
			}
			Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "max");
			Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "max");
			if (streammaxinHash.get("max") != null) {
				maxput = (String) streammaxinHash.get("max");
				reporthash.put("maxinput", maxput);
			}
			if (streammaxoutHash.get("max") != null) {
				maxput = (String) streammaxoutHash.get("max");
				reporthash.put("maxoutput", maxput);
			}
			// zhushouzhi--------------------------------------end
			maxhash.put("cpumax", cpumax);
			maxhash.put("avgcpu", avgcpu);

			// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector) _IpRouterHash.get(ip);
			if (vector != null)
				reporthash.put("iprouterVector", vector);
			// zhushouzhi--------------------start
			EventListDao eventdao = new EventListDao();
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
			s.append(" and nodeid=" + node.getId());

			List infolist = new ArrayList();
			try {
				infolist = eventdao.findByCriteria(s.toString());
			} catch (Exception e) {

			} finally {
				eventdao.close();
			}

			if (infolist != null && infolist.size() > 0) {
				// mainreport = mainreport+ " \r\n";

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					String subentity = eventlist.getSubentity();
					if (("ping").equals(subentity)) {// 联通率
						pingvalue = pingvalue + 1;
					} else if (("cpu").equals(subentity)) {// cpu
						cpuvalue = cpuvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") >= 0) {// 端口
						updownvalue = updownvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") < 0) {// 流速
						utilvalue = utilvalue + 1;
					}
				}
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("cpuvalue", cpuvalue);
			reporthash.put("updownvalue", updownvalue);
			reporthash.put("utilvalue", utilvalue);
			// zhushouzhi-------------------------end

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
			Vector cpuVector = new Vector();
			if (hdata.get("cpu") != null)
				cpuVector = (Vector) hdata.get("cpu");
			if (cpuVector != null && cpuVector.size() > 0) {
				// for(int si=0;si<cpuVector.size();si++){
				CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
				maxhash.put("cpu", cpudata.getThevalue());
				reporthash.put("CPU", maxhash);
				// }
			} else {
				reporthash.put("CPU", maxhash);
			}
			// 运行评价
			String grade = "良";
			if (pingvalue + cpuvalue + updownvalue + utilvalue >= 3) {
				grade = "差";
			} else if (pingvalue + cpuvalue + updownvalue + utilvalue < 3 && pingvalue + cpuvalue + updownvalue + utilvalue > 0) {
				grade = "良";
			} else {
				grade = "优";
			}
			reporthash.put("grade", grade);
			// 流速
			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameNetDoc", equipnameNetDoc);
			reporthash.put("ip", ip);
			if ("network".equals(type)) {
				typename = "网络设备";

			}
			reporthash.put("typename", typename);

			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			// 画图

			p_draw_line(cpuhash, "", newip + "cpu", 740, 120);

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			// zhushouzhi--------------------------------
			if ("0".equals(str)) {
				report.createReport_network("temp/networknms_report.xls");
				// SysLogger.info("filename---" +
				// report.getFileName());//excel综合报表
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkDoc(fileName);// word综合报表

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
					String file = "temp/networkNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkNewDoc(fileName);// word运行分析报表

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
					String file = "temp/networkNewknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkNewPdf(fileName);// pdf网络设备业务分析表

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
				try {
					String file = "temp/networkknms_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_networkPDF(fileName);

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

		return "/capreport/net/download.jsp";
		// zhushouzhi---------------------------end
		// return mapping.findForward("report_info");
	}

	private String downloadmultinetreport() {
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
		String equipname = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Vector vector = new Vector();
		// Hashtable reporthash = new Hashtable();
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					// Monitoriplist monitor = monitorManager.getById(ids[i]);
					// Equipment equipment = (Equipment)hostMonitor.get(i);
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// 按排序标志取各端口最新记录的列表
					String orderflag = "index";

					String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };

					vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
					PortconfigDao portdao = new PortconfigDao();
					Hashtable portconfigHash = new Hashtable();
					List reportports = new ArrayList();
					try {
						portconfigHash = portdao.getIpsHash(ip);
						reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
					} catch (Exception e) {

					} finally {
						portdao.close();
					}
					reporthash.put("portconfigHash", portconfigHash);
					reporthash.put("reportports", reportports);
					if (reportports != null && reportports.size() > 0) {
						// 显示端口的流速图形
						I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
						String unit = "kb/s";
						String title = startdate + "至" + todate + "端口流速";
						String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
						String[] bandch3 = { "入口流速", "出口流速" };

						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports.get(k);
							// 按分钟显示报表
							Hashtable value = new Hashtable();
							value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
							String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
							p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
							// String url1 =
							// "/resource/image/jfreechart/"+newip+portconfig.getPortindex()+"ifspeed_day.png";
						}
					}

					reporthash.put("netifVector", vector);

					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
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
					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

					// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
					Hashtable _IpRouterHash = ShareData.getIprouterdata();
					vector = (Vector) _IpRouterHash.get(ip);
					if (vector != null)
						reporthash.put("iprouterVector", vector);

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
					Vector cpuVector = new Vector();
					if (hdata.get("cpu") != null)
						cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						// for(int si=0;si<cpuVector.size();si++){
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						maxhash.put("cpu", cpudata.getThevalue());
						reporthash.put("CPU", maxhash);
						// }
					} else {
						reporthash.put("CPU", maxhash);
					}
					reporthash.put("equipname", equipname);
					reporthash.put("memmaxhash", memmaxhash);
					reporthash.put("memavghash", memavghash);
					allreporthash.put(ip, reporthash);
				}
			}
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			report.createReport_networkall("/temp/networknms_report.xls");
			request.setAttribute("filename", report.getFileName());

		} catch (Exception e) {
			e.printStackTrace();
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
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp
							.get(Calendar.YEAR));
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
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp
					.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






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
		// Paragraph title = new Paragraph("主机服务器连通率报表", titleFont);
		Paragraph title = new Paragraph("连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(8);
		int width[] = { 30, 50, 50, 70, 50, 50, 60, 60 };
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

		Cell cell0 = new Cell("");
		cell0.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell0);
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("设备名称");
		Cell cell2 = new Cell("操作系统");
		Cell cell3 = new Cell("平均连通率");
		Cell cell4 = new Cell("宕机次数");
		Cell cell13 = null;
		cell13 = new Cell("平均响应时间(ms)");
		Cell cell14 = new Cell("最大响应时间(ms)");
		cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell14.setHorizontalAlignment(Element.ALIGN_CENTER);

		cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell13.setBackgroundColor(Color.LIGHT_GRAY);
		cell14.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
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
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);
		// 导出响应时间图片
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		img = Image.getInstance(responsetimepath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);
		document.add(aTable);
		document.close();
	}

	// zhushouzhi调用服务器连通率报表方法
	public String createdoc() {
		String file = "/temp/liantonglvbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContext(fileName);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi服务器事件报表
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
		List eventlist = (List) session.getAttribute("eventlist");
		Table aTable = new Table(12);
		// int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55 };
		// aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 90%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();

		Cell cell0 = new Cell("");
		cell0.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell0);
		Cell cell1 = new Cell("IP地址");
		Cell cell2 = new Cell("设备名称");
		Cell cell3 = new Cell("操作系统");
		Cell cell4 = new Cell("事件总数(个)");
		Cell cell5 = new Cell("普通(个)");
		Cell cell6 = new Cell("紧急(个)");
		Cell cell7 = new Cell("严重(个)");
		Cell cell8 = new Cell("连通率事件(个)");
		Cell cell9 = new Cell("cpu事件(个)");
		Cell cell10 = new Cell("端口事件(个)");
		Cell cell11 = new Cell("流速事件(个)");

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

		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);

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

	// zhushouzhi调用服务器事件报表方法
	public String createdocEvent() {
		String file = "/temp/shijianbaobiao.doc";// 保存到项目文件夹下的指定文件夹
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

	// 之上为zhushouzhi
	// wxy add
	public String networkReport() {
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
		PortconfigDao portconfigDao = new PortconfigDao();
		List interfaceList = new ArrayList();
		try {
			interfaceList = portconfigDao.getAllBySms();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portconfigDao.close();
		}
		List networkList = new ArrayList();
		HostNodeDao dao = new HostNodeDao();
		try {
			networkList = dao.loadNetworkByBid(10, current_user.getBusinessids());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("list", networkList);
		request.setAttribute("interfaceList", interfaceList);
		return "/capreport/net/netWorkReport.jsp";
	}

	// wxy add 统计报表
	private String statisticsReport() {
		BusinessDao businessDao = new BusinessDao();
		List allbusiness = null;
		try {
			allbusiness = businessDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("allbusiness", allbusiness);
		return "/capreport/statistics/statisticsReport.jsp";
	}

	private String networkReportConfig() {
		String id = this.getParaValue("id");

		UtilReportDao dao = new UtilReportDao();
		UtilReport report = dao.findByBid(id);
		String ids = report.getIds();
		String[] idValue = null;
		if (ids != null && !ids.equals("null") && !ids.equals("")) {
			idValue = new String[ids.split(",").length];
			idValue = ids.split(",");
		}
		List<String> list = new ArrayList<String>();
		if (idValue != null) {
			for (int i = 0; i < idValue.length; i++) {
				list.add(idValue[i]);
			}
		}
		SubscribeResources vo = new SubscribeResources();
		SubscribeResourcesDao subDao = new SubscribeResourcesDao();

		vo = subDao.findById(id);
		// ////////////
		String[] frequencyName = { "每天", "每周", "每月", "每季", "每年" };
		String[] monthCh = { " 1月", " 2月", " 3月", " 4月", " 5月", " 6月", " 7月", " 8月", " 9月", " 10月", " 11月", " 12月" };
		String[] weekCh = { " 星期日", " 星期一", " 星期二", " 星期三", " 星期四", " 星期五", " 星期六" };
		String[] dayCh = null;
		String[] hourCh = null;

		StringBuffer sb = new StringBuffer();
		int frequency = vo.getReport_sendfrequency();
		String month = splitDate(vo.getReport_time_month(), monthCh, "month");
		String week = splitDate(vo.getReport_time_week(), weekCh, "week");

		String day = splitDate(vo.getReport_time_day(), dayCh, "day");
		String hour = splitDate(vo.getReport_time_hou(), hourCh, "hour");
		sb.append(frequencyName[frequency - 1] + " ");
		if (month != null && !month.equals(""))
			sb.append(" 月份：(" + month + ")");
		if (week != null && !week.equals(""))
			sb.append(" 星期:(" + week + ")");
		if (day != null && !day.equals(""))
			sb.append(" 日期：(" + day + ")");
		if (hour != null && !hour.equals(""))
			sb.append(" 时间：(" + hour + ")");
		String sendDate = sb.toString();
		// ///////////
		Vector vector = new Vector();
		vector.add(0, report);
		vector.add(1, vo);
		request.setAttribute("vector", vector);
		request.setAttribute("list", list);
		request.setAttribute("sendDate", sendDate);
		return "/capreport/net/netWorkReportDetail.jsp";
	}

	public String splitDate(String item, String[] itemCh, String type) {
		String[] idValue = null;
		String value = "";
		idValue = new String[item.split("/").length];
		idValue = item.split("/");

		for (int i = 0; i < idValue.length; i++) {
			if (!idValue[i].equals("")) {
				if (type.equals("week")) {
					value += itemCh[Integer.parseInt(idValue[i])];
				} else if (type.equals("day")) {
					value += (idValue[i] + "日 ");
				} else if (type.equals("hour")) {
					value += (idValue[i] + "时 ");
				} else {
					value += itemCh[Integer.parseInt(idValue[i]) - 1];
				}
			}
		}

		return value;
	}

	public String executeReport() {
		Hashtable cpuhash = new Hashtable();

		String id = request.getParameter("id");
		String startTime = request.getParameter("startTime");
		if (startTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startTime = sdf.format(new Date()) + " 00:00:00";
		}
		String toTime = request.getParameter("toTime");
		if (toTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toTime = sdf.format(new Date()) + " 23:59:59";
		}
		HostNodeDao hostdao = new HostNodeDao();
		id = "1";
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU", "Utilization", startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("cpuhash", cpuhash);
		return "/capreport/net/netWorkReport.jsp";
	}

	private String downloadReport() {
		String ids = getParaValue("ids");
		String type = getParaValue("type");
		String exportType = getParaValue("exportType");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (ids == null || ids.equals("") || ids.equals("null")) {
			String id = request.getParameter("id");

			if (id.equals("null"))
				return null;
			UtilReport report = new UtilReport();
			UtilReportDao dao = new UtilReportDao();
			report = (UtilReport) dao.findByBid(id);
			ids = report.getIds();
		}
		String filename = "";
		if (type.equals("net")) {
			if (exportType.equals("xls")) {
				filename = "/temp/network_report.xls";
			} else if (exportType.equals("doc")) {
				filename = "/temp/network_report.doc";
			} else if (exportType.equals("pdf")) {
				filename = "/temp/network_report.pdf";
			}
		} else if (type.equals("host")) {
			if (exportType.equals("xls")) {
				filename = "/temp/host_report.xls";
			} else if (exportType.equals("doc")) {
				filename = "/temp/host_report.doc";
			} else if (exportType.equals("pdf")) {
				filename = "/temp/host_report.pdf";
			}
		} else if (type.equals("db")) {
			if (exportType.equals("xls")) {
				filename = "/temp/db_report.xls";
			} else if (exportType.equals("doc")) {
				filename = "/temp/db_report.doc";
			} else if (exportType.equals("pdf")) {
				filename = "/temp/db_report.pdf";
			}
		} else if (type.equals("midware")) {
			if (exportType.equals("xls")) {
				filename = "/temp/db_report.xls";
			} else if (exportType.equals("doc")) {
				filename = "/temp/db_report.doc";
			} else if (exportType.equals("pdf")) {
				filename = "/temp/db_report.pdf";
			}
		}
		String filePath = ResourceCenter.getInstance().getSysPath() + filename;
		String startTime = getParaValue("startdate");
		String toTime = getParaValue("todate");
		int beginHour = getParaIntValue("beginHour");
		int endHour = getParaIntValue("endHour");
		String begin = " " + beginHour + ":00:00";
		String end = " " + endHour + ":59:59";
		if (beginHour < 10) {
			begin = " 0" + beginHour + ":00:00";
		}
		if (endHour < 10) {
			end = " 0" + endHour + ":59:59";
		}
		if (startTime == null) {
			startTime = sdf.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + begin;
		}
		if (toTime == null) {
			toTime = sdf.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + end;
		}
		ReportExport export = new ReportExport();
		export.exportReport(ids, type, filePath, startTime, toTime, exportType);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}

	public String execute(String action) {

		if (action.equalsIgnoreCase("creatXlsEMC")) {
			return creatXlsEMC();
		}
		if (action.equalsIgnoreCase("createpdfEMC")) {
			return createpdfEMC();
		}
		if (action.equalsIgnoreCase("createdocEMC")) {
			return createdocEMC();
		}
		if (action.equalsIgnoreCase("emcenvbakwt")) {
			return emcenvbakwt();
		}
		if (action.equalsIgnoreCase("emcenvbakavgwt")) {
			return emcenvbakavgwt();
		}
		if (action.equalsIgnoreCase("emcenvmemtmp")) {
			return emcenvmemtmp();
		}
		if (action.equalsIgnoreCase("emcenvmemavgtmp")) {
			return emcenvmemavgtmp();
		}
		if (action.equalsIgnoreCase("emcenvmemavgwt")) {
			return emcenvmemavgwt();
		}
		if (action.equalsIgnoreCase("emcenvmemwt")) {
			return emcenvmemwt();
		}
		if (action.equalsIgnoreCase("emcenvarrayavgwt")) {
			return emcenvarrayavgwt();
		}
		if (action.equalsIgnoreCase("emcenvarraywt")) {
			return emcenvarraywt();
		}
		if (action.equalsIgnoreCase("emcdiskread")) {
			return emcdiskread();
		}
		if (action.equalsIgnoreCase("emcdiskwrite")) {
			return emcdiskwrite();
		}
		if (action.equalsIgnoreCase("emcdisksoftread")) {
			return emcdisksoftread();
		}
		if (action.equalsIgnoreCase("emcdisksoftwrite")) {
			return emcdisksoftwrite();
		}
		if (action.equalsIgnoreCase("emcdiskreadkb")) {
			return emcdiskreadkb();
		}
		if (action.equalsIgnoreCase("emcdiskwritekb")) {
			return emcdiskwritekb();
		}
		if (action.equalsIgnoreCase("emcdiskfree")) {
			return emcdiskfree();
		}
		if (action.equalsIgnoreCase("emcdiskbus")) {
			return emcdiskbus();
		}
		if (action.equalsIgnoreCase("emcdiskhardread")) {
			return emcdiskhardread();
		}
		if (action.equalsIgnoreCase("emcdiskhardwrite")) {
			return emcdiskhardwrite();
		}

		if (action.equalsIgnoreCase("emcTotalLength")) {
			return emcTotalLength();
		}
		if (action.equalsIgnoreCase("emcSofterrors")) {
			return emcLunSofterrors();
		}
		if (action.equalsIgnoreCase("emcHarderrors")) {
			return emcLunHarderrors();
		}
		if (action.equals("vmwarecpuCR")) {
			return vmwarecpuCR();
		}
		if (action.equals("vmwarecputotalCR")) {
			return vmwarecpuuseCR();
		}
		if (action.equals("vmwarememCR")) {
			return vmwarememCR();
		}
		if (action.equals("vmwarememtotalCR")) {
			return vmwarememuseCR();
		}
		if (action.equals("vmwarecpuRP")) {
			return vmwarecpuRP();
		}
		if (action.equals("creatXlsVM")) {
			return creatXlsVM();
		}
		if (action.equals("createdocVM")) {
			return createdocVM();
		}
		if (action.equals("createpdfVM")) {
			return createpdfVM();
		}
		if (action.equals("vmwaremem")) {
			return vmwaremem();
		}
		if (action.equals("vmwarememin")) {
			return vmwarememin();
		}
		if (action.equals("vmwarememout")) {
			return vmwarememout();
		}
		if (action.equals("vmwaredisk")) {
			return vmwaredisk();
		}
		if (action.equals("vmwarenet")) {
			return vmwarenet();
		}
		if (action.equals("vmwarecpuuse")) {
			return vmwarecpuuse();
		}
		if (action.equals("vmwarecpu")) {
			return vmwarecpu();
		}
		if (action.equals("storageSwitch")) {
			return storageLunSwitch();
		}
		if (action.equals("storageLunCon")) {
			return storageLunCon();
		}
		if (action.equals("storageNumber")) {
			return storageWwnNumber();
		}
		if (action.equals("storageWwnCon")) {
			return storageWwnCon();
		}
		if (action.equals("storageSafety")) {
			return storageSlunSafety();
		}
		if (action.equals("storageSlunCon")) {
			return storageSlunCon();
		}
		if (action.equals("storageHDS")) {
			return storageHDS();
		}
		if (action.equals("storageFan")) {
			return storageFan();
		}
		if (action.equals("storageEnv")) {
			return storageEnv();
		}
		if (action.equals("storageDrive")) {
			return storageDrive();
		}
		if (action.equals("storageCpu")) {
			return storageCpu();
		}
		if (action.equals("storageCable")) {
			return storageCable();
		}
		if (action.equals("storageCache")) {
			return storageCache();
		}
		if (action.equals("storageButter")) {
			return storageButter();
		}
		if (action.equals("storageMemory")) {
			return storageMemory();
		}
		if (action.equals("createdocStorage")) {
			return createdocStorage();
		}
		if (action.equals("createpdfStorage")) {
			return createpdfStorage();
		}
		if (action.equals("creatXlsStorage")) {
			return creatXlsStorage();
		}
		if (action.equals("list"))
			return list();
		if (action.equals("eventlist"))
			return eventlist();
		if (action.equals("netmultilist"))
			return netmultilist();
		if (action.equals("netping"))
			return netping();
		if (action.equals("netevent"))
			return netevent();
		if (action.equals("downloadpingreport"))
			return downloadpingreport();
		if (action.equals("downloadnetpingreport"))
			return downloadnetpingreport();
		if (action.equals("downloadnetnetflowreport"))
			return downloadnetnetflowreport();
		if (action.equals("downloadnetnetflowdetailreport"))
			return downloadnetnetflowdetailreport();
		if (action.equals("downloadneteventreport"))
			return downloadneteventreport();
		if (action.equals("downloadselfnetreport"))
			return downloadselfnetreport();
		if (action.equals("downloadmultinetreport"))
			return downloadmultinetreport();
		if (action.equals("createdoc"))
			return createdoc();
		if (action.equals("createdocEvent"))
			return createdocEvent();
		if (action.equals("createpdf"))
			return createpdf();
		if (action.equals("createdocEventPDF"))
			return createdocEventPDF();
		if (action.equals("choceDoc"))
			return choceDoc();
		if (action.equals("downloadselfnetchocereport"))
			return downloadselfnetchocereport();

		// 山西移动oa网络接口平均流速统计报表（konglq）
		if (action.equals("netIfxls")) {
			return downloadsxoaIfreport();
		}
		if (action.equals("showPingReport")) {
			return showNetworkPingReport();
		}
		if (action.equals("showEventReport")) {
			return showNetworkEventReport();
		}
		if (action.equals("showCompositeReport")) {
			return showNetworkCompositeReport();
		}
		if (action.equals("showNetworkConfigReport")) {
			return showNetworkConfigReport();
		}
		if (action.equals("downloadNetworkPingReport")) {
			return downloadNetworkPingReport();
		}
		if (action.equals("downloadNetworkEventReport")) {
			return downloadNetworkEventReport();
		}
		if (action.equals("downloadNetworkCompositeReport")) {
			return downloadNetworkCompositeReport();
		}
		if (action.equals("downloadNetworkConfigReport")) {
			return downloadNetworkConfigReport();
		}
		if (action.equals("find")) {
			return find();
		}
		if (action.equals("networkReport"))
			return networkReport();
		if (action.equals("executeReport"))
			return executeReport();
		if (action.equals("networkReportConfig"))
			return networkReportConfig();
		if (action.equals("downloadReport"))
			return downloadReport();
		if (action.equals("showChoce"))
			return showChoce();
		if (action.equals("statisticsReport"))
			return statisticsReport();
		if (action.equals("showHPstorPingReport"))
			// HP存储联通率报表
			return showHPstorPingReport();
		if (action.equals("showHPstorDiskReport"))
			// HP存储磁盘报表
			return showHPstorDiskReport();
		if (action.equals("showHPstorCompositeReport"))
			// HP存储综合报表
			return showHPstorCompositeReport();
		if (action.equals("downloadHPStorageDiskReport"))
			// 导出HP存储磁盘报表
			return downloadHPStorageDiskReport();
		if (action.equals("downloadHPStorageCompReport"))
			// 导出HP存储综合报表
			return downloadHPStorageCompReport();

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
		// //System.out.println("newip="+newip);
		// return newip;
		ip = ip.replaceAll("\\.", "_");
		return ip;
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
							Minute minute = new Minute(tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.DAY_OF_MONTH), tempCal
									.get(Calendar.MONTH) + 1, tempCal.get(Calendar.YEAR));
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
								Minute minute = new Minute(tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.DAY_OF_MONTH), tempCal
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
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
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
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
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
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
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
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
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
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
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
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
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
						Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp
								.get(Calendar.YEAR));
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

	// zhushouzhi--------------------------start pdf
	public void createPdfContextPing(String file) throws DocumentException, IOException {

		// com.lowagie.text.Font FontChinese = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);

		// 设置纸张大小

		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// com.lowagie.text.Font titleFont = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
		// 正文字体风格
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = new Paragraph("连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 11, Font.NORMAL, Color.black);
		List pinglist = (List) session.getAttribute("pinglist");
		PdfPTable aTable = new PdfPTable(8);

		int width[] = { 30, 50, 50, 70, 50, 50, 60, 60 };
		aTable.setWidthPercentage(100);
		aTable.setWidths(width);

		PdfPCell c = new PdfPCell(new Phrase("", fontChinese));
		c.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(c);
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", fontChinese));
		PdfPCell cell11 = new PdfPCell(new Phrase("设备名称", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("操作系统", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("平均连通率", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("宕机次数", contextFont));
		PdfPCell cell15 = new PdfPCell(new Phrase("平均响应时间(ms)", contextFont));
		PdfPCell cell16 = new PdfPCell(new Phrase("最大响应时间(ms)", contextFont));
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
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
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
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
				PdfPCell cell7 = new PdfPCell(new Paragraph(equname, fontChinese));
				PdfPCell cell8 = new PdfPCell(new Phrase(osname, contextFont));
				PdfPCell cell9 = new PdfPCell(new Phrase(avgping));
				PdfPCell cell10 = new PdfPCell(new Phrase(downnum));

				PdfPCell cell17 = new PdfPCell(new Phrase(responseavg.replace("毫秒", "")));
				PdfPCell cell18 = new PdfPCell(new Phrase(responsemax.replace("毫秒", "")));

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
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		// 导出响应时间图片
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		// document.add(new Paragraph("\n"));
		// String ipaddress = (String)request.getParameter("ipaddress");
		// String newip = doip(ipaddress);
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(75);
		document.add(img);
		img = Image.getInstance(responsetimepath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.close();
	}

	public void createDocContexxtEventPDF() {

	}

	// zhushouzhi调用服务器连通率报表方法
	public String createpdf() {
		String file = "/temp/liantonglvbaobiao.PDF";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createPdfContextPing(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi调用服务器连通率报表方法---pdf

	// zhushouzhi-----------------------pdf--eventlist
	public void createDocContextEventPDF(String file) throws DocumentException, IOException {
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
		title.setSpacingAfter(10);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List eventlist = (List) session.getAttribute("eventlist");
		PdfPTable aTable = new PdfPTable(12);
		// int width[] = { 30, 70, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55 };
		// aTable.setWidths(width);
		aTable.setWidthPercentage(100);

		PdfPCell cell0 = new PdfPCell(new Phrase(""));
		cell0.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell0);
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("设备名称", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("操作系统", contextFont));
		PdfPCell cell5 = new PdfPCell(new Phrase("事件总数(个)", contextFont));
		PdfPCell cell61 = new PdfPCell(new Phrase("普通（个）", contextFont));
		PdfPCell cell6 = new PdfPCell(new Phrase("紧急(个)", contextFont));
		PdfPCell cell7 = new PdfPCell(new Phrase("严重(个)", contextFont));
		PdfPCell cell8 = new PdfPCell(new Phrase("连通率事件(个)", contextFont));
		PdfPCell cell9 = new PdfPCell(new Phrase("cpu事件(个)", contextFont));
		PdfPCell cell10 = new PdfPCell(new Phrase("端口事件(个)", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("流速事件(个)", contextFont));

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell61.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		cell9.setBackgroundColor(Color.LIGHT_GRAY);
		cell10.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);

		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell61.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell61.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell5);
		aTable.addCell(cell61);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);
		aTable.addCell(cell11);
		document.add(aTable);
		aTable = new PdfPTable(12);
		// aTable.setWidths(width);
		aTable.setWidthPercentage(100);
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
				PdfPCell cell16 = new PdfPCell(new Phrase(osname, contextFont));
				PdfPCell cell17 = new PdfPCell(new Phrase(sum));
				PdfPCell cell18 = new PdfPCell(new Phrase(levelone));
				PdfPCell cell19 = new PdfPCell(new Phrase(levelone));
				PdfPCell cell20 = new PdfPCell(new Phrase(levelthree));
				PdfPCell cell21 = new PdfPCell(new Phrase(levelthree));
				PdfPCell cell22 = new PdfPCell(new Phrase(levelthree));
				PdfPCell cell23 = new PdfPCell(new Phrase(levelthree));
				PdfPCell cell24 = new PdfPCell(new Phrase(levelthree));

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

	public String createdocEventPDF() {
		String file = "/temp/shijianbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextEventPDF(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public String choceDoc() {
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
		return "/capreport/net/choce.jsp";
	}

	/**
	 * 预览决策支持报表页面
	 * 
	 * @return
	 */
	private String showChoce() {
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
		String equipname = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserView view = new UserView();

		String positionname = view.getPosition(vo.getPosition());
		String username = vo.getName();
		// String position = vo.getPosition();
		Vector vector = new Vector();
		// Hashtable reporthash = new Hashtable();
		String runAppraise = "良";// 运行评价
		int levelOneAlarmNum = 0;// 告警的条数
		int levelTwoAlarmNum = 0;// 告警的条数
		int levelThreeAlarmNum = 0;// 告警的条数
		try {
			Hashtable allreporthash = new Hashtable();

			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					if (node == null)
						continue;
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();
					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
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
					levelOneAlarmNum = levelOneAlarmNum + levelone;
					levelTwoAlarmNum = levelTwoAlarmNum + levletwo;
					levelThreeAlarmNum = levelThreeAlarmNum + levelthree;
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					// ------------------------事件end
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// 按排序标志取各端口最新记录的列表
					String orderflag = "index";

					String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };

					vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
					PortconfigDao portdao = new PortconfigDao();
					Hashtable portconfigHash = portdao.getIpsHash(ip);
					// Hashtable portconfigHash =
					// portconfigManager.getIpsHash(ip);
					reporthash.put("portconfigHash", portconfigHash);

					List reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
					reporthash.put("reportports", reportports);
					if (reportports != null && reportports.size() > 0) {
						// 显示端口的流速图形
						I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
						String unit = "kb/s";
						String title = startdate + "至" + todate + "端口流速";
						String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
						String[] bandch3 = { "入口流速", "出口流速" };

						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports.get(k);
							// 按分钟显示报表
							Hashtable value = new Hashtable();
							value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
							String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
							p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
							// String url1 =
							// "/resource/image/jfreechart/"+newip+portconfig.getPortindex()+"ifspeed_day.png";
						}
					}

					reporthash.put("netifVector", vector);

					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
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
					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

					// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
					Hashtable _IpRouterHash = ShareData.getIprouterdata();
					vector = (Vector) _IpRouterHash.get(ip);
					if (vector != null)
						reporthash.put("iprouterVector", vector);

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
					Vector cpuVector = new Vector();
					if (hdata.get("cpu") != null)
						cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						// for(int si=0;si<cpuVector.size();si++){
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						maxhash.put("cpu", cpudata.getThevalue());
						reporthash.put("CPU", maxhash);
						// }
					} else {
						reporthash.put("CPU", maxhash);
					}// -----流速
					Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
					Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
					String avgput = "";
					if (streaminHash.get("avgput") != null) {
						avgput = (String) streaminHash.get("avgput");
						reporthash.put("avginput", avgput);
					}
					if (streamoutHash.get("avgput") != null) {
						avgput = (String) streamoutHash.get("avgput");
						reporthash.put("avgoutput", avgput);
					}
					Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "max");
					Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "max");
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

					allreporthash.put(ip, reporthash);
				}
				runAppraise = SysUtil.getRunAppraise(levelOneAlarmNum, levelTwoAlarmNum, levelThreeAlarmNum);
			}
			// String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
			// String fileName = ResourceCenter.getInstance().getSysPath() +
			// file;// 获取系统文件夹路径
			// ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
			// allreporthash);
			//
			// report.createReport_networkchoce(starttime, totime, fileName,
			// username, positionname);
			// request.setAttribute("filename", fileName);
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
		return "/capreport/net/showChoce.jsp";
	}

	// zhushouzhi------------------------------------end pdf

	// zhushouzhi-----------------------------start
	private String downloadselfnetchocereport() {
		String oids = getParaValue("ids");
		String businessAnalytics = getParaValue("businessAnalytics");// 业务系统运行分析
		String runAppraise = getParaValue("runAppraise");// 业务系统运行状况
		if (businessAnalytics == null) {
			businessAnalytics = "";
		}
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
		String starttime = startdate;
		String totime = todate;
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
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					if (node == null)
						continue;
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();
					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
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
					reporthash.put("levelone", levelone + "");
					reporthash.put("levletwo", levletwo + "");
					reporthash.put("levelthree", levelthree + "");
					// ------------------------事件end
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// 按排序标志取各端口最新记录的列表
					String orderflag = "index";

					String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };

					vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
					PortconfigDao portdao = new PortconfigDao();
					Hashtable portconfigHash = portdao.getIpsHash(ip);
					// Hashtable portconfigHash =
					// portconfigManager.getIpsHash(ip);
					reporthash.put("portconfigHash", portconfigHash);

					List reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
					reporthash.put("reportports", reportports);
					if (reportports != null && reportports.size() > 0) {
						// 显示端口的流速图形
						I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
						String unit = "kb/s";
						String title = startdate + "至" + todate + "端口流速";
						String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
						String[] bandch3 = { "入口流速", "出口流速" };

						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports.get(k);
							// 按分钟显示报表
							Hashtable value = new Hashtable();
							value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
							String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
							p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
							// String url1 =
							// "/resource/image/jfreechart/"+newip+portconfig.getPortindex()+"ifspeed_day.png";
						}
					}

					reporthash.put("netifVector", vector);

					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
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
					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

					// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
					Hashtable _IpRouterHash = ShareData.getIprouterdata();
					vector = (Vector) _IpRouterHash.get(ip);
					if (vector != null)
						reporthash.put("iprouterVector", vector);

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
					Vector cpuVector = new Vector();
					if (hdata.get("cpu") != null)
						cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						// for(int si=0;si<cpuVector.size();si++){
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						maxhash.put("cpu", cpudata.getThevalue());
						reporthash.put("CPU", maxhash);
						// }
					} else {
						reporthash.put("CPU", maxhash);
					}// -----流速
					Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
					Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
					String avgput = "";
					if (streaminHash.get("avgput") != null) {
						avgput = (String) streaminHash.get("avgput");
						reporthash.put("avginput", avgput);
					}
					if (streamoutHash.get("avgput") != null) {
						avgput = (String) streamoutHash.get("avgput");
						reporthash.put("avgoutput", avgput);
					}
					Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "max");
					Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "max");
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

					allreporthash.put(ip, reporthash);
				}
			}
			String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			Hashtable dataHash = new Hashtable();
			dataHash.put("businessAnalytics", businessAnalytics);// 运行分析
			dataHash.put("runAppraise", runAppraise);// 运行评价
			report.setDataHash(dataHash);
			report.createReport_networkchoce(starttime, totime, fileName, username, positionname);
			request.setAttribute("filename", fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	// zhushouzhi-------------------------------end

	// ===============================生成oa需要报表数据

	/**
	 * 
	 * 调用此方法来解决山西移动oa 统计网络设备接口流速报表 方法已经写好就差生成报表的表格方法
	 * 
	 */
	public String downloadsxoaIfreport() {
		String oids = getParaValue("ids");// 获取要检查的id
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
		String equipname = "";

		Hashtable allreporthash = new Hashtable();
		Vector vector = new Vector();

		try {

			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					// Monitoriplist monitor = monitorManager.getById(ids[i]);
					// Equipment equipment = (Equipment)hostMonitor.get(i);
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					// 按排序标志取各端口最新记录的列表
					String orderflag = "index";

					// 获取端口列表
					PortconfigDao portdao = new PortconfigDao();
					// Hashtable portconfigHash = portdao.getIpsHash(ip);

					// 需要做报表的列表
					List reportports = portdao.getByIpAndReportflag(ip, new Integer(1));

					if (reportports != null && reportports.size() > 0) {
						// 显示端口的流速图形
						I_HostCollectDataDay daymanager = new HostCollectDataDayManager();

						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports.get(k);

							Hashtable value = new Hashtable();
							value = ((HostCollectDataDayManager) daymanager).getmultiHisHdx_OA(ip, portconfig.getPortindex() + "", startdate, todate, "UtilHdx", portconfig
									.getName(), portconfig.getLinkuse());
							vector.add(value);
						}
					}

				}
			}

			// reporthash.put("netifVector",vector);

			allreporthash.put("sxoanetifreport", vector);
			// 报表接口需要改一下

			// -----------------需要协助修改---------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			// -----------------------------------------------

			report.createReport_oawork("/temp/networknms_report.xls", startdate, todate);
			// report.createReport_networkall("/temp/networknms_report.xls");
			request.setAttribute("filename", report.getFileName());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	private String showNetworkPingReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String ipaddress = getParaValue("ipaddress");
		String newip = doip(ipaddress);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("newip", newip);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

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
		HostNodeDao dao = new HostNodeDao();
		List selectedIPNodeList = dao.findBynode("ip_address", ipaddress);
		if (selectedIPNodeList != null && selectedIPNodeList.size() > 0) {
			HostNode node = (HostNode) selectedIPNodeList.get(0);

			request.setAttribute("nodeid", node.getId());

			Hashtable pinghash = new Hashtable();
			try {
				pinghash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			try {
				responsehash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ResponseTime", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("responsehash", responsehash);
			orderList.add(ipmemhash);

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum") || orderflag.equalsIgnoreCase("responseavg")
				|| orderflag.equalsIgnoreCase("responsemax")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					Hashtable responsehash = (Hashtable) _pinghash.get("responsehash");
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
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
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
					} else if (orderflag.equalsIgnoreCase("responseavg")) {
						String avgping = "";
						if (ipdiskList.get(5) != null) {
							avgping = (String) ipdiskList.get(5);
						}
						String _avgping = "";
						if (ipdiskList.get(5) != null) {
							_avgping = (String) _ipdiskList.get(5);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
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
		String pingconavg = "";
		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(ipaddress, "Ping", "ConnectUtilization", starttime, totime);
		} catch (Exception e) {

		}
		if (ConnectUtilizationhash.get("avgpingcon") != null)
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
		String ConnectUtilizationmax = "";
		// maxping.put("avgpingcon", pingconavg);
		if (ConnectUtilizationhash.get("max") != null) {
			ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
		}
		// maxping.put("pingmax", ConnectUtilizationmax);

		// Hashtable ConnectUtilizationhash = new Hashtable();
		// try{
		// ConnectUtilizationhash =
		// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",getStartTime(),getToTime());
		// }catch(Exception ex){
		// ex.printStackTrace();
		// }

		Hashtable ResponseTimehash = new Hashtable();
		try {
			ResponseTimehash = hostmanager.getCategory(ipaddress, "Ping", "ResponseTime", starttime, totime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String[] bandch = { "连通率", "响应时间" };
		String[] bandch1 = { "连通率(%)", "响应时间(ms)" };

		String returnStr = area_chooseDrawLineType("minute", ConnectUtilizationhash, ResponseTimehash, bandch, bandch1, 350, 200, 110, 20);
		SysLogger.info("returnStr######## " + returnStr + " ############");
		JFreeChartBrother jfb = (JFreeChartBrother) ResourceCenter.getInstance().getChartStorage().get(returnStr);

		String imgpath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imgpath + returnStr + ".png");// 将统计图标输出成JPG文件
			ChartUtilities.writeChartAsJPEG(fos, // 输出到哪个输出流
					1,// JPEG图片的质量，0~1之间
					jfb.getChart(), // 统计图标对象
					740, // 宽
					200,// 宽
					null// ChartRenderingInfo 信息
					);
			fos.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

		session.setAttribute("pingpath", imgpath + returnStr + ".png");

		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pinglist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("pinglist", list);
		request.setAttribute("imgpath", returnStr + ".png");
		return "/capreport/net/showNetworkPingReport.jsp";
	}

	/*
	 * HP存储连通率报表
	 */
	private String showHPstorPingReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String ipaddress = getParaValue("ipaddress");
		String newip = doip(ipaddress);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("newip", newip);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

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
		HostNodeDao dao = new HostNodeDao();
		List selectedIPNodeList = dao.findBynode("ip_address", ipaddress);
		if (selectedIPNodeList != null && selectedIPNodeList.size() > 0) {
			HostNode node = (HostNode) selectedIPNodeList.get(0);

			request.setAttribute("nodeid", node.getId());

			Hashtable pinghash = new Hashtable();
			try {
				pinghash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			try {
				responsehash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ResponseTime", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("responsehash", responsehash);
			orderList.add(ipmemhash);

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum") || orderflag.equalsIgnoreCase("responseavg")
				|| orderflag.equalsIgnoreCase("responsemax")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					Hashtable responsehash = (Hashtable) _pinghash.get("responsehash");
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
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
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
					} else if (orderflag.equalsIgnoreCase("responseavg")) {
						String avgping = "";
						if (ipdiskList.get(5) != null) {
							avgping = (String) ipdiskList.get(5);
						}
						String _avgping = "";
						if (ipdiskList.get(5) != null) {
							_avgping = (String) _ipdiskList.get(5);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
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
		String pingconavg = "";
		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(ipaddress, "Ping", "ConnectUtilization", starttime, totime);
		} catch (Exception e) {

		}
		if (ConnectUtilizationhash.get("avgpingcon") != null)
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
		String ConnectUtilizationmax = "";
		// maxping.put("avgpingcon", pingconavg);
		if (ConnectUtilizationhash.get("max") != null) {
			ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
		}
		// maxping.put("pingmax", ConnectUtilizationmax);

		// Hashtable ConnectUtilizationhash = new Hashtable();
		// try{
		// ConnectUtilizationhash =
		// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",getStartTime(),getToTime());
		// }catch(Exception ex){
		// ex.printStackTrace();
		// }

		Hashtable ResponseTimehash = new Hashtable();
		try {
			ResponseTimehash = hostmanager.getCategory(ipaddress, "Ping", "ResponseTime", starttime, totime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String[] bandch = { "连通率", "响应时间" };
		String[] bandch1 = { "连通率(%)", "响应时间(ms)" };

		String returnStr = area_chooseDrawLineType("minute", ConnectUtilizationhash, ResponseTimehash, bandch, bandch1, 350, 200, 110, 20);
		SysLogger.info("returnStr######## " + returnStr + " ############");
		JFreeChartBrother jfb = (JFreeChartBrother) ResourceCenter.getInstance().getChartStorage().get(returnStr);

		String imgpath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imgpath + returnStr + ".png");// 将统计图标输出成JPG文件
			ChartUtilities.writeChartAsJPEG(fos, // 输出到哪个输出流
					1,// JPEG图片的质量，0~1之间
					jfb.getChart(), // 统计图标对象
					740, // 宽
					200,// 宽
					null// ChartRenderingInfo 信息
					);
			fos.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}

		p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

		session.setAttribute("pingpath", imgpath + returnStr + ".png");

		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pinglist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("pinglist", list);
		request.setAttribute("imgpath", returnStr + ".png");
		return "/application/hpstorage/showPingReport.jsp";
	}

	/*
	 * HP存储磁盘报表 由于现在需求还不是很明确，所以，这里的报表只展示最新采集到的HP存储磁盘信息
	 */
	private String showHPstorDiskReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String ipaddress = getParaValue("ipaddress");
		// String newip = doip(ipaddress);
		request.setAttribute("ipaddress", ipaddress);
		// request.setAttribute("newip", newip);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		String nodeid = getParaValue("id");
		request.setAttribute("nodeid", Integer.parseInt(nodeid));
		return "/application/hpstorage/showDiskReport.jsp";
	}

	/*
	 * HP存储综合报表 由于现在需求还不是很明确，所以，这里的报表只展示最新采集到的HP存储综合信息
	 * 
	 */
	private String showHPstorCompositeReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String ipaddress = getParaValue("ipaddress");
		// String newip = doip(ipaddress);
		request.setAttribute("ipaddress", ipaddress);
		// request.setAttribute("newip", newip);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		String nodeid = getParaValue("id");
		request.setAttribute("nodeid", Integer.parseInt(nodeid));
		return "/application/hpstorage/showCompositeReport.jsp";
	}

	/*
	 * 导出HP存储磁盘报表
	 * 
	 */
	private String downloadHPStorageDiskReport() {
		String str = request.getParameter("str");
		String ip = request.getParameter("ipaddress");
		if (str.equals("1")) {
			String file = "/temp/" + ip + "hpstoragediskreport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				// createDocContext(fileName);
				creatHPstorageDiskDoc(fileName, ip);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}
		if (str.equals("0")) {
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

			List<Disk> disks = null;
			Hashtable ipAllData = new Hashtable();
			// 只取当前采集的磁盘信息
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ip);
			if (ipAllData != null) {
				disks = (List<Disk>) ipAllData.get("disks");
			}

			Hashtable reporthash = new Hashtable();

			reporthash.put("disks", disks);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			report.setRequest(request);
			report.createReport_HPstorageDisk("/temp/" + ip + "hpstoragediskreport.xls", ip);
			request.setAttribute("filename", report.getFileName());
		}
		if (str.equals("4")) {
			String file = "/temp/" + ip + "hpstoragediskreport.PDF";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				// createPdfContextPing(fileName);
				createHPstorageDiskPdf(fileName, ip);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}

		return "/capreport/net/download.jsp";
	}

	/*
	 * 导出HP存储综合报表
	 * 
	 */
	private String downloadHPStorageCompReport() {
		String str = request.getParameter("str");
		String ip = request.getParameter("ipaddress");
		if (str.equals("1")) {
			String file = "/temp/" + ip + "hpstoragecompreport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				// createDocContext(fileName);
				creatHPstorageCompDoc(fileName, ip);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}
		if (str.equals("0")) {
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

			List<Disk> disks = null;
			ArrayInfo arrayinfo = null;
			List<Enclosure> enclosures = null;
			List<Controller> controllers = null;
			List<Port> ports = null;
			List<Lun> luns = null;
			List<VFP> vfps = null;
			SubSystemInfo subSystemInfo = null;
			Hashtable ipAllData = new Hashtable();
			// 只取当前采集的综合信息
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ip);
			if (ipAllData != null) {
				disks = (List<Disk>) ipAllData.get("disks");
				arrayinfo = (ArrayInfo) ipAllData.get("arrayinfo");
				enclosures = (List<Enclosure>) ipAllData.get("enclosures");
				controllers = (List<Controller>) ipAllData.get("controllers");
				ports = (List<Port>) ipAllData.get("ports");
				luns = (List<Lun>) ipAllData.get("luns");
				vfps = (List<VFP>) ipAllData.get("vfps");
				subSystemInfo = (SubSystemInfo) ipAllData.get("subSystemInfo");
			}

			Hashtable reporthash = new Hashtable();

			reporthash.put("disks", disks);
			reporthash.put("arrayinfo", arrayinfo);
			reporthash.put("enclosures", enclosures);
			reporthash.put("controllers", controllers);
			reporthash.put("ports", ports);
			reporthash.put("luns", luns);
			reporthash.put("vfps", vfps);
			reporthash.put("subSystemInfo", subSystemInfo);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			report.setRequest(request);
			report.createReport_HPstorageComp("/temp/" + ip + "hpstoragecompreport.xls", ip);
			request.setAttribute("filename", report.getFileName());
		}
		if (str.equals("4")) {
			String file = "/temp/" + ip + "hpstoragecompreport.PDF";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				// createPdfContextPing(fileName);
				createHPstorageCompPdf(fileName, ip);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}

		return "/capreport/net/download.jsp";
	}

	/*
	 * HP存储综合生成DOC文档
	 */
	public void creatHPstorageCompDoc(String file, String ipaddress) throws DocumentException, IOException {

		List<Disk> disks = null;
		ArrayInfo arrayinfo = null;
		List<Enclosure> enclosures = null;
		List<Controller> controllers = null;
		List<Port> ports = null;
		List<Lun> luns = null;
		List<VFP> vfps = null;
		SubSystemInfo subSystemInfo = null;

		Hashtable ipAllData = new Hashtable();
		// 只取当前采集的综合信息
		ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ipaddress);
		if (ipAllData != null) {
			disks = (List<Disk>) ipAllData.get("disks");
			arrayinfo = (ArrayInfo) ipAllData.get("arrayinfo");
			enclosures = (List<Enclosure>) ipAllData.get("enclosures");
			controllers = (List<Controller>) ipAllData.get("controllers");
			ports = (List<Port>) ipAllData.get("ports");
			luns = (List<Lun>) ipAllData.get("luns");
			vfps = (List<VFP>) ipAllData.get("vfps");
			subSystemInfo = (SubSystemInfo) ipAllData.get("subSystemInfo");
		}

		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		Font subtitle = new Font(bfChinese, 8, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		// Paragraph title = new Paragraph("主机服务器连通率报表", titleFont);
		Paragraph title = new Paragraph("HP存储" + ipaddress + "综合报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		// /Font fontChinese = new Font(bfChinese, 12, Font.NORMAL,
		// Color.black);

		if (arrayinfo != null) {// 阵列信息
			Table arrtable = new Table(4);
			int arrwidth[] = { 100, 100, 100, 100 };
			arrtable.setWidths(arrwidth);
			arrtable.setWidth(100); // 占页面宽度 90%
			arrtable.setAlignment(Element.ALIGN_CENTER);// 居中显示
			arrtable.setAutoFillEmptyCells(true); // 自动填满
			arrtable.setBorderWidth(1); // 边框宽度
			arrtable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			arrtable.setPadding(2);// 衬距，看效果就知道什么意思了
			arrtable.setSpacing(0);// 即单元格之间的间距
			arrtable.setBorder(2);// 边框
			arrtable.endHeaders();

			Cell arrtitle = new Cell("阵列信息");
			arrtitle.setBackgroundColor(Color.LIGHT_GRAY);
			arrtitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			arrtitle.setColspan(4);

			Cell arrcell1 = new Cell("阵列状态:");
			Cell arrcell2 = new Cell("固件版本:");
			Cell arrcell3 = new Cell("产品版本:");
			Cell arrcell4 = new Cell("本地控制器产品版本:");
			Cell arrcell5 = new Cell("远程控制器产品版本:");
			Cell arrcell6 = new Cell("");
			Cell arrcell66 = new Cell("");

			Cell arrcell11 = new Cell(SysUtil.ifNull(arrayinfo.getArrayStatus()));
			Cell arrcell22 = new Cell(SysUtil.ifNull(arrayinfo.getFirmwareRevision()));
			Cell arrcell33 = new Cell(SysUtil.ifNull(arrayinfo.getProductRevision()));
			Cell arrcell44 = new Cell(SysUtil.ifNull(arrayinfo.getLocalControllerProductRevision()));
			Cell arrcell55 = new Cell(SysUtil.ifNull(arrayinfo.getRemoteControllerProductRevision()));

			arrcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell5.setHorizontalAlignment(Element.ALIGN_CENTER);

			arrcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell22.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell33.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell44.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell55.setHorizontalAlignment(Element.ALIGN_CENTER);

			arrtable.addCell(arrtitle);
			arrtable.addCell(arrcell1);
			arrtable.addCell(arrcell11);
			arrtable.addCell(arrcell2);
			arrtable.addCell(arrcell22);
			arrtable.addCell(arrcell3);
			arrtable.addCell(arrcell33);
			arrtable.addCell(arrcell4);
			arrtable.addCell(arrcell44);
			arrtable.addCell(arrcell5);
			arrtable.addCell(arrcell55);
			arrtable.addCell(arrcell6);
			arrtable.addCell(arrcell66);
			document.add(arrtable);
		}

		if (enclosures != null && enclosures.size() > 0) {// 附件信息
			for (int i = 0; i < enclosures.size(); i++) {
				Enclosure encl = enclosures.get(i);

				Table encltable = new Table(4);
				int enclwidth[] = { 100, 100, 100, 100 };
				encltable.setWidths(enclwidth);
				encltable.setWidth(100); // 占页面宽度 90%
				encltable.setAlignment(Element.ALIGN_CENTER);// 居中显示
				encltable.setAutoFillEmptyCells(true); // 自动填满
				encltable.setBorderWidth(1); // 边框宽度
				encltable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
				encltable.setPadding(2);// 衬距，看效果就知道什么意思了
				encltable.setSpacing(0);// 即单元格之间的间距
				encltable.setBorder(2);// 边框
				encltable.endHeaders();

				Cell encltitle = new Cell("附件信息");
				encltitle.setBackgroundColor(Color.LIGHT_GRAY);
				encltitle.setHorizontalAlignment(Element.ALIGN_LEFT);
				encltitle.setColspan(4);

				Cell enclcell1 = new Cell("附件名称:");
				Cell enclcell2 = new Cell("附件编号:");
				Cell enclcell3 = new Cell("附加状态:");
				Cell enclcell4 = new Cell("附件类型:");
				Cell enclcell5 = new Cell("附件全局名称:");
				Cell enclcell6 = new Cell("");
				Cell enclcell66 = new Cell("");

				Cell enclcell11 = new Cell(SysUtil.ifNull(encl.getName()));
				Cell enclcell22 = new Cell(SysUtil.ifNull(encl.getEnclosureID()));
				Cell enclcell33 = new Cell(SysUtil.ifNull(encl.getEnclosureStatus()));
				Cell enclcell44 = new Cell(SysUtil.ifNull(encl.getEnclosureType()));
				Cell enclcell55 = new Cell(SysUtil.ifNull(encl.getNodeWWN()));

				enclcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell5.setHorizontalAlignment(Element.ALIGN_CENTER);

				enclcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell33.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell44.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell55.setHorizontalAlignment(Element.ALIGN_CENTER);

				encltable.addCell(encltitle);
				encltable.addCell(enclcell1);
				encltable.addCell(enclcell11);
				encltable.addCell(enclcell2);
				encltable.addCell(enclcell22);
				encltable.addCell(enclcell3);
				encltable.addCell(enclcell33);
				encltable.addCell(enclcell4);
				encltable.addCell(enclcell44);
				encltable.addCell(enclcell5);
				encltable.addCell(enclcell55);
				encltable.addCell(enclcell6);
				encltable.addCell(enclcell66);
				document.add(encltable);
				if (encl.getFrus() != null && encl.getFrus().size() > 0) {
					Table enclfrutable = new Table(4);
					int enclfruwidth[] = { 100, 100, 100, 100 };
					enclfrutable.setWidths(enclfruwidth);
					enclfrutable.setWidth(100); // 占页面宽度 90%
					enclfrutable.setAlignment(Element.ALIGN_CENTER);// 居中显示
					enclfrutable.setAutoFillEmptyCells(true); // 自动填满
					encltable.setBorderWidth(1); // 边框宽度
					enclfrutable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
					enclfrutable.setPadding(2);// 衬距，看效果就知道什么意思了
					enclfrutable.setSpacing(0);// 即单元格之间的间距
					enclfrutable.setBorder(2);// 边框
					enclfrutable.endHeaders();

					Cell enclfrutitle = new Cell("FRU信息");
					enclfrutitle.setBackgroundColor(Color.LIGHT_GRAY);
					enclfrutitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					enclfrutitle.setColspan(4);
					enclfrutable.addCell(enclfrutitle);

					Cell enclfrucell1 = new Cell("名称:");
					Cell enclfrucell2 = new Cell("组件:");
					Cell enclfrucell3 = new Cell("标识:");
					Cell enclfrucell4 = new Cell("状态:");

					enclfrucell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					enclfrucell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					enclfrucell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					enclfrucell4.setHorizontalAlignment(Element.ALIGN_CENTER);

					enclfrutable.addCell(enclfrucell1);
					enclfrutable.addCell(enclfrucell2);
					enclfrutable.addCell(enclfrucell3);
					enclfrutable.addCell(enclfrucell4);

					for (int j = 0; j < encl.getFrus().size(); j++) {
						EnclosureFru fru = encl.getFrus().get(j);

						Cell enclfrucell11 = new Cell(SysUtil.ifNull(fru.getFru()));
						Cell enclfrucell22 = new Cell(SysUtil.ifNull(fru.getHwComponent()));
						Cell enclfrucell33 = new Cell(SysUtil.ifNull(fru.getIdentification()));
						Cell enclfrucell44 = new Cell(SysUtil.ifNull(fru.getIdStatus()));

						enclfrucell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						enclfrucell22.setHorizontalAlignment(Element.ALIGN_CENTER);
						enclfrucell33.setHorizontalAlignment(Element.ALIGN_CENTER);
						enclfrucell44.setHorizontalAlignment(Element.ALIGN_CENTER);

						enclfrutable.addCell(enclfrucell11);
						enclfrutable.addCell(enclfrucell22);
						enclfrutable.addCell(enclfrucell33);
						enclfrutable.addCell(enclfrucell44);
					}
					document.add(enclfrutable);
				}

			}

		}

		if (controllers != null) {// 控制器信息
			for (int i = 0; i < controllers.size(); i++) {
				Controller cont = controllers.get(i);

				Table conttable = new Table(4);
				int enclwidth[] = { 100, 100, 100, 100 };
				conttable.setWidths(enclwidth);
				conttable.setWidth(100); // 占页面宽度 90%
				conttable.setAlignment(Element.ALIGN_CENTER);// 居中显示
				conttable.setAutoFillEmptyCells(true); // 自动填满
				conttable.setBorderWidth(1); // 边框宽度
				conttable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
				conttable.setPadding(2);// 衬距，看效果就知道什么意思了
				conttable.setSpacing(0);// 即单元格之间的间距
				conttable.setBorder(2);// 边框
				conttable.endHeaders();

				Cell conttitle = new Cell("控制器信息");
				conttitle.setBackgroundColor(Color.LIGHT_GRAY);
				conttitle.setHorizontalAlignment(Element.ALIGN_LEFT);
				conttitle.setColspan(4);

				Cell contcell1 = new Cell("名称:");
				Cell contcell2 = new Cell("状态:");
				Cell contcell3 = new Cell("序列号:");
				Cell contcell4 = new Cell("供货商编号:");
				Cell contcell5 = new Cell("产品编号:");
				Cell contcell6 = new Cell("产品版本:");
				Cell contcell7 = new Cell("固件版本:");
				Cell contcell8 = new Cell("制造商产品编码:");
				Cell contcell9 = new Cell("控制类型:");
				Cell contcell10 = new Cell("充电电池固件版本:");
				Cell contcell11 = new Cell("附件交换设置:");
				Cell contcell12 = new Cell("附件编号:");
				Cell contcell13 = new Cell("连环对:");
				Cell contcell14 = new Cell("连环编号:");
				Cell contcell15 = new Cell("基础驱动地址:");
				Cell contcell16 = new Cell("牢固地址:");

				contcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell16.setHorizontalAlignment(Element.ALIGN_CENTER);

				Cell vcontcell1 = new Cell(SysUtil.ifNull(cont.getName()));
				Cell contcell22 = new Cell(SysUtil.ifNull(cont.getStatus()));
				Cell contcell33 = new Cell(SysUtil.ifNull(cont.getSerialNumber()));
				Cell contcell44 = new Cell(SysUtil.ifNull(cont.getVendorID()));
				Cell contcell55 = new Cell(SysUtil.ifNull(cont.getProductID()));
				Cell contcell66 = new Cell(SysUtil.ifNull(cont.getProductRevision()));
				Cell contcell77 = new Cell(SysUtil.ifNull(cont.getFirmwareRevision()));
				Cell contcell88 = new Cell(SysUtil.ifNull(cont.getManufacturingProductCode()));
				Cell contcell99 = new Cell(SysUtil.ifNull(cont.getControllerType()));
				Cell contcell100 = new Cell(SysUtil.ifNull(cont.getBatteryChargerFirmwareRevision()));
				Cell contcell111 = new Cell(SysUtil.ifNull(cont.getEnclosureSwitchSetting()));
				Cell contcell122 = new Cell(SysUtil.ifNull(cont.getEnclosureID()));
				Cell contcell133 = new Cell(SysUtil.ifNull(cont.getLoopPair()));
				Cell contcell144 = new Cell(SysUtil.ifNull(cont.getLoopID()));
				Cell contcell155 = new Cell(SysUtil.ifNull(cont.getDriveAddressBasis()));
				Cell contcell166 = new Cell(SysUtil.ifNull(cont.getHardAddress()));

				vcontcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell33.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell44.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell55.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell66.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell77.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell88.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell99.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell100.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell111.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell122.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell133.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell144.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell155.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell166.setHorizontalAlignment(Element.ALIGN_CENTER);

				conttable.addCell(conttitle);
				conttable.addCell(contcell1);
				conttable.addCell(vcontcell1);
				conttable.addCell(contcell2);
				conttable.addCell(contcell22);
				conttable.addCell(contcell3);
				conttable.addCell(contcell33);
				conttable.addCell(contcell4);
				conttable.addCell(contcell44);
				conttable.addCell(contcell5);
				conttable.addCell(contcell55);
				conttable.addCell(contcell6);
				conttable.addCell(contcell66);
				conttable.addCell(contcell7);
				conttable.addCell(contcell77);
				conttable.addCell(contcell8);
				conttable.addCell(contcell88);
				conttable.addCell(contcell9);
				conttable.addCell(contcell99);
				conttable.addCell(contcell10);
				conttable.addCell(contcell100);
				conttable.addCell(contcell11);
				conttable.addCell(contcell111);
				conttable.addCell(contcell12);
				conttable.addCell(contcell122);
				conttable.addCell(contcell13);
				conttable.addCell(contcell133);
				conttable.addCell(contcell14);
				conttable.addCell(contcell144);
				conttable.addCell(contcell15);
				conttable.addCell(contcell155);
				conttable.addCell(contcell16);
				conttable.addCell(contcell166);
				document.add(conttable);

				if (cont.getFrontPortList() != null && cont.getFrontPortList().size() > 0) {
					Table fptable = new Table(4);
					int fpwidth[] = { 100, 100, 100, 100 };
					fptable.setWidths(fpwidth);
					fptable.setWidth(100); // 占页面宽度 90%
					fptable.setAlignment(Element.ALIGN_CENTER);// 居中显示
					fptable.setAutoFillEmptyCells(true); // 自动填满
					fptable.setBorderWidth(1); // 边框宽度
					fptable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
					fptable.setPadding(2);// 衬距，看效果就知道什么意思了
					fptable.setSpacing(0);// 即单元格之间的间距
					fptable.setBorder(2);// 边框
					fptable.endHeaders();

					Cell fptitle = new Cell("前端口列表");
					fptitle.setBackgroundColor(Color.LIGHT_GRAY);
					fptitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					fptitle.setColspan(4);
					fptable.addCell(fptitle);

					for (int j = 0; j < cont.getFrontPortList().size(); j++) {
						CtrlPort fctp = cont.getFrontPortList().get(j);
						Cell fpcell1 = new Cell("名称:");
						Cell fpcell2 = new Cell("状态:");
						Cell fpcell3 = new Cell("端口实例:");
						Cell fpcell4 = new Cell("物理地址:");
						Cell fpcell5 = new Cell("链接声明:");
						Cell fpcell6 = new Cell("节点全局名称:");
						Cell fpcell7 = new Cell("端口全局名称:");
						Cell fpcell8 = new Cell("拓扑:");
						Cell fpcell9 = new Cell("传输速率:");
						Cell fpcell10 = new Cell("端口编号:");
						Cell fpcell11 = new Cell("设备名称:");
						Cell fpcell12 = new Cell("设备路径:");

						fpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						Cell vfpcell1 = new Cell(SysUtil.ifNull(fctp.getName()));
						Cell vfpcell2 = new Cell(SysUtil.ifNull(fctp.getStatus()));
						Cell vfpcell3 = new Cell(SysUtil.ifNull(fctp.getPortInstance()));
						Cell vfpcell4 = new Cell(SysUtil.ifNull(fctp.getHardAddress()));
						Cell vfpcell5 = new Cell(SysUtil.ifNull(fctp.getLinkState()));
						Cell vfpcell6 = new Cell(SysUtil.ifNull(fctp.getNodeWWN()));
						Cell vfpcell7 = new Cell(SysUtil.ifNull(fctp.getPortWWN()));
						Cell vfpcell8 = new Cell(SysUtil.ifNull(fctp.getTopology()));
						Cell vfpcell9 = new Cell(SysUtil.ifNull(fctp.getDataRate()));
						Cell vfpcell10 = new Cell(SysUtil.ifNull(fctp.getPortID()));
						Cell vfpcell11 = new Cell(SysUtil.ifNull(fctp.getDeviceHostName()));
						Cell vfpcell12 = new Cell(SysUtil.ifNull(fctp.getDevicePath()));

						vfpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						fptable.addCell(fpcell1);
						fptable.addCell(vfpcell1);
						fptable.addCell(fpcell2);
						fptable.addCell(vfpcell2);
						fptable.addCell(fpcell3);
						fptable.addCell(vfpcell3);
						fptable.addCell(fpcell4);
						fptable.addCell(vfpcell4);
						fptable.addCell(fpcell5);
						fptable.addCell(vfpcell5);
						fptable.addCell(fpcell6);
						fptable.addCell(vfpcell6);
						fptable.addCell(fpcell7);
						fptable.addCell(vfpcell7);
						fptable.addCell(fpcell8);
						fptable.addCell(vfpcell8);
						fptable.addCell(fpcell9);
						fptable.addCell(vfpcell9);
						fptable.addCell(fpcell10);
						fptable.addCell(vfpcell10);
						fptable.addCell(fpcell11);
						fptable.addCell(vfpcell11);
						fptable.addCell(fpcell12);
						fptable.addCell(vfpcell12);
					}
					document.add(fptable);
				}
				if (cont.getBackPortList() != null && cont.getBackPortList().size() > 0) {
					Table bptable = new Table(4);
					int bpwidth[] = { 100, 100, 100, 100 };
					bptable.setWidths(bpwidth);
					bptable.setWidth(100); // 占页面宽度 90%
					bptable.setAlignment(Element.ALIGN_CENTER);// 居中显示
					bptable.setAutoFillEmptyCells(true); // 自动填满
					bptable.setBorderWidth(1); // 边框宽度
					bptable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
					bptable.setPadding(2);// 衬距，看效果就知道什么意思了
					bptable.setSpacing(0);// 即单元格之间的间距
					bptable.setBorder(2);// 边框
					bptable.endHeaders();

					Cell bptitle = new Cell("后端口列表");
					bptitle.setBackgroundColor(Color.LIGHT_GRAY);
					bptitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					bptitle.setColspan(4);
					bptable.addCell(bptitle);

					for (int j = 0; j < cont.getBackPortList().size(); j++) {
						CtrlPort bctp = cont.getBackPortList().get(j);
						Cell bpcell1 = new Cell("名称:");
						Cell bpcell2 = new Cell("状态:");
						Cell bpcell3 = new Cell("端口实例:");
						Cell bpcell4 = new Cell("物理地址:");
						Cell bpcell5 = new Cell("链接声明:");
						Cell bpcell6 = new Cell("节点全局名称:");
						Cell bpcell7 = new Cell("端口全局名称:");
						Cell bpcell8 = new Cell("拓扑:");
						Cell bpcell9 = new Cell("传输速率:");
						Cell bpcell10 = new Cell("端口编号:");
						Cell bpcell11 = new Cell("设备名称:");
						Cell bpcell12 = new Cell("设备路径:");

						bpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						bpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						Cell vbpcell1 = new Cell(SysUtil.ifNull(bctp.getName()));
						Cell vbpcell2 = new Cell(SysUtil.ifNull(bctp.getStatus()));
						Cell vbpcell3 = new Cell(SysUtil.ifNull(bctp.getPortInstance()));
						Cell vbpcell4 = new Cell(SysUtil.ifNull(bctp.getHardAddress()));
						Cell vbpcell5 = new Cell(SysUtil.ifNull(bctp.getLinkState()));
						Cell vbpcell6 = new Cell(SysUtil.ifNull(bctp.getNodeWWN()));
						Cell vbpcell7 = new Cell(SysUtil.ifNull(bctp.getPortWWN()));
						Cell vbpcell8 = new Cell(SysUtil.ifNull(bctp.getTopology()));
						Cell vbpcell9 = new Cell(SysUtil.ifNull(bctp.getDataRate()));
						Cell vbpcell10 = new Cell(SysUtil.ifNull(bctp.getPortID()));
						Cell vbpcell11 = new Cell(SysUtil.ifNull(bctp.getDeviceHostName()));
						Cell vbpcell12 = new Cell(SysUtil.ifNull(bctp.getDevicePath()));

						vbpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						vbpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						bptable.addCell(bpcell1);
						bptable.addCell(vbpcell1);
						bptable.addCell(bpcell2);
						bptable.addCell(vbpcell2);
						bptable.addCell(bpcell3);
						bptable.addCell(vbpcell3);
						bptable.addCell(bpcell4);
						bptable.addCell(vbpcell4);
						bptable.addCell(bpcell5);
						bptable.addCell(vbpcell5);
						bptable.addCell(bpcell6);
						bptable.addCell(vbpcell6);
						bptable.addCell(bpcell7);
						bptable.addCell(vbpcell7);
						bptable.addCell(bpcell8);
						bptable.addCell(vbpcell8);
						bptable.addCell(bpcell9);
						bptable.addCell(vbpcell9);
						bptable.addCell(bpcell10);
						bptable.addCell(vbpcell10);
						bptable.addCell(bpcell11);
						bptable.addCell(vbpcell11);
						bptable.addCell(bpcell12);
						bptable.addCell(vbpcell12);
					}
					document.add(bptable);
				}
				if (cont.getDimmList() != null && cont.getDimmList().size() > 0) {
					Table dimtable = new Table(4);
					int dimwidth[] = { 100, 100, 100, 100 };
					dimtable.setWidths(dimwidth);
					dimtable.setWidth(100); // 占页面宽度 90%
					dimtable.setAlignment(Element.ALIGN_CENTER);// 居中显示
					dimtable.setAutoFillEmptyCells(true); // 自动填满
					dimtable.setBorderWidth(1); // 边框宽度
					dimtable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
					dimtable.setPadding(2);// 衬距，看效果就知道什么意思了
					dimtable.setSpacing(0);// 即单元格之间的间距
					dimtable.setBorder(2);// 边框
					dimtable.endHeaders();

					Cell dimtitle = new Cell("双列直插式存储模块列表");
					dimtitle.setBackgroundColor(Color.LIGHT_GRAY);
					dimtitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					dimtitle.setColspan(4);
					dimtable.addCell(dimtitle);

					for (int j = 0; j < cont.getDimmList().size(); j++) {
						DIMM dimm = cont.getDimmList().get(j);
						Cell dimcell1 = new Cell("名称:");
						Cell dimcell2 = new Cell("状态:");
						Cell dimcell3 = new Cell("容量:");
						Cell dimcell4 = new Cell("");

						dimcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dimcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						dimcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						dimcell4.setHorizontalAlignment(Element.ALIGN_CENTER);

						Cell vdimcell1 = new Cell(SysUtil.ifNull(dimm.getName()));
						Cell vdimcell2 = new Cell(SysUtil.ifNull(dimm.getStatus()));
						Cell vdimcell3 = new Cell(SysUtil.ifNull(dimm.getCapacity()));
						Cell vdimcell4 = new Cell("");

						vdimcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						vdimcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						vdimcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						vdimcell4.setHorizontalAlignment(Element.ALIGN_CENTER);

						dimtable.addCell(dimcell1);
						dimtable.addCell(vdimcell1);
						dimtable.addCell(dimcell2);
						dimtable.addCell(vdimcell2);
						dimtable.addCell(dimcell3);
						dimtable.addCell(vdimcell3);
						dimtable.addCell(dimcell4);
						dimtable.addCell(vdimcell4);
					}
					document.add(dimtable);
				}
				if (cont.getBattery() != null) {
					Battery battery = cont.getBattery();
					Table battable = new Table(4);
					int batwidth[] = { 100, 100, 100, 100 };
					battable.setWidths(batwidth);
					battable.setWidth(100); // 占页面宽度 90%
					battable.setAlignment(Element.ALIGN_CENTER);// 居中显示
					battable.setAutoFillEmptyCells(true); // 自动填满
					battable.setBorderWidth(1); // 边框宽度
					battable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
					battable.setPadding(2);// 衬距，看效果就知道什么意思了
					battable.setSpacing(0);// 即单元格之间的间距
					battable.setBorder(2);// 边框
					battable.endHeaders();

					Cell battitle = new Cell("电池信息");
					battitle.setBackgroundColor(Color.LIGHT_GRAY);
					battitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					battitle.setColspan(4);
					battable.addCell(battitle);

					Cell batcell1 = new Cell("名称:");
					Cell batcell2 = new Cell("状态:");
					Cell batcell3 = new Cell("标识:");
					Cell batcell4 = new Cell("厂商:");
					Cell batcell5 = new Cell("设备名称:");
					Cell batcell6 = new Cell("生产日期:");
					Cell batcell7 = new Cell("剩余电量:");
					Cell batcell8 = new Cell("剩余电量百分比:");
					Cell batcell9 = new Cell("电压:");
					Cell batcell10 = new Cell("放电周期:");

					batcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell10.setHorizontalAlignment(Element.ALIGN_CENTER);

					Cell vbatcell1 = new Cell(SysUtil.ifNull(battery.getName()));
					Cell vbatcell2 = new Cell(SysUtil.ifNull(battery.getStatus()));
					Cell vbatcell3 = new Cell(SysUtil.ifNull(battery.getIdentification()));
					Cell vbatcell4 = new Cell(SysUtil.ifNull(battery.getManufacturerName()));
					Cell vbatcell5 = new Cell(SysUtil.ifNull(battery.getDeviceName()));
					Cell vbatcell6 = new Cell(SysUtil.ifNull(battery.getManufacturerDate()));
					Cell vbatcell7 = new Cell(SysUtil.ifNull(battery.getRemainingCapacity()));
					Cell vbatcell8 = new Cell(SysUtil.ifNull(battery.getPctRemainingCapacity()));
					Cell vbatcell9 = new Cell(SysUtil.ifNull(battery.getVoltage()));
					Cell vbatcell10 = new Cell(SysUtil.ifNull(battery.getDischargeCycles()));

					vbatcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell10.setHorizontalAlignment(Element.ALIGN_CENTER);

					battable.addCell(batcell1);
					battable.addCell(vbatcell1);
					battable.addCell(batcell2);
					battable.addCell(vbatcell2);
					battable.addCell(batcell3);
					battable.addCell(vbatcell3);
					battable.addCell(batcell4);
					battable.addCell(vbatcell4);
					battable.addCell(batcell5);
					battable.addCell(vbatcell5);
					battable.addCell(batcell6);
					battable.addCell(vbatcell6);
					battable.addCell(batcell7);
					battable.addCell(vbatcell7);
					battable.addCell(batcell8);
					battable.addCell(vbatcell8);
					battable.addCell(batcell9);
					battable.addCell(vbatcell9);
					battable.addCell(batcell10);
					battable.addCell(vbatcell10);

					document.add(battable);
				}
				if (cont.getProcessor() != null) {
					Processor processor = cont.getProcessor();
					Table protable = new Table(4);
					int prowidth[] = { 100, 100, 100, 100 };
					protable.setWidths(prowidth);
					protable.setWidth(100); // 占页面宽度 90%
					protable.setAlignment(Element.ALIGN_CENTER);// 居中显示
					protable.setAutoFillEmptyCells(true); // 自动填满
					protable.setBorderWidth(1); // 边框宽度
					protable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
					protable.setPadding(2);// 衬距，看效果就知道什么意思了
					protable.setSpacing(0);// 即单元格之间的间距
					protable.setBorder(2);// 边框
					protable.endHeaders();

					Cell protitle = new Cell("处理器信息");
					protitle.setBackgroundColor(Color.LIGHT_GRAY);
					protitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					protitle.setColspan(4);
					protable.addCell(protitle);

					Cell procell1 = new Cell("名称:");
					Cell procell2 = new Cell("状态:");
					Cell procell3 = new Cell("标识:");
					Cell procell4 = new Cell("");

					procell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					procell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					procell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					procell4.setHorizontalAlignment(Element.ALIGN_CENTER);

					Cell vprocell1 = new Cell(SysUtil.ifNull(processor.getName()));
					Cell vprocell2 = new Cell(SysUtil.ifNull(processor.getStatus()));
					Cell vprocell3 = new Cell(SysUtil.ifNull(processor.getIdentification()));
					Cell vprocell4 = new Cell("");

					vprocell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					vprocell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					vprocell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					vprocell4.setHorizontalAlignment(Element.ALIGN_CENTER);

					protable.addCell(procell1);
					protable.addCell(vprocell1);
					protable.addCell(procell2);
					protable.addCell(vprocell2);
					protable.addCell(procell3);
					protable.addCell(vprocell3);
					protable.addCell(procell4);
					protable.addCell(vprocell4);
					document.add(protable);
				}

			}
		}

		if (ports != null && ports.size() > 0) {
			Table portTable = new Table(6);
			int portwidth[] = { 50, 50, 50, 100, 50, 150 };
			portTable.setWidths(portwidth);
			portTable.setWidth(100); // 占页面宽度 90%
			portTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
			portTable.setAutoFillEmptyCells(true); // 自动填满
			portTable.setBorderWidth(1); // 边框宽度
			portTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			portTable.setPadding(2);// 衬距，看效果就知道什么意思了
			portTable.setSpacing(0);// 即单元格之间的间距
			portTable.setBorder(2);// 边框
			portTable.endHeaders();

			Cell porttitle = new Cell("端口信息");
			porttitle.setBackgroundColor(Color.LIGHT_GRAY);
			porttitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			porttitle.setColspan(6);
			portTable.addCell(porttitle);

			Cell portcell1 = new Cell("名称");
			Cell portcell2 = new Cell("编号");
			Cell portcell3 = new Cell("行为");
			Cell portcell4 = new Cell("端口拓扑");
			Cell portcell5 = new Cell("队列最大值");
			Cell portcell6 = new Cell("数据传输速率");

			portcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell6.setHorizontalAlignment(Element.ALIGN_CENTER);

			portcell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell6.setVerticalAlignment(Element.ALIGN_MIDDLE);

			portcell1.setBackgroundColor(Color.LIGHT_GRAY);
			portcell2.setBackgroundColor(Color.LIGHT_GRAY);
			portcell3.setBackgroundColor(Color.LIGHT_GRAY);
			portcell4.setBackgroundColor(Color.LIGHT_GRAY);
			portcell5.setBackgroundColor(Color.LIGHT_GRAY);
			portcell6.setBackgroundColor(Color.LIGHT_GRAY);

			portTable.addCell(portcell1);
			portTable.addCell(portcell2);
			portTable.addCell(portcell3);
			portTable.addCell(portcell4);
			portTable.addCell(portcell5);
			portTable.addCell(portcell6);

			for (int i = 0; i < ports.size(); i++) {
				Port port = (Port) ports.get(i);

				Cell cell6 = new Cell(SysUtil.ifNull(port.getName()));
				Cell cell7 = new Cell(SysUtil.ifNull(port.getPortID()));
				Cell cell8 = new Cell(SysUtil.ifNull(port.getBehavior()));
				Cell cell9 = new Cell(SysUtil.ifNull(port.getTopology()));
				Cell cell10 = new Cell(SysUtil.ifNull(port.getQueueFullThreshold()));
				Cell cell15 = new Cell(SysUtil.ifNull(port.getDataRate()));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);

				portTable.addCell(cell6);
				portTable.addCell(cell7);
				portTable.addCell(cell8);
				portTable.addCell(cell9);
				portTable.addCell(cell10);
				portTable.addCell(cell15);
			}
			document.add(portTable);
		}

		if (disks != null && disks.size() > 0) {
			Table aTable = new Table(7);
			int width[] = { 50, 50, 50, 130, 30, 50, 50 };
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

			Cell atitle = new Cell("磁盘信息");
			atitle.setBackgroundColor(Color.LIGHT_GRAY);
			atitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			atitle.setColspan(7);
			aTable.addCell(atitle);

			Cell cell1 = new Cell("名称");
			Cell cell11 = new Cell("状态");
			Cell cell2 = new Cell("类型");
			Cell cell3 = new Cell("容量");
			Cell cell4 = new Cell("冗余组");
			Cell cell13 = new Cell("初始化声明");
			Cell cell14 = new Cell("固件版本");

			cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell14.setHorizontalAlignment(Element.ALIGN_CENTER);

			cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);

			cell1.setBackgroundColor(Color.LIGHT_GRAY);
			cell11.setBackgroundColor(Color.LIGHT_GRAY);
			cell13.setBackgroundColor(Color.LIGHT_GRAY);
			cell14.setBackgroundColor(Color.LIGHT_GRAY);
			cell2.setBackgroundColor(Color.LIGHT_GRAY);
			cell3.setBackgroundColor(Color.LIGHT_GRAY);
			cell4.setBackgroundColor(Color.LIGHT_GRAY);
			aTable.addCell(cell1);
			aTable.addCell(cell11);
			aTable.addCell(cell2);
			aTable.addCell(cell3);
			aTable.addCell(cell4);
			aTable.addCell(cell13);
			aTable.addCell(cell14);
			for (int i = 0; i < disks.size(); i++) {
				Disk disk = (Disk) disks.get(i);
				Cell cell6 = new Cell(SysUtil.ifNull(disk.getName()));
				Cell cell7 = new Cell(SysUtil.ifNull(disk.getStatus()));
				Cell cell8 = new Cell(SysUtil.ifNull(disk.getVendorID()));
				Cell cell9 = new Cell(SysUtil.ifNull(disk.getDataCapacity()));
				Cell cell10 = new Cell(SysUtil.ifNull(disk.getRedundancyGroup()));
				Cell cell15 = new Cell(SysUtil.ifNull(disk.getInitializeState()));
				Cell cell16 = new Cell(SysUtil.ifNull(disk.getFirmwareRevision()));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell15);
				aTable.addCell(cell16);
			}
			document.add(aTable);
		}
		if (luns != null && luns.size() > 0) {
			Table lunTable = new Table(5);
			int lunwidth[] = { 50, 50, 50, 100, 200 };
			lunTable.setWidths(lunwidth);
			lunTable.setWidth(100); // 占页面宽度 90%
			lunTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
			lunTable.setAutoFillEmptyCells(true); // 自动填满
			lunTable.setBorderWidth(1); // 边框宽度
			lunTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			lunTable.setPadding(2);// 衬距，看效果就知道什么意思了
			lunTable.setSpacing(0);// 即单元格之间的间距
			lunTable.setBorder(2);// 边框
			lunTable.endHeaders();

			Cell luntitle = new Cell("逻辑单元信息");
			luntitle.setBackgroundColor(Color.LIGHT_GRAY);
			luntitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			luntitle.setColspan(5);
			lunTable.addCell(luntitle);

			Cell luncell1 = new Cell("名称");
			Cell luncell2 = new Cell("编号");
			Cell luncell3 = new Cell("行为");
			Cell luncell4 = new Cell("端口拓扑");
			Cell luncell5 = new Cell("队列最大值");

			luncell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell5.setHorizontalAlignment(Element.ALIGN_CENTER);

			luncell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

			luncell1.setBackgroundColor(Color.LIGHT_GRAY);
			luncell2.setBackgroundColor(Color.LIGHT_GRAY);
			luncell3.setBackgroundColor(Color.LIGHT_GRAY);
			luncell4.setBackgroundColor(Color.LIGHT_GRAY);
			luncell5.setBackgroundColor(Color.LIGHT_GRAY);

			lunTable.addCell(luncell1);
			lunTable.addCell(luncell2);
			lunTable.addCell(luncell3);
			lunTable.addCell(luncell4);
			lunTable.addCell(luncell5);
			for (int i = 0; i < luns.size(); i++) {
				Lun lun = (Lun) luns.get(i);

				Cell cell6 = new Cell(SysUtil.ifNull(lun.getName()));
				Cell cell7 = new Cell(SysUtil.ifNull(lun.getRedundancyGroup()));
				Cell cell8 = new Cell(SysUtil.ifNull(lun.getActive()));
				Cell cell9 = new Cell(SysUtil.ifNull(lun.getDataCapacity()));
				Cell cell10 = new Cell(SysUtil.ifNull(lun.getWwn()));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);

				lunTable.addCell(cell6);
				lunTable.addCell(cell7);
				lunTable.addCell(cell8);
				lunTable.addCell(cell9);
				lunTable.addCell(cell10);

			}
			document.add(lunTable);
		}
		if (vfps != null && vfps.size() > 0) {
			Table vfpTable = new Table(3);
			int vfpwidth[] = { 50, 50, 50 };
			vfpTable.setWidths(vfpwidth);
			vfpTable.setWidth(100); // 占页面宽度 90%
			vfpTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
			vfpTable.setAutoFillEmptyCells(true); // 自动填满
			vfpTable.setBorderWidth(1); // 边框宽度
			vfpTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			vfpTable.setPadding(2);// 衬距，看效果就知道什么意思了
			vfpTable.setSpacing(0);// 即单元格之间的间距
			vfpTable.setBorder(2);// 边框
			vfpTable.endHeaders();

			Cell vfptitle = new Cell("VFP信息");
			vfptitle.setBackgroundColor(Color.LIGHT_GRAY);
			vfptitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			vfptitle.setColspan(3);
			vfpTable.addCell(vfptitle);

			Cell vfpcell1 = new Cell("名称");
			Cell vfpcell2 = new Cell("波特率");
			Cell vfpcell3 = new Cell("分页值");

			vfpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			vfpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			vfpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);

			vfpcell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			vfpcell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			vfpcell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

			vfpcell1.setBackgroundColor(Color.LIGHT_GRAY);
			vfpcell2.setBackgroundColor(Color.LIGHT_GRAY);
			vfpcell3.setBackgroundColor(Color.LIGHT_GRAY);

			vfpTable.addCell(vfpcell1);
			vfpTable.addCell(vfpcell2);
			vfpTable.addCell(vfpcell3);
			for (int i = 0; i < vfps.size(); i++) {
				VFP vfp = (VFP) vfps.get(i);

				Cell cell6 = new Cell(SysUtil.ifNull(vfp.getName()));
				Cell cell7 = new Cell(SysUtil.ifNull(vfp.getVFPBaudRate()));
				Cell cell8 = new Cell(SysUtil.ifNull(vfp.getVFPPagingValue()));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);

				vfpTable.addCell(cell6);
				vfpTable.addCell(cell7);
				vfpTable.addCell(cell8);
			}
			document.add(vfpTable);
		}
		if (subSystemInfo != null) {
			Table substable = new Table(4);
			int subswidth[] = { 100, 100, 100, 100 };
			substable.setWidths(subswidth);
			substable.setWidth(100); // 占页面宽度 90%
			substable.setAlignment(Element.ALIGN_CENTER);// 居中显示
			substable.setAutoFillEmptyCells(true); // 自动填满
			substable.setBorderWidth(1); // 边框宽度
			substable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
			substable.setPadding(2);// 衬距，看效果就知道什么意思了
			substable.setSpacing(0);// 即单元格之间的间距
			substable.setBorder(2);// 边框
			substable.endHeaders();

			Cell substitle = new Cell("子系统信息");
			substitle.setBackgroundColor(Color.LIGHT_GRAY);
			substitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			substitle.setColspan(4);
			substable.addCell(substitle);

			Cell subscell1 = new Cell("RAID模式:");
			Cell subscell2 = new Cell("自动格式化驱动:");
			Cell subscell3 = new Cell("垂直侦测:");
			Cell subscell4 = new Cell("容量损耗临界值:");
			Cell subscell5 = new Cell("垂直侦测:");
			Cell subscell6 = new Cell("容量损耗临界值:");
			Cell subscell7 = new Cell("最大队列临界值:");
			Cell subscell8 = new Cell("优化策略:");
			Cell subscell9 = new Cell("手动覆盖:");
			Cell subscell10 = new Cell("手动覆盖目标:");
			Cell subscell11 = new Cell("闪存不能读:");
			Cell subscell12 = new Cell("重建优先级:");
			Cell subscell13 = new Cell("是否激活安全策略:");
			Cell subscell14 = new Cell("关机完成数:");
			Cell subscell15 = new Cell("子系统类型编号:");
			Cell subscell16 = new Cell("是否开启部件关注:");
			Cell subscell17 = new Cell("是否开启列划分:");
			Cell subscell18 = new Cell("闪存不能写:");
			Cell subscell19 = new Cell("写间隔:");
			Cell subscell20 = new Cell("是否开启预取:");
			Cell subscell21 = new Cell("关闭第二路径描述:");
			Cell subscell22 = new Cell("");

			subscell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell12.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell14.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell15.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell16.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell17.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell18.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell20.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell22.setHorizontalAlignment(Element.ALIGN_CENTER);

			Cell vsubscell1 = new Cell(SysUtil.ifNull(subSystemInfo.getRaidLevel()));
			Cell vsubscell2 = new Cell(SysUtil.ifNull(subSystemInfo.getAutoFormatDrive()));
			Cell vsubscell3 = new Cell(SysUtil.ifNull(subSystemInfo.getHangDetection()));
			Cell vsubscell4 = new Cell(SysUtil.ifNull(subSystemInfo.getCapacityDepletionThreshold()));
			Cell vsubscell5 = new Cell(SysUtil.ifNull(subSystemInfo.getHangDetection()));
			Cell vsubscell6 = new Cell(SysUtil.ifNull(subSystemInfo.getCapacityDepletionThreshold()));
			Cell vsubscell7 = new Cell(SysUtil.ifNull(subSystemInfo.getQueueFullThresholdMaximum()));
			Cell vsubscell8 = new Cell(SysUtil.ifNull(subSystemInfo.getEnableOptimizePolicy()));
			Cell vsubscell9 = new Cell(SysUtil.ifNull(subSystemInfo.getEnableManualOverride()));
			Cell vsubscell10 = new Cell(SysUtil.ifNull(subSystemInfo.getManualOverrideDestination()));
			Cell vsubscell11 = new Cell(SysUtil.ifNull(subSystemInfo.getReadCacheDisable()));
			Cell vsubscell12 = new Cell(SysUtil.ifNull(subSystemInfo.getRebuildPriority()));
			Cell vsubscell13 = new Cell(SysUtil.ifNull(subSystemInfo.getSecurityEnabled()));
			Cell vsubscell14 = new Cell(SysUtil.ifNull(subSystemInfo.getShutdownCompletion()));
			Cell vsubscell15 = new Cell(SysUtil.ifNull(subSystemInfo.getSubsystemTypeID()));
			Cell vsubscell16 = new Cell(SysUtil.ifNull(subSystemInfo.getUnitAttention()));
			Cell vsubscell17 = new Cell(SysUtil.ifNull(subSystemInfo.getVolumeSetPartition()));
			Cell vsubscell18 = new Cell(SysUtil.ifNull(subSystemInfo.getWriteCacheEnable()));
			Cell vsubscell19 = new Cell(SysUtil.ifNull(subSystemInfo.getWriteWorkingSetInterval()));
			Cell vsubscell20 = new Cell(SysUtil.ifNull(subSystemInfo.getEnablePrefetch()));
			Cell vsubscell21 = new Cell(SysUtil.ifNull(subSystemInfo.getDisableSecondaryPathPresentation()));
			Cell vsubscell22 = new Cell("");

			vsubscell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell12.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell14.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell15.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell16.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell17.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell18.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell19.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell20.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell22.setHorizontalAlignment(Element.ALIGN_CENTER);

			substable.addCell(subscell1);
			substable.addCell(vsubscell1);
			substable.addCell(subscell2);
			substable.addCell(vsubscell2);
			substable.addCell(subscell3);
			substable.addCell(vsubscell3);
			substable.addCell(subscell4);
			substable.addCell(vsubscell4);
			substable.addCell(subscell5);
			substable.addCell(vsubscell5);
			substable.addCell(subscell6);
			substable.addCell(vsubscell6);
			substable.addCell(subscell7);
			substable.addCell(vsubscell7);
			substable.addCell(subscell8);
			substable.addCell(vsubscell8);
			substable.addCell(subscell9);
			substable.addCell(vsubscell9);
			substable.addCell(subscell10);
			substable.addCell(vsubscell10);
			substable.addCell(subscell11);
			substable.addCell(vsubscell11);
			substable.addCell(subscell12);
			substable.addCell(vsubscell12);
			substable.addCell(subscell13);
			substable.addCell(vsubscell13);
			substable.addCell(subscell14);
			substable.addCell(vsubscell14);
			substable.addCell(subscell15);
			substable.addCell(vsubscell15);
			substable.addCell(subscell16);
			substable.addCell(vsubscell16);
			substable.addCell(subscell17);
			substable.addCell(vsubscell17);
			substable.addCell(subscell18);
			substable.addCell(vsubscell18);
			substable.addCell(subscell19);
			substable.addCell(vsubscell19);
			substable.addCell(subscell20);
			substable.addCell(vsubscell20);
			substable.addCell(subscell21);
			substable.addCell(vsubscell21);
			substable.addCell(subscell22);
			substable.addCell(vsubscell22);
			document.add(substable);
		}
		document.close();
	}

	/*
	 * HP存储综合报表生成PDF文档
	 */
	public void createHPstorageCompPdf(String file, String ipaddress) throws DocumentException, IOException {

		List<Disk> disks = null;
		ArrayInfo arrayinfo = null;
		List<Enclosure> enclosures = null;
		List<Controller> controllers = null;
		List<Port> ports = null;
		List<Lun> luns = null;
		List<VFP> vfps = null;
		SubSystemInfo subSystemInfo = null;

		Hashtable ipAllData = new Hashtable();
		// 只取当前采集的综合信息
		ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ipaddress);
		if (ipAllData != null) {
			disks = (List<Disk>) ipAllData.get("disks");
			arrayinfo = (ArrayInfo) ipAllData.get("arrayinfo");
			enclosures = (List<Enclosure>) ipAllData.get("enclosures");
			controllers = (List<Controller>) ipAllData.get("controllers");
			ports = (List<Port>) ipAllData.get("ports");
			luns = (List<Lun>) ipAllData.get("luns");
			vfps = (List<VFP>) ipAllData.get("vfps");
			subSystemInfo = (SubSystemInfo) ipAllData.get("subSystemInfo");
		}

		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// com.lowagie.text.Font titleFont = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
		// 正文字体风格
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = new Paragraph("HP存储" + ipaddress + "综合报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 11, Font.NORMAL, Color.black);

		if (arrayinfo != null) {// 阵列信息
			PdfPTable arrtable = new PdfPTable(4);
			int arrwidth[] = { 100, 100, 100, 100 };
			arrtable.setWidthPercentage(100);
			arrtable.setWidths(arrwidth);

			PdfPCell arrtitle = new PdfPCell(new Phrase("阵列信息", contextFont));
			arrtitle.setBackgroundColor(Color.LIGHT_GRAY);
			arrtitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			arrtitle.setColspan(4);

			PdfPCell arrcell1 = new PdfPCell(new Phrase("阵列状态:", contextFont));
			PdfPCell arrcell2 = new PdfPCell(new Phrase("固件版本:", contextFont));
			PdfPCell arrcell3 = new PdfPCell(new Phrase("产品版本:", contextFont));
			PdfPCell arrcell4 = new PdfPCell(new Phrase("本地控制器产品版本:", contextFont));
			PdfPCell arrcell5 = new PdfPCell(new Phrase("远程控制器产品版本:", contextFont));
			PdfPCell arrcell6 = new PdfPCell(new Phrase("", contextFont));
			PdfPCell arrcell66 = new PdfPCell(new Phrase("", contextFont));

			PdfPCell arrcell11 = new PdfPCell(new Phrase(SysUtil.ifNull(arrayinfo.getArrayStatus()), contextFont));
			PdfPCell arrcell22 = new PdfPCell(new Phrase(SysUtil.ifNull(arrayinfo.getFirmwareRevision()), contextFont));
			PdfPCell arrcell33 = new PdfPCell(new Phrase(SysUtil.ifNull(SysUtil.ifNull(arrayinfo.getProductRevision())), contextFont));
			PdfPCell arrcell44 = new PdfPCell(new Phrase(SysUtil.ifNull(SysUtil.ifNull(arrayinfo.getLocalControllerProductRevision())), contextFont));
			PdfPCell arrcell55 = new PdfPCell(new Phrase(SysUtil.ifNull(arrayinfo.getRemoteControllerProductRevision()), contextFont));

			arrcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell5.setHorizontalAlignment(Element.ALIGN_CENTER);

			arrcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell22.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell33.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell44.setHorizontalAlignment(Element.ALIGN_CENTER);
			arrcell55.setHorizontalAlignment(Element.ALIGN_CENTER);

			arrtable.addCell(arrtitle);
			arrtable.addCell(arrcell1);
			arrtable.addCell(arrcell11);
			arrtable.addCell(arrcell2);
			arrtable.addCell(arrcell22);
			arrtable.addCell(arrcell3);
			arrtable.addCell(arrcell33);
			arrtable.addCell(arrcell4);
			arrtable.addCell(arrcell44);
			arrtable.addCell(arrcell5);
			arrtable.addCell(arrcell55);
			arrtable.addCell(arrcell6);
			arrtable.addCell(arrcell66);
			document.add(arrtable);
		}

		if (enclosures != null && enclosures.size() > 0) {// 附件信息
			for (int i = 0; i < enclosures.size(); i++) {
				Enclosure encl = enclosures.get(i);

				PdfPTable encltable = new PdfPTable(4);
				int enclwidth[] = { 100, 100, 100, 100 };
				encltable.setWidthPercentage(100);
				encltable.setWidths(enclwidth);

				PdfPCell encltitle = new PdfPCell(new Phrase("附件信息", contextFont));
				encltitle.setBackgroundColor(Color.LIGHT_GRAY);
				encltitle.setHorizontalAlignment(Element.ALIGN_LEFT);
				encltitle.setColspan(4);

				PdfPCell enclcell1 = new PdfPCell(new Phrase("附件名称:", contextFont));
				PdfPCell enclcell2 = new PdfPCell(new Phrase("附件编号:", contextFont));
				PdfPCell enclcell3 = new PdfPCell(new Phrase("附件状态:", contextFont));
				PdfPCell enclcell4 = new PdfPCell(new Phrase("附件类型:", contextFont));
				PdfPCell enclcell5 = new PdfPCell(new Phrase("附件全局名称:", contextFont));
				PdfPCell enclcell6 = new PdfPCell(new Phrase("", contextFont));
				PdfPCell enclcell66 = new PdfPCell(new Phrase("", contextFont));

				PdfPCell enclcell11 = new PdfPCell(new Phrase(SysUtil.ifNull(encl.getName()), contextFont));
				PdfPCell enclcell22 = new PdfPCell(new Phrase(SysUtil.ifNull(encl.getEnclosureID()), contextFont));
				PdfPCell enclcell33 = new PdfPCell(new Phrase(SysUtil.ifNull(encl.getEnclosureStatus()), contextFont));
				PdfPCell enclcell44 = new PdfPCell(new Phrase(SysUtil.ifNull(encl.getEnclosureType()), contextFont));
				PdfPCell enclcell55 = new PdfPCell(new Phrase(SysUtil.ifNull(encl.getNodeWWN()), contextFont));

				enclcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell5.setHorizontalAlignment(Element.ALIGN_CENTER);

				enclcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell33.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell44.setHorizontalAlignment(Element.ALIGN_CENTER);
				enclcell55.setHorizontalAlignment(Element.ALIGN_CENTER);

				encltable.addCell(encltitle);
				encltable.addCell(enclcell1);
				encltable.addCell(enclcell11);
				encltable.addCell(enclcell2);
				encltable.addCell(enclcell22);
				encltable.addCell(enclcell3);
				encltable.addCell(enclcell33);
				encltable.addCell(enclcell4);
				encltable.addCell(enclcell44);
				encltable.addCell(enclcell5);
				encltable.addCell(enclcell55);
				encltable.addCell(enclcell6);
				encltable.addCell(enclcell66);
				document.add(encltable);
				if (encl.getFrus() != null && encl.getFrus().size() > 0) {
					PdfPTable enclfrutable = new PdfPTable(4);
					int enclfruwidth[] = { 100, 100, 100, 100 };
					enclfrutable.setWidthPercentage(100);
					enclfrutable.setWidths(enclfruwidth);

					PdfPCell enclfrutitle = new PdfPCell(new Phrase("FRU信息", contextFont));
					enclfrutitle.setBackgroundColor(Color.LIGHT_GRAY);
					enclfrutitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					enclfrutitle.setColspan(4);
					enclfrutable.addCell(enclfrutitle);

					PdfPCell enclfrucell1 = new PdfPCell(new Phrase("名称:", contextFont));
					PdfPCell enclfrucell2 = new PdfPCell(new Phrase("组件:", contextFont));
					PdfPCell enclfrucell3 = new PdfPCell(new Phrase("标识:", contextFont));
					PdfPCell enclfrucell4 = new PdfPCell(new Phrase("状态:", contextFont));

					enclfrucell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					enclfrucell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					enclfrucell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					enclfrucell4.setHorizontalAlignment(Element.ALIGN_CENTER);

					enclfrutable.addCell(enclfrucell1);
					enclfrutable.addCell(enclfrucell2);
					enclfrutable.addCell(enclfrucell3);
					enclfrutable.addCell(enclfrucell4);

					for (int j = 0; j < encl.getFrus().size(); j++) {
						EnclosureFru fru = encl.getFrus().get(j);

						PdfPCell enclfrucell11 = new PdfPCell(new Phrase(SysUtil.ifNull(fru.getFru()), contextFont));
						PdfPCell enclfrucell22 = new PdfPCell(new Phrase(SysUtil.ifNull(fru.getHwComponent()), contextFont));
						PdfPCell enclfrucell33 = new PdfPCell(new Phrase(SysUtil.ifNull(fru.getIdentification()), contextFont));
						PdfPCell enclfrucell44 = new PdfPCell(new Phrase(SysUtil.ifNull(fru.getIdStatus()), contextFont));

						enclfrucell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						enclfrucell22.setHorizontalAlignment(Element.ALIGN_CENTER);
						enclfrucell33.setHorizontalAlignment(Element.ALIGN_CENTER);
						enclfrucell44.setHorizontalAlignment(Element.ALIGN_CENTER);

						enclfrutable.addCell(enclfrucell11);
						enclfrutable.addCell(enclfrucell22);
						enclfrutable.addCell(enclfrucell33);
						enclfrutable.addCell(enclfrucell44);
					}
					document.add(enclfrutable);
				}

			}

		}

		if (controllers != null) {// 控制器信息
			for (int i = 0; i < controllers.size(); i++) {
				Controller cont = controllers.get(i);

				PdfPTable conttable = new PdfPTable(4);
				int enclwidth[] = { 100, 100, 100, 100 };
				conttable.setWidthPercentage(100);
				conttable.setWidths(enclwidth);

				PdfPCell conttitle = new PdfPCell(new Phrase("控制器信息", contextFont));
				conttitle.setBackgroundColor(Color.LIGHT_GRAY);
				conttitle.setHorizontalAlignment(Element.ALIGN_LEFT);
				conttitle.setColspan(4);

				PdfPCell contcell1 = new PdfPCell(new Phrase("名称:", contextFont));
				PdfPCell contcell2 = new PdfPCell(new Phrase("状态:", contextFont));
				PdfPCell contcell3 = new PdfPCell(new Phrase("序列号:", contextFont));
				PdfPCell contcell4 = new PdfPCell(new Phrase("供货商编号:", contextFont));
				PdfPCell contcell5 = new PdfPCell(new Phrase("产品编号:", contextFont));
				PdfPCell contcell6 = new PdfPCell(new Phrase("产品版本:", contextFont));
				PdfPCell contcell7 = new PdfPCell(new Phrase("固件版本:", contextFont));
				PdfPCell contcell8 = new PdfPCell(new Phrase("制造商产品编码:", contextFont));
				PdfPCell contcell9 = new PdfPCell(new Phrase("控制类型;", contextFont));
				PdfPCell contcell10 = new PdfPCell(new Phrase("充电电池固件版本:", contextFont));
				PdfPCell contcell11 = new PdfPCell(new Phrase("附件交换设置:", contextFont));
				PdfPCell contcell12 = new PdfPCell(new Phrase("附件编号:", contextFont));
				PdfPCell contcell13 = new PdfPCell(new Phrase("连环对:", contextFont));
				PdfPCell contcell14 = new PdfPCell(new Phrase("连环编号:", contextFont));
				PdfPCell contcell15 = new PdfPCell(new Phrase("基础驱动地址:", contextFont));
				PdfPCell contcell16 = new PdfPCell(new Phrase("牢固地址:", contextFont));

				contcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell12.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell13.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell14.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell16.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell vcontcell1 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getName()), contextFont));
				PdfPCell contcell22 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getStatus()), contextFont));
				PdfPCell contcell33 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getSerialNumber()), contextFont));
				PdfPCell contcell44 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getVendorID()), contextFont));
				PdfPCell contcell55 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getProductID()), contextFont));
				PdfPCell contcell66 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getProductRevision()), contextFont));
				PdfPCell contcell77 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getFirmwareRevision()), contextFont));
				PdfPCell contcell88 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getManufacturingProductCode()), contextFont));
				PdfPCell contcell99 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getControllerType()), contextFont));
				PdfPCell contcell100 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getBatteryChargerFirmwareRevision()), contextFont));
				PdfPCell contcell111 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getEnclosureSwitchSetting()), contextFont));
				PdfPCell contcell122 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getEnclosureID()), contextFont));
				PdfPCell contcell133 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getLoopPair()), contextFont));
				PdfPCell contcell144 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getLoopID()), contextFont));
				PdfPCell contcell155 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getDriveAddressBasis()), contextFont));
				PdfPCell contcell166 = new PdfPCell(new Phrase(SysUtil.ifNull(cont.getHardAddress()), contextFont));

				vcontcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell33.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell44.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell55.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell66.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell77.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell88.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell99.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell100.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell111.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell122.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell133.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell144.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell155.setHorizontalAlignment(Element.ALIGN_CENTER);
				contcell166.setHorizontalAlignment(Element.ALIGN_CENTER);

				conttable.addCell(conttitle);
				conttable.addCell(contcell1);
				conttable.addCell(vcontcell1);
				conttable.addCell(contcell2);
				conttable.addCell(contcell22);
				conttable.addCell(contcell3);
				conttable.addCell(contcell33);
				conttable.addCell(contcell4);
				conttable.addCell(contcell44);
				conttable.addCell(contcell5);
				conttable.addCell(contcell55);
				conttable.addCell(contcell6);
				conttable.addCell(contcell66);
				conttable.addCell(contcell7);
				conttable.addCell(contcell77);
				conttable.addCell(contcell8);
				conttable.addCell(contcell88);
				conttable.addCell(contcell9);
				conttable.addCell(contcell99);
				conttable.addCell(contcell10);
				conttable.addCell(contcell100);
				conttable.addCell(contcell11);
				conttable.addCell(contcell111);
				conttable.addCell(contcell12);
				conttable.addCell(contcell122);
				conttable.addCell(contcell13);
				conttable.addCell(contcell133);
				conttable.addCell(contcell14);
				conttable.addCell(contcell144);
				conttable.addCell(contcell15);
				conttable.addCell(contcell155);
				conttable.addCell(contcell16);
				conttable.addCell(contcell166);
				document.add(conttable);

				if (cont.getFrontPortList() != null && cont.getFrontPortList().size() > 0) {
					PdfPTable fptable = new PdfPTable(4);
					int fpwidth[] = { 100, 100, 100, 100 };
					fptable.setWidthPercentage(100);
					fptable.setWidths(fpwidth);

					PdfPCell fptitle = new PdfPCell(new Phrase("前端口列表", contextFont));
					fptitle.setBackgroundColor(Color.LIGHT_GRAY);
					fptitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					fptitle.setColspan(4);
					fptable.addCell(fptitle);

					for (int j = 0; j < cont.getFrontPortList().size(); j++) {
						CtrlPort fctp = cont.getFrontPortList().get(j);
						PdfPCell fpcell1 = new PdfPCell(new Phrase("名称:", contextFont));
						PdfPCell fpcell2 = new PdfPCell(new Phrase("状态:", contextFont));
						PdfPCell fpcell3 = new PdfPCell(new Phrase("端口实例:", contextFont));
						PdfPCell fpcell4 = new PdfPCell(new Phrase("物理地址:", contextFont));
						PdfPCell fpcell5 = new PdfPCell(new Phrase("链接声明:", contextFont));
						PdfPCell fpcell6 = new PdfPCell(new Phrase("节点全局名称:", contextFont));
						PdfPCell fpcell7 = new PdfPCell(new Phrase("端口全局名称:", contextFont));
						PdfPCell fpcell8 = new PdfPCell(new Phrase("拓扑:", contextFont));
						PdfPCell fpcell9 = new PdfPCell(new Phrase("传输速率:", contextFont));
						PdfPCell fpcell10 = new PdfPCell(new Phrase("端口编号:", contextFont));
						PdfPCell fpcell11 = new PdfPCell(new Phrase("设备名称:", contextFont));
						PdfPCell fpcell12 = new PdfPCell(new Phrase("设备路径:", contextFont));

						fpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						PdfPCell vfpcell1 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getName()), contextFont));
						PdfPCell vfpcell2 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getStatus()), contextFont));
						PdfPCell vfpcell3 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getPortInstance()), contextFont));
						PdfPCell vfpcell4 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getHardAddress()), contextFont));
						PdfPCell vfpcell5 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getLinkState()), contextFont));
						PdfPCell vfpcell6 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getNodeWWN()), contextFont));
						PdfPCell vfpcell7 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getPortWWN()), contextFont));
						PdfPCell vfpcell8 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getTopology()), contextFont));
						PdfPCell vfpcell9 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getDataRate()), contextFont));
						PdfPCell vfpcell10 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getPortID()), contextFont));
						PdfPCell vfpcell11 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getDeviceHostName()), contextFont));
						PdfPCell vfpcell12 = new PdfPCell(new Phrase(SysUtil.ifNull(fctp.getDevicePath()), contextFont));

						vfpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						fptable.addCell(fpcell1);
						fptable.addCell(vfpcell1);
						fptable.addCell(fpcell2);
						fptable.addCell(vfpcell2);
						fptable.addCell(fpcell3);
						fptable.addCell(vfpcell3);
						fptable.addCell(fpcell4);
						fptable.addCell(vfpcell4);
						fptable.addCell(fpcell5);
						fptable.addCell(vfpcell5);
						fptable.addCell(fpcell6);
						fptable.addCell(vfpcell6);
						fptable.addCell(fpcell7);
						fptable.addCell(vfpcell7);
						fptable.addCell(fpcell8);
						fptable.addCell(vfpcell8);
						fptable.addCell(fpcell9);
						fptable.addCell(vfpcell9);
						fptable.addCell(fpcell10);
						fptable.addCell(vfpcell10);
						fptable.addCell(fpcell11);
						fptable.addCell(vfpcell11);
						fptable.addCell(fpcell12);
						fptable.addCell(vfpcell12);
					}
					document.add(fptable);
				}
				if (cont.getBackPortList() != null && cont.getBackPortList().size() > 0) {
					PdfPTable bptable = new PdfPTable(4);
					int bpwidth[] = { 100, 100, 100, 100 };
					bptable.setWidthPercentage(100);
					bptable.setWidths(bpwidth);

					PdfPCell bptitle = new PdfPCell(new Phrase("后端口列表", contextFont));
					bptitle.setBackgroundColor(Color.LIGHT_GRAY);
					bptitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					bptitle.setColspan(4);
					bptable.addCell(bptitle);

					for (int j = 0; j < cont.getBackPortList().size(); j++) {
						CtrlPort bctp = cont.getBackPortList().get(j);
						PdfPCell fpcell1 = new PdfPCell(new Phrase("名称:", contextFont));
						PdfPCell fpcell2 = new PdfPCell(new Phrase("状态:", contextFont));
						PdfPCell fpcell3 = new PdfPCell(new Phrase("端口实例:", contextFont));
						PdfPCell fpcell4 = new PdfPCell(new Phrase("物理地址:", contextFont));
						PdfPCell fpcell5 = new PdfPCell(new Phrase("链接声明:", contextFont));
						PdfPCell fpcell6 = new PdfPCell(new Phrase("节点全局名称:", contextFont));
						PdfPCell fpcell7 = new PdfPCell(new Phrase("端口全局名称:", contextFont));
						PdfPCell fpcell8 = new PdfPCell(new Phrase("拓扑:", contextFont));
						PdfPCell fpcell9 = new PdfPCell(new Phrase("传输速率:", contextFont));
						PdfPCell fpcell10 = new PdfPCell(new Phrase("端口编号:", contextFont));
						PdfPCell fpcell11 = new PdfPCell(new Phrase("设备名称:", contextFont));
						PdfPCell fpcell12 = new PdfPCell(new Phrase("设备路径:", contextFont));

						fpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						fpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						PdfPCell vfpcell1 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getName()), contextFont));
						PdfPCell vfpcell2 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getStatus()), contextFont));
						PdfPCell vfpcell3 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getPortInstance()), contextFont));
						PdfPCell vfpcell4 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getHardAddress()), contextFont));
						PdfPCell vfpcell5 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getLinkState()), contextFont));
						PdfPCell vfpcell6 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getNodeWWN()), contextFont));
						PdfPCell vfpcell7 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getPortWWN()), contextFont));
						PdfPCell vfpcell8 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getTopology()), contextFont));
						PdfPCell vfpcell9 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getDataRate()), contextFont));
						PdfPCell vfpcell10 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getPortID()), contextFont));
						PdfPCell vfpcell11 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getDeviceHostName()), contextFont));
						PdfPCell vfpcell12 = new PdfPCell(new Phrase(SysUtil.ifNull(bctp.getDevicePath()), contextFont));

						vfpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell10.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell11.setHorizontalAlignment(Element.ALIGN_CENTER);
						vfpcell12.setHorizontalAlignment(Element.ALIGN_CENTER);

						bptable.addCell(fpcell1);
						bptable.addCell(vfpcell1);
						bptable.addCell(fpcell2);
						bptable.addCell(vfpcell2);
						bptable.addCell(fpcell3);
						bptable.addCell(vfpcell3);
						bptable.addCell(fpcell4);
						bptable.addCell(vfpcell4);
						bptable.addCell(fpcell5);
						bptable.addCell(vfpcell5);
						bptable.addCell(fpcell6);
						bptable.addCell(vfpcell6);
						bptable.addCell(fpcell7);
						bptable.addCell(vfpcell7);
						bptable.addCell(fpcell8);
						bptable.addCell(vfpcell8);
						bptable.addCell(fpcell9);
						bptable.addCell(vfpcell9);
						bptable.addCell(fpcell10);
						bptable.addCell(vfpcell10);
						bptable.addCell(fpcell11);
						bptable.addCell(vfpcell11);
						bptable.addCell(fpcell12);
						bptable.addCell(vfpcell12);
					}
					document.add(bptable);
				}

				if (cont.getDimmList() != null && cont.getDimmList().size() > 0) {
					PdfPTable dimtable = new PdfPTable(4);
					int dimwidth[] = { 100, 100, 100, 100 };
					dimtable.setWidthPercentage(100);
					dimtable.setWidths(dimwidth);

					PdfPCell dimtitle = new PdfPCell(new Phrase("双列直插式存储模块列表", contextFont));
					dimtitle.setBackgroundColor(Color.LIGHT_GRAY);
					dimtitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					dimtitle.setColspan(4);
					dimtable.addCell(dimtitle);

					for (int j = 0; j < cont.getDimmList().size(); j++) {
						DIMM dimm = cont.getDimmList().get(j);
						PdfPCell dimcell1 = new PdfPCell(new Phrase("名称:", contextFont));
						PdfPCell dimcell2 = new PdfPCell(new Phrase("状态:", contextFont));
						PdfPCell dimcell3 = new PdfPCell(new Phrase("容量:", contextFont));
						PdfPCell dimcell4 = new PdfPCell(new Phrase("", contextFont));

						dimcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dimcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						dimcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						dimcell4.setHorizontalAlignment(Element.ALIGN_CENTER);

						PdfPCell vdimcell1 = new PdfPCell(new Phrase(SysUtil.ifNull(dimm.getName()), contextFont));
						PdfPCell vdimcell2 = new PdfPCell(new Phrase(SysUtil.ifNull(dimm.getStatus()), contextFont));
						PdfPCell vdimcell3 = new PdfPCell(new Phrase(SysUtil.ifNull(dimm.getCapacity()), contextFont));
						PdfPCell vdimcell4 = new PdfPCell(new Phrase("", contextFont));

						vdimcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						vdimcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						vdimcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
						vdimcell4.setHorizontalAlignment(Element.ALIGN_CENTER);

						dimtable.addCell(dimcell1);
						dimtable.addCell(vdimcell1);
						dimtable.addCell(dimcell2);
						dimtable.addCell(vdimcell2);
						dimtable.addCell(dimcell3);
						dimtable.addCell(vdimcell3);
						dimtable.addCell(dimcell4);
						dimtable.addCell(vdimcell4);
					}
					document.add(dimtable);
				}
				if (cont.getBattery() != null) {
					Battery battery = cont.getBattery();
					PdfPTable battable = new PdfPTable(4);
					int batwidth[] = { 100, 100, 100, 100 };
					battable.setWidthPercentage(100);
					battable.setWidths(batwidth);

					PdfPCell battitle = new PdfPCell(new Phrase("电池信息", contextFont));
					battitle.setBackgroundColor(Color.LIGHT_GRAY);
					battitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					battitle.setColspan(4);
					battable.addCell(battitle);

					PdfPCell batcell1 = new PdfPCell(new Phrase("名称:", contextFont));
					PdfPCell batcell2 = new PdfPCell(new Phrase("状态:", contextFont));
					PdfPCell batcell3 = new PdfPCell(new Phrase("标识:", contextFont));
					PdfPCell batcell4 = new PdfPCell(new Phrase("厂商:", contextFont));
					PdfPCell batcell5 = new PdfPCell(new Phrase("设备名称:", contextFont));
					PdfPCell batcell6 = new PdfPCell(new Phrase("生产日期:", contextFont));
					PdfPCell batcell7 = new PdfPCell(new Phrase("剩余电量:", contextFont));
					PdfPCell batcell8 = new PdfPCell(new Phrase("剩余电量百分比:", contextFont));
					PdfPCell batcell9 = new PdfPCell(new Phrase("电压:", contextFont));
					PdfPCell batcell10 = new PdfPCell(new Phrase("放电周期:", contextFont));

					batcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					batcell10.setHorizontalAlignment(Element.ALIGN_CENTER);

					PdfPCell vbatcell1 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getName()), contextFont));
					PdfPCell vbatcell2 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getStatus()), contextFont));
					PdfPCell vbatcell3 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getIdentification()), contextFont));
					PdfPCell vbatcell4 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getManufacturerName()), contextFont));
					PdfPCell vbatcell5 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getDeviceName()), contextFont));
					PdfPCell vbatcell6 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getManufacturerDate()), contextFont));
					PdfPCell vbatcell7 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getRemainingCapacity()), contextFont));
					PdfPCell vbatcell8 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getPctRemainingCapacity()), contextFont));
					PdfPCell vbatcell9 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getVoltage()), contextFont));
					PdfPCell vbatcell10 = new PdfPCell(new Phrase(SysUtil.ifNull(battery.getDischargeCycles()), contextFont));

					vbatcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					vbatcell10.setHorizontalAlignment(Element.ALIGN_CENTER);

					battable.addCell(batcell1);
					battable.addCell(vbatcell1);
					battable.addCell(batcell2);
					battable.addCell(vbatcell2);
					battable.addCell(batcell3);
					battable.addCell(vbatcell3);
					battable.addCell(batcell4);
					battable.addCell(vbatcell4);
					battable.addCell(batcell5);
					battable.addCell(vbatcell5);
					battable.addCell(batcell6);
					battable.addCell(vbatcell6);
					battable.addCell(batcell7);
					battable.addCell(vbatcell7);
					battable.addCell(batcell8);
					battable.addCell(vbatcell8);
					battable.addCell(batcell9);
					battable.addCell(vbatcell9);
					battable.addCell(batcell10);
					battable.addCell(vbatcell10);

					document.add(battable);
				}
				if (cont.getProcessor() != null) {
					Processor processor = cont.getProcessor();
					PdfPTable protable = new PdfPTable(4);
					int prowidth[] = { 100, 100, 100, 100 };
					protable.setWidthPercentage(100);
					protable.setWidths(prowidth);

					PdfPCell protitle = new PdfPCell(new Phrase("处理器信息", contextFont));
					protitle.setBackgroundColor(Color.LIGHT_GRAY);
					protitle.setHorizontalAlignment(Element.ALIGN_LEFT);
					protitle.setColspan(4);
					protable.addCell(protitle);

					PdfPCell procell1 = new PdfPCell(new Phrase("名称:", contextFont));
					PdfPCell procell2 = new PdfPCell(new Phrase("状态:", contextFont));
					PdfPCell procell3 = new PdfPCell(new Phrase("标识:", contextFont));
					PdfPCell procell4 = new PdfPCell(new Phrase("", contextFont));

					procell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					procell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					procell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					procell4.setHorizontalAlignment(Element.ALIGN_CENTER);

					PdfPCell vprocell1 = new PdfPCell(new Phrase(SysUtil.ifNull(processor.getName()), contextFont));
					PdfPCell vprocell2 = new PdfPCell(new Phrase(SysUtil.ifNull(processor.getStatus()), contextFont));
					PdfPCell vprocell3 = new PdfPCell(new Phrase(SysUtil.ifNull(processor.getIdentification()), contextFont));
					PdfPCell vprocell4 = new PdfPCell(new Phrase("", contextFont));

					vprocell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					vprocell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					vprocell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					vprocell4.setHorizontalAlignment(Element.ALIGN_CENTER);

					protable.addCell(procell1);
					protable.addCell(vprocell1);
					protable.addCell(procell2);
					protable.addCell(vprocell2);
					protable.addCell(procell3);
					protable.addCell(vprocell3);
					protable.addCell(procell4);
					protable.addCell(vprocell4);
					document.add(protable);
				}

			}
		}

		if (ports != null && ports.size() > 0) {
			PdfPTable portTable = new PdfPTable(6);
			int portwidth[] = { 50, 50, 50, 100, 50, 150 };
			portTable.setWidthPercentage(100);
			portTable.setWidths(portwidth);

			PdfPCell porttitle = new PdfPCell(new Phrase("端口信息", contextFont));
			porttitle.setBackgroundColor(Color.LIGHT_GRAY);
			porttitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			porttitle.setColspan(6);
			portTable.addCell(porttitle);

			PdfPCell portcell1 = new PdfPCell(new Phrase("名称", contextFont));
			PdfPCell portcell2 = new PdfPCell(new Phrase("编号", contextFont));
			PdfPCell portcell3 = new PdfPCell(new Phrase("行为", contextFont));
			PdfPCell portcell4 = new PdfPCell(new Phrase("端口拓扑", contextFont));
			PdfPCell portcell5 = new PdfPCell(new Phrase("队列最大值", contextFont));
			PdfPCell portcell6 = new PdfPCell(new Phrase("数据传输速率", contextFont));

			portcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			portcell6.setHorizontalAlignment(Element.ALIGN_CENTER);

			portcell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
			portcell6.setVerticalAlignment(Element.ALIGN_MIDDLE);

			portcell1.setBackgroundColor(Color.LIGHT_GRAY);
			portcell2.setBackgroundColor(Color.LIGHT_GRAY);
			portcell3.setBackgroundColor(Color.LIGHT_GRAY);
			portcell4.setBackgroundColor(Color.LIGHT_GRAY);
			portcell5.setBackgroundColor(Color.LIGHT_GRAY);
			portcell6.setBackgroundColor(Color.LIGHT_GRAY);

			portTable.addCell(portcell1);
			portTable.addCell(portcell2);
			portTable.addCell(portcell3);
			portTable.addCell(portcell4);
			portTable.addCell(portcell5);
			portTable.addCell(portcell6);

			for (int i = 0; i < ports.size(); i++) {
				Port port = (Port) ports.get(i);

				PdfPCell cell6 = new PdfPCell(new Phrase(SysUtil.ifNull(port.getName()), contextFont));
				PdfPCell cell7 = new PdfPCell(new Phrase(SysUtil.ifNull(port.getPortID()), contextFont));
				PdfPCell cell8 = new PdfPCell(new Phrase(SysUtil.ifNull(port.getBehavior()), contextFont));
				PdfPCell cell9 = new PdfPCell(new Phrase(SysUtil.ifNull(port.getTopology()), contextFont));
				PdfPCell cell10 = new PdfPCell(new Phrase(SysUtil.ifNull(port.getQueueFullThreshold()), contextFont));
				PdfPCell cell15 = new PdfPCell(new Phrase(SysUtil.ifNull(port.getDataRate()), contextFont));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);

				portTable.addCell(cell6);
				portTable.addCell(cell7);
				portTable.addCell(cell8);
				portTable.addCell(cell9);
				portTable.addCell(cell10);
				portTable.addCell(cell15);
			}
			document.add(portTable);
		}

		if (disks != null && disks.size() > 0) {
			PdfPTable aTable = new PdfPTable(7);
			int width[] = { 50, 50, 50, 130, 30, 50, 50 };
			aTable.setWidthPercentage(100);
			aTable.setWidths(width);

			PdfPCell atitle = new PdfPCell(new Phrase("磁盘信息", contextFont));
			atitle.setBackgroundColor(Color.LIGHT_GRAY);
			atitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			atitle.setColspan(7);
			aTable.addCell(atitle);

			PdfPCell cell1 = new PdfPCell(new Phrase("名称", contextFont));
			PdfPCell cell11 = new PdfPCell(new Phrase("状态", contextFont));
			PdfPCell cell2 = new PdfPCell(new Phrase("类型", contextFont));
			PdfPCell cell3 = new PdfPCell(new Phrase("容量", contextFont));
			PdfPCell cell4 = new PdfPCell(new Phrase("冗余组", contextFont));
			PdfPCell cell15 = new PdfPCell(new Phrase("初始化声明", contextFont));
			PdfPCell cell16 = new PdfPCell(new Phrase("固件版本", contextFont));

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

			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);

			aTable.addCell(cell1);
			aTable.addCell(cell11);
			aTable.addCell(cell2);
			aTable.addCell(cell3);
			aTable.addCell(cell4);
			aTable.addCell(cell15);
			aTable.addCell(cell16);

			for (int i = 0; i < disks.size(); i++) {
				Disk disk = (Disk) disks.get(i);

				PdfPCell cell6 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getName()), contextFont));
				PdfPCell cell7 = new PdfPCell(new Paragraph(SysUtil.ifNull(disk.getStatus()), contextFont));
				PdfPCell cell8 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getVendorID()), contextFont));
				PdfPCell cell9 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getDataCapacity()), contextFont));
				PdfPCell cell10 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getRedundancyGroup()), contextFont));
				PdfPCell cell17 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getInitializeState()), contextFont));
				PdfPCell cell18 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getFirmwareRevision()), contextFont));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
			}
			document.add(aTable);
		}
		if (luns != null && luns.size() > 0) {
			PdfPTable lunTable = new PdfPTable(5);
			int lunwidth[] = { 50, 50, 50, 100, 200 };
			lunTable.setWidthPercentage(100);
			lunTable.setWidths(lunwidth);

			PdfPCell luntitle = new PdfPCell(new Phrase("逻辑单元信息", contextFont));
			luntitle.setBackgroundColor(Color.LIGHT_GRAY);
			luntitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			luntitle.setColspan(5);
			lunTable.addCell(luntitle);

			PdfPCell luncell1 = new PdfPCell(new Phrase("名称", contextFont));
			PdfPCell luncell2 = new PdfPCell(new Phrase("编号", contextFont));
			PdfPCell luncell3 = new PdfPCell(new Phrase("行为", contextFont));
			PdfPCell luncell4 = new PdfPCell(new Phrase("端口拓扑", contextFont));
			PdfPCell luncell5 = new PdfPCell(new Phrase("队列最大值", contextFont));

			luncell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			luncell5.setHorizontalAlignment(Element.ALIGN_CENTER);

			luncell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			luncell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

			luncell1.setBackgroundColor(Color.LIGHT_GRAY);
			luncell2.setBackgroundColor(Color.LIGHT_GRAY);
			luncell3.setBackgroundColor(Color.LIGHT_GRAY);
			luncell4.setBackgroundColor(Color.LIGHT_GRAY);
			luncell5.setBackgroundColor(Color.LIGHT_GRAY);

			lunTable.addCell(luncell1);
			lunTable.addCell(luncell2);
			lunTable.addCell(luncell3);
			lunTable.addCell(luncell4);
			lunTable.addCell(luncell5);
			for (int i = 0; i < luns.size(); i++) {
				Lun lun = (Lun) luns.get(i);

				PdfPCell cell6 = new PdfPCell(new Phrase(SysUtil.ifNull(lun.getName()), contextFont));
				PdfPCell cell7 = new PdfPCell(new Phrase(SysUtil.ifNull(lun.getRedundancyGroup()), contextFont));
				PdfPCell cell8 = new PdfPCell(new Phrase(SysUtil.ifNull(lun.getActive()), contextFont));
				PdfPCell cell9 = new PdfPCell(new Phrase(SysUtil.ifNull(lun.getDataCapacity()), contextFont));
				PdfPCell cell10 = new PdfPCell(new Phrase(SysUtil.ifNull(lun.getWwn()), contextFont));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);

				lunTable.addCell(cell6);
				lunTable.addCell(cell7);
				lunTable.addCell(cell8);
				lunTable.addCell(cell9);
				lunTable.addCell(cell10);

			}
			document.add(lunTable);
		}
		if (vfps != null && vfps.size() > 0) {
			PdfPTable vfpTable = new PdfPTable(3);
			int vfpwidth[] = { 50, 50, 50 };
			vfpTable.setWidthPercentage(100);
			vfpTable.setWidths(vfpwidth);

			PdfPCell vfptitle = new PdfPCell(new Phrase("VFP信息", contextFont));
			vfptitle.setBackgroundColor(Color.LIGHT_GRAY);
			vfptitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			vfptitle.setColspan(3);
			vfpTable.addCell(vfptitle);

			PdfPCell vfpcell1 = new PdfPCell(new Phrase("名称", contextFont));
			PdfPCell vfpcell2 = new PdfPCell(new Phrase("波特率", contextFont));
			PdfPCell vfpcell3 = new PdfPCell(new Phrase("分页值", contextFont));

			vfpcell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			vfpcell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			vfpcell3.setHorizontalAlignment(Element.ALIGN_CENTER);

			vfpcell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			vfpcell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			vfpcell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

			vfpcell1.setBackgroundColor(Color.LIGHT_GRAY);
			vfpcell2.setBackgroundColor(Color.LIGHT_GRAY);
			vfpcell3.setBackgroundColor(Color.LIGHT_GRAY);

			vfpTable.addCell(vfpcell1);
			vfpTable.addCell(vfpcell2);
			vfpTable.addCell(vfpcell3);
			for (int i = 0; i < vfps.size(); i++) {
				VFP vfp = (VFP) vfps.get(i);

				PdfPCell cell6 = new PdfPCell(new Phrase(SysUtil.ifNull(vfp.getName()), contextFont));
				PdfPCell cell7 = new PdfPCell(new Phrase(SysUtil.ifNull(vfp.getVFPBaudRate()), contextFont));
				PdfPCell cell8 = new PdfPCell(new Phrase(SysUtil.ifNull(vfp.getVFPPagingValue()), contextFont));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);

				vfpTable.addCell(cell6);
				vfpTable.addCell(cell7);
				vfpTable.addCell(cell8);
			}
			document.add(vfpTable);
		}
		if (subSystemInfo != null) {
			PdfPTable substable = new PdfPTable(4);
			int subswidth[] = { 100, 100, 100, 100 };
			substable.setWidthPercentage(100);
			substable.setWidths(subswidth);

			PdfPCell substitle = new PdfPCell(new Phrase("子系统信息", contextFont));
			substitle.setBackgroundColor(Color.LIGHT_GRAY);
			substitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			substitle.setColspan(4);
			substable.addCell(substitle);

			PdfPCell subscell1 = new PdfPCell(new Phrase("RAID模式:", contextFont));
			PdfPCell subscell2 = new PdfPCell(new Phrase("自动格式化驱动:", contextFont));
			PdfPCell subscell3 = new PdfPCell(new Phrase("垂直侦测:", contextFont));
			PdfPCell subscell4 = new PdfPCell(new Phrase("容量损耗临界值:", contextFont));
			PdfPCell subscell5 = new PdfPCell(new Phrase("垂直侦测:", contextFont));
			PdfPCell subscell6 = new PdfPCell(new Phrase("容量损耗临界值:", contextFont));
			PdfPCell subscell7 = new PdfPCell(new Phrase("最大队列临界值:", contextFont));
			PdfPCell subscell8 = new PdfPCell(new Phrase("优化策略:", contextFont));
			PdfPCell subscell9 = new PdfPCell(new Phrase("手动覆盖:", contextFont));
			PdfPCell subscell10 = new PdfPCell(new Phrase("手动覆盖目标:", contextFont));
			PdfPCell subscell11 = new PdfPCell(new Phrase("闪存不能读:", contextFont));
			PdfPCell subscell12 = new PdfPCell(new Phrase("重建优先级:", contextFont));
			PdfPCell subscell13 = new PdfPCell(new Phrase("是否激活安全策略:", contextFont));
			PdfPCell subscell14 = new PdfPCell(new Phrase("关机完成数:", contextFont));
			PdfPCell subscell15 = new PdfPCell(new Phrase("子系统类型编号:", contextFont));
			PdfPCell subscell16 = new PdfPCell(new Phrase("是否开启部件关注:", contextFont));
			PdfPCell subscell17 = new PdfPCell(new Phrase("是否开启列划分:", contextFont));
			PdfPCell subscell18 = new PdfPCell(new Phrase("闪存不能写:", contextFont));
			PdfPCell subscell19 = new PdfPCell(new Phrase("写间隔:", contextFont));
			PdfPCell subscell20 = new PdfPCell(new Phrase("是否开启预取:", contextFont));
			PdfPCell subscell21 = new PdfPCell(new Phrase("关闭第二路径描述:", contextFont));
			PdfPCell subscell22 = new PdfPCell(new Phrase("", contextFont));

			subscell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell12.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell14.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell15.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell16.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell17.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell18.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell20.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			subscell22.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell vsubscell1 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getRaidLevel()), contextFont));
			PdfPCell vsubscell2 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getAutoFormatDrive()), contextFont));
			PdfPCell vsubscell3 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getHangDetection()), contextFont));
			PdfPCell vsubscell4 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getCapacityDepletionThreshold()), contextFont));
			PdfPCell vsubscell5 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getHangDetection()), contextFont));
			PdfPCell vsubscell6 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getCapacityDepletionThreshold()), contextFont));
			PdfPCell vsubscell7 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getQueueFullThresholdMaximum()), contextFont));
			PdfPCell vsubscell8 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getEnableOptimizePolicy()), contextFont));
			PdfPCell vsubscell9 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getEnableManualOverride()), contextFont));
			PdfPCell vsubscell10 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getManualOverrideDestination()), contextFont));
			PdfPCell vsubscell11 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getReadCacheDisable()), contextFont));
			PdfPCell vsubscell12 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getRebuildPriority()), contextFont));
			PdfPCell vsubscell13 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getSecurityEnabled()), contextFont));
			PdfPCell vsubscell14 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getShutdownCompletion()), contextFont));
			PdfPCell vsubscell15 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getSubsystemTypeID()), contextFont));
			PdfPCell vsubscell16 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getUnitAttention()), contextFont));
			PdfPCell vsubscell17 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getVolumeSetPartition()), contextFont));
			PdfPCell vsubscell18 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getWriteCacheEnable()), contextFont));
			PdfPCell vsubscell19 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getWriteWorkingSetInterval()), contextFont));
			PdfPCell vsubscell20 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getEnablePrefetch()), contextFont));
			PdfPCell vsubscell21 = new PdfPCell(new Phrase(SysUtil.ifNull(subSystemInfo.getDisableSecondaryPathPresentation()), contextFont));
			PdfPCell vsubscell22 = new PdfPCell(new Phrase("", contextFont));

			vsubscell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell12.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell13.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell14.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell15.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell16.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell17.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell18.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell19.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell20.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell21.setHorizontalAlignment(Element.ALIGN_CENTER);
			vsubscell22.setHorizontalAlignment(Element.ALIGN_CENTER);

			substable.addCell(subscell1);
			substable.addCell(vsubscell1);
			substable.addCell(subscell2);
			substable.addCell(vsubscell2);
			substable.addCell(subscell3);
			substable.addCell(vsubscell3);
			substable.addCell(subscell4);
			substable.addCell(vsubscell4);
			substable.addCell(subscell5);
			substable.addCell(vsubscell5);
			substable.addCell(subscell6);
			substable.addCell(vsubscell6);
			substable.addCell(subscell7);
			substable.addCell(vsubscell7);
			substable.addCell(subscell8);
			substable.addCell(vsubscell8);
			substable.addCell(subscell9);
			substable.addCell(vsubscell9);
			substable.addCell(subscell10);
			substable.addCell(vsubscell10);
			substable.addCell(subscell11);
			substable.addCell(vsubscell11);
			substable.addCell(subscell12);
			substable.addCell(vsubscell12);
			substable.addCell(subscell13);
			substable.addCell(vsubscell13);
			substable.addCell(subscell14);
			substable.addCell(vsubscell14);
			substable.addCell(subscell15);
			substable.addCell(vsubscell15);
			substable.addCell(subscell16);
			substable.addCell(vsubscell16);
			substable.addCell(subscell17);
			substable.addCell(vsubscell17);
			substable.addCell(subscell18);
			substable.addCell(vsubscell18);
			substable.addCell(subscell19);
			substable.addCell(vsubscell19);
			substable.addCell(subscell20);
			substable.addCell(vsubscell20);
			substable.addCell(subscell21);
			substable.addCell(vsubscell21);
			substable.addCell(subscell22);
			substable.addCell(vsubscell22);
			document.add(substable);
		}
		document.close();
	}

	/*
	 * HP存储磁盘生成DOC文档
	 */
	public void creatHPstorageDiskDoc(String file, String ipaddress) throws DocumentException, IOException {

		List<Disk> disks = null;
		Hashtable ipAllData = new Hashtable();
		// 只取当前采集的磁盘信息
		ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ipaddress);
		if (ipAllData != null) {
			disks = (List<Disk>) ipAllData.get("disks");
		}
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
		// Paragraph title = new Paragraph("主机服务器连通率报表", titleFont);
		Paragraph title = new Paragraph("HP存储" + ipaddress + "磁盘报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		Table aTable = new Table(7);
		int width[] = { 50, 50, 50, 130, 30, 50, 50 };
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

		Cell cell1 = new Cell("名称");
		Cell cell11 = new Cell("状态");
		Cell cell2 = new Cell("类型");
		Cell cell3 = new Cell("容量");
		Cell cell4 = new Cell("冗余组");
		Cell cell13 = new Cell("初始化声明");
		Cell cell14 = new Cell("固件版本");

		cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell14.setHorizontalAlignment(Element.ALIGN_CENTER);

		cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell13.setBackgroundColor(Color.LIGHT_GRAY);
		cell14.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell13);
		aTable.addCell(cell14);

		if (disks != null && disks.size() > 0) {
			for (int i = 0; i < disks.size(); i++) {
				Disk disk = (Disk) disks.get(i);
				Cell cell6 = new Cell(SysUtil.ifNull(disk.getName()));
				Cell cell7 = new Cell(SysUtil.ifNull(disk.getStatus()));
				Cell cell8 = new Cell(SysUtil.ifNull(disk.getVendorID()));
				Cell cell9 = new Cell(SysUtil.ifNull(disk.getDataCapacity()));
				Cell cell10 = new Cell(SysUtil.ifNull(disk.getRedundancyGroup()));
				Cell cell15 = new Cell(SysUtil.ifNull(disk.getInitializeState()));
				Cell cell16 = new Cell(SysUtil.ifNull(disk.getFirmwareRevision()));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell16.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell15);
				aTable.addCell(cell16);

			}
		}
		document.add(aTable);
		document.close();
	}

	/*
	 * HP存储磁盘生成PDF文档
	 */
	public void createHPstorageDiskPdf(String file, String ipaddress) throws DocumentException, IOException {

		// com.lowagie.text.Font FontChinese = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);

		// 设置纸张大小

		List<Disk> disks = null;
		Hashtable ipAllData = new Hashtable();
		// 只取当前采集的磁盘信息
		ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ipaddress);
		if (ipAllData != null) {
			disks = (List<Disk>) ipAllData.get("disks");
		}

		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// com.lowagie.text.Font titleFont = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
		// 正文字体风格
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = new Paragraph("HP存储" + ipaddress + "磁盘报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 11, Font.NORMAL, Color.black);

		PdfPTable aTable = new PdfPTable(7);

		int width[] = { 50, 50, 50, 130, 30, 50, 50 };
		aTable.setWidthPercentage(100);
		aTable.setWidths(width);

		PdfPCell cell1 = new PdfPCell(new Phrase("名称", fontChinese));
		PdfPCell cell11 = new PdfPCell(new Phrase("状态", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("类型", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("容量", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("冗余组", contextFont));
		PdfPCell cell15 = new PdfPCell(new Phrase("初始化声明", contextFont));
		PdfPCell cell16 = new PdfPCell(new Phrase("固件版本", contextFont));

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

		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell15);
		aTable.addCell(cell16);

		if (disks != null && disks.size() > 0) {
			for (int i = 0; i < disks.size(); i++) {
				Disk disk = (Disk) disks.get(i);

				PdfPCell cell6 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getName()), contextFont));
				PdfPCell cell7 = new PdfPCell(new Paragraph(SysUtil.ifNull(disk.getStatus()), contextFont));
				PdfPCell cell8 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getVendorID()), contextFont));
				PdfPCell cell9 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getDataCapacity()), contextFont));
				PdfPCell cell10 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getRedundancyGroup()), contextFont));
				PdfPCell cell17 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getInitializeState()), contextFont));
				PdfPCell cell18 = new PdfPCell(new Phrase(SysUtil.ifNull(disk.getFirmwareRevision()), contextFont));

				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell18.setHorizontalAlignment(Element.ALIGN_CENTER);

				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell17);
				aTable.addCell(cell18);
			}
		}
		document.add(aTable);
		document.close();
	}

	private String showNetworkEventReport() {
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
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
		HostNodeDao dao = new HostNodeDao();
		List selectedIPNodeList = dao.findBynode("ip_address", ipaddress);
		if (selectedIPNodeList != null && selectedIPNodeList.size() > 0) {
			HostNode node = (HostNode) selectedIPNodeList.get(0);
			EventListDao eventdao = new EventListDao();
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			// s.append("select * from system_eventlist where recordtime>= '" +
			// starttime + "' " + "and recordtime<='" + totime + "' ");
			if (SystemConstant.DBType.equals("mysql")) {
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");

			} else if (SystemConstant.DBType.equals("oracle")) {
				s.append("select * from system_eventlist where recordtime>=to_date( '" + starttime + "','yyyy-mm-dd  hh24:mi:ss') " + "and recordtime<=to_date('" + totime
						+ "','yyyy-mm-dd hh24:mi:ss') ");

			}
			s.append(" and nodeid=" + node.getId());

			request.setAttribute("nodeid", node.getId());

			List infolist = eventdao.findByCriteria(s.toString());

			// List infolist =
			// eventqueryManager.getQuery(starttime,totime,"99","99",99,node.getId());
			if (infolist != null && infolist.size() > 0) {
				// mainreport = mainreport+ " \r\n";
				int levelone = 0;
				int levletwo = 0;
				int levelthree = 0;
				int pingvalue = 0;
				int cpuvalue = 0;
				int updownvalue = 0;
				int utilvalue = 0;

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					String subentity = eventlist.getSubentity();
					if (eventlist.getLevel1() == 1) {
						levelone = levelone + 1;
					} else if (eventlist.getLevel1() == 2) {
						levletwo = levletwo + 1;
					} else if (eventlist.getLevel1() == 3) {
						levelthree = levelthree + 1;
					}
					// if(content.indexOf("连通率")>=0){
					// pingvalue = pingvalue + 1;
					// }else if(content.indexOf("CPU利用率")>=0){
					// cpuvalue = cpuvalue + 1;
					// }else if(content.indexOf("up")>=0 ||
					// content.indexOf("down")>=0){
					// updownvalue = updownvalue + 1;
					// }else if(content.indexOf("流速")>=0){
					// utilvalue = utilvalue + 1;
					// }

					if (("ping").equals(subentity)) {// 联通率
						pingvalue = pingvalue + 1;
					} else if (("cpu").equals(subentity)) {// cpu
						cpuvalue = cpuvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") >= 0) {// 端口
						updownvalue = updownvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") < 0) {// 流速
						utilvalue = utilvalue + 1;
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
				ipeventList.add(cpuvalue + "");
				ipeventList.add(updownvalue + "");
				ipeventList.add(utilvalue + "");
				orderList.add(ipeventList);

			}
		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("one") || orderflag.equalsIgnoreCase("two") || orderflag.equalsIgnoreCase("three") || orderflag.equalsIgnoreCase("ping")
				|| orderflag.equalsIgnoreCase("cpu") || orderflag.equalsIgnoreCase("updown") || orderflag.equalsIgnoreCase("util") || orderflag.equalsIgnoreCase("sum")) {
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
					} else if (orderflag.equalsIgnoreCase("cpu")) {
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
					} else if (orderflag.equalsIgnoreCase("updown")) {
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
					} else if (orderflag.equalsIgnoreCase("util")) {
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

		// setListProperty(capReportForm, request, list);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", list);
		session.setAttribute("eventlist", list);
		return "/capreport/net/showNetworkEventReport.jsp";
	}

	private String showNetworkCompositeReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// dddddddddddddddddd
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Hashtable allcpuhash = new Hashtable();
		String ip = "";
		String equipname = "";
		// zhushouzhi--------------------------start
		String type = "";
		String typename = "";
		String equipnameNetDoc = "";
		// zhushouzhi-----------------------------end
		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max

		Vector vector = new Vector();
		Vector iprouterVector = new Vector();
		List routerList = null;
		Hashtable reporthash = new Hashtable();
		int pingvalue = 0;
		int cpuvalue = 0;
		int updownvalue = 0;
		int utilvalue = 0;
		String runmodel = PollingEngine.getCollectwebflag();

		try {
			ip = getParaValue("ipaddress");
			// System.out.println("##########################ipaddress="+ip);
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = null;
			try {
				node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			// ip=node.getIpAddress();
			equipname = node.getAlias() + "(" + ip + ")";

			// zhushouzhi---------------------start
			equipnameNetDoc = node.getAlias();
			// zhushouzhi-----------------------end
			String remoteip = request.getRemoteAddr();
			String newip = doip(ip);
			// System.out.println("#####################newip"+newip);
			request.setAttribute("newip", newip);
			request.setAttribute("nodeid", node.getId());
			// 按排序标志取各端口最新记录的列表
			String orderflag = "index";

			String[] netInterfaceItem = { "index", "ifname", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				Hashtable pingdata = ShareData.getPingdata();
				Hashtable sharedata = ShareData.getSharedata();
				vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, orderflag, startdate, todate);
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
				// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
				Hashtable _IpRouterHash = ShareData.getIprouterdata();
				iprouterVector = (Vector) _IpRouterHash.get(ip);

				Hashtable hdata = (Hashtable) sharedata.get(ip);
				if (hdata == null)
					hdata = new Hashtable();
				Vector cpuVector = new Vector();
				if (hdata.get("cpu") != null)
					cpuVector = (Vector) hdata.get("cpu");
				if (cpuVector != null && cpuVector.size() > 0) {
					// for(int si=0;si<cpuVector.size();si++){
					CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
					maxhash.put("cpu", cpudata.getThevalue());
					reporthash.put("CPU", maxhash);
					// }
				} else {
					reporthash.put("CPU", maxhash);
				}
			} else {
				// 采集与访问是分离模式
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);

				// 获取当前内存值
				MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
				List memoryInfoList = memoryInfoService.getCurrPerMemoryListInfo();
				// 端口流速
				try {
					vector = hostlastmanager.getInterface(ip, netInterfaceItem, orderflag, starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// CPU
				CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
				String currCpuAvgInfo = cpuInfoService.getCurrCpuAvgInfo();
				maxhash.put("cpu", currCpuAvgInfo);
				// iprout
				NetService netService = new NetService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
				routerList = netService.getRouterInfo();
				// iprouterVector =
				// netService.getIpRouterVectorByList(routerList);
				// ******当前连通率start
				String pingnow = "0.0";
				String pingconavg = "0";
				Vector pingData = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype()).getPingInfo();
				;
				if (pingData != null && pingData.size() > 0) {
					PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
					Calendar tempCal = (Calendar) pingdata.getCollecttime();
					Date cc = tempCal.getTime();
					pingnow = pingdata.getThevalue();// 当前连通率
				}
				Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
				if (ConnectUtilizationhash.get("avgpingcon") != null) {
					pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				}
				String ConnectUtilizationmax = "";
				maxping.put("avgpingcon", pingconavg);
				if (ConnectUtilizationhash.get("max") != null) {
					ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
				}
				maxping.put("pingmax", ConnectUtilizationmax);
				reporthash.put("ping", maxping);
				reporthash.put("Ping", pingnow);
				// ******当前连通率end
			}
			PortconfigDao portdao = new PortconfigDao();
			Hashtable portconfigHash = portdao.getIpsHash(ip);
			List reportports = portdao.getByIpAndReportflag(ip, new Integer(1));
			if (reportports != null && reportports.size() > 0) {
				// SysLogger.info("reportports size
				// ##############"+reportports.size());
				// 显示端口的流速图形
				I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
				String unit = "kb/s";
				String title = starttime + "至" + totime + "端口流速";
				String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
				String[] bandch3 = { "入口流速", "出口流速" };

				for (int i = 0; i < reportports.size(); i++) {
					// SysLogger.info(reportports.get(i).getClass()+
					// "===============");
					com.afunms.config.model.Portconfig portconfig = null;
					try {
						portconfig = (com.afunms.config.model.Portconfig) reportports.get(i);
						// 按分钟显示报表
						Hashtable value = new Hashtable();
						value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
						String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
						p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
						String url1 = "../images/jfreechart/" + newip + portconfig.getPortindex() + "ifspeed_day.png";
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
			// System.out.println("----------------------------"+routerList);
			if (routerList != null) {
				reporthash.put("routerList", routerList);
			}
			reporthash.put("portconfigHash", portconfigHash);
			reporthash.put("reportports", reportports);
			reporthash.put("netifVector", vector);
			// zhushouzhi--------------------start
			reporthash.put("startdate", startdate);
			reporthash.put("todate", todate);
			reporthash.put("totime", totime);
			reporthash.put("starttime", starttime);
			reporthash.put("time", Calendar.getInstance());
			// zhushouzhi-------------------------end
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
			Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
			String pingconavg = "";
			String avgput = "";
			// String avgoutput = "";
			// maxhash = new Hashtable();
			String cpumax = "";
			String avgcpu = "";
			String maxput = "";
			if (cpuhash.get("max") != null) {
				cpumax = (String) cpuhash.get("max");
			}
			if (cpuhash.get("avgcpucon") != null) {
				avgcpu = (String) cpuhash.get("avgcpucon");
			}
			// zhushouzhi-----------------------start
			if (streaminHash.get("avgput") != null) {
				avgput = (String) streaminHash.get("avgput");
				reporthash.put("avginput", avgput);
			}
			if (streamoutHash.get("avgput") != null) {
				avgput = (String) streamoutHash.get("avgput");
				reporthash.put("avgoutput", avgput);
			}
			Hashtable streammaxinHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "max");
			Hashtable streammaxoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "max");
			if (streammaxinHash.get("max") != null) {
				maxput = (String) streammaxinHash.get("max");
				reporthash.put("maxinput", maxput);
			}
			if (streammaxoutHash.get("max") != null) {
				maxput = (String) streammaxoutHash.get("max");
				reporthash.put("maxoutput", maxput);
			}
			// zhushouzhi--------------------------------------end
			maxhash.put("cpumax", cpumax);
			maxhash.put("avgcpu", avgcpu);

			if (iprouterVector != null)
				reporthash.put("iprouterVector", iprouterVector);
			// zhushouzhi--------------------start
			EventListDao eventdao = new EventListDao();
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			// s.append("select * from system_eventlist where recordtime>= '" +
			// starttime + "' " + "and recordtime<='" + totime + "' ");
			if (SystemConstant.DBType.equals("mysql")) {
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");

			} else if (SystemConstant.DBType.equals("oracle")) {
				s.append("select * from system_eventlist where recordtime>= to_date('" + starttime + "','yyyy-mm-dd hh24:mi:ss') " + "and recordtime<= to_date('" + totime
						+ "','yyyy-mm-dd hh24:mi:ss') ");

			}
			s.append(" and nodeid=" + node.getId());

			List infolist = eventdao.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {
				// mainreport = mainreport+ " \r\n";

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					String subentity = eventlist.getSubentity();
					if (("ping").equals(subentity)) {// 联通率
						pingvalue = pingvalue + 1;
					} else if (("cpu").equals(subentity)) {// cpu
						cpuvalue = cpuvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") >= 0) {// 端口
						updownvalue = updownvalue + 1;
					} else if (("interface").equals(subentity) && content.indexOf("端口") < 0) {// 流速
						utilvalue = utilvalue + 1;
					}
				}
			}
			reporthash.put("CPU", maxhash);
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("cpuvalue", cpuvalue);
			reporthash.put("updownvalue", updownvalue);
			reporthash.put("utilvalue", utilvalue);
			// zhushouzhi-------------------------end

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

			// CPU
			// Hashtable hdata = (Hashtable) sharedata.get(ip);
			// if (hdata == null)
			// hdata = new Hashtable();
			// Vector cpuVector = new Vector();
			// if (hdata.get("cpu") != null)
			// cpuVector = (Vector) hdata.get("cpu");
			// if (cpuVector != null && cpuVector.size() > 0) {
			// // for(int si=0;si<cpuVector.size();si++){
			// CPUcollectdata cpudata = (CPUcollectdata) cpuVector.elementAt(0);
			// maxhash.put("cpu", cpudata.getThevalue());
			// reporthash.put("CPU", maxhash);
			// // }
			// } else {
			// reporthash.put("CPU", maxhash);
			// }
			// 流速

			reporthash.put("Memory", memhash);
			reporthash.put("Disk", diskhash);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameNetDoc", equipnameNetDoc);
			reporthash.put("ip", ip);
			if ("network".equals(type)) {
				typename = "网络设备";

			}
			reporthash.put("typename", typename);

			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			// 画图

			p_draw_line(cpuhash, "", newip + "cpu", 740, 120);

			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.setAttribute("reporthash", reporthash);
		return "/capreport/net/showNetworkCompositeReport.jsp";
	}

	private String showNetworkConfigReport() {
		String startdate = this.getParaValue("startdate");
		String todate = this.getParaValue("todate");
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/capreport/net/showNetworkConfigReport.jsp";
	}

	private String downloadNetworkPingReport() {
		String str = request.getParameter("str");
		if (str.equals("1")) {
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
		}
		if (str.equals("0")) {
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
			List memlist = (List) session.getAttribute("pinglist");
			Hashtable reporthash = new Hashtable();

			reporthash.put("pinglist", memlist);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);

			reporthash.put("pingpath", session.getAttribute("pingpath"));

			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			report.setRequest(request);
			report.createReport_hostping("/temp/liantonglvbaobiao.xls");
			request.setAttribute("filename", report.getFileName());
		}
		if (str.equals("4")) {
			String file = "/temp/liantonglvbaobiao.PDF";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				createPdfContextPing(fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}

		return "/capreport/net/download.jsp";
	}

	private String downloadNetworkEventReport() {
		String str = request.getParameter("str");
		if (str.equals("1")) {
			String file = "/temp/shijianbaobiao.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				createDocContextEvent(fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}
		if (str.equals("0")) {
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
			report.createReport_netevent("/temp/shijianbiao_report.xls");
			request.setAttribute("filename", report.getFileName());
		}
		if (str.equals("4")) {
			String file = "/temp/shijianbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				createDocContextEventPDF(fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
		}
		return "/capreport/net/download.jsp";
	}

	private String downloadNetworkCompositeReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		// zhushouzhi--------------------------------
		if ("0".equals(str)) {
			report.createReport_network("temp/networknms_report.xls");
			// SysLogger.info("filename---" + report.getFileName());//excel综合报表
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_networkDoc(fileName);// word综合报表

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
				String file = "temp/networkNewknms_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_networkNewDoc(fileName);// word运行分析报表

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
				String file = "temp/networkNewknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_networkNewPdf(fileName);// pdf网络设备业务分析表

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
			try {
				String file = "temp/networkknms_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_networkPDF(fileName);

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

	private String downloadNetworkConfigReport() {
		return "/capreport/net/download.jsp";
	}

	private String area_chooseDrawLineType(String timeType, Hashtable hash, Hashtable hash1, String[] brand1, String[] brand2, int w, int h, int range1, int range2) {
		String returnStr = "";
		if (timeType == null) {
			timeType = "minute";
		}
		if ("minute".equals(timeType)) {
			returnStr = area_p_draw_line(hash, hash1, brand1, brand2, w, h, range1, range2);
		} else if ("hour".equals(timeType)) {
			// p_draw_lineByHour(hash, title1, title2, w, h);
		} else if ("day".equals(timeType)) {
			// p_draw_lineByDay(hash, title1, title2, w, h);
		} else if ("month".equals(timeType)) {
			// p_draw_lineByMonth(hash,title1,title2,w,h);
		} else if ("year".equals(timeType)) {
			// p_draw_line(hash,title1,title2,w,h);
		} else {
			// p_draw_line(hash,title1,title2,w,h);
		}
		// SysLogger.info("###########"+returnStr);
		return returnStr;
	}

	public String area_p_draw_line(Hashtable hash, Hashtable hash1, String[] brand1, String[] brand2, int w, int h, int range1, int range2) {
		String seriesKey = SysUtil.getLongID();
		List list = (List) hash.get("list");
		// SysLogger.info("list size ============ "+list.size());
		try {

			if (list == null || list.size() == 0) {
				// draw_blank(title1,title2,w,h);
				seriesKey = "";
			} else {
				// 初始化数据
				final JFreeChart chart = ChartFactory.createTimeSeriesChart(brand1[0] + brand1[1] + "趋势图", "时间", brand2[0], ChartCreator.createForceDataset(hash, brand1[0]), true,
						true, false);

				// 设置全图背景色为白色
				chart.setBackgroundPaint(Color.WHITE);

				final XYPlot plot = chart.getXYPlot();
				plot.getDomainAxis().setLowerMargin(0.0);
				plot.getDomainAxis().setUpperMargin(0.0);
				plot.setRangeCrosshairVisible(true);
				plot.setDomainCrosshairVisible(true);
				plot.setBackgroundPaint(Color.WHITE);
				plot.setForegroundAlpha(0.8f);
				plot.setRangeGridlinesVisible(true);
				plot.setRangeGridlinePaint(Color.darkGray);
				plot.setDomainGridlinesVisible(true);
				plot.setDomainGridlinePaint(new Color(139, 69, 19));
				XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer) plot.getRenderer(0);
				render0.setSeriesPaint(0, Color.BLUE);

				// configure the range axis to display directions...
				final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
				rangeAxis.setAutoRangeIncludesZero(true);
				rangeAxis.setRange(0, range1);

				plot.setRangeAxis(rangeAxis);
				final XYItemRenderer renderer2 = new XYAreaRenderer();
				final ValueAxis axis2 = new NumberAxis(brand2[1]);
				axis2.setRange(0.0, range2);

				// 设置面积图颜色
				XYAreaRenderer xyarearenderer = new XYAreaRenderer();
				xyarearenderer.setSeriesPaint(1, new Color(0, 204, 0));
				xyarearenderer.setSeriesFillPaint(1, Color.GREEN);
				xyarearenderer.setPaint(Color.GREEN);
				plot.setDataset(1, ChartCreator.createForceDataset(hash1, brand1[1]));
				plot.setRenderer(1, xyarearenderer);
				plot.setRangeAxis(1, axis2);
				plot.mapDatasetToRangeAxis(1, 1);

				LegendTitle legend = chart.getLegend();
				// legend.setItemFont(new Font("Verdena", 0, 9));
				JFreeChartBrother jfb = new JFreeChartBrother();
				jfb.setChart(chart);
				jfb.setWidth(w);
				jfb.setHeight(h);
				ResourceCenter.getInstance().getChartStorage().put(seriesKey, jfb);

			}
			hash = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return seriesKey;
	}

	public String area_p_draw_multiline(Hashtable hash, String title, String[] bandch, String[] bandch1, int w, int h, int range1, int range2) {
		String seriesKey = SysUtil.getLongID();
		Vector datasetV = ChartCreator.createMultiDataset(hash, bandch, bandch1);
		try {
			if (datasetV == null || datasetV.size() == 0) {
				seriesKey = "";
			} else {
				// 初始化数据
				final JFreeChart chart = ChartFactory.createTimeSeriesChart(title + "趋势图", "时间", bandch1[0], (XYDataset) datasetV.get(0), true, true, false);

				// 设置全图背景色为白色
				chart.setBackgroundPaint(Color.WHITE);
				chart.setBorderPaint(new Color(30, 144, 255));
				chart.setBorderVisible(true);

				final XYPlot plot = chart.getXYPlot();
				plot.getDomainAxis().setLowerMargin(0.0);
				plot.getDomainAxis().setUpperMargin(0.0);
				plot.setRangeCrosshairVisible(true);
				plot.setDomainCrosshairVisible(true);
				plot.setBackgroundPaint(Color.WHITE);
				plot.setForegroundAlpha(0.8f);
				plot.setRangeGridlinesVisible(true);
				plot.setRangeGridlinePaint(Color.darkGray);
				plot.setDomainGridlinesVisible(true);
				plot.setDomainGridlinePaint(new Color(139, 69, 19));
				XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer) plot.getRenderer(0);
				render0.setSeriesPaint(0, Color.BLUE);

				// configure the range axis to display directions...
				final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
				rangeAxis.setAutoRangeIncludesZero(true);
				rangeAxis.setRange(0, range1);

				plot.setRangeAxis(rangeAxis);
				final XYItemRenderer renderer2 = new XYAreaRenderer();
				final ValueAxis axis2 = new NumberAxis(bandch1[1]);
				axis2.setRange(0.0, range2);

				// 设置面积图颜色
				XYAreaRenderer xyarearenderer = new XYAreaRenderer();
				xyarearenderer.setSeriesPaint(1, new Color(0, 204, 0));
				xyarearenderer.setSeriesFillPaint(1, Color.GREEN);
				xyarearenderer.setPaint(Color.GREEN);
				plot.setDataset(1, (XYDataset) datasetV.get(1));
				plot.setRenderer(1, xyarearenderer);
				plot.setRangeAxis(1, axis2);
				plot.mapDatasetToRangeAxis(1, 1);

				LegendTitle legend = chart.getLegend();
				// legend.setItemFont(new Font("Verdena", 0, 9));
				// legend.setMargin(0, 0, 0, 0);
				JFreeChartBrother jfb = new JFreeChartBrother();
				jfb.setChart(chart);
				jfb.setWidth(w);
				jfb.setHeight(h);

				ResourceCenter.getInstance().getChartStorage().put(seriesKey, jfb);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seriesKey;
	}

	private void p_draw_lineByHour(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Hour.class);
				TimeSeries[] s = { ss };
				System.out.println(list.size());
				Vector v0 = (Vector) list.get(0);
				String dt0 = (String) v0.get(1);
				SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time0 = sdf0.parse(dt0);
				Calendar temp0 = Calendar.getInstance();
				temp0.setTime(time0);
				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);

					Hour hour = new Hour(temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					if (temp.get(Calendar.HOUR_OF_DAY) > temp0.get(Calendar.HOUR_OF_DAY)) {
						ss.addOrUpdate(hour, d);
					}

					// ss.addOrUpdate(hour,d);
				}

				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void p_draw_lineByDay(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Day.class);
				TimeSeries[] s = { ss };

				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);

					Day day = new Day(temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));

					ss.addOrUpdate(day, d);
				}

				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void p_draw_lineByMonth(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Month.class);
				TimeSeries[] s = { ss };

				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);

					Month month = new Month(temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));

					ss.addOrUpdate(month, d);
				}

				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String storageHDS() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rpower", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "电源状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsPower.jsp";
	}

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	HostCollectDataManager manage = new HostCollectDataManager();

	public synchronized boolean createHostItemData(String ip, Hashtable datahash, String type, String subtype, String category) {
		DBManager dbmanager = null;
		try {
			dbmanager = new DBManager();
			Hashtable hash = ShareData.getOctetsdata(ip);

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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','"
										+ cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue()
										+ "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','"
										+ cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue()
										+ "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + processdata.getRestype() + "','" + processdata.getCategory() + "','" + processdata.getEntity() + "','"
											+ processdata.getSubentity() + "','" + processdata.getUnit() + "','" + processdata.getChname() + "','" + processdata.getBak() + "',"
											+ processdata.getCount() + ",'" + processdata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + processdata.getRestype() + "','" + processdata.getCategory() + "','" + processdata.getEntity() + "','"
											+ processdata.getSubentity() + "','" + processdata.getUnit() + "','" + processdata.getChname() + "','" + processdata.getBak() + "',"
											+ processdata.getCount() + ",'" + processdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','"
											+ memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "',"
											+ memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','"
											+ memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "',"
											+ memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','"
											+ memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "',"
											+ memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','"
											+ memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "',"
											+ memorydata.getCount() + ",'" + memorydata.getThevalue() + "'," + time + ")";
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
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','"
											+ diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','"
											+ diskdata.getThevalue() + "','" + time + "','" + diskdata.getUnit() + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','"
											+ diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','"
											+ diskdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'),'" + diskdata.getUnit() + "')";
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
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','"
											+ diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','"
											+ diskdata.getThevalue() + "','" + time + "','" + diskdata.getUnit() + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,category,subentity,restype,entity,thevalue,collecttime,unit) " + "values('" + ip + "','"
											+ diskdata.getCategory() + "','" + diskdata.getSubentity() + "','" + diskdata.getRestype() + "','" + diskdata.getEntity() + "','"
											+ diskdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'),'" + diskdata.getUnit() + "')";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity()
											+ "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'"
											+ utilhdx.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity()
											+ "','" + utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'"
											+ utilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','"
										+ cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue()
										+ "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + cpudata.getRestype() + "','" + cpudata.getCategory() + "','" + cpudata.getEntity() + "','" + cpudata.getSubentity() + "','"
										+ cpudata.getUnit() + "','" + cpudata.getChname() + "','" + cpudata.getBak() + "'," + cpudata.getCount() + ",'" + cpudata.getThevalue()
										+ "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','"
											+ memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "',"
											+ memorydata.getCount() + ",'" + memorydata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','"
											+ memorydata.getSubentity() + "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "',"
											+ memorydata.getCount() + ",'" + memorydata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + flashdata.getRestype() + "','" + flashdata.getCategory() + "','" + flashdata.getEntity() + "','" + flashdata.getSubentity()
										+ "','" + flashdata.getUnit() + "','" + flashdata.getChname() + "','" + flashdata.getBak() + "'," + flashdata.getCount() + ",'"
										+ flashdata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + flashdata.getRestype() + "','" + flashdata.getCategory() + "','" + flashdata.getEntity() + "','" + flashdata.getSubentity()
										+ "','" + flashdata.getUnit() + "','" + flashdata.getChname() + "','" + flashdata.getBak() + "'," + flashdata.getCount() + ",'"
										+ flashdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + temperdata.getRestype() + "','" + temperdata.getCategory() + "','" + temperdata.getEntity() + "','" + temperdata.getSubentity()
										+ "','" + temperdata.getUnit() + "','" + temperdata.getChname() + "','" + temperdata.getBak() + "'," + temperdata.getCount() + ",'"
										+ temperdata.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + temperdata.getRestype() + "','" + temperdata.getCategory() + "','" + temperdata.getEntity() + "','" + temperdata.getSubentity()
										+ "','" + temperdata.getUnit() + "','" + temperdata.getChname() + "','" + temperdata.getBak() + "'," + temperdata.getCount() + ",'"
										+ temperdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + fandata.getRestype() + "','" + fandata.getCategory() + "','" + fandata.getEntity() + "','" + fandata.getSubentity() + "','"
										+ fandata.getUnit() + "','" + fandata.getChname() + "','" + fandata.getBak() + "'," + fandata.getCount() + ",'" + fandata.getThevalue()
										+ "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + fandata.getRestype() + "','" + fandata.getCategory() + "','" + fandata.getEntity() + "','" + fandata.getSubentity() + "','"
										+ fandata.getUnit() + "','" + fandata.getChname() + "','" + fandata.getBak() + "'," + fandata.getCount() + ",'" + fandata.getThevalue()
										+ "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','"
											+ powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "',"
											+ powerdata.getCount() + ",'" + powerdata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','"
											+ powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "',"
											+ powerdata.getCount() + ",'" + powerdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','"
											+ powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "',"
											+ powerdata.getCount() + ",'" + powerdata.getThevalue() + "','" + time + "')";
								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('"
											+ ip + "','" + powerdata.getRestype() + "','" + powerdata.getCategory() + "','" + powerdata.getEntity() + "','"
											+ powerdata.getSubentity() + "','" + powerdata.getUnit() + "','" + powerdata.getChname() + "','" + powerdata.getBak() + "',"
											+ powerdata.getCount() + ",'" + powerdata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity()
										+ "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'"
										+ allutilhdx.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + allutilhdx.getRestype() + "','" + allutilhdx.getCategory() + "','" + allutilhdx.getEntity() + "','" + allutilhdx.getSubentity()
										+ "','" + allutilhdx.getUnit() + "','" + allutilhdx.getChname() + "','" + allutilhdx.getBak() + "'," + allutilhdx.getCount() + ",'"
										+ allutilhdx.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','"
										+ utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "',"
										+ utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + utilhdxperc.getRestype() + "','" + utilhdxperc.getCategory() + "','" + utilhdxperc.getEntity() + "','"
										+ utilhdxperc.getSubentity() + "','" + utilhdxperc.getUnit() + "','" + utilhdxperc.getChname() + "','" + utilhdxperc.getBak() + "',"
										+ utilhdxperc.getCount() + ",'" + utilhdxperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','"
										+ utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue()
										+ "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + utilhdx.getRestype() + "','" + utilhdx.getCategory() + "','" + utilhdx.getEntity() + "','" + utilhdx.getSubentity() + "','"
										+ utilhdx.getUnit() + "','" + utilhdx.getChname() + "','" + utilhdx.getBak() + "'," + utilhdx.getCount() + ",'" + utilhdx.getThevalue()
										+ "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + discardsperc.getRestype() + "','" + discardsperc.getCategory() + "','" + discardsperc.getEntity() + "','"
										+ discardsperc.getSubentity() + "','" + discardsperc.getUnit() + "','" + discardsperc.getChname() + "','" + discardsperc.getBak() + "',"
										+ discardsperc.getCount() + ",'" + discardsperc.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + discardsperc.getRestype() + "','" + discardsperc.getCategory() + "','" + discardsperc.getEntity() + "','"
										+ discardsperc.getSubentity() + "','" + discardsperc.getUnit() + "','" + discardsperc.getChname() + "','" + discardsperc.getBak() + "',"
										+ discardsperc.getCount() + ",'" + discardsperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + errorsperc.getRestype() + "','" + errorsperc.getCategory() + "','" + errorsperc.getEntity() + "','" + errorsperc.getSubentity()
										+ "','" + errorsperc.getUnit() + "','" + errorsperc.getChname() + "','" + errorsperc.getBak() + "'," + errorsperc.getCount() + ",'"
										+ errorsperc.getThevalue() + "','" + time + "')";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
										+ "','" + errorsperc.getRestype() + "','" + errorsperc.getCategory() + "','" + errorsperc.getEntity() + "','" + errorsperc.getSubentity()
										+ "','" + errorsperc.getUnit() + "','" + errorsperc.getChname() + "','" + errorsperc.getBak() + "'," + errorsperc.getCount() + ",'"
										+ errorsperc.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
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
							String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
									+ "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit()
									+ "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
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
							String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
									+ "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit()
									+ "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
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
							String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
									+ "','" + packs.getRestype() + "','" + packs.getCategory() + "','" + packs.getEntity() + "','" + packs.getSubentity() + "','" + packs.getUnit()
									+ "','" + packs.getChname() + "','" + packs.getBak() + "'," + packs.getCount() + ",'" + packs.getThevalue() + "'," + time + ")";
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
					String sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','"
							+ bufferdata.getRestype() + "','" + bufferdata.getCategory() + "','" + bufferdata.getEntity() + "','" + bufferdata.getSubentity() + "','"
							+ bufferdata.getUnit() + "','" + bufferdata.getChname() + "','" + bufferdata.getBak() + "'," + bufferdata.getCount() + ",'" + bufferdata.getThevalue()
							+ "'," + time + ")";
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
				List list = manage.loadFromIpMacBandRS(rs);
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
							dbmanager.executeUpdate(manage.ipmacInsertSQL(ipmac));
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
									dbmanager.executeUpdate(manage.ipmacbandUpdateSQL(changeMacBase));
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
										dbmanager.executeUpdate(manage.ipmacchangeInsertSQL(_ipmacchange));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
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
									dbmanager.executeUpdate(manage.ipmacbaseInsertSQL(_ipmacband));
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
									dbmanager.executeUpdate(manage.ipmacchangeInsertSQL(_ipmacchange));
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
								dbmanager.executeUpdate(manage.ipmacchangeInsertSQL(_ipmacchange));
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
	 * @see com.dhcc.webnms.host.api.I_HostCollectData#deleteHostData(java.lang.String)
	 */

	public Hashtable getCategory(String ip, String category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				if (category.equals("Ping") || category.equals("rcpu") || category.equals("rmemory") || category.equals("rcable") || category.equals("rcache")
						|| category.equals("rmemory") || category.equals("rpower") || category.equals("rfan") || category.equals("renv")) {
					sid = SysUtil.doip(ip);
				}
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (category.equals("Ping")) {
					//
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime ,h.unit from pings" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from pings" + sid + " h where ");
					}
				} else if (category.equals("rcpu")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rcpu" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rcpu" + sid + " h where ");
					}

				} else if (category.equals("rmemory")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rmemory" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rmemory" + sid + " h where ");
					}

				} else if (category.equals("rcable")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rcable" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rcable" + sid + " h where ");
					}

				} else if (category.equals("rcache")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rcache" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rcache" + sid + " h where ");
					}

				} else if (category.equals("rmemory")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rmemory" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rmemory" + sid + " h where ");
					}

				} else if (category.equals("rpower")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rpower" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rpower" + sid + " h where ");
					}

				} else if (category.equals("rfan")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rfan" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rfan" + sid + " h where ");
					}

				} else if (category.equals("renv")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from renv" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from renv" + sid + " h where ");
					}

				} else if (category.equals("edrive")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from edrive" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from edrive" + sid + " h where ");
					}

				} else if (category.equals("rbutter")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rbutter" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rbutter" + sid + " h where ");
					}

				} else if (category.equals("rnumber")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rnumber" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rnumber" + sid + " h where ");
					}

				} else if (category.equals("rluncon")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rluncon" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rluncon" + sid + " h where ");
					}

				} else if (category.equals("rswitch")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rswitch" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rswitch" + sid + " h where ");
					}

				} else if (category.equals("rwwncon")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rwwncon" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rwwncon" + sid + " h where ");
					}

				} else if (category.equals("rsafety")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rsafety" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rsafety" + sid + " h where ");
					}

				} else if (category.equals("rsluncon")) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from rsluncon" + sid + " h where ");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from rsluncon" + sid + " h where ");
					}

				}
				sb.append(" h.category='");
				sb.append(category);
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

				// System.out.println("--------HostCollectDataManager-------->"+sql);

				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				String max = "";
				double tempfloat = 0;
				// HONGLI ADD START1
				double pingMaxFloat = 0;
				// HONGLI ADD END1
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
						list1.add(v);
					}
					rs.close();
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

	private String dofloat(String num) {
		String snum = "0.0";
		if (num != null) {
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = Double.toString(inum / 100.0);
		}
		return snum;
	}

	private String storageFan() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rfan", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "风扇", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsFan.jsp";
	}

	private String storageEnv() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "renv", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "环境状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsEnv.jsp";
	}

	private String storageDrive() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "edrive", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "驱动状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsDrive.jsp";
	}

	private String storageCpu() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rcpu", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "CPU", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsCpu.jsp";
	}

	private String storageCache() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rcache", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "缓存", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsCache.jsp";
	}

	private String storageCable() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rcable", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "内部总线", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsCable.jsp";
	}

	private String storageButter() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rbutter", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "电池", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsButter.jsp";
	}

	private String storageMemory() {
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

		String ids = request.getParameter("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rmemory", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);

		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "共享内存", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		return "/capreport/net/hdsMemory.jsp";
	}

	public void createDocContextStorage(String file, String flag) throws DocumentException, IOException {
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

		Paragraph title = null;
		if (flag.equalsIgnoreCase("rpower")) {
			title = new Paragraph("存储设备电源报表", titleFont);
		} else if (flag.equalsIgnoreCase("rfan")) {
			title = new Paragraph("存储设备风扇报表", titleFont);
		} else if (flag.equalsIgnoreCase("rcpu")) {
			title = new Paragraph("存储设备CPU报表", titleFont);
		} else if (flag.equalsIgnoreCase("rcable")) {
			title = new Paragraph("存储设备内部总线报表", titleFont);
		} else if (flag.equalsIgnoreCase("rcache")) {
			title = new Paragraph("存储设备缓存报表", titleFont);
		} else if (flag.equalsIgnoreCase("rbutter")) {
			title = new Paragraph("存储设备电池报表", titleFont);
		} else if (flag.equalsIgnoreCase("rmemory")) {
			title = new Paragraph("存储设备共享内存报表", titleFont);
		} else if (flag.equalsIgnoreCase("edrive")) {
			title = new Paragraph("存储设备驱动报表", titleFont);
		} else if (flag.equalsIgnoreCase("renv")) {
			title = new Paragraph("存储设备环境报表", titleFont);
		} else if (flag.equalsIgnoreCase("rswitch")) {
			title = new Paragraph("转换开关报表", titleFont);
		} else if (flag.equalsIgnoreCase("rluncon")) {
			title = new Paragraph("交换控制器状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rnumber")) {
			title = new Paragraph("编号使用状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rwwncon")) {
			title = new Paragraph("wwn控制器状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rsluncon")) {
			title = new Paragraph("slun控制器状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rsafety")) {
			title = new Paragraph("wwn安全报表", titleFont);
		}
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		// List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(6);
		int width[] = { 50, 50, 50, 50, 50, 50 };
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

		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);

		Cell cell1 = new Cell("图例：");
		Cell cell2 = new Cell("1-正常");
		Cell cell3 = new Cell("2-紧急");
		Cell cell4 = new Cell("3-严重");
		Cell cell5 = new Cell("4-中等");
		Cell cell6 = new Cell("5-维护");
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		if (!flag.equals("rswitch") && !flag.equals("rluncon") && !flag.equals("rsluncon") && !flag.equals("rwwncon") && !flag.equals("rsafety") && !flag.equals("rnumber")) {
			document.add(aTable);
		}
		document.close();
	}

	// zhushouzhi调用服务器连通率报表方法
	public String createdocStorage() {
		String file = "";// 保存到项目文件夹下的指定文件夹
		String flag = request.getParameter("flag");
		if (flag.equalsIgnoreCase("rpower")) {
			file = "/temp/dianyuanbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rfan")) {
			file = "/temp/fengshanbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rcpu")) {
			file = "/temp/cpubaobiao.doc";
		} else if (flag.equalsIgnoreCase("rcable")) {
			file = "/temp/neibuzongxianbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rcache")) {
			file = "/temp/huancunbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rbutter")) {
			file = "/temp/dianchibaobiao.doc";
		} else if (flag.equalsIgnoreCase("rmemory")) {
			file = "/temp/gongxiangneicunbaobiao.doc";
		} else if (flag.equalsIgnoreCase("edrive")) {
			file = "/temp/qudongbaobiao.doc";
		} else if (flag.equalsIgnoreCase("renv")) {
			file = "/temp/huanjingbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rnumber")) {
			file = "/temp/numberbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rluncon")) {
			file = "/temp/lunControllerbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rswitch")) {
			file = "/temp/switchbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rwwncon")) {
			file = "/temp/wwnControllerbaobiao.doc";
		} else if (flag.equalsIgnoreCase("rsafety")) {
			file = "/temp/safetybaobiao.doc";
		} else if (flag.equalsIgnoreCase("rsluncon")) {
			file = "/temp/slunControllerbaobiao.doc";
		}
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextStorage(fileName, flag);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createPdfStorage(String file, String flag) throws DocumentException, IOException {

		// com.lowagie.text.Font FontChinese = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);

		// 设置纸张大小

		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// com.lowagie.text.Font titleFont = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
		// 正文字体风格
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = null;
		if (flag.equalsIgnoreCase("rpower")) {
			title = new Paragraph("存储设备电源报表", titleFont);
		} else if (flag.equalsIgnoreCase("rfan")) {
			title = new Paragraph("存储设备风扇报表", titleFont);
		} else if (flag.equalsIgnoreCase("rcpu")) {
			title = new Paragraph("存储设备CPU报表", titleFont);
		} else if (flag.equalsIgnoreCase("rcable")) {
			title = new Paragraph("存储设备内部总线报表", titleFont);
		} else if (flag.equalsIgnoreCase("rcache")) {
			title = new Paragraph("存储设备缓存报表", titleFont);
		} else if (flag.equalsIgnoreCase("rbutter")) {
			title = new Paragraph("存储设备电池报表", titleFont);
		} else if (flag.equalsIgnoreCase("rmemory")) {
			title = new Paragraph("存储设备共享内存报表", titleFont);
		} else if (flag.equalsIgnoreCase("edrive")) {
			title = new Paragraph("存储设备驱动报表", titleFont);
		} else if (flag.equalsIgnoreCase("renv")) {
			title = new Paragraph("存储设备环境报表", titleFont);
		} else if (flag.equalsIgnoreCase("rluncon")) {
			title = new Paragraph("交换控制器状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rnumber")) {
			title = new Paragraph("编号使用状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rwwncon")) {
			title = new Paragraph("wwn控制器状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rsluncon")) {
			title = new Paragraph("slun控制器状态报表", titleFont);
		} else if (flag.equalsIgnoreCase("rsafety")) {
			title = new Paragraph("wwn安全报表", titleFont);
		} else if (flag.equalsIgnoreCase("rswitch")) {
			title = new Paragraph("转换开关", titleFont);
		}
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 11, Font.NORMAL, Color.black);
		PdfPTable aTable = new PdfPTable(6);

		int width[] = { 50, 50, 50, 50, 50, 50 };
		aTable.setWidthPercentage(100);
		aTable.setWidths(width);

		// PdfPCell c = new PdfPCell(new Phrase("", fontChinese));
		// c.setBackgroundColor(Color.LIGHT_GRAY);
		// aTable.addCell(c);
		PdfPCell cell1 = new PdfPCell(new Phrase("图例：", fontChinese));
		PdfPCell cell11 = new PdfPCell(new Phrase("1-正常", fontChinese));
		PdfPCell cell2 = new PdfPCell(new Phrase("2-紧急", fontChinese));
		PdfPCell cell3 = new PdfPCell(new Phrase("3-严重", fontChinese));
		PdfPCell cell4 = new PdfPCell(new Phrase("4-中等", fontChinese));
		PdfPCell cell15 = new PdfPCell(new Phrase("5-维护", fontChinese));
		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell11.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell15.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell15);
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		// 导出响应时间图片
		// document.add(new Paragraph("\n"));
		// String ipaddress = (String)request.getParameter("ipaddress");
		// String newip = doip(ipaddress);
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(75);
		document.add(img);
		if (!flag.equals("rswitch") && !flag.equals("rluncon") && !flag.equals("rsluncon") && !flag.equals("rwwncon") && !flag.equals("rsafety") && !flag.equals("rnumber")) {
			document.add(aTable);
		}
		document.close();
	}

	// zhushouzhi调用服务器连通率报表方法
	public String createpdfStorage() {
		String file = "";// 保存到项目文件夹下的指定文件夹
		String flag = request.getParameter("flag");
		if (flag.equalsIgnoreCase("rpower")) {
			file = "/temp/dianyuanbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rfan")) {
			file = "/temp/fengshanbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rcpu")) {
			file = "/temp/cpubaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rcable")) {
			file = "/temp/neibuzongxianbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rcache")) {
			file = "/temp/huancunbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rbutter")) {
			file = "/temp/dianchibaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rmemory")) {
			file = "/temp/gongxiangneicunbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("edrive")) {
			file = "/temp/qudongbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("renv")) {
			file = "/temp/huanjingbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rnumber")) {
			file = "/temp/numberbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rluncon")) {
			file = "/temp/lunControllerbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rswitch")) {
			file = "/temp/switchbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rwwncon")) {
			file = "/temp/wwnControllerbaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rsafety")) {
			file = "/temp/safetybaobiao.PDF";
		} else if (flag.equalsIgnoreCase("rsluncon")) {
			file = "/temp/slunControllerbaobiao.PDF";
		}
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createPdfStorage(fileName, flag);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	private String creatXlsStorage() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		String flag = request.getParameter("flag");
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
		// List memlist = (List) session.getAttribute("pinglist");
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Hashtable reporthash = new Hashtable();
		// reporthash.put("pinglist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);

		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String file = "";
		if (flag.equalsIgnoreCase("rpower")) {
			file = "/temp/dianyuanbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rfan")) {
			file = "/temp/fengshanbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rcpu")) {
			file = "/temp/cpubaobiao.xls";
		} else if (flag.equalsIgnoreCase("rcable")) {
			file = "/temp/neibuzongxianbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rcache")) {
			file = "/temp/huancunbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rbutter")) {
			file = "/temp/dianchibaobiao.xls";
		} else if (flag.equalsIgnoreCase("rmemory")) {
			file = "/temp/gongxiangneicunbaobiao.xls";
		} else if (flag.equalsIgnoreCase("edrive")) {
			file = "/temp/qudongbaobiao.xls";
		} else if (flag.equalsIgnoreCase("renv")) {
			file = "/temp/huanjingbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rnumber")) {
			file = "/temp/numberbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rluncon")) {
			file = "/temp/lunControllerbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rswitch")) {
			file = "/temp/switchbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rwwncon")) {
			file = "/temp/wwnControllerbaobiao.xls";
		} else if (flag.equalsIgnoreCase("rsafety")) {
			file = "/temp/safetybaobiao.xls";
		} else if (flag.equalsIgnoreCase("rsluncon")) {
			file = "/temp/slunControllerbaobiao.xls";
		}
		report.createReport_storage(file, flag);
		// request.getSession().removeAttribute("pingpath");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	private String storageLunSwitch() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rswitch", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "转换开关", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsLunSwitch.jsp";
	}

	private String storageLunCon() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rluncon", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "交换控制器状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsLunCon.jsp";
	}

	private String storageWwnNumber() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rnumber", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "编号使用状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsWwnNumber.jsp";
	}

	private String storageWwnCon() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rwwncon", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "wwn控制器状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsWwnCon.jsp";
	}

	private String storageSlunSafety() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rsafety", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "wwn安全", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsSlunSafety.jsp";
	}

	private String storageSlunCon() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		// System.out.println(startdate+"*****************************************");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		// System.out.println("*****************************************"+todate);
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ids = getParaValue("id");
		String flag = (String) session.getAttribute("ids");
		if (flag != null && ids == null) {
			ids = flag;
			request.getSession().removeAttribute("ids");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null) {
			session.setAttribute("ids", ids);
			HostNodeDao dao = new HostNodeDao();
			HostNode node = dao.loadHost(Integer.parseInt(ids));
			String ip = node.getIpAddress();
			if (node.getIpAddress().contains(".")) {
				ip = node.getIpAddress().replace(".", "_");
			}
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getCategory(ip, "rsluncon", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable responsehash = new Hashtable();
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("hostnode", node);
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", node.getIpAddress() + "(" + node.getAlias() + ")");
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		// System.out.println("--------amchart---------"+pingChartDivStr);
		// String responsetimeDivStr = ReportHelper.getChartDivStr(orderList,
		// "responsetime");
		// 连通率
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		// ReportValue responseReportValue =
		// ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "slun控制器状态", "", "");
		// String responsetimepath = new
		// ReportExport().makeJfreeChartData(responseReportValue.getListValue(),
		// responseReportValue.getIpList(), "响应时间", "时间", "");
		System.out.println(pingReportValue.getListValue().size());
		// request.setAttribute("responsetimeDivStr", responsetimeDivStr);
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		// session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("pinglist", list);
		// request.setAttribute("id", ids);
		return "/capreport/net/hdsSlunCon.jsp";
	}

	private String vmwarecpu() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "cpu", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "cpu", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "cpu", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare CPU利用率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarecpu.jsp";
	}

	public Hashtable getVMCategory(String ip, String category, String vid, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select h." + category + ",DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime  from vm_guesthost" + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select h." + category + ",to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from vm_guesthost" + sid + " h where ");
			}

			// }
			sb.append(" h.vid='");
			sb.append(vid);
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

			// System.out.println("--------HostCollectDataManager-------->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			String unit = "";
			String max = "";
			double tempfloat = 0;
			// HONGLI ADD START1
			double pingMaxFloat = 0;
			// HONGLI ADD END1
			double pingcon = 0;
			double cpucon = 0;
			double memory = 0;
			int downnum = 0;
			int i = 0;
			if (rs != null) {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString(category);
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					list1.add(v);
				}
				rs.close();
			}
			// stmt.close();

			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					// unit = (String) tempV.get(2);
				}
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

	public String findMaxValue(String ip, String category, String vid, String starttime, String endtime) throws Exception {
		String result = "";
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h." + category + ") as " + category + " from vm_guesthost" + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h." + category + ") as " + category + " from vm_guesthost" + sid + " h where ");
			}

			// }
			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			// System.out.println("--------HostCollectDataManager----max---->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					result = rs.getString(category);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public String findAvgValue(String ip, String category, String vid, String starttime, String endtime) throws Exception {
		String result = "";
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select round(avg(h." + category + "),2) as " + category + " from vm_guesthost" + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select avg(h." + category + ") as " + category + " from vm_guesthost" + sid + " h where ");
			}

			// }
			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			System.out.println("--------HostCollectDataManager----avg---->" + sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					result = rs.getString(category);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	private String vmwarecpuuse() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "cpuuse", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "cpuuse", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "cpuuse", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare CPU使用情况", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		return "/capreport/net/vmwarecpuuse.jsp";
	}

	private String vmwarememout() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "memout", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "memout", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "memout", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 内存换出速率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarememout.jsp";
	}

	private String vmwarememin() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "memin", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "memin", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "memin", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 内存换入速率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarememin.jsp";
	}

	private String vmwaremem() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "mem", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "mem", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "mem", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 内存使用情况", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwaremem.jsp";
	}

	private String vmwaredisk() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "disk", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "disk", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "disk", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 磁盘使用情况", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwaredisk.jsp";
	}

	private String vmwarenet() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategory(ipaddress, "net", vid, starttime, totime);
				maxvalue = this.findMaxValue(ipaddress, "net", vid, starttime, totime);
				avgvalue = this.findAvgValue(ipaddress, "net", vid, starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 网络使用", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarenet.jsp";
	}

	public String createdocVM() {
		String file = "";// 保存到项目文件夹下的指定文件夹
		String flag = request.getParameter("flag");
		String nowvalue = request.getParameter("nowvalue");
		String avgvalue = request.getParameter("avgvalue");
		String maxvalue = request.getParameter("maxvalue");
		if (flag.equalsIgnoreCase("cpu")) {
			file = "/temp/cpu.doc";
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			file = "/temp/cpuuse.doc";
		} else if (flag.equalsIgnoreCase("mem")) {
			file = "/temp/mem.doc";
		} else if (flag.equalsIgnoreCase("memin")) {
			file = "/temp/memin.doc";
		} else if (flag.equalsIgnoreCase("memout")) {
			file = "/temp/memout.doc";
		} else if (flag.equalsIgnoreCase("disk")) {
			file = "/temp/disk.doc";
		} else if (flag.equalsIgnoreCase("net")) {
			file = "/temp/net.doc";
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			file = "/temp/cpuCR.doc";
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			file = "/temp/cputotalCR.doc";
		} else if (flag.equalsIgnoreCase("memCR")) {
			file = "/temp/memCR.doc";
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			file = "/temp/memtotalCR.doc";
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			file = "/temp/resourcepoolCPU.doc";
		}
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextVM(fileName, flag, nowvalue, avgvalue, maxvalue);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createDocContextVM(String file, String flag, String nowvalue, String avgvalue, String maxvalue) throws DocumentException, IOException {
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

		Paragraph title = null;
		if (flag.equalsIgnoreCase("cpu")) {
			title = new Paragraph("cpu利用率报表", titleFont);
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			title = new Paragraph("cpu使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("mem")) {
			title = new Paragraph("内存使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("memin")) {
			title = new Paragraph("内存换入速率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memout")) {
			title = new Paragraph("内存换出速率报表", titleFont);
		} else if (flag.equalsIgnoreCase("disk")) {
			title = new Paragraph("磁盘使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("net")) {
			title = new Paragraph("网络使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			title = new Paragraph("集群cpu使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			title = new Paragraph("集群cpu总计报表", titleFont);
		} else if (flag.equalsIgnoreCase("memCR")) {
			title = new Paragraph("集群内存使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			title = new Paragraph("集群内存总计报表", titleFont);
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			title = new Paragraph("资源池cpu使用情况报表", titleFont);
		}
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		// List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(4);
		int width[] = { 50, 50, 50, 50, };
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

		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);

		Cell cell1 = new Cell("指标名称");
		Cell cell2 = new Cell("当前");
		Cell cell3 = new Cell("平均");
		Cell cell4 = new Cell("最大");
		Cell cell5 = null;
		if (flag.equalsIgnoreCase("cpu")) {
			cell5 = new Cell("cpu利用率");
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			cell5 = new Cell("cpu使用情况");
		} else if (flag.equalsIgnoreCase("mem")) {
			cell5 = new Cell("内存使用情况");
		} else if (flag.equalsIgnoreCase("memin")) {
			cell5 = new Cell("内存换入速率");
		} else if (flag.equalsIgnoreCase("memout")) {
			cell5 = new Cell("内存换出速率");
		} else if (flag.equalsIgnoreCase("disk")) {
			cell5 = new Cell("磁盘使用情况");
		} else if (flag.equalsIgnoreCase("net")) {
			cell5 = new Cell("网络使用情况");
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			cell5 = new Cell("cpu使用情况");
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			cell5 = new Cell("cpu总计");
		} else if (flag.equalsIgnoreCase("memCR")) {
			cell5 = new Cell("内存使用情况");
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			cell5 = new Cell("内存总计");
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			cell5 = new Cell("cpu使用情况");
		}
		Cell cell6 = new Cell(nowvalue);
		Cell cell7 = new Cell(avgvalue);
		Cell cell8 = new Cell(maxvalue);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		document.add(aTable);
		document.close();
	}

	public String createpdfVM() {
		String file = "";// 保存到项目文件夹下的指定文件夹
		String flag = request.getParameter("flag");
		String nowvalue = request.getParameter("nowvalue");
		String avgvalue = request.getParameter("avgvalue");
		String maxvalue = request.getParameter("maxvalue");
		if (flag.equalsIgnoreCase("cpu")) {
			file = "/temp/cpu.PDF";
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			file = "/temp/cpuuse.PDF";
		} else if (flag.equalsIgnoreCase("mem")) {
			file = "/temp/mem.PDF";
		} else if (flag.equalsIgnoreCase("memin")) {
			file = "/temp/memin.PDF";
		} else if (flag.equalsIgnoreCase("memout")) {
			file = "/temp/memout.PDF";
		} else if (flag.equalsIgnoreCase("disk")) {
			file = "/temp/disk.PDF";
		} else if (flag.equalsIgnoreCase("net")) {
			file = "/temp/net.PDF";
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			file = "/temp/cpuCR.PDF";
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			file = "/temp/cputotalCR.PDF";
		} else if (flag.equalsIgnoreCase("memCR")) {
			file = "/temp/memCR.PDF";
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			file = "/temp/memtotalCR.PDF";
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			file = "/temp/resourcepoolCPU.PDF";
		}
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createPdfVM(fileName, flag, nowvalue, avgvalue, maxvalue);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createPdfVM(String file, String flag, String nowvalue, String avgvalue, String maxvalue) throws DocumentException, IOException {

		// com.lowagie.text.Font FontChinese = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);

		// 设置纸张大小

		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// com.lowagie.text.Font titleFont = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
		// 正文字体风格
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = null;
		if (flag.equalsIgnoreCase("cpu")) {
			title = new Paragraph("cpu利用率报表", titleFont);
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			title = new Paragraph("cpu使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("mem")) {
			title = new Paragraph("内存使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("memin")) {
			title = new Paragraph("内存换入速率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memout")) {
			title = new Paragraph("内存换出速率报表", titleFont);
		} else if (flag.equalsIgnoreCase("disk")) {
			title = new Paragraph("磁盘使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("net")) {
			title = new Paragraph("网络使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			title = new Paragraph("集群cpu使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			title = new Paragraph("集群cpu总计报表", titleFont);
		} else if (flag.equalsIgnoreCase("memCR")) {
			title = new Paragraph("集群内存使用情况报表", titleFont);
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			title = new Paragraph("集群内存总计报表", titleFont);
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			title = new Paragraph("资源池cpu使用情况报表", titleFont);
		}
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 11, Font.NORMAL, Color.black);
		PdfPTable aTable = new PdfPTable(4);

		int width[] = { 50, 50, 50, 50 };
		aTable.setWidthPercentage(100);
		aTable.setWidths(width);

		// PdfPCell c = new PdfPCell(new Phrase("", fontChinese));
		// c.setBackgroundColor(Color.LIGHT_GRAY);
		// aTable.addCell(c);
		PdfPCell cell1 = new PdfPCell(new Phrase("指标名称", fontChinese));
		PdfPCell cell2 = new PdfPCell(new Phrase("当前", fontChinese));
		PdfPCell cell3 = new PdfPCell(new Phrase("平均", fontChinese));
		PdfPCell cell4 = new PdfPCell(new Phrase("最大", fontChinese));

		PdfPCell cell5 = null;
		if (flag.equalsIgnoreCase("cpu")) {
			cell5 = new PdfPCell(new Phrase("cpu利用率", fontChinese));
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			cell5 = new PdfPCell(new Phrase("cpu使用情况", fontChinese));
		} else if (flag.equalsIgnoreCase("mem")) {
			cell5 = new PdfPCell(new Phrase("内存使用情况", fontChinese));
		} else if (flag.equalsIgnoreCase("memin")) {
			cell5 = new PdfPCell(new Phrase("内存换入速率", fontChinese));
		} else if (flag.equalsIgnoreCase("memout")) {
			cell5 = new PdfPCell(new Phrase("内存换出速率", fontChinese));
		} else if (flag.equalsIgnoreCase("disk")) {
			cell5 = new PdfPCell(new Phrase("磁盘使用情况", fontChinese));
		} else if (flag.equalsIgnoreCase("net")) {
			cell5 = new PdfPCell(new Phrase("网络使用情况", fontChinese));
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			cell5 = new PdfPCell(new Phrase("cpu使用情况", fontChinese));
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			cell5 = new PdfPCell(new Phrase("cpu总计", fontChinese));
		} else if (flag.equalsIgnoreCase("memCR")) {
			cell5 = new PdfPCell(new Phrase("内存使用情况", fontChinese));
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			cell5 = new PdfPCell(new Phrase("内存总计", fontChinese));
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			cell5 = new PdfPCell(new Phrase("cpu使用情况", fontChinese));
		}
		PdfPCell cell6 = new PdfPCell(new Phrase(nowvalue, fontChinese));
		PdfPCell cell7 = new PdfPCell(new Phrase(avgvalue, fontChinese));
		PdfPCell cell8 = new PdfPCell(new Phrase(maxvalue, fontChinese));

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		// 导出响应时间图片
		// document.add(new Paragraph("\n"));
		// String ipaddress = (String)request.getParameter("ipaddress");
		// String newip = doip(ipaddress);
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.close();
	}

	private String creatXlsVM() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		String flag = request.getParameter("flag");
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
		// List memlist = (List) session.getAttribute("pinglist");
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Hashtable reporthash = new Hashtable();
		// reporthash.put("pinglist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);

		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String file = "";
		String nowvalue = request.getParameter("nowvalue");
		String avgvalue = request.getParameter("avgvalue");
		String maxvalue = request.getParameter("maxvalue");
		if (flag.equalsIgnoreCase("cpu")) {
			file = "/temp/cpu.xls";
		} else if (flag.equalsIgnoreCase("cpuuse")) {
			file = "/temp/cpuuse.xls";
		} else if (flag.equalsIgnoreCase("mem")) {
			file = "/temp/mem.xls";
		} else if (flag.equalsIgnoreCase("memin")) {
			file = "/temp/memin.xls";
		} else if (flag.equalsIgnoreCase("memout")) {
			file = "/temp/memout.xls";
		} else if (flag.equalsIgnoreCase("disk")) {
			file = "/temp/disk.xls";
		} else if (flag.equalsIgnoreCase("net")) {
			file = "/temp/net.xls";
		} else if (flag.equalsIgnoreCase("cpuCR")) {
			file = "/temp/cpuCR.xls";
		} else if (flag.equalsIgnoreCase("cputotalCR")) {
			file = "/temp/cputotalCR.xls";
		} else if (flag.equalsIgnoreCase("memCR")) {
			file = "/temp/memCR.xls";
		} else if (flag.equalsIgnoreCase("memtotalCR")) {
			file = "/temp/memtotalCR.xls";
		} else if (flag.equalsIgnoreCase("cpuRP")) {
			file = "/temp/resourcepoolCPU.xls";
		}
		report.createReport_vm(file, flag, nowvalue, avgvalue, maxvalue);
		// request.getSession().removeAttribute("pingpath");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

	public String findMaxValueOther(String ip, String category, String vid, String starttime, String endtime, String tablename) throws Exception {
		String result = "";
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h." + category + ") as " + category + " from " + tablename + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h." + category + ") as " + category + " from " + tablename + sid + " h where ");
			}

			// }
			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			// System.out.println("--------HostCollectDataManager----max---->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					result = rs.getString(category);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public String findAvgValueOther(String ip, String category, String vid, String starttime, String endtime, String tablename) throws Exception {
		String result = "";
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select round(avg(h." + category + "),2) as " + category + " from " + tablename + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select avg(h." + category + ") as " + category + " from " + tablename + sid + " h where ");
			}

			// }
			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			// System.out.println("--------HostCollectDataManager----avg---->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					result = rs.getString(category);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public String emcFindMaxValueOther(String ip, String category, String flag, String starttime, String endtime, String tablename, String column) throws Exception {
		String result = "";
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h." + category + ") as " + category + " from " + tablename + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select max(h." + category + ") as " + category + " from " + tablename + sid + " h where ");
			}

			// }
			if (!column.equalsIgnoreCase("null")) {
				sb.append(" h." + column + "='");
				sb.append(flag);
				sb.append("' and ");
			}
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("  h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			// System.out.println("--------HostCollectDataManager----max---->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					result = rs.getString(category);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public String emcFindAvgValueOther(String ip, String category, String flag, String starttime, String endtime, String tablename, String column) throws Exception {
		String result = "";
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select round(avg(h." + category + "),2) as " + category + " from " + tablename + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select avg(h." + category + ") as " + category + " from " + tablename + sid + " h where ");
			}

			// }
			if (!column.equalsIgnoreCase("null")) {
				sb.append(" h." + column + "='");
				sb.append(flag);
				sb.append("' and ");
			}
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			// System.out.println("--------HostCollectDataManager----avg---->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			if (rs != null) {
				while (rs.next()) {
					result = rs.getString(category);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	private String vmwarecpuCR() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategoryOther(ipaddress, "cpu", vid, starttime, totime, "vm_cluster");
				maxvalue = this.findMaxValueOther(ipaddress, "cpu", vid, starttime, totime, "vm_cluster");
				avgvalue = this.findAvgValueOther(ipaddress, "cpu", vid, starttime, totime, "vm_cluster");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 集群cpu使用情况", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarecpuCR.jsp";
	}

	public Hashtable getVMCategoryOther(String ip, String category, String vid, String starttime, String endtime, String tablename) throws Exception {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select h." + category + ",DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime  from " + tablename + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select h." + category + ",to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from " + tablename + sid + " h where ");
			}

			// }
			sb.append(" h.vid='");
			sb.append(vid);
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

			// System.out.println("--------HostCollectDataManager-------->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			String unit = "";
			String max = "";
			double tempfloat = 0;
			// HONGLI ADD START1
			double pingMaxFloat = 0;
			// HONGLI ADD END1
			double pingcon = 0;
			double cpucon = 0;
			double memory = 0;
			int downnum = 0;
			int i = 0;
			if (rs != null) {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString(category);
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					list1.add(v);
				}
				rs.close();
			}
			// stmt.close();

			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					// unit = (String) tempV.get(2);
				}
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

	public Hashtable getEMCCategory(String ip, String category, String flag, String starttime, String endtime, String tablename, String column) throws Exception {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			// if (category.equals("Ping")) {
			//
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select h." + category + ",DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime  from " + tablename + sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" select h." + category + ",to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime ,h.unit from " + tablename + sid + " h where ");
			}

			// }
			if (!column.equalsIgnoreCase("null")) {
				sb.append(" h." + column + "='");
				sb.append(flag);
				sb.append("' and");
			}
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime asc");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append(" h.collecttime >= to_date('");
				sb.append(starttime);
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append(endtime);
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
				sb.append(" order by h.collecttime asc");
			}

			sql = sb.toString();

			// System.out.println("--------HostCollectDataManager-------->"+sql);

			rs = dbmanager.executeQuery(sql);
			List list1 = new ArrayList();
			String unit = "";
			String max = "";
			double tempfloat = 0;
			// HONGLI ADD START1
			double pingMaxFloat = 0;
			// HONGLI ADD END1
			double pingcon = 0;
			double cpucon = 0;
			double memory = 0;
			int downnum = 0;
			int i = 0;
			if (rs != null) {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString(category);
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					list1.add(v);
				}
				rs.close();
			}
			// stmt.close();

			Integer size = new Integer(0);
			hash.put("list", list1);
			if (list1.size() != 0) {
				size = new Integer(list1.size());
				if (list1.get(0) != null) {
					Vector tempV = (Vector) list1.get(0);
					// unit = (String) tempV.get(2);
				}
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

	private String vmwarecpuuseCR() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategoryOther(ipaddress, "cputotal", vid, starttime, totime, "vm_cluster");
				maxvalue = this.findMaxValueOther(ipaddress, "cputotal", vid, starttime, totime, "vm_cluster");
				avgvalue = this.findAvgValueOther(ipaddress, "cputotal", vid, starttime, totime, "vm_cluster");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 集群cpu总计", "", "");
		// System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarecputotalCR.jsp";
	}

	private String vmwarememCR() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategoryOther(ipaddress, "mem", vid, starttime, totime, "vm_cluster");
				maxvalue = this.findMaxValueOther(ipaddress, "mem", vid, starttime, totime, "vm_cluster");
				avgvalue = this.findAvgValueOther(ipaddress, "mem", vid, starttime, totime, "vm_cluster");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 集群内存使用情况", "", "");
		// System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarememCR.jsp";
	}

	private String vmwarememuseCR() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategoryOther(ipaddress, "memtotal", vid, starttime, totime, "vm_cluster");
				maxvalue = this.findMaxValueOther(ipaddress, "memtotal", vid, starttime, totime, "vm_cluster");
				avgvalue = this.findAvgValueOther(ipaddress, "memtotal", vid, starttime, totime, "vm_cluster");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 集群内存总计", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarememtotalCR.jsp";
	}

	private String vmwarecpuRP() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String vid = getParaValue("vid");
		String nowvalue = request.getParameter("cpunow");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_vid = (String) session.getAttribute("vid");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_vid != null && vid == null) {
			vid = flag_vid;
			request.getSession().removeAttribute("vid");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && vid != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("vid", vid);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getVMCategoryOther(ipaddress, "cpu", vid, starttime, totime, "vm_resourcepool");
				maxvalue = this.findMaxValueOther(ipaddress, "cpu", vid, starttime, totime, "vm_resourcepool");
				avgvalue = this.findAvgValueOther(ipaddress, "cpu", vid, starttime, totime, "vm_resourcepool");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "VMWare 资源池cpu使用情况", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("vid", vid);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/vmwarecpuRP.jsp";
	}

	private String emcTotalLength() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "totalqueuelength", name, starttime, totime, "emclunper", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "totalqueuelength", name, starttime, totime, "emclunper", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "totalqueuelength", name, starttime, totime, "emclunper", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc Lun队列总长度", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcLunLength.jsp";
	}

	private String emcLunSofterrors() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "totalsofterrors", name, starttime, totime, "emclunper", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "totalsofterrors", name, starttime, totime, "emclunper", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "totalsofterrors", name, starttime, totime, "emclunper", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc Lun软件错误总数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcLunsoft.jsp";
	}

	private String emcLunHarderrors() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "totalqueuelength", name, starttime, totime, "emclunper", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "totalqueuelength", name, starttime, totime, "emclunper", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "totalqueuelength", name, starttime, totime, "emclunper", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc Lun硬件错误总数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcLunHard.jsp";
	}

	private String emcdiskread() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "numberofreads", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "numberofreads", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "numberofreads", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘读请求数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskRead.jsp";
	}

	private String emcdisksoftread() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "softreaderrors", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "softreaderrors", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "softreaderrors", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘软件读错误数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskSoftRead.jsp";
	}

	private String emcdisksoftwrite() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "softwriteerrors", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "softwriteerrors", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "softwriteerrors", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘软件写错误数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskSoftWrite.jsp";
	}

	private String emcdiskreadkb() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "kbytesread", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "kbytesread", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "kbytesread", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘读KB", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskReadKb.jsp";
	}

	private String emcdiskwritekb() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "kbyteswritten", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "kbyteswritten", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "kbyteswritten", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc磁盘写KB", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskWriteKb.jsp";
	}

	private String emcdiskfree() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "idleticks", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "idleticks", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "idleticks", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc磁盘空闲状态时间量", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskFree.jsp";
	}

	private String emcdiskbus() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "busyticks", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "busyticks", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "busyticks", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc磁盘忙状态时间量", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskBus.jsp";
	}

	private String emcdiskhardread() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "hardreaderrors", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "hardreaderrors", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "hardreaderrors", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘硬件读错误数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskHardRead.jsp";
	}

	private String emcdiskhardwrite() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "hardwritererrors", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "hardwritererrors", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "hardwritererrors", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘硬件写错误数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskHardWrite.jsp";
	}

	private String emcdiskwrite() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "numberofwrites", name, starttime, totime, "emcdiskper", "serialnumber");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "numberofwrites", name, starttime, totime, "emcdiskper", "serialnumber");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "numberofwrites", name, starttime, totime, "emcdiskper", "serialnumber");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 磁盘写请求数", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcDiskWrite.jsp";
	}

	private String emcenvarraywt() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		// String name = getParaValue("flag");
		// String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		// String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		// if(flag_name != null && name == null){
		// name = flag_name;
		// request.getSession().removeAttribute("flag");
		// }
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null) {
			session.setAttribute("ipaddress", ipaddress);
			// session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "presentwatts", "", starttime, totime, "emcenvpower", "null");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "presentwatts", "", starttime, totime, "emcenvpower", "null");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "presentwatts", "", starttime, totime, "emcenvpower", "null");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 阵列电源当前功率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		// request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvArraywt.jsp";
	}

	private String emcenvarrayavgwt() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		// String name = getParaValue("flag");
		// String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		// String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		// if(flag_name != null && name == null){
		// name = flag_name;
		// request.getSession().removeAttribute("flag");
		// }
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null) {
			session.setAttribute("ipaddress", ipaddress);
			// session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "averagewatts", "", starttime, totime, "emcenvpower", "null");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "averagewatts", "", starttime, totime, "emcenvpower", "null");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "averagewatts", "", starttime, totime, "emcenvpower", "null");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 阵列电源平均功率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		// request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvArrayAvgwt.jsp";
	}

	private String emcenvmemwt() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "PresentWatts", name, starttime, totime, "emcenvstore", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "PresentWatts", name, starttime, totime, "emcenvstore", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "PresentWatts", name, starttime, totime, "emcenvstore", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 存储模块电源当前平均功率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvMemWt.jsp";
	}

	private String emcenvmemavgwt() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "AverageWatts", name, starttime, totime, "emcenvstore", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "AverageWatts", name, starttime, totime, "emcenvstore", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "AverageWatts", name, starttime, totime, "emcenvstore", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 存储模块电源平均功率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvMemAvgWt.jsp";
	}

	private String emcenvmemtmp() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "PresentDegree", name, starttime, totime, "emcenvstore", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "PresentDegree", name, starttime, totime, "emcenvstore", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "PresentDegree", name, starttime, totime, "emcenvstore", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 存储入口温度当前温度", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvMemTmp.jsp";
	}

	private String emcenvmemavgtmp() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "AverageDegree", name, starttime, totime, "emcenvstore", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "AverageDegree", name, starttime, totime, "emcenvstore", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "AverageDegree", name, starttime, totime, "emcenvstore", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc 存储入口温度平均温度", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvMemAvgTmp.jsp";
	}

	private String emcenvbakwt() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "PresentWatts", name, starttime, totime, "emcbakpower", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "PresentWatts", name, starttime, totime, "emcbakpower", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "PresentWatts", name, starttime, totime, "emcbakpower", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc sps备用电源当前功率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvBakWt.jsp";
	}

	private String emcenvbakavgwt() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String maxvalue = "";
		String avgvalue = "";
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ipaddress = getParaValue("ip");
		String name = getParaValue("flag");
		String nowvalue = request.getParameter("totallength");
		String flag = (String) session.getAttribute("ipaddress");
		String flag_name = (String) session.getAttribute("flag");
		if (flag != null && ipaddress == null) {
			ipaddress = flag;
			request.getSession().removeAttribute("ipaddress");
		}
		if (flag_name != null && name == null) {
			name = flag_name;
			request.getSession().removeAttribute("flag");
		}
		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ipaddress != null && name != null) {
			session.setAttribute("ipaddress", ipaddress);
			session.setAttribute("flag", name);
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = this.getEMCCategory(ipaddress, "AverageWatts", name, starttime, totime, "emcbakpower", "name");
				maxvalue = this.emcFindMaxValueOther(ipaddress, "AverageWatts", name, starttime, totime, "emcbakpower", "name");
				avgvalue = this.emcFindAvgValueOther(ipaddress, "AverageWatts", name, starttime, totime, "emcbakpower", "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable ipmemhash = new Hashtable();
			ipmemhash.put("pinghash", pinghash);
			ipmemhash.put("ipaddress", ipaddress);
			orderList.add(ipmemhash);
		}
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "emc sps备用电源平均功率", "", "");
		System.out.println(pingReportValue.getListValue().size());
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("orderList", orderList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("name", name);
		request.setAttribute("ip", ipaddress.replace("_", "."));
		request.setAttribute("maxvalue", maxvalue);
		request.setAttribute("avgvalue", avgvalue);
		request.setAttribute("nowvalue", nowvalue);
		return "/capreport/net/emcEnvBakAvgWt.jsp";
	}

	// EMc存储 报表

	public String createdocEMC() {
		String file = "";// 保存到项目文件夹下的指定文件夹
		String flag = request.getParameter("flag");
		String nowvalue = request.getParameter("nowvalue");
		String avgvalue = request.getParameter("avgvalue");
		String maxvalue = request.getParameter("maxvalue");
		if (flag.equalsIgnoreCase("arraywt")) {
			file = "/temp/arraywt.doc";
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			file = "/temp/arrayavgwt.doc";
		} else if (flag.equalsIgnoreCase("memwt")) {
			file = "/temp/memwt.doc";
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			file = "/temp/memavgwt.doc";
		} else if (flag.equalsIgnoreCase("memtmp")) {
			file = "/temp/memtmp.doc";
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			file = "/temp/memavgtmp.doc";
		} else if (flag.equalsIgnoreCase("bakwt")) {
			file = "/temp/bakwt.doc";
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			file = "/temp/bakavgwt.doc";
		} else if (flag.equalsIgnoreCase("diskread")) {
			file = "/temp/diskread.doc";
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			file = "/temp/memCR.doc";
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			file = "/temp/disksoftreaderror.doc";
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			file = "/temp/disksoftwriteerror.doc";
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			file = "/temp/diskwritekb.doc";
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			file = "/temp/diskreadkb.doc";
		} else if (flag.equalsIgnoreCase("diskfree")) {
			file = "/temp/diskfree.doc";
		} else if (flag.equalsIgnoreCase("diskbus")) {
			file = "/temp/diskbus.doc";
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			file = "/temp/diskhardwriteerror.doc";
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			file = "/temp/diskhardreaderror.doc";
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			file = "/temp/lunharderror.doc";
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			file = "/temp/lunsofterror.doc";
		} else if (flag.equalsIgnoreCase("lunlength")) {
			file = "/temp/lunlength.doc";
		}
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextEMC(fileName, flag, nowvalue, avgvalue, maxvalue);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createDocContextEMC(String file, String flag, String nowvalue, String avgvalue, String maxvalue) throws DocumentException, IOException {
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

		Paragraph title = null;
		if (flag.equalsIgnoreCase("arraywt")) {
			title = new Paragraph("阵列电源当前功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			title = new Paragraph("阵列电源平均功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memwt")) {
			title = new Paragraph("存储模块电源当前功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			title = new Paragraph("存储模块电源平均功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memtmp")) {
			title = new Paragraph("存储入口温度当前温度报表", titleFont);
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			title = new Paragraph("存储入口温度平均温度报表", titleFont);
		} else if (flag.equalsIgnoreCase("bakwt")) {
			title = new Paragraph("SPS备用电源当前输出功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			title = new Paragraph("SPS备用电源平均输出功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			title = new Paragraph("硬件错误总数报表", titleFont);
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			title = new Paragraph("软件错误总数报表", titleFont);
		} else if (flag.equalsIgnoreCase("lunlength")) {
			title = new Paragraph("队列总长度", titleFont);
		} else if (flag.equalsIgnoreCase("diskread")) {
			title = new Paragraph("读请求数报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			title = new Paragraph("写请求数报表", titleFont);
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			title = new Paragraph("软件读错误数报表", titleFont);
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			title = new Paragraph("软件写错误数报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			title = new Paragraph("读KB报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			title = new Paragraph("写KB报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskfree")) {
			title = new Paragraph("闲状态时间量报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskbus")) {
			title = new Paragraph("忙状态时间量报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			title = new Paragraph("硬件读错误数报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			title = new Paragraph("硬件写错误数报表", titleFont);
		}
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		// List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(4);
		int width[] = { 50, 50, 50, 50, };
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

		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);

		Cell cell1 = new Cell("指标名称");
		Cell cell2 = new Cell("当前");
		Cell cell3 = new Cell("平均");
		Cell cell4 = new Cell("最大");
		Cell cell5 = null;
		if (flag.equalsIgnoreCase("arraywt")) {
			cell5 = new Cell("阵列电源当前功率");
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			cell5 = new Cell("阵列电源平均功率");
		} else if (flag.equalsIgnoreCase("memwt")) {
			cell5 = new Cell("存储模块电源当前功率");
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			cell5 = new Cell("存储模块电源平均功率");
		} else if (flag.equalsIgnoreCase("memtmp")) {
			cell5 = new Cell("存储入口温度当前温度");
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			cell5 = new Cell("SPS备用电源当前输出功率");
		} else if (flag.equalsIgnoreCase("bakwt")) {
			cell5 = new Cell("SPS备用电源当前输出功率");
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			cell5 = new Cell("SPS备用电源平均输出功率");
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			cell5 = new Cell("硬件错误总数");
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			cell5 = new Cell("软件错误总数");
		} else if (flag.equalsIgnoreCase("lunlength")) {
			cell5 = new Cell("队列总长度");
		} else if (flag.equalsIgnoreCase("diskread")) {
			cell5 = new Cell("读请求数");
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			cell5 = new Cell("写请求数");
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			cell5 = new Cell("软件读错误数");
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			cell5 = new Cell("软件写错误数");
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			cell5 = new Cell("读KB");
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			cell5 = new Cell("写KB");
		} else if (flag.equalsIgnoreCase("diskfree")) {
			cell5 = new Cell("闲状态时间量");
		} else if (flag.equalsIgnoreCase("diskbus")) {
			cell5 = new Cell("忙状态时间量");
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			cell5 = new Cell("硬件读错误数");
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			cell5 = new Cell("硬件写错误数");
		}
		Cell cell6 = new Cell(nowvalue);
		Cell cell7 = new Cell(avgvalue);
		Cell cell8 = new Cell(maxvalue);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		document.add(aTable);
		document.close();
	}

	public String createpdfEMC() {
		String file = "";// 保存到项目文件夹下的指定文件夹
		String flag = request.getParameter("flag");
		String nowvalue = request.getParameter("nowvalue");
		String avgvalue = request.getParameter("avgvalue");
		String maxvalue = request.getParameter("maxvalue");
		if (flag.equalsIgnoreCase("arraywt")) {
			file = "/temp/arraywt.PDF";
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			file = "/temp/arrayavgwt.PDF";
		} else if (flag.equalsIgnoreCase("memwt")) {
			file = "/temp/memwt.PDF";
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			file = "/temp/memavgwt.PDF";
		} else if (flag.equalsIgnoreCase("memtmp")) {
			file = "/temp/memtmp.PDF";
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			file = "/temp/memavgtmp.PDF";
		} else if (flag.equalsIgnoreCase("bakwt")) {
			file = "/temp/bakwt.PDF";
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			file = "/temp/bakavgwt.PDF";
		} else if (flag.equalsIgnoreCase("diskread")) {
			file = "/temp/diskread.PDF";
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			file = "/temp/memCR.PDF";
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			file = "/temp/disksoftreaderror.PDF";
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			file = "/temp/disksoftwriteerror.PDF";
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			file = "/temp/diskwritekb.PDF";
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			file = "/temp/diskreadkb.PDF";
		} else if (flag.equalsIgnoreCase("diskfree")) {
			file = "/temp/diskfree.PDF";
		} else if (flag.equalsIgnoreCase("diskbus")) {
			file = "/temp/diskbus.PDF";
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			file = "/temp/diskhardwriteerror.PDF";
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			file = "/temp/diskhardreaderror.PDF";
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			file = "/temp/lunharderror.PDF";
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			file = "/temp/lunsofterror.PDF";
		} else if (flag.equalsIgnoreCase("lunlength")) {
			file = "/temp/lunlength.PDF";
		}
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createPdfEMC(fileName, flag, nowvalue, avgvalue, maxvalue);
			// request.getSession().removeAttribute("pingpath");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public void createPdfEMC(String file, String flag, String nowvalue, String avgvalue, String maxvalue) throws DocumentException, IOException {

		// com.lowagie.text.Font FontChinese = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);

		// 设置纸张大小

		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// com.lowagie.text.Font titleFont = new
		// com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
		// 正文字体风格
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = null;
		// title = new Paragraph("cpu利用率报表", titleFont);
		if (flag.equalsIgnoreCase("arraywt")) {
			title = new Paragraph("阵列电源当前功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			title = new Paragraph("阵列电源平均功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memwt")) {
			title = new Paragraph("存储模块电源当前功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			title = new Paragraph("存储模块电源平均功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("memtmp")) {
			title = new Paragraph("存储入口温度当前温度报表", titleFont);
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			title = new Paragraph("存储入口温度平均温度报表", titleFont);
		} else if (flag.equalsIgnoreCase("bakwt")) {
			title = new Paragraph("SPS备用电源当前输出功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			title = new Paragraph("SPS备用电源平均输出功率报表", titleFont);
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			title = new Paragraph("硬件错误总数报表", titleFont);
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			title = new Paragraph("软件错误总数报表", titleFont);
		} else if (flag.equalsIgnoreCase("lunlength")) {
			title = new Paragraph("队列总长度", titleFont);
		} else if (flag.equalsIgnoreCase("diskread")) {
			title = new Paragraph("读请求数报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			title = new Paragraph("写请求数报表", titleFont);
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			title = new Paragraph("软件读错误数报表", titleFont);
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			title = new Paragraph("软件写错误数报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			title = new Paragraph("读KB报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			title = new Paragraph("写KB报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskfree")) {
			title = new Paragraph("闲状态时间量报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskbus")) {
			title = new Paragraph("忙状态时间量报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			title = new Paragraph("硬件读错误数报表", titleFont);
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			title = new Paragraph("硬件写错误数报表", titleFont);
		}
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		Font fontChinese = new Font(bfChinese, 11, Font.NORMAL, Color.black);
		PdfPTable aTable = new PdfPTable(4);

		int width[] = { 50, 50, 50, 50 };
		aTable.setWidthPercentage(100);
		aTable.setWidths(width);

		// PdfPCell c = new PdfPCell(new Phrase("", fontChinese));
		// c.setBackgroundColor(Color.LIGHT_GRAY);
		// aTable.addCell(c);
		PdfPCell cell1 = new PdfPCell(new Phrase("指标名称", fontChinese));
		PdfPCell cell2 = new PdfPCell(new Phrase("当前", fontChinese));
		PdfPCell cell3 = new PdfPCell(new Phrase("平均", fontChinese));
		PdfPCell cell4 = new PdfPCell(new Phrase("最大", fontChinese));
		// cell5 = new PdfPCell(new Phrase("cpu利用率", fontChinese));
		PdfPCell cell5 = null;
		if (flag.equalsIgnoreCase("arraywt")) {
			cell5 = new PdfPCell(new Phrase("阵列电源当前功率", fontChinese));
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			cell5 = new PdfPCell(new Phrase("阵列电源平均功率", fontChinese));
		} else if (flag.equalsIgnoreCase("memwt")) {
			cell5 = new PdfPCell(new Phrase("存储模块电源当前功率", fontChinese));
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			cell5 = new PdfPCell(new Phrase("存储模块电源平均功率", fontChinese));
		} else if (flag.equalsIgnoreCase("memtmp")) {
			cell5 = new PdfPCell(new Phrase("存储入口温度当前温度", fontChinese));
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			cell5 = new PdfPCell(new Phrase("SPS备用电源当前输出功率", fontChinese));
		} else if (flag.equalsIgnoreCase("bakwt")) {
			cell5 = new PdfPCell(new Phrase("SPS备用电源当前输出功率", fontChinese));
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			cell5 = new PdfPCell(new Phrase("SPS备用电源平均输出功率", fontChinese));
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			cell5 = new PdfPCell(new Phrase("硬件错误总数", fontChinese));
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			cell5 = new PdfPCell(new Phrase("软件错误总数", fontChinese));
		} else if (flag.equalsIgnoreCase("lunlength")) {
			cell5 = new PdfPCell(new Phrase("队列总长度", fontChinese));
		} else if (flag.equalsIgnoreCase("diskread")) {
			cell5 = new PdfPCell(new Phrase("读请求数", fontChinese));
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			cell5 = new PdfPCell(new Phrase("写请求数", fontChinese));
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			cell5 = new PdfPCell(new Phrase("软件读错误数", fontChinese));
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			cell5 = new PdfPCell(new Phrase("软件写错误数", fontChinese));
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			cell5 = new PdfPCell(new Phrase("读KB", fontChinese));
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			cell5 = new PdfPCell(new Phrase("写KB", fontChinese));
		} else if (flag.equalsIgnoreCase("diskfree")) {
			cell5 = new PdfPCell(new Phrase("闲状态时间量", fontChinese));
		} else if (flag.equalsIgnoreCase("diskbus")) {
			cell5 = new PdfPCell(new Phrase("忙状态时间量", fontChinese));
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			cell5 = new PdfPCell(new Phrase("硬件读错误数", fontChinese));
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			cell5 = new PdfPCell(new Phrase("硬件写错误数", fontChinese));
		}
		PdfPCell cell6 = new PdfPCell(new Phrase(nowvalue, fontChinese));
		PdfPCell cell7 = new PdfPCell(new Phrase(avgvalue, fontChinese));
		PdfPCell cell8 = new PdfPCell(new Phrase(maxvalue, fontChinese));

		cell1.setBackgroundColor(Color.LIGHT_GRAY);
		cell2.setBackgroundColor(Color.LIGHT_GRAY);
		cell3.setBackgroundColor(Color.LIGHT_GRAY);
		cell4.setBackgroundColor(Color.LIGHT_GRAY);
		cell5.setBackgroundColor(Color.LIGHT_GRAY);
		cell6.setBackgroundColor(Color.LIGHT_GRAY);
		cell7.setBackgroundColor(Color.LIGHT_GRAY);
		cell8.setBackgroundColor(Color.LIGHT_GRAY);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		aTable.addCell(cell1);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		// 导出响应时间图片
		// document.add(new Paragraph("\n"));
		// String ipaddress = (String)request.getParameter("ipaddress");
		// String newip = doip(ipaddress);
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.close();
	}

	private String creatXlsEMC() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		String flag = request.getParameter("flag");
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
		// List memlist = (List) session.getAttribute("pinglist");
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Hashtable reporthash = new Hashtable();
		// reporthash.put("pinglist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);

		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String file = "";
		String nowvalue = request.getParameter("nowvalue");
		String avgvalue = request.getParameter("avgvalue");
		String maxvalue = request.getParameter("maxvalue");
		if (flag.equalsIgnoreCase("arraywt")) {
			file = "/temp/arraywt.xls";
		} else if (flag.equalsIgnoreCase("arrayavgwt")) {
			file = "/temp/arrayavgwt.xls";
		} else if (flag.equalsIgnoreCase("memwt")) {
			file = "/temp/memwt.xls";
		} else if (flag.equalsIgnoreCase("memavgwt")) {
			file = "/temp/memavgwt.xls";
		} else if (flag.equalsIgnoreCase("memtmp")) {
			file = "/temp/memtmp.xls";
		} else if (flag.equalsIgnoreCase("memavgtmp")) {
			file = "/temp/memavgtmp.xls";
		} else if (flag.equalsIgnoreCase("bakwt")) {
			file = "/temp/bakwt.xls";
		} else if (flag.equalsIgnoreCase("bakavgwt")) {
			file = "/temp/bakavgwt.xls";
		} else if (flag.equalsIgnoreCase("diskread")) {
			file = "/temp/diskread.xls";
		} else if (flag.equalsIgnoreCase("diskwrite")) {
			file = "/temp/memCR.xls";
		} else if (flag.equalsIgnoreCase("disksoftreaderror")) {
			file = "/temp/disksoftreaderror.xls";
		} else if (flag.equalsIgnoreCase("disksoftwriteerror")) {
			file = "/temp/disksoftwriteerror.xls";
		} else if (flag.equalsIgnoreCase("diskwritekb")) {
			file = "/temp/diskwritekb.xls";
		} else if (flag.equalsIgnoreCase("diskreadkb")) {
			file = "/temp/diskreadkb.xls";
		} else if (flag.equalsIgnoreCase("diskfree")) {
			file = "/temp/diskfree.xls";
		} else if (flag.equalsIgnoreCase("diskbus")) {
			file = "/temp/diskbus.PDF";
		} else if (flag.equalsIgnoreCase("diskhardwriteerror")) {
			file = "/temp/diskhardwriteerror.xls";
		} else if (flag.equalsIgnoreCase("diskhardreaderror")) {
			file = "/temp/diskhardreaderror.xls";
		} else if (flag.equalsIgnoreCase("lunharderror")) {
			file = "/temp/lunharderror.xls";
		} else if (flag.equalsIgnoreCase("lunsofterror")) {
			file = "/temp/lunsofterror.xls";
		} else if (flag.equalsIgnoreCase("lunlength")) {
			file = "/temp/lunlength.xls";
		}
		report.createReport_emc(file, flag, nowvalue, avgvalue, maxvalue);
		// request.getSession().removeAttribute("pingpath");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
		// return mapping.findForward("report_info");
	}

}
