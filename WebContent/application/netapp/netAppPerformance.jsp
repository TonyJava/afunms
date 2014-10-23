<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.util.Date"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@page import="com.afunms.detail.net.service.NetService"%>
<%@page import="com.afunms.temp.model.NodeTemp"%>

<%
	String rootPath = request.getContextPath();
	String runmodel = PollingEngine.getCollectwebflag();
	String menuTable = (String) request.getAttribute("menuTable");
	int alarmLevel = (Integer) request.getAttribute("alarmLevel");
	String pingcollecttime = "";
	String tmp = request.getParameter("id");
	double cpuvalue = 0;
	String pingconavg = "0";
	String maxpingvalue = "0";//最大ping值
	String responsevalue = "0";
	String pingvalue = "0";

	int cpuper = 0;
	Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	NodeUtil nodeUtil = new NodeUtil();
	NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
	String ipaddress = host.getIpAddress();
	String picip = CommonUtil.doip(ipaddress);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String date = sdf.format(new Date());
	String starttime = date + " 00:00:00";
	String totime = date + " 23:59:59";
	if ("0".equals(runmodel)) {
		//采集与访问是集成模式
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				Pingcollectdata pingdata = (Pingcollectdata) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				SimpleDateFormat _sdf1 = new SimpleDateFormat("MM-dd HH:mm");
				pingcollecttime = _sdf1.format(cc);
				pingvalue = pingdata.getThevalue();//当前ping值
				pingdata = (Pingcollectdata) pingData.get(1);
				responsevalue = pingdata.getThevalue();//当前响应时间
			}
			if (ipAllData != null) {
				if (cpuV != null && cpuV.size() > 0) {
					CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
					if (cpu != null && cpu.getThevalue() != null) {
						cpuvalue = new Double(cpu.getThevalue());
						cpuper = Double.valueOf(cpuvalue).intValue();//当前 Cpu值
					}
				}
			}
		}

	} else {
		//采集与访问是分离模式
		List pingList = new ArrayList();
		try {
			pingList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrPingInfo();
			cpuvalue = new Double(new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrCpuAvgInfo());
			cpuper = Double.valueOf(cpuvalue).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pingList != null && pingList.size() > 0) {
			for (int i = 0; i < pingList.size(); i++) {
				NodeTemp nodetemp = (NodeTemp) pingList.get(i);
				if ("ConnectUtilization".equals(nodetemp.getSindex())) {
					pingvalue = nodetemp.getThevalue();
					SimpleDateFormat _sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date d = _sdf1.parse(nodetemp.getCollecttime());
					_sdf1 = new SimpleDateFormat("MM-dd HH:mm");
					pingcollecttime = _sdf1.format(d);
				}
				if ("ResponseTime".equals(nodetemp.getSindex())) {
					responsevalue = nodetemp.getThevalue();
				}
			}
		}
	}

	I_HostCollectData hostmanager = new HostCollectDataManager();

	String cpuAvg = "";//今日Cpu平均利用率
	String cpuMax = "";//今日Cpu最大利用率
	Hashtable cpuHashTable = new Hashtable();
	try {
		cpuHashTable = hostmanager.getCategory(host.getIpAddress(), "CPU", "Utilization", starttime, totime);
	} catch (Exception e) {
		e.printStackTrace();
	}
	if (cpuHashTable.get("avgcpucon") != null) {
		cpuAvg = (String) cpuHashTable.get("avgcpucon");
		cpuAvg = cpuAvg.replace("%", "");
		cpuMax = (String) cpuHashTable.get("max");
		cpuMax = cpuMax.replace("%", "");
	}

	Hashtable ConnectUtilizationhash = new Hashtable();
	try {
		ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime, totime);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	if (ConnectUtilizationhash.get("avgpingcon") != null) {
		pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
		pingconavg = pingconavg.replace("%", "");
		maxpingvalue = (String) ConnectUtilizationhash.get("pingmax");
		maxpingvalue = maxpingvalue.replaceAll("%", "");//最大ping值
	}
	double avgpingcon = new Double(pingconavg);
	int percent1 = Double.valueOf(avgpingcon).intValue();//平均ping值

	String avgresponse = "0";
	String maxresponse = "0";
	try {
		ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ResponseTime", starttime, totime);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	if (ConnectUtilizationhash.get("avgpingcon") != null) {
		avgresponse = (String) ConnectUtilizationhash.get("avgpingcon");//平均响应时间
		avgresponse = avgresponse.replace("毫秒", "").replaceAll("%", "");
		maxresponse = (String) ConnectUtilizationhash.get("pingmax");//最大响应时间
		maxresponse = maxresponse.replaceAll("%", "");
	}

	//这里是生成[Cpu]信息的函数
	CreateMetersPic cmp = new CreateMetersPic();
	cmp.createCpuPic(picip, cpuper); //生成CPU仪表盘
	cmp.createMaxCpuPic(picip, cpuMax); //生成CPU最大值仪表盘
	cmp.createAvgCpuPic(picip, cpuAvg); //生成CPU平均值仪表盘

	CreateBarPic cbp = new CreateBarPic();
	TitleModel tm = new TitleModel();
	//这里是生成[响应时间]图的函数
	double[] r_data1 = { new Double(responsevalue), new Double(maxresponse), new Double(avgresponse) };
	String[] r_labels = { "当前响应时间(ms)", "最大响应时间(ms)", "平均响应时间(ms)" };
	tm = new TitleModel();
	tm.setPicName(picip + "response");//
	tm.setBgcolor(0xffffff);
	tm.setXpic(450);//图片长度
	tm.setYpic(180);//图片高度
	tm.setX1(30);//左面距离
	tm.setX2(20);//上面距离
	tm.setX3(400);//内图宽度
	tm.setX4(130);//内图高度
	tm.setX5(10);
	tm.setX6(115);
	cbp.createTimeBarPic(r_data1, r_labels, tm, 40);

	//这里是组装[连通率]字符串的函数
	Double realValue = Double.valueOf(pingvalue.replaceAll("%", ""));
	StringBuffer dataStr1 = new StringBuffer();
	dataStr1.append("连通;").append(Math.round(realValue)).append(";false;7CFC00\\n");
	dataStr1.append("未连通;").append(100 - Math.round(realValue)).append(";false;FF0000\\n");
	String realdata = dataStr1.toString();//实时连通率
	StringBuffer dataStr2 = new StringBuffer();
	dataStr2.append("连通;").append(Math.round(avgpingcon)).append(";false;7CFC00\\n");
	dataStr2.append("未连通;").append(100 - Math.round(avgpingcon)).append(";false;FF0000\\n");
	String avgdata = dataStr2.toString();//平均连通率
	StringBuffer dataStr3 = new StringBuffer();
	String maxping = maxpingvalue.replaceAll("%", "");
	dataStr3.append("连通;").append(Math.round(Float.parseFloat(maxping))).append(";false;7CFC00\\n");
	dataStr3.append("未连通;").append(100 - Math.round(Float.parseFloat(maxping))).append(";false;FF0000\\n");
	String maxdata = dataStr3.toString();//最大连通率
