<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@ page import="com.afunms.polling.om.oraclerac.*"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.common.util.CreatePiePicture"%>
<%@page import="com.afunms.initialize.*"%>


<%
  String rootPath = request.getContextPath();;
  NtpConfig vo  = (NtpConfig)request.getAttribute("vo");    
  String id = (String)request.getAttribute("id");
  Hashtable  tmpHash = (Hashtable)request.getAttribute("ntpHash");
  String ntpdate = "";
  String currTime = "";
  String ipaddress = "";
  if( tmpHash != null){
  	Hashtable ntpHash = (Hashtable) tmpHash.get(vo.getIpAddress());
  	ntpdate = (String)ntpHash.get("datetime");
  	currTime = (String)ntpHash.get("collecttime");
  	ipaddress = (String)ntpHash.get("ipaddress");
  }
  
   String startdate = (String)request.getAttribute("startdate");
  String todate = (String)request.getAttribute("todate");
  SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
  if(startdate == null){
  	startdate = sdf1.format(new Date());
  }
  if(todate == null){
  	todate = sdf1.format(new Date());
  }
  
  
%>
<% String menuTable = (String)request.getAttribute("menuTable");%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

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

		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>


		<script language="javascript">	



  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  } 
  
// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("topology/network/index.jsp", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}

