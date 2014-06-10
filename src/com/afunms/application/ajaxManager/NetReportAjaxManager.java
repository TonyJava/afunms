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

import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.model.ReportValue;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.system.util.UserView;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class NetReportAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
	
	@Override
	public void execute(String action) {
		if(action.equals("getNodeListForPing")) {
			getNodeListForPing();
		}else if(action.equals("exportNetPingWord")){
			exportNetPingWord();
		}else if(action.equals("exportNetPingExcel")){
			exportNetPingExcel();
		}else if(action.equals("exportNetPingPdf")){
			exportNetPingPdf();
		}else if(action.equals("getEventDetailList")){
			getEventDetailList();
		}else if(action.equals("exportEventWord")){
			exportEventWord();
		}else if(action.equals("exportEventExcel")){
			exportEventExcel();
		}else if(action.equals("exportEventPdf")){
			exportEventPdf();
		}else if(action.equals("exportNetReport")){
			exportNetReport();
		}else if(action.equals("exportNetMultiReport")){
			exportNetMultiReport();
		}else if(action.equals("getPingDetailForNet")){
			getPingDetailForNet();
		}else if(action.equals("netchoceList")){
			netchoceList();
		}else if(action.equals("exportNetChoceReport")){
			exportNetChoceReport();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportNetChoceReport(){
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
			String[] _ids = oids.split(";");
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
		String equipname = "";
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
		Vector vector = new Vector();
		String runAppraise = "良";// 运行评价
		int levelOneAlarmNum = 0;// 告警的条数
		int levelTwoAlarmNum = 0;// 告警的条数
		int levelThreeAlarmNum = 0;// 告警的条数
		String fileName = "";
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
					String newip = SysUtil.doip(ip);
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
						String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
						String[] bandch3 = { "入口流速", "出口流速" };
						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports.get(k);
							// 按分钟显示报表
							Hashtable value = new Hashtable();
							value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
							String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
							p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
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
					
					//memory
					Hashtable memoryhash = hostmanager.getCategory(ip, "Memory", "Utilization", starttime, totime);
					maxhash = new Hashtable();
					String memorymax = "";
					String avgmemory = "";
					if (memoryhash.get("max") != null) {
						memorymax = (String) memoryhash.get("max");
					}
					if (memoryhash.get("avgmemory") != null) {
						avgmemory = (String) memoryhash.get("avgmemory");
					}
					memmaxhash.put("memorymax", memorymax);
					memavghash.put("avgmemory", avgmemory);
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
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("equipname", equipname);
					reporthash.put("memmaxhash", memmaxhash);
					reporthash.put("memavghash", memavghash);
					allreporthash.put(ip, reporthash);
				}
				runAppraise = SysUtil.getRunAppraise(levelOneAlarmNum, levelTwoAlarmNum, levelThreeAlarmNum);
			}
			 String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
			 fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			 ExcelReport1 report = new ExcelReport1(new IpResourceReport(),allreporthash);
			 Hashtable dataHash = new Hashtable();
			 dataHash.put("businessAnalytics", "");// 运行分析
			 dataHash.put("runAppraise", runAppraise);// 运行评价
			 report.setDataHash(dataHash);
			 report.createReport_networkchoce(starttime, totime, fileName,username, positionname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPingDetailForNet(){
		//生成报表所需要的数据
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
		
		String id = getParaValue("ids");
		String[] ids = {};
		if(id != null){
			ids = id.split(";");
		}
		if (ids != null && ids.length > 0) {
			session.setAttribute("ids", ids);
		} else {
			ids = (String[]) session.getAttribute("ids");
		}

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
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum") || orderflag.equalsIgnoreCase("responseavg") || orderflag.equalsIgnoreCase("responsemax")) {
			returnList = (List) session.getAttribute("pinglist");
			orderList = (List) session.getAttribute("orderList");
		} else {
			// 对orderList根据theValue进行排序

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

		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "连通率", "时间", "");
		String responsetimepath = new ReportExport().makeJfreeChartData(responseReportValue.getListValue(), responseReportValue.getIpList(), "响应时间", "时间", "");

		session.setAttribute("pingpath", pingpath);
		session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		session.setAttribute("pinglist", list);
		session.setAttribute("orderList", orderList);
		
		StringBuffer jsonString = new StringBuffer("[{");
		jsonString.append("pic:[");
		jsonString.append("{\"pingChartDivStr\":\"");
		jsonString.append(pingChartDivStr);
		jsonString.append("\",");

		jsonString.append("\"responsetimeDivStr\":\"");
		jsonString.append(responsetimeDivStr);
		jsonString.append("\"}]");
		StringBuffer jsonStringNode = new StringBuffer("node:[{Rows:[");
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List _pinglist = (List)list.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);	
				String osname = (String)_pinglist.get(2);
				String avgping = (String)_pinglist.get(3);
	            String downnum = (String)_pinglist.get(4);
	            String responseavg = (String)_pinglist.get(5);
				String responsemax = (String)_pinglist.get(6) + "毫秒";
	            
				jsonStringNode.append("{\"nodeid\":\"");
				jsonStringNode.append("");
				jsonStringNode.append("\",");

				jsonStringNode.append("\"ipaddress\":\"");
				jsonStringNode.append(ip);
				jsonStringNode.append("\",");
				
				jsonStringNode.append("\"hostname\":\"");
				jsonStringNode.append(equname);
				jsonStringNode.append("\",");
				
				jsonStringNode.append("\"hostos\":\"");
				jsonStringNode.append(osname);
				jsonStringNode.append("\",");
				
				jsonStringNode.append("\"avgping\":\"");
				jsonStringNode.append(avgping);
				jsonStringNode.append("\",");
				
				jsonStringNode.append("\"downnum\":\"");
				jsonStringNode.append(downnum);
				jsonStringNode.append("\",");
				
				jsonStringNode.append("\"responseavg\":\"");
				jsonStringNode.append(responseavg);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"responsemax\":\"");
				jsonStringNode.append(responsemax);
				jsonStringNode.append("\"}");
				
				if (i != list.size() - 1) {
					jsonStringNode.append(",");
				}
			}
		}
		jsonStringNode.append("]}]");
		jsonString.append(",");
		jsonString.append(jsonStringNode);
		jsonString.append("}]");
		//jsonString.append("],total:" + list.size() + "}");
	System.out.println(jsonString);
		out.print(jsonString.toString());
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportNetMultiReport(){
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
		String equipname = "";
		String type = "";
		String typename = "";
		String equipnameNetDoc = "";
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
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
		int memoryvalue = 0;
		String fileName = "";
		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameNetDoc = node.getAlias();
			String newip = SysUtil.doip(ip);
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
				// 显示端口的流速图形
				I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
				String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
				String[] bandch3 = { "入口流速", "出口流速" };
				for (int i = 0; i < reportports.size(); i++) {
					com.afunms.config.model.Portconfig portconfig = null;
					try {
						portconfig = (com.afunms.config.model.Portconfig) reportports.get(i);
						// 按分钟显示报表
						Hashtable value = new Hashtable();
						value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
						String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
						p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
			reporthash.put("portconfigHash", portconfigHash);
			reporthash.put("reportports", reportports);
			reporthash.put("netifVector", vector);
			reporthash.put("startdate", startdate);
			reporthash.put("todate", todate);
			reporthash.put("totime", totime);
			reporthash.put("starttime", starttime);
			Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
			Hashtable streaminHash = hostmanager.getAllutilhdx(ip, "AllInBandwidthUtilHdx", starttime, totime, "avg");
			Hashtable streamoutHash = hostmanager.getAllutilhdx(ip, "AllOutBandwidthUtilHdx", starttime, totime, "avg");
			String pingconavg = "";
			String avgput = "";
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
			maxhash.put("cpumax", cpumax);
			maxhash.put("avgcpu", avgcpu);
			// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector) _IpRouterHash.get(ip);
			if (vector != null)
				reporthash.put("iprouterVector", vector);
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
					} else if(("memory").equals(subentity)){ //内存
						memoryvalue = memoryvalue + 1;
					}
				}
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("cpuvalue", cpuvalue);
			reporthash.put("updownvalue", updownvalue);
			reporthash.put("utilvalue", utilvalue);
			reporthash.put("memoryvalue", memoryvalue);
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
				CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
				maxhash.put("cpu", cpudata.getThevalue());
				reporthash.put("CPU", maxhash);
			} else {
				reporthash.put("CPU", maxhash);
			}
			// 运行评价
			String grade = "良";
			if (pingvalue + cpuvalue + updownvalue + utilvalue + memoryvalue>= 3) {
				grade = "差";
			} else if (pingvalue + cpuvalue + updownvalue + utilvalue + memoryvalue < 3 && pingvalue + cpuvalue + updownvalue + utilvalue + memoryvalue > 0) {
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
			reporthash.put("ConnectUtilizationImgPath", SysUtil.doip(ip) + "ConnectUtilization");
			reporthash.put("cpuImgPath", SysUtil.doip(ip) + "cpu");
			
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
			String str = request.getParameter("str");
			if ("0".equals(str)) {
				report.createReport_network("temp/network_multiple.xls");// excel综合报表
				fileName = report.getFileName();
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/network_multiple.doc";
					fileName = ResourceCenter.getInstance().getSysPath() + file;
					report1.createReport_networkDoc(fileName);// word综合报表
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/network_analysis.doc";
					fileName = ResourceCenter.getInstance().getSysPath() + file;
					report1.createReport_networkNewDoc(fileName);// word运行分析报表
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/network_analysis.pdf";
					fileName = ResourceCenter.getInstance().getSysPath() + file;
					report1.createReport_networkNewPdf(fileName);// PDF网络设备业务分析表
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("5".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/network_multiple.pdf";
					fileName = ResourceCenter.getInstance().getSysPath() + file;
					report1.createReport_networkPDF(fileName);
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
	private void exportNetReport(){
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
			String[] _ids = oids.split(";");
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
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		
		String equipname = "";
		String filename = "";

		Hashtable memHash = new Hashtable();
		Hashtable cpuHash = new Hashtable();
		Hashtable pingHash = new Hashtable();
		Hashtable pingData = ShareData.getPingdata();
		Hashtable shareData = ShareData.getSharedata();
		Vector vector = new Vector();
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Hashtable reporthash = new Hashtable();
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					ip = node.getIpAddress();
					equipname = node.getAlias();
					String newip = SysUtil.doip(ip);
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
						e.printStackTrace();
					} finally {
						portdao.close();
					}
					reporthash.put("portconfigHash", portconfigHash);
					reporthash.put("reportports", reportports);
					if (reportports != null && reportports.size() > 0) {
						// 显示端口的流速图形
						I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
						String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
						String[] bandch3 = { "入口流速", "出口流速" };

						for (int k = 0; k < reportports.size(); k++) {
							com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) reportports.get(k);
							// 按分钟显示报表
							Hashtable value = new Hashtable();
							value = daymanager.getmultiHisHdx(ip, "ifspeed", portconfig.getPortindex() + "", banden3, bandch3, startdate, todate, "UtilHdx");
							String reportname = "第" + portconfig.getPortindex() + "(" + portconfig.getName() + ")端口流速" + startdate + "至" + todate + "报表(按分钟显示)";
							p_drawchartMultiLineMonth(value, reportname, newip + portconfig.getPortindex() + "ifspeed_day", 800, 200, "UtilHdx");
						}
					}
					reporthash.put("netifVector", vector);
					// CPU
					Hashtable cpuTempHash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
					String cpumax = "";
					String avgcpu = "";
					if (cpuTempHash.get("max") != null) {
						cpumax = (String) cpuTempHash.get("max");
					}
					if (cpuTempHash.get("avgcpucon") != null) {
						avgcpu = (String) cpuTempHash.get("avgcpucon");
					}
					cpuHash.put("cpumax", cpumax);
					cpuHash.put("avgcpu", avgcpu);
					// CPU利用率画图
					p_draw_line(cpuTempHash, "", newip + "cpu", 740, 120);
					// Memory
					Hashtable memTempHash = hostmanager.getCategory(ip, "Memory", "Utilization", starttime, totime);
					String memMax = "";
					String avgMem = "";
					if (memTempHash.get("max") != null) {
						memMax = (String) memTempHash.get("max");
					}
					if (memTempHash.get("avgmemory") != null) {
						avgMem = (String) memTempHash.get("avgmemory");
					}
					memHash.put("memMax", memMax);
					memHash.put("avgMem", avgMem);
					// 连通率
					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					String ConnectUtilizationmax = "";
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					pingHash.put("avgpingcon", pingconavg);
					pingHash.put("pingmax", ConnectUtilizationmax);
					// 连通率画图
					p_draw_line(ConnectUtilizationhash, "", newip + "ConnectUtilization", 740, 120);

					// 从内存中获得当前的跟此IP相关的IP-MAC的FDB表信息
					Hashtable _IpRouterHash = ShareData.getIprouterdata();
					vector = (Vector) _IpRouterHash.get(ip);
					if (vector != null)
						reporthash.put("iprouterVector", vector);

					Vector pdata = (Vector) pingData.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata.getSubentity().equals("ConnectUtilization")) {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());// 当前ping值
								reporthash.put("ping", pingHash);
							}
						}
					}
					// CPU
					Hashtable hdata = (Hashtable) shareData.get(ip);
					if (hdata == null)
						hdata = new Hashtable();

					Vector cpuVector = new Vector();
					if (hdata.get("cpu") != null)
						cpuVector = (Vector) hdata.get("cpu");
					if (cpuVector != null && cpuVector.size() > 0) {
						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.elementAt(0);
						cpuHash.put("cpu", cpudata.getThevalue());// 当前的Cpu值
						reporthash.put("CPU", cpuHash);
					} else {
						reporthash.put("CPU", cpuHash);
					}
					// Memory
					Vector memVector = new Vector();
					if (hdata.get("memory") != null)
						memVector = (Vector) hdata.get("memory");
					if (memVector != null && memVector.size() > 0) {
						Memorycollectdata memData = (Memorycollectdata) memVector.elementAt(0);
						memHash.put("memory", memData.getThevalue());// 当前的Memory值
						reporthash.put("memory", memHash);
					} else {
						reporthash.put("memory", memHash);
					}
					reporthash.put("equipname", equipname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
			}
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			report.createReport_networkall("/temp/networknms_report.xls");
			filename = report.getFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(filename);
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void exportEventPdf(){
		String file = "/temp/net_event.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String starttime = (String) session.getAttribute("starttime");
			String totime = (String) session.getAttribute("totime");
			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph title = new Paragraph("网络设备事件报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(10);
			document.add(title);

			Paragraph blank = new Paragraph("\n");
			document.add(blank);

			Font timeFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph createReportTime = new Paragraph("报表生成时间：" + sdf.format(new Date()), timeFont);
			createReportTime.setAlignment(Element.ALIGN_CENTER);
			document.add(createReportTime);

			Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
			timeSapce.setAlignment(Element.ALIGN_CENTER);
			document.add(timeSapce);
			document.add(blank);

			// 设置 Table 表格
			List eventlist = (List) session.getAttribute("eventlist");
			PdfPTable aTable = new PdfPTable(13);
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
			PdfPCell cell12 = new PdfPCell(new Phrase("内存事件(个)", contextFont));

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
			cell12.setBackgroundColor(Color.LIGHT_GRAY);

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
			cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);

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
			cell12.setHorizontalAlignment(Element.ALIGN_CENTER);

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
			aTable.addCell(cell12);
			document.add(aTable);
			aTable = new PdfPTable(13);
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
					String pingValue = (String) _eventlist.get(7);
					String cpuValue = (String) _eventlist.get(8);
					String interfaceValue = (String) _eventlist.get(9);
					String flowValue = (String) _eventlist.get(10);
					String memValue = (String) _eventlist.get(11);
					PdfPCell cell13 = new PdfPCell(new Phrase(i + 1 + ""));
					PdfPCell cell14 = new PdfPCell(new Phrase(ip));
					PdfPCell cell15 = new PdfPCell(new Phrase(equname, contextFont));
					PdfPCell cell16 = new PdfPCell(new Phrase(osname, contextFont));
					PdfPCell cell17 = new PdfPCell(new Phrase(sum));// 事件总数
					PdfPCell cell18 = new PdfPCell(new Phrase(levelone));// 普通事件
					PdfPCell cell19 = new PdfPCell(new Phrase(leveltwo));// 紧急
					PdfPCell cell20 = new PdfPCell(new Phrase(levelthree));// 严重
					PdfPCell cell21 = new PdfPCell(new Phrase(pingValue));
					PdfPCell cell22 = new PdfPCell(new Phrase(cpuValue));
					PdfPCell cell23 = new PdfPCell(new Phrase(interfaceValue));
					PdfPCell cell24 = new PdfPCell(new Phrase(flowValue));
					PdfPCell cell25 = new PdfPCell(new Phrase(memValue));

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
					aTable.addCell(cell25);
					PdfPCell cellEventTitle = new PdfPCell(new Phrase("事件列表", contextFont));
					PdfPCell cellEventList = new PdfPCell();
					cellEventList.setColspan(12);
					List eventContentList = (List) _eventlist.get(12);
					for (int j = 0; j < eventContentList.size(); j++) {
						cellEventList.addElement(new Paragraph( (j+1) + ".  " + eventContentList.get(j) + "", contextFont));
						cellEventList.addElement(new Paragraph("", contextFont));
					}

					aTable.addCell(cellEventTitle);
					aTable.addCell(cellEventList);
				}
			}
			document.add(aTable);
			document.add(new Paragraph("\n"));
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportEventExcel(){
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

		List eventlist = (List) session.getAttribute("eventlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", eventlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostevent("/temp/netevent_report.xls");
		
		out.print(report.getFileName());
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void exportEventWord(){
		//报表模块中被调用
		String file = "/temp/net_event.doc"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {
			String starttime = (String) session.getAttribute("starttime");
			String totime = (String) session.getAttribute("totime");
			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			document.open();
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);

			Paragraph title = new Paragraph("网络设备事件报表", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			Paragraph blank = new Paragraph("\n");
			document.add(blank);

			Font timeFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph createReportTime = new Paragraph("报表生成时间：" + sdf.format(new Date()), timeFont);
			createReportTime.setAlignment(Element.ALIGN_CENTER);
			document.add(createReportTime);

			Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
			timeSapce.setAlignment(Element.ALIGN_CENTER);
			document.add(timeSapce);
			document.add(blank);

			// 设置 Table 表格
			List eventlist = (List) session.getAttribute("eventlist");
			Table aTable = new Table(13);
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
			Cell cell12 = new Cell("内存事件(个)");

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
			cell12.setBackgroundColor(Color.LIGHT_GRAY);

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
			cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);

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
			cell12.setHorizontalAlignment(Element.ALIGN_CENTER);

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
			aTable.addCell(cell12);

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
					String pingvalue = (String)_eventlist.get(7);
		            String cpuvalue = (String)_eventlist.get(8);
		            String updownvalue = (String)_eventlist.get(9);
		            String utilvalue = (String)_eventlist.get(10);
		            String memvalue = (String)_eventlist.get(11);
					Cell cell13 = new Cell(i + 1 + "");
					Cell cell14 = new Cell(ip);
					Cell cell15 = new Cell(equname);
					Cell cell16 = new Cell(osname);
					Cell cell17 = new Cell(sum);
					Cell cell18 = new Cell(levelone);
					Cell cell19 = new Cell(leveltwo);
					Cell cell20 = new Cell(levelthree);
					Cell cell21 = new Cell(pingvalue);
					Cell cell22 = new Cell(cpuvalue);
					Cell cell23 = new Cell(updownvalue);
					Cell cell24 = new Cell(utilvalue);
					Cell cell25 = new Cell(memvalue);

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
					aTable.addCell(cell25);

					Cell cellEventTitle = new Cell("事件列表");
					Cell cellEventList = new Cell();
					cellEventList.setColspan(12);
					List eventContentList = (List) _eventlist.get(12);
					for (int j = 0; j < eventContentList.size(); j++) {

						cellEventList.add(new Paragraph( (j+1) + ".  " + eventContentList.get(j) + ""));
						cellEventList.add(new Paragraph(""));
					}

					aTable.addCell(cellEventTitle);
					aTable.addCell(cellEventList);

				}
			}
			document.add(aTable);
			document.add(new Paragraph("\n"));
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getEventDetailList(){
		//报表模块中被调用
		Date d = new Date();
		String startdate = getParaValue("beginDate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("endDate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate;
		String totime = todate;

		String id = getParaValue("ids");
		String[] ids = {};
		if(id != null && id.length() > 0){
			ids = id.split(";");
		}

		if (ids != null && ids.length > 0) {
			session.setAttribute("ids", ids);
		} else {
			ids = (String[]) session.getAttribute("ids");
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
				EventListDao eventdao = new EventListDao();
				// 得到事件列表
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' and level1>0");
				s.append(" and nodeid=" + node.getId());

				List infolist = eventdao.findByCriteria(s.toString());

				if (infolist != null && infolist.size() > 0) {
					List ipeventList = new ArrayList();
					List eventContentList = new ArrayList();
					int levelone = 0;
					int levletwo = 0;
					int levelthree = 0;
					int pingvalue = 0;
					int cpuvalue = 0;
					int updownvalue = 0;
					int utilvalue = 0;
					int memoryvalue = 0;
					String content = "";

					for (int j = 0; j < infolist.size(); j++) {
						EventList eventlist = (EventList) infolist.get(j);
						if (eventlist.getContent() == null)
							eventlist.setContent("");
						content = eventlist.getContent();
						Calendar recordCalendar = eventlist.getRecordtime();
						Date recordTime = recordCalendar.getTime();

						eventContentList.add(content + " ( " + df.format(recordTime) + " 至 " + eventlist.getLasttime() + " )");

						String subentity = eventlist.getSubentity();
						if (eventlist.getLevel1() == 1) {
							levelone = levelone + 1;
						} else if (eventlist.getLevel1() == 2) {
							levletwo = levletwo + 1;
						} else if (eventlist.getLevel1() == 3) {
							levelthree = levelthree + 1;
						}

						if (("ping").equals(subentity)) {// 联通率
							pingvalue = pingvalue + 1;
						} else if (("cpu").equals(subentity)) {// cpu
							cpuvalue = cpuvalue + 1;
						} else if (("interface").equals(subentity) || content.indexOf("端口") >= 0) {// 端口
							updownvalue = updownvalue + 1;
						} else if (("AllInBandwidthUtilHdx").equals(subentity) || ("AllOutBandwidthUtilHdx").equals(subentity)) {// 流速
							utilvalue = utilvalue + 1;
						} else if (("memory").equals(subentity)) {// cpu
							memoryvalue = memoryvalue + 1;
						}
					}
					String equname = node.getAlias();
					String ip = node.getIpAddress();

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
					ipeventList.add(memoryvalue + "");
					ipeventList.add(eventContentList);
					orderList.add(ipeventList);

				}
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("one") || orderflag.equalsIgnoreCase("two") || orderflag.equalsIgnoreCase("three") || orderflag.equalsIgnoreCase("ping") || orderflag.equalsIgnoreCase("cpu") || orderflag.equalsIgnoreCase("updown") || orderflag.equalsIgnoreCase("util") || orderflag.equalsIgnoreCase("sum")) {
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
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}

		session.setAttribute("eventlist", list);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List _eventlist = (List)list.get(i);
				String ip = (String)_eventlist.get(0);
				String equname = (String)_eventlist.get(1);	
				String osname = (String)_eventlist.get(2);
				String sum = (String)_eventlist.get(3);
	            String levelone = (String)_eventlist.get(4);
	            String leveltwo = (String)_eventlist.get(5);
	            String levelthree = (String)_eventlist.get(6);
	            String pingvalue = (String)_eventlist.get(7);
	            String cpuvalue = (String)_eventlist.get(8);
	            String portvalue = (String)_eventlist.get(9);
	            String utilvalue = (String)_eventlist.get(10);
	            String memvalue = (String)_eventlist.get(11);
	            
	            jsonString.append("{\"nodeid\":\"");
				jsonString.append("");
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(ip);
				jsonString.append("\",");
				
				jsonString.append("\"hostname\":\"");
				jsonString.append(equname);
				jsonString.append("\",");
				
				jsonString.append("\"hostos\":\"");
				jsonString.append(osname);
				jsonString.append("\",");
				
				jsonString.append("\"sum\":\"");
				jsonString.append(sum);
				jsonString.append("\",");
				
				jsonString.append("\"levelone\":\"");
				jsonString.append(levelone);
				jsonString.append("\",");
				
				jsonString.append("\"leveltwo\":\"");
				jsonString.append(leveltwo);
				jsonString.append("\",");
				
				jsonString.append("\"levelthree\":\"");
				jsonString.append(levelthree);
				jsonString.append("\",");
				
				jsonString.append("\"pingvalue\":\"");
				jsonString.append(pingvalue);
				jsonString.append("\",");
				
				jsonString.append("\"cpuvalue\":\"");
				jsonString.append(cpuvalue);
				jsonString.append("\",");
				
				jsonString.append("\"portvalue\":\"");
				jsonString.append(portvalue);
				jsonString.append("\",");
				
				jsonString.append("\"utilvalue\":\"");
				jsonString.append(utilvalue);
				jsonString.append("\",");

				jsonString.append("\"memvalue\":\"");
				jsonString.append(memvalue);
				jsonString.append("\"}");
				
				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	
	}
	
	@SuppressWarnings("rawtypes")
	private void netchoceList(){
		HostNodeDao dao = new HostNodeDao();
		List list = new ArrayList();
		list = (List)dao.loadNetwork(1);
		dao.close();
		
		String content = getParaValue("content");
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		HostNode vo = new HostNode();
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (HostNode) list.get(i);
				if(content != null && !content.equals("")){
					if(!vo.getIpAddress().contains(content.trim())){
						continue;
					}
				}
				jsonString.append("{\"nodeid\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(vo.getIpAddress());
				jsonString.append("\",");
				
				jsonString.append("\"hostname\":\"");
				if (null != vo.getAlias()) {
					jsonString.append(vo.getAlias());
				} else {
					jsonString.append(" ");
				}
				jsonString.append("\",");

				jsonString.append("\"hostos\":\"");
				if (null != vo.getType()) {
					jsonString.append(vo.getType());
				} else {
					jsonString.append(" ");
				}
				jsonString.append("\"}");
				
				if (i != list.size() - 1) {
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
		String ipaddress = getParaValue("content");
		String ip = "";
		if(ipaddress != null){
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
		if(ip != null && !ip.equals("")){
			sql = " and ip_address like '%" + ip + "%'";
		}
		HostNodeDao dao = new HostNodeDao();
		List list = new ArrayList();
		list = (List)dao.findByCondition(" where 1=1 and (category<4 or category=7 or category=8)" + sql);
		
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		HostNode vo = new HostNode();
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (HostNode) list.get(i);
				jsonString.append("{\"nodeid\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(vo.getIpAddress());
				jsonString.append("\",");
				
				jsonString.append("\"hostname\":\"");
				if (null != vo.getAlias()) {
					jsonString.append(vo.getAlias());
				} else {
					jsonString.append(" ");
				}
				jsonString.append("\",");

				jsonString.append("\"hostos\":\"");
				if (null != vo.getType()) {
					jsonString.append(vo.getType());
				} else {
					jsonString.append(" ");
				}
				jsonString.append("\"}");
				
				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}
	
	private void exportNetPingWord(){
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getNetPingDataForReport();
		}
		
		String file = "/temp/net_ping.doc";
		String fileName = ResourceCenter.getInstance().getSysPath() + file;
		
		try {
			createNetPingDocContext(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportNetPingExcel(){
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getNetPingDataForReport();
		}
		
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		List pingList = (List) session.getAttribute("pinglist");
		String pingpath = (String) session.getAttribute("pingpath");
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		
		Hashtable reporthash = new Hashtable();
		reporthash.put("pinglist", pingList);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);
		reporthash.put("responsetimepath", responsetimepath);
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_netping("/temp/net_ping.xls");
		
		out.print(report.getFileName());
		out.flush();
	}
	
	private void exportNetPingPdf(){
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getNetPingDataForReport();
		}
		
		String file = "/temp/net_ping.pdf"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {
			createNetPingPdfContext(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getNetPingDataForReport(){
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
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		String id = getParaValue("id");
		String[] ids = null;
		if(id != null){
			ids = id.split(";");
		}
		if (ids != null && ids.length > 0) {
			session.setAttribute("ids", ids);
		} else {
			ids = (String[]) session.getAttribute("ids");
		}
		request.setAttribute("ids", ids);
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
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum") || orderflag.equalsIgnoreCase("responseavg") || orderflag.equalsIgnoreCase("responsemax")) {
			returnList = (List) session.getAttribute("pinglist");
			orderList = (List) session.getAttribute("orderList");
		} else {
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
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
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		ReportValue responseReportValue = ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "连通率", "时间", "");
		String responsetimepath = new ReportExport().makeJfreeChartData(responseReportValue.getListValue(), responseReportValue.getIpList(), "响应时间", "时间", "");
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("responsetimepath", responsetimepath);
		session.setAttribute("pinglist", list);
		session.setAttribute("orderList", orderList);
	}
	
	@SuppressWarnings("rawtypes")
	public void createNetPingPdfContext(String file) throws DocumentException, IOException {
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");

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
		Font contextFont = new com.lowagie.text.Font(bfChinese, 11, Font.NORMAL);
		Paragraph title = new Paragraph("网络设备连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);

		Paragraph blank = new Paragraph("\n");
		document.add(blank);
		Font timeFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph createReportTime = new Paragraph("报表生成时间：" + df.format(new Date()), timeFont);
		createReportTime.setAlignment(Element.ALIGN_CENTER);
		document.add(createReportTime);
		Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
		timeSapce.setAlignment(Element.ALIGN_CENTER);
		document.add(timeSapce);
		document.add(blank);

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
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(69);
		document.add(img);
		img = Image.getInstance(responsetimepath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(69);
		document.add(img);
		document.add(aTable);
		document.close();
	}
	
	// 生成主机连通率word报表
	@SuppressWarnings("rawtypes")
	public void createNetPingDocContext(String file) throws DocumentException, IOException {
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");

		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		Font timeFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph blank = new Paragraph("\n");
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		Paragraph title = new Paragraph("网络设备连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		document.add(blank);

		Paragraph createReportTime = new Paragraph("报表生成时间：" + df.format(new Date()), timeFont);
		createReportTime.setAlignment(Element.ALIGN_CENTER);
		document.add(createReportTime);
		Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
		timeSapce.setAlignment(Element.ALIGN_CENTER);
		document.add(timeSapce);
		document.add(blank);

		// 设置 Table 表格
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
		img.scalePercent(90);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		img = Image.getInstance(responsetimepath);
		img.setAbsolutePosition(0, 0);
		img.scalePercent(90);
		img.setAlignment(Image.LEFT);
		document.add(img);
		document.add(blank);
		document.add(aTable);
		document.close();
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
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
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);

			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}/**
	 * 设置表格格式
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
	 * 设置单元格的格式
	 * 
	 * @param cell
	 *            单元格
	 * @param flag
	 *            是否设置灰色背景色
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
			cg.zhu(title1, title2, dataset, w, h);
		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}
	
	@SuppressWarnings("rawtypes")
	private void p_drawchartMultiLineMonth(Hashtable hash, String title1, String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			String unit = "";
			String[] keys = (String[]) hash.get("key");
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
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
							Minute minute = new Minute(tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.DAY_OF_MONTH), tempCal.get(Calendar.MONTH) + 1, tempCal.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
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
}
