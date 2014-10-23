<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="com.afunms.alarm.util.AlarmConstant"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.application.model.DnsConfig"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%
	String runmodel = PollingEngine.getCollectwebflag();
	String rootPath = request.getContextPath();
	DnsConfig vo = (DnsConfig) request.getAttribute("vo");
	int id = 0;
	
	String connrate = request.getAttribute("connrate").toString();
 	String conn_name = request.getAttribute("conn_name").toString();
 	String valid_name = request.getAttribute("valid_name").toString();
 	String fresh_name = request.getAttribute("fresh_name").toString();
 	String wave_name = request.getAttribute("wave_name").toString();
 	String delay_name = request.getAttribute("delay_name").toString();
        connrate = request.getAttribute("connrate").toString();
        if(connrate!=null)
	connrate = connrate.replaceAll("%", "");
     String serverName="";//服务名称
     String hostIp="";//服务IP
     String dnsIp="";//DNS ip
     int port=0;
     if(vo!=null){
    	 id=vo.getId();
    	 serverName=vo.getDns(); //域名
    	 hostIp=vo.getHostip();
    	 dnsIp=vo.getDnsip();
    	 port=vo.getHostinter();
     }
	String menuTable = (String) request.getAttribute("menuTable");
	int status = SystemSnap.getNodeStatus(id + "", AlarmConstant.TYPE_MIDDLEWARE, "dns");
	String imgsrc = rootPath + "/resource/" + NodeHelper.getCurrentStatusImage(status);
	
	float avgPing = Float.parseFloat(connrate);
	StringBuffer dataStr = new StringBuffer();
	dataStr.append("连通;").append(Math.round(avgPing)).append(";false;7CFC00\\n");
	dataStr.append("未连通;").append(100 - Math.round(avgPing)).append(";false;FF0000\\n");
	String avgdata = dataStr.toString();
	String allipstr = SysUtil.doip(hostIp);
	
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312" />
		 <link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script type="text/javascript">
