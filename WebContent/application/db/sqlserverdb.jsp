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
  String dbPage = "sqlserverdb";
  String dbType = "sqlserver";
double avgpingcon = (Double)request.getAttribute("avgpingcon");
int percent1 = Double.valueOf(avgpingcon).intValue();
int percent2 = 100-percent1;

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
%>

<%
			
			Hashtable dbValue = (Hashtable)request.getAttribute("dbValue");
			Hashtable alldatabase = new Hashtable();
			Hashtable alllogfile = new Hashtable();
			alldatabase = (Hashtable)dbValue.get("database");
			if(alldatabase == null)alldatabase = new Hashtable();
			alllogfile = (Hashtable)dbValue.get("logfile");
			if(alllogfile == null)alllogfile = new Hashtable();	
			Vector names = (Vector)	dbValue.get("names");					
			
		//Hashtable sqlValue = (Hashtable)request.getAttribute("sqlValue");
		if(sqlValue == null) sqlValue = new Hashtable();
		Hashtable connsValue = (Hashtable)sqlValue.get("conns");
		Hashtable cachesValue = (Hashtable)sqlValue.get("caches");
		Hashtable sqlsValue = (Hashtable)sqlValue.get("sqls");
		Hashtable scansValue = (Hashtable)sqlValue.get("scans");	
   %>
<%
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
		
		
	String picip = CommonUtil.doip(myip);
	
    	//生成当天平均连通率图形
	CreatePiePicture _cpp = new CreatePiePicture();
	_cpp.createAvgPingPic(picip,avgpingcon); 
	

    %>
<%String menuTable = (String)request.getAttribute("menuTable");%>
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
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />



		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
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
	setClass();
}

