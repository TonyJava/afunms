<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc" %>

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

<html>
<head>
<%
  String runmodel = PollingEngine.getCollectwebflag(); 
   String rootPath = request.getContextPath(); 
   String menuTable = (String)request.getAttribute("menuTable");
   String timeFormat = "yyyy-MM-dd";
   String from_date1 = (String)request.getAttribute("starttime1");
   String to_date1 = (String)request.getAttribute("totime1");
   String _flag = (String)request.getAttribute("flag");
  String tmp = request.getParameter("id");
  int alarmLevel = (Integer)request.getAttribute("alarmLevel");
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
  String tablename1="iisconn"+allipstr;
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
  
 
	imgurlhash =(Hashtable) request.getAttribute("imgurlhash");
	//ping连通率
	String image_ping =(String)imgurlhash.get("IISPing").toString();
   
	iisping=(double)tm.iisping(iis.getId());
	
	int percent1 = Double.valueOf(iisping).intValue();
	int percent2 = 100-percent1;
	
	 
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
	String chart1 = null,chart2 = null,chart3 = null,responseTime = null;  

	DefaultPieDataset dpd = new DefaultPieDataset();
	dpd.setValue("可用时间",iisping);
	dpd.setValue("不可用时间",100 - iisping);
	chart1 = ChartCreator.createPieChart(dpd,"",120,120); 
	int status=0;
	status = iis.getStatus();
	//amcharts 生成连通率图形
    StringBuffer dataStr1 = new StringBuffer();
	dataStr1.append("连通;").append(Math.round(percent1)).append(";false;7CFC00\\n");
	dataStr1.append("未连通;").append(100-Math.round(percent1)).append(";false;FF0000\\n");
	String realdata = dataStr1.toString();
	
	StringBuffer dataStr2 = new StringBuffer();
	dataStr2.append("连通;").append(Math.round(Float.parseFloat(pingvalue))).append(";false;7CFC00\\n");
	dataStr2.append("未连通;").append(100-Math.round(Float.parseFloat(pingvalue))).append(";false;FF0000\\n");
	String avgdata = dataStr2.toString();
	String dbPage = "detail";
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>IIS性能报表</title>
<script type='text/javascript' src='/afunms/dwr/interface/DWRUtil.js'></script>
<script type='text/javascript' src='/afunms/dwr/engine.js'></script>
<script type='text/javascript' src='/afunms/dwr/util.js'></script>
<script language="javascript" src="/afunms/js/tool.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<script language="JavaScript" type="text/JavaScript">
function ping_word_report()
{
	mainForm.action = "<%=rootPath%>/iis.do?action=downloadReport&flag=per&str=1&id=<%=tmp%>";
	mainForm.submit();
}
function ping_excel_report()
{
	mainForm.action = "<%=rootPath%>/iis.do?action=downloadReport&flag=per&str=0&id=<%=tmp%>";
	mainForm.submit();
}
function ping_pdf_report()
{
	mainForm.action = "<%=rootPath%>/iis.do?action=downloadReport&flag=per&str=2&id=<%=tmp%>";
	mainForm.submit();
}
function ping_ok()
{
	var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	mainForm.action = "<%=rootPath%>/tomcat.do?action=showPingReport&id=<%=tmp%>&startdate="+starttime+"&todate="+endtime;
	mainForm.submit();
}
function ping_cancel()
{
window.close();
}
function query(){
  	var startdate = mainForm.startdate.value;
  	var todate = mainForm.todate.value;      
  	var oids ="";
  	var checkbox = document.getElementsByName("checkbox");
 		for (var i=0;i<checkbox.length;i++){
 			if(checkbox[i].checked==true){
 				if (oids==""){
 					oids=checkbox[i].value;
 				}else{
 					oids=oids+","+checkbox[i].value;
 				}
 			}
 		}
 	if(oids==null||oids==""){
 		alert("请至少选择一个设备");
 		return;
 	}
 	window.open ("<%=rootPath%>/hostreport.do?action=downloadmultihostreport&startdate="+startdate+"&todate="+todate+"&ids="+oids, "newwindow", "height=500, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes")    	      
}

