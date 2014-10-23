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
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>

<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>

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
  String _flag = (String) request.getAttribute("flag");
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "sqlserverwait";
  String dbType = "sqlserver";  
  
String[] sysItem3={"HOST_NAME","DBNAME","VERSION","INSTANCE_NAME","STATUS","STARTUP_TIME","ARCHIVER"};
String[] sysItemch3={"主机名称","DB 名称","DB 版本","例程名","例程状态","例程开始时间","归档日志模式"};

double avgpingcon = (Double)request.getAttribute("avgpingcon");
int percent1 = Double.valueOf(avgpingcon).intValue();
int percent2 = 100-percent1;

  
String[] memoryItem={"AllSize","UsedSize","Utilization"};
String[] memoryItemch={"总容量","已用容量","当前利用率","最大利用率"};
String[] sysItem={"sysName","sysUpTime","sysContact","sysLocation","sysServices","sysDescr"};
String[] sysItemch={"设备名","设备启动时间","设备联系","设备位置","设备服务","设备描述"};
Hashtable max = (Hashtable) request.getAttribute("max");
Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");
  
 Hashtable sqlsys = (Hashtable)request.getAttribute("sqlsys"); 
 if(sqlsys == null)sqlsys = new Hashtable();
 String HostName = "";
 if(sqlsys.get("MACHINENAME")!=null){
 	HostName = (String)sqlsys.get("MACHINENAME");
  }
 String VERSION = "";
 if(sqlsys.get("VERSION")!=null){
 	VERSION = (String)sqlsys.get("VERSION");
  } 
 String productlevel = "";
 if(sqlsys.get("productlevel")!=null){
 	productlevel = (String)sqlsys.get("productlevel");
  }   
 String ProcessID = "";
 if(sqlsys.get("ProcessID")!=null){
 	ProcessID = (String)sqlsys.get("ProcessID");
  } 
 String IsSingleUser = "";
 if(sqlsys.get("IsSingleUser")!=null){
 	IsSingleUser = (String)sqlsys.get("IsSingleUser");
  } 
  
 String IsIntegratedSecurityOnly = "";
 if(sqlsys.get("IsIntegratedSecurityOnly")!=null){
 	IsIntegratedSecurityOnly = (String)sqlsys.get("IsIntegratedSecurityOnly");
  }
 String IsClustered = "";
 if(sqlsys.get("IsClustered")!=null){
 	IsClustered = (String)sqlsys.get("IsClustered");
  }        
 
Hashtable sqlValue = new Hashtable();
if((Hashtable)request.getAttribute("sqlValue")!= null)
	sqlValue = (Hashtable)request.getAttribute("sqlValue");
Hashtable memValue = new Hashtable();
if(sqlValue.get("mems") != null) 
	memValue = (Hashtable)sqlValue.get("mems");
Hashtable pagesValue = new Hashtable();
if(sqlValue.get("pages") != null)
	pagesValue = (Hashtable)sqlValue.get("pages");     	
	
	String myBufferCacheHitRatio = (String)request.getAttribute("bufferCacheHitRatio");
	double intMyBufferCacheHitRatio = 0;
	if(myBufferCacheHitRatio!= null && !myBufferCacheHitRatio.equals(""))
		intMyBufferCacheHitRatio = Double.valueOf(myBufferCacheHitRatio);
		
	String planCacheHitRatio = (String)request.getAttribute("planCacheHitRatio");
	double intPlanCacheHitRatio = 0;
	if(planCacheHitRatio!= null && !planCacheHitRatio.equals(""))
		intPlanCacheHitRatio = Double.valueOf(planCacheHitRatio);
		
	String cursorManagerByTypeHitRatio = (String)request.getAttribute("cursorManagerByTypeHitRatio");
	double intCursorManagerByTypeHitRatio = 0;
	if(cursorManagerByTypeHitRatio != null && !cursorManagerByTypeHitRatio.equals(""))
		intCursorManagerByTypeHitRatio = Double.valueOf(cursorManagerByTypeHitRatio);
		
	String catalogMetadataHitRatio = (String)request.getAttribute("catalogMetadataHitRatio");
	double intCatalogMetadataHitRatio = 0;
	if(catalogMetadataHitRatio != null && !catalogMetadataHitRatio.equals(""))
		intCatalogMetadataHitRatio = Double.valueOf(catalogMetadataHitRatio);
			
	String dbOfflineErrors = (String)request.getAttribute("dbOfflineErrors");
		
	String killConnectionErrors = (String)request.getAttribute("killConnectionErrors");
	String userErrors = (String)request.getAttribute("userErrors");
	String infoErrors = (String)request.getAttribute("infoErrors");
	String sqlServerErrors_total = (String)request.getAttribute("sqlServerErrors_total");
	String cachedCursorCounts = (String)request.getAttribute("cachedCursorCounts");
	String cursorCacheUseCounts = (String)request.getAttribute("cursorCacheUseCounts");
	String cursorRequests_total = (String)request.getAttribute("cursorRequests_total");
	String activeCursors = (String)request.getAttribute("activeCursors");
	String cursorMemoryUsage = (String)request.getAttribute("cursorMemoryUsage");
	String cursorWorktableUsage = (String)request.getAttribute("cursorWorktableUsage");
	String activeOfCursorPlans = (String)request.getAttribute("activeOfCursorPlans");		  	   