function setClass(){
	document.getElementById('sqlDetailTitle-2').className='detail-data-title';
	document.getElementById('sqlDetailTitle-2').onmouseover="this.className='detail-data-title'";
	document.getElementById('sqlDetailTitle-2').onmouseout="this.className='detail-data-title'";
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
								<table id="container-main-application-detail"
									class="container-main-application-detail">
									<tr>
										<td>
											<table class="container-main-application-detail">
												<tr> 
													<td> 
														<jsp:include page="/topology/includejsp/db_sqlserver.jsp">
															<jsp:param name="dbtye" value="<%=dbtye %>"/>
															<jsp:param name="ipAddress" value="<%=vo.getIpAddress() %>"/> 
															<jsp:param name="managed" value="<%=managed %>"/>
															<jsp:param name="runstr" value="<%=runstr %>"/>
															<jsp:param name="HostName" value="<%=HostName %>"/>
															<jsp:param name="VERSION" value="<%=VERSION %>"/>
															<jsp:param name="productlevel" value="<%=productlevel %>"/>
															<jsp:param name="ProcessID" value="<%=ProcessID %>"/>
															<jsp:param name="IsSingleUser" value="<%=IsSingleUser %>"/>
															<jsp:param name="IsIntegratedSecurityOnly" value="<%=IsIntegratedSecurityOnly %>"/>
															<jsp:param name="IsClustered" value="<%=IsClustered %>"/>
															<jsp:param name="intMyBufferCacheHitRatio" value="<%=intMyBufferCacheHitRatio %>"/>
															<jsp:param name="intPlanCacheHitRatio" value="<%=intPlanCacheHitRatio %>"/>
															<jsp:param name="intCatalogMetadataHitRatio" value="<%=intCatalogMetadataHitRatio %>"/>
															<jsp:param name="intCursorManagerByTypeHitRatio" value="<%=intCursorManagerByTypeHitRatio %>"/> 
															<jsp:param name="picip" value="<%=picip %>"/> 
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
																		<tr align="left" bgcolor="#ECECEC" height="28">
																			<td height="23" background="images/yemian_16.gif">
																				<b>&nbsp;数据库连接信息</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="0"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<%
																						if (connsValue != null) {
																					%>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							活动连接
																						</td>
																						<td width="20%" colspan=3
																							class="application-detail-data-body-list"><%=connsValue.get("connections")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							登录
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=connsValue.get("totalLogins")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							登录/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=connsValue.get("totalLoginsRate")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							退出
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=connsValue.get("totalLogouts")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							退出/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=connsValue.get("totalLogoutsRate")%></td>
																					</tr>
																					<%
																						}
																					%>

																				</table>
																			</td>
																		</tr>

																	</table>
																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1s">
																		<tr align="left" bgcolor="#ECECEC" height="28">
																			<td height="23" background="images/yemian_16.gif">
																				<b>&nbsp;数据库缓存明细</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="0"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<%
																						if (cachesValue != null) {
																					%>

																					<!--<tr bgcolor="DEEBF7"> 
																	                       <td width="20%">缓存击中率</td>
																	                       <td width="20%"><%=cachesValue.get("cacheHitRatio")%></td>
																	                       <td width="20%">缓存击中率计算的基数</td>
																	                       <td width="40%"><%=cachesValue.get("cacheHitRatioBase")%></td>                        
																                       </tr>-->

																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							缓存数
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=cachesValue.get("cacheCount")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							缓存页
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=cachesValue.get("cachePages")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							使用的缓存
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=cachesValue.get("cacheUsed")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							使用的缓存/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=cachesValue.get("cacheUsedRate")%></td>
																					</tr>
																					<%
																						}
																					%>

																				</table>
																			</td>
																		</tr>
																	</table>
																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1">
																		<tr align="left" bgcolor="#ECECEC" height="28">
																			<td height="23" background="images/yemian_16.gif">
																				<b>&nbsp;数据库SQL信息</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="0"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<%
																						if (sqlsValue != null) {
																					%>

																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							批请求
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("batchRequests")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							批请求/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("batchRequestsRate")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							SQL编辑
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("sqlCompilations")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							SQL编辑/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("sqlCompilationsRate")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							SQL重编辑
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("sqlRecompilation")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							SQL重编辑/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("sqlRecompilationRate")%></td>
																					</tr>

																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							自动参数化尝试次数
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("autoParams")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							自动参数化尝试次数/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("autoParamsRate")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							自动参数化失败次数
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("failedAutoParams")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							自动参数化失败次数/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=sqlsValue.get("failedAutoParamsRate")%></td>
																					</tr>
																					<%
																						}
																					%>

																				</table>
																			</td>
																		</tr>
																	</table>
																	<table width="100%" border="0" cellpadding="0"
																		cellspacing="1">
																		<tr align="left" bgcolor="#ECECEC">
																			<td height="23" background="images/yemian_16.gif">
																				<b>&nbsp;数据库访问方法明细</b>
																			</td>
																		</tr>
																		<tr bgcolor="DEEBF7">
																			<td>
																				<table width="100%" border="0" cellpadding="0"
																					cellspacing="1" bgcolor="#FFFFFF">
																					<%
																						if (scansValue != null) {
																					%>

																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							完全扫描
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=scansValue.get("fullScans")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							完全扫描/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=scansValue.get("fullScansRate")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%"
																							class="application-detail-data-body-list">
																							范围扫描
																						</td>
																						<td width="20%"
																							class="application-detail-data-body-list"><%=scansValue.get("rangeScans")%></td>
																						<td width="20%"
																							class="application-detail-data-body-list">
																							范围扫描/分
																						</td>
																						<td width="40%"
																							class="application-detail-data-body-list"><%=scansValue.get("rangeScansRate")%></td>
																					</tr>
																					<tr bgcolor="#FFFFFF" height="28">
																						<td width="20%">
																							探针扫描
																						</td>
																						<td width="20%"><%=scansValue.get("probeScans")%></td>
																						<td width="20%">
																							探针扫描/分
																						</td>
																						<td width="40%"><%=scansValue.get("probeScansRate")%></td>
																					</tr>
																					<%
																						}
																					%>

																				</table>
																			</td>
																		</tr>
																	</table>

																	<table width="100%" border="0" cellspacing="1"
																		cellpadding="0">
																		<tr align="left" bgcolor="#ECECEC" height="28">
																			<td width="76%">
																				<b>&nbsp;数据库信息</b>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<table width="80%" align="center" border=0
																					cellspacing="0" cellpadding="0"
																					bordercolordark="#FFFFFF"
																					bordercolorlight="#000000">
																					<tr height="28">
																						<td align=center
																							class="application-detail-data-body-list">
																							数据库
																						</td>
																						<td align=center
																							class="application-detail-data-body-list">
																							总大小（MB）
																						</td>
																						<td align=center
																							class="application-detail-data-body-list">
																							使用大小（MB）
																						</td>
																						<td align=center
																							class="application-detail-data-body-list">
																							利用率
																						</td>
																					</tr>
																					<%
																						if (alldatabase != null && alldatabase.size() > 0) {
																							if (names == null)
																								names = new Vector();
																							for (int i = 0; i < names.size(); i++) {
																								String key = (String) names.get(i);
																								if (alldatabase.get(key) == null)
																									continue;
																								Hashtable data = (Hashtable) alldatabase.get(key);
																								String dbname = data.get("dbname").toString();
																								String size = data.get("size").toString();
																								String usedsize = "";
																								if (data.get("usedsize") != null) {
																									usedsize = data.get("usedsize").toString();
																								}
																								String usedperc = "";
																								if (data.get("usedperc") != null)
																									usedperc = data.get("usedperc").toString();
																					%>


																					<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
																						height="28">
																						<td class="application-detail-data-body-list">
																							&nbsp;<%=dbname%></td>
																						<td class="application-detail-data-body-list">
																							&nbsp;<%=size%></td>
																						<td class="application-detail-data-body-list">
																							&nbsp;<%=usedsize%></td>
																						<td class="application-detail-data-body-list">
																							&nbsp;<%=usedperc%></td>
																					</tr>

																					<%
																						}
																						}
																					%>

																				</table>

																				<table width="100%" cellpadding="0" cellspacing="1">
																					<tr>
																						<td align=center>
																							<br>
																							<table cellpadding="0" cellspacing="0" width=98%>
																								<tr>
																									<td width="100%">
																										<div id="flashcontent3">
																											<strong>You need to upgrade your
																												Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Column_SqlDb.swf?ip=<%=vo.getIpAddress()%>&user=<%=vo.getUser()%>&pwd=<%=vo.getPassword()%>", "Column_SqlDb", "346", "250", "8", "#ffffff");
																											so.write("flashcontent3");
																										</script>
																									</td>
																								</tr>
																							</table>
																						</td>
																						<td>
																							<br>
																							<table cellpadding="0" cellspacing="0" width=98%>
																								<tr>
																									<td width="100%">
																										<div id="flashcontent4">
																											<strong>You need to upgrade your
																												Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Column_SqlDb_Log.swf?ip=<%=vo.getIpAddress()%>&user=<%=vo.getUser()%>&pwd=<%=vo.getPassword()%>", "Column_SqlDb_Log", "346", "250", "8", "#ffffff");
																											so.write("flashcontent4");
																										</script>
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
		  mainForm.action = "<%=rootPath%>/sqlserver.do?action=sychronizeData&dbPage=<%=dbPage%>&id=<%=id%>&myip=<%=myip%>&myport=<%=myport%>&myUser=<%=myUser%>&myPassword=<%=myPassword%>&sid=<%=mysid%>&flag=<%=_flag%>";
		  mainForm.submit();
		 });    
		});
	</script>
</BODY>
</HTML>