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
	document.getElementById('iisDetailTitle-0').className='detail-data-title';
	document.getElementById('iisDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('iisDetailTitle-0').onmouseout="this.className='detail-data-title'";
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
<form method="post" name="mainForm">
<input type=hidden name="orderflag">
<input type=hidden name="id">
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
										<td>
											<table id="application-detail-data" class="application-detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=iisDetailTitleTable%>
											    	</td>
											   </tr>
		                                       <tr>
											    	<td>
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
												<td width=20%>
													<table width="100%" style="BORDER-COLLAPSE: collapse" cellPadding=0 rules=none align=center border=1 algin="center">
                        								<tbody>
                        								<tr bgcolor=#F1F1F1 height="26">
														<td align="center">
															今日平均连通率
														</td>
														</tr>
                          									<tr class="topNameRight">
                      											<td height="70" align="center">
                      																		<!--  		<div id="flashcontent00">
																						<strong>You need to upgrade your Flash Player</strong>	
																							</div>
																							<script type="text/javascript">
																							var so = new SWFObject("<%=rootPath%>/flex/Pie_Component.swf?percent1=<%=percent1%>&percentStr1=可用&percent2=<%=percent2%>&percentStr2=不可用", "Pie_Component", "160", "160", "8", "#ffffff");
																							so.write("flashcontent00");
																							</script>
																							-->
														                                        <div id="realping">
							                                                                           <strong>You need to upgrade your Flash Player</strong>
						                                                                       </div>
						                                                                       <script type="text/javascript"
							                                                                         src="<%=rootPath%>/include/swfobject.js"></script>
						                                                                      <script type="text/javascript">
						                                                                             var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
						                                                                                 so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                 so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                                                                 so.addVariable("chart_data","<%=realdata%>");
						                                                                                 so.write("realping");
						                                                                    </script>									
																</td>
                    										</tr>
                    										<tr>
										           		<td height="7" align=center><img src="<%=rootPath%>/resource/image/Loading_2.gif"><br><br></td>
										         	</tr>
            										</tbody>
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
																				</table>	
																										
											</td>
										</tr>
			
											  <tr>
											  	<td colspan=2>											  		
															&nbsp;													  													  		
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