<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.awt.Label"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.detail.service.IISInfo.IISInfoService"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*" %>
<%@page import="com.afunms.polling.om.*" %>
<%@page import="com.afunms.monitor.item.base.MonitorResult"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.application.util.*"%>
<%@page import="org.jdom.Element"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.manage.IISManager"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.event.model.EventList"%>

<%
	String runmodel = PollingEngine.getCollectwebflag(); 
   String flag_1 = (String) request.getAttribute("flag");
   String rootPath = request.getContextPath(); 
   String menuTable = (String)request.getAttribute("menuTable");
   String timeFormat = "yyyy-MM-dd";
   String startdate = (String)request.getAttribute("startdate");
   String todate = (String)request.getAttribute("todate");
   String _flag = (String)request.getAttribute("flag");
   IISConfig iisconf =   (IISConfig)request.getAttribute("iisconf");
   int alarmLevel = (Integer)request.getAttribute("alarmLevel");
  String tmp = request.getParameter("id");
  
  	int level1 = Integer.parseInt(request.getAttribute("level1")+"");
	int _status = Integer.parseInt(request.getAttribute("status")+"");
	
	String level0str="";
	String level1str="";
	String level2str="";
	String level3str="";
	if(level1 == 0){
		level0str = "selected";
	}else if(level1 == 1){
		level1str = "selected";
	}else if(level1 == 2){
		level2str = "selected";
	}else if(level1 == 3){
		level3str = "selected";	
	}
	
	String status0str="";
	String status1str="";
	String status2str="";
	if(_status == 0){
		status0str = "selected";
	}else if(_status == 1){
		status1str = "selected";
	}else if(_status == 2){
		status2str = "selected";	
	}
  
  double jvm_memoryuiltillize=0;
  double iisping=0;

  String lasttime ;
  String nexttime;
	String totalBytesSentHighWord= "";
	String totalBytesSentLowWord= "";
	String totalBytesReceivedHighWord= "";
	String totalBytesReceivedLowWord = "";
	
	String totalFilesSent = "";
	String totalFilesReceived = "";
	String currentAnonymousUsers = "";
	String totalAnonymousUsers = "";
	
	String maxAnonymousUsers = "";
	String currentConnections = "";
	String maxConnections = "";
	String connectionAttempts = "";
	
	String logonAttempts = "";
	String totalGets = "";
	String totalPosts = "";
	String totalNotFoundErrors = "";

  List data_list=new ArrayList();
  
  Hashtable imgurlhash=new Hashtable();
  
  IISManager tm= new IISManager();

   
  IIS iis = (IIS)PollingEngine.getInstance().getIisByID(Integer.parseInt(tmp)); 
  String allipstr = SysUtil.doip(iis.getIpAddress());
  String tablename="iisping"+allipstr;
  System.out.println("tablename======="+tablename);
  Hashtable iisvalues = null; 
  String pingvalue ="0";
  	Hashtable hashPing=new Hashtable();
   if("0".equals(runmodel)){
   		//采集与访问是集成模式
  		iisvalues = ShareData.getIisdata();
 		if(iisvalues != null && iisvalues.size()>0){
	  		data_list = (List)iisvalues.get(iis.getIpAddress());
	  	}
	  	
	  	Hashtable allissdata = (Hashtable)ShareData.getIISPingdata();
		if(allissdata != null && allissdata.size()>0){
			Vector vec = (Vector)allissdata.get(iis.getIpAddress());
			//System.out.println("vector"+vec.size());
			if(vec != null && vec.size()>0){
				Pingcollectdata iispingdata = (Pingcollectdata)vec.get(0);
				iis = (IIS)vec.get(1);
				if(iispingdata != null){
					pingvalue = iispingdata.getThevalue();
				}
			}
		}	
  }else{
  		//采集与访问是分离模式
  		IISInfoService iisInfoService = new IISInfoService();
  		data_list = iisInfoService.getIISData(iis.getId()+"");
  		pingvalue = iisInfoService.getIISPing(iis.getIpAddress());
  }
	iisping=(double)tm.iisping(iis.getId());
	
	
	 Hashtable pollingtime_ht=tm.getCollecttime(iis.getIpAddress());
	 if(pollingtime_ht!=null){
	 lasttime=(String)pollingtime_ht.get("lasttime");
	 nexttime=(String)pollingtime_ht.get("nexttime");
	 
	 }else{
	 	lasttime=null;
	 	nexttime=null;	 
	 }
	 
	if(data_list!=null && data_list.size()>0){
		
		IISVo iisvo = (IISVo)data_list.get(0);
		totalBytesSentHighWord= iisvo.getTotalBytesSentHighWord();
		if(totalBytesSentHighWord == null)totalBytesSentHighWord="";
		totalBytesSentLowWord= iisvo.getTotalBytesSentLowWord();
		if(totalBytesSentLowWord == null)totalBytesSentLowWord="";
		totalBytesReceivedHighWord= iisvo.getTotalBytesReceivedHighWord();
		if(totalBytesReceivedHighWord == null)totalBytesReceivedHighWord="";
		totalBytesReceivedLowWord = iisvo.getTotalBytesReceivedLowWord();
		if(totalBytesReceivedLowWord == null)totalBytesReceivedLowWord="";
	
		totalFilesSent = iisvo.getTotalFilesSent();
		if(totalFilesSent == null)totalFilesSent="";
		totalFilesReceived = iisvo.getTotalFilesReceived();
		if(totalFilesReceived == null)totalFilesReceived = "";
		currentAnonymousUsers = iisvo.getCurrentAnonymousUsers();
		if(currentAnonymousUsers == null)currentAnonymousUsers = "";
		totalAnonymousUsers = iisvo.getTotalAnonymousUsers();
		if(totalAnonymousUsers == null)totalAnonymousUsers = "";
		
	
		maxAnonymousUsers = iisvo.getMaxAnonymousUsers();
		if(maxAnonymousUsers == null)maxAnonymousUsers = "";
		currentConnections = iisvo.getCurrentConnections();
		if(currentConnections == null)currentConnections = "";
		maxConnections = iisvo.getMaxConnections();
		if(maxConnections == null)maxConnections = "";
		connectionAttempts = iisvo.getConnectionAttempts();
		if(connectionAttempts == null)connectionAttempts = "";
	
		logonAttempts = iisvo.getLogonAttempts();
		if(logonAttempts == null)logonAttempts = "";
		totalGets = iisvo.getTotalGets();
		if(totalGets == null)totalGets = "";
		totalPosts = iisvo.getTotalPosts();
		if(totalPosts == null)totalPosts = "";
		totalNotFoundErrors = iisvo.getTotalNotFoundErrors();
		if(totalNotFoundErrors == null)totalNotFoundErrors = "";
	
	}	

	String dbPage = "detail";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>