%>
<%
	String pingjun_lockWaits = (String)request.getAttribute("pingjun_lockWaits");
	String pingjun_memoryGrantQueueWaits = (String)request.getAttribute("pingjun_memoryGrantQueueWaits");
	String pingjun_threadSafeMemoryObjectWaits = (String)request.getAttribute("pingjun_threadSafeMemoryObjectWaits");
	String pingjun_logWriteWaits = (String)request.getAttribute("pingjun_logWriteWaits");
	String pingjun_logBufferWaits = (String)request.getAttribute("pingjun_logBufferWaits");
	String pingjun_networkIOWaits = (String)request.getAttribute("pingjun_networkIOWaits");
	String pingjun_pageIOLatchWaits = (String)request.getAttribute("pingjun_pageIOLatchWaits");
	String pingjun_pageLatchWaits = (String)request.getAttribute("pingjun_pageLatchWaits");
	String pingjun_nonPageLatchWaits = (String)request.getAttribute("pingjun_nonPageLatchWaits");
	String pingjun_waitForTheWorker = (String)request.getAttribute("pingjun_waitForTheWorker");
	String pingjun_workspaceSynchronizationWaits = (String)request.getAttribute("pingjun_workspaceSynchronizationWaits");
	String pingjun_transactionOwnershipWaits = (String)request.getAttribute("pingjun_transactionOwnershipWaits");
			
	String jingxing_lockWaits = (String)request.getAttribute("jingxing_lockWaits");
	String jingxing_memoryGrantQueueWaits = (String)request.getAttribute("jingxing_memoryGrantQueueWaits");
	String jingxing_threadSafeMemoryObjectWaits = (String)request.getAttribute("jingxing_threadSafeMemoryObjectWaits");
	String jingxing_logWriteWaits = (String)request.getAttribute("jingxing_logWriteWaits");
	String jingxing_logBufferWaits = (String)request.getAttribute("jingxing_logBufferWaits");
	String jingxing_networkIOWaits = (String)request.getAttribute("jingxing_networkIOWaits");
	String jingxing_pageIOLatchWaits = (String)request.getAttribute("jingxing_pageIOLatchWaits");
	String jingxing_pageLatchWaits = (String)request.getAttribute("jingxing_pageLatchWaits");
	String jingxing_nonPageLatchWaits = (String)request.getAttribute("jingxing_nonPageLatchWaits");
	String jingxing_waitForTheWorker = (String)request.getAttribute("jingxing_waitForTheWorker");
	String jingxing_workspaceSynchronizationWaits = (String)request.getAttribute("jingxing_workspaceSynchronizationWaits");
	String jingxing_transactionOwnershipWaits = (String)request.getAttribute("jingxing_transactionOwnershipWaits");

	String qidong_lockWaits = (String)request.getAttribute("qidong_lockWaits");
	String qidong_memoryGrantQueueWaits = (String)request.getAttribute("qidong_memoryGrantQueueWaits");
	String qidong_threadSafeMemoryObjectWaits = (String)request.getAttribute("qidong_threadSafeMemoryObjectWaits");
	String qidong_logWriteWaits = (String)request.getAttribute("qidong_logWriteWaits");
	String qidong_logBufferWaits = (String)request.getAttribute("qidong_logBufferWaits");
	String qidong_networkIOWaits = (String)request.getAttribute("qidong_networkIOWaits");
	String qidong_pageIOLatchWaits = (String)request.getAttribute("qidong_pageIOLatchWaits");
	String qidong_pageLatchWaits = (String)request.getAttribute("qidong_pageLatchWaits");
	String qidong_nonPageLatchWaits = (String)request.getAttribute("qidong_nonPageLatchWaits");
	String qidong_waitForTheWorker = (String)request.getAttribute("qidong_waitForTheWorker");
	String qidong_workspaceSynchronizationWaits = (String)request.getAttribute("qidong_workspaceSynchronizationWaits");
	String qidong_transactionOwnershipWaits = (String)request.getAttribute("qidong_transactionOwnershipWaits");
			
	String leiji_lockWaits = (String)request.getAttribute("leiji_lockWaits");
	String leiji_memoryGrantQueueWaits = (String)request.getAttribute("leiji_memoryGrantQueueWaits");
	String leiji_threadSafeMemoryObjectWaits = (String)request.getAttribute("leiji_threadSafeMemoryObjectWaits");
	String leiji_logWriteWaits = (String)request.getAttribute("leiji_logWriteWaits");
	String leiji_logBufferWaits = (String)request.getAttribute("leiji_logBufferWaits");
	String leiji_networkIOWaits = (String)request.getAttribute("leiji_networkIOWaits");
	String leiji_pageIOLatchWaits = (String)request.getAttribute("leiji_pageIOLatchWaits");
	String leiji_pageLatchWaits = (String)request.getAttribute("leiji_pageLatchWaits");
	String leiji_nonPageLatchWaits = (String)request.getAttribute("leiji_nonPageLatchWaits");
	String leiji_waitForTheWorker = (String)request.getAttribute("leiji_waitForTheWorker");
	String leiji_workspaceSynchronizationWaits = (String)request.getAttribute("leiji_workspaceSynchronizationWaits");
	String leiji_transactionOwnershipWaits = (String)request.getAttribute("leiji_transactionOwnershipWaits");
	
	
	
	
	String picip = CommonUtil.doip(myip);
	
    	//生成当天平均连通率图形
	CreatePiePicture _cpp = new CreatePiePicture();
	_cpp.createAvgPingPic(picip,avgpingcon); 
	
	
 %>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>


