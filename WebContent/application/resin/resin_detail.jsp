<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.polling.node.Resin"%>
<%@page import="com.afunms.common.util.*"%>

<%
	String rootPath = request.getContextPath();
	String tmp = request.getParameter("id");
	String menuTable = (String) request.getAttribute("menuTable");
	Resin resin = (Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(tmp));
	String allipstr = SysUtil.doip(resin.getIpAddress());
	String avgphycon = (String) request.getAttribute("avgphycon");
	String maxphycon = (String) request.getAttribute("maxsphycon");
	String curphycon = (String) request.getAttribute("curphycon");
	String avgpingcon = (String) request.getAttribute("avgpingcon");
	String maxpingcon = (String) request.getAttribute("maxpingcon");
	String curpingcon = (String) request.getAttribute("curpingcon");
	String avgmemcon = (String) request.getAttribute("avgmemcon");
	String curmemcon = (String) request.getAttribute("curmemcon");
	String maxmemcon = (String) request.getAttribute("maxmemcon");

	String avgheapcon = (String) request.getAttribute("avgheapcon");
	String maxheapcon = (String) request.getAttribute("maxheapcon");
	String curheapcon = (String) request.getAttribute("curheapcon");
	String avgswapcon = (String) request.getAttribute("avgswapcon");
	String maxswapcon = (String) request.getAttribute("maxswapcon");
	String curswapcon = (String) request.getAttribute("curswapcon");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="gb2312"></script>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<style>
.detail-td {
	font-size: 12px;
	border-top: 1px solid #e8e9e9;
	border-left: 1px solid #e8e9e9;
	border-bottom: 1pt solid #e8e9e9;
	border-right: 1px solid #e8e9e9;
	text-align: center;
	height: 28px;
}
</style>
		<script type="text/javascript">