<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 

<script language="javascript">	

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
	
  function query()
  {  
     mainForm.action = "<%=rootPath%>/iis.do?action=alarm";
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
	document.getElementById('iisDetailTitle-1').className='detail-data-title';
	document.getElementById('iisDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('iisDetailTitle-1').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=iis.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}

</script>
</head>
<body id="body" class="body" onload="initmenu();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="orderflag">
<input type="hidden" id="flag" name="flag" value="<%=flag_1%>">
<input type=hidden name="id" value=<%=tmp%>> 
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
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
								<table id="container-main-application-detail" class="container-main-application-detail">

									<tr>
										<td>
											<table id="application-detail-content" class="application-detail-content">
												<tr>
													<td>
														 <jsp:include page="/topology/includejsp/detail_content_top.jsp">
														 	<jsp:param name="contentTitle" value="IIS详细信息"/> 
														 </jsp:include>
													</td>
												</tr>												
												<tr>
													<td>
					<table>
                   <tr bgcolor="#F1F1F1">
                      <td width="10%" height="26" valign=middle align="right" nowrap  class=txtGlobal>&nbsp;名称:&nbsp;</td>
                      <td width="15%">
                      <%if(iis.getAlias()!=null){ %>
                      <%=iis.getAlias()%>
                      <%} %>
                      </td>
                      <td width="10%" class=txtGlobal align="right" valign=middle nowrap>&nbsp;状态:&nbsp;</td>
                      <td width=15% valign=middle ><img src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmLevel)%>">&nbsp;<%=NodeHelper.getStatusDescr(alarmLevel)%></td>
                      <td width="10%" height="26" align=right valign=center nowrap   class=txtGlobal>&nbsp;IP地址:&nbsp;</td>
                      <td width="15%">
                      <%if(iis.getIpAddress()!=null)
                      { %>
                      <%=iis.getIpAddress()%>
                      <%} %>
                      </td>
                      <td width="10%" height="26" align=right valign=center nowrap class=txtGlobal>&nbsp;数据采集时间:&nbsp;</td>
                      <td width="15%">
                      <%if(lasttime!=null)
                      { %>
                      <%=lasttime%>
                      <%} %>
                      </td>
                    </tr>
                  </table>
												</td>
											</tr>
				                    </table>
				                    </td>
									</tr>
									<tr>
										<td colspan=8>
											<table id="application-detail-data" class="application-detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=iisDetailTitleTable%>
											    	</td>
											   </tr>
		                                       <tr>
											    	<td>

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
																			java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat( "MM-dd HH:mm");
																			System.out.println("======================1");
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

																			<td class="detail-data-body-list"><INPUT type="checkbox" name="checkbox" value="<%=eventlist.getId()%>"><%=i + 1%></td>
																			<td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
																			<td class="detail-data-body-list"><%=content%></td>
																			<td class="detail-data-body-list"><%=rtime1%></td>
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
															<jsp:include page="/include/iistoolbar.jsp">
																<jsp:param value="<%=iis.getId() %>" name="id" />
															    <jsp:param value="<%=iis.getIpAddress() %>" name="ipaddress" />
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
  mainForm.action = "<%=rootPath%>/iis.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=tmp %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>