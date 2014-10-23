<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
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
<%@page import="com.afunms.application.manage.*" %>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%
System.out.println("1111======================================");
  Hashtable hs = null;
  Hashtable JDBCProviderHst = null;
  Hashtable SessionManagerHst = null;
  Hashtable SystemMetricsHst = null;
  Hashtable ThreadPoolHst = null;
  Hashtable DynaCacheHst = null;
  Hashtable TransactionServiceHst = null;
  Hashtable ORBHst = null;
  Hashtable JvmHst = null;
  String rootPath = request.getContextPath();
System.out.println("3333======================================");
  WasConfig vo  = (WasConfig)request.getAttribute("was");
  hs = (Hashtable)com.afunms.common.util.ShareData.getWasdata().get("was"+":"+vo.getIpaddress());
  JvmHst = (Hashtable)hs.get("JvmHst");
  JDBCProviderHst = (Hashtable)hs.get("JDBCProviderHst");
  SessionManagerHst = (Hashtable)hs.get("SessionManagerHst");
  SystemMetricsHst = (Hashtable)hs.get("SystemMetricsHst");
  ThreadPoolHst = (Hashtable)hs.get("ThreadPoolHst");
  DynaCacheHst = (Hashtable)hs.get("DynaCacheHst");
  TransactionServiceHst = (Hashtable)hs.get("TransactionServiceHst");
  ORBHst = (Hashtable)hs.get("ORBHst");
 double wasping=0;
 WasManager wm = new WasManager();
        wasping=(double)wm.wasping(vo.getId());
		
		int percent1 = Double.valueOf(wasping).intValue();
		int percent2 = 100-percent1;
System.out.println("4444======================================");

%>
<% String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 



<script language="javascript">	

  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
  //location.href="EventqueryMgr.do";
        //msg.style.display="block";
        mainForm.action = "<%=rootPath%>/cfgfile.do?action=getcfgfile&ipaddress=<%=vo.getIpaddress()%>";
        mainForm.submit();
 });	
	
});

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

}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">


