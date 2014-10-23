<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.application.model.Resin"%>
<%@page import="java.math.BigDecimal"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page language="java" contentType="text/html;charset=gb2312"%>
<%

	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	String tmp = request.getParameter("id");
	String avgpingStr = (String) request.getAttribute("avgpingcon") == null ? "0" : (String) request.getAttribute("avgpingcon");
	double avgpingcon = Double.parseDouble(avgpingStr.replace("%", ""));
	String avgmem = (String) request.getAttribute("avgmemcon") == null ? "0" : (String) request.getAttribute("avgmemcon");
	double avgmemcon = Double.parseDouble(avgmem.replace("%", ""));
	Resin resin = (Resin) request.getAttribute("resin");
	String monStr = (String) request.getAttribute("monStr");
	String flag_1 = (String) request.getAttribute("flag");
	String lasttime = (String) request.getAttribute("lasttime");
	String nexttime = (String) request.getAttribute("nexttime");
	String avgdata = (String) request.getAttribute("avgdata") == null ? "0" : (String) request.getAttribute("avgdata");
	String uptime = (String) request.getAttribute("uptime") == null ? "" : (String) request.getAttribute("uptime");
	String hostname = (String) request.getAttribute("hostname") == null ? "" : (String) request.getAttribute("hostname");
	String serverId = (String) request.getAttribute("serverId") == null ? "" : (String) request.getAttribute("serverId");
	String configPath = (String) request.getAttribute("configPath") == null ? "" : (String) request.getAttribute("configPath");
	String homePath = (String) request.getAttribute("homePath") == null ? "" : (String) request.getAttribute("homePath");
	String username = (String) request.getAttribute("username") == null ? "" : (String) request.getAttribute("username");
	String version = (String) request.getAttribute("version") == null ? "" : (String) request.getAttribute("version");
	String memName = "内存平均利用率";
	if (resin != null && resin.getVersion().equals("4")) {
		memName = "物理内存平均利用率";
	}

%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
        <link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
		
      function setClass(){
       document.getElementById('resDetailTitle-0').className='detail-data-title';
       document.getElementById('resDetailTitle-0').onmouseover="this.className='detail-data-title'";
       document.getElementById('resDetailTitle-0').onmouseout="this.className='detail-data-title'";
     }

