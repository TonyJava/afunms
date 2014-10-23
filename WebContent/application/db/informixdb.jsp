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
		String rootPath = request.getContextPath();
		DBVo vo = (DBVo) request.getAttribute("db");
		String id = (String) request.getAttribute("id");
		String _flag = (String) request.getAttribute("flag");
		String myip = vo.getIpAddress();
		String myport = vo.getPort();
		String myUser = vo.getUser();
		String myPassword = EncryptUtil.decode(vo.getPassword());
		String mysid = "";
		String dbPage = "informixdb";
		String dbType = "informix";
		Hashtable max = (Hashtable) request.getAttribute("max");
		Hashtable imgurl = (Hashtable) request.getAttribute("imgurl");
		String chart1 = (String) request.getAttribute("chart1");
		String dbtye = (String) request.getAttribute("dbtye");
		String managed = (String) request.getAttribute("managed");
		String runstr = (String) request.getAttribute("runstr");

		Hashtable dbinfo = new Hashtable();
		dbinfo = (Hashtable) request.getAttribute("dbValue");

		ArrayList dbspaces = new ArrayList();
		ArrayList dblog = new ArrayList();
		ArrayList dbdatabase = new ArrayList();

		Hashtable databaseinfo = new Hashtable();

		String name = "";
		String dbserver = "";
		String createuser = "";
		String createtime = "";
		if (dbinfo != null) {
			dbdatabase = (ArrayList) dbinfo.get("databaselist");//数据库基本信息
			dbspaces = (ArrayList) dbinfo.get("informixspaces");//数据库空间信息
			dblog = (ArrayList) dbinfo.get("informixlog");//数据库日志信息

			if (dbdatabase != null && dbdatabase.size() > 0) {
				databaseinfo = (Hashtable) dbdatabase.get(0);
				name = (String) databaseinfo.get("dbname");
				dbserver = (String) databaseinfo.get("dbserver");
				createuser = (String) databaseinfo.get("createuser");
				createtime = (String) databaseinfo.get("createtime");
			}
		}
		double avgpingcon = (Double) request.getAttribute("avgpingcon");
		int percent1 = Double.valueOf(avgpingcon).intValue();
		int percent2 = 100 - percent1;

		String picip = CommonUtil.doip(myip);

		//生成当天平均连通率图形
		CreatePiePicture _cpp = new CreatePiePicture();
		_cpp.createAvgPingPic(picip, avgpingcon);

		StringBuffer dataStr2 = new StringBuffer();
		dataStr2.append("连通;").append(Math.round(avgpingcon)).append(
				";false;7CFC00\\n");
		dataStr2.append("未连通;").append(100 - Math.round(avgpingcon))
				.append(";false;FF0000\\n");
		String avgdata = dataStr2.toString();
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
%>
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
	document.getElementById('inforDetailTitle-1').className='detail-data-title';
	document.getElementById('inforDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('inforDetailTitle-1').onmouseout="this.className='detail-data-title'";
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

			<table id="body-container" class="body-container"  height="100%">
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
															<jsp:include page="/topology/includejsp/db_informix.jsp" >
																<jsp:param name="dbtye" value="<%=dbtye %>"/>
																<jsp:param name="managed" value="<%=managed %>"/>
																<jsp:param name="runstr" value="<%=runstr %>"/>
																<jsp:param name="ipAddress" value="<%=vo.getIpAddress() %>"/>
																<jsp:param name="name" value="<%=name %>"/>
																<jsp:param name="dbserver" value="<%=dbserver %>"/>
																<jsp:param name="createuser" value="<%=createuser %>"/>
																<jsp:param name="createuser" value="<%=createuser %>"/>
																<jsp:param name="createtime" value="<%=createtime %>"/>
																<jsp:param name="picip" value="<%=picip %>"/>
																<jsp:param name="avgdata" value="<%=avgdata %>"/> 
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
															<%=inforDetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<table class="application-detail-data-body">
																<tr>
																	<td colspan="6">
																		<table width="100%" border="0" cellpadding="0"
																			cellspacing="1">

																			<tr align="left" bgcolor="#ECECEC">
																				<td height="28">
																					<b>&nbsp;Informix数据库日志信息&nbsp;&nbsp;</b>
																				</td>
																			</tr>
																			<tr bgcolor="#ECECEC">
																				<td align=center>

																					<table width="90%" border="0" cellpadding="3"
																						cellspacing="1" bgcolor="#FFFFFF">
																						<tr bgcolor="397dbd" height=28>
																							<td width="6%"
																								class="application-detail-data-body-title">
																								<strong>文件ID</strong>
																							</td>
																							<td width="12%"
																								class="application-detail-data-body-title">
																								<strong>日志文件的页数</strong>
																							</td>
																							<td width="16%"
																								class="application-detail-data-body-title">
																								<strong>日志文件已用的页数</strong>
																							</td>
																							<td width="9%"
																								class="application-detail-data-body-title">
																								<strong>是否被使用</strong>
																							</td>
																							<td width="12%"
																								class="application-detail-data-body-title">
																								<strong>是否是当前文件</strong>
																							</td>
																							<td width="12%"
																								class="application-detail-data-body-title">
																								<strong>是否已经备份过</strong>
																							</td>
																							<td width="17%"
																								class="application-detail-data-body-title">
																								<strong>是否已置于备份磁盘上</strong>
																							</td>
																							<td width="17%"
																								class="application-detail-data-body-title">
																								<strong>是否为临时日志文件</strong>
																							</td>
																						</tr>
																						<%
																							if (dblog != null && dblog.size() > 0) {
																									for (int i = 0; i < dblog.size(); i++) {
																										Hashtable tablesVO = (Hashtable) dblog.get(i);
																						%>
																						<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
																							height=28>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("uniqid")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("size")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("used")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("is_used")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("is_current")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("is_backed_up")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("is_archived")%></td>
																							<td class="application-detail-data-body-list"><%=tablesVO.get("is_temp")%></td>
																						</tr>
																						<%
																							}
																								}
																						%>
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
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td valign=top width=15%>
						<jsp:include page="/include/dbtoolbar.jsp">
							<jsp:param value="<%=myip%>" name="myip" />
							<jsp:param value="<%=myport%>" name="myport" />
							<jsp:param value="<%=myUser%>" name="myUser" />
							<jsp:param value="<%=myPassword%>" name="myPassword" />
							<jsp:param value="<%=mysid%>" name="sid" />
							<jsp:param value="<%=id%>" name="id" />
							<jsp:param value="<%=dbPage%>" name="dbPage" />
							<jsp:param value="<%=dbType%>" name="dbType" />
							<jsp:param value="informix" name="subtype" />
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
  mainForm.action = "<%=rootPath%>/informix.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&myip=<%=myip %>&myport=<%=myport %>&myUser=<%=myUser %>&myPassword=<%=myPassword %>&sid=<%=mysid %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>