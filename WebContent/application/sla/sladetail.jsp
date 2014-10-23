<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.polling.base.*"%>
<%@page import="com.afunms.polling.*"%>

<%@page import="java.util.*"%>

<%
	String myflag = (String) request.getAttribute("flag");
	Integer myId = (Integer) request.getAttribute("id");
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");

	List slalist = (List) request.getAttribute("list");
	SlaNodeConfig slaconf = (SlaNodeConfig) request.getAttribute("slanode");
	myId = slaconf.getId();
	String avgresponse = (String) request.getAttribute("avgresponse");
	String ipaddress = (String) request.getAttribute("ipaddress");
	String statusValue = (String) request.getAttribute("statusValue");
	String coltime = (String) request.getAttribute("coltime");
	
		int reslength = 3;//响应时间显示的位数；
	if (avgresponse.indexOf(".") > 0) {
		avgresponse = avgresponse.substring(0, avgresponse.indexOf("."));//删除小数点后值
	}
	if (avgresponse.length() > reslength) {
		avgresponse = avgresponse.substring(0, reslength);
	}

	String flag_1 = (String) request.getAttribute("flag");

	int status = 0;
	Node node = (Node) PollingEngine.getInstance().getWebByID(
			slaconf.getId());
	String alarmmessage = "";
	if (node != null) {
		status = node.getStatus();
		List alarmlist = node.getAlarmMessage();
		if (alarmlist != null && alarmlist.size() > 0) {
			for (int k = 0; k < alarmlist.size(); k++) {
				alarmmessage = alarmmessage
						+ alarmlist.get(k).toString();
			}
		}
	}
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
			charset="utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="JavaScript" type="text/JavaScript">