function setClass(){
	document.getElementById('dnsDetailTitle-0').className='detail-data-title';
	document.getElementById('dnsDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('dnsDetailTitle-0').onmouseout="this.className='detail-data-title'";
		}
		
function refer(action){
		document.getElementById("id").value="<%=id%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>


	</head>
	<body id="body" class="body" onload="setClass();">
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="id">

			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td><%=menuTable%></td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-service-detail">
									<table id="container-main-service-detail"
										class="container-main-service-detail">
										<tr>
											<td>
												<jsp:include page="/topology/includejsp/middleware_dns.jsp">
													<jsp:param name="id" value="<%=id%>" />
                                                    <jsp:param name="name" value="<%=serverName%>" />
                                                     <jsp:param name="ipaddress" value="<%=hostIp%>" />
												</jsp:include>
											</td>
										</tr>
										<tr>
											<td>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
														<td>
															<%=dnsDetailTitleTable%>
														</td>
													</tr>

													<tr>
														<td>
															<table id="service-detail-content-body"
																class="service-detail-content-body">
																<tr>
																	<td>
																		<table style="BORDER-COLLAPSE: collapse" rules=none
																			align=center border=1 cellpadding=0 cellspacing="0"
																			width=100%>
																			<tr>
																				<td width="80%" align="left" valign="top">
																					<table>
																						<tr>
																							<td width="20%" height="29" align="left" nowrap>
																								&nbsp;服务名称:
																							</td>
																							<td width="30%"><%=serverName%></td>
																							<td width="20%" height="29" align="left" nowrap>
																								
																							</td>
																							<td width="30%">
																								
																							</td>
																						</tr>
																						<tr bgcolor="#F1F1F1">


																							<td height="29" align=left nowrap>
																								&nbsp;DNS服务器:
																							</td>
																							<td><%=hostIp%></td>

																							<td height="29" class=txtGlobal align="left"
																								nowrap>
																								&nbsp;DNS服务器端口:
																							</td>
																							<td><%=port%></td>
																						</tr>
																						
																						<tr>

																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;域名:
																							</td>
																							<td><%=serverName%></td>
																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;域址IP:
																							</td>
																							<td>
																								<%=dnsIp%>
																							</td>
																						</tr>
																						<tr bgcolor="#F1F1F1">
																							<td height="29" align=left nowrap>
																								&nbsp;
																							</td>
																							<td></td>
																							<td height="29" align=left nowrap>
																								&nbsp;
																							</td>
																							<td></td>
																						</tr>


																						<tr>
																							<td height="29" align=left nowrap>
																								&nbsp;
																							</td>
																							<td></td>
																							<td height="29" nowrap>
																								&nbsp;
																							</td>
																							<td></td>
																						</tr>

																						<tr>
																							<td height="29" align=left nowrap>
																								&nbsp;
																							</td>
																							<td></td>
																							<td colspan=2></td>
																						</tr>

<tr>
																							<td height="29" align=left nowrap>
																								
																							</td>
																							<td></td>
																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;
																							</td>
																							<td></td>
																						</tr>
																					</table>
																				</td>
																				<td width=20% align="center" valign=top>
																					<table class="container-main-service-detail-tool">
																						<tr>
																							<td>
																								<table style="BORDER-COLLAPSE: collapse"
																									rules=none align=center border=1 cellpadding=0
																									cellspacing="0" width=100%>
																									<tr bgcolor=#F1F1F1 height="26">
																										<td align="center">
																											今日连通率
																										</td>
																									</tr>
																									<tr>
																										<td width="100%" align="center">

																											<div id="avgping">
																												<strong>You need to upgrade your
																													Flash Player</strong>
																											</div>
																											<script type="text/javascript"
																												src="<%=rootPath%>/include/swfobject.js"></script>
																											<script type="text/javascript">
						                                        var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","165", "165", "8", "#FFFFFF");
						                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                           so.addVariable("chart_data","<%=avgdata%>");
						                                           so.write("avgping");
					                                   </script>
																										</td>
																									</tr>
																									<tr>
																										<td height="7" align=center>
																											<img
																												src="<%=rootPath%>/resource/image/Loading_2.gif">
																										</td>
																									</tr>
																								</table>
																							</td>
																						</tr>
																						<tr>

																							<td>
																								<table style="BORDER-COLLAPSE: collapse"
																									rules=none align=center border=1 cellpadding=0
																									cellspacing="0" width=100%>


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
													<tr>
														<td>
															<table class="detail-data-body">
																<tr>
																	<td align=center valign=top>
																		<br>
																		<table cellpadding="0" cellspacing="0" width=48%
																			align=center>
																			<tr>
																				<td width="50%" align="center">
																					<table width="100%" cellspacing="0" cellpadding="0"
																						align="center">
																						<tr>
																							<td width="100%" align="center">
																								<div id="flashcontent1">
																									<strong>You need to upgrade your Flash
																										Player</strong>
																								</div>
																								<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?title=连通曲线&tablename=dnsping<%=allipstr%>", "common_line", "400", "250", "8", "#ffffff");
																											so.write("flashcontent1");
																										</script>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<td width="48%" align="center">
																		<table width="100%" cellspacing="0" cellpadding="0"
																			align="center">
																			<tr>
																				<td width="100%" align="center">
																					<div id="flashcontent3">
																						<strong>You need to upgrade your Flash
																							Player</strong>
																					</div>
																									<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_pie.swf?title=服务连通&tablename=dnsping<%=allipstr%>&category=thevalue&type=util", "common_line", "400", "250", "8", "#ffffff");
																											so.write("flashcontent3");
																									</script>
																				</td>
																			</tr>
																		</table>
																	</td>
																			</tr>
																		</table>
																		<br>
																	</td>
																</tr>
															</table>
														</td>
													</tr>

													<tr>
														<td>
															<table id="application-detail-content-footer"
																class="application-detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_01.jpg"
																						width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_03.jpg"
																						width="5" height="12" />
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
					<td class="td-container-main-tool" width="14%">
						<jsp:include page="/include/midtoolbar.jsp">
							<jsp:param value="<%=hostIp%>" name="ipaddress" />
							<jsp:param value="<%=id%>" name="id" />
							<jsp:param value="<%=flag%>" name="flag" />
							<jsp:param value="middleware" name="type" />
							<jsp:param value="dns" name="subtype" />
						</jsp:include>
					</td>
				</tr>
			</table>

		</form>

	</BODY>
</HTML>