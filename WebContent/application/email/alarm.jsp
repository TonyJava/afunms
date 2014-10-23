<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="java.util.List"%>
<%@page import="com.afunms.application.model.EmailMonitorConfig"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.polling.base.*"%>
<%@page import="java.util.*"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.inform.util.SystemSnap" %>
<%
	String startdate = (String) request.getAttribute("startdate");
	String todate = (String) request.getAttribute("todate");
	int level1 = Integer.parseInt(request.getAttribute("level1") + "");
	int _status = Integer.parseInt(request.getAttribute("status") + "");

	String level0str = "";
	String level1str = "";
	String level2str = "";
	String level3str = "";
	if (level1 == 0) {
		level0str = "selected";
	} else if (level1 == 1) {
		level1str = "selected";
	} else if (level1 == 2) {
		level2str = "selected";
	} else if (level1 == 3) {
		level3str = "selected";
	}
	String status0str = "";
	String status1str = "";
	String status2str = "";
	if (_status == 0) {
		status0str = "selected";
	} else if (_status == 1) {
		status1str = "selected";
	} else if (_status == 2) {
		status2str = "selected";
	}
	String rootPath = request.getContextPath();
	String flag_1 = (String) request.getAttribute("flag");
	String timeFormat = "yyyy-MM-dd";
	String fdate = (String) request.getAttribute("from_date1");
	String tdate = (String) request.getAttribute("to_date1");
	String fhour = (String) request.getAttribute("from_hour");
	String thour = (String) request.getAttribute("to_hour");
	
	Integer myId = (Integer) request.getAttribute("id");
	java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(
			timeFormat);
	String from_date1 = "";
	if (fdate == null) {
		from_date1 = timeFormatter.format(new java.util.Date());
	} else {
		from_date1 = fdate;

	}
	String to_date1 = "";
	if (tdate == null) {
		to_date1 = timeFormatter.format(new java.util.Date());
	} else {
		to_date1 = tdate;

	}

	String from_hour = "";
	if (fhour == null) {
		from_hour = new java.util.Date().getHours() + "";
	} else {
		from_hour = fhour;

	}
	String to_hour = "";
	if (thour == null) {
		to_hour = new java.util.Date().getHours() + "";
	} else {
		to_hour = thour;

	}
	EmailMonitorConfig queryconf = (EmailMonitorConfig) request
			.getAttribute("initconf");

	String conn_name = request.getAttribute("conn_name").toString();
	String valid_name = request.getAttribute("valid_name").toString();
	String fresh_name = request.getAttribute("fresh_name").toString();
	String wave_name = request.getAttribute("wave_name").toString();
	String delay_name = request.getAttribute("delay_name").toString();
	String connrate = request.getAttribute("connrate").toString();
	String validrate = request.getAttribute("validrate").toString();
	String freshrate = request.getAttribute("freshrate").toString();
	String connsrc = rootPath + "/resource/image/jfreechart/"
			+ conn_name + ".png";
	String validsrc = rootPath + "/resource/image/jfreechart/"
			+ valid_name + ".png";
	String freshsrc = rootPath + "/resource/image/jfreechart/"
			+ fresh_name + ".png";
	String wavesrc = rootPath + "/resource/image/jfreechart/"
			+ wave_name + ".png";
	String delaysrc = rootPath + "/resource/image/jfreechart/"
			+ delay_name + ".png";
	List urllist = (List) request.getAttribute("urllist");

	int status = 0;
	Node node = (Node) PollingEngine.getInstance().getMailByID(
			queryconf.getId());
	String alarmmessage = "";
	if (node != null) {
		//status = node.getStatus();
		status = SystemSnap.getNodeStatus(node);
		List alarmlist = node.getAlarmMessage();
		if (alarmlist != null && alarmlist.size() > 0) {
			for (int k = 0; k < alarmlist.size(); k++) {
				alarmmessage = alarmmessage
						+ alarmlist.get(k).toString();
			}
		}
	}

	SupperDao supperdao = new SupperDao();
	Supper supper = null;
	String suppername = "";
	try {
		supper = (Supper) supperdao.findByID(queryconf.getSupperid()
				+ "");
		if (supper != null)
			suppername = supper.getSu_name() + "("
					+ supper.getSu_dept() + ")";
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		supperdao.close();
	}
%>