<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />

<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<script>
function createQueryString(){
		var user_name = encodeURI($("#user_name").val());
		var passwd = encodeURI($("#passwd").val());
		var queryString = {userName:user_name,passwd:passwd};
		return queryString;
	}

</script>

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
	//go();
	setClass();
}

function setClass(){
	document.getElementById('sqlDetailTitle-5').className='detail-data-title';
	document.getElementById('sqlDetailTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('sqlDetailTitle-5').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
<script type="text/javascript">
        var xmlHttp;
        var bar_color = 'skyblue';      /*进度条颜色*/
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
                   elem.innerHTML = "<%=intMyBufferCacheHitRatio%>%";
                }
            }
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
	<div id="loading">
		<div class="loading-indicator">
			<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif"
				width="32" height="32" style="margin-right: 8px;" align="middle" />
			Loading...
		</div>
	</div>
	<div id="loading-mask" style=""></div>
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
							<td class="td-container-main-application-detail">
								<table id="container-main-application-detail" class="container-main-application-detail">
									<tr>
										<td>
										  <table class="container-main-application-detail">
											<tr> 
											  <td>
											<jsp:include page="/topology/includejsp/db_sqlserver.jsp">
												<jsp:param name="dbtye" value="<%=dbtye%>" />
												<jsp:param name="ipAddress" value="<%=vo.getIpAddress()%>" />
												<jsp:param name="managed" value="<%=managed%>" />
												<jsp:param name="runstr" value="<%=runstr%>" />
												<jsp:param name="HostName" value="<%=HostName%>" />
												<jsp:param name="VERSION" value="<%=VERSION%>" />
												<jsp:param name="productlevel" value="<%=productlevel%>" />
												<jsp:param name="ProcessID" value="<%=ProcessID%>" />
												<jsp:param name="IsSingleUser" value="<%=IsSingleUser%>" />
												<jsp:param name="IsIntegratedSecurityOnly" value="<%=IsIntegratedSecurityOnly%>" />
												<jsp:param name="IsClustered" value="<%=IsClustered%>" />
												<jsp:param name="intMyBufferCacheHitRatio" value="<%=intMyBufferCacheHitRatio%>" />
												<jsp:param name="intPlanCacheHitRatio" value="<%=intPlanCacheHitRatio%>" />
												<jsp:param name="intCatalogMetadataHitRatio" value="<%=intCatalogMetadataHitRatio%>" />
												<jsp:param name="intCursorManagerByTypeHitRatio" value="<%=intCursorManagerByTypeHitRatio%>" />
												<jsp:param name="picip" value="<%=picip%>" />
												<jsp:param name="pingavg" value="<%=avgpingcon %>"/> 
											</jsp:include>
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
													<td class="detail-data-header">
														<%=sqlDetailTitleTable%>
													</td>
												</tr>
												<tr>
													<td>
														<table class="application-detail-data-body">
															<tr>
																<td>
																	<%
																		String str1 = "", str2 = "";
																		if (max.get("avgpingcon") != null) {
																			str2 = (String) max.get("avgpingcon");
																		}
																	%>


																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1">
																		<tr align="left" bgcolor="#ECECEC">
																			<td height="28" background="images/yemian_16.gif">
																				<b>&nbsp;平均等待时间</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="3"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待锁
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=pingjun_lockWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待内存授予
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_memoryGrantQueueWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待线程安全内存分配器
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=pingjun_threadSafeMemoryObjectWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待写入日志缓冲区
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_logWriteWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待日志缓冲区可用
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=pingjun_logBufferWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待网络 I/O
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_networkIOWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待页I/O闩锁
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_pageIOLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待页闩锁（不包括 I/O 闩锁）
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_pageLatchWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待非页闩锁
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_nonPageLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待工作线程变得可用
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_waitForTheWorker%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待同步访问工作空间
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_workspaceSynchronizationWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							等待同步访问事务
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=pingjun_transactionOwnershipWaits%></td>
																					</tr>
																				</table>
																			</td>
																		</tr>

																	</table>
																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1">
																		<tr align="left" bgcolor="#ECECEC">
																			<td height="28" background="images/yemian_16.gif">
																				<b>&nbsp;正在进行的等待时间</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="3"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							lockWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=jingxing_lockWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							memoryGrantQueueWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_memoryGrantQueueWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							threadSafeMemoryObjectWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=jingxing_threadSafeMemoryObjectWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							logWriteWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_logWriteWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							logBufferWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=jingxing_logBufferWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							networkIOWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_networkIOWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							pageIOLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_pageIOLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							pageLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_pageLatchWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							nonPageLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_nonPageLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							waitForTheWorker
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_waitForTheWorker%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							workspaceSynchronizationWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_workspaceSynchronizationWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							transactionOwnershipWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=jingxing_transactionOwnershipWaits%></td>
																					</tr>
																				</table>
																			</td>
																		</tr>

																	</table>
																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1">
																		<tr align="left" bgcolor="#ECECEC">
																			<td height="28" background="images/yemian_16.gif">
																				<b>&nbsp;每秒启动的等待数</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="3"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							lockWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=qidong_lockWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							memoryGrantQueueWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_memoryGrantQueueWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							threadSafeMemoryObjectWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=qidong_threadSafeMemoryObjectWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							logWriteWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_logWriteWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							logBufferWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=qidong_logBufferWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							networkIOWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_networkIOWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							pageIOLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_pageIOLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							pageLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_pageLatchWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							nonPageLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_nonPageLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							waitForTheWorker
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_waitForTheWorker%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							workspaceSynchronizationWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_workspaceSynchronizationWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							transactionOwnershipWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=qidong_transactionOwnershipWaits%></td>
																					</tr>
																				</table>
																			</td>
																		</tr>

																	</table>
																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1">
																		<tr align="left" bgcolor="#ECECEC">
																			<td height="28" background="images/yemian_16.gif">
																				<b>&nbsp;每秒的累积等待时间</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="3"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							lockWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=leiji_lockWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							memoryGrantQueueWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_memoryGrantQueueWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							threadSafeMemoryObjectWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=leiji_threadSafeMemoryObjectWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							logWriteWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_logWriteWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							logBufferWaits
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=leiji_logBufferWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							networkIOWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_networkIOWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							pageIOLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_pageIOLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							pageLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_pageLatchWaits%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							nonPageLatchWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_nonPageLatchWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							waitForTheWorker
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_waitForTheWorker%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							workspaceSynchronizationWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_workspaceSynchronizationWaits%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							transactionOwnershipWaits
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=leiji_transactionOwnershipWaits%></td>
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
				<td width=15% valign=top>
					<jsp:include page="/include/dbtoolbar.jsp">
						<jsp:param value="<%=myip%>" name="myip" />
						<jsp:param value="<%=myport%>" name="myport" />
						<jsp:param value="<%=myUser%>" name="myUser" />
						<jsp:param value="<%=myPassword%>" name="myPassword" />
						<jsp:param value="<%=mysid%>" name="sid" />
						<jsp:param value="<%=id%>" name="id" />
						<jsp:param value="<%=dbPage%>" name="dbPage" />
						<jsp:param value="<%=dbType%>" name="dbType" />
						<jsp:param value="sqlserver" name="subtype" />
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
  mainForm.action = "<%=rootPath%>/sqlserver.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&myip=<%=myip %>&myport=<%=myport %>&myUser=<%=myUser %>&myPassword=<%=myPassword %>&sid=<%=mysid %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>