function openwin(str,operate,ip) 
{	
  var startdate = mainForm.startdate.value;
  var todate = mainForm.todate.value;
  window.open ("<%=rootPath%>/hostreport.do?action="+operate+"&startdate="+startdate+"&todate="+todate+"&ipaddress="+ip+"&str="+str+"&type=host", "newwindow", "height=500, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes") 
}
function changeOrder(para){
	mainForm.orderflag.value = para;
	mainForm.action="<%=rootPath%>/netreport.do?action=netping"
  	mainForm.submit();
}
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
     
     //if(chk1&&chk2&&chk3)
     //{
     
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
	mainForm.action="<%=rootPath%>/hostreport.do?action=hostcpu";
	mainForm.submit();        
        //mainForm.action = "<%=rootPath%>/network.do?action=add";
        //mainForm.submit();
     //}  
       // mainForm.submit();
 });	
	
});
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
	document.getElementById('hostReportTitle-5').className='detail-data-title';
	document.getElementById('hostReportTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('hostReportTitle-5').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
<!-- snow add 2010-5-28 -->
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/resource/image/bg.jpg);
TEXT-ALIGN: center;
}
-->
</style>
</head>
<body id="body" class="body" onload="init();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form id="mainForm" method="post" name="mainForm">
<input type=hidden id="ipaddress" name="ipaddress" value=<%=request.getParameter("ipaddress") %>>
	<table id="container-main" class="container-main">
		<tr>
			<td>
				<table id="container-main-win" class="container-main-win">
					<tr>
						<td>
							<table id="win-content" class="win-content">
								<tr>
									<td>
										<table id="win-content-header" class="win-content-header">
				                			<tr>
							                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
							                	<td class="win-content-title" style="align:center">&nbsp;IIS性能报表</td>
							                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>											   
											</tr>
									    
					       				</table>
				       				</td>
				       			</tr>
						       	<tr>
						       		<td>
						       			<table id="win-content-body" class="win-content-body">
											<tr>
						       					<td>
													<table bgcolor="#ECECEC">
														<tr align="left" valign="center"> 
														<td height="28" align="left" width=60%>
														</td>
														<td height="28" align="left">
															<a href="javascript:ping_word_report()"><img name="selDay1" alt='导出word' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_word.gif" width=18  border="0">导出WORLD</a>
														</td>
														<td height="28" align="left">
															<a href="javascript:ping_excel_report()"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出EXCEL</a>
														</td>
														<td height="28" align="left">&nbsp;
															<a href="javascript:ping_pdf_report()"><img name="selDay1" alt='导出word' style="CURSOR: hand" src="<%=rootPath%>/resource/image/export_pdf.gif" width=18 border="0">导出PDF</a>
														</td>
														</tr>  
													</table>
						       					</td>
						       				</tr>
											<tr>
							                	<td class="win-data-title" style="height: 29px;" ></td>
							       			</tr>
							       			<tr align="left" valign="center"> 
			             						<td height="28" align="left" border="0">
													
													<input type=hidden name="eventid">
													<div id="loading">
													<div class="loading-indicator">
														<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
													</div>
													<table border="1" width="90%">
														 <tr>
											    	<td width=100%>
											    		<table class="application-detail-data-body">
														<tr>
														<td valign=top>
												      		<table style="BORDER-COLLAPSE: collapse" cellpadding=0 rules=none width=100% align=center border=1 algin="center">
								                    				<tr bgcolor="#F1F1F1">
								                      					<td width="30%" height="26" align=right  nowrap class=txtGlobal>&nbsp;发送的总字节数中的高32 位:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      				     <%if(totalBytesSentHighWord!=""){ %>
								                      					<%=totalBytesSentHighWord%>
								                      					<%} %>
								                      					</td>
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;发送的总字节数中的低32 位:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(totalBytesSentLowWord!=""){ %>
								                     		 			<%=totalBytesSentLowWord%>
								                     		 			<%} %>
								                     		 			</td>
								                    				</tr>
								                    				<tr >
								                      				
								                      					<td width="30%" height="26" align=right nowrap class=txtGlobal>&nbsp;接收的总字节数中的高32 位:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      					<%if(totalBytesReceivedHighWord!=""){ %>
								                      					<%=totalBytesReceivedHighWord%>
								                      					<%} %>
								                      					</td>
								                    				
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;接收的总字节数中的低32 位:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(totalBytesReceivedLowWord!=""){ %>
								                     		 			<%=totalBytesReceivedLowWord%>
								                     		 			<%} %>
								                     		 			</td>
								                    				</tr> 
								                    				<tr bgcolor="#F1F1F1">
								                      					<td width="30%" height="26" align=right  nowrap class=txtGlobal>&nbsp;发送的文件总数:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      					<%if(totalFilesSent!=""){ %>
								                      					<%=totalFilesSent%>
								                      					<%} %>
								                      					</td>
								                    				
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;接收的文件总数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(totalFilesReceived!=""){ %>
								                     		 			<%=totalFilesReceived%>
								                     		 			<%} %>
								                     		 			</td>
								                    				</tr>
								                    				<tr >
								                      					<td width="30%" height="26" align=right  nowrap class=txtGlobal>&nbsp;匿名连接的当前用户数:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      					<%if(currentAnonymousUsers!=""){ %>
								                      					<%=currentAnonymousUsers%>
								                      					<%} %>
								                      					</td>
								                    				
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;匿名连接的用户总数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(totalAnonymousUsers!=""){ %>
								                     		 			<%=totalAnonymousUsers%>
								                     		 			<%} %>
								                     		 			</td>
								                    				</tr>
								                    				<tr bgcolor="#F1F1F1">
								                      					<td width="30%" height="26" align=right  nowrap class=txtGlobal>&nbsp;匿名连接的最大用户数:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      					<%if(maxAnonymousUsers!=""){ %>
								                      					<%=maxAnonymousUsers%></td>
								                    				    <%} %>
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;当前连接数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(currentConnections!=""){ %>
								                     		 			<%=currentConnections%>
								                     		 			<%} %>
								                     		 			</td>
								                    				</tr>
								                    				<tr >
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;最大连接数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(maxConnections!=""){ %>
								                     		 			<%=maxConnections%></td>
								                    				    <%} %>
								                      					<td width="30%" height="26" align=right align=right nowrap class=txtGlobal>&nbsp;尝试连接数:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      					<%if(connectionAttempts!=""){ %>
								                      					<%=connectionAttempts%>
								                      					<%} %>
								                      					</td>
								                    				</tr>
								                    				<tr bgcolor="#F1F1F1">
								                      					<td width="30%" height="26" class=txtGlobal align=right nowrap>&nbsp;尝试登录数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(logonAttempts!=""){ %>
								                     		 			<%=logonAttempts%></td>
								                    				    <%} %>
								                      					<td width="30%" height="26" align=right nowrap class=txtGlobal>&nbsp;GET方法请求数:</td>
								                      					<td width="20%">&nbsp;&nbsp;
								                      					<%if(totalGets!=""){ %>
								                      					<%=totalGets%>
								                      					 <%} %>
								                      					</td>
								                    				</tr>
								                    				<tr >
								                      					<td width="30%" height="31" class=txtGlobal align=right nowrap>&nbsp;POST方法请求数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(totalPosts!=""){ %>
								                     		 			<%=totalPosts%>
								                     		 			<%} %>
								                     		 			</td>
								                    				
								                      					<td width="30%" height="31" class=txtGlobal align=right nowrap>&nbsp;页面访问错误总数:</td>
								                     		 			<td width="20%">&nbsp;&nbsp;
								                     		 			<%if(totalNotFoundErrors!=""){ %>
								                     		 			<%=totalNotFoundErrors%>
								                     		 			<%} %>
								                     		 			</td>
								                    				</tr>
                    									</table>       
												</td>
												
										</tr>

										<tr>
											<td colspan=2 style="align:center">
												<table   align=center >
																					<tr>
																						<td >&nbsp;&nbsp;</td>
																						<td style="align:center" >
																							<br>
																							<table cellpadding="0" cellspacing="0" width=98%>
																								<tr>
																									<td width="100%" style="align:center">
																										<div id="flashcontent1">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_area_other.swf?title=连通率&tablename=<%=tablename%>&category=ConnectUtilization", "IIS_Ping", "400", "300", "8", "#ffffff");
																											so.write("flashcontent1");
																										</script>
																										
																									</td>
																								</tr>
																							</table>
																						</td>
																						<td>
																							&nbsp;
																						</td>
																						<td style="align:center"  >
																							<br>
																							<table cellpadding="0" cellspacing="0" width=98%  style="align:center">
																								<tr>
																									<td width="100%" style="align:center">
																										<div id="flashcontent2">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Common_More_Line.swf?title=用户连接统计&tablename=<%=tablename1%>", "IIS_Connect", "400", "300", "8", "#ffffff");
																											so.write("flashcontent2");
																										</script>
																									</td>
																								</tr>
																							</table>
																						</td>
																					</tr>
																				</table><br><br>	
																					
            		<div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div>  	<br>				
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
			
					<br>
</form>  
</body>
</html>