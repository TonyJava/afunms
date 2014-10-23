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

<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@ page import="com.afunms.event.model.EventList"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.common.util.CreatePiePicture"%>


<%
  String rootPath = request.getContextPath();;
  DBVo vo  = (DBVo)request.getAttribute("db");	
  String id = (String)request.getAttribute("id");
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "mysqlevent";
  String dbType = "mysql";
String _flag = (String)request.getAttribute("flag");
Hashtable max = (Hashtable) request.getAttribute("max");
Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
	double avgpingcon = (Double)request.getAttribute("avgpingcon");	
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100-percent1;
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");
String versionvalue=(String)request.getAttribute("versionvalue");
String vercommaval=(String)request.getAttribute("vercommaval");
String vercomosval=(String)request.getAttribute("vercomosval");
String verwaittimeval=(String)request.getAttribute("verwaittimeval");
     
String basePath=(String)request.getAttribute("basePath");
String dataPath=(String)request.getAttribute("dataPath");
String logerrorPath=(String)request.getAttribute("logerrorPath");
String version=(String)request.getAttribute("version");
String hostOS=(String)request.getAttribute("hostOS");
   
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

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




	//String ip = host.getIpAddress();
	String picip = CommonUtil.doip(myip);
	
%>


<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
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

  function doQuery()
  {  

     mainForm.action = "<%=rootPath%>/mysql.do?action=mysqlevent&id=<%=vo.getId()%>";
     mainForm.submit();
  }
  function opennewwin()
  {  
   window.open("<%=rootPath%>/monitor.do?action=hosteventlist&nodetype=db&id=<%=vo.getId()%>");
    
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
	document.getElementById('mysqlDetailTitle-6').className='detail-data-title';
	document.getElementById('mysqlDetailTitle-6').onmouseover="this.className='detail-data-title'";
	document.getElementById('mysqlDetailTitle-6').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>

<script>
function gzmajax(){
$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/serverAjaxManager.ajax?action=ajaxUpdate_availability&tmp=<%=id%>&nowtime="+(new Date()),
			success:function(data){
				 $("#pingavg").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>pingavg.png?nowtime="+(new Date()), alt: "平均连通率" });
				$("#cpu").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>cpu.png?nowtime="+(new Date()), alt: "当前CPU利用率" }); 
			}
		});
}
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
		<input type=hidden name="id" id="id" value="<%=vo.getId()%>">
		<input type=hidden name="flag" id="flag" value="<%=_flag%>">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
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
								<table class="container-main-application-detail">
									<tr>
										<td> 
											<jsp:include page="/topology/includejsp/db_mysql.jsp">
												<jsp:param name="dbName" value="<%=vo.getDbName() %>"/>
												<jsp:param name="alias" value="<%=vo.getAlias() %>"/>
												<jsp:param name="ipAddress" value="<%=vo.getIpAddress() %>"/>
												<jsp:param name="port" value="<%=vo.getPort() %>"/>
												<jsp:param name="dbtye" value="<%=dbtye %>"/>
												<jsp:param name="managed" value="<%=managed %>"/>
												<jsp:param name="runstr" value="<%=runstr %>"/>
												<jsp:param name="version" value="<%=version %>"/>
												<jsp:param name="hostOS" value="<%=hostOS %>"/>
												<jsp:param name="basePath" value="<%=basePath %>"/>
												<jsp:param name="dataPath" value="<%=dataPath %>"/>
												<jsp:param name="logerrorPath" value="<%=logerrorPath %>"/>
												<jsp:param name="picip" value="<%=picip %>"/> 
												<jsp:param name="pingavg" value="<%=avgpingcon %>"/>  
											</jsp:include>
										</td>
									</tr>
									<tr>
										<td>
											<table id="application-detail-data"
												class="application-detail-data">
												<tr>
													<td class="detail-data-header">
														<%=mysqlDetailTitleTable%>
													</td>
												</tr>
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
																	<input type="button" id="process1" name="process1" value="查 询" onclick="doQuery()">
																	<input type="button" name="pop" value="告警新窗口" onclick="opennewwin()"><hr/>
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
																				<strong>登记时间</strong>
																			</td>
																			<td class="application-detail-data-body-title">
																				<strong>最后告警时间</strong>
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
																				String lastTime=eventlist.getLasttime();
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
																				//String rptman = eventlist.getReportman();
																				String rtime1 = _sdf.format(cc);
																				String rtime2 = lastTime.substring(5,16);
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
																				<%=rtime2%></td>
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
				<td  width=15% valign="top">
					<jsp:include page="/include/dbtoolbar.jsp">
						<jsp:param value="<%=myip%>" name="myip" />
						<jsp:param value="<%=myport%>" name="myport" />
						<jsp:param value="<%=myUser%>" name="myUser" />
						<jsp:param value="<%=myPassword%>" name="myPassword" />
						<jsp:param value="<%=mysid%>" name="sid" />
						<jsp:param value="<%=id%>" name="id" />
						<jsp:param value="<%=dbPage%>" name="dbPage" />
						<jsp:param value="<%=dbType%>" name="dbType" />
						<jsp:param value="mysql" name="subtype" />
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
  mainForm.action = "<%=rootPath%>/mysql.do?action=sychronizeData&dbPage=<%=dbPage%>&id=<%=id%>&myip=<%=myip%>&myport=<%=myport%>&myUser=<%=myUser%>&myPassword=<%=myPassword%>&sid=<%=mysid%>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>