</script>
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
	document.getElementById('nasDetailTitle-1').className='detail-data-title';
	document.getElementById('nasDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('nasDetailTitle-1').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>



<script>
$(document).ready(function(){
	//$("#testbtn").bind("click",function(){
	//	gzmajax();
	//});
setInterval(gzmajax,60000);
});
</script>



	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="orderflag">
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
								<td class="td-container-main-application-detail">
									<table id="container-main-application-detail"
										class="container-main-application-detail">
										<tr>
											<td>
												<table class="container-main-application-detail">
													<tr>
														<td> 
															<table id="application-detail-content"
	class="application-detail-content">
	<tr>
		<td>
			 <jsp:include page="/topology/includejsp/detail_content_top.jsp">
			 	<jsp:param name="contentTitle" value="数据信息"/> 
			 </jsp:include>
		</td>
	</tr>
	<tr>
		<td>
			<table id="application-detail-content-body" class="application-detail-content-body">
				<tr>
					<td>
						<table align=center cellpadding=0 cellspacing="0" width=100%>
							<tr>
								<td width="80%" align="left" valign="top">
									<table>
										<tr>
											<td>
												<table>
													<tr>
														<td width="15%" height="26" align="left" nowrap class=txtGlobal>
															&nbsp;别名:
														</td>
														<td width="35%"><%=vo.getAlias() %>
														</td>
													</tr>
													<tr bgcolor="#F1F1F1">
														<td width="15%" height="26" align="left" nowrap class=txtGlobal>
															&nbsp;IP地址:
														</td>
														<td width="35%"><%=vo.getIpAddress() %>
														</td>
													</tr>
													<tr>
														<td width="15%" height="26" align="left" nowrap class=txtGlobal>
															&nbsp;类型:
														</td>
														<td width="35%">ntp
														</td>
													</tr>
													<tr bgcolor="#F1F1F1">
														<td width="15%" height="26" align="left" nowrap class=txtGlobal>
															&nbsp;系统时间:
														</td>
														<td width="35%"><%=currTime %>
														</td>
													</tr>
													<tr>
														<td width="15%" height="26" align="left" nowrap class=txtGlobal>
															&nbsp;NTP时间:
														</td>
														<td width="35%"><%=ntpdate %>
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
			 <jsp:include page="/topology/includejsp/detail_content_footer.jsp"/>
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
												<table id="application-detail-data"
													class="application-detail-data">
													<tr>
													<td>
														<table class="application-detail-data-body">
															<tr bgcolor="#ECECEC" height="28">
																<td colspan=5>


																	开始日期
																	<input type="text" name="startdate" value="<%=startdate%>" size="10">
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																		<img id=imageCalendar1 width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a> 截止日期
																	<input type="text" name="todate" value="<%=todate%>" size="10" />
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																		<img id=imageCalendar2 width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a> 事件等级
																	<select name="level1">
																		<option value="99">
																			不限
																		</option>
																		<option value="0" >
																			提示信息
																		</option>
																		<option value="1" >
																			普通事件
																		</option>
																		<option value="2" >
																			严重事件
																		</option>
																		<option value="3" selected>
																			紧急事件
																		</option>
																	</select>

																	<input type="button" id="process1" name="process1" value="查 询" onclick="doQuery()">
																</td>
															</tr>
															<tr align="right" bgcolor="#ECECEC">
																<td>
																	<table>
																		<tr>
																			<td width="75%">
																				&nbsp;
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																			<td height=15>
																				&nbsp;&nbsp;
																				<input type="button" name="" value="接受处理"
																					onclick='batchAccfiEvent();' />
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																			<td height=15>
																				&nbsp;&nbsp;
																				<input type="button" name="" value="填写报告"
																					onclick='batchDoReport();' />
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																			<td height=15>
																				&nbsp;&nbsp;
																				<input type="button" name="submitss" value="修改等级"
																					onclick="batchEditAlarmLevel();">
																				&nbsp;&nbsp;
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr bgcolor="DEEBF7">
																<td>
																	<table width="100%" border="0" cellpadding="3"
																		cellspacing="1" bgcolor="#FFFFFF">

																		<tr height="28" bgcolor="#ECECEC">
																			<td class="application-detail-data-body-title">
																				<INPUT type="checkbox" name="checkall"
																					onclick="javascript:chkall()">
																			</td>
																			<td class="application-detail-data-body-title"
																				width="10%">
																				<strong>事件等级</strong>
																			</td>
																			<td class="application-detail-data-body-title"
																				width="40%">
																				<strong>事件描述</strong>
																			</td>
																			<td class="application-detail-data-body-title">
																				<strong>登记日期</strong>
																			</td>
																			<td class="application-detail-data-body-title">
																				<strong>登记人</strong>
																			</td>
																			<td class="application-detail-data-body-title">
																				<strong>处理状态</strong>
																			</td>
																			<td class="application-detail-data-body-title">
																				<strong>操作</strong>
																			</td>
																		</tr>
																		<%
																			int index = 0;
																			java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat(
																					"MM-dd HH:mm");
																			List list = (List) request.getAttribute("list");
																			for (int i = 0; i < list.size(); i++) {
																				index++;
																				EventList eventlist = (EventList) list.get(i);
																				Date cc = eventlist.getRecordtime().getTime();
																				Integer eventid = eventlist.getId();
																				String eventlocation = eventlist.getEventlocation();
																				String content = eventlist.getContent();
																				String level = String.valueOf(eventlist.getLevel1());
																				String status = String.valueOf(eventlist.getManagesign());
																				String s = status;
																				String showlevel = null;
																				String bgcolor = "";
																				String act = "处理报告";
																				if ("0".equals(level)) {
																					level = "提示信息";
																					bgcolor = "bgcolor='blue'";
																				}
																				if ("1".equals(level)) {
																					level = "普通告警";
																					bgcolor = "bgcolor='yellow'";
																				}
																				if ("2".equals(level)) {
																					level = "严重告警";
																					bgcolor = "bgcolor='orange'";
																				}
																				if ("3".equals(level)) {
																					level = "紧急告警";
																					bgcolor = "bgcolor='red'";
																				}
																				String bgcolorstr = "";
																				if ("0".equals(status)) {
																					status = "未处理";
																					bgcolorstr = "#9966FF";
																				}
																				if ("1".equals(status)) {
																					status = "处理中";
																					bgcolorstr = "#3399CC";
																				}
																				if ("2".equals(status)) {
																					status = "处理完成";
																					bgcolorstr = "#33CC33";
																				}
																				String rptman = eventlist.getReportman();
																				String rtime1 = _sdf.format(cc);
																		%>

																		<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
																			<td class="detail-data-body-list">
																				<INPUT type="checkbox" name="checkbox"
																					value="<%=eventlist.getId()%>"><%=i + 1%></td>
																			<td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
																			<td class="application-detail-data-body-list">
																				<%=content%></td>
																			<td class="application-detail-data-body-list">
																				<%=rtime1%></td>
																			<td class="application-detail-data-body-list">
																				<%=rptman%></td>
																			<td class="application-detail-data-body-list"
																				bgcolor=<%=bgcolorstr%>>
																				<%=status%></td>
																			<td class="application-detail-data-body-list"
																				align="center">
																				<%
																					if ("0".equals(s)) {
																				%>
																				<input type="button" value="接受处理" class="button"
																					onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 500, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
																				<%
																					}
																						if ("1".equals(s)) {
																				%>
																				<input type="button" value="填写报告" class="button"
																					onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
																				<%
																					}
																						if ("2".equals(s)) {
																				%>
																				<input type="button" value="查看报告" class="button"
																					onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
																				<%
																					}
																				%>
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
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td  width=13% valign=top align=right>
							<jsp:include page="/include/dptoolbar.jsp">
								<jsp:param value="<%=id %>" name="id" />
								<jsp:param value="ntp" name="subtype"/>
							</jsp:include>
							
						</td>
				</tr>
			</table>

		</form>
		
	</BODY>
</HTML>