<%
	String menuTable = (String) request.getAttribute("menuTable");
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
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
		<script language="JavaScript" type="text/JavaScript">

function batchAccfiEvent(){
		var eventids = ''; 
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
	 				if (eventids==""){
	 					eventids=checkbox.value;
	 				}else{
	 					eventids=eventids+","+checkbox.value;
	 				}
 				}
			}
		}
        if(eventids == ""){
        	alert("未选中");
        	return ;
        }
		window.open("<%=rootPath%>/alarm/event/batch_accitevent.jsp?eventids="+eventids,"accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0");
	}
	
	//batchDoReport();
	function batchDoReport(){
		 var eventids = ''; 
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
	 				if (eventids==""){
	 					eventids=checkbox.value;
	 				}else{
	 					eventids=eventids+","+checkbox.value;
	 				}
 				}
			}
		}
        if(eventids == ""){
        	alert("未选中");
        	return ;
        }
		window.open("<%=rootPath%>/alarm/event/batch_doreport.jsp?eventids="+eventids,"accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0");
	}
	
	function batchEditAlarmLevel(){
		var eventids = ''; 
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
	 				if (eventids==""){
	 					eventids=checkbox.value;
	 				}else{
	 					eventids=eventids+","+checkbox.value;
	 				}
 				}
			}
		}
        if(eventids == ""){
        	alert("未选中");
        	return ;
        }
		window.open("<%=rootPath%>/alarm/event/batch_editAlarmLevel.jsp?eventids="+eventids,"accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0");
	}

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
}//添加菜单	
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
	document.getElementById('emailDetailTitle-1').className='detail-data-title';
	document.getElementById('emailDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('emailDetailTitle-1').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
		
}

function show_graph(){
      mainForm.action = "<%=rootPath%>/mail.do?action=detail";
      mainForm.submit();
} 
function query()
  {  
     mainForm.action = "<%=rootPath%>/mail.do?action=alarm";
     mainForm.submit();
  }

