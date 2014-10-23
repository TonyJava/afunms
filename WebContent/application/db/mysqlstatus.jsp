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
  System.out.println("########################id = "+id);
  String myip = vo.getIpAddress();
  String myport = vo.getPort();
  String myUser = vo.getUser();
  String _flag = (String) request.getAttribute("flag");
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "mysqlstatus";
  String dbType = "mysql";
        
Hashtable max = (Hashtable) request.getAttribute("max");
//Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
String chart1 = (String)request.getAttribute("chart1");
	double avgpingcon = (Double)request.getAttribute("avgpingcon");	
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100-percent1;
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");
Vector tableinfo_v = (Vector)request.getAttribute("tableinfo_v");
String versionvalue=(String)request.getAttribute("versionvalue");
String vercommaval=(String)request.getAttribute("vercommaval");
String vercomosval=(String)request.getAttribute("vercomosval");
String verwaittimeval=(String)request.getAttribute("verwaittimeval");

String basePath=(String)request.getAttribute("basePath");
String dataPath=(String)request.getAttribute("dataPath");
String logerrorPath=(String)request.getAttribute("logerrorPath");
String version=(String)request.getAttribute("version");
String hostOS=(String)request.getAttribute("hostOS");

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
	document.getElementById('mysqlDetailTitle-4').className='detail-data-title';
	document.getElementById('mysqlDetailTitle-4').onmouseover="this.className='detail-data-title'";
	document.getElementById('mysqlDetailTitle-4').onmouseout="this.className='detail-data-title'";
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
																					<b>数据库状态信息</b>
																				</td>
																			</tr>
																			<%
			if(tableinfo_v != null)
			{
				for(int i=0;i<tableinfo_v.size();i++){
					Hashtable ht = (Hashtable)tableinfo_v.get(i);
					
					String size = ht.get("variable_name").toString();
					String free = ht.get("value").toString();
					if(size.equalsIgnoreCase("Aborted_clients"))
					{
					 size="由于客户端没有正确关闭连接导致客户端终止而中断的连接数";
					}
					if(size.equalsIgnoreCase("Aborted_connects"))
					{
					 size="试图连接到MySQL服务器而失败的连接数";
					}
					if(size.equalsIgnoreCase("Binlog_cache_disk_use"))
					{
					 size="使用临时二进制日志缓存但超过binlog_cache_size值并使用临时文件来保存事务中的语句的事务数量";
					}
					if(size.equalsIgnoreCase("Binlog_cache_use"))
					{
					 size="使用临时二进制日志缓存的事务数量";
					}
					if(size.equalsIgnoreCase("Bytes_received"))
					{
					 size="从所有客户端接收到的字节数";
					}
					if(size.equalsIgnoreCase("Bytes_sent"))
					{
					 size="发送给所有客户端的字节数";
					}
					if(size.equalsIgnoreCase("Com_admin_commands"))
					{
					 size="admin_commands语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_alter_db"))
					{
					 size="alter_db语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_alter_table"))
					{
					 size="alter_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_analyze"))
					{
					 size="analyze语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_backup_table"))
					{
					 size="backup_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_begin"))
					{
					 size="begin语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_change_db"))
					{
					 size="change_db语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_change_master"))
					{
					 size="change_master语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_check"))
					{
					 size="check语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_checksum"))
					{
					 size="checksum语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_commit"))
					{
					 size="commit语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_create_db"))
					{
					 size="create_db语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_create_function"))
					{
					 size="create_function语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_create_index"))
					{
					 size="create_index语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_create_table"))
					{
					 size="create_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_dealloc_sql"))
					{
					 size="dealloc_sql语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_delete"))
					{
					 size="delete语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_delete_multi"))
					{
					 size="delete_multi语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_do"))
					{
					 size="do语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_drop_db"))
					{
					 size="drop_db语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_drop_function"))
					{
					 size="drop_function语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_drop_index"))
					{
					 size="drop_index语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_drop_table"))
					{
					 size="drop_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_drop_user"))
					{
					 size="drop_user语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_execute_sql"))
					{
					 size="execute_sql语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_flush"))
					{
					 size="flush语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_grant"))
					{
					 size="grant语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_ha_close"))
					{
					 size="ha_close语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_ha_open"))
					{
					 size="ha_open语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_ha_read"))
					{
					 size="ha_read语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_help"))
					{
					 size="help语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_insert"))
					{
					 size="insert语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_insert_select"))
					{
					 size="insert_select语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_kill"))
					{
					 size="kill语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_load"))
					{
					 size="load语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_load_master_data"))
					{
					 size="load_master_data语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_load_master_table"))
					{
					 size="load_master_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_lock_tables"))
					{
					 size="lock_tables语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_optimize"))
					{
					 size="optimize语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_preload_keys"))
					{
					 size="preload_keys语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_prepare_sql"))
					{
					 size="prepare_sql语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_purge"))
					{
					 size="purge语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_purge_before_date"))
					{
					 size="purge_before_date语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_rename_table"))
					{
					 size="rename_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_repair"))
					{
					 size="repair语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_replace"))
					{
					 size="replace语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_replace_select"))
					{
					 size="replace_select语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_reset"))
					{
					 size="reset语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_restore_table"))
					{
					 size="restore_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_revoke"))
					{
					 size="revoke语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_revoke_all"))
					{
					 size="revoke_all语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_rollback"))
					{
					 size="rollback语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_savepoint"))
					{
					 size="savepoint语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_select"))
					{
					 size="select语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_set_option"))
					{
					 size="set_option语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_binlog_events"))
					{
					 size="show_binlog_events语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_binlogs"))
					{
					 size="show_binlogs语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_charsets"))
					{
					 size="show_charsets语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_collations"))
					{
					 size="show_collations语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_column_types"))
					{
					 size="how_column_types语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_create_db"))
					{
					 size="show_create_db语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_create_table"))
					{
					 size="show_create_table语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_databases"))
					{
					 size="show_databases语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_errors"))
					{
					 size="show_errors语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_fields"))
					{
					 size="show_fields语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_grants"))
					{
					 size="show_grants语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_innodb_status"))
					{
					 size="show_innodb_status语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_keys"))
					{
					 size="show_keys语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_logs"))
					{
					 size="show_logs语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_master_status"))
					{
					 size="show_master_status语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_ndb_status"))
					{
					 size="show_ndb_status语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_new_master"))
					{
					 size="show_new_master语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_open_tables"))
					{
					 size="show_open_tables语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_privileges"))
					{
					 size="show_privileges语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_processlist"))
					{
					 size="show_processlist语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_slave_hosts"))
					{
					 size="show_slave_hosts语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_slave_status"))
					{
					 size="show_slave_status语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_status"))
					{
					 size="show_status语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_storage_engines"))
					{
					 size="show_storage_engines语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_tables"))
					{
					 size="show_tables语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_triggers"))
					{
					 size="show_triggers语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_variables"))
					{
					 size="show_variables语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_show_warnings"))
					{
					 size="show_warnings语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_slave_start"))
					{
					 size="slave_start语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_slave_stop"))
					{
					 size="slave_stop语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_truncate"))
					{
					 size="truncate语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_unlock_tables"))
					{
					 size="unlock_tables语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_update"))
					{
					 size="update语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_update_multi"))
					{
					 size="update_multi语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_xa_commit"))
					{
					 size="xa_commi语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_xa_end"))
					{
					 size="xa_end语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_xa_prepare"))
					{
					 size="xa_prepare语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_xa_recover"))
					{
					 size="xa_recover语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_xa_rollback"))
					{
					 size="xa_rollback语句执行的次数";
					}
					if(size.equalsIgnoreCase("Com_xa_start"))
					{
					 size="xa_start语句执行的次数";
					}
					if(size.equalsIgnoreCase("Connections"))
					{
					 size="试图连接到(不管是否成功)MySQL服务器的连接数";
					}
					if(size.equalsIgnoreCase("Created_tmp_disk_tables"))
					{
					 size="服务器执行语句时在硬盘上自动创建的临时表的数量";
					}
					if(size.equalsIgnoreCase("Created_tmp_files"))
					{
					 size="mysqld已经创建的临时文件的数量";
					}
					if(size.equalsIgnoreCase("Created_tmp_tables"))
					{
					 size="服务器执行语句时自动创建的内存中的临时表的数量";
					}
					if(size.equalsIgnoreCase("Delayed_errors"))
					{
					 size="用INSERT DELAYED写的出现错误的行数(可能为duplicate key)";
					}
					if(size.equalsIgnoreCase("Delayed_insert_threads"))
					{
					 size="用使用的INSERT DELAYED处理器线程数";
					}
					if(size.equalsIgnoreCase("Delayed_writes"))
					{
					 size="写入的INSERT DELAYED行数";
					}
					if(size.equalsIgnoreCase("Flush_commands"))
					{
					 size="执行的FLUSH语句数";
					}
					if(size.equalsIgnoreCase("Handler_commit"))
					{
					 size="内部提交语句数";
					}
					if(size.equalsIgnoreCase("Handler_delete"))
					{
					 size="行从表中删除的次数";
					}
					if(size.equalsIgnoreCase("Handler_discover"))
					{
					 size="MySQL服务器可以问NDB CLUSTER存储引擎是否知道某一名字的表";
					}
					if(size.equalsIgnoreCase("Handler_read_first"))
					{
					 size="索引中第一条被读的次数";
					}
					if(size.equalsIgnoreCase("Handler_read_key"))
					{
					 size="根据键读一行的请求数";
					}
					if(size.equalsIgnoreCase("Handler_read_next"))
					{
					 size="按照键顺序读下一行的请求数";
					}
					if(size.equalsIgnoreCase("Handler_read_prev"))
					{
					 size="按照键顺序读前一行的请求数";
					}
					if(size.equalsIgnoreCase("Handler_read_rnd"))
					{
					 size="根据固定位置读一行的请求数";
					}
					if(size.equalsIgnoreCase("Handler_read_rnd_next"))
					{
					 size="在数据文件中读下一行的请求数";
					}
					if(size.equalsIgnoreCase("Handler_rollback"))
					{
					 size="内部ROLLBACK语句的数量";
					}
					if(size.equalsIgnoreCase("Handler_update"))
					{
					 size="在表内更新一行的请求数";
					}
					if(size.equalsIgnoreCase("Handler_write"))
					{
					 size="在表内插入一行的请求数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_data"))
					{
					 size="包含数据的页数(脏或干净)";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_dirty"))
					{
					 size="当前的脏页数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_flushed"))
					{
					 size="求清空的缓冲池页数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_free"))
					{
					 size="空页数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_latched"))
					{
					 size="在InnoDB缓冲池中锁定的页数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_misc"))
					{
					 size="忙的页数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_pages_total"))
					{
					 size="缓冲池总大小（页数）";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_read_ahead_rnd"))
					{
					 size="InnoDB初始化的“随机”read-aheads数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_read_ahead_seq"))
					{
					 size="InnoDB初始化的顺序read-aheads数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_read_requests"))
					{
					 size="InnoDB已经完成的逻辑读请求数";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_reads"))
					{
					 size="不能满足InnoDB必须单页读取的缓冲池中的逻辑读数量";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_wait_free"))
					{
					 size="没有特殊情况 通过后台向InnoDB缓冲池写";
					}
					if(size.equalsIgnoreCase("Innodb_buffer_pool_write_requests"))
					{
					 size="向InnoDB缓冲池的写数量";
					}
					if(size.equalsIgnoreCase("Innodb_data_fsyncs"))
					{
					 size="fsync()操作数";
					}
					if(size.equalsIgnoreCase("Innodb_data_pending_fsyncs"))
					{
					 size="当前挂起的fsync()操作数";
					}
					if(size.equalsIgnoreCase("Innodb_data_pending_reads"))
					{
					 size="当前挂起的读数";
					}
					if(size.equalsIgnoreCase("Innodb_data_pending_writes"))
					{
					 size="当前挂起的写数";
					}
					if(size.equalsIgnoreCase("Innodb_data_read"))
					{
					 size="至此已经读取的数据数量（字节）";
					}
					if(size.equalsIgnoreCase("Innodb_data_reads"))
					{
					 size="数据读总数量";
					}
					if(size.equalsIgnoreCase("IInnodb_data_writes"))
					{
					 size="数据写总数量";
					}
					if(size.equalsIgnoreCase("Innodb_data_written"))
					{
					 size="至此已经写入的数据量（字节）";
					}
					if(size.equalsIgnoreCase("Innodb_dblwr_pages_written"))
					{
					 size="为已经执行的双写操作数量写好的页数";
					}
					if(size.equalsIgnoreCase("Innodb_dblwr_writes"))
					{
					 size="已经执行的双写操作数量";
					}
					if(size.equalsIgnoreCase("Innodb_log_waits"))
					{
					 size="必须等待的时间，因为日志缓冲区太小，在继续前必须先等待对它清空";
					}
					if(size.equalsIgnoreCase("Innodb_log_write_requests"))
					{
					 size="日志写请求数";
					}
					if(size.equalsIgnoreCase("Innodb_log_writes"))
					{
					 size="向日志文件的物理写数量";
					}
					if(size.equalsIgnoreCase("Innodb_os_log_fsyncs"))
					{
					 size="向日志文件完成的fsync()写数量";
					}
					if(size.equalsIgnoreCase("Innodb_os_log_pending_fsyncs"))
					{
					 size="挂起的日志文件fsync()操作数量";
					}
					if(size.equalsIgnoreCase("Innodb_os_log_pending_writes"))
					{
					 size="挂起的日志文件写操作";
					}
					if(size.equalsIgnoreCase("Innodb_os_log_written"))
					{
					 size="写入日志文件的字节数";
					}
					if(size.equalsIgnoreCase("Innodb_page_size"))
					{
					 size="编译的InnoDB页大小(默认16KB) 许多值用页来记数 页的大小很容易转换为字节";
					}
					if(size.equalsIgnoreCase("Innodb_pages_created"))
					{
					 size="创建的页数";
					}
					if(size.equalsIgnoreCase("Innodb_pages_read"))
					{
					 size="读取的页数";
					}
					if(size.equalsIgnoreCase("Innodb_pages_written"))
					{
					 size="写入的页数";
					}
					if(size.equalsIgnoreCase("Innodb_row_lock_current_waits"))
					{
					 size="当前等待的待锁定的行数";
					}
					if(size.equalsIgnoreCase("Innodb_row_lock_time"))
					{
					 size="行锁定花费的总时间，单位毫秒";
					}
					if(size.equalsIgnoreCase("Innodb_row_lock_time_avg"))
					{
					 size="行锁定的平均时间，单位毫秒";
					}
					if(size.equalsIgnoreCase("Innodb_row_lock_time_max"))
					{
					 size="行锁定的最长时间，单位毫秒";
					}
					if(size.equalsIgnoreCase("Innodb_row_lock_waits"))
					{
					 size="一行锁定必须等待的时间数";
					}
					if(size.equalsIgnoreCase("Innodb_rows_deleted"))
					{
					 size="从InnoDB表删除的行数";
					}
					if(size.equalsIgnoreCase("Innodb_rows_inserted"))
					{
					 size="插入到InnoDB表的行数";
					}
					if(size.equalsIgnoreCase("Innodb_rows_read"))
					{
					 size="从InnoDB表读取的行数";
					}
					if(size.equalsIgnoreCase("Innodb_rows_updated"))
					{
					 size="InnoDB表内更新的行数";
					}
					if(size.equalsIgnoreCase("Key_blocks_not_flushed"))
					{
					 size="键缓存内已经更改但还没有清空到硬盘上的键的数据块数量";
					}
					if(size.equalsIgnoreCase("Key_blocks_unused"))
					{
					 size="键缓存内未使用的块数量";
					}
					if(size.equalsIgnoreCase("Key_blocks_used"))
					{
					 size="键缓存内使用的块数量";
					}
					if(size.equalsIgnoreCase("Key_read_requests"))
					{
					 size="从缓存读键的数据块的请求数";
					}
					if(size.equalsIgnoreCase("Key_reads"))
					{
					 size="从硬盘读取键的数据块的次数";
					}
					if(size.equalsIgnoreCase("Key_write_requests"))
					{
					 size="将键的数据块写入缓存的请求数";
					}
					if(size.equalsIgnoreCase("Key_writes"))
					{
					 size="向硬盘写入将键的数据块的物理写操作的次数";
					}
					if(size.equalsIgnoreCase("Last_query_cost"))
					{
					 size="用查询优化器计算的最后编译的查询的总成本";
					}
					if(size.equalsIgnoreCase("Max_used_connections"))
					{
					 size="服务器启动后已经同时使用的连接的最大数量";
					}
					if(size.equalsIgnoreCase("Not_flushed_delayed_rows"))
					{
					 size="等待写入INSERT DELAY队列的行数";
					}
					if(size.equalsIgnoreCase("Open_files"))
					{
					 size="打开的文件的数目";
					}
					if(size.equalsIgnoreCase("Open_streams"))
					{
					 size="打开的流的数量(主要用于记录)";
					}
					if(size.equalsIgnoreCase("Open_tables"))
					{
					 size="当前打开的表的数量";
					}
					if(size.equalsIgnoreCase("Opened_tables"))
					{
					 size="已经打开的表的数量";
					}
					if(size.equalsIgnoreCase("Qcache_free_blocks"))
					{
					 size="查询缓存内自由内存块的数量";
					}
					if(size.equalsIgnoreCase("Qcache_free_memory"))
					{
					 size="用于查询缓存的自由内存的数量";
					}
					if(size.equalsIgnoreCase("Qcache_hits"))
					{
					 size="查询缓存被访问的次数";
					}
					if(size.equalsIgnoreCase("Qcache_inserts"))
					{
					 size="加入到缓存的查询数量";
					}
					if(size.equalsIgnoreCase("Qcache_lowmem_prunes"))
					{
					 size="由于内存较少从缓存删除的查询数量";
					}
					if(size.equalsIgnoreCase("Qcache_not_cached"))
					{
					 size="非缓存查询数(不可缓存，或由于query_cache_type设定值未缓存)";
					}
					if(size.equalsIgnoreCase("Qcache_queries_in_cache"))
					{
					 size="登记到缓存内的查询的数量";
					}
					if(size.equalsIgnoreCase("Qcache_total_blocks"))
					{
					 size="查询缓存内的总块数";
					}
					if(size.equalsIgnoreCase("Questions"))
					{
					 size="已经发送给服务器的查询的个数";
					}
					if(size.equalsIgnoreCase("Rpl_status"))
					{
					 size="失败安全复制状态(还未使用)";
					}
					if(size.equalsIgnoreCase("Select_full_join"))
					{
					 size="没有使用索引的联接的数量。如果该值不为0,你应仔细检查表的索引";
					}
					if(size.equalsIgnoreCase("Select_full_range_join"))
					{
					 size="在引用的表中使用范围搜索的联接的数量";
					}
					if(size.equalsIgnoreCase("Select_range"))
					{
					 size="在第一个表中使用范围的联接的数量";
					}
					if(size.equalsIgnoreCase("Select_range_check"))
					{
					 size="在每一行数据后对键值进行检查的不带键值的联接的数量";
					}
					if(size.equalsIgnoreCase("Select_scan"))
					{
					 size="对第一个表进行完全扫描的联接的数量";
					}
					if(size.equalsIgnoreCase("Slave_open_temp_tables"))
					{
					 size="当前由从SQL线程打开的临时表的数量";
					}
					if(size.equalsIgnoreCase("Slave_retried_transactions"))
					{
					 size="启动后复制从服务器SQL线程尝试事务的总次数";
					}
					if(size.equalsIgnoreCase("Slave_running"))
					{
					 size="如果该服务器是连接到主服务器的从服务器，则该值为ON";
					}
					if(size.equalsIgnoreCase("Slow_launch_threads"))
					{
					 size="创建时间超过slow_launch_time秒的线程数";
					}
					if(size.equalsIgnoreCase("Slow_queries"))
					{
					 size="查询时间超过long_query_time秒的查询的个数";
					}
					if(size.equalsIgnoreCase("Sort_merge_passes"))
					{
					 size="排序算法已经执行的合并的数量";
					}
					if(size.equalsIgnoreCase("Sort_range"))
					{
					 size="在范围内执行的排序的数量";
					}
					if(size.equalsIgnoreCase("Sort_rows"))
					{
					 size="已经排序的行数";
					}
					if(size.equalsIgnoreCase("Sort_scan"))
					{
					 size="通过扫描表完成的排序的数量";
					}
					if(size.equalsIgnoreCase("Ssl_accept_renegotiates"))
					{
					 size="用于SSL连接的变量";
					}
					if(size.equalsIgnoreCase("Ssl_accept_renegotiates"))
					{
					 size="用于SSL连接的变量accept_renegotiates";
					}
					if(size.equalsIgnoreCase("Ssl_accepts"))
					{
					 size="用于SSL连接的变量accepts";
					}
					if(size.equalsIgnoreCase("Ssl_callback_cache_hits"))
					{
					 size="用于SSL连接的变量callback_cache_hits";
					}
					if(size.equalsIgnoreCase("Ssl_cipher"))
					{
					 size="用于SSL连接的变量cipher";
					}
					if(size.equalsIgnoreCase("Ssl_cipher_list"))
					{
					 size="用于SSL连接的变量cipher_list";
					}
					if(size.equalsIgnoreCase("Ssl_client_connects"))
					{
					 size="用于SSL连接的变量client_connects";
					}
					if(size.equalsIgnoreCase("Ssl_connect_renegotiates"))
					{
					 size="用于SSL连接的变量connect_renegotiates";
					}
					if(size.equalsIgnoreCase("Ssl_ctx_verify_depth"))
					{
					 size="用于SSL连接的变量ctx_verify_depth";
					}
					if(size.equalsIgnoreCase("Ssl_ctx_verify_mode"))
					{
					 size="用于SSL连接的变量ctx_verify_mode";
					}
					if(size.equalsIgnoreCase("Ssl_default_timeout"))
					{
					 size="用于SSL连接的变量default_timeout";
					}
					if(size.equalsIgnoreCase("Ssl_finished_accepts"))
					{
					 size="用于SSL连接的变量finished_accepts";
					}
					if(size.equalsIgnoreCase("Ssl_finished_connects"))
					{
					 size="用于SSL连接的变量finished_connects";
					}
					if(size.equalsIgnoreCase("Ssl_session_cache_hits"))
					{
					 size="用于SSL连接的变量session_cache_hits";
					}
					if(size.equalsIgnoreCase("Ssl_session_cache_misses"))
					{
					 size="用于SSL连接的变量session_cache_misses";
					}
					if(size.equalsIgnoreCase("Ssl_session_cache_mode"))
					{
					 size="用于SSL连接的变量session_cache_mode";
					}
					if(size.equalsIgnoreCase("Ssl_session_cache_overflows"))
					{
					 size="用于SSL连接的变量session_cache_overflows";
					}
					if(size.equalsIgnoreCase("Ssl_session_cache_size"))
					{
					 size="用于SSL连接的变量session_cache_size";
					}
					if(size.equalsIgnoreCase("Ssl_session_cache_timeouts"))
					{
					 size="用于SSL连接的变量session_cache_timeouts";
					}
					if(size.equalsIgnoreCase("Ssl_sessions_reused"))
					{
					 size="用于SSL连接的变量sessions_reused";
					}
					if(size.equalsIgnoreCase("Ssl_used_session_cache_entries"))
					{
					 size="用于SSL连接的变量used_session_cache_entries";
					}
					if(size.equalsIgnoreCase("Ssl_verify_depth"))
					{
					 size="用于SSL连接的变量verify_depth";
					}
					if(size.equalsIgnoreCase("Ssl_verify_mode"))
					{
					 size="用于SSL连接的变量verify_mode";
					}
					if(size.equalsIgnoreCase("Ssl_version"))
					{
					 size="用于SSL连接的变量version";
					}
					if(size.equalsIgnoreCase("Table_locks_immediate"))
					{
					 size="立即获得的表的锁的次数";
					}
					if(size.equalsIgnoreCase("Table_locks_waited"))
					{
					 size="不能立即获得的表的锁的次数";
					}
					if(size.equalsIgnoreCase("Threads_cached"))
					{
					 size="线程缓存内的线程的数量";
					}
					if(size.equalsIgnoreCase("Threads_connected"))
					{
					 size="当前打开的连接的数量";
					}
					if(size.equalsIgnoreCase("Threads_created"))
					{
					 size="创建用来处理连接的线程数";
					}
					if(size.equalsIgnoreCase("Threads_running"))
					{
					 size="激活的（非睡眠状态）线程数";
					}
						if(size.equalsIgnoreCase("Uptime"))
					{
					 size="服务器已经运行的时间（以秒为单位）";
					}
					
					
			%>
																			<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=28>
																				<td class="application-detail-data-body-list"><%=i+1%></td>
																				<td class="application-detail-data-body-list"
																					align=left>
																					&nbsp;<%=size%></td>
																				<td class="application-detail-data-body-list"
																					align=left width=20%>
																					&nbsp;<%=free%></td>
																			</tr>
																			<%
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
					<td  width=15% valign=top>&gt;<jsp:include page="/include/dbtoolbar.jsp"><jsp:param value="<%=myip %>" name="myip" />
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