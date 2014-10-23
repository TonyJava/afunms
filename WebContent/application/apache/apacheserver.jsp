<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="com.afunms.alarm.util.AlarmConstant"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.detail.service.apacheInfo.ApacheInfoService"%>
<%
	String runmodel = PollingEngine.getCollectwebflag(); 
	String rootPath = request.getContextPath();
	ApacheConfig vo=(ApacheConfig)request.getAttribute("vo");
    String ip="";
    int id=0;
	Hashtable apache_ht = null;
	
	if(vo!=null){
		ip=vo.getIpaddress();
		id=vo.getId();
	}
	if("0".equals(runmodel)){
	  	//采集与访问是集成模式
		apache_ht = (Hashtable)com.afunms.common.util.ShareData.getApachedata().get("apache"+":"+ip);
	}else{  
		//采集与访问是分离模式
		ApacheInfoService apacheInfoService = new ApacheInfoService();
		apache_ht = apacheInfoService.getApacheDataHashtable(vo.getId()+"");
	}
	if(apache_ht == null)apache_ht = new Hashtable();
	String status_str = (String)apache_ht.get("status");
	Integer status = 0;
	if(status_str!=null){
		status = Integer.valueOf(status_str);
	}
	String connrate = request.getAttribute("connrate").toString();
	connrate = connrate.replaceAll("%","");
	int percent1 = Double.valueOf(connrate).intValue();
	int percent2 = 100-percent1;
    String menuTable = (String)request.getAttribute("menuTable");
    status= SystemSnap.getNodeStatus(id+"",AlarmConstant.TYPE_MIDDLEWARE,"apache");
    %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>


<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript">

function setClass(){
	document.getElementById('apacheDetailTitle-1').className='detail-data-title';
	document.getElementById('apacheDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('apacheDetailTitle-1').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
</head>
<body id="body" class="body" onload="setClass();">

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
																<td width="80%" align="left" class=dashLeft>
																	<table class="application-detail-data-body">
																		<tr bgcolor="#FFFFFF"    height="28">
																			<td align=center class="application-detail-data-body-list">
																				<%
																					if (apache_ht.get("pre") != null) {
																				%>
																				<%=apache_ht.get("pre")%>
																				<%
																					}
																				%>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<font color="#ff0000">*</font>&nbsp;&nbsp;注释：&nbsp;&nbsp;_：等待连结中&nbsp;&nbsp;S：启动中&nbsp;&nbsp;R：正在读取要求&nbsp;&nbsp;W：正在送出回应&nbsp;&nbsp;K：处于保持联机的状态&nbsp;&nbsp;D：正在查找DNS&nbsp;&nbsp;C：正在关闭连结&nbsp;
																			</td>
																		</tr>
																		<tr>
																			<td>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;L：正在写入记录文件&nbsp;&nbsp;G：进入正常结束程序中&nbsp;&nbsp;I：处理闲置&nbsp;&nbsp;.：尚无此程序
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
														</TABLE>
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