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
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	String id = (String) request.getAttribute("id");
	String _flag = (String) request.getAttribute("flag");
	//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%id="+id);
	String mysid = (String) request.getAttribute("sid");
	String dbPage = "oracleping";
	DBVo vo = (DBVo) request.getAttribute("db");
	int dbvoid = vo.getId();
	String myip = vo.getIpAddress();
	String myport = vo.getPort();
	String myUser = vo.getUser();
	String myPassword = EncryptUtil.decode(vo.getPassword());
    DecimalFormat df=new DecimalFormat("#.##");
	String[] sysItem3 = { "HOST_NAME", "DBNAME", "VERSION",
			"INSTANCE_NAME", "STATUS", "STARTUP_TIME", "ARCHIVER" };
	String[] sysItemch3 = { "主机名称", "DB 名称", "DB 版本", "例程名", "例程状态",
			"例程开始时间", "归档日志模式" };

	String[] sysItem1 = { "shared_pool", "large_pool",
			"DEFAULT_buffer_cache", "java_pool" };
	String[] sysItemch1 = { "共享池", "大型池", "缓冲区高速缓存", "Java池" };

	String[] sysItem2 = { "aggregate_PGA_target_parameter",
			"total_PGA_allocated", "maximum_PGA_allocated" };
	String[] sysItemch2 = { "总计PGA目标", "分配的当前PGA", "分配的最大PGA" };

	String[] memoryItem = { "AllSize", "UsedSize", "Utilization" };
	String[] memoryItemch = { "总容量", "已用容量", "当前利用率", "最大利用率" };
	String[] sysItem = { "sysName", "sysUpTime", "sysContact",
			"sysLocation", "sysServices", "sysDescr" };
	String[] sysItemch = { "设备名", "设备启动时间", "设备联系", "设备位置", "设备服务",
			"设备描述" };

	Hashtable max = (Hashtable) request.getAttribute("max");

	String chart1 = (String) request.getAttribute("chart1");

	double avgpingcon = (Double) request.getAttribute("avgpingcon");
	
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100 - percent1;

	String dbtye = (String) request.getAttribute("dbtye");
	String managed = (String) request.getAttribute("managed");
	String runstr = (String) request.getAttribute("runstr");

	String lstrnStatu = (String) request.getAttribute("lstrnStatu");
	if ("READ WRITE".equalsIgnoreCase(lstrnStatu)) {
		lstrnStatu = "已启动";
	} else {
		lstrnStatu = "未启动";
	}
	Hashtable isArchive_h = (Hashtable) request
			.getAttribute("isArchive_h");
	String created = "";
	if (isArchive_h != null && isArchive_h.containsKey("CREATED")) {
		created = (String) isArchive_h.get("CREATED");
	} 
		String createddate = "";
	if (isArchive_h != null && isArchive_h.containsKey("CREATED")) {
		created = (String) isArchive_h.get("CREATED");
	}
	Vector contrFile_v = (Vector) request.getAttribute("contrFile_v");
	Vector logFile_v = (Vector) request.getAttribute("logFile_v");
	Vector extent_v = (Vector) request.getAttribute("extent_v");
	Vector keepObj_v = (Vector) request.getAttribute("keepObj_v");
	Hashtable orasys = (Hashtable) request.getAttribute("sysvalue");
	Hashtable memPerfValue = (Hashtable) request
			.getAttribute("memPerfValue");
	if (memPerfValue == null)
		memPerfValue = new Hashtable();
	Hashtable cursors = (Hashtable) request.getAttribute("cursors");
	Hashtable oramem = (Hashtable) request.getAttribute("memValue");
	Vector tableinfo = (Vector) request.getAttribute("tableinfo");
	if (cursors == null)
		cursors = new Hashtable();

	String buffercache = "0";
	Vector pingData = (Vector) ShareData.getPingdata().get(myip);
			if (pingData != null && pingData.size() > 0) {
				Pingcollectdata pingdata = (Pingcollectdata) pingData
						.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				
				
				String pingvalue = pingdata.getThevalue();
				
				
			}
		