<form method="post" name="mainForm">
<input type=hidden name="orderflag">
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
							<td class="td-container-main-detail">
								<table id="container-main-detail" class="container-main-detail">
									<tr>
										<td>
											<table id="detail-content" class="detail-content">
												<tr>
													<td>
														<table id="detail-content-header" class="detail-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29"/></td>
					                                        <td class="detail-content-title">应用 >>WAS监视 >> <%=vo.getName()%> 详细信息</td>
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
				        											<table>
										               					<tr>
										     								<td width="70%" align="left" valign="top">
										     									<table style="BORDER-COLLAPSE: collapse"   cellpadding=0 rules=none width=100% align=center border=1 algin="center">
                          						<tr>
                								<td width="80%" align="left" valign="top" class=dashLeft>
                									<table style="BORDER-COLLAPSE: collapse" bordercolor=#ececec  cellpadding=0 rules=none width=100% align=center border=1 algin="center">
													<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">WAS信息1</div></td>
                    										</tr>      
                                       <tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;名称:</td>
                      											<td width="35%"><%=vo.getName()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;节点名称:</td>
                      											<td width="35%"><%=vo.getNodename()%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;IP地址:</td>
                      											<td width="35%"><%=vo.getIpaddress()%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务名称:</td>
                      											<td width="35%"><%=vo.getServername()%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口:</td>
                      											<td width="35%"><%=vo.getPortnum()%> </td>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;管理状态:</td>
                      											<td width="35%"><%=vo.getMon_flag()%> </td>
                    										</tr>                 						
                    										     										     								
                									</table>
                									   <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">JVM信息</div></td>
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>JVM已运行的时间数(秒):</td>
                      											<td width="35%"><%=JvmHst.get("UpTime")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>JVM运行时中的总内存（千字节）:</td>
                      											<td width="35%"><%=JvmHst.get("HeapSize")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>CPU使用数:</td>
                      											<td width="35%"><%=JvmHst.get("ProcessCpuUsage")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>JVM运行时中使用的内存数量（千字节）:</td>
                      											<td width="35%"><%=JvmHst.get("UsedMemory")%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>JVM运行时中的空闲内存（千字节）:</td>
                      											<td width="35%"><%=JvmHst.get("FreeMemory")%> </td>
                    										</tr>                       						
                                                    </table>
                                                      <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">JDBC信息</div></td>
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>池中的空闲连接数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("FreePoolSize")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>使用连接的平均时间（毫秒）:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("UseTime")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>高速缓存已满而废弃的语句数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("PrepStmtCacheDiscardCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>返回到池的连接的总数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("ReturnCount")%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>分配的连接的总数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("AllocateCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>池中的连接超时数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("FaultCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>特定连接池ManagedConnection对象数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("ManagedConnectionCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>等待连接的平均并发线程数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("WaitingThreadCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>运行JDBC平均调用时间（毫秒）:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("JDBCTime")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>所有连接的平均使用时间百分比:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("PercentMaxed")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>允许连接之前的平均等待时间（毫秒）:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("WaitTime")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>创建连接的总数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("CreateCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>用于特定连接池的Connection对象数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("ConnectionHandleCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>池的平均使用率:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("PercentUsed")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>连接池的大小:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("PoolSize")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>已关闭的连接的总数:</td>
                      											<td width="35%"><%=JDBCProviderHst.get("CloseCount")%> </td>
                    										</tr>   
															
                        </table>
						  <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">SessionManager信息</div></td>
                    										</tr>

                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>对不再存在的会话的请求数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("ActivateNonExistSessionCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>被强制逐出高速缓存的会话对象数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("CacheDiscardCount")%> </td>
                    										</tr>
															<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>当前存活的会话总数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("LiveCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>平均会话生存期（毫秒）:</td>
                      											<td width="35%"><%=SessionManagerHst.get("LifeTime")%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>前一个和当前访问时间戳记的时间之差（毫秒）:</td>
                      											<td width="35%"><%=SessionManagerHst.get("TimeSinceLastActivated")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>写到持久性存储的会话数据大:</td>
                      											<td width="35%"><%=SessionManagerHst.get("ExternalWriteSize")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>创建的会话数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("CreateCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>仅适用于AllowOverflow=false的内存中的会话:</td>
                      											<td width="35%"><%=SessionManagerHst.get("NoRoomForNewSessionCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>Session数据大小:</td>
                      											<td width="35%"><%=SessionManagerHst.get("SessionObjectSize")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>从持久性存储写会话数据花费的时间（毫秒）:</td>
                      											<td width="35%"><%=SessionManagerHst.get("ExternalWriteTime")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>从持久性存储读取的会话数据大小:</td>
                      											<td width="35%"><%=SessionManagerHst.get("ExternalReadSize")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>中断的HTTP会话亲缘关系数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("AffinityBreakCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>平均会话生存期（毫秒）:</td>
                      											<td width="35%"><%=SessionManagerHst.get("InvalidateCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>超时失效的会话数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("TimeoutInvalidationCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>请求当前访问的会话总数:</td>
                      											<td width="35%"><%=SessionManagerHst.get("ActiveCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>从持久性存储读会话数据花费的时间（毫秒）:</td>
                      											<td width="35%"><%=SessionManagerHst.get("ExternalReadTime")%> </td>
                    										</tr>   
													</table>
													 <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">SystemMetrics信息</div></td>
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>空闲内存的快照（KB）:</td>
                      											<td width="35%"><%=SystemMetricsHst.get("FreeMemory")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>自服务器启动以来的CPU平均使用率:</td>
                      											<td width="35%"><%=SystemMetricsHst.get("CPUUsageSinceServerStarted")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>自上次查询以来的平均CPU使用率:</td>
                      											<td width="35%"><%=SystemMetricsHst.get("CPUUsageSinceLastMeasurement")%> </td>
																
                    										</tr>                      						
                                                    </table>
													<table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">DynaCache信息</div></td>
                    										</tr>

                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>由此复制域中协同操作的高速缓存生成的可高速缓存对象请求的数目:</td>
                      											<td width="35%"><%=DynaCacheHst.get("DistributedRequestCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>内存内高速缓存条目的当前数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("InMemoryCacheEntryCount")%> </td>
                    										</tr>
															<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>内存和磁盘中当前使用的高速缓存条目数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("InMemoryAndDiskCacheEntryCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>由磁盘满足的可高速缓存对象请求的数目:</td>
                      											<td width="35%"><%=DynaCacheHst.get("HitsOnDiskCount")%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>导致从内存除去条目的显式失效数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("ExplicitMemoryInvalidationCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>超时到期从内存和磁盘除去的高速缓存条目数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("TimeoutInvalidationCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>由此应用程序服务器上运行的应用程序所生成的可高速缓存对象请求的数目:</td>
                      											<td width="35%"><%=DynaCacheHst.get("ClientRequestCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>高速缓存未能满足的可高速缓存对象请求的数目:</td>
                      											<td width="35%"><%=DynaCacheHst.get("MissCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>最近最少使用的（LRU）算法从内存中除去的高速缓存条目数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("LruInvalidationCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>程序化地或通过高速缓存策略本地生成的显式失效数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("LocalExplicitInvalidationCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>显式无效数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("ExplicitInvalidationCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>内存内高速缓存条目的最大数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("MaxInMemoryCacheEntryCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>导致从磁盘除去条目的显式失效数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("ExplicitDiskInvalidationCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>由此复制域中其它JVM满足的可高速缓存对象请求的数目:</td>
                      											<td width="35%"><%=DynaCacheHst.get("RemoteHitCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>由内存满足的可高速缓存对象请求的数目:</td>
                      											<td width="35%"><%=DynaCacheHst.get("HitsInMemoryCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>从协同操作的动态高速缓存接收到的高速缓存条目数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("RemoteCreationCount")%> </td>
                    										</tr> 
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>从此复制域中协同操作的JVM接收到的显式失效数:</td>
                      											<td width="35%"><%=DynaCacheHst.get("RemoteExplicitInvalidationCount")%> </td>	
                    										</tr>
                                                        </table>
                                                <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">TransactionService信息</div></td>
                    										</tr>

                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>全局事务提交的平均持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalCommitTime")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>在服务器上已开始的本地事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalBegunCount")%> </td>
                    										</tr>
															<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>为优化而转换成单阶段的全局事务总数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("OptimizationCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>全局事务的平均持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalTranTime")%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>准备全局事务的平均持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalPrepareTime")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>全局事务的平均before_completion持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalBeforeCompletionTime")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>本地事务提交的平均持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalCommitTime")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>回滚的本地事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalRolledbackCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>已提交的全局事务的个数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("CommittedCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>并发活动的全局事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("ActiveCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>回滚的全局事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("RolledbackCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>已提交的本地事务的个数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalCommittedCount")%> </td>
                    										</tr>   
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>服务器上涉及的全局事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalInvolvedCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>全局事务的平均持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalTranTime")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>超时的全局事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalTimeoutCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>超时的本地事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalTimeoutCount")%> </td>
                    										</tr> 
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>在服务器上开始的全局事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("GlobalBegunCount")%> </td>
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>并发活动的本地事务数:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalActiveCount")%> </td>
                    										</tr>
															<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>本地事务的before_completion平均持续时间:</td>
                      											<td width="35%"><%=TransactionServiceHst.get("LocalBeforeCompletionTime")%> </td>
                    										</tr>
												</table>
												 <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                                      <tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div class="txtGlobalBigBold">ORB信息</div></td>
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>由对象请求代理程序并发处理的请求数:</td>
                      											<td width="35%"><%=ORBHst.get("ConcurrentRequestCount")%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>发送到对象请求代理程序的请求总数:</td>
                      											<td width="35%"><%=ORBHst.get("RequestCount")%> </td>
                    										</tr>
                    										<tr>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>查找对象引用花费的时间量（毫秒）:</td>
                      											<td width="35%"><%=ORBHst.get("LookupTime")%> </td>
																
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
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
										  <td>&rdquo;</td>
										</tr>
										 </table>
										  <td>=</td>
										</tr>
										 </table>
							
</form>
</BODY>
</HTML>