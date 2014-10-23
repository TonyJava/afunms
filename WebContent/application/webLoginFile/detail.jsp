<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.polling.base.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.inform.util.SystemSnap" %>
<%@page import="java.util.*"%>

<%
	String myflag = (String) request.getAttribute("flag");
	Integer myId = (Integer) request.getAttribute("id");
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");

	List urllist = (List) request.getAttribute("urllist");
	webloginConfig queryconf = (webloginConfig) request.getAttribute("initconf");


	String flag_1 = (String) request.getAttribute("flag");

	int alarmlevel = 0;
	Node node = (Node) PollingEngine.getInstance().getWebLoginByID(
			queryconf.getId());
	String alarmmessage = "";
	if (node != null) {
		alarmlevel = SystemSnap.getNodeStatus(node); 
		List alarmlist = node.getAlarmMessage();
		if (alarmlist != null && alarmlist.size() > 0) {
			for (int k = 0; k < alarmlist.size(); k++) {
				alarmmessage = alarmmessage
						+ alarmlist.get(k).toString();
			}
		}
	}

	SupperDao supperdao = new SupperDao();
	Supper supper = null;
	String suppername = "";
	try {
		supper = (Supper) supperdao.findByID(queryconf.getSupperid()
				+ "");
		if (supper != null)
			suppername = supper.getSu_name() + "("
					+ supper.getSu_dept() + ")";
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		supperdao.close();
	}
	//System.out.println("====555==========1===");
	
	
	
	
	
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
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

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

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
	document.getElementById('webLoginDetailTitle-0').className='detail-data-title';
	document.getElementById('webLoginDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('webLoginDetailTitle-0').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
		
}

function show_graph(){
      mainForm.action = "<%=rootPath%>/webLogin.do?action=detail";
      mainForm.submit();
} 


</script>


	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" id="flag" name="flag" value="<%=flag_1%>">
			<input type="hidden" id="id" name="id" value="<%=myId%>">
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
					
					<td class="td-container-main-detail">
						<table id="container-main-detail" class="container-main-detail">
							<tr>
								<td>
									<table id="detail-content" class="detail-content">
										<tr>
											<td>
												<table id="detail-content-header"
													class="detail-content-header">
													<tr>
														<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
														<td class="detail-content-title">
															WEB虚拟登陆详细信息
														</td>
														<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="detail-content-body" class="detail-content-body">
													<tr>
														<td>
															<table >
																<tr>
																	<td>

																		<table>
																			
																			<tr>
																				<td>
																					<table cellspacing="10" border=0 >
																						<tr>
																							<td width="50%" align="center" valign=top
																								cellspacing="0" border=0>

																								<table style="BORDER-COLLAPSE: collapse"
																									bordercolor=#cedefa cellpadding=0 rules=none
																									width=100% align=center border=1 algin="center">
																									<tr bgcolor="#F1F1F1">
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;名称:
																										</td>
																										<td width="70%"><%=queryconf.getAlias()%></td>
																									</tr>
																									<tr>
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;类型:
																										</td>
																										<td width="70%">
																											WEB虚拟登陆监视
																										</td>
																									</tr>
																									<tr bgcolor="#F1F1F1">
																										<td width="30%" height="26" align="left"
																											nowrap>
																											&nbsp;状态:
																										</td>
																										<td width="70%">
																											<img
																												src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmlevel)%>"
																												border="0" alt=<%=alarmmessage%>>
																										</td>
																									</tr>
																									<tr>
																										<td height="29" align="left">
																											&nbsp;登陆URL:
																										</td>
																										<td>
																											<a href="<%=queryconf.getUrl()%>"
																												target="_blank"><%=queryconf.getUrl()%></a>
																										</td>
																									</tr>
																									<tr bgcolor="#F1F1F1">
																										<td height="29" align="left">
																											&nbsp;退出URL:
																										</td>
																										<td>
																											<a href="<%=queryconf.getOuturl()%>"
																												target="_blank"><%=queryconf.getOuturl()%></a>
																										</td>
																									</tr>
																									<tr >
																										<td width="30%" height="26" align=left nowrap>
																											&nbsp;用户名:
																										</td>
																										<td width="70%"><%=queryconf.getUser_name()%></td>
																									</tr>
																									<tr bgcolor="#F1F1F1">
																										<td height="29" align="left">
																											&nbsp;上次轮询时间:
																										</td>
																										<td><%=request.getAttribute("lasttime")%></td>
																									</tr>
																									<tr >
																										<td width="30%" height="26" align=left>
																											&nbsp;下次轮询时间:
																										</td>
																										<td width="70%"><%=request.getAttribute("nexttime")%></td>
																									</tr>
																									<tr bgcolor="#F1F1F1">
																										<td height="29" class=txtGlobal valign=center
																											nowrap>
																											&nbsp;供应商:
																										</td>
																										<td>
																											<%
																												if (supper != null) {
																											%>
																											<a href="#" style="cursor: hand"
																												onclick="window.showModalDialog('<%=rootPath%>/supper.do?action=read&id=<%=supper.getSu_id()%>',window,',dialogHeight:400px;dialogWidth:600px')"><%=suppername%></a>
																											<%
																												}
																											%>
																										</td>
																									</tr>

																								</table>
																							</td>
																							<td width="50%" align="center">
																								<table width="100%" cellspacing="0"
																									cellpadding="0" align="center">
																									<tr>
																										<td width="100%" align="center">
																											<div id="flashcontent1">
																												<strong>You need to upgrade your
																													Flash Player</strong>
																											</div>
																											<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_area.swf?title=连通率&tablename=weblogin_ping<%=queryconf.getId()%>", "common_area", "400", "250", "8", "#ffffff");
																											so.write("flashcontent1");
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
										<tr>
											<td>
												<table id="detail-content-footer"
													class="detail-content-footer">
													<tr>
														<td>
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
																	<td></td>
																	<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
							<tr>
								<td>
									<table id="detail-data" class="detail-data">
										<tr>
											<td class="detail-data-header">
												<%=webLoginDetailTitleTable%>
											</td>
										</tr>
										<tr>
											<td>
												<table class="detail-data-body">
													<tr>
														<td align=center valign=top>
															<br>
															<table cellpadding="0" cellspacing="0" width=48%
																align=center>
																<tr>
																	<td width="48%" align="center">
																		<table width="100%" cellspacing="0" cellpadding="0"
																			align="center">
																			<tr>
																				<td width="100%" align="center">
																					<div id="flashcontent2">
																						<strong>You need to upgrade your Flash
																							Player</strong>
																					</div>
																					<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?title=连通率&tablename=weblogin_ping<%=queryconf.getId()%>", "common_line", "400", "250", "8", "#ffffff");
																											so.write("flashcontent2");
																										</script>
																				</td>
																			</tr>
																		</table>
																	</td>
																	<td width="48%" align="center">
																		<table width="100%" cellspacing="0" cellpadding="0"
																			align="center">
																			<tr>
																				<td width="100%" align="center">
																					<div id="flashcontent3">
																						<strong>You need to upgrade your Flash
																							Player</strong>
																					</div>
																					<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?title=响应时间(ms)&tablename=weblogin_response<%=queryconf.getId()%>", "Web_time_area", "400", "250", "8", "#ffffff");
																											so.write("flashcontent3");
																										</script>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
															<br>
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
						<jsp:include page="/include/weblogintoolbar.jsp">
																<jsp:param value="<%=queryconf.getId() %>" name="id" />
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
  mainForm.action = "<%=rootPath%>/weblogin.do?action=sychronizeData&id=<%=queryconf.getId()%>&flag=<%=flag_1%>&page=detail";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>