%>
<%
	int intbuffercache = 0;
	String opencurstr = "0";
	String curconnectstr = "0";
	String dictionarycache = "0";
	String librarycache = "0";
	String pctmemorysorts = "";
	String pctbufgets = "0";
	String unpctmemorysorts = "0";
	String undictionarycache = "0";
	String unlibrarycache = "0";
	String unbuffercache = "0";
	String unpctbufgets = "0";
	if (memPerfValue != null) {
		if (memPerfValue.containsKey("buffercache")
				&& memPerfValue.get("buffercache") != null)
			buffercache = (String) memPerfValue.get("buffercache");

		try {
			intbuffercache = Math.round(Float.parseFloat(buffercache));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (memPerfValue.containsKey("dictionarycache")
				&& memPerfValue.get("dictionarycache") != null)
			dictionarycache = (String) memPerfValue
					.get("dictionarycache");
		if (memPerfValue.containsKey("librarycache")
				&& memPerfValue.get("librarycache") != null)
			librarycache = (String) memPerfValue.get("librarycache");
		if (memPerfValue.containsKey("pctmemorysorts")
				&& memPerfValue.get("pctmemorysorts") != null)
			pctmemorysorts = (String) memPerfValue
					.get("pctmemorysorts");
		if (memPerfValue.containsKey("pctbufgets")
				&& memPerfValue.get("pctbufgets") != null)
			pctbufgets = (String) memPerfValue.get("pctbufgets");
		if (cursors.containsKey("opencur")&& cursors.get("opencur") != null){
			opencurstr = (String) cursors.get("opencur");
		}
		if (cursors.containsKey("curconnect")&& cursors.get("curconnect") != null){
			curconnectstr = (String) cursors.get("curconnect");
		}
		if (pctmemorysorts.equals(""))
			pctmemorysorts = "0";
		unpctmemorysorts = String.valueOf(100 - Math.round(Float
				.parseFloat(pctmemorysorts)));
		if (dictionarycache.equals(""))
			dictionarycache = "0";
		undictionarycache = String.valueOf(100 - Integer
				.parseInt(dictionarycache));
		if (librarycache.equals(""))
			librarycache = "0";
		unlibrarycache = String.valueOf(100 - Integer
				.parseInt(librarycache));
		if (buffercache.equals(""))
			buffercache = "0";
		unbuffercache = String.valueOf(100 - Integer
				.parseInt(buffercache));
		if (pctbufgets.equals(""))
			pctbufgets = "0";
		unpctbufgets = String.valueOf(100 - Double.valueOf(pctbufgets));
	}

	String picip = CommonUtil.doip(myip);

	//生成当天平均连通率图形
	CreatePiePicture _cpp = new CreatePiePicture();
	_cpp.createAvgPingPic(picip, avgpingcon);

	//缓存利用率
	CreateMetersPic cmp = new CreateMetersPic();
	cmp.createpubliccpuPic(picip, intbuffercache, "缓存命中率");

	StringBuffer dataStr2 = new StringBuffer();
	dataStr2.append("连通;").append(Math.round(avgpingcon)).append(
			";false;7CFC00\\n");
	dataStr2.append("未连通;").append(100 - Math.round(avgpingcon))
			.append(";false;FF0000\\n");
	String avgdata = dataStr2.toString();
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />


		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
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
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>

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
	//go();
	setClass();	
}