%>
<html>
	<head>

		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet"
			href="<%=rootPath%>/common/css/tableNewStyle.css" type="text/css"></link>
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>

		<script type="text/javascript"
			src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script>

$(document).ready(function(){
	$('#xiangying-detail').hide();
	$('#xiangxi-detail').hide();
	$('#liantong').bind('click',function(){
			$('#liantong-detail').slideToggle('slow');
		});
	$('#xiangying').bind('click',function(){
			$('#xiangying-detail').slideToggle('slow');
		});
	$('#xiangxi').bind('click',function(){
			$('#xiangxi-detail').slideToggle('slow');
		});
});
function cpuReportByMinute(){
	window.open("<%=rootPath%>/monitor.do?action=hostcpu_report&id=<%=tmp%>&timeType=minute","newWindow","height=300, width=800, top=0, left= 200,screenX=0,screenY=0");
  }
  
  function memoryReportByMinute(){
	window.open("<%=rootPath%>/monitor.do?action=hostmemory_report&id=<%=tmp%>&timeType=minute","newWindow","height=300, width=800, top=0, left= 200,screenX=0,screenY=0");
   
  }
  
  function refer(action){
		document.getElementById("id").value="<%=tmp%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}

</script>

	</head>
	<body id="body" class="body">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="id">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-detail" width=85%>
									<table id="container-main-detail" class="container-main-detail">
										<tr>
											<td class="td-container-main-detail" width=98%>
												<table id="container-main-detail"
													class="container-main-detail">
													<tr>
														<td>
															<table id="detail-content" class="detail-content"
																width="100%" cellspacing="0" cellpadding="0">
																<!-- 顶部标题 -->
																<tr class="bar">
																	<td colspan=4 align=center>
																		<b>设备详细信息</b>
																	</td>
																</tr>
																<!-- 顶部标题end -->
																<!-- 设备概要信息 -->
																<tr class="bar">
																	<td width="20%" style="padding-left: 30px;">
																		设备标签:<%=host.getAlias()%>
																	</td>
																	<td width="20%">
																		系统名称:<%=host.getSysName()%>
																	</td>
																	<td width=15%>
																		<table>
																			<tr>
																				<td width="10px;">
																					<img
																						src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmLevel)%>">
																				</td>
																				<td>
																					<span><%=NodeHelper.getStatusDescr(alarmLevel)%></span>
																				</td>
																			</tr>
																		</table>
																	</td>
																	<td width="20%">
																		IP地址:<%=host.getIpAddress()%>
																	</td>
																</tr>
																<!-- 设备概要信息end -->
															</table>
														</td>
													</tr>
													<!-- 顶部区域块end -->
													<tr>
														<td>
															<table id="detail-data" class="detail-data">
																<tr>
																	<td class="detail-data-header">
																		<%=netAppDetailTitleTable%>
																	</td>
																</tr>

																<tr>
																	<td>
																		<table class="detail-data-body">
																			<tr id="liantong">
																				<td colspan=2>
																					<table id="add-content-header"
																						class="add-content-header">
																						<tr id="cpuliyonglv">
																							<td align="left" width="5">
																								<img
																									src="<%=rootPath%>/common/images/right_t_01.jpg"
																									width="5" height="29" />
																							</td>
																							<td class="add-content-title">
																								<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;连通率实时</b>
																							</td>
																							<td align="right">
																								<img
																									src="<%=rootPath%>/common/images/right_t_03.jpg"
																									width="5" height="29" />
																							</td>
																						</tr>
																					</table>

																				</td>
																			</tr>
																			<tr>
																				<td>
																					<div id="liantong-detail">
																						<table>
																							<tr>
																								<td align=center width=43% valign=top>
																									<br>
																									<table cellpadding="0" cellspacing="0"
																										width=48% align=center>
																										<tr>
																											<td width="100%" align=center>
																												<div id="flashcontent3">
																													<strong>You need to upgrade your
																														Flash Player</strong>
																												</div>
																												<script type="text/javascript">
																					var so = new SWFObject("<%=rootPath%>/flex/Ping.swf?ipadress=<%=host.getIpAddress()%>", "Ping", "346", "191", "8", "#ffffff");
																					so.write("flashcontent3");
																				</script>
																											</td>
																										</tr>
																										<tr>
																											<td width=100% align=center>
																												<table cellpadding="1" cellspacing="1"
																													style="align: center; width: 346"
																													bgcolor="#FFFFFF">
																													<tr bgcolor="#FFFFFF">
																														<td height="29"
																															class="detail-data-body-title"
																															style="height: 29">
																															名称
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															当前（%）
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															最小（%）
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															平均（%）
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															采集时间
																														</td>
																													</tr>
																													<tr>
																														<td class="detail-data-body-list"
																															height="29">
																															连通率
																														</td>
																														<td class="detail-data-body-list"><%=pingvalue%></td>
																														<td class="detail-data-body-list"><%=Math.round(Float.parseFloat(maxpingvalue))%></td>
																														<td class="detail-data-body-list"><%=percent1%></td>
																														<td class="detail-data-body-list"><%=pingcollecttime%></td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																									</table>
																								</td>
																								<td align=left valign=top width=57%>

																									<br>
																									<table id="body-container"
																										class="body-container"
																										style="background-color: #FFFFFF;" width="40%">
																										<tr>
																											<td class="td-container-main">
																												<table id="container-main"
																													class="container-main">
																													<tr>
																														<td class="td-container-main-add">
																															<table id="container-main-add"
																																class="container-main-add">
																																<tr>
																																	<td style="background-color: #FFFFFF;">
																																		<table id="add-content"
																																			class="add-content">
																																			<tr>
																																				<td>
																																					<table id="add-content-header"
																																						class="add-content-header">
																																						<tr>
																																							<td align="left" width="5">
																																								<img
																																									src="<%=rootPath%>/common/images/right_t_01.jpg"
																																									width="5" height="29" />
																																							</td>
																																							<td class="add-content-title">
																																								<b>连通率实时</b>
																																							</td>
																																							<td align="right">
																																								<img
																																									src="<%=rootPath%>/common/images/right_t_03.jpg"
																																									width="5" height="29" />
																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>
																																			<tr>
																																				<td>
																																					<table id="detail-content-body"
																																						class="detail-content-body"
																																						border='1'>
																																						<tr>
																																							<td>
																																								<table cellpadding="1"
																																									cellspacing="1" width=48%>

																																									<tr>
																																										<td valign=top>



																																											<table width="90%"
																																												cellpadding="0"
																																												cellspacing="0" border=0>
																																												<tr>
																																													<td valign=top>
																																														<!--  	<img
																																								src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>realping.png">-->
																																														<div id="realping">
																																															<strong>You need
																																																to upgrade your Flash
																																																Player</strong>
																																														</div>
																																														<script
																																															type="text/javascript"
																																															src="<%=rootPath%>/include/swfobject.js"></script>
																																														<script
																																															type="text/javascript">
						                                                                                                                           var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
						                                                                                                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                                                                                                           so.addVariable("chart_data","<%=realdata%>");
						                                                                                                                           so.write("realping");
						                                                                                                                            </script>
																																													</td>
																																													<td valign=top>
																																														<!--	<img
																																								src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>minping.png">-->
																																														<div id="maxping">
																																															<strong>You need
																																																to upgrade your Flash
																																																Player</strong>
																																														</div>
																																														<script
																																															type="text/javascript"
																																															src="<%=rootPath%>/include/swfobject.js"></script>
																																														<script
																																															type="text/javascript">
						                                                                                                                           var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
						                                                                                                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                                                                                                           so.addVariable("chart_data","<%=maxdata%>");
						                                                                                                                           so.write("maxping");
						                                                                                                                            </script>
																																													</td>
																																													<td valign=top>
																																														<!--	<img
																																								src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>pingavg.png">-->
																																														<div id="avgping">
																																															<strong>You need
																																																to upgrade your Flash
																																																Player</strong>
																																														</div>
																																														<script
																																															type="text/javascript"
																																															src="<%=rootPath%>/include/swfobject.js"></script>
																																														<script
																																															type="text/javascript">
						                                                                                                                           var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
						                                                                                                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                                                                                                           so.addVariable("chart_data","<%=avgdata%>");
						                                                                                                                           so.write("avgping");
						                                                                                                                            </script>
																																													</td>
																																												</tr>
																																												<tr height=50>
																																													<td valign=top align=center>
																																														<b>当前</b>
																																													</td>
																																													<td valign=top align=center>
																																														<b>最小</b>
																																													</td>
																																													<td valign=top align=center>
																																														<b>平均</b>
																																													</td>
																																												</tr>
																																											</table>

																																										</td>






																																									</tr>

																																								</table>

																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>

																																		</table>
																																	</td>
																																</tr>
																															</table>
																														</td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																									</table>



																								</td>
																							</tr>
																						</table>
																					</div>
																				</td>
																			</tr>
																			<tr id="xiangying">
																				<td colspan=2>
																					<table id="add-content-header"
																						class="add-content-header">
																						<tr id="cpuliyonglv">
																							<td align="left" width="5">
																								<img
																									src="<%=rootPath%>/common/images/right_t_01.jpg"
																									width="5" height="29" />
																							</td>
																							<td class="add-content-title">
																								<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;响应时间</b>
																							</td>
																							<td align="right">
																								<img
																									src="<%=rootPath%>/common/images/right_t_03.jpg"
																									width="5" height="29" />
																							</td>
																						</tr>
																					</table>

																				</td>
																			</tr>
																			<tr>
																				<td colspan=2>
																					<div id="xiangying-detail">
																						<table>
																							<tr>

																								<td align=center valign=top>
																									<br>
																									<table cellpadding="0" cellspacing="0"
																										width=48%>
																										<tr>
																											<td width="100%" align=center>
																												<div id="flashcontent_5">
																													<strong>You need to upgrade your
																														Flash Player</strong>
																												</div>
																												<script type="text/javascript">
																					var so = new SWFObject("<%=rootPath%>/flex/Response_time.swf?ipadress=<%=host.getIpAddress()%>", "Response_time", "346", "196", "8", "#ffffff");
																					so.write("flashcontent_5");
																				</script>
																											</td>
																										</tr>
																										<tr>
																											<td width=100% align=center>
																												<table cellpadding="1" cellspacing="1"
																													style="align: center; width: 346"
																													bgcolor="#FFFFFF">
																													<tr bgcolor="#FFFFFF">
																														<td class="detail-data-body-title"
																															style="height: 29" height="29">
																															名称
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															当前（ms）
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															最大（ms）
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															平均（ms）
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															采集时间
																														</td>
																													</tr>
																													<tr>
																														<td class="detail-data-body-list"
																															height="29">
																															响应时间
																														</td>
																														<td class="detail-data-body-list"><%=responsevalue%></td>
																														<td class="detail-data-body-list"><%=maxresponse%></td>
																														<td class="detail-data-body-list"><%=avgresponse%></td>
																														<td class="detail-data-body-list"><%=pingcollecttime%></td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																									</table>
																								</td>
																								<td align=center>
																									<br>
																									<table id="body-container"
																										class="body-container"
																										style="background-color: #FFFFFF;" width="40%">
																										<tr>
																											<td class="td-container-main">
																												<table id="container-main"
																													class="container-main">
																													<tr>
																														<td class="td-container-main-add">
																															<table id="container-main-add"
																																class="container-main-add">
																																<tr>
																																	<td style="background-color: #FFFFFF;">
																																		<table id="add-content"
																																			class="add-content">
																																			<tr>
																																				<td>
																																					<table id="add-content-header"
																																						class="add-content-header">
																																						<tr>
																																							<td align="left" width="5">
																																								<img
																																									src="<%=rootPath%>/common/images/right_t_01.jpg"
																																									width="5" height="29" />
																																							</td>
																																							<td class="add-content-title">
																																								<b>响应时间详情</b>
																																							</td>
																																							<td align="right">
																																								<img
																																									src="<%=rootPath%>/common/images/right_t_03.jpg"
																																									width="5" height="29" />
																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>
																																			<tr>
																																				<td>
																																					<table id="detail-content-body"
																																						class="detail-content-body"
																																						border='1'>
																																						<tr>
																																							<td>
																																								<table cellpadding="1"
																																									cellspacing="1" width=48%>

																																									<tr>
																																										<td valign=top>



																																											<table cellpadding="0"
																																												cellspacing="0" width=48%
																																												align=center>
																																												<tr>
																																													<td align=center colspan=3>
																																														<img
																																															src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>response.png">
																																													</td>
																																												</tr>
																																												<tr height=30>
																																													<td valign=top align=center>
																																														<b>当前</b>
																																													</td>
																																													<td valign=top align=center>
																																														<b>最大</b>
																																													</td>
																																													<td valign=top align=center>
																																														<b>平均</b>
																																													</td>
																																												</tr>
																																											</table>

																																										</td>

																																									</tr>

																																								</table>

																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>

																																		</table>
																																	</td>
																																</tr>
																															</table>
																														</td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																									</table>
																								</td>
																							</tr>
																						</table>
																					</div>
																				</td>
																			</tr>
																			<%
																				if (host.getCollecttype() != 3 && host.getCollecttype() != 4) {
																			%>
																			<tr id="xiangxi">
																				<td colspan=2>
																					<table id="add-content-header"
																						class="add-content-header">
																						<tr id="cpuliyonglv">
																							<td align="left" width="5">
																								<img
																									src="<%=rootPath%>/common/images/right_t_01.jpg"
																									width="5" height="29" />
																							</td>
																							<td class="add-content-title">
																								<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CPU详细</b>
																							</td>
																							<td align="right">
																								<img
																									src="<%=rootPath%>/common/images/right_t_03.jpg"
																									width="5" height="29" />
																							</td>
																						</tr>
																					</table>

																				</td>
																			</tr>
																			<tr>
																				<td>
																					<div id="xiangxi-detail">
																						<table>
																							<tr>
																								<td align=center valign=top>
																									<br>
																									<table cellpadding="0" cellspacing="0"
																										width=48% align=center>
																										<tr>
																											<td width="100%" align=center>
																												<div id="flashcontent1">
																													<strong>You need to upgrade your
																														Flash Player</strong>
																												</div>
																												<script type="text/javascript">
																					var so = new SWFObject("<%=rootPath%>/flex/Line_CPU.swf?ipadress=<%=host.getIpAddress()%>", "Line_CPU", "346", "230", "8", "#ffffff");
																					so.write("flashcontent1");
																				</script>
																											</td>
																										</tr>
																										<tr>
																											<td width=100% align=center>
																												<table cellpadding="0" cellspacing="1"
																													style="align: center; width: 346"
																													bgcolor="#FFFFFF">
																													<tr bgcolor="#FFFFFF">
																														<td height="29"
																															class="detail-data-body-title"
																															style="height: 29">
																															名称
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															当前
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															最大
																														</td>
																														<td class="detail-data-body-title"
																															style="height: 29">
																															平均
																														</td>
																													</tr>
																													<tr>
																														<td class="detail-data-body-list"
																															height="29">
																															&nbsp;CPU利用率
																														</td>
																														<td class="detail-data-body-list">
																															&nbsp;<%=cpuper%>%
																														</td>
																														<td class="detail-data-body-list">
																															&nbsp;<%=cpuMax%></td>
																														<td class="detail-data-body-list">
																															&nbsp;<%=cpuAvg%></td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																									</table>
																								</td>
																								<td align=center valign=top>
																									<br>
																									<table id="body-container"
																										class="body-container"
																										style="background-color: #FFFFFF;" width="40%">
																										<tr>
																											<td class="td-container-main">
																												<table id="container-main"
																													class="container-main">
																													<tr>
																														<td class="td-container-main-add">
																															<table id="container-main-add"
																																class="container-main-add">
																																<tr>
																																	<td style="background-color: #FFFFFF;">
																																		<table id="add-content"
																																			class="add-content">
																																			<tr>
																																				<td>
																																					<table id="add-content-header"
																																						class="add-content-header">
																																						<tr>
																																							<td align="left" width="5">
																																								<img
																																									src="<%=rootPath%>/common/images/right_t_01.jpg"
																																									width="5" height="29" />
																																							</td>
																																							<td class="add-content-title">
																																								<b>CPU利用率详情</b>
																																							</td>
																																							<td align="right">
																																								<img
																																									src="<%=rootPath%>/common/images/right_t_03.jpg"
																																									width="5" height="29" />
																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>
																																			<tr>
																																				<td>
																																					<table id="detail-content-body"
																																						class="detail-content-body"
																																						border='1'>
																																						<tr>
																																							<td>
																																								<table cellpadding="1"
																																									cellspacing="1" width=48%>
																																									<tr>
																																										<td valign=top>
																																											<table cellpadding="0"
																																												cellspacing="0" width=48%
																																												align=center>
																																												<tr>
																																													<td align=center>
																																														<img
																																															src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>cpu.png">
																																													</td>
																																													<td align=center>
																																														<img
																																															src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>cpumax.png">
																																													</td>
																																													<td align=center>
																																														<img
																																															src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>cpuavg.png">
																																													</td>
																																												</tr>
																																												<tr height=20>
																																													<td valign=top align=center>
																																														<b>当前</b>
																																													</td>
																																													<td valign=top align=center>
																																														<b>最大</b>
																																													</td>
																																													<td valign=top align=center>
																																														<b>平均</b>
																																													</td>
																																												</tr>
																																												<tr height=20>
																																													<td colspan=3>
																																														&nbsp;
																																													</td>
																																												</tr>

																																											</table>

																																										</td>

																																									</tr>

																																								</table>

																																							</td>
																																						</tr>

																																						<tr>
																																							<td>


																																								<table cellpadding="1"
																																									cellspacing="1" width=80%>

																																									<tr>
																																										<td valign=top width=80%>
																																											<table cellpadding="0"
																																												cellspacing="0" width=80%
																																												align=center>
																																											</table>

																																										</td>

																																									</tr>

																																								</table>

																																							</td>
																																						</tr>
																																					</table>
																																				</td>
																																			</tr>

																																		</table>
																																	</td>
																																</tr>
																															</table>
																														</td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																									</table>
																								</td>
																							</tr>
																						</table>
																					</div>
																				</td>
																			</tr>
																			<%
																				}
																			%>
																		</table>
																	</td>
																</tr>

															</table>

														</td>
													</tr>
												</table>
											</td>
											<td class="td-container-main-tool" width='15%'>
												<jsp:include page="/include/netapptoolbar.jsp">
													<jsp:param value="<%=host.getIpAddress()%>"
														name="ipaddress" />
													<jsp:param value="<%=host.getSysOid()%>" name="sys_oid" />
													<jsp:param value="<%=tmp%>" name="tmp" />
													<jsp:param value="network" name="category" />
													<jsp:param value="<%=nodedto.getSubtype()%>" name="subtype" />
												</jsp:include>
											</td>

										</tr>
									</table>
									</form>
	</BODY>
</HTML>
