<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.application.manage.ApacheManager"%>
<%@page import="com.afunms.detail.service.apacheInfo.ApacheInfoService"%>
<%@page import="com.afunms.application.model.ApacheConfig"%>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="com.afunms.alarm.util.AlarmConstant"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc" %>

<%@page import="java.util.*"%>

<%@ page import="com.afunms.event.model.EventList"%>

<%
	String runmodel = PollingEngine.getCollectwebflag(); 
  String rootPath = request.getContextPath(); 
  String menuTable = (String)request.getAttribute("menuTable");
  String id =(String) request.getAttribute("temp");
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
	
	ApacheConfig vo=(ApacheConfig)request.getAttribute("vo");
	
    String ip="";
    
	Hashtable apache_ht = null;
	
	if(vo!=null){
		ip=vo.getIpaddress();
		id=vo.getId()+"";
	}
	if("0".equals(runmodel)){
	  	//采集与访问是集成模式
		apache_ht = (Hashtable)com.afunms.common.util.ShareData.getApachedata().get("apache"+":"+ip);
	}else{  
		//采集与访问是分离模式
		ApacheInfoService apacheInfoService = new ApacheInfoService();
		apache_ht = apacheInfoService.getApacheDataHashtable(vo.getId()+"");
	}
	if(apache_ht == null)apache_ht = new Hashtable();
	String status_str = (String)apache_ht.get("status");
	Integer status = 0;
	if(status_str!=null){
		status = Integer.valueOf(status_str);
	} 
	String connrate = request.getAttribute("connrate").toString();

	if(connrate!=null)
	connrate = connrate.replaceAll("%","");
	int percent1 = Double.valueOf(connrate).intValue();
	int percent2 = 100-percent1;
    status= SystemSnap.getNodeStatus(id+"",AlarmConstant.TYPE_MIDDLEWARE,"apache");

%>


<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>


<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script language="JavaScript" type="text/JavaScript">


function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
		
}

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

function show_graph(){
	 var id=<%=request.getAttribute("id")%>
      mainForm.action = "<%=rootPath%>/tomcat.do?action=tomcat_jvm&id="+id;
      mainForm.submit();
} 
function query()
  {  
  	 var id=<%=id%>
     mainForm.action = "<%=rootPath%>/apache.do?action=alarm&id="+id+"&flag=<%=flag%>";
     mainForm.submit();
  }
</script>
<script type="text/javascript">
function setClass(){
	document.getElementById('apacheDetailTitle-4').className='detail-data-title';
	document.getElementById('apacheDetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('apacheDetailTitle-4').onmouseout="this.className='detail-data-title'";
		}
</script>
<script type="text/javascript">
function event(){
	mainForm.action = "<%=rootPath%>/apache.do?action=event"+"&flag=<%=flag%>";
    mainForm.submit();
}

</script>


</head>
<body id="body" class="body" onload="setClass();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="id" value="<%=id %>">
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
											<td valign=top>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
														<td>
															<%=apacheDetailTitleTable%>
														</td>
													</tr>
												<tr>
													<td>
														<table class="detail-data-body">
															<tr>
																<td colspan=5 height="28" bgcolor="#ECECEC">
																	开始日期
																	<input type="text" name="startdate" value="<%=startdate%>" size="10">
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																		<img id=imageCalendar1 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0> </a> 截止日期
																	<input type="text" name="todate" value="<%=todate%>" size="10" />
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
																	<input type="button" name="submitss" value="查询" onclick="query()">
																	<hr />
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
																				&nbsp;&nbsp;<input type="button" name="" value="接受处理" onclick='batchAccfiEvent();' />
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																			<td height=15>
																				&nbsp;&nbsp;<input type="button" name="" value="填写报告" onclick='batchDoReport();' />
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																			<td height=15>
																				&nbsp;&nbsp;<input type="button" name="submitss" value="修改等级" onclick="batchEditAlarmLevel();">&nbsp;&nbsp;
																			</td>
																			<td width="15" height=15>
																				&nbsp;&nbsp;
																			</td>
																		</tr>
																	</table>
																</td>
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
																				<strong>登记人</strong>
																			</td>
																			<td class="detail-data-body-title">
																				<strong>状态</strong>
																			</td>
																			<td class="detail-data-body-title">
																				<strong>操作</strong>
																			</td>
																		</tr>
																		<%
																			int index = 0;
																			java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat( "MM-dd HH:mm");
																			List list = (List) request.getAttribute("list");
																			String lasttime = "";
																			for (int i = 0; i < list.size(); i++) {
																				index++;
																				EventList eventlist = (EventList) list.get(i);
																				Date cc = eventlist.getRecordtime().getTime();
																				Integer eventid = eventlist.getId();
																				String eventlocation = eventlist.getEventlocation();
																				String content = eventlist.getContent();
																				String level = String.valueOf(eventlist.getLevel1());
																				String status_level = String.valueOf(eventlist.getManagesign());
																				String s = status_level;
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
																				if ("0".equals(status_level)) {
																					status_level = "未处理";
																					bgcolorstr = "#9966FF";
																				}
																				if ("1".equals(status_level)) {
																					status_level = "处理中";
																					bgcolorstr = "#3399CC";
																				}
																				if ("2".equals(status_level)) {
																					status_level = "处理完成";
																					bgcolorstr = "#33CC33";
																				}
																				lasttime = eventlist.getLasttime();
																				if(lasttime.length()>5)lasttime=lasttime.substring(5);
																				String rptman = eventlist.getReportman();
																				String rtime1 = _sdf.format(cc);
																		%>

																		<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>

																			<td class="detail-data-body-list"><INPUT type="checkbox" name="checkbox" value="<%=eventlist.getId()%>"><%=i + 1%></td>
																			<td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
																			<td class="detail-data-body-list"><%=content%></td>
																			<td class="detail-data-body-list"><%=lasttime%></td>
																			<td class="detail-data-body-list"><%=rptman%></td>
																			<td class="detail-data-body-list" bgcolor=<%=bgcolorstr%>><%=status_level%></td>
																			<td class="detail-data-body-list" align="center">
																				<%
																					if ("0".equals(s)) {
																				%>
																				<input type="button" value="接受处理" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																				<!--<input type ="button" value="接受处理" class="button" onclick="accEvent('<%=eventid%>')">-->
																				<%
																					}
																						if ("1".equals(s)) {
																				%>
																				<input type="button" value="填写报告" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																				<!--<input type ="button" value="填写报告" class="button" onclick="fiReport('<%=eventid%>')">-->
																				<%
																					}
																						if ("2".equals(s)) {
																				%>
																				<input type="button" value="查看报告" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
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
					<jsp:param value="<%=ip%>" name="ipaddress" />
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