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
<%
  String rootPath = request.getContextPath();;
  DBVo vo  = (DBVo)request.getAttribute("db");	
  String id = (String)request.getAttribute("id");
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "sybasecap";
  String dbType = "sybase";
  String _flag = (String)request.getAttribute("flag");
Hashtable max = (Hashtable) request.getAttribute("max");
Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");

List<TablesVO> list = (List)request.getAttribute("tablesVO");

SybaseVO sysbaseVO = (SybaseVO)request.getAttribute("sysbaseVO");
if (sysbaseVO == null)sysbaseVO = new SybaseVO();     
double avgpingcon = (Double)request.getAttribute("avgpingcon");
int percent1 = Double.valueOf(avgpingcon).intValue();
int percent2 = 100-percent1; 
StringBuffer dataStr2 = new StringBuffer();
	dataStr2.append("连通;").append(Math.round(avgpingcon)).append(
			";false;7CFC00\\n");
	dataStr2.append("未连通;").append(100 - Math.round(avgpingcon))
			.append(";false;FF0000\\n");
	String avgdata = dataStr2.toString();  
//System.out.println("============================122222222"); 		

StringBuffer sb=new StringBuffer();
	String dataStr = "";																				                      
    String[] colorStr=new String[] {"#33FF33","#FF0033","#9900FF","#FFFF00","#33CCFF","#003366","#33FF33","#FF0033","#9900FF","#FFFF00","#333399","#0000FF","#A52A2A","#800080"};
    sb.append("<chart><series>");
    TablesVO tablesVO = null;
    if(list!=null){
    for(int k=0;k<list.size();k++){
       tablesVO = list.get(k);
	   sb.append("<value xid='").append(k).append("'>").append(tablesVO.getDb_name()).append("</value>");
	  }
	}
	sb.append("</series><graphs><graph gid='0'>");
	if(list!=null){
	for(int i=0,j=0;i<list.size();i++,j++){
	   tablesVO = list.get(i);
	if(i/colorStr.length==1)j=0;
	int perInt=Math.round(Float.parseFloat(tablesVO.getDb_usedperc()));
	  sb.append("<value xid='").append(i).append("' color='").append(colorStr[j]).append("'>"+perInt).append("</value>");
	 }
	}
	sb.append("</graph></graphs></chart>");
	dataStr=sb.toString();
	//System.out.println("--->"+dataStr);	  	   
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
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script>
function createQueryString(){
		var user_name = encodeURI($("#user_name").val());
		var passwd = encodeURI($("#passwd").val());
		var queryString = {userName:user_name,passwd:passwd};
		return queryString;
	}