</script>


	</head>
	<body id="body" class="body" onload="initmenu();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
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
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content" colspan=2>
									<table id="container-main-content"
										class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
														<td class="content-title">
															<b>应用 >> 服务管理 >> 邮件监视信息</b>
														</td>
														<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body"
													style="text-align: center;" cellspacing="10">
													<tr>
														<td align=left>
															服务名称
															<select name="id">
																<%
																	if (urllist != null && urllist.size() > 0) {
																		for (int i = 0; i < urllist.size(); i++) {
																			EmailMonitorConfig webconfig = (EmailMonitorConfig) urllist
																					.get(i);
																%>

																<option value="<%=webconfig.getId()%>">
																	<%=webconfig.getName()%></option>

																<%
																	}
																	}
																%>
															</select>
															<input type="button" onclick="show_graph()" class=button
																value="查询">
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
															<br>
														</td>
													</tr>
													<tr style="text-align: center;">
														<td align="center" width="60%" style="text-align: center;"
															cellspacing="10">
															<table style="BORDER-COLLAPSE: collapse"
																bordercolor=#ECECEC cellpadding=0 rules=none width=100%
																align=center border=1 algin="center">
																<tr bgcolor="#F1F1F1">
																	<td width="30%" height="26" align="left" nowrap>
																		&nbsp;名称:
																	</td>
																	<td width="70%"><%=queryconf.getName()%></td>
																</tr>
																<tr>
																	<td width="30%" height="26" align="left" nowrap>
																		&nbsp;类型:
																	</td>
																	<td width="70%">
																		邮件服务监视
																	</td>
																</tr>
																<tr bgcolor="#F1F1F1">
																	<td width="30%" height="26" align="left" nowrap>
																		&nbsp;状态:
																	</td>
																	<td width="70%">
																		<img
																			src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(status)%>"
																			border="0" alt=<%=alarmmessage%>>
																	</td>
																</tr>
																<tr>
																	<td height="29" align="left">
																		&nbsp;邮件地址:
																	</td>
																	<td>
																		&nbsp;<%=queryconf.getAddress()%>
																	</td>
																</tr>
																<tr bgcolor="#F1F1F1">
																	<td width="30%" height="26" align=left nowrap>
																		&nbsp;发送帐号:
																	</td>
																	<td width="70%"><%=queryconf.getUsername()%></td>
																</tr>
																<tr>
																	<td height="29" align="left">
																		&nbsp;接收邮箱:
																	</td>
																	<td>
																		&nbsp;<%=queryconf.getRecivemail()%>
																	</td>
																</tr>
																<tr>
																	<td height="29" align="left">
																		&nbsp;上次轮询时间:
																	</td>
																	<td><%=request.getAttribute("lasttime")%></td>
																</tr>
																<tr>
																	<td height="29" class=txtGlobal valign=center nowrap>
																		&nbsp;供应商:
																	</td>
																	<td>
																		<%
																			if (supper != null) {
																		%>
																		<a href="#" style="cursor: hand"
																			onclick="window.showModalDialog('<%=rootPath%>/supper.do?action=read&id=<%=supper.getSu_id()%>',window,',dialogHeight:400px;dialogWidth:600px')"><%=suppername%></a>
																		<%
																			}
																		%>
																	</td>
																</tr>

															</table>
															<!--<table cellspacing="1" cellpadding="0" width="100%" align="center">
													            	<tr> 
													         		<td width="100%" > 
													         			<div id="flashcontent1">
																			<strong>You need to upgrade your Flash Player</strong>
																		</div>
																		<script type="text/javascript">
																			var so = new SWFObject("<%=rootPath%>/flex/Email_Info.swf?names=<%=queryconf.getName()%>&adress=<%=queryconf.getAddress()%>&send=<%=queryconf.getUsername()%>&accept=<%=queryconf.getRecivemail()%>&lasttime=<%=request.getAttribute("lasttime")%>&nexttime=<%=request.getAttribute("nexttime")%>", "Email_Info", "400", "250", "8", "#ffffff");
																			so.write("flashcontent1");
																		</script>				
													                	</td>
															</tr> 
          														</table>-->
														</td>
														<td align="center" width="40%" valign=top>
															<table width="100%" cellspacing="1" cellpadding="1">
																<tr>
																	<td width="100%">
																		<div id="flashcontent2">
																			<strong>You need to upgrade your Flash
																				Player</strong>
																		</div>
																		<script type="text/javascript">
																var so = new SWFObject("<%=rootPath%>/flex/Email_Ping_Pie.swf?id=<%=queryconf.getId()%>", "Email_Ping_Pie", "380", "220", "8", "#ffffff");
																so.write("flashcontent2");
															</script>
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
												<table id="content-footer" class="content-footer">
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
							 <td width="8" align="left">
								<td>
									<table id="detail-data" class="detail-data">
										<tr>
											<td class="detail-data-header">
												<%=emailDetailTitleTable%>
											</td>
										</tr>
										<tr>
											<td>
												<table class="detail-data-body">

													<tr>
														<td colspan=5 height="28" bgcolor="#ECECEC">


															开始日期
															<input type="text" name="startdate"
																value="<%=startdate%>" size="10">
															<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																<img id=imageCalendar1 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0> </a> 截止日期
															<input type="text" name="todate" value="<%=todate%>"
																size="10" />
															<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																<img id=imageCalendar2 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0> </a> 事件等级
															<select name="level1">
																<option value="99">
																	不限
																</option>
																<option value="0" <%=level0str%>>
																	提示信息
																</option>
																<option value="1" <%=level1str%>>
																	普通事件
																</option>
																<option value="2" <%=level2str%>>
																	严重事件
																</option>
																<option value="3" <%=level3str%>>
																	紧急事件
																</option>
															</select>

															处理状态
															<select name="status">
																<option value="99">
																	不限
																</option>
																<option value="0" <%=status0str%>>
																	未处理
																</option>
																<option value="1" <%=status1str%>>
																	正在处理
																</option>
																<option value="2" <%=status2str%>>
																	已处理
																</option>
															</select>
															<input type="button" name="submitss" value="查询"
																onclick="query()"><hr>
														</td>
													</tr>
													<tr align="right" bgcolor="#ECECEC">
														<td><table><tr>
														<td width="75%">&nbsp;</td>
														<td width="15" height=15 >&nbsp;&nbsp;</td>
														<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="接受处理" onclick='batchAccfiEvent();'/></td>
														<td width="15" height=15>&nbsp;&nbsp;</td>
														<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="填写报告" onclick='batchDoReport();'/></td>
														<td width="15" height=15>&nbsp;&nbsp;</td>
														<td  height=15>&nbsp;&nbsp;<input type="button" name="submitss" value="修改等级" onclick="batchEditAlarmLevel();">&nbsp;&nbsp;</td>
														<td width="15" height=15>&nbsp;&nbsp;</td>
														</tr></table></td>
													</tr>
													<tr bgcolor="#ECECEC">
														<td>
															<table cellSpacing="0" cellPadding="0" border=0>

																<tr>
																	<td class="detail-data-body-title">
																		<INPUT type="checkbox" name="checkall" onclick="javascript:chkall()">
																	</td>
																	<td width="10%" class="detail-data-body-title">
																		<strong>事件等级</strong>
																	</td>
																	<td width="40%" class="detail-data-body-title">
																		<strong>事件描述</strong>
																	</td>
																	<td class="detail-data-body-title">
																		<strong>最近告警时间</strong>
																	</td>
																	<td class="detail-data-body-title">
																		<strong>告警次数</strong>
																	</td>
																	<td class="detail-data-body-title">
																		<strong>查看状态</strong>
																	</td>
																	<td class="detail-data-body-title">
																		<strong></strong>
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
																		String my_status = String.valueOf(eventlist.getManagesign());
																		String s = my_status;
																		String act = "处理报告";
																		String bgcolor = "";
																		String levelstr = "";
																		if ("0".equals(level)) {
																			levelstr = "提示信息";
																			bgcolor = "bgcolor='blue'";
																		}
																		if ("1".equals(level)) {
																			levelstr = "普通事件";
																			bgcolor = "bgcolor='yellow'";
																		}
																		if ("2".equals(level)) {
																			levelstr = "严重事件";
																			bgcolor = "bgcolor='orange'";
																		}
																		if ("3".equals(level)) {
																			levelstr = "紧急事件";
																			bgcolor = "bgcolor='red'";
																		}
																		String bgcolorstr="";
																		if ("0".equals(my_status)) {
																			my_status = "未处理";
																			bgcolorstr="#9966FF";
																		}
																		if ("1".equals(my_status)) {
																			my_status = "处理中";
																			bgcolorstr="#3399CC";	
																		}
																		if ("2".equals(my_status)) {
																			my_status = "处理完成";
																			bgcolorstr="#33CC33";
																		}
																		String rptman = eventlist.getReportman();
																		String rtime1 = _sdf.format(cc);
																%>

																<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
																	<td class="detail-data-body-list"><INPUT type="checkbox" name="checkbox" value="<%=eventlist.getId()%>"><%=i+1%></td>
   	 															    <td class="detail-data-body-list" <%=bgcolor%>><%=levelstr%></td>
																	<td class="detail-data-body-list">
																		<%=content%></td>
																	<td class="detail-data-body-list">
																		<%=rtime1%></td>
																	<td class="detail-data-body-list">
																		<%=rptman%></td>
																	<td class="detail-data-body-list"  bgcolor=<%=bgcolorstr%>>
																		<%=my_status%></td>
																	<td class="detail-data-body-list" align="center">
																		<%
																			if ("0".equals(s)) {
																		%>
																		<input type="button" value="接受处理" class="button"
																			onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																		<!--<input type ="button" value="接受处理" class="button" onclick="accEvent('<%=eventid%>')">-->
																		<%
																			}
																				if ("1".equals(s)) {
																		%>
																		<input type="button" value="填写报告" class="button"
																			onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																		<!--<input type ="button" value="填写报告" class="button" onclick="fiReport('<%=eventid%>')">-->
																		<%
																			}
																				if ("2".equals(s)) {
																		%>
																		<input type="button" value="查看报告" class="button"
																			onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																		<!--<input type ="button" value="查看报告" class="button" onclick="viewReport('<%=eventid%>')">-->
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
					<td width=15% valign=top>
						<jsp:include page="/include/mailtoolbar.jsp">
							<jsp:param value="<%=queryconf.getId()%>" name="id" />
						</jsp:include>
					</td>
				</tr>

			</table>
		</form>
		<script>			
Ext.onReady(function()
{  


	    Ext.get("process").on("click",function(){
  
  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
  mainForm.action = "<%=rootPath%>/mail.do?action=sychronizeData&id=<%=queryconf.getId()%>&flag=<%=flag_1%>";
  mainForm.submit();
 });    
});
</script>
	</body>
</html>
