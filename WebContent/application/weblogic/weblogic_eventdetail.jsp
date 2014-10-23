<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.*" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.polling.api.I_Portconfig"%>
<%@page import="com.afunms.polling.om.Portconfig"%>
<%@page import="com.afunms.polling.om.*"%>
<%@page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.application.manage.WeblogicManager"%>
<%@page import="com.afunms.application.dao.WeblogicConfigDao"%>
<%@page import="com.afunms.application.model.WeblogicConfig"%>
<%@page import="com.afunms.application.weblogicmonitor.*"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.temp.dao.WeblogicDao" %>

<%
	String runmodel = PollingEngine.getCollectwebflag(); 
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");
	Hashtable hash = (Hashtable)request.getAttribute("hash");
	String id= (String)request.getAttribute("id");
	String chart1=null;
	double weblogicping=0;
	Hashtable pollingtime_ht=new Hashtable();
	String lasttime;
	String nexttime;
    
	WeblogicManager wm=new WeblogicManager();
    WeblogicConfig weblogicconf = (WeblogicConfig)request.getAttribute("weblogicconf");
	
	pollingtime_ht=wm.getCollecttime(weblogicconf.getIpAddress());
	
	if(pollingtime_ht!=null){
		lasttime=(String)pollingtime_ht.get("lasttime");
		nexttime=(String)pollingtime_ht.get("nexttime");
	}else{
		lasttime=null;
		nexttime=null;	 
	}
	weblogicping=(double)wm.weblogicping(weblogicconf.getId());
	int percent1 = Double.valueOf(weblogicping).intValue();
	int percent2 = 100-percent1;

	DefaultPieDataset dpd = new DefaultPieDataset();
	dpd.setValue("可用时间",weblogicping);
	dpd.setValue("不可用时间",100 - weblogicping);
	chart1 = ChartCreator.createPieChart(dpd,"",130,130); 

	WeblogicNormal normalvalue=null;
	List normaldatalist =new ArrayList();
	List queuedatalist =new ArrayList();
		
	if("0".equals(runmodel)){
		//采集与访问是集成模式
		hash = (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
	}else{
 		List labelList = new ArrayList();
		labelList.add("normalValue");
		//labelList.add("jdbcValue");
		//labelList.add("jdbcValue");
		//labelList.add("webappValue");
		//labelList.add("heapValue");
		labelList.add("serverValue");
		WeblogicDao weblogicDao = new WeblogicDao();
		hash = weblogicDao.getWeblogicData(labelList,id);
	}
	if(hash!=null){
		 normaldatalist =(List) hash.get("normalValue");
		 queuedatalist =(List) hash.get("queueValue");
		List jdbcdatalist = (List)hash.get("jdbcValue");
		List webappdatalist = (List)hash.get("webappValue");
		List heapdatalist =(List) hash.get("heapValue");
		List serverdatalist = (List)hash.get("serverValue");
	if(normaldatalist!=null&&normaldatalist.size()>0){
		 normalvalue = (WeblogicNormal)normaldatalist.get(0);
		}else{
		 normalvalue = new WeblogicNormal();
		}
	}
	String startdate = (String)request.getAttribute("startdate");
	String todate = (String)request.getAttribute("todate");
	
	int level1 = Integer.parseInt(request.getAttribute("level1")+"");
	int _status = Integer.parseInt(request.getAttribute("status")+"");
	
	String level1str="";
	String level2str="";
	String level3str="";
	if(level1 == 1){
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
	String dbPage = "eventdetail";
	String flag_1 = (String)request.getAttribute("flag");
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
     //if(mainForm.key.value=="")
     //{
    // 	alert("请输入查询条件");
     //	return false;
    // }
     var id=<%=request.getAttribute("id")%>
     mainForm.action = "<%=rootPath%>/weblogic.do?action=eventdetail&id="+id+"&flag=<%=flag%>";
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
	document.getElementById('weblogicDetailTitle-7').className='detail-data-title';
	document.getElementById('weblogicDetailTitle-7').onmouseover="this.className='detail-data-title'";
	document.getElementById('weblogicDetailTitle-7').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=weblogicconf.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}

</script>

</head>
<body id="body" class="body" onload="initmenu();">

<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="orderflag">
<input type=hidden name="id">
<input type=hidden name="flag" value="<%=flag%>">

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
											<jsp:include page="/topology/includejsp/middleware_weblogic.jsp">
												<jsp:param name="IpAddress" value="<%= weblogicconf.getIpAddress()%>"/> 
												<jsp:param name="id" value="<%= weblogicconf.getId()%>"/>
												<jsp:param name="name" value="<%= weblogicconf.getAlias()%>"/>
												<jsp:param name="Portnum" value="<%= weblogicconf.getPortnum()%>"/> 
												<jsp:param name="DomainName" value="<%= normalvalue.getDomainName()%>"/> 
												<jsp:param name="DomainActive" value="<%= normalvalue.getDomainActive()%>"/> 
												<jsp:param name="DomainAdministrationPort" value="<%= normalvalue.getDomainAdministrationPort()%>"/> 
												<jsp:param name="DomainConfigurationVersion" value="<%=normalvalue.getDomainConfigurationVersion()%>"/> 
												<jsp:param name="lasttime" value="<%=lasttime%>"/>
												<jsp:param name="percent2" value="<%=nexttime%>"/>
												<jsp:param name="percent1" value="<%=percent1%>"/>
												<jsp:param name="percent2" value="<%=percent2%>"/>
												<jsp:param name="Mon_flag" value="<%=weblogicconf.getMon_flag()%>"/>
											</jsp:include>
										</td>
									</tr>
									<tr>
										<td>
											<table id="application-detail-data" class="application-detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=weblogicDetailTitleTable%>
											    	</td>
											   </tr>
		                                       <tr>
											    	<td>
											    		<table class="application-detail-data-body">
														<tr bgcolor="#ECECEC" height="28">
														<td>
												      		开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
		事件等级
		<select name="level1">
		<option value="99">不限</option>
		<option value="1" <%=level1str%>>普通事件</option>
		<option value="2" <%=level2str%>>严重事件</option>
		<option value="3" <%=level3str%>>紧急事件</option>
		</select>
					
		处理状态
		<select name="status">
		<option value="99">不限</option>
		<option value="0" <%=status0str%>>未处理</option>
		<option value="1" <%=status1str%>>正在处理</option>
		<option value="2" <%=status2str%>>已处理</option>
		</select>	
	<input type="button" id="process1" name="process1" value="查 询" onclick="doQuery()"><hr>
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
      <table width="100%" border="0" cellpadding="0" cellspacing="1">
	
   <tr>
    	<td class="application-detail-data-body-title"><INPUT type="checkbox" name="checkall" onclick="javascript:chkall()"></td>
        <td class="application-detail-data-body-title" width="10%"><strong>事件等级</strong></td>
    	<td class="application-detail-data-body-title" width="40%"><strong>事件描述</strong></td>
		<td class="application-detail-data-body-title"><strong>开始时间</strong></td>
    	<td class="application-detail-data-body-title"><strong>最新时间</strong></td>
    	<td class="application-detail-data-body-title"><strong>处理状态</strong></td>
    	<td class="application-detail-data-body-title"><strong></strong></td>
   </tr>
<%
	int index = 0;
  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
  	List list = (List)request.getAttribute("list");
  	
  	for(int i=0;i<list.size();i++){
 	index++;
  	EventList eventlist = (EventList)list.get(i);
  	Date cc = eventlist.getRecordtime().getTime();
  	//Date cc1 = eventlist.getLasttime().getTime();
  	Integer eventid = eventlist.getId();
  	String eventlocation = eventlist.getEventlocation();
  	String content = eventlist.getContent();
  	String level = String.valueOf(eventlist.getLevel1());
  	String status = String.valueOf(eventlist.getManagesign());
  	String showlevel = null;
  	String s = status;
  	String bgcolor = "";
  	String act="处理报告";
  	if("0".equals(level)){
  		level="提示信息";
  		bgcolor = "bgcolor='blue'";
  	}
	if("1".equals(level)){
  		level="普通告警";
  		bgcolor = "bgcolor='yellow'";
  	}
  	if("2".equals(level)){
  		level="严重告警";
  		bgcolor = "bgcolor='orange'";
  	}
  	if("3".equals(level)){
  		level="紧急告警";
  		bgcolor = "bgcolor='red'";
  	}
   	String bgcolorstr="";
  	if("0".equals(status)){
  		status = "未处理";
  		bgcolorstr="#9966FF";
  	}
  	if("1".equals(status)){
  		status = "处理中";  
  		bgcolorstr="#3399CC";	
  	}
  	if("2".equals(status)){
  	  	status = "处理完成";
  	  	bgcolorstr="#33CC33";
  	}
  	String rptman = eventlist.getReportman();
  	String rtime1 = _sdf.format(cc);
  	String rtime2 =eventlist.getLasttime().substring(5);
%>

 <tr  bgcolor="#FFFFFF" <%=onmouseoverstyle%> height="28">

   	  <td class="detail-data-body-list"><INPUT type="checkbox" name="checkbox" value="<%=eventlist.getId()%>"><%=i+1%></td>
   	  <td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
      <td class="application-detail-data-body-list" align="center">
      <%=content%></td>
       <td class="application-detail-data-body-list" align="center">
      <%=rtime1%></td>
       <td class="application-detail-data-body-list" align="center">
      <%=rtime2%></td>
       <td class="application-detail-data-body-list" align="center" bgcolor=<%=bgcolorstr%>>
      <%=status%></td>
       <td class="application-detail-data-body-list" align="center">
       <%
       		if("0".equals(s)){
       		%>
       			<input type ="button" value="接受处理" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 500, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
       		<%
       		}
       		if("1".equals(s)){
       		%>
       			<input type ="button" value="填写报告" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
       		<%
       		}
       		if("2".equals(s)){
       		%>
       			<input type ="button" value="查看报告" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
       		<%
       		}
       %>
       </td>
 </tr>
 <%}

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
				<td width=15% valign=top>
															<jsp:include page="/include/weblogictoolbar.jsp">
																<jsp:param value="<%=weblogicconf.getId() %>" name="id" />
															    <jsp:param name="ip" value="<%= weblogicconf.getIpAddress()%>"/> 
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
  mainForm.action = "<%=rootPath%>/weblogic.do?action=sychronizeData&dbPage=<%=dbPage%>&id=<%=id %>&flag=<%=flag_1%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>