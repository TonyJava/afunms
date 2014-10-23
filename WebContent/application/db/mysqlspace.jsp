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
  String rootPath = request.getContextPath();;
  DBVo vo  = (DBVo)request.getAttribute("db");	
  String id = (String)request.getAttribute("id");
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String _flag = (String) request.getAttribute("flag");
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "mysqlspace";
  String dbType = "mysql";
		

Hashtable max = (Hashtable) request.getAttribute("max");
//Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
	double avgpingcon = (Double)request.getAttribute("avgpingcon");	
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100-percent1;
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr"); 
Vector tableinfo_v=(Vector)request.getAttribute("tableinfo_v");
Vector tableinfo_v1=(Vector)request.getAttribute("tableinfo_v1");
Vector tableinfo_v2=(Vector)request.getAttribute("tableinfo_v2");
Vector tableinfo_v3=(Vector)request.getAttribute("tableinfo_v3");
Vector tableinfo_v4=(Vector)request.getAttribute("tableinfo_v4");
Vector tableinfo_v5=(Vector)request.getAttribute("tableinfo_v5");
Vector tableinfo_v6=(Vector)request.getAttribute("tableinfo_v6");
String versionvalue=(String)request.getAttribute("versionvalue");
String vercommaval=(String)request.getAttribute("vercommaval");
String vercomosval=(String)request.getAttribute("vercomosval");
String verwaittimeval=(String)request.getAttribute("verwaittimeval");

String basePath=(String)request.getAttribute("basePath");
String dataPath=(String)request.getAttribute("dataPath");
String logerrorPath=(String)request.getAttribute("logerrorPath");
String version=(String)request.getAttribute("version");
String hostOS=(String)request.getAttribute("hostOS");

