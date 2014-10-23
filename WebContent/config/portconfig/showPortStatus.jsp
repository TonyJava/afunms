<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.HashMap"%>
<html>
	<head>
		<%
			String rootPath = request.getContextPath();
			String id = (String) request.getAttribute("id");
			String hourData = (String) request.getAttribute("hourData");
			String newip = (String) request.getAttribute("newip");
			String ip = (String) request.getAttribute("ip");
			//	String startdate = (String) request.getAttribute("startdate");
			String todate = (String) request.getAttribute("todate");
			String index = (String) request.getAttribute("index");
		%>
		<link href="<%=rootPath%>/resource/css/global/global.css"
			rel="stylesheet" type="text/css" />

		<title>端口状态图表</title>
		<script type='text/javascript' src='/afunms/dwr/interface/DWRUtil.js'></script>
		<script type='text/javascript' src='/afunms/dwr/engine.js'></script>
		<script type='text/javascript' src='/afunms/dwr/util.js'></script>
		<script language="javascript" src="/afunms/js/tool.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
		<script language="JavaScript" type="text/JavaScript">

function query_ok()
{
	//var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	mainForm.action = "<%=rootPath%>/portconfig.do?action=showPortStatus&ip=<%=ip%>&index=<%=index%>&todate="+endtime;
	mainForm.submit();
}
function ping_cancel()
{
window.close();
}

</script>

		<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;


function setClass(){
	document.getElementById('hostReportTitle-5').className='detail-data-title';
	document.getElementById('hostReportTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('hostReportTitle-5').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>

		<style>
body {
	background-image:
		url(${pageContext.request.contextPath}/common/images/menubg_report.jpg)
		;
	TEXT-ALIGN: center;
}
</style>
	</head>
	<body id="body" class="body" onload="init();">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden id="ipaddress" name="ipaddress"
				value=<%=request.getParameter("ip")%>>
			<table id="container-main" class="container-main">
				<tr>
					<td>
						<table id="container-main-win" class="container-main-win">
							<tr>
								<td>
									<table id="win-content" class="win-content">
										<tr>
											<td>
												<table id="win-content-header" class="win-content-header">
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="win-content-title" style="align: center">
															&nbsp;端口状态
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" height="29" />
														</td>
													</tr>

												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="win-content-body" class="win-content-body">
													<tr>
														<td>
															<table bgcolor="#ECECEC">
																<tr align="left" valign="center">
																	<td height="28" align="left">
																		<!-- 	开始日期
																		<input type="text" id="mystartdate" name="startdate"
																			value="" size="10">
																		<a onclick="event.cancelBubble=true;"
																			href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																			<img id=imageCalendar1 align=absmiddle width=34
																				height=21
																				src="<%=rootPath%>/include/calendar/button.gif"
																				border=0> </a>  -->
																		选择日期
																		<input type="text" id="mytodate" name="todate"
																			value="<%=todate%>" size="10" />
																		<a onclick="event.cancelBubble=true;"
																			href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																			<img id=imageCalendar2 align=absmiddle width=34
																				height=21
																				src="<%=rootPath%>/include/calendar/button.gif"
																				border=0> </a>
																		<input type="button" name="doprocess" value="确定" onclick="query_ok()">
																	</td>

																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="win-data-title" style="height: 29px;"></td>
													</tr>
													<tr align="left" valign="center">
														<td height="28" align="left" border="0">

															<input type=hidden name="eventid">

															<table border="0" width="90%">

																<!--  
																<tr>
																	<td colspan="4" align="center">
																		<img
																			src="<%=rootPath%>/resource/image/jfreechart/<%=newip%>portstatus.png" />
																	</td>
																</tr>-->
															<% if(hourData.equals("0"))
															 {
															 %>
															 <tr ><td>&nbsp;</td></tr>
															 <tr ><td class="win-content-title" align=center height="28">无信息显示,请确认是否已设置端口的监控状态,刚设置为关键端口的，须等下次轮询采集后展示!</td></tr>
															 <tr><td>&nbsp;</td></tr>
															
															 <%}else { %>
																<tr>
																	<td>
																		<div id="lineOne">
																			<strong>You need to upgrade your Flash
																				Player</strong>
																		</div>
																		<script type="text/javascript">
																		// <![CDATA[		
																		var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "amline","720", "200", "8", "#FFFFFF");
																		so.addVariable("path", "<%=rootPath%>/amchart/");
																		so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/hourport_setting.xml"));
																		so.addVariable("chart_data", "<%=hourData%>");
																		so.write("lineOne");
																		// ]]>
																	</script>
																	</td>
																</tr>
																<%} %>
															</table>
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

			<div align=center>
				<input type=button value="关闭窗口" onclick="window.close()">
			</div>
			<br>
		</form>
	</body>
</html>