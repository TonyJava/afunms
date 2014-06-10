package com.afunms.application.ajaxManager;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
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
import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.capreport.model.ReportValue;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.IIS;
import com.afunms.polling.node.Weblogic;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.temp.dao.WeblogicDao;
import com.lowagie.text.BadElementException;
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
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class MiddlewareReportAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	@Override
	public void execute(String action) {
		if (action.equals("getNodeListForPing")) {
			getNodeListForPing();
		} else if (action.equals("exportMiddlewarePingWord")) {
			exportMiddlewarePingWord();
		} else if (action.equals("exportMiddlewarePingExcel")) {
			exportMiddlewarePingExcel();
		} else if (action.equals("exportMiddlewarePingPdf")) {
			exportMiddlewarePingPdf();
		} else if (action.equals("getEventDetailList")) {
			getEventDetailList();
		} else if (action.equals("exportEventWord")) {
			exportEventWord();
		} else if (action.equals("exportEventExcel")) {
			exportEventExcel();
		} else if (action.equals("exportEventPdf")) {
			exportEventPdf();
		} else if (action.equals("exportMiddlewareReportForTomcat")) {
			exportMiddlewareReportForTomcat();
		} else if (action.equals("exportMiddlewareReportForIis")) {
			exportMiddlewareReportForIis();
		} else if (action.equals("exportMiddlewareReportForWeblogic")) {
			exportMiddlewareReportForWeblogic();
		} else if (action.equals("exportTomcatReportForType")) {
			exportTomcatReportForType();
		} else if (action.equals("exportIisReportForType")) {
			exportIisReportForType();
		} else if (action.equals("exportWeblogicReportForType")) {
			exportWeblogicReportForType();
		} else if (action.equals("getPingDetailForMiddleware")) {
			getPingDetailForMiddleware();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPingDetailForMiddleware() {
		TomcatManager tomcatManager = new TomcatManager();
		IISManager iisManager = new IISManager();
		WeblogicManager weblogiManager = new WeblogicManager();
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate;
		String totime = todate;
		
		String[] idstomcat = {};
		String[] idsiis = {};
		String[] idsweblogic = {};
		
		String tomcatIds = getParaValue("tomcatIds");
		String iisIds = getParaValue("iisIds");
		String weblogicIds = getParaValue("weblogicIds");
		
		if(tomcatIds != null && tomcatIds.length() > 0){
			idstomcat = tomcatIds.split(";");
		}
		if(iisIds != null && iisIds.length() > 0){
			idsiis = iisIds.split(";");
		}
		if(weblogicIds != null && weblogicIds.length() > 0){
			idsweblogic = weblogicIds.split(";");
		}
		
		// �������־ȡ���˿����¼�¼���б�
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		List allMiddelwareOrderList = new ArrayList();
		// tomcat=====================================================
		if (idstomcat != null && idstomcat.length > 0) {
			for (int i = 0; i < idstomcat.length; i++) {
				Node tomcatNode = PollingEngine.getInstance().getTomcatByID(Integer.parseInt(idstomcat[i]));
				Hashtable pinghash = new Hashtable();
				try {
					pinghash = tomcatManager.getCategory(tomcatNode.getIpAddress(), "TomcatPing", "ConnectUtilization",
						starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("tomcat", tomcatNode);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("ipaddress", tomcatNode.getIpAddress()+"("+tomcatNode.getAlias()+")");
				orderList.add(ipmemhash);
				allMiddelwareOrderList.add(ipmemhash);
			}
		}
		// iis
		List orderListiis = new ArrayList();
		if (idsiis != null && idsiis.length > 0) {
			for (int i = 0; i < idsiis.length; i++) {

				Node iisNode = PollingEngine.getInstance().getIisByID(Integer.parseInt(idsiis[i]));
				Hashtable pinghashiis = new Hashtable();
				try {
					pinghashiis = iisManager.getCategory(iisNode.getIpAddress(), "IISPing", "ConnectUtilization",
						starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhashiis = new Hashtable();
				ipmemhashiis.put("iis", iisNode);
				ipmemhashiis.put("pinghashiis", pinghashiis);
				ipmemhashiis.put("ipaddress", iisNode.getIpAddress()+"("+iisNode.getAlias()+")");
				orderListiis.add(ipmemhashiis);
				allMiddelwareOrderList.add(ipmemhashiis);
			}
		}
		// -----------------end------------iis
		// weblogic=============================================
		List orderListweblogic = new ArrayList();
		if (idsweblogic != null && idsweblogic.length > 0) {
			for (int i = 0; i < idsweblogic.length; i++) {
				Node weblogicNode = PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(idsweblogic[i]));
				Hashtable pinghashweblogic = new Hashtable();
				try {
					pinghashweblogic = weblogiManager.getCategory(weblogicNode.getIpAddress(), "WeblogicPing",
						"ConnectUtilization", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhashweblogic = new Hashtable();
				ipmemhashweblogic.put("weblogic", weblogicNode);
				ipmemhashweblogic.put("pinghashweblogic", pinghashweblogic);
				ipmemhashweblogic.put("ipaddress", weblogicNode.getIpAddress()+"("+weblogicNode.getAlias()+")");
				orderListweblogic.add(ipmemhashweblogic);
				allMiddelwareOrderList.add(ipmemhashweblogic);
			}
		}
		// ===================end weblogic
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������
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
		// iis================================
		List returnListiis = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnListiis = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������
			// **********************************************************
			List pinglist = orderListiis;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					Node node = (Node) _pinghash.get("iis");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable pinghashiis = (Hashtable) _pinghash.get("pinghashiis");
					if (pinghashiis == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					String pingconavg = "";
					String downnum = "";
					if (pinghashiis.get("avgpingcon") != null)
						pingconavg = (String) pinghashiis.get("avgpingcon");
					if (pinghashiis.get("downnum") != null)
						downnum = (String) pinghashiis.get("downnum");
					List ipdiskListiis = new ArrayList();
					ipdiskListiis.add(ip);
					ipdiskListiis.add(equname);
					ipdiskListiis.add(node.getType());
					ipdiskListiis.add(pingconavg);
					ipdiskListiis.add(downnum);
					returnListiis.add(ipdiskListiis);
				}
			}
		}
		// iis---------------------end
		// weblogic================================
		List returnListweblogic = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnListweblogic = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������
			// **********************************************************
			List pinglist = orderListweblogic;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					Node node = (Node) _pinghash.get("weblogic");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable pinghashiis = (Hashtable) _pinghash.get("pinghashweblogic");
					if (pinghashiis == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					String pingconavg = "";
					String downnum = "";
					if (pinghashiis.get("avgpingcon") != null)
						pingconavg = (String) pinghashiis.get("avgpingcon");
					if (pinghashiis.get("downnum") != null)
						downnum = (String) pinghashiis.get("downnum");
					List ipdiskListweblogic = new ArrayList();
					ipdiskListweblogic.add(ip);
					ipdiskListweblogic.add(equname);
					ipdiskListweblogic.add(node.getType());
					ipdiskListweblogic.add(pingconavg);
					ipdiskListweblogic.add(downnum);
					returnListweblogic.add(ipdiskListweblogic);
				}
			}
		}
		// weblogic---------------------end
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
				// �õ�������Subentity���б�
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		// iis-===================================
		List listiis = new ArrayList();
		if (returnListiis != null && returnListiis.size() > 0) {
			for (int m = 0; m < returnListiis.size(); m++) {
				List ipdiskList = (List) returnListiis.get(m);
				for (int n = m + 1; n < returnListiis.size(); n++) {

					List _ipdiskList = (List) returnListiis.get(n);
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
							returnListiis.remove(m);
							returnListiis.add(m, _ipdiskList);
							returnListiis.remove(n);
							returnListiis.add(n, ipdiskList);
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
							returnListiis.remove(m);
							returnListiis.add(m, _ipdiskList);
							returnListiis.remove(n);
							returnListiis.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				listiis.add(ipdiskList);
				ipdiskList = null;
			}
		}
		// weblogic===================================
		List listweblogic = new ArrayList();
		if (returnListweblogic != null && returnListweblogic.size() > 0) {
			for (int m = 0; m < returnListweblogic.size(); m++) {
				List ipdiskList = (List) returnListweblogic.get(m);
				for (int n = m + 1; n < returnListweblogic.size(); n++) {

					List _ipdiskList = (List) returnListweblogic.get(n);
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
							returnListweblogic.remove(m);
							returnListweblogic.add(m, _ipdiskList);
							returnListweblogic.remove(n);
							returnListweblogic.add(n, ipdiskList);
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
							returnListweblogic.remove(m);
							returnListweblogic.add(m, _ipdiskList);
							returnListweblogic.remove(n);
							returnListweblogic.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				listweblogic.add(ipdiskList);
				ipdiskList = null;
			}
		}
		//
		String pingChartDivStr =  ReportHelper.getMiddelChartDivStr(allMiddelwareOrderList, "ping");
		ReportValue pingReportValue =  ReportHelper.getMiddleReportValue(allMiddelwareOrderList,"ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "��ͨ��", "ʱ��", "");
		request.setAttribute("pingChartDivStr", pingChartDivStr);
		session.setAttribute("pingpath", pingpath);
		
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		
		session.setAttribute("pinglist", list);
		session.setAttribute("pinglistiis", listiis);
		session.setAttribute("pinglistweblogic", listweblogic);
		StringBuffer jsonString = new StringBuffer("[{");
		jsonString.append("pic:[");
		jsonString.append("{\"pingChartDivStr\":\"");
		jsonString.append(pingChartDivStr);
		jsonString.append("\"}]");
		StringBuffer jsonStringNode = new StringBuffer("node:[{Rows:[");
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				List _pinglist = (List) list.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);	
				String avgping = (String)_pinglist.get(3);
        		String downnum = (String)_pinglist.get(4);

				jsonStringNode.append("{\"nodeid\":\"");
				jsonStringNode.append("");
				jsonStringNode.append("\",");

				jsonStringNode.append("\"ipaddress\":\"");
				jsonStringNode.append(ip);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"name\":\"");
				jsonStringNode.append(equname);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"avg\":\"");
				jsonStringNode.append(avgping);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"downnum\":\"");
				jsonStringNode.append(downnum);
				jsonStringNode.append("\"}");

				if(listiis.size() > 0 || listweblogic.size() > 0){
					jsonStringNode.append(",");
				}else{
					if(i != list.size() - 1){
						jsonStringNode.append(",");
					}
				}
			}
		}
		
		if (listiis != null && listiis.size() > 0) {
			for (int i = 0; i < listiis.size(); i++) {
				List _pinglist = (List) listiis.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);	
				String avgping = (String)_pinglist.get(3);
        		String downnum = (String)_pinglist.get(4);

				jsonStringNode.append("{\"nodeid\":\"");
				jsonStringNode.append("");
				jsonStringNode.append("\",");

				jsonStringNode.append("\"ipaddress\":\"");
				jsonStringNode.append(ip);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"name\":\"");
				jsonStringNode.append(equname);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"avg\":\"");
				jsonStringNode.append(avgping);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"downnum\":\"");
				jsonStringNode.append(downnum);
				jsonStringNode.append("\"}");
				
				if(listweblogic.size() > 0){
					jsonStringNode.append(",");
				}else{
					if(i != listiis.size() - 1){
						jsonStringNode.append(",");
					}
				}

				
			}
		}
		
		if (listweblogic != null && listweblogic.size() > 0) {
			for (int i = 0; i < listweblogic.size(); i++) {
				List _pinglist = (List) listweblogic.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);	
				String avgping = (String)_pinglist.get(3);
        		String downnum = (String)_pinglist.get(4);

				jsonStringNode.append("{\"nodeid\":\"");
				jsonStringNode.append("");
				jsonStringNode.append("\",");

				jsonStringNode.append("\"ipaddress\":\"");
				jsonStringNode.append(ip);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"name\":\"");
				jsonStringNode.append(equname);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"avg\":\"");
				jsonStringNode.append(avgping);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"downnum\":\"");
				jsonStringNode.append(downnum);
				jsonStringNode.append("\"}");
				
				if(i != listweblogic.size() - 1){
					jsonStringNode.append(",");
				}
			}
		}
		jsonStringNode.append("]}]");
		jsonString.append(",");
		jsonString.append(jsonStringNode);
		jsonString.append("}]");
		// jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private void exportMysqlReportForAnalyse() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		int downnum = 0;
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// ��ǰ��ͨ��
		String pingmin = "0.0";// ��С��ͨ��
		String pingmax = "0.0";// �����ͨ��
		String runstr = "����ֹͣ";
		Hashtable dbValue = new Hashtable();
		// ���ݿ����еȼ�=====================
		String grade = "��";
		Hashtable mems = new Hashtable();// �ڴ���Ϣ
		Hashtable sysValue = new Hashtable();
		Hashtable spaceInfo = new Hashtable();
		Hashtable conn = new Hashtable();
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
		int count = 0;
		Vector Val = new Vector();
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		Hashtable tablesHash = new Hashtable();
		Vector tableinfo_v = new Vector();
		List eventList = new ArrayList();// �¼��б�
		String fileName = "";
		try {
			ip = getParaValue("ipaddress");
			DBDao dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 4).get(0);
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(ip);
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable ipData = dao.getMysqlDataByServerip(serverip);
			if (dao != null) {
				dao.close();
			}
			if (ipData != null && ipData.size() > 0) {
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (doneFlag == 0) {
								// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
								if (returnValue.containsKey("configVal")) {
									doneFlag = 1;
								}
								if (returnValue.containsKey("Val")) {
									Val = (Vector) returnValue.get("Val");
								}
							}
							if (returnValue.containsKey("sessionsDetail")) {
								// �������ݿ�������Ϣ
								sessionlist.add((List) returnValue
										.get("sessionsDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// �������ݿ����Ϣ
								tablesHash.put(dbStr,
										(List) returnValue.get("tablesDetail"));
							}
							if (returnValue.containsKey("global_status")) {
								// �������ݿ����Ϣ
								tableinfo_v = (Vector) returnValue
										.get("global_status");
							}
						}
					}
				}

				runstr = (String) ipData.get("runningflag");
				if (runstr != null && runstr.contains("����ֹͣ")) {// ��<font
					runstr = "����ֹͣ";
				}
				if (runstr != null && runstr.contains("��������")) {
					pingnow = "100";
				}
			}
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager
						.getCategory(vo.getId() + "", "MYPing",
								"ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// ƽ����ͨ��
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = Integer.parseInt((String) ConnectUtilizationhash
						.get("downnum"));
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// �����ͨ��
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// �����ͨ��
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();
			p_draw_line(ConnectUtilizationhash, "��ͨ��", newip
					+ "ConnectUtilization", 740, 150);// ��ͼ
			// �õ����еȼ�
			DBTypeDao dbTypeDao = new DBTypeDao();
			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}
			// alex
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '"
					+ starttime + "' " + "and recordtime<='" + totime + "' ");
			s.append(" and nodeid=" + vo.getId());

			EventListDao eventdao1 = new EventListDao();
			List infolist = eventdao1.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						downnum = downnum + 1;
					}
				}
			}
			if (count > 0 && count <= 3) {

				grade = "��";
			}
			if (downnum > 0 || count > 3) {
				grade = "��";
			}
			// �¼��б�
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			try {
				User user = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db",
							status + "", level1 + "", user.getBusinessids(),
							vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Hashtable reporthash = new Hashtable();
			Hashtable maxping = new Hashtable();
			maxping.put("pingmax", pingmin + "%");// ��С��ͨ��
			maxping.put("pingnow", pingnow + "%");
			maxping.put("avgpingcon", avgpingcon + "%");// ƽ����ͨ��
			reporthash.put("list", eventList);
			reporthash.put("pingmin", pingmin);
			reporthash.put("pingnow", pingnow);
			reporthash.put("pingmax", pingmax);
			reporthash.put("pingconavg", avgpingcon + "");
			reporthash.put("tablesHash", tablesHash);
			reporthash.put("sessionlist", sessionlist);
			reporthash.put("Val", Val);
			reporthash.put("downnum", downnum);
			reporthash.put("count", count + "");
			reporthash.put("grade", grade);
			reporthash.put("vo", vo);
			reporthash.put("runstr", runstr);
			reporthash.put("typevo", typevo);
			reporthash.put("dbValue", dbValue);
			reporthash.put("typename", typevo.getDbtype());
			reporthash.put("hostnamestr", vo.getDbName());
			reporthash.put("tableinfo_v", tableinfo_v);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			reporthash.put("ping", maxping);
			reporthash.put("dbname",
					typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
			reporthash.put("ip", vo.getIpAddress());

			String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
			if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				try {
					String file = "temp/dbmysql_reportcheck.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_mysqlNewDoc(fileName, "doc");// wordҵ�������
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				try {
					String file = "temp/dbmysql_reportcheck.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_mysqlNewDoc(fileName, "pdf");// wordҵ�������
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void exportWeblogicReportForType(){
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String typename = "WEBLOGIC";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		WeblogicManager weblogicManager = new WeblogicManager();
		String fileName = "";
		ip = getParaValue("ipaddress");
		try {
			
			String newip = SysUtil.doip(ip);
			// zhushouzhi----------------
			Hashtable ConnectUtilizationhash = weblogicManager.getCategory(ip, "WeblogicPing", "ConnectUtilization", starttime, totime);
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
			// ��ping�õ������ݼӽ�ȥ
			reporthash.put("ping", maxping);
			Hashtable hdata = (Hashtable) sharedata.get(ip);
			if (hdata == null)
				hdata = new Hashtable();
			// downum
			String downnum = (String) ConnectUtilizationhash.get("downnum");
			String grade = "��";
			if (!"0".equals(downnum)) {
				grade = "��";
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
			equipname = weblogicconf.getAlias() + "(" + ip + ")";
			equipnameDoc = weblogicconf.getAlias();
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
				// �ɼ�������Ǽ���ģʽ
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
			WeblogicNormal normalvalue = new WeblogicNormal();
			if (normalValue != null && normalValue.size() >0) {
				normalvalue = (WeblogicNormal) normalValue.get(0);
			}
			reporthash.put("weblogicnmphash", weblogicnmphash);
			reporthash.put("grade", grade);
			reporthash.put("weblogicnow", weblogicnow);
			reporthash.put("downnum", downnum);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("ip", ip);
			//reporthash.put("weblogic", weblogic);
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			reporthash.put("weblogicconf", weblogicconf);
			reporthash.put("normalvalue", normalvalue);
			// ��ͼ----------------------
			p_draw_line(ConnectUtilizationhash, "��ͨ��", newip + "WeblogicPing", 740, 150);
			// ��ͼ-----------------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
			if ("0".equals(str)) {
				report.createReport_weblogic("temp/weblogicnms_report.xls");// excel�ۺϱ���
				fileName = report.getFileName();
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnms_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_weblogicDoc(fileName, "doc");// word�ۺϱ���
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnmsnewdoc_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_weblogicNewDoc(fileName, "doc");// wordҵ�������
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnms_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��

					report1.createReport_weblogicNewDoc(fileName, "pdf");// pdfҵ�������
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/weblogicnms_report1.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_weblogicDoc(fileName, "pdf");// pdf�ۺϱ���
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportIisReportForType() {

		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String typename = "IIS";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		IISManager iisManager = new IISManager();
		String fileName = "";
		try {
			ip = getParaValue("ipaddress");
			IISConfigDao iisDao = null;
			IIS iis = null;
			int id = 0;
			try {
				iisDao = new IISConfigDao();
				id = iisDao.getidByIp(ip);
				iis = (com.afunms.polling.node.IIS) PollingEngine.getInstance().getIisByID(id);
			} catch (Exception e) {
			} finally {
				iisDao.close();
			}
			equipname = iis.getAlias() + "(" + ip + ")";
			equipnameDoc = iis.getAlias();

			String newip = SysUtil.doip(ip);
			// zhushouzhi----------------
			Hashtable ConnectUtilizationhash = iisManager.getCategory(ip, "IISPing", "ConnectUtilization", starttime,
				totime);
			String pingconavg = "";
			List list = (List) ConnectUtilizationhash.get("list");
			Vector vector = (Vector) list.get(list.size() - 1);
			String iisnow = "0.0";
			iisnow = (String) vector.get(0);
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
			// jvm------------tomcat
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			// ��ping�õ������ݼӽ�ȥ
			reporthash.put("ping", maxping);
			Hashtable hdata = (Hashtable) sharedata.get(ip);
			if (hdata == null)
				hdata = new Hashtable();
			// downum
			String downnum = (String) ConnectUtilizationhash.get("downnum");
			String grade = "��";
			if (!"0".equals(downnum)) {
				grade = "��";
			}
			reporthash.put("grade", grade);
			reporthash.put("iisnow", iisnow);
			reporthash.put("downnum", downnum);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("ip", ip);
			reporthash.put("iis", iis);
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// ��ͼ----------------------
			p_draw_line(ConnectUtilizationhash, "��ͨ��", newip + "IISPing", 740, 150);
			// ��ͼ-----------------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
			if ("0".equals(str)) {
				report.createReport_iis("temp/iisnms_report.xls");// excel�ۺϱ���
				fileName = report.getFileName();
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/iisnms_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_iisDoc(fileName);// word�ۺϱ���
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/iisnmsnewdoc_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_iisNewDoc(fileName, "doc");// wordҵ�������
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/iisnms_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_iisNewDoc(fileName, "pdf");// pdfҵ�������
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/iisnms_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_iisPDF(fileName);// pdf�ۺϱ���
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportTomcatReportForType() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String typename = "TOMCAT";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		TomcatManager tomcatManager = new TomcatManager();
		String fileName = "";
		try {
			ip = getParaValue("ipaddress");
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
			String newip = SysUtil.doip(ip);
			// zhushouzhi----------------
			Hashtable ConnectUtilizationhash = tomcatManager.getCategory(ip, "TomcatPing", "ConnectUtilization",
				starttime, totime);
			Hashtable hash = tomcatManager.getCategory(ip, "tomcat_jvm", "jvm_utilization", starttime, totime);
			
			List listp = (List) ConnectUtilizationhash.get("list");
			String tomcatnow = "0.0";
			if(listp != null && listp.size() > 0){
				Vector vector1 = (Vector) listp.get(listp.size() - 1);
				tomcatnow = (String) vector1.get(0);
			}
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
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			// ��ping�õ������ݼӽ�ȥ
			reporthash.put("ping", maxping);
			// ���jvm����
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
			String grade = "��";
			if (!"0".equals(downnum)) {
				grade = "��";
			}
			reporthash.put("grade", grade);
			reporthash.put("downnum", downnum);
			reporthash.put("equipname", equipname);
			reporthash.put("equipnameDoc", equipnameDoc);
			reporthash.put("ip", ip);
			reporthash.put("tomcat", tomcat);
			reporthash.put("typename", typename);
			reporthash.put("startdate", startdate);
			// ��ͼ----------------------
			p_draw_line(ConnectUtilizationhash, "��ͨ��", newip + "TomcatPing", 576, 170);
			p_draw_line(hash, "JVM�ڴ�������", newip + "tomcat_jvm", 576, 170);
			// ��ͼ-----------------------------
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
			if ("0".equals(str)) {
				report.createReport_tomcat("temp/tomcatnms_report.xls");// excel�ۺϱ���
				fileName = report.getFileName();
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/tomcatnms_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_tomcatDoc(fileName, "doc");// word�ۺϱ���
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/tomcatnewdoc_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_tomcatNewDoc(fileName, "doc");// wordҵ�������
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/tomcatnms_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_tomcatNewDoc(fileName, "pdf");// pdfҵ�������
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/iisnms_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_tomcatDoc(fileName, "pdf");// pdf�ۺϱ���
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		
		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked", "static-access" })
	private void exportDatabaseReportForMysql() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
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
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String dbname = "";
		String fileName = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Vector vector = new Vector();
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// ��ǰ��ͨ��
		String pingmin = "0.0";// ��С��ͨ��
		String pingmax = "0.0";// �����ͨ��
		String runstr = "����ֹͣ";
		String downnum = "";
		// ���ݿ����еȼ�=====================
		String grade = "��";
		Hashtable mems = new Hashtable();// �ڴ���Ϣ
		Hashtable sysValue = new Hashtable();
		List eventList = new ArrayList();// �¼��б�
		Hashtable spaceInfo = new Hashtable();
		// ���ݿ����еȼ�=====================
		Hashtable conn = new Hashtable();// ������Ϣ
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
		int doneFlag = 0;
		int count = 0;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					DBTypeDao typedao = new DBTypeDao();
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype()
								+ "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";
					String newip = SysUtil.doip(ip);
					Hashtable reporthash = new Hashtable();
					Vector Val = new Vector();
					List sessionlist = new ArrayList();
					Hashtable tablesHash = new Hashtable();
					Vector tableinfo_v = new Vector();
					Hashtable dbValue = new Hashtable();
					IpTranslation tranfer = new IpTranslation();
					String hex = tranfer.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + vo.getId();
					Hashtable ipData = dao.getMysqlDataByServerip(serverip);
					if (dao != null) {
						dao.close();
					}
					if (ipData != null && ipData.size() > 0) {
						String dbnames = vo.getDbName();
						String[] dbs = dbnames.split(",");
						for (int k = 0; k < dbs.length; k++) {
							// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
							String dbStr = dbs[k];
							if (ipData.containsKey(dbStr)) {
								Hashtable returnValue = new Hashtable();
								returnValue = (Hashtable) ipData.get(dbStr);
								if (returnValue != null
										&& returnValue.size() > 0) {
									if (doneFlag == 0) {
										// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
										if (returnValue
												.containsKey("configVal")) {
											doneFlag = 1;
										}
										if (returnValue.containsKey("Val")) {
											Val = (Vector) returnValue
													.get("Val");
										}
									}
									if (returnValue
											.containsKey("sessionsDetail")) {
										// �������ݿ�������Ϣ
										sessionlist.add((List) returnValue
												.get("sessionsDetail"));
									}
									if (returnValue.containsKey("tablesDetail")) {
										// �������ݿ����Ϣ
										tablesHash.put(dbStr,
												(List) returnValue
														.get("tablesDetail"));
									}
									if (returnValue.containsKey("tablesDetail")) {
										// �������ݿ����Ϣ
										tableinfo_v = (Vector) returnValue
												.get("variables");
									}
								}
							}
						}
						runstr = (String) ipData.get("runningflag");
						if (runstr != null && runstr.contains("����ֹͣ")) {// ��<font
							// color=red>����ֹͣ</font>
							// �滻
							runstr = "����ֹͣ";
						}
						if (runstr != null && runstr.contains("��������")) {
							pingnow = "100";
						}
					}
					Hashtable ConnectUtilizationhash = new Hashtable();
					I_HostCollectData hostmanager = new HostCollectDataManager();
					try {
						ConnectUtilizationhash = hostmanager.getCategory(
								vo.getId() + "", "MYPing",
								"ConnectUtilization", starttime, totime);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String pingconavg = "0";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash
								.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// ƽ����ͨ��
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash
								.get("downnum");
					}
					if (ConnectUtilizationhash.get("pingMax") != null) {
						pingmax = (String) ConnectUtilizationhash
								.get("pingMax");// �����ͨ��
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash
								.get("pingmax");// �����ͨ��
					}
					avgpingcon = new Double(pingconavg + "").doubleValue();
					p_draw_line(ConnectUtilizationhash, "��ͨ��", newip
							+ "ConnectUtilization", 740, 150);// ��ͼ
					// �õ����еȼ�
					DBTypeDao dbTypeDao = new DBTypeDao();
					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}
					if (count > 0) {
						grade = "��";
					}
					if (!"0".equals(downnum)) {
						grade = "��";
					}
					// �¼��б�
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					try {
						User user = (User) session
								.getAttribute(SessionConstant.CURRENT_USER); // �û�����
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime,
									"db", status + "", level1 + "",
									user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Hashtable maxping = new Hashtable();
					maxping.put("pingmax", pingmin + "%");// ��С��ͨ��
					maxping.put("pingnow", pingnow + "%");
					maxping.put("avgpingcon", avgpingcon + "%");// ƽ����ͨ��
					reporthash.put("list", eventList);
					reporthash.put("pingmin", pingmin);
					reporthash.put("pingnow", pingnow);
					reporthash.put("pingmax", pingmax);
					reporthash.put("pingconavg", avgpingcon + "");
					reporthash.put("tablesHash", tablesHash);
					reporthash.put("sessionlist", sessionlist);
					reporthash.put("Val", Val);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count);
					reporthash.put("grade", grade);
					reporthash.put("vo", vo);
					reporthash.put("runstr", runstr);
					reporthash.put("typevo", typevo);
					reporthash.put("dbValue", dbValue);
					reporthash.put("typename", typevo.getDbtype());
					reporthash.put("hostnamestr", vo.getDbName());
					reporthash.put("tableinfo_v", tableinfo_v);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("ping", maxping);
					reporthash.put("dbname",
							typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());
					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
						allreporthash);
				report.createReport_mysqlall("/temp/mysqlall_report.xls");
				fileName = report.getFileName();
			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();

	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void exportMiddlewareReportForWeblogic(){
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
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
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String typename = "WEBLOGIC";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable sharedata = ShareData.getSharedata();
		
		WeblogicManager weblogicManager = new WeblogicManager();
		String fileName = "";
		Hashtable allreporthash = new Hashtable();
		try {
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					int id = 0;
					id = ids[i];
					Weblogic weblogic = null;
					try {
						weblogic = (com.afunms.polling.node.Weblogic) PollingEngine.getInstance().getWeblogicByID(id);
						ip = weblogic.getIpAddress();
					} catch (Exception e) {
						SysLogger.error("", e);
					}
					equipname = weblogic.getAlias() + "(" + ip + ")";
					equipnameDoc = weblogic.getAlias();

					String newip = SysUtil.doip(ip);
					Hashtable ConnectUtilizationhash = weblogicManager.getCategory(ip, "WeblogicPing", "ConnectUtilization", starttime, totime);
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
					// ��ping�õ������ݼӽ�ȥ
					reporthash.put("ping", maxping);
					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null)
						hdata = new Hashtable();
					// downum
					String downnum = (String) ConnectUtilizationhash.get("downnum");
					String grade = "��";
					if (!"0".equals(downnum)) {
						grade = "��";
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
						// �ɼ�������Ǽ���ģʽ
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
					WeblogicNormal normalvalue = new WeblogicNormal();;
					if (normalValue != null && normalValue.size() > 0) {
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
					p_draw_line(ConnectUtilizationhash, "��ͨ��", newip + "WeblogicPing", 740, 150);
					allreporthash.put(ip, reporthash);
				}
			}
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			report.createReport_weblogicAll("temp/weblogicnms_report.xls");// excel�ۺϱ���
			fileName = report.getFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void exportMiddlewareReportForIis(){
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
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
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String typename = "IIS";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		IISManager iisManager = new IISManager();
		String fileName = "";
		Hashtable allreporthash = new Hashtable();
		try {
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					int id = 0;
					id = ids[i];
					IISConfigDao iisDao = null;
					IIS iis = null;
					try {
						iisDao = new IISConfigDao();
						iis = (com.afunms.polling.node.IIS) PollingEngine.getInstance().getIisByID(id);
						ip = iis.getIpAddress();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						iisDao.close();
					}
					equipname = iis.getAlias() + "(" + ip + ")";
					equipnameDoc = iis.getAlias();

					String newip = SysUtil.doip(ip);
					Hashtable ConnectUtilizationhash = iisManager.getCategory(ip, "IISPing", "ConnectUtilization",
						starttime, totime);
					String pingconavg = "";
					List list = (List) ConnectUtilizationhash.get("list");
					Vector vector = (Vector) list.get(list.size() - 1);
					String iisnow = "0.0";
					iisnow = (String) vector.get(0);
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					String jvmconavg = "";
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					reporthash.put("ping", maxping);

					Hashtable hdata = (Hashtable) sharedata.get(ip);
					if (hdata == null)
						hdata = new Hashtable();
					// downum
					String downnum = (String) ConnectUtilizationhash.get("downnum");
					String grade = "��";
					if (!"0".equals(downnum)) {
						grade = "��";
					}
					reporthash.put("grade", grade);
					reporthash.put("iisnow", iisnow);
					reporthash.put("downnum", downnum);
					reporthash.put("equipname", equipname);
					reporthash.put("equipnameDoc", equipnameDoc);
					reporthash.put("ip", ip);
					reporthash.put("iis", iis);
					reporthash.put("typename", typename);
					reporthash.put("startdate", startdate);
					// ��ͼ----------------------
					String timeType = "minute";
					PollMonitorManager pollMonitorManager = new PollMonitorManager();
					p_draw_line(ConnectUtilizationhash, "��ͨ��", newip + "IISPing", 740, 150);
				}
			}
			allreporthash.put(ip, reporthash);
			// ��ͼ-----------------------------
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			report.createReport_iisAll("temp/iisnms_report.xls");// excel�ۺϱ���
			fileName = report.getFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportMiddlewareReportForTomcat() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
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
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String typename = "TOMCAT";
		String equipname = "";
		String equipnameDoc = "";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable maxjvm = new Hashtable();// jnm--max
		Hashtable sharedata = ShareData.getSharedata();
		Hashtable reporthash = new Hashtable();
		TomcatManager tomcatManager = new TomcatManager();
		String fileName = "";
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					int id = 0;
					id = ids[i];
					com.afunms.polling.node.Tomcat node = null;
					try {
						node = (com.afunms.polling.node.Tomcat) PollingEngine.getInstance().getTomcatByID(id);
						ip = node.getIpAddress();
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						// tomcatdao.close();
					}
					equipname = node.getAlias() + "(" + ip + ")";
					equipnameDoc = node.getAlias();
					String newip = SysUtil.doip(ip);
					Hashtable ConnectUtilizationhash = tomcatManager.getCategory(ip, "TomcatPing",
						"ConnectUtilization", starttime, totime);
					Hashtable hash = tomcatManager.getCategory(ip, "tomcat_jvm", "jvm_utilization", starttime, totime);
					String tomcatnow = "0.0";
					List listp = (List) ConnectUtilizationhash.get("list");
					if(listp != null && listp.size() > 0){
						Vector vector1 = (Vector) listp.get(listp.size() - 1);
						tomcatnow = (String) vector1.get(0);
					}
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
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					// ��ping�õ������ݼӽ�ȥ
					reporthash.put("ping", maxping);
					// ���jvm����
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
					String grade = "��";
					if (!"0".equals(downnum)) {
						grade = "��";
					}
					reporthash.put("grade", grade);
					reporthash.put("downnum", downnum);
					reporthash.put("equipname", equipname);
					reporthash.put("equipnameDoc", equipnameDoc);
					reporthash.put("ip", ip);
					reporthash.put("tomcat", tomcat);
					reporthash.put("typename", typename);
					reporthash.put("startdate", startdate);
					// ��ͼ----------------------
					p_draw_line(hash, "JVM�ڴ�������", newip + "tomcat_jvm", 576, 170);
					allreporthash.put(ip, reporthash);
				}
				// ��ͼ-----------------------------
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_tomcatAll("temp/tomcatnms_report.xls");// excel�ۺϱ���
				fileName = report.getFileName();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		
		out.print(fileName);
		out.flush();
	}

	private void exportEventPdf() {
		String file = "/temp/middlewareEventReport.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createMiddlewarePDFContext(fileName,"�¼�");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportEventExcel() {
		String starttime = (String)session.getAttribute("starttime");
		String totime = (String)session.getAttribute("totime");
		Hashtable reporthash = new Hashtable();
		List pinglist = (List) session.getAttribute("eventlist");
		List pinglistiis = (List) session.getAttribute("eventlistiis");
		List pinglistweblogic = (List) session.getAttribute("eventlistweblogic");
		String pingpath = (String) session.getAttribute("pingpath");
		reporthash.put("pinglist", pinglist);
		reporthash.put("pinglistiis", pinglistiis);
		reporthash.put("pinglistweblogic", pinglistweblogic);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_midping("temp/middlewareEventReport.xls","�¼�");// excel�ۺϱ���

		out.print(report.getFileName());
		out.flush();
	}

	private void exportEventWord() {
		String file = "/temp/middlewareEventReport.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createMiddlewareDocContext(fileName,"�¼�");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getEventDetailList() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate;
		String totime = todate;

		String[] idstomcat = {};
		String[] idsiis = {};
		String[] idsweblogic = {};
		String tomcatIds = getParaValue("tomcatIds");
		String iisIds = getParaValue("iisIds");
		String weblogicIds = getParaValue("weblogicIds");
		if(tomcatIds != null && tomcatIds.length() > 0){
			idstomcat = tomcatIds.split(";");
		}
		if(iisIds != null && iisIds.length() > 0){
			idsiis = iisIds.split(";");
		}
		if(weblogicIds != null && weblogicIds.length() > 0){
			idsweblogic = weblogicIds.split(";");
		}
		// �������־ȡ���˿����¼�¼���б�
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}
		List orderList = new ArrayList();
		List allMiddelwareOrderList = new ArrayList();
		
		TomcatManager tomcatManager = new TomcatManager();
		IISManager iisManager = new IISManager();
		WeblogicManager weblogiManager = new WeblogicManager();
		// tomcat=====================================================
		if (idstomcat != null && idstomcat.length > 0) {
			for (int i = 0; i < idstomcat.length; i++) {

				Node tomcatNode = PollingEngine.getInstance().getTomcatByID(Integer.parseInt(idstomcat[i]));
				Hashtable pinghash = new Hashtable();
				try {
					pinghash = tomcatManager.getCategory(tomcatNode.getIpAddress(), "TomcatPing", "ConnectUtilization",
						starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("tomcat", tomcatNode);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("ipaddress", tomcatNode.getIpAddress()+"("+tomcatNode.getAlias()+")");
				orderList.add(ipmemhash);
				allMiddelwareOrderList.add(ipmemhash);
			}
		}
		// iis
		List orderListiis = new ArrayList();
		if (idsiis != null && idsiis.length > 0) {
			for (int i = 0; i < idsiis.length; i++) {

				Node iisNode = PollingEngine.getInstance().getIisByID(Integer.parseInt(idsiis[i]));
				Hashtable pinghashiis = new Hashtable();
				try {
					pinghashiis = iisManager.getCategory(iisNode.getIpAddress(), "IISPing", "ConnectUtilization",
						starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhashiis = new Hashtable();
				ipmemhashiis.put("iis", iisNode);
				ipmemhashiis.put("pinghashiis", pinghashiis);
				ipmemhashiis.put("ipaddress", iisNode.getIpAddress()+"("+iisNode.getAlias()+")");
				orderListiis.add(ipmemhashiis);
				allMiddelwareOrderList.add(ipmemhashiis);
			}
		}
		// -----------------end------------iis
		// weblogic=============================================
		List orderListweblogic = new ArrayList();
		if (idsweblogic != null && idsweblogic.length > 0) {
			for (int i = 0; i < idsweblogic.length; i++) {

				Node weblogicNode = PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(idsweblogic[i]));
				Hashtable pinghashweblogic = new Hashtable();
				try {
					pinghashweblogic = weblogiManager.getCategory(weblogicNode.getIpAddress(), "WeblogicPing",
						"ConnectUtilization", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhashweblogic = new Hashtable();
				ipmemhashweblogic.put("weblogic", weblogicNode);
				ipmemhashweblogic.put("pinghashweblogic", pinghashweblogic);
				ipmemhashweblogic.put("ipaddress", weblogicNode.getIpAddress()+"("+weblogicNode.getAlias()+")");
				orderListweblogic.add(ipmemhashweblogic);
				allMiddelwareOrderList.add(ipmemhashweblogic);
			}
		}
		// ===================end weblogic
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������

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
		// iis================================
		List returnListiis = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnListiis = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������

			// **********************************************************
			List pinglist = orderListiis;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					Node node = (Node) _pinghash.get("iis");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable pinghashiis = (Hashtable) _pinghash.get("pinghashiis");
					if (pinghashiis == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();

					String pingconavg = "";
					String downnum = "";
					if (pinghashiis.get("avgpingcon") != null)
						pingconavg = (String) pinghashiis.get("avgpingcon");
					if (pinghashiis.get("downnum") != null)
						downnum = (String) pinghashiis.get("downnum");
					List ipdiskListiis = new ArrayList();
					ipdiskListiis.add(ip);
					ipdiskListiis.add(equname);
					ipdiskListiis.add(node.getType());
					ipdiskListiis.add(pingconavg);
					ipdiskListiis.add(downnum);
					returnListiis.add(ipdiskListiis);
				}
			}
		}
		// iis---------------------end
		// weblogic================================
		List returnListweblogic = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnListweblogic = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������

			// **********************************************************
			List pinglist = orderListweblogic;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					Node node = (Node) _pinghash.get("weblogic");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable pinghashiis = (Hashtable) _pinghash.get("pinghashweblogic");
					if (pinghashiis == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();

					String pingconavg = "";
					String downnum = "";
					if (pinghashiis.get("avgpingcon") != null)
						pingconavg = (String) pinghashiis.get("avgpingcon");
					if (pinghashiis.get("downnum") != null)
						downnum = (String) pinghashiis.get("downnum");
					List ipdiskListweblogic = new ArrayList();
					ipdiskListweblogic.add(ip);
					ipdiskListweblogic.add(equname);
					ipdiskListweblogic.add(node.getType());
					ipdiskListweblogic.add(pingconavg);
					ipdiskListweblogic.add(downnum);
					returnListweblogic.add(ipdiskListweblogic);
				}
			}
		}
		// weblogic---------------------end
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
				// �õ�������Subentity���б�
				list.add(ipdiskList);

				ipdiskList = null;
			}
		}
		// iis-===================================
		List listiis = new ArrayList();
		if (returnListiis != null && returnListiis.size() > 0) {
			for (int m = 0; m < returnListiis.size(); m++) {
				List ipdiskList = (List) returnListiis.get(m);
				for (int n = m + 1; n < returnListiis.size(); n++) {

					List _ipdiskList = (List) returnListiis.get(n);
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
							returnListiis.remove(m);
							returnListiis.add(m, _ipdiskList);
							returnListiis.remove(n);
							returnListiis.add(n, ipdiskList);
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
							returnListiis.remove(m);
							returnListiis.add(m, _ipdiskList);
							returnListiis.remove(n);
							returnListiis.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				listiis.add(ipdiskList);
				ipdiskList = null;
			}
		}
		// weblogic===================================
		List listweblogic = new ArrayList();
		if (returnListweblogic != null && returnListweblogic.size() > 0) {
			for (int m = 0; m < returnListweblogic.size(); m++) {
				List ipdiskList = (List) returnListweblogic.get(m);
				for (int n = m + 1; n < returnListweblogic.size(); n++) {

					List _ipdiskList = (List) returnListweblogic.get(n);
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
							returnListweblogic.remove(m);
							returnListweblogic.add(m, _ipdiskList);
							returnListweblogic.remove(n);
							returnListweblogic.add(n, ipdiskList);
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
							returnListweblogic.remove(m);
							returnListweblogic.add(m, _ipdiskList);
							returnListweblogic.remove(n);
							returnListweblogic.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				listweblogic.add(ipdiskList);
				ipdiskList = null;
			}
		}
		
		ReportValue pingReportValue =  ReportHelper.getMiddleReportValue(allMiddelwareOrderList,"ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "��ͨ��", "ʱ��", "");
		session.setAttribute("pingpath", pingpath);
		
		session.setAttribute("eventlist", list);
		session.setAttribute("eventlistiis", listiis);
		session.setAttribute("eventlistweblogic", listweblogic);
		
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				List _pinglist = (List)list.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);
				String downnum = (String)_pinglist.get(4);
				jsonString.append("{\"nodeid\":\"");
				jsonString.append("");
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(ip);
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(equname);
				jsonString.append("\",");

				jsonString.append("\"downnum\":\"");
				jsonString.append(downnum);
				jsonString.append("\"}");

				if(listiis.size() > 0 || listweblogic.size() > 0){
					jsonString.append(",");
				}else{
					if(i != list.size() - 1){
						jsonString.append(",");
					}
				}
			}
		}
		if (listiis != null && listiis.size() > 0) {
			for (int i = 0; i < listiis.size(); i++) {
				List _pinglist = (List)listiis.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);
				String downnum = (String)_pinglist.get(4);

				jsonString.append("{\"nodeid\":\"");
				jsonString.append("");
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(ip);
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(equname);
				jsonString.append("\",");

				jsonString.append("\"downnum\":\"");
				jsonString.append(downnum);
				jsonString.append("\"}");

				if(listweblogic.size() > 0){
					jsonString.append(",");
				}else{
					if(i != listiis.size() - 1){
						jsonString.append(",");
					}
				}
			}
		}
		if (listweblogic != null && listweblogic.size() > 0) {
			for (int i = 0; i < listweblogic.size(); i++) {
				List _pinglist = (List)listweblogic.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);
				String downnum = (String)_pinglist.get(4);

				jsonString.append("{\"nodeid\":\"");
				jsonString.append("");
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(ip);
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(equname);
				jsonString.append("\",");

				jsonString.append("\"downnum\":\"");
				jsonString.append(downnum);
				jsonString.append("\"}");

				if (i != listweblogic.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();

	}

	@SuppressWarnings("rawtypes")
	private void getNodeListForPing() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("content");
		String ip = "";
		if (ipaddress != null) {
			ip = ipaddress;
		}
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}

		String sql = "";
		if (ip != null && !ip.equals("")) {
			sql = " and ip_address like '%" + ip + "%'";
		}
		
		List weblogicList = new ArrayList();
		List tomcatList = new ArrayList();
		List iisList = new ArrayList();
		
		if(type != null && type.equals("all")){
			WeblogicConfigDao configdao = new WeblogicConfigDao();
			weblogicList = configdao.findByCondition(" where 1 = 1" + sql);
		}else if(type != null && type.equals("weblogic")){
			WeblogicConfigDao configdao = new WeblogicConfigDao();
			weblogicList = configdao.findByCondition(" where 1 = 1" + sql);
		}
		
		if(type != null && type.equals("all")){
			TomcatDao dao = new TomcatDao();
			tomcatList = dao.findByCondition(" where 1 = 1" + sql);
		}else if(type != null && type.equals("tomcat")){
			TomcatDao dao = new TomcatDao();
			tomcatList = dao.findByCondition(" where 1 = 1" + sql);
		}
		
		if(type != null && type.equals("all")){
			IISConfigDao iisdao = new IISConfigDao();
			iisList = iisdao.findByCondition(" where 1 = 1" + sql);
		}else if(type != null && type.equals("iis")){
			IISConfigDao iisdao = new IISConfigDao();
			iisList = iisdao.findByCondition(" where 1 = 1" + sql);
		}

		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		WeblogicConfig node = null;
		Tomcat tomcat = null;
		IISConfig iis = null;
		if (null != weblogicList && weblogicList.size() > 0) {
			for (int i = 0; i < weblogicList.size(); i++) {
				node = (WeblogicConfig) weblogicList.get(i);

				jsonString.append("{\"nodeid\":\"");
				jsonString.append(node.getId());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(node.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(node.getAlias());
				jsonString.append("\",");
				
				jsonString.append("\"type\":\"");
				jsonString.append("weblogic");
				jsonString.append("\",");
				
				if(type.equals("all")){
					jsonString.append("\"port\":\"");
					jsonString.append(node.getPortnum());
					jsonString.append("\"}");
				}else{
					jsonString.append("\"port\":\"");
					jsonString.append(node.getCommunity());
					jsonString.append("\"}");
				}

				

				jsonString.append(",");
			}
			size += weblogicList.size();
		}
		
		if (null != tomcatList && tomcatList.size() > 0) {
			for (int i = 0; i < tomcatList.size(); i++) {
				tomcat = (Tomcat) tomcatList.get(i);

				jsonString.append("{\"nodeid\":\"");
				jsonString.append(tomcat.getId());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(tomcat.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(tomcat.getAlias());
				jsonString.append("\",");
				
				jsonString.append("\"type\":\"");
				jsonString.append("tomcat");
				jsonString.append("\",");

				jsonString.append("\"port\":\"");
				jsonString.append(tomcat.getPort());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			size += tomcatList.size();
		}
		
		if (null != iisList && iisList.size() > 0) {
			for (int i = 0; i < iisList.size(); i++) {
				iis = (IISConfig) iisList.get(i);

				jsonString.append("{\"nodeid\":\"");
				jsonString.append(iis.getId());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(iis.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(iis.getName());
				jsonString.append("\",");
				
				jsonString.append("\"type\":\"");
				jsonString.append("iis");
				jsonString.append("\",");

				jsonString.append("\"port\":\"");
				jsonString.append(iis.getCommunity());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			size += iisList.size();
		}
		
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void exportMiddlewarePingWord() {

		String file = "/temp/middlewareliantonglvbaobiao.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createMiddlewareDocContext(fileName,"��ͨ��");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportMiddlewarePingExcel() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate;
		String totime = todate;
		Hashtable reporthash = new Hashtable();
		List pinglist = (List) session.getAttribute("pinglist");
		List pinglistiis = (List) session.getAttribute("pinglistiis");
		List pinglistweblogic = (List) session.getAttribute("pinglistweblogic");
		String pingpath = (String) session.getAttribute("pingpath");
		reporthash.put("pinglist", pinglist);
		reporthash.put("pinglistiis", pinglistiis);
		reporthash.put("pinglistweblogic", pinglistweblogic);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_midping("temp/midnms_report.xls","��ͨ��");// excel�ۺϱ���

		out.print(report.getFileName());
		out.flush();
	}

	private void exportMiddlewarePingPdf() {

		String file = "/temp/middlewareliantonglvbaobiao.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createMiddlewarePDFContext(fileName,"��ͨ��");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(fileName);
		out.flush();
	}


	@SuppressWarnings("rawtypes")
	public void createDatabasePingPdfContext(String file)
			throws DocumentException, IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// ����������
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("���ݿ���ͨ�ʱ���", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf1.format(cc);

		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("��������ʱ��:" + time, contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("����ͳ��ʱ���: " + starttime + " �� " + totime,
				contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		// ���� Table ���
		document.add(new Paragraph("\n"));
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(7);
		setTableFormat(aTable);

		aTable.addCell(this.setCellFormat(new Phrase("����", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP��ַ", contextFont));
		Cell cell11 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell2 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell3 = new Cell(new Phrase("���ݿ�Ӧ��", contextFont));
		Cell cell4 = new Cell(new Phrase("ƽ����ͨ��", contextFont));
		Cell cell5 = new Cell(new Phrase("崻�����(��)", contextFont));
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell4, true);
		this.setCellFormat(cell5, true);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String avgping = (String) _pinglist.get(4);
				String downnum = (String) _pinglist.get(5);

				Cell cell15 = new Cell(new Phrase(i + 1 + ""));
				Cell cell6 = new Cell(new Phrase(ip));
				Cell cell7 = new Cell(new Phrase(dbtype));
				Cell cell8 = new Cell(new Phrase(equname, contextFont));
				Cell cell9 = new Cell(new Phrase(dbuse));
				Cell cell10 = new Cell(new Phrase(avgping));
				Cell cell16 = new Cell(new Phrase(downnum));
				this.setCellFormat(cell15, false);
				this.setCellFormat(cell16, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell9, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell7, false);
				this.setCellFormat(cell6, false);
				aTable.addCell(cell15);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell16);

			}
		}
		// ������ͨ��
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// ����������ͨ��word����
	@SuppressWarnings("rawtypes")
	public void createDBPingDocContext(String file) throws DocumentException,
			IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "",
				BaseFont.NOT_EMBEDDED);
		// ����������
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("���ݿ���ͨ�ʱ���", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf1.format(cc);

		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("��������ʱ��:" + time, contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("����ͳ��ʱ���: " + starttime + " �� " + totime,
				contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// ���� Table ���
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(7);
		int width[] = { 50, 50, 50, 70, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // ռҳ���� 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// ������ʾ
		aTable.setAutoFillEmptyCells(true); // �Զ�����
		aTable.setBorderWidth(1); // �߿���
		aTable.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
		aTable.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
		aTable.setSpacing(0);// ����Ԫ��֮��ļ��
		aTable.setBorder(2);// �߿�
		aTable.endHeaders();

		Cell cell = new Cell("���");
		this.setCellFormat(cell, true);
		aTable.addCell(cell);
		Cell cell1 = new Cell("IP��ַ");
		Cell cell11 = new Cell("���ݿ�����");
		Cell cell2 = new Cell("���ݿ�����");
		Cell cell3 = new Cell("���ݿ�Ӧ��");
		Cell cell15 = new Cell("ƽ����ͨ��");
		Cell cell4 = new Cell("崻�����(��)");
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell15, true);
		this.setCellFormat(cell4, true);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell15);
		aTable.addCell(cell4);
		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String avgping = (String) _pinglist.get(4);
				String downnum = (String) _pinglist.get(5);

				Cell cell5 = new Cell(i + 1 + "");
				Cell cell6 = new Cell(ip);
				Cell cell7 = new Cell(dbtype);
				Cell cell8 = new Cell(equname);
				Cell cell9 = new Cell(dbuse);
				Cell cell10 = new Cell(avgping);
				Cell cell13 = new Cell(downnum);
				this.setCellFormat(cell5, false);
				this.setCellFormat(cell6, false);
				this.setCellFormat(cell7, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell9, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell13, false);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell13);

			}
		}
		// ������ͨ��
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}


	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE),
					temp.get(Calendar.HOUR_OF_DAY),
					temp.get(Calendar.DAY_OF_MONTH),
					temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(ʱ��)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
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
					Minute minute = new Minute(temp.get(Calendar.MINUTE),
							temp.get(Calendar.HOUR_OF_DAY),
							temp.get(Calendar.DAY_OF_MONTH),
							temp.get(Calendar.MONTH) + 1,
							temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1, title2, w, h);

			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ñ���ʽ
	 * 
	 * @param aTable
	 */
	protected void setTableFormat(Table aTable) {
		aTable.setWidth(100);
		aTable.setAutoFillEmptyCells(true);
		aTable.setPadding(5);
		aTable.setAlignment(Element.ALIGN_CENTER);
	}

	/**
	 * ���õ�Ԫ��ĸ�ʽ
	 * 
	 * @param cell
	 *            ��Ԫ��
	 * @param flag
	 *            �Ƿ����û�ɫ����ɫ
	 * @return cell
	 */
	protected Cell setCellFormat(Object obj, boolean flag) {
		Cell cell = null;
		Phrase p = null;
		if (obj instanceof Cell) {
			cell = (Cell) obj;
		} else if (obj instanceof Phrase) {
			p = (Phrase) obj;
			try {
				cell = new Cell(p);
			} catch (BadElementException e) {
				SysLogger.error("", e);
			}
		}
		if (cell != null) {
			if (flag) {
				cell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		return cell;
	}

	@SuppressWarnings("rawtypes")
	public void draw_column(Hashtable bighash, String title1, String title2,
			int w, int h) {
		if (bighash.size() != 0) {
			ChartGraph cg = new ChartGraph();
			int size = bighash.size();
			double[][] d = new double[1][size];
			String c[] = new String[size];
			Hashtable hash;
			for (int j = 0; j < size; j++) {
				hash = (Hashtable) bighash.get(new Integer(j));
				c[j] = (String) hash.get("name");
				d[0][j] = Double.parseDouble((String) hash.get("Utilization"
						+ "value"));
			}
			String rowKeys[] = { "" };
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
					rowKeys, c, d);// .createCategoryDataset(rowKeys,
			cg.zhu(title1, title2, dataset, w, h);
		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}

	@SuppressWarnings("rawtypes")
	public void createDocContexteventDatabase(String file, String type)
			throws DocumentException, IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		if ("pdf".equals(type)) {
			PdfWriter.getInstance(document, new FileOutputStream(file));
		} else {
			RtfWriter2.getInstance(document, new FileOutputStream(file));
		}
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// ����������
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("���ݿ��¼�����", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String contextString = "��������ʱ��:"
				+ sdf.format(new Date())
				+ " \n"// ����
				+ "����ͳ��ʱ���:"
				+ String.valueOf(session.getAttribute("starttime")) + " �� "
				+ String.valueOf(session.getAttribute("totime"));
		Paragraph context = new Paragraph(contextString, contextFont);
		// ���ĸ�ʽ�����
		context.setAlignment(Element.ALIGN_LEFT);
		// context.setFont(contextFont);
		context.setSpacingBefore(5);
		// ���õ�һ�пյ�����
		context.setFirstLineIndent(5);
		document.add(context);
		// ���� Table ���
		List pinglist = null;
		pinglist = (List) session.getAttribute("eventlist");// HONGLI
		if (pinglist == null) {
			pinglist = (List) request.getAttribute("ls");
		}
		Table aTable = new Table(10);
		this.setTableFormat(aTable);
		aTable.endHeaders();

		aTable.addCell(this.setCellFormat(new Phrase("���", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP��ַ", contextFont));
		Cell cell11 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell2 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell3 = new Cell(new Phrase("���ݿ�Ӧ��", contextFont));
		Cell cell20 = new Cell(new Phrase("�¼�����", contextFont));
		Cell cell21 = new Cell(new Phrase("��ͨ�¼�", contextFont));
		Cell cell22 = new Cell(new Phrase("�����¼�", contextFont));
		Cell cell23 = new Cell(new Phrase("�����¼�", contextFont));
		Cell cell15 = new Cell(new Phrase("�����������ô���", contextFont));
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell20, true);
		this.setCellFormat(cell21, true);
		this.setCellFormat(cell22, true);
		this.setCellFormat(cell23, true);
		this.setCellFormat(cell15, true);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell20);
		aTable.addCell(cell21);
		aTable.addCell(cell22);
		aTable.addCell(cell23);
		aTable.addCell(cell15);

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				String level1 = String.valueOf(_pinglist.get(5));
				String level2 = String.valueOf(_pinglist.get(6));
				String level3 = String.valueOf(_pinglist.get(7));
				String total = String.valueOf(_pinglist.get(8));

				Cell cell5 = new Cell(i + 1 + "");
				Cell cell6 = new Cell(ip);
				Cell cell7 = new Cell(dbtype);
				Cell cell8 = new Cell(equname);
				Cell cell9 = new Cell(dbuse);
				Cell cell24 = new Cell(total);
				Cell cell25 = new Cell(level1);
				Cell cell26 = new Cell(level2);
				Cell cell27 = new Cell(level3);
				Cell cell10 = new Cell(downnum);
				this.setCellFormat(cell5, false);
				this.setCellFormat(cell6, false);
				this.setCellFormat(cell7, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell9, false);
				this.setCellFormat(cell24, false);
				this.setCellFormat(cell25, false);
				this.setCellFormat(cell26, false);
				this.setCellFormat(cell27, false);
				this.setCellFormat(cell10, false);

				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell24);
				aTable.addCell(cell25);
				aTable.addCell(cell26);
				aTable.addCell(cell27);
				aTable.addCell(cell10);

			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}
	
	@SuppressWarnings("rawtypes")
	public void createMiddlewareDocContext(String file,String type) throws DocumentException, IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// ����������
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// ����������
		Paragraph title = new Paragraph("�м��" + type + "����", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");

		
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = df.format(cc);

		String contextString = "��������ʱ��:" + time + " \n"// ����
				+ "����ͳ��ʱ���:" + starttime + " �� " + totime;

		Paragraph context = new Paragraph(contextString, titleFont);
		context.setAlignment(Element.ALIGN_CENTER);
		document.add(context);
		document.add(new Paragraph("\n"));
		// ���� Table ���
		List pinglist = new ArrayList();
		List pinglistiis = new ArrayList();
		List pinglistweblogic = new ArrayList();
		if(type.equals("��ͨ��")){
			pinglist = (List) session.getAttribute("pinglist");
			pinglistiis = (List) session.getAttribute("pinglistiis");
			pinglistweblogic = (List) session.getAttribute("pinglistweblogic");
		}else{
			pinglist = (List) session.getAttribute("eventlist");
			pinglistiis = (List) session.getAttribute("eventlistiis");
			pinglistweblogic = (List) session.getAttribute("eventlistweblogic");
		}
		
		Table aTable = new Table(5);
		this.setTableFormat(aTable);
		aTable.addCell(this.setCellFormat(new Cell("���"), true));
		Cell cell1 = new Cell("IP��ַ");
		Cell cell11 = new Cell("�豸����");
		Cell cell15 = new Cell("ƽ����ͨ��");
		Cell cell4 = new Cell("崻�����(��)");
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell15, true);
		this.setCellFormat(cell4, true);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell15);
		aTable.addCell(cell4);
		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				Cell cell5 = new Cell(i + 1 + "");
				Cell cell6 = new Cell(ip);
				Cell cell8 = new Cell(equname);
				Cell cell10 = new Cell(avgping);
				Cell cell13 = new Cell(downnum);
				this.setCellFormat(cell5, false);
				this.setCellFormat(cell6, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell13, false);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell8);
				aTable.addCell(cell10);
				aTable.addCell(cell13);

			}
		}
		Cell cell = null;
		if (pinglistiis != null && pinglistiis.size() > 0) {
			for (int i = 0; i < pinglistiis.size(); i++) {
				List _pinglist = (List) pinglistiis.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				cell = new Cell(i + 1 + +pinglist.size() + "");
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(ip);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(equname);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(avgping);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(downnum);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
			}
		}
		if (pinglistweblogic != null && pinglistweblogic.size() > 0) {
			for (int i = 0; i < pinglistweblogic.size(); i++) {
				List _pinglist = (List) pinglistweblogic.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				cell = new Cell(i + 1 + pinglist.size() + pinglistiis.size() + "");
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(ip);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(equname);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(avgping);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(downnum);
				this.setCellFormat(cell, false);
				aTable.addCell(cell);

			}
		}
		//������ͨ��
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}
	
	@SuppressWarnings("rawtypes")
	public void createMiddlewarePDFContext(String file,String type) throws DocumentException, IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// ����������
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 14, Font.NORMAL);
		Paragraph title = new Paragraph("�м��" + type + " ����", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");

		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = df.format(cc);

		String contextString = "��������ʱ��:" + time + " \n"// ����
				+ "����ͳ��ʱ���:" + starttime + " �� " + totime;
		Paragraph context = new Paragraph(contextString, titleFont);
		context.setAlignment(Element.ALIGN_CENTER);
		document.add(context);
		document.add(new Paragraph("\n"));
		// ���� Table ���
		List pinglist = new ArrayList();
		List pinglistiis = new ArrayList();
		List pinglistweblogic = new ArrayList();
		if(type.equals("��ͨ��")){
			pinglist = (List) session.getAttribute("pinglist");
			pinglistiis = (List) session.getAttribute("pinglistiis");
			pinglistweblogic = (List) session.getAttribute("pinglistweblogic");
		}else{
			pinglist = (List) session.getAttribute("eventlist");
			pinglistiis = (List) session.getAttribute("eventlistiis");
			pinglistweblogic = (List) session.getAttribute("eventlistweblogic");
		}
		Table aTable = new Table(5);
		this.setTableFormat(aTable);
		Cell cell2 = new Cell(new Phrase("���", contextFont));
		Cell cell1 = new Cell(new Phrase("IP��ַ", contextFont));
		Cell cell11 = new Cell(new Phrase("�豸����", contextFont));
		Cell cell15 = new Cell(new Phrase("ƽ����ͨ��", contextFont));
		Cell cell4 = new Cell(new Phrase("崻�����(��)", contextFont));
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell15, true);
		this.setCellFormat(cell4, true);
		aTable.addCell(cell2);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell15);
		aTable.addCell(cell4);

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				Cell cell5 = new Cell(new Phrase(i + 1 + ""));
				Cell cell6 = new Cell(new Phrase(ip));
				Cell cell8 = new Cell(new Phrase(equname));
				Cell cell10 = new Cell(new Phrase(avgping));
				Cell cell13 = new Cell(new Phrase(downnum));
				this.setCellFormat(cell5, false);
				this.setCellFormat(cell6, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell13, false);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell8);
				aTable.addCell(cell10);
				aTable.addCell(cell13);

			}
		}
		Cell cell = null;
		if (pinglistiis != null && pinglistiis.size() > 0) {
			for (int i = 0; i < pinglistiis.size(); i++) {
				List _pinglist = (List) pinglistiis.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				cell = new Cell(new Phrase(i + 1 + +pinglist.size() + ""));
				aTable.addCell(cell);
				this.setCellFormat(cell, false);
				cell = new Cell(new Phrase(ip));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(new Phrase(equname, contextFont));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				this.setCellFormat(cell, false);
				cell = new Cell(new Phrase(avgping));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(new Phrase(downnum));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
			}
		}
		if (pinglistweblogic != null && pinglistweblogic.size() > 0) {
			for (int i = 0; i < pinglistweblogic.size(); i++) {
				List _pinglist = (List) pinglistweblogic.get(i);
				String ip = (String) _pinglist.get(0);
				String equname = (String) _pinglist.get(1);
				String avgping = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				cell = new Cell(new Phrase(i + 1 + pinglist.size() + pinglistiis.size() + ""));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(new Phrase(ip));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(new Phrase(equname, contextFont));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(new Phrase(avgping));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);
				cell = new Cell(new Phrase(downnum));
				this.setCellFormat(cell, false);
				aTable.addCell(cell);

			}
		}
		//������ͨ��
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}
}
