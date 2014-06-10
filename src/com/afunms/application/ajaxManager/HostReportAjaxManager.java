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
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.CpuCollectEntity;
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

public class HostReportAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
	
	@Override
	public void execute(String action) {
		if(action.equals("getNodeListForPing")) {
			getNodeListForPing();
		}else if(action.equals("exportHostPingWord")){
			exportHostPingWord();
		}else if(action.equals("getMemoryDetailList")){
			getMemoryDetailList();
		}else if(action.equals("exportHostPingExcel")){
			exportHostPingExcel();
		}else if(action.equals("exportHostPingPdf")){
			exportHostPingPdf();
		}else if(action.equals("exportMemoryWord")){
			try {
				exportMemoryWord();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(action.equals("exportMemoryExcel")){
			try {
				exportMemoryExcel();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(action.equals("exportMemoryPdf")){
			try {
				exportMemoryPdf();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(action.equals("getDiskDetailList")){
			getDiskDetailList();
		}else if(action.equals("exportDiskWord")){
			exportDiskWord();
		}else if(action.equals("exportDiskExcel")){
			exportDiskExcel();
		}else if(action.equals("exportDiskPdf")){
			exportDiskPdf();
		}else if(action.equals("getCpuDetailList")){
			getCpuDetailList();
		}else if(action.equals("exportCpuWord")){
			exportCpuWord();
		}else if(action.equals("exportCpuExcel")){
			exportCpuExcel();
		}else if(action.equals("exportCpuPdf")){
			exportCpuPdf();
		}else if(action.equals("getEventDetailList")){
			getEventDetailList();
		}else if(action.equals("exportEventWord")){
			exportEventWord();
		}else if(action.equals("exportEventExcel")){
			exportEventExcel();
		}else if(action.equals("exportEventPdf")){
			exportEventPdf();
		}else if(action.equals("exportHostReport")){
			exportHostReport();
		}else if(action.equals("exportHostMultiReport")){
			exportHostMultiReport();
		}else if(action.equals("getPingDetailForHost")){
			getPingDetailForHost();
		}else if(action.equals("hostchoceList")){
			hostchoceList();
		}else if(action.equals("exportHostChoceReport")){
			exportHostChoceReport();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "null" })
	private void exportHostChoceReport(){
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
		String fileName = "";

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
		String runAppraise = "良";// 运行评价
		int levelOneAlarmNum = 0;// 告警的条数
		int levelTwoAlarmNum = 0;// 告警的条数
		int levelThreeAlarmNum = 0;// 告警的条数
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					Hashtable reporthash = new Hashtable();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
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
					ip = node.getIpAddress();
					equipname = node.getAlias();
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						memhash = hostlastmanager.getMemory_share(ip, "Memory", startdate, todate);
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
						MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						memhash = memoryInfoService.getCurrMemoryListInfo();
						// 取出当前的硬盘信息
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
						PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						pdata = pingInfoService.getPingInfo();
						// CPU信息
						CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
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
					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
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
			
			request.setAttribute("allreporthash", allreporthash);
			request.setAttribute("startdate", starttime);
			request.setAttribute("todate", totime);
			request.setAttribute("username", username);
			request.setAttribute("positionname", positionname);
			request.setAttribute("oids", oids);
			request.setAttribute("runAppraise", runAppraise);
			
			String file = "temp/networknms_report.doc";// 保存到项目文件夹下的指定文件夹
			fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
			Hashtable dataHash = new Hashtable();
			dataHash.put("businessAnalytics", "");// 运行分析
			dataHash.put("runAppraise", runAppraise);// 运行评价
			report.setDataHash(dataHash);
			report.createReport_hostchoce(starttime, totime, fileName, username, positionname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void hostchoceList(){
		HostNodeDao dao = new HostNodeDao();
		List list = new ArrayList();
		list = (List)dao.loadHostByFlag(1);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPingDetailForHost(){
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
				dao.close();

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
		out.print(jsonString.toString());
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportHostMultiReport(){
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
		String type = "";
		String typename = "";
		String equipname = "";
		String equipnameDoc = "";

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
		int utilvalue = 0;
		String fileName = "";
		try {
			ip = getParaValue("ipaddress");
			type = getParaValue("type");
			HostNodeDao dao = new HostNodeDao();
			HostNode node = (HostNode) dao.findByCondition("ip_address", ip).get(0);
			dao.close();
			equipname = node.getAlias() + "(" + ip + ")";
			equipnameDoc = node.getAlias();
			String newip = SysUtil.doip(ip);
			memhash = hostlastmanager.getMemory_share(ip, "Memory", starttime, totime);
			diskhash = hostlastmanager.getDisk_share(ip, "Disk", starttime, totime);
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
			Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (ConnectUtilizationhash.get("max") != null) {
				ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
			// 得到事件列表
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
			s.append(" and nodeid=" + node.getId());
			EventListDao eventdao = new EventListDao();
			List infolist = eventdao.findByCriteria(s.toString());
			if (infolist != null && infolist.size() > 0) {
				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("physicalmemory") || eventlist.getSubentity().equalsIgnoreCase("virtualmemory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("diskperc")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx") || eventlist.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")) {
						utilvalue = utilvalue + 1;
					} 
				}
			}
			String grade = "良";
			if (pingvalue + cpuvalue + memvalue + diskvalue + utilvalue >= 3) {
				grade = "差";
			} else if (pingvalue + cpuvalue + memvalue + diskvalue + utilvalue < 3 && pingvalue + cpuvalue + memvalue + diskvalue + utilvalue > 0) {
				grade = "良";
			} else {
				grade = "优";
			}
			reporthash.put("pingvalue", pingvalue);
			reporthash.put("memvalue", memvalue);
			reporthash.put("diskvalue", diskvalue);
			reporthash.put("cpuvalue", cpuvalue);
			reporthash.put("utilvalue", utilvalue);

//			request.setAttribute("hash", hash);
//			request.setAttribute("max", maxhash);
//			request.setAttribute("memmaxhash", memmaxhash);
//			request.setAttribute("memavghash", memavghash);
//			request.setAttribute("diskhash", diskhash);
//			request.setAttribute("memhash", memhash);
//			request.setAttribute("grade", grade);
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
			String timeType = "minute";
			
			reporthash.put("ConnectUtilizationImgPath", SysUtil.doip(ip) + "ConnectUtilization");
			reporthash.put("cpuImgPath", SysUtil.doip(ip) + "cpu");
			reporthash.put("memoryImgPath", SysUtil.doip(ip) + "memory");
			reporthash.put("diskImgPath", SysUtil.doip(ip) + "disk");
			reporthash.put("diskTimeSpaceImgPath", SysUtil.doip(ip) + "diskTimeSpace");
			
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);
			pollMonitorManager.p_draw_line(cpuhash, "CPU利用率", newip + "cpu", 750, 150);
			pollMonitorManager.draw_column(diskhash, "", newip + "disk", 750, 150);
			pollMonitorManager.p_drawchartMultiLine(memoryhash[0], "内存利用率", newip + "memory", 750, 150);
			
			//画图
			Hashtable[] historyDiskHash = null;
			try {
				historyDiskHash = hostmanager.getDiskByIp(node.getIpAddress(), "Disk", starttime, totime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			p_drawchartMultiLine(historyDiskHash[0], "", SysUtil.doip(node.getIpAddress()) + "diskTimeSpace", 750, 150);// 画图
			
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = getParaValue("str"); 
			if ("0".equals(str)) {
				report.createReport_host("temp/host_multi.xls");// excel综合报表
				fileName = report.getFileName();
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/host_multi.doc"; 
					fileName = ResourceCenter.getInstance().getSysPath() + file; 
					report1.createReport_hostDoc(fileName);// word综合报表
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/host_analysis.doc";// 保存到项目文件夹下的指定文件夹
					fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_hostNewDoc(fileName);// word业务分析表
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/host_analysis.pdf"; 
					fileName = ResourceCenter.getInstance().getSysPath() + file; 
					report1.createReport_hostNewPDF(fileName);// pdf业务分析表
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/host_multi.pdf"; 
					fileName = ResourceCenter.getInstance().getSysPath() + file; 
					report1.createReport_hostPDF(fileName);// pdf综合报表
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
	
	@SuppressWarnings({ "rawtypes", "unchecked", "null" })
	private void exportHostReport(){
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

		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Vector pdata = new Vector();
		Vector cpuVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag();
		String filename = "";
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					ip = node.getIpAddress();
					equipname = node.getAlias() + "(" + ip + ")";
					String newip = SysUtil.doip(ip);
					// 从lastcollectdata中取最新的cpu利用率，内存利用率，磁盘利用率数据
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						Hashtable pingdata = ShareData.getPingdata();
						Hashtable sharedata = ShareData.getSharedata();
						memhash = hostlastmanager.getMemory_share(ip, "Memory", startdate, todate);
						diskhash = hostlastmanager.getDisk_share(ip, "Disk", startdate, todate);
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
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
						PingInfoService pingInfoService = new PingInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						pdata = pingInfoService.getPingInfo();
						// CPU信息
						CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
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

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(ip, "Ping", "ConnectUtilization", starttime, totime);
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
					//画图
		            Hashtable[] historyDiskHash = null;
		            try {
		                historyDiskHash = hostmanager.getDiskByIp(node.getIpAddress(), "Disk", starttime, totime);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		            p_drawchartMultiLine(historyDiskHash[0], "", SysUtil.doip(node.getIpAddress()) + "diskTimeSpace", 750, 150);// 画图

					Hashtable reporthash = new Hashtable();
					
					reporthash.put("ConnectUtilizationImgPath", SysUtil.doip(ip) + "ConnectUtilization");
		            reporthash.put("cpuImgPath", SysUtil.doip(ip) + "cpu");
		            reporthash.put("memoryImgPath", SysUtil.doip(ip) + "memory");
		            reporthash.put("diskImgPath", SysUtil.doip(ip) + "disk");
		            reporthash.put("diskTimeSpaceImgPath", SysUtil.doip(ip) + "diskTimeSpace");

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
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
				AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_hostall("/temp/hostnms_report.xls");
				filename = report.getFileName();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.print(filename);
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void exportEventPdf(){
		String file = "/temp/host_event.pdf";
		String fileName = ResourceCenter.getInstance().getSysPath() + file;
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
			Paragraph title = new Paragraph("主机服务器事件报表", titleFont);
			// 设置标题格式对齐方式
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
			PdfPTable aTable = new PdfPTable(13);
			int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55, 55 };
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
			PdfPCell cell9 = new PdfPCell(new Phrase("内存事件", contextFont));
			PdfPCell cell10 = new PdfPCell(new Phrase("磁盘事件", contextFont));
			PdfPCell cell11 = new PdfPCell(new Phrase("CPU事件", contextFont));
			PdfPCell cell12 = new PdfPCell(new Phrase("流速事件", contextFont));

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
					String utilvalue = (String) _eventlist.get(11);
					
					PdfPCell cell13 = new PdfPCell(new Phrase(i + 1 + ""));
					PdfPCell cell14 = new PdfPCell(new Phrase(ip));
					PdfPCell cell15 = new PdfPCell(new Phrase(equname));
					PdfPCell cell16 = new PdfPCell(new Phrase(osname));
					PdfPCell cell17 = new PdfPCell(new Phrase(sum));
					PdfPCell cell18 = new PdfPCell(new Phrase(levelone));
					PdfPCell cell19 = new PdfPCell(new Phrase(leveltwo));
					PdfPCell cell20 = new PdfPCell(new Phrase(levelthree));
					PdfPCell cell21 = new PdfPCell(new Phrase(pingvalue));
					PdfPCell cell22 = new PdfPCell(new Phrase(memvalue));
					PdfPCell cell23 = new PdfPCell(new Phrase(diskvalue));
					PdfPCell cell24 = new PdfPCell(new Phrase(cpuvalue));
					PdfPCell cell25 = new PdfPCell(new Phrase(utilvalue));
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
					cell25.setHorizontalAlignment(Element.ALIGN_CENTER);
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

						cellEventList.addElement(new Paragraph( (j+1) + ".  " + eventContentList.get(j) + ""));
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
		String starttime = (String)session.getAttribute("starttime");
		String totime = (String)session.getAttribute("totime");

		List eventlist = (List) session.getAttribute("eventlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", eventlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostevent("/temp/hostevent_report.xls");
		
		out.print(report.getFileName());
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void exportEventWord(){
		//报表模块中被调用
		String file = "/temp/host_event.doc"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {

			String starttime = (String)session.getAttribute("starttime");
			String totime = (String)session.getAttribute("totime");

			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("主机服务器事件报表", titleFont);
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
			Table aTable = new Table(13);
			int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55, 55};
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
			Cell cell9 = new Cell("内存事件");
			Cell cell10 = new Cell("磁盘事件");
			Cell cell11 = new Cell("CPU事件");
			Cell cell12 = new Cell("流速事件");

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
					String utilvalue = (String) _eventlist.get(11);
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
					Cell cell25 = new Cell(utilvalue);
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
					cell25.setHorizontalAlignment(Element.ALIGN_CENTER);

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
				dao.close();
				if (node == null)
					continue;
				EventListDao eventdao = new EventListDao();
				// 得到事件列表
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' and level1>0");
				s.append(" and nodeid=" + node.getId());

				List infolist = eventdao.findByCriteria(s.toString());

				int levelone = 0;
				int levletwo = 0;
				int levelthree = 0;
				int pingvalue = 0;
				int memvalue = 0;
				int diskvalue = 0;
				int cpuvalue = 0;
				int utilvalue = 0;
				String content = "";
				List eventContentList = new ArrayList();

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					content = eventlist.getContent();
					Calendar recordCalendar = eventlist.getRecordtime();
					Date recordTime = recordCalendar.getTime();

					eventContentList.add(content + " ( " + df.format(recordTime) + " 至 " + eventlist.getLasttime() + " )");
					if (eventlist.getLevel1() == null)
						continue;
					if (eventlist.getLevel1() == 1) {
						levelone = levelone + 1;
					} else if (eventlist.getLevel1() == 2) {
						levletwo = levletwo + 1;
					} else if (eventlist.getLevel1() == 3) {
						levelthree = levelthree + 1;
					}

					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("physicalmemory") || eventlist.getSubentity().equalsIgnoreCase("virtualmemory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("diskperc")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx") || eventlist.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")) {
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
				ipeventList.add(memvalue + "");
				ipeventList.add(diskvalue + "");
				ipeventList.add(cpuvalue + "");
				ipeventList.add(utilvalue + "");
				ipeventList.add(eventContentList);
				orderList.add(ipeventList);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("one") || orderflag.equalsIgnoreCase("two") || orderflag.equalsIgnoreCase("three") || orderflag.equalsIgnoreCase("ping") || orderflag.equalsIgnoreCase("mem") || orderflag.equalsIgnoreCase("disk") || orderflag.equalsIgnoreCase("cpu") || orderflag.equalsIgnoreCase("sum")) {
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
	            String memvalue = (String)_eventlist.get(8);
	            String diskvalue = (String)_eventlist.get(9);
	            String cpuvalue = (String)_eventlist.get(10);
	            String utilvalue = (String)_eventlist.get(11);
	            
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
				
				jsonString.append("\"memvalue\":\"");
				jsonString.append(memvalue);
				jsonString.append("\",");
				
				jsonString.append("\"diskvalue\":\"");
				jsonString.append(diskvalue);
				jsonString.append("\",");
				
				jsonString.append("\"cpuvalue\":\"");
				jsonString.append(cpuvalue);
				jsonString.append("\",");

				jsonString.append("\"utilvalue\":\"");
				jsonString.append(utilvalue);
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
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private void exportCpuPdf(){
		//报表模块中被调用
		String file = "/temp/host_cpu.pdf"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {

			String starttime = (String)session.getAttribute("starttime");
			String totime = (String)session.getAttribute("totime");

			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);//
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 12, Font.NORMAL);
			Paragraph title = new Paragraph("主机服务器CPU利用率报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);

			// chenyuanhua add
			Paragraph blank = new Paragraph("\n");
			document.add(blank);

			Font timeFont = new Font(bfChinese, 10, Font.NORMAL);//
			Paragraph createReportTime = new Paragraph("报表生成时间：" + sdf.format(new Date()), timeFont);
			createReportTime.setAlignment(Element.ALIGN_CENTER);
			document.add(createReportTime);

			Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
			timeSapce.setAlignment(Element.ALIGN_CENTER);
			document.add(timeSapce);
			document.add(blank);
			// end
			// 设置 Table 表格
			List cpulist = (List) session.getAttribute("cpulist");// cpu利用率

			Table aTable = new Table(6);
			this.setTableFormat(aTable);
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
					Cell cell12 = new Cell(new Phrase(equname));
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

			for (int i = 0; i < cpulist.size(); i++) {

				Hashtable cpuhash = (Hashtable) cpulist.get(i);
				String ip = (String) cpuhash.get("ipaddress");
				// 设置图片
				String newip = ip.replace(".", "_");
				String imgPath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/" + newip + "cpu" + ".png";
				// 向sheet里面增加图片,0,0,5,1分别代表列,行,图片宽度占位多少个列,高度占位多少个行
				Paragraph chartTitle = new Paragraph(ip + "Cpu曲线图", contextFont);
				chartTitle.setAlignment(Element.ALIGN_CENTER);
				document.add(chartTitle);
				Image img = Image.getInstance(imgPath);
				img.setAlignment(Image.LEFT);// 设置图片显示位置
				img.scalePercent(69);
				document.add(img);
			}
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
	private void exportCpuExcel(){
		String starttime = (String)session.getAttribute("starttime");
		String totime =(String) session.getAttribute("totime");

		List orderList = (List) session.getAttribute("cpulist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("cpulist", orderList);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostcpu("/temp/host_cpu.xls");
		
		out.print(report.getFileName());
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private void exportCpuWord(){
		String file = "/temp/host_cpu.doc"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {

			String starttime = (String)session.getAttribute("starttime");
			String totime = (String)session.getAttribute("totime");
			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("主机服务器CPU利用率报表", titleFont);
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
			List cpulist = (List) session.getAttribute("cpulist");// cpu利用率
			Table aTable = new Table(6);
			this.setTableFormat(aTable);
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
			Paragraph graghTitle = new Paragraph("主机Cpu曲线图");
			graghTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(graghTitle);
			document.add(new Paragraph("\n"));
			for (int i = 0; i < cpulist.size(); i++) {
				Hashtable cpuhash = (Hashtable) cpulist.get(i);
				String ip = (String) cpuhash.get("ipaddress");
				String newip = ip.replace(".", "_");
				String memPath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/" + newip + "cpu" + ".png";
				Image img = Image.getInstance(memPath);
				img.setAbsolutePosition(0, 0);
				img.setAlignment(Image.LEFT);// 设置图片显示位置
				img.scalePercent(90);
				document.add(img);
				document.add(new Paragraph(""));
				Paragraph ipString = new Paragraph(ip);
				ipString.setAlignment(Element.ALIGN_CENTER);
				document.add(ipString);
				document.add(new Paragraph("\n"));
			}
			document.add(new Paragraph("\n"));
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.println(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void getCpuDetailList(){
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
		String[] ids = null;
		if(id != null){
			ids = id.split(";");
		}

		if (ids != null && ids.length > 0) {
			session.setAttribute("ids", ids);
		} else {
			ids = (String[]) session.getAttribute("ids");
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
				String newip = node.getIpAddress().replace(".", "_");
				Hashtable cpuhash = null;
				try {
					cpuhash = hostmanager.getCategory(node.getIpAddress(), "CPU", "Utilization", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				p_draw_line(cpuhash, "", newip + "cpu", 750, 150);// 画图

				cpuhash.put("ipaddress", node.getIpAddress());
				allcpuhash.put(node.getIpAddress(), cpuhash);
				orderList.add(cpuhash);
			}

		}
		if (orderflag.equalsIgnoreCase("cpuavg") || orderflag.equalsIgnoreCase("cpumax")) {
			orderList = (List) session.getAttribute("cpulist");
		}
		List list = new ArrayList();
		if (orderList != null && orderList.size() > 0) {
			for (int m = 0; m < orderList.size(); m++) {
				Hashtable cpuhash = (Hashtable) orderList.get(m);
				for (int n = m + 1; n < orderList.size(); n++) {
					Hashtable _cpuhash = (Hashtable) orderList.get(n);
					 if (orderflag.equalsIgnoreCase("cpuavg")) {
						String avgcpu = "";
						if (cpuhash.get("avgcpucon") != null) {
							avgcpu = (String) cpuhash.get("avgcpucon");
						}
						String _avgcpu = "";
						if (_cpuhash.get("avgcpucon") != null) {
							_avgcpu = (String) _cpuhash.get("avgcpucon");
						}
						if (new Double(avgcpu.substring(0, avgcpu.length() - 2)).doubleValue() < new Double(_avgcpu.substring(0, _avgcpu.length() - 2)).doubleValue()) {
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
						if (new Double(cpumax.substring(0, avgcpu.length() - 2)).doubleValue() < new Double(_cpumax.substring(0, _avgcpu.length() - 2)).doubleValue()) {
							orderList.remove(m);
							orderList.add(m, _cpuhash);
							orderList.remove(n);
							orderList.add(n, cpuhash);
							cpuhash = _cpuhash;
							_cpuhash = null;
						}
					}
				}
				list.add(cpuhash);
				cpuhash = null;
			}
		}
		session.setAttribute("allcpuhash", allcpuhash);
		session.setAttribute("cpulist", list);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if(list != null && list.size() > 0){
			HostNodeDao dao = null;
			for(int i = 0; i < list.size(); i++){
				dao = new HostNodeDao();
				Hashtable cpuhash = (Hashtable)list.get(i);
				String ip = (String)cpuhash.get("ipaddress");
				if(cpuhash == null) cpuhash = new Hashtable();
				String cpumax="";
				String avgcpu="";
				if(cpuhash.get("max")!=null){
					cpumax = (String)cpuhash.get("max");
				}
				if(cpuhash.get("avgcpucon")!=null){
					avgcpu = (String)cpuhash.get("avgcpucon");
				}
				HostNode node = (HostNode)dao.findByCondition("ip_address",ip).get(0);
				dao.close();
				String equname = node.getAlias();				
				String osname = node.getType();
	            
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
				
				jsonString.append("\"avg\":\"");
				jsonString.append(avgcpu);
				jsonString.append("\",");
				
				jsonString.append("\"max\":\"");
				jsonString.append(cpumax);
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
	private void exportDiskPdf(){
		String file = "/temp/cipanbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			String starttime = (String)session.getAttribute("starttime");
			String totime =(String) session.getAttribute("totime");
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
			Paragraph title = new Paragraph("主机服务器磁盘利用率报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Phrase("\n"));
			// chenyuanhua add
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Font timeFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph createReportTime = new Paragraph("报表生成时间：" + sdf.format(new Date()), timeFont);
			createReportTime.setAlignment(Element.ALIGN_CENTER);
			document.add(createReportTime);
			document.add(new Phrase("\n"));
			Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
			timeSapce.setAlignment(Element.ALIGN_CENTER);
			document.add(timeSapce);
			document.add(new Phrase("\n"));
			// end
			// 设置 Table 表格
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
					PdfPCell cell12 = new PdfPCell(new Phrase(equname));
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
			if(disklist != null && disklist.size() > 0){
				document.add(new Paragraph("\n"));
				Paragraph graghTitle = new Paragraph("主机磁盘曲线图");
				graghTitle.setAlignment(Element.ALIGN_CENTER);
				document.add(graghTitle);
				document.add(new Paragraph("\n"));
				String sb = new String();
				for (int j = 0; j < disklist.size(); j++) {
					List diskhash = (List) disklist.get(j);
					String ip = (String) diskhash.get(0);
					if(sb.contains(ip)){
						continue;
					}
					sb = sb + ip;
					String newip = ip.replace(".", "_");
					String memPath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/" + newip + "disk" + ".png";
					Image img = Image.getInstance(memPath);
					img.setAlignment(Image.LEFT);// 设置图片显示位置
					img.scalePercent(69);
					document.add(img);
					Paragraph chartTitle = new Paragraph(ip + "磁盘曲线图", contextFont);
					chartTitle.setAlignment(Element.ALIGN_CENTER);
					document.add(chartTitle);
					document.add(new Paragraph("\n"));
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
	private void exportDiskExcel(){
		String starttime = (String)session.getAttribute("starttime");
		String totime =(String) session.getAttribute("totime");

		List memlist = (List) session.getAttribute("disklist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("disklist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostdisk("/temp/hostdisk_report.xls");
		
		out.print(report.getFileName());
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void exportDiskWord(){
		String file = "/temp/cipanbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			String starttime = (String)	session.getAttribute("starttime");
			String totime = (String) session.getAttribute("totime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			RtfWriter2.getInstance(document, new FileOutputStream(fileName));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Paragraph title = new Paragraph("主机服务器磁盘利用率报表", titleFont);
			// 设置标题格式对齐方式
			title.setAlignment(Element.ALIGN_CENTER);
			// title.setFont(titleFont);
			document.add(title);
			// chenyuanhua add
			Paragraph blank = new Paragraph("\n");
			document.add(blank);
			Font timeFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph createReportTime = new Paragraph("报表生成时间：" + sdf.format(new Date()), timeFont);
			createReportTime.setAlignment(Element.ALIGN_CENTER);
			document.add(createReportTime);
			document.add(blank);
			Paragraph timeSapce = new Paragraph("统计时间段：" + starttime + " 至 " + totime, timeFont);
			timeSapce.setAlignment(Element.ALIGN_CENTER);
			document.add(timeSapce);
			document.add(blank);
			// end
			// 设置 Table 表格
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
				String flagIp = "";
				for (int i = 0; i < disklist.size(); i++) {
					List _disklist = (List) disklist.get(i);
					String ip = (String) _disklist.get(0);
					if (!flagIp.equals(ip) && i != 0) {

						Cell cellBlank = new Cell();
						cellBlank.setColspan(8);
						cellBlank.setBackgroundColor(Color.GRAY);
						aTable.addCell(cellBlank);

					}
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

					flagIp = ip;

				}

			}
			document.add(aTable);
			if(disklist != null && disklist.size() > 0){
				document.add(new Paragraph("\n"));
				Paragraph graghTitle = new Paragraph("主机磁盘曲线图");
				graghTitle.setAlignment(Element.ALIGN_CENTER);
				document.add(graghTitle);
				document.add(new Paragraph("\n"));
				String sb = new String();
				for (int j = 0; j < disklist.size(); j++) {
					List diskhash = (List) disklist.get(j);
					String ip = (String) diskhash.get(0);
					if(sb.contains(ip)){
						continue;
					}
					sb = sb + ip;
					String newip = ip.replace(".", "_");
					String memPath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/" + newip + "disk" + ".png";
					Image img = Image.getInstance(memPath);
					img.setAbsolutePosition(0, 0);
					img.setAlignment(Image.LEFT);// 设置图片显示位置
					img.scalePercent(90);
					document.add(img);
					document.add(new Paragraph(""));
					Paragraph ipString = new Paragraph(ip);
					ipString.setAlignment(Element.ALIGN_CENTER);
					document.add(ipString);
					document.add(new Paragraph("\n"));
				}
				document.add(new Paragraph("\n"));
			}
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void getDiskDetailList(){
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
		String runmodel = PollingEngine.getCollectwebflag();

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				//画图
				Hashtable[] historyDiskHash = null;
				try {
					historyDiskHash = hostmanager.getDiskByIp(node.getIpAddress(), "Disk", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				p_drawchartMultiLine(historyDiskHash[0], "", SysUtil.doip(node.getIpAddress()) + "disk", 750, 150);// 画图
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
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo();
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
						if (new Double(allsizevalue.substring(0, allsizevalue.length() - 2)).doubleValue() < new Double(_allsizevalue.substring(0, _allsizevalue.length() - 2)).doubleValue()) {
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
						if (new Double(utilization.substring(0, utilization.length() - 2)).doubleValue() < new Double(_utilization.substring(0, _utilization.length() - 2)).doubleValue()) {
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
		session.setAttribute("disklist", list);
		session.setAttribute("starttime",startdate);
		session.setAttribute("totime",todate);
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List _disklist = (List)list.get(i);
				String ip = (String)_disklist.get(0);
				String equname = (String)_disklist.get(1);	
				String osname = (String)_disklist.get(2);
				String name = (String)_disklist.get(3);
	            String allsizevalue = (String)_disklist.get(4);
	            String usedsizevalue = (String)_disklist.get(5);
				String utilization = (String)_disklist.get(6);
	            
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
				
				jsonString.append("\"diskname\":\"");
				jsonString.append(name);
				jsonString.append("\",");
				
				jsonString.append("\"total\":\"");
				jsonString.append(allsizevalue);
				jsonString.append("\",");
				
				jsonString.append("\"use\":\"");
				jsonString.append(usedsizevalue);
				jsonString.append("\",");
				
				jsonString.append("\"utilization\":\"");
				jsonString.append(utilization);
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
	private void exportMemoryPdf() throws DocumentException, IOException{
		String file = "/temp/host_memory.pdf"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {

			String starttime = (String)session.getAttribute("starttime");
			String totime = (String)session.getAttribute("totime");

			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph title = new Paragraph("主机服务器内存利用率报表", titleFont);
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
					PdfPCell cell12 = new PdfPCell(new Phrase(equname));
					PdfPCell cell13 = new PdfPCell(new Phrase(osname));
					PdfPCell cell14 = new PdfPCell(new Phrase(Capability));
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
			Paragraph graghTitle = new Paragraph("主机内存曲线图");
			graghTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(graghTitle);
			document.add(new Paragraph("\n"));
			for (int i = 0; i < memlist.size(); i++) {
				List _memlist = (List) memlist.get(i);
				String ip = (String) _memlist.get(0);
				String newip = ip.replace(".", "_");
				String memPath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/" + newip + "memory" + ".png";
				Image img = Image.getInstance(memPath);
				img.setAlignment(Image.LEFT);// 设置图片显示位置
				img.scalePercent(69);
				document.add(img);
				document.add(new Paragraph(""));
				Paragraph ipString = new Paragraph(ip);
				ipString.setAlignment(Element.ALIGN_CENTER);
				document.add(ipString);
				document.add(new Paragraph("\n"));
			}
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
	private void exportMemoryExcel() throws DocumentException, IOException{
		String starttime = (String)session.getAttribute("starttime");
		String totime = (String)session.getAttribute("totime");
		List memlist = (List) session.getAttribute("memlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("memlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostmem("/temp/host_memory.xls");
		out.print(report.getFileName());
		out.flush();
	}
	
	@SuppressWarnings("rawtypes")
	private void exportMemoryWord() throws DocumentException, IOException{
		String file = "/temp/host_memory.doc"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		

		String starttime = (String)session.getAttribute("starttime");
		String totime = (String)session.getAttribute("totime");

		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(fileName));
		document.open();
		
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		
		Paragraph title = new Paragraph("主机服务器内存利用率报表", titleFont);
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
		Paragraph graghTitle = new Paragraph("主机内存曲线图");
		graghTitle.setAlignment(Element.ALIGN_CENTER);
		document.add(graghTitle);
		document.add(new Paragraph("\n"));
		for (int i = 0; i < memlist.size(); i++) {
			List _memlist = (List) memlist.get(i);
			String ip = (String) _memlist.get(0);
			String newip = ip.replace(".", "_");
			String memPath = ResourceCenter.getInstance().getSysPath() + "/resource/image/jfreechart/" + newip + "memory" + ".png";
			Image img = Image.getInstance(memPath);
			img.setAbsolutePosition(0, 0);
			img.setAlignment(Image.LEFT);// 设置图片显示位置
			img.scalePercent(90);
			document.add(img);
			document.add(new Paragraph(""));
			Paragraph ipString = new Paragraph(ip);
			ipString.setAlignment(Element.ALIGN_CENTER);
			document.add(ipString);
			document.add(new Paragraph("\n"));
		}
		document.add(new Paragraph("\n"));
		document.close();
	
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getMemoryDetailList(){
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
				String ip = node.getIpAddress();
				String newip = SysUtil.doip(ip);;
				Hashtable realTimeMemoryHash = new Hashtable();
				try {
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						realTimeMemoryHash = hostlastmanager.getMemory_share(node.getIpAddress(), "Memory", starttime, totime);
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						MemoryInfoService memoryInfoService = new MemoryInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						realTimeMemoryHash = memoryInfoService.getCurrMemoryListInfo();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable[] historyMemoryHash = null;
				try {
					historyMemoryHash = hostmanager.getMemory(node.getIpAddress(), "Memory", starttime, totime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				p_drawchartMultiLine(historyMemoryHash[0], "", newip + "memory", 750, 150);// 画图

				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("node", node);
				ipmemhash.put("memoryhash", historyMemoryHash);
				ipmemhash.put("ipmemhash", realTimeMemoryHash);
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("phyavg") || orderflag.equalsIgnoreCase("phymax") || orderflag.equalsIgnoreCase("viravg") || orderflag.equalsIgnoreCase("virmax")) {
			returnList = (List) session.getAttribute("memlist");
		} else {
			List memlist = orderList;
			if (memlist != null && memlist.size() > 0) {
				for (int i = 0; i < memlist.size(); i++) {
					Hashtable _memhash = (Hashtable) memlist.get(i);
					HostNode node = (HostNode) _memhash.get("node");
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

		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipmemList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipmemList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("phyavg")) {
						String avgmem = "";
						if (ipmemList.get(4) != null) {
							avgmem = (String) ipmemList.get(4);
						}

						String _avgmem = "";
						if (ipmemList.get(4) != null) {
							_avgmem = (String) _ipmemList.get(4);
						}
						if (new Double(avgmem.substring(0, avgmem.length() - 2)).doubleValue() < new Double(_avgmem.substring(0, _avgmem.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					} else if (orderflag.equalsIgnoreCase("phymax")) {
						String memmax = "";
						if (ipmemList.get(5) != null) {
							memmax = (String) ipmemList.get(5);
						}

						String _memmax = "";
						if (ipmemList.get(5) != null) {
							_memmax = (String) _ipmemList.get(5);
						}
						if (new Double(memmax.substring(0, memmax.length() - 2)).doubleValue() < new Double(_memmax.substring(0, _memmax.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					} else if (orderflag.equalsIgnoreCase("viravg")) {
						String avgmem = "";
						if (ipmemList.get(7) != null) {
							avgmem = (String) ipmemList.get(7);
						}
						String _avgmem = "";
						if (ipmemList.get(7) != null) {
							_avgmem = (String) _ipmemList.get(7);
						}
						if (new Double(avgmem.substring(0, avgmem.length() - 2)).doubleValue() < new Double(_avgmem.substring(0, _avgmem.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					} else if (orderflag.equalsIgnoreCase("virmax")) {
						String memmax = "";
						if (ipmemList.get(8) != null) {
							memmax = (String) ipmemList.get(8);
						}
						String _memmax = "";
						if (ipmemList.get(8) != null) {
							_memmax = (String) _ipmemList.get(8);
						}
						if (new Double(memmax.substring(0, memmax.length() - 2)).doubleValue() < new Double(_memmax.substring(0, _memmax.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipmemList);
							returnList.remove(n);
							returnList.add(n, ipmemList);
							ipmemList = _ipmemList;
							_ipmemList = null;
						}
					}
				}
				list.add(ipmemList);
				ipmemList = null;
			}
		}
		
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		session.setAttribute("memlist", list);
		session.setAttribute("startdate", startdate);
		session.setAttribute("todate", todate);
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List _memlist = (List)list.get(i);
				String ip = (String)_memlist.get(0);
				String equname = (String)_memlist.get(1);	
				String osname = (String)_memlist.get(2);
				String Capability = (String)_memlist.get(3);
	            String maxvalue = (String)_memlist.get(5);
	            String avgvalue = (String)_memlist.get(4);
				String Capability1 = (String)_memlist.get(6);
	            String maxvalue1 = (String)_memlist.get(8);
	            String avgvalue1 = (String)_memlist.get(7);	
	            
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
				
				jsonString.append("\"phsyicMemory\":\"");
				jsonString.append(Capability);
				jsonString.append("\",");
				
				jsonString.append("\"phsyicAvg\":\"");
				jsonString.append(avgvalue);
				jsonString.append("\",");
				
				jsonString.append("\"phsyicMax\":\"");
				jsonString.append(maxvalue);
				jsonString.append("\",");
				
				jsonString.append("\"vitrualMemory\":\"");
				jsonString.append(Capability1);
				jsonString.append("\",");
				
				jsonString.append("\"vitrualAvg\":\"");
				jsonString.append(avgvalue1);
				jsonString.append("\",");

				jsonString.append("\"vitrualMax\":\"");
				jsonString.append(maxvalue1);
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
		list = (List)dao.findByCondition(" where category=4" + sql);
		
		
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
	
	private void exportHostPingWord(){
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getHostPingDataForReport();
		}
		
		String file = "/temp/host_ping.doc"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {
			createHostPingDocContext(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportHostPingExcel(){
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getHostPingDataForReport();
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
		report.createReport_hostping("/temp/host_ping.xls");
		
		out.print(report.getFileName());
		out.flush();
	}
	
	private void exportHostPingPdf(){
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getHostPingDataForReport();
		}
		
		String file = "/temp/host_ping.pdf"; 
		String fileName = ResourceCenter.getInstance().getSysPath() + file; 
		try {
			createHostPingPdfContext(fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		out.print(fileName);
		out.flush();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getHostPingDataForReport(){
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
	public void createHostPingPdfContext(String file) throws DocumentException, IOException {
		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		List pinglist = (List) session.getAttribute("pinglist");
		
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
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		document.add(new Paragraph("\n"));
		
		title = new Paragraph("报表生成时间:" + df.format(new Date()), contextFont);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		title = new Paragraph("数据统计时间段: " + starttime + " 至 " + totime, contextFont);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);

		// 设置 Table 表格
		document.add(new Paragraph("\n"));
		PdfPTable aTable = new PdfPTable(8);
		int width[] = { 50, 50, 50, 70, 50, 50, 50, 50 };
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
				PdfPCell cell7 = new PdfPCell(new Phrase(equname));
				PdfPCell cell8 = new PdfPCell(new Phrase(osname));
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
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(69);
		document.add(img);
		String responsetimepath = (String) session.getAttribute("responsetimepath");
		img = Image.getInstance(responsetimepath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(69);
		document.add(img);
		document.add(new Paragraph("\n"));
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}
	
	// 生成主机连通率word报表
	@SuppressWarnings("rawtypes")
	public void createHostPingDocContext(String file) throws DocumentException, IOException {
			String starttime = (String) session.getAttribute("starttime");
			String totime = (String) session.getAttribute("totime");
			List pingList = (List) session.getAttribute("pinglist");
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
			document.add(title);
			document.add(new Paragraph("\n"));
			title = new Paragraph("报表生成时间:" + df.format(new Date()), contextFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			title = new Paragraph("数据统计时间段: " + starttime + " 至 " + totime, contextFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph("\n"));
			// 设置 Table 表格
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
			if (pingList != null && pingList.size() > 0) {
				for (int i = 0; i < pingList.size(); i++) {
					List _pinglist = (List) pingList.get(i);
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
			// 导出连通率图片
			String pingpath = (String) session.getAttribute("pingpath");
			Image img = Image.getInstance(pingpath);
			img.setAbsolutePosition(0, 0);
			// 设置图片显示位置
			img.setAlignment(Image.LEFT);
			img.scalePercent(90);
			document.add(img);
			document.add(new Paragraph("\n"));
			// 导出响应时间图片
			String responsetimepath = (String) session.getAttribute("responsetimepath");
			img = Image.getInstance(responsetimepath);
			img.setAbsolutePosition(0, 0);
			img.setAlignment(Image.LEFT); 
			img.scalePercent(90);
			document.add(img);
			document.add(new Paragraph("\n"));
			document.add(aTable);
			document.close();
		}

	@SuppressWarnings("rawtypes")
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
						Vector obj = (Vector) vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
						ss.addOrUpdate(minute, v);
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
}
