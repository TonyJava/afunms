<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.system.model.TimeShareConfig"%>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="com.afunms.alarm.util.AlarmConstant"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.detail.service.apacheInfo.ApacheInfoService"%>
<%
	String runmodel = PollingEngine.getCollectwebflag(); 
	String rootPath = request.getContextPath();;
	  
	ApacheConfig vo=(ApacheConfig)request.getAttribute("vo");
	//Integer status=(Integer)request.getAttribute("status");
	//Hashtable apache_ht=(Hashtable)request.getAttribute("apache_ht");
    String ip="";
    int id=0;
    if(vo!=null)ip=vo.getIpaddress();
    if(vo!=null)id=vo.getId();
	Hashtable apache_ht = null;

	if("0".equals(runmodel)){
	 	//采集与访问是集成模式
		apache_ht = (Hashtable)com.afunms.common.util.ShareData.getApachedata().get("apache"+":"+ip);
	}else{  
		//采集与访问是分离模式
		ApacheInfoService apacheInfoService = new ApacheInfoService();
		apache_ht = apacheInfoService.getApacheDataHashtable(id+"");
	}
	if(apache_ht == null)apache_ht = new Hashtable();
	String status_str = (String)apache_ht.get("status");
	Integer status = 0;
	if(status_str!=null){
		status = Integer.valueOf(status_str);
	}

	String conn_name = request.getAttribute("conn_name").toString();
	String connsrc = rootPath+"/resource/image/jfreechart/"+conn_name+".png";
	String wave_name = request.getAttribute("wave_name").toString();
	String wavesrc =  rootPath+"/resource/image/jfreechart/"+wave_name+".png";
	String connrate = request.getAttribute("connrate").toString();
	connrate = connrate.replaceAll("%","");
	int percent1 = Double.valueOf(connrate).intValue();
	int percent2 = 100-percent1;
	String timeFormat = "yyyy-MM-dd";
	
	
	String fdate = (String)request.getAttribute("from_date1");
	String tdate = (String)request.getAttribute("to_date1");

	java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
	String from_date1 = "";
	if (fdate == null ){
		from_date1=timeFormatter.format(new java.util.Date());
	}else{
		from_date1 = fdate;
	}
	String to_date1 = "";
	if (tdate == null ){
		to_date1=timeFormatter.format(new java.util.Date());
	}else{
		to_date1 = tdate;
	}
	
	
 String menuTable = (String)request.getAttribute("menuTable");
 status= SystemSnap.getNodeStatus(id+"",AlarmConstant.TYPE_MIDDLEWARE,"apache");
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />

<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>



<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<script type="text/javascript">

function setClass(){
	document.getElementById('apacheDetailTitle-4').className='detail-data-title';
	document.getElementById('apacheDetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('apacheDetailTitle-4').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
function query(){
	 	
     	mainForm.action = "<%=rootPath%>/apache.do?action=ping&flag=1&id=<%=vo.getId()%>";
    	mainForm.submit();
}
</script>
</head>
<body id="body" class="body" onload="setClass();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>

<!-- 这里用来定义需要显示的右键菜单 -->
	<div id="itemMenu" style="display: none";>
	<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
		style="border: thin" cellspacing="0">
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.edit()">修改信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.detail();">监视信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.cancelmanage()">取消监视</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.addmanage()">添加监视</td>
		</tr>		
	</table>
	</div>
	<!-- 右键菜单结束-->

	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="id">
		
		<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td><%=menuTable%></td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-service-detail">
									<table id="container-main-service-detail"
										class="container-main-service-detail">
										<tr>
											<td>
												<jsp:include page="/topology/includejsp/middleware_apa.jsp">
													<jsp:param name="id" value="<%=id%>" />

												</jsp:include>
											</td>
										</tr>
										<tr>
											<td valign=top>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
														<td>
															<%=apacheDetailTitleTable%>
														</td>
													</tr>
											  	<tr>
											  		<td>
														<table class="detail-data-body">	 
											          	 	<tr>
											                    <td colspan="5" width="80%" align="left"  class=dashLeft>		
										                  			<table style="BORDER-COLLAPSE: collapse"  bordercolor=#cedefa cellpadding=0 rules=none width=100% align=center border=1 algin="center">
										                    			<tr>
														                    <td width="100%" height="28" bgcolor="#ECECEC" style="HEIGHT: 25px">
														                    
														                    
														                    	开始日期
														                    <input type="text" name="from_date1" value="<%=from_date1%>" size="10"/>
														                    <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].from_date1,null,0,330)">
														                    <img id=imageCalendar1 width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
														                   		 截止日期 
														                    <input type="text" name="to_date1" value="<%=to_date1%>" size="10"/>
														                    <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].to_date1,null,0,330)">
														                    <img id=imageCalendar2 width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
														                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														                    <input type="button" name="submitss" value="查询" onclick="query()">                          																															
														                    
														                    
														                    </td> 
																	    </tr>
																		<tr>
																			<%if(wavesrc!=null)
																		
																			{ %>
																			<td align="center" width="100%"><img src="<%=wavesrc%>"/>
																			<%} %>
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
				
				<td class="td-container-main-tool" width="14%">
					<jsp:include page="/include/apatoolbar.jsp">
						<jsp:param value="<%=ip%>" name="ipaddress" />
						<jsp:param value="<%=id%>" name="id" />
						<jsp:param value="<%=flag%>" name="flag" />
						<jsp:param value="middleware" name="type" />
						<jsp:param value="apache" name="subtype" />
					</jsp:include>
				</td>
			</tr>
		</table> 
	</form>
</body>
</HTML>