function setClass(){
	document.getElementById('resDetailTitle-1').className='detail-data-title';
	document.getElementById('resDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('resDetailTitle-1').onmouseout="this.className='detail-data-title'";
		}
</script>
		<script type="text/javascript">
function event(){
	mainForm.action = "<%=rootPath%>/tomcat.do?action=event"+"&flag=<%=flag%>";
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
												<jsp:include
													page="/topology/includejsp/middleware_resin.jsp">
													<jsp:param name="tmp" value="<%=tmp%>" />

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
																				<td width="100%" align="left" valign="top">
																					<table>




																					</table>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>


																<tr>
																	<td align=center>
																		<table id="service-detail-content-body"
																			class="service-detail-content-body">
																			<tr>
																				<td>
																					<table>
																						<tr>
																							<td width=98%>
																								<table border="0" cellpadding="10"
																									cellspacing="10" align=center>
																									<tr>
																										<td width=100%>
																											<br>
																											<table cellpadding="0" cellspacing="0"
																												width=98%>
																												<tr>
																													<td width="100%">
																														<div id="flashcontent1">
																															<strong>You need to upgrade your Flash Player</strong>
																														</div>
																														<script type="text/javascript">
																											               var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?title=连通曲线&tablename=resinping<%=allipstr%>", "common_line", "420", "250", "8", "#ffffff");
																											                   so.write("flashcontent1");
																										              </script>

																													</td>
																												</tr>
																												<tr>
																													<td width="420">
																														<table>
																															<tr bgcolor="#ECECEC"> 
																																<td class="detail-td">
																																	名称
																																</td>
																																<td class="detail-td">
																																	当前
																																</td>
																																<td class="detail-td">
																																	最小
																																</td>
																																<td class="detail-td">
																																	平均
																																</td>

																															</tr>
																															<tr>
																																<td class="detail-td">
																																	连通率
																																</td>
																																<td class="detail-td"><%=curpingcon%></td>
																																<td class="detail-td"><%=maxpingcon%></td>
																																<td class="detail-td"><%=avgpingcon%></td>

																															</tr>
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																										<td>
																											&nbsp;
																										</td>

																										<td align=center>
																											<br>
																											<table cellpadding="0" cellspacing="0"
																												width=98%>
																												<tr>
																													<td width="100%">
																														<div id="flashcontent2">
																															<strong>You need to upgrade your
																																Flash Player</strong>
																														</div>
																														<%
																															if (resin.getVersion().equals("4")) {
																														%>
																														<script type="text/javascript">
																										             	  var so = new SWFObject("<%=rootPath%>/flex/common_line_mid.swf?ipadress=<%=resin.getIpAddress()%>&title=Heap利用率&type=resin&colunm=heap_utilization", "common_line_mid", "420", "250", "8", "#ffffff");
																											                so.write("flashcontent2");
																										               </script>
																														<%
																															} else if (resin.getVersion().equals("3")) {
																														%>
																														<script type="text/javascript">
																										             	  var so = new SWFObject("<%=rootPath%>/flex/common_line_mid.swf?ipadress=<%=resin.getIpAddress()%>&title=内存利用率&type=resin&colunm=mem_utilization", "common_line_mid", "420", "250", "8", "#ffffff");
																											                so.write("flashcontent2");
																										               </script>
																														<%
																															}
																														%>
																													</td>
																												</tr>
																												<tr>
																													<td width="420">
																														<table >
																															<tr bgcolor="#ECECEC">
																																<td class="detail-td">
																																	名称
																																</td>
																																<td class="detail-td">
																																	当前
																																</td>
																																<td class="detail-td">
																																	最大
																																</td>
																																<td class="detail-td">
																																	平均
																																</td>

																															</tr>
																															<tr>
																																<%
																																	if (resin.getVersion().equals("4")) {
																																%>
																																<td class="detail-td">
																																	Heap利用率
																																</td>
																																<td class="detail-td"><%=curheapcon%></td>
																																<td class="detail-td"><%=maxheapcon%></td>
																																<td class="detail-data-body-list"
																																	align=center height="28"><%=avgheapcon%></td>
																																<%
																																	} else if (resin.getVersion().equals("3")) {
																																%>
																																<td class="detail-td">
																																	内存利用率
																																</td>
																																<td class="detail-td"><%=curmemcon%></td>
																																<td class="detail-td"><%=maxmemcon%></td>
																																<td class="detail-td"><%=avgmemcon%></td>
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
																											<%
																												if (resin.getVersion().equals("4")) {
																											%>

																											<br>
																											<table cellpadding="0" cellspacing="0" width=98%>
																												<tr>
																													<td width="100%">
																														<div id="flashcontent3">
																															<strong>You need to upgrade your Flash Player</strong>
																														</div>
																														<script type="text/javascript">
																										             	var so = new SWFObject("<%=rootPath%>/flex/common_line_mid.swf?ipadress=<%=resin.getIpAddress()%>&title=物理内存利用率&type=resin&colunm=physical_utilization", "common_line_mid", "420", "250", "8", "#ffffff");
																											                so.write("flashcontent3");
																										               </script>
																													</td>
																												</tr>
																												<tr>
																													<td width="420">
																														<table>
																															<tr bgcolor="#ECECEC">
																																<td class="detail-td">
																																	名称
																																</td>
																																<td class="detail-td">
																																	当前
																																</td>
																																<td class="detail-td">
																																	最大
																																</td>
																																<td class="detail-td">
																																	平均
																																</td>

																															</tr>
																															<tr>

																																<td class="detail-td">
																																	物理内存利用率
																																</td>
																																<td class="detail-td"><%=curphycon%></td>
																																<td class="detail-td"><%=maxphycon%></td>
																																<td class="detail-td"><%=avgphycon%></td>

																															</tr>
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																										<td>
																											&nbsp;
																										</td>
																										<td align=center>
																											<br>
																											<table cellpadding="0" cellspacing="0"
																												width=98%>
																												<tr>
																													<td width="100%">
																														<div id="flashcontent4">
																															<strong>You need to upgrade your Flash Player</strong>
																														</div>
																														<script type="text/javascript">
																										             var so = new SWFObject("<%=rootPath%>/flex/common_line_mid.swf?ipadress=<%=resin.getIpAddress()%>&title=交换内存利用率&type=resin&colunm=swap_utilization", "common_line_mid", "420", "250", "8", "#ffffff");
																											             so.write("flashcontent4");
																										               </script>
																													</td>
																												</tr>
																												<tr>
																													<td width="420">
																														<table>
																															<tr bgcolor="#ECECEC">
																																<td class="detail-td">
																																	名称
																																</td>
																																<td class="detail-td">
																																	当前
																																</td>
																																<td class="detail-td">
																																	最大
																																</td>
																																<td class="detail-td">
																																	平均
																																</td>

																															</tr>
																															<tr>
																																<td class="detail-td">
																																	交换内存利用率
																																</td>
																																<td class="detail-td"><%=curswapcon%></td>
																																<td class="detail-td"><%=maxswapcon%></td>
																																<td class="detail-td"><%=avgswapcon%></td>

																															</tr>
																														</table>


																														<%
																															}
																														%>
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
																					<table id="application-detail-content-footer" class="application-detail-content-footer">
																						<tr>
																							<td>
																								<table width="100%" border="0" cellspacing="0" cellpadding="0">
																									<tr>
																										<td align="left" valign="bottom">
																											<img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" />
																										</td>
																										<td></td>
																										<td align="right" valign="bottom">
																											<img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" />
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
								<td valign=top width="14%">
									<jsp:include page="/include/resintoolbar.jsp">
										<jsp:param value="<%=resin.getId()%>" name="id" />
										<jsp:param value="<%=resin.getIpAddress()%>" name="ip" />
									
										<jsp:param value="resin" name="subtype" />
										<jsp:param value="middleware" name="type" />

									</jsp:include>
								</td>
							</tr>

						</table>
					</td>
			</table>

		</form>

		
	</BODY>
</HTML>