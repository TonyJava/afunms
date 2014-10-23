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
<%@page import="com.afunms.application.manage.WasManager"%>
<%@page import="com.afunms.application.wasmonitor.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>

<%
	String runmodel = PollingEngine.getCollectwebflag();
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");
	HashMap hm = null;
	Hashtable wasIpaddressData = null;
  	WasConfig vo  = (WasConfig)request.getAttribute("was");
	if("0".equals(runmodel)){
   		  //采集与访问是集成模式
		  Hashtable wasHashtables = (Hashtable)com.afunms.common.util.ShareData.getWasdata();
		  wasIpaddressData = (Hashtable)wasHashtables.get(vo.getIpaddress());
		  if(wasIpaddressData != null){
		  	Hashtable cacheHashtable = (Hashtable)wasIpaddressData.get("extension7hst");
		  	hm = CommonUtil.converHashTableToHashMap(cacheHashtable); 
		  }
  	}else{
  		 //采集与访问分离模式
  		 GetWasInfo wasconf = new GetWasInfo();
  		 try{
		 	hm = wasconf.executeQueryHashMap(vo.getIpaddress(),"wascache");
	 	 }catch(Exception e){
		 	e.printStackTrace();
		 }finally{
		 	 wasconf.close();
		 }
  	} 
  	if(hm == null) {
  		hm = new HashMap();
  	}
    double wasping=0;
    WasManager wm = new WasManager();
    wasping=(double)wm.wasping(vo.getId());
	
	int percent1 = Double.valueOf(wasping).intValue();
	int percent2 = 100-percent1;
		
	String dbPage = "cache";
	String flag_1 = (String)request.getAttribute("flag");
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
	document.getElementById('was7DetailTitle-4').className='detail-data-title';
	document.getElementById('was7DetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('was7DetailTitle-4').onmouseout="this.className='detail-data-title'";
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
	<form method="post" name="mainForm">
		<input type=hidden name="orderflag">
		<input type=hidden name="id">
		<div id="loading">
			<div class="loading-indicator">
				<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif"
					width="32" height="32" style="margin-right: 8px;" align="middle" />
				Loading...
			</div>
		</div>
		<div id="loading-mask" style=""></div>
		<table id="body-container" class="body-container" border="0" id="table1" cellpadding="0" cellspacing="0"
			width=960>
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
											<jsp:include page="/topology/includejsp/middleware_was7.jsp">
												<jsp:param name="wasName" value="<%= vo.getName()%>"/> 
												<jsp:param name="wasIpaddress" value="<%= vo.getIpaddress()%>"/> 
												<jsp:param name="wasPort" value="<%= vo.getPortnum()%>"/> 
												<jsp:param name="wasFlag" value="<%= vo.getMon_flag()%>"/>
												<jsp:param name="id" value="<%=vo.getId()%>"/> 
												<jsp:param name="wasVersion" value="<%= vo.getVersion()%>"/> 
												<jsp:param name="percent1" value="<%=percent1%>"/> 
												<jsp:param name="percent2" value="<%=percent2%>"/>
											</jsp:include>
										</td>
									</tr>


									<tr>
										<td>
											<table id="application-detail-data"
												class="application-detail-data">
												<tr>
													<td class="detail-data-header">
														<%=was7DetailTitleTable%>
													</td>
												</tr>
												<tr>
													<td>
														<table class="application-detail-data-body">
															<tr>
																<td>
																	<table width="96%" align="center">
																		<tr bgcolor="#F1F1F1">
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;缓存命中率:
																			</td>
																			<%
																				if (hm.get("inMemoryCacheCount") != null) {
																			%>
																			<td width="35%"><%=hm.get("inMemoryCacheCount")%>%
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;缓存命中数:
																			</td>
																			<%
																				if (hm.get("timeoutInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=hm.get("timeoutInvalidationCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																		</tr>
																		<tr>
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;缓存读取请求总数:
																			</td>
																			<%
																				if (hm.get("maxInMemoryCacheCount") != null) {
																			%>
																			<td width="35%"><%=hm.get("maxInMemoryCacheCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal></td>

																			<td width="35%"></td>
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
					<jsp:include page="/include/was5toolbar.jsp">
						<jsp:param value="<%=vo.getId()%>" name="id" />
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
  mainForm.action = "<%=rootPath%>/was.do?action=sychronizeData&dbPage=<%=dbPage%>&id=<%=vo.getId()%>&flag=<%=flag_1%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>