function setClass(){
	document.getElementById('oraDetailTitle-0').className='detail-data-title';
	document.getElementById('oraDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('oraDetailTitle-0').onmouseout="this.className='detail-data-title'";

}

function refer(action){
		document.getElementById("id").value="<%=vo.getId()%>";
		document.getElementById("sid").value="<%=request.getAttribute("sid")%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
		<script type="text/javascript">
        var xmlHttp;
        var bar_color = '#00FA9A';      /*进度条颜色*/
        var span_id = "block";
        var clear = "&nbsp;&nbsp;&nbsp;"

        function createXMLHttpRequest() {
            if (window.ActiveXObject) {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            } 
            else if (window.XMLHttpRequest) {
                xmlHttp = new XMLHttpRequest();                
            }
        }

        function go() {     /*请求ProgresBarJsp.jsp*/
            createXMLHttpRequest();
            checkDiv();
            var url = "<%=rootPath%>/application/db/ProgressBarJsp.jsp?task=create";
            var button = document.getElementById("go");
            button.disabled = true;
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = goCallback;     //请求响应后，调用的goCallback函数
            xmlHttp.send(null);
        }

        function goCallback() {     
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    setTimeout("pollServer()", 500);
                }
            }
        }
        
        function pollServer() {
            createXMLHttpRequest();
            var url = "<%=rootPath%>/application/db/ProgressBarJsp.jsp?task=poll";
            xmlHttp.open("GET", url, true);
            xmlHttp.onreadystatechange = pollCallback;
            xmlHttp.send(null);
        }
        
        function pollCallback() {
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    var percent_complete = xmlHttp.responseXML.getElementsByTagName("percent")[0].firstChild.data;                 
                    var index = processResult(percent_complete);                  
                    for (var i = 1; i <= index; i++) {
                        var elem = document.getElementById("block" + i);
                        if(i != 5){                        
                        elem.innerHTML = clear;
                        }
                        elem.style.backgroundColor = bar_color;
                        
                        //var next_cell = i + 1;
                        //if (next_cell > index && next_cell <= 9) {
                        //    document.getElementById("block" + next_cell).innerHTML = percent_complete + "%";
                        //}
                    }                    
                    if (index < 9) {
                        setTimeout("pollServer()", 500);   //每隔俩秒执行一次pollServer（）函数，只执行一次
                    } else {
                        setTimeout("go()", 500);            //隔1秒重新请求一次go函数，达到循环显示的目的
                        //document.getElementById("complete").innerHTML = "Complete!";
                        //document.getElementById("go").disabled = false;
                    }
                }
            }
        }
        
        function processResult(percent_complete) {
            var ind;
            if (percent_complete.length == 2) {
                ind = percent_complete.substring(0, 1);   /*取俩位数的第一位数*/
            } else {
                ind = 9;
            }
            return ind;
        }

        function checkDiv() {
            var progress_bar = document.getElementById("progressBar");      
            if (progress_bar.style.visibility == "visible") {
                clearBar();
                document.getElementById("complete").innerHTML = "";
            } else {
                progress_bar.style.visibility = "visible";
                //rightbar.style.visibility = "visible";               
                //document.getElementById("rightb").style.backgroundColor = "#ECECEC";              
            }
        }
        
        function clearBar() {
            for (var i = 1; i < 10; i++) {
                var elem = document.getElementById("block" + i);
                elem.innerHTML = clear;
                elem.style.backgroundColor = "#ececec";                
                if(i == 5)
                {
                   elem.innerHTML = "<%=pctbufgets%>%";
                }
            }
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
				$("#public").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>public.png?nowtime="+(new Date()), alt: "缓存命中率" }); 
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
			<input type="hidden" name="sid" id="sid" />
			<table id="body-container" class="body-container">
				<tr>
					<!-- 左右结构421-->
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
									<table>
										<!-- 上下结构 -->
										<tr>
											<td>
												<!-- !!!!!! -->
												<table>
													<tr>
														<td width=85%>
															<table id="container-main-application-detail"
																class="container-main-application-detail">
																<tr> 
																	<td>
																		<jsp:include page="/topology/includejsp/db_oracle.jsp">
																			<jsp:param name="Id" value="<%=vo.getId()%>" />
																			<jsp:param name="IpAddress" value="<%=vo.getIpAddress()%>" />
																			<jsp:param name="_flag" value="<%=_flag%>" />
																			<jsp:param name="Alias" value="<%=vo.getAlias()%>" />
																			<jsp:param name="Port" value="<%=vo.getPort()%>" />
																			<jsp:param name="DbName" value="<%=vo.getDbName()%>" />
																			<jsp:param name="dbtye" value="<%=dbtye%>" />
																			<jsp:param name="runstr" value="<%=runstr%>" />
																			<jsp:param name="created" value="<%=created%>" />
																			<jsp:param name="buffercache" value="<%=buffercache%>" />
																			<jsp:param name="lstrnStatu" value="<%=lstrnStatu%>" />
																			<jsp:param name="dictionarycache" value="<%=dictionarycache%>" />
																			<jsp:param name="unbuffercache" value="<%=unbuffercache%>" />
																			<jsp:param name="unpctmemorysorts" value="<%=unpctmemorysorts%>" />
																			<jsp:param name="librarycache" value="<%=librarycache%>" />
																			<jsp:param name="unlibrarycache" value="<%=unlibrarycache%>" />
																			<jsp:param name="pctmemorysorts" value="<%=pctmemorysorts%>" />
																			<jsp:param name="opencurstr" value="<%=opencurstr%>" />
																			<jsp:param name="curconnectstr" value="<%=curconnectstr%>" />
																			<jsp:param name="picip" value="<%=picip%>" />
																			<jsp:param name="avgdata" value="<%=avgdata%>" />
																			<jsp:param name="memPerfValue" value="<%=memPerfValue%>" />
																				<jsp:param name="managed" value="<%=managed%>" />
																			
																			<jsp:param name="sid"  value="<%=mysid %>" />
																		</jsp:include> 
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td >
												<table id="container-main-application-detail" class="container-main-application-detail">
													<tr>
														<td >
															<table id="application-detail-content" class="application-detail-content"> 
																<tr>
																	<td class="application-detail-data">
																		<table id="application-detail-data" class="application-detail-data">
																			<tr>
																				<td class="detail-data-header">
																					<%=oraDetailTitleTable%>
																				</td>
																			</tr>
																			<tr>
																				<td align=left>
																					<table class="application-detail-data-body">
																						<tr>
																							<td>
																								<%
																									String str1 = "", str2 = "";

																									if (max.get("avgpingcon") != null) {
																										str2 = (String) max.get("avgpingcon");
																									}
																									String sid = request.getParameter("sid");
																								%>
																								<table cellpadding="0" cellspacing="0"
																									width=100%>
																									
																									
																									
																									<!--  begin -->
																									<tr>
																										
																										<td align=center  valign=top>
																											
																											<table id="body-container" class="body-container" style="background-color: #FFFFFF;">
																												<tr>
																													<td class="td-container-main">
																														<table id="container-main" class="container-main">
																															<tr>
																																<td class="td-container-main-add">
																																	<table id="container-main-add" class="container-main-add">
																																		<tr>
																																			<td>
																																				<table id="add-content" class="add-content">
																																					<tr>
																																						<td>
																																							<table id="add-content-header"
																																								class="add-content-header">
																																								<tr>
																																									<td align="left" width="5">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_01.jpg"
																																											width="5" height="29" />
																																									</td>
																																									<td class="add-content-title">
																																										<b>连通率</b>
																																									</td>
																																									<td align="right">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_03.jpg"
																																											width="5" height="29" />
																																									</td>
																																								</tr>
																																							</table>
																																						</td>
																																					</tr>
																																					<tr>
																																						<td>
																																							<table id="detail-content-body"
																																								class="detail-content-body">
																																								<tr>
																																									<td>
																																										<table cellpadding="1"
																																											cellspacing="1" width=48%>

																																											<tr>
																																												<td valign=top>

																																													<table cellpadding="0"
																																														cellspacing="0" width=48%
																																														align=center>
																																														<tr>
																																															<td align=center
																																																colspan=3>

																																																<div id="flashcontent1" style="width:400;height:280">
																															<strong>You need to upgrade your Flash Player</strong>
																														</div>
																														<script type="text/javascript">
											                                                                             var so = new SWFObject("<%=rootPath%>/flex/Oracle_Ping.swf?ipadress=<%=vo.getId()%>&category=ORAPing", "Oracle_Ping", "99%", "285", "8", "#ffffff");
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
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																										<td align=center >
																											
																											<table id="body-container"
																												class="body-container"
																												style="background-color: #FFFFFF;">
																												<tr>
																													<td class="td-container-main">
																														<table id="container-main"
																															class="container-main">
																															<tr>
																																<td class="td-container-main-add">
																																	<table id="container-main-add"
																																		class="container-main-add">
																																		<tr>
																																			<td>
																																				<table id="add-content"
																																					class="add-content">
																																					<tr>
																																						<td>
																																							<table id="add-content-header"
																																								class="add-content-header">
																																								<tr>
																																									<td align="left" width="5">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_01.jpg"
																																											width="5" height="29" />
																																									</td>
																																									<td class="add-content-title">
																																										<b>表空间使用率</b>
																																									</td>
																																									<td align="right">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_03.jpg"
																																											width="5" height="29" />
																																									</td>
																																								</tr>
																																							</table>
																																						</td>
																																					</tr>
																																					<tr>
																																						<td>
																																							<table id="detail-content-body"
																																								class="detail-content-body">
																																								<tr>
																																									<td>
																																										<table cellpadding="1"
																																											cellspacing="1" width=48%>

																																											<tr>
																																												<td valign=top>



																																													<table cellpadding="0"
																																														cellspacing="0" width=48%
																																														align=center>
																																														<tr>
																																															<td align=center
																																																colspan=3>
																																																<%
																															StringBuffer tablespace = new StringBuffer();
																															String tabledata = "";
																															String[] colorStr = new String[] { "#33CCFF", "#003366", "#33FF33",
																																	"#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF",
																																	"#A52A2A", "#23f266","#9932CC","#FF1493","#ADFF2F","#33CCFF", "#003366", "#33FF33",
																																	"#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF",
																																	"#A52A2A", "#23f266","#800080", "#23f266","#9932CC","#FF1493"};
																															tablespace.append("<chart><series>");

																															String[] filenames = null;
																															String[] tablespaces = null;
																															String[] tspercents = null;
																															String[] size_mbs = null;
																															String[] free_mbs = null;
																															String[] status = null;
																															String[] chunks = null;
																															Hashtable tableHt = null;
																															if (tableinfo != null&&tableinfo.size()>0) {
																																filenames = new String[tableinfo.size()];
																																tablespaces = new String[tableinfo.size()];
																																tspercents = new String[tableinfo.size()];
																																size_mbs = new String[tableinfo.size()];
																																free_mbs = new String[tableinfo.size()];
																																status = new String[tableinfo.size()];
																																chunks = new String[tableinfo.size()];
																																for (int i = 0; i < tableinfo.size(); i++) {
																																	tableHt = new Hashtable();
																																	tableHt = (Hashtable) tableinfo.get(i);
																																	if (tableHt.get("file_name") != null)
																																		filenames[i] = (String) tableHt.get("file_name");
																																	if (tableHt.get("tablespace") != null)
																																		tablespaces[i] = (String) tableHt.get("tablespace");
																																	if (tableHt.get("size_mb") != null)
																																		size_mbs[i] = (String) tableHt.get("size_mb");
																																	if (tableHt.get("free_mb") != null)
																																		free_mbs[i] = (String) tableHt.get("free_mb");
																																	if (tableHt.get("status") != null)
																																		status[i] = (String) tableHt.get("status");
																																	if (tableHt.get("chunks") != null)
																																		chunks[i] = (String) tableHt.get("chunks");

																																	String value = "0";
																																	if (tableHt.get("percent_free") != null) {
																																		value = (String) tableHt.get("percent_free");
																																	}
																																	value = value.trim();
																																	//int data = Math.round(Float.parseFloat(value));
																																	String data=df.format(100-Float.parseFloat(value));
																																	tspercents[i] = data;
																																	
																																}
																																for (int i = 0; i < tablespaces.length; i++) {
																																tablespace.append("<value xid='").append(i).append("'>")
																																		.append(tablespaces[i]).append("</value>");
																															}
																															tablespace.append("</series><graphs><graph gid='0'>");
																															for (int i = 0,j=0; i < tspercents.length; i++,j++) {
																															  if(i/colorStr.length==1)j=0;
																																tablespace.append("<value xid='").append(i).append("' color='")
																																		.append(colorStr[j]).append("'>" + tspercents[i])
																																		.append("</value>");
																																

																															}
																															tablespace.append("</graph></graphs></chart>");
																															tabledata = tablespace.toString();
																															}else{
																															tabledata="0";
																															}%>
																																																<div id="tablespace">
																																																	<strong>You
																																																		need to upgrade your
																																																		Flash Player</strong>
																																																</div>
																																																<script
																																																	type="text/javascript"
																																																	src="<%=rootPath%>/include/swfobject.js"></script>
																														<script type="text/javascript">
																														<% if(!tabledata.equals("0")){ %>	
						                                                                                                 var so = new SWFObject("<%=rootPath%>/amchart/amcolumn.swf", "ampie","400", "285", "8", "#FFFFFF");
						                                                                                                     so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                                     so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/dbpercent_settings.xml"));
						                                                                                                     so.addVariable("chart_data","<%=tabledata%>");
						                                                                                                     so.write("tablespace");
						                                                                                                     <%}else{%>
																			                                                 var _div=document.getElementById("tablespace");
																			                                                 var img=document.createElement("img");
																			                                                     img.setAttribute("src","<%=rootPath%>/resource/image/nodata.gif");
																			                                                     _div.appendChild(img);
																			                                                  <%}%>
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
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>

																									</tr>
																									
																									
																									
																									
																									
																									
																									
																									<tr>
																										
																										<td align=center  valign=top>
																											
																											<table id="body-container" class="body-container" style="background-color: #FFFFFF;">
																												<tr>
																													<td class="td-container-main">
																														<table id="container-main" class="container-main">
																															<tr>
																																<td class="td-container-main-add">
																																	<table id="container-main-add" class="container-main-add">
																																		<tr>
																																			<td>
																																				<table id="add-content" class="add-content">
																																					<tr>
																																						<td>
																																							<table id="add-content-header"
																																								class="add-content-header">
																																								<tr>
																																									<td align="left" width="5">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_01.jpg"
																																											width="5" height="29" />
																																									</td>
																																									<td class="add-content-title">
																																										<b>PGA信息详情</b>
																																									</td>
																																									<td align="right">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_03.jpg"
																																											width="5" height="29" />
																																									</td>
																																								</tr>
																																							</table>
																																						</td>
																																					</tr>
																																					<tr>
																																						<td>
																																							<table id="detail-content-body"
																																								class="detail-content-body">
																																								<tr>
																																									<td>
																																										<table cellpadding="1"
																																											cellspacing="1" width=48%>

																																											<tr>
																																												<td valign=top>

																																													<table cellpadding="0"
																																														cellspacing="0" width=48%
																																														align=center>
																																														<tr>
																																															<td align=center
																																																colspan=3>

																																																<%
																																																	StringBuffer sb = new StringBuffer();
																																																	String netdata = "";
																																																	
																																																	sb.append("<chart><series>");
																																																	for (int k = 0; k < sysItem2.length; k++) {
																																																		sb.append("<value xid='").append(k).append("'>").append(
																																																				sysItemch2[k]).append("</value>");
																																																	}
																																																	sb.append("</series><graphs><graph gid='0'>");
																																																	if (oramem != null) {

																																																		for (int i = 0; i < sysItem2.length; i++) {
																																																			String key = sysItemch2[i];
																																																			String value = "0";
																																																			if (oramem.get(sysItem2[i]) != null) {
																																																				value = (String) oramem.get(sysItem2[i]);
																																																			}
																																																			value = value.replace("MB", "");
																																																			int data = Math.round(Float.parseFloat(value));
																																																			sb.append("<value xid='").append(i).append("' color='")
																																																					.append(colorStr[i]).append("'>" + data).append(
																																																							"</value>");
																																																		}
																																																	} else {
																																																		sb.append("</series><graphs><graph gid='0'>");
																																																	}
																																																	sb.append("</graph></graphs></chart>");
																																																	String dbdata = sb.toString();
																																																%>
																																																<div id="dbmemory">
																																																	<strong>You
																																																		need to upgrade your
																																																		Flash Player</strong>
																																																</div>
																																																<script
																																																	type="text/javascript"
																																																	src="<%=rootPath%>/include/swfobject.js"></script>
																																																<script
																																																	type="text/javascript">
						                                                                                                                                                                              var so = new SWFObject("<%=rootPath%>/amchart/amcolumn.swf", "ampie","320", "278", "8", "#FFFFFF");
						                                                                                                                                                                                  so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                                                                                                                  so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/dbmemory_settings.xml"));
						                                                                                                                                                                                  so.addVariable("chart_data","<%=dbdata%>");
						                                                                                                                                                                                  so.write("dbmemory");
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
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																										
																										
																										
																										<td align=center  valign=top>
																											
																											<table id="body-container" class="body-container" style="background-color: #FFFFFF;">
																												<tr>
																													<td class="td-container-main">
																														<table id="container-main" class="container-main">
																															<tr>
																																<td class="td-container-main-add">
																																	<table id="container-main-add" class="container-main-add">
																																		<tr>
																																			<td>
																																				<table id="add-content" class="add-content">
																																					<tr>
																																						<td>
																																							<table id="add-content-header"
																																								class="add-content-header">
																																								<tr>
																																									<td align="left" width="5">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_01.jpg"
																																											width="5" height="29" />
																																									</td>
																																									<td class="add-content-title">
																																										<b>SGA信息详情</b>
																																									</td>
																																									<td align="right">
																																										<img
																																											src="<%=rootPath%>/common/images/right_t_03.jpg"
																																											width="5" height="29" />
																																									</td>
																																								</tr>
																																							</table>
																																						</td>
																																					</tr>
																																					<tr>
																																						<td>
																																							<table id="detail-content-body"
																																								class="detail-content-body">
																																								<tr>
																																									<td>
																																										<table cellpadding="1"
																																											cellspacing="1" width=49%>

																																											<tr>
																																												<td valign=top>

																																													<table cellpadding="0"
																																														cellspacing="0" width=49%
																																														align=center>
																																														<tr>
																																															<td align=center
																																																colspan=3>

																																																<%
																																																	StringBuffer sga = new StringBuffer();
																																																	String sgadata = "";
																																																	sga.append("<chart><series>");
																																																	for (int k = 0; k < sysItem1.length; k++) {
																																																		sga.append("<value xid='").append(k).append("'>").append(
																																																				sysItemch1[k]).append("</value>");
																																																	}

																																																	sga.append("</series><graphs><graph gid='0'>");
																																																	if (oramem != null) {

																																																		for (int i = 0; i < sysItem1.length; i++) {

																																																			String value = "0";
																																																			if (oramem.get(sysItem1[i]) != null) {
																																																				value = (String) oramem.get(sysItem1[i]);
																																																			}
																																																			if(value != null && "null".equals(value)){
																																																				value = value.replace("MB", "");
																																																			}
																																																			if(value == null || "null".equals(value))value="0";
																																																			int data = Math.round(Float.parseFloat(value));
																																																			sga.append("<value xid='").append(i).append("' color='")
																																																					.append(colorStr[i]).append("'>" + data).append(
																																																							"</value>");
																																																		}
																																																	} else {
																																																		sga.append("</series><graphs><graph gid='0'>");
																																																	}
																																																	sga.append("</graph></graphs></chart>");
																																																	sgadata = sga.toString();
																																																%>
																																																<div id="sgamemory">
																																																	<strong>You
																																																		need to upgrade your
																																																		Flash Player</strong>
																																																</div>
																																																<script
																																																	type="text/javascript"
																																																	src="<%=rootPath%>/include/swfobject.js"></script>
																																																<script
																																																	type="text/javascript">
						                                                                                                                                                                             var so = new SWFObject("<%=rootPath%>/amchart/amcolumn.swf", "ampie","320", "278", "8", "#FFFFFF");
						                                                                                                                                                                                 so.addVariable("path", "<%=rootPath%>/amchart/");
						                                                                                                                                                                                 so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/dbmemory_settings.xml"));
						                                                                                                                                                                                 so.addVariable("chart_data","<%=sgadata%>");
						                                                                                                                                                                                 so.write("sgamemory");
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
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																										
																										
																										
																										
																										

																									</tr>
																									<tr>
																										<td align=center colspan=2 width=99%>
																										<table>
																										 <tr>
																										   
																											<td width=45% valign=top>
																											
																														<table cellpadding="1" cellspacing="1"
																															style="align: center; width: 98%"
																															bgcolor="#FFFFFF">
																															<tr bgcolor="#FFFFFF">
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	文件名
																																</td>
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	表空间名称
																																</td>
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	表空间大小
																																</td>
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	空闲大小
																																</td>
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	空间使用率
																																</td>
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	空闲块数目
																																</td>
																																<td align=center
																																	class="detail-data-body-title"
																																	style="align: center; height: 29">
																																	文件状态
																																</td>
																															</tr>
																															<%
																																for (int i = 0; i < tableinfo.size(); i++) {
																															%>
																															<tr>
																																<td align=center
																																	class="detail-data-body-list"
																																	style="height: 29"><%=filenames[i]%></td>
																																<td align=center
																																	class="detail-data-body-list"><%=tablespaces[i]%></td>
																																<td align=center
																																	class="detail-data-body-list"><%=size_mbs[i]%>MB
																																</td>
																																<td align=center
																																	class="detail-data-body-list"><%=free_mbs[i]%>MB
																																</td>
																																<td align=center
																																	class="detail-data-body-list"><%=tspercents[i]%>%
																																</td>
																																<td align=center
																																	class="detail-data-body-list"><%=chunks[i]%></td>
																																<td align=center
																																	class="detail-data-body-list"><%=status[i]%></td>
																															</tr>
																															<%
																																}
																															%>
																														</table>

																												
																											</td>
																											</tr>
																											</table>
																										</td>
																										
																									</tr>

																									<tr>
																										<td align=center colspan=3 width=99%>
																											<table width=98% align=center>
																												<tr>
																													<td align=center valign=top width=48%>
																														<br>
																														<table cellpadding="0" cellspacing="0"
																															width=98% align=center>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center colspan=2>
																																	控制文件状态
																																</td>
																															</tr>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center>
																																	名称
																																</td>
																																<td align=center>
																																	状态
																																</td>
																															</tr>
																															<%
																																if (contrFile_v != null) {
																																	for (int k = 0; k < contrFile_v.size(); k++) {
																																		Hashtable ht = (Hashtable) contrFile_v.get(k);
																															%>
																															<tr height="28" bgcolor="#FFFFFF"
																																<%=onmouseoverstyle%>>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("name").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("status").toString()%></td>
																															</tr>
																															<%
																																}
																																}
																															%>
																														</table>
																													</td>
																													<td width=1%>
																														&nbsp;
																													</td>
																													<td align=center valign=top width=48%>
																														<br>
																														<table cellpadding="0" cellspacing="0"
																															width=98% align=center>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center colspan=4>
																																	日志文件状态
																																</td>
																															</tr>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center>
																																	GROUP#
																																</td>
																																<td align=center>
																																	状态
																																</td>
																																<td align=center>
																																	类型
																																</td>
																																<td align=center>
																																	名称
																																</td>
																															</tr>
																															<%
																																if (logFile_v != null) {
																																	for (int k = 0; k < logFile_v.size(); k++) {
																																		Hashtable ht = (Hashtable) logFile_v.get(k);
																															%>
																															<tr height="28" bgcolor="#FFFFFF"
																																<%=onmouseoverstyle%>>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("group#").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("status").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("type").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("member").toString()%></td>
																															</tr>
																															<%
																																}
																																}
																															%>
																														</table>
																													</td>
																												</tr>
																												<tr>
																													<td align=center valign=top width=48%>
																														<br>
																														<table cellpadding="0" cellspacing="0"
																															width=98% align=center>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center colspan=2>
																																	字典管理表空间中的Extent总数
																																</td>
																															</tr>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center>
																																	表空间名称
																																</td>
																																<td align=center>
																																	Extent总数
																																</td>
																															</tr>
																															<%
																																if (extent_v != null && extent_v.size() > 0) {
																																	for (int k = 0; k < extent_v.size(); k++) {
																																		Hashtable ht = (Hashtable) extent_v.get(k);
																															%>
																															<tr height="28" bgcolor="#FFFFFF"
																																<%=onmouseoverstyle%>>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("tablespace_name").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("sum(a.extents)").toString()%></td>
																															</tr>
																															<%
																																}
																																} else {
																															%>
																															<tr height="28" bgcolor="#FFFFFF"
																																<%=onmouseoverstyle%>>
																																<td colspan=2>
																																	&nbsp;表空间是采用LOCAL方式管理的，并不是采用DICTIONARY方式管理，extent不会影响系统的性能
																																</td>
																															</tr>
																															<%
																																}
																															%>
																														</table>
																													</td>
																													<td width=1%>
																														&nbsp;
																													</td>
																													<td align=center valign=top width=48%>
																														<br>
																														<table cellpadding="0" cellspacing="0"
																															width=98% align=center>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center colspan=7>
																																	固定缓存对象
																																</td>
																															</tr>
																															<tr height="28" bgcolor="#ECECEC">
																																<td align=center>
																																	所有者
																																</td>
																																<td align=center>
																																	名称
																																</td>
																																<td align=center>
																																	NAMESPACE
																																</td>
																																<td align=center>
																																	类型
																																</td>
																																<td align=center>
																																	SHARABLE_MEM
																																</td>
																																<td align=center>
																																	PINS
																																</td>
																																<td align=center>
																																	KEPT
																																</td>
																															</tr>
																															<%
																																if (keepObj_v != null) {
																																	for (int k = 0; k < keepObj_v.size(); k++) {
																																		Hashtable ht = (Hashtable) keepObj_v.get(k);
																															%>
																															<tr height="28" bgcolor="#FFFFFF"
																																<%=onmouseoverstyle%>>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("owner").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("name").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("namespace").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("type").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("sharable_mem").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("pins").toString()%></td>
																																<td
																																	class="application-detail-data-body-list"
																																	align=center>
																																	&nbsp;<%=ht.get("kept").toString()%></td>
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
					<td class="td-container-main-tool" width=15%>
															<jsp:include page="/include/dbtoolbar.jsp">

																<jsp:param value="<%=myip%>" name="myip" />
																<jsp:param value="<%=myport%>" name="myport" />
																<jsp:param value="<%=myUser%>" name="myUser" />
																<jsp:param value="<%=myPassword%>" name="myPassword" />
																<jsp:param value="<%=mysid%>" name="sid" />
																<jsp:param value="<%=id%>" name="id" />
																<jsp:param value="<%=dbPage%>" name="dbPage" />
																<jsp:param value="Oracle" name="subtype" />
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
  mainForm.action = "<%=rootPath%>/oracle.do?action=sychronizeData&dbPage=<%=dbPage%>&id=<%=id%>&myip=<%=myip%>&myport=<%=myport%>&myUser=<%=myUser%>&myPassword=<%=myPassword%>&sid=<%=mysid%>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>

	</BODY>
</HTML>