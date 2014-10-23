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
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());	
  String dbPage = "oracleevent";
  String id = (String)request.getAttribute("id");
  String mysid = (String)request.getAttribute("sid");
  
	double avgpingcon = (Double)request.getAttribute("avgpingcon");
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100-percent1;
	String _flag = (String)request.getAttribute("flag");
		
	String lstrnStatu = (String)request.getAttribute("lstrnStatu");
	if("READ WRITE".equalsIgnoreCase(lstrnStatu)){lstrnStatu="已启动";}else{lstrnStatu="未启动";}
	Hashtable isArchive_h = (Hashtable)request.getAttribute("isArchive_h"); 
	String created = "";
	if(isArchive_h!=null){created=(String)isArchive_h.get("CREATED");}
	Hashtable memPerfValue = (Hashtable)request.getAttribute("memPerfValue"); 
	if(memPerfValue == null)memPerfValue = new Hashtable();
	String buffercache = "0";

String[] sysItem3={"HOST_NAME","DBNAME","VERSION","INSTANCE_NAME","STATUS","STARTUP_TIME","ARCHIVER"};
String[] sysItemch3={"主机名称","DB 名称","DB 版本","例程名","例程状态","例程开始时间","归档日志模式"};

//Hashtable hash = (Hashtable)request.getAttribute("hash");
//Hashtable hash1 = (Hashtable)request.getAttribute("hash1");
Hashtable max = (Hashtable) request.getAttribute("max");
//Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");
Vector tableinfo_v = (Vector)request.getAttribute("tableinfo_v");
  
  Hashtable orasys = (Hashtable)request.getAttribute("sysvalue");
  Hashtable cursors = (Hashtable)request.getAttribute("cursors");
if(cursors == null)cursors = new Hashtable();
  
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
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>

<%
 int intbuffercache = 0;
 String opencurstr = "0";
 String curconnectstr = "0";
 String dictionarycache = "0";
 String librarycache = "0";
 String pctmemorysorts = "";
 String pctbufgets = "0";
 String unpctmemorysorts = "0";
 String undictionarycache = "0";
 String unlibrarycache = "0";
 String unbuffercache = "0";
 String unpctbufgets = "0";
	if(memPerfValue!=null){
		if(memPerfValue.containsKey("buffercache") &&  memPerfValue.get("buffercache") != null)
			buffercache = (String)memPerfValue.get("buffercache");
																	                  		
		try{
			intbuffercache = Math.round(Float.parseFloat(buffercache));
		}catch(Exception e){
			e.printStackTrace();
		}
																
		if(memPerfValue.containsKey("dictionarycache") &&  memPerfValue.get("dictionarycache") != null)
			dictionarycache = (String)memPerfValue.get("dictionarycache");													
		if(memPerfValue.containsKey("librarycache") &&  memPerfValue.get("librarycache") != null)
			librarycache = (String)memPerfValue.get("librarycache");	
		if(memPerfValue.containsKey("pctmemorysorts") &&  memPerfValue.get("pctmemorysorts") != null)
			pctmemorysorts = (String)memPerfValue.get("pctmemorysorts");
		if(memPerfValue.containsKey("pctbufgets") &&  memPerfValue.get("pctbufgets") != null)
			pctbufgets = (String)memPerfValue.get("pctbufgets");
		if(cursors.containsKey("opencur") &&  cursors.get("opencur") != null)
			opencurstr = (String)cursors.get("opencur");
		if (cursors.containsKey("curconnect")&& cursors.get("curconnect") != null){
			curconnectstr = (String) cursors.get("curconnect");
		}			
		if(pctmemorysorts.equals(""))
			pctmemorysorts = "0";
		unpctmemorysorts = String.valueOf(100 - Math.round(Float.parseFloat(pctmemorysorts)));
		if(dictionarycache.equals(""))
			dictionarycache = "0";
		undictionarycache = String.valueOf(100 - Integer.parseInt(dictionarycache));
		if(librarycache.equals(""))
			librarycache = "0";
		unlibrarycache = String.valueOf(100 - Integer.parseInt(librarycache));
		if(buffercache.equals(""))
			buffercache = "0";
		unbuffercache = String.valueOf(100 - Integer.parseInt(buffercache));
		if(pctbufgets.equals(""))
			pctbufgets = "0";
		unpctbufgets = String.valueOf(100 - Double.valueOf(pctbufgets));
	}
	
	
	
	
	
	String picip = CommonUtil.doip(myip);
	
    	//生成当天平均连通率图形
	CreatePiePicture _cpp = new CreatePiePicture();
	_cpp.createAvgPingPic(picip,avgpingcon); 
	
	//缓存利用率
	CreateMetersPic cmp = new CreateMetersPic();
	cmp.createpubliccpuPic(picip,percent2,"缓存命中率");
	
	StringBuffer dataStr = new StringBuffer();
		dataStr.append("连通;").append(Math.round(avgpingcon)).append(
				";false;7CFC00\\n");
		dataStr.append("未连通;").append(100 - Math.round(avgpingcon))
				.append(";false;FF0000\\n");
		String avgdata = dataStr.toString();
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script>
function createQueryString(){
		var user_name = encodeURI($("#user_name").val());
		var passwd = encodeURI($("#passwd").val());
		var queryString = {userName:user_name,passwd:passwd};
		return queryString;
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
				$("#public").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>public.png?nowtime="+(new Date()), alt: "缓存利用率" }); 
			}
		});
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
	
	    function oracledelete()
  {  
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
     mainForm.action = "<%=rootPath%>/oracle.do?action=oracledelete&id=<%=vo.getId()%>&sid=<%=mysid%>";
     mainForm.submit();
  } 
	