function event(){
	mainForm.action = "<%=rootPath%>/resin.do?action=event"+"&flag=<%=flag%>";
    mainForm.submit();
}
function alarm(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/resin.do?action=alarm&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function detail(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/resin.do?action=detail&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function tcpport(){

	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/resin.do?action=tcpport&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function system(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/resin.do?action=system&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function pool(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/resin.do?action=connPool&id="+id+"&flag=<%=flag%>";
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
												<jsp:include page="/topology/includejsp/middleware_resin.jsp">
													<jsp:param name="tmp" value="<%=tmp%>" />
													<jsp:param name="avgpingcon" value="<%=avgpingcon%>" />
													<jsp:param name="avgmemcon" value="<%=avgmemcon%>" />
												</jsp:include>
											</td>
										</tr>
										<tr>
											<td>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
														<td>
															<%=resDetailTitleTable%>
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
																							<td width="30%" height="29" align="left" nowrap
																								class=txtGlobal>
																								&nbsp;名称:
																							</td>
																							<td width="70%"><%=resin.getAlias()%></td>
																						</tr>
																						<tr bgcolor="#F1F1F1">
																							<td width="30%" height="29" align="left" nowrap
																								class=txtGlobal>
																								&nbsp;服务ID:
																							</td>
																							<td width="70%"><%=serverId%></td>
																						</tr>
																						<tr>
																							<td height="29" class=txtGlobal align="left"
																								nowrap>
																								&nbsp;管理状态:
																							</td>
																							<td>
																								<%=monStr%>
																							</td>
																						</tr>
																						<tr bgcolor="#F1F1F1">
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;IP地址:
																							</td>
																							<td><%=resin.getIpAddress()%></td>
																						</tr>
																						
																						<tr>
																							<td height="29" class=txtGlobal align="left"
																								nowrap>
																								&nbsp;端口:
																							</td>
																							<td><%=resin.getPort()%></td>
																						</tr>
																						<tr  bgcolor="#F1F1F1">
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;resin版本:
																							</td>
																							<td><%=version%></td>
																						</tr>
																						<tr>
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;运行时间:
																							</td>
																							<td><%=uptime%></td>
																						</tr>
																						<%%>
																						<tr bgcolor="#F1F1F1">
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;所属主机:
																							</td>
																							<td><%=hostname%></td>
																						</tr>
																						<%
																							if (resin.getVersion().equals("4")) {
																						%>
																						<tr>
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;主机用户:
																							</td>
																							<td><%=username%></td>
																						</tr>
																						<tr bgcolor="#F1F1F1">
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;服务器操作系统:
																							</td>
																							<td><%=resin.getOs()%></td>
																						</tr>
																						<%
																							} else if (resin.getVersion().equals("3")) {
																						%>
																						<tr>
																							<td height="29" nowrap class=txtGlobal>
																								&nbsp;配置文件路径:
																							</td>
																							<td><%=configPath%></td>
																						</tr>
																						<tr  bgcolor="#F1F1F1">
																							<td height="29" nowrap class=txtGlobal>
																								&nbsp;安装路径:
																							</td>
																							<td><%=homePath%></td>
																						</tr>
																						<%
																							}
																						%>
																						
																						<tr >
																							<td height="29" align=left nowrap class=txtGlobal>
																								&nbsp;上一次轮询:
																							</td>
																							<td><%=lasttime%></td>
																						</tr>
																						<tr bgcolor="#F1F1F1">
																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;下一次轮询:
																							</td>
																							<td><%=nexttime%></td>
																						</tr>
																						
																					</table>
																				</td>

																				<td width=20% align="center">
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
																										<td align=center>
																											<div id="avgping">
																												<strong>You need to upgrade your
																													Flash Player</strong>
																											</div>
																											<script type="text/javascript"
																												src="<%=rootPath%>/include/swfobject.js"></script>
																											<script type="text/javascript">
																				                                       var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
																				                                           so.addVariable("path", "<%=rootPath%>/amchart/");
																				                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
																				                                           so.addVariable("chart_data","<%=avgdata%>");
																				                                           so.write("avgping");
																			                                </script>
																										</td>
																									</tr>
																									<tr>
																										<td height="7" align=center>
																											<img src="<%=rootPath%>/resource/image/Loading_2.gif">
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
																									<tr bgcolor=#F1F1F1 height="26">
																										<td align="center">
																											<%=memName %>
																										</td>
																									</tr>
																									<tr height=160>
																										<td align="center">
																											<img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=tmp%>resin_mem.png">
																										</td>
																									</tr>
																									<tr height="7">
																										<td align=center>
																											&nbsp;
																											<img
																												src="<%=rootPath%>/resource/image/Loading.gif">
																										</td>
																									</tr>
																								</table>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<%
																					if (resin.getVersion().equals("4")) {
																				%>
																				<td width=20% align="center">
																					<table class="container-main-service-detail-tool">
																					<tr>
																							<td>
																								<table style="BORDER-COLLAPSE: collapse"
																									rules=none align=center border=1 cellpadding=0
																									cellspacing="0" width=100%>
																									<tr bgcolor=#F1F1F1 height="26">
																										<td align="center">
																											Heap平均利用率
																										</td>
																									</tr>
																									<tr height=160>
																										<td align="center">
																											<img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=tmp%>resin_heap.png">
																										</td>
																									</tr>
																									<tr height="7">
																										<td align=center>
																											&nbsp;
																											<img src="<%=rootPath%>/resource/image/Loading.gif">
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
																									<tr bgcolor=#F1F1F1 height="26">
																										<td align="center">
																											交换内存平均利用率
																										</td>
																									</tr>
																									<tr height=155>
																										<td align="center">
																											<img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=tmp%>resin_swap.png">
																										</td>
																									</tr>
																									<tr height="7">
																										<td align=center>
																											&nbsp; <img src="<%=rootPath%>/resource/image/Loading_2.gif">
																										</td>
																									</tr>
																									
																								</table>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<%
																					}
																				%>
																			</tr>
																		</table>
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
					<td valign=top width=200px>
						<jsp:include page="/include/resintoolbar.jsp">
							<jsp:param value="<%=resin.getId()%>" name="id" />
							<jsp:param value="<%=resin.getIpAddress()%>" name="ip" />
							<jsp:param value="resin" name="subtype" />
							<jsp:param value="middleware" name="type" />
						</jsp:include>
					</td>
				</tr>
			</table>

		</form>

	</BODY>
</HTML>