<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.application.model.ApacheConfig"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.detail.service.apacheInfo.ApacheInfoService"%>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="com.afunms.alarm.util.AlarmConstant"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%
	String runmodel = PollingEngine.getCollectwebflag();
	String rootPath = request.getContextPath();
	ApacheConfig vo = (ApacheConfig) request.getAttribute("vo");
	String ipaddress = "";
	int id = 0;
	Hashtable apache_ht = null;
	String alias = "";
	int port = 0;
	if (vo != null) {
		ipaddress = vo.getIpaddress();
		id = vo.getId();
		alias = vo.getAlias();
		port = vo.getPort();
	}
	if ("0".equals(runmodel)) {
		//采集与访问是集成模式
		apache_ht = (Hashtable) com.afunms.common.util.ShareData.getApachedata().get("apache" + ":" + ipaddress);
	} else {
		//采集与访问是分离模式
		ApacheInfoService apacheInfoService = new ApacheInfoService();
		apache_ht = apacheInfoService.getApacheDataHashtable(vo.getId() + "");
	}
	if (apache_ht == null)
		apache_ht = new Hashtable();
	//String status_str = (String) apache_ht.get("status");
	//Integer status = 0;
	//if (status_str != null) {
	//	status = Integer.valueOf(status_str);
	//}
	String connrate = request.getAttribute("connrate").toString();
	connrate = connrate.replaceAll("%", "");

	String menuTable = (String) request.getAttribute("menuTable");
	//status = SystemSnap.getNodeStatus(id + "", AlarmConstant.TYPE_MIDDLEWARE, "apache");
	//String imgsrc = rootPath + "/resource/" + NodeHelper.getCurrentStatusImage(status);
	String apache_ht_version = (String) (apache_ht.get("version") == null ? " " : apache_ht.get("version"));
	String apache_ht_built = (String) (apache_ht.get("built") == null ? " " : apache_ht.get("built"));
	String apache_ht_current = (String) (apache_ht.get("current") == null ? " " : apache_ht.get("current"));
	String apache_ht_restart = (String) (apache_ht.get("restart") == null ? " " : apache_ht.get("restart"));
	String apache_ht_parent = (String) (apache_ht.get("parent") == null ? " " : apache_ht.get("parent"));
	String apache_ht_uptime = (String) (apache_ht.get("uptime") == null ? " " : apache_ht.get("uptime"));
	String apache_ht_accesses = (String) (apache_ht.get("accesses") == null ? " " : apache_ht.get("accesses"));
	String apache_ht_traffic = (String) (apache_ht.get("traffic") == null ? " " : apache_ht.get("traffic"));
	float avgPing = Float.parseFloat(connrate);
	StringBuffer dataStr = new StringBuffer();
	dataStr.append("连通;").append(Math.round(avgPing)).append(";false;7CFC00\\n");
	dataStr.append("未连通;").append(100 - Math.round(avgPing)).append(";false;FF0000\\n");
	String avgdata = dataStr.toString();
	String allipstr = SysUtil.doip(ipaddress);
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


		<script language="JavaScript" type="text/JavaScript">


</script>
		<script type="text/javascript">
function setClass(){
	document.getElementById('apacheDetailTitle-0').className='detail-data-title';
	document.getElementById('apacheDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('apacheDetailTitle-0').onmouseout="this.className='detail-data-title'";
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
												<jsp:include page="/topology/includejsp/middleware_apa.jsp">
													<jsp:param name="id" value="<%=id%>" />

												</jsp:include>
											</td>
										</tr>
										<tr>
											<td>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
														<td>
															<%=apacheDetailTitleTable%>
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
																								&nbsp;名称:
																							</td>
																							<td width="30%"><%=alias%></td>
																							<td width="20%" height="29" align="left" nowrap>
																								
																							</td>
																							<td width="30%">
																								
																							</td>
																						</tr>
																						<tr bgcolor="#F1F1F1">


																							<td height="29" align=left nowrap>
																								&nbsp;IP地址:
																							</td>
																							<td><%=ipaddress%></td>

																							<td height="29" class=txtGlobal align="left"
																								nowrap>
																								&nbsp;端口:
																							</td>
																							<td><%=port%></td>
																						</tr>
																						<tr>
																							<td height="29" align=left nowrap>
																								&nbsp;版本号:
																							</td>
																							<td><%=apache_ht_version%></td>
																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;父程序的世代编号:
																							</td>
																							<td><%=apache_ht_parent%></td>
																						</tr>
																						<tr bgcolor="#F1F1F1">

																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;接收的联机数量:
																							</td>
																							<td><%=apache_ht_accesses%></td>
																							<td height="29" class=txtGlobal nowrap>
																								&nbsp;传输的数据量:
																							</td>
																							<td>
																								<%=apache_ht_traffic%>
																							</td>
																						</tr>
																						<tr>
																							<td height="29" align=left nowrap>
																								&nbsp;编译安装时间:
																							</td>
																							<td><%=apache_ht_built%></td>
																							<td height="29" align=left nowrap>
																								&nbsp;系统时间:
																							</td>
																							<td><%=apache_ht_current%></td>
																						</tr>


																						<tr bgcolor="#F1F1F1">
																							<td height="29" align=left nowrap>
																								&nbsp;重新启动时间:
																							</td>
																							<td><%=apache_ht_restart%></td>
																							<td height="29" nowrap>
																								&nbsp;重新启动时间:
																							</td>
																							<td><%=apache_ht_restart%></td>
																						</tr>

																						<tr>
																							<td height="29" align=left nowrap>
																								&nbsp;服务器运行时间:
																							</td>
																							<td><%=apache_ht_uptime%></td>
																							<td colspan=2></td>
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
																											var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?title=连通曲线&tablename=apaping<%=allipstr%>", "common_line", "400", "250", "8", "#ffffff");
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
																											var so = new SWFObject("<%=rootPath%>/flex/common_pie.swf?title=服务连通&tablename=apaping<%=allipstr%>&category=thevalue&type=util", "common_line", "400", "250", "8", "#ffffff");
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
						<jsp:include page="/include/apatoolbar.jsp">
							<jsp:param value="<%=ipaddress%>" name="ipaddress" />
							<jsp:param value="<%=id%>" name="id" />
							<jsp:param value="<%=flag%>" name="flag" />
							<jsp:param value="middleware" name="type" />
							<jsp:param value="apache" name="subtype" />
						</jsp:include>
					</td>
				</tr>
			</table>

		</form>

	</BODY>
</HTML>