var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}
	setClass();
}
function setClass(){
	document.getElementById('slaDetailTitle-0').className='detail-data-title';
	document.getElementById('slaDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('slaDetailTitle-0').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
		
}

function show_graph(){
      mainForm.action = "<%=rootPath%>/ciscosla.do?action=detail";
      mainForm.submit();
} 


</script>


	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" id="flag" name="flag" value="<%=flag_1%>">
			<input type="hidden" id="id" name="id" value="<%=myId%>">
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
					<td class="td-container-main-detail">
						<table id="container-main-detail" class="container-main-detail">
							<tr>
								<td>
									<table id="detail-content" class="detail-content">
										<tr>
											<td>
												<table id="detail-content-header"
													class="detail-content-header">
													<tr>
														<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
														<td class="content-title">
															SLA 详细信息
														</td>
														<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="detail-content-body" class="detail-content-body">
													<tr>
														<td>
															<table >
																<tr>
																	<td>

																		<table>
																			<tr>
																				<td>

																					&nbsp;&nbsp;名称
																					<select name="id">
																						<%
																							if (slalist != null && slalist.size() > 0) {
																								for (int i = 0; i < slalist.size(); i++) {
																									SlaNodeConfig _slaconfig = (SlaNodeConfig) slalist.get(i);
																									if (_slaconfig.getId() == slaconf.getId()) {
																						%>

																						<option value="<%=_slaconfig.getId()%>"
																							selected="selected">
																							<%=_slaconfig.getName()%></option>


																						<%
																							} else {
																						%>
																						<option value="<%=_slaconfig.getId()%>">
																							<%=_slaconfig.getName()%></option>
																						<%
																							}
																								}
																							}
																						%>
																					</select>
																					<input type="button" onclick="show_graph()"
																						class=button value="查询">
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																					<br>
																				</td>


																			</tr>
																			<tr>
																				<td>
																					<table cellspacing="10" border=0 >
																						<tr>
																							<td width="60%" align="center" valign=top
																								cellspacing="0" border=0>

																								<table style="BORDER-COLLAPSE: collapse"
																									bordercolor=#cedefa cellpadding=0 rules=none
																									width=100% align=center border=1 algin="center">
																									<tr bgcolor="#F1F1F1">
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;名称:
																										</td>
																										<td width="70%"><%=slaconf.getName()%></td>
																									</tr>
																									<tr >
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;描述:
																										</td>
																										<td width="70%"><%=slaconf.getDescr()%></td>
																									</tr>
																									<tr bgcolor="#F1F1F1">
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;IP地址:
																										</td>
																										<td width="70%">
																											<%=ipaddress%>
																										</td>
																									</tr>
																									<tr>
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;SLA类型:
																										</td>
																										<td width="70%">
																											<%=slaconf.getSlatype()%>
																										</td>
																									</tr>
																									<tr bgcolor="#F1F1F1">
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;入口号:
																										</td>
																										<td width="70%">
																											<%=slaconf.getEntrynumber()%>
																										</td>
																									</tr>
																									<tr >
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;状态:
																										</td>
																										<td width="70%">
																													<% if("100".equals(statusValue)){%>
		<img src="<%=rootPath%>/resource/image/topo/a_level_0.gif" border="0">
		<%}else{%>
		<img src="<%=rootPath%>/resource/image/topo/a_level_1.gif" border="0">&nbsp;
		<%}%>
																										</td>
																									</tr>
																									<tr  bgcolor="#F1F1F1">
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;采集时间:
																										</td>
																										<td width="70%">
																											<%=coltime%>
																										</td>
																									</tr>
																									<tr>
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;
																										</td>
																										<td width="70%">
																											&nbsp;
																										</td>
																									</tr>

																								</table>
																							</td>
																							<td width="40%" align="center">
																								<table cellPadding=0 cellspacing="0" align="center" border="1" bordercolor="#D3D3D3">
																									<tr>
		<td width="50%" align="center" valign="top">
			<table  align=center  cellpadding=0 cellspacing="0" width=100%>
				<tr bgcolor=#F1F1F1 height="29">
					<td align="center">今日平均成功率 </td>
				</tr>
				<tr  height=160>
					<td align="center"> <img src="<%=rootPath%>/resource/image/jfreechart/reportimg/slastatus<%=slaconf.getId()%>pingdata.png"> </td>
				</tr>
				<tr height="7">
					<td align=center> <img src="<%=rootPath%>/resource/image/Loading_2.gif"> </td>
				</tr>
			</table>
		</td>

		<td width="50%" align="center" valign="top">
			<table align=center  cellpadding=0 cellspacing="0" width=100%>
				<tr bgcolor=#F1F1F1 height="29">
					<td align="center">今日平均RTT</td>
				</tr>
				<tr height=160>
					<td align="center">
					
<%
										if (avgresponse != null && avgresponse != "") {
											for (int i = 0; i < reslength - avgresponse.length(); i++) {
									%>
									<img src="<%=rootPath%>/resource/image/chartdirector/0.png" border="0">
									<%
										}
											for (int i = 0; i < avgresponse.length(); i++) {
									%>
									<img
										src="<%=rootPath%>/resource/image/chartdirector/<%=avgresponse.charAt(i)%>.png"
										border="0">
									<%
										}
										} else {
									%>
									<%
										for (int i = 0; i < reslength; i++) {
									%>
									<img src="<%=rootPath%>/resource/image/chartdirector/0.png" border="0">
									<%
										}
										}
									%>
									<img src="<%=rootPath%>/resource/image/chartdirector/ms.png" border="0">					
					
					</td>
				</tr>
				<tr height="7"> 
					<td align=center>&nbsp;<img src="<%=rootPath%>/resource/image/Loading.gif"></td>
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
										<tr>
											<td>
												<table id="detail-content-footer"
													class="detail-content-footer">
													<tr>
														<td>
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
																	<td></td>
																	<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
									<table id="detail-data" class="detail-data">
										<tr>
											<td class="detail-data-header">
												<%=slaDetailTitleTable%>
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
																	<td width="48%" align="center">
																		<table width="100%" cellspacing="0" cellpadding="0"
																			align="center">
																			<tr>
																				<td width="100%" align="center">
																					<div id="flashcontent2">
																						<strong>You need to upgrade your Flash
																							Player</strong>
																					</div>
																					<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_area.swf?tablename=slartt<%=myId%>&title=RTT响应时间", "common_area", "400", "250", "8", "#ffffff");
																											so.write("flashcontent2");
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
																											//var so = new SWFObject("<%=rootPath%>/flex/Web_Ping_Line.swf?id=<%=slaconf.getId()%>", "Web_Ping_Line", "400", "250", "8", "#ffffff");
																											var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?tablename=slastatus<%=myId%>&title=SLA执行成功状态", "common_area", "400", "250", "8", "#ffffff");
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
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td width=15% valign=top>
						<jsp:include page="/include/slatoolbar.jsp">
							<jsp:param value="<%=slaconf.getId()%>" name="id" />
						</jsp:include>
					</td>
				</tr>
			</table>

		</form>
		<script>			
Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	    Ext.get("process").on("click",function(){
  
  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
  mainForm.action = "<%=rootPath%>/web.do?action=sychronizeData&id=<%=slaconf.getId()%>&flag=<%=flag_1%>&page=detail";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>