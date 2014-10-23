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
<%@page import="com.afunms.application.wasmonitor.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.application.manage.WasManager"%>

<%

	 String rootPath = request.getContextPath();
	 String menuTable = (String)request.getAttribute("menuTable");
	 Hashtable hs = null;
  Hashtable JDBCProviderHst = null;
  Hashtable SessionManagerHst = null;
  Hashtable SystemMetricsHst = null;
  Hashtable ThreadPoolHst = null;
  Hashtable DynaCacheHst = null;
  Hashtable TransactionServiceHst = null;
  Hashtable ORBHst = null;
  Hashtable JvmHst = null;
  WasConfig vo  = (WasConfig)request.getAttribute("was");
  hs = (Hashtable)com.afunms.common.util.ShareData.getWasdata().get("was"+":"+vo.getIpaddress());
  if(hs == null)hs = new Hashtable();
  JvmHst = (Hashtable)hs.get("JvmHst");
  if(JvmHst == null)JvmHst = new Hashtable();
  JDBCProviderHst = (Hashtable)hs.get("JDBCProviderHst");
  if(JDBCProviderHst == null)JDBCProviderHst = new Hashtable();
  SessionManagerHst = (Hashtable)hs.get("SessionManagerHst");
  if(SessionManagerHst == null)SessionManagerHst = new Hashtable();
  SystemMetricsHst = (Hashtable)hs.get("SystemMetricsHst");
  if(SystemMetricsHst == null)SystemMetricsHst = new Hashtable();
  ThreadPoolHst = (Hashtable)hs.get("ThreadPoolHst");
  if(ThreadPoolHst == null)ThreadPoolHst = new Hashtable();
  DynaCacheHst = (Hashtable)hs.get("DynaCacheHst");
  if(DynaCacheHst == null)DynaCacheHst = new Hashtable();
  TransactionServiceHst = (Hashtable)hs.get("TransactionServiceHst");
  if(TransactionServiceHst == null)TransactionServiceHst = new Hashtable();
  ORBHst = (Hashtable)hs.get("ORBHst");
  if(ORBHst == null)ORBHst = new Hashtable();
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
	document.getElementById('wasDetailTitle-4').className='detail-data-title';
	document.getElementById('wasDetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('wasDetailTitle-4').onmouseout="this.className='detail-data-title'";
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
											<jsp:include page="/topology/includejsp/middleware_was.jsp">
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
														<%=wasDetailTitleTable%>
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
																				&nbsp;由此复制域中协同操作的高速缓存生成的可高速缓存对象请求的数目:
																			</td>
																			<%
																				if (DynaCacheHst.get("DistributedRequestCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("DistributedRequestCount")%>
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
																				&nbsp;内存内高速缓存条目的当前数:
																			</td>
																			<%
																				if (DynaCacheHst.get("InMemoryCacheEntryCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("InMemoryCacheEntryCount")%>
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
																				&nbsp;内存和磁盘中当前使用的高速缓存条目数:
																			</td>
																			<%
																				if (DynaCacheHst.get("InMemoryAndDiskCacheEntryCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("InMemoryAndDiskCacheEntryCount")%>
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
																				&nbsp;由磁盘满足的可高速缓存对象请求的数目:
																			</td>
																			<%
																				if (DynaCacheHst.get("HitsOnDiskCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("HitsOnDiskCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																		</tr>
																		<tr bgcolor="#F1F1F1">
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;导致从内存除去条目的显式失效数:
																			</td>
																			<%
																				if (DynaCacheHst.get("ExplicitMemoryInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("ExplicitMemoryInvalidationCount")%>
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
																				&nbsp;超时到期从内存和磁盘除去的高速缓存条目数:
																			</td>
																			<%
																				if (DynaCacheHst.get("TimeoutInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("TimeoutInvalidationCount")%>
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
																				&nbsp;由此应用程序服务器上运行的应用程序所生成的可高速缓存对象请求的数目:
																			</td>
																			<%
																				if (DynaCacheHst.get("ClientRequestCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("ClientRequestCount")%>
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
																				&nbsp;高速缓存未能满足的可高速缓存对象请求的数目:
																			</td>
																			<%
																				if (DynaCacheHst.get("MissCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("MissCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																		</tr>
																		<tr bgcolor="#F1F1F1">
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;最近最少使用的（LRU）算法从内存中除去的高速缓存条目数:
																			</td>
																			<%
																				if (DynaCacheHst.get("LruInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("LruInvalidationCount")%>
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
																				&nbsp;程序化地或通过高速缓存策略本地生成的显式失效数:
																			</td>
																			<%
																				if (DynaCacheHst.get("LocalExplicitInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("LocalExplicitInvalidationCount")%>
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
																				&nbsp;显式无效数:
																			</td>
																			<%
																				if (DynaCacheHst.get("ExplicitInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("ExplicitInvalidationCount")%>
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
																				&nbsp;内存内高速缓存条目的最大数:
																			</td>
																			<%
																				if (DynaCacheHst.get("MaxInMemoryCacheEntryCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("MaxInMemoryCacheEntryCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																		</tr>
																		<tr bgcolor="#F1F1F1">
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;导致从磁盘除去条目的显式失效数:
																			</td>
																			<%
																				if (DynaCacheHst.get("ExplicitDiskInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("ExplicitDiskInvalidationCount")%>
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
																				&nbsp;由此复制域中其它JVM满足的可高速缓存对象请求的数目:
																			</td>
																			<%
																				if (DynaCacheHst.get("RemoteHitCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("RemoteHitCount")%>
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
																				&nbsp;由内存满足的可高速缓存对象请求的数目:
																			</td>
																			<%
																				if (DynaCacheHst.get("HitsInMemoryCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("HitsInMemoryCount")%>
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
																				&nbsp;从协同操作的动态高速缓存接收到的高速缓存条目数:
																			</td>
																			<%
																				if (DynaCacheHst.get("RemoteCreationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("RemoteCreationCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																		</tr>
																		<tr bgcolor="#F1F1F1">
																			<td width="15%" height="26" align="left" nowrap
																				class=txtGlobal>
																				&nbsp;从此复制域中协同操作的JVM接收到的显式失效数:
																			</td>
																			<%
																				if (DynaCacheHst.get("RemoteExplicitInvalidationCount") != null) {
																			%>
																			<td width="35%"><%=DynaCacheHst.get("RemoteExplicitInvalidationCount")%>
																			</td>
																			<%
																				} else {
																			%>
																			<td width="35%"></td>
																			<%
																				}
																			%>
																			<td></td>
																			<td></td>
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