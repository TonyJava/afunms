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
  String myUser = vo.getUser();
  String myPassword = EncryptUtil.decode(vo.getPassword());
  String mysid = "";
  String dbPage = "mysqlvariables";
  String dbType = "mysql";
        String _flag = (String) request.getAttribute("flag");
	
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
	document.getElementById('mysqlDetailTitle-5').className='detail-data-title';
	document.getElementById('mysqlDetailTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('mysqlDetailTitle-5').onmouseout="this.className='detail-data-title'";
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
											<td><jsp:include page="/topology/includejsp/db_mysql.jsp"><jsp:param name="dbName" value="<%=vo.getDbName() %>"/>
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
																		<table cellspacing="1" cellpadding="0">
																			<tr>
																				<td height="28" align="center" bgcolor="#ECECEC"
																					colspan=6>
																					<b> 数据库状态信息</b>
																				</td>
																			</tr>

																			<%
			if(tableinfo_v != null)
			{
				for(int i=0;i<tableinfo_v.size();i++){
					Hashtable ht = (Hashtable)tableinfo_v.get(i);
					String size = ht.get("variable_name").toString();
					String free = ht.get("value").toString();
					if(size.equalsIgnoreCase("auto_increment_increment"))
					{
					 size="控制列中的值的增量值";
					}
					if(size.equalsIgnoreCase("auto_increment_offset"))
					{
					 size="确定AUTO_INCREMENT列值的起点";
					}
					if(size.equalsIgnoreCase("automatic_sp_privileges"))
					{
					 size="automatic_sp_privileges";
					}
					if(size.equalsIgnoreCase("back_log"))
					{
					 size="连接请求的数量";
					}
					if(size.equalsIgnoreCase("basedir"))
					{
					 size="MySQL安装基准目录";
					}
					if(size.equalsIgnoreCase("binlog_cache_size"))
					{
					 size="容纳二进制日志SQL语句的缓存大小";
					}
					if(size.equalsIgnoreCase("bulk_insert_buffer_size"))
					{
					 size="每线程的字节数限制缓存树的大小";
					}
					if(size.equalsIgnoreCase("character_set_client"))
					{
					 size="来自客户端的语句的字符集";
					}
					if(size.equalsIgnoreCase("character_set_connection"))
					{
					 size="没有字符集导入符的字符串转换";
					}
					if(size.equalsIgnoreCase("character_set_database"))
					{
					 size="默认数据库使用的字符集";
					}
					if(size.equalsIgnoreCase("character_set_filesystem"))
					{
					 size="character_set_filesystem";
					}
					if(size.equalsIgnoreCase("character_set_results"))
					{
					 size="用于向客户端返回查询结果的字符集";
					}
					if(size.equalsIgnoreCase("character_set_server"))
					{
					 size="服务器的默认字符集";
					}
					if(size.equalsIgnoreCase("character_set_system"))
					{
					 size="服务器用来保存识别符的字符集";
					}
					if(size.equalsIgnoreCase("character_sets_dir"))
					{
					 size="字符集安装目录";
					}
					if(size.equalsIgnoreCase("collation_connection"))
					{
					 size="连接字符集的校对规则";
					}
					if(size.equalsIgnoreCase("collation_database"))
					{
					 size="默认数据库使用的校对规则";
					}
					if(size.equalsIgnoreCase("collation_server"))
					{
					 size="服务器的默认校对规则";
					}
					if(size.equalsIgnoreCase("completion_type"))
					{
					 size="事务结束类型";
					}
					if(size.equalsIgnoreCase("concurrent_insert"))
					{
					 size="存储值情况";
					}
					if(size.equalsIgnoreCase("connect_timeout"))
					{
					 size="服务器用Bad handshake响应前等待连接包的秒数";
					}
					if(size.equalsIgnoreCase("datadir"))
					{
					 size="MySQL数据目录";
					}
					if(size.equalsIgnoreCase("date_format"))
					{
					 size="date_format(为被使用)";
					}
					if(size.equalsIgnoreCase("datetime_format"))
					{
					 size="datetime_format(为被使用)";
					}
					if(size.equalsIgnoreCase("default_week_format"))
					{
					 size="WEEK() 函数使用的默认模式";
					}
					if(size.equalsIgnoreCase("delay_key_write"))
					{
					 size="使用的DELAY_KEY_WRITE表选项的处理";
					}
					if(size.equalsIgnoreCase("delayed_insert_limit"))
					{
					 size="INSERT DELAYED处理器线程检查是否有挂起的SELECT语句";
					}
					if(size.equalsIgnoreCase("delayed_insert_timeout"))
					{
					 size="INSERT DELAYED处理器线程终止前应等待INSERT语句的时间";
					}
					if(size.equalsIgnoreCase("delayed_queue_size"))
					{
					 size="处理INSERT DELAYED语句时队列中行的数量限制";
					}
					if(size.equalsIgnoreCase("div_precision_increment"))
					{
					 size="用/操作符执行除操作的结果可增加的精确度的位数";
					}
					if(size.equalsIgnoreCase("engine_condition_pushdown"))
					{
					 size="适用于NDB的检测";
					}
					if(size.equalsIgnoreCase("expire_logs_days"))
					{
					 size="二进制日志自动删除的天数";
					}
					if(size.equalsIgnoreCase("flush"))
					{
					 size="flush选项启动mysqld值";
					}
					if(size.equalsIgnoreCase("flush_time"))
					{
					 size="查看释放资源情况";
					}
					if(size.equalsIgnoreCase("ft_boolean_syntax"))
					{
					 size="使用IN BOOLEAN MODE执行的布尔全文搜索支持的操作符系列";
					}
					if(size.equalsIgnoreCase("ft_max_word_len"))
					{
					 size="FULLTEXT索引中所包含的字的最大长度";
					}
					if(size.equalsIgnoreCase("ft_min_word_len"))
					{
					 size="FULLTEXT索引中所包含的字的最小长度";
					}
					if(size.equalsIgnoreCase("ft_query_expansion_limit"))
					{
					 size="使用WITH QUERY EXPANSION进行全文搜索的最大匹配数";
					}
					if(size.equalsIgnoreCase("ft_stopword_file"))
					{
					 size="用于读取全文搜索的停止字清单的文件";
					}
					if(size.equalsIgnoreCase("group_concat_max_len"))
					{
					 size="允许的GROUP_CONCAT()函数结果的最大长度";
					}
					if(size.equalsIgnoreCase("have_archive"))
					{
					 size="mysqld支持ARCHIVE表支持表情况";
					}
					if(size.equalsIgnoreCase("have_bdb"))
					{
					 size="mysqld支持BDB表情况";
					}
					if(size.equalsIgnoreCase("have_blackhole_engine"))
					{
					 size="mysqld支持BLACKHOLE表情况";
					}
					if(size.equalsIgnoreCase("have_compress"))
					{
					 size="是否zlib压缩库适合该服务器";
					}
					if(size.equalsIgnoreCase("have_crypt"))
					{
					 size="是否crypt()系统调用适合该服务器";
					}
					if(size.equalsIgnoreCase("have_csv"))
					{
					 size="mysqld支持ARCHIVE表情况";
					}
					if(size.equalsIgnoreCase("have_example_engine"))
					{
					 size="mysqld支持EXAMPLE表情况";
					}
					if(size.equalsIgnoreCase("have_federated_engine"))
					{
					 size="mysqld支持FEDERATED表情况";
					}
					if(size.equalsIgnoreCase("have_geometry"))
					{
					 size="是否服务器支持空间数据类型";
					}
					if(size.equalsIgnoreCase("have_innodb"))
					{
					 size="mysqld支持InnoDB表情况";
					}
					if(size.equalsIgnoreCase("have_isam"))
					{
					 size="向后兼容";
					}
					if(size.equalsIgnoreCase("have_ndbcluster"))
					{
					 size="mysqld支持NDB CLUSTER表情况";
					}
					if(size.equalsIgnoreCase("have_openssl"))
					{
					 size="mysqld支持客户端/服务器协议的SSL(加密)情况";
					}
					if(size.equalsIgnoreCase("have_query_cache"))
					{
					 size="mysqld支持查询缓存情况";
					}
					if(size.equalsIgnoreCase("have_raid"))
					{
					 size="mysqld支持RAID选项情况";
					}
					if(size.equalsIgnoreCase("have_rtree_keys"))
					{
					 size="RTREE索引是否可用";
					}
					if(size.equalsIgnoreCase("have_symlink"))
					{
					 size="是否启用符号链接支持";
					}
					if(size.equalsIgnoreCase("init_connect"))
					{
					 size="字符串处理";
					}
					if(size.equalsIgnoreCase("init_file"))
					{
					 size="启动服务器时用--init-file选项指定的文件名";
					}
					if(size.equalsIgnoreCase("init_slave"))
					{
					 size="SQL线程启动时从服务器应执行该字符串";
					}
					if(size.equalsIgnoreCase("innodb_additional_mem_pool_size"))
					{
					 size="InnoDB用来存储数据内存大小情况";
					}
					if(size.equalsIgnoreCase("innodb_autoextend_increment"))
					{
					 size="表空间被填满之时扩展表空间的尺寸";
					}
					if(size.equalsIgnoreCase("innodb_buffer_pool_awe_mem_mb"))
					{
					 size="缓冲池被放在32位Windows的AWE内存里缓存池大小";
					}
					if(size.equalsIgnoreCase("innodb_buffer_pool_size"))
					{
					 size="InnoDB用来缓存它的数据和索引的内存缓冲区的大小";
					}
					if(size.equalsIgnoreCase("innodb_checksums"))
					{
					 size="InnoDB在所有对磁盘的页面读取上的状态";
					}
					if(size.equalsIgnoreCase("innodb_commit_concurrency"))
					{
					 size="innodb_commit_concurrency";
					}
					if(size.equalsIgnoreCase("innodb_concurrency_tickets"))
					{
					 size="innodb_concurrency_tickets";
					}
					if(size.equalsIgnoreCase("innodb_data_file_path"))
					{
					 size="单独数据文件和它们尺寸的路径";
					}
					if(size.equalsIgnoreCase("innodb_data_home_dir"))
					{
					 size="目录路径对所有InnoDB数据文件的共同部分";
					}
					if(size.equalsIgnoreCase("innodb_doublewrite"))
					{
					 size="InnoDB存储所有数据情况";
					}
					if(size.equalsIgnoreCase("innodb_fast_shutdown"))
					{
					 size="InnoDB在关闭情况的值选择";
					}
					if(size.equalsIgnoreCase("innodb_file_io_threads"))
					{
					 size="InnoDB中文件I/O线程的数";
					}
					if(size.equalsIgnoreCase("innodb_file_per_table"))
					{
					 size="确定是否InnoDB用自己的.ibd文件为存储数据和索引创建每一个新表";
					}
					if(size.equalsIgnoreCase("innodb_flush_log_at_trx_commit"))
					{
					 size="InnoDB对日志操作情况";
					}
					if(size.equalsIgnoreCase("innodb_flush_method"))
					{
					 size="InnoDB使用fsync()来刷新数据和日志文件";
					}
					if(size.equalsIgnoreCase("innodb_force_recovery"))
					{
					 size="损坏的数据库转储表的方案";
					}
					if(size.equalsIgnoreCase("innodb_lock_wait_timeout"))
					{
					 size="InnoDB事务在被回滚之前可以等待一个锁定的超时秒数";
					}
					if(size.equalsIgnoreCase("innodb_locks_unsafe_for_binlog"))
					{
					 size="InnoDB搜索和索引扫描中关闭下一键锁定";
					}
					if(size.equalsIgnoreCase("innodb_log_arch_dir"))
					{
					 size="使用日志档案 被完整写入的日志文件所在的目录的归档值";
					}
					if(size.equalsIgnoreCase("innodb_log_archive"))
					{
					 size="日志处理情况";
					}
					if(size.equalsIgnoreCase("innodb_log_buffer_size"))
					{
					 size="InnoDB用来往磁盘上的日志文件写操作的缓冲区的大小";
					}
					if(size.equalsIgnoreCase("innodb_log_file_size"))
					{
					 size="日志组里每个日志文件的大小";
					}
					if(size.equalsIgnoreCase("innodb_log_files_in_group"))
					{
					 size="日志组里日志文件的数目";
					}
					if(size.equalsIgnoreCase("innodb_log_group_home_dir"))
					{
					 size="InnoDB日志文件的目录路径";
					}
					if(size.equalsIgnoreCase("innodb_max_dirty_pages_pct"))
					{
					 size="InnoDB中处理脏页的情况";
					}
					if(size.equalsIgnoreCase("innodb_max_purge_lag"))
					{
					 size="净化操作被滞后之时，如何延迟INSERT,UPDATE和DELETE操作";
					}
					if(size.equalsIgnoreCase("innodb_mirrored_log_groups"))
					{
					 size="为数据库保持的日志组内同样拷贝的数量";
					}
					if(size.equalsIgnoreCase("innodb_open_files"))
					{
					 size="定InnoDB一次可以保持打开的.ibd文件的最大数";
					}
					if(size.equalsIgnoreCase("innodb_support_xa"))
					{
					 size="InnoDB支持在XA事务中的双向提交情况";
					}
					if(size.equalsIgnoreCase("innodb_sync_spin_loops"))
					{
					 size="innodb_sync_spin_loops";
					}
					if(size.equalsIgnoreCase("innodb_table_locks"))
					{
					 size="InnoDB对表的锁定情况";
					}
					if(size.equalsIgnoreCase("innodb_thread_concurrency"))
					{
					 size="InnoDB试着在InnoDB内保持操作系统线程的数量少于或等于这个参数给出的限制范围";
					}
					if(size.equalsIgnoreCase("innodb_thread_sleep_delay"))
					{
					 size="让InnoDB为周期的SHOW INNODB STATUS输出创建一个文件<datadir>/innodb_status";
					}
					if(size.equalsIgnoreCase("interactive_timeout"))
					{
					 size="服务器关闭交互式连接前等待活动的秒数";
					}
					if(size.equalsIgnoreCase("join_buffer_size"))
					{
					 size="用于完全联接的缓冲区的大小";
					}
					if(size.equalsIgnoreCase("key_buffer_size"))
					{
					 size="索引块缓冲区的大小";
					}
					if(size.equalsIgnoreCase("key_cache_age_threshold"))
					{
					 size="控制将缓冲区从键值缓存热子链(sub-chain)降级到温子链(sub-chain)的值";
					}
					if(size.equalsIgnoreCase("key_cache_block_size"))
					{
					 size="键值缓存内块的字节大小";
					}
					if(size.equalsIgnoreCase("key_cache_division_limit"))
					{
					 size="键值缓存缓冲区链热子链和温子链的划分点";
					}
					if(size.equalsIgnoreCase("language"))
					{
					 size="错误消息所用语言";
					}
					if(size.equalsIgnoreCase("large_files_support"))
					{
					 size="mysqld编译时是否使用了大文件支持选项";
					}
					if(size.equalsIgnoreCase("large_page_size"))
					{
					 size="large_page_size";
					}
					if(size.equalsIgnoreCase("large_pages"))
					{
					 size="是否启用了大页面支持";
					}
					if(size.equalsIgnoreCase("license"))
					{
					 size="服务器的许可类型";
					}
					if(size.equalsIgnoreCase("local_infile"))
					{
					 size="是否LOCAL支持LOAD DATA INFILE语句";
					}
					if(size.equalsIgnoreCase("log"))
					{
					 size="是否启用将所有查询记录到常规查询日志中";
					}
					if(size.equalsIgnoreCase("log_bin"))
					{
					 size="是否启用二进制日志";
					}
					if(size.equalsIgnoreCase("log_bin_trust_function_creators"))
					{
					 size="是否可以信任保存的程序的作者不会创建向二进制日志写入不安全事件的程序";
					}
					if(size.equalsIgnoreCase("log_error"))
					{
					 size="错误日志的位置";
					}
					if(size.equalsIgnoreCase("log_slave_updates"))
					{
					 size="是否从服务器从主服务器收到的更新应记入从服务器自己的二进制日志";
					}
					if(size.equalsIgnoreCase("log_slow_queries"))
					{
					 size="是否记录慢查询";
					}
					if(size.equalsIgnoreCase("log_warnings"))
					{
					 size="是否产生其它警告消息";
					}
					if(size.equalsIgnoreCase("long_query_time"))
					{
					 size="查询时间超过该值，则增加Slow_queries状态变量";
					}
					if(size.equalsIgnoreCase("low_priority_updates"))
					{
					 size="表示sql语句等待语句将等待直到受影响的表没有挂起的SELECT或LOCK TABLE READ";
					}
					if(size.equalsIgnoreCase("lower_case_file_system"))
					{
					 size="说明是否数据目录所在的文件系统对文件名的大小写敏感";
					}
					if(size.equalsIgnoreCase("lower_case_table_names"))
					{
					 size="为1表示表名用小写保存到硬盘上，并且表名比较时不对大小写敏感";
					}
					if(size.equalsIgnoreCase("max_allowed_packet"))
					{
					 size="包或任何生成的/中间字符串的最大大小";
					}
					if(size.equalsIgnoreCase("max_binlog_cache_size"))
					{
					 size="多语句事务需要更大的内存时出现的情况";
					}
					if(size.equalsIgnoreCase("max_binlog_size"))
					{
					 size="多语句事务需要更大的内存时出现的情况";
					}
					if(size.equalsIgnoreCase("max_connect_errors"))
					{
					 size="断的与主机的连接的最大限制数";
					}
					if(size.equalsIgnoreCase("max_connections"))
					{
					 size="允许的并行客户端连接数目";
					}
					if(size.equalsIgnoreCase("max_delayed_threads"))
					{
					 size="启动线程来处理INSERT DELAYED语句的限制数";
					}
					if(size.equalsIgnoreCase("max_error_count"))
					{
					 size="存由SHOW ERRORS或SHOW WARNINGS显示的错误、警告和注解的最大数目";
					}
					if(size.equalsIgnoreCase("max_heap_table_size"))
					{
					 size="设置MEMORY (HEAP)表可以增长到的最大空间大小";
					}
					if(size.equalsIgnoreCase("max_insert_delayed_threads"))
					{
					 size="启动线程来处理INSERT DELAYED语句的限制数(同max_delayed_threads)";
					}
					if(size.equalsIgnoreCase("max_join_size"))
					{
					 size="不允许可能需要检查多于max_join_size行的情况";
					}
					if(size.equalsIgnoreCase("max_length_for_sort_data"))
					{
					 size="确定使用的filesort算法的索引值大小的限值";
					}
					if(size.equalsIgnoreCase("max_prepared_stmt_count"))
					{
					 size="max_prepared_stmt_count";
					}
					if(size.equalsIgnoreCase("max_relay_log_size"))
					{
					 size="如果复制从服务器写入中继日志时超出给定值，则滚动中继日";
					}
					if(size.equalsIgnoreCase("max_seeks_for_key"))
					{
					 size="限制根据键值寻找行时的最大搜索数";
					}
					if(size.equalsIgnoreCase("max_sort_length"))
					{
					 size="排序BLOB或TEXT值时使用的字节数";
					}
					if(size.equalsIgnoreCase("max_sp_recursion_depth"))
					{
					 size="max_sp_recursion_depth";
					}
					if(size.equalsIgnoreCase("max_tmp_tables"))
					{
					 size="客户端可以同时打开的临时表的最大数";
					}
					if(size.equalsIgnoreCase("max_user_connections"))
					{
					 size="给定的MySQL账户允许的最大同时连接数";
					}
					if(size.equalsIgnoreCase("max_write_lock_count"))
					{
					 size="超过写锁定限制后，允许部分读锁定";
					}
					if(size.equalsIgnoreCase("multi_range_count"))
					{
					 size="multi_range_count";
					}
					if(size.equalsIgnoreCase("myisam_data_pointer_size"))
					{
					 size="默认指针大小的值";
					}
					if(size.equalsIgnoreCase("myisam_max_sort_file_size"))
					{
					 size="重建MyISAM索引时，允许MySQL使用的临时文件的最大空间大小";
					}
					if(size.equalsIgnoreCase("myisam_recover_options"))
					{
					 size="myisam-recover选项的值";
					}
					if(size.equalsIgnoreCase("myisam_repair_threads"))
					{
					 size="如果该值大于1，在Repair by sorting过程中并行创建MyISAM表索引";
					}
					if(size.equalsIgnoreCase("myisam_sort_buffer_size"))
					{
					 size="在REPAIR TABLE或用CREATE INDEX创建索引或ALTER TABLE过程中排序MyISAM索引分配的缓冲区";
					}
					if(size.equalsIgnoreCase("myisam_stats_method"))
					{
					 size="MyISAM表搜集关于索引值分发的统计信息时服务器如何处理NULL值";
					}
					if(size.equalsIgnoreCase("named_pipe"))
					{
					 size="明服务器是否支持命名管道连接";
					}
					if(size.equalsIgnoreCase("net_buffer_length"))
					{
					 size="在查询之间将通信缓冲区重设为该值";
					}
					if(size.equalsIgnoreCase("net_read_timeout"))
					{
					 size="中断读前等待连接的其它数据的秒数";
					}
					if(size.equalsIgnoreCase("net_retry_count"))
					{
					 size="表示某个通信端口的读操作中断了，在放弃前重试多次";
					}
					if(size.equalsIgnoreCase("net_write_timeout"))
					{
					 size="中断写之前等待块写入连接的秒数";
					}
					if(size.equalsIgnoreCase("new"))
					{
					 size="表示在MySQL 4.0中使用该变量来打开4.1中的一些行为，并用于向后兼容性";
					}
					if(size.equalsIgnoreCase("old_passwords"))
					{
					 size="是否服务器应为MySQL用户账户使用pre-4.1-style密码性";
					}
					if(size.equalsIgnoreCase("open_files_limit"))
					{
					 size="操作系统允许mysqld打开的文件的数量";
					}
					if(size.equalsIgnoreCase("optimizer_prune_level"))
					{
					 size="在查询优化从优化器搜索空间裁减低希望局部计划中使用的控制方法 0表示禁用方法";
					}
					if(size.equalsIgnoreCase("optimizer_search_depth"))
					{
					 size="查询优化器进行的搜索的最大深度";
					}
					if(size.equalsIgnoreCase("pid_file"))
					{
					 size="进程ID (PID)文件的路径名";
					}
					if(size.equalsIgnoreCase("prepared_stmt_count"))
					{
					 size="prepared_stmt_count";
					}
					if(size.equalsIgnoreCase("port"))
					{
					 size="服务器帧听TCP/IP连接所用端口";
					}
					if(size.equalsIgnoreCase("preload_buffer_size"))
					{
					 size="重载索引时分配的缓冲区大小";
					}
					if(size.equalsIgnoreCase("protocol_version"))
					{
					 size="MySQL服务器使用的客户端/服务器协议的版本";
					}
					if(size.equalsIgnoreCase("query_alloc_block_size"))
					{
					 size="为查询分析和执行过程中创建的对象分配的内存块大小";
					}
					if(size.equalsIgnoreCase("query_cache_limit"))
					{
					 size="不要缓存大于该值的结果";
					}
					if(size.equalsIgnoreCase("query_cache_min_res_unit"))
					{
					 size="查询缓存分配的最小块的大小(字节)";
					}
					if(size.equalsIgnoreCase("query_cache_size"))
					{
					 size="为缓存查询结果分配的内存的数量";
					}
					if(size.equalsIgnoreCase("query_cache_type"))
					{
					 size="设置查询缓存类型";
					}
					if(size.equalsIgnoreCase("query_cache_wlock_invalidate"))
					{
					 size="对表进行WRITE锁定的设置值";
					}
					if(size.equalsIgnoreCase("query_prealloc_size"))
					{
					 size="用于查询分析和执行的固定缓冲区的大小";
					}
					if(size.equalsIgnoreCase("range_alloc_block_size"))
					{
					 size="范围优化时分配的块的大小";
					}
					if(size.equalsIgnoreCase("read_buffer_size"))
					{
					 size="每个线程连续扫描时为扫描的每个表分配的缓冲区的大小(字节)";
					}
					if(size.equalsIgnoreCase("read_only"))
					{
					 size="变量对复制从服务器设置为ON时，服务器是否允许更新";
					}
					if(size.equalsIgnoreCase("read_only"))
					{
					 size="变量对复制从服务器设置为ON时，从服务器不允许更新";
					}
					if(size.equalsIgnoreCase("relay_log_purge"))
					{
					 size="当不再需要中继日志时禁用或启用自动清空中继日志";
					}
					if(size.equalsIgnoreCase("read_rnd_buffer_size"))
					{
					 size="当排序后按排序后的顺序读取行时，则通过该缓冲区读取行，避免搜索硬盘";
					}
					if(size.equalsIgnoreCase("secure_auth"))
					{
					 size="如果用--secure-auth选项启动了MySQL服务器，是否将阻塞有旧格式(4.1之前)密码的所有账户所发起的连接";
					}
					if(size.equalsIgnoreCase("shared_memory"))
					{
					 size="(只用于Windows)服务器是否允许共享内存连接";
					}
					if(size.equalsIgnoreCase("shared_memory_base_name"))
					{
					 size="(只用于Windows)说明服务器是否允许共享内存连接，并为共享内存设置识别符";
					}
					if(size.equalsIgnoreCase("server_id"))
					{
					 size="用于主复制服务器和从复制服务器";
					}
					if(size.equalsIgnoreCase("skip_external_locking"))
					{
					 size="mysqld是否使用外部锁定";
					}
					if(size.equalsIgnoreCase("skip_networking"))
					{
					 size="如果服务器只允许本地(非TCP/IP)连接";
					}
					if(size.equalsIgnoreCase("skip_show_database"))
					{
					 size="防止不具有SHOW DATABASES权限的人们使用SHOW DATABASES语句";
					}
					if(size.equalsIgnoreCase("slave_compressed_protocol"))
					{
					 size="如果主、从服务器均支持，确定是否使用从/主压缩协议";
					}
					if(size.equalsIgnoreCase("slave_load_tmpdir"))
					{
					 size="从服务器为复制LOAD DATA INFILE语句创建临时文件的目录名";
					}
					if(size.equalsIgnoreCase("slave_net_timeout"))
					{
					 size="放弃读操作前等待主/从连接的更多数据的等待秒数";
					}
					if(size.equalsIgnoreCase("slave_skip_errors"))
					{
					 size="从服务器应跳过(忽视)的复制错误";
					}
					if(size.equalsIgnoreCase("slave_transaction_retries"))
					{
					 size="复制从服务器SQL线程未能执行事务，在提示错误并停止前它自动重复slave_transaction_retries次";
					}
					if(size.equalsIgnoreCase("slow_launch_time"))
					{
					 size="如果创建线程的时间超过该秒数，服务器增加Slow_launch_threads状态变量";
					}
					if(size.equalsIgnoreCase("sort_buffer_size"))
					{
					 size="每个排序线程分配的缓冲区的大小";
					}
					if(size.equalsIgnoreCase("sql_mode"))
					{
					 size="当前的服务器SQL模式，可以动态设置";
					}
					if(size.equalsIgnoreCase("storage_engine"))
					{
					 size="该变量是table_typeis的同义词。在MySQL 5.1中,首选storage_engine";
					}
					if(size.equalsIgnoreCase("sync_binlog"))
					{
					 size="如果为正，当每个sync_binlog'th写入该二进制日志后，MySQL服务器将它的二进制日志同步到硬盘上";
					}
					if(size.equalsIgnoreCase("sync_frm"))
					{
					 size="如果该变量设为1,当创建非临时表时它的.frm文件是否被同步到硬盘上";
					}
					if(size.equalsIgnoreCase("system_time_zone"))
					{
					 size="服务器系统时区";
					}
					if(size.equalsIgnoreCase("table_cache"))
					{
					 size="所有线程打开的表的数目";
					}
					if(size.equalsIgnoreCase("table_type"))
					{
					 size="默认表类型(存储引擎)";
					}
					if(size.equalsIgnoreCase("thread_cache_size"))
					{
					 size="服务器应缓存多少线程以便重新使用";
					}
					if(size.equalsIgnoreCase("thread_stack"))
					{
					 size="每个线程的堆栈大小";
					}
					if(size.equalsIgnoreCase("time_format"))
					{
					 size="该变量为使用";
					}
					if(size.equalsIgnoreCase("time_zone"))
					{
					 size="当前的时区";
					}
					if(size.equalsIgnoreCase("tmp_table_size"))
					{
					 size="如果内存内的临时表超过该值，MySQL自动将它转换为硬盘上的MyISAM表";
					}
					if(size.equalsIgnoreCase("tmpdir"))
					{
					 size="保存临时文件和临时表的目录";
					}
					if(size.equalsIgnoreCase("transaction_alloc_block_size"))
					{
					 size="为保存将保存到二进制日志中的事务的查询而分配的内存块的大小(字节)";
					}
					if(size.equalsIgnoreCase("transaction_prealloc_size"))
					{
					 size="transaction_alloc_blocks分配的固定缓冲区的大小（字节），在两次查询之间不会释放";
					}
					if(size.equalsIgnoreCase("tx_isolation"))
					{
					 size="默认事务隔离级别";
					}
					if(size.equalsIgnoreCase("updatable_views_with_limit"))
					{
					 size="该变量控制如果更新包含LIMIT子句，是否可以在当前表中使用不包含主关键字的视图进行更新";
					}
					if(size.equalsIgnoreCase("version"))
					{
					 size="服务器版本号";
					}
					if(size.equalsIgnoreCase("version_bdb"))
					{
					 size="BDB存储引擎版本";
					}
					if(size.equalsIgnoreCase("version_comment"))
					{
					 size="configure脚本有一个--with-comment选项，当构建MySQL时可以进行注释";
					}
					if(size.equalsIgnoreCase("version_compile_machine"))
					{
					 size="MySQL构建的机器或架构的类型";
					}
					if(size.equalsIgnoreCase("version_compile_os"))
					{
					 size="MySQL构建的操作系统的类型";
					}
					if(size.equalsIgnoreCase("wait_timeout"))
					{
					 size="服务器关闭非交互连接之前等待活动的秒数";
					}
			%>
																			<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
																				height="28">
																				<td class="application-detail-data-body-list"><%=i+1%></td>
																				<td align=left
																					class="application-detail-data-body-list">
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
					<td width=15% valign=top>_<jsp:include page="/include/dbtoolbar.jsp"><jsp:param value="<%=myip %>" name="myip" />
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