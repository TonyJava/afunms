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
<%
  String rootPath = request.getContextPath();;
  DBVo vo  = (DBVo)request.getAttribute("db");	
  String id = (String)request.getAttribute("id");
  String _flag = (String) request.getAttribute("flag");
  String myip = vo.getIpAddress();
  String picip = CommonUtil.doip(myip);
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "db2pool"; 
  String dbType = "db2";
Hashtable returnhash = (Hashtable)request.getAttribute("returnhash");	
if(returnhash == null)returnhash = new Hashtable();	

Hashtable commonValue = (Hashtable)request.getAttribute("commonValue");	
if(commonValue == null)commonValue = new Hashtable();

String os_name = "";
String host_name = "";
String total_memory = "";
String installed_prod = "";
String prod_release = "";
String total_cpu = "";
String configured_cpu = "";

if(commonValue.containsKey("os_name")){
	os_name = (String)commonValue.get("os_name");
}
if(commonValue.containsKey("host_name")){
	host_name = (String)commonValue.get("host_name");
}
if(commonValue.containsKey("total_memory")){
	total_memory = (String)commonValue.get("total_memory");
}
if(commonValue.containsKey("installed_prod")){
	installed_prod = (String)commonValue.get("installed_prod");
}
if(commonValue.containsKey("total_memory")){
	prod_release = (String)commonValue.get("prod_release");
}
if(commonValue.containsKey("total_cpu")){
	total_cpu = (String)commonValue.get("total_cpu");
}
if(commonValue.containsKey("configured_cpu")){
	configured_cpu = (String)commonValue.get("configured_cpu");
}

Hashtable max = (Hashtable) request.getAttribute("max");
Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");     
double avgpingcon = (Double)request.getAttribute("avgpingcon");
int percent1 = Double.valueOf(avgpingcon).intValue();
int percent2 = 100-percent1;     			  	   
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
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script>
function createQueryString(){
		var user_name = encodeURI($("#user_name").val());
		var passwd = encodeURI($("#passwd").val());
		var queryString = {userName:user_name,passwd:passwd};
		return queryString;
	}
