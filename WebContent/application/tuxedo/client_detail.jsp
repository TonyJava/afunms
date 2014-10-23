<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.application.model.TuxedoConfig"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%
	 String rootPath = request.getContextPath();
	 String menuTable = (String)request.getAttribute("menuTable");
	 List clientList = (List)request.getAttribute("clientList");
	 TuxedoConfig tuxedoConfig = (TuxedoConfig)request.getAttribute("tuxedoConfig");
	 if(tuxedoConfig == null){
	     tuxedoConfig = new TuxedoConfig();
	 }
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

  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
      
	
});
  
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
	document.getElementById('tuxedoDetailTitle-4').className='detail-data-title';
	document.getElementById('tuxedoDetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('tuxedoDetailTitle-4').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=tuxedoConfig.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}

</script>
</head>
<body id="body" class="body" onload="initmenu();">
<form method="post" name="mainForm">
<input type=hidden name="id">
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
<table  id="body-container" class="body-container" border="0" id="table1" cellpadding="0" cellspacing="0" width=960>
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
											 <jsp:include page="/topology/includejsp/middleware_tuxedo.jsp">
												 <jsp:param name="name" value="<%= tuxedoConfig.getName()%>"/>
												 <jsp:param name="address" value="<%= tuxedoConfig.getIpAddress()%>"/>
												 <jsp:param name="flag" value="<%= tuxedoConfig.getMon_flag()%>"/>
												 <jsp:param name="status" value="<%= tuxedoConfig.getStatus()%>"/> 
											 </jsp:include>
										</td>
									</tr> 
									<tr>
										<td>
											<table id="application-detail-data" class="application-detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=tuxedoDetailTitleTable%>
											    	</td>
											   	</tr>
		                                       	<tr>
											    	<td>
											    		<table class="application-detail-data-body">
											    			<tr>
											    				<td>
											    					<table>
											    						<tr height="28">
														          			<td class="application-detail-data-body-title"><input type="checkbox" name="checkall" id="checkall">序号</td>
																			<td class="application-detail-data-body-title">已经登录的客户端机器的LMID</td>
																		 	<td class="application-detail-data-body-title">用户名</td>
																			<td class="application-detail-data-body-title">客户端名</td>
																			<td class="application-detail-data-body-title">客户端连接后经过的时间</td>
																			<td class="application-detail-data-body-title">客户端状态</td>
																			<td class="application-detail-data-body-title">启动/提交/中断的交易数</td>
														           		</tr>
														           		
														           		<% 
														           			if(clientList != null && clientList.size()>0 ){
														           				int num = 0;
														           				for(int i = 0 ; i < clientList.size(); i++ ){
														           					Hashtable per_hashtable = (Hashtable)clientList.get(i);
														           					if(per_hashtable!=null){
														           						num++;
														           				%>
														           					<tr height="28">
																	          			<td class="application-detail-data-body-list"><input type="checkbox" name="checkall" id="checkall"><%=num%></td>
																						<td class="application-detail-data-body-list"><%=per_hashtable.get("LMID")%></td>
																					 	<td class="application-detail-data-body-list"><%=per_hashtable.get("UserName")%></td>
																						<td class="application-detail-data-body-list"><%=per_hashtable.get("ClientName")%></td>
																						<td class="application-detail-data-body-list"><%=per_hashtable.get("Time")%></td>
																						<td class="application-detail-data-body-list"><%=per_hashtable.get("Status")%></td>
																						<td class="application-detail-data-body-list"><%=per_hashtable.get("Bca")%></td>
																	           		</tr>
														           				<%
														           					}
														           				}
														           			}
														           			
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
</form>
</BODY>
</HTML>