function gzmajax(){
$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/sybaseAjaxManager.ajax?action=ajaxUpdate_availability&id=<%=id%>&nowtime="+(new Date()),
			success:function(data){
				//$("#flashcontent00gzm").html(data.percent1+":"+data.percent2+":"+data.cpuper);
				var so = new SWFObject("<%=rootPath%>/flex/Pie_Component.swf?percent1="+data.percent1+"&percentStr1=可用&percent2="+data.percent2+"&percentStr2=不可用", "Pie_Component", "160", "160", "8", "#ffffff");
				so.write("flashcontent00");
				
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
	document.getElementById('sybDetailTitle-0').className='detail-data-title';
	document.getElementById('sybDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('sybDetailTitle-0').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
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
														<td valign="top">
															<jsp:include page="/topology/includejsp/db_sysbase.jsp">
																<jsp:param name="dbtye" value="<%= dbtye%>"/> 
																<jsp:param name="IpAddress" value="<%= vo.getIpAddress()%>"/> 
																<jsp:param name="managed" value="<%= managed%>"/> 
																<jsp:param name="runstr" value="<%= runstr%>"/> 
																<jsp:param name="ServerName" value="<%= sysbaseVO.getServerName()%>"/> 
																<jsp:param name="Version" value="<%= sysbaseVO.getVersion()%>"/> 
																<jsp:param name="avgdata" value="<%= avgdata%>"/> 
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
															<%=sybDetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<table class="application-detail-data-body">
																<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="0"
																			cellspacing="1">
																			<tr>
																				<td>
																					<table cellpadding="0" cellspacing="0" width="100%">
																						<tr>
																							<td width="48%" align="center">
																								<br>
																								<table cellpadding="0" cellspacing="0" width=98%>
																									<tr>
																										<td width="100%">
																											<div id="flashcontent1">
																												<strong>You need to upgrade your
																													Flash Player</strong>
																											</div>
																											<script type="text/javascript">
													var so = new SWFObject("<%=rootPath%>/flex/Oracle_Ping.swf?ipadress=<%=vo.getId()%>&category=SYSPing", "Oracle_Ping", "346", "250", "8", "#ffffff");
													so.write("flashcontent1");
												</script>
																										</td>
																									</tr>
																								</table>
																								<br>
																							</td>
																							<td width="48%" align="center">
																								<br>
																								<table cellpadding="0" cellspacing="0" width=98%>
																									<tr>
																										<td width="100%">
																											<div id="flashcontent2">
																												<strong>You need to upgrade your
																													Flash Player</strong>
																											</div>
																										<!--  
																											<script type="text/javascript">
													                                                            var so = new SWFObject("<%=rootPath%>/flex/Column_Sybase_DB.swf?ipadress=<%=vo.getIpAddress()%>&name=<%=vo.getDbName()%>", "Column_Sybase_DB", "346", "250", "8", "#ffffff");
													                                                            so.write("flashcontent2");
												                                                            </script>
												                                                        -->
												                                                           <script type="text/javascript"
							                                                                    src="<%=rootPath%>/include/swfobject.js"></script>
						                                                                   <script type="text/javascript">
						                                                                         var so = new SWFObject("<%=rootPath%>/amchart/amcolumn.swf", "ampie","364", "250", "8", "#FFFFFF");
						                                                                             so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                             so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/dbpercent_settings.xml"));
						                                                                             so.addVariable("chart_data","<%=dataStr%>");
						                                                                             so.write("flashcontent2");
						                                                                  </script>	
												                                                        
																										</td>
																									</tr>
																								</table>
																								<br>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																			
																			<tr align="left" bgcolor="#ECECEC">
																				<td height="28"
																					class="application-detail-data-body-title">
																					&nbsp;&nbsp;
																					<b>Sysbase数据库: <%=vo.getDbName()%>表空间</b>
																				</td>
																			</tr>
																			<tr bgcolor="#FFFFFF">
																				<td>
																					<table width="100%" border="0" cellpadding="3"
																						cellspacing="1" bgcolor="#FFFFFF">
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="25%"
																								class="application-detail-data-body-list">
																								表名称
																							</td>
																							<td width="25%"
																								class="application-detail-data-body-list">数据库大小(MB)</td>
																							<td width="25%"
																								class="application-detail-data-body-list">
																								数据库可使用大小(MB)
																							</td>
																							<td width="25%"
																								class="application-detail-data-body-list">数据库利用率(%)</td>
																						</tr>

                                                                                        <% 
                                                                                           if(list != null && list.size() >0){
                                                                                             TablesVO vo_ = null;
                                                                                             for(int i = 0;i<list.size();i++){
                                                                                               vo_ = list.get(i);
                                                                                        %>
                                                                                          <tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="25%"
																								class="application-detail-data-body-list">
																								<%=vo_.getDb_name() %>
																							</td>
																							<td width="25%"
																								class="application-detail-data-body-list"><%=vo_.getDb_size()%></td>
																							<td width="25%"
																								class="application-detail-data-body-list">
																								<%=vo_.getDb_freesize()%>
																							</td>
																							<td width="25%"
																								class="application-detail-data-body-list"><%=vo_.getDb_usedperc()%></td>
																						</tr>
                                                                                        <%
                                                                                           }
                                                                                          }
                                                                                         %>

																					</table>
																				</td>
																			</tr>
																			
																			
																			<tr align="left" bgcolor="#ECECEC">
																				<td height="28"
																					class="application-detail-data-body-title">
																					&nbsp;&nbsp;
																					<b>Sysbase数据库: <%=vo.getDbName()%>( IP:<%=vo.getIpAddress()%>
																						)</b>
																				</td>
																			</tr>
																			<tr bgcolor="#FFFFFF">
																				<td>
																					<table width="100%" border="0" cellpadding="3"
																						cellspacing="1" bgcolor="#FFFFFF">
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								数据库空闲时间(秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getIdle()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								存储缓存匹配度(%)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getProcedure_hitrate()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								cpu工作时间(秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getCpu_busy()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								cpu利用率(%)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getCpu_busy_rate()%></td>
																						</tr>

																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								I/O工作时间(秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getIo_busy()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								IO利用率(%)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getIo_busy_rate()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								输出数据速率(条/秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getSent_rate()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								输入数据速率(条/秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getReceived_rate()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								写入磁盘速率(条/秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getWrite_rate()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								读取磁盘速率(条/秒)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getRead_rate()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								设备个数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getDisk_count()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								活动锁个数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getLocks_count()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								事务的个数
																							</td>
																							<td width="20%" 
																								class="application-detail-data-body-list"><%=sysbaseVO.getXact_count()%>
																								</td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								可用锁数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getLocks_count()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								持有锁并且阻塞其他进程的进程数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getLong_locks_count()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								离线状态的引擎数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getOfflineengine()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								数据库设置的最大连接数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getMaxconn()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								用户已使用连接数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getUsedconn()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								数据库可用连接数
																							</td>
																							<%
																								int useableConn = 0;//可用连接数
																								int usedConnPer = 0;//连接使用率
																								if(sysbaseVO.getMaxconn() != null && !sysbaseVO.getMaxconn().equals("")){
																									useableConn = Integer.parseInt(sysbaseVO.getMaxconn())-Integer.parseInt(sysbaseVO.getUsedconn());
																									usedConnPer = Math.round(Integer.parseInt(sysbaseVO.getUsedconn())*100/Integer.parseInt(sysbaseVO.getMaxconn()));
																								}
																							%>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=useableConn%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								用户连接数使用率
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=usedConnPer%>%</td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								数据库进程数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getProcesscount()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								空闲进程数
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getSleepingproccount()%></td>
																						</tr>
																						<tr bgcolor="#ECECEC" height=28>
																							<td width="100%" colspan=4
																								class="application-detail-data-body-title">
																								&nbsp;&nbsp;
																								<b>存储信息</b>
																							</td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								总数据高速缓存大小(MB)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getTotal_dataCache()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								总物理内存大小(MB)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getTotal_physicalMemory()%></td>
																						</tr>

																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								Metadata缓存(MB)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getMetadata_cache()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								存储过程缓存大小(MB)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getProcedure_cache()%></td>
																						</tr>
																						<tr bgcolor="#FFFFFF" height=28
																							<%=onmouseoverstyle%>>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								总逻辑内存大小(MB)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getTotal_logicalMemory()%></td>
																							<td width="30%"
																								class="application-detail-data-body-list">
																								数据缓存匹配度(%)
																							</td>
																							<td width="20%"
																								class="application-detail-data-body-list"><%=sysbaseVO.getData_hitrate()%></td>
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
								<td valign=top width=18%>
															<jsp:include page="/include/dbtoolbar.jsp">
																<jsp:param value="<%=myip %>" name="myip" />
																<jsp:param value="<%=myport %>" name="myport" />
																<jsp:param value="<%=myUser %>" name="myUser" />
																<jsp:param value="<%=myPassword %>" name="myPassword" />
																<jsp:param value="<%=mysid  %>" name="sid" />
																<jsp:param value="<%=id %>" name="id" />
																<jsp:param value="<%=dbPage %>" name="dbPage" />
																<jsp:param value="<%=dbType %>" name="dbType" />
																<jsp:param value="sybase" name="subtype"/>
															</jsp:include>
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
  mainForm.action = "<%=rootPath%>/sybase.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&myip=<%=myip %>&myport=<%=myport %>&myUser=<%=myUser %>&myPassword=<%=myPassword %>&sid=<%=mysid %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  