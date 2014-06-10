package com.afunms.application.ajaxManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.Arith;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.polling.task.CheckLinkTask;
import com.afunms.system.model.User;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.LinkPerformanceDTO;

@SuppressWarnings("rawtypes")
public class LinkPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings("unchecked")
	private void getLinkPerformanceList(){
		List linkList = new ArrayList();
		LinkDao linkdao = new LinkDao();
		try{
			linkList = (List)linkdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(linkdao != null){
				linkdao.close();
			}
		}
		List linkPerformanceList = new ArrayList();
		DecimalFormat df = new DecimalFormat("#.##");
		String runmodel = PollingEngine.getCollectwebflag();
		if ("1".equals(runmodel)) {//调试用1
			// 采集与访问是集成模式
			for (int i = 0; i < linkList.size(); i++) {
				Link link = (Link) linkList.get(i);
				if (link.getLinktype() != -1) {
					LinkPerformanceDTO linkPerformanceDTO = getLinkPerformanceDTO(link);
					linkPerformanceList.add(linkPerformanceDTO);
				}
			}
		} else {
	System.out.println(linkList.size() + "----");
	System.out.println("123");
			// 取端口流速
			Vector end_vector = new Vector();
			Vector start_vector = new Vector();
			Hashtable interfaceHash = CheckLinkTask.getLinknodeInterfaceData(linkList);// 先采集所有，避免在for循环中多次采集
			for (int k = 0; k < linkList.size(); k++) {
				Link link = (Link) linkList.get(k);
				if (link.getLinktype() != -1) {
					int startId = link.getStartId();
					int endId = link.getEndId();
					String arisName = link.getLinkArisName(); // hipo add
					String startIndex = link.getStartIndex();
					String endIndex = link.getEndIndex();
					String start_inutilhdx = "0";
					String start_oututilhdx = "0";
					String start_inutilhdxperc = "0";
					String start_oututilhdxperc = "0";
					String end_inutilhdx = "0";
					String end_oututilhdx = "0";
					String end_inutilhdxperc = "0";
					String end_oututilhdxperc = "0";
					String starOper = "";
					String endOper = "";
					String pingValue = "0";
					String allSpeedRate = "0";
					com.afunms.polling.base.Node startnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByID(startId);
					com.afunms.polling.base.Node endnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByID(endId);
					if (startnode == null || endnode == null) {
						continue;
					}
					try {
						if (interfaceHash != null && interfaceHash.containsKey(startnode.getIpAddress())) {
							start_vector = (Vector) interfaceHash.get(startnode.getIpAddress());
						}
						if (interfaceHash != null && interfaceHash.containsKey(endnode.getIpAddress())) {
							end_vector = (Vector) interfaceHash.get(endnode.getIpAddress());
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if (startnode != null) {
						try {
							for (int i = 0; i < start_vector.size(); i++) {
								String[] strs = (String[]) start_vector.get(i);
								String index = strs[0];
								if (index.equalsIgnoreCase(startIndex)) {
									starOper = strs[3].trim();
									start_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									start_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									start_oututilhdxperc = strs[10].replaceAll("%", "");
									start_inutilhdxperc = strs[11].replaceAll("%", "");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (endnode != null) {
						try {
							for (int i = 0; i < end_vector.size(); i++) {
								String[] strs = (String[]) end_vector.get(i);
								String index = strs[0];
								if (index.equalsIgnoreCase(endIndex)) {
									endOper = strs[3].trim();
									;
									end_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									end_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");
									end_oututilhdxperc = strs[10].replaceAll("%", "");
									end_inutilhdxperc = strs[11].replaceAll("%", "");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					int downspeed = (Integer.parseInt(start_oututilhdx) + Integer.parseInt(end_inutilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
					int upspeed = (Integer.parseInt(start_inutilhdx) + Integer.parseInt(end_oututilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;

					double upperc = 0;
					try {
						if (start_oututilhdxperc != null && start_oututilhdxperc.trim().length() > 0 && end_inutilhdxperc != null && end_inutilhdxperc.trim().length() > 0)
							upperc = Arith.div((Double.parseDouble(start_oututilhdxperc) + Double.parseDouble(end_inutilhdxperc)), 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					double downperc = 0;
					try {
						if (start_inutilhdxperc != null && start_inutilhdxperc.trim().length() > 0 && end_oututilhdxperc != null && end_oututilhdxperc.trim().length() > 0)
							downperc = Arith.div((Double.parseDouble(start_inutilhdxperc) + Double.parseDouble(end_oututilhdxperc)), 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					int linkflag = 100;
					if ("".equals(starOper.trim()) || "".equals(endOper.trim()) || "down".equalsIgnoreCase(starOper) || "down".equalsIgnoreCase(endOper)) {
						linkflag = 0;
					}

					pingValue = String.valueOf(linkflag);
					LinkPerformanceDTO linkPerformanceDTO = new LinkPerformanceDTO();
					String name = link.getLinkName();
					int id = link.getId();
					allSpeedRate = String.valueOf(df.format(downperc + upperc));
					// 组装链路端口流速等信息
					linkPerformanceDTO.setId(id);
					linkPerformanceDTO.setName(name);
					linkPerformanceDTO.setArisName(arisName); // hipo add
					linkPerformanceDTO.setStartNode(startnode.getIpAddress());
					linkPerformanceDTO.setEndNode(endnode.getIpAddress());
					linkPerformanceDTO.setStratIndex(startIndex);
					linkPerformanceDTO.setEndIndex(endIndex);
					linkPerformanceDTO.setUplinkSpeed(upspeed + "");
					linkPerformanceDTO.setDownlinkSpeed(downspeed + "");
					linkPerformanceDTO.setPingValue(pingValue);
					linkPerformanceDTO.setAllSpeedRate(allSpeedRate);
					linkPerformanceList.add(linkPerformanceDTO);
				}
			}
		}
		
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if(linkPerformanceList != null && linkPerformanceList.size() > 0){
			for(int i = 0; i < linkPerformanceList.size() ; i++){
				LinkPerformanceDTO linkPerformanceDTO = (LinkPerformanceDTO)linkPerformanceList.get(i);
				jsonString.append("{\"nodeid\":\"");
				jsonString.append(linkPerformanceDTO.getId());
				jsonString.append("\",");

				jsonString.append("\"linkname\":\"");
				jsonString.append(linkPerformanceDTO.getName());
				jsonString.append("\",");

				jsonString.append("\"startip\":\"");
				jsonString.append(linkPerformanceDTO.getStartNode());
				jsonString.append("\",");

				jsonString.append("\"startport\":\"");
				jsonString.append(linkPerformanceDTO.getStratIndex());
				jsonString.append("\",");

				jsonString.append("\"endip\":\"");
				jsonString.append(linkPerformanceDTO.getEndNode());
				jsonString.append("\",");

				jsonString.append("\"endport\":\"");
				jsonString.append(linkPerformanceDTO.getEndIndex());
				jsonString.append("\",");

				jsonString.append("\"uplinkspeed\":\"");
				jsonString.append(linkPerformanceDTO.getUplinkSpeed());
				jsonString.append("\",");

				jsonString.append("\"downlinkspeed\":\"");
				jsonString.append(linkPerformanceDTO.getDownlinkSpeed());
				jsonString.append("\",");

				jsonString.append("\"pingvalue\":\"");
				jsonString.append(linkPerformanceDTO.getPingValue());
				jsonString.append("\",");

				jsonString.append("\"allspeedrate\":\"");
				jsonString.append(linkPerformanceDTO.getAllSpeedRate());
				jsonString.append("\"}");

				if (i != linkPerformanceList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total : " + linkPerformanceList.size() + "}");
	System.out.println(jsonString.toString());
		out.print(jsonString.toString());
		out.flush();
	}
	
	public LinkPerformanceDTO getLinkPerformanceDTO(Link link) {

		LinkPerformanceDTO linkPerformanceDTO = new LinkPerformanceDTO();

		try {
			
			//IP转换成名字显示
			String name = link.getLinkName();
			String nm[] = name.split("/");
			String nm0 = nm[0].split("_")[0];
			String nm1 = nm[1].split("_")[0];
			name = PollingEngine.getInstance().getNodeByIP(nm0).getAlias() + "_" + nm[0].split("_")[1] + "/" + PollingEngine.getInstance().getNodeByIP(nm1).getAlias() + "_" + nm[1].split("_")[1];
			
			int id = link.getId();
			String arisName = link.getLinkArisName(); // hipo add
			LinkRoad linkRoad = null;
			
			linkRoad = PollingEngine.getInstance().getLinkByID(id);//从内存中读取数据
			String stratIndex = linkRoad.getStartDescr();
			String endIndex = linkRoad.getEndDescr();
			String startNode = linkRoad.getStartIp();
			String endNode = linkRoad.getEndIp();

			String uplinkSpeed = linkRoad.getUplinkSpeed();
			String downlinkSpeed = linkRoad.getDownlinkSpeed();
			String pingValue = linkRoad.getPing();
			String allSpeedRate = linkRoad.getAllSpeedRate();
			DecimalFormat df = new DecimalFormat("#.##");
			if (allSpeedRate == null)
				allSpeedRate = "0";
			Double allspeed = Double.parseDouble(allSpeedRate);
			allSpeedRate = String.valueOf(df.format(allspeed));

			linkPerformanceDTO.setId(id);
			linkPerformanceDTO.setName(name);
			linkPerformanceDTO.setStartNode(startNode);
			linkPerformanceDTO.setEndNode(endNode);
			linkPerformanceDTO.setStratIndex(stratIndex);
			linkPerformanceDTO.setEndIndex(endIndex);
			linkPerformanceDTO.setUplinkSpeed((uplinkSpeed == null || "null".equalsIgnoreCase(uplinkSpeed + "")) ? "0" : uplinkSpeed + "");
			linkPerformanceDTO.setDownlinkSpeed((downlinkSpeed == null || "null".equalsIgnoreCase(downlinkSpeed + "")) ? "0" : downlinkSpeed + "");
			linkPerformanceDTO.setPingValue((pingValue == null || "null".equalsIgnoreCase(pingValue)) ? "0" : pingValue);
			linkPerformanceDTO.setAllSpeedRate((allSpeedRate == null || "null".equalsIgnoreCase(allSpeedRate)) ? "0" : allSpeedRate);
			linkPerformanceDTO.setArisName(arisName); // hipo add
		} catch (Exception e) {
			e.printStackTrace();
		}

		return linkPerformanceDTO;
	}
	// 获取指定用户业务SQL
	private String getBidSql() {
		User currentUser = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer bidSQL = new StringBuffer();
		// 拼接标志
		int flag = 0;
		if (currentUser.getBusinessids() != null) {
			if (currentUser.getBusinessids() != "-1") {
				String[] bids = currentUser.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								bidSQL.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								bidSQL.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					bidSQL.append(") ");
				}
			}
		}
		if (currentUser.getRole() == 0) {
			// 超级管理员
			return "";
		} else {
			return bidSQL.toString();
		}
	}

	private void getLinkLine(){
		String line = request.getParameter("line");
		   int id = 0;
		   if(line!=null)
			 id = Integer.parseInt(line); 
		   LinkRoad link = PollingEngine.getInstance().getLinkByID(id);
		 System.out.println(link.getStartId());
		 System.out.println(link.getEndId() + "  --link.getEndId()");
		   if(link == null)System.out.println("is null");
		   Host host1 = (Host)PollingEngine.getInstance().getNodeByID(link.getStartId());
		   Host host2 = (Host)PollingEngine.getInstance().getNodeByID(link.getEndId());
		   IfEntity if1 = host1.getIfEntityByIndex(link.getStartIndex());
		   IfEntity if2 = host2.getIfEntityByIndex(link.getEndIndex());
		  
		   if(if1 == null){
		   	if1 = new IfEntity();
		   	if1.setOperStatus(1);
		   	if1.setDescr("");
		   }
		   if(if2 == null){
		   	if2 = new IfEntity();
		   	if2.setOperStatus(1);
		   	if2.setDescr("");
		   }
		   
		   StringBuffer jsonString = new StringBuffer("{Rows:[");
		   jsonString.append("{\"startAlias\":\"");
		   jsonString.append(host1.getAlias());
		   jsonString.append("\",");
		   
		   jsonString.append("\"endAlias\":\"");
		   jsonString.append(host2.getAlias());
		   jsonString.append("\",");
		   
		   jsonString.append("\"startIp\":\"");
		   jsonString.append(host1.getIpAddress());
		   jsonString.append("\",");
		   
		   jsonString.append("\"endIp\":\"");
		   jsonString.append(host2.getIpAddress());
		   jsonString.append("\",");
		   
		   jsonString.append("\"startLinkIp\":\"");
		   jsonString.append(link.getStartIp());
		   jsonString.append("\",");
		   
		   jsonString.append("\"endLinkIp\":\"");
		   jsonString.append(link.getEndIp());
		   jsonString.append("\",");
		   
		   jsonString.append("\"startIndex\":\"");
		   String port = "";
		   if(if1.getOperStatus() == 1){
			   port = "up";
		   }else{
			   port = "dwon";
		   }
		   jsonString.append(link.getStartIndex() + "(" + port + ")");
		   jsonString.append("\",");
		   
		   if(if2.getOperStatus() == 1){
			   port = "up";
		   }else{
			   port = "dwon";
		   }
		   jsonString.append("\"endIndex\":\"");
		   jsonString.append(link.getEndIndex() + "(" + port + ")");
		   jsonString.append("\",");
		   
		   jsonString.append("\"startDescr\":\"");
		   jsonString.append(if1.getDescr());
		   jsonString.append("\",");
		   
		   jsonString.append("\"endDescr\":\"");
		   jsonString.append(if2.getDescr());
		   jsonString.append("\"}");
		   
		   jsonString.append("],total : 1}");
		   out.print(jsonString.toString());
		   out.flush();
	}
	
	public void execute(String action) {
		if (action.equals("getLinkPerformanceList")) {
			getLinkPerformanceList();
		}else if(action.equals("getLinkLine")){
			getLinkLine();
		}

	}

	@SuppressWarnings("unused")
	private float handleCpuTime(String CpuTime) {
		float sumOfCPU = 0.0f;
		Pattern p = Pattern.compile("(\\d+):(\\d+)");
		if (CpuTime != null) {
			if (CpuTime.indexOf(":") != -1) {
				Matcher matcher = p.matcher(CpuTime);
				if (matcher.find()) {
					String t1 = matcher.group(1);
					String t2 = matcher.group(2);
					sumOfCPU = Float.parseFloat(t1) * 60 + Float.parseFloat(t2);
				}
			} else {
				sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
			}
		}
		return sumOfCPU;
	}

	@SuppressWarnings("unused")
	private String removeUnit(String str, int l) {
		if (str != null && str.trim().length() > 0) {
			return str.substring(0, str.length() - l);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private float getFloatDigitByRemoveUnit(String str) {
		float floatDigit = 0.0f;
		if (str != null && str.trim().length() > 0) {
			floatDigit = Float.parseFloat(str.substring(0, str.length() - 1));
		}
		return floatDigit;
	}

	@SuppressWarnings("unused")
	private float floatFormate(Float f) {
		int scale = 2;// 设置位数
		int roundingMode = 4;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
		BigDecimal bd = new BigDecimal((double) f);
		bd = bd.setScale(scale, roundingMode);
		f = bd.floatValue();
		return f;
	}

	@SuppressWarnings("unused")
	private void getTime(HttpServletRequest request, String[] time) {
		DateE datemanager = new DateE();
		Calendar current = new GregorianCalendar();
		if (getParaValue("beginhour") == null) {
			Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			request.setAttribute("beginhour", new Integer(hour.intValue() - 1));
			request.setAttribute("endhour", hour);
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
		} else {
			String temp = getParaValue("startdate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("todate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
	}

}