$(document).ready(function(){
	//$("#testbtn").bind("click",function(){
	//	gzmajax();
	//});
setInterval(gzmajax,60000);
});
</script>


		<script language="javascript">	
  function doQuery()
  {  
     mainForm.action = "<%=rootPath%>/oracle.do?action=oracleevent&id=<%=vo.getId()%>&sid=<%=mysid%>";//HONGLI
     mainForm.submit();
  }
  function opennewwin()
  {  
   window.open("<%=rootPath%>/monitor.do?action=hosteventlist&nodetype=db&id=<%=vo.getId()%>");
    
  }
function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/oracle.do?action=accit";	
	mainForm.submit();
}
function fiReport(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/oracle.do?action=fireport";	
	mainForm.submit();	;
}
function viewReport(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/oracle.do?action=viewreport";	
	mainForm.submit();
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
	//go();
	setClass();
}

function setClass(){
	document.getElementById('oraDetailTitle-9').className='detail-data-title';
	document.getElementById('oraDetailTitle-9').onmouseover="this.className='detail-data-title'";
	document.getElementById('oraDetailTitle-9').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		document.getElementById("sid").value="<%=request.getAttribute("sid")%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
		<script type="text/javascript">
        var xmlHttp;
        var bar_color = '#00FA9A';      /*进度条颜色*/
        var span_id = "block";
        var clear = "&nbsp;&nbsp;&nbsp;"

        function createXMLHttpRequest() {
            if (window.ActiveXObject) {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            } 
            else if (window.XMLHttpRequest) {
                xmlHttp = new XMLHttpRequest();                
            }
        }

        function go() {     /*请求ProgresBarJsp.jsp*/
            createXMLHttpRequest();
            checkDiv();
            var url = "<%=rootPath%>/application/db/ProgressBarJsp.jsp?task=create";
            var button = document.getElementById("go");
            button.disabled = true;
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = goCallback;     //请求响应后，调用的goCallback函数
            xmlHttp.send(null);
        }

        function goCallback() {     
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    setTimeout("pollServer()", 500);
                }
            }
        }
        
        function pollServer() {
            createXMLHttpRequest();
            var url = "<%=rootPath%>/application/db/ProgressBarJsp.jsp?task=poll";
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = pollCallback;
            xmlHttp.send(null);
        }
        
        function pollCallback() {
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    var percent_complete = xmlHttp.responseXML.getElementsByTagName("percent")[0].firstChild.data;                 
                    var index = processResult(percent_complete);                  
                    for (var i = 1; i <= index; i++) {
                        var elem = document.getElementById("block" + i);
                        if(i != 5){                        
                        elem.innerHTML = clear;
                        }
                        elem.style.backgroundColor = bar_color;
                        
                        //var next_cell = i + 1;
                        //if (next_cell > index && next_cell <= 9) {
                        //    document.getElementById("block" + next_cell).innerHTML = percent_complete + "%";
                        //}
                    }                    
                    if (index < 9) {
                        setTimeout("pollServer()", 500);   //每隔俩秒执行一次pollServer（）函数，只执行一次
                    } else {
                        setTimeout("go()", 500);            //隔1秒重新请求一次go函数，达到循环显示的目的
                        //document.getElementById("complete").innerHTML = "Complete!";
                        //document.getElementById("go").disabled = false;
                    }
                }
            }
        }
        
        function processResult(percent_complete) {
            var ind;
            if (percent_complete.length == 2) {
                ind = percent_complete.substring(0, 1);   /*取俩位数的第一位数*/
            } else {
                ind = 9;
            }
            return ind;
        }

        function checkDiv() {
            var progress_bar = document.getElementById("progressBar");
            //var rightbar = document.getElementById("rightBar");           
            if (progress_bar.style.visibility == "visible") {
                clearBar();
                document.getElementById("complete").innerHTML = "";
            } else {
                progress_bar.style.visibility = "visible";
                //rightbar.style.visibility = "visible";               
                //document.getElementById("rightb").style.backgroundColor = "#ECECEC";              
            }
        }
        
        function clearBar() {
            for (var i = 1; i < 10; i++) {
                var elem = document.getElementById("block" + i);
                elem.innerHTML = clear;
                elem.style.backgroundColor = "#ececec";                
                if(i == 5)
                {
                   elem.innerHTML = "<%=pctbufgets%>%";
                }
            }
        }
    </script>


	</head>
	<body id="body" class="body" onload="initmenu();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="orderflag">
			<input type=hidden name="id">
			<input type=hidden name="flag" id="flag" value="<%=_flag%>">
			<input type="hidden" name="sid" id="sid" />
			<table id="body-container" class="body-container">
				<tr>
					<!-- 左右结构421-->
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
									<table>
										<!-- 上下结构 -->
										<tr>
											<td>
												<!-- !!!!!! -->
												<table>
													<tr>
														<td width=85%>
															<table id="container-main-application-detail"
																class="container-main-application-detail">
																<tr> 
																	<td>
																		<jsp:include page="/topology/includejsp/db_oracle.jsp">
																			<jsp:param name="Id" value="<%=vo.getId()%>" />
																			<jsp:param name="IpAddress" value="<%=vo.getIpAddress()%>" />
																			<jsp:param name="_flag" value="<%=_flag%>" />
																			<jsp:param name="Alias" value="<%=vo.getAlias()%>" />
																			<jsp:param name="Port" value="<%=vo.getPort()%>" />
																			<jsp:param name="DbName" value="<%=vo.getDbName()%>" />
																			<jsp:param name="dbtye" value="<%=dbtye%>" />
																			<jsp:param name="runstr" value="<%=runstr%>" />
																			<jsp:param name="created" value="<%=created%>" />
																			<jsp:param name="buffercache" value="<%=buffercache%>" />
																			<jsp:param name="lstrnStatu" value="<%=lstrnStatu%>" />
																			<jsp:param name="dictionarycache" value="<%=dictionarycache%>" />
																			<jsp:param name="unbuffercache" value="<%=unbuffercache%>" />
																			<jsp:param name="unpctmemorysorts" value="<%=unpctmemorysorts%>" />
																			<jsp:param name="librarycache" value="<%=librarycache%>" />
																			<jsp:param name="unlibrarycache" value="<%=unlibrarycache%>" />
																			<jsp:param name="pctmemorysorts" value="<%=pctmemorysorts%>" />
																			<jsp:param name="opencurstr" value="<%=opencurstr%>" />
																			<jsp:param name="picip" value="<%=picip%>" />
																			<jsp:param name="avgdata" value="<%=avgdata%>" />
																			<jsp:param name="memPerfValue" value="<%=memPerfValue%>" />
																			<jsp:param name="curconnectstr" value="<%=curconnectstr%>" />
																			<jsp:param name="managed" value="<%=managed%>" />
																			<jsp:param name="sid"  value="<%=mysid %>" />
																		</jsp:include> 
																	</td>
																</tr>
															</table>
														</td>
														<td class="td-container-main-tool" width=15%>
															<jsp:include page="/include/dbtoolbar.jsp">
																<jsp:param value="<%=myip %>" name="myip" />
																<jsp:param value="<%=myport %>" name="myport" />
																<jsp:param value="<%=myUser %>" name="myUser" />
																<jsp:param value="<%=myPassword %>" name="myPassword" />
																<jsp:param value="<%=mysid  %>" name="sid" />
																<jsp:param value="<%=id %>" name="id" />
																<jsp:param value="<%=dbPage %>" name="dbPage" />
																<jsp:param value="Oracle" name="subtype"/>
															</jsp:include>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="container-main-application-detail" class="container-main-application-detail">
																<tr>
																	<td width=70%>
																		<table id="application-detail-content" class="application-detail-content">
																			<tr>
																			<td>
																				<table id="application-detail-data"
													class="application-detail-data">
													<tr>
														<td class="detail-data-header">
															<%=oraDetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<table class="application-detail-data-body">
																<tr height="28" bgcolor="#ECECEC">
																	<td>
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
																		<input type="button" id="process" name="process" value="查 询" onclick="doQuery()">
																		<input type="button" name="pop" value="告警新窗口" onclick="opennewwin()"><hr/>
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
																	<td  height=15>&nbsp;&nbsp;<input type="button" name="submitss" value="修改等级" onclick="batchEditAlarmLevel();"></td>
																	<td width="15" height=15>&nbsp;&nbsp;</td>
																	<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="删除警告" onclick='oracledelete();'/>&nbsp;&nbsp;</td>
																<td width="15" height=15>&nbsp;&nbsp;</td>
																	</tr></table></td>
																</tr>
																<tr bgcolor="#ECECEC">
																	<td>
																		<table width="100%" border="0" cellpadding="0"
																			cellspacing="1">

																			<tr>
																				<td class="application-detail-data-body-title">
																					<INPUT type="checkbox" name="checkall" onclick="javascript:chkall()">
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
																					<strong>最近告警时间</strong>
																				</td>
																				<td class="application-detail-data-body-title">
																					<strong>处理状态</strong>
																				</td>
																				<td class="application-detail-data-body-title">
																					<strong></strong>
																				</td>
																			</tr>
																			<%
	int index = 0;
  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
  	List list = (List)request.getAttribute("list");
  	for(int i=0;i<list.size();i++){
 	index++;
  	EventList eventlist = (EventList)list.get(i);
  	Date cc = eventlist.getRecordtime().getTime();
  	String lastTime=eventlist.getLasttime();
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
  	//String rptman = eventlist.getReportman();
  	String rtime1 = _sdf.format(cc);
  	String rtime2 = lastTime.substring(5,16);
%>

																			<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
																				height="28">

																				<td class="detail-data-body-list"><INPUT type="checkbox" name="checkbox" value="<%=eventlist.getId()%>"><%=i+1%></td>
   	                                                                            <td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
																				<td class="application-detail-data-body-list"
																					align="center">
																					<%=content%></td>
																				<td class="application-detail-data-body-list"
																					align="center">
																					<%=rtime1%></td>
																				<td class="application-detail-data-body-list"
																					align="center">
																					<%=rtime2%></td>
																				<td class="detail-data-body-list" bgcolor=<%=bgcolorstr%>>
																					<font color=#ffffff><%=status%></font>
																				</td>
																				<td class="application-detail-data-body-list"
																					align="center">
																					<%
       	 if ("0".equals(s)&&"提示信息".equals(level)) {
																	    	 
																					}else if ("0".equals(s)) {
       		%>
																					<input type="button" value="接受处理" class="button"
																						onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 500, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
																					<%
       		}
       		if("1".equals(s)){
       		%>
																					<input type="button" value="填写报告" class="button"
																						onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
																					<%
       		}
       		if("2".equals(s)){
       		%>
																					<input type="button" value="查看报告" class="button"
																						onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
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
										</tr>
									</table>
								</td>
							</tr>
						</table>
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
  mainForm.action = "<%=rootPath%>/oracle.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&myip=<%=myip %>&myport=<%=myport %>&myUser=<%=myUser %>&myPassword=<%=myPassword %>&sid=<%=mysid %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>