function gzmajax(){
$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/db2AjaxManager.ajax?action=ajaxUpdate_availability&id=<%=id%>&nowtime="+(new Date()),
			success:function(data){
				//$("#flashcontent00gzm").html(data.percent1+":"+data.percent2+":"+data.cpuper);
				var so = new SWFObject("<%=rootPath%>/flex/Pie_Component.swf?percent1="+data.percent1+"&percentStr1=可用&percent2="+data.percent2+"&percentStr2=不可用", "Pie_Component", "160", "160", "8", "#ffffff");
				so.write("flashcontent00");
				
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
	document.getElementById('db2DetailTitle-5').className='detail-data-title';
	document.getElementById('db2DetailTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('db2DetailTitle-5').onmouseout="this.className='detail-data-title'";
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
														
														<td valign="top">
															<jsp:include page="/topology/includejsp/db_db2.jsp">
															<jsp:param name="dbtye" value="<%=dbtye %>"/> 
															<jsp:param name="ipAddress" value="<%=vo.getIpAddress() %>"/> 
															<jsp:param name="managed" value="<%= managed%>"/> 
															<jsp:param name="runstr" value="<%= runstr%>"/>  
															
															<jsp:param name="dbName" value="<%= vo.getDbName()%>"/> 
															<jsp:param name="port" value="<%=vo.getPort() %>"/> 
															<jsp:param name="os_name" value="<%=os_name %>"/> 
															<jsp:param name="host_name" value="<%=host_name %>"/> 
															
															<jsp:param name="total_cpu" value="<%=total_cpu %>"/> 
															<jsp:param name="configured_cpu" value="<%=configured_cpu %>"/> 
															<jsp:param name="total_memory" value="<%= total_memory%>"/> 
															<jsp:param name="installed_prod" value="<%= installed_prod%>"/> 
															
															<jsp:param name="prod_release" value="<%=prod_release %>"/>   
															<jsp:param name="picip" value="<%=picip %>"/>  
															 <jsp:param name="pingAvg" value="<%=avgpingcon %>"/>  
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
															<%=db2DetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<table class="application-detail-data-body">
																<tr>
																	<td>


																		<%
																			Enumeration dbs = returnhash.keys();
																			Hashtable<String,Object> allhash = new Hashtable();
																			List poolList = new ArrayList();
																			while(dbs.hasMoreElements()){								
																				String obj = (String)dbs.nextElement();
																				allhash = (Hashtable)returnhash.get(obj);
																				poolList = (List)allhash.get("poolValue");
																				//System.out.println("poolList size === "+poolList.size());
																				List lockList = (List)allhash.get("lockValue");
																				List readList = (List)allhash.get("readValue");
											   									List writeList = (List)allhash.get("writeValue");
											
																		%>


																		<table width="100%" border="0" cellspacing="0" cellpadding="0">
																			


																			<tr bgcolor="#ECECEC">
																				<td width="76%" height="28">
																					&nbsp;读频率最高的10张表
																				</td>
																			</tr>
																			<tr>
																				<td height="28">
																					<table width="96%" align="center">
																						<tr bgcolor="#ECECEC">
																							<td align=center
																								class="application-detail-data-body-title">
																								&nbsp;
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								模式
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								表名称
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								读行数
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								写行数
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								溢出访问
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								页重组数
																							</td>
																						</tr>
																						<%
																							for(int i=0;i<readList.size();i++){
																								Hashtable ht = (Hashtable)readList.get(i);
																								String tbschema = ht.get("tbschema").toString();
																								String tbname = ht.get("tbname").toString();
																								String rows_read = ht.get("rows_read").toString();
																								String rows_written = ht.get("rows_written").toString();
																								String overflow_accesses = ht.get("overflow_accesses").toString();
																								String page_reorgs = ht.get("page_reorgs").toString();
																								
																						%>
																						<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
																							<td class="application-detail-data-body-list"><%=i+1%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=tbschema%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=tbname%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=rows_read%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=rows_written%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=overflow_accesses%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=page_reorgs%></td>

																						</tr>
																						<%}%>
																					</table>

																				</td>
																			</tr>

																			<tr bgcolor="#ECECEC">
																				<td width="76%" height="28">
																					&nbsp;写频率最高的10张表
																				</td>
																			</tr>
																			<tr>
																				<td height="28">
																					<table width="96%" align="center">
																						<tr bgcolor="#ECECEC">
																							<td align=center
																								class="application-detail-data-body-title">
																								&nbsp;
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								模式
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								表名称
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								读行数
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								写行数
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								溢出访问
																							</td>
																							<td align=center
																								class="application-detail-data-body-title">
																								页重组数
																							</td>
																						</tr>
																						<%
																							for(int i=0;i<writeList.size();i++){
																								Hashtable ht = (Hashtable)writeList.get(i);
																								String tbschema = ht.get("tbschema").toString();
																								String tbname = ht.get("tbname").toString();
																								String rows_read = ht.get("rows_read").toString();
																								String rows_written = ht.get("rows_written").toString();
																								String overflow_accesses = ht.get("overflow_accesses").toString();
																								String page_reorgs = ht.get("page_reorgs").toString();
																								
																						%>
																						<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
																							<td class="application-detail-data-body-list"><%=i+1%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=tbschema%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=tbname%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=rows_read%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=rows_written%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=overflow_accesses%></td>
																							<td align=center
																								class="application-detail-data-body-list">
																								&nbsp;<%=page_reorgs%></td>

																						</tr>
																						<%}%>
																					</table> 
																				</td>
																			</tr>
																		</table>
																		<%
															            }//end of while(dbs.hasMoreElements())
															            %>
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
																<jsp:param value="<%=myip %>" name="myip" />
																<jsp:param value="<%=myport %>" name="myport" />
																<jsp:param value="<%=myUser %>" name="myUser" />
																<jsp:param value="<%=myPassword %>" name="myPassword" />
																<jsp:param value="<%=mysid  %>" name="sid" />
																<jsp:param value="<%=id %>" name="id" />
																<jsp:param value="<%=dbPage %>" name="dbPage" />
																<jsp:param value="<%=dbType %>" name="dbType" />
																<jsp:param value="db2" name="subtype" />
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
  mainForm.action = "<%=rootPath%>/db2.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&myip=<%=myip %>&myport=<%=myport %>&myUser=<%=myUser %>&myPassword=<%=myPassword %>&sid=<%=mysid %>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
	</BODY>
</HTML>