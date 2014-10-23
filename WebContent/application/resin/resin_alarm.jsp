<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.polling.node.Resin"%>
<%@page import="com.afunms.application.manage.ResinManager"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc" %>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%
	String runmodel = PollingEngine.getCollectwebflag(); 
  String rootPath = request.getContextPath(); 
  String tmp = request.getParameter("id");
  String menuTable = (String)request.getAttribute("menuTable");
  
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
   



  Hashtable data_ht=new Hashtable();
  ResinManager tm= new ResinManager();

   
  	Resin resin = (Resin)PollingEngine.getInstance().getResinByID(Integer.parseInt(tmp));  
	Hashtable hash_data = null;
	 if("0".equals(runmodel)){
	   		//采集与访问是集成模式
		 Hashtable resinvalues = ShareData.getResindata();
	     if(resinvalues != null && resinvalues.containsKey(tmp)){
	    	 data_ht = (Hashtable)resinvalues.get(tmp);
	     }
	 }else{
		 //采集与访问分离模式
		// TomcatInfoService tomcatInfoService = new TomcatInfoService();
	    // data_ht = tomcatInfoService.getTomcatDataHashtable(tmp);
	 }

String flag_1 = (String)request.getAttribute("flag");

%>


<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
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
      mainForm.action = "<%=rootPath%>/resin.do?action=detail&id="+id;
      mainForm.submit();
} 
function query()
  {  
  	 var id=<%=request.getAttribute("id")%>
     mainForm.action = "<%=rootPath%>/resin.do?action=alarm&id="+id+"&flag=<%=flag%>";
     mainForm.submit();
  }
</script>
<script type="text/javascript">
function setClass(){

	document.getElementById('resDetailTitle-4').className='detail-data-title';
	document.getElementById('resDetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('resDetailTitle-4').onmouseout="this.className='detail-data-title'";
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
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
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
							<td class="td-container-main-service-detail">
								<table id="container-main-service-detail" class="container-main-service-detail">
									<tr>
										<td> 
											<jsp:include page="/topology/includejsp/middleware_resin.jsp">
												<jsp:param name="tmp" value="<%=tmp%>"/>
												<jsp:param name="avgpingcon" value="0"/> 
												<jsp:param name="avgjvmcon" value="0"/> 
											</jsp:include>
										</td>
									</tr>
									<tr>
										<td>
											<table id="service-detail-content" class="service-detail-content">
												<tr>
													<td>
														<%=resDetailTitleTable%>
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
																				String status = String.valueOf(eventlist.getManagesign());
																				String s = status;
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
																			<td class="detail-data-body-list" bgcolor=<%=bgcolorstr%>><%=status%></td>
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