Vector val=(Vector)request.getAttribute("Val");
	
	String picip = CommonUtil.doip(myip);
     			  	   
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
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
	document.getElementById('mysqlDetailTitle-3').className='detail-data-title';
	document.getElementById('mysqlDetailTitle-3').onmouseover="this.className='detail-data-title'";
	document.getElementById('mysqlDetailTitle-3').onmouseout="this.className='detail-data-title'";
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
				$("#cpu").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>cpu.png?nowtime="+(new Date()), alt: "当前CPU利用率" }); 
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
		<form id="mainForm" method="post" name="mainForm">
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
								<td class="td-container-main-application-detail">
									<table id="container-main-application-detail"
										class="container-main-application-detail">
										<tr>
											<td>
												<table class="container-main-application-detail">
													<tr>
														<td> 
															<jsp:include page="/topology/includejsp/db_mysql.jsp">
																<jsp:param name="dbName" value="<%=vo.getDbName() %>"/>
																<jsp:param name="alias" value="<%=vo.getAlias() %>"/>
																<jsp:param name="ipAddress" value="<%=vo.getIpAddress() %>"/>
																<jsp:param name="port" value="<%=vo.getPort() %>"/>
																<jsp:param name="dbtye" value="<%=dbtye %>"/>
																<jsp:param name="managed" value="<%=managed %>"/>
																<jsp:param name="runstr" value="<%=runstr %>"/>
																<jsp:param name="version" value="<%=version %>"/>
																<jsp:param name="hostOS" value="<%=hostOS %>"/>
																<jsp:param name="basePath" value="<%=basePath %>"/>
																<jsp:param name="dataPath" value="<%=dataPath %>"/>
																<jsp:param name="logerrorPath" value="<%=logerrorPath %>"/>
																<jsp:param name="picip" value="<%=picip %>"/>
																<jsp:param name="pingavg" value="<%=avgpingcon %>"/>   
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
															<%=mysqlDetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<table class="application-detail-data-body">
																<tr>
																	<td>
																		<table cellspacing="1" cellpadding="0" width="100%">
																			<tr>
																				<td height="28" align="center" bgcolor="#ECECEC"
																					colspan=6>
																					&nbsp;&nbsp;
																					<b>数据库性能信息</b>
																				</td>
																			</tr>

																			<%   
            	if(val != null && val.size()>0){
            		for(int i=0;i<val.size();i++){
            			Hashtable return_value = (Hashtable)val.get(i);
            			if(return_value != null && return_value.size()>0){
            				String name=return_value.get("variable_name").toString();
            				String value=return_value.get("value").toString();
            				if(name.equalsIgnoreCase("Max_used_connections"))
					{
					 name="服务器相应的最大连接数";
					}
					if(name.equalsIgnoreCase("Handler_read_first"))
					{
					 name="索引中第一条被读的次数";
					}
					if(name.equalsIgnoreCase("Handler_read_key"))
					{
					 name="根据键读一行的请求数";
					}
					if(name.equalsIgnoreCase("Handler_read_next"))
					{
					 name="按照键顺序读下一行的请求数";
					}
					if(name.equalsIgnoreCase("Handler_read_prev"))
					{
					 name="按照键顺序读前一行的请求数";
					}
					if(name.equalsIgnoreCase("Handler_read_rnd"))
					{
					 name="H根据固定位置读一行的请求数";
					}
					if(name.equalsIgnoreCase("Handler_read_rnd_next"))
					{
					 name="在数据文件中读下一行的请求数";
					}
					if(name.equalsIgnoreCase("Open_tables"))
					{
					 name="当前打开的表的数量";
					}
					if(name.equalsIgnoreCase("Opened_tables"))
					{
					 name="已经打开的表的数量";
					}
					if(name.equalsIgnoreCase("Threads_cached"))
					{
					 name="线程缓存内的线程的数量";
					}
					if(name.equalsIgnoreCase("Threads_connected"))
					{
					 name="当前打开的连接的数量";
					}
					if(name.equalsIgnoreCase("Threads_created"))
					{
					 name="创建用来处理连接的线程数";
					}
					if(name.equalsIgnoreCase("Threads_running"))
					{
					 name="激活的非睡眠状态的线程数";
					}
					if(name.equalsIgnoreCase("Table_locks_immediate"))
					{
					 name="立即获得的表的锁的次数";
					}
					if(name.equalsIgnoreCase("Table_locks_waited"))
					{
					 name="不能立即获得的表的锁的次数";
					}
					if(name.equalsIgnoreCase("Key_read_requests"))
					{
					 name="从缓存读键的数据块的请求数";
					}
					if(name.equalsIgnoreCase("Key_reads"))
					{
					 name="从硬盘读取键的数据块的次数";
					}
					if(name.equalsIgnoreCase("log_slow_queries"))
					{
					 name="是否记录慢查询";
					}
					if(name.equalsIgnoreCase("slow_launch_time"))
					{
					 name="创建线程的时间超过该秒数，服务器增加Slow_launch_threads状态变量";
					}
            
             %>
																			<tr height=28>
																				<td width=10%
																					class="application-detail-data-body-list">
																					&nbsp;
																				</td>
																				<td align=left width=50%
																					class="application-detail-data-body-list">
																					&nbsp;<%=name%></td>
																				<td align=left
																					class="application-detail-data-body-list">
																					&nbsp;<%=value%></td>
																				<td width=10%
																					class="application-detail-data-body-list">
																					&nbsp;
																				</td>
																			</tr>
																			<%			}
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
					<td  width=15% valign=top>
															<jsp:include page="/include/dbtoolbar.jsp">
																<jsp:param value="<%=myip %>" name="myip" />
																<jsp:param value="<%=myport %>" name="myport" />
																<jsp:param value="<%=myUser %>" name="myUser" />
																<jsp:param value="<%=myPassword %>" name="myPassword" />
																<jsp:param value="<%=mysid  %>" name="sid" />
																<jsp:param value="<%=id %>" name="id" />
																<jsp:param value="<%=dbPage %>" name="dbPage" />
																<jsp:param value="<%=dbType %>" name="dbType" />
																<jsp:param value="mysql" name="subtype"/>
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
  mainForm.action = "<%=rootPath%>/mysql.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&myip=<%=myip %>&myport=<%=myport %>&myUser=<%=myUser %>&myPassword=<%=myPassword %>&sid=